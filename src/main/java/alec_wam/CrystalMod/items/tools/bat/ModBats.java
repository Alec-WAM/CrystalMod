package alec_wam.CrystalMod.items.tools.bat;

import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.types.DiamondBatType;
import alec_wam.CrystalMod.items.tools.bat.types.GoldBatType;
import alec_wam.CrystalMod.items.tools.bat.types.IronBatType;
import alec_wam.CrystalMod.items.tools.bat.types.StoneBatType;
import alec_wam.CrystalMod.items.tools.bat.types.WoodBatType;
import alec_wam.CrystalMod.items.tools.bat.upgrades.CakeBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.EnderBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.FireworkBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.LapisBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.MuffleBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.PistonBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.PoisonBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.QuartzBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.RedstoneBatUpgrade;
import alec_wam.CrystalMod.items.tools.bat.upgrades.SkullBatUpgrade;

public class ModBats {

	public static IBatType WOOD;
	public static IBatType STONE;
	public static IBatType IRON;
	public static IBatType DIAMOND;
	public static IBatType GOLD;
	public static IBatType CRYSTAL;
	
	public static IBatUpgrade PISTON;
	public static IBatUpgrade REDSTONE;
	public static IBatUpgrade FLAME;
	public static IBatUpgrade QUARTZ;
	public static IBatUpgrade LAPIS;
	public static IBatUpgrade CAKE;
	public static IBatUpgrade SKULL;
	public static IBatUpgrade POISON;
	public static IBatUpgrade FIREWORKS;
	public static IBatUpgrade ENDER;
	public static IBatUpgrade MUFFLE;
	
	public static void registerBats(){
		WOOD = BatHelper.registerBatType(new WoodBatType());
		STONE = BatHelper.registerBatType(new StoneBatType());
		IRON = BatHelper.registerBatType(new IronBatType());
		DIAMOND = BatHelper.registerBatType(new DiamondBatType());
		GOLD = BatHelper.registerBatType(new GoldBatType());
	}
	
	public static void registerUpgrades(){
		PISTON = BatHelper.registerBatUpgrade(new PistonBatUpgrade(3, 10));
		REDSTONE = BatHelper.registerBatUpgrade(new RedstoneBatUpgrade(64, 10));
		//FLAME = BatHelper.registerBatUpgrade(new FlameBatUpgrade(64, 10));
		QUARTZ = BatHelper.registerBatUpgrade(new QuartzBatUpgrade(32, 20));
		LAPIS = BatHelper.registerBatUpgrade(new LapisBatUpgrade(100, 3));
		CAKE = BatHelper.registerBatUpgrade(new CakeBatUpgrade(10, 10));
		SKULL = BatHelper.registerBatUpgrade(new SkullBatUpgrade(1, 10));
		POISON = BatHelper.registerBatUpgrade(new PoisonBatUpgrade(20, 10));
		FIREWORKS = BatHelper.registerBatUpgrade(new FireworkBatUpgrade(1, 1));
		ENDER = BatHelper.registerBatUpgrade(new EnderBatUpgrade(16, 1));
		MUFFLE = BatHelper.registerBatUpgrade(new MuffleBatUpgrade());
	}
	
}
