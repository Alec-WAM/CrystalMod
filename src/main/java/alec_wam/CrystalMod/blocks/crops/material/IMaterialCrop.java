package alec_wam.CrystalMod.blocks.crops.material;

import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Vector4d;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IMaterialCrop {
	
	public String getUnlocalizedName();
	
	public boolean customLocalized();
	public String getLocalizedName();
	
	//Measured by seconds not ticks
	public int getGrowthTime(@Nullable IBlockAccess world, @Nullable BlockPos pos);
	
	public List<ItemStack> getDrops(@Nullable IBlockAccess world, @Nullable BlockPos pos, int yield, int fortune);

	public int getMinYield(@Nullable IBlockAccess world, @Nullable BlockPos pos);

	public int getMaxYield(@Nullable IBlockAccess world, @Nullable BlockPos pos);

	public ItemStack getRenderStack(@Nullable IBlockAccess world, @Nullable BlockPos pos);
	
	public ISeedInfo getSeedInfo();
	
	public int getExtraSeedDropChance(@Nullable IBlockAccess world, @Nullable BlockPos pos);
	
	public IPlantType getPlantType();
	
	public int getPlantColor(@Nullable IBlockAccess world, @Nullable BlockPos pos, int pass);
	
}
