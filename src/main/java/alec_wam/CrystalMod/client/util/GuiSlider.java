package alec_wam.CrystalMod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlider extends GuiButton
{
    public int sliderValue;
    public boolean dragging;
    private final int minValue;
    private final int maxValue;

    public GuiSlider(int buttonId, int x, int y)
    {
        this(buttonId, x, y, 0, 10);
    }

    public GuiSlider(int buttonId, int x, int y, int minValueIn, int maxValue)
    {
        super(buttonId, x, y, 75, 15, "");
        this.sliderValue = 1;
        this.minValue = minValueIn;
        this.maxValue = maxValue;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
        	float fin = 0.0f;
            if (this.dragging)
            {
            	float q = ((float)((mouseX - (this.xPosition + 4)) / (float)(this.width - 8)));
                
                float d = minValue + (maxValue - minValue) * MathHelper.clamp(q, 0.0F, 1.0F);
                float f = 1.0F * (float)Math.round(d / 1.0F);
                fin = MathHelper.clamp((f - minValue) / (maxValue - minValue), 0.0F, 1.0F);
                this.sliderValue = (int) (maxValue * fin);
            }

            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(fin * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20/2);
            this.drawTexturedModalRect(this.xPosition + (int)(fin * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20/2);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            float q = ((float)((mouseX - (this.xPosition + 4)) / (float)(this.width - 8)));
            
            float d = minValue + (maxValue - minValue) * MathHelper.clamp(q, 0.0F, 1.0F);
            float f = 1.0F * (float)Math.round(d / 1.0F);
            this.sliderValue = (int) (maxValue * MathHelper.clamp((f - minValue) / (maxValue - minValue), 0.0F, 1.0F));
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }
}