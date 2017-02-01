package alec_wam.CrystalMod.util;

import java.util.LinkedList;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;

public class TeleportUtil {

	public static Entity teleportEntity(Entity entity, int dimension, double xCoord, double yCoord, double zCoord) {
        return teleportEntity(entity, dimension, xCoord, yCoord, zCoord, entity.rotationYaw, entity.rotationPitch, new Vector3d(entity.motionX, entity.motionY, entity.motionZ));
    }
	
	public static Entity teleportEntity(Entity entity, int dimension, double xCoord, double yCoord, double zCoord, float yaw, float pitch, Vector3d motion) {
        if (entity == null || entity.getEntityWorld().isRemote) {
            return entity;
        }

        MinecraftServer server = entity.getServer();
        int sourceDim = entity.getEntityWorld().provider.getDimension();

        if (!entity.isBeingRidden() && !entity.isRiding()) {
            return handleEntityTeleport(entity, server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch, motion);
        }

        Entity rootEntity = entity.getLowestRidingEntity();
        PassengerTeleporter PassengerTeleporter = new PassengerTeleporter(rootEntity);
        PassengerTeleporter rider = PassengerTeleporter.getPassenger(entity);
        if (rider == null) {
            ModLogger.info("RiddenEntity: This error should not be possible");
            return entity;
        }
        PassengerTeleporter.teleport(server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch, motion);
        PassengerTeleporter.remountRiders();
        PassengerTeleporter.updateClients();

        return rider.entity;
    }
	
	private static Entity handleEntityTeleport(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch, Vector3d motion) {
        if (entity == null || entity.getEntityWorld().isRemote) {
            return entity;
        }

        boolean interDimensional = sourceDim != targetDim;

        if (interDimensional && !ForgeHooks.onTravelToDimension(entity, targetDim)) {
            return entity;
        }

        if (interDimensional) {
            if (entity instanceof EntityPlayerMP) {
                return teleportPlayerToDim((EntityPlayerMP) entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch, motion);
            }
            else {
                return teleportEntityToDim(entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch, motion);
            }
        }
        else {
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
                player.setRotationYawHead(yaw);
                player.motionX += motion.x; player.motionY += motion.y; player.motionZ += motion.z;
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setDouble("X", motion.x);
                nbt.setDouble("Y", motion.y);
                nbt.setDouble("Z", motion.z);
                CrystalModNetwork.sendToAll(new PacketEntityMessage(player, "AddMotion", nbt));
            }
            else {
                entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
                entity.setRotationYawHead(yaw);
                entity.motionX += motion.x; entity.motionY += motion.y; entity.motionZ += motion.z;
            }
        }

