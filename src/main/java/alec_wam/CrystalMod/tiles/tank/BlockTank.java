package alec_wam.CrystalMod.tiles.tank;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.crate.ContainerBlockVariant;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BlockTank extends ContainerBlockVariant<EnumCrystalColorSpecialWithCreative> {
	//TODO add Void Upgrade support
	public BlockTank(EnumCrystalColorSpecialWithCreative type, BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockTank> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		//TODO Add Fluid Content Info
		if(type != EnumCrystalColorSpecialWithCreative.CREATIVE){
			int largeNumber = (Fluid.BUCKET_VOLUME*TileEntityTank.TIER_BUCKETS[type.ordinal()]);
			tooltip.add(new TranslationTextComponent("crystalmod.info.tank.storage", NumberFormat.getNumberInstance(Locale.US).format(largeNumber)));
		}
    }
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank)tile;
        	if(tank.tank.getFluid() !=null){
        		return tank.tank.getFluid().getFluid().getLuminosity();
        	}
        }

        return super.getLightValue(state, world, pos);
    }
	
	@Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTank) {
        	TileEntityTank tank = (TileEntityTank) tile;
            return (tank.tank.getFluidAmount() * 15 / tank.tank.getCapacity());
        }

        return 0;
    }
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityTank)) return false;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
        		return ToolUtil.breakBlockWithWrench(world, pos, player, hand);
        	}            

			LazyOptional<IFluidHandlerItem> containerFluidHandler = FluidConversion.getHandlerFromItem(held);//FluidUtil.getFluidHandler(held);
        	if (containerFluidHandler !=null)
        	{
        		if(FluidConversion.interactWithFluidHandler(player, hand, world, pos, ray.getFace())){
        			return true;
        		}
        	}
		}
        return false;
    }

}
