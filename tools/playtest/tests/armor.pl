# Armor piece and full-set effects, for anyone and for the set's own people. Takes about 25 minutes.
use FindBin;use lib $FindBin::Bin;use Playtest;
setup();
sect 'controls'; origin('human');wear();hold('minecraft:diamond_sword');mob('husk');near(hit(10),10,'control hit 10');mob('husk');near(dmg(10,'mob_attack'),10,'control hurt 10');
sect 'citadel';my$la=0;
for my$o('human','dunedain'){my$own=$o ne'human';origin($o);wear('citadel');
 r("effect give $P minecraft:weakness 10");has($P,'weakness',"$o Steadfast blocks weakness",0);
 r("effect clear $P","effect give $P minecraft:slowness 20");my$d=ev($P,'slowness');$d=~/duration: (\d+)/;near($1,$own?300:400,"$o Steadfast slowness duration",.03);r("effect clear $P");
 wear('citadel');since($la,121)if$own;r("attribute $P minecraft:max_health base set 20");heal();nap(.5);r("damage $P 16 minecraft:generic");$la=now();nap(.2);has($P,'strength',"$o Last Alliance strength");has($P,'speed',"$o Last Alliance speed");r("attribute $P minecraft:max_health base set 400");heal();r("effect clear $P");}
sect 'gondolin';
for my$o('human','noldor'){my$own=$o ne'human';origin($o);wear('gondolin');r("effect give $P minecraft:blindness 10","effect give $P minecraft:darkness 10");has($P,'blindness',"$o Unclouded blindness",0);has($P,'darkness',"$o Unclouded darkness",0);
 heal();near(hurt(10,'magic'),$own?7:8,"$o Spell-ward magic");heal();near(hurt(10,'wither'),$own?7:8,"$o Spell-ward wither");
 here();r("setblock 0 -61 0 snow_block");nap(.5);near(attr('movement_speed'),.1*($own?1.2:1.1),"$o Helcaraxe speed on snow",.005);r("setblock 0 -61 0 grass_block");nap(.4);near(attr('movement_speed'),.1,"$o Helcaraxe off snow",.005);
 r("setblock 0 -61 0 stone","effect clear $P");heal();near(hurt(5,'freeze'),0,"$o Helcaraxe freeze immune");}
sect 'imladris';my$fs=0;
for my$o('human','peredhil'){my$own=$o ne'human';origin($o);wear('imladris');here();since($fs,46);mob('husk');$fs=now();my$a=dmg(10,'mob_attack');my$b=dmg(10,'mob_attack');near($a,5,"$o Foresight first hit halved");near($b,10,"$o Foresight second hit full");
 heal();near(hurt(20,'fall'),$own?5:10,"$o Soft Landing");
 r("time set midnight");nap(.3);heal();near(hurt(10,'generic'),8.8,"$o Star-woven night sky");here();r("fill -4 -57 -4 4 -57 4 stone");nap(1);heal();near(hurt(10,'generic'),$own?8.8:10,"$o Star-woven under roof");r("fill -4 -57 -4 4 -57 4 air","time set noon");
 r("effect clear $P");heal();nap(.6);r("damage $P 60 minecraft:generic");my$h=hp($P);nap(16.2);near(hp($P)-$h,$own?5:4,"$o House of Elrond 16s heal (2 per 6/8s)",.4);
 # horse
 r("kill \@e[tag=h]","summon horse 0.5 -60 0.5 {Tags:[\"h\"],Tame:1b,NoAI:1b,Health:40f,attributes:[{id:\"minecraft:max_health\",base:100}],equipment:{saddle:{id:\"saddle\",count:1}}}","ride $P mount \@e[tag=h,limit=1]");nap(.4);heal();near(hurt(10,'generic'),8.5,"$o Rider's Grace rider");my$x=hp('@e[tag=h,limit=1]');r("damage \@e[tag=h,limit=1] 10 minecraft:generic");my$y=hp('@e[tag=h,limit=1]');near($x-$y,8.5,"$o Rider's Grace mount");nap(10.2);my$z=hp('@e[tag=h,limit=1]');near($z-$y,$own?4:0,"$o Rider's Grace mount heal 10s",.3);r("ride $P dismount","kill \@e[tag=h]","tp $P 0.5 -60 0.5 0 0");}
