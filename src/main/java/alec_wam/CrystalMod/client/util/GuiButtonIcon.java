package alec_wam.CrystalMod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonIcon extends GuiButton
{
    ResourceLocation texture;
    protected int u;
    protected int v;
    protected int hoverOffsetU;
    protected int hoverOffsetV;

    public GuiButtonIcon(int id, int x, int y, int w, int h, int u, int v, ResourceLocation texture)
    {
        this(id, x, y, w, h, u, v, texture, w, 0);
    }

    public GuiButtonIcon(int id, int x, int y, int w, int h, int u, int v, ResourceLocation texture, int hoverOffsetU, int hoverOffsetV)
    {
        super(id, x, y, w, h, "");
        this.u = u;
        this.v = v;
        this.texture = texture;
        this.hoverOffsetU = hoverOffsetU;
        this.hoverOffsetV = hoverOffsetV;
    }

    protected int getU()
    {
        return this.u;
    }

    protected int getV()
    {
        return this.v;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(this.texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int state = this.getHoverState(this.hovered);
            if(!this.enabled){
            	state = 0;
            }
            this.drawTexturedModalRect(this.xPosition, this.yPosition, this.getU() + state * this.hoverOffsetU, this.getV() + state * this.hoverOffsetV, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
