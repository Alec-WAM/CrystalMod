package alec_wam.CrystalMod.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketRequestProfile;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProfileUtil {
	public static final ConcurrentMap<UUID, GameProfile> profileCache = buildCache(3 * 60 * 60, 1024 * 5);
	public static <K, V> ConcurrentMap<K, V> buildCache(int seconds, int maxSize) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        if (seconds > 0) {
            builder.expireAfterWrite(seconds, TimeUnit.SECONDS);
        }

        if (maxSize > 0) {
            builder.maximumSize(maxSize);
        }

        return builder.build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }).asMap();
    }
	
	public static String getUsernameClient(UUID owner) {
		if(profileCache.containsKey(owner)){
			GameProfile profile = profileCache.get(owner);
			return profile.getName();
		} else {
			CrystalModNetwork.sendToServer(new PacketRequestProfile(owner));
		}
		return "*LOADING*";
	}
	
	public static GameProfile getProfileServer(UUID uuid){
		return ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid);
	}
	
	public static String getUsernameServer(UUID uuid){
		return getProfileServer(uuid).getName();
	}
	
	public static ItemStack createPlayerSkull(GameProfile profile){
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		ItemNBTHelper.getCompound(stack).setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
		return stack;
	}
}
