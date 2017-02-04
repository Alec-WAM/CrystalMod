package alec_wam.CrystalMod.entities.minions.ai;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MinionRandomPositionGenerator
{
    /**
     * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vec3d staticVector = new Vec3d(0.0D, 0.0D, 0.0D);

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    public static Vec3d findRandomTarget(EntityLivingBase entitycreatureIn, int xz, int y)
    {
        /**
         * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
         * of par3Vec3d, then points to the tile for which creature.getBlockPathWeight returns the highest number
         */
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3d)null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    public static Vec3d findRandomTargetBlockTowards(EntityLivingBase entitycreatureIn, int xz, int y, Vec3d targetVec3)
    {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        /**
         * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
         * of par3Vec3d, then points to the tile for which creature.getBlockPathWeight returns the highest number
         */
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    public static Vec3d findRandomTargetBlockAwayFrom(EntityLivingBase entitycreatureIn, int xz, int y, Vec3d targetVec3d)
    {
        staticVector = (new Vec3d(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3d);
        /**
         * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
         * of par3Vec3d, then points to the tile for which creature.getBlockPathWeight returns the highest number
         */
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3d, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vec3d findRandomTargetBlock(EntityLivingBase entitycreatureIn, int xz, int y, Vec3d targetVec3d)
    {
        Random random = entitycreatureIn.getRNG();
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1 = false;

        for (int j1 = 0; j1 < 10; ++j1)
        {
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVec3d == null || (double)l * targetVec3d.xCoord + (double)i1 * targetVec3d.zCoord >= 0.0D)
            {
                l = l + MathHelper.floor(entitycreatureIn.posX);
                k1 = k1 + MathHelper.floor(entitycreatureIn.posY);
                i1 = i1 + MathHelper.floor(entitycreatureIn.posZ);
                BlockPos blockpos1 = new BlockPos(l, k1, i1);

                if (!flag1)
                {
                    float f1 = entitycreatureIn instanceof IBlockWeighted ? ((IBlockWeighted)entitycreatureIn).getBlockPathWeight(blockpos1) : 0.0F;

                    if (f1 > f)
                    {
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vec3d((double)i, (double)j, (double)k);
        }
        else
        {
            return null;
        }
    }
}