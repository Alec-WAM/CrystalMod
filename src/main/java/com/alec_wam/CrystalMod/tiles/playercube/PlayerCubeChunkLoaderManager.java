package com.alec_wam.CrystalMod.tiles.playercube;

import com.mojang.authlib.*;

import net.minecraftforge.common.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.event.world.*;
import gnu.trove.list.array.*;
import net.minecraft.nbt.*;

import java.util.*;

import org.apache.logging.log4j.core.helpers.Strings;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.world.ModDimensions;
import com.google.common.collect.*;

public class PlayerCubeChunkLoaderManager implements ForgeChunkManager.LoadingCallback, ForgeChunkManager.PlayerOrderedLoadingCallback
{
    public static final HashSet<PlayerCube> chunkLoaders;
    public static PlayerCubeChunkLoaderManager instance;
    public static boolean dirty;
    private static HashMap<GameProfile, ForgeChunkManager.Ticket> playerTickets;
    
    public static void init() {
        ForgeChunkManager.setForcedChunkLoadingCallback((Object)CrystalMod.instance, (ForgeChunkManager.LoadingCallback)PlayerCubeChunkLoaderManager.instance);
        MinecraftForge.EVENT_BUS.register((Object)PlayerCubeChunkLoaderManager.instance);
    }
    
    public static void register(final PlayerCube loader) {
        synchronized (PlayerCubeChunkLoaderManager.chunkLoaders) {
            final GameProfile profile = loader.getOwner();
            if (profile != null) {
                final ForgeChunkManager.Ticket playerTicket = PlayerCubeChunkLoaderManager.instance.getPlayerTicket(profile);
                if (playerTicket != null) {
                    ForgeChunkManager.forceChunk(playerTicket, new ChunkPos(loader.getSpawnBlock().getX() >> 4, loader.getSpawnBlock().getZ() >> 4));
                }
            }
            PlayerCubeChunkLoaderManager.chunkLoaders.add(loader);
            PlayerCubeChunkLoaderManager.dirty = true;
        }
    }
    
    public static void unregister(final PlayerCube loader) {
        synchronized (PlayerCubeChunkLoaderManager.chunkLoaders) {
            PlayerCubeChunkLoaderManager.chunkLoaders.remove((Object)loader);
            PlayerCubeChunkLoaderManager.dirty = true;
        }
    }
    
