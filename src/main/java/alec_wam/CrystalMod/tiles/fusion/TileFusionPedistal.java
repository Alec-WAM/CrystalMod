package alec_wam.CrystalMod.tiles.fusion;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.pedistals.IFusionPedistal;
import alec_wam.CrystalMod.api.pedistals.IPedistal;
import alec_wam.CrystalMod.api.recipe.IFusionRecipe;
import alec_wam.CrystalMod.client.sound.FusionRunningSound;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Vector3d;
import alec_wam.CrystalMod.util.data.watchable.WatchableBoolean;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileFusionPedistal extends TileEntityInventory implements IFusionPedistal, IFacingTile, IMessageHandler {

	public List<IPedistal> linkedPedistals = Lists.newArrayList();
	public final WatchableBoolean isCrafting = new WatchableBoolean();
	public final WatchableInteger craftingProgress = new WatchableInteger();
	public final WatchableInteger craftingCooldown = new WatchableInteger();
	public IFusionRecipe runningRecipe;
	public EnumFacing facing = EnumFacing.UP;
	
	public TileFusionPedistal() {
		super("FusionPedistal", 1);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		setFacing(nbt.getInteger("Facing"));
		updateAfterLoad();
	}
	
	@SideOnly(Side.CLIENT)
	private FusionRunningSound fusionSound = null;
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			if(this.shouldDoWorkThisTick(5)){
				if(isCrafting.getValue() !=isCrafting.getLastValue()){
					isCrafting.setLastValue(isCrafting.getValue());
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("Value", isCrafting.getValue());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "isCrafting", nbt), this);
				}
			}
			if(this.shouldDoWorkThisTick(2)){
				if(craftingProgress.getValue() !=craftingProgress.getLastValue()){
					craftingProgress.setLastValue(craftingProgress.getValue());
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Value", craftingProgress.getValue());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Progress", nbt), this);
				}
				if(craftingCooldown.getValue() !=craftingCooldown.getLastValue()){
					craftingCooldown.setLastValue(craftingCooldown.getValue());
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Value", craftingCooldown.getValue());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Cooldown", nbt), this);
				}
			}
			
			if(isCrafting.getValue()){
				for(IPedistal pedistal : linkedPedistals){
					if(((TileEntity)pedistal).isInvalid()){
						cancelCrafting();
						return;
					}
				}
				
				if(runningRecipe == null || !runningRecipe.matches(this, getWorld(), linkedPedistals)){
					cancelCrafting();
					return;
				}
				
				if(craftingProgress.getValue() < 200){
					if(craftingProgress.getValue() == 0){
						getWorld().playSound(null, getPos(), ModSounds.fusionStartup, SoundCategory.BLOCKS, 0.75F, 1.0F);
					}
					craftingProgress.add(1);
					if(craftingCooldown.getValue() < 100)this.craftingCooldown.add(1);
				} else if(craftingProgress.getValue() >= 200){
					runningRecipe.finishCrafting(this, getWorld(), linkedPedistals);
					getWorld().playSound(null, getPos(), ModSounds.fusionCooldown, SoundCategory.BLOCKS, 0.75F, 1.0F);
					isCrafting.setValue(false);
				}
				
			} else if(craftingProgress.getValue() > 0){
				craftingProgress.setValue(0);
			}
			
			if(craftingCooldown.getValue() > 0 && !isCrafting.getValue()){
				craftingCooldown.sub(1);
				if(craftingCooldown.getValue() <= 0 && runningRecipe != null){
					getWorld().playSound(null, getPos(), ModSounds.fusionDing, SoundCategory.BLOCKS, 0.75F, 1.0F);
				}
			}
		} else {
			updateSound();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void updateSound(){
		//Remote
		if(isCrafting.getValue() && fusionSound == null){
			fusionSound = new FusionRunningSound(this);
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(fusionSound);
		}
		if(fusionSound !=null && !isCrafting.getValue()){
			fusionSound = null;
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean canExtract(int slot, int amount){
		return !isLocked();
	}
	
	public void startCrafting(@Nullable EntityPlayer player){
		pedistalSearch();
		
		runningRecipe = CrystalModAPI.findFusionRecipe(this, getWorld(), linkedPedistals);
		
		if(runningRecipe !=null && runningRecipe.matches(this, getWorld(), linkedPedistals)){
			String message = runningRecipe.canCraft(this, getWorld(), linkedPedistals);
			boolean passes = message.equalsIgnoreCase("true");
			if(passes){
				isCrafting.setValue(true);
			} else {
				if(player !=null && !getWorld().isRemote){
					ChatUtil.sendChat(player, message);
				}
			}
		} else {
			runningRecipe = null;
		}
	}
	
	public void cancelCrafting(){
		isCrafting.setValue(false);
		runningRecipe = null;
		craftingProgress.setValue(0);
		linkedPedistals.clear();
		getWorld().playSound(null, getPos(), ModSounds.fusionCooldown, SoundCategory.BLOCKS, 0.75F, 1.0F);
	}
	
	public void pedistalSearch(){
		if(isCrafting.getValue()){
			return;
		}
		
		linkedPedistals.clear();
		int range = 8;
		
		List<BlockPos> searchList = Lists.newArrayList();
		
		if(facing == EnumFacing.UP){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-range, 0, -range), getPos().add(range, 2, range)));
		} 
		else if(facing == EnumFacing.DOWN){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-range, -2, -range), getPos().add(range, 0, range)));
		} 
		else if(facing == EnumFacing.NORTH){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-range, -range, -2), getPos().add(range, range, 0)));
		} 
		else if(facing == EnumFacing.SOUTH){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-range, -range, 0), getPos().add(range, range, 2)));
		} 
		else if(facing == EnumFacing.WEST){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-2, -range, -range), getPos().add(0, range, range)));
		} 
		else if(facing == EnumFacing.EAST){
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(0, -range, -range), getPos().add(2, range, range)));
		} 
		else {
			searchList = Lists.newArrayList(BlockPos.getAllInBox(getPos().add(-range, 0, -range), getPos().add(range, 0, range)));
		}
		
		Vector3d thisPos = new Vector3d(getPos());
		for(BlockPos searchPos : searchList){
			if(getWorld().isBlockLoaded(searchPos)){
				TileEntity tile = getWorld().getTileEntity(searchPos);
				if(tile instanceof IPedistal && !(tile instanceof IFusionPedistal)){
					IPedistal pedistal = (IPedistal)tile;
					Vector3d tilePos = new Vector3d(tile.getPos());
					
					Vector3d facingVec = tilePos.copy();
					facingVec.sub(thisPos);
					double distance = tilePos.distanceSquared(thisPos);
					EnumFacing facing = EnumFacing.getFacingFromVector((float)facingVec.x, (float)facingVec.y, (float)facingVec.z);
					boolean inLine = false;
					if(this.facing.getAxis() == Axis.X){
						inLine = tile.getPos().getX() == getPos().getX();
					}
					if(this.facing.getAxis() == Axis.Y){
						inLine = tile.getPos().getY() == getPos().getY();
					}
					if(this.facing.getAxis() == Axis.Z){
						inLine = tile.getPos().getZ() == getPos().getZ();
					}
					if(distance >= 2.0D && (facing != pedistal.getRotation() && (inLine ? this.facing.getOpposite() !=pedistal.getRotation() : true))){
						linkedPedistals.add(pedistal);
					}
				}
			}
		}
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
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("isCrafting")){
			isCrafting.setLastValue(isCrafting.getValue());
			isCrafting.setValue(messageData.getBoolean("Value"));
		}
		if(messageId.equalsIgnoreCase("Progress")){
			craftingProgress.setLastValue(craftingProgress.getValue());
			craftingProgress.setValue(messageData.getInteger("Value"));
		}
		if(messageId.equalsIgnoreCase("Cooldown")){
			craftingCooldown.setLastValue(craftingCooldown.getValue());
			craftingCooldown.setValue(messageData.getInteger("Value"));
		}
	}
	
	@Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-9, -9, -9), pos.add(9, 9, 9));
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

	@Override
	public ItemStack getStack() {
		return getStackInSlot(0);
	}

	@Override
	public void setStack(ItemStack stack) {
		setInventorySlotContents(0, stack);
	}
	
	@Override
	public void onItemChanged(int slot){
		if(slot == 0){
			syncStack();
		}
	}
	
	public void syncStack(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			ItemStack stack = getStack();
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "StackSync", ItemStackTools.isEmpty(stack) ? new NBTTagCompound() : stack.serializeNBT()), this);
		}
	}

	@Override
	public EnumFacing getRotation() {
		return facing;
	}
	
	@Override
	public boolean isLocked(){
		return craftingCooldown.getValue() > 0 || isCrafting.getValue();
	}
	
}
