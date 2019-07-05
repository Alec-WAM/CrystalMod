package alec_wam.CrystalMod.client;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestManager;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.GuiEngineFurnace;
import alec_wam.CrystalMod.tiles.energy.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.miner.GuiMiner;
import alec_wam.CrystalMod.tiles.machine.miner.TileEntityMiner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
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
    	Screen currentScreen = Minecraft.getInstance().currentScreen;
    	if(currentScreen instanceof GuiEngineFurnace){
    		TileEntityEngineFurnace engine = ((GuiEngineFurnace)currentScreen).tileFurnace;
    		int fuel = TileEntityEngineFurnace.getItemEnergyValue(stack);
    		if(fuel > 0) {
    			fuel*=engine.getFuelValue();
    			String energyString = NumberFormat.getNumberInstance(Locale.US).format(fuel);
    			ITextComponent text = new TranslationTextComponent("crystalmod.engine.furnace.fuel.item", energyString);
	        	text.applyTextStyle(TextFormatting.AQUA);
    			lines.add(text);
    		}
    	}
    	
    	if(currentScreen instanceof GuiMiner){
    		if(stack.getItem() == Items.ENCHANTED_BOOK){
    			ListNBT enchantData = EnchantedBookItem.getEnchantments(stack);
    			if(enchantData.size() == 1){
    				CompoundNBT compoundnbt = enchantData.getCompound(0);
    		        ResourceLocation id = ResourceLocation.tryCreate(compoundnbt.getString("id"));
    		        if(id !=null && id.equals(Enchantments.SILK_TOUCH.getRegistryName())){
    		        	ITextComponent text = new TranslationTextComponent("crystalmod.miner.upgradecost", ""+TileEntityMiner.SILK_TOUCH_POWER_COST);
    		        	text.applyTextStyle(TextFormatting.AQUA);
    		        	lines.add(text);
    		        }
    		        if(id !=null && id.equals(Enchantments.FORTUNE.getRegistryName())){
    		        	ITextComponent text = new TranslationTextComponent("crystalmod.miner.upgradecost.lvl", ""+TileEntityMiner.FORTUNE_POWER_COST);
    		        	text.applyTextStyle(TextFormatting.AQUA);
    		        	lines.add(text);
    		        }
    		        if(id !=null && id.equals(Enchantments.EFFICIENCY.getRegistryName())){
    		        	ITextComponent text = new TranslationTextComponent("crystalmod.miner.upgradecost.lvl", ""+TileEntityMiner.EFFICENCY_POWER_COST);
    		        	text.applyTextStyle(TextFormatting.AQUA);
    		        	lines.add(text);
    		        }
    			}
    		}
    	}
    }
    
    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event){
    	WirelessChestManager.resetClientManager();
    }
}
