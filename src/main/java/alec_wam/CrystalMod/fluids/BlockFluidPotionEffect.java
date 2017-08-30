package alec_wam.CrystalMod.fluids;

import alec_wam.CrystalMod.blocks.BlockCrystalFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidPotionEffect extends BlockCrystalFluid {
	public final PotionEffect effect;
	public BlockFluidPotionEffect(Fluid fluid, Material material, PotionEffect effect) {
		super(fluid, material);
		this.effect = effect;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
		if(!worldIn.isRemote){
			if(entityIn instanceof EntityLivingBase){
				EntityLivingBase living = (EntityLivingBase)entityIn;
				living.addPotionEffect(effect);
			}
		}
    }

}
