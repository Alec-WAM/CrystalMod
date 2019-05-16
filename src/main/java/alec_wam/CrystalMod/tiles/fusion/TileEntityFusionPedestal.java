package alec_wam.CrystalMod.tiles.fusion;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.recipes.IFusionRecipe;
import alec_wam.CrystalMod.api.tile.IFusionPedestal;
import alec_wam.CrystalMod.api.tile.IPedestal;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.data.WatchableBoolean;
import alec_wam.CrystalMod.util.data.WatchableInteger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityFusionPedestal extends TileEntityInventory implements IMessageHandler, IFusionPedestal {
	
	public List<IPedestal> linkedPedestals = Lists.newArrayList();
	public final WatchableBoolean isCrafting = new WatchableBoolean();
	public final WatchableInteger craftingProgress = new WatchableInteger();
	public final WatchableInteger craftingCooldown = new WatchableInteger();
	public IFusionRecipe runningRecipe;
	
	public TileEntityFusionPedestal() {
		super(ModBlocks.TILE_FUSION_PEDESTAL, "FusionPedestal", 1);
	}
	
	/*@SideOnly(Side.CLIENT)
	private FusionRunningSound fusionSound = null;*/

	@Override
	public EnumFacing getRotation() {
		return this.getBlockState().get(BlockPedestal.FACING);
	}
	
	@Override
	public void tick(){
		super.tick();
		/*BlockPos under = getPos().offset(getRotation().getOpposite());
		IBlockState below = getWorld().getBlockState(under);
		if(below.getBlock() == Blocks.REDSTONE_LAMP){*/
			if(isCrafting.getValue() == false){
				if(getWorld().getRedstonePowerFromNeighbors(getPos()) > 0){
					this.startCrafting(null);
				}
			}
		//}
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
					nbt.setInt("Value", craftingProgress.getValue());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Progress", nbt), this);
				}
				if(craftingCooldown.getValue() !=craftingCooldown.getLastValue()){
					craftingCooldown.setLastValue(craftingCooldown.getValue());
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInt("Value", craftingCooldown.getValue());
					CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Cooldown", nbt), this);
				}
			}
			
			if(isCrafting.getValue()){
				for(IPedestal pedestal : linkedPedestals){
					if(((TileEntity)pedestal).isRemoved()){
						cancelCrafting();
						return;
					}
				}
				
				if(runningRecipe == null || !runningRecipe.matches(this, getWorld(), linkedPedestals)){
					cancelCrafting();
					return;
				}
				//Default 200
				int craftTime = 100;
				if(craftingProgress.getValue() < craftTime){
					if(craftingProgress.getValue() == 0){
						//getWorld().playSound(null, getPos(), ModSounds.fusionStartup, SoundCategory.BLOCKS, 0.4F, 1.0F);
					}
					craftingProgress.add(1);
					if(craftingCooldown.getValue() < craftTime/2)this.craftingCooldown.add(1);
				} else if(craftingProgress.getValue() >= craftTime){
					runningRecipe.finishCrafting(this, getWorld(), linkedPedestals);
					//getWorld().playSound(null, getPos(), ModSounds.fusionCooldown, SoundCategory.BLOCKS, 0.1F, 1.0F);
					isCrafting.setValue(false);
				}
				
			} else if(craftingProgress.getValue() > 0){
				craftingProgress.setValue(0);
			}
			
			if(craftingCooldown.getValue() > 0 && !isCrafting.getValue()){
				craftingCooldown.sub(1);
				if(craftingCooldown.getValue() <= 0 && runningRecipe != null){
					//getWorld().playSound(null, getPos(), ModSounds.fusionDing, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
		} else {
			//updateSound();
		}
	}
	
	/*@SideOnly(Side.CLIENT)
	public void updateSound(){
		//Remote
		if(isCrafting.getValue() && fusionSound == null){
			fusionSound = new FusionRunningSound(this);
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(fusionSound);
		}
		if(fusionSound !=null && !isCrafting.getValue()){
			fusionSound = null;
		}
	}*/

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean canExtract(int slot, int amount){
		return !isCrafting();
	}
	
	public void startCrafting(@Nullable EntityPlayer player){
		pedestalSearch();
		runningRecipe = CrystalModAPI.findFusionRecipe(this, getWorld(), linkedPedestals);
		if(runningRecipe !=null && runningRecipe.matches(this, getWorld(), linkedPedestals)){
			String message = runningRecipe.canCraft(this, getWorld(), linkedPedestals);
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
		linkedPedestals.clear();
		//getWorld().playSound(null, getPos(), ModSounds.fusionCooldown, SoundCategory.BLOCKS, 0.75F, 1.0F);
	}
	
	public void pedestalSearch(){
		if(isCrafting.getValue()){
			return;
		}
		
		linkedPedestals.clear();
		int range = 8;
		
		List<BlockPos> searchList = Lists.newArrayList();
		EnumFacing facing = getRotation();
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
		
		for(BlockPos searchPos : searchList){
			if(getWorld().isBlockLoaded(searchPos)){
				TileEntity tile = getWorld().getTileEntity(searchPos);
				if(tile instanceof TileEntityPedestal){
					TileEntityPedestal pedestal = (TileEntityPedestal)tile;
					BlockPos tilePos = pedestal.getPos();
					BlockPos facingPos = new BlockPos(tilePos.subtract(getPos()));
					double distance = tilePos.distanceSq(getPos());
					EnumFacing dir = EnumFacing.getFacingFromVector(facingPos.getX(), facingPos.getY(), facingPos.getZ());
					boolean inLine = false;
					if(facing.getAxis() == Axis.X){
						inLine = tile.getPos().getX() == getPos().getX();
					}
					if(facing.getAxis() == Axis.Y){
						inLine = tile.getPos().getY() == getPos().getY();
					}
					if(facing.getAxis() == Axis.Z){
						inLine = tile.getPos().getZ() == getPos().getZ();
					}
					if(distance >= 2.0D && (dir != pedestal.getRotation() && (inLine ? facing.getOpposite() !=pedestal.getRotation() : true))){
						linkedPedestals.add(pedestal);
					}
				}
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("StackSync")){
			this.setStack(ItemStackTools.loadFromNBT(messageData));
		}
		if(messageId.equalsIgnoreCase("isCrafting")){
			isCrafting.setLastValue(isCrafting.getValue());
			isCrafting.setValue(messageData.getBoolean("Value"));
		}
		if(messageId.equalsIgnoreCase("Progress")){
			craftingProgress.setLastValue(craftingProgress.getValue());
			craftingProgress.setValue(messageData.getInt("Value"));
		}
		if(messageId.equalsIgnoreCase("Cooldown")){
			craftingCooldown.setLastValue(craftingCooldown.getValue());
			craftingCooldown.setValue(messageData.getInt("Value"));
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
	public boolean isCrafting(){
		return craftingCooldown.getValue() > 0 || isCrafting.getValue();
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		return ItemStackTools.isEmpty(getStack());
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return ItemStackTools.isEmpty(getStack());
	}
	
}
