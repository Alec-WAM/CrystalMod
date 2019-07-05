package alec_wam.CrystalMod.tiles.crate;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.init.ModItems;
import alec_wam.CrystalMod.items.EnumPlateItem;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.EnumCrystalColorWithCreative;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockCrate extends ContainerBlockVariant<EnumCrystalColorSpecialWithCreative> {
	//All Directions
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	/**Click Times**/
	public static final int STARTING_TIME = 20,	CONTINOUS_TIME = 5,	DOUBLE_CLICK_TIME = 10,	FILL_TIME = 20,	DELAY_PICKUP_TIME = 20,	EMPLY_CRATE_TIME = 5;

	public BlockCrate(EnumCrystalColorSpecialWithCreative type, BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockCrate> variantGroup,Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}	
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {		
		if(type != EnumCrystalColorSpecialWithCreative.CREATIVE){
			int stacks = TileEntityCrate.TIER_STORAGE_STACKS[type.ordinal()];
			int largeNumber = (64*TileEntityCrate.TIER_STORAGE_STACKS[type.ordinal()]);
			tooltip.add(new TranslationTextComponent("crystalmod.info.crate.storage", ""+stacks, NumberFormat.getNumberInstance(Locale.US).format(largeNumber)));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityCrate){
			TileEntityCrate crate = (TileEntityCrate)tile;
			if(player.isSneaking() && crate.getLastClicked() == CONTINOUS_TIME){
				crate.setLastClicked(CONTINOUS_TIME + 1);

				ItemStack stored = crate.getStack();
				if(ItemStackTools.isValid(stored)){
					ItemStack item2 = ItemStackTools.getEmptyStack();
					if (ItemStackTools.getStackSize(stored) > 1){
						item2 = ItemUtil.copy(stored, ItemStackTools.getStackSize(stored)-1);
					}
					ItemStackTools.setStackSize(stored, 1);

					if (ItemStackTools.isEmpty(spawnItem(player, stored))){
						crate.setStack(item2);
					}
					return 0.0f;
				}
			}
		}
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof TileEntityCrate){
			TileEntityCrate crate = (TileEntityCrate)tile;
			crate.resetLastClicked();
		
			if (crate.getLastClicked() > 0){
				return -1;
			}
		}
		
		return super.getBlockHardness(state, world, pos);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		if(worldIn.isRemote)return true;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCrate){
			TileEntityCrate crate = (TileEntityCrate)tile;
			boolean isCreativeCrate = crate.tier == EnumCrystalColorWithCreative.CREATIVE.ordinal();
			ItemStack playerItem = player.getHeldItem(hand);
			ItemStack stored = crate.getStack();
			boolean changed = false;
			boolean isFront = ray.getFace() == state.get(FACING);
			
			if(isFront){
				if(player.isSneaking()){
					if(ToolUtil.isHoldingWrench(player, hand)){
						crate.rotation++;
						crate.rotation%=4;
						BlockUtil.markBlockForUpdate(worldIn, pos);
						return true;
					}
				}
				else {
					if(ItemStackTools.isValid(playerItem)){
						if(playerItem.getItem() == ModItems.metalPlateGroup.getItem(EnumPlateItem.DARKIRON) && crate.hasVoidUpgrade){
							if(worldIn.isRemote)return true;
							crate.hasVoidUpgrade = false;
							CompoundNBT data = new CompoundNBT();
							data.putBoolean("VoidUpgrade", false);
							CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "VoidUpgrade", data), crate);
							dropItemInWorld(state, crate, player, new ItemStack(ModItems.miscUpgrades.getItem(EnumMiscUpgrades.VOID)), crate.getWorld().rand.nextFloat());
							return true;
						}
						if(playerItem.getItem() == ModItems.miscUpgrades.getItem(EnumMiscUpgrades.VOID) && !crate.hasVoidUpgrade){
							if(worldIn.isRemote)return true;
							crate.hasVoidUpgrade = true;
							if(!player.abilities.isCreativeMode){
								playerItem.shrink(1);
								player.setHeldItem(hand, playerItem);
							}
							
							SoundType soundtype = SoundType.STONE;
		                    worldIn.playSound((PlayerEntity)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
							
							CompoundNBT data = new CompoundNBT();
							data.putBoolean("VoidUpgrade", true);
							CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "VoidUpgrade", data), crate);
							return true;
						}
					}
				}
			}
			
			if(crate.isTimerActive()){
				crate.resetTimer();
				if(ItemStackTools.isValid(stored)){
					if (crate.getMode() == 0 && ItemStackTools.isValid(playerItem) && playerItem.getItem() instanceof BlockItem && ((BlockItem)playerItem.getItem()).getBlock() instanceof BlockCrate) {
						BlockCrate crateBlock = (BlockCrate) ((BlockItem)playerItem.getItem()).getBlock();
						if(crateBlock.type.ordinal() > this.type.ordinal() && crateBlock.type != EnumCrystalColorSpecialWithCreative.CREATIVE){
							playerItem = playerItem.copy();
							if(!player.abilities.isCreativeMode && ItemStackTools.isValid(spawnItem(player, new ItemStack(this, 1)))){
								playerItem = player.getHeldItem(hand);
							} else {
								final ItemStack oldStored = crate.getStack();
								final Direction oldFacing = state.get(FACING);
								BlockState newState = crateBlock.getDefaultState().with(FACING, oldFacing);
								worldIn.setBlockState(pos, newState, 1);
								SoundType soundtype = getSoundType(newState, worldIn, pos, player);
			                    worldIn.playSound((PlayerEntity)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								TileEntity newTile = worldIn.getTileEntity(pos);
								if(newTile !=null && newTile instanceof TileEntityCrate){
									((TileEntityCrate)newTile).setStack(oldStored);
									((TileEntityCrate)newTile).tier = crateBlock.type.ordinal();
								}
								BlockUtil.markBlockForUpdate(worldIn, pos);
								if(!player.abilities.isCreativeMode){
									playerItem = ItemUtil.consumeItem(playerItem);
									player.setHeldItem(hand, playerItem);
								}
								return true;
							}
						}
					} else if(crate.getMode() == 1 && isFront && (ItemStackTools.isEmpty(playerItem) || ItemUtil.canCombine(playerItem, stored)) && !isCreativeCrate){
						for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
							ItemStack original = player.inventory.getStackInSlot(slot);
							ItemStack newItem = crate.addItem(original);
							if(original !=newItem){
								changed = true;
							}
							player.inventory.setInventorySlotContents(slot, newItem);
						}
					} else if(ItemStackTools.isValid(playerItem) && isFront){
						if (playerItem.getItem() instanceof BlockItem && ((BlockItem)playerItem.getItem()).getBlock() instanceof BlockCrate) {
							BlockCrate crateBlock = (BlockCrate) ((BlockItem)playerItem.getItem()).getBlock();
							if(crateBlock.type.ordinal() > this.type.ordinal() && crateBlock.type != EnumCrystalColorSpecialWithCreative.CREATIVE){
								crate.setClick(0, 0, DOUBLE_CLICK_TIME);
								
								playerItem = crate.addItem(playerItem);
								changed = true;
							}
						} else if(ItemStackTools.isEmpty(stored) || ItemUtil.canCombine(playerItem, stored)){
							crate.setClick(0, 1, DOUBLE_CLICK_TIME);
							
							playerItem = crate.addItem(playerItem);
							changed = true;
						}
					}
				}
			} else if(ItemStackTools.isValid(playerItem) && isFront){
				crate.setClick(0, 1, DOUBLE_CLICK_TIME);
				
				playerItem = crate.addItem(playerItem);
				changed = true;
			}
			
			if(changed){
				player.setHeldItem(hand, playerItem);
				return true;
			}
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }
	
	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityCrate){
			TileEntityCrate crate = (TileEntityCrate)tile;
			if(crate.isAbrupted()){
				ItemStack stored = crate.getStack();
				ItemStack held = player.getHeldItemMainhand();
				BlockRayTraceResult ray = BlockUtil.rayTrace(world, player, RayTraceContext.FluidMode.NONE);
				boolean inFront = ray.getFace() == state.get(FACING);
				if(ItemStackTools.isValid(stored) && inFront){
					if (crate.isTimerActive() && crate.getSelectedSlot() != player.inventory.currentItem && ItemStackTools.isEmpty(held) && crate.getMode() == 2 && !player.isSneaking()){
						crate.resetTimer();
					}else{
						if (ItemStackTools.isEmpty(held) && !player.isSneaking()){
							crate.setClick(player.inventory.currentItem, 2, FILL_TIME);
						}else{
							crate.resetTimer();
						}
						
						if (player.isSneaking()) {
							ItemStack item2 = stored.copy();
							ItemStackTools.incStackSize(item2, -1);
							ItemStackTools.setStackSize(stored, 1);
							dropItemInWorld(state, crate, player, stored, crate.getWorld().rand.nextFloat());
							stored = item2;
							if (ItemStackTools.isEmpty(stored)){
								crate.setStack(ItemStackTools.getEmptyStack());
							}else{
								crate.setStack(stored);
							}
						}else{
							int size = ItemStackTools.getStackSize(stored);
							if(ModConfig.BLOCKS.Crate_LeaveItem.get() && size == 0 && !ItemStackTools.isEmpty(stored))size++;
							int maxStackSize = Math.min(size, stored.getMaxStackSize());
							ItemStack item2 = stored.copy();
							ItemStackTools.incStackSize(item2, -maxStackSize);
							ItemStackTools.setStackSize(stored, maxStackSize);
							
							dropItemInWorld(state, crate, player, stored, crate.getWorld().rand.nextFloat());
							stored = item2;
							if (ItemStackTools.isEmpty(stored)){
								crate.setStack(ItemStackTools.getEmptyStack());
							}else{
								crate.setStack(stored);
							}
						}
						if (ItemStackTools.isEmpty(crate.getStack())){
							crate.resetTimer();
						}
						crate.setLastClicked(STARTING_TIME);
					}
				}else{
					crate.resetTimer();
					crate.setLastClicked(EMPLY_CRATE_TIME);
				}
			}
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof BlockCrate)) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileEntityCrate) {
				TileEntityCrate crate = (TileEntityCrate)tileentity;
				//Made a custom random drop to make sure stack sizes are not too big because of massive storage
				float f2 = RANDOM.nextFloat() * 0.75F + 0.125F;
				float f3 = RANDOM.nextFloat() * 0.75F;
				float f4 = RANDOM.nextFloat() * 0.75F + 0.125F;
				ItemStack stack = crate.getStack();
				while(!stack.isEmpty()) {
					@SuppressWarnings("deprecation")
					int sizeSizeDropped = Math.min(RANDOM.nextInt(21) + 10, stack.getItem().getMaxStackSize());
					ItemEntity entityitem = new ItemEntity(worldIn, pos.getX() + (double)f2, pos.getY() + (double)f3, pos.getZ() + (double)f4, stack.split(sizeSizeDropped));
					Vec3d motion = new Vec3d(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
					entityitem.setMotion(motion);
					worldIn.addEntity(entityitem);
				}
				
				if(crate.hasVoidUpgrade){
					ItemEntity entityitem = new ItemEntity(worldIn, pos.getX() + (double)f2, pos.getY() + (double)f3, pos.getZ() + (double)f4, new ItemStack(ModItems.miscUpgrades.getItem(EnumMiscUpgrades.VOID)));
					Vec3d motion = new Vec3d(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
					entityitem.setMotion(motion);
					worldIn.addEntity(entityitem);
				}
				
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		} else {
			TileEntity newTile = worldIn.getTileEntity(pos);
			if(newTile !=null && newTile instanceof TileEntityCrate){
				((TileEntityCrate)newTile).tier = ((BlockCrate)newState.getBlock()).type.ordinal();
			}
		}
	}
	
	@Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction enumfacing = BlockUtil.getFacingFromContext(context, true);
		return this.getDefaultState().with(FACING, enumfacing);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link BlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
	 */
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}	
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		int powerInput = 0;
		
		TileEntity tile = world.getTileEntity(pos);
		
		if (tile != null && tile instanceof TileEntityCrate){
			TileEntityCrate crate = (TileEntityCrate)tile;
			if(ItemStackTools.isValid(crate.getStack())){
				ItemStack stored = crate.getStack();
				int maxItems = (crate.getCrateSize() * stored.getMaxStackSize());
				int result = (int) Math.round((double) ItemStackTools.getStackSize(stored) / (double) maxItems * 15.0D);
				
				if (result == 0) result = 1;
				if (result == 15 && ItemStackTools.getStackSize(stored) != maxItems) result = 14;
				
				return Math.max(powerInput, result);
			}
		}
		
		return powerInput;
	}
	
	public ItemStack spawnItem(PlayerEntity player, ItemStack item){
		if (player.getEntityWorld().isRemote) return item;
		
		int currentSlot = player.inventory.currentItem;
		int inventorySize = player.inventory.mainInventory.size();
		int maxStackSize = Math.min(player.inventory.getInventoryStackLimit(), item.getMaxStackSize());
		
		int pass = 0;
		int slot = 0;
		
		while (ItemStackTools.isValid(item)) {
			if (pass == 0){
				slot = currentSlot;
			}else if (slot == currentSlot){
				slot++;
			}
			
			ItemStack playerItem = player.inventory.getStackInSlot(slot);
			
			if ((pass == 0 || pass == 2) && (ItemStackTools.isEmpty(playerItem))){
				playerItem = item.copy();
				
				if (ItemStackTools.getStackSize(item) > maxStackSize){
					ItemStackTools.setStackSize(playerItem, maxStackSize);
					ItemStackTools.incStackSize(item, -maxStackSize);
				}else{
					item = ItemStackTools.getEmptyStack();
				}
				
				playerItem.setAnimationsToGo(5);
			}else if ((pass == 0 || pass == 1) && ItemUtil.canCombine(item, playerItem) && ItemStackTools.getStackSize(playerItem) < maxStackSize){
				if (ItemStackTools.getStackSize(item) + ItemStackTools.getStackSize(playerItem) > maxStackSize){
					int len = Math.min(ItemStackTools.getStackSize(item), maxStackSize - ItemStackTools.getStackSize(playerItem));
					ItemStackTools.incStackSize(playerItem, len);
					ItemStackTools.incStackSize(item, -len);
				}else{
					if(ItemStackTools.isValid(playerItem)){
						ItemStackTools.incStackSize(playerItem, ItemStackTools.getStackSize(item));
					} else {
						playerItem = item;
					}
					item = ItemStackTools.getEmptyStack();
				}
				
				playerItem.setAnimationsToGo(5);
			}
			
			player.inventory.mainInventory.set(slot, playerItem);
			
			if (pass == 0){
				pass = 1;
				slot = 0;
				continue;
			}
			
			if (slot >= inventorySize - 1){
				if (pass > 2){
					break;
				}else{
					slot = -1;
				}
				pass++;
			}
			slot++;
		}
		
		return item;
	}
	
	public static void dropItemInWorld(BlockState sourceState, TileEntityCrate source, PlayerEntity player, ItemStack stack, double speedfactor)
	{
		boolean anySide = false;
		if(anySide){
			int hitOrientation = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
			double stackCoordX = 0.0D;double stackCoordY = 0.0D;double stackCoordZ = 0.0D;
			switch (hitOrientation)
			{
			case 0: 
				stackCoordX = source.getPos().getX() + 0.5D;
				stackCoordY = source.getPos().getY() + 0.5D;
				stackCoordZ = source.getPos().getZ() - 0.25D;
				break;
			case 1: 
				stackCoordX = source.getPos().getX() + 1.25D;
				stackCoordY = source.getPos().getY() + 0.5D;
				stackCoordZ = source.getPos().getZ() + 0.5D;
				break;
			case 2: 
				stackCoordX = source.getPos().getX() + 0.5D;
				stackCoordY = source.getPos().getY() + 0.5D;
				stackCoordZ = source.getPos().getZ() + 1.25D;
				break;
			case 3: 
				stackCoordX = source.getPos().getX() - 0.25D;
				stackCoordY = source.getPos().getY() + 0.5D;
				stackCoordZ = source.getPos().getZ() + 0.5D;
			}
			ItemEntity droppedEntity = new ItemEntity(source.getWorld(), stackCoordX, stackCoordY, stackCoordZ, stack);
			if (player != null)
			{
				Vec3d motion = new Vec3d(player.posX - stackCoordX, player.posY - stackCoordY, player.posZ - stackCoordZ);
				motion.normalize();
				droppedEntity.setMotion(motion);
				double offset = 0.25D;
				droppedEntity.setVelocity(motion.x * offset, motion.y * offset, motion.z * offset);
			}
			Vec3d newMotion = droppedEntity.getMotion().mul(speedfactor, speedfactor, speedfactor);
			droppedEntity.setMotion(newMotion);

			if(!source.getWorld().isRemote)source.getWorld().addEntity(droppedEntity);
		}
		else {
			Direction facing = sourceState.get(FACING);
			double stackCoordX = source.getPos().getX() + 0.5 + (facing.getXOffset() * 0.6);
			double stackCoordY = source.getPos().getY() + 0.5 + (facing.getYOffset() * 0.6);
			double stackCoordZ = source.getPos().getZ() + 0.5 + (facing.getZOffset() * 0.6);
			ItemEntity droppedEntity = new ItemEntity(source.getWorld(), stackCoordX, stackCoordY, stackCoordZ, stack);
			
			Vec3d motion = new Vec3d(facing.getXOffset() * speedfactor, facing.getYOffset() * speedfactor, facing.getZOffset() * speedfactor);
			motion.normalize();
			droppedEntity.setMotion(motion);
			double offset = 0.25D;
			droppedEntity.setVelocity(motion.x * offset, motion.y * offset, motion.z * offset);

			if(!source.getWorld().isRemote)source.getWorld().addEntity(droppedEntity);
		}
	}
}
