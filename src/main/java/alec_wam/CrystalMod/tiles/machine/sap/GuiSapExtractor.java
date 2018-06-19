package alec_wam.CrystalMod.tiles.machine.sap;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiSapExtractor extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/sap_extractor.png");
	public TileSapExtractor tileMachine;
	public GuiSapExtractor(EntityPlayer player, TileSapExtractor tilePart)
    {
        super(new ContainerSapExtractor(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "Sap Extractor";
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		super.drawGuiContainerBackgroundLayer(partialTick, x, y);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(texture !=null){
			bindTexture(texture);
			
			CrystalColors.Basic type = tileMachine.getTreeType();
			GlStateManager.pushMatrix();
			if(type !=null){
				float r = 1.0f;
				float g = 1.0f;
				float b = 1.0f;
				if(type == CrystalColors.Basic.BLUE){
					r = 0.0f; g = 1.0f; b = 1.0f;
				}
				if(type == CrystalColors.Basic.RED){
					r = 1.0f; g = 0.0f; b = 0.0f;
				}
				if(type == CrystalColors.Basic.GREEN){
					r = 0.0f; g = 1.0f; b = 0.0f;
				}
				if(type == CrystalColors.Basic.DARK){
					r = 0.0f; g = 0.0f; b = 0.0f;
				}
				GlStateManager.color(r, g, b, 1.0F);
			}
			GlStateManager.translate(guiLeft + 65.5, guiTop + 14, 0);
			GlStateManager.scale(0.7, 0.7, 1);
			drawTexturedModalRect(0, 0, 0, 166, 65, 56);
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int progress = tileMachine.getScaledProgress(43);
			drawTexturedModalRect(guiLeft+37, guiTop+73-progress, 176, (43-progress), 24, progress);
			drawTexturedModalRect(guiLeft+116, guiTop+73-progress, 178, 43+(43-progress), 23, 43);
			GlStateManager.popMatrix();
		}
	}
}
