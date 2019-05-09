package alec_wam.CrystalMod.init;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistrationHandler {

	public static List<BlockItemPair> BLOCK_LIST;
	public static List<Item> ITEM_LIST;
	public static List<TileEntityType<?>> TILE_LIST;
	public static List<BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block>> VARIANT_BLOCK_LIST;
	public static List<ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item>> VARIANT_ITEM_LIST;
	static{
		BLOCK_LIST = new ArrayList<BlockItemPair>();
		ITEM_LIST = new ArrayList<Item>();
		TILE_LIST = new ArrayList<TileEntityType<?>>();
		VARIANT_BLOCK_LIST = new ArrayList<BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block>>();
		VARIANT_ITEM_LIST = new ArrayList<ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item>>();
		ModBlocks.buildList();
		ModItems.buildList();
	}
	
	public static class BlockItemPair {
		public Block block;
		public ItemBlock itemBlock;
	} 
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		for(BlockItemPair pair : BLOCK_LIST){
			registry.register(pair.block);
		}
		for(BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block> group : VARIANT_BLOCK_LIST){
			group.registerBlocks(registry);
		}
	}
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		for(BlockItemPair pair : BLOCK_LIST){
			registry.register(pair.itemBlock);
		}
		for(BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block> group : VARIANT_BLOCK_LIST){
			group.registerItems(registry);
		}
		for(Item item : ITEM_LIST){
			registry.register(item);
		}
		for(ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item> group : VARIANT_ITEM_LIST){
			group.registerItems(registry);
		}
	}
	
	@SubscribeEvent
	public static void registerTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> event) {
		final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
		for(TileEntityType<?> tile : TILE_LIST){
			registry.register(tile);
		}
	}
	
	public static void createBlock(Block block, ItemGroup group, String name){
		createBlock(block, new ItemBlock(block, defaultItemProperties(group)), name);
	}
	
	public static void createBlock(Block block, ItemBlock itemBlock, String name){
		BlockItemPair pair = new BlockItemPair();
		String regName = CrystalMod.resource(name);
		pair.block = block.setRegistryName(regName);
		pair.itemBlock = (ItemBlock) itemBlock.setRegistryName(regName);
		BLOCK_LIST.add(pair);
	}
	
	public static void addBlockGroup(BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block> group){
		VARIANT_BLOCK_LIST.add(group);
	}
	
	public static void addItem(Item item, String name){
		String regName = CrystalMod.resource(name);
		ITEM_LIST.add(item.setRegistryName(regName));
	}
	
	public static void addItemGroup(ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item> group){
		VARIANT_ITEM_LIST.add(group);
	}
	
	public static void addTile(TileEntityType<?> tile){
		TILE_LIST.add(tile);
	}
	
	public static Item.Properties defaultItemProperties(ItemGroup group) {
		return new Item.Properties().group(group);
	}
}
