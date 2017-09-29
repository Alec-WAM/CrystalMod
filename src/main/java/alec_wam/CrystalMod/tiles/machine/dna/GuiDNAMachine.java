package alec_wam.CrystalMod.tiles.machine.dna;

import alec_wam.CrystalMod.client.util.ElementDualScaled;
import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDNAMachine extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/dnamachine.png");
	public TileEntityDNAMachine tileMachine;
	ElementDualScaled progress;
	public GuiDNAMachine(EntityPlayer player, TileEntityDNAMachine tilePart)
    {
        super(new ContainerDNAMachine(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "DNA Machine";
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 76, 38).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Right.png", 48, 16)));
	    
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	}
}
