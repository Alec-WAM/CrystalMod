package alec_wam.CrystalMod.tiles.xp;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.client.IGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiXPTank extends GuiScreen implements IGuiScreen {

	public TileEntityXPTank tank;
	private int guiLeft, guiTop;
	
	
	public GuiXPTank(TileEntityXPTank tank){
		this.tank = tank;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		this.guiLeft = (this.width - 200) / 2;	
        this.guiTop = (this.height - 106) / 2;
		this.buttonList.add(new GuiButton(0, guiLeft + 35, guiTop + 40, 20, 20, "+"));
		this.buttonList.add(new GuiButton(1, guiLeft + 55, guiTop + 40, 20, 20, "-"));
		this.buttonList.add(new GuiButton(2, guiLeft + 80, guiTop + 40, 20, 20, "+5"));
		this.buttonList.add(new GuiButton(3, guiLeft + 100, guiTop + 40, 20, 20, "-5"));
		this.buttonList.add(new GuiButton(4, guiLeft + 125, guiTop + 40, 20, 20, "+10"));
		this.buttonList.add(new GuiButton(5, guiLeft + 145, guiTop + 40, 20, 20, "-10"));
	}
	
	@Override
	public void actionPerformed(GuiButton button) throws IOException{
		if(tank !=null){
			int value = 0;
			boolean add = false;
			boolean change = false;
			if(button.id == 0){
				value = 1;
				add = true;
				change = true;
			}
			if(button.id == 1){
				value = 1;
				add = false;
				change = true;
			}
			if(button.id == 2){
				value = 5;
				add = true;
				change = true;
			}
			if(button.id == 3){
				value = 5;
				add = false;
				change = true;
			}
			if(button.id == 4){
				value = 10;
				add = true;
				change = true;
			}
			if(button.id == 5){
				value = 10;
				add = false;
				change = true;
			}
			
			if(change){
				int levelValue = add ? XpUtil.getExperienceForLevel(value) : value;
				tank.changeXP(CrystalMod.proxy.getClientPlayer(), levelValue, add);
				CrystalModNetwork.sendToServer(new PacketXPTank(tank.getPos(), levelValue, add));
				return;
			}
		}
		super.actionPerformed(button);
	}
	
	public static final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/gui/xp_tank.png");
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 200, 106);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		int i = tank.xpCon.getXpBarCapacity();
        int xpLevel = tank.xpCon.getExperienceLevel();

        if (i > 0)
        {
            int k = (int)(tank.xpCon.getExperience() * 183.0F);
            int l = guiTop+32;
            this.drawTexturedModalRect(guiLeft + 8, l, 0, 64, 182, 5);

            if (k > 0)
            {
                this.drawTexturedModalRect(guiLeft + 8, l, 0, 69, k, 5);
            }
        }

        if (xpLevel > 0)
        {
            String s = "" + xpLevel;
            int i1 = (guiLeft +8) + (183/2) - (this.fontRendererObj.getStringWidth(s) / 2);
            int j1 = guiTop + 20;
            this.fontRendererObj.drawString(s, i1 + 1, j1, 0);
            this.fontRendererObj.drawString(s, i1 - 1, j1, 0);
            this.fontRendererObj.drawString(s, i1, j1 + 1, 0);
            this.fontRendererObj.drawString(s, i1, j1 - 1, 0);
            this.fontRendererObj.drawString(s, i1, j1, 8453920);
        }
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
}
