package alec_wam.CrystalMod.tiles.energy.engine.furnace;

import alec_wam.CrystalMod.client.gui.ElementDualScaled;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEngineFurnace extends GuiElementContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/engine_furnace.png");
	public TileEntityEngineFurnace tileFurnace;
	ElementDualScaled speed;
	public GuiEngineFurnace(EntityPlayer player, TileEntityEngineFurnace tilePart)
    {
        super(new ContainerEngineFurnace(player, tilePart), TEXTURE);

        this.tileFurnace = tilePart;
        this.name = "Furnace Engine";
    }
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	public void initGui(){
		super.initGui();		
		//System.out.println(""+this.tileFurnace.energyStorage);
		ElementEnergy energyElement = new ElementEnergy(this, 8+100, 22, this.tileFurnace.energyStorage);
		addElement(energyElement);
		this.speed = ((ElementDualScaled)addElement(new ElementDualScaled(this, 54, 28).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/scale_flame.png", 32, 16)));	    
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    if(this.speed !=null)this.speed.setQuantity(this.tileFurnace.getScaledFuel(16));
	}
}
