package alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil;
import alec_wam.CrystalMod.tiles.pipes.attachments.ItemPipeAttachment;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.power.rf.RFPowerPipeNetwork;
import alec_wam.CrystalMod.tiles.pipes.power.rf.TileEntityPipePowerRF;
import alec_wam.CrystalMod.tiles.pipes.types.IPipeType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.tool.ToolUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityPipe extends TileEntityMod implements ITickable, IMessageHandler {

	public static enum PipePart {
		ERROR, PIPE, CONNECTOR_SMALL, CONNECTOR_LARGE, COVER, ATTACHMENT;
	}
	
	public static enum RedstoneMode {
		NONE, IGNORE, OFF, ON;
		
		public boolean passes(World world, BlockPos pos){
			if(this == NONE)return false;
			if(this == IGNORE)return true;
			if(this == OFF)return !world.isBlockPowered(pos);
			return world.isBlockPowered(pos);
		}
		
		public static RedstoneMode getNextRedstoneMode(RedstoneMode oldMode) {
			  if(oldMode == null) {
				  oldMode = RedstoneMode.ON;
			  }
			  return oldMode == RedstoneMode.NONE ? RedstoneMode.ON : oldMode == RedstoneMode.ON ? RedstoneMode.OFF : oldMode == RedstoneMode.OFF ? RedstoneMode.IGNORE : RedstoneMode.NONE;
		}
		
		public RedstoneMode next(){
			int index = ordinal();
			index++;
			index%=values().length;
			return RedstoneMode.values()[index];
		}
		
		public RedstoneMode previous(){
			int index = ordinal();
			index--;
			if(index < 0){
				index = values().length-1;
			}
			return RedstoneMode.values()[index];
		}
	}

	public TileEntityPipe() {
		this.blockType = ModBlocks.crystalPipe;
	}

	public AbstractPipeNetwork network;

	public AbstractPipeNetwork getNetwork() {
		return network;
	}

	public boolean setNetwork(AbstractPipeNetwork abstractPipeNetwork) {
		network = abstractPipeNetwork;
		return true;
	}

	public abstract IPipeType getPipeType();

	public abstract AbstractPipeNetwork createNetwork();

	public boolean canConnectToPipe(EnumFacing faceHit, TileEntityPipe neighbour) {
		return neighbour.getPipeType().getClass() == getPipeType().getClass() && neighbour.getAttachmentData(faceHit.getOpposite()) == null;
	}

	protected boolean collidablesDirty = true;

	private boolean clientStateDirty = true;

	private boolean dodgyChangeSinceLastCallFlagForBundle = true;

	protected boolean connectionsDirty = true;
	
	public boolean getConnectionsDirty(){
		return this.connectionsDirty;
	}
	
	protected boolean clientDirty = false;
	protected boolean serverDirty = true;

	public void onChunkUnload() {
		super.onChunkUnload();
		if (this.network != null) {
			this.network.destroyNetwork();
		}
	}

	@Override
	public void update() {
		super.update();
		for(EnumFacing face : EnumFacing.VALUES){
			if(getAttachmentData(face) !=null){
				getAttachmentData(face).update(this, face);
			}
		}


		
		if (!getWorld().isRemote) {
			if (getNetwork() == null && getWorld().isBlockLoaded(getPos())) {
				PipeUtil.ensureValidNetwork(this);
			}
			
			updateConnections();

			if (clientStateDirty) {
				dirty();
				clientStateDirty = false;
			}
			
			if (this.serverDirty) {
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				markDirty();
				this.serverDirty = false;
			}
		}

		if (clientDirty && this.getWorld() != null && this.getWorld().isRemote) {
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
			clientDirty = false;
		}

	}

	private void updateConnections() {
		if (!connectionsDirty) {
			return;
		}

		boolean externalConnectionsChanged = false;
		List<EnumFacing> copy = new ArrayList<EnumFacing>(externalConnections);
		// remove any no longer valid connections
		for (EnumFacing dir : copy) {
			if (!canConnectToExternal(dir, false)) {
				externalConnectionRemoved(dir);
				externalConnectionsChanged = true;
			}
		}

		// then check for new ones
		for (EnumFacing dir : EnumFacing.VALUES) {
			if (!pipeConnections.contains(dir)
					&& !externalConnections.contains(dir)) {
				if (canConnectToExternal(dir, false)) {
					externalConnectionAdded(dir);
					externalConnectionsChanged = true;
				}
			}
		}
		if (externalConnectionsChanged) {
			connectionsChanged();
		}

		connectionsDirty = false;
	}

	public void dirty() {
		serverDirty = true;
		collidablesDirty = true;
	}

	public void connectionsChanged() {
		collidablesDirty = true;
		clientStateDirty = true;
		dodgyChangeSinceLastCallFlagForBundle = true;
	}

	public void onAdded() {

		World world = getWorld();

		pipeConnections.clear();
		for (EnumFacing dir : EnumFacing.VALUES) {
			TileEntityPipe neighbour = PipeUtil.getPipe(world, this, dir, getPipeType());
			if (neighbour != null && this.canConnectToPipe(dir, neighbour)) {
				this.pipeConnectionAdded(dir);
				neighbour.pipeConnectionAdded(dir.getOpposite());
				neighbour.connectionsChanged();
			}
		}

		externalConnections.clear();
		for (EnumFacing dir : EnumFacing.VALUES) {
			if (!containsPipeConnection(dir)
					&& canConnectToExternal(dir, false)) {
				externalConnectionAdded(dir);
			}
		}
		connectionsChanged();

		dirty();
	}

	public void onRemoved() {
		World world = getWorld();

		for (EnumFacing dir : pipeConnections) {
			TileEntityPipe neighbour = PipeUtil.getPipe(world, this, dir,
					getPipeType());
			if (neighbour != null) {
				neighbour.pipeConnectionRemoved(dir.getOpposite());
				neighbour.connectionsChanged();
			}
		}
		pipeConnections.clear();

		if (!externalConnections.isEmpty()) {
			world.notifyNeighborsOfStateChange(getPos(), ModBlocks.crystalPipe, true);
		}
		externalConnections.clear();

		AbstractPipeNetwork network = getNetwork();
		if (network != null) {
			network.destroyNetwork();
		}
		connectionsChanged();

		dirty();
	}

	public boolean onNeighborBlockChange(Block block) {

		// NB: No need to check externals if the neighbour that changed was a
		// conduit bundle as this
		// can't effect external connections.
		/*if (block == ModBlocks.crystalPipe) {
			return false;
		}*/

		// Check for changes to external connections, connections to conduits
		// are
		// handled by the bundle
		Set<EnumFacing> newCons = EnumSet.noneOf(EnumFacing.class);
		for (EnumFacing dir : EnumFacing.VALUES) {
			if (!containsPipeConnection(dir)
					&& canConnectToExternal(dir, false)) {
				newCons.add(dir);
			}
		}
		if (newCons.size() != externalConnections.size()) {
			connectionsDirty = true;
			dirty();
			return true;
		}
		for (EnumFacing dir : externalConnections) {
			if (!newCons.remove(dir)) {
				connectionsDirty = true;
				dirty();
				return true;
			}
		}
		if (!newCons.isEmpty()) {
			connectionsDirty = true;
			dirty();
			return true;
		}
		return false;
	}

	protected final Set<EnumFacing> pipeConnections = EnumSet
			.noneOf(EnumFacing.class);
	protected final Set<EnumFacing> externalConnections = EnumSet
			.noneOf(EnumFacing.class);
	protected final Map<EnumFacing, CoverData> covers = Maps.newHashMap();
	protected final Map<EnumFacing, AttachmentData> attachments = Maps.newHashMap();

	protected final EnumMap<EnumFacing, ConnectionMode> conectionModes = new EnumMap<EnumFacing, ConnectionMode>(
			EnumFacing.class);

	public Set<EnumFacing> getPipeConnections() {
		return pipeConnections;
	}

	public boolean containsPipeConnection(EnumFacing dir) {
		return pipeConnections.contains(dir);
	}

	public void pipeConnectionAdded(EnumFacing fromDirection) {
		pipeConnections.add(fromDirection);
	}

	public void pipeConnectionRemoved(EnumFacing fromDirection) {
		pipeConnections.remove(fromDirection);
	}

	public boolean hasConnectionMode(ConnectionMode mode) {
		if (mode == null) {
			return false;
		}
		if (mode == getDefaultConnectionMode() && conectionModes.size() != 6) {
			return true;
		}
		for (ConnectionMode cm : conectionModes.values()) {
			if (cm == mode) {
				return true;
			}
		}
		return false;
	}

	public ConnectionMode getConnectionMode(EnumFacing dir) {
		ConnectionMode res = conectionModes.get(dir);
		if (res == null) {
			return getDefaultConnectionMode();
		}
		return res;
	}

	protected ConnectionMode getDefaultConnectionMode() {
		return ConnectionMode.IN_OUT;
	}

	public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
		ConnectionMode oldVal = conectionModes.get(dir);
		if (oldVal == mode) {
			return;
		}
		
		if (mode == null) {
			conectionModes.remove(dir);
		} else {
			conectionModes.put(dir, mode);
		}
		clientStateDirty = true;
		collidablesDirty = true;

		connectionsChanged();
	}

	public ConnectionMode getNextConnectionMode(EnumFacing dir) {
		ConnectionMode curMode = getConnectionMode(dir);
		ConnectionMode next = ConnectionMode.getNext(curMode);
		if (next == ConnectionMode.NOT_SET) {
			next = ConnectionMode.IN_OUT;
		}
		return next;
	}

	public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
		ConnectionMode curMode = getConnectionMode(dir);
		ConnectionMode prev = ConnectionMode.getPrevious(curMode);
		if (prev == ConnectionMode.NOT_SET) {
			prev = ConnectionMode.DISABLED;
		}
		return prev;
	}

	public boolean canConnectToExternal(EnumFacing direction, boolean ignoreConnectionMode) {
		return false;
	}

	public Set<EnumFacing> getExternalConnections() {
		return externalConnections;
	}

	public boolean hasExternalConnections() {
		return !externalConnections.isEmpty();
	}

	public boolean hasConnections() {
		return hasPipeConnections() || hasExternalConnections();
	}

	public boolean hasPipeConnections() {
		return !pipeConnections.isEmpty();
	}

	public boolean containsExternalConnection(EnumFacing dir) {
		return externalConnections.contains(dir);
	}

	public void externalConnectionAdded(EnumFacing fromDirection) {
		externalConnections.add(fromDirection);
	}

	public void externalConnectionRemoved(EnumFacing fromDirection) {
		externalConnections.remove(fromDirection);
	}

	public boolean isConnectedTo(EnumFacing dir) {
		return containsPipeConnection(dir) || containsExternalConnection(dir);
	}

	public boolean setCover(EnumFacing dir, CoverData data) {
		this.covers.put(dir, data);
		clientStateDirty = true;
		return true;
	}

	public CoverData getCoverData(EnumFacing dir) {
		return dir == null ? null : covers.get(dir);
	}
	
	public Collection<CoverData> getCovers(){
		return covers.values();
	}
	
	public boolean setAttachment(EnumFacing dir, AttachmentData data) {
		this.attachments.put(dir, data);
		clientStateDirty = true;
		return true;
	}

	public AttachmentData getAttachmentData(EnumFacing dir) {
		return dir == null ? null : attachments.get(dir);
	}

	public void writeCustomNBT(NBTTagCompound nbtRoot) {
		super.writeCustomNBT(nbtRoot);
		int[] dirs = new int[pipeConnections.size()];
		Iterator<EnumFacing> cons = pipeConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbtRoot.setIntArray("connections", dirs);

		dirs = new int[externalConnections.size()];
		cons = externalConnections.iterator();
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = cons.next().getIndex();
		}
		nbtRoot.setIntArray("externalConnections", dirs);

		if (conectionModes.size() > 0) {
			byte[] modes = new byte[6];
			int i = 0;
			for (EnumFacing dir : EnumFacing.VALUES) {
				modes[i] = (byte) getConnectionMode(dir).ordinal();
				i++;
			}
			nbtRoot.setByteArray("conModes", modes);
		}
		if (covers.size() > 0) {
			NBTTagList coverList = new NBTTagList();
			for (EnumFacing dir : EnumFacing.VALUES) {
				if (this.covers.containsKey(dir)
						&& this.covers.get(dir) != null) {
					NBTTagCompound coverNBT = new NBTTagCompound();
					coverNBT.setByte("Face", (byte) dir.ordinal());
					this.covers.get(dir).writeToNBT(coverNBT);
					coverList.appendTag(coverNBT);
				}
			}
			nbtRoot.setTag("coverData", coverList);
		}
		if (attachments.size() > 0) {
			NBTTagList coverList = new NBTTagList();
			for (EnumFacing dir : EnumFacing.VALUES) {
				if (this.attachments.containsKey(dir)
						&& this.attachments.get(dir) != null) {
					NBTTagCompound coverNBT = new NBTTagCompound();
					coverNBT.setByte("Face", (byte) dir.ordinal());
					this.attachments.get(dir).writeToNBT(coverNBT);
					coverList.appendTag(coverNBT);
				}
			}
			nbtRoot.setTag("attachmentData", coverList);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbtRoot) {
		super.readCustomNBT(nbtRoot);
		pipeConnections.clear();
		cachedCollidables.clear();

		int[] dirs = nbtRoot.getIntArray("connections");
		for (int i = 0; i < dirs.length; i++) {
			pipeConnections.add(EnumFacing.getFront(dirs[i]));
		}

		externalConnections.clear();
		dirs = nbtRoot.getIntArray("externalConnections");
		for (int i = 0; i < dirs.length; i++) {
			externalConnections.add(EnumFacing.values()[dirs[i]]);
		}

		conectionModes.clear();
		byte[] modes = nbtRoot.getByteArray("conModes");
		if (modes != null && modes.length == 6) {
			int i = 0;
			for (EnumFacing dir : EnumFacing.VALUES) {
				conectionModes.put(dir, ConnectionMode.values()[modes[i]]);
				i++;
			}
		}
		covers.clear();
		NBTTagList coverList = nbtRoot.getTagList("coverData", 10);
		if (coverList != null) {
			for (int c = 0; c < coverList.tagCount(); c++) {
				NBTTagCompound coverNBT = coverList.getCompoundTagAt(c);
				EnumFacing dir = EnumFacing.getFront(coverNBT.getByte("Face"));
				CoverData cover = CoverData.readFromNBT(coverNBT);
				if (cover != null && dir != null) {
					covers.put(dir, cover);
				}
			}
		}
		
		attachments.clear();
		NBTTagList attachList = nbtRoot.getTagList("attachmentData", 10);
		if (attachList != null) {
			for (int c = 0; c < attachList.tagCount(); c++) {
				NBTTagCompound coverNBT = attachList.getCompoundTagAt(c);
				EnumFacing dir = EnumFacing.getFront(coverNBT.getByte("Face"));
				AttachmentData cover = AttachmentData.readFromNBT(coverNBT);
				if (cover != null && dir != null) {
					attachments.put(dir, cover);
				}
			}
		}

		updateAfterLoad();

	}

	public boolean onActivated(World world, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, Vec3d hitVec) {

		RaytraceResult closest = getClosest(player);

		boolean hasWrench = ToolUtil.isToolEquipped(player, hand);
		
		if (closest != null) {
			if (closest.component != null) {
				if (closest.component.dir != null) {
					EnumFacing dir = closest.component.dir;
					if (closest.component.data instanceof PipePart) {
						PipePart part = (PipePart) closest.component.data;
						if (part == PipePart.CONNECTOR_SMALL
								&& this.containsExternalConnection(dir)
								&& hasWrench) {
							ConnectionMode mode = getNextConnectionMode(dir);
							setConnectionMode(dir, mode);
							return true;
						}

						if (part == PipePart.ATTACHMENT) {
							if(hasWrench && player.isSneaking()){
								final AttachmentData data = this.getAttachmentData(dir);
								this.setAttachment(dir, null);
								if(!player.capabilities.isCreativeMode){
									ItemStack attach = new ItemStack(ModItems.pipeAttachmant);
									ItemNBTHelper.setString(attach, "ID", data.getID());
									ItemUtil.spawnItemInWorldWithoutMotion(getWorld(), attach, getPos());
								}
								SoundType soundtype = SoundType.METAL;
				                getWorld().playSound((EntityPlayer)null, getPos(), soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								return true;
							}
							if(player.getEntityWorld().isRemote)return true;
							player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_TE_FACING+dir.getIndex(),
									player.getEntityWorld(), getPos().getX(), getPos()
											.getY(), getPos().getZ());
							return true;
						}
						
						if (part == PipePart.CONNECTOR_LARGE) {
							if(player.getEntityWorld().isRemote)return true;
							player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_TE_FACING+dir.getIndex(),
									player.getEntityWorld(), getPos().getX(), getPos()
											.getY(), getPos().getZ());
							return true;
						}

						if (part == PipePart.PIPE) {
							if (containsPipeConnection(dir) && hasWrench) {
								PipeUtil.disconectPipes(this, dir);
								return true;
							}
						}
						if(part == PipePart.COVER){
							if(hasWrench){
								final CoverData coverData = this.getCoverData(dir);
								this.setCover(dir, null);
								SoundType soundtype = coverData.getBlockState().getBlock().getSoundType(coverData.getBlockState(), getWorld(), getPos(), player);
				                getWorld().playSound((EntityPlayer)null, getPos(), soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								
								if (!player.capabilities.isCreativeMode) {
									ItemStack cover = ItemPipeCover.getCover(coverData);
									if (ItemStackTools.isValid(cover)) {
										ItemUtil.spawnItemInWorldWithoutMotion(getWorld(), cover, getPos());
									}
								}
								return true;
							} else {
								CoverData coverData = this.getCoverData(dir);
								World worldWrapped = new PipeWorldWrapper(world, getPos(), dir);
								if(coverData.getBlockState().getBlock().onBlockActivated(worldWrapped, getPos(), coverData.getBlockState(), player, hand, dir, (float)hitVec.xCoord, (float)hitVec.yCoord, (float)hitVec.zCoord)){
									return true;
								}
							}
						}
					}
				} else {
					if (hasWrench && !player.isSneaking()) {
						if (getConnectionMode(side) == ConnectionMode.DISABLED) {
							setConnectionMode(side, getNextConnectionMode(side));
							return true;
						}
						// Attempt to join networks
						return PipeUtil.joinPipes(this, side);
					}
				}
			}
		}
		
		if(hasWrench && player.isSneaking()){
    		return ToolUtil.breakBlockWithTool(getBlockType(), getWorld(), getPos(), player, hand);
    	}
		
		ItemStack handItem = player.getHeldItem(hand);
		
		if (ItemStackTools.isValid(handItem)
				&& handItem.getItem() instanceof ItemPipeCover) {
			CoverData data = ItemPipeCover.getCoverData(handItem);
			EnumFacing coverDir = side;
			if (closest != null) {
				if (closest.component != null && closest.component.dir != null) {
					Object compData = closest.component.data;
					if (compData != null && compData instanceof PipePart) {
						if (((PipePart) compData) == PipePart.CONNECTOR_LARGE || ((PipePart) compData) == PipePart.COVER || (player.isSneaking() && ((PipePart) compData) == PipePart.PIPE)) {
							coverDir = closest.component.dir;
						}
					}
				}
			}
			if (this.getCoverData(coverDir) != null
					&& this.getCoverData(coverDir).equals(data))
				return false;
			if (this.getCoverData(coverDir) != null
					&& !this.getCoverData(coverDir).equals(data)) {
				final CoverData coverData = this.getCoverData(coverDir);
				this.setCover(coverDir, null);
				if (!player.capabilities.isCreativeMode) {
					ItemStack cover = ItemPipeCover.getCover(coverData);
					if (cover != null) {
						EntityItem ent = new EntityItem(getWorld(), getPos()
								.getX() + 0.5, getPos().getY() + 0.5, getPos()
								.getZ() + 0.5, cover);
						if (!getWorld().isRemote) {
							getWorld().spawnEntity(ent);
						}
					}
				}
				this.setCover(coverDir, data);

				SoundType soundtype = data.getBlockState().getBlock().getSoundType(data.getBlockState(), getWorld(), getPos(), player);
                getWorld().playSound((EntityPlayer)null, getPos(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				
				wrapBlockAdded(coverDir);
				if(!player.capabilities.isCreativeMode){
					player.setHeldItem(hand, ItemUtil.consumeItem(player.getHeldItem(hand)));
				}
				return true;
			}
			this.setCover(coverDir, data);
			SoundType soundtype = data.getBlockState().getBlock().getSoundType(data.getBlockState(), getWorld(), getPos(), player);
            getWorld().playSound((EntityPlayer)null, getPos(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			wrapBlockAdded(coverDir);
			if(!player.capabilities.isCreativeMode){
				player.setHeldItem(hand, ItemUtil.consumeItem(player.getHeldItem(hand)));
			}
			return true;
		}
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held) && held.getItem() == Items.STICK){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
			return true;
		}
		
		if(ItemStackTools.isValid(held) && held.getItem() == ModItems.pipeAttachmant){
			AttachmentData data = AttachmentUtil.getFromID(ItemPipeAttachment.getID(held));
			if(data !=null && data.isPipeValid(this, side, held)){
				this.setAttachment(side, data);
				if(!player.capabilities.isCreativeMode){
					player.setHeldItem(hand, ItemUtil.consumeItem(player.getHeldItem(hand)));
				}
				SoundType soundtype = SoundType.METAL;
                getWorld().playSound((EntityPlayer)null, getPos(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				BlockUtil.markBlockForUpdate(getWorld(), getPos());
				return true;
			}
		}
		
		if(this instanceof TileEntityPipePowerRF){
		TileEntityPipePowerRF pipe = (TileEntityPipePowerRF)this;
		if(pipe.getNetwork() !=null && pipe.getNetwork() instanceof RFPowerPipeNetwork){
			RFPowerPipeNetwork network = (RFPowerPipeNetwork)pipe.getNetwork();
			if(network.getPowerManager() !=null){
				ChatUtil.sendNoSpam(player, network.getPowerManager().getPowerInConduits()+" / "+network.getPowerManager().getMaxPowerInConduits());
				return true;
			}
		}
		}
		
		return false;
	}

	public void wrapBlockAdded(EnumFacing face){
		CoverData data = getCoverData(face);
		if(data !=null && data.getBlockState() !=null && data.getBlockState().getBlock() !=null){
			Block block = data.getBlockState().getBlock();
			//block.onBlockPlaced(new PipeWorldWrapper(getWorld(), getPos(), face), getPos(), facing, hitX, hitY, hitZ, meta, placer);
			block.onBlockAdded(new PipeWorldWrapper(getWorld(), getPos(), face), getPos(), data.getBlockState());
		}
	}
	
	private final List<CollidableComponent> cachedCollidables = new ArrayList<CollidableComponent>();

	public List<CollidableComponent> getCollidableComponents() {

		if (this.dodgyChangeSinceLastCallFlagForBundle) {
			collidablesDirty = true;
			dodgyChangeSinceLastCallFlagForBundle = false;
		}

		if (!collidablesDirty && !cachedCollidables.isEmpty()) {
			return cachedCollidables;
		}
		cachedCollidables.clear();
		boolean disabledUP = this.getConnectionMode(EnumFacing.UP) == ConnectionMode.DISABLED;
		boolean disabledDOWN = this.getConnectionMode(EnumFacing.DOWN) == ConnectionMode.DISABLED;
		boolean disabledNORTH = this.getConnectionMode(EnumFacing.NORTH) == ConnectionMode.DISABLED;
		boolean disabledSOUTH = this.getConnectionMode(EnumFacing.SOUTH) == ConnectionMode.DISABLED;
		boolean disabledWEST = this.getConnectionMode(EnumFacing.WEST) == ConnectionMode.DISABLED;
		boolean disabledEAST = this.getConnectionMode(EnumFacing.EAST) == ConnectionMode.DISABLED;

		for (EnumFacing face : EnumFacing.VALUES) {
			float pixel = 1.0f / 16f;
			float min = /*0.314f*/pixel*5;
			float max = /*0.686f*/pixel*11;
			if (!disabledUP) {
				if (isConnectedTo(EnumFacing.UP))
					cachedCollidables.add(new CollidableComponent(
							new AxisAlignedBB(min, 0.75f, min, max, 1f,
									max), EnumFacing.UP, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.UP)) {
					AxisAlignedBB bbLargeUp = new AxisAlignedBB(pixel * 2.0f,
							pixel * 15.0f, pixel * 2.0f, pixel * 14.0f,
							pixel * 16.0f, pixel * 14.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeUp,
							EnumFacing.UP, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallUp = new AxisAlignedBB(pixel * 3.0f,
							pixel * 14.0f, pixel * 3.0f, pixel * 13.0f,
							pixel * 15.0f, pixel * 13.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallUp,
							EnumFacing.UP, PipePart.CONNECTOR_SMALL));
				}
			}

			if (!disabledDOWN) {
				if (isConnectedTo(EnumFacing.DOWN))
					cachedCollidables.add(new CollidableComponent(
							new AxisAlignedBB(min, 0, min, max, 0.25f,
									max), EnumFacing.DOWN, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.DOWN)) {
					AxisAlignedBB bbLargeDown = new AxisAlignedBB(pixel * 2.0f,
							pixel * 1.0f, pixel * 2.0f, pixel * 14.0f,
							pixel * 0.0f, pixel * 14.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeDown,
							EnumFacing.DOWN, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallDown = new AxisAlignedBB(pixel * 3.0f,
							pixel * 1.0f, pixel * 3.0f, pixel * 13.0f,
							pixel * 2.0f, pixel * 13.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallDown,
							EnumFacing.DOWN, PipePart.CONNECTOR_SMALL));
				}
			}

			if (!disabledNORTH) {
				if (isConnectedTo(EnumFacing.NORTH))
					cachedCollidables.add(new CollidableComponent(
							new AxisAlignedBB(min, min, 0.0, max, max,
									0.25f), EnumFacing.NORTH, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.NORTH)) {
					AxisAlignedBB bbLargeNorth = new AxisAlignedBB(
							pixel * 2.0f, pixel * 2.0f, pixel * 1f,
							pixel * 14.0f, pixel * 14.0f, pixel * 0.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeNorth,
							EnumFacing.NORTH, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallNorth = new AxisAlignedBB(
							pixel * 3.0f, pixel * 3.0f, pixel * 2f,
							pixel * 13.0f, pixel * 13.0f, pixel * 1.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallNorth,
							EnumFacing.NORTH, PipePart.CONNECTOR_SMALL));
				}
			}

			if (!disabledSOUTH) {
				if (isConnectedTo(EnumFacing.SOUTH))
					cachedCollidables
							.add(new CollidableComponent(new AxisAlignedBB(
									min, min, 0.75f, max, max, 1f),
									EnumFacing.SOUTH, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.SOUTH)) {
					AxisAlignedBB bbLargeSouth = new AxisAlignedBB(
							pixel * 2.0f, pixel * 2.0f, pixel * 15.0f,
							pixel * 14.0f, pixel * 14.0f, pixel * 16.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeSouth,
							EnumFacing.SOUTH, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallSouth = new AxisAlignedBB(
							pixel * 3.0f, pixel * 3.0f, pixel * 14f,
							pixel * 13.0f, pixel * 13.0f, pixel * 15.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallSouth,
							EnumFacing.SOUTH, PipePart.CONNECTOR_SMALL));
				}
			}

			if (!disabledWEST) {
				if (isConnectedTo(EnumFacing.WEST))
					cachedCollidables.add(new CollidableComponent(
							new AxisAlignedBB(0.0f, min, min, 0.25f, max,
									max), EnumFacing.WEST, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.WEST)) {
					AxisAlignedBB bbLargeWest = new AxisAlignedBB(pixel * 1.0f,
							pixel * 2.0f, pixel * 2.0f, pixel * 0.0f,
							pixel * 14.0f, pixel * 14.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeWest,
							EnumFacing.WEST, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallWest = new AxisAlignedBB(pixel * 2.0f,
							pixel * 3.0f, pixel * 3.0f, pixel * 1.0f,
							pixel * 13.0f, pixel * 13.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallWest,
							EnumFacing.WEST, PipePart.CONNECTOR_SMALL));
				}
			}

			if (!disabledEAST) {
				if (isConnectedTo(EnumFacing.EAST))
					cachedCollidables.add(new CollidableComponent(
							new AxisAlignedBB(0.75f, min, min, 1f, max,
									max), EnumFacing.EAST, PipePart.PIPE));
				if (containsExternalConnection(EnumFacing.EAST)) {
					AxisAlignedBB bbLargeEast = new AxisAlignedBB(
							pixel * 15.0f, pixel * 2.0f, pixel * 2.0f,
							pixel * 16.0f, pixel * 14.0f, pixel * 14.0f);
					cachedCollidables.add(new CollidableComponent(bbLargeEast,
							EnumFacing.EAST, PipePart.CONNECTOR_LARGE));
					AxisAlignedBB bbSmallEast = new AxisAlignedBB(
							pixel * 14.0f, pixel * 3.0f, pixel * 3.0f,
							pixel * 15.0f, pixel * 13.0f, pixel * 13.0f);
					cachedCollidables.add(new CollidableComponent(bbSmallEast,
							EnumFacing.EAST, PipePart.CONNECTOR_SMALL));
				}
			}

			if(getAttachmentData(face) !=null){
				for(AxisAlignedBB bb : getAttachmentData(face).getBoxes(face)){
					cachedCollidables.add(new CollidableComponent(bb,
							face, PipePart.ATTACHMENT));
					/*if(face == EnumFacing.UP){
						cachedCollidables.add(new CollidableComponent(
								new AxisAlignedBB(0.25f, 0.75f, 0.25f, 0.75f, 1f,
										0.75f), EnumFacing.UP, PipePart.PIPE));
					}
					if(face == EnumFacing.DOWN){
						cachedCollidables.add(new CollidableComponent(
								new AxisAlignedBB(0.25f, 0, 0.25f, 0.75f, 0.25f,
										0.75f), EnumFacing.DOWN, PipePart.PIPE));
					}
					if(face == EnumFacing.NORTH){
						cachedCollidables.add(new CollidableComponent(
								new AxisAlignedBB(0.25f, 0.25f, 0.0, 0.75f, 0.75f,
										0.25f), EnumFacing.NORTH, PipePart.PIPE));
					}
					if(face == EnumFacing.SOUTH){
						cachedCollidables
						.add(new CollidableComponent(new AxisAlignedBB(
								0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1f),
								EnumFacing.SOUTH, PipePart.PIPE));
					}
					if(face == EnumFacing.WEST){
						cachedCollidables.add(new CollidableComponent(
								new AxisAlignedBB(0.0f, 0.25f, 0.25f, 0.25f, 0.75f,
										0.75f), EnumFacing.WEST, PipePart.PIPE));
					}
					if(face == EnumFacing.EAST){
						cachedCollidables.add(new CollidableComponent(
								new AxisAlignedBB(0.75f, 0.25f, 0.25f, 1f, 0.75f,
										0.75f), EnumFacing.EAST, PipePart.PIPE));
					}*/
				}
			}
			
			if (this.getCoverData(face) != null) {
				AxisAlignedBB bbUp = new AxisAlignedBB(pixel * 0.0f,
						pixel * 14.0f, pixel * 0.0f, pixel * 16.0f,
						pixel * 16.0f, pixel * 16.0f);
				AxisAlignedBB bbDown = new AxisAlignedBB(pixel * 0.0f,
						pixel * 0.0f, pixel * 0.0f, pixel * 16.0f,
						pixel * 2.0f, pixel * 16.0f);
				AxisAlignedBB bbNorth = new AxisAlignedBB(pixel * 0.0f,
						pixel * 0.0f, pixel * 0.0f, pixel * 16.0f,
						pixel * 16.0f, pixel * 2.0f);
				AxisAlignedBB bbSouth = new AxisAlignedBB(pixel * 0.0f,
						pixel * 0.0f, pixel * 14.0f, pixel * 16.0f,
						pixel * 16.0f, pixel * 16.0f);
				AxisAlignedBB bbWest = new AxisAlignedBB(pixel * 0.0f,
						pixel * 0.0f, pixel * 0.0f, pixel * 2.0f,
						pixel * 16.0f, pixel * 16.0f);
				AxisAlignedBB bbEast = new AxisAlignedBB(pixel * 14.0f,
						pixel * 0.0f, pixel * 0.0f, pixel * 16.0f,
						pixel * 16.0f, pixel * 16.0f);
				if (face == EnumFacing.UP)
					cachedCollidables.add(new CollidableComponent(bbUp,
							EnumFacing.UP, PipePart.COVER));
				if (face == EnumFacing.DOWN)
					cachedCollidables.add(new CollidableComponent(bbDown,
							EnumFacing.DOWN, PipePart.COVER));
				if (face == EnumFacing.NORTH)
					cachedCollidables.add(new CollidableComponent(bbNorth,
							EnumFacing.NORTH, PipePart.COVER));
				if (face == EnumFacing.SOUTH)
					cachedCollidables.add(new CollidableComponent(bbSouth,
							EnumFacing.SOUTH, PipePart.COVER));
				if (face == EnumFacing.WEST)
					cachedCollidables.add(new CollidableComponent(bbWest,
							EnumFacing.WEST, PipePart.COVER));
				if (face == EnumFacing.EAST)
					cachedCollidables.add(new CollidableComponent(bbEast,
							EnumFacing.EAST, PipePart.COVER));
			}

			cachedCollidables.add(new CollidableComponent(new AxisAlignedBB(
					0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f), null,
					PipePart.PIPE));
		}

		collidablesDirty = false;

		return cachedCollidables;
	}

	public RaytraceResult getClosest(EntityPlayer player){
		return ModBlocks.crystalPipe.doRayTrace(getWorld(),	getPos().getX(), getPos().getY(), getPos().getZ(), player);
	}
	
	public ItemStack getPickBlock(RayTraceResult target, EntityPlayer player) {
		RaytraceResult closest = player == null ? null : getClosest(player);
		
		if(closest !=null){
			if (closest.component != null) {
				if (closest.component.dir != null) {
					EnumFacing dir = closest.component.dir;
					if (closest.component.data instanceof PipePart) {
						PipePart part = (PipePart) closest.component.data;
						if(part == PipePart.COVER){
							CoverData data = getCoverData(dir);
							if(data !=null){
								if(!player.isSneaking()){
									ItemStack cover = ItemPipeCover.getCover(data);
									return cover;
								}
								
								if(data.getBlockState() !=null){
									Block block = data.getBlockState().getBlock();
									return block.getPickBlock(data.getBlockState(), target, new PipeWorldWrapper(getWorld(), pos, dir), pos, player);
								}
							}
						}
					}
				}
			}
		}else{
			EnumFacing side = target.sideHit;
			CoverData data = getCoverData(side);
			if(data !=null){
				if(!player.isSneaking()){
					ItemStack cover = ItemPipeCover.getCover(data);
					return cover;
				}
				
				if(data.getBlockState() !=null){
					Block block = data.getBlockState().getBlock();
					return block.getPickBlock(data.getBlockState(), target, new PipeWorldWrapper(getWorld(), pos, side), pos, player);
				}
			}
		}
		return null;
	}

	public List<ItemStack> getDrops() {
		List<ItemStack> list = Lists.newArrayList();
		for(EnumFacing dir : EnumFacing.VALUES){
			CoverData data = getCoverData(dir);
  		  	if(data !=null){
  		  		list.add(ItemPipeCover.getCover(data));
  		  	}
		}
		return list;
	}

	public ItemStack getPipeDropped() {
		if(getWorld() == null){
			ModLogger.warning("returning errored pipe on pick [World is null]");
			return new ItemStack(getBlockType(), 1, getBlockMetadata());
		}
		IBlockState state = getWorld().getBlockState(getPos());
		return new ItemStack(ModBlocks.crystalPipe, 1, state.getBlock().getMetaFromState(state));
	}

	public Object getContainer(int iD, EntityPlayer player) {
		return new ContainerNormalPipe();
	}
	
	@SideOnly(Side.CLIENT)
	public Object getGui(int iD, EntityPlayer player) {
		return null;
	}
	
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client){
		if(messageId.equalsIgnoreCase("Attachment")){
			EnumFacing dir = messageData.hasKey("Dir") ? EnumFacing.getFront(messageData.getInteger("Dir")) : null;
			if(dir !=null){
				AttachmentData data = getAttachmentData(dir);
				if(data !=null){
					data.handleMessage(this, dir, messageData);
				}
			}
		}
	}

}
