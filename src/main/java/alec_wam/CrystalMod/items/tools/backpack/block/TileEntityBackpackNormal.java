package alec_wam.CrystalMod.items.tools.backpack.block;

import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackNormal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBackpackNormal extends TileEntityBackpackInventory {

	public TileEntityBackpackNormal(){
		super();
	}


	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		BackpackOpenSourceBlock source = new BackpackOpenSourceBlock(player, this);
		return new GuiBackpackNormal(inventory, player.inventory, source);
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		return new ContainerBackpackNormal(inventory, player.inventory);
	}


	@Override
	public int getSizeOfBackpackInventory() {
		return Math.min(BackpackNormal.getSizeOfInventory(getBackpack()), 72);
	}
}
