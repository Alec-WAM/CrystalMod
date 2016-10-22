package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.MessageTileContainerUpdate;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.ISynchronizedContainer;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.ViewType;
import alec_wam.CrystalMod.tiles.pipes.estorage.INetworkTile;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetworkClient.SortType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.TileEntityPanelMonitor;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class TileEntityPanel extends TileEntityMod implements IMessageHandler, INetworkTile {

	public EStorageNetwork network;
	public EnumFacing connectionDir;
	public String searchBarText;
	public SortType sortType = SortType.NAME;
	public ViewType viewType = ViewType.BOTH;
	public boolean jeiSync = false;
	
	public EnumFacing facing = EnumFacing.NORTH;
	public boolean connected;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(connectionDir !=null){
			nbt.setInteger("ConDir", connectionDir.ordinal());
		}
		if(searchBarText !=null){
			nbt.setString("SearchBar", searchBarText);
		}
		if(sortType !=null){
			nbt.setByte("SortType", (byte) sortType.ordinal());
		}
		if(viewType !=null){
			nbt.setByte("ViewType", (byte) viewType.ordinal());
		}
		if(facing !=null){
			nbt.setInteger("Facing", facing.ordinal());
		}
		nbt.setBoolean("Connected", connected);
		nbt.setBoolean("JEISync", jeiSync);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("ConDir")){
			connectionDir = EnumFacing.getFront(nbt.getInteger("ConDir"));
		}
		searchBarText = "";
		if(nbt.hasKey("SearchBar")){
			searchBarText = nbt.getString("SearchBar");
		}
		sortType = SortType.NAME;
		if(nbt.hasKey("SortType")){
			sortType = SortType.values()[nbt.getByte("SortType")];
		}
		viewType = ViewType.BOTH;
		if(nbt.hasKey("ViewType")){
			viewType = ViewType.values()[nbt.getByte("ViewType")];
		}
		if(nbt.hasKey("Facing")){
			facing = EnumFacing.getFront(nbt.getInteger("Facing"));
		}else{
			facing = EnumFacing.NORTH;
		}
		connected = nbt.getBoolean("Connected");
		this.jeiSync = nbt.getBoolean("JEISync");
		this.updateAfterLoad();
	}
	
	public void setSort(SortType newType){
		this.sortType = newType;
		if(this.worldObj !=null && this.worldObj.isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("SortType", newType.name());
			CrystalModNetwork.sendToServer(new PacketTileMessage(getPos(), "Sort", nbt));
		}else{
			markDirty();
		}
	}
	
	public void setView(ViewType newType){
		this.viewType = newType;
		if(this.worldObj !=null && this.worldObj.isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("ViewType", newType.name());
			CrystalModNetwork.sendToServer(new PacketTileMessage(getPos(), "View", nbt));
		}else{
			markDirty();
		}
	}
	
	public void setSearchBar(String text){
		this.searchBarText = text;
		if(this.worldObj !=null && this.worldObj.isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("SearchBar", text);
			CrystalModNetwork.sendToServer(new PacketTileMessage(getPos(), "Search", nbt));
		}else{
			markDirty();
		}
	}
	
	public void setJEISync(boolean mode){
		this.jeiSync = mode;
		if(this.worldObj !=null && this.worldObj.isRemote){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("JEISync", mode);
			CrystalModNetwork.sendToServer(new PacketTileMessage(getPos(), "JEISync", nbt));
		}else{
			markDirty();
		}
	}
	
	@Override
	public void update() {
		super.update();
		if(!getWorld().isRemote){
			if(this.connected == true && this.network == null){
				this.connected = false;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Connected", false);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "Connected", nbt), this);
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
			if(this.connected == false && this.network != null){
				this.connected = true;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("Connected", true);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(this.getPos(), "Connected", nbt), this);
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
			}
            if (this instanceof TileEntityPanelMonitor) {
                for (EntityPlayer player : worldObj.playerEntities) {
                    if (((ISynchronizedContainer) this).getContainer() == player.openContainer.getClass()) {
                        CrystalModNetwork.sendTo(new MessageTileContainerUpdate(this), (EntityPlayerMP) player);
                    }
                }
            }
		}
		
		
		if(network == null){
			if(getWorld().isRemote){
				network = new EStorageNetworkClient();
				return;
			}
			
			if(connectionDir !=null){
				TileEntity tile = getWorld().getTileEntity(getPos().offset(connectionDir));
				if(tile !=null && tile instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)tile;
					if(pipe.getConnectionMode(connectionDir.getOpposite()) == ConnectionMode.INPUT){
						if(pipe.network !=null && pipe.network instanceof EStorageNetwork){
							network = (EStorageNetwork) pipe.network;
						}
					}
					if(network !=null){
						return;
					}
				}
			}
			for(EnumFacing dir : EnumFacing.VALUES){
				TileEntity tile = getWorld().getTileEntity(getPos().offset(dir));
				if(tile !=null && tile instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage pipe = (TileEntityPipeEStorage)tile;
					if(pipe.getConnectionMode(dir.getOpposite()) == ConnectionMode.INPUT){
						if(pipe.network !=null && pipe.network instanceof EStorageNetwork){
							network = (EStorageNetwork) pipe.network;
							connectionDir = dir;
						}
					}
					if(network !=null){
						break;
					}
				}
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("Sort")){
			for(SortType type : SortType.values()){
				if(type.name().equalsIgnoreCase(messageData.getString("SortType"))){
					sortType = type;
					return;
				}
			}
		}
		if(messageId.equalsIgnoreCase("View")){
			for(ViewType type : ViewType.values()){
				if(type.name().equalsIgnoreCase(messageData.getString("ViewType"))){
					viewType = type;
					return;
				}
			}
		}
		if(messageId.equalsIgnoreCase("Search")){
			this.searchBarText = messageData.getString("SerchBar");
		}
		if(messageId.equalsIgnoreCase("ResetNet")){
			this.network = null;
		}
		if(messageId.equalsIgnoreCase("Connected")){
			if(messageData.getBoolean("Connected")){
				connected = true;
			}else {
				connected = false;
			}
		}
		if(messageId.equalsIgnoreCase("JEISync")){
			if(messageData.getBoolean("JEISync")){
				jeiSync = true;
			}else {
				jeiSync = false;
			}
		}
	}
	
	@Override
	public void setNetwork(EStorageNetwork network) {
		this.network = network;
		if(network == null){
			if(getWorld() !=null && !getWorld().isRemote){
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "ResetNet"), this);
			}
		}
	}

	@Override
	public EStorageNetwork getNetwork() {
		return network;
	}

	@Override
	public void onDisconnected() {}

	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side) {
		if(!player.isSneaking()){
			player.openGui(CrystalMod.instance, 0, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
			return true;
		}
		return false;
	}

}
