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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityPipeBase extends TileEntityMod implements IMessageHandler {

	private NetworkPos networkPos;
	protected PipeNetworkBase<?> network;

	protected boolean serverDirty;
	protected boolean rebuildConnections;
	private PipeConnectionMode[] connectionSettings;
	private RedstoneMode[] redstoneSettings;
	
	protected final Set<Direction> pipeConnections;
	protected final Set<Direction> externalConnections;
	
	public TileEntityPipeBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		serverDirty = true;
		rebuildConnections = true;
		connectionSettings = new PipeConnectionMode[6];
		Arrays.fill(connectionSettings, PipeConnectionMode.OUT);
		redstoneSettings = new RedstoneMode[6];
		Arrays.fill(redstoneSettings, RedstoneMode.ON);
		pipeConnections = EnumSet.noneOf(Direction.class);
		externalConnections = EnumSet.noneOf(Direction.class);
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		CompoundNBT connectionSettingData = new CompoundNBT();
		for(Direction facing : Direction.values()){
			int i = facing.getIndex();
			connectionSettingData.putByte(facing.getName(), (byte)connectionSettings[i].ordinal());
		}
		nbt.put("ConnectionSettings", connectionSettingData);
		
		int[] redstone = new int[6];
		for (int i = 0; i < 6; i++) {
			redstone[i] = redstoneSettings[i].ordinal();
		}
		nbt.putIntArray("RedstoneSettings", redstone);
		
		int[] dirs = new int[pipeConnections.size()];
		Iterator<Direction> cons = pipeConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbt.putIntArray("Connections", dirs);

		dirs = new int[externalConnections.size()];
		cons = externalConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbt.putIntArray("ExternalConnections", dirs);
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		CompoundNBT connectionSettingData = nbt.getCompound("ConnectionSettings");
		for(Direction facing : Direction.values()){
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
			pipeConnections.add(Direction.byIndex(dirs[i]));
		}

		externalConnections.clear();
		dirs = nbt.getIntArray("ExternalConnections");
		for (int i = 0; i < dirs.length; i++) {
			externalConnections.add(Direction.byIndex(dirs[i]));
		}
	}
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client){
		if(messageId.equalsIgnoreCase("Connections")){
			pipeConnections.clear();
			int[] dirs = messageData.getIntArray("Connections");
			for (int i = 0; i < dirs.length; i++) {
				pipeConnections.add(Direction.byIndex(dirs[i]));
			}

			externalConnections.clear();
			dirs = messageData.getIntArray("ExternalConnections");
			for (int i = 0; i < dirs.length; i++) {
				externalConnections.add(Direction.byIndex(dirs[i]));
			}
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		
		if(messageId.equalsIgnoreCase("IO.Client")){
			Direction facing = Direction.byIndex(messageData.getInt("Facing"));
			PipeConnectionMode mode = PipeConnectionMode.values()[messageData.getInt("Mode")];
			setConnectionSetting(facing, mode);
		}
		if(messageId.equalsIgnoreCase("Redstone.Client")){
			Direction facing = Direction.byIndex(messageData.getInt("Facing"));
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
	
	public PipeConnectionMode getConnectionSetting(Direction facing){
		int i = facing.getIndex();
		return connectionSettings[i];
	}
	
	public void setConnectionSetting(Direction facing, PipeConnectionMode mode){
		int i = facing.getIndex();
		connectionSettings[i] = mode;
		rebuildConnections = true;
		serverDirty = true;
	}
	
	public void incrsConnectionMode(Direction facing){
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
	
	public boolean isConnectedTo(Direction facing) {
		return pipeConnections.contains(facing);
	}

	public void externalConnectionAdded(Direction fromDirection) {
		externalConnections.add(fromDirection);
	}

	public void externalConnectionRemoved(Direction fromDirection) {
		externalConnections.remove(fromDirection);
	}
	
	public boolean canConnectToExternal(Direction facing, boolean ignoreConnectionMode) {
		return false;
	}
	
	public boolean hasExternalConnection(Direction facing) {
		return externalConnections.contains(facing);
	}
	
	private void updateConnections() {
		pipeConnections.clear();
		for(Direction facing : Direction.values()){
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
		for(Direction facing : externalConnections){
			if(getConnectionSetting(facing) == PipeConnectionMode.DISABLED){
				externalConnectionRemoved(facing);
			}
			else if(!canConnectToExternal(facing, false)){
				externalConnectionRemoved(facing);
			}
		}
		
		for(Direction facing : Direction.values()){
			if(getConnectionSetting(facing) == PipeConnectionMode.DISABLED) continue;
			if(!isConnectedTo(facing) && canConnectToExternal(facing, false)){
				externalConnectionAdded(facing);
			}
		}
		
		CompoundNBT connectionData = new CompoundNBT();		
		int[] dirs = new int[pipeConnections.size()];
		Iterator<Direction> cons = pipeConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		connectionData.putIntArray("Connections", dirs);

		dirs = new int[externalConnections.size()];
		cons = externalConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		connectionData.putIntArray("ExternalConnections", dirs);
		
		CrystalModNetwork.sendToAllAround(new PacketTileMessage(pos, "Connections", connectionData), this);
	}

	public Set<Direction> getPipeConnections() {
		return pipeConnections;
	}
	
	public Set<Direction> getExternalConnections() {
		return externalConnections;
	}
	
	//Redstone
	public RedstoneMode getRedstoneSetting(Direction facing){
		int i = facing.getIndex();
		return redstoneSettings[i];
	}
	
	public void setRedstoneSetting(Direction facing, RedstoneMode mode){
		int i = facing.getIndex();
		redstoneSettings[i] = mode;
	}
	
	public void incrsRedstoneMode(Direction facing){
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

	public boolean openConnector(PlayerEntity player, Hand hand, Direction side) {
		return false;
	}

	public List<ItemStack> getDrops() {
		return Lists.newArrayList();
	}

	public boolean onActivated(World world, PlayerEntity player, Hand hand, PipeHitData hitData) {
		return false;
	}

	public boolean canConnectToPipe(TileEntityPipeBase otherPipe) {
		return true;
	}
	
}
