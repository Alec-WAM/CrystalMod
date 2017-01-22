package alec_wam.CrystalMod.items.tools.grapple;

import java.util.HashMap;
import java.util.HashSet;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class GrappleHandler {

	public static HashMap<Integer, GrappleControllerBase> controllers = new HashMap<Integer, GrappleControllerBase>(); // client side
	public static HashMap<BlockPos, GrappleControllerBase> controllerpos = new HashMap<BlockPos, GrappleControllerBase>();
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side
	
	public static HashMap<Entity, EntityGrapplingHook> grappleHooks = new HashMap<Entity, EntityGrapplingHook>();
	
	public static EntityGrapplingHook getHook(Entity entity, World world) {
		if (grappleHooks.containsKey(entity)) {
			EntityGrapplingHook hook = grappleHooks.get(entity);
			if (hook != null && !hook.isDead) {
				return hook;
			}
		}
		return null;
	}
	
	public static void setHook(Entity entity, EntityGrapplingHook hook) {
		grappleHooks.put(entity, hook);
	}
	
	public static GrappleControllerBase createController(GrappleType type, int entityid, int hookId, World world, Vec3d vec, int maxlength, BlockPos pos){
		GrappleControllerBase controller = null;
		
		if(type == GrappleType.BLOCK){
			controller = new GrappleControllerBase(type, entityid, hookId, world, vec, maxlength);
		}
		
		if (pos != null && controller != null) {
			controllerpos.put(pos, controller);
		}
		
		return controller;
	}

	public static void registerController(int entityId, GrappleControllerBase controller) {
		controllers.put(entityId, controller);
	}
	
	public static void unregisterController(int entityId) {
		controllers.remove(entityId);
	}
	
	public static void receiveGrappleEnd(int id, World world, int hookID) {
		if (attached.contains(id)) {
			attached.remove(new Integer(id));
		} 
  		
		if(hookID >= 0){
			Entity entityHook = world.getEntityByID(hookID);
	  		if (entityHook != null && entityHook instanceof EntityGrapplingHook) {
	  			((EntityGrapplingHook)entityHook).removeServer();
	  		}
		}
		
  		Entity entity = world.getEntityByID(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
	}

	public static void getPlayerMovement(GrappleControllerBase grappleController, int entityId) {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			Entity entity = grappleController.entity;
			if (entity instanceof EntityPlayerSP) {
				EntityPlayerSP player = (EntityPlayerSP) entity;
				grappleController.receivePlayerMovementMessage(player.moveStrafing, player.moveForward, player.movementInput.jump);
			}
		}
	}

	public static boolean isSneaking(Entity entity) {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			if(entity == FMLClientHandler.instance().getClientPlayerEntity()){
				return GameSettings.isKeyDown(FMLClientHandler.instance().getClient().gameSettings.keyBindSneak);
			}
		}
		return entity.isSneaking();
	}
	
	public static void serverAttach(Entity entity, int hookID, BlockPos blockpos, Vec3d pos, EnumFacing sideHit, GrappleType type) {
		Vec3d vec3 = pos;
		if (sideHit == EnumFacing.DOWN) {
			vec3 = vec3.subtract(0, 0.3, 0);
		} else if (sideHit == EnumFacing.WEST) {
			vec3 = vec3.subtract(0.05, 0, 0);
		} else if (sideHit == EnumFacing.NORTH) {
			vec3 = vec3.subtract(0, 0, 0.05);
		}
		
		//west -x
		//north -z
        
        attached.add(entity.getEntityId());
		
        NBTTagCompound nbtAttach = new NBTTagCompound();
        nbtAttach.setByte("Type", (byte)type.ordinal());
        nbtAttach.setDouble("x", vec3.xCoord);
        nbtAttach.setDouble("y", vec3.yCoord);
        nbtAttach.setDouble("z", vec3.zCoord);
        nbtAttach.setInteger("EntityID", entity.getEntityId());
        nbtAttach.setInteger("HookID", hookID);
        nbtAttach.setInteger("MaxLength", 0);
        if(blockpos !=null)nbtAttach.setTag("BlockPos", NBTUtil.createPosTag(blockpos));
		if (entity instanceof EntityPlayerMP) { // fixes strange bug in LAN
			CrystalModNetwork.sendTo(new PacketEntityMessage(entity, "GrappleConnect", nbtAttach), (EntityPlayerMP)entity);
			EntityPlayerMP sender = (EntityPlayerMP) entity;
			int dimension = sender.dimension;
			MinecraftServer minecraftServer = sender.mcServer;
			for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
				nbtAttach = new NBTTagCompound();
		        nbtAttach.setDouble("x", vec3.xCoord);
		        nbtAttach.setDouble("y", vec3.yCoord);
		        nbtAttach.setDouble("z", vec3.zCoord);
		        nbtAttach.setInteger("EntityID", hookID);
		        // must generate a fresh message for every player!
				if (dimension == player.dimension) {
					CrystalModNetwork.sendTo(new PacketEntityMessage(entity, "GrappleHookConnect", nbtAttach), player);
				}
			}
		}
	}

	
}
