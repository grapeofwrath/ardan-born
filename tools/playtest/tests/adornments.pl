# Adornments: the screen, wearing, death, effects and loot.
use FindBin;use lib $FindBin::Bin;use Playtest;use strict;use warnings;
setup();origin('human');
r("gamerule keep_inventory true","attribute $P minecraft:max_health base set 20","effect clear $P");
my %X=(necklace=>2,back=>4,belt=>6);
my %IT=(necklace=>'white_gem_of_arwen',back=>'quiver_of_legolas',belt=>'belt_of_boromir');
sub worn{my$s=shift;r("execute if data entity $P BukkitValues.\"ardanborn:adornment_$s\"")=~/passed/i?1:0}
sub inv{my$o=r("clear $P minecraft:nautilus_shell 0");$o=~/Found (\d+)/?$1:0}
sub at{my($c,$y)=@_;in("mousemove ".(281+36*$c)." $y");nap(.35)} sub cl{at(@_);in('click 1');nap(.5)}
sub shift_cl{at(@_);in('keydown shift');nap(.2);in('click 1');nap(.2);in('keyup shift');nap(.5)}
sub opens{r("execute as $P run adornments");nap(1.2)} sub shut{in('key Escape');nap(.8)}
sub strip{opens();shut();r("clear $P")}             # empties the slots below
sub bare{opens();for(values%X){shift_cl($_,159)}shut();r("clear $P");ok(!worn('necklace')&&!worn('back')&&!worn('belt'),'stripped')}
sub wearit{my$id=shift;bare();r("ardanborn give $P $id");nap(.3);in('key 1');face(0,-40);rc();nap(.5);ok(inv()==0,"right-click puts on $id")}
my($T,$H)=(159,340);

sect 'screen';bare();r("ardanborn give $P $IT{$_}")for qw(necklace back belt);
opens();cl(0,$H);cl($X{necklace},$T);ok(worn('necklace')&&inv()==2,'click on');
cl(1,$H);cl($X{necklace},$T);ok(worn('necklace')&&!worn('back')&&inv()==2,'wrong slot refused');cl($X{back},$T);ok(worn('back')&&inv()==1,'back on');
shift_cl(2,$H);ok(worn('belt')&&inv()==0,'shift-click on');
shift_cl($X{belt},$T);ok(!worn('belt')&&inv()==1,'shift-click off');
at($X{belt},$T);in('key 5');nap(.5);ok(!worn('belt'),'empty hotbar key does nothing');
shut();r("clear $P");r("ardanborn give $P $IT{belt}");opens();at($X{belt},$T);in('key 1');nap(.5);ok(worn('belt')&&inv()==0,'number key on');
in('key f');nap(.5);ok(!worn('belt')&&inv()==1,'off-hand swap off');in('key f');nap(.5);ok(worn('belt')&&inv()==0,'off-hand swap on');
cl(3,$T);shift_cl(0,$T);ok(inv()==0&&r("clear $P *[custom_data~{}] 0")!~/pane/,'fillers stay');
at($X{belt},$T);in('key q');nap(.6);shut();ok(!worn('belt'),'Q drops it');my$d=cnt('@e[type=item]');ok($d+inv()==1,'and it exists once',"ground $d");r("kill \@e[type=item]");
r("clear $P","ardanborn give $P $IT{belt}");opens();cl(0,$H);cl($X{belt},$T);shut();ok(worn('belt')&&inv()==0,'closed');opens();shut();ok(worn('belt')&&worn('back')&&worn('necklace'),'kept after reopening');
r("ardanborn give $P $IT{belt}");in('key 1');face(0,-40);rc();ok(inv()==1,'right-click refused when the slot is full');r("clear $P");
opens();cl(0,225);shut();

sect 'full inventory';r("item replace entity $P hotbar.$_ with stone 64")for 0..8;r("item replace entity $P inventory.$_ with stone 64")for 0..26;
opens();shift_cl($X{belt},$T);ok(worn('belt'),'shift-click off refused');shut();r("clear $P");

sect 'button';in('key e');nap(.8);in('mousemove 574 145');nap(.4);in('key 5');in('key q');in('key f');nap(.4);in('keydown shift');in('key q');in('keyup shift');nap(.4);
ok(r("clear $P minecraft:armor_stand 0")!~/Found/&&cnt('@e[type=item]')==0,'button cannot be taken');
in('click 1');nap(1);cl($X{belt},$T);ok(!worn('belt'),'button opens the screen');cl($X{belt},$T);shut();ok(worn('belt'),'put back');

