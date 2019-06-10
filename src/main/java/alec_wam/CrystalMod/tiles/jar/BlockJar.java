package alec_wam.CrystalMod.tiles.jar;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.ContainerBlockVariant;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockJar extends ContainerBlockVariant<WoodType> implements IBucketPickupHandler, ILiquidContainer  {
	protected static final VoxelShape SHAPE_CORK = Block.makeCuboidShape(5.0D, 13.0D, 5.0D, 11.0D, 15.0D, 11.0D);
	protected static final VoxelShape SHAPE_MAIN = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D);
	public static final VoxelShape SHAPE_ALL = VoxelShapes.or(SHAPE_MAIN, SHAPE_CORK);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final int MAX_POTIONS_STORED = 3;

	public BlockJar(WoodType type, BlockVariantGroup<WoodType, BlockJar> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		if(ItemNBTHelper.verifyExistance(stack, "TILE_DATA")){
			CompoundNBT tileNBT = ItemNBTHelper.getCompound(stack).getCompound("TILE_DATA");
			if(tileNBT.getBoolean("IsShulker")){
				tooltip.add(new TranslationTextComponent("crystalmod.info.jar.shulker"));
			}
			if(tileNBT.contains("Potion")){
				Potion type = PotionUtils.getPotionTypeFromNBT(tileNBT);
				if(type !=Potions.field_185229_a){
					for (EffectInstance potioneffect : type.getEffects())
		            {
		                String s1 = Lang.translateToLocal(potioneffect.getEffectName()).trim();
		                Effect potion = potioneffect.getPotion();

		                if (potioneffect.getAmplifier() > 0)
		                {
		                    s1 = s1 + " " + Lang.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
		                }

		                if (potioneffect.getDuration() > 20)
		                {
		                    s1 = s1 + " (" + EffectUtils.getPotionDurationString(potioneffect, 1.0F) + ")";
		                }

		                if (!potion.isBeneficial())
		                {
		                    tooltip.add(new StringTextComponent(TextFormatting.RED + s1));
		                }
		                else
		                {
		                	tooltip.add(new StringTextComponent(TextFormatting.BLUE + s1));
		                }
		            }
					tooltip.add(new TranslationTextComponent("crystalmod.info.jar.contains", new Object[]{""+tileNBT.getInt("Count"), "3"}));
				}
			}
		}
    }
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_ALL;
	}
	
	@Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityJar) {
        	TileEntityJar jar = (TileEntityJar)tile;
        	if(jar.isShulkerLamp()){
        		return 15;
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

        if (tile instanceof TileEntityJar) {
        	TileEntityJar jar = (TileEntityJar) tile;
            return jar.getPotionCount();
        }

        return 0;
    }
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		Direction facing = ray.getFace();
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityJar)) return false;
		TileEntityJar jar = (TileEntityJar)tile;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(held.getItem() == Items.SHULKER_SHELL && !jar.isShulkerLamp() && jar.getPotionCount() == 0){
				if(!jar.getLabelMap().isEmpty()){
					for(Direction face : Direction.Plane.HORIZONTAL){
						if(jar.hasLabel(face)){
							if(!world.isRemote)ItemUtil.dropItemOnSide(world, pos, new ItemStack(Items.ITEM_FRAME), face);
							jar.setHasLabel(face, false);
						}
					}
				}
				jar.setShulkerLamp(true);
				if(!player.playerAbilities.isCreativeMode){
					player.setHeldItem(hand, ItemUtil.consumeItem(held));
				}
				//TODO Do Light Update
				//world.checkLightFor(LightType.BLOCK, pos);
				BlockUtil.markBlockForUpdate(world, pos);
				return true;
			}
			else if(held.getItem() == Items.ITEM_FRAME && facing.getAxis().isHorizontal() && !jar.isShulkerLamp()){
				if(!jar.hasLabel(facing)){
					jar.setHasLabel(facing, true);
					world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					if(!player.playerAbilities.isCreativeMode){
						player.setHeldItem(hand, ItemUtil.consumeItem(held));
					}
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			else if(held.getItem() == Items.POTION){
				Potion type = PotionUtils.getPotionFromItem(held);
				if(type.getEffects().size() > 0 && (jar.getPotion() == type || jar.getPotion() == Potions.field_185229_a)){
					if(jar.getPotionCount() < MAX_POTIONS_STORED){
						if(jar.getPotion() == Potions.field_185229_a){
							jar.setPotionType(type);
						}
						jar.setPotionCount(jar.getPotionCount()+1);
						if(!player.playerAbilities.isCreativeMode){
							ItemUtil.setPlayerHandSilently(player, hand, new ItemStack(Items.GLASS_BOTTLE));
						}
						world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
						BlockUtil.markBlockForUpdate(world, pos);
						return true;
					}
				}
			} else if(held.getItem() == Items.GLASS_BOTTLE){
				if(jar.getPotion() !=Potions.field_185229_a && jar.getPotionCount() > 0){
					ItemUtil.setPlayerHandSilently(player, hand, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), jar.getPotion()));
					jar.setPotionCount(jar.getPotionCount()-1);
					if(jar.getPotionCount() <= 0){
						jar.setPotionType(Potions.field_185229_a);
					}
					world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			
		}
        return false;
    }
	
	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityJar)) return;
		TileEntityJar jar = (TileEntityJar)tile;
		ItemStack held = player.getHeldItemMainhand();
		if(ItemStackTools.isValid(held) && held.getItem() == Items.ITEM_FRAME){
			BlockRayTraceResult ray = EntityUtil.getLookedObject(player);
			if(ray.getFace() !=null){
				if(jar.hasLabel(ray.getFace())){
					jar.setHasLabel(ray.getFace(), false);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
					if(!player.playerAbilities.isCreativeMode){
						if(!worldIn.isRemote)ItemUtil.dropItemOnSide(worldIn, pos, new ItemStack(Items.ITEM_FRAME), ray.getFace());
					}
					BlockUtil.markBlockForUpdate(worldIn, pos);
				}
			}
		}
    }	
	
	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 *  
	 * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
	 * currentPos
	 */
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
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

}
