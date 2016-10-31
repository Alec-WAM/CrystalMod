package alec_wam.CrystalMod.proxy;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.Project;

import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.ModelGlass;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.client.model.BakedCustomItemModel;
import alec_wam.CrystalMod.client.model.LayerDragonWings;
import alec_wam.CrystalMod.client.model.LayerHorseAccessories;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.fluids.FluidColored;
import alec_wam.CrystalMod.fluids.Fluids;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.ModelEnderBuffer;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.machine.power.battery.ModelBattery;
import alec_wam.CrystalMod.tiles.pipes.attachments.ModelAttachment;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.render.BakedModelLoader;
import alec_wam.CrystalMod.tiles.tank.ModelTank;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.world.game.tag.TagManager;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
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
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
    public static final List<String> CUSTOM_RENDERS = Lists.newArrayList();//new String[]{"flagItem", "dragonWings", "mobEssence"};
    
    @SubscribeEvent
    public void onBakeModel(final ModelBakeEvent event) {
    	
        for(GlassType type : GlassType.values()){
        	event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.crystalGlass.getRegistryName(), "type="+type.getName()), ModelGlass.INSTANCE);
        }
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.crystalGlass.getRegistryName(), "inventory"), ModelGlass.INSTANCE);
        
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:battery", "normal"), ModelBattery.INSTANCE);
        
        
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:battery", "inventory"), ModelBattery.INSTANCE);
        
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.enderBuffer.getRegistryName(), "normal"), ModelEnderBuffer.INSTANCE);
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.enderBuffer.getRegistryName(), "active=false,facing=north"), ModelEnderBuffer.INSTANCE);
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.enderBuffer.getRegistryName(), "active=true,facing=north"), ModelEnderBuffer.INSTANCE);
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.enderBuffer.getRegistryName(), "inventory"), ModelEnderBuffer.INSTANCE);
        
        ModelCover.map.clear();
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:pipecover", "inventory"), ModelCover.INSTANCE);
        
        ModelAttachment.map.clear();
        event.getModelRegistry().putObject(new ModelResourceLocation(ModItems.pipeAttachmant.getRegistryName(), "inventory"), ModelAttachment.INSTANCE);
        
        for(TankType type : TankType.values()){
        	event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:crystaltank", "type="+type.getName()), ModelTank.INSTANCE);
        }
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:crystaltank", "inventory"), ModelTank.INSTANCE);
        
        for(String s : CUSTOM_RENDERS)
		{
			ModelResourceLocation model = new ModelResourceLocation("crystalmod:" + s, "inventory");
	        Object obj = event.getModelRegistry().getObject(model);
	        
	        if(obj instanceof IBakedModel)
	        {
	        	event.getModelRegistry().putObject(model, new BakedCustomItemModel((IBakedModel)obj));
	        }else{
	        	event.getModelRegistry().putObject(model, new BakedCustomItemModel(null));
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
        if (Fluids.fluidXpJuice != null) {
            event.getMap().registerSprite(Fluids.fluidXpJuice.getStill());
            event.getMap().registerSprite(Fluids.fluidXpJuice.getFlowing());
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
        if (stack == null)
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
