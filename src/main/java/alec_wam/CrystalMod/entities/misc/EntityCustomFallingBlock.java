package alec_wam.CrystalMod.entities.misc;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
			Block block = this.getBlock().getBlock();

	        if (this.getBlock().getMaterial() == Material.AIR)
	        {
	            this.setDead();
	        }
	        else
	        {
	            this.prevPosX = this.posX;
	            this.prevPosY = this.posY;
	            this.prevPosZ = this.posZ;

	            if (this.fallTime++ == 0)
	            {
	                /*BlockPos blockpos = new BlockPos(this);

	                if (this.world.getBlockState(blockpos).getBlock() == block)
	                {
	                    this.world.setBlockToAir(blockpos);
	                }
	                else if (!this.world.isRemote)
	                {
	                    this.setDead();
	                    return;
	                }*/
	            }

	            if (!this.hasNoGravity())
	            {
	                this.motionY -= 0.03999999910593033D;
	            }

	            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
	            this.motionX *= 0.9800000190734863D;
	            this.motionY *= 0.9800000190734863D;
	            this.motionZ *= 0.9800000190734863D;

	            if (!this.world.isRemote)
	            {
	                BlockPos blockpos1 = new BlockPos(this);

	                if (this.onGround)
	                {
	                    IBlockState iblockstate = this.world.getBlockState(blockpos1);

	                    if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) //Forge: Don't indent below.
	                    if (BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))))
	                    {
	                        this.onGround = false;
	                        return;
	                    }

	                    this.motionX *= 0.699999988079071D;
	                    this.motionZ *= 0.699999988079071D;
	                    this.motionY *= -0.5D;

	                    if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION)
	                    {
	                        this.setDead();
	                        boolean ok = true; //!this.dontSetBlock
	                        if (ok)
	                        {
	                            if (this.world.mayPlace(block, blockpos1, true, EnumFacing.UP, (Entity)null) && !BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down())) && this.world.setBlockState(blockpos1, this.getBlock(), 3))
	                            {
	                                if (block instanceof BlockFalling)
	                                {
	                                    ((BlockFalling)block).onEndFalling(this.world, blockpos1);
	                                }

	                                if (this.tileEntityData != null && block instanceof ITileEntityProvider)
	                                {
	                                    TileEntity tileentity = this.world.getTileEntity(blockpos1);

	                                    if (tileentity != null)
	                                    {
	                                        NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

	                                        for (String s : this.tileEntityData.getKeySet())
	                                        {
	                                            NBTBase nbtbase = this.tileEntityData.getTag(s);

	                                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
	                                            {
	                                                nbttagcompound.setTag(s, nbtbase.copy());
	                                            }
	                                        }

	                                        tileentity.readFromNBT(nbttagcompound);
	                                        tileentity.markDirty();
	                                    }
	                                }
	                            }
	                            else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
	                            {
	                                this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.getBlock())), 0.0F);
	                            }
	                        }
	                        else if (block instanceof BlockFalling)
	                        {
	                            ((BlockFalling)block).onBroken(this.world, blockpos1);
	                        }
	                    }
	                }
	                else if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)
	                {
	                    if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
	                    {
	                        this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.getBlock())), 0.0F);
	                    }

	                    this.setDead();
	                }
	            }
	        }
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
