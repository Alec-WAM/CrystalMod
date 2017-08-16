package alec_wam.CrystalMod.tiles.pipes.estorage.security;

import java.util.UUID;

import alec_wam.CrystalMod.api.estorage.INetworkPowerTile;
import alec_wam.CrystalMod.api.estorage.INetworkTileConnectable;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.BasicItemHandler;
import alec_wam.CrystalMod.tiles.IItemValidator;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

public class TileSecurityController extends TileEntityMod implements INetworkPowerTile, INetworkTileConnectable {

	//The player that placed the controller
	public UUID owner;
	
	private BasicItemHandler cards = new BasicItemHandler(16, this, new IItemValidator() {
        @Override
        public boolean valid(ItemStack stack) {
        	return stack.getItem() instanceof ItemSecurityCard && ItemSecurityCard.isValid(stack);
        }
    }){
	    @Override
	    public void onContentsChanged(int slot) {
	        super.onContentsChanged(slot);
	        if(getNetwork() !=null){
	        	getNetwork().updateSecurity();
	        }
	    }
    };
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(owner !=null)nbt.setString("Owner", UUIDUtils.fromUUID(owner));
		nbt.setTag("Cards", cards.serializeNBT());
        
        nbt.setBoolean("Connected", connected);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("Owner")){
			owner = UUIDUtils.fromString(nbt.getString("Owner"));
		}
		cards.deserializeNBT(nbt.getCompoundTag("Cards"));
		connected = nbt.getBoolean("Connected");
	}
	
	@Override
	public void update(){
		super.update();
	}

	private EStorageNetwork network;
	public boolean connected;
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
		this.connected = network !=null;
		markDirty();
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {
		this.connected = false;
		markDirty();
	}

	@Override
	public int getEnergyUsage() {
		int usage = 8;
		return usage;
	}
	
	public final ItemStack displayStack = new ItemStack(ModBlocks.securityController);
	
	@Override
	public ItemStack getDisplayStack(){
		return displayStack;
	}

	public IItemHandler getCards() {
		return cards;
	}

	@Override
	public boolean canConnect(EStorageNetwork network) {
		if(owner !=null){
			return network.hasAbility(owner, NetworkAbility.SECURITY);
		}
		return true;
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) cards;
        return super.getCapability(capability, facing);
    }
}

