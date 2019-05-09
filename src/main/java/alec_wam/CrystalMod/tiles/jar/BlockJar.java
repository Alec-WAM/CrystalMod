package alec_wam.CrystalMod.tiles.jar;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockJar extends BlockContainerVariant<WoodType> implements IBucketPickupHandler, ILiquidContainer  {
	protected static final VoxelShape SHAPE_CORK = Block.makeCuboidShape(5.0D, 13.0D, 5.0D, 11.0D, 15.0D, 11.0D);
	protected static final VoxelShape SHAPE_MAIN = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D);
	public static final VoxelShape SHAPE_ALL = VoxelShapes.or(SHAPE_MAIN, SHAPE_CORK);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public BlockJar(WoodType type, BlockVariantGroup<WoodType, BlockJar> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		if(ItemNBTHelper.verifyExistance(stack, "TILE_DATA")){
			NBTTagCompound tileNBT = ItemNBTHelper.getCompound(stack).getCompound("TILE_DATA");
			if(tileNBT.getBoolean("IsShulker")){
				tooltip.add(new TextComponentTranslation("crystalmod.info.jar.shulker"));
			}
			if(tileNBT.hasKey("Potion")){
				PotionType type = PotionUtils.getPotionTypeFromNBT(tileNBT);
				if(type !=PotionTypes.EMPTY){
					for (PotionEffect potioneffect : type.getEffects())
		            {
		                String s1 = Lang.translateToLocal(potioneffect.getEffectName()).trim();
		                Potion potion = potioneffect.getPotion();

		                if (potioneffect.getAmplifier() > 0)
		                {
		                    s1 = s1 + " " + Lang.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
		                }

		                if (potioneffect.getDuration() > 20)
		                {
		                    s1 = s1 + " (" + PotionUtil.getPotionDurationString(potioneffect, 1.0F) + ")";
		                }

		                if (potion.isBadEffect())
		                {
		                    tooltip.add(new TextComponentString(TextFormatting.RED + s1));
		                }
		                else
		                {
		                	tooltip.add(new TextComponentString(TextFormatting.BLUE + s1));
		                }
		            }
					tooltip.add(new TextComponentTranslation("crystalmod.info.jar.contains", new Object[]{""+tileNBT.getInt("Count"), "3"}));
				}
			}
		}
    }
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
	@Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE_ALL;
	}
	
	@Override
    public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos) {
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
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityJar) {
        	TileEntityJar jar = (TileEntityJar) tile;
            return jar.getPotionCount();
        }

        return 0;
    }
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityJar)) return false;
		TileEntityJar jar = (TileEntityJar)tile;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(held.getItem() == Items.SHULKER_SHELL && !jar.isShulkerLamp() && jar.getPotionCount() == 0){
				if(!jar.getLabelMap().isEmpty()){
					for(EnumFacing face : EnumFacing.Plane.HORIZONTAL){
						if(jar.hasLabel(face)){
							if(!world.isRemote)ItemUtil.dropItemOnSide(world, pos, new ItemStack(Items.ITEM_FRAME), face);
							jar.setHasLabel(face, false);
						}
					}
				}
				jar.setShulkerLamp(true);
				if(!player.abilities.isCreativeMode){
					player.setHeldItem(hand, ItemUtil.consumeItem(held));
				}
				world.checkLightFor(EnumLightType.BLOCK, pos);
				BlockUtil.markBlockForUpdate(world, pos);
				return true;
			}
			else if(held.getItem() == Items.ITEM_FRAME && facing.getAxis().isHorizontal() && !jar.isShulkerLamp()){
				if(!jar.hasLabel(facing)){
					jar.setHasLabel(facing, true);
					world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					if(!player.abilities.isCreativeMode){
						player.setHeldItem(hand, ItemUtil.consumeItem(held));
					}
					BlockUtil.markBlockForUpdate(world, pos);
					return true;
				}
			}
			else if(held.getItem() == Items.POTION){
				PotionType type = PotionUtils.getPotionFromItem(held);
				if(type.getEffects().size() > 0 && (jar.getPotion() == type || jar.getPotion() == PotionTypes.EMPTY)){
					if(jar.getPotionCount() < 3){
						if(jar.getPotion() == PotionTypes.EMPTY){
							jar.setPotionType(type);
						}
						jar.setPotionCount(jar.getPotionCount()+1);
						if(!player.abilities.isCreativeMode){
							ItemUtil.setPlayerHandSilently(player, hand, new ItemStack(Items.GLASS_BOTTLE));
						}
						world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
						BlockUtil.markBlockForUpdate(world, pos);
						return true;
					}
				}
			} else if(held.getItem() == Items.GLASS_BOTTLE){
				if(jar.getPotion() !=PotionTypes.EMPTY && jar.getPotionCount() > 0){
					ItemUtil.setPlayerHandSilently(player, hand, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), jar.getPotion()));
					jar.setPotionCount(jar.getPotionCount()-1);
					if(jar.getPotionCount() <= 0){
						jar.setPotionType(PotionTypes.EMPTY);
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
	public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof TileEntityJar)) return;
		TileEntityJar jar = (TileEntityJar)tile;
		ItemStack held = player.getHeldItemMainhand();
		if(ItemStackTools.isValid(held) && held.getItem() == Items.ITEM_FRAME){
			RayTraceResult ray = EntityUtil.getLookedObject(player);
			if(ray.sideHit !=null){
				if(jar.hasLabel(ray.sideHit)){
					jar.setHasLabel(ray.sideHit, false);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
					if(!player.abilities.isCreativeMode){
						if(!worldIn.isRemote)ItemUtil.dropItemOnSide(worldIn, pos, new ItemStack(Items.ITEM_FRAME), ray.sideHit);
					}
					BlockUtil.markBlockForUpdate(worldIn, pos);
				}
			}
		}
    }	
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
	    //TODO Re-add this
		/*TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityJar){
			TileEntityJar jar = (TileEntityJar)tile;
			if(jar.isShulkerLamp() && !player.abilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0){
				if(!worldIn.isRemote){
					EntityShulkerBullet bullet = new EntityShulkerBullet(worldIn, player, player, EnumFacing.Axis.Y);
					bullet.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1 + 0.5, pos.getZ() + 0.5, bullet.rotationYaw, bullet.rotationPitch);
					worldIn.spawnEntity(bullet);
				}
				jar.setShulkerLamp(false);
			}
		}*/
		super.harvestBlock(worldIn, player, pos, state, te, stack);
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
	public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(WATERLOGGED);
	}	
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
		if (state.get(WATERLOGGED)) {
			worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IFluidState getFluidState(IBlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
		return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
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
