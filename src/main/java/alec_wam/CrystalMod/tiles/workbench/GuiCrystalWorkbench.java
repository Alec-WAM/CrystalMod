package alec_wam.CrystalMod.tiles.workbench;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCrystalWorkbench extends GuiContainer
{
    public int meta = -1;
    
    public GuiCrystalWorkbench(InventoryPlayer playerInv, World worldIn, TileEntityCrystalWorkbench bench)
    {
        super(new ContainerCrystalWorkbench(playerInv, worldIn, bench));
        try{
        	this.meta = worldIn.getBlockState(bench.getPos()).getBlock().getMetaFromState(worldIn.getBlockState(bench.getPos()));
        }catch(Exception e){
        	
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 28, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        String color = "";
        if(meta !=-1){
        	switch(meta){
	        	case 0 : default :{
	        		color = "_blue";
	        		break;
	        	}
	        	case 1 :{
	        		color = "_red";
	        		break;
	        	}
	        	case 2 :{
	        		color = "_green";
	        		break;
	        	}
	        	case 3 :{
	        		color = "_dark";
	        		break;
	        	}
        	}
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("crystalmod:textures/gui/workbench"+color+".png"));
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}