package alec_wam.CrystalMod.tiles.pipes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.RedstoneMode;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeHitData;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkBuilder.PipeChecker;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityPipeBase extends TileEntityMod implements IMessageHandler {

	private NetworkPos networkPos;
	protected PipeNetworkBase<?> network;

	protected boolean serverDirty;
	protected boolean rebuildConnections;
	private PipeConnectionMode[] connectionSettings;
	private RedstoneMode[] redstoneSettings;
	
	protected final Set<EnumFacing> pipeConnections;
	protected final Set<EnumFacing> externalConnections;
	
	public TileEntityPipeBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		serverDirty = true;
		rebuildConnections = true;
		connectionSettings = new PipeConnectionMode[6];
		Arrays.fill(connectionSettings, PipeConnectionMode.OUT);
		redstoneSettings = new RedstoneMode[6];
		Arrays.fill(redstoneSettings, RedstoneMode.ON);
		pipeConnections = EnumSet.noneOf(EnumFacing.class);
		externalConnections = EnumSet.noneOf(EnumFacing.class);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		NBTTagCompound connectionSettingData = new NBTTagCompound();
		for(EnumFacing facing : EnumFacing.values()){
			int i = facing.getIndex();
			connectionSettingData.setByte(facing.getName(), (byte)connectionSettings[i].ordinal());
		}
		nbt.setTag("ConnectionSettings", connectionSettingData);
		
		int[] redstone = new int[6];
		for (int i = 0; i < 6; i++) {
			redstone[i] = redstoneSettings[i].ordinal();
		}
		nbt.setIntArray("RedstoneSettings", redstone);
		
		int[] dirs = new int[pipeConnections.size()];
		Iterator<EnumFacing> cons = pipeConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbt.setIntArray("Connections", dirs);

		dirs = new int[externalConnections.size()];
		cons = externalConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbt.setIntArray("ExternalConnections", dirs);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		NBTTagCompound connectionSettingData = nbt.getCompound("ConnectionSettings");
		for(EnumFacing facing : EnumFacing.values()){
			byte mode = connectionSettingData.getByte(facing.getName());
			int i = facing.getIndex();
			connectionSettings[i] = PipeConnectionMode.values()[mode];
		}		
		
		redstoneSettings = new RedstoneMode[6];
		Arrays.fill(redstoneSettings, RedstoneMode.ON);
		int[] redstone = nbt.getIntArray("RedstoneSettings");
		for (int i = 0; i < 6; i++) {
			redstoneSettings[i] = RedstoneMode.values()[redstone[i]];
		}
		
		pipeConnections.clear();
		int[] dirs = nbt.getIntArray("Connections");
		for (int i = 0; i < dirs.length; i++) {
			pipeConnections.add(EnumFacing.byIndex(dirs[i]));
		}

		externalConnections.clear();
		dirs = nbt.getIntArray("ExternalConnections");
		for (int i = 0; i < dirs.length; i++) {
			externalConnections.add(EnumFacing.byIndex(dirs[i]));
		}
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client){
		if(messageId.equalsIgnoreCase("Connections")){
			pipeConnections.clear();
			int[] dirs = messageData.getIntArray("Connections");
			for (int i = 0; i < dirs.length; i++) {
				pipeConnections.add(EnumFacing.byIndex(dirs[i]));
			}

			externalConnections.clear();
			dirs = messageData.getIntArray("ExternalConnections");
			for (int i = 0; i < dirs.length; i++) {
				externalConnections.add(EnumFacing.byIndex(dirs[i]));
			}
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		
		if(messageId.equalsIgnoreCase("IO.Client")){
			EnumFacing facing = EnumFacing.byIndex(messageData.getInt("Facing"));
			PipeConnectionMode mode = PipeConnectionMode.values()[messageData.getInt("Mode")];
			setConnectionSetting(facing, mode);
		}
		if(messageId.equalsIgnoreCase("Redstone.Client")){
			EnumFacing facing = EnumFacing.byIndex(messageData.getInt("Facing"));
			RedstoneMode mode = RedstoneMode.values()[messageData.getInt("Mode")];
			setRedstoneSetting(facing, mode);
		}
	}
	
	public abstract NetworkType getNetworkType();
	
	public abstract PipeNetworkBase<?> createNewNetwork();
	
	@Override
	public void remove(){
		super.remove();
		if(network !=null){
			network.resetNetwork();
		}
	}
	
	@Override
	public void tick(){
		super.tick();
		if (!getWorld().isRemote) {
			if (getNetwork() == null && getWorld().isBlockLoaded(getPos())) {
				PipeNetworkBuilder.buildNetwork(this, getPipeChecker());
			}
			
			if(rebuildConnections){
				updateConnections();
				rebuildConnections = false;
			}
			
			if(serverDirty){
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				markDirty();
				serverDirty = false;
			}
		}
	}
	
	public PipeChecker getPipeChecker(){
		return PipeNetworkBuilder.NORMAL;
	}
	
	public boolean isRebuildingConnections(){
		return rebuildConnections;
	}
	
	public PipeConnectionMode getConnectionSetting(EnumFacing facing){
		int i = facing.getIndex();
		return connectionSettings[i];
	}
	
	public void setConnectionSetting(EnumFacing facing, PipeConnectionMode mode){
		int i = facing.getIndex();
		connectionSettings[i] = mode;
		rebuildConnections = true;
		serverDirty = true;
	}
	
	public void incrsConnectionMode(EnumFacing facing){
		int i = facing.getIndex();
		final PipeConnectionMode mode = connectionSettings[i];
		if(mode == PipeConnectionMode.IN){
			connectionSettings[i] = PipeConnectionMode.OUT;
		}
		if(mode == PipeConnectionMode.OUT){
			connectionSettings[i] = PipeConnectionMode.BOTH;
		}
		if(mode == PipeConnectionMode.BOTH){
			connectionSettings[i] = PipeConnectionMode.IN;
		}
		//connectionSettings[i] = PipeConnectionMode.values()[(mode.ordinal() + 1) % (PipeConnectionMode.values().length)];*/
		rebuildConnections = true;
		serverDirty = true;
	}
	
	public boolean isConnectedTo(EnumFacing facing) {
		return pipeConnections.contains(facing);
	}

	public void externalConnectionAdded(EnumFacing fromDirection) {
		externalConnections.add(fromDirection);
	}

	public void externalConnectionRemoved(EnumFacing fromDirection) {
		externalConnections.remove(fromDirection);
	}
	
	public boolean canConnectToExternal(EnumFacing facing, boolean ignoreConnectionMode) {
		return false;
	}
	
	public boolean hasExternalConnection(EnumFacing facing) {
		return externalConnections.contains(facing);
	}
	
	private void updateConnections() {
		pipeConnections.clear();
		for(EnumFacing facing : EnumFacing.values()){
			if(getConnectionSetting(facing) == PipeConnectionMode.DISABLED) continue;
			BlockPos otherPos = getPos().offset(facing);
			TileEntity tile = getWorld().getTileEntity(otherPos);
			if(tile instanceof TileEntityPipeBase){
				TileEntityPipeBase pipe = (TileEntityPipeBase)tile;
				if(pipe.getNetworkType() == getNetworkType() && getPipeChecker().canConnect(pipe)){
					if(pipe.getConnectionSetting(facing.getOpposite()) != PipeConnectionMode.DISABLED){
						pipeConnections.add(facing);
					}
				}
			}
		}
		//TODO Look into cobblegen sticking around
		for(EnumFacing facing : externalConnections){
			if(getConnectionSetting(facing) == PipeConnectionMode.DISABLED){
				externalConnectionRemoved(facing);
			}
			else if(!canConnectToExternal(facing, false)){
				externalConnectionRemoved(facing);
			}
		}
		
		for(EnumFacing facing : EnumFacing.values()){
			if(getConnectionSetting(facing) == PipeConnectionMode.DISABLED) continue;
			if(!isConnectedTo(facing) && canConnectToExternal(facing, false)){
				externalConnectionAdded(facing);
			}
		}
		
		NBTTagCompound connectionData = new NBTTagCompound();		
		int[] dirs = new int[pipeConnections.size()];
		Iterator<EnumFacing> cons = pipeConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		connectionData.setIntArray("Connections", dirs);

		dirs = new int[externalConnections.size()];
		cons = externalConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		connectionData.setIntArray("ExternalConnections", dirs);
		
		CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "Connections", connectionData), this);
	}

	public Set<EnumFacing> getPipeConnections() {
		return pipeConnections;
	}
	
	public Set<EnumFacing> getExternalConnections() {
		return externalConnections;
	}
	
	//Redstone
	public RedstoneMode getRedstoneSetting(EnumFacing facing){
		int i = facing.getIndex();
		return redstoneSettings[i];
	}
	
	public void setRedstoneSetting(EnumFacing facing, RedstoneMode mode){
		int i = facing.getIndex();
		redstoneSettings[i] = mode;
	}
	
	public void incrsRedstoneMode(EnumFacing facing){
		int i = facing.getIndex();
		final RedstoneMode mode = redstoneSettings[i];
		redstoneSettings[i] = RedstoneMode.values()[(mode.ordinal() + 1) % (RedstoneMode.values().length)];
	}	
	
	public void setNetwork(PipeNetworkBase<?> network){
		this.network = network;
	}
	
	public PipeNetworkBase<?> getNetwork(){
		return network;
	}
	
	/**
	 * Get the Dimension sensitive position of the pipe
	 * @return NetworkPos of pipe
	 */
	public NetworkPos getNetworkPos(){
		if(!hasWorld()){
			return null;
		}
		if(networkPos == null){
			networkPos = new NetworkPos(getPos(), getWorld().dimension.getType());
		}
		return networkPos;
	}

	public boolean openConnector(EntityPlayer player, EnumHand hand, EnumFacing side) {
		return false;
	}

	public List<ItemStack> getDrops() {
		return Lists.newArrayList();
	}

	public boolean onActivated(World world, EntityPlayer player, EnumHand hand, PipeHitData hitData) {
		return false;
	}

	public boolean canConnectToPipe(TileEntityPipeBase otherPipe) {
		return true;
	}
	
}
