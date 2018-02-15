package alec_wam.CrystalMod.tiles.obsidiandispenser;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class TileObsidianDispenser extends TileEntityInventory implements IFacingTile{
	public static final Random RNG = new Random();
	private EnumFacing facing = EnumFacing.NORTH;
	private boolean isTriggered;
	public TileObsidianDispenser() {
		super("ObsidianDispenser", 9);
	}	
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
		nbt.setBoolean("Triggered", isTriggered);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		setFacing(nbt.getInteger("Facing"));
		isTriggered = nbt.getBoolean("Triggered");
		updateAfterLoad();
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean powered = getWorld().isBlockPowered(pos) || getWorld().isBlockPowered(pos.up());
	        
			if(powered && !isTriggered){
				dispense();
				isTriggered = true;
			}
			if(!powered && isTriggered){
				isTriggered = false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private final Predicate<Entity> ITEM_TARGETS = Predicates.and(new Predicate[] {EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity p_apply_1_)
        {
            return ((Entity)p_apply_1_).canBeCollidedWith();
        }
    }});
	
	public void dispense(){
		double d0 = pos.getX() + 0.5 + (0.7D * (double)facing.getFrontOffsetX());
		double d1 = pos.getY() + 0.5 + (0.7D * (double)facing.getFrontOffsetY());
		double d2 = pos.getZ() + 0.5 + (0.7D * (double)facing.getFrontOffsetZ());

		if (facing.getAxis() == EnumFacing.Axis.Y)
		{
			d1 = d1 - 0.125D;
		}
		else
		{
			d1 = d1 - 0.15625D;
		}


		int slot = -1;
		int j = 1;

		for (int k = 0; k < getSizeInventory(); ++k)
		{
			if (!getStackInSlot(k).isEmpty() && RNG.nextInt(j++) == 0)
			{
				slot = k;
			}
		}

		if(slot > -1){
			double speed = 0.0D;
			ItemStack itemstack = getStackInSlot(slot);
			ItemStack stack = itemstack.splitStack(1);
			EntityItem entityitem = new EntityItem(world, d0, d1, d2, stack) {
				
				@Override
				public void onUpdate(){
					Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
					Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
					RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
					vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
					vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

					if (raytraceresult != null)
					{
						vec3d = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
					}

					Entity entity = findEntityOnPath(this, vec3d1, vec3d);

					if (entity != null)
					{
						float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			            float i = MathHelper.ceil((double)f * 5.0F);
			            
			            
			            ItemStack item = this.getEntityItem();
			            if(ItemStackTools.isValid(item)){
			            	if(item.getItem() instanceof ItemSword){
			            		i = ((ItemSword)item.getItem()).getDamageVsEntity();
			            	}
			            }
			            
			            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, null), i);
					}
					super.onUpdate();					
				}
				
			};
			entityitem.setPickupDelay(40);
			double d3 = world.rand.nextDouble() * 0.4D + 0.2D;
			entityitem.motionX = (double)facing.getFrontOffsetX() * d3;
			entityitem.motionY = ((double)facing.getFrontOffsetY() * d3) + (facing.getAxis() != EnumFacing.Axis.Y ? 0.20000000298023224D : 0.0);
			entityitem.motionZ = (double)facing.getFrontOffsetZ() * d3;
			entityitem.motionX += world.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
			entityitem.motionY += world.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
			entityitem.motionZ += world.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
			getWorld().spawnEntity(entityitem);
			setInventorySlotContents(slot, itemstack);
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
	protected Entity findEntityOnPath(EntityItem item, Vec3d start, Vec3d end)
    {
        Entity entity = null;
        List<Entity> list = getWorld().getEntitiesInAABBexcluding(item, item.getEntityBoundingBox().addCoord(item.motionX, item.motionY, item.motionZ).expandXyz(1.0D), ITEM_TARGETS);
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = (Entity)list.get(i);

            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

            if (raytraceresult != null)
            {
            	double d1 = start.squareDistanceTo(raytraceresult.hitVec);

            	if (d1 < d0 || d0 == 0.0D)
            	{
            		entity = entity1;
            		d0 = d1;
            	}
            }
        }

        return entity;
    }
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	@Override
	public boolean useVerticalFacing(){
		return true;
	}
}
