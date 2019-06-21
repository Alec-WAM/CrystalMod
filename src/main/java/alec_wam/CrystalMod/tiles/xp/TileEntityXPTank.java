package alec_wam.CrystalMod.tiles.xp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.gui.overlay.IOvelayTile;
import alec_wam.CrystalMod.client.gui.overlay.InfoProviderTank;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.XPUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityXPTank extends TileEntityMod implements IMessageHandler, INBTDrop, IOvelayTile {
	
	public static int maxLevels = 60;
	public ExperienceContainer xpCon;
	private final LazyOptional<IFluidHandler> holder;
	private final InfoProvider info;
	
	public TileEntityXPTank(){
		super(ModBlocks.TILE_XP_TANK);
		xpCon = new ExperienceContainer(XPUtil.getExperienceForLevel(maxLevels));
	    this.info = new InfoProviderTank(xpCon);
		this.holder = LazyOptional.of(() -> xpCon);
	}
	
	public void changeXP(PlayerEntity player, int amount, boolean add){
		if(add){
			int realAmount = Math.min(amount, XPUtil.getPlayerXP(player));
			int added = xpCon.addExperience(realAmount);
			if(added > 0)XPUtil.addPlayerXP(player, -realAmount);
		} else {
			xpCon.givePlayerXp(player, amount);
		}
	}
	
	public static int getXPFromStack(ItemStack stack){
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
    	if(nbt.contains("XPStorage")){
    		return nbt.getCompound("XPStorage").getInt("experienceLevel");
    	}
		return 0;
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		nbt.put("XPStorage", xpCon.writeToNBT(new CompoundNBT()));
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		xpCon.readFromNBT(nbt.getCompound("XPStorage"));
	}
	
	@Override
	public void tick(){
		super.tick();
		if(getBlockState().get(BlockXPTank.ENDER)){
			if(!getWorld().isBlockPowered(getPos())){
				TileEntityXPVacuum.vacuumXP(getWorld(), getPos(), xpCon, 8, 4.5, 1.5);
			}
		}
		
		if (getWorld().isRemote) return;
		if(shouldDoWorkThisTick(10)){
			if(xpCon.isDirty()){
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("XP", xpCon.getExperienceTotal());
				PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateXP", nbt);
				CrystalModNetwork.sendToAllAround(packet, this);
				xpCon.setDirty(false);
			}
		}
	}
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }
    
    @Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateXP")){
			if(messageData.contains("XP")){
				xpCon.setExperience(messageData.getInt("XP"));
			}
		}
	}

	@Override
	public void writeToItemNBT(ItemStack stack) {
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
		nbt.put("XPStorage", xpCon.writeToNBT(new CompoundNBT()));
		if(getBlockState().get(BlockXPTank.ENDER)){
			nbt.putBoolean("IsEnder", true);
		}
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
		xpCon.readFromNBT(nbt.getCompound("XPStorage"));
	}

	@Override
	public InfoProvider getInfo() {
		return info;
	}
}