sect 'death';r("gamerule keep_inventory true");r("kill $P");nap(3);ok(worn('necklace')&&worn('back')&&worn('belt'),'kept with keep_inventory');
r("gamerule keep_inventory false","kill \@e[type=item]","clear $P","tp $P 0.5 -60 20.5");nap(.5);r("kill $P");nap(.4);my$g=cnt('@e[type=item]');nap(2.5);
ok(!worn('necklace')&&!worn('back')&&!worn('belt'),'dropped without it');ok($g+inv()==3,'three dropped, once each',"ground $g inv ".inv());r("kill \@e[type=item]","clear $P","gamerule keep_inventory true");here();

sect 'White Gem';wearit('white_gem_of_arwen');for my$e(qw(darkness blindness weakness)){r("effect clear $P","effect give $P minecraft:$e 100 0");nap(.3);my($t)=ev($P,$e)=~/duration: (\d+)/;near($t//0,995,"$e halved",.02)}
r("effect clear $P","effect give $P minecraft:slowness 100 0");nap(.3);my($t)=ev($P,'slowness')=~/duration: (\d+)/;near($t//0,1995,'slowness is not',.02);
r("effect clear $P","effect give $P minecraft:weakness infinite 0");nap(.3);ok(ev($P,'weakness')=~/duration: -1/,'infinite is left alone');r("effect clear $P");
for my$on(1,0){bare()unless$on;r("item replace entity $P weapon.offhand with air","ardanborn give $P ring_nine");r("item replace entity $P weapon.offhand from entity $P hotbar.0","item replace entity $P hotbar.0 with air");in('key 1');face(0,-40);rc();nap(1.5);my$a=pdc('shadow')//0;nap(10);my$b=pdc('shadow')//0;near(($b-$a)/10,$on?1.125:1.5,$on?'Shadow 25% slower':'Shadow unchanged without it',.12);r("item replace entity $P weapon.offhand with air","clear $P");nap(2)}

sect 'Emeralds';wearit('emeralds_of_girion');r("tp $P 0.5 -60 0.5 0 10","summon villager 0.5 -60 2.5 {Tags:[\"vl\"],NoAI:1b,VillagerData:{profession:\"minecraft:librarian\",level:5,type:\"minecraft:plains\"},Offers:{Recipes:[{buy:{id:\"minecraft:emerald\",count:20},sell:{id:\"minecraft:diamond\",count:1},maxUses:99,priceMultiplier:0.05f}]}}");nap(.6);in('click 3');nap(1.2);
my$sp=r('data get entity @e[tag=vl,limit=1] Offers.Recipes[0].specialPrice');ok($sp=~/-3$/,'20 emeralds become 17',$sp);system("$FindBin::Bin/../shot /tmp/adornments-trade.png");shut();nap(.5);$sp=r('data get entity @e[tag=vl,limit=1] Offers.Recipes[0].specialPrice');ok($sp=~/: 0$/||$sp=~/no elements|Found no/i,'price forgotten on close',$sp);r('kill @e[tag=vl]');
r("clear $P","item replace entity $P hotbar.0 with netherite_pickaxe[unbreakable={}]","effect give $P haste infinite 4 true","tp $P 0.5 -60 0.5 0 60","kill \@e[type=item]");in('key 1');my$n=0;for(1..60){r("setblock 0 -60 1 emerald_ore");nap(.1);in('mousedown 1');nap(.55);in('mouseup 1');$n++ if r('execute if block 0 -60 1 air')=~/passed/i}nap(1);r("tp \@e[type=item] $P");nap(2);my$e=0;$e+=$1 while r("clear $P minecraft:emerald 0")=~/Found (\d+)/g&&!$e;prop(($e-$n)/($n||1),.25,$n||1,"extra emeralds ($e from $n ores)");
r("item replace entity $P hotbar.0 with netherite_pickaxe[enchantments={\"minecraft:silk_touch\":1}]","clear $P emerald");for(1..8){r("setblock 0 -60 1 emerald_ore");nap(.1);in('mousedown 1');nap(.6);in('mouseup 1')}r("tp \@e[type=item] $P");nap(1.5);ok(r("clear $P minecraft:emerald 0")!~/Found/,'none with Silk Touch');r("effect clear $P","setblock 0 -60 1 air");

sect 'Quiver';wearit('quiver_of_legolas');r("clear $P","item replace entity $P hotbar.0 with bow[unbreakable={}]","give $P arrow 64");in('key 1');face(0,-60);my$s=40;for(1..$s){in('mousedown 3');nap(1.15);in('mouseup 3');nap(.15)}nap(.5);my($left)=r("clear $P minecraft:arrow 0")=~/Found (\d+)/;my$fl=cnt('@e[type=arrow]');prop(1-(64-$left)/$s,.25,$s,"arrows not spent (left $left)");
my$pick=0;$pick++ while r("execute as \@e[type=arrow,nbt={pickup:2b},limit=1,tag=!c] run tag \@s add c")=~/Added/;ok($pick==$s-(64-$left)||$fl<$s,'returned arrows cannot be picked up',"$pick of $fl");r('kill @e[type=arrow]');
r("item replace entity $P hotbar.0 with bow[enchantments={\"minecraft:infinity\":1}]","clear $P arrow","give $P arrow 1");for(1..12){in('mousedown 3');nap(1.15);in('mouseup 3');nap(.15)}ok(r("clear $P minecraft:arrow 0")=~/Found 1 /,'Infinity gives no extra arrows');r('kill @e[type=arrow]');

sect "Sam's pack";wearit('sams_pack');r("clear $P","give $P cooked_beef 64","effect give $P hunger 4 255 true");nap(5);in('key 1');face(0,-60);
r("effect give $P hunger 1 255 true");nap(2);my$eats=30;my$ate=0;for(1..$eats){r("effect give $P hunger 6 255 true");nap(6.3);eat();}
my($cb)=r("clear $P minecraft:cooked_beef 0")=~/Found (\d+)/;print "  left $cb of 64 after $eats meals\n";prop(1-(64-$cb)/$eats,.2,$eats,'cooked food not used up');
r("clear $P","give $P bread 5");for(1..3){r("effect give $P hunger 6 255 true");nap(6.3);eat()}ok(r("clear $P minecraft:bread 0")=~/Found 2 /,'bread is always used up');
r("clear $P","give $P cooked_chicken 9");my$sat=sub{r("data get entity $P foodSaturationLevel")=~/([\d.]+)f/;$1};my$fd=sub{r("data get entity $P foodLevel")=~/: (\d+)$/;$1};
for my$on(1,0){bare()unless$on;r("clear $P","give $P cooked_chicken 9");r("effect clear $P");r("effect give $P saturation 1 20 true");nap(1.5);for(1..60){last if $fd->()<=14&&$sat->()==0;r("effect give $P hunger 1 60 true");nap(1.1)}nap(1);my($f,$s)=($fd->(),$sat->());in('key 1');face(0,-60);eat();near($sat->()-$s,$on?10.8:7.2,$on?'50% more saturation':'saturation unchanged without it',.08);print "  food $f -> ",$fd->(),"\n"}

sect 'Belt of Boromir';r("effect clear $P");wearit('belt_of_boromir');r("attribute $P minecraft:max_health base set 400","effect give $P instant_health 1 20");nap(1);near(hurt(20,'generic'),20,'above half: full damage');r("effect clear $P","damage $P 250 minecraft:generic");near(hurt(20,'generic'),17,'below half: 15% less');

sect 'Sheath-belt';r("effect give $P instant_health 1 20");wearit('sheath_belt_of_anduril');r("clear $P","item replace entity $P hotbar.0 with diamond_sword");mob('husk',z=>2.5);in('key 1');r("tp $P 0.5 -60 0.5 0 10");nap(.5);for(1..60){in('click 1');nap(.75)}my($dm)=r("data get entity $P Inventory[{Slot:0b}].components.\"minecraft:damage\"")=~/: (\d+)/;$dm//=0;prop($dm/60,.5,60,"sword wear ($dm of 60)");r('kill @e[tag=v]');

sect 'loot';bare();r("kill \@e[type=item]","tp $P 0.5 -60 0.5","gamerule max_block_modifications 100000");
my$tot=0;my$rounds=6;for(1..$rounds){r("fill 8 -60 8 17 -51 12 chest{LootTable:\"minecraft:chests/desert_pyramid\"}");r("fill 8 -60 8 17 -51 12 air destroy");nap(1);$tot+=cnt('@e[type=item,nbt={Item:{id:"minecraft:nautilus_shell"}}]');r("kill \@e[type=item]")}
prop($tot/(500*$rounds),.03,500*$rounds,"desert temple chests ($tot)");
r("fill 8 -60 8 17 -51 12 chest{LootTable:\"minecraft:chests/simple_dungeon\"}","fill 8 -60 8 17 -51 12 air destroy");nap(1);ok(cnt('@e[type=item,nbt={Item:{id:"minecraft:nautilus_shell",components:{"minecraft:custom_data":{}}}}]')==0,'none in dungeons');r("kill \@e[type=item]");
for(['elder_guardian',.1,120],['wither_skeleton',.0025,300],['evoker',.01,300]){my($m,$p,$k)=@$_;my$got=0;for(1..$k){mob($m,hp=>1,max=>1);r("damage \@e[tag=v,limit=1] 1000 minecraft:player_attack by $P")}nap(1.5);$got=cnt('@e[type=item,nbt={Item:{id:"minecraft:nautilus_shell"}}]')+inv();prop($got/$k,$p,$k,"$m ($got of $k)");r("kill \@e[type=item]","kill \@e[type=$m]","clear $P","kill \@e[type=vex]")}
r("clear $P");summary();
