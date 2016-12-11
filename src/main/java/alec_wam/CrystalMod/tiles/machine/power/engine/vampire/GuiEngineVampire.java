package alec_wam.CrystalMod.tiles.machine.power.engine.vampire;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEngineVampire extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/enginevampire.png");
	public TileEntityEngineVampire tileEngine;
	public GuiEngineVampire(EntityPlayer player, TileEntityEngineVampire tilePart)
    {
        super(new ContainerEngineVampire(player, tilePart), TEXTURE);

        this.tileEngine = tilePart;
        this.name = "Vampiric Engine";
    }
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 104, 24, this.tileEngine.energyStorage);
		addElement(energyElement);
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	}
	
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		int maxFuel = tileEngine.maxFuel.getValue();
		int fuel = maxFuel-tileEngine.fuel.getValue();
		if(fuel > 0){
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			bindTexture(TEXTURE);
			int offset = (int)((fuel * 21) / maxFuel);
			drawModalRectWithCustomSizedTexture(49, 36+offset, 176, offset, 21, 21, 256, 256);
			GlStateManager.popMatrix();
		}
	}
}
