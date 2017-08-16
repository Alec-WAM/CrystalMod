package alec_wam.CrystalMod.util.loot;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootHelper {

	public static final String VANILLA_LOOT_POOL_ID = "main";	
	
	public static void createPoolIfNotExists(LootTable lootTable, String poolId) {
        if (poolId.equals(VANILLA_LOOT_POOL_ID) || lootTable.getPool(poolId) != null) return;
        lootTable.addPool(new LootPool(new LootEntry[] {}, new LootCondition[] {}, new RandomValueRange(1), new RandomValueRange(0), poolId));
    }
	
	public static LootEntryItem createLootEntryItem(Item item, int weight, int quality) {
        return createLootEntryItem(item, weight, quality, new LootFunction[]{});
    }

    public static LootEntryItem createLootEntryItem(Item item, int weight, int quality, LootFunction[] lootFunctions, LootCondition... lootConditions) {
        return new LootEntryItem(item, weight, quality, lootFunctions, lootConditions, CrystalMod.prefix(item.getUnlocalizedName()));
    }
	
}
