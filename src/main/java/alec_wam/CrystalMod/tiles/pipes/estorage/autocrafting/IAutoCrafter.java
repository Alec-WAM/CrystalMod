package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public interface IAutoCrafter {

	public int getSpeed();
	
	public IItemHandler getPatterns();
	
	public boolean showPatterns();

	public World getWorld();
	
	public BlockPos getPos();
	
	public int getDimension();
	
	public IItemHandler getFacingInventory();
	
}
