package alec_wam.CrystalMod.tiles.pipes.item;

import alec_wam.CrystalMod.client.gui.GuiButtonIcon;
import alec_wam.CrystalMod.client.gui.GuiButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.RedstoneMode;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiItemPipe extends GuiContainerBase {

	private final TileEntityPipeItem pipe;
	private final EnumFacing facing;
	private GuiButton buttonIO;
	private GuiButtonIcon buttonRedstone;
	private GuiButton buttonPriorityUp;
	private GuiButton buttonPriorityDown;
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/pipe/item.png");
	public GuiItemPipe(EntityPlayer player, TileEntityPipeItem pipe, EnumFacing facing) {
		super(new ContainerItemPipe(player, pipe, facing), TEXTURE);
		this.pipe = pipe;
		this.facing = facing;
	}
	
	@Override
	public void initGui(){
		super.initGui();
		setupButtons();
	}
	
	public void setupButtons(){
		this.buttons.clear();
		String io = Lang.localize("gui.pipe.io."+pipe.getConnectionSetting(facing).name().toLowerCase());
		buttonIO = new GuiButton(0, guiLeft + 5, guiTop + 5, 40, 20, io) {
			@Override
			public void onClick(double mouseX, double mouseY){
				pipe.incrsConnectionMode(facing);
				//TODO Send to Server
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Mode", pipe.getConnectionSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "IO.Client", nbt));
				setupButtons();
			}
		};
		
		RedstoneMode setting = pipe.getRedstoneSetting(facing);
		int index = setting.ordinal();
		int iconX = 0;
		int iconY = index * 16;
		String redstoneTooltip = Lang.localize("gui.redstone."+setting.name().toLowerCase());
		buttonRedstone = new GuiButtonIconTooltip(1, guiLeft + 150, guiTop + 5, iconX, iconY, redstoneTooltip) {
			@Override
			public void onClick(double mouseX, double mouseY){
				pipe.incrsRedstoneMode(facing);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Mode", pipe.getRedstoneSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Redstone.Client", nbt));
				setupButtons();
			}
		};
		
		buttonPriorityDown = new GuiButton(2, guiLeft + 11, guiTop + 70, 10, 10, "-") {
			@Override
			public void onClick(double mouseX, double mouseY){
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current - 1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Value", current - 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		};
		buttonPriorityUp = new GuiButton(2, guiLeft + 22, guiTop + 70, 10, 10, "+") {
			@Override
			public void onClick(double mouseX, double mouseY){
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current + 1);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInt("Facing", facing.getIndex());
				nbt.setInt("Value", current + 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		};
		
		this.addButton(buttonIO);
		this.addButton(buttonRedstone);
		this.addButton(buttonPriorityDown);
		this.addButton(buttonPriorityUp);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		String str = "" + pipe.getPriority(facing);
		this.fontRenderer.drawString(str, 22.0F - (this.fontRenderer.getStringWidth(str) / 2), 60.0F, 4210752);
		String dir = Lang.localize("gui.direction."+facing.getName());
		this.fontRenderer.drawString(dir, 88.0F - (this.fontRenderer.getStringWidth(dir) / 2), 10.0F, 4210752);
		GlStateManager.enableLighting();
	}

}
