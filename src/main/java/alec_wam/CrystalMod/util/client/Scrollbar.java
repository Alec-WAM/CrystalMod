package alec_wam.CrystalMod.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

import alec_wam.CrystalMod.items.backpack.gui.GuiBackpack;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;

public class Scrollbar {
    private boolean canScroll = true;

    private int x;
    private int y;
    private int scrollbarWidth;
    private int scrollbarHeight;

    private float scrollDelta = 15f;

    private float currentScroll;
    private boolean wasClicking = false;
    private boolean isScrolling = false;

    public Scrollbar(int x, int y, int scrollbarWidth, int scrollbarHeight) {
        this.x = x;
        this.y = y;
        this.scrollbarWidth = scrollbarWidth;
        this.scrollbarHeight = scrollbarHeight;
    }

    public int getScrollbarWidth() {
        return scrollbarWidth;
    }

    public int getScrollbarHeight() {
        return scrollbarHeight;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean canScroll() {
        return canScroll;
    }

    public float getCurrentScroll() {
        return currentScroll;
    }

    public void setCurrentScroll(float newCurrentScroll) {
        if (newCurrentScroll < 0) {
            newCurrentScroll = 0;
        }

        int scrollbarItselfHeight = 12;

        int max = scrollbarHeight - scrollbarItselfHeight - 3;

        if (newCurrentScroll > max) {
            newCurrentScroll = max;
        }

        currentScroll = newCurrentScroll;
    }

    public void setScrollDelta(float delta) {
        this.scrollDelta = delta;
    }

    public void draw(IGuiScreen gui) {
    	if(!(gui instanceof GuiScreen))return;
    	GuiScreen screen = (GuiScreen)gui;
    	Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBackpack.WIDGETS);
        screen.drawTexturedModalRect(gui.getGuiLeft() + x, gui.getGuiTop() + y + (int) currentScroll, canScroll() ? 232 : 244, 32, 12, 15);
    }

    public void update(int mouseX, int mouseY) {
        if (!canScroll()) {
            isScrolling = false;
            wasClicking = false;
            currentScroll = 0;
        } else {
            int wheel = Mouse.getDWheel();

            wheel = Math.max(Math.min(-wheel, 1), -1);

            if (wheel == -1) {
                setCurrentScroll(currentScroll - scrollDelta);
            } else if (wheel == 1) {
                setCurrentScroll(currentScroll + scrollDelta);
            }

            boolean down = Mouse.isButtonDown(0);

            if (!wasClicking && down && GuiUtil.inBounds(x, y, scrollbarWidth, scrollbarHeight, mouseX, mouseY)) {
                isScrolling = true;
            }

            if (!down) {
                isScrolling = false;
            }

            wasClicking = down;

            if (isScrolling) {
                setCurrentScroll(mouseY - 20);
            }
        }
    }
}
