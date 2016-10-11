package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class WorksiteChunkLoader implements LoadingCallback
{

	public WorksiteChunkLoader(){}

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		for(Ticket tk : tickets)
		{
			if(tk.getModData().hasKey("tilePosition"))
			{
				NBTTagCompound posTag = tk.getModData().getCompoundTag("tilePosition");
				int x = posTag.getInteger("x");
				int y = posTag.getInteger("y");
				int z = posTag.getInteger("z");
				TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
				if(te instanceof IChunkLoaderTile) 
				{
					((IChunkLoaderTile)te).setTicket(tk);
				}
			}
		}
	}

	public static void writeDataToTicket(Ticket tk, BlockPos pos)
	{
		NBTTagCompound posTag = new NBTTagCompound();
		posTag.setInteger("x", pos.getX());
		posTag.setInteger("y", pos.getY());
		posTag.setInteger("z", pos.getZ());
		tk.getModData().setTag("tilePosition", posTag);
	}

}