sect 'isengard';
for my$o('human','uruk_hai'){my$own=$o ne'human';origin($o);wear('isengard');nap(.3);prop(massrate('husk','weakness',200),$own?.2:.1,200,"$o Dread");
 mob('husk');heal();my$b=hp();dmg(20,'mob_attack');near($b-hp(),20*($own?.25:.15),"$o Spiked Plate reflect",.05);
 r("effect clear $P");mob('husk',hp=>5);r("damage \@e[tag=v,limit=1] 50 minecraft:player_attack by $P");nap(.2);has($P,'speed',"$o Fury's Tread speed on kill");r("effect clear $P");
 r("attribute $P minecraft:max_health base set 20");heal();nap(.5);my$as=attr('attack_speed');r("damage $P 12 minecraft:generic");nap(.4);near(attr('attack_speed')/$as,1.2,"$o Berserker attack speed",.01);near(attr('movement_speed'),$own?.11:.1,"$o Berserker move speed",.01);r("effect give $P minecraft:instant_health 1 5");nap(.4);near(attr('attack_speed')/$as,1,"$o Berserker ends",.01);r("attribute $P minecraft:max_health base set 400");heal();}
sect 'carrock';
for my$o('human','beorning'){my$own=$o ne'human';origin($o);wear('carrock');nap(.4);near(attr('jump_strength'),.42*1.095,"$o Great Bounds day",.01);r("time set midnight");nap(.4);near(attr('jump_strength'),.42*($own?1.162:1.095),"$o Great Bounds night",.01);
 r("time set noon");for my$n(0,1){r($n?"time set midnight":"time set noon");wear('carrock');my$ev=$n&&$own?3:4;my$seq='';for(1..$ev){mob('husk',n=>2);my@h=(1..2);r("damage \@e[tag=v,limit=1,sort=nearest] 10 minecraft:player_attack by $P");nap(.2);my$dm=cnt('@e[tag=v,nbt={Health:1000.0f}]');$seq.=$dm==0?'S':'.';}ok($seq eq('.'x($ev-1)).'S',"$o Maul cadence ".($n?'night':'day'),$seq);}r("time set noon","effect clear $P");}

sect 'arkenstone';sub tnt{heal();nap(.6);my$b=hp($P);r("summon tnt 3.5 -60 0.5 {fuse:1}");nap(.8);my$d=$b-hp($P);r("fill -8 -60 -8 8 -55 8 air","fill -8 -61 -8 8 -61 8 grass_block","fill -8 -63 -8 8 -62 8 dirt","kill \@e[type=item]");here();$d}
origin('human');r("attribute $P minecraft:knockback_resistance base set 1");wear();here();my$tnt0=tnt();
for my$o('human','dwarf'){my$own=$o ne'human';origin($o);r("attribute $P minecraft:knockback_resistance base set 0");wear('arkenstone');here();near(attr('knockback_resistance'),.4,"$o Unmoved kb");
 near(tnt()/$tnt0,.75,"$o Unmoved TNT",.05);near(attr('step_height'),$own?1.1:.6,"$o Snowtread step");
 here();box();heal();near(hurt(10,'generic'),$own?8.5:10,"$o Unmoved in deep");unbox();r("fill 0 -61 0 0 -61 0 stone");
 wear('arkenstone:netherite');here();nap(.4);near(attr('movement_speed'),$own?.11:.1,"$o Snowtread netherite stone speed",.005);r("setblock 0 -61 0 grass_block","attribute $P minecraft:knockback_resistance base set 1");}
