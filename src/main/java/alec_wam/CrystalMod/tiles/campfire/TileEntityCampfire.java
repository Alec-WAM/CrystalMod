package alec_wam.CrystalMod.tiles.campfire;

import java.util.List;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketSpawnParticle;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.BlockUtil.BlockFilter;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileEntityCampfire extends TileEntityMod {

	private int stickCount;
	private int burnTime;
	
	public boolean addStick() {
		if(this.stickCount < 16){
			this.stickCount++;
			return true;
		}
		return false;
	}
	
	public int getStickCount(){
		return stickCount;
	}
	
	public int getBurnTime() {
		return burnTime;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("StickCount", stickCount);
		nbt.setInteger("BurnTime", burnTime);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		stickCount = nbt.getInteger("StickCount");
		burnTime = nbt.getInteger("BurnTime");
	}
	
	@Override
	public void update(){
		super.update();
		
		if(getWorld().isRemote){
			if(burnTime > 0){
				if (this.shouldDoWorkThisTick(5) && getWorld().rand.nextInt(24) == 0)
		        {
					getWorld().playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + getWorld().rand.nextFloat(), getWorld().rand.nextFloat() * 0.7F + 0.3F, false);
		        }
			}
		}
		if(!getWorld().isRemote){
			if(burnTime > 0){
				//DO EFFECTS
				aoeEffects();				
				
				burnTime--;
				if(burnTime == 0){					
					if(stickCount > 0){
						stickCount--;
						burnTime = TimeUtil.MINUTE;
					} else {
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setDouble("x", getPos().getX() + 0.5);
						nbt.setDouble("y", getPos().getY() + 0.3);
						nbt.setDouble("z", getPos().getZ() + 0.5);
						nbt.setDouble("my", 0.2);
						CrystalModNetwork.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.SMOKE_LARGE, nbt), this);
					}
					BlockUtil.markBlockForUpdate(getWorld(), getPos());
				}
			}
		}
	}
	
	public boolean lightFire(){
		if(stickCount > 0 && burnTime <= 0){
			burnTime = TimeUtil.MINUTE;
			return true;
		}
		return false;
	}
	
	public void aoeEffects(){
		AxisAlignedBB BB = new AxisAlignedBB(getPos()).expand(3, 0, 3);
		BB.addCoord(getPos().getX(), getPos().up().getY(), getPos().getZ());
		
		//Melt Snow
		List<BlockPos> snowList = BlockUtil.getBlocksInBBWithFilter(getWorld(), getPos(), 3, 1, 3, new BlockFilter(){

			@Override
			public boolean isValid(World world, BlockPos pos, IBlockState state) {
				return state.getBlock() == Blocks.SNOW_LAYER;
			}
			
		});
		
		if(this.shouldDoWorkThisTick(20)){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setDouble("x", getPos().getX() + 0.5);
			nbt.setDouble("y", getPos().getY() + 0.3);
			nbt.setDouble("z", getPos().getZ() + 0.5);
			double mX = 0.1 * getWorld().rand.nextDouble();
			double mZ = 0.1 * getWorld().rand.nextDouble();
			nbt.setDouble("mx", mX);
			nbt.setDouble("my", 0.4);
			nbt.setDouble("mz", mZ);
			CrystalModNetwork.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.SMOKE_NORMAL, nbt), this);
		}
		
		if(!snowList.isEmpty() && this.burnTime % 100 == 0){
			BlockPos snowPos = snowList.get(MathHelper.getInt(getWorld().rand, 0, snowList.size()-1));
			IBlockState snowState = getWorld().getBlockState(snowPos);
			if(snowState.getBlock() == Blocks.SNOW_LAYER){
				int layers = snowState.getValue(BlockSnow.LAYERS);
				if(layers == 1){
					getWorld().setBlockToAir(snowPos);
				} else {
					getWorld().setBlockState(snowPos, snowState.withProperty(BlockSnow.LAYERS, layers-1));
				}
			}
		}
		
		List<Entity> nearbyEntities = getWorld().getEntitiesWithinAABB(Entity.class, BB);
		if(!nearbyEntities.isEmpty() && this.burnTime % 60 == 0){
			Entity entity = nearbyEntities.get(MathHelper.getInt(getWorld().rand, 0, nearbyEntities.size()-1));
			if(entity instanceof EntityLivingBase){
				EntityLivingBase living = (EntityLivingBase)entity;
				boolean heal = false;
				boolean damage = false;
				
				if(living instanceof EntityPlayer)heal = true;
				if(living instanceof IAnimals)heal = true;
				if(living instanceof EntitySnowman)damage = true;
				
				if(heal){
					living.heal(1.0F);
				}
				if(damage){
					living.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
				}
			}
		}
	}
}
