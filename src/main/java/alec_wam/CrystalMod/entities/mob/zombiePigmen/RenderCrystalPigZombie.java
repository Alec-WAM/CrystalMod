package alec_wam.CrystalMod.entities.mob.zombiePigmen;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrystalPigZombie extends RenderBiped<EntityCrystalPigZombie>
{
    private static final ResourceLocation[] ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation[]{new ResourceLocation("crystalmod:textures/entities/blue_zombie_pigman.png"), new ResourceLocation("crystalmod:textures/entities/red_zombie_pigman.png"), new ResourceLocation("crystalmod:textures/entities/green_zombie_pigman.png"), new ResourceLocation("crystalmod:textures/entities/dark_zombie_pigman.png"), new ResourceLocation("crystalmod:textures/entities/pure_zombie_pigman.png")};

    public RenderCrystalPigZombie(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelZombie(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        });
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCrystalPigZombie entity)
    {
        return ZOMBIE_PIGMAN_TEXTURE[entity.getColor()];
    }
    
    public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityCrystalPigZombie> {

        @Override
        public Render<? super EntityCrystalPigZombie> createRenderFor(RenderManager manager) {
          return new RenderCrystalPigZombie(manager);
        }
    }
}
