package alec_wam.CrystalMod.tiles.explosives.remover;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileRemoverExplosion extends TileEntityMod implements IMessageHandler {

	public static enum RemovingType {
		REDSTONE {
			@Override
			public List<BlockPos> onExplosion(World world, BlockPos pos) {
				List<BlockPos> list = Lists.newArrayList();
				ImmutableList<BlockPos> posList = BlockUtil.getBlocksInBB(pos, 8, 8, 8);
				for(BlockPos otherPos : posList){
					IBlockState state = world.getBlockState(otherPos);
					if(isRedstoneComp(state)){
						world.setBlockToAir(otherPos);
						list.add(otherPos);
					}
				}
				world.playSound(null, pos, ModSounds.redstone_removed, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return list;
			}
			
			public boolean isRedstoneComp(IBlockState state){
				Block block = state.getBlock();
				if(block == Blocks.REDSTONE_BLOCK || block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_WIRE || block == Blocks.REDSTONE_ORE){
					return true;
				}
				
				if(block == Blocks.REDSTONE_LAMP || block == Blocks.LIT_REDSTONE_LAMP) return true;
				
				if(block == Blocks.NOTEBLOCK || block == Blocks.JUKEBOX) return true;
				
				if(block == Blocks.UNPOWERED_REPEATER || block == Blocks.POWERED_REPEATER){
					return true;
				}
				if(block == Blocks.UNPOWERED_COMPARATOR || block == Blocks.POWERED_COMPARATOR){
					return true;
				}
				
				if(block == Blocks.STONE_PRESSURE_PLATE || block == Blocks.WOODEN_PRESSURE_PLATE || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE || block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE){
					return true;
				}
				
				if(block == Blocks.LEVER || block == Blocks.STONE_BUTTON || block == Blocks.WOODEN_BUTTON) return true;
				
				if(block == Blocks.TRIPWIRE || block == Blocks.TRIPWIRE_HOOK) return true;
				
				if(block == Blocks.DISPENSER || block == Blocks.DROPPER || block == Blocks.OBSERVER || block == Blocks.HOPPER || block == Blocks.DAYLIGHT_DETECTOR || block == Blocks.DAYLIGHT_DETECTOR_INVERTED) return true;
				
				return false;
			}
		}, WATER {
			@Override
			public List<BlockPos> onExplosion(World world, BlockPos pos) {
				List<BlockPos> list = Lists.newArrayList();
				ImmutableList<BlockPos> posList = BlockUtil.getBlocksInBB(pos, 8, 8, 8);
				for(BlockPos otherPos : posList){
					IBlockState state = world.getBlockState(otherPos);
					if(FluidUtil.isFluidBlock(world, otherPos, state)){
						world.setBlockToAir(otherPos);
						list.add(otherPos);
					}
				}
				world.playSound(null, pos, ModSounds.unsplash, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return list;
			}
		}, XP {
			@Override
			public List<BlockPos> onExplosion(World world, BlockPos pos) {
				List<BlockPos> list = Lists.newArrayList();
				AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D).expandXyz(8.0D);
				List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, bb);
				for(EntityXPOrb orb : orbs){
					if(!orb.isDead && orb.isEntityAlive()){
						final BlockPos orbPos = new BlockPos(orb);
						orb.setDead();
						if(!list.contains(orbPos)){
							list.add(orbPos);
						}
					}
				}
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bb);
				for(EntityPlayer player : players){
					if(!player.isDead && !player.capabilities.disableDamage){
						final BlockPos playerPos = new BlockPos(player);
						if(!list.contains(playerPos)){
							list.add(playerPos);
						}
						player.removeExperienceLevel(Integer.MAX_VALUE);
						if(!world.isRemote){
							if(player instanceof EntityPlayerMP){
								CrystalModNetwork.sendTo(new PacketEntityMessage(player, "#ClearXP#"), (EntityPlayerMP)player);
							}
						}
					}
				}
				return list;
			}
		};
		
		RemovingType(){}
		
		public abstract List<BlockPos> onExplosion(World world, BlockPos pos);
	}
	
	public RemovingType type;
	public int timerRemaining;
	
	public TileRemoverExplosion(){
		this(RemovingType.REDSTONE);
	}

	public TileRemoverExplosion(RemovingType type) {
		this.type = type;
	}
	
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Type", type.ordinal());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.type = RemovingType.values()[nbt.getInteger("Type")];
	}
	
	@Override
	public void update(){
		super.update();
		
		if(!getWorld().isRemote){
			if(timerRemaining <= 0){
				if(getWorld().isBlockPowered(getPos())){
					timerRemaining = 3 * TimeUtil.SECOND;
				}
			} else {
				timerRemaining--;
				if(timerRemaining <= 0){
					double explosionX = getPos().getX() + 0.5D;
					double explosionY = getPos().getY() + 0.5D;
					double explosionZ = getPos().getZ() + 0.5D;
					//getWorld().playSound((EntityPlayer)null, explosionX, explosionY, explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
			        getWorld().setBlockToAir(getPos());
					List<BlockPos> particlePosList = type.onExplosion(getWorld(), getPos());
					if(!particlePosList.isEmpty()){
				        NBTTagList posList = new NBTTagList();
				        for(BlockPos pos : particlePosList){
				        	posList.appendTag(NBTUtil.createPosTag(pos));
				        }
				        NBTTagCompound messageData = new NBTTagCompound();
				        messageData.setTag("PosList", posList);
				        if(!getWorld().isRemote){
				        	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Explosion", messageData), this);
				        }
					}
				}
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Explosion")){
			List<BlockPos> particlePosList = Lists.newArrayList();
			
			NBTTagList posList = messageData.getTagList("PosList", Constants.NBT.TAG_COMPOUND);
			
			for(int i = 0; i < posList.tagCount(); i++){
				particlePosList.add(NBTUtil.getPosFromTag(posList.getCompoundTagAt(i)));
			}
			
			double explosionX = getPos().getX() + 0.5D;
			double explosionY = getPos().getY() + 0.5D;
			double explosionZ = getPos().getZ() + 0.5D;
			getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
			for (BlockPos blockpos : particlePosList)
            {
	        	double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
	        	double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
	        	double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
	        	double d3 = d0 - explosionX;
	        	double d4 = d1 - explosionY;
	        	double d5 = d2 - explosionZ;
	        	double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
	        	d3 = d3 / d6;
	        	d4 = d4 / d6;
	        	d5 = d5 / d6;
	        	double d7 = 0.5D / (d6 / (double)5.0D + 0.1D);
	        	d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
	        	d3 = d3 * d7;
	        	d4 = d4 * d7;
	        	d5 = d5 * d7;
	        	getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + explosionX) / 2.0D, (d1 + explosionY) / 2.0D, (d2 + explosionZ) / 2.0D, d3, d4, d5, new int[0]);
	        	getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
            }
		}
	}
	
	
}
