package alec_wam.CrystalMod.proxy;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    	return super.getReachDistanceForPlayer(entityPlayer);
    }
    
    public World getWorld(int dim) {
		return super.getWorld(dim);
	}
    
    public EntityPlayer getPlayerForUsername(String playerName) {
		return super.getPlayerForUsername(playerName);
	}

	public boolean isOp(GameProfile profile) {
		return super.isOp(profile);
	}
}
