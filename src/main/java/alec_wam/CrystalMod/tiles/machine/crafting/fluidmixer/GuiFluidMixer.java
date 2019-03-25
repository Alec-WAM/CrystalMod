package alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.ElementEnergy;
import alec_wam.CrystalMod.client.util.ElementFluidScaled;
import alec_wam.CrystalMod.client.util.GuiElementContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.FluidMixerRecipeManager.FluidMixRecipe;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidMixer extends GuiElementContainer{
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/machine/fluidmixer.png");
	public TileEntityFluidMixer tileMachine;
	ElementFluidScaled progressLeft;
	ElementFluidScaled progressRight;
	public GuiFluidMixer(EntityPlayer player, TileEntityFluidMixer tilePart)
    {
        super(new ContainerFluidMixer(player, tilePart), TEXTURE);

        this.tileMachine = tilePart;
        this.name = "Fluid Mixer";
    }
	
	@Override
	public void initGui(){
		super.initGui();
		
		ElementEnergy energyElement = new ElementEnergy(this, 8, 22, this.tileMachine.getEnergyStorage());
		addElement(energyElement);
		this.progressLeft = ((ElementFluidScaled)addElement(new ElementFluidScaled(this, 49, 34).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Fluid_Right.png", 48, 16)));
		this.progressRight = ((ElementFluidScaled)addElement(new ElementFluidScaled(this, 103, 34).setMode(1).setSize(24, 16).setTexture("crystalmod:textures/gui/elements/Progress_Fluid_Left.png", 48, 16)));
	}
	
	@Override
	protected void mouseClicked(int mX, int mY, int mouseButton) {
		if(this.isPointInRegion(80, 61, 16, 16, mX, mY)){
			String nextRecipe = FluidMixerRecipeManager.getNextRecipe(tileMachine.selectedRecipe, mouseButton == 1);
			tileMachine.selectedRecipe = nextRecipe;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Recipe", nextRecipe);
			CrystalModNetwork.sendToServer(new PacketTileMessage(tileMachine.getPos(), "SetRecipe", nbt));
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return;
		}		
		super.mouseClicked(mX, mY, mouseButton);
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2){
		super.drawGuiContainerForegroundLayer(par1, par2);
		List<String> list = Lists.newArrayList();
				
		//TODO Add to drawScreen Layer to make text render
		RenderUtil.renderGuiTank(tileMachine.tankLeft, 32, 23, zLevel, 12, 40, true);
		if(this.isPointInRegion(32, 23, 12, 40, par1, par2)){
			if(tileMachine.tankLeft !=null){
				if(tileMachine.tankLeft.getFluid() !=null){
					FluidStack stack = tileMachine.tankLeft.getFluid();
					list.add(stack.getLocalizedName()+" ("+stack.amount+")");
				}else {
					list.add("Empty");
				}
			}
		}
		RenderUtil.renderGuiTank(tileMachine.tankRight, 132, 23, zLevel, 12, 40, true);
		if(this.isPointInRegion(132, 23, 12, 40, par1, par2)){
			if(tileMachine.tankRight !=null){
				if(tileMachine.tankRight.getFluid() !=null){
					FluidStack stack = tileMachine.tankRight.getFluid();
					list.add(stack.getLocalizedName()+" ("+stack.amount+")");
				}else {
					list.add("Empty");
				}
			}
		}
		
		FluidMixRecipe recipe = FluidMixerRecipeManager.getRecipe(tileMachine.selectedRecipe);
		if(recipe !=null){
			GlStateManager.pushMatrix();
	        RenderHelper.enableGUIStandardItemLighting();
	        this.drawItemStack(recipe.getOutput(), 80, 61);
			GlStateManager.popMatrix();
	        
			if(this.isPointInRegion(80, 61, 16, 16, par1, par2)){
				list.add(recipe.getOutput().getDisplayName());
			}
		}		
		
		if(!list.isEmpty()){
			this.drawTooltipHoveringText(list, par1-guiLeft, par2-guiTop, mc.fontRendererObj);
		}
	}
	
	public void drawItemStack(ItemStack stack, int x, int y)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }
	
	@Override
	protected void updateElementInformation()
	{
		super.updateElementInformation();
		int progress = this.tileMachine.getScaledProgress(24);
	    FluidStack fluidLeft = tileMachine.tankLeft.getFluid();
	    FluidStack fluidRight = tileMachine.tankRight.getFluid();
	    if(fluidLeft !=null){
	    	progressLeft.setFluid(new FluidStack(fluidLeft, 1));
	    	progressLeft.setQuanitity(progress);
	    }else{
	    	progressLeft.setFluid(null);
	    	progressLeft.setQuanitity(0);
	    }
	    if(fluidRight !=null){
	    	progressRight.setFluid(new FluidStack(fluidRight, 1));
	    	progressRight.setQuanitity(progress);
	    }else{
	    	progressRight.setFluid(null);
	    	progressRight.setQuanitity(0);
	    }
	}
}
