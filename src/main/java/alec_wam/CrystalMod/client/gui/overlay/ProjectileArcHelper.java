package alec_wam.CrystalMod.client.gui.overlay;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ProjectileArcHelper {
	
	public static class ProjectileArcData {
		public List<Vec3d> arcPoints = Lists.newArrayList();
		private int age = 0;
		public Vec3d finalHit = null;
		public Type finalHitType;
		private Vec3d motion = Vec3d.ZERO;
		private float rotationYaw;
		private float rotationPitch;
		private float prevRotationYaw;
		private float prevRotationPitch;
		private AxisAlignedBB boundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		private double posX;
		private double posY;
		private double posZ;

		public static ProjectileArcData shoot(PlayerEntity shooter, float pitch, float yaw, float velocity, float inaccuracy, Random rand) {
			ProjectileArcData data = new ProjectileArcData();
			double x = shooter.posX;
			double y = shooter.posY + (double)shooter.getEyeHeight() - (double)0.1F;
			double z = shooter.posZ;
			data.setPosition(x, y, z);
			float f = -MathHelper.sin(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
			float f1 = -MathHelper.sin(pitch * ((float)Math.PI / 180F));
			float f2 = MathHelper.cos(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
			data.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy, rand);
			data.motion = data.motion.add(shooter.getMotion().x, shooter.onGround ? 0.0D : shooter.getMotion().y, shooter.getMotion().z);
			
			Minecraft.getInstance().getProfiler().startSection("tick");
			for(int i = 0; i < 100 && data.finalHit == null; i++){
				data.tick(shooter.getEntityWorld(), shooter);
			}
			Minecraft.getInstance().getProfiler().endSection();
			return data;
		}
		
		private void shoot(double x, double y, double z, float velocity, float inaccuracy, Random rand) {
			Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
			motion = vec3d;
			float f = MathHelper.sqrt(Entity.func_213296_b(vec3d));
			this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
			this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}

		public void tick(World world, PlayerEntity shooter) {
			boolean flag = false;
			if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
				float f = MathHelper.sqrt(Entity.func_213296_b(motion));
				this.rotationYaw = (float)(MathHelper.atan2(motion.x, motion.z) * (double)(180F / (float)Math.PI));
				this.rotationPitch = (float)(MathHelper.atan2(motion.y, (double)f) * (double)(180F / (float)Math.PI));
				this.prevRotationYaw = this.rotationYaw;
				this.prevRotationPitch = this.rotationPitch;
			}

			BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
			BlockState blockstate = world.getBlockState(blockpos);
			if (!blockstate.isAir(world, blockpos) && !flag) {
				VoxelShape voxelshape = blockstate.getCollisionShape(world, blockpos);
				if (!voxelshape.isEmpty()) {
					for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
						if (axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
							finalHit = new Vec3d(blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5);
							finalHitType = RayTraceResult.Type.BLOCK;
							break;
						}
					}
				}
			}

			if (finalHit !=null && !flag) {
			} else {
				//Update Water
				boolean inWater = false;
				if (handleFluidAcceleration(world, FluidTags.WATER)) {
					inWater = true;
				} else {
					inWater = false;
				}
				
				Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
				Vec3d vec3d2 = vec3d1.add(motion);
				RayTraceContext context = new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, shooter){
					   @Override
					   public VoxelShape func_222251_a(BlockState p_222251_1_, IBlockReader p_222251_2_, BlockPos p_222251_3_) {
					      return RayTraceContext.BlockMode.COLLIDER.get(p_222251_1_, p_222251_2_, p_222251_3_, ISelectionContext.dummy());
					   }
				};
				RayTraceResult raytraceresult = world.func_217299_a(context);
				if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
					vec3d2 = raytraceresult.getHitVec();
				}

				while(finalHit == null) {
					EntityRayTraceResult entityraytraceresult = createEntityRay(world, shooter, vec3d1, vec3d2);
					if (entityraytraceresult != null) {
						raytraceresult = entityraytraceresult;
					}

					if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
						Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
						if (entity instanceof PlayerEntity && !shooter.canAttackPlayer((PlayerEntity)entity)) {
							raytraceresult = null;
							entityraytraceresult = null;
						}
					}

					if (raytraceresult != null && !flag) {
						if(raytraceresult.getType() == Type.ENTITY){
							Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
							Vec3d middle = entity.getBoundingBox().getCenter();
							this.finalHit = middle;
							finalHitType = RayTraceResult.Type.ENTITY;
						}
					}

					if (entityraytraceresult == null) {
						break;
					}

					raytraceresult = null;
				}

				double d1 = motion.x;
				double d2 = motion.y;
				double d0 = motion.z;

				this.posX += d1;
				this.posY += d2;
				this.posZ += d0;
				float f4 = MathHelper.sqrt(Entity.func_213296_b(motion));
				if (flag) {
					this.rotationYaw = (float)(MathHelper.atan2(-d1, -d0) * (double)(180F / (float)Math.PI));
				} else {
					this.rotationYaw = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI));
				}

				for(this.rotationPitch = (float)(MathHelper.atan2(d2, (double)f4) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
					;
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

				this.rotationPitch = MathHelper.func_219799_g(0.2F, this.prevRotationPitch, this.rotationPitch);
				this.rotationYaw = MathHelper.func_219799_g(0.2F, this.prevRotationYaw, this.rotationYaw);
				float f1 = 0.99F;
				
				if (inWater) {
					//Drag in water
					f1 = getWaterDrag();
				}

				motion = motion.scale((double)f1);
				if (!flag) {
					motion = motion.add(0, -0.05F, 0);
				}

				setPosition(this.posX, this.posY, this.posZ);
				age++;
			}
		}

		public float getWaterDrag(){
			return 0.6f;
		}
		
		public void setPosition(double x, double y, double z) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			Vec3d arcPos = new Vec3d(x, y, z);
			this.arcPoints.add(arcPos);
			float f = 0.5f / 2.0F;
			float f1 = 0.5f;
			boundingBox = new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
		}
		
		protected EntityRayTraceResult createEntityRay(World world, PlayerEntity shoter, Vec3d p_213866_1_, Vec3d p_213866_2_) {
			return calcEntityHit(world, p_213866_1_, p_213866_2_, boundingBox.func_216361_a(motion).grow(1.0D), (p_213871_1_) -> {
				return !p_213871_1_.isSpectator() && p_213871_1_.isAlive() && p_213871_1_.canBeCollidedWith() && (p_213871_1_ != shoter || age > 5);
			}, Double.MAX_VALUE);
		}

		public EntityRayTraceResult calcEntityHit(World world, Vec3d p_221269_2_, Vec3d p_221269_3_, AxisAlignedBB p_221269_4_, Predicate<Entity> p_221269_5_, double p_221269_6_) {
			double d0 = p_221269_6_;
			Entity entity = null;

			for(Entity entity1 : world.getEntitiesInAABBexcluding(null, p_221269_4_, p_221269_5_)) {
				AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
				Optional<Vec3d> optional = axisalignedbb.func_216365_b(p_221269_2_, p_221269_3_);
				if (optional.isPresent()) {
					double d1 = p_221269_2_.squareDistanceTo(optional.get());
					if (d1 < d0) {
						entity = entity1;
						d0 = d1;
					}
				}
			}

			if (entity == null) {
				return null;
			} else {
				return new EntityRayTraceResult(entity);
			}
		}

		@SuppressWarnings("deprecation")
		public boolean handleFluidAcceleration(World world, Tag<Fluid> p_210500_1_) {
			AxisAlignedBB axisalignedbb = boundingBox.shrink(0.001D);
			int i = MathHelper.floor(axisalignedbb.minX);
			int j = MathHelper.ceil(axisalignedbb.maxX);
			int k = MathHelper.floor(axisalignedbb.minY);
			int l = MathHelper.ceil(axisalignedbb.maxY);
			int i1 = MathHelper.floor(axisalignedbb.minZ);
			int j1 = MathHelper.ceil(axisalignedbb.maxZ);
			if (!world.isAreaLoaded(i, k, i1, j, l, j1)) {
				return false;
			} else {
				double d0 = 0.0D;
				boolean flag = true;
				boolean flag1 = false;
				Vec3d vec3d = Vec3d.ZERO;
				int k1 = 0;

				try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
					for(int l1 = i; l1 < j; ++l1) {
						for(int i2 = k; i2 < l; ++i2) {
							for(int j2 = i1; j2 < j1; ++j2) {
								blockpos$pooledmutableblockpos.setPos(l1, i2, j2);
								IFluidState ifluidstate = world.getFluidState(blockpos$pooledmutableblockpos);
								if (ifluidstate.isTagged(p_210500_1_)) {
									double d1 = (double)((float)i2 + ifluidstate.func_215679_a(world, blockpos$pooledmutableblockpos));
									if (d1 >= axisalignedbb.minY) {
										flag1 = true;
										d0 = Math.max(d1 - axisalignedbb.minY, d0);
										if (flag) {
											Vec3d vec3d1 = ifluidstate.getFlow(world, blockpos$pooledmutableblockpos);
											if (d0 < 0.4D) {
												vec3d1 = vec3d1.scale(d0);
											}

											vec3d = vec3d.add(vec3d1);
											++k1;
										}
									}
								}
							}
						}
					}
				}

				if (vec3d.length() > 0.0D) {
					if (k1 > 0) {
						vec3d = vec3d.scale(1.0D / (double)k1);
					}

					vec3d = vec3d.normalize();

					motion = motion.add(vec3d.scale(0.014D));
				}
				return flag1;
			}
		}
	}
	
	public static class TridentProjectileArc extends ProjectileArcData {
		
		@Override
		public float getWaterDrag(){
			return 0.99f;
		}
		
	}

	public static boolean isCrossbowLoadedWith(ItemStack p_220019_0_, Item p_220019_1_) {
		return getLoadedCrossbowItems(p_220019_0_).stream().anyMatch((p_220010_1_) -> {
			return p_220010_1_.getItem() == p_220019_1_;
		});
	}

	public static List<ItemStack> getLoadedCrossbowItems(ItemStack p_220018_0_) {
		List<ItemStack> list = Lists.newArrayList();
		CompoundNBT compoundnbt = p_220018_0_.getTag();
		if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
			ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 10);
			if (listnbt != null) {
				for(int i = 0; i < listnbt.size(); ++i) {
					CompoundNBT compoundnbt1 = listnbt.getCompound(i);
					list.add(ItemStack.read(compoundnbt1));
				}
			}
		}

		return list;
	}
	
}
