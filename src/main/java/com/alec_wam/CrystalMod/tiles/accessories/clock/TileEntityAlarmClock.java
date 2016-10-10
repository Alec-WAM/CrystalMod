package com.alec_wam.CrystalMod.tiles.accessories.clock;

import net.minecraft.nbt.NBTTagCompound;

import com.alec_wam.CrystalMod.tiles.TileEntityMod;

public class TileEntityAlarmClock extends TileEntityMod {

	private int hour;
	private int minute;

	public TileEntityAlarmClock() {

	}

   @Override
   public void readCustomNBT(NBTTagCompound nbt) {
      hour = nbt.getInteger("hour");
      minute = nbt.getInteger("minute");
   }

   @Override
   public void writeCustomNBT(NBTTagCompound nbt) {
      nbt.setInteger("hour", hour);
      nbt.setInteger("minute", minute);
   }

   public void incHour() {
      hour++;
      if (hour >= 24)
         hour = 0;
   }

   public void decHour() {
      hour--;
      if (hour < 0)
         hour = 23;
   }

   public void incMinute() {
      minute++;
      if (minute >= 60)
         minute = 0;
   }

   public void decMinute() {
      minute--;
      if (minute < 0)
         minute = 59;
   }

   public void incTenHours() {
      hour += 10;
      if (hour >= 24)
         hour -= 30;
      if (hour < 0)
         hour += 10;

      hour %= 24;
   }

   public void decTenHours() {
      hour -= 10;
      if (hour < 0)
         hour += 30;

      hour %= 24;
   }

   public int getHour() {
      return hour;
   }

   public int getMinute() {
      return minute;
   }	
	
}
