package alec_wam.CrystalMod.tiles.machine.crafting.press;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPress extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/press.png");
	public TileEntityPress tileMachine;
	ElementDualScaled progress;
	public GuiPress(EntityPlayer player, TileEntityPress tilePart)
    {
        super(new ContainerPress(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "Crystal Press";
    }
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Right.png", 48, 16)));
	    
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	}
}
