package alec_wam.CrystalMod.entities.boatflume;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.entities.boatflume.rails.BlockFlumeRailAscending;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketSpawnParticle;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFlumeBoat extends Entity {

	private static final DataParameter<Integer> BOAT_TYPE = EntityDataManager.<Integer>createKey(EntityFlumeBoat.class, DataSerializers.VARINT);
    
	//Client Sync Variables
	private int turnProgress;
	private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;
    
	public EntityFlumeBoat(World worldIn) {
		super(worldIn);
        this.setSize(1.375F, 0.5625F);
    }

    public EntityFlumeBoat(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }
    
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }
    
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    /**
     * Returns the collision bounding box for this entity
     */
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
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
    
    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getMountedYOffset()
    {
        return -0.1D;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.boatX = x;
        this.boatY = y;
        this.boatZ = z;
        this.boatYaw = (double)yaw;
        this.boatPitch = (double)pitch;
        this.turnProgress = posRotationIncrements + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }
    
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        this.velocityX = this.motionX;
        this.velocityY = this.motionY;
        this.velocityZ = this.motionZ;
    }
    
    @Override
    public void onUpdate()
    {
    	if(world.isRemote){
    		if (this.turnProgress > 0)
            {
                double d4 = this.posX + (this.boatX - this.posX) / (double)this.turnProgress;
                double d5 = this.posY + (this.boatY - this.posY) / (double)this.turnProgress;
                double d6 = this.posZ + (this.boatZ - this.posZ) / (double)this.turnProgress;
                double d1 = MathHelper.wrapDegrees(this.boatYaw - (double)this.rotationYaw);
                //this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
                //this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(d4, d5, d6);
                this.setRotation((float)this.boatYaw, (float)this.boatPitch);
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
    	}else {
	    	
	    	this.prevPosX = this.posX;
	        this.prevPosY = this.posY;
	        this.prevPosZ = this.posZ;
	        
	        this.motionY -= 0.03999999910593033D;
	    	//super.onUpdate();
	    	
	        int k = MathHelper.floor(this.posX);
            int l = MathHelper.floor(this.posY);
            int i1 = MathHelper.floor(this.posZ);
            if (BlockFlumeRailBase.isRailBlock(this.world, new BlockPos(k, l - 1, i1)))
            {
                --l;
            }

            BlockPos currentPos = new BlockPos(k, l, i1);
	    	IBlockState state = getEntityWorld().getBlockState(currentPos);
	    	
	    	if(BlockFlumeRailBase.isRailBlock(state)){
	    		this.moveAlongRail(currentPos, state);
	    	} else {
	    		motionX *=0.05;
	    		motionZ *=0.05;
	    		if(getEntityWorld().isAirBlock(currentPos)){
	    			motionY-=0.1;
	    			this.move(MoverType.SELF, motionX, motionY, motionZ);
	    		}
	    	}
	    	
	    	boolean kill = false;
	    	
	    	IBlockState above = getEntityWorld().getBlockState(currentPos.up());
	    	if(above.getBlock() == Blocks.BRICK_BLOCK){
	    		kill = true;
	    	}
	    	if(kill){
	    		setDead();
	    	}
    	}
    	
    }
    
    public static final int[][][] MATRIX = new int[][][] {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    public double getSlopeAdjustment()
    {
    	//Roll Back speed
        return 0.0078125D;
    }
    
    protected void moveAlongRail(BlockPos pos, IBlockState state)
    {
        this.fallDistance = 0.0F;
        Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = (double)pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        BlockFlumeRailBase blockrailbase = (BlockFlumeRailBase)state.getBlock();

        boolean ramp = blockrailbase instanceof BlockFlumeRailAscending;
        if (ramp)
        {
            flag = true;
            flag1 = !flag;
        }

        double slopeAdjustment = ramp ? 0.0 : getSlopeAdjustment() * 2;
        BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = blockrailbase.getRailDirection(world, pos, state, this);
        boolean slope = blockrailbase$enumraildirection.isAscending();
        
        switch (blockrailbase$enumraildirection)
        {
            case ASCENDING_EAST:
                this.motionX -= slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_WEST:
                this.motionX += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_NORTH:
                this.motionZ += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_SOUTH:
                this.motionZ -= slopeAdjustment;
                ++this.posY;
        }

        int trackMeta = blockrailbase$enumraildirection.getMetadata();
        int[][] aint = MATRIX[trackMeta];
        double d1 = (double)(aint[1][0] - aint[0][0]);
        double d2 = (double)(aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motionX * d1 + this.motionZ * d2;

        if (d4 < 0.0D)
        {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (d5 > 2.0D)
        {
            d5 = 2.0D;
        }

        this.motionX = d5 * d1 / d3;
        this.motionZ = d5 * d2 / d3;
        Entity entity = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);

        double d7 = -Math.sin((double)(rotationYaw * 0.017453292F));
        double d8 = Math.cos((double)(rotationYaw * 0.017453292F)); 
        double currentSpeed = this.motionX * this.motionX + this.motionZ * this.motionZ;
        if(currentSpeed <= 0.01){
        	double speed = blockrailbase.getSpeed(getEntityWorld(), this, pos);

        	boolean move = entity == null;
        	if(entity !=null && entity instanceof EntityLivingBase){
        		boolean input = (double)((EntityLivingBase)entity).moveForward > 0.0;
        		move = true;
        		speed *= (input ? 3.0 : 1.5);
        	}
        	
        	if(move || ramp){
	        	this.motionX += d7 * speed;
	    		this.motionZ += d8 * speed;
	
	        	Vec3d motionVec = new Vec3d(motionX, motionY, motionZ);
	        	Vec3d newMotion = blockrailbase.handleMotion(getEntityWorld(), this, pos, motionVec);
	        	if(motionVec !=newMotion){
	        		motionX = newMotion.xCoord;
	        		motionY = newMotion.yCoord;
	        		motionZ = newMotion.zCoord;
	        	}
	        	flag1 = false;
        	}
        }

        boolean motionStuff = true;
        boolean motionStuff2 = true;
        if (flag1 && motionStuff2)
        {
            double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d17 < 0.03D)
            {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            }
            else
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        double d18 = (double)pos.getX() + 0.5D + (double)aint[0][0] * 0.5D;
        double d19 = (double)pos.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
        double d20 = (double)pos.getX() + 0.5D + (double)aint[1][0] * 0.5D;
        double d21 = (double)pos.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
        d1 = d20 - d18;
        d2 = d21 - d19;
        double d10;

        if (d1 == 0.0D)
        {
            this.posX = (double)pos.getX() + 0.5D;
            d10 = this.posZ - (double)pos.getZ();
        }
        else if (d2 == 0.0D)
        {
            this.posZ = (double)pos.getZ() + 0.5D;
            d10 = this.posX - (double)pos.getX();
        }
        else
        {
            double d11 = this.posX - d18;
            double d12 = this.posZ - d19;
            d10 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        //Cornering
        if(blockrailbase$enumraildirection.isCorner()){
        	double baseX = (d1 * d10);
        	double progressX = baseX * 2.0D;
        	double baseZ = (d2 * d10);
        	double progressZ = baseZ * 2.0D;
        	
        	float angle = rotationYaw;
        	float properAngle = angle;
        	if (vec3d != null)
            {
                Vec3d vec3d1 = getPosOffset(posX, posY, posZ, 0.30000001192092896D);
                Vec3d vec3d2 = getPosOffset(posX, posY, posZ, -0.30000001192092896D);

                if (vec3d1 == null)
                {
                    vec3d1 = vec3d;
                }

                if (vec3d2 == null)
                {
                    vec3d2 = vec3d;
                }
                Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);

                if (vec3d3.lengthVector() != 0.0D)
                {
                    vec3d3 = vec3d3.normalize();
                    properAngle = (float)(Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
                } else {
                	properAngle = (float)(Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
                }
            }
        	
        	
        	if(blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_EAST){
        		if(motionZ > 0 && motionX > 0){
        			angle = properAngle+90;
            		if(progressX > 0.8){
        				angle = -90 + (float)(40.0 * (1.0-progressX));
        			}
        		}
        		
        		if(motionZ < 0 && motionX < 0){
        			angle = properAngle-90;
            		if(progressX < 0.2){
        				angle = -180 - (float)(40.0 * progressX);
        			}
        		}
        	}
        	if(blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_WEST){
        		if(motionX > 0 && motionZ < 0){
        			angle = properAngle-90;
        			if(progressX > -0.2){
        				angle = 180 - (float)(40.0 * progressX);
        			}
        		}
        		if(motionX < 0 && motionZ > 0){
        			angle = properAngle+90;
        			if(progressX < -0.8){
        				angle = 90 - (float)(40.0 * (progressX + 1.0));
        			}
        		}
        	}
        	if(blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.SOUTH_EAST){
        		if(motionX < 0 && motionZ > 0){
        			angle = properAngle-90;
            		if(progressX < 0.2){
        				angle = 0 + (float)(40.0 * progressX);
        			}
        		}
        		if(motionX > 0 && motionZ < 0){
        			angle = properAngle+90;
        			if(progressX > 0.8){
        				angle = -90 - (float)(40.0 * (1.0-progressX));
        			}
        		}
        		
        	}
        	if(blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.SOUTH_WEST){
        		if(motionX > 0 && motionZ > 0){
        			angle = properAngle-90;
        			if(progressX > -0.2){
        				angle = 0 + (float)(40.0 * progressX);
        			}
        		}
        		if(motionX < 0 && motionZ < 0){
        			angle = Math.abs(properAngle) + 90;
        			if(progressX < -0.8){
        				angle = 90 + (float)(40.0 * (progressX + 1.0));
        			}
        		}
        	}
        	if(angle !=this.rotationYaw){
        		this.rotationYaw = angle;
        		this.setRotation(rotationYaw, rotationPitch);
        	}
    	}
        
        this.posX = d18 + d1 * d10;
        this.posZ = d19 + d2 * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        
        if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.EAST_WEST)
        {
        	if (this.world.isAirBlock(pos.west()) && !BlockFlumeRailBase.isRailBlock(getEntityWorld(), pos.west().down()) || this.world.isAirBlock(pos.east())&& !BlockFlumeRailBase.isRailBlock(getEntityWorld(), pos.east().down()))
            {
                this.motionX = 0.0D;
            }
        }
        if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH)
        {
            if (this.world.isAirBlock(pos.north()) && !BlockFlumeRailBase.isRailBlock(getEntityWorld(), pos.north().down())|| this.world.isAirBlock(pos.south())&& !BlockFlumeRailBase.isRailBlock(getEntityWorld(), pos.south().down()))
            {
                this.motionZ = 0.0D;
            }
        }
        
        this.moveBoatOnRail(pos);

        //Up and Down
        if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        }
        else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2])
        {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
        }

        //this.applyDrag();
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.0D;
        this.motionZ *= 0.9599999785423279D;
        
        Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);

        if (vec3d1 != null && vec3d != null)
        {
            double d14 = (vec3d.yCoord - vec3d1.yCoord) * 0.05D;
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d5 > 0.0D)
            {
                this.motionX = this.motionX / d5 * (d5 + d14);
                this.motionZ = this.motionZ / d5 * (d5 + d14);
            }

            this.setPosition(this.posX, vec3d1.yCoord, this.posZ);
        }

        int j = MathHelper.floor(this.posX);
        int i = MathHelper.floor(this.posZ);

        if (j != pos.getX() || i != pos.getZ())
        {
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = d5 * (double)(j - pos.getX());
            this.motionZ = d5 * (double)(i - pos.getZ());
        }

        blockrailbase.onFlumePass(getEntityWorld(), this, pos);

        if (flag && motionStuff)
        {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            //ModLogger.info("MotionStuff: " + d15);
            if (d15 > 0.01D)
            {
                double speed = blockrailbase.getSpeed(getEntityWorld(), this, pos) * 0.5;
                this.motionX += this.motionX / d15 * speed;
                this.motionZ += this.motionZ / d15 * speed;
            }
            else if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.EAST_WEST)
            {
                if (this.world.getBlockState(pos.west()).isNormalCube())
                {
                    this.motionX = 0.02D;
                }
                else if (this.world.getBlockState(pos.east()).isNormalCube())
                {
                    this.motionX = -0.02D;
                }
                
                if (this.world.isAirBlock(pos.west()) || this.world.isAirBlock(pos.east()))
                {
                    this.motionX = 0.0D;
                }
            }
            else if (blockrailbase$enumraildirection == BlockFlumeRailBase.EnumRailDirection.NORTH_SOUTH)
            {
                if (this.world.getBlockState(pos.north()).isNormalCube())
                {
                    this.motionZ = 0.02D;
                }
                else if (this.world.getBlockState(pos.south()).isNormalCube())
                {
                    this.motionZ = -0.02D;
                }
                if (this.world.isAirBlock(pos.north()) || this.world.isAirBlock(pos.south()))
                {
                    this.motionZ = 0.0D;
                }
            }
        }
        
        boolean splashEffect = false;
        double speed = this.motionX * this.motionX + this.motionZ * this.motionZ;
    	if(speed > 0.25){
    		splashEffect = true;
    	}
    	//splashEffect = getEntityWorld().getBlockState(pos.up().up()).getBlock() == Blocks.STONE;
    	
    	if(splashEffect){
    		EnumFacing boatFacing = this.getAdjustedHorizontalFacing();
    		if(!blockrailbase$enumraildirection.isAscending()){
	    		BlockPos behind = pos.offset(boatFacing.getOpposite());
	        	if(BlockFlumeRailBase.isRailBlock(getEntityWorld(), behind)){
	        		IBlockState behindState = getEntityWorld().getBlockState(behind);
	        		BlockFlumeRailBase.EnumRailDirection behindDirection = ((BlockFlumeRailBase)behindState.getBlock()).getRailDirection(getEntityWorld(), behind, behindState, this);
	        		if(behindDirection.isAscending()){
	        			//ModLogger.info("SPLASH!!!!");
	        			boolean speedOffset = true;
	        			for(int r = 0; r < 4; r++){
		        			for(int p = 0; p <= 6; p++){
		        				double pX = pos.getX() + 0.5 + (boatFacing.getFrontOffsetX() * (1 + r*0.2)) + (boatFacing.getAxis() == Axis.Z ? -0.7 + (p * ((1.0 / 6.0) + 0.08)) : 0.0);
		        				double pY = pos.getY() + 0.5;
		        				double pZ = pos.getZ() + 0.5 + (boatFacing.getFrontOffsetZ() * (1 + r*0.2)) + (boatFacing.getAxis() == Axis.X ? -0.7 + (p * ((1.0 / 6.0) + 0.08)) : 0.0);
		        				
		        				if(boatFacing.getAxis() == Axis.X){
		        					if(p == 0 || p == 6){
		        						pX -= (boatFacing.getFrontOffsetX() * 0.3);
		        					}
		        					if(speedOffset){
		        						pZ +=(boatFacing.getFrontOffsetZ() * 2);
		        					}
		        				}
		        				if(boatFacing.getAxis() == Axis.Z){
		        					if(p == 0 || p == 6){
		        						pZ -= (boatFacing.getFrontOffsetZ() * 0.3);
		        					}
		        					if(speedOffset){
		        						pX +=(boatFacing.getFrontOffsetX() * 2);
		        					}
		        				}
		        						        				
		        				double mX = -(boatFacing.getFrontOffsetX() * 0.05);
		        				double mY = 0.2;
		        				double mZ = -(boatFacing.getFrontOffsetZ() * 0.05);
		        				NBTTagCompound data = new NBTTagCompound();
		        				data.setDouble("x", pX);
		        				data.setDouble("y", pY);
		        				data.setDouble("z", pZ);
		        				data.setDouble("mX", mX);
		        				data.setDouble("mY", mY);
		        				data.setDouble("mZ", mZ);
		        				CrystalModNetwork.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.WATER_WAKE, data), this);
		        			}
	        			}
	        		}
	        	}
    		}
    	}
    }
    
    public void moveBoatOnRail(BlockPos pos)
    {
        double mX = this.motionX;
        double mZ = this.motionZ;

        if (this.isBeingRidden())
        {
            mX *= 0.75D;
            mZ *= 0.75D;
        }

        double max = this.getMaxSpeed();
        mX = MathHelper.clamp(mX, -max, max);
        mZ = MathHelper.clamp(mZ, -max, max);
        this.move(MoverType.SELF, mX, 0.0D, mZ);
    }
    
    protected double getMaxSpeed()
    {
        //if (!canUseRail()) return getMaximumSpeed();
        BlockPos pos = this.getCurrentRailPosition();
        IBlockState state = this.world.getBlockState(pos);
        if (!BlockFlumeRailBase.isRailBlock(state)) return getMaximumSpeed();

        float railMaxSpeed = ((BlockFlumeRailBase)state.getBlock()).getMaxSpeed(world, null, pos);
        return Math.min(railMaxSpeed, getMaxCartSpeedOnRail());
    }
    
    public float getMaxCartSpeedOnRail()
    {
        return 1.2f;
    }
    
    protected double getMaximumSpeed()
    {
        return 0.4D;
    }
    
    private BlockPos getCurrentRailPosition()
    {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);

        if (BlockFlumeRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) y--;
        return new BlockPos(x, y, z);
    }
    
    @Nullable
    public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_)
    {
        int i = MathHelper.floor(p_70489_1_);
        int j = MathHelper.floor(p_70489_3_);
        int k = MathHelper.floor(p_70489_5_);

        if (BlockFlumeRailBase.isRailBlock(this.world, new BlockPos(i, j - 1, k)))
        {
            --j;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

        if (BlockFlumeRailBase.isRailBlock(iblockstate))
        {
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockFlumeRailBase)iblockstate.getBlock()).getRailDirection(world, new BlockPos(i, j, k), iblockstate, null);
            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            final double RAIL_OFFSET_Y = 0.0625D;
            double d0 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
            
            double d1 = (double)j + RAIL_OFFSET_Y + (double)aint[0][1] * 0.5D;
            
            double d2 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
            double d3 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
            
            double d4 = (double)j + RAIL_OFFSET_Y + (double)aint[1][1] * 0.5D;
            
            double d5 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
            double d6 = d3 - d0;
            
            double d7 = (d4 - d1) * 2.0D;
            
            double d8 = d5 - d2;
            double d9;

            if (d6 == 0.0D)
            {
                d9 = p_70489_5_ - (double)k;
            }
            else if (d8 == 0.0D)
            {
                d9 = p_70489_1_ - (double)i;
            }
            else
            {
                double d10 = p_70489_1_ - d0;
                double d11 = p_70489_5_ - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0D;
            }

            p_70489_1_ = d0 + d6 * d9;
            p_70489_3_ = d1 + d7 * d9;
            p_70489_5_ = d2 + d8 * d9;

            if (d7 < 0.0D)
            {
                ++p_70489_3_;
            }

            if (d7 > 0.0D)
            {
                p_70489_3_ += 0.5D;
            }

            return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
        }
        else
        {
            return null;
        }
    }
    
    @Nullable
    @SideOnly(Side.CLIENT)
    public Vec3d getPosOffset(double x, double y, double z, double offset)
    {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);

        if (BlockFlumeRailBase.isRailBlock(this.world, new BlockPos(i, j - 1, k)))
        {
            --j;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

        if (BlockFlumeRailBase.isRailBlock(iblockstate))
        {
            BlockFlumeRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockFlumeRailBase)iblockstate.getBlock()).getRailDirection(world, new BlockPos(i, j, k), iblockstate, this);
            y = (double)j;

            if (blockrailbase$enumraildirection.isAscending())
            {
                y = (double)(j + 1);
            }

            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double)(aint[1][0] - aint[0][0]);
            double d1 = (double)(aint[1][2] - aint[0][2]);
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            d0 = d0 / d2;
            d1 = d1 / d2;
            x = x + d0 * offset;
            z = z + d1 * offset;

            if (aint[0][1] != 0 && MathHelper.floor(x) - i == aint[0][0] && MathHelper.floor(z) - k == aint[0][2])
            {
                y += (double)aint[0][1];
            }
            else if (aint[1][1] != 0 && MathHelper.floor(x) - i == aint[1][0] && MathHelper.floor(z) - k == aint[1][2])
            {
                y += (double)aint[1][1];
            }

            return this.getPos(x, y, z);
        }
        else
        {
            return null;
        }
    }
    
	@Override
	protected void entityInit() {
		this.dataManager.register(BOAT_TYPE, Integer.valueOf(EntityBoat.Type.OAK.ordinal()));
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (player.isSneaking())
        {
            return false;
        }
        else
        {
            if (!this.world.isRemote)
            {
                player.startRiding(this);
            }

            return true;
        }
    }

	protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

	@Override
	public void updatePassenger(Entity passenger)
    {
		super.updatePassenger(passenger);
		if(isPassenger(passenger)){
			applyYawToEntity(passenger);
		}
    }
	
    @Override
	@SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        applyYawToEntity(entityToUpdate);
    }
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("Type", 8))
        {
            this.setBoatType(EntityBoat.Type.getTypeFromString(compound.getString("Type")));
        }
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setString("Type", this.getBoatType().getName());
	}
	
	public void setBoatType(EntityBoat.Type boatType)
    {
        this.dataManager.set(BOAT_TYPE, Integer.valueOf(boatType.ordinal()));
    }

    public EntityBoat.Type getBoatType()
    {
        return EntityBoat.Type.byId(((Integer)this.dataManager.get(BOAT_TYPE)).intValue());
    }

}
