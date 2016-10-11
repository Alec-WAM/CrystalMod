package alec_wam.CrystalMod.tiles.spawner;

import alec_wam.CrystalMod.client.model.BakedCustomItemModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CustomSpawnerBaseLogic {
	public int spawnDelay = 20;
	public String entityName = "";

	public double renderRotation0;
	public double renderRotation1;
	private int minSpawnDelay = 400;
	private int maxSpawnDelay = 600;
	
	private int spawnCount = 6;
	private Entity renderedEntity;
	private int maxNearbyEntities = 20;
	
	public boolean powered = false;
	/**
	 * Dose the spawner require a player nearby
	 */
	public boolean requiresPlayer = true;
	/**
	 * Ignore Mob Spawn Requirements
	 */
	public boolean ignoreSpawnRequirements = false;
	/**
	 * Spawn Speed
	 */
	public int spawnSpeed = 1;
	/**
	 * The distance from which a player activates the spawner.
	 */
	private int activatingRangeFromPlayer = 24;
	/**
	 * The range coefficient for spawning entities around.
	 */
	private int spawnRange = 4;
	/**
	 * Gets the entity name that should be spawned.
	 */
	public String getEntityNameToSpawn() {
		return this.entityName;
	}

	public void setEntityName(String name) {
		this.entityName = name;
	}

	/**
	 * Returns true if there's a player close enough to this mob spawner to activate it.
	 */
	public boolean isActivated() {
		if (!requiresPlayer)
			return true;
		else
			return this.getSpawnerWorld().getClosestPlayer((double) this.getSpawnerX() + 0.5D, (double) this.getSpawnerY() + 0.5D, (double) this.getSpawnerZ() + 0.5D, (double) this.activatingRangeFromPlayer, false) != null;
	}

	public void updateSpawner() {
		if (isActivated() && !powered) {
			double d2;

			if (this.getSpawnerWorld().isRemote) {
				double d0 = (double) ((float) this.getSpawnerX() + this.getSpawnerWorld().rand.nextFloat());
				double d1 = (double) ((float) this.getSpawnerY() + this.getSpawnerWorld().rand.nextFloat());
				d2 = (double) ((float) this.getSpawnerZ() + this.getSpawnerWorld().rand.nextFloat());
				this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);

				if (this.spawnDelay > 0) {
					--this.spawnDelay;
				}

				this.renderRotation1 = this.renderRotation0;
				this.renderRotation0 = (this.renderRotation0 + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
			} else {
				if (this.spawnDelay == -1) {
					this.resetTimer();
				}

				if (this.spawnDelay > 0) {
					--this.spawnDelay;
					return;
				}

				boolean flag = false;

				for (int i = 0; i < this.spawnCount; ++i) {
					EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(getEntityNameToSpawn());
					Entity entity = essence.createEntity(getSpawnerWorld());

					if (entity == null) {
						return;
					}

					int j = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), new AxisAlignedBB((double) this.getSpawnerX(), (double) this.getSpawnerY(), (double) this.getSpawnerZ(), (double) (this.getSpawnerX() + 1), (double) (this.getSpawnerY() + 1), (double) (this.getSpawnerZ() + 1)).expand((double) (this.spawnRange * 2), 4.0D, (double) (this.spawnRange * 2))).size();

					if (j >= this.maxNearbyEntities) {
						this.resetTimer();
						return;
					}
					World world = getSpawnerWorld();
					BlockPos blockpos = new BlockPos(getSpawnerX(), getSpawnerY(), getSpawnerZ());
					double x = (double)blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
					double y = (double)(blockpos.getY() + world.rand.nextInt(3) - 1);
					double z = (double)blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * (double)this.spawnRange + 0.5D;
					EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
					entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

					if (entityliving == null || (net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(entityliving, getSpawnerWorld(), (float)entity.posX, (float)entity.posY, (float)entity.posZ) || ignoreSpawnRequirements && getSpawnerWorld().isAirBlock(new BlockPos(x, y, z)))) {
						this.spawnEntity(entity);
						this.getSpawnerWorld().playEvent(2004, this.getSpawnerPos(), 0);

						if (entityliving != null) {
							entityliving.spawnExplosionParticle();
						}

						flag = true;

					}
				}

				if (flag) {
					this.resetTimer();
				}
			}
		}
	}

	public Entity spawnEntity(Entity par1Entity) {
		if (par1Entity instanceof EntityLivingBase && par1Entity.worldObj != null) {
			EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(getEntityNameToSpawn());
			if(essence !=null && essence.useInitialSpawn()){
				if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(((EntityLiving) par1Entity), this.getSpawnerWorld(), (float)par1Entity.posX, (float)par1Entity.posY, (float)par1Entity.posZ))
				((EntityLiving) par1Entity).onInitialSpawn(getSpawnerWorld().getDifficultyForLocation(new BlockPos(((EntityLiving)par1Entity))), null);
			}
			this.getSpawnerWorld().spawnEntityInWorld(par1Entity);
			((EntityLiving)par1Entity).playLivingSound();
		}

		return par1Entity;
	}

	private void resetTimer() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			int i = this.maxSpawnDelay - this.minSpawnDelay;
			this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
		}

		this.blockEvent(1);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		this.entityName = par1NBTTagCompound.getString("EntityId");
		this.spawnDelay = par1NBTTagCompound.getShort("Delay");

		powered = par1NBTTagCompound.getBoolean("Powered");
		spawnSpeed = par1NBTTagCompound.getShort("Speed");
		requiresPlayer = par1NBTTagCompound.getBoolean("RequiresPlayer");
		ignoreSpawnRequirements = par1NBTTagCompound.getBoolean("IgnoreSpawnRequirements");

		this.minSpawnDelay = par1NBTTagCompound.getShort("MinSpawnDelay");
		this.maxSpawnDelay = par1NBTTagCompound.getShort("MaxSpawnDelay");
		this.spawnCount = par1NBTTagCompound.getShort("SpawnCount");

		if (par1NBTTagCompound.hasKey("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = par1NBTTagCompound.getShort("MaxNearbyEntities");
			this.activatingRangeFromPlayer = par1NBTTagCompound.getShort("RequiredPlayerRange");
		}

		if (par1NBTTagCompound.hasKey("SpawnRange", 99)) {
			this.spawnRange = par1NBTTagCompound.getShort("SpawnRange");
		}

		if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
			this.renderedEntity = null;
		}
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setString("EntityId", getEntityNameToSpawn());
		par1NBTTagCompound.setShort("Delay", (short) spawnDelay);
		par1NBTTagCompound.setShort("MinSpawnDelay", (short) minSpawnDelay);
		par1NBTTagCompound.setShort("MaxSpawnDelay", (short) maxSpawnDelay);
		par1NBTTagCompound.setShort("SpawnCount", (short) spawnCount);
		par1NBTTagCompound.setShort("MaxNearbyEntities", (short) maxNearbyEntities);
		par1NBTTagCompound.setShort("RequiredPlayerRange", (short) activatingRangeFromPlayer);
		par1NBTTagCompound.setShort("SpawnRange", (short) spawnRange);
		par1NBTTagCompound.setBoolean("Powered", powered);
		par1NBTTagCompound.setShort("Speed", (short) spawnSpeed);
		par1NBTTagCompound.setBoolean("RequiresPlayer", requiresPlayer);
		par1NBTTagCompound.setBoolean("IgnoreSpawnRequirements", ignoreSpawnRequirements);
	}

	/**
	 * Sets the delay to minDelay if parameter given is 1, else return false.
	 */
	public boolean setDelayToMin(int par1) {
		if (par1 == 1 && this.getSpawnerWorld().isRemote) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public Entity getEntityForRenderer() {
		if (this.renderedEntity == null) {
			this.renderedEntity = BakedCustomItemModel.getRenderEntityNullable(getEntityNameToSpawn());
		}

		return this.renderedEntity;
	}

	public abstract void blockEvent(int var1);

	public abstract World getSpawnerWorld();

	public abstract BlockPos getSpawnerPos();
	
	public int getSpawnerX(){
		return getSpawnerPos().getX();
	}

	public int getSpawnerY(){
		return getSpawnerPos().getY();
	}

	public int getSpawnerZ(){
		return getSpawnerPos().getZ();
	}

	public void setSpawnRate(int i){
		spawnSpeed = i;
		minSpawnDelay = 400 - (i * 150);
		maxSpawnDelay = 600 - (i * 200);
		spawnCount = 4 + (i*2);
		if (i == 3){
			minSpawnDelay = 40;
			maxSpawnDelay = 40;
			spawnCount = 12;
		}
		if (minSpawnDelay < 0) minSpawnDelay = 0;
		if (maxSpawnDelay < 1) maxSpawnDelay = 1;
		resetTimer();
	}
}
