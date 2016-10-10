package com.alec_wam.CrystalMod.tiles.matter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alec_wam.CrystalMod.tiles.matter.imps.Matter;
import com.alec_wam.CrystalMod.tiles.matter.imps.MatterPlanks;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;

public class MatterRegistry {
	
	public static Matter stone;
	public static Matter dirt;
	public static Matter cobble;
	public static Matter planks;
	
	public static final Map<Matter, Map<ItemStack, MatterStack>> values = new HashMap<Matter, Map<ItemStack, MatterStack>>();
	public static final List<Matter> matterList = Lists.newArrayList();
	
	public static void init(){
		stone = new Matter("stone");
		matterList.add(stone);
		dirt = new Matter("dirt");
		matterList.add(dirt);
		cobble = new Matter("cobblestone");
		matterList.add(cobble);
		planks = new MatterPlanks("planks");
		matterList.add(planks);
	}
	
	public static Matter getMatterFromName(String name){
		for(Matter matter : matterList){
			if(matter.getUnlocalizedName().equals(name)){
				return matter;
			}
		}
		return null;
	}
	
	public static void initValues(){
		/*Map<ItemStack, MatterStack> stoneValues = new HashMap<ItemStack, MatterStack>();
		for(ItemStack blockStone : OreDictionary.getOres("stone")){
			if(blockStone !=null){
				stoneValues.put(blockStone.copy(), new MatterStack(stone, BlockStone.EnumType.STONE.getMetadata(), MatterHelper.VALUE_Block));
			}
		}
		stoneValues.put(new ItemStack(Blocks.STONE_BUTTON), new MatterStack(stone, BlockStone.EnumType.STONE.getMetadata(), MatterHelper.VALUE_Block));
		stoneValues.put(new ItemStack(Blocks.STONE_PRESSURE_PLATE), new MatterStack(stone, BlockStone.EnumType.STONE.getMetadata(), MatterHelper.VALUE_Block*2));
		stoneValues.put(new ItemStack(Blocks.STONE_SLAB, 1, BlockStoneSlab.EnumType.STONE.getMetadata()), new MatterStack(stone, BlockStone.EnumType.STONE.getMetadata(), MatterHelper.VALUE_Block/2));
		values.put(stone, stoneValues);
		
		Map<ItemStack, MatterStack> dirtValues = new HashMap<ItemStack, MatterStack>();
		stoneValues.put(new ItemStack(Blocks.DIRT, 1, DirtType.DIRT.getMetadata()), new MatterStack(dirt, DirtType.DIRT.getMetadata(), MatterHelper.VALUE_Block));
		values.put(dirt, dirtValues);
		
		Map<ItemStack, MatterStack> cobbleValues = new HashMap<ItemStack, MatterStack>();
		for(ItemStack blockStone : OreDictionary.getOres("cobblestone")){
			if(blockStone !=null){
				cobbleValues.put(blockStone.copy(), new MatterStack(cobble, MatterHelper.VALUE_Block));
			}
		}
		cobbleValues.put(new ItemStack(Blocks.STONE_SLAB, 1, BlockStoneSlab.EnumType.COBBLESTONE.getMetadata()), new MatterStack(cobble, MatterHelper.VALUE_Block/2));
		cobbleValues.put(new ItemStack(Blocks.FURNACE), new MatterStack(cobble, MatterHelper.VALUE_Block*8));
		cobbleValues.put(new ItemStack(Blocks.STONE_STAIRS), new MatterStack(cobble, (MatterHelper.VALUE_Block*6)/4));
		cobbleValues.put(new ItemStack(Blocks.COBBLESTONE_WALL, 1, BlockWall.EnumType.NORMAL.getMetadata()), new MatterStack(cobble, MatterHelper.VALUE_Block));
		values.put(cobble, cobbleValues);
		
		Map<ItemStack, MatterStack> plankValues = new HashMap<ItemStack, MatterStack>();
		for(BlockPlanks.EnumType type : BlockPlanks.EnumType.values()){
			plankValues.put(new ItemStack(Blocks.PLANKS, 1, type.getMetadata()), new MatterStack(planks, type.getMetadata(), MatterHelper.VALUE_Block));
			plankValues.put(new ItemStack(Blocks.WOODEN_SLAB, 1, type.getMetadata()), new MatterStack(planks, type.getMetadata(), MatterHelper.VALUE_Block/2));
		}
		plankValues.put(new ItemStack(Blocks.OAK_DOOR), new MatterStack(planks, BlockPlanks.EnumType.OAK.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.SPRUCE_DOOR), new MatterStack(planks, BlockPlanks.EnumType.SPRUCE.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.BIRCH_DOOR), new MatterStack(planks, BlockPlanks.EnumType.BIRCH.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.JUNGLE_DOOR), new MatterStack(planks, BlockPlanks.EnumType.JUNGLE.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.ACACIA_DOOR), new MatterStack(planks, BlockPlanks.EnumType.ACACIA.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.DARK_OAK_DOOR), new MatterStack(planks, BlockPlanks.EnumType.DARK_OAK.getMetadata(), MatterHelper.VALUE_Block*2));
		plankValues.put(new ItemStack(Blocks.TRAPDOOR), new MatterStack(planks, MatterHelper.VALUE_Block*3));
		plankValues.put(new ItemStack(Blocks.oak_stairs), new MatterStack(planks, BlockPlanks.EnumType.OAK.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.spruce_stairs), new MatterStack(planks, BlockPlanks.EnumType.SPRUCE.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.birch_stairs), new MatterStack(planks, BlockPlanks.EnumType.BIRCH.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.jungle_stairs), new MatterStack(planks, BlockPlanks.EnumType.JUNGLE.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.acacia_stairs), new MatterStack(planks, BlockPlanks.EnumType.ACACIA.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.dark_oak_stairs), new MatterStack(planks, BlockPlanks.EnumType.DARK_OAK.getMetadata(), (MatterHelper.VALUE_Block*6)/4));
		plankValues.put(new ItemStack(Blocks.wooden_button), new MatterStack(planks, MatterHelper.VALUE_Block));
		plankValues.put(new ItemStack(Blocks.wooden_pressure_plate), new MatterStack(planks, MatterHelper.VALUE_Block*2));
		
		values.put(planks, plankValues);*/
	}
	
	public static Matter getMatter(ItemStack stack){
		for(Entry<Matter, Map<ItemStack, MatterStack>> entry : values.entrySet()){
			for(ItemStack s : entry.getValue().keySet()){
				if(ItemUtil.canCombine(s, stack)){
					return entry.getKey();
				}
			}
		}
		return null;
	}
	
	public static MatterStack getMatterStack(Matter matter, ItemStack stack){
		Map<ItemStack, MatterStack> valueMap = values.get(matter);
		for(ItemStack entry : valueMap.keySet()){
			if(ItemUtil.canCombine(entry, stack)){
				return valueMap.get(entry).copy();
			}
		}
		return null;
	}
	
}
