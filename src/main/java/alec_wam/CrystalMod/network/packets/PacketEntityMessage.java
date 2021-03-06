package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.KnowledgeManager;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.handler.EventHandler;
import alec_wam.CrystalMod.items.enchancements.ModEnhancements;
import alec_wam.CrystalMod.items.tools.grapple.EntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.grapple.GrappleControllerBase;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.items.tools.grapple.GrappleType;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.TimeUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
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
	
	public static final String MESSAGE_UPDATETIME = "#UpdateTime#";
	
	public void handle(World world, int id, boolean client){
		if(world == null){
			return;
		}
		Entity entity = world.getEntityByID(id);
		if(entity !=null){
			if(type.equalsIgnoreCase("CustomDataSync")){
				EntityUtil.setCustomEntityData(entity, data);
			}
			if(type.equalsIgnoreCase("#ClearXP#")){
				if(entity instanceof EntityPlayer){
					EntityPlayer ePlayer = (EntityPlayer)entity;
					ePlayer.removeExperienceLevel(Integer.MAX_VALUE);
					if(client){
						CrystalMod.proxy.getClientWorld().playSound(ePlayer.posX, ePlayer.posY, ePlayer.posZ, ModSounds.levelDown, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					}
				}
			}
			if(type.equalsIgnoreCase("#ZeroFall#")){
				entity.fallDistance = 0.0F;
			}
			if(type.equalsIgnoreCase("#RemoveXP#")){
				if(entity instanceof EntityPlayer){
					EntityPlayer ePlayer = (EntityPlayer)entity;
					ePlayer.removeExperienceLevel(data.getInteger("Amount"));
					/*if(client){
						CrystalMod.proxy.getClientWorld().playSound(ePlayer.posX, ePlayer.posY, ePlayer.posZ, ModSounds.levelDown, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					}*/
				}
			}
			if(type.equalsIgnoreCase("#FusorFlash#")){
				if(entity instanceof EntityPlayer){
					EntityPlayer ePlayer = (EntityPlayer)entity;
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(ePlayer);
					if(exPlayer !=null){
						exPlayer.setScreenFlashTime(TimeUtil.SECOND * 5);
						CrystalMod.proxy.getClientWorld().playSound(ePlayer.posX, ePlayer.posY, ePlayer.posZ, ModSounds.explosion_ringing, SoundCategory.AMBIENT, 1F, 1.0F, false);
					}
				}
			}
			if(type.equalsIgnoreCase(MESSAGE_UPDATETIME)){
				if(entity instanceof EntityPlayer){
					EntityPlayer ePlayer = (EntityPlayer)entity;
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(ePlayer);
					if(exPlayer !=null){
						if(data.getString("Type").equalsIgnoreCase("Radiation")){
							exPlayer.setRadiation(data.getInteger("Time"));
						}
						if(data.getString("Type").equalsIgnoreCase("Intellect")){
							exPlayer.setIntellectTime(data.getInteger("Time"));
						}
					}
				}
			}
			if(type.equalsIgnoreCase("#Jump#")){
				if(entity instanceof EntityPlayer){
					EntityPlayer ePlayer = (EntityPlayer)entity;
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(ePlayer);
					if(exPlayer !=null){
						if(!exPlayer.hasJumped){
							ePlayer.jump();
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setDouble("X", ePlayer.motionX);
							nbt.setDouble("Y", ePlayer.motionY);
							nbt.setDouble("Z", ePlayer.motionZ);
							CrystalModNetwork.sendTo(new PacketEntityMessage(ePlayer, "AddMotion", nbt), (EntityPlayerMP)ePlayer);
							exPlayer.hasJumped = true;
						}
					}
				}
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
					if(data.hasKey("NullUUID")){
						playerEx.setPlayerDisguiseUUID(null);
					}else if(data.hasKey("UUID", Constants.NBT.TAG_COMPOUND)){
						playerEx.setPlayerDisguiseUUID(NBTUtil.getUUIDFromTag(data.getCompoundTag("UUID")));
					}
					if(data.hasKey("Mini")){
						playerEx.setMini(data.getBoolean("Mini"));
					}
				}
			}
			if(type.equalsIgnoreCase("ExtendedPlayerSync")){
				if(!client){
					ModLogger.warning("A ExtendedPlayerSync packet was just sent to the server!");
					return;
				}
				if(entity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer)entity;
					ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
					playerEx.unpackSyncPacket(data);
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
			if(type.equalsIgnoreCase("SyncKnowledge")){
				KnowledgeManager.loadData(data);
			}
			if(type.equalsIgnoreCase("RotationChange")){
				if(data.hasKey("Yaw")){
					entity.rotationYaw = data.getFloat("Yaw");
				}
			}
			if(type.equalsIgnoreCase("ElytraFly")){
				if(entity instanceof EntityPlayerMP){
					EntityPlayerMP player = (EntityPlayerMP)entity;
					if (!player.onGround && player.motionY < 0.0D && !player.isElytraFlying() && !player.isInWater())
	                {
						ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

	                    if (ModEnhancements.ELYTRA.isApplied(itemstack))
	                    {
	                    	ModLogger.info("Fly Point 3");
							player.setElytraFlying();
	                    	EventHandler.addElytraToPlayer(player);
	                    }
	                }
	                else
	                {
	                	player.clearElytraFlying();
                    	EventHandler.removeElytraFromPlayer(player);
	                }
				}
			}
			if(type.equalsIgnoreCase("#SetMouseStack#")){
				if(entity instanceof EntityPlayerMP){
					EntityPlayerMP player = (EntityPlayerMP)entity;
					if(data.hasKey("EmptyStack")){
						player.inventory.setItemStack(ItemStackTools.getEmptyStack());
					} else {
						ItemStack loaded = ItemStackTools.loadFromNBT(data.getCompoundTag("Stack"));
						player.inventory.setItemStack(loaded);
					}
					player.updateHeldItem();
				}
			}
			if(type.equalsIgnoreCase("#PickupEffects#")){
				if(client){
			        EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
			        int collectedID = data.getInteger("CollectedID");
					Entity collectedEntity = CrystalMod.proxy.getClientWorld().getEntityByID(collectedID);
					if (collectedEntity != null)
			        {
			            if (collectedEntity instanceof EntityXPOrb)
			            {
			            	CrystalMod.proxy.getClientWorld().playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (EntityUtil.rand.nextFloat() - EntityUtil.rand.nextFloat()) * 0.35F + 0.9F, false);
			            }
			            else
			            {
			            	CrystalMod.proxy.getClientWorld().playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (EntityUtil.rand.nextFloat() - EntityUtil.rand.nextFloat()) * 1.4F + 2.0F, false);
			            }
			            int oldAmount = 0;
			            if (entity instanceof EntityItem)
			            {
			            	oldAmount = ((EntityItem)entity).getEntityItem().getCount();
			            	((EntityItem)entity).getEntityItem().setCount(data.getInteger("Amount"));
			            }
			            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleItemPickup(CrystalMod.proxy.getClientWorld(), collectedEntity, entitylivingbase, 0.5F));
			            if (entity instanceof EntityItem)
			            {
			            	((EntityItem)entity).getEntityItem().setCount(oldAmount);
			            }
			            ((WorldClient)CrystalMod.proxy.getClientWorld()).removeEntityFromWorld(collectedID);
			        }
				}
			}
			if(entity instanceof IMessageHandler){
				((IMessageHandler)entity).handleMessage(type, data, client);
			}
		}
	}

}
