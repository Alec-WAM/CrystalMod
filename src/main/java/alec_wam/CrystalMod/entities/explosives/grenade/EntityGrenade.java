package alec_wam.CrystalMod.entities.explosives.grenade;

import java.util.UUID;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.util.RotatedAxes;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.Vector3d;
import alec_wam.CrystalMod.util.Vector3f;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityGrenade extends Entity implements IEntityAdditionalSpawnData
{
	private static final DataParameter<Integer> BOUNCES = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> EXPLODED = EntityDataManager.createKey(EntityGrenade.class, DataSerializers.BOOLEAN);
	private EntityLivingBase thrower;
    public EntityGrenade(World worldIn)
    {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
    }

    public EntityGrenade(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public EntityGrenade(World worldIn, EntityLivingBase throwerIn)
    {
        this(worldIn, throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        this.thrower = throwerIn;
    }
    
    public RotatedAxes axes = new RotatedAxes();
	public Vector3f angularVelocity = new Vector3f(0, 0, 0);
	public float prevRotationRoll = 0f;
   
	public boolean spinEffect(){
		return true;
	}
	
    public void setHeadingFromThrower(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        
        
        this.setThrowableHeading((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        
        //axes.setAngles(entityThrower.rotationYaw + 90, spinEffect() ? entityThrower.rotationPitch : 0F, 0);
        
        if(spinEffect()){
        	angularVelocity = new Vector3f(0, 0, 10);
        }
        
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            this.motionY += entityThrower.motionY;
        }
    	/*setPosition(entityThrower.posX, entityThrower.posY + entityThrower.getEyeHeight(), entityThrower.posZ);
    	axes.setAngles(entityThrower.rotationYaw + 90F, spinEffect() ? entityThrower.rotationPitch : 0F, 0F);
		rotationYaw = prevRotationYaw = spinEffect() ? entityThrower.rotationYaw + 90F : 0F;
		rotationPitch = prevRotationPitch = entityThrower.rotationPitch;
		//Give the grenade velocity in the direction the player is looking
		float speed = velocity;
		motionX = axes.getXAxis().x * speed;
		motionY = axes.getXAxis().y * speed;
		motionZ = axes.getXAxis().z * speed;*/
    }
    
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
		
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        axes.setAngles(rotationYaw, rotationPitch, 0F);
    }
    
    @Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int i, boolean b)
	{
		
	}
    
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        super.setVelocity(x, y, z);
    	/*this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }*/
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }
    
    @Override
	protected void entityInit() {
		dataManager.register(BOUNCES, 0);
		dataManager.register(EXPLODED, false);
    }
    
     
    @Override
    public void onUpdate(){
    	//this.lastTickPosX = this.posX;
        //this.lastTickPosY = this.posY;
        //this.lastTickPosZ = this.posZ;
        super.onUpdate();
        
        
		Vector3d posVec = new Vector3d(this.posX, this.posY, this.posZ);
    	Vector3d motVec = new Vector3d(this.motionX, this.motionY, this.motionZ);
    	Vector3d nextPosVec = new Vector3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult hit = this.world.rayTraceBlocks(posVec.getVec3(), nextPosVec.getVec3());
        posVec = new Vector3d(this.posX, this.posY, this.posZ);
        nextPosVec = new Vector3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		
		//If we hit block
		if(hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			prevRotationYaw = axes.getYaw();
			prevRotationPitch = axes.getPitch();
			prevRotationRoll = axes.getRoll();
			if(angularVelocity.lengthSquared() > 0.00000001F)
				axes.rotateLocal(angularVelocity.length(), angularVelocity.normal());
			
			
			Vector3d hitVec = new Vector3d(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
			Vector3d preHitMotVec = hitVec.subtract(posVec);
			Vector3d postHitMotVec = motVec.subtract(preHitMotVec);
			EnumFacing sideHit = hit.sideHit;
			switch(sideHit)
			{
				case UP : case DOWN : postHitMotVec.set(postHitMotVec.x, -postHitMotVec.y, postHitMotVec.z); break;
				case EAST : case WEST : postHitMotVec.set(-postHitMotVec.x, postHitMotVec.y, postHitMotVec.z); break;
				case NORTH : case SOUTH : postHitMotVec.set(postHitMotVec.x, postHitMotVec.y, -postHitMotVec.z); break;
			}
			double lambda = Math.abs(motVec.lengthSquared()) < 0.00000001F ? 1F : postHitMotVec.length() / motVec.length();
			postHitMotVec.scale(0.8 / 2);
			
			posX += preHitMotVec.x + postHitMotVec.x;
			posY += preHitMotVec.y + postHitMotVec.y;
			posZ += preHitMotVec.z + postHitMotVec.z;
			
			motionX = postHitMotVec.x / lambda;
			motionY = postHitMotVec.y / lambda;
			motionZ = postHitMotVec.z / lambda;
			
			motVec = new Vector3d(motionX, motionY, motionZ);
			float randomSpinner = 90F;
			angularVelocity.add((float)rand.nextGaussian() * randomSpinner, (float)rand.nextGaussian() * randomSpinner, (float)rand.nextGaussian() * randomSpinner).scale((float)motVec.lengthSquared());
		}
		else
		{
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
		}
    	
    	if(this.ticksExisted > TimeUtil.SECOND * getFuse()){
    		if(!this.world.isRemote){
    			explodeServer();
    			this.world.setEntityState(this, (byte)3);
    		} 
    		if(hasExploded()){
    			this.setDead();
    		}
    	}
    	
    	if (!this.hasNoGravity())
        {
            this.motionY -= (double)getGravityVelocity();
        }
		setPosition(posX, posY, posZ);
    }
    
    public double getGravityVelocity() {
		return 0.03F;
	}

	public abstract int getFuse();
    public abstract void explodeServer();
    public abstract void explodeClient();

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
        	explodeClient();
        	//Client Explosion
            /*for (int i = 0; i < 8; ++i)
            {
                
            }*/
        }
    }

    public int getTimesBounced() {
		return dataManager.get(BOUNCES);
	}

    public void setTimesBounced(int times) {
		dataManager.set(BOUNCES, times);
	}
	
    public boolean hasExploded() {
		return dataManager.get(EXPLODED);
	}

    public void setExploded(boolean value) {
		dataManager.set(EXPLODED, value);
	}
    
    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
    	switch (result.typeOfHit) {
    		case BLOCK: {
    			/*Block block = world.getBlockState(result.getBlockPos()).getBlock();

    			int bounces = getTimesBounced();
    			double speed = motionX * motionY * motionZ;
    			
    			//ModLogger.info(""+speed);
    			EnumFacing dir = result.sideHit;
				if(bounces < 3) {
					Vector3d currentMovementVec = new Vector3d(motionX, motionY, motionZ);
    				Vector3d normalVector = new Vector3d(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()).normal();
    				Vector3d posVec = new Vector3d(posX, posY, posZ);
    				Vector3d bounceVec = normalVector.scale(-0.5 * currentMovementVec.dot(normalVector));
    				Vector3d movementVec = bounceVec.add(currentMovementVec.scale(0.3));
    				double bounceAmt = movementVec.length();
    				boolean notDown = dir !=EnumFacing.UP && movementVec.y >= 0;
    				//if(bounceAmt > 0.05 && notDown){
    					//ModLogger.info("Bounce Speed: "+bounceAmt);
        				
        				motionX = movementVec.x;
	    				motionY = movementVec.y;
	    				motionZ = movementVec.z;
	
	    				if(!world.isRemote)
	    					setTimesBounced(getTimesBounced() + 1);
    				//}
    			}*/

    			break;
    		}
    		case ENTITY: {
    			/*if(!world.isRemote && pos.entityHit != null && pos.entityHit instanceof EntityLivingBase && pos.entityHit != getThrower()) {
    				EntityLivingBase thrower = getThrower();
    				pos.entityHit.attackEntityFrom(thrower != null ? thrower instanceof EntityPlayer ? DamageSource.causeThrownDamage(this, thrower) : DamageSource.causeMobDamage(thrower) : DamageSource.GENERIC, 12);
    				if(isFire())
    					pos.entityHit.setFire(5);
    				else if(world.rand.nextInt(3) == 0)
    					((EntityLivingBase) pos.entityHit).addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 0));
    			}*/

    			break;
    		}
    		default: break;
    	}
    	
    	if (!this.world.isRemote)
        {
        	//Client Explosion
            //this.world.setEntityState(this, (byte)3);
            //this.setDead();
        }
    }
    
    @Override
	protected void writeEntityToNBT(NBTTagCompound tags) 
	{
		if(thrower != null)
			tags.setString("Thrower", thrower.getName());
		tags.setFloat("RotationYaw", axes.getYaw());
		tags.setFloat("RotationPitch", axes.getPitch());
	}
    
    @Override
	protected void readEntityFromNBT(NBTTagCompound tags) 
	{
		thrower = getThrower(tags.getString("Thrower"));
		rotationYaw = tags.getFloat("RotationYaw");
		rotationPitch = tags.getFloat("RotationPitch");
		axes.setAngles(rotationYaw, rotationPitch, 0F);
	}
    
    @Nullable
    public EntityLivingBase getThrower(String name)
    {
    	EntityLivingBase thrower = null;
    	if (name != null && !name.isEmpty())
        {
            thrower = this.world.getPlayerEntityByName(name);

            if (thrower == null && this.world instanceof WorldServer)
            {
                try
                {
                    Entity entity = ((WorldServer)this.world).getEntityFromUuid(UUID.fromString(name));

                    if (entity instanceof EntityLivingBase)
                    {
                        thrower = (EntityLivingBase)entity;
                    }
                }
                catch (Throwable var2)
                {
                    thrower = null;
                }
            }
        }

        return thrower;
    }
    
    @Override
	public void writeSpawnData(ByteBuf data) 
	{
		data.writeInt(thrower == null ? 0 : thrower.getEntityId());
		data.writeFloat(axes.getYaw());
		data.writeFloat(axes.getPitch());
	}

	@Override
	public void readSpawnData(ByteBuf data) 
	{
		thrower = (EntityLivingBase)world.getEntityByID(data.readInt());
		setRotation(data.readFloat(), data.readFloat());
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
		axes.setAngles(rotationYaw, rotationPitch, 0F);
		if(spinEffect())
			angularVelocity = new Vector3f(0F, 0F, 10F);
	}
}