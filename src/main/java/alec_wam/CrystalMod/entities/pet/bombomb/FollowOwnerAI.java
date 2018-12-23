package alec_wam.CrystalMod.entities.pet.bombomb;

import alec_wam.CrystalMod.entities.EntityOwnable;
import alec_wam.CrystalMod.entities.ai.AIBase;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.util.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class FollowOwnerAI extends AIBase<EntityOwnable> {

	@Override
	public void reset(EntityOwnable entity) 
	{	
	}

	@Override
	public void onUpdateCommon(EntityOwnable entity) 
	{
	}

	@Override
	public void onUpdateClient(EntityOwnable entity) 
	{
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdateServer(EntityOwnable entity) 
	{
		EntityLivingBase target = entity.getAttackTarget();
		
		if(entity instanceof EntityBombomb){
			BombombAICombat combatAI = ((EntityBombomb)entity).getAIManager().getAI(BombombAICombat.class);
			if(combatAI !=null){
				target = combatAI.getAttackTarget();
			}
		}
		if(target !=null)return;
		
		final EntityLiving entityPathController = entity;
		final EntityPlayer entityPlayer = (EntityPlayer) entity.getOwner();
		
		if (entityPlayer != null)
		{
			entityPathController.getLookHelper().setLookPositionWithEntity(entityPlayer, 10.0F, entity.getVerticalFaceSpeed());
			
			final double distanceToPlayer = EntityUtil.getDistanceToEntity(entity, entityPlayer);

			if (distanceToPlayer >= 10.0D && entityPlayer.getEntityBoundingBox() != null)
			{
				final int playerX = net.minecraft.util.math.MathHelper.floor(entityPlayer.posX) - 2;
				final int playerY = net.minecraft.util.math.MathHelper.floor(entityPlayer.getEntityBoundingBox().minY);
				final int playerZ = net.minecraft.util.math.MathHelper.floor(entityPlayer.posZ) - 2;

				for (int i = 0; i <= 4; ++i)
				{
					for (int i2 = 0; i2 <= 4; ++i2)
					{
						BlockPos below = new BlockPos(playerX + i, playerY - 1, playerZ + i2);
						BlockPos pos = new BlockPos(playerX + i, playerY, playerZ + i2);
						IBlockState posState = entity.getEntityWorld().getBlockState(pos);
						BlockPos above = new BlockPos(playerX + i, playerY + 1, playerZ + i2);
						IBlockState aboveState = entity.getEntityWorld().getBlockState(above);
						if ((i < 1 || i2 < 1 || i > 3 || i2 > 3) && entity.getEntityWorld().getBlockState(below).isSideSolid(entity.getEntityWorld(), below, EnumFacing.UP) && !posState.getBlock().isNormalCube(posState) && !aboveState.getBlock().isNormalCube(aboveState))
						{
							entityPathController.setLocationAndAngles(playerX + i + 0.5F, playerY, playerZ + i2 + 0.5F, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
							entityPathController.getNavigator().clearPathEntity();
						}
					}
				}
			}

			else if (distanceToPlayer >= 4.5D && entity.getNavigator().noPath())
			{
				float speed = entityPlayer.isSprinting() ? MinionConstants.SPEED_WALK * 1.5f : MinionConstants.SPEED_WALK;
				entityPathController.getNavigator().tryMoveToEntityLiving(entityPlayer, speed);
			}

			else if (distanceToPlayer <= 2.0D)
			{
				entityPathController.getNavigator().clearPathEntity();
			}
		}

		else
		{
			//minion.setMovementState(EnumMovementState.MOVE);
		}
	}

	@Override
	public void writeToNBT(EntityOwnable entity, NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(EntityOwnable entity, NBTTagCompound nbt) {
	}

}
