package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.client.GuiHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public abstract class BasicInterfaceBlock extends ContainerBlockCustom {

	public BasicInterfaceBlock(Properties builder) {
		super(builder);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
	{
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile == null || !(tile instanceof INamedContainerProvider))return false;
		if(worldIn.isRemote)return true;
		if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
		{
			ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;

			GuiHandler.openCustomGui(GuiHandler.TILE_NORMAL, entityPlayerMP, (INamedContainerProvider)tile, buf -> buf.writeBlockPos(pos));
		}
		return true;
	}
}
