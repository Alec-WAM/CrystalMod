package com.alec_wam.CrystalMod.tiles.accessories.clock;

import java.util.ArrayList;
import java.util.List;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.util.Lang;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Alarm {

	public static Alarm INSTANCE = new Alarm();
	
	public static final int MINUTES_IN_HOUR = 50;

	@SubscribeEvent
	public void onPreWorldTick(TickEvent.WorldTickEvent event) {
	      if (!(event.world instanceof WorldServer))
	         return;
	
	      if (event.phase != TickEvent.Phase.START)
	         return;
	
	      WorldServer world = (WorldServer) event.world;
	
	      if (world.areAllPlayersAsleep() && Config.enableAlarmClocks) {
	         Alarm.sleepWorld(world);
	      }
   	}
	
	public static List<TileEntityAlarmClock> getAllAlarms(List<EntityPlayer> playerEntities, List<EntityPlayer> outPlayers) {
		List<TileEntityAlarmClock> alarms = new ArrayList<TileEntityAlarmClock>();

		for (EntityPlayer player : playerEntities) {
			TileEntityAlarmClock alarm = findNearbyAlarm(player);
			if (alarm != null) {
				alarms.add(alarm);
				outPlayers.add(player);
			}
		}

		return alarms;
	}

	public static TileEntityAlarmClock findNearbyAlarm(EntityPlayer player) {
		World world = player.worldObj;
		for (double x = player.posX - 2; x < player.posX + 2; x++) {
			for (double y = player.posY - 1; y < player.posY + 2; y++) {
				for (double z = player.posZ - 2; z < player.posZ + 2; z++) {
					TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
					if (tileEntity !=null && tileEntity instanceof TileEntityAlarmClock)
						return (TileEntityAlarmClock) tileEntity;
				}
			}
		}

		return null;
	}

	public static void sleepWorld(World world) {
		List<EntityPlayer> playersWithAlarms = new ArrayList<EntityPlayer>();
		List<TileEntityAlarmClock> alarms = getAllAlarms(world.playerEntities, playersWithAlarms);
		if(alarms.isEmpty())return;
		int mntTotal = 0;
		for (TileEntityAlarmClock alarm : alarms) {
			mntTotal += MinecraftTime.extrapolateTime(alarm.getHour(), alarm.getMinute()); // (minutes + hours * minutesInHour) * ticks
		}

		mntTotal /= alarms.size();

		long curTime = world.getWorldTime();

      	long i = curTime;
      	if (mntTotal <= curTime % 24000) // have to roll to another day
      		i += 24000L;

      	i -= i % 24000; // align to the next morning
      	// append new time
      	i += mntTotal;

      	world.setWorldTime(i);

	      MinecraftTime time = MinecraftTime.getFromWorldTime(world.getWorldTime());

	      // reset all players
	      for (EntityPlayer player : (List<EntityPlayer>) world.playerEntities) {
	         if (player.isPlayerSleeping()) {
	            player.wakeUpPlayer(false, false, true);
	            if (playersWithAlarms.contains(player)) {
	               //player.playSound(BetterSleeping.MODID + ":alarm", 0.5F, 2.0F);
	               /*if (Config.alarmSoundLevel > 0)
	                  world.playSoundEffect(player.posX, player.posY, player.posZ, BetterSleeping.MODID + ":alarm", (float) Config
	                          .alarmSoundLevel, 1F);*/
	            }
	         }

	         player.addChatComponentMessage(new net.minecraft.util.text.TextComponentString(String.format(Lang.prefix+"msg.wakeUp", time.toString())));
	      }

	      world.provider.resetRainAndThunder();
	   }
}
