package alec_wam.CrystalMod.entities.misc;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityCustomFallingBlock extends EntityFallingBlock implements IMessageHandler {

	private TileEntity tile;
	
	public EntityCustomFallingBlock(World worldIn) {
		super(worldIn);
	}
	
	public EntityCustomFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState)
    {
		super(worldIn, x, y, z, fallingBlockState);
    }

	@Override
	public void onUpdate(){
		if(getBlock() != null){
			super.onUpdate();
		}
	}
	
	protected void writeEntityToNBT(NBTTagCompound compound)
    {
		super.writeEntityToNBT(compound);
    }
	
	protected void readEntityFromNBT(NBTTagCompound compound)
    {
		super.readEntityFromNBT(compound);
		if(tileEntityData !=null){
			tile = TileEntity.create(getWorldObj(), tileEntityData);
			int meta = getBlock().getBlock().getMetaFromState(getBlock());
			ReflectionHelper.setPrivateValue(TileEntity.class, tile, meta, 5);
		}
    }
	
	public TileEntity getTile() {
		return tile;
	}
	
	public void setTile(TileEntity tile){
		this.tile = tile;
		if(tile != null){
			this.tileEntityData = tile.writeToNBT(new NBTTagCompound());
		} else {
			tileEntityData = null;
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("BlockSync")){
			setBlock(Block.getStateById(messageData.getInteger("BlockID")));
			
			if(messageData.hasKey("TileData")){
				TileEntity tile = TileEntity.create(getEntityWorld(), messageData.getCompoundTag("TileData"));
				int meta = getBlock().getBlock().getMetaFromState(getBlock());
				ReflectionHelper.setPrivateValue(TileEntity.class, tile, meta, 5);
				setTile(tile);
			}
		}
	}
	
	public void setBlock(IBlockState state){
		ReflectionHelper.setPrivateValue(EntityFallingBlock.class, this, state, 0);
	}

}
