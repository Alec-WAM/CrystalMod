package alec_wam.CrystalMod.tiles.lamps;

import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.tiles.explosives.remover.BlockRemoverExplosion.RemoverType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFakeLight extends EnumBlock<BlockFakeLight.LightType> {

	public static final PropertyEnum<LightType> TYPE = PropertyEnum.<LightType>create("type", LightType.class);
	
	public BlockFakeLight() {
		super(Material.AIR, TYPE, LightType.class);
		setLightLevel(1.0F);
	}

	@Override
    public int getLightValue(IBlockState state) {
        return state.getValue(TYPE) == LightType.DARK ? 0 : 15;
    }
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		return state.getValue(TYPE) == LightType.DARK ? 15 : 0;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    	return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public static enum LightType implements IStringSerializable, alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta {
		LIGHT, DARK;

		final int meta;
		
		LightType(){
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
