package alec_wam.CrystalMod.items.tools.projectiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityDarkarang extends EntityArrow implements IEntityAdditionalSpawnData {

	public int spin = 0;
	
	public EntityDarkarang(World world) {
		super(world);
	}

	public EntityDarkarang(World world, double d, double d1, double d2) {
		this(world);
		this.setPosition(d, d1, d2);
	}

	public EntityDarkarang(World world, EntityPlayer player, float speed, float inaccuracy) {
		this(world);

		this.shootingEntity = player;


		pickupStatus = player.isCreative() ? PickupStatus.CREATIVE_ONLY : PickupStatus.ALLOWED;

		// stuff from the arrow
		this.setLocationAndAngles(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);

		this.setPosition(this.posX, this.posY, this.posZ);
		//this.yOffset = 0.0F;
		this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI);
		this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, inaccuracy);
	}

	protected void playHitBlockSound(float speed, IBlockState state) {
		this.playSound(state.getBlock().getSoundType().getStepSound(), 0.8f, 1.0f);
	}

	protected void playHitEntitySound() {
		this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}
	
	public double getStuckDepth() {
		return 0.4f;
	}
	
	protected void onEntityHit(Entity entityHit) {
		setDead();
	}

	protected float getSpeed() {
		return MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
	}

	public void onHitBlock(RayTraceResult raytraceResult) {
		BlockPos blockpos = raytraceResult.getBlockPos();
		setXTile(blockpos.getX());
		setYTile(blockpos.getY());
		setZTile(blockpos.getZ());
		IBlockState iblockstate = this.getEntityWorld().getBlockState(blockpos);
		setTile(iblockstate.getBlock());
		setData(getTile().getMetaFromState(iblockstate));
		this.motionX = (double) ((float) (raytraceResult.hitVec.xCoord - this.posX));
		this.motionY = (double) ((float) (raytraceResult.hitVec.yCoord - this.posY));
		this.motionZ = (double) ((float) (raytraceResult.hitVec.zCoord - this.posZ));
		float speed = getSpeed();
		this.posX -= this.motionX / (double) speed * 0.05000000074505806D;
		this.posY -= this.motionY / (double) speed * 0.05000000074505806D;
		this.posZ -= this.motionZ / (double) speed * 0.05000000074505806D;

		playHitBlockSound(speed, iblockstate);

		this.inGround = true;
		this.arrowShake = 7;
		this.setIsCritical(false);

		if(iblockstate.getMaterial() != Material.AIR) {
			getTile().onEntityCollidedWithBlock(this.getEntityWorld(), blockpos, iblockstate, this);
		}
	}

	public void onHitEntity(RayTraceResult raytraceResult) {
		boolean bounceOff = false;
		Entity entityHit = raytraceResult.entityHit;
		if(this.shootingEntity instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) this.shootingEntity;
			
			float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			bounceOff = !dealDamage(speed, attacker, entityHit);
			
			if(!bounceOff) {
				onEntityHit(entityHit);
			}
		}

		if(bounceOff) {
			this.motionX *= -0.10000000149011612D;
			this.motionY *= -0.10000000149011612D;
			this.motionZ *= -0.10000000149011612D;
			this.rotationYaw += 180.0F;
			this.prevRotationYaw += 180.0F;
			setTicksInAir(0);
		}

		playHitEntitySound();
	}

	public boolean dealDamage(float speed, EntityLivingBase attacker, Entity target) {
		return target.attackEntityFrom(new DamageSourceDarkarang("darkarang", attacker, attacker), 5.0F);
	}

	@Override
	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {}

	@Override
	public void onUpdate() {
		onEntityUpdate();
		if(this.arrowShake > 0) {
			--this.arrowShake;
		}

		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D / Math.PI);
		}

		BlockPos blockpos = new BlockPos(getXTile(), getYTile(), getZTile());
		IBlockState iblockstate = this.getEntityWorld().getBlockState(blockpos);

		if(iblockstate.getMaterial() != Material.AIR) {
			AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.getEntityWorld(), blockpos);

			if(axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).isVecInside(new Vec3d(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if(this.inGround) {
			updateInGround(iblockstate);
		}
		else {
			updateInAir();
		}
	}

	public void updateInGround(IBlockState state) {
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);

		if(block == getTile() && meta == getData()) {
			setTicksInGround(getTicksInGround()+1);
			if(getTicksInGround() >= 1200) {
				this.setDead();
			}
		}
		else {
			this.inGround = false;
			this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
			setTicksInGround(0);
			setTicksInAir(0);
		}

		++this.timeInGround;
	}

	public void updateInAir() {
		this.timeInGround = 0;
		setTicksInAir(getTicksInAir()+1);

		Vec3d oldPos = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d newPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		RayTraceResult raytraceResult = this.getEntityWorld().rayTraceBlocks(oldPos, newPos, false, true, false);

		if(raytraceResult != null) {
			newPos = new Vec3d(raytraceResult.hitVec.xCoord, raytraceResult.hitVec.yCoord, raytraceResult.hitVec.zCoord);
		}
		
		Entity entity = this.findEntityOnPath(oldPos, newPos);

		if(entity != null) {
			raytraceResult = new RayTraceResult(entity);
		}

		if(raytraceResult != null && raytraceResult.entityHit != null && raytraceResult.entityHit instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) raytraceResult.entityHit;

			if(entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
				raytraceResult = null;
			}
		}

		if(raytraceResult != null) {
			if(raytraceResult.entityHit != null) {
				onHitEntity(raytraceResult);
			}
			else {
				onHitBlock(raytraceResult);
			}
		}

		if(this.getIsCritical()) {
			drawCritParticles();
		}

		doMoveUpdate();
		double slowdown = 1.0d - getSlowdown();

		if(this.isInWater()) {
			for(int l = 0; l < 4; ++l) {
				float f3 = 0.25F;
				this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX, this.motionY, this.motionZ);
			}
			slowdown *= 0.60d;
		}

		if(this.isWet()) {
			this.extinguish();
		}

		this.motionX *= slowdown;
		this.motionY *= slowdown;
		this.motionZ *= slowdown;
		if(!this.hasNoGravity()) {
			this.motionY -= getGravity();
		}
		this.setPosition(this.posX, this.posY, this.posZ);

		this.doBlockCollisions();
	}

	@Nullable
	@Override
	protected Entity findEntityOnPath(@Nonnull Vec3d start, @Nonnull Vec3d end) {
		return super.findEntityOnPath(start, end);
	}

	public void drawCritParticles() {
		for(int k = 0; k < 4; ++k) {
			this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double) k / 4.0D, this.posY + this.motionY * (double) k / 4.0D, this.posZ + this.motionZ * (double) k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
		}
	}

	protected void doMoveUpdate() {
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		double f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
		this.rotationPitch = (float) (Math.atan2(this.motionY, f2) * 180.0D / Math.PI);

		while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
	}

	/**
	 * Factor for the slowdown. 0 = no slowdown, >0 = (1-slowdown)*speed slowdown, <0 = speedup
	 */
	public double getSlowdown() {
		return 0.01;
	}

	/**
	 * Added to the y-velocity as gravitational pull. Otherwise stuff would simply float midair.
	 */
	public double getGravity() {
		return 0.05;
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	@Override
	public void onCollideWithPlayer(@Nonnull EntityPlayer player) {
		if(!this.getEntityWorld().isRemote && this.inGround && this.arrowShake <= 0) {
			boolean pickedUp = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;

			if(pickedUp) {
				pickedUp = playerPickup(player, pickupStatus != PickupStatus.ALLOWED);
			}

			if(pickedUp) {
				this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				this.setDead();
			}
		}
	}
	
	public boolean playerPickup(EntityPlayer player, boolean blocked){
		
		if(!blocked){
			if(player.inventory.addItemStackToInventory(new ItemStack(ModItems.darkarang))){
				return true;
			}
		}
		
		return blocked;
	}


	/** NBT stuff **/

	@Override
	public void writeEntityToNBT(NBTTagCompound tags) {
		super.writeEntityToNBT(tags);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tags) {
		super.readEntityFromNBT(tags);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeFloat(rotationYaw);

		int id = shootingEntity == null ? this.getEntityId() : shootingEntity.getEntityId();
		data.writeInt(id);

		data.writeDouble(this.motionX);
		data.writeDouble(this.motionY);
		data.writeDouble(this.motionZ);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		rotationYaw = data.readFloat();
		shootingEntity = getEntityWorld().getEntityByID(data.readInt());

		this.motionX = data.readDouble();
		this.motionY = data.readDouble();
		this.motionZ = data.readDouble();

		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		
		this.spin = rand.nextInt(360);
	}
	
	public void setTile(Block block){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, block, 5);
	}
	
	public Block getTile(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 5);
	}
	
	public void setData(int meta){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, meta, 6);
	}
	
	public int getData(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 6);
	}
	
	public boolean getInGround(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 7);
	}
	
	public void setXTile(int x){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, x, 2);
	}
	
	public int getXTile(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 2);
	}
	
	public void setYTile(int y){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, y, 3);
	}
	
	public int getYTile(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 3);
	}
	
	public void setZTile(int z){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, z, 4);
	}
	
	public int getZTile(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 4);
	}
	
	public void setTicksInGround(int time){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, time, 12);
	}
	
	public int getTicksInGround(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 12);
	}
	
	public void setTicksInAir(int time){
		ReflectionHelper.setPrivateValue(EntityArrow.class, this, time, 13);
	}
	
	public int getTicksInAir(){
		return ReflectionHelper.getPrivateValue(EntityArrow.class, this, 13);
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(ModItems.darkarang);
	}
}
