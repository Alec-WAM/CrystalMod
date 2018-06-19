package alec_wam.CrystalMod.blocks.crops.bamboo;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ItemMiscFood.FoodType;
import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBamboo extends Block {

	public BlockBamboo() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
        this.setHardness(1.5F);
		this.setCreativeTab(CrystalMod.tabCrops);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
        Random rand = world instanceof World ? ((World)world).rand : RANDOM;
        int chance = 20;
        if (fortune > 0)
        {
            chance -= 5 * fortune;
            if (chance < 5) chance = 5;
        }
        if(rand.nextInt(chance) == 0){
        	ret.add(new ItemStack(ModItems.miscFood, 1, FoodType.EUCALYPTUS.getMeta()));
        }
        return ret;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.25D, 0.0, 0.25D, 0.75D, 1.0D, 0.75D);
	
	@Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
		return BOUNDING_BOX;
    }

}
