package com.alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import com.alec_wam.CrystalMod.client.util.ElementDualScaled;
import com.alec_wam.CrystalMod.client.util.ElementEnergy;
import com.alec_wam.CrystalMod.client.util.GuiElementContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCrystalFurnace extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/crystalfurnace.png");
	public TileEntityCrystalFurnace tileFurnace;
	ElementDualScaled progress;
	ElementDualScaled speed;
	public GuiCrystalFurnace(EntityPlayer player, TileEntityCrystalFurnace tilePart)
    {
        super(new ContainerCrystalFurnace(player, tilePart), TEXTURE);

        this.tileFurnace = tilePart;
        this.name = "Crystal Furnace";
    }
	
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileFurnace.getEnergyStorage());
		addElement(energyElement);
		this.progress = ((ElementDualScaled)addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Arrow_Right.png", 48, 16)));
	    this.speed = ((ElementDualScaled)addElement(new ElementDualScaled(this, 56, 44).setSize(16, 16).setTexture("crystalmod:textures/gui/elements/Scale_Flame.png", 32, 16)));
	    
	}
	
	protected void updateElementInformation()
	{
		super.updateElementInformation();
	    this.progress.setQuantity(this.tileFurnace.getScaledProgress(24));
	    this.speed.setQuantity(this.tileFurnace.getScaledSpeed(16));
	}
}
