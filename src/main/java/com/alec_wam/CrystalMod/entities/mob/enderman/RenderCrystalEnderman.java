package com.alec_wam.CrystalMod.entities.mob.enderman;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrystalEnderman extends RenderEnderman
{

    private static final ResourceLocation[] ENDERMAN_TEXTURE = new ResourceLocation[]{new ResourceLocation("crystalmod:textures/entities/enderman/blue_enderman.png"), new ResourceLocation("crystalmod:textures/entities/enderman/red_enderman.png"), new ResourceLocation("crystalmod:textures/entities/enderman/green_enderman.png"), new ResourceLocation("crystalmod:textures/entities/enderman/dark_enderman.png"), new ResourceLocation("crystalmod:textures/entities/enderman/pure_enderman.png")};

	
	public RenderCrystalEnderman(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	protected ResourceLocation getEntityTexture(EntityEnderman entity)
    {
		if(entity instanceof EntityCrystalEnderman){
			EntityCrystalEnderman ender = (EntityCrystalEnderman)entity;
			return ENDERMAN_TEXTURE[ender.getColor()];
		}
        return super.getEntityTexture(entity);
    }
	
	public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityCrystalEnderman> {

        @Override
        public Render<? super EntityCrystalEnderman> createRenderFor(RenderManager manager) {
          return new RenderCrystalEnderman(manager);
        }
    }

}
