package alec_wam.CrystalMod.items.tools.backpack;

import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBackpackBlockHandler {

	public TileEntityBackpack createTile(World world);
	
	public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumFacing side);
	
}
