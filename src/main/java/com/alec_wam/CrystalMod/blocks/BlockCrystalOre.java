package com.alec_wam.CrystalMod.blocks;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import com.alec_wam.CrystalMod.items.ModItems;

public class BlockCrystalOre extends EnumBlock<BlockCrystalOre.CrystalOreType> {

	public static final PropertyEnum<CrystalOreType> TYPE = PropertyEnum.<CrystalOreType>create("type", CrystalOreType.class);
	
	public BlockCrystalOre(){
		super(Material.ROCK, TYPE, CrystalOreType.class);
		setHardness(3.0F).setResistance(5.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
    public boolean canSilkHarvest()
    {
        return true;
    }
	
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        Random rand = world instanceof World ? ((World)world).rand : RANDOM;
        int count = 0;
        int fortOffset = 0;
        if (fortune > 0)
        {
        	fortOffset = rand.nextInt(fortune + 2) - 1;

            if (fortOffset < 0)
            {
            	fortOffset = 0;
            }

            count = 1 * (fortOffset + 1);
        }
        else
        {
            count = 1;
        }
        
        for(int i = 0; i < count; i++){
	        Item item = ModItems.crystals;
	        if (item != null)
	        {
	        	int meta = this.getMetaFromState(state);
	            ret.add(new ItemStack(item, 1, meta == 0 ? CrystalType.BLUE.getMetadata() : meta == 1 ? CrystalType.RED.getMetadata() : meta == 2 ? CrystalType.GREEN.getMetadata() : CrystalType.DARK.getMetadata()));
	        }
        }
        
        if(rand.nextInt(5-fortOffset) == 0){
        	Item item = ModItems.crystals;
	        if (item != null)
	        {
	        	int meta = this.getMetaFromState(state);
	            ret.add(new ItemStack(item, 1, meta == 0 ? CrystalType.BLUE_SHARD.getMetadata() : meta == 1 ? CrystalType.RED_SHARD.getMetadata() : meta == 2 ? CrystalType.GREEN_SHARD.getMetadata() : CrystalType.DARK_SHARD.getMetadata()));
	        }
        }
        
        return ret;
    }
	
	@Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this))
        {
            return MathHelper.getRandomIntegerInRange(rand, 3, 7);
        }
        return 0;
    }
	
	public static enum CrystalOreType implements IStringSerializable, com.alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta{
		BLUE, RED, GREEN, DARK;

		final int meta;
		
		CrystalOreType(){
			meta = ordinal();
		}
		
		@Override
		public int getMeta() {
			return meta;
		}

		@Override
		public String getName() {
			return this.toString().toLowerCase(Locale.US);
		}
		
	}
	
}