sect 'alqualonde';
for my$o('human','teleri'){my$own=$o ne'human';origin($o);wear('alqualonde');r("time set noon","weather rain");here();nap(3);heal();near(hurt(10,'generic'),8.8,"$o Swan-mail rain");
 r("damage $P 100 minecraft:generic");my$b=hp($P);nap(10.1);near(hp($P)-$b,$own?2:0,"$o Swan-mail heal 10s",.1);
 my$e=hrate(80,'slowness');prop($e,$own?.25:.15,80,"$o Undertow");
 heal();r("damage $P 100 minecraft:generic");my($h,$tr)=(0,0);nap(3.1);for(1..20){mob('husk');my$x=hp($P);r("damage \@e[tag=v,limit=1] 4 minecraft:player_attack by $P");my$y=hp($P);$h++if$y-$x>=1.9;nap(3.05)}prop($h/20,$own?.25:.15,20,"$o Song of the Sea");
 r("weather clear");nap(12);heal();near(hurt(10,'generic'),10,"$o Swan-mail dry");
 for my$m('drowned','guardian'){r("kill \@e[tag=v]","summon $m 0.5 -60 6.5 {Tags:[\"v\"],PersistenceRequired:1b,attributes:[{id:\"minecraft:movement_speed\",base:0.0}]}");heal();my$b2=hp($P);nap(5);near($b2-hp($P),0,"$o Friend of Osse $m peaceful")}r("kill \@e[tag=v]")}
sect 'thorondor';sub lift{my($want,$name)=@_;my$hit=0;for(1..6){mob('husk',n=>20);r("execute as \@e[tag=v] run data merge entity \@s {NoAI:0b}","execute as \@e[tag=v] run attribute \@s minecraft:movement_speed base set 0","execute as \@e[tag=v] run attribute \@s minecraft:knockback_resistance base set 1");nap(.2);r("execute as \@e[tag=v] run damage \@s 4 minecraft:player_attack by $P");my$mx=0;for(1..8){my$c=cnt('@e[tag=v,x=-30,dx=60,z=-30,dz=60,y=-58.0,dy=5]');$mx=$c if$c>$mx;nap(.03)}$hit+=$mx}prop($hit/120,$want,120,$name)}
for my$o('human','great_eagle'){my$own=$o ne'human';origin($o);wear('thorondor');here();
 my$n=0;for(1..60){mob('husk');r("data merge entity \@e[tag=v,limit=1] {NoAI:0b}","attribute \@e[tag=v,limit=1] minecraft:movement_speed base set 0");r("damage $P 6 minecraft:mob_attack by \@e[tag=v,limit=1]");nap(.12);my@m=motion();my$p=r('data get entity @e[tag=v,limit=1] Pos[2]');my($z)=$p=~/data: ([\d.]+)d/;$n++if$z>4.6;nap(.4);heal()unless$_%20}prop($n/60,$own?.25:.15,60,"$o Storm-feathers push");
 lift($own?.25:.15,"$o Talon-grip lift");
 r("kill \@e[type=!player]");nap(1.5);mob('husk');r("tp $P 0.5 -60 0.5 0 0");nap(.8);has('@e[tag=v,limit=1]','glowing',"$o Hawk-sight glow")}
sect 'lorien';
for my$o('human','silvan'){my$own=$o ne'human';for my$bi('plains','forest'){r("fillbiome -20 -61 -20 20 -50 20 minecraft:$bi");origin($o);wear('lorien');here();
 near(attr('step_height'),1.1,"$o Bough-runner step");
 my$n=0;for(1..80){mob('husk');my$b=hp($P);r("damage $P 6 minecraft:mob_attack by \@e[tag=v,limit=1]");$n++if$b-hp($P)<.1;nap(.42);heal()unless$_%20}prop($n/80,$bi eq'forest'?.15:.10,80,"$o Elusive $bi");
 mob('husk',z=>'25.5');r("effect clear $P");my$d=arrow(10);near($d,10*($own?1.2:1.1)*($own&&$bi eq'forest'?1.2:1),"$o Far-sighted $bi 25 blocks");my$e=ev($P,'speed');my($a)=$e=~/amplifier: (\d)/;$a//=0;print "   Leaf-light $bi amp $a\n";ok($e ne'',"$o Leaf-light $bi speed");
 mob('husk',z=>'10.5');near(arrow(10),10*($own&&$bi eq'forest'?1.2:1),"$o Far-sighted near $bi")}
 my$n=0;for(1..60){mob('husk');mob('husk',keep=>1,z=>'8.5');arrow(10);$n++if cnt('@e[tag=v,nbt=!{Health:1000.0f}]')>1}prop($n/60,$own?.25:.15,60,"$o Galadhrim Volley")}
