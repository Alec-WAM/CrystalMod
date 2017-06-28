package alec_wam.CrystalMod.proxy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.material.CropOverlays;
import alec_wam.CrystalMod.blocks.crops.material.ModelSeed;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.capability.LayerExtendedPlayerInventory;
import alec_wam.CrystalMod.client.model.CustomBakedModel;
import alec_wam.CrystalMod.client.model.CustomItemModelFactory;
import alec_wam.CrystalMod.client.model.LayerDragonWings;
import alec_wam.CrystalMod.client.model.LayerHorseAccessories;
import alec_wam.CrystalMod.client.model.LayerWolfAccessories;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.entities.disguise.DisguiseClientHandler;
import alec_wam.CrystalMod.fluids.FluidColored;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.handler.ClientEventHandler;
import alec_wam.CrystalMod.handler.KeyHandler;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuidePageLoader;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.LookupResult;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.render.BakedModelLoader;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initClient();
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        ModelLoaderRegistry.registerLoader(ModelSeed.LoaderSeeds.INSTANCE);
        IResourceManager manager = FMLClientHandler.instance().getClient().getResourceManager();
        if(manager !=null && manager instanceof IReloadableResourceManager){
        	((IReloadableResourceManager)manager).registerReloadListener(new GuidePageLoader());
        	((IReloadableResourceManager)manager).registerReloadListener(this);
        }
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new DisguiseClientHandler());
        ModBlocks.initClient();
        ModEntites.initClient();
        OBJLoader.INSTANCE.addDomain(CrystalMod.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        LayerDragonWings dragonWingsRenderer = new LayerDragonWings();
		for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()){
			renderer.addLayer(dragonWingsRenderer);
			renderer.addLayer(new LayerExtendedPlayerInventory(renderer));
		}
		
		LayerHorseAccessories horseAccessoryRenderer = new LayerHorseAccessories();
		Render<?> renderHorse = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(AbstractHorse.class);
		if(renderHorse !=null && renderHorse instanceof RenderLivingBase){
			RenderLivingBase<?> livingRender = (RenderLivingBase<?>)renderHorse;
			livingRender.addLayer(horseAccessoryRenderer);
		}
		Render<?> renderWolf = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityWolf.class);
		if(renderWolf !=null && renderWolf instanceof RenderLivingBase){
			RenderLivingBase<?> livingRender = (RenderLivingBase<?>)renderWolf;
			livingRender.addLayer(new LayerWolfAccessories(livingRender));
		}
        MinecraftForge.EVENT_BUS.register(this);
        
        keyHandler = new KeyHandler();
        MinecraftForge.EVENT_BUS.register(keyHandler);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
		GuidePages.createPages();
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
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass_tinted"));
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass_painted"));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/decorative/dense_darkness"));
    	
        
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
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/case_piston_head"));
        
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
        event.getMap().registerSprite(ModFluids.fluidInk.getStill());
        event.getMap().registerSprite(ModFluids.fluidInk.getFlowing());
        event.getMap().registerSprite(ModFluids.fluidTears.getStill());
        event.getMap().registerSprite(ModFluids.fluidTears.getFlowing());
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystalcluster"));
        
        event.getMap().registerSprite(FluidColored.LiquidStill);
        event.getMap().registerSprite(FluidColored.LiquidFlowing);
        CropOverlays.registerIcons(event.getMap());
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/crop/seed_background"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/crop/seed_overlay"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/icon_sword"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/icon_pickaxe"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/bat/leather_handle"));
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
        		if(ItemStackTools.getStackSize(stack) > 999){
        			event.getToolTip().add("Stack Size: "+ItemStackTools.getStackSize(stack));
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
        
        List<IEnhancement> enhancementList = EnhancementManager.getAppliedEnhancements(stack);
        if(!enhancementList.isEmpty()){
        	
        	if(!GuiScreen.isShiftKeyDown()){
        		event.getToolTip().add(Lang.localize("gui.enhancement.holdshift"));
        	}else{
        		for(IEnhancement enhancement : enhancementList)enhancement.addToolTip(stack, event.getToolTip());
        	}
        }
    }
    
    @Override
    public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
    }
    
    @Override
    public World getClientWorld() {
      return Minecraft.getMinecraft().world;
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
    
    @SideOnly(Side.CLIENT)
	public static GuiGuideBase forcedChapter;
    
    public void setForcedGuidePage(LookupResult result){
    	GuiGuideChapter chapterGui = null;
		if(result !=null && result.getChapter() != null){
			if(result.getPage() !=null){
				chapterGui = new GuiGuideChapter(null, result.getChapter(), result.getPage());
			} else {
				chapterGui = new GuiGuideChapter(null, result.getChapter());
			}
		}
		forcedChapter = chapterGui;
    }
    
    public Object getForcedGuidePage(){
    	return forcedChapter;
    }
    
    public static final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		if(Minecraft.getMinecraft().getTextureMapBlocks() !=null){
			for(int i = 0; i < destroyBlockIcons.length; ++i) {
				destroyBlockIcons[i] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
			}
		}
	}
}
