package Playtest;
# Helpers for the playtests: RCON, the client, and checks. See ../README.md.
use strict; use warnings; use FindBin; use IO::Socket::INET; use Time::HiRes qw(sleep); use Exporter 'import';
our @EXPORT=qw($P r ok near has wear hold origin class mob hit hurt hp nap sect loss cnt gone summary ev);
sub ok($$;$); our $P='ClaudeTester'; sub P{$P} my($pass,$fail)=(0,0); my @f; my $tools="$FindBin::Bin/.."; my $R="$tools/../../run-test/.playtest/rcon";
my $pw=do{local(@ARGV,$/)=$R;<>}; my $s=IO::Socket::INET->new('127.0.0.1:25576') or die; $s->autoflush(1);
sub rd{my$n=shift;my$b='';while(length$b<$n){my$k=sysread($s,$b,$n-length$b,length$b);die"rcon closed"unless$k}$b}
sub pk{my($t,$d)=@_;syswrite($s,pack('V',10+length$d).pack('VV',7,$t).$d."\0\0")} sub gtt{my$n=unpack'V',rd(4);my$b=rd($n);substr($b,8,$n-10)}
pk(3,$pw);gtt();
sub r{my$o='';for(@_){pk(2,$_);my$x;do{$x=gtt();$o.=$x}while(length($x)>=4096);$o.="\n"}$o=~s/\xc2?\xa7.//g;$o=~s/\e\[[\d;]*m//g;chomp$o;$o} sub nap{sleep$_[0]} $|=1;
sub sect{print"\n== $_[0]\n"}
sub ok($$;$){my($c,$n,$d)=@_;$d//='';if($c){$pass++;print"ok   $n $d\n"}else{$fail++;push@f,"$n $d";print"FAIL $n $d\n"}}
sub near{my($g,$w,$n,$tol)=@_;$tol//=0.03;ok(defined$g&&abs($g-$w)<=$tol*($w||1)+0.011,$n,sprintf('got %.3f want %.3f',$g//-999,$w))}
sub ev{my($t,$id)=@_;my$o=r("data get entity $t active_effects[{id:\"minecraft:$id\"}]");$o=~/following entity data/?$o:''}
sub has{my($t,$id,$n,$want)=@_;$want//=1;my$e=ev($t,$id);ok(!!$e==!!$want,$n,$e?'present':'absent')}
my%sl=(head=>'helmet',chest=>'chestplate',legs=>'leggings',feet=>'boots');
sub wear{my($set,@p)=@_;r("item replace entity $P armor.$_ with air")for keys%sl;return unless$set;@p=keys%sl unless@p;my$t='diamond';
 for(@p){r("item replace entity $P hotbar.8 with air","clear $P","ardanborn give $P ${set}_${t}_$sl{$_}","item replace entity $P armor.$_ from entity $P hotbar.0","clear $P minecraft:nothing")}
 # clear removes worn armor too, so pieces are stacked in order below
}
sub wearall{my($set,$t,@p)=@_;$t||='diamond';@p=qw(head chest legs feet)unless@p;r("clear $P");my$i=0;
 for(@p){r("ardanborn give $P ${set}_${t}_$sl{$_}")} for(@p){r("item replace entity $P armor.$_ from entity $P hotbar.$i");$i++} for(0..$i-1){r("item replace entity $P hotbar.$_ with air")} sleep .3}
{no warnings;*wear=sub{my($set,@p)=@_;unless($set){r("clear $P");sleep .2;return}my$t='diamond';if($set=~s/:(\w+)//){$t=$1}wearall($set,$t,@p)}}
sub hold{my$id=shift;r("item replace entity $P hotbar.0 with air");return unless$id;
 if($id=~/:/){r("item replace entity $P hotbar.0 with $id")}else{r("ardanborn give $P $id")}sleep .15}
sub origin{my$o=shift;my$x=r("origin set $P origin $o");sleep .3;r("attribute $P minecraft:armor modifier add t:z -1 add_multiplied_total","attribute $P minecraft:max_health base set 400");$x} sub class{r("origin set $P class $_[0]");sleep .3}
sub mob{my($type,%o)=@_;my$h=$o{hp}//1000;my$m=$o{max}//1000;my$x=$o{x}//'0.5';my$z=$o{z}//'4.5';my$extra=$o{nbt}//'';my$n=$o{n}//1;r("kill \@e[tag=v]")unless$o{keep};
 r("summon $type $x -60 $z {Tags:[\"v\",\"new\"],NoAI:1b,Silent:1b,PersistenceRequired:1b,Health:${h}f,attributes:[{id:\"minecraft:max_health\",base:$m},{id:\"minecraft:armor\",base:0},{id:\"minecraft:knockback_resistance\",base:0}]$extra}")for 1..$n;sleep .1}
sub hp{my$t=shift//'@e[tag=v,limit=1]';my$o=r("data get entity $t Health");$o=~/data: ([\d.]+)f/?$1:undef}
sub hit{my($a,$ty)=@_;$ty//='player_attack';my$b=hp();my$o=r("damage \@e[tag=v,limit=1] $a minecraft:$ty by $P");my$e=hp();defined$b&&defined$e?$b-$e:(print("   hit: $o\n"),undef)}
sub hurt{my($a,$ty,$by)=@_;sleep .6;my$b=hp($P);r("damage $P $a minecraft:$ty".($by?" by $by":''));my$e=hp($P);$b-$e}
sub cnt{my$q=shift;my$o=r("execute if entity $q");$o=~/count: (\d+)/i?$1:0}
sub summary{print"\nPASS $pass FAIL $fail\n";print"  - $_\n"for@f}

sub setup{
r("difficulty normal","time set noon","weather clear","gamerule natural_health_regeneration false","gamerule keep_inventory true","gamemode survival $P","attribute $P minecraft:armor modifier remove t:z","attribute $P minecraft:armor modifier add t:z -1 add_multiplied_total","attribute $P minecraft:max_health base set 400","effect give $P minecraft:instant_health 1 10","effect clear $P","tp $P 0.5 -60 0.5 0 0","fillbiome -20 -61 -20 20 -50 20 minecraft:plains","kill \@e[type=!player]","gamerule max_entity_cramming 0","attribute $P minecraft:knockback_resistance base set 1","setworldspawn 0 -60 0");
class('herbalist');
in("keyup shift");in("key 1");die "the player is dead: respawn first\n" if hp($P)<=0}
sub heal{r("effect give $P minecraft:instant_health 1 10");nap(.1)}
# average over several of chance: victims with effect
sub massrate{my($type,$eff,$n)=@_;mob($type,n=>$n);for(1..int($n/25)+1){r("execute as \@e[tag=v,tag=!d,limit=25] run damage \@s 4 minecraft:player_attack by $P");r("tag \@e[tag=v,tag=!d,limit=25] add d")};nap(.3);my$c=cnt("\@e[tag=v,nbt={active_effects:[{id:\"minecraft:$eff\"}]}]");$c/$n}
sub hrate{my($n,$eff)=@_;my$c=0;for(1..$n){mob('husk');r("damage $P 6 minecraft:mob_attack by \@e[tag=v,limit=1]");my$e=ev('@e[tag=v,limit=1]',$eff);$c++if$e;nap(.42);heal()unless$_%20}$c/$n}
sub prop{my($g,$p,$n,$nm)=@_;my$sd=sqrt($p*(1-$p)/$n);ok(abs($g-$p)<3.5*$sd,$nm,sprintf('rate %.3f want %.2f n=%d',$g,$p,$n))}
sub attr{my$o=r("attribute $P minecraft:$_[0] get");$o=~/is ([-\d.]+)/?$1:undef}
sub dmg{my($amt,$ty)=@_;heal();my$h=hurt($amt,$ty,'@e[tag=v,limit=1]');$h}
our $U=join',',unpack('l>4',pack('H*','6a61bb62ecab30b993fc7949e87245b4'));
sub here{r("tp $P 0.5 -60 0.5 0 0","effect clear $P");nap(.3)}
sub arrow{my($amt,$type,$pdc)=@_;$type//='arrow';r("kill \@e[tag=ar]","summon $type 0.5 -58 -8 {Tags:[\"ar\"],NoGravity:1b,Owner:[I;$U]".($pdc?",BukkitValues:{\"ardanborn:$pdc\":1b}":'')."}");my$b=hp();my$dt=$type eq'trident'?'trident':$type eq'snowball'?'thrown':'arrow';r("damage \@e[tag=v,limit=1,sort=nearest] $amt minecraft:$dt by \@e[tag=ar,limit=1] from $P");my$e=hp();r("kill \@e[tag=ar]");$b-$e}
sub horse{r("kill \@e[tag=h]","summon horse 0.5 -60 0.5 {Tags:[\"h\"],Tame:1b,NoAI:1b,Health:40f,attributes:[{id:\"minecraft:max_health\",base:100}],equipment:{saddle:{id:\"saddle\",count:1}}}","ride $P mount \@e[tag=h,limit=1]");nap(.5)}
sub unhorse{r("ride $P dismount","kill \@e[tag=h]");here()}
sub motion{my$o=r('data get entity @e[tag=v,limit=1] Motion');my@m=$o=~/(-?[\d.]+(?:E-?\d+)?)d/g;@m}
our $IN="$tools/input";
sub in{system("$IN @_ >/dev/null 2>&1")}
sub rc{in('click 3');nap(.4)} sub eat{in('mousedown 3');nap(2.4);in('mouseup 3');nap(.3)}
sub sneak{in($_[0]?'keydown shift':'keyup shift');nap(.4)}
sub face{my($y,$p)=@_;r("tp $P 0.5 -60 0.5 $y $p");nap(.4)}
sub food{my$o=r("data get entity $P foodLevel");$o=~/data: (\d+)/;$1}
sub amp{my($t,$e)=@_;my$o=r("data get entity $t active_effects[{id:\"minecraft:$e\"}].amplifier");return $1 if$o=~/data: (\d+)/;ev($t,$e)?0:-1}
sub pdc{my$o=r("data get entity $P BukkitValues.\"ardanborn:$_[0]\"");$o=~/data: ([\d.]+)/?$1:undef}
sub v1{r("kill \@e[type=!player]");nap(.3);mob(@_)}
# Wait until a wall-clock cooldown started at $_[0] (seconds, from now()) has run for $_[1] seconds.
sub now{Time::HiRes::time()} sub since{my($t,$s)=@_;my$w=$t+$s-now();nap($w)if$w>0}
sub box{r("fill -4 -60 -4 4 -55 4 stone hollow","fill -3 -60 -3 3 -60 3 air");nap(1.5)} sub unbox{r("fill -4 -60 -4 4 -55 4 air")}
sub slot{my$x=285+36*($_[0]-1);in("keydown shift mousemove $x 374 sleep 0.25 click 1 sleep 0.25 keyup shift")}
# Smith the first two (or three) hotbar items at a real table.
sub smith{my@give=@_;r("clear $P","setblock 0 -60 2 smithing_table","tp $P 0.5 -60 0.5 0 35");r(/^minecraft:/?"give $P $_":"ardanborn give $P $_")for@give;nap(.4);in('key 9');nap(.2);in('click 3');nap(.7);slot($_)for 1..@give;nap(.3);in('keydown shift mousemove 463 187 sleep 0.3 click 1 sleep 0.3 keyup shift');nap(.3);in('key Escape');nap(.4)}
sub holds{r("execute if items entity $P container.* $_[0]")=~/passed/i}
push @EXPORT,qw($U in rc eat sneak face food heal massrate hrate prop attr dmg here arrow horse unhorse motion amp pdc v1 now since box unbox slot smith holds setup);
1;
