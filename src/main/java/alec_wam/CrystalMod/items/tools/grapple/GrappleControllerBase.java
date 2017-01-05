package alec_wam.CrystalMod.items.tools.grapple;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//Credit https://github.com/yyon/grapplemod/
public class GrappleControllerBase {

	public GrappleType type;
	public World world;
	public Entity entity;
	public int entityID;
	public int hookID;
	public Vec3d pos;
	
	public double playerForward = 0;
	public double playerStrafe = 0;
	public boolean playerJump = false;
	public boolean waitingOnPlayerJump = false;
	public Vec3d playerMovement_unrotated = new Vec3d(0,0,0);
	public Vec3d playerMovement = new Vec3d(0,0,0);
	
	public boolean attached = true;
	
	public double r;
	public Vec3d motion;
	
	public int onGroundTimer = 0;
	public int maxOnGroundTimer = 3;
	
	public int maxlen;
	
	public GrappleControllerBase(GrappleType type, int entityID, int hookId, World world, Vec3d pos, int maxLength){
		this.type = type;
		this.world = world;
		this.entityID = entityID;
		this.hookID = hookId;
		this.entity = world.getEntityByID(entityID);
		this.pos = pos;
		this.maxlen = maxLength;
		
		this.r = this.pos.subtract(entity.posX, entity.posY, entity.posZ).lengthVector();
		this.motion = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
		
		this.onGroundTimer = 0;
		
		GrappleHandler.registerController(entityID, this);
	}
	
	public void clientTick(){
		if (attached) {
			if (entity == null || entity.isDead) {
				unattach();
			} else {
				GrappleHandler.getPlayerMovement(this, entity.getEntityId());
				updatePlayerPos();
			}
		}
	}
	
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				this.normalGround();
				this.normalCollisions();

				Vec3d arrowpos = this.pos;
				Vec3d playerpos = new Vec3d(entity.posX, entity.posY, entity.posZ);

				Vec3d oldspherevec = playerpos.subtract(arrowpos);
				Vec3d spherevec = changelen(oldspherevec, r);
				Vec3d spherechange = spherevec.subtract(oldspherevec);

				Vec3d additionalmotion;
				if (arrowpos.subtract(playerpos).lengthVector() < this.r) {
					additionalmotion = new Vec3d(0,0,0);
				} else {
					additionalmotion = spherechange;
				}

