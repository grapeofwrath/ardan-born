package dev.grapeofwrath.ardanborn.adornments;

import dev.grapeofwrath.ardanborn.items.rings.RingBearer;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Warden;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/// Where adornments are found: in the chests of some structures (not those that hold Rings of Power), in the
/// vaults of trial chambers, and on strong mobs slain by players.
public final class AdornmentLoot implements Listener {
    private static final double CHEST = 0.03;
    private static final double GREAT_MOB = 0.10;
    private static final double MOB = 0.01;
    /// Wither skeletons are easily farmed.
    private static final double WITHER_SKELETON = 0.0025;
    private static final Set<String> CHESTS = Set.of("chests/woodland_mansion", "chests/buried_treasure",
            "chests/desert_pyramid", "chests/jungle_temple");
    private static final Set<String> VAULTS =
            Set.of("chests/trial_chambers/reward", "chests/trial_chambers/reward_ominous");

    private static boolean roll(double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    private static ItemStack any() {
        Adornment[] all = Adornment.values();
        return all[ThreadLocalRandom.current().nextInt(all.length)].create();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChest(LootGenerateEvent event) {
        if (CHESTS.contains(event.getLootTable().getKey().getKey()) && roll(CHEST)) event.getLoot().add(any());
    }

    /// Vaults dispense their loot rather than fill a chest.
    @EventHandler(ignoreCancelled = true)
    public void onVault(BlockDispenseLootEvent event) {
        LootTable table = event.getLootTable();
        if (table == null || !VAULTS.contains(table.getKey().getKey()) || !roll(CHEST)) return;
        List<ItemStack> loot = new ArrayList<>(event.getDispensedLoot());
        loot.add(any());
        event.setDispensedLoot(loot);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        LivingEntity mob = event.getEntity();
        // The servants sent by a ring leave nothing behind.
        if (mob.getKiller() == null || mob.getPersistentDataContainer().has(RingBearer.SERVANT)) return;
        if (roll(chance(mob))) event.getDrops().add(any());
    }

    private static double chance(LivingEntity mob) {
        return switch (mob) {
            case ElderGuardian _, Warden _ -> GREAT_MOB;
            case WitherSkeleton _ -> WITHER_SKELETON;
            case PiglinBrute _ -> MOB;
            case Evoker evoker -> evoker.getRaid() == null ? MOB : 0;
            default -> 0;
        };
    }
}
