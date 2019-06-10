package alec_wam.CrystalMod.client;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientEventHandler {
	public static volatile int elapsedTicks;
    
    @SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
    	if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT && event.side == LogicalSide.CLIENT) {
    		elapsedTicks++;
    	}
    }
    
    @SubscribeEvent
    public void addTooltips(ItemTooltipEvent event){
    	ItemStack stack = event.getItemStack();
    	List<ITextComponent> lines = event.getToolTip();
    	Screen currentScreen = Minecraft.getInstance().field_71462_r;
    	if(currentScreen instanceof GuiEngineFurnace){
    		TileEntityEngineFurnace engine = ((GuiEngineFurnace)currentScreen).tileFurnace;
    		int fuel = TileEntityEngineFurnace.getItemEnergyValue(stack);
    		if(fuel > 0) {
    			fuel*=engine.getFuelValue();
    			String energyString = NumberFormat.getNumberInstance(Locale.US).format(fuel);
    			lines.add(new TranslationTextComponent("crystalmod.engine.furnace.fuel.item", energyString));
    		}
    	}
    }
}
