package alec_wam.CrystalMod.blocks;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WrapperState implements IBlockState {

	public IBlockState state;
	
	public WrapperState(IBlockState state){
		this.state = state;
	}
	
	@Override
	public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
		return state.onBlockEventReceived(worldIn, pos, id, param);
	}

	@Override
	public void neighborChanged(World worldIn, BlockPos pos, Block p_189546_3_) {
		state.neighborChanged(worldIn, pos, p_189546_3_);
	}

	@Override
	public Material getMaterial() {
		return state.getMaterial();
	}

	@Override
	public boolean isFullBlock() {
		return state.isFullBlock();
	}

	@Override
	public boolean func_189884_a(Entity p_189884_1_) {
		return state.func_189884_a(p_189884_1_);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightOpacity() {
		return state.getLightOpacity();
	}

	@Override
	public int getLightOpacity(IBlockAccess world, BlockPos pos) {
		return state.getLightOpacity(world, pos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue() {
		return state.getLightValue();
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		return state.getLightValue(world, pos);
	}

	@Override
	public boolean isTranslucent() {
		return state.isTranslucent();
	}

	@Override
	public boolean useNeighborBrightness() {
		return state.useNeighborBrightness();
	}

	@Override
	public MapColor getMapColor() {
		return state.getMapColor();
	}

	@Override
	public IBlockState withRotation(Rotation rot) {
		return state.withRotation(rot);
	}

	@Override
	public IBlockState withMirror(Mirror mirrorIn) {
		return state.withMirror(mirrorIn);
	}

	@Override
	public boolean isFullCube() {
		return state.isFullCube();
	}

	@Override
	public EnumBlockRenderType getRenderType() {
		return state.getRenderType();
	}

	@Override
	public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos) {
		return state.getPackedLightmapCoords(source, pos);
	}

	@Override
	public float getAmbientOcclusionLightValue() {
		return state.getAmbientOcclusionLightValue();
	}

	@Override
	public boolean isBlockNormalCube() {
		return state.isBlockNormalCube();
	}

	@Override
	public boolean isNormalCube() {
		return state.isNormalCube();
	}

	@Override
	public boolean canProvidePower() {
		return state.canProvidePower();
	}

	@Override
	public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return state.getWeakPower(blockAccess, pos, side);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return state.hasComparatorInputOverride();
	}

	@Override
	public int getComparatorInputOverride(World worldIn, BlockPos pos) {
		return state.getComparatorInputOverride(worldIn, pos);
	}

	@Override
	public float getBlockHardness(World worldIn, BlockPos pos) {
		return state.getBlockHardness(worldIn, pos);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos) {
		return state.getPlayerRelativeBlockHardness(player, worldIn, pos);
	}

	@Override
	public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return state.getStrongPower(blockAccess, pos, side);
	}

	@Override
	public EnumPushReaction getMobilityFlag() {
		return state.getMobilityFlag();
	}

	@Override
	public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos) {
		return state.getActualState(blockAccess, pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
		return state.getSelectedBoundingBox(worldIn, pos);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing) {
		return state.shouldSideBeRendered(blockAccess, pos, facing);
	}

	@Override
	public boolean isOpaqueCube() {
		return state.isOpaqueCube();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos) {
		return state.getCollisionBoundingBox(worldIn, pos);
	}

	@Override
	public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_,	Entity p_185908_5_) {
		state.addCollisionBoxToList(worldIn, pos, p_185908_3_, p_185908_4_, p_185908_5_);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos) {
		return state.getBoundingBox(blockAccess, pos);
	}

	@Override
	public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		return state.collisionRayTrace(worldIn, pos, start, end);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullyOpaque() {
		return state.isFullyOpaque();
	}

	@Override
	public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos,	EnumFacing side) {
		return state.doesSideBlockRendering(world, pos, side);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.isSideSolid(world, pos, side);
	}

	@Override
	public Collection<IProperty<?>> getPropertyNames() {
		return state.getPropertyNames();
	}

	@Override
	public <T extends Comparable<T>> T getValue(IProperty<T> property) {
		return state.getValue(property);
	}

	@Override
	public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
		return state.withProperty(property, value);
	}

	@Override
	public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
		return state.cycleProperty(property);
	}

	@Override
	public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
		return state.getProperties();
	}

	@Override
	public Block getBlock() {
		return state.getBlock();
	}

}
