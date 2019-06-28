package alec_wam.CrystalMod.blocks.plants;

import java.util.Random;

import alec_wam.CrystalMod.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

public class BlockCattail extends Block implements net.minecraftforge.common.IPlantable, IGrowable, IWaterLoggable {
   public static enum PlantLayer implements IStringSerializable {
	   ROOTS, MIDDLE, TOP;
	   
	   @Override
	   public String getName(){
		   return name().toLowerCase();
	   }
   }
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
   public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 12);
   public static final EnumProperty<PlantLayer> LAYER = EnumProperty.create("layer", PlantLayer.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public BlockCattail(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(STAGE, Integer.valueOf(0)).with(AGE, Integer.valueOf(0)).with(LAYER, PlantLayer.ROOTS).with(WATERLOGGED, Boolean.FALSE));
   }

   @Override
   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   @Override
   public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else  {
    	  if(state.get(LAYER) == PlantLayer.ROOTS){
    		  //Handle growth from roots
    		  grow(state, worldIn, pos, random);
    	  }    	  
      }
   }
   
   public void grow(BlockState state, World worldIn, BlockPos pos, Random random){
	   int age = state.get(AGE);
	   if(age < 3){
		   //Tick growth
		   worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(age + 1)), 4);
	   } else {
		   //Handle stage increase and reset age
		   int stage = state.get(STAGE);
		   if(stage < 12){
			   BlockPos middlePos = pos.up();
			   BlockPos topPos = middlePos.up();
			   if(stage == 3){
				   if(!worldIn.isAirBlock(middlePos)){
					   return;
				   }
			   }
			   if(stage == 6){
				   if(!worldIn.isAirBlock(topPos)){
					   return;
				   }
			   }
			   int newStage = stage + 1;
			   BlockState newState = state.with(AGE, Integer.valueOf(0)).with(STAGE, Integer.valueOf(newStage));
			   worldIn.setBlockState(pos, newState, 4);
			   if(newStage == 4){
				   //Add middle layer
				   BlockState middleLayer = getDefaultState().with(LAYER, PlantLayer.MIDDLE).with(AGE, Integer.valueOf(0));
				   worldIn.setBlockState(middlePos, middleLayer, 4);
			   }
			   if(newStage == 7){
				   //Add top layer
				   BlockState topLayer = getDefaultState().with(LAYER, PlantLayer.TOP).with(AGE, Integer.valueOf(0));
				   worldIn.setBlockState(topPos, topLayer, 4);
			   }
			   
			   if(newStage > 3){
				   BlockState middleState = worldIn.getBlockState(middlePos);
				   worldIn.setBlockState(middlePos, middleState.with(STAGE, Integer.valueOf(newStage)));
			   }
			   if(newStage > 6){
				   BlockState topState = worldIn.getBlockState(topPos);
				   worldIn.setBlockState(topPos, topState.with(STAGE, Integer.valueOf(newStage)));
			   }
		   }
	   }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   @SuppressWarnings("deprecation")
   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }
      if (stateIn.get(WATERLOGGED)) {
    	  worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }
      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   @Override
   public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
	   PlantLayer layer = state.get(LAYER);
	   int stage = state.get(STAGE);
	   if(layer == PlantLayer.TOP){
		   if(stage == 12){
			   BlockState middle = world.getBlockState(pos.down());
			   BlockState bottom = world.getBlockState(pos.down().down());
			   world.setBlockState(pos, state.with(STAGE, 8));
			   world.setBlockState(pos.down(), middle.with(STAGE, 8));
			   world.setBlockState(pos.down().down(), bottom.with(STAGE, 8));
			   
			   int count = 2 + RANDOM.nextInt(2);
			   ItemStack fluff = new ItemStack(ModItems.cattailFluff, count);
			   ItemEntity entityitem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fluff);
			   Vec3d motion = new Vec3d(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
			   entityitem.setMotion(motion);
			   if(!world.isRemote)world.func_217376_c(entityitem);			   
			   return true;
		   }
	   }
	   if(layer == PlantLayer.ROOTS){
		   ItemStack held = player.getHeldItem(hand);
		   if(stage > 8 && held.getItem() instanceof ShearsItem){
			   BlockState middle = world.getBlockState(pos.up());
			   BlockState top = world.getBlockState(pos.up().up());
			   world.setBlockState(pos, state.with(STAGE, 8));
			   world.setBlockState(pos.up(), middle.with(STAGE, 8));
			   world.setBlockState(pos.up().up(), top.with(STAGE, 8));
			   
			   ItemStack roots = new ItemStack(ModItems.cattailRootsRaw);
			   ItemEntity entityitem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, roots);
			   Vec3d motion = new Vec3d(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
			   entityitem.setMotion(motion);
			   if(!world.isRemote)world.func_217376_c(entityitem);	
			   held.func_222118_a(1, player, e -> e.func_213334_d(hand));
			   return true;
		   }
	   }
	   return false;
   }
   
   @Override
   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
	  PlantLayer layer = state.get(LAYER);
      BlockState below = worldIn.getBlockState(pos.down());
      BlockState above = worldIn.getBlockState(pos.up());
      if (layer == PlantLayer.ROOTS) {
    	  IFluidState fluid = worldIn.getFluidState(pos);
    	  if(!Block.isDirt(below.getBlock()) || !fluid.isTagged(FluidTags.WATER))return false;
    	  if(state.get(STAGE) > 3){
    		  if(above.getBlock() != this || above.get(LAYER) != PlantLayer.MIDDLE)return false;
    	  }
    	  return true;
      }
      if(layer == PlantLayer.MIDDLE){
    	  if(below.getBlock() != this || below.get(LAYER) != PlantLayer.ROOTS)return false;
    	  if(state.get(STAGE) > 6){
    		  if(above.getBlock() != this || above.get(LAYER) != PlantLayer.TOP)return false;
    	  }
    	  return true;
      }
      if(layer == PlantLayer.TOP){
    	  return below.getBlock() == this && below.get(LAYER) == PlantLayer.MIDDLE;
      }
   	  return false;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @Override
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Override
   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE, LAYER, STAGE, WATERLOGGED);
   }

   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
	   IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
	   return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   @Override
   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
	   if (state.get(WATERLOGGED)) {
		   worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
		   return Fluids.WATER;
	   } else {
		   return Fluids.EMPTY;
	   }
   }

   @SuppressWarnings("deprecation")
   @Override
   public IFluidState getFluidState(BlockState state) {
	   return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   @Override
   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
	   return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   @Override
   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
	   if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
		   if (!worldIn.isRemote()) {
			   worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
			   worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		   }

		   return true;
	   } else {
		   return false;
	   }
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
       return PlantType.Plains;
   }

   @Override
   public BlockState getPlant(IBlockReader world, BlockPos pos) {
      return getDefaultState();
   }

   @Override
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
	   return state.get(LAYER) == PlantLayer.ROOTS;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
	   return state.get(LAYER) == PlantLayer.ROOTS;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
	   if(state.get(LAYER) == PlantLayer.ROOTS){
 		  //Handle growth from roots
 		  grow(state, worldIn, pos, rand);
 	  }  
   }
}