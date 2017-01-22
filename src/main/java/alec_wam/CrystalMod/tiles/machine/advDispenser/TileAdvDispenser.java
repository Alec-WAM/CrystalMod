package alec_wam.CrystalMod.tiles.machine.advDispenser;

import java.util.Iterator;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.handler.EventHandler;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerCM;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileAdvDispenser extends TileEntityInventory implements IFacingTile, IMessageHandler {

	private FakePlayerCM fakePlayer;
	private EnumFacing facing = EnumFacing.NORTH;
	private final ItemStackList pendingStacks = new ItemStackList();
	public InteractType interact = InteractType.BLOCK;
	public ClickType click = ClickType.RIGHT;
	public boolean isSneaking;
	public RedstoneMode redstone = RedstoneMode.ON;
	public int cooldown;
	public TileAdvDispenser() {
		super("AdvDispenser", 9);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
		nbt.setInteger("InteractType", interact.ordinal());
		nbt.setInteger("ClickType", click.ordinal());
		nbt.setBoolean("Sneaking", isSneaking);
		nbt.setInteger("Cooldown", cooldown);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		setFacing(nbt.getInteger("Facing"));
		interact = InteractType.values()[nbt.getInteger("InteractType")];
		click = ClickType.values()[nbt.getInteger("ClickType")];
		isSneaking = nbt.getBoolean("Sneaking");
		cooldown = nbt.getInteger("Cooldown");
		updateAfterLoad();
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			
			boolean dontContinue = false;
			
			if(!pendingStacks.isEmpty()){
				Iterator<ItemStack> i = pendingStacks.getStacks().iterator();
				while(i.hasNext()){
					ItemStack stack = i.next();
					int inserted = ItemUtil.doInsertItem(this, stack, EnumFacing.UP);
					if(inserted > 0){
						pendingStacks.remove(stack, inserted, true);
					}
				}
				dontContinue = true;
			}
			
			if(this.cooldown > 0){
				int decrs = 1;
				this.cooldown-=decrs;
				if(cooldown > 0){
					dontContinue = true;
				}
			}
			
			if(dontContinue)return;
			
			if(!redstone.passes(getWorld(), getPos()))return;
			
			final BlockPos facingPos = getPos().offset(facing);
			int slotIndex = 0;
			@SuppressWarnings("unused")
			ItemStack stack = ItemStackTools.getEmptyStack();
			boolean random = false;
			if(random){
				
			} else {
				slotIndex = 0;
				stack = getStackInSlot(0);
			}
			if(this.fakePlayer == null){
				this.fakePlayer = new FakePlayerCM((WorldServer)getWorld());
			}
			this.fakePlayer.setLocationSide(facingPos, facing);
			this.fakePlayer.setSneaking(isSneaking);
			this.fakePlayer.inventory.clear();
			final InventoryPlayer playerInv = this.fakePlayer.inventory;
			for(int i = 0; i < 9; i++){
				playerInv.setInventorySlotContents(i, ItemStackTools.safeCopy(getStackInSlot(i)));
			}
			playerInv.currentItem = slotIndex;
			this.fakePlayer.updateAttributes();
			final ItemStack copy = playerInv.getStackInSlot(slotIndex);
			final float hX = (float)(fakePlayer.posX - facingPos.getX());
			final float hY = (float)(fakePlayer.posY - facingPos.getX());
			final float hZ = (float)(fakePlayer.posZ - facingPos.getX());
			
			
			
			EnumActionResult result = performInteraction(facingPos, copy, interact, click, hX, hY, hZ);
			
			if(result !=null){
				cooldown = Config.advDispenser_cooldown;
			}
			
			if(ItemStackTools.isValid(copy)){
				this.fakePlayer.getCooldownTracker().setCooldown(copy.getItem(), 0);
				this.fakePlayer.updateCooldown();
			}
			for (int l = 0; l < 9; ++l) {
	            ItemStack slot = playerInv.getStackInSlot(l);
	            if (ItemStackTools.isEmpty(slot)) {
	                slot = ItemStackTools.getEmptyStack();
	            }
	            playerInv.setInventorySlotContents(l, ItemStackTools.getEmptyStack());
	            setInventorySlotContents(l, ItemStackTools.safeCopy(slot));
	        }
	        for (int l = 9; l < playerInv.getSizeInventory(); ++l) {
	            ItemStack stackInSlot = playerInv.getStackInSlot(l);
	            if (ItemStackTools.isValid(stackInSlot)) {
	                ItemStackTools.incStackSize(stackInSlot, -ItemUtil.doInsertItem(this, stackInSlot, EnumFacing.UP));
	                if (!ItemStackTools.isEmpty(stackInSlot)) {
	                    this.pendingStacks.add(stackInSlot);
	                }
	            }
	        }
	        playerInv.clear();
	        this.fakePlayer.updateAttributes();
	        this.fakePlayer.setSneaking(false);
		}
	}

	
	public EnumActionResult performInteraction(BlockPos facingPos, ItemStack copy, InteractType interact, ClickType click, float hX, float hY, float hZ){
		if(interact == InteractType.BLOCK){
			if(!ItemStackTools.isValid(copy))return EnumActionResult.FAIL;
			if(click == ClickType.RIGHT){
				return this.fakePlayer.interactionManager.processRightClick(fakePlayer, getWorld(), copy, EnumHand.MAIN_HAND);
			} else {
				this.fakePlayer.interactionManager.onBlockClicked(facingPos, facing.getOpposite());
				for(int u = 0; u < 20; u++){
					this.fakePlayer.interactionManager.updateBlockRemoving();
				}
				this.fakePlayer.interactionManager.blockRemoving(facingPos);
				this.fakePlayer.interactionManager.cancelDestroyingBlock();
				return EnumActionResult.SUCCESS;
			}
		} else if(interact == InteractType.PLACE){
			if(click !=ClickType.RIGHT || !ItemStackTools.isValid(copy))return EnumActionResult.FAIL;
			if(copy.getItem() instanceof ItemBlock){
				ItemBlock itemblock = (ItemBlock)copy.getItem();
				int meta = itemblock.getMetadata(copy);
				@SuppressWarnings("deprecation")
				IBlockState placedState = itemblock.block.getStateForPlacement(getWorld(), facingPos, facing.getOpposite(), hX, hY, hZ, meta, fakePlayer);
				if(itemblock.placeBlockAt(copy, fakePlayer, getWorld(), facingPos, facing.getOpposite(), hX, hY, hZ, placedState)){
					ItemStackTools.incStackSize(copy, -1);
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.PASS;
			}
		} else if(interact == InteractType.USE){
			if(click == ClickType.RIGHT){
				if(!ItemStackTools.isValid(copy))return EnumActionResult.FAIL;
				//Can Interact
				this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, copy);
				final PlayerInteractEvent.RightClickBlock event = (PlayerInteractEvent.RightClickBlock)ForgeHooks.onRightClickBlock(this.fakePlayer, EnumHand.MAIN_HAND, facingPos, facing.getOpposite(), ForgeHooks.rayTraceEyeHitVec(this.fakePlayer, 2.0));
                if (!event.isCanceled() && event.getUseItem() != Event.Result.DENY && copy.getItem().onItemUseFirst(this.fakePlayer, getWorld(), facingPos, facing.getOpposite(), hX, hY, hZ, EnumHand.MAIN_HAND) == EnumActionResult.PASS) {
                    return copy.onItemUse(this.fakePlayer, getWorld(), facingPos, EnumHand.MAIN_HAND, facing.getOpposite(), hX, hY, hZ);
                }
                this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
                return EnumActionResult.FAIL;
			} else {
				final PlayerInteractEvent.LeftClickBlock event = (PlayerInteractEvent.LeftClickBlock)ForgeHooks.onLeftClickBlock(this.fakePlayer, facingPos, facing.getOpposite(), ForgeHooks.rayTraceEyeHitVec(this.fakePlayer, 2.0));
				if (!event.isCanceled() && event.getUseItem() != Event.Result.DENY){
					//Prevents Looping the event
					EventHandler.blockClickEvent = true;
					this.fakePlayer.interactionManager.onBlockClicked(facingPos, facing.getOpposite());
					EventHandler.blockClickEvent = false;
					for(int u = 0; u < 20; u++){
						this.fakePlayer.interactionManager.updateBlockRemoving();
					}
					this.fakePlayer.interactionManager.blockRemoving(facingPos);
					this.fakePlayer.interactionManager.cancelDestroyingBlock();
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.FAIL;
			}
		} else if(interact == InteractType.ACTIVATE){
			if(click == ClickType.RIGHT){
				//Can Interact
				this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, copy);
				final PlayerInteractEvent.RightClickBlock event = (PlayerInteractEvent.RightClickBlock)ForgeHooks.onRightClickBlock(this.fakePlayer, EnumHand.MAIN_HAND, facingPos, facing.getOpposite(), ForgeHooks.rayTraceEyeHitVec(this.fakePlayer, 2.0));
                if (!event.isCanceled() && event.getUseItem() != Event.Result.DENY){
                	IBlockState blockFacing = getWorld().getBlockState(facingPos);
                	boolean interacted = blockFacing.getBlock().onBlockActivated(getWorld(), facingPos, blockFacing, fakePlayer, EnumHand.MAIN_HAND, facing.getOpposite(), hX, hY, hZ);
                	return interacted ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
                }
                this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
                return EnumActionResult.FAIL;
			} else {
				//Mainly for single clicks
				final PlayerInteractEvent.LeftClickBlock event = (PlayerInteractEvent.LeftClickBlock)ForgeHooks.onLeftClickBlock(this.fakePlayer, facingPos, facing.getOpposite(), ForgeHooks.rayTraceEyeHitVec(this.fakePlayer, 2.0));
				if (!event.isCanceled() && event.getUseItem() != Event.Result.DENY){
					this.fakePlayer.interactionManager.onBlockClicked(facingPos, facing.getOpposite());
					getWorld().extinguishFire(null, facingPos, facing.getOpposite());
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.FAIL;
			}
		} else if(interact == InteractType.USEAIR){
			if(click !=ClickType.RIGHT || !ItemStackTools.isValid(copy))return EnumActionResult.FAIL;
			//Proper events called 
			return this.fakePlayer.interactionManager.processRightClick(fakePlayer, getWorld(), copy, EnumHand.MAIN_HAND);
		} else if(interact == InteractType.ENTITY){
			/*Pair<Vec3d, Vec3d> rayVecs = EntityUtil.getStartAndEndLookVec(fakePlayer, 3.0f);
			final Vec3d start = rayVecs.getLeft();
            Vec3d end = rayVecs.getRight();
            final RayTraceResult trace = this.fakePlayer.worldObj.rayTraceBlocks(start, end, false, true, true);
            if (trace != null && trace.hitVec != null) {
                end = trace.hitVec;
            }
            Entity hitEntity = null;
            final List<Entity> list = (List<Entity>)this.worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(facingPos));
            if (list.isEmpty()) {
            	return EnumActionResult.FAIL;
            }
            double distance = 0.0;
            Entity backupEntity = null;
            for(Entity entity : list){
            	//Right Click Anything
            	if(entity.canBeCollidedWith() && !entity.noClip && (click == ClickType.RIGHT || (!(hitEntity instanceof EntityItem) && !(hitEntity instanceof EntityXPOrb) && !(hitEntity instanceof EntityArrow)))){
            		backupEntity = hitEntity;
            		final AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expandXyz(0.30000001192092896);
                    final RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(start, end);
                    if (raytraceresult1 == null) {
                        continue;
                    }
                    final double distance2 = start.squareDistanceTo(raytraceresult1.hitVec);
                    if (distance2 >= distance && distance != 0.0) {
                        continue;
                    }
                    hitEntity = entity;
                    distance = distance2;
            	}
            }
            
            if(hitEntity == null && backupEntity !=null){
            	hitEntity = backupEntity;
            }*/
            Entity hitEntity = null;
            
            RayTraceResult ray = EntityUtil.getRayTraceEntity(getWorld(), fakePlayer, 3.0D, fakePlayer.interactionManager.getBlockReachDistance());
            
            hitEntity = ray == null ? null : ray.entityHit;
            
            if(hitEntity == null){
            	return EnumActionResult.FAIL;
            }
            
            if(click == ClickType.RIGHT){
            	this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, copy);
            	return this.fakePlayer.interactOn(hitEntity, EnumHand.MAIN_HAND);
            } else {
            	if(!(hitEntity instanceof EntityItem) && !(hitEntity instanceof EntityXPOrb) && !(hitEntity instanceof EntityArrow)){
            		this.fakePlayer.updateCooldown();
            		this.fakePlayer.attackTargetEntityWithCurrentItem(hitEntity);
            		return EnumActionResult.SUCCESS;
            	}
            }
		}
		return EnumActionResult.FAIL;
	}
	
	public static enum InteractType {
		BLOCK, PLACE, USE, ACTIVATE, USEAIR, ENTITY;
	}
	
	public static enum ClickType {
		LEFT, RIGHT;
	}
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	@Override
	public boolean useVerticalFacing(){
		return true;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Settings")){
			boolean dirty = false;
			if(messageData.hasKey("Interact")){
				this.interact = InteractType.values()[messageData.getInteger("Interact")];
				dirty = true;
			}
			if(messageData.hasKey("Sneaking")){
				this.isSneaking = messageData.getBoolean("Sneaking");
				dirty = true;
			}
			if(messageData.hasKey("Click")){
				this.click = ClickType.values()[messageData.getInteger("Click")];
				dirty = true;
			}
			if(messageData.hasKey("Redstone")){
				this.redstone = RedstoneMode.values()[messageData.getInteger("Redstone")];
				dirty = true;
			}
			if(dirty){
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
		}
	}
}
