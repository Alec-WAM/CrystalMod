package alec_wam.CrystalMod.entities.minions;

import java.util.UUID;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.client.DownloadedTextures;

public class RenderMinionBase extends RenderBiped<EntityMinionBase> {

	public RenderMinionBase(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMinionBase(0.0F, false), 0.5F);
		this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
        this.addLayer(new LayerBackItem(this));
	}
	
    public ModelMinionBase getMainModel()
    {
        return (ModelMinionBase)super.getMainModel();
    }
    
    protected void preRenderCallback(EntityMinionBase entitylivingbaseIn, float partialTickTime)
    {
    	super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    	this.mainModel.isChild = false;
    	
        float f = 0.9375F/2;
        GlStateManager.scale(f, f, f);
    }
    
    public void doRender(EntityMinionBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        double d0 = y;

        if (entity.isSneaking())
        {
            d0 = y - 0.125D;
        }
        
        if(entity.isSitting()){
        	d0-=0.25D;
        }

        this.setModelVisibilities(entity);
        super.doRender(entity, x, d0, z, entityYaw, partialTicks);
        //float f = 0.9375F;
        //GlStateManager.scale(f, f, f);
    }
    
    private void setModelVisibilities(EntityMinionBase clientPlayer)
    {
    	ModelMinionBase modelplayer = this.getMainModel();
    	ItemStack itemstack = clientPlayer.getHeldItemMainhand();
        ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
        modelplayer.setInvisible(true);
        modelplayer.bipedHeadwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.HAT);
        modelplayer.bipedBodyWear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
        modelplayer.bipedLeftLegwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        modelplayer.bipedRightLegwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        modelplayer.bipedLeftArmwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
        modelplayer.bipedRightArmwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
        modelplayer.leftArmPose = ModelBiped.ArmPose.EMPTY;
        modelplayer.isSneak = clientPlayer.isSneaking();

        ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

        if (itemstack != null)
        {
            modelbiped$armpose = ModelBiped.ArmPose.ITEM;

            if (clientPlayer.getItemInUseCount() > 0)
            {
                EnumAction enumaction = itemstack.getItemUseAction();

                if (enumaction == EnumAction.BLOCK)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                }
                else if (enumaction == EnumAction.BOW)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }

        if (itemstack1 != null)
        {
            modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

            if (clientPlayer.getItemInUseCount() > 0)
            {
                EnumAction enumaction1 = itemstack1.getItemUseAction();

                if (enumaction1 == EnumAction.BLOCK)
                {
                    modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                }
            }
        }

        if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
        {
            modelplayer.rightArmPose = modelbiped$armpose;
            modelplayer.leftArmPose = modelbiped$armpose1;
        }
        else
        {
            modelplayer.rightArmPose = modelbiped$armpose1;
            modelplayer.leftArmPose = modelbiped$armpose;
        }
    }
	
	@Override
	protected ResourceLocation getEntityTexture(EntityMinionBase entity) {
		try{
			UUID owner = entity.getOwnerId();
			if(owner !=null){
				String name = ProfileUtil.getUsername(owner);
				ResourceLocation skin = DownloadedTextures.getSkin(name);
				if(skin !=null){
					return skin;
				}
			}
		}catch(Exception e){
		}
		return super.getEntityTexture(entity);
	}
	
	public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityMinionBase> {

        @Override
        public Render<? super EntityMinionBase> createRenderFor(RenderManager manager) {
          return new RenderMinionBase(manager);
        }
    }

}
