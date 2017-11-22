package alec_wam.CrystalMod.fluids;

import alec_wam.CrystalMod.blocks.BlockCrystalFluid;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidEnder extends BlockCrystalFluid {

	public BlockFluidEnder(Fluid fluid, Material material) {
		super(fluid, material);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
		if(!worldIn.isRemote){
			EntityUtil.randomTeleport(entityIn, 16);
		}
    }

}
