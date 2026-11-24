# Weapon bonuses, barding, marked arrows and Cleave.
use FindBin;use lib $FindBin::Bin;use Playtest;
setup();
sect 'weapons';
sub wp{my($o,$item,$type,$want,$n,%x)=@_;origin($o);wear();here();hold($item);mob($type,%x);r("effect give \@e[tag=v] minecraft:glowing 30")if$x{glow};near(hit(10),10*$want,"$o $item vs $type $n")}
wp('human','sting_diamond','spider',1.25,'');wp('hobbit','sting_diamond','spider',1.5625,'unwary');wp('hobbit','sting_diamond','husk',1.25,'unwary');wp('human','sting_diamond','husk',1,'');wp('human','sting_diamond','cave_spider',1.25,'');
for my$t('pillager','vindicator','evoker','piglin_brute','zombified_piglin'){wp('human','orcrist_diamond',$t,1.2,'')}wp('noldor','orcrist_diamond','pillager',1.2,'');has('@e[tag=v,limit=1]','weakness','noldor Orcrist weakness');wp('noldor','orcrist_diamond','piglin_brute',1.5,'+Wrath');wp('human','orcrist_diamond','husk',1,'');
wp('human','anduril_diamond','husk',1,'');my$f=r('data get entity @e[tag=v,limit=1] Fire');ok($f=~/data: ([1-9]\d*)s/,'Anduril burns undead',$f);wp('dunedain','anduril_diamond','husk',1.05,'');wp('human','anduril_diamond','pillager',1,'');
wp('human','axe_of_azaghal_diamond','husk',1.15,'big');wp('dwarf','axe_of_azaghal_diamond','husk',1.5,'big+Durin');has('@e[tag=v,limit=1]','slowness','Axe of Durin slow');wp('human','axe_of_azaghal_diamond','husk',1,'small',max=>30,hp=>30);
wp('human','scimitar_of_isengard_diamond','husk',1.25,'wounded',hp=>400);wp('human','scimitar_of_isengard_diamond','husk',1,'healthy');
wp('human','aeglos_diamond','husk',1.25,'glowing',glow=>1);wp('human','aeglos_diamond','husk',1,'plain');
wp('human','spear_of_the_havens','husk',1,'');wp('teleri','spear_of_the_havens','husk',1.15,'');
for my$o('human','rohirrim'){origin($o);wear();here();hold('spear_of_the_mark_diamond');mob('pillager');near(hit(10),$o eq'human'?10:10*1.15*1.2,"$o Spear of Mark on foot vs illager");horse();mob('husk');near(hit(10),$o eq'human'?11.5:16,"$o Spear of Mark mounted (cap .60)");unhorse()}
wp('rohirrim','minecraft:iron_spear','husk',1.15,'Spear-hardy');
for my$o('human','peredhil'){origin($o);hold('hadhafang_diamond');horse();mob('husk');near(hit(10),$o eq'human'?10:11.5,"$o Hadhafang mounted");unhorse();hold('hadhafang_diamond');nap(.8);r("damage $P 100 minecraft:generic");mob('husk',hp=>1);my$b=hp($P);r("damage \@e[tag=v,limit=1] 50 minecraft:player_attack by $P");near(hp($P)-$b,2,"$o Hadhafang kill heal",.4)}
sub ar{my($o,$pdc,$want,$n,%x)=@_;origin($o);here();mob($x{type}//'husk',%x);near(arrow(10,'arrow',$pdc),10*$want,"$o $pdc arrow $n")}
ar('human','galadhrim_arrow',1.2,'unhurt');ar('silvan','galadhrim_arrow',1.2,'unhurt');has('@e[tag=v,limit=1]','slowness','silvan Galadhrim slow');ar('human','galadhrim_arrow',1,'hurt',hp=>900);
r("time set midnight");ar('human','hunting_arrow',1,'night');ar('beorning','hunting_arrow',1.2,'night');r("time set noon");ar('beorning','hunting_arrow',1,'day');ar('silvan','x',1.5,'spider Bane',type=>'spider');
for my$o('human','rohirrim'){origin($o);here();r("kill \@e[tag=h]","summon horse 0.5 -60 0.5 {Tags:[\"h\"],Tame:1b,NoAI:1b,Health:40f,attributes:[{id:\"minecraft:max_health\",base:100},{id:\"minecraft:armor\",base:0}],equipment:{saddle:{id:\"saddle\",count:1}}}");hold('barding_of_the_mark_diamond');r("item replace entity \@e[tag=h,limit=1] armor.body from entity $P weapon.mainhand","ride $P mount \@e[tag=h,limit=1]");nap(.4);my$b=hp('@e[tag=h,limit=1]');r("damage \@e[tag=h,limit=1] 10 minecraft:generic");my$e=hp('@e[tag=h,limit=1]');near($b-$e,8,"$o Barding");nap(10.1);my$g=hp('@e[tag=h,limit=1]')-$e;ok($o eq'human'?$g<=2:$g>=4&&$g<=6,"$o Barding heal 10s",$g);unhorse()}

sect 'cleave';class('warrior');
for my$c(['human','minecraft:diamond_axe'],['dwarf','minecraft:diamond_axe'],['noldor','minecraft:diamond_sword'],['human','anduril_diamond'],['dwarf','axe_of_azaghal_diamond']){my($o,$i)=@$c;origin($o);wear();here();hold($i);v1('husk',z=>'2.7');r("summon husk 2.3 -60 3.0 {Tags:[\"w\"],NoAI:1b,Silent:1b,Health:1000f,attributes:[{id:\"minecraft:max_health\",base:1000},{id:\"minecraft:armor\",base:0}]}");nap(3.5);face(0,10);my$b=hp();in('click 1');nap(.6);my$d=$b-hp();my$s=1000-hp('@e[tag=w,limit=1]');near($d?$s/$d:0,.5,"$o $i Cleave splash is half (main ".sprintf('%.2f',$d).")",.02)}
class('herbalist');
summary();
