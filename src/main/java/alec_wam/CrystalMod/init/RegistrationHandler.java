package alec_wam.CrystalMod.init;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.compatibility.materials.ItemMaterial;
import alec_wam.CrystalMod.compatibility.materials.MaterialLoader;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.core.ItemVariantGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistrationHandler {

	public static List<BlockItemPair> BLOCK_LIST;
	public static List<Item> ITEM_LIST;
	public static List<BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block>> VARIANT_BLOCK_LIST;
	public static List<ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item>> VARIANT_ITEM_LIST;
	static{
		BLOCK_LIST = new ArrayList<BlockItemPair>();
		ITEM_LIST = new ArrayList<Item>();
		VARIANT_BLOCK_LIST = new ArrayList<BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block>>();
		VARIANT_ITEM_LIST = new ArrayList<ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item>>();
		ModBlocks.buildList();
		ModItems.buildList();
	}
	
	public static class BlockItemPair {
		public Block block;
		public BlockItem itemBlock;
	} 
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		CrystalMod.LOGGER.info("Adding Blocks");
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
		CrystalMod.LOGGER.info("Adding Block Items");
		final IForgeRegistry<Item> registry = event.getRegistry();
		for(BlockItemPair pair : BLOCK_LIST){
			registry.register(pair.itemBlock);
		}
		for(BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block> group : VARIANT_BLOCK_LIST){
			group.registerItems(registry);
		}
		
		CrystalMod.LOGGER.info("Adding Items");
		for(Item item : ITEM_LIST){
			registry.register(item);
		}
		for(ItemVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Item> group : VARIANT_ITEM_LIST){
			group.registerItems(registry);
		}
		
		CrystalMod.LOGGER.info("Adding Material Items");
		MaterialLoader.registerMaterialItems(registry);
	}
	
	@SubscribeEvent
	public static void registerTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> event) {
		CrystalMod.LOGGER.info("Adding Tiles");
		final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
		for(BlockVariantGroup<? extends Enum<? extends IStringSerializable>, ? extends Block> group : VARIANT_BLOCK_LIST){
			group.registerTiles(registry);
		}
	}
	
	@SubscribeEvent
	public static void registerRecipeSerlizers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		final IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
		registry.register(ModRecipes.GRINDER_SERIALIZER);
		registry.register(ModRecipes.PRESS_SERIALIZER);
	}
	
	@SubscribeEvent
	public static void fixDusts(RegistryEvent.MissingMappings<ItemMaterial> event) {
		for(Mapping<ItemMaterial> obj : event.getMappings()){
			obj.ignore();
		}
	}
	
	public static void createBlock(Block block, ItemGroup group, String name){
		createBlock(block, new BlockItem(block, defaultItemProperties(group)), name);
	}
	
	public static void createBlock(Block block, BlockItem itemBlock, String name){
		BlockItemPair pair = new BlockItemPair();
		String regName = CrystalMod.resource(name);
		pair.block = block.setRegistryName(regName);
		pair.itemBlock = (BlockItem) itemBlock.setRegistryName(regName);
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
	
	public static Item.Properties defaultItemProperties(ItemGroup group) {
		return new Item.Properties().group(group);
	}
}
