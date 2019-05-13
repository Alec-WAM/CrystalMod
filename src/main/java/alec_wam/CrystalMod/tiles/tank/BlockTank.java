package alec_wam.CrystalMod.tiles.tank;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BlockTank extends BlockContainerVariant<EnumCrystalColorSpecialWithCreative> {
	//TODO Handle rendering when fluids are back in forge
	public BlockTank(EnumCrystalColorSpecialWithCreative type, BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockTank> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		if(type != EnumCrystalColorSpecialWithCreative.CREATIVE){
			int buckets = TileEntityTank.TIER_BUCKETS[type.ordinal()];
			int largeNumber = (Fluid.BUCKET_VOLUME*TileEntityTank.TIER_BUCKETS[type.ordinal()]);
			tooltip.add(new TextComponentTranslation("crystalmod.info.tank.storage", NumberFormat.getNumberInstance(Locale.US).format(largeNumber)));
		}
    }
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
    public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank)tile;
        	/*if(tank.tank.getFluid().){
        		return 15;
        	}*/
        }

        return super.getLightValue(state, world, pos);
    }
	
	@Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank) tile;
            return (tank.tank.getFluidAmount() * 15 / tank.tank.getCapacity());
        }

        return 0;
    }
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityTank)) return false;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(ToolUtil.isHoldingWrench(player, hand)){
        		return ToolUtil.breakBlockWithWrench(world, pos, player, hand);
        	}            

			LazyOptional<IFluidHandlerItem> containerFluidHandler = FluidUtil.getFluidHandler(held);
        	if (containerFluidHandler.isPresent())
        	{
        		if(FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)){
        			return true;
        		}
        	}
		}
        return false;
    }

}
