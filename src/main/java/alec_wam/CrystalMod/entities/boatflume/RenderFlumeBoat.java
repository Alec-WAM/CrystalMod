package alec_wam.CrystalMod.entities.boatflume;

import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailAscending;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFlumeBoat extends Render<EntityFlumeBoat>
{
    private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[] {new ResourceLocation("textures/entity/boat/boat_oak.png"), new ResourceLocation("textures/entity/boat/boat_spruce.png"), new ResourceLocation("textures/entity/boat/boat_birch.png"), new ResourceLocation("textures/entity/boat/boat_jungle.png"), new ResourceLocation("textures/entity/boat/boat_acacia.png"), new ResourceLocation("textures/entity/boat/boat_darkoak.png")};
    /** instance of ModelBoat for rendering */
    protected ModelBase modelBoat = new ModelFlumeBoat();

    public RenderFlumeBoat(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void doRender(EntityFlumeBoat entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        double positionOffset = 0.0F;
        
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        double d3 = 0.30000001192092896D;
        Vec3d vec3d = entity.getPos(d0, d1 - positionOffset, d2);
        float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        if (vec3d != null)
        {
            Vec3d vec3d1 = entity.getPosOffset(d0, d1 - positionOffset, d2, 0.30000001192092896D);
            Vec3d vec3d2 = entity.getPosOffset(d0, d1 - positionOffset, d2, -0.30000001192092896D);

            boolean neg = false;
            
            if (vec3d1 == null)
            {
                vec3d1 = vec3d;
            }

            if (vec3d2 == null)
            {
                vec3d2 = vec3d;
            }

            x += vec3d.xCoord - d0;
            y += (vec3d1.yCoord + vec3d2.yCoord) / 2.0D - (d1 - positionOffset);
            z += vec3d.zCoord - d2;

            
            if(vec3d1.yCoord != vec3d2.yCoord){
            	neg = entity.getHorizontalFacing().getAxisDirection() == EnumFacing.AxisDirection.POSITIVE;
            }
            
            Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);

            if (vec3d3.lengthVector() != 0.0D)
            {
                vec3d3 = vec3d3.normalize();
                if(neg){
                	f3 = -(float)(Math.atan(vec3d3.yCoord) * 73.0D);
                } else {
                	f3 = (float)(Math.atan(vec3d3.yCoord) * 73.0D);
                }
            }
        }

        GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
        GlStateManager.rotate(180 - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
        
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        
        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(EntityFlumeBoat p_188311_1_, float p_188311_2_, float p_188311_3_)
    {
    	double d0 = p_188311_1_.lastTickPosX + (p_188311_1_.posX - p_188311_1_.lastTickPosX) * (double)p_188311_3_;
        double d1 = p_188311_1_.lastTickPosY + (p_188311_1_.posY - p_188311_1_.lastTickPosY) * (double)p_188311_3_;
        double d2 = p_188311_1_.lastTickPosZ + (p_188311_1_.posZ - p_188311_1_.lastTickPosZ) * (double)p_188311_3_;
    	float f3 = p_188311_1_.prevRotationPitch + (p_188311_1_.rotationPitch - p_188311_1_.prevRotationPitch) * p_188311_3_;
    	
    	final double offsetFix = 0.6;
    	Vec3d offset = new Vec3d(0, 0, 0);
    	Vec3d railVec = p_188311_1_.getPos(d0, d1 - offsetFix, d2);
    	if(railVec !=null){
    		//ModLogger.info("Vec "+railVec);
    		int i = MathHelper.floor(railVec.xCoord);
            int j = MathHelper.floor(railVec.yCoord);
            int k = MathHelper.floor(railVec.zCoord);
    		BlockPos currentPos = new BlockPos(i, j, k);
        	IBlockState state = p_188311_1_.getEntityWorld().getBlockState(currentPos);
    		//BlockFlumeRailBase.EnumRailDirection dir = ((BlockFlumeRailBase)state.getBlock()).getRailDirection(p_188311_1_.getEntityWorld(), currentPos, state, p_188311_1_);
    		
    		if(state.getBlock() instanceof BlockFlumeRailAscending){
    			f3 = 45;
    			Vec3d specialVec1 = p_188311_1_.getPosOffset(d0, d1 - offsetFix, d2, 0.30000001192092896D);
    			Vec3d specialVec2 = p_188311_1_.getPosOffset(d0, d1 - offsetFix, d2, -0.30000001192092896D);
    			Vec3d railFixed = railVec.subtract(d0, d1 - offsetFix, d2);
    			
    			if(specialVec1 == null){
    				specialVec1 = railVec;
    			}
    			if(specialVec2 == null){
    				specialVec2 = railVec;
    			}
    			double y = (specialVec1.yCoord + specialVec2.yCoord) / 2.0D - (d1 - offsetFix);
    			Vec3d specialVec3 = specialVec2.addVector(-specialVec1.xCoord, -specialVec1.yCoord, -specialVec1.zCoord);
                
    			if (specialVec3.lengthVector() != 0.0D)
                {
    				specialVec3 = specialVec3.normalize();
    				f3 = (float)(Math.atan(specialVec3.yCoord) * 73.0D);
                }
    			
    			offset = new Vec3d(railFixed.xCoord, y, railFixed.zCoord);
    			
    			
    			/*Vec3d vec3d1 = p_188311_1_.getPosOffset(d0, d1 - 0.5, d2, 0.30000001192092896D);
                Vec3d vec3d2 = p_188311_1_.getPosOffset(d0, d1 - 0.5, d2, -0.30000001192092896D);

                if (vec3d1 == null)
                {
                    vec3d1 = railVec;
                }

                if (vec3d2 == null)
                {
                    vec3d2 = railVec;
                }
    			
                double x = railVec.xCoord - d0;
                double y = (vec3d1.yCoord + vec3d2.yCoord) / 2.0D - (d1 - 0.5);
                double z = railVec.zCoord - d2;
                Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);
                
    			if (vec3d3.lengthVector() != 0.0D)
                {
                    vec3d3 = vec3d3.normalize();
                    //entityYaw = (float)(Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
                    f3 = (float)(Math.atan(vec3d3.yCoord) * 73.0D);
                }
                
                offset = vec3d3;*/
                /*f3 = 42;
    			offset = railVec.subtract(d0, d1-0.5, d2);*/
                //if(vec3d1 !=null)offset = vec3d1.subtract(d0, d1-0.5, d2);
    		}
    	}
    	
    	
    	
    	GlStateManager.rotate(180.0F - p_188311_2_, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
    	if(offset !=null)GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
    	GlStateManager.translate(0.0, -0.4, 0);
        float f = (float)0 - p_188311_3_;
        float f1 = 0 - p_188311_3_;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(f) * f * f1 / 10.0F * (float)1, 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
    }

    public void setupTranslation(double p_188309_1_, double p_188309_3_, double p_188309_5_)
    {
        GlStateManager.translate((float)p_188309_1_, (float)p_188309_3_ + 0.375F, (float)p_188309_5_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFlumeBoat entity)
    {
        return BOAT_TEXTURES[entity.getBoatType().ordinal()];
    }

    @Override
    public boolean isMultipass()
    {
        return true;
    }

    @Override
    public void renderMultipass(EntityFlumeBoat p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_, float p_188300_8_, float p_188300_9_)
    {
        /*GlStateManager.pushMatrix();
        this.setupTranslation(p_188300_2_, p_188300_4_, p_188300_6_);
        this.setupRotation(p_188300_1_, p_188300_8_, p_188300_9_);
        this.bindEntityTexture(p_188300_1_);
        ((IMultipassModel)this.modelBoat).renderMultipass(p_188300_1_, p_188300_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();*/
    }
}