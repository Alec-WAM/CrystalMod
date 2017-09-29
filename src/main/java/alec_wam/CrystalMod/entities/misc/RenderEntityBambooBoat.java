package alec_wam.CrystalMod.entities.misc;

import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;

public class RenderEntityBambooBoat extends RenderBoat {

	public static final ResourceLocation BOAT_TEXTURE = new ResourceLocation("crystalmod:textures/entities/boat_bamboo.png");
	
	public RenderEntityBambooBoat(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBoat entity)
    {
        return BOAT_TEXTURE;
    }

}
