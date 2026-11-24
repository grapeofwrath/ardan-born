# Every smithing upgrade, at a real table with the client, and rune inscription.
use FindBin;use lib $FindBin::Bin;use Playtest;
setup();
sect 'weapons and artifacts';
sub made{my($want,$n)=@_;ok(holds("*[minecraft:custom_data~{PublicBukkitValues:{$want}}]"),"smith $n")}
my@u=([pearl_of_osse=>'trident','spear_of_the_havens'],[feather_of_thorondor=>'diamond_spear','aeglos'],[feather_of_thorondor=>'breeze_rod','breath_of_manwe'],[arkenstone=>'diamond_pickaxe','arkenstone_pickaxe'],[arkenstone=>'netherite_axe','axe_of_azaghal'],[honey_of_beorn=>'bow','hunting_bow'],[honey_of_beorn=>'flint','skin_changers_claw'],[light_of_the_trees=>'glass_bottle','phial_of_galadriel'],[light_of_the_trees=>'netherite_sword','orcrist'],[star_of_elendil=>'goat_horn','horn_of_gondor'],[star_of_elendil=>'diamond_sword','anduril'],[evenstar=>'honey_bottle','miruvor'],[evenstar=>'diamond_sword','hadhafang'],[white_hand=>'diamond_sword','scimitar_of_isengard'],[white_hand=>'glass_bottle','orc_draught'],[pearl_of_osse=>'lead','net_of_the_havens'],[mallorn_leaf=>'bow','bow_of_the_galadhrim'],[mallorn_leaf=>'lead','hithlain'],[mathom=>'diamond_sword','sting'],[mathom=>'stick','longbottom_pipe'],[banner_of_the_mark=>'netherite_spear','spear_of_the_mark'],[banner_of_the_mark=>'diamond_horse_armor','barding_of_the_mark']);
for(@u){smith($_->[0],"minecraft:$_->[1]");made("\"ardanborn:item\":\"$_->[2]\"","$_->[0]+$_->[1]")}
sect 'armor';
my%set=(arkenstone=>'arkenstone',pearl_of_osse=>'alqualonde',feather_of_thorondor=>'thorondor',mallorn_leaf=>'lorien',mathom=>'mithril',light_of_the_trees=>'gondolin',star_of_elendil=>'citadel',evenstar=>'imladris',white_hand=>'isengard',honey_of_beorn=>'carrock',banner_of_the_mark=>'edoras');
for my$k(sort keys%set){for('helmet','chestplate','leggings','boots'){smith($k,"minecraft:diamond_$_");made("\"ardanborn:armor_set\":\"$set{$k}\"","$k+diamond_$_")}}
sect 'netherite upgrades';
for my$c(['citadel_diamond_chestplate','netherite_chestplate','citadel'],['arkenstone_diamond_boots','leather_boots','arkenstone'],['lorien_diamond_chestplate','leather_chestplate','lorien'],['mithril_diamond_chestplate','chainmail_chestplate','mithril'],['alqualonde_diamond_helmet','turtle_helmet','alqualonde']){my($g,$m,$s)=@$c;smith('minecraft:netherite_upgrade_smithing_template',$g,'minecraft:netherite_ingot');ok(holds("minecraft:$m\[minecraft:custom_data~{PublicBukkitValues:{\"ardanborn:armor_set\":\"$s\"}}]")&&!holds('minecraft:netherite_ingot'),"netherite $g")}
sect 'runes';
smith('rune_stone_westernesse','minecraft:diamond_sword','minecraft:lapis_lazuli');ok(!holds('minecraft:diamond_sword[minecraft:custom_data]'),'non-Smith cannot inscribe');
class('smith');smith('rune_stone_westernesse','minecraft:diamond_sword','minecraft:lapis_lazuli');ok(holds('minecraft:diamond_sword[minecraft:custom_data]'),'Smith inscribes');
for(0..8){if(r("execute if items entity $P hotbar.$_ minecraft:diamond_sword[minecraft:custom_data]")=~/passed/i){r("item replace entity $P hotbar.0 from entity $P hotbar.$_")}}in('key 1');r("setblock 0 -60 2 air");here();v1('husk');nap(.4);near(hit(10),13,'Westernesse +30% undead');v1('pillager');near(hit(10),10,'Westernesse not illager');
class('herbalist');r("clear $P");summary();
