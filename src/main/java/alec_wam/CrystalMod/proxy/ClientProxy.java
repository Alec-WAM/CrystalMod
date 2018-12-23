package alec_wam.CrystalMod.proxy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.ModelScaffold;
import alec_wam.CrystalMod.blocks.crops.bamboo.ItemWrappedFood.WrappedFoodType;
import alec_wam.CrystalMod.blocks.crops.bamboo.ModelWrappedFood;
import alec_wam.CrystalMod.blocks.crops.material.CropOverlays;
import alec_wam.CrystalMod.blocks.crops.material.ModelSeed;
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
import alec_wam.CrystalMod.integration.minecraft.ItemBoatRender;
import alec_wam.CrystalMod.integration.minecraft.ItemMinecartRender;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.guide.GuiGuideBase;
import alec_wam.CrystalMod.items.guide.GuiGuideChapter;
import alec_wam.CrystalMod.items.guide.GuidePageLoader;
import alec_wam.CrystalMod.items.guide.GuidePages;
import alec_wam.CrystalMod.items.guide.GuidePages.LookupResult;
import alec_wam.CrystalMod.tiles.WoodenBlockProperies.WoodType;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.power.cu.TileEntityPipePowerCU;
import alec_wam.CrystalMod.tiles.pipes.render.BakedModelLoader;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
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
        ModelLoaderRegistry.registerLoader(new CustomModModelLoader());
        ModelLoaderRegistry.registerLoader(ModelSeed.LoaderSeeds.INSTANCE);
        ModelLoaderRegistry.registerLoader(ModelWrappedFood.LoaderWrappedFood.INSTANCE);
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

    @SuppressWarnings("unchecked")
	@Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        LayerDragonWings dragonWingsRenderer = new LayerDragonWings();
		for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()){
			renderer.addLayer(dragonWingsRenderer);
			renderer.addLayer(new LayerExtendedPlayerInventory(renderer));
		}
		
		LayerHorseAccessories horseAccessoryRenderer = new LayerHorseAccessories();
		for(Class<? extends AbstractHorse> clazz : new Class[]{AbstractHorse.class, AbstractChestHorse.class, EntityHorse.class, EntitySkeletonHorse.class, EntityZombieHorse.class}){
			Render<?> renderHorse = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(clazz);
			if(renderHorse !=null && renderHorse instanceof RenderLivingBase){
				RenderLivingBase<?> livingRender = (RenderLivingBase<?>)renderHorse;
				livingRender.addLayer(horseAccessoryRenderer);
			}
		}
		Render<?> renderWolf = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityWolf.class);
		if(renderWolf !=null && renderWolf instanceof RenderLivingBase){
			RenderLivingBase<?> livingRender = (RenderLivingBase<?>)renderWolf;
			livingRender.addLayer(new LayerWolfAccessories(livingRender));
		}
        MinecraftForge.EVENT_BUS.register(this);
        
        keyHandler = new KeyHandler();
        MinecraftForge.EVENT_BUS.register(keyHandler);

        
        //Block Colors
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor(){
        	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
        	{
        		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
        	}
        }, new Block[] {ModBlocks.bambooLeaves});
        
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor(){
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				return ColorizerFoliage.getFoliageColorBasic();
			}
        }, ModBlocks.bambooLeaves);
        
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor(){
        	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
        	{
        		return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
        	}
        }, ModBlocks.seaweed);
        
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor(){
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				return ColorizerGrass.getGrassColor(0.5D, 1.0D);
			}
        }, ModBlocks.seaweed);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
		GuidePages.createPages();
    }
    
    private static final Map<ResourceLocation, ICustomItemRenderer> CUSTOM_RENDERS = Maps.newHashMap();
    private static final Map<ModelResourceLocation, CustomBakedModel> CUSTOM_MODELS = Maps.newHashMap();
    
    public static class CustomModModelLoader implements ICustomModelLoader {
    	public static boolean ENABLE_TEST_SCAFFOLD_MODEL = false;
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			if(ENABLE_TEST_SCAFFOLD_MODEL)ModelScaffold.reloadModels();
		}

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			if(ENABLE_TEST_SCAFFOLD_MODEL && modelLocation.toString().startsWith("crystalmod:scaffold")){
				return true;
			}
			if(modelLocation.toString().endsWith("#inventory")){
				String name = StringUtils.chopAtFirst(modelLocation.toString(), "#");
				if(CUSTOM_RENDERS.containsKey(new ResourceLocation(name))){
					return true;
				}
			}
			return CUSTOM_MODELS.containsKey(modelLocation);
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception {
			if(ENABLE_TEST_SCAFFOLD_MODEL && modelLocation.toString().startsWith("crystalmod:scaffold")){
				WoodType type = WoodType.OAK;
				String name = modelLocation.toString();
				if(name.startsWith("crystalmod:scaffold_birch")){
					type = WoodType.BIRCH;
				}
				if(name.startsWith("crystalmod:scaffold_spruce")){
					type = WoodType.SPRUCE;
				}
				if(name.startsWith("crystalmod:scaffold_jungle")){
					type = WoodType.JUNGLE;
				}
				if(name.startsWith("crystalmod:scaffold_acacia")){
					type = WoodType.ACACIA;
				}
				if(name.startsWith("crystalmod:scaffold_darkoak")){
					type = WoodType.DARK_OAK;
				}
				final WoodType endType = type;
				return new IModel(){

					@Override
					public Collection<ResourceLocation> getDependencies()
					{
						return Collections.emptySet();
					}

					@Override
					public Collection<ResourceLocation> getTextures()
					{
						return ImmutableSet.of();
					}

					@Override
					public IBakedModel bake( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
					{
						return ModelScaffold.getModelForType(endType);
					}

					@Override
					public IModelState getDefaultState()
					{
						return TRSRTransformation.identity();
					}
					
				};
			}
			boolean hasCustomItemRender = false;
			if(modelLocation.toString().endsWith("#inventory")){
				String name = StringUtils.chopAtFirst(modelLocation.toString(), "#");
				hasCustomItemRender = CUSTOM_RENDERS.containsKey(new ResourceLocation(name));
			}
			if(hasCustomItemRender){
				return ModelLoaderRegistry.getModelOrMissing(CrystalMod.resourceL("null"));
			}
			return new IModel(){

				@Override
				public Collection<ResourceLocation> getDependencies()
				{
					return Collections.emptySet();
				}

				@Override
				public Collection<ResourceLocation> getTextures()
				{
					return ImmutableSet.of();
				}

				@Override
				public IBakedModel bake( IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
				{
					return CUSTOM_MODELS.get(modelLocation).getModel();
				}

				@Override
				public IModelState getDefaultState()
				{
					return TRSRTransformation.identity();
				}
				
			};
		}
    	
    }
    
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
    	CUSTOM_MODELS.put(model.getModelLoc(), model);
    }
    
    public static ItemMinecartRender MinecartRenderer3d = new ItemMinecartRender();
    public static ItemBoatRender BoatRenderer3d = new ItemBoatRender();

    @SubscribeEvent
    public void onBakeModel(final ModelBakeEvent event) {
    	for(CustomBakedModel model : CUSTOM_MODELS.values()){
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
    	
    	if(Config.vanillaBoats3d){
    		ModelResourceLocation boat = ModelLoader.getInventoryVariant("minecraft:oak_boat");
    		event.getModelRegistry().putObject(boat, new CustomItemModelFactory(null, BoatRenderer3d));
    		boat = ModelLoader.getInventoryVariant("minecraft:boat");
    		event.getModelRegistry().putObject(boat, new CustomItemModelFactory(null, BoatRenderer3d));
	    	for(Item item : new Item[] {Items.BIRCH_BOAT, Items.SPRUCE_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT}){
	    		ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
	    		Object obj = event.getModelRegistry().getObject(model);
		        
		        if(obj instanceof IBakedModel)
		        {
		        	event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj, BoatRenderer3d));
		        }
			}
    	}
    	
        for(Entry<ResourceLocation, ICustomItemRenderer> entry : CUSTOM_RENDERS.entrySet())
		{
        	if(entry.getValue().getModels().isEmpty()){
				ModelResourceLocation model = new ModelResourceLocation(entry.getKey(), "inventory");
		        Object obj = event.getModelRegistry().getObject(model);
		        
		        if(obj instanceof IBakedModel)
		        {
		        	event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj, entry.getValue()));
		        }
        	} else {
        		for(ModelResourceLocation model : entry.getValue().getModels()){
        			Object obj = event.getModelRegistry().getObject(model);
    		        
    		        if(obj instanceof IBakedModel)
    		        {
    		        	event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj, entry.getValue()));
    		        }
        		}
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
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/pipe_connector"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/item_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/fluid_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/storage_square"));
        for(int i = 0; i < TileEntityPipePowerCU.Tier.values().length; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/power_square_"+i));
        }
        for(int i = 0; i < 4; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/rfpower_square_"+i));
        }
        
        for(CrystalColors.Special type : CrystalColors.Special.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass"));
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass_tinted"));
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass_painted"));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/decorative/dense_darkness"));
    	
        for(WoodType type : WoodType.values()){
        	 event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/scaffold/"+type.getName()+"_scaffolding"));
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
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/case_piston_head"));
        
        for(EnumDyeColor type : EnumDyeColor.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/coral/coral_"+type.getName().toLowerCase()));
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
        event.getMap().registerSprite(ModFluids.fluidInk.getStill());
        event.getMap().registerSprite(ModFluids.fluidInk.getFlowing());
        event.getMap().registerSprite(ModFluids.fluidTears.getStill());
        event.getMap().registerSprite(ModFluids.fluidTears.getFlowing());
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystalcluster"));
        
        event.getMap().registerSprite(FluidColored.LiquidStill);
        event.getMap().registerSprite(FluidColored.LiquidFlowing);
        CropOverlays.registerIcons(event.getMap());
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/flume/rail_ramp"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/crop/seed_background"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/crop/seed_overlay"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/food/eucalyptus_overlay_basic"));
        for(WrappedFoodType wrappedFood : WrappedFoodType.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:items/food/eucalyptus/"+wrappedFood.getName()));
        }
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/icon_sword"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/icon_pickaxe"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/icon_bow"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/pipe/icon_filter"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:items/bat/leather_handle"));
    }
    
    @Override
	public EntityPlayer getPlayerForUsername(String playerName) {
    	return null;
    }
    
    @Override
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
    
    @Override
	public World getWorld(int dim) {
		return null;
	}
    
    @Override
	public boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
    
    @SideOnly(Side.CLIENT)
	public static GuiGuideBase forcedChapter;
    
    @Override
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
    
    @Override
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
		ModelScaffold.reloadModels();
	}
}
