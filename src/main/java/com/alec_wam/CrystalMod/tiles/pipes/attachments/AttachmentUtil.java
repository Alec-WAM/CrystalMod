package com.alec_wam.CrystalMod.tiles.pipes.attachments;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;

public class AttachmentUtil {

	private static final Map<String, AttachmentData> REGISTRY = new HashMap<String, AttachmentData>();
	
	public static AttachmentData registerAttachment(String id, AttachmentData attachment){
		REGISTRY.put(id, attachment);
		return attachment;
	}
	
	public static Set<String> getIds(){
		return REGISTRY.keySet();
	}
	
	public static AttachmentEStorageExport eStorage_Export;
	public static AttachmentEStorageImport eStorage_Import;
	public static AttachmentEStorageSensor eStorage_Sensor;
	
	public static void initAttachments(){
		eStorage_Export = (AttachmentEStorageExport) registerAttachment("estorage.export", new AttachmentEStorageExport());
		eStorage_Import = (AttachmentEStorageImport) registerAttachment("estorage.import", new AttachmentEStorageImport());
		eStorage_Sensor = (AttachmentEStorageSensor) registerAttachment("estorage.sensor", new AttachmentEStorageSensor());
	}
	
	public static AttachmentData getFromID(String id){
		AttachmentData data = REGISTRY.get(id);
		if(data !=null){
			try {
				return data.getClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public static abstract class AttachmentData{
		
		public abstract String getID();
		
		public void update(TileEntityPipe pipe, EnumFacing face){}
		
		public void writeToNBT(NBTTagCompound nbt){
			nbt.setString("ID", getID());
		}
		
		public void loadFromNBT(NBTTagCompound nbt){
		}
		
		public abstract List<AxisAlignedBB> getBoxes(EnumFacing dir);
		
		public static AttachmentData readFromNBT(NBTTagCompound nbt) {
			if(!nbt.hasKey("ID"))return null;
            String ID = nbt.getString("ID");
            AttachmentData data = AttachmentUtil.getFromID(ID);
            if(data !=null){
            	data.loadFromNBT(nbt);
            }
            return data;
        }

		@SideOnly(Side.CLIENT)
		public abstract void addQuads(FaceBakery faceBakery, List<BakedQuad> list, EnumFacing dir);

		@SideOnly(Side.CLIENT)
		public Object getGui(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
			return null;
		}
		
		public Object getContainer(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir) {
			return null;
		}

		public boolean isPipeValid(TileEntityPipe tileEntityPipe, EnumFacing side, ItemStack held) {
			return false;
		}
		
		public int getRedstonePower(TileEntityPipe pipe, EnumFacing side, boolean strong){
			return 0;
		}

		public void handleMessage(TileEntityPipe pipe, EnumFacing dir, NBTTagCompound messageData) {
		}

		public boolean canConnectRedstone(TileEntityPipe pipe, EnumFacing side) {
			return false;
		}
	}
	
}
