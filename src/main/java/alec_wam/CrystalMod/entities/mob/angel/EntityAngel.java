package alec_wam.CrystalMod.entities.mob.angel;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.entities.mob.devil.EntityDevil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAngel extends EntityMob
{
    public EntityAngel(World worldIn)
    {
        super(worldIn);
        this.moveHelper = new EntityAngel.AIMoveControl(this);
        this.setSize(0.8F, 1.6F);
        this.experienceValue = 6;
    }

    /**
     * Tries to move the entity towards the specified location.
     */
    public void move(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        this.fallDistance = 0;
        this.setNoGravity(true);
    }

    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAngel.AIChargeAttack());
        this.tasks.addTask(8, new EntityAngel.AIMoveRandom());
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[] {EntityAngel.class}));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityDevil>(this, EntityDevil.class, true));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    public static void registerFixesVex(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityAngel.class);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_VEX;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);
    }

    class AIChargeAttack extends EntityAIBase
    {
        public AIChargeAttack()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return EntityAngel.this.getAttackTarget() != null && !EntityAngel.this.getMoveHelper().isUpdating() && EntityAngel.this.rand.nextInt(7) == 0 ? EntityAngel.this.getDistanceSqToEntity(EntityAngel.this.getAttackTarget()) > 4.0D : false;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting()
        {
            return EntityAngel.this.getMoveHelper().isUpdating() /*&& EntityAngel.this.isCharging()*/ && EntityAngel.this.getAttackTarget() != null && EntityAngel.this.getAttackTarget().isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            EntityLivingBase entitylivingbase = EntityAngel.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
            EntityAngel.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.0D);
            //EntityAngel.this.setIsCharging(true);
            EntityAngel.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
            //EntityAngel.this.setIsCharging(false);
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = EntityAngel.this.getAttackTarget();

            if (EntityAngel.this.getEntityBoundingBox().expand(0.15, 0.15, 0.15).intersectsWith(entitylivingbase.getEntityBoundingBox()))
            {
                EntityAngel.this.attackEntityAsMob(entitylivingbase);
            }
            else
            {
                double d0 = EntityAngel.this.getDistanceSqToEntity(entitylivingbase);

                if (d0 < 9.0D)
                {
                    Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                    EntityAngel.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.0D);
                }
            }
        }
    }

    class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(EntityAngel vex)
        {
            super(vex);
        }

        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - EntityAngel.this.posX;
                double d1 = this.posY - EntityAngel.this.posY;
                double d2 = this.posZ - EntityAngel.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double)MathHelper.sqrt(d3);

                if (d3 < EntityAngel.this.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityAngel.this.motionX *= 0.5D;
                    EntityAngel.this.motionY *= 0.5D;
                    EntityAngel.this.motionZ *= 0.5D;
                }
                else
                {
                    EntityAngel.this.motionX += d0 / d3 * 0.05D * this.speed;
                    EntityAngel.this.motionY += d1 / d3 * 0.05D * this.speed;
                    EntityAngel.this.motionZ += d2 / d3 * 0.05D * this.speed;

                    if (EntityAngel.this.getAttackTarget() == null)
                    {
                        EntityAngel.this.rotationYaw = -((float)MathHelper.atan2(EntityAngel.this.motionX, EntityAngel.this.motionZ)) * (180F / (float)Math.PI);
                        EntityAngel.this.renderYawOffset = EntityAngel.this.rotationYaw;
                    }
                    else
                    {
                        double d4 = EntityAngel.this.getAttackTarget().posX - EntityAngel.this.posX;
                        double d5 = EntityAngel.this.getAttackTarget().posZ - EntityAngel.this.posZ;
                        EntityAngel.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        EntityAngel.this.renderYawOffset = EntityAngel.this.rotationYaw;
                    }
                }
            }
        }
    }

    class AIMoveRandom extends EntityAIBase
    {
        public AIMoveRandom()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return !EntityAngel.this.getMoveHelper().isUpdating() && EntityAngel.this.rand.nextInt(14) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting()
        {
            return false;
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
            BlockPos blockpos = new BlockPos(EntityAngel.this);

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(EntityAngel.this.rand.nextInt(15) - 7, EntityAngel.this.rand.nextInt(5) - 3, EntityAngel.this.rand.nextInt(15) - 7);
                BlockPos blockpos2 = blockpos1.add(0, 1, 0);
                IBlockState state = EntityAngel.this.world.getBlockState(blockpos1);
                IBlockState state2 = EntityAngel.this.world.getBlockState(blockpos2);
                
                if (state.getBlock().isAir(state, world, blockpos1) && state2.getBlock().isAir(state2, world, blockpos2))
                {
                    EntityAngel.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

                    if (EntityAngel.this.getAttackTarget() == null)
                    {
                        EntityAngel.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }

                    break;
                }
            }
        }
    }
}