        return entity;
    }
	
	private static Entity teleportEntityToDim(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch, Vector3d motion) {
        if (entity.isDead) {
            return null;
        }

        WorldServer sourceWorld = server.worldServerForDimension(sourceDim);
        WorldServer targetWorld = server.worldServerForDimension(targetDim);
        entity.dimension = targetDim;

        sourceWorld.removeEntity(entity);
        entity.isDead = false;
        entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        sourceWorld.updateEntityWithOptionalForce(entity, false);

        Entity newEntity = EntityList.createEntityByIDFromName(EntityList.getKey(entity), targetWorld);
        if (newEntity != null) {
            copyDataFromOld(entity, newEntity);
            newEntity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
            boolean flag = newEntity.forceSpawn;
            newEntity.forceSpawn = true;
            targetWorld.spawnEntity(newEntity);
            newEntity.forceSpawn = flag;
            targetWorld.updateEntityWithOptionalForce(newEntity, false);
        }

        entity.isDead = true;
        sourceWorld.resetUpdateEntityTick();
        targetWorld.resetUpdateEntityTick();

        return newEntity;
    }
	
	public static void copyDataFromOld(Entity entityIn, Entity entityOut)
    {
        NBTTagCompound nbttagcompound = entityIn.writeToNBT(new NBTTagCompound());
        nbttagcompound.removeTag("Dimension");
        entityOut.readFromNBT(nbttagcompound);
    }
	
	private static EntityPlayer teleportPlayerToDim(EntityPlayerMP player, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch, Vector3d motion) {
        WorldServer sourceWorld = server.worldServerForDimension(sourceDim);
        WorldServer targetWorld = server.worldServerForDimension(targetDim);
        PlayerList playerList = server.getPlayerList();

        player.dimension = targetDim;
        player.connection.sendPacket(new SPacketRespawn(player.dimension, targetWorld.getDifficulty(), targetWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
        playerList.updatePermissionLevel(player);
        sourceWorld.removeEntityDangerously(player);
        player.isDead = false;

        //region Transfer to world

        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        targetWorld.spawnEntity(player);
        targetWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(targetWorld);

        //endregion

        playerList.preparePlayer(player, sourceWorld);
        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        player.interactionManager.setWorld(targetWorld);
        player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));

        playerList.updateTimeAndWeatherForPlayer(player, targetWorld);
        playerList.syncPlayerInventory(player);

        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceDim, targetDim);
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);

        player.motionX += motion.x; player.motionY += motion.y; player.motionZ += motion.z;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setDouble("X", motion.x);
        nbt.setDouble("Y", motion.y);
        nbt.setDouble("Z", motion.z);
        CrystalModNetwork.sendToAll(new PacketEntityMessage(player, "AddMotion", nbt));
        
        return player;
    }
	
	//https://github.com/brandon3055/BrandonsCore/blob/master/src/main/java/com/brandon3055/brandonscore/lib/TeleportUtils.java
	private static class PassengerTeleporter {
        public Entity entity;
        public LinkedList<PassengerTeleporter> passengers = new LinkedList<>();
        public double offsetX, offsetY, offsetZ;

        /**
         * Creates a new passenger helper for the given entity and recursively adds all of the entities passengers.
         *
         * @param entity The root entity. If you have multiple stacked entities this would be the one at the bottom of the stack.
         */
        public PassengerTeleporter(Entity entity) {
            this.entity = entity;
            if (entity.isRiding()) {
                offsetX = entity.posX - entity.getRidingEntity().posX;
                offsetY = entity.posY - entity.getRidingEntity().posY;
                offsetZ = entity.posZ - entity.getRidingEntity().posZ;
            }
            for (Entity passenger : entity.getPassengers()) {
                passengers.add(new PassengerTeleporter(passenger));
            }
        }

        /**
         * Recursively teleports the entity and all of its passengers after dismounting them.
         * @param server The minecraft server.
         * @param sourceDim The source dimension.
         * @param targetDim The target dimension.
         * @param xCoord The target x position.
         * @param yCoord The target y position.
         * @param zCoord The target z position.
         * @param yaw The target yaw.
         * @param pitch The target pitch.
         */
        public void teleport(MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch, Vector3d motion) {
            entity.removePassengers();
            entity = handleEntityTeleport(entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch, motion);
            for (PassengerTeleporter passenger : passengers) {
                passenger.teleport(server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch, motion);
            }
        }

        /**
         * Recursively remounts all of this entities riders and offsets their position relative to their position before teleporting.
         */
        public void remountRiders() {
            if (entity.isRiding()) {
                entity.setLocationAndAngles(entity.posX + offsetX, entity.posY + offsetY, entity.posZ + offsetZ, entity.rotationYaw, entity.rotationPitch);
            }
            for (PassengerTeleporter passenger : passengers) {
                passenger.entity.startRiding(entity, true);
                passenger.remountRiders();
            }
        }

        /**
         * This method sends update packets to any players that were teleported with the entity stack.
         */
        public void updateClients() {
            if (entity instanceof EntityPlayerMP) {
                updateClient((EntityPlayerMP) entity);
            }
            for (PassengerTeleporter passenger : passengers) {
                passenger.updateClients();
            }
        }

        /**
         * This is the method that is responsible for actually sending the update to each client.
         * @param playerMP The Player.
         */
        private void updateClient(EntityPlayerMP playerMP) {
            if (entity.isBeingRidden()) {
                playerMP.connection.sendPacket(new SPacketSetPassengers(entity));
            }
            for (PassengerTeleporter passenger : passengers) {
                passenger.updateClients();
            }
        }

        /**
         * This method returns the helper for a specific entity in the stack.
         * @param passenger The passenger you are looking for.
         * @return The passenger helper for the specified passenger.
         */
        public PassengerTeleporter getPassenger(Entity passenger) {
            if (this.entity == passenger) {
                return this;
            }

            for (PassengerTeleporter rider : passengers) {
                PassengerTeleporter re = rider.getPassenger(passenger);
                if (re != null) {
                    return re;
                }
            }

            return null;
        }
    }
	
}
