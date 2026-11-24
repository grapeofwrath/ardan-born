# Every item by command, every recipe, and the guards that keep our items out of vanilla recipes.
use FindBin;use lib $FindBin::Bin;use Playtest;
setup();
r("gamerule spawn_mobs false","gamerule advance_time false","gamerule advance_weather false","time set noon","weather clear","difficulty normal","gamerule natural_health_regeneration false","gamemode survival ".$P,"gamerule send_command_feedback true");
sect 'give';my@ids;
for my$s(qw(arkenstone alqualonde thorondor lorien mithril gondolin citadel imladris isengard carrock edoras)){for my$t(qw(diamond netherite)){push@ids,"${s}_${t}_$_"for qw(helmet chestplate leggings boots)}}
push@ids,qw(arkenstone pearl_of_osse feather_of_thorondor honey_of_beorn light_of_the_trees mallorn_leaf star_of_elendil evenstar mathom white_hand banner_of_the_mark
 spear_of_the_havens aeglos_diamond aeglos_netherite arkenstone_pickaxe_diamond hunting_bow phial_of_galadriel horn_of_gondor miruvor scimitar_of_isengard_diamond axe_of_azaghal_diamond net_of_the_havens breath_of_manwe bow_of_the_galadhrim hithlain sting_diamond longbottom_pipe orcrist_diamond anduril_diamond hadhafang_diamond orc_draught skin_changers_claw spear_of_the_mark_diamond barding_of_the_mark_diamond athelas ring_seven ring_nine);
for(@ids){r("clear ".$P);my$o=r("ardanborn give ".$P." $_");ok($o=~/^Gave 1/,"give $_",$o)}
ok(r("ardanborn give ".$P." nonsense")=~/Unknown/,'give unknown rejected');
ok(r("ardanborn ring release seven")=~/Not a unique/,'release non-unique rejected');
sect 'craft';
sub craft{my($shape,$k,$name)=@_;my$i=0;my@it;for my$c(split//,join'',@$shape){push@it,"{Slot:${i}b,id:\"$k->{$c}\",count:1}"if$c ne' ';$i++}
 r("kill \@e[type=item]","setblock 10 -60 10 air","setblock 10 -59 10 air","setblock 10 -60 10 crafter[orientation=up_north]{Items:[".join(',',@it)."]}","setblock 11 -60 10 redstone_block");nap .7;
 my$o=r('data get entity @e[type=item,limit=1] Item.id');my$c=r('data get entity @e[type=item,limit=1] Item.components."minecraft:custom_data"');r("setblock 11 -60 10 air","kill \@e[type=item]");"$o $c"}
my%d=(G=>'gold_ingot',D=>'diamond');
my@t=([arkenstone=>'amethyst_shard','emerald'],[pearl_of_osse=>'prismarine_shard','nautilus_shell'],[feather_of_thorondor=>'feather','breeze_rod'],[honey_of_beorn=>'honeycomb','honey_block'],[light_of_the_trees=>'glowstone_dust','glow_berries'],[mallorn_leaf=>'oak_leaves','string'],[star_of_elendil=>'quartz','iron_ingot'],[evenstar=>'ender_pearl','glow_ink_sac'],[mathom=>'pumpkin_pie','iron_ingot'],[white_hand=>'coal_block','white_dye'],[banner_of_the_mark=>'hay_block','leather']);
for(@t){my($id,$u,$v)=@$_;my$o=craft(['UVU','GDG','UVU'],{%d,U=>$u,V=>$v});ok($o=~/"$id"/,"craft $id",$o=~/custom|ardan/?'':$o)}
sect 'guards';my$o=craft(['PP ','PP ','   '],{P=>'prismarine_shard'});ok($o=~/prismarine"/,'control: vanilla prismarine crafts',$o);
my%rs=(westernesse=>['bone','gold_nugget'],warning=>['glow_ink_sac','lapis_lazuli'],frost=>['blue_ice','snowball'],delving=>['coal','raw_iron'],harvest=>['wheat','bone_meal'],vein=>['redstone','iron_ingot']);
for(sort keys%rs){my$o=craft(['ABA','BXB','ABA'],{A=>$rs{$_}[0],B=>$rs{$_}[1],X=>'amethyst_shard'});ok($o=~/rune_stone/,"craft rune $_",$o)}
# guarded: custom prismarine crystals in sea lantern; custom stick in torch
sub gcraft{my($items,$n,$want)=@_;r("kill \@e[type=item]","setblock 10 -60 10 air","setblock 10 -60 10 crafter[orientation=up_north]{Items:[$items]}","setblock 11 -60 10 redstone_block");nap .7;my$o=r('data get entity @e[type=item,limit=1] Item.id');r("setblock 11 -60 10 air");ok(($o=~/entity data/?1:0)==$want,$n,$o)}
sub c{my($id)=@_;"components:{\"minecraft:custom_data\":{PublicBukkitValues:{\"ardanborn:item\":\"$id\"}}}"}
my$sh=join',',map{"{Slot:${_}b,id:\"prismarine_shard\",count:1}"}0,2,6,8;
my$pc=join',',map{"{Slot:${_}b,id:\"prismarine_crystals\",count:1}"}1,3,4,5,7;my$pg=join',',map{"{Slot:${_}b,id:\"prismarine_crystals\",count:1,".c('mathom')."}"}1,3,4,5,7;
gcraft("$sh,$pc",'vanilla sea lantern crafts',1);gcraft("$sh,$pg",'templates refused in sea lantern',0);
gcraft("{Slot:0b,id:\"coal\",count:1},{Slot:3b,id:\"stick\",count:1}",'vanilla torch crafts',1);gcraft("{Slot:0b,id:\"coal\",count:1},{Slot:3b,id:\"stick\",count:1,".c('longbottom_pipe')."}",'pipe refused in torch',0);
gcraft("{Slot:0b,id:\"lead\",count:1}",'noop',0);
gcraft("{Slot:0b,id:\"flint\",count:1,".c('skin_changers_claw')."},{Slot:1b,id:\"iron_ingot\",count:1}",'claw refused in flint&steel',0);
gcraft("{Slot:0b,id:\"breeze_rod\",count:1,".c('breath_of_manwe')."}",'Manwe refused in wind charge',0);
for my$q(['stick','longbottom_pipe'],['breeze_rod','x']){}
r("setblock 12 -60 12 furnace{Items:[{Slot:0b,id:\"raw_iron\",count:1},{Slot:1b,id:\"stick\",count:1,".c('longbottom_pipe')."}]}");nap 1.5;ok(r("data get block 12 -60 12 Items[{Slot:1b}].id")=~/stick/,'pipe not burned as fuel');r("setblock 12 -60 12 air");
r("kill \@e[type=item]");summary();
