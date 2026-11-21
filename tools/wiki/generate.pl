#!/usr/bin/env perl
# Regenerates the wiki's Ancestries, Classes, Sizes and Powers pages. See README.md next to this file.
# Usage: perl tools/wiki/generate.pl <wiki checkout dir>
#
# Sources:
#   src/main/resources/origins/*.json  origins (name, description, layer, order, impact, icon, powers)
#   OriginAbilities.java               ArdanBorn's own powers: super("id", "Title", "Description")
#   external-powers.tsv                powers from Origins-Reborn and its addons (key, title, description)
#   power-details.md                   extra detail per power for the Powers page ("## <key>" sections)
# Stops with an error if an origin uses a power with no description, so pages never have gaps.
use strict;
use warnings;
use utf8;
use JSON::PP;
use File::Basename qw(dirname);
use File::Spec;

my $out = shift or die "Usage: perl tools/wiki/generate.pl <wiki checkout dir>\n";
-d $out or die "Not a directory: $out\n";
my $here = dirname(File::Spec->rel2abs(__FILE__));
my $root = File::Spec->catdir($here, '..', '..');
my $origins_dir = "$root/src/main/resources/origins";
my $abilities_java = "$root/src/main/java/dev/grapeofwrath/ardanborn/origins/OriginAbilities.java";
my $powers_tsv = "$here/external-powers.tsv";
my $details_md = "$here/power-details.md";
my %details;
binmode STDOUT, ':utf8';

my %SOURCE = (
    ardanborn      => 'ArdanBorn',
    origins        => '[Origins-Reborn](https://modrinth.com/plugin/origins-reborn)',
    fantasyorigins => '[Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy)',
    magicorigins   => '[Origins-Magic](https://modrinth.com/plugin/origins-magic)',
    moborigins     => '[Origins-Mobs](https://modrinth.com/plugin/origins-mobs)',
    monsterorigins => '[Origins-Monsters](https://modrinth.com/plugin/origins-monsters)',
);
my %LAYER_PAGE = (origin => 'Ancestries', class => 'Classes', size => 'Sizes');
my @IMPACT = ('None', 'Low', 'Medium', 'High');

# GitHub heading anchor: lowercase, drop punctuation except hyphens and spaces, spaces -> hyphens.
sub anchor {
    my $s = lc shift;
    $s =~ s/[^\w\- ]//g;
    $s =~ s/ /-/g;
    return $s;
}

my %power;
open my $tsv, '<:utf8', $powers_tsv or die $!;
while (<$tsv>) {
    chomp;
    next unless length;
    my ($key, $title, $desc) = split /\t/;
    $power{$key} = { title => $title, desc => $desc };
}
close $tsv;

# ArdanBorn powers, straight from the plugin code.
open my $java, '<:utf8', $abilities_java or die "$abilities_java: $!";
my $src = do { local $/; <$java> };
my $string = qr/"((?:[^"\\]|\\.)*)"/;
my $found = 0;
while ($src =~ /super\(\s*$string\s*,\s*$string\s*,\s*$string\s*\)/g) {
    my ($id, $title, $desc) = map { s/\\(.)/$1/gr } ($1, $2, $3);
    $power{"ardanborn:$id"} = { title => $title, desc => $desc };
    $found++;
}
die "No powers found in $abilities_java; has the Base constructor changed?\n" unless $found;

# Extra detail for the Powers page: "## <key>" sections, each copied as Markdown.
open my $det, '<:utf8', $details_md or die "$details_md: $!";
my $current;
while (<$det>) {
    if (/^## (\S+)\s*$/) { $current = $1; $details{$current} = ''; next; }
    $details{$current} .= $_ if defined $current;
}
close $det;
s/^\s+|\s+$//g for values %details;

my (%by_layer, %used_by);
opendir my $dh, $origins_dir or die $!;
for my $file (sort grep { /\.json$/ } readdir $dh) {
    open my $fh, '<:raw', "$origins_dir/$file" or die $!;
    my $o = decode_json(do { local $/; <$fh> });
    $o->{id} = $file =~ s/\.json$//r;
    push @{ $by_layer{ $o->{layer} } }, $o;
    for my $key (@{ $o->{powers} }) {
        die "No description for power $key (origin $o->{id})\n" unless $power{$key};
        push @{ $used_by{$key} }, $o;
    }
}

sub power_link {
    my $key = shift;
    my $title = $power{$key}{title};
    return "[$title](Powers#" . anchor($title) . ")";
}

sub source_of { my ($ns) = split /:/, shift; $SOURCE{$ns} // $ns }

my %INTRO = (
    origin => "Your **ancestry** is the people you were born to. It is chosen first, in the `origin` layer.",
    class  => "Your **class** is the calling you follow. It is chosen second, in the `class` layer, and combines with any ancestry.",
    size   => "Your **size** is chosen last, in the `size` layer. It defaults to Normal Size.",
);

for my $layer (sort keys %LAYER_PAGE) {
    my @list = sort { $a->{order} <=> $b->{order} } @{ $by_layer{$layer} || [] };
    my $page = $LAYER_PAGE{$layer};
    open my $md, '>:utf8', "$out/$page.md" or die $!;
    print $md "$INTRO{$layer}\n\n";
    print $md "| | Impact | Powers |\n|---|---|---|\n";
    for my $o (@list) {
        printf $md "| [%s](#%s) | %s | %d |\n", $o->{name}, anchor($o->{name}), $IMPACT[$o->{impact}], scalar @{ $o->{powers} };
    }
    for my $o (@list) {
        print $md "\n## $o->{name}\n\n";
        print $md "$o->{description}\n\n";
        printf $md "**Impact:** %s · **Icon:** `%s` · **File:** `%s.json`\n\n",
            $IMPACT[$o->{impact}], $o->{icon}{item} =~ s/^minecraft://r, $o->{id};
        if (@{ $o->{powers} }) {
            print $md "| Power | What it does | From |\n|---|---|---|\n";
            for my $key (@{ $o->{powers} }) {
                printf $md "| %s | %s | %s |\n", power_link($key), $power{$key}{desc}, source_of($key);
            }
        } else {
            print $md "No powers.\n";
        }
    }
    close $md;
}

open my $md, '>:utf8', "$out/Powers.md" or die $!;
my @keys = sort { lc $power{$a}{title} cmp lc $power{$b}{title} } grep { $used_by{$_} } keys %power;
print $md "Every power an ArdanBorn origin can have, A–Z. ArdanBorn's own powers are listed alongside the ones it borrows from Origins-Reborn and its addons. See [[Installation]] for which addons to install.\n\n";
for my $key (@keys) {
    my $p = $power{$key};
    my @origins = map { "[$_->{name}]($LAYER_PAGE{$_->{layer}}#" . anchor($_->{name}) . ")" }
        sort { $a->{name} cmp $b->{name} } @{ $used_by{$key} };
    print $md "### $p->{title}\n\n";
    print $md "$p->{desc}\n\n";
    if ($details{$key}) {
        print $md "$details{$key}\n\n";
    } else {
        warn "No details for power $key in power-details.md\n";
    }
    printf $md "`%s` · from %s · used by %s\n\n", $key, source_of($key), join(', ', @origins);
}
close $md;
printf "%d origins, %d powers\n", scalar(map { @$_ } values %by_layer), scalar @keys;
