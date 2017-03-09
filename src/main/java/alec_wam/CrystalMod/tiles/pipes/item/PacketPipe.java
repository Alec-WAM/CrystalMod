package alec_wam.CrystalMod.tiles.pipes.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.pipes.ConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageExport;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentEStorageImport;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentIOType;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;
import alec_wam.CrystalMod.tiles.pipes.liquid.TileEntityPipeLiquid;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ModLogger;

public class PacketPipe extends AbstractPacketThreadsafe {

	private int x;
	private int y;
	private int z;
	private String type;
	private int dir;
	private String data;
	
	public PacketPipe(){}
	
    public PacketPipe(TileEntityPipe pipe, String type, EnumFacing dir, String data){
    	x = pipe.getPos().getX();
    	y = pipe.getPos().getY();
    	z = pipe.getPos().getZ();
    	this.type = type;
    	this.dir = dir == null ? -1 : dir.getIndex();
    	this.data = data;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		type = buffer.readString(200);
		dir = buffer.readShort();
		data = buffer.readString(200);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeString(type);
		buffer.writeShort(dir);
		buffer.writeString(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().getEntityWorld();
		if(world == null){
			return;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null && tile instanceof TileEntityPipe){
			TileEntityPipe pipe = (TileEntityPipe) tile;
			EnumFacing dir = this.dir == -1 ? null : EnumFacing.getFront(this.dir);
			if(dir==null)return;
			if(type.equalsIgnoreCase("FilterGhost")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null && item.getFilter(dir).getStackInSlot(0).getMetadata() == FilterType.NORMAL.ordinal()){
						String slot = data.substring(0, data.lastIndexOf(";"));
						String amt = data.substring(data.lastIndexOf(";"));
						//System.out.println("Ghost Set to "+slot+" "+amt);
					}
				}
			}
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		World world = netHandler.playerEntity.getEntityWorld();
		if(world == null){
			return;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null && tile instanceof TileEntityPipe){
			TileEntityPipe pipe = (TileEntityPipe) tile;
			EnumFacing dir = this.dir == -1 ? null : EnumFacing.getFront(this.dir);
			if(type.equalsIgnoreCase("CMode")){
				ConnectionMode mode = null;
				for(ConnectionMode md : ConnectionMode.values()){
					if(md.name().equalsIgnoreCase(data)){
						mode = md;
						break;
					}
				}
				if(mode !=null && dir !=null){
					pipe.setConnectionMode(dir, mode);
				}
			}
			if(type.equalsIgnoreCase("RMode")){
				RedstoneMode mode = null;
				for(RedstoneMode md : RedstoneMode.values()){
					if(md.name().equalsIgnoreCase(data)){
						mode = md;
						break;
					}
				}
				if(mode !=null && dir !=null){
					if(pipe instanceof TileEntityPipeItem){
						((TileEntityPipeItem)pipe).setRedstoneMode(mode, dir);
					}
					if(pipe instanceof TileEntityPipeEStorage){
						TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
						AttachmentData data2 = item.getAttachmentData(dir);
						if(data2 !=null && data2 instanceof AttachmentEStorageExport){
							((AttachmentEStorageExport)data2).rMode = mode; 
						}
						if(data2 !=null && data2 instanceof AttachmentEStorageImport){
							((AttachmentEStorageImport)data2).rMode = mode; 
						}
					}
				}
			}
			if(type.equalsIgnoreCase("IOType")){
				AttachmentIOType io = null;
				for(AttachmentIOType md : AttachmentIOType.values()){
					if(md.name().equalsIgnoreCase(data)){
						io = md;
						break;
					}
				}
				if(io !=null && dir !=null){
					if(pipe instanceof TileEntityPipeEStorage){
						TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
						AttachmentData data2 = item.getAttachmentData(dir);
						if(data2 !=null && data2 instanceof AttachmentEStorageExport){
							((AttachmentEStorageExport)data2).ioType = io; 
						}
						if(data2 !=null && data2 instanceof AttachmentEStorageImport){
							((AttachmentEStorageImport)data2).ioType = io; 
						}
					}
				}
			}
			if(type.equalsIgnoreCase("Pri")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(dir !=null){
						item.setOutputPriority(dir, item.getOutputPriority(dir)+(data.equalsIgnoreCase("-") ? -1 : 1));
					}
				}
			}
			if(type.equalsIgnoreCase("SelfFeed")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(dir !=null){
						item.setSelfFeedEnabled(dir, data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
			if(type.equalsIgnoreCase("RoundRobin")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(dir !=null){
						item.setRoundRobinEnabled(dir, data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
			if(type.equalsIgnoreCase("FilterVis")){
				if(netHandler.playerEntity.openContainer !=null && netHandler.playerEntity.openContainer instanceof ContainerItemPipe){
					((ContainerItemPipe)netHandler.playerEntity.openContainer).setGhostVisible(data.equalsIgnoreCase("true") ? true : false);
				}
			}
			if(type.equalsIgnoreCase("FilterSetBlack")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null){
						ItemNBTHelper.setBoolean(item.getFilter(dir).getStackInSlot(0), "BlackList", data.equalsIgnoreCase("true") ? true : false);
					}
				}
				if(pipe instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
					AttachmentData data2 = item.getAttachmentData(dir);
					if(data2 !=null && data2 instanceof AttachmentEStorageImport){
						ItemNBTHelper.setBoolean(((AttachmentEStorageImport)data2).getFilter(), "BlackList", data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
			if(type.equalsIgnoreCase("FilterScanInv")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null && item.getFilter(dir).getStackInSlot(0).getMetadata() == FilterType.CAMERA.ordinal()){
						item.scanInventory(dir);
					}
				}
			}
			if(type.equalsIgnoreCase("FilterGhost")){
				System.out.println("Handleing Paket Ghost SERVER");
			}
			if(type.equalsIgnoreCase("FilterSetOre")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null){
						ItemNBTHelper.setBoolean(item.getFilter(dir).getStackInSlot(0), "OreMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}

				if(pipe instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
					AttachmentData data2 = item.getAttachmentData(dir);
					if(data2 !=null && data2 instanceof AttachmentEStorageImport){
						ItemNBTHelper.setBoolean(((AttachmentEStorageImport)data2).getFilter(), "OreMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
			if(type.equalsIgnoreCase("FilterSetMeta")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null){
						ItemNBTHelper.setBoolean(item.getFilter(dir).getStackInSlot(0), "MetaMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}

				if(pipe instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
					AttachmentData data2 = item.getAttachmentData(dir);
					if(data2 !=null && data2 instanceof AttachmentEStorageImport){
						ItemNBTHelper.setBoolean(((AttachmentEStorageImport)data2).getFilter(), "MetaMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
			if(type.equalsIgnoreCase("FilterSetNBTMatch")){
				if(pipe instanceof TileEntityPipeItem){
					TileEntityPipeItem item = (TileEntityPipeItem) pipe;
					if(item.getFilter(dir).getStackInSlot(0) !=null){
						ItemNBTHelper.setBoolean(item.getFilter(dir).getStackInSlot(0), "NBTMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}

				if(pipe instanceof TileEntityPipeEStorage){
					TileEntityPipeEStorage item = (TileEntityPipeEStorage) pipe;
					AttachmentData data2 = item.getAttachmentData(dir);
					if(data2 !=null && data2 instanceof AttachmentEStorageImport){
						ItemNBTHelper.setBoolean(((AttachmentEStorageImport)data2).getFilter(), "NBTMatch", data.equalsIgnoreCase("true") ? true : false);
					}
				}
			}
		}
		return;
	}

}
