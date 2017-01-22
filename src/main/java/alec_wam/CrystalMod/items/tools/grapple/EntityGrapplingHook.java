package alec_wam.CrystalMod.items.tools.grapple;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGrapplingHook extends EntityThrowable implements IEntityAdditionalSpawnData {

	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	public Vec3d vecPos;
	
	private boolean firstAttach = false;
	public boolean attached = false;
	public EnumHand hand = EnumHand.MAIN_HAND;
	public double taut;
	
	//Traits
	public boolean canGrabEntities = false;
	public boolean canGrabMobs = false;
	public boolean canGrabPlayers = false;
	
	public EntityGrapplingHook(World worldIn) {
		super(worldIn);
	}
	
	public EntityGrapplingHook(World worldIn, EntityLivingBase shooter,	EnumHand hand) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getEntityId();
		this.hand = hand;
	}

	@Override
	public void onEntityUpdate(){
		super.onEntityUpdate();
		
		if (this.shootingEntityID == 0) { // removes ghost grappling hooks
			this.kill();
		}
		
		if (this.firstAttach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstAttach = false;
			super.setPosition(this.vecPos.xCoord, this.vecPos.yCoord, this.vecPos.zCoord);
		}
		
		
		/*if (this.toofaraway()) {
			this.removeServer();
		}*/
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	public final int RenderBoundingBoxSize = 999;
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(-RenderBoundingBoxSize, -RenderBoundingBoxSize, -RenderBoundingBoxSize, 
				RenderBoundingBoxSize, RenderBoundingBoxSize, RenderBoundingBoxSize);
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		if (this.vecPos != null) {
			x = this.vecPos.xCoord;
			y = this.vecPos.yCoord;
			z = this.vecPos.zCoord;
		}
		super.setPosition(x, y, z);
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
		buffer.writeBoolean((hand == null || hand == EnumHand.MAIN_HAND) ? false : true);
		buffer.writeBoolean(canGrabEntities);buffer.writeBoolean(canGrabMobs);buffer.writeBoolean(canGrabPlayers);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.shootingEntityID = additionalData.readInt();
	    this.shootingEntity = this.getEntityWorld().getEntityByID(this.shootingEntityID);
	    this.hand = additionalData.readBoolean() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
	    this.canGrabEntities = additionalData.readBoolean();
	    this.canGrabMobs = additionalData.readBoolean();
	    this.canGrabPlayers = additionalData.readBoolean();
	}

	public void remove() {
		this.kill();
	}
	
	@Override
	protected void onImpact(RayTraceResult movingobjectposition) {
		if (!this.getEntityWorld().isRemote) {
			if (this.shootingEntityID != 0) {
				if (movingobjectposition.typeOfHit == RayTraceResult.Type.ENTITY) {
					// hit entity
					Entity entityhit = movingobjectposition.entityHit;
					if (entityhit == this.shootingEntity) {
						return;
					}
					
					if(!canGrabMobs && entityhit instanceof IMob)return;
					if(!canGrabPlayers && entityhit instanceof EntityPlayer)return;
					if(!canGrabEntities && !(entityhit instanceof EntityItem))return; //Always allow item grabbing

					Vec3d playerpos = this.shootingEntity.getPositionVector();
					Vec3d entitypos = entityhit.getPositionVector();
					Vec3d yank = GrappleControllerBase.multiply(playerpos.subtract(entitypos), 0.4);
					entityhit.addVelocity(yank.xCoord, Math.min(yank.yCoord, 2), yank.zCoord);
					
					this.removeServer();
					return;
				}
				
				BlockPos blockpos = null;
				
				boolean blockPass = false;
				
				if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
					blockpos = movingobjectposition.getBlockPos();
					
					IBlockState iblockstate = this.getEntityWorld().getBlockState(blockpos);

			        if (iblockstate.getMaterial() != Material.AIR)
			        {
			            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.getEntityWorld(), blockpos);

			            if (axisalignedbb != Block.NULL_AABB /*&& axisalignedbb.offset(blockpos).isVecInside(new Vec3d(this.posX, this.posY, this.posZ))*/)
			            {
			            	blockPass = true;
			            }
			        }
				}
				Vec3d vec3 = null;
		        
		        if (movingobjectposition != null)
		        {
		            vec3 = movingobjectposition.hitVec;
		        }
		        
		        if(blockpos == null || blockPass)this.serverAttach(blockpos, vec3, movingobjectposition.sideHit);
			}
		}
	}
	
	public void serverAttach(BlockPos blockpos, Vec3d pos, EnumFacing sideHit) {
		if (this.attached) {
			return;
		}
		this.attached = true;
		
		Vec3d vec3 = getPositionVector();
		vec3 = vec3.addVector(motionX, motionY, motionZ);
		if (pos != null) {
            vec3 = pos;
            
            this.setPositionAndUpdate(vec3.xCoord, vec3.yCoord, vec3.zCoord);
		}
		this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        
        this.vecPos = this.getPositionVector();
		this.firstAttach = true;
		GrappleHandler.serverAttach(shootingEntity, getEntityId(), blockpos, vec3, sideHit, GrappleType.BLOCK);
	}
	
	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);
	}
	
	@Override
    protected float getGravityVelocity()
    {
        return /*0.03F*/0.05000000074505806f;
    }
	
    public float getVelocity()
    {
        return 5F;
    }

	public void removeServer() {
		this.kill();
		this.shootingEntityID = 0;

	}

	public void setAttachPos(double x, double y, double z) {
		this.setPositionAndUpdate(x, y, z);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstAttach = true;
		this.attached = true;
        this.vecPos = new Vec3d(x, y, z);
	}

}
