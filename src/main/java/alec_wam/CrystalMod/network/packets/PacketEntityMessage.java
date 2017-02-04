package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.entities.disguise.DisguiseType;
import alec_wam.CrystalMod.items.tools.grapple.EntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.grapple.GrappleControllerBase;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.items.tools.grapple.GrappleType;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.EntityUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class PacketEntityMessage extends AbstractPacketThreadsafe {

	public int id;
	private String type;
	private NBTTagCompound data;
	
	public PacketEntityMessage(){}
	
	public PacketEntityMessage(Entity entity, String type){
		this(entity, type, new NBTTagCompound());
	}
	
    public PacketEntityMessage(Entity entity, String type, NBTTagCompound data){
    	this.id = entity.getEntityId();
    	this.type = type;
    	this.data = data;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		id = buffer.readInt();
		type = buffer.readString(100);
		try {
			data = buffer.readCompoundTag();
		} catch (IOException e) {
			data = new NBTTagCompound();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(id);
		buffer.writeString(type);
		buffer.writeCompoundTag(data);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		handle(CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().getEntityWorld(), id, true);
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		handle(netHandler.playerEntity.getEntityWorld(), id, false);
	}
	
	public void handle(World world, int id, boolean client){
		if(world == null){
			return;
		}
		Entity entity = world.getEntityByID(id);
		if(entity !=null){
			if(type.equalsIgnoreCase("CustomDataSync")){
				EntityUtil.setCustomEntityData(entity, data);
			}
			if(type.equalsIgnoreCase("SetSize")){
				float width = data.getFloat("Width");
				float height = data.getFloat("Height");
				float stepSize = data.getFloat("StepSize");
				float eyeHeight = data.getFloat("EyeHeight");
				EntityUtil.setEntitySize(entity, width, height);
				entity.setPosition(entity.posX, entity.posY, entity.posZ);
				entity.stepHeight = stepSize;
				if(entity instanceof EntityPlayer){
					((EntityPlayer)entity).eyeHeight = eyeHeight;
				}
			}
			if(type.equalsIgnoreCase("DisguiseSync")){
				
				if(entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;
					ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(data.hasKey("Type")){
						byte type = data.getByte("Type");
						DisguiseType disguise = DisguiseType.values()[type];
						playerEx.setCurrentDiguise(disguise);
					}
					if(data.hasKey("UUID", Constants.NBT.TAG_COMPOUND) && !data.hasKey("NullUUID")){
						playerEx.setPlayerDisguiseUUID(NBTUtil.getUUIDFromTag(data.getCompoundTag("UUID")));
					}
					if(data.hasKey("LastNullUUID")){
						playerEx.setPlayerDisguiseUUID(null);
						playerEx.setPlayerDisguiseUUID(null);
					}
				}
			}
			if(type.equalsIgnoreCase("EntityMovement")){
				double x = data.getDouble("x");
				double y = data.getDouble("y");
				double z = data.getDouble("z");
				double mX = data.getDouble("mX");
				double mY = data.getDouble("mY");
				double mZ = data.getDouble("mZ");
				entity.posX = x; entity.posY = y; entity.posZ = z;
				entity.motionX = mX; entity.motionY = mY; entity.motionZ = mZ;
			}
			if(type.equalsIgnoreCase("AddMotion")){
				double x = data.getDouble("X");
				double y = data.getDouble("Y");
				double z = data.getDouble("Z");
				entity.motionX = x; entity.motionY = y; entity.motionZ = z;
			}
			if(type.equalsIgnoreCase("GrappleConnect")){
				GrappleType type = GrappleType.values()[data.getByte("Type")];
				double x = data.getDouble("x");
				double y = data.getDouble("y");
				double z = data.getDouble("z");
				BlockPos blockpos = null;
				if(data.hasKey("BlockPos")){
					blockpos = NBTUtil.getPosFromTag(data.getCompoundTag("BlockPos"));
				}
				int hookID = data.getInteger("HookID");
				
				Entity entityHook = world.getEntityByID(hookID);
				if(entityHook !=null && entityHook instanceof EntityGrapplingHook){
					((EntityGrapplingHook)entityHook).clientAttach(x, y, z);
				}
				GrappleHandler.createController(type, data.getInteger("EntityID"), hookID, world, new Vec3d(x, y, z), data.getInteger("MaxLength"), blockpos);
			}
			if(type.equalsIgnoreCase("GrappleHookConnect")){
				double x = data.getDouble("x");
				double y = data.getDouble("y");
				double z = data.getDouble("z");
				int hookID = data.getInteger("EntityID");
				
				Entity entityHook = world.getEntityByID(hookID);
				if(entityHook !=null && entityHook instanceof EntityGrapplingHook){
					((EntityGrapplingHook)entityHook).setAttachPos(x, y, z);
				}
			}
			if(type.equalsIgnoreCase("GrappleUnattach")){
				GrappleControllerBase controller = GrappleHandler.controllers.get(entity.getEntityId());
				controller.unattach();
			}
			if(type.equalsIgnoreCase("GrappleDisconnect")){
				GrappleHandler.receiveGrappleEnd(id, world, data.getInteger("HookID"));
			}
			if(entity instanceof IMessageHandler){
				((IMessageHandler)entity).handleMessage(type, data, client);
			}
		}
	}

}
