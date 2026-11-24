# Artifacts used by right-click, and the Arkenstone pickaxe.
use FindBin;use lib $FindBin::Bin;use Playtest;
setup();
sect 'artifacts';
my$bm=0;for my$o('human','great_eagle'){origin($o);wear();here();since($bm,46);mob('husk',z=>'6.5',nbt=>',x:1');mob('husk',z=>'11.0',keep=>1);r("execute as \@e[tag=v] run data merge entity \@s {NoAI:0b}","execute as \@e[tag=v] run attribute \@s minecraft:movement_speed base set 0");hold('breath_of_manwe');face(0,-40);rc();$bm=now();nap(1);has($P,'slow_falling',"$o Manwe slow fall");my@p=map{r("data get entity \@e[tag=v,limit=1,sort=$_] Pos[2]")=~/data: ([\d.]+)/}('nearest','furthest');# a human throws only the near mob, past the far one; an Eagle throws both
 ok($o eq'human'?abs($p[0]-11)<.5&&$p[1]>12:$p[0]>12,"$o Manwe reach","@p")}
for my$o('human','teleri'){origin($o);here();v1('husk',z=>'6.5');hold('net_of_the_havens');face(0,5);nap(46)if$o ne'human';my$b=hp();rc();ok(amp('@e[tag=v,limit=1]','slowness')==3,"$o Net Slowness IV",amp('@e[tag=v,limit=1]','slowness'));nap(5);near($b-hp(),$o eq'human'?0:4,"$o Net hurt",.1)}
for my$o('human','uruk_hai'){origin($o);here();nap(61)if$o ne'human';heal();hold('orc_draught');face(0,-40);my$b=hp($P);my$as=attr('attack_speed');rc();near($b-hp($P),$o eq'human'?4:0,"$o Orc-draught price");has($P,'speed',"$o draught speed");near(attr('attack_speed')/$as,1.2,"$o draught attack speed",.01)}
origin('human');here();hold('skin_changers_claw');face(0,-40);my$as=attr('movement_speed');r("effect clear $P");rc();near(attr('movement_speed'),.11,'Claw speed',.01);v1('husk');near(hit(10),12,'Claw +20%');nap(8.5);near(attr('movement_speed'),.1,'Claw ends',.01);
hold('longbottom_pipe');rc();has($P,'regeneration','Pipe regen');
here();v1('spider');hold('phial_of_galadriel');face(0,-40);r("effect give $P darkness 60");my$b=hp();rc();near($b-hp(),4,'Phial burns spider',.1);has('@e[tag=v,limit=1]','glowing','Phial reveals');has($P,'darkness','Phial lifts darkness',0);
r("kill \@e[type=!player]","fill -2 -60 8 2 -54 8 stone");here();hold('hithlain');face(0,-20);rc();nap(1);my$z=r("data get entity $P Pos[2]");print "  Hithlain pulled to $z (from 0.5; wall at 8)\n";ok($z=~/data: ([\d.]+)/&&$1>3,'Hithlain pulls');r("fill -2 -60 8 2 -54 8 air");here();
hold('athelas');face(0,-40);rc();has($P,'regeneration','Athelas regen unwounded');
sect 'pickaxe 3x3';r("fill -3 -60 2 3 -55 4 stone","gamemode survival $P");r("tp $P 0.5 -60 0.5 0 0");hold('arkenstone_pickaxe_diamond');nap(.5);in('mousedown 1');nap(1.6);in('mouseup 1');nap(.4);my$n=0;for my$x(-1..1){for my$y(-60..-57){for my$zz(2){$n++ if r("execute if block $x $y $zz air")=~/passed/i}}}print "  blocks gone in 1st layer: $n\n";ok($n>=8,'Arkenstone pickaxe bores 3x3',$n);r("fill -3 -60 2 3 -55 4 air","kill \@e[type=item]");

r("clear $P");
summary();
