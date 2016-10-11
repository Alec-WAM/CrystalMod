package alec_wam.CrystalMod.tiles.pipes.power;

import java.awt.Color;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.machine.crafting.BlockCrystalMachine.MachineType;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GuiPowerTracker extends GuiScreen {

	private void drawStats(int sx, int sy) {
	    /*FontRenderer fontRenderer = getFontRenderer();

	    int valuesCol = Color.BLACK.getRGB();
	    int errorCol = Color.RED.getRGB();
	    int x = sx + TEXT_MARGIN_LEFT;
	    int y = sy + TEXT_MARGIN_TOP;

	    StatData statData = getTileEntity().getStatData();
	    if (statData == null || statData.maxPowerInConduits == 0) {
	      fontRenderer.drawSplitString(EnderIO.lang.localize("gui.powerMonitor.noNetworkError"), x, y, TEXT_WIDTH, errorCol);
	      return;
	    }*/

	    /*RenderHelper.enableGUIStandardItemLighting();
	    itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.crystalPipe, 1, PipeType.POWERCU.getMeta()), x, y);
	    //tooltipConduitStorage.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP, TEXT_WIDTH, 16));
	    
	    itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.crystalMachine, 1, MachineType.FURNACE.getMeta()), x, y + 3 * LINE_Y_OFFSET);
	    //tooltipMachineBuffers.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP + 3 * LINE_Y_OFFSET, TEXT_WIDTH, 16));
	    RenderHelper.disableStandardItemLighting();*/

	    //bindGuiTexture(1);
	    //drawTexturedModalRect(x, y + 2 * LINE_Y_OFFSET, 180, 31, 16, 16);
	    //tooltipAverageOutput.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP + 2 * LINE_Y_OFFSET, TEXT_WIDTH / 2, 16));

	   // drawTexturedModalRect(x + TEXT_WIDTH / 2, y + 2 * LINE_Y_OFFSET, 196, 31, 16, 16);
	    //tooltipAverageInput.setBounds(new Rectangle(TEXT_MARGIN_LEFT + TEXT_WIDTH / 2, TEXT_MARGIN_TOP + 2 * LINE_Y_OFFSET, TEXT_WIDTH / 2, 16));

	    /*StringBuilder sb = new StringBuilder();
	    sb.append(formatPower(statData.powerInConduits));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.ofStr());
	    sb.append(" ");
	    sb.append(formatPower(statData.maxPowerInConduits));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.abrevation());
	    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET, valuesCol, false);
	    
	    sb = new StringBuilder();
	    sb.append(formatPower(statData.powerInMachines));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.ofStr());
	    sb.append(" ");
	    sb.append(formatPower(statData.maxPowerInMachines));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.abrevation());
	    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET + 3 * LINE_Y_OFFSET, valuesCol, false);

	    sb = new StringBuilder();
	    sb.append(formatPowerFloat(statData.aveRfSent));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.abrevation());
	    sb.append(PowerDisplayUtil.perTickStr());
	    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET + 2 * LINE_Y_OFFSET, valuesCol, false);

	    sb = new StringBuilder();
	    sb.append(formatPowerFloat(statData.aveRfReceived));
	    sb.append(" ");
	    sb.append(PowerDisplayUtil.abrevation());
	    sb.append(PowerDisplayUtil.perTickStr());
	    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET + TEXT_WIDTH / 2, y + TEXT_Y_OFFSET + 2 * LINE_Y_OFFSET, valuesCol, false);*/

	}
	
}
