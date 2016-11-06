package alec_wam.CrystalMod.entities.minions.warrior;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.entities.minions.EnumMovementState;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.util.EntityUtil;

public class MinionAIFollow extends AIBase<EntityMinionWarrior> {

	@Override
	public void reset(EntityMinionWarrior minion) 
	{	
	}

	@Override
	public void onUpdateCommon(EntityMinionWarrior minion) 
	{
	}

	@Override
	public void onUpdateClient(EntityMinionWarrior minion) 
	{
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdateServer(EntityMinionWarrior minion) 
	{
		if (minion.getMovementState() == EnumMovementState.FOLLOW)
		{
			
			EntityLivingBase target = null;
			
			MinionAICombat combatAI = minion.getAIManager().getAI(MinionAICombat.class);
			if(combatAI !=null){
				target = combatAI.getAttackTarget();
			}
			if(target !=null)return;
			
			final EntityLiving entityPathController = (EntityLiving) (minion.getRidingEntity() instanceof EntityHorse ? minion.getRidingEntity() : minion);
			final EntityPlayer entityPlayer = (EntityPlayer) minion.getOwner();

			if (entityPathController instanceof EntityHorse)
			{
				final EntityHorse horse = (EntityHorse) entityPathController;

				//This makes the horse move properly.
				if (horse.isHorseSaddled())
				{
					horse.setHorseSaddled(false);
				}
			}
			
			if (entityPlayer != null)
			{
				entityPathController.getLookHelper().setLookPositionWithEntity(entityPlayer, 10.0F, minion.getVerticalFaceSpeed());
				
				final double distanceToPlayer = EntityUtil.getDistanceToEntity(minion, entityPlayer);

				//Crash was reported where bounding box ended up being null.
				if (distanceToPlayer >= 10.0D && entityPlayer.getEntityBoundingBox() != null)
				{
					final int playerX = net.minecraft.util.math.MathHelper.floor_double(entityPlayer.posX) - 2;
					final int playerY = net.minecraft.util.math.MathHelper.floor_double(entityPlayer.getEntityBoundingBox().minY);
					final int playerZ = net.minecraft.util.math.MathHelper.floor_double(entityPlayer.posZ) - 2;

					for (int i = 0; i <= 4; ++i)
					{
						for (int i2 = 0; i2 <= 4; ++i2)
						{
							BlockPos below = new BlockPos(playerX + i, playerY - 1, playerZ + i2);
							BlockPos pos = new BlockPos(playerX + i, playerY, playerZ + i2);
							IBlockState posState = minion.worldObj.getBlockState(pos);
							BlockPos above = new BlockPos(playerX + i, playerY + 1, playerZ + i2);
							IBlockState aboveState = minion.worldObj.getBlockState(above);
							if ((i < 1 || i2 < 1 || i > 3 || i2 > 3) && minion.worldObj.getBlockState(below).isSideSolid(minion.worldObj, below, EnumFacing.UP) && !posState.getBlock().isNormalCube(posState) && !aboveState.getBlock().isNormalCube(aboveState))
							{
								entityPathController.setLocationAndAngles(playerX + i + 0.5F, playerY, playerZ + i2 + 0.5F, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
								entityPathController.getNavigator().clearPathEntity();
							}
						}
					}
				}

				else if (distanceToPlayer >= 4.5D && minion.getNavigator().noPath())
				{
					float speed = entityPathController instanceof EntityHorse ? MinionConstants.SPEED_HORSE_RUN :  entityPlayer.isSprinting() ? MinionConstants.SPEED_SPRINT : MinionConstants.SPEED_WALK;
					entityPathController.getNavigator().tryMoveToEntityLiving(entityPlayer, speed);
				}

				else if (distanceToPlayer <= 2.0D || (entityPathController instanceof EntityHorse && distanceToPlayer <= 3.0D)) //To avoid crowding the player.
				{
					entityPathController.getNavigator().clearPathEntity();
				}
			}

			else
			{
				//minion.setMovementState(EnumMovementState.MOVE);
			}
		}
	}

	@Override
	public void writeToNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(EntityMinionWarrior minion, NBTTagCompound nbt) {
	}

}
