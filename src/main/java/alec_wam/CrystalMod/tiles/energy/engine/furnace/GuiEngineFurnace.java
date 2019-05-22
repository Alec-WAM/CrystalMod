package alec_wam.CrystalMod.tiles.energy.engine.furnace;

import java.util.List;

import alec_wam.CrystalMod.client.gui.ElementDualScaled;
import alec_wam.CrystalMod.client.gui.ElementEnergy;
import alec_wam.CrystalMod.client.gui.GuiElementContainer;
import alec_wam.CrystalMod.util.Lang;
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
	public void initGui(){
		super.initGui();		
		//System.out.println(""+this.tileFurnace.energyStorage);
		ElementEnergy energyElement = new ElementEnergy(this, 8+100, 22, this.tileFurnace.energyStorage);
		addElement(energyElement);
		//;
		ElementDualScaled fuel = (ElementDualScaled) (new ElementDualScaled(this, 54, 28){
			@Override
			public void addTooltip(List<String> list) {
				if(tileFurnace.fuel.getValue() > 0){
					int seconds = tileFurnace.fuel.getValue() / 20;
					list.add(Lang.localizeFormat("engine.furnace.fuel", ""+seconds));
				}
			}
		}).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/scale_flame.png", 32, 16);
		
		this.speed = (ElementDualScaled) addElement(fuel);	    
	}
	
	@Override
	public void addTooltips(List<String> tooltip) {
		if(isPointInRegion(77 - guiLeft, 36 - guiTop, 24, 15, mouseX, mouseY)){
			tooltip.add(Lang.localizeFormat("power.cu.tick", tileFurnace.getFuelValue()));
		}
		super.addTooltips(tooltip);
	}
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    if(this.speed !=null)this.speed.setQuantity(this.tileFurnace.getScaledFuel(16));
	}
}
