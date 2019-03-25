package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackOpenSource;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BackpackOpenSourceBlock implements IBackpackOpenSource {

	public EntityPlayer player;
	public TileEntityBackpack backpack;
	
	public BackpackOpenSourceBlock(EntityPlayer player, TileEntityBackpack backpack){
		this.player = player;
		this.backpack = backpack;
	}
	
	@Override
	public ItemStack getBackpack() {
		return backpack.getBackpack();
	}

	@Override
	public void openMainInventory() {
		BlockPos pos = backpack.getPos();
		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_BACKPACK_BLOCK, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void openUpgradesInventory(int tab) {
		BlockPos pos = backpack.getPos();
		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_BACKPACK_BLOCK + 1 + tab, pos.getX(), pos.getY(), pos.getZ());
	}

}