r("fillbiome -20 -61 -20 20 -50 20 minecraft:plains");
sect 'mithril';
for my$o('human','hobbit'){my$own=$o ne'human';origin($o);wear('mithril','chest');here();mob('husk');near(dmg(20,'mob_attack'),20*($own?.6:.7),"$o Mithril coat big hit");near(dmg(6,'mob_attack'),6,"$o Mithril coat small hit");wear('mithril');here();mob('husk');near(dmg(6,'mob_attack'),6*($own?.88:.9),"$o Fellowship");
 mob('zombie');my$d=arrow(0.01,'snowball');near($d,$own?4:2,"$o Stone's Throw snowball")}
sect 'edoras';
for my$o('human','rohirrim'){my$own=$o ne'human';origin($o);wear('edoras');here();my$as=attr('attack_speed');horse();near(attr('attack_speed')/$as,$own?1.25:1.15,"$o Firm in the Saddle",.01);
 mob('skeleton');heal();r("summon arrow 0.5 -50 8 {Tags:[\"ar\"],NoGravity:1b}");nap(.5);my$b=hp($P);r("damage $P 10 minecraft:arrow by \@e[tag=ar,limit=1] from \@e[tag=v,limit=1]");near($b-hp($P),8,"$o Horsetail Crest rider");my$hb=hp('@e[tag=h,limit=1]');r("damage \@e[tag=h,limit=1] 10 minecraft:arrow by \@e[tag=ar,limit=1] from \@e[tag=v,limit=1]");near($hb-hp('@e[tag=h,limit=1]'),$own?8:10,"$o Horsetail Crest mount");r("kill \@e[tag=ar]");
 unhorse()}

sect 'Death! ramp';for my$o('human','rohirrim'){origin($o);wear();nap(11);wear('edoras');here();hold('minecraft:diamond_sword');horse();r("kill \@e[tag=v]");my$s='';for my$k(0..8){mob('husk',keep=>1,z=>30.5+$k*2);my$b=r('x')&&0;r("tag \@e[tag=new] remove old");my$d=do{r("tag \@e[tag=v] remove v","tag \@e[tag=new,sort=nearest,limit=1] add cur","execute positioned 0.5 -60 ".(30.5+$k*2)." run tag \@e[type=husk,distance=..0.6] add v");my$x=hp();r("damage \@e[tag=v,limit=1] 10 minecraft:player_attack by $P");$x-hp()};$s.=sprintf'%.1f ',$d;r("tag \@e[tag=v] remove v","summon husk 0.5 -60 4.5 {Tags:[\"k\"],NoAI:1b,Health:1f}","damage \@e[tag=k,limit=1] 50 minecraft:player_attack by $P");nap(.2)}print"  $o $s\n";ok($s eq($o eq'human'?'10.0 10.4 10.8 11.2 11.6 12.0 12.0 12.0 12.0 ':'12.0 12.4 12.8 13.2 13.6 14.0 14.4 14.8 14.8 '),"$o Death! ramp");unhorse()}
r("kill \@e[type=!player]");
sect 'spell-ward, real sources';
sub src{my$k=shift;here();heal();nap(1);my$b=hp($P);if($k eq'fangs'){r("summon evoker_fangs 0.5 -60 0.5 {Warmup:0}");nap(1.4)}
 elsif($k eq'fireball'){r("summon dragon_fireball 0.5 -58 0.5 {Motion:[0.0,-0.3,0.0],acceleration_power:0.1d}");nap(2.2)}
 else{r("summon $k 0.5 -57.5 0.5 {Motion:[0.0,-0.8,0.0],Item:{id:\"$k\",count:1,components:{\"minecraft:potion_contents\":{potion:\"minecraft:harming\"}}}}");nap(1.6)}
 my$d=$b-hp($P);r("kill \@e[type=!player]");$d}
for my$k('splash_potion','lingering_potion','fireball','fangs'){origin('human');wear();my$c=src($k);for my$o('human','noldor'){origin($o);wear('gondolin');my$g=src($k);ok($c>0&&abs($g/$c-($o eq'human'?.8:.7))<.02,"$o Spell-ward $k","$c -> $g")}}
summary();
