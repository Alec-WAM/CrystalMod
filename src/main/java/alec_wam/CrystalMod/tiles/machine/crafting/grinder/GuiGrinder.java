package alec_wam.CrystalMod.tiles.machine.crafting.grinder;

import alec_wam.CrystalMod.client.gui.ElementDualScaled;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.tabs.GuiContainerTabbed;
import alec_wam.CrystalMod.tiles.machine.crafting.CraftingMachineIOTab;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiGrinder extends GuiContainerTabbed<ContainerGrinder> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/grinder.png");
	public TileEntityGrinder tileMachine;
	ElementDualScaled progress;
	public GuiGrinder(int windowId, PlayerEntity player, TileEntityGrinder tilePart)
    {
        super(new ContainerGrinder(windowId, player, tilePart), player.inventory, new TranslationTextComponent("block.crystalmod.machine_grinder"), TEXTURE);

        this.tileMachine = tilePart;
        this.name = Lang.translateToLocal("block.crystalmod.machine_grinder");
    }
	
	@Override
	public void init(){
		super.init();
		tabManager.getTabs().clear();
		tabManager.add(new CraftingMachineIOTab(tileMachine));
		
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
