package alec_wam.CrystalMod.entities.misc;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCustomBoat extends EntityBoat
{
    public EntityCustomBoat(World worldIn)
    {
        super(worldIn);
    }

    public EntityCustomBoat(World worldIn, double x, double y, double z)
    {
        this(worldIn);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!this.world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null && this.isPassenger(source.getEntity()))
            {
                return false;
            }
            else
            {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                this.setBeenAttacked();
                boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

                if (flag || this.getDamageTaken() > 40.0F)
                {
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        this.dropItemWithOffset(this.getItemBoat(), 1, 0.0F);
                        this.dropLoot();
                    }

                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void dropLoot(){}

    public final int FIELD_OOCT = 7;
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        //this.previousStatus = this.status;
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_STATUS), FIELD_PREVSTATUS);
        //this.status = this.getBoatStatus();
        EntityBoat.Status status = getBoatStatus();
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, status, FIELD_STATUS);
        
        if (status != EntityCustomBoat.Status.UNDER_WATER && status != EntityCustomBoat.Status.UNDER_FLOWING_WATER)
        {
            //this.outOfControlTicks = 0.0F;

            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.0F, FIELD_OOCT);
        }
        else
        {
            //++this.outOfControlTicks;
        	float ooct = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_OOCT);
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, ooct + 1.0F, FIELD_OOCT);
        }
        
        float ooct = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_OOCT);
        if (!this.world.isRemote && ooct >= 60.0F)
        {
            this.removePassengers();
        }

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();
        
        this.tickLerp();

        if (this.canPassengerSteer())
        {
            if (this.getPassengers().size() == 0 || !(this.getPassengers().get(0) instanceof EntityPlayer))
            {
                this.setPaddleState(false, false);
            }

            this.updateMotion();

            if (this.world.isRemote)
            {
                this.controlBoat();
                this.world.sendPacketToServer(new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
        }

        for (int i = 0; i <= 1; ++i)
        {
        	float[] paddlePositions = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, 5);
            if (this.getPaddleState(i))
            {
                paddlePositions[i] = (float)((double)paddlePositions[i] + 0.01D);
            }
            else
            {
                paddlePositions[i] = 0.0F;
            }
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, paddlePositions, 5);
        }

        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.<Entity>getTeamCollisionPredicate(this));

        if (!list.isEmpty())
        {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity = (Entity)list.get(j);

                if (!entity.isPassenger(this))
                {
                    if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
                    {
                        entity.startRiding(this);
                    }
                    else
                    {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }
    }

    public final int FIELD_LERP = 9;
    public final int FIELD_LERP_Y = 11;
    public final int FIELD_LERP_Z = 12;
    public final int FIELD_LERP_X_ROT= 14;
    public final int FIELD_BOAT_YAW = 13;
    public final int FIELD_BOAT_PITCH = 10;
    
    public void tickLerp()
    {
    	int lerp = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_LERP);
    	double lerpXRot = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_LERP_X_ROT);
    	double lerpY = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_LERP_Y);
    	double lerpZ = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_LERP_Z);
    	double boatPitch = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_BOAT_PITCH);
    	double boatYaw = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_BOAT_YAW);
        if (lerp > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (boatPitch - this.posX) / (double)lerp;
            double d1 = this.posY + (lerpY - this.posY) / (double)lerp;
            double d2 = this.posZ + (lerpZ - this.posZ) / (double)lerp;
            double d3 = MathHelper.wrapDegrees(boatYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)lerp);
            this.rotationPitch = (float)((double)this.rotationPitch + (lerpXRot - (double)this.rotationPitch) / (double)lerp);
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, lerp-1, FIELD_LERP);
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }
    
    public final int FIELD_DELTA_ROTATION = 8;
    public final int FIELD_MOMENTUM = 6;
    public final int FIELD_WATERLEVEL = 19;
    public final int FIELD_GLIDE = 20;
    public final int FIELD_STATUS = 21;
    public final int FIELD_PREVSTATUS = 22;
    public final int FIELD_LAST_YD = 23;
    
    public void updateMotion()
    {
        double d0 = -0.03999999910593033D;
        double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;
        //this.momentum = 0.05F;
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.05F, FIELD_MOMENTUM);
        
        EntityBoat.Status previousStatus = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_PREVSTATUS);
        EntityBoat.Status status = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_STATUS);

        if (previousStatus == EntityBoat.Status.IN_AIR && status != EntityBoat.Status.IN_AIR && status != EntityBoat.Status.ON_LAND)
        {
            //this.waterLevel = this.getEntityBoundingBox().minY + (double)this.height;
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, this.getEntityBoundingBox().minY + (double)this.height, FIELD_WATERLEVEL);
            this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.height) + 0.101D, this.posZ);
            this.motionY = 0.0D;
            //this.lastYd = 0.0D;
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.0D, FIELD_LAST_YD);
            //this.status = EntityBoat.Status.IN_WATER;
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, EntityBoat.Status.IN_WATER, FIELD_STATUS);
        }
        else
        {
            if (status == EntityBoat.Status.IN_WATER)
            {
            	double waterLevel = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_WATERLEVEL);
                d2 = (waterLevel - this.getEntityBoundingBox().minY) / (double)this.height;
                //this.momentum = 0.9F;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.9F, FIELD_MOMENTUM);
            }
            else if (status == EntityBoat.Status.UNDER_FLOWING_WATER)
            {
                d1 = -7.0E-4D;
                //this.momentum = 0.9F;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.9F, FIELD_MOMENTUM);
            }
            else if (status == EntityBoat.Status.UNDER_WATER)
            {
                d2 = 0.009999999776482582D;
                //this.momentum = 0.45F;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.45F, FIELD_MOMENTUM);
            }
            else if (status == EntityBoat.Status.IN_AIR)
            {
                //this.momentum = 0.9F;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.9F, FIELD_MOMENTUM);
            }
            else if (status == EntityBoat.Status.ON_LAND)
            {
            	float boatGlide = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_GLIDE);
                //this.momentum = this.boatGlide;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, boatGlide, FIELD_MOMENTUM);

                if (this.getControllingPassenger() instanceof EntityPlayer)
                {
                    //this.boatGlide /= 2.0F;
                    ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, boatGlide / 2.0F, FIELD_GLIDE);
                }
            }

            float momentum = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_MOMENTUM);
            
            this.motionX *= (double)momentum;
            this.motionZ *= (double)momentum;
            //this.deltaRotation *= momentum;
            float deltaRotation = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_DELTA_ROTATION);
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, deltaRotation * momentum, FIELD_DELTA_ROTATION);
            this.motionY += d1;

            if (d2 > 0.0D)
            {
                double d3 = 0.65D;
                this.motionY += d2 * 0.06153846016296973D;
                double d4 = 0.75D;
                this.motionY *= 0.75D;
            }
        }
    }


    public final int FIELD_LEFT_INPUT = 15;
    public final int FIELD_RIGHT_INPUT = 16;
    public final int FIELD_FORWARD_INPUT = 17;
    public final int FIELD_BACK_INPUT = 18;
    public void controlBoat()
    {
        if (this.isBeingRidden())
        {
            float f = 0.0F;

            boolean leftInputDown = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_LEFT_INPUT);
            boolean rightInputDown = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_RIGHT_INPUT);
            boolean forwardInputDown = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_FORWARD_INPUT);
            boolean backInputDown = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_BACK_INPUT);

            if (leftInputDown)
            {
                //this.deltaRotation += -1.0F;
                float deltaRotation = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_DELTA_ROTATION);
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, deltaRotation - 1.0F, FIELD_DELTA_ROTATION);
            }

            if (rightInputDown)
            {
                //++this.deltaRotation;
            	float deltaRotation = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_DELTA_ROTATION);
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, deltaRotation + 1.0F, FIELD_DELTA_ROTATION);
            }

            if (rightInputDown != leftInputDown && !forwardInputDown && !backInputDown)
            {
                f += 0.005F;
            }

            this.rotationYaw += (float)ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_DELTA_ROTATION);

            if (forwardInputDown)
            {
                f += 0.04F;
            }

            if (backInputDown)
            {
                f -= 0.005F;
            }

            this.motionX += (double)(MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
            this.motionZ += (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * f);
            this.setPaddleState(rightInputDown && !leftInputDown || forwardInputDown, leftInputDown && !rightInputDown || forwardInputDown);
        }
    }
    
    @Nullable
    public EntityBoat.Status getUnderwaterStatus()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER && d0 < (double)BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos))
                        {
                            if (((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() != 0)
                            {
                                EntityBoat.Status entityboat$status = EntityBoat.Status.UNDER_FLOWING_WATER;
                                return entityboat$status;
                            }

                            flag = true;
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return flag ? EntityBoat.Status.UNDER_WATER : null;
    }
    
    public boolean checkInWater()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        //this.waterLevel = Double.MIN_VALUE;
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, Double.MIN_VALUE, FIELD_WATERLEVEL);
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IBlockState iblockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);

                        if (iblockstate.getMaterial() == Material.WATER)
                        {
                            float f = BlockLiquid.getLiquidHeight(iblockstate, this.world, blockpos$pooledmutableblockpos);
                            //this.waterLevel = Math.max((double)f, waterLevel);
                            double waterLevel = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_WATERLEVEL);
                            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, Math.max((double)f, waterLevel), FIELD_WATERLEVEL);
                            flag |= axisalignedbb.minY < (double)f;
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return flag;
    }
    
    public EntityBoat.Status getBoatStatus()
    {
        EntityBoat.Status entityboat$status = this.getUnderwaterStatus();

        if (entityboat$status != null)
        {
            //this.waterLevel = this.getEntityBoundingBox().maxY;
        	ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, this.getEntityBoundingBox().maxY, FIELD_WATERLEVEL);
            return entityboat$status;
        }
        else if (this.checkInWater())
        {
            return EntityBoat.Status.IN_WATER;
        }
        else
        {
            float f = this.getBoatGlide();

            if (f > 0.0F)
            {
                //this.boatGlide = f;
                ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, f, FIELD_GLIDE);
                return EntityBoat.Status.ON_LAND;
            }
            else
            {
                return EntityBoat.Status.IN_AIR;
            }
        }
    }
}