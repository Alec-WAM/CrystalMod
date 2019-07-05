package alec_wam.CrystalMod.util;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.UUID;
import java.util.WeakHashMap;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class FakePlayerUtil {
	private static final WeakHashMap<World, FakePlayer> FAKE_PLAYERS = new WeakHashMap<World, FakePlayer>();

	private static WeakReference<FakePlayer> CRYSTALMOD_PLAYER = null;
	public static final GameProfile CRYSTALMOD = new GameProfile(UUID.nameUUIDFromBytes("[CrystalMod]".getBytes()), "[CrystalMod]");

	private static FakePlayer getCrystalMod(ServerWorld world)
	{
		FakePlayer ret = CRYSTALMOD_PLAYER != null ? CRYSTALMOD_PLAYER.get() : null;
		if (ret == null)
		{
			ret = FakePlayerFactory.get(world, CRYSTALMOD);
			CRYSTALMOD_PLAYER = new WeakReference<FakePlayer>(ret);
		}
		return ret;
	}

	public static FakePlayer getPlayer(final ServerWorld w)
	{
		if(w == null)
		{
			throw new InvalidParameterException( "World is null." );
		}

		final FakePlayer wrp = FAKE_PLAYERS.get(w);
		if(wrp != null)
		{
			return wrp;
		}

		final FakePlayer p = getCrystalMod(w);
		FAKE_PLAYERS.put(w, p);
		return p;
	}

}
