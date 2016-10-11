package alec_wam.CrystalMod.tiles.machine.power.engine.furnace;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEngineFurnace extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/enginefurnace.png");
	public TileEntityEngineFurnace tileFurnace;
	ElementDualScaled speed;
	public GuiEngineFurnace(EntityPlayer player, TileEntityEngineFurnace tilePart)
    {
        super(new ContainerEngineFurnace(player, tilePart), TEXTURE);

        this.tileFurnace = tilePart;
        this.name = "Furnace Engine";
    }
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8+100, 22, this.tileFurnace.energyStorage);
		addElement(energyElement);
		this.speed = ((ElementDualScaled)addElement(new ElementDualScaled(this, 54, 28).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/Scale_Flame.png", 32, 16)));
	    
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.speed.setQuantity(this.tileFurnace.getScaledFuel(16));
	}
}
