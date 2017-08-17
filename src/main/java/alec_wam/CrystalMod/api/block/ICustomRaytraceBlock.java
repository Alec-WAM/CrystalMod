package alec_wam.CrystalMod.api.block;

import java.util.Collection;

import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ICustomRaytraceBlock {

	public Collection<? extends CollidableComponent> getCollidableComponents(World world, BlockPos pos);

	public RayTraceResult defaultRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d origin,	Vec3d direction);

	public void setBounds(AxisAlignedBB bound);

	public void resetBounds();

}
