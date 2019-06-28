package alec_wam.CrystalMod.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;

public class InfoBoxBuilder {

	public int boxX;
	public int boxY;
	public int boxTextWidth = 0;
	public int boxHeight;
	public List<String> textLines;
	public int finalLineCount;
	public final Rectangle2d boxShape;
	public final FontRenderer font;
	
	public InfoBoxBuilder(int x, int y, int screenWidth, int screenHeight, int maxTextWidth, List<String> lines){
		boxTextWidth = 0;
		textLines = lines;
		font = getEventFont();

        for (String textLine : textLines)
        {
            int textLineWidth = font.getStringWidth(textLine);

            if (textLineWidth > boxTextWidth)
            {
                boxTextWidth = textLineWidth;
            }
        }

        boolean needsWrap = false;

        finalLineCount = 1;
        boxX = x + 12;
        if (boxX + boxTextWidth + 4 > screenWidth)
        {
            boxX = x - 16 - boxTextWidth;
            if (boxX < 4) // if the tooltip doesn't fit on the screen
            {
                if (x > screenWidth / 2)
                {
                    boxTextWidth = x - 12 - 8;
                }
                else
                {
                    boxTextWidth = screenWidth - 16 - x;
                }
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && boxTextWidth > maxTextWidth)
        {
            boxTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap)
        {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<String>();
            for (int i = 0; i < textLines.size(); i++)
            {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, boxTextWidth);
                if (i == 0)
                {
                    finalLineCount = wrappedLine.size();
                }

                for (String line : wrappedLine)
                {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth)
                    {
                        wrappedTooltipWidth = lineWidth;
                    }
                    wrappedTextLines.add(line);
                }
            }
            boxTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (x > screenWidth / 2)
            {
                boxX = x - 16 - boxTextWidth;
            }
            else
            {
                boxX = x + 12;
            }
        }

        boxY = y - 12;
        boxHeight = 8;

        if (textLines.size() > 1)
        {
            boxHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > finalLineCount) {
                boxHeight += 2; // gap between title lines and next lines
            }
        }

        if (boxY < 4)
        {
            boxY = 4;
        }
        else if (boxY + boxHeight + 4 > screenHeight)
        {
            boxY = screenHeight - boxHeight - 4;
        }
        
        boxShape = new Rectangle2d(boxX - 4, boxY - 4, boxTextWidth + 4, boxHeight + 4);
	}
	
	public FontRenderer getEventFont(){
		RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(ItemStackTools.getEmptyStack(), textLines, 0, 0, 0, 0, 0, Minecraft.getInstance().fontRenderer);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return Minecraft.getInstance().fontRenderer;
        }
        //incase the font render is changed
        return event.getFontRenderer();
	}
	
}