    public static void clear() {
        PlayerCubeChunkLoaderManager.chunkLoaders.clear();
        PlayerCubeChunkLoaderManager.playerTickets.clear();
    }
    
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent event) {
        if (PlayerCubeChunkLoaderManager.dirty) {
            this.reloadChunkLoaders();
        }
    }
    
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
    	if(event.getWorld().provider.getDimension() == ModDimensions.CUBE_ID){
	        PlayerCubeChunkLoaderManager.playerTickets.clear();
	        PlayerCubeChunkLoaderManager.chunkLoaders.clear();
    	}
    }
    
    public void reloadChunkLoaders() {
        synchronized (PlayerCubeChunkLoaderManager.chunkLoaders) {
            PlayerCubeChunkLoaderManager.dirty = false;
            final HashSet<ChunkPos> worldChunks = Sets.newHashSet();
            final Multimap<ForgeChunkManager.Ticket, ChunkPos> toUnload = HashMultimap.create();
            final Multimap<ForgeChunkManager.Ticket, ChunkPos> loaded = HashMultimap.create();
            final Multimap<ForgeChunkManager.Ticket, ChunkPos> toAdd = HashMultimap.create();
            final HashMap<GameProfile, ForgeChunkManager.Ticket> map = PlayerCubeChunkLoaderManager.playerTickets;
            for (final ForgeChunkManager.Ticket ticket : map.values()) {
                final ImmutableSet<ChunkPos> chunkList = (ImmutableSet<ChunkPos>)ticket.getChunkList();
                for (final ChunkPos pair : chunkList) {
                    ticket.world.getBlockState(pair.getCenterBlock(20));
                }
                worldChunks.addAll(chunkList);
                toUnload.putAll(ticket, chunkList);
                loaded.putAll(ticket, chunkList);
            }
            final Iterator<PlayerCube> iterator = PlayerCubeChunkLoaderManager.chunkLoaders.iterator();
            while (iterator.hasNext()) {
                final PlayerCube chunkLoader = iterator.next();
                if (!CubeManager.getInstance().getWorld().isBlockLoaded(chunkLoader.getSpawnBlock())) {
                	PlayerCubeChunkLoaderManager.dirty = true;
                    iterator.remove();
                }
                /*if (!chunkLoader.isChunkLoaded()) {
                    PlayerCubeChunkLoaderManager.dirty = true;
                }*/
                final GameProfile profile = chunkLoader.getOwner();
                if (chunkLoader.isChunkLoaded() && profile != null) {
                    final ForgeChunkManager.Ticket ticket = this.getPlayerTicket(profile);
                    if (ticket == null) {
                        continue;
                    }
                    for (final ChunkPos coordIntPair : chunkLoader.getChunkCoords()) {
                        worldChunks.remove(coordIntPair);
                        toUnload.remove(ticket, coordIntPair);
                        if (!loaded.containsEntry(ticket, coordIntPair)) {
                            toAdd.put(ticket, coordIntPair);
                        }
                    }
                }
            }
            final Iterator<ForgeChunkManager.Ticket> iterator2 = map.values().iterator();
            while (iterator2.hasNext()) {
                final ForgeChunkManager.Ticket ticket = iterator2.next();
                for (final ChunkPos pair2 : toUnload.get(ticket)) {
                    ForgeChunkManager.unforceChunk(ticket, pair2);
                }
                for (final ChunkPos pair2 : toAdd.get(ticket)) {
                    ForgeChunkManager.forceChunk(ticket, pair2);
                }
                if (ticket.getChunkList().isEmpty()) {
                    ForgeChunkManager.releaseTicket(ticket);
                    iterator2.remove();
                }
                else {
                    final TIntArrayList x = new TIntArrayList();
                    final TIntArrayList z = new TIntArrayList();
                    for (final ChunkPos chunkCoordIntPair : ticket.getChunkList()) {
                        x.add(chunkCoordIntPair.chunkXPos);
                        z.add(chunkCoordIntPair.chunkZPos);
                    }
                    ticket.getModData().setIntArray("x", x.toArray());
                    ticket.getModData().setIntArray("z", z.toArray());
                }
            }
        }
    }
    
    public ForgeChunkManager.Ticket getPlayerTicket(final GameProfile profile) {
        HashMap<GameProfile, ForgeChunkManager.Ticket> gameProfileTicketHashMap = PlayerCubeChunkLoaderManager.playerTickets;
        if (gameProfileTicketHashMap == null) {
            PlayerCubeChunkLoaderManager.playerTickets = gameProfileTicketHashMap = new HashMap<GameProfile, ForgeChunkManager.Ticket>();
        }
        ForgeChunkManager.Ticket ticket = gameProfileTicketHashMap.get(profile);
        if (ticket == null) {
            ticket = ForgeChunkManager.requestPlayerTicket((Object)CrystalMod.instance, profile.getName(), CubeManager.getInstance().getWorld(), ForgeChunkManager.Type.NORMAL);
            final NBTTagCompound tag = ticket.getModData();
            tag.setString("Name", profile.getName());
            final UUID id = profile.getId();
            if (id != null) {
                tag.setLong("UUIDL", id.getLeastSignificantBits());
                tag.setLong("UUIDU", id.getMostSignificantBits());
            }
            gameProfileTicketHashMap.put(profile, ticket);
        }
        return ticket;
    }
    
    public void ticketsLoaded(final List<ForgeChunkManager.Ticket> tickets, final World world) {
    	if(world.provider.getDimension() != ModDimensions.CUBE_ID)return;
        PlayerCubeChunkLoaderManager.dirty = true;
        final HashMap<GameProfile, ForgeChunkManager.Ticket> cache = new HashMap<GameProfile, ForgeChunkManager.Ticket>();
        PlayerCubeChunkLoaderManager.playerTickets = cache;
        for (final ForgeChunkManager.Ticket ticket : tickets) {
            final NBTTagCompound modData = ticket.getModData();
            GameProfile profile = null;
    		final String name = modData.getString("Name");
            UUID uuid = null;
            if (modData.hasKey("UUIDL")) {
                uuid = new UUID(modData.getLong("UUIDU"), modData.getLong("UUIDL"));
            }
            if (Strings.isEmpty((CharSequence)name)) {
                profile = null;
            }
            else profile = new GameProfile(uuid, name);
            
            cache.put(profile, ticket);
            final int[] x = modData.getIntArray("x");
            final int[] z = modData.getIntArray("z");
            if (x.length == z.length) {
                for (int i = 0; i < x.length; ++i) {
                    ForgeChunkManager.forceChunk(ticket, new ChunkPos(x[i], z[i]));
                }
            }
        }
    }
    
    public ListMultimap<String, ForgeChunkManager.Ticket> playerTicketsLoaded(final ListMultimap<String, ForgeChunkManager.Ticket> tickets, final World world) {
        return tickets;
    }
    
    static {
        chunkLoaders = Sets.newHashSet();
        PlayerCubeChunkLoaderManager.instance = new PlayerCubeChunkLoaderManager();
        PlayerCubeChunkLoaderManager.dirty = false;
        PlayerCubeChunkLoaderManager.playerTickets = new HashMap<GameProfile, ForgeChunkManager.Ticket>();
    }
}
