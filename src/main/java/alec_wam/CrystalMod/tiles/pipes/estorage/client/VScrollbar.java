package alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.awt.Rectangle;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;

import alec_wam.CrystalMod.items.backpack.gui.GuiBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class VScrollbar {

    protected final IGuiScreen gui;

    protected int xOrigin;
    protected int yOrigin;
    protected int height;

    
    protected int x;
    
    protected int y;
    
    protected Rectangle wholeArea;
    protected Rectangle btnUp;
    protected Rectangle btnDown;
    protected Rectangle thumbArea;

    protected int scrollPos;
    protected int scrollMax;

    protected boolean pressedUp;
    protected boolean pressedDown;
    protected boolean pressedThumb;
    protected int scrollDir;
    protected long timeNextScroll;
    
    protected boolean visible = true;

    public VScrollbar(IGuiScreen gui, int xOrigin, int yOrigin, int height) {
        this.gui = gui;
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.height = height;
    }

    public void adjustPosition() {
        x = xOrigin + gui.getGuiLeft();
        y = yOrigin + gui.getGuiTop();
        wholeArea = new Rectangle(x, y, 11, height);
        btnUp = new Rectangle(x, y, 11, 8);
        btnDown = new Rectangle(x, y + Math.max(0, height - 8), 11, 8);
        thumbArea = new Rectangle(x, y + btnUp.height, 11, Math.max(0, height - (btnUp.height + btnDown.height)));
    }

    public int getScrollPos() {
        return scrollPos;
    }

    public void setScrollPos(int scrollPos) {
        this.scrollPos = limitPos(scrollPos);
    }

    public void scrollBy(int amount) {
        setScrollPos(scrollPos + amount);
    }

    public int getScrollMax() {
        return scrollMax;
    }

    public void setScrollMax(int scrollMax) {
        this.scrollMax = scrollMax;
        setScrollPos(scrollPos);
    }

    public void drawScrollbar(int mouseX, int mouseY) {
        if (visible) {
            boolean hoverUp = btnUp.contains(mouseX, mouseY);
            boolean hoverDown = btnDown.contains(mouseX, mouseY);

            Vector2d vecUp = null;
            
            if (pressedUp) {
            	vecUp = hoverUp ? new Vector2d(245, 0) : new Vector2d(223, 0);
            } else {
            	vecUp = hoverUp ? new Vector2d(234, 0) : new Vector2d(212, 0);
            }

            Vector2d vecDown = null;
            if (pressedDown) {
            	vecDown = hoverDown ? new Vector2d(245, 8) : new Vector2d(223, 8);
            } else {
            	vecDown = hoverDown ? new Vector2d(234, 8) : new Vector2d(212, 8);
            }

            if (scrollDir != 0) {
                long time = Minecraft.getSystemTime();
                if (timeNextScroll - time <= 0) {
                    timeNextScroll = time + 100;
                    scrollBy(scrollDir);
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBackpack.WIDGETS);
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor3f(1, 1, 1);

            if(vecUp !=null){
            	drawTexturedModalRect(btnUp.x, btnUp.y, 0, (int)vecUp.x, (int)vecUp.y, 11, 8);
            }
            
            if(vecDown !=null){
            	drawTexturedModalRect(btnDown.x, btnDown.y, 0, (int)vecDown.x, (int)vecDown.y, 11, 8);
            }

            if (getScrollMax() > 0) {
                int thumbPos = getThumbPosition();
                boolean hoverThumb = thumbArea.contains(mouseX, mouseY) && mouseY >= thumbPos && mouseY < thumbPos + 8;

                Vector2d vecThumb = null;
                if (pressedThumb) {
                	vecThumb = new Vector2d(245, 24);
                } else {
                	vecThumb = hoverThumb ? new Vector2d(234, 24) : new Vector2d(234, 16);
                }
                if(vecThumb !=null)drawTexturedModalRect(thumbArea.x, thumbPos, 100, (int)vecThumb.x, (int)vecThumb.y, 11, 8);
            }
            GL11.glPopAttrib();
        }
    }

    public boolean mouseClicked(int x, int y, int button) {
        if (button == 0) {
            if (getScrollMax() > 0 && thumbArea.contains(x, y)) {
                int thumbPos = getThumbPosition();
                pressedUp = y < thumbPos;
                pressedDown = y >= thumbPos + 8;
                pressedThumb = !pressedUp && !pressedDown;
            } else {
                pressedUp = btnUp.contains(x, y);
                pressedDown = btnDown.contains(x, y);
                pressedThumb = false;
            }

            scrollDir = (pressedDown ? 1 : 0) - (pressedUp ? 1 : 0);
            if (scrollDir != 0) {
                timeNextScroll = Minecraft.getSystemTime() + 200;
                scrollBy(scrollDir);
            }
        }
        return isDragActive();
    }

    public boolean mouseClickMove(int x, int y, int button, long time) {
        if (pressedThumb) {
            int pos = y - (thumbArea.y + 8 / 2);
            int len = thumbArea.height - 8;
            if (len > 0) {
                setScrollPos(Math.round(pos * (float) getScrollMax() / len));
            }
            return true;
        }
        return false;
    }

    public void mouseMovedOrUp(int x, int y, int button) {
        pressedUp = false;
        pressedDown = false;
        pressedThumb = false;
        scrollDir = 0;
    }

    public void mouseWheel(int x, int y, int delta) {
        if (!isDragActive()) {
            scrollBy(-Integer.signum(delta));
        }
    }

    public boolean isDragActive() {
        return pressedUp || pressedDown || pressedThumb;
    }

    protected int getThumbPosition() {
        return thumbArea.y + (thumbArea.height - 8) * scrollPos / getScrollMax();
    }

    protected int limitPos(int pos) {
        return Math.max(0, Math.min(pos, getScrollMax()));
    }

    public void setIsVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
      return visible;
    }
    
    public void drawTexturedModalRect(int x, int y, int z, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x + 0), (double)(y + height), (double)z).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)z).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + 0), (double)z).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)z).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
}
