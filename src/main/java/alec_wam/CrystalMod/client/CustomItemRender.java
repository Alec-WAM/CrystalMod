package alec_wam.CrystalMod.client;

import java.util.EnumMap;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.render.MobSkullRenderHelper;
import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.items.ItemMobSkull;
import alec_wam.CrystalMod.items.tools.ItemPoweredShield;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.chests.metal.BlockMetalCrystalChest;
import alec_wam.CrystalMod.tiles.chests.metal.MetalCrystalChestType;
import alec_wam.CrystalMod.tiles.chests.metal.TileEntityMetalCrystalChestRender;
import alec_wam.CrystalMod.tiles.chests.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chests.wireless.TileEntityWirelessChestRender;
import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chests.wooden.BlockWoodenCrystalChest;
import alec_wam.CrystalMod.tiles.chests.wooden.TileEntityWoodenCrystalChestRender;
import alec_wam.CrystalMod.tiles.chests.wooden.WoodenCrystalChestType;
import alec_wam.CrystalMod.tiles.energy.battery.BlockBattery;
import alec_wam.CrystalMod.tiles.energy.battery.ModelBattery;
import alec_wam.CrystalMod.tiles.energy.battery.TileEntityBattery;
import alec_wam.CrystalMod.tiles.energy.battery.TileEntityBatteryRender;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.jar.TileEntityJarRender;
import alec_wam.CrystalMod.tiles.tank.BlockTank;
import alec_wam.CrystalMod.tiles.tank.TileEntityTank;
import alec_wam.CrystalMod.tiles.tank.TileEntityTankRender;
import alec_wam.CrystalMod.tiles.xp.BlockXPTank;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPTankRender;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.RenderUtil;
import alec_wam.CrystalMod.util.XPUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CustomItemRender extends ItemStackTileEntityRenderer
{
    public static CustomItemRender instance = new CustomItemRender();
    private final BannerTileEntity shieldBanner = new BannerTileEntity();
    private final ShieldModel shieldModel = new ShieldModel();
    public static final ResourceLocation SHIELD_BASE_TEXTURE = new ResourceLocation("crystalmod:textures/model/shield/shield.png");
    public static final ResourceLocation SHIELD_PATTERN_TEXTURE = new ResourceLocation("crystalmod:textures/model/shield/shield_pattern.png");
    public static final BannerTextures.Cache POWERED_SHIELD_DESIGNS = new BannerTextures.Cache("powered_shield_", new ResourceLocation("crystalmod:textures/model/shield/shield_pattern.png"), "textures/entity/shield/");
    
    @Override
    public void renderByItem(ItemStack itemStackIn)
    {
        Item item = itemStackIn.getItem();
        Block block = Block.getBlockFromItem(item);
        if (block instanceof BlockWoodenCrystalChest)
        {
        	WoodenCrystalChestType typeOut = ((BlockWoodenCrystalChest)block).type;
            if (typeOut == null)
            {
            	TileEntityWoodenCrystalChestRender.renderChest(0, 0, 0, WoodenCrystalChestType.BLUE, Direction.NORTH, 0, -1);
            }
            else
            {
                TileEntityWoodenCrystalChestRender.renderChest(0, 0, 0, typeOut, Direction.NORTH, 0, -1);
            }
        }
        else if (block instanceof BlockMetalCrystalChest)
        {
        	MetalCrystalChestType typeOut = ((BlockMetalCrystalChest)block).type;
            if (typeOut == null)
            {
            	TileEntityMetalCrystalChestRender.renderChest(0, 0, 0, MetalCrystalChestType.BLUE, Direction.NORTH, 0, -1);
            }
            else
            {
            	TileEntityMetalCrystalChestRender.renderChest(0, 0, 0, typeOut, Direction.NORTH, 0, -1);
            }
        }
        else if (block instanceof BlockWirelessChest)
        {
        	int code = ItemNBTHelper.getInteger(itemStackIn, WirelessChestHelper.NBT_CODE, 0);
        	TileEntityWirelessChestRender.renderChest(0, 0, 0, code, Direction.NORTH, ItemNBTHelper.verifyExistance(itemStackIn, WirelessChestHelper.NBT_OWNER), 0, -1);
        }
        else if (block instanceof BlockJar)
        {
        	IBakedModel ibakedmodel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
        	RenderUtil.renderModel(ibakedmodel, itemStackIn);
        	Potion type = Potions.EMPTY;
    		int count = 0;
    		boolean lamp = false;
    		EnumMap<Direction, Boolean> labels = Maps.newEnumMap(Direction.class); 
    		if(ItemNBTHelper.verifyExistance(itemStackIn, "TILE_DATA")){
    			CompoundNBT tileNBT = ItemNBTHelper.getCompound(itemStackIn).getCompound("TILE_DATA");
    			type = PotionUtils.getPotionTypeFromNBT(tileNBT);
    			count = tileNBT.getInt("Count");
    			lamp = tileNBT.getBoolean("IsShulker");
    			for(Direction facing : Direction.Plane.HORIZONTAL){
    				labels.put(facing, tileNBT.getBoolean("Label."+facing.getName().toUpperCase()));
    			}
    		}    		
    		
        	for(int pass = 0; pass < 2; pass++){
    			TileEntityJarRender.renderInternalJar(0, 0, 0, type, count, lamp, labels, pass);
    		}
        }
        else if (block instanceof BlockBattery)
        {
        	EnumCrystalColorSpecialWithCreative type = ((BlockBattery)block).type;
        	int energy = 0;
        	int maxEnergy = TileEntityBattery.MAX_ENERGY[type.ordinal()];
        	if(ItemNBTHelper.verifyExistance(itemStackIn, TileEntityBattery.NBT_DATA)){
        		CompoundNBT nbt = ItemNBTHelper.getCompound(itemStackIn).getCompound(TileEntityBattery.NBT_DATA);
        		energy = nbt.getInt("Energy");
        	}
        	
        	ModelBattery ibakedmodel = new ModelBattery(itemStackIn, type);
        	RenderUtil.renderModel(ibakedmodel, itemStackIn);
        	
        	TileEntityBatteryRender.renderMeter(Direction.NORTH, energy, maxEnergy, type == EnumCrystalColorSpecialWithCreative.CREATIVE);
        }
        else if (block instanceof BlockTank)
        {
        	IBakedModel ibakedmodel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
        	RenderUtil.renderModel(ibakedmodel, itemStackIn);
        	if(ItemNBTHelper.getCompound(itemStackIn).contains("Tank")){
        		CompoundNBT tankNBT = ItemNBTHelper.getCompound(itemStackIn).getCompound("Tank");
        		if(!tankNBT.contains("Empty")){
		        	EnumCrystalColorSpecialWithCreative type = ((BlockTank)block).type;
		        	int capacity = TileEntityTank.TIER_BUCKETS[type.ordinal()] * Fluid.BUCKET_VOLUME;
		        	FluidStack stack = FluidConversion.loadFluidStackFromNBT(tankNBT);
		        	GlStateManager.pushMatrix();	
		        	GlStateManager.disableLighting();
		        	TileEntityTankRender.renderTankFluid(stack, capacity, 0, 0, 0, stack.getFluid().getLuminosity());
		        	GlStateManager.enableLighting();
		        	GlStateManager.enableBlend();
		        	GlStateManager.popMatrix();		    		
        		}
        	}
        }
        else if (block instanceof BlockXPTank)
        {
        	boolean isEnder = ItemNBTHelper.getBoolean(itemStackIn, "IsEnder", false);
        	IBakedModel ibakedmodel = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState().with(BlockXPTank.ENDER, Boolean.valueOf(isEnder)));
        	RenderUtil.renderModel(ibakedmodel, itemStackIn);
        	if(ItemNBTHelper.getCompound(itemStackIn).contains("XPStorage")){
        		CompoundNBT tankNBT = ItemNBTHelper.getCompound(itemStackIn).getCompound("XPStorage");
        		if(!tankNBT.contains("Empty")){
		        	int xp = tankNBT.getInt("experienceTotal");
		        	GlStateManager.pushMatrix();	
		        	GlStateManager.disableLighting();
		        	TileEntityXPTankRender.renderTankXP(XPUtil.experienceToLiquid(xp), 0, 0, 0, 8);
		        	GlStateManager.enableLighting();
		        	GlStateManager.enableBlend();
		        	GlStateManager.popMatrix();		    		
        		}
        	}
        }
        else if(item instanceof ItemPoweredShield){
        	if (itemStackIn.getChildTag("BlockEntityTag") != null) {
                this.shieldBanner.loadFromItemStack(itemStackIn, ShieldItem.getColor(itemStackIn));
                Minecraft.getInstance().getTextureManager().bindTexture(POWERED_SHIELD_DESIGNS.getResourceLocation(this.shieldBanner.getPatternResourceLocation(), this.shieldBanner.getPatternList(), this.shieldBanner.getColorList()));
             } else {
                Minecraft.getInstance().getTextureManager().bindTexture(SHIELD_BASE_TEXTURE);
             }

             GlStateManager.pushMatrix();
             GlStateManager.scalef(1.0F, -1.0F, -1.0F);
             this.shieldModel.render();
             if (itemStackIn.hasEffect()) {
                this.renderEffect(this.shieldModel::render);
             }

             GlStateManager.popMatrix();
        }
        else if(item instanceof ItemMobSkull){
        	MobSkullRenderHelper.renderSkull(itemStackIn);
        }
        else
        {
            super.renderByItem(itemStackIn);
        }
    }

    public void renderEffect(Runnable renderModelFunction) {
    	GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
    	Minecraft.getInstance().getTextureManager().bindTexture(ItemRenderer.RES_ITEM_GLINT);
    	ItemRenderer.renderEffect(Minecraft.getInstance().getTextureManager(), renderModelFunction, 1);
    }
}