				double dist = oldspherevec.lengthVector();
				this.calctaut(dist);

				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					if (this.isJumping()) {
						this.doJump(player, spherevec);
						return;
					} else if (GrappleHandler.isSneaking(entity)) {
						if (arrowpos.yCoord > playerpos.yCoord) {
							Vec3d motiontorwards = changelen(spherevec, -0.1);
							motiontorwards = new Vec3d(motiontorwards.xCoord, 0, motiontorwards.zCoord);
							if (motion.dotProduct(motiontorwards) < 0) {
								motion = motion.add(motiontorwards);
							}

							Vec3d newmotion = dampenmotion(motion, motiontorwards);
							motion = new Vec3d(newmotion.xCoord, motion.yCoord, newmotion.zCoord);

							if (this.playerForward != 0) {
								if (dist < maxlen || this.playerForward > 0 || maxlen == 0) {
									additionalmotion = new Vec3d(0, this.playerForward, 0);
									this.r = dist;
									this.r -= this.playerForward*0.3;
									if (this.r < 0) {
										this.r = dist;
									}
								}
							}
						}
					} else {
						motion = motion.add(changelen(this.playerMovement, 0.01));
					}
				}

				if (!(this.onGroundTimer > 0)) {
					motion = motion.addVector(0, -0.05, 0);
				}

				Vec3d newmotion = motion.add(additionalmotion);

				if (arrowpos.subtract(playerpos.add(motion)).lengthVector() > r) { // moving away
					motion = removealong(motion, spherevec);
				}

				entity.motionX = newmotion.xCoord;
				entity.motionY = newmotion.yCoord;
				entity.motionZ = newmotion.zCoord;

				updateServerPos();
			}
		}
	}
	
	public boolean isJumping() {
		if (playerJump && waitingOnPlayerJump) {
			waitingOnPlayerJump = false;
			return true;
		}
		return false;
	}
	
	public Vec3d dampenmotion(Vec3d motion, Vec3d forward) {
		Vec3d newmotion = projectVec(motion, forward);
		double dampening = 0.05;
		return new Vec3d(newmotion.xCoord*dampening + motion.xCoord*(1-dampening), newmotion.yCoord*dampening + motion.yCoord*(1-dampening), newmotion.zCoord*dampening + motion.zCoord*(1-dampening));
	}
	
	public void calctaut(double dist) {
		Entity hook = world.getEntityByID(hookID);
		if(hook !=null && hook instanceof EntityGrapplingHook){
			if (dist < this.r) {
				double taut = 1 - ((this.r - dist) / 5);
				if (taut < 0) {
					taut = 0;
				}
				((EntityGrapplingHook)hook).taut = taut;
			} else {
				((EntityGrapplingHook)hook).taut = 1;
			}
		}
	}
	
	public void normalGround() {
		if (entity.onGround) {
			onGroundTimer = maxOnGroundTimer;
			if (this.motion.yCoord < 0) {
				this.motion = new Vec3d(motion.xCoord, 0, motion.zCoord);
			}
		} else {
			if (this.onGroundTimer > 0) {
				onGroundTimer--;
			}
		}
		if (this.onGroundTimer > 0) {
			if (!GrappleHandler.isSneaking(entity)) {
				this.motion = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
			}
		}
	}
	
	public void normalCollisions() {
		// stop if collided with object
		if (entity.isCollidedHorizontally) {
			if (entity.motionX == 0) {
				this.motion = new Vec3d(0, motion.yCoord, motion.zCoord);
			}
			if (entity.motionZ == 0) {
				this.motion = new Vec3d(motion.xCoord, motion.yCoord, 0);
			}
		}
		if (entity.isCollidedVertically) {
			if (entity.motionY == 0) {
				this.motion = new Vec3d(motion.xCoord, 0, motion.zCoord);
			}
		}
	}

	public static Vec3d multiply(Vec3d v1, double changefactor) {
		return new Vec3d(v1.xCoord * changefactor, v1.yCoord * changefactor, v1.zCoord * changefactor);
	}
	
	public Vec3d changelen(Vec3d v1, double l) {
		double oldl = v1.lengthVector();
		if (oldl != 0) {
			double changefactor = l / oldl;
			return multiply(v1, changefactor);
		} else {
			return v1;
		}
	}
	
	public Vec3d projectVec(Vec3d v1, Vec3d v2) {
		Vec3d v3 = v2.normalize();
		double dot = v1.dotProduct(v3);
		return changelen(v3, dot);
	}
	
	public Vec3d removealong(Vec3d v1, Vec3d v2) {
		return v1.subtract(projectVec(v1, v2));
	}
	
	public void updateServerPos() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", entity.posX);
		nbt.setDouble("y", entity.posY);
		nbt.setDouble("z", entity.posZ);
		nbt.setDouble("mX", entity.motionX);
		nbt.setDouble("mY", entity.motionY);
		nbt.setDouble("mZ", entity.motionZ);
		CrystalModNetwork.sendToServer(new PacketEntityMessage(entity, "EntityMovement", nbt));
	}

	public void doJump(Entity player, double jumppower) {
		double maxjump = 1;
		if (onGroundTimer > 0) { // on ground: jump normally
			onGroundTimer = 20;
			return;
		}
		if (player.onGround) {
			jumppower = 0;
		}
		if (player.isCollided) {
			jumppower = maxjump;
		}
		if (jumppower < 0) {
			jumppower = 0;
		}
		
		this.unattach();
		
		if (jumppower > 0) {
			if (jumppower > player.motionY + jumppower) {
				player.motionY = jumppower;
			} else {
				player.motionY += jumppower;
			}
		}
		
		this.updateServerPos();
	}
	
	public void doJump(Entity player, Vec3d spherevec) {
		double maxjump = 1;
		Vec3d jump = new Vec3d(0, maxjump, 0);
		if (spherevec != null) {
			jump = projectVec(jump, spherevec);
		}
		double jumppower = jump.yCoord;
		
		if (spherevec != null && spherevec.yCoord > 0) {
			jumppower = 0;
		}
		if ((this.pos != null) && r < 1 && (player.posY < this.pos.yCoord)) {
			jumppower = maxjump;
		}
		
		this.doJump(player, jumppower);
	}
	
	public void unattach() {
		if (GrappleHandler.controllers.containsValue(this)) {
			this.attached = false;
			
			GrappleHandler.unregisterController(entity.getEntityId());
			
			if (this.type != GrappleType.NONE) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("HookID", hookID);
				CrystalModNetwork.sendToServer(new PacketEntityMessage(entity, "GrappleDisconnect", nbt));
				GrappleHandler.createController(GrappleType.NONE, entityID, hookID, entity.getEntityWorld(), new Vec3d(0,0,0), 0, null);
			}
		}
	}

	public void receivePlayerMovementMessage(float strafe, float forward, boolean jump) {
		playerForward = forward;
		playerStrafe = strafe;
		if (!jump) {
			playerJump = false;
		} else if (jump && !playerJump) {
			playerJump = true;
			waitingOnPlayerJump = true;
		}
		playerMovement_unrotated = new Vec3d(strafe, 0, forward);
		playerMovement = playerMovement_unrotated.rotateYaw((float) (this.entity.rotationYaw * (Math.PI / 180.0)));
	}
	
}
