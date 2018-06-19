package alec_wam.CrystalMod.blocks;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
	
	@Override
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
        
        CrystalOreType type = state.getValue(TYPE);
        boolean blueOre = (type == CrystalOreType.BLUE || type == CrystalOreType.BLUE_NETHER || type == CrystalOreType.BLUE_END);
        boolean redOre = (type == CrystalOreType.RED || type == CrystalOreType.RED_NETHER || type == CrystalOreType.RED_END);
        boolean greenOre = (type == CrystalOreType.GREEN || type == CrystalOreType.GREEN_NETHER || type == CrystalOreType.GREEN_END);
        for(int i = 0; i < count; i++){
	        Item item = ModItems.crystals;
	        if (item != null)
	        {
	        	ret.add(new ItemStack(item, 1, blueOre ? CrystalType.BLUE.getMeta() : redOre ? CrystalType.RED.getMeta() : greenOre ? CrystalType.GREEN.getMeta() : CrystalType.DARK.getMeta()));
	        }
        }
        
        if(rand.nextInt(5-fortOffset) == 0){
        	Item item = ModItems.crystals;
	        if (item != null)
	        {
	        	ret.add(new ItemStack(item, 1, blueOre ? CrystalType.BLUE_SHARD.getMeta() : redOre ? CrystalType.RED_SHARD.getMeta() : greenOre ? CrystalType.GREEN_SHARD.getMeta() : CrystalType.DARK_SHARD.getMeta()));
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
            return MathHelper.getInt(rand, 3, 7);
        }
        return 0;
    }
	
	public static enum CrystalOreType implements IStringSerializable, alec_wam.CrystalMod.util.IEnumMeta{
		BLUE, RED, GREEN, DARK, BLUE_NETHER, RED_NETHER, GREEN_NETHER, DARK_NETHER, BLUE_END, RED_END, GREEN_END, DARK_END;

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
