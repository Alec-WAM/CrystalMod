package alec_wam.CrystalMod.handler;

import org.lwjgl.input.Keyboard;

import alec_wam.CrystalMod.capability.PacketOpenExtendedInventory;
import alec_wam.CrystalMod.items.tools.backpack.PacketOpenBackpack;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeyHandler {

	public KeyBinding key = new KeyBinding(Lang.translateToLocal("keybind.crystalmodinventory"), 
			Keyboard.KEY_C, "key.categories.crystalmod");
	public KeyBinding keyBack = new KeyBinding(Lang.translateToLocal("keybind.crystalmodbackpack"), 
			Keyboard.KEY_G, "key.categories.crystalmod");
	
	public KeyHandler() {
		 ClientRegistry.registerKeyBinding(key);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) return;
		if (event.phase == Phase.START ) {
			if (FMLClientHandler.instance().getClient().inGameHasFocus) {
				if(key.isPressed())CrystalModNetwork.sendToServer(new PacketOpenExtendedInventory());
				if(keyBack.isPressed())CrystalModNetwork.sendToServer(new PacketOpenBackpack());
			}
		}
	}
}
