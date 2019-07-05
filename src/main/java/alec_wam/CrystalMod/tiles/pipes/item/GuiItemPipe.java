package alec_wam.CrystalMod.tiles.pipes.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.client.gui.ButtonIcon;
import alec_wam.CrystalMod.client.gui.ButtonIconTooltip;
import alec_wam.CrystalMod.client.gui.ButtonTiny;
import alec_wam.CrystalMod.client.gui.ButtonTooltip;
import alec_wam.CrystalMod.client.gui.GuiContainerBase;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packet.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GuiItemPipe extends GuiContainerBase<ContainerItemPipe> {

	private final TileEntityPipeItem pipe;
	private final Direction facing;
	private Button buttonIO;
	private ButtonIcon buttonRedstone;
	private Button buttonPriorityUp;
	private Button buttonPriorityDown;
	private Button buttonRR;
	private boolean twoFilters;
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("crystalmod:textures/gui/pipe/item.png");
	public GuiItemPipe(int windowId, PlayerEntity player, TileEntityPipeItem pipe, Direction facing) {
		super(new ContainerItemPipe(windowId, player, pipe, facing), player.inventory, new StringTextComponent("ItemPipe"), TEXTURE);
		this.pipe = pipe;
		this.facing = facing;
	}
	
	@Override
	public void init(){
		super.init();
		setupButtons();
	}
	
	public void setupButtons(){
		this.buttons.clear();
		PipeConnectionMode connectionMode = pipe.getConnectionSetting(facing);
		String io = Lang.localize("gui.pipe.io."+connectionMode.name().toLowerCase());
		List<String> ioInfo = Lists.newArrayList();
		if(connectionMode == PipeConnectionMode.IN || connectionMode == PipeConnectionMode.OUT){
			ioInfo.add(Lang.localize("gui.pipe.io."+connectionMode.name().toLowerCase() + ".info"));
		}
		buttonIO = new ButtonTooltip(guiLeft + 5, guiTop + 5, 40, 20, io, ioInfo, new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				pipe.incrsConnectionMode(facing);
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Facing", facing.getIndex());
				nbt.putInt("Mode", pipe.getConnectionSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "IO.Client", nbt));
				setupButtons();
			}
		});
		
		RedstoneMode setting = pipe.getRedstoneSetting(facing);
		int index = setting.ordinal();
		int iconX = 0;
		int iconY = index * 16;
		String redstoneTooltip = Lang.localize("gui.redstone."+setting.name().toLowerCase());
		buttonRedstone = new ButtonIconTooltip(guiLeft + 150, guiTop + 5, iconX, iconY, redstoneTooltip, new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				pipe.incrsRedstoneMode(facing);
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Facing", facing.getIndex());
				nbt.putInt("Mode", pipe.getRedstoneSetting(facing).ordinal());
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Redstone.Client", nbt));
				setupButtons();
			}
		});
		
		buttonPriorityDown = new ButtonTiny(guiLeft + 11, guiTop + 70, 10, 10, "-", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current - 1);
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Facing", facing.getIndex());
				nbt.putInt("Value", current - 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		});
		buttonPriorityUp = new ButtonTiny(guiLeft + 22, guiTop + 70, 10, 10, "+", new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				final int current = pipe.getPriority(facing);
				pipe.setPriority(facing, current + 1);
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Facing", facing.getIndex());
				nbt.putInt("Value", current + 1);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "Priority", nbt));
			}
		});
		boolean rr = pipe.isRoundRobinEnabled(facing);
		String rrString = rr ? "R" : "D";
		List<String> rrInfo = Lists.newArrayList();
		rrInfo.add(Lang.localize("gui.pipe.io."+ (rr ? "round" : "dist")));
		rrInfo.add(Lang.localize("gui.pipe.io."+ (rr ? "round" : "dist") +".info"));
		buttonRR = new ButtonTooltip(guiLeft + 12, guiTop + 35, 20, 20, rrString, rrInfo, new Button.IPressable(){
			@Override
			public void onPress(Button p_onPress_1_) {
				final boolean rr = pipe.isRoundRobinEnabled(facing);
				pipe.setRoundRobin(facing, !rr);;
				CompoundNBT nbt = new CompoundNBT();
				nbt.putInt("Facing", facing.getIndex());
				nbt.putBoolean("Value", !rr);
				CrystalModNetwork.sendToServer(new PacketTileMessage(pipe.getPos(), "RoundRobin", nbt));
				setupButtons();
			}
		});
		
		this.addButton(buttonIO);
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.IN){
			this.addButton(buttonRedstone);
			this.addButton(buttonRR);
		}
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.OUT){
			this.addButton(buttonPriorityDown);
			this.addButton(buttonPriorityUp);
		}
		final boolean oldFilters = twoFilters;
		twoFilters = connectionMode == PipeConnectionMode.BOTH;
		if(oldFilters != twoFilters){
			((ContainerItemPipe)this.container).updateSlots();
			CrystalModNetwork.sendToServer(new PacketGuiMessage("UpdateSlots"));
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		//Render Filter Slot
		if(twoFilters){
			blit(guiLeft + 71, guiTop + 34, 133, 57, 36, 18);
		} else {
			blit(guiLeft + 79, guiTop + 34, 133, 57, 18, 18);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		PipeConnectionMode connectionMode = pipe.getConnectionSetting(facing);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		if(connectionMode == PipeConnectionMode.BOTH || connectionMode == PipeConnectionMode.OUT){
			String str = "" + pipe.getPriority(facing);
			this.font.drawString(str, 22.0F - (this.font.getStringWidth(str) / 2), 60.0F, 4210752);
		}
		String dir = Lang.localize("gui.direction."+facing.getName());
		this.font.drawString(dir, 88.0F - (this.font.getStringWidth(dir) / 2), 10.0F, 4210752);
		
		if(twoFilters){
			boolean emptyFilterIn = pipe.getInFilter(facing).isEmpty();
			boolean emptyFilterOut = pipe.getOutFilter(facing).isEmpty();
			
			if(emptyFilterIn || emptyFilterOut){
				GlStateManager.pushMatrix();
				GlStateManager.translated(80.0, 40, 0.0F);
				GlStateManager.scaled(0.8, 0.8, 1.0);
				String inString = Lang.localize("gui.pipe.io.in");
				if(emptyFilterIn)this.font.drawString(inString, - (this.font.getStringWidth(inString) / 2), 0.0F, 4210752);
				String outString = Lang.localize("gui.pipe.io.out");
				if(emptyFilterOut)this.font.drawString(outString, 23.0F - (this.font.getStringWidth(outString) / 2), 0.0F, 4210752);
				GlStateManager.popMatrix();
			}
		}
		
		GlStateManager.enableLighting();
	}

}
