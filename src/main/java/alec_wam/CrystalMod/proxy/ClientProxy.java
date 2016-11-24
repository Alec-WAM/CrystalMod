package alec_wam.CrystalMod.proxy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.client.model.CustomBakedModel;
import alec_wam.CrystalMod.client.model.CustomItemModelFactory;
import alec_wam.CrystalMod.client.model.LayerDragonWings;
import alec_wam.CrystalMod.client.model.LayerHorseAccessories;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.fluids.FluidColored;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.handler.KeyHandler;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.ModelEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.render.BakedModelLoader;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initClient();
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        ModBlocks.initClient();
        ModEntites.initClient();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        LayerDragonWings dragonWingsRenderer = new LayerDragonWings();
		for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()){
			renderer.addLayer(dragonWingsRenderer);
		}
		LayerHorseAccessories horseAccessoryRenderer = new LayerHorseAccessories();
		Render<?> renderHorse = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityHorse.class);
		if(renderHorse !=null && renderHorse instanceof RenderLivingBase){
			RenderLivingBase<?> livingRender = (RenderLivingBase<?>)renderHorse;
			livingRender.addLayer(horseAccessoryRenderer);
		}
        MinecraftForge.EVENT_BUS.register(this);
        
        keyHandler = new KeyHandler();
        MinecraftForge.EVENT_BUS.register(keyHandler);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
    private static final Map<ResourceLocation, ICustomItemRenderer> CUSTOM_RENDERS = Maps.newHashMap();
    private static final List<CustomBakedModel> CUSTOM_MODELS = Lists.newArrayList();
    
    public static void registerItemRenderCustom(String id, ICustomItemRenderer render){
    	CUSTOM_RENDERS.put(new ResourceLocation(id), render);
    }
    
    public static void registerItemRender(String id, ICustomItemRenderer render){
    	CUSTOM_RENDERS.put(new ResourceLocation(id), render);
    }
    
    public static ICustomItemRenderer getRenderer(Item item){
    	return getRenderer(item.getRegistryName());
    }
    
    public static ICustomItemRenderer getRenderer(ResourceLocation loc){
    	return CUSTOM_RENDERS.get(loc);
    }
    
    public static void registerCustomModel(ModelResourceLocation loc, IBakedModel model){
    	registerCustomModel(new CustomBakedModel(loc, model));
    }
    
    public static void registerCustomModel(CustomBakedModel model){
    	CUSTOM_MODELS.add(model);
    }
    
    public static ItemMinecartRender MinecartRenderer3d = new ItemMinecartRender();
    
    @SubscribeEvent
    public void onBakeModel(final ModelBakeEvent event) {
    	for(CustomBakedModel model : CUSTOM_MODELS){
    		model.preModelRegister();
    		event.getModelRegistry().putObject(model.getModelLoc(), model.getModel());
    		model.postModelRegister();
    	}
    	
    	if(Config.vanillaMinecarts3d){
    		for(Item item : new Item[] {Items.MINECART, Items.CHEST_MINECART, Items.FURNACE_MINECART, Items.TNT_MINECART, Items.HOPPER_MINECART, Items.COMMAND_BLOCK_MINECART}){
    			ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
    			Object obj = event.getModelRegistry().getObject(model);
    	        
    	        if(obj instanceof IBakedModel)
    	        {
    	        	event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj, MinecartRenderer3d));
    	        }
    		}
    	}
    	
        for(Entry<ResourceLocation, ICustomItemRenderer> entry : CUSTOM_RENDERS.entrySet())
		{
			ModelResourceLocation model = new ModelResourceLocation(entry.getKey(), "inventory");
	        Object obj = event.getModelRegistry().getObject(model);
	        
	        if(obj instanceof IBakedModel)
	        {
	        	event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj, entry.getValue()));
	        }
		}
    }
    
    public boolean isPaused()
	{
		if(FMLClientHandler.instance().getClient().isSingleplayer() && !FMLClientHandler.instance().getClient().getIntegratedServer().getPublic())
		{
			GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;

			if(screen != null && screen.doesGuiPauseGame())
			{
				return true;
			}
		}

		return false;
	}
    
    @SubscribeEvent
    public void onStitch(final TextureStitchEvent.Pre event) {
    	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/blank"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/power_plus"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/item_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/fluid_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/storage_square"));
        for(int i = 0; i < 4; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/power_square_"+i));
        }
        for(int i = 0; i < 4; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/rfpower_square_"+i));
        }
        
        for(GlassType type : GlassType.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass"));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/attachment/import"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/iron_cap"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_inout"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_in"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_out"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/glass"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/tank_creative"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/tank_creative_top"));
        
        for(BatteryType type : BatteryType.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/battery_"+type.getName().toLowerCase()));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_blocked"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_in"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_out"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/uncharged"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/charged"));
        for(int i = 0; i < 9; i++){
            event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/"+i));
        }
        if (ModFluids.fluidXpJuice != null) {
            event.getMap().registerSprite(ModFluids.fluidXpJuice.getStill());
            event.getMap().registerSprite(ModFluids.fluidXpJuice.getFlowing());
        }
        
        event.getMap().registerSprite(FluidColored.LiquidStill);
        event.getMap().registerSprite(FluidColored.LiquidFlowing);
    }
    
    public EntityPlayer getPlayerForUsername(String playerName) {
    	return null;
    }
    
    public boolean isOp(GameProfile profile){
    	return getClientWorld().getWorldInfo().areCommandsAllowed();
    }
    
    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (ItemStackTools.isNullStack(stack))
        {
            return;
        }
        
        if(Minecraft.getMinecraft().currentScreen !=null && (Minecraft.getMinecraft().currentScreen instanceof GuiHDDInterface || Minecraft.getMinecraft().currentScreen instanceof GuiPanel)){
        	if(ItemNBTHelper.getBoolean(stack, "DummyItem", false)){
        		if(stack.stackSize > 999){
        			event.getToolTip().add("Stack Size: "+stack.stackSize);
        		}
        	}
        }
        
        if (GhostItemHelper.hasGhostAmount(stack))
        {
            int amount = GhostItemHelper.getItemGhostAmount(stack);
            if (amount == 0)
            {
                event.getToolTip().add("Everything");
            } else
            {
                event.getToolTip().add("Ghost item amount: " + amount);
            }
        }
        
        if(ItemNBTHelper.verifyExistance(stack, ItemDragonWings.UPGRADE_NBT)){
        	event.getToolTip().add(TextFormatting.DARK_PURPLE+""+TextFormatting.UNDERLINE+"Dragon Wings");
        }
        
        if(ItemNBTHelper.verifyExistance(stack, "CrystalMod.InvisArmor")){
        	event.getToolTip().add(TextFormatting.WHITE+""+TextFormatting.UNDERLINE+"Invisible");
        }
    }
    
    @Override
    public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().thePlayer;
    }
    
    @Override
    public World getClientWorld() {
      return Minecraft.getMinecraft().theWorld;
    }
    
    @Override
    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
      if (entityPlayer instanceof EntityPlayerMP) {
        return ((EntityPlayerMP) entityPlayer).interactionManager.getBlockReachDistance();
      }
      return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
    
    public World getWorld(int dim) {
		return null;
	}
    
    public boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
