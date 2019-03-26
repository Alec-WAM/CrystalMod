package alec_wam.CrystalMod.entities.balloon;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityBalloon extends Entity {
	private static final DataParameter<Byte> COLOR = EntityDataManager.<Byte>createKey(EntityBalloon.class, DataSerializers.BYTE);
	
	public EntityBalloon(World world){
		super(world);
		this.setSize(0.5f, 2.0f);
	}
	
	public EntityBalloon(World world, double x, double y, double z, EnumDyeColor color) {
		this(world);
		this.setPosition(x, y, z);
		this.setColor(color);
	}

	@Override
	public double getYOffset()
    {
        return this.getRidingEntity() !=null && this.getRidingEntity() instanceof EntityPlayer ? 0.5 : 0.0D;
    }
	
	//TODO Max Pop noise on death and make an inflate noise as well on equip
	
	@Override
	public boolean hitByEntity(Entity entityIn)
    {
        return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            if (!this.isDead && !this.world.isRemote)
            {
                this.setDead();
                this.setBeenAttacked();
            }

            return true;
        }
    }
	
	@Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return null;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return super.getCollisionBoundingBox();
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }
    
    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
	
	@Override
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if(!isRiding()){
			this.motionY+=0.01;
		} else {
			Entity entity = this.getRidingEntity();
			if(entity.motionY < 0){
				entity.motionY *=0.8;
				entity.fallDistance = 0;
			}
		}
		
		if(this.posY >= this.getEntityWorld().getHeight() || getEntityWorld().getBlockState(getPosition().up().up()) == Blocks.DIRT.getDefaultState()){
			this.setDead();
		}
		
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.motionX *=0.7;
		this.motionY *=0.9;
		this.motionZ *=0.7;
	}
	
	public EnumDyeColor getColor()
    {
        return EnumDyeColor.byMetadata(((Byte)this.dataManager.get(COLOR)).byteValue() & 15);
    }

    public void setColor(EnumDyeColor color)
    {
        byte c = ((Byte)this.dataManager.get(COLOR)).byteValue();
        this.dataManager.set(COLOR, Byte.valueOf((byte)(c & 240 | color.getMetadata() & 15)));
    }
	
	@Override
	protected void entityInit() {
        this.dataManager.register(COLOR, Byte.valueOf((byte)0));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setColor(EnumDyeColor.byMetadata(compound.getByte("Color")));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setByte("Color", (byte)getColor().getMetadata());
	}

}
