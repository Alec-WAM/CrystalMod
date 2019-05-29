package alec_wam.CrystalMod.tiles.machine.crafting.press;

import alec_wam.CrystalMod.client.gui.ElementDualScaled;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import alec_wam.CrystalMod.tiles.machine.crafting.ContainerBasicCraftingMachine;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPress extends GuiElementContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/press.png");
	public TileEntityPress tileMachine;
	ElementDualScaled progress;
	public GuiPress(EntityPlayer player, TileEntityPress tilePart)
    {
        super(new ContainerBasicCraftingMachine<TileEntityPress>(player, tilePart, TileEntityPress.CHECKER), TEXTURE);

        this.tileMachine = tilePart;
        this.name = Lang.translateToLocal("block.crystalmod.machine_press");
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/progress_arrow_right.png", 48, 16)));
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    if(this.progress !=null)this.progress.setQuantity(this.tileMachine.getScaledProgress(24));
	}
}
