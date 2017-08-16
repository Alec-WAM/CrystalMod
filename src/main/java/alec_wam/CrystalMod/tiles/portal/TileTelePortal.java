package alec_wam.CrystalMod.tiles.portal;

import java.util.List;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.TeleportUtil;
import alec_wam.CrystalMod.util.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileTelePortal extends TileEntityMod implements IFacingTile {

	public BlockPos otherPortalPos;
	public int otherPortalDim;
	protected EnumFacing facing = EnumFacing.NORTH;
	
	public TileTelePortal(){
		
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			Vector3d boxVec = new Vector3d(getPos());
			//boxVec.add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
			double xMin = boxVec.x+(facing.getFrontOffsetX()*1);
			double yMin = boxVec.y+(facing.getFrontOffsetY()*1);
			double zMin = boxVec.z+(facing.getFrontOffsetZ()*1);
			double xMax = xMin;
			double yMax = yMin;
			double zMax = zMin;
			double boxSize = 0.05;
			if(facing == EnumFacing.NORTH){
				xMax+=1;
				yMax+=1;
				zMin = boxVec.z+1;
				zMax = boxVec.z-boxSize;
			}
			if(facing == EnumFacing.SOUTH){
				xMax+=1;
				yMax+=1;
				zMax+=boxSize;
			}
			if(facing == EnumFacing.WEST){
				zMax+=1;
				yMax+=1;
				xMin = boxVec.x+1;
				xMax = boxVec.x-boxSize;
			}
			if(facing == EnumFacing.EAST){
				zMax+=1;
				yMax+=1;
				xMax+=boxSize;
			}
			if(facing == EnumFacing.UP){
				zMax+=1;
				xMax+=1;
				yMax+=boxSize;
			}
			if(facing == EnumFacing.DOWN){
				zMax+=1;
				xMax+=1;
				yMin = boxVec.y+1;
				yMax = boxVec.y-boxSize;
			}
			AxisAlignedBB bb = new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax);
			List<Entity> list = getWorld().getEntitiesWithinAABB(Entity.class, bb);
			for(Entity entity : list){
				if(!entity.isDead && !entity.isSneaking()){
					travel(entity);
				}
			}
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(otherPortalPos !=null){
			nbt.setTag("PortalPos", NBTUtil.createPosTag(otherPortalPos));
			nbt.setInteger("PortalDim", otherPortalDim);
		}
		nbt.setInteger("Facing", getFacing());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("PortalPos")){
			otherPortalPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("PortalPos"));
			otherPortalDim = nbt.getInteger("PortalDim");
		}
		this.facing = EnumFacing.getFront(nbt.getInteger("Facing"));
	}
	
	public void travel(Entity entity){
		if(entity.getEntityWorld().isRemote)return;
		if(otherPortalPos !=null){
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(otherPortalDim);
			if(world !=null){
				TileEntity tile = world.getTileEntity(otherPortalPos);
				if(tile !=null && tile instanceof TileTelePortal){
					TileTelePortal otherPortal = (TileTelePortal)tile;
					Vector3d motion = new Vector3d(entity.motionX, entity.motionY, entity.motionZ);
					double ejectPower = 0.0;
					if(facing.getAxis() == Axis.X){
						ejectPower = Math.abs(motion.x);
					}
					if(facing.getAxis() == Axis.Y){
						ejectPower = Math.abs(motion.y);
					}
					if(facing.getAxis() == Axis.Z){
						ejectPower = Math.abs(motion.z);
					}
					teleportTo(entity, otherPortal, ejectPower);
				}
			}
		}
	}
	
	public Entity teleportTo(Entity entity, TileTelePortal portal, double ejectPower){
		if(entity == null || entity.getEntityWorld().isRemote) return entity;
		World destinationWorld = portal.getWorld();

		if (destinationWorld == null){
			ModLogger.warning("Destination world does not exist!");
			return entity;
		}
		
		int portalDim = destinationWorld.provider.getDimension();
		EnumFacing portalFacing = portal.facing;
		Vector3d outPos = new Vector3d(portal.getPos());
		outPos.add(portalFacing.getFrontOffsetX(), portalFacing.getFrontOffsetY(), portalFacing.getFrontOffsetZ());
		outPos.add(0.5, 0.5, 0.5);
		
		Vector3d motionVec = new Vector3d(ejectPower * portalFacing.getFrontOffsetX(), ejectPower * portalFacing.getFrontOffsetY(), ejectPower * portalFacing.getFrontOffsetZ());
		
		switch (portalFacing) {
		case DOWN: {
			break;
		}
		case UP: {
			break;
		}
		case NORTH: {
			break;
		}
		case SOUTH: {
			break;
		}
		case WEST: {
			break;
		}
		case EAST: {
			break;
		}
		default: {
			throw new RuntimeException("Invalid Side (" + portalFacing + ")");
		}
		}
		
		TeleportUtil.teleportEntity(entity, portalDim, outPos.x, outPos.y, outPos.z, entity.rotationYaw, entity.rotationPitch, motionVec);
		entity.fallDistance = 0;
		return entity;
	}

	
	public void eject(Entity entity, EnumFacing facing, double ejectAmount){
		entity.motionX = entity.motionY = entity.motionZ = 0;
		entity.motionX = ejectAmount * facing.getFrontOffsetX();
		entity.motionY = ejectAmount * facing.getFrontOffsetY();
		entity.motionZ = ejectAmount * facing.getFrontOffsetZ();
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
