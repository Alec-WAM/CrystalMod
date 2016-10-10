package com.alec_wam.CrystalMod.tiles.machine.elevator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.tiles.TileEntityMod;
import com.alec_wam.CrystalMod.tiles.machine.elevator.caller.TileEntityElevatorCaller;
import com.alec_wam.CrystalMod.tiles.machine.elevator.floor.TileEntityElevatorFloor;
import com.alec_wam.CrystalMod.util.BlockUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TileEntityElevator extends TileEntityMod implements ITickable {

	private boolean prevIn = false;
    private boolean powered = false;

    private double movingY = -1;
    private int startY;
    private int stopY;

    // The positions of the blocks we are currently moving (with 'y' set to the height of the controller)
    private List<BlockPos> positions = new ArrayList<BlockPos>();
    private Bounds bounds;
    // The state that is moving
    private IBlockState movingState;
    
    // Cache: points to the current controller (bottom elevator block)
    private BlockPos cachedControllerPos;
    private int cachedLevels;       // Cached number of levels
    private int cachedCurrent = -1;
    
    public Map<Integer, BlockPos> floors = Maps.newHashMap(); 
    public List<TileEntityElevatorCaller> watchers = Lists.newArrayList();
    
    // All entities currently on the platform (server side only)
    private Set<Entity> entitiesOnPlatform = new HashSet<Entity>();
    private boolean entitiesOnPlatformComplete = false; // If true then we know entitiesOnPlatform is complete, otherwise it only contains players.
	
    public void clearCaches() {
        EnumFacing side = worldObj.getBlockState(getPos()) != ModBlocks.elevator ? null : worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        for (int y = 0 ; y < worldObj.getHeight() ; y++) {
            BlockPos pos2 = getPosAtY(getPos(), y);
            TileEntity te = worldObj.getTileEntity(pos2);
            if (worldObj.getBlockState(pos2).getBlock() == ModBlocks.elevator) {
	            if (te instanceof TileEntityElevator) {
	                EnumFacing side2 = worldObj.getBlockState(getPos()) != ModBlocks.elevator ? null : worldObj.getBlockState(pos2).getValue(BlockElevator.FACING_HORIZ);
	                if (side == null || side2 == null || side2 == side) {
	                	TileEntityElevator tileEntity = (TileEntityElevator) te;
	                    tileEntity.cachedControllerPos = null;
	                    tileEntity.cachedLevels = 0;
	                    tileEntity.cachedCurrent = -1;
	                }
	            }
            }
        }
    }
    
    public void setPowered(int powered) {
        boolean p = powered > 0;
        if (this.powered != p) {
            this.powered = p;
            markDirty();
        }
    }
	
    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (isMoving()) {
            	markDirtyClient();

                //TODO Energy
                /*int rfNeeded = (int) (ElevatorConfiguration.rfPerTickMoving * (3.0f - getInfusedFactor()) / 3.0f);
                if (getEnergyStored(EnumFacing.DOWN) < rfNeeded) {
                    return;
                }
                consumeEnergy(rfNeeded);*/

                double d = calculateSpeed();
                boolean stopped = handlePlatformMovement(d);
                if (stopped) {
                    stopMoving();
                    moveEntities(0, true);
                    updateFloors(true);
                    clearMovement();
                } else {
                    moveEntities(d, false);
                }
                return;
            }

            if (powered == prevIn) {
                return;
            }
            prevIn = powered;
            markDirty();

            if (powered) {
                movePlatformHere();
            }
        } else {
        	if (isMoving()) {
                handleClientMovement();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleClientMovement() {
        double d = calculateSpeed();
        handlePlatformMovement(d);
        if(bounds !=null){
	        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	        AxisAlignedBB aabb = getAABBAboveElevator(d);
	        boolean on = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().intersectsWith(aabb);
	        if (on) {
	            player.setPosition(player.posX, movingY + 1, player.posZ);
	            player.onGround = true;
	            player.fallDistance = 0;
	        }
        }
    }

    public static double minimumSpeed = .1;
    public static double maximumSpeed = .3;
    public static double maxSpeedDistanceStart = 5;
    public static double maxSpeedDistanceEnd = 2;
    
    private double calculateSpeed() {
        // The speed center y location is the location at which speed is maximum.
        // It is located closer to the end to make sure slowing down is a shorter period then speeding up.
        double speedDiff = maximumSpeed - minimumSpeed;
        double speedFromStart = minimumSpeed + speedDiff * Math.abs((movingY - startY) / maxSpeedDistanceStart);
        double speedFromStop = minimumSpeed + speedDiff * Math.abs((movingY - stopY) / maxSpeedDistanceEnd);
        double d = Math.min(speedFromStart, speedFromStop);
        if (stopY < startY) {
            d = -d;
        }
        return d;
    }

    private boolean handlePlatformMovement(double d) {
        if (stopY > startY) {
            if (movingY >= stopY) {
                return true;
            }
            movingY += d;

            if (movingY >= stopY) {
                movingY = stopY;
            }
        } else {
            if (movingY <= stopY) {
                return true;
            }
            movingY += d;

            if (movingY <= stopY) {
                movingY = stopY;
            }
        }
        return false;
    }

    private void moveEntities(double speed, boolean stop) {
    	List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, getAABBAboveElevator(speed));
    	double offset = speed > 0 ? speed * 2 : speed;
    	Set<Entity> oldEntities = this.entitiesOnPlatform;
    	entitiesOnPlatform = new HashSet<Entity>();
        for (Entity entity : entities) {
        	entity.fallDistance = 0;
        	entitiesOnPlatform.add(entity);
        	moveEntityOnPlatform(stop, offset, entity);
            entity.onGround = true;
            entity.fallDistance = 0;
        }
        
        for (Entity entity : oldEntities) {
            if (!this.entitiesOnPlatform.contains(entity)) {
                // Entity was on the platform before but it isn't anymore. If it was a player we do a safety check
                // to ensure it is still in the patform shaft and in that case put it back on the platform.
                // We also put back the entity if we know the list is complete.
                if (entity instanceof EntityPlayer || entitiesOnPlatformComplete) {
                    if (entity.getEntityBoundingBox().intersectsWith(getAABBBigMargin())) {
                        // Entity is no longer on the platform but was on the platform before and
                        // is still in the elevator shaft. In that case we put it back.
                    	entity.fallDistance = 0;
                    	entitiesOnPlatform.add(entity);
                        moveEntityOnPlatform(stop, offset, entity);
                        entity.onGround = true;
                        entity.fallDistance = 0;
                    }
                }

                if (entity instanceof EntityPlayer) {
                    //BuffProperties.disableElevatorMode((EntityPlayer) entity);
                }
            }
        }

        // Entities on platform is now complete so set this to true
        entitiesOnPlatformComplete = true;
    }
    
    private void moveEntityOnPlatform(boolean stop, double offset, Entity entity) {
        if (entity instanceof EntityPlayer) {
            double dy = 1;
            //EntityPlayer player = (EntityPlayer) entity;
            if (stop) {
                //BuffProperties.disableElevatorMode(player);
                entity.posY = movingY + dy;
                entity.setPositionAndUpdate(entity.posX, movingY + dy, entity.posZ);
            } else {
                //BuffProperties.enableElevatorMode(player);
                entity.setPosition(entity.posX, movingY + dy, entity.posZ);
            }
        } else {
            double dy = 1.2 + offset;
            entity.posY = movingY + dy;
            entity.setPositionAndUpdate(entity.posX, movingY + dy, entity.posZ);
        }
    }

    // Find the position of the bottom elevator.
    public BlockPos findBottomElevator() {
    	if (cachedControllerPos != null) {
            return cachedControllerPos;
        }
        // The orientation of this elevator.
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);

        for (int y = 0 ; y < worldObj.getHeight() ; y++) {
            BlockPos elevatorPos = getPosAtY(getPos(), y);
            IBlockState otherState = worldObj.getBlockState(elevatorPos);
            if (otherState.getBlock() instanceof BlockElevator) {
                EnumFacing otherSide = otherState.getValue(BlockElevator.FACING_HORIZ);
                if (otherSide == side) {
                	cachedControllerPos = elevatorPos;
                    return elevatorPos;
                }
            }
        }
        return null;
    }

    // Find the position of the elevator that has the platform.
    public BlockPos findElevatorWithPlatform() {
        // The orientation of this elevator.
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);

        for (int y = 0 ; y < worldObj.getHeight() ; y++) {
            BlockPos elevatorPos = getPosAtY(getPos(), y);
            IBlockState otherState = worldObj.getBlockState(elevatorPos);
            if (otherState.getBlock() instanceof BlockElevator) {
                EnumFacing otherSide = otherState.getValue(BlockElevator.FACING_HORIZ);
                if (otherSide == side) {
                    BlockPos frontPos = elevatorPos.offset(side);
                    if (isValidPlatformBlock(frontPos)) {
                        return elevatorPos;
                    }
                }
            }
        }
        return null;
    }

    private boolean isValidPlatformBlock(BlockPos frontPos) {
        if (worldObj.isAirBlock(frontPos)) {
            return false;
        }
        if (worldObj.getTileEntity(frontPos) != null) {
            return false;
        }
        return true;
    }

    public List<BlockPos> getPositions() {
        return positions;
    }

    public IBlockState getMovingState() {
        return movingState;
    }

    private void stopMoving() {
        movingY = stopY;
        for (BlockPos pos : positions) {
            worldObj.setBlockState(getPosAtY(pos, (int) stopY), movingState, 3);
        }
        // Current level will have to be recalculated
        cachedCurrent = -1;
        //markDirtyClient();
    }
    
    private void clearMovement() {
        positions.clear();
        entitiesOnPlatform.clear();
        movingState = null;
        bounds = null;
        movingY = -1;
    }

    private static class Bounds {
    	private int minX = 1000000000;
    	private int minZ = 1000000000;
    	private int maxX = -1000000000;
    	private int maxZ = -1000000000;

    	public Bounds() {
    	}
 
        public Bounds(int minX, int minZ, int maxX, int maxZ) {
        	this.maxX = maxX;
        	this.maxZ = maxZ;
        	this.minX = minX;
     		this.minZ = minZ;
        }
    	
        public void addPos(BlockPos pos) {
            if (pos.getX() < minX) {
                minX = pos.getX();
            }
            if (pos.getX() > maxX) {
                maxX = pos.getX();
            }
            if (pos.getZ() < minZ) {
                minZ = pos.getZ();
            }
            if (pos.getZ() > maxZ) {
                maxZ = pos.getZ();
            }
        }

        public int getMinX() {
            return minX;
        }

        public int getMinZ() {
            return minZ;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMaxZ() {
            return maxZ;
        }
    }

    public static int maxPlatformSize = 11;
    
    // Only call this on the controller (bottom elevator)
    private void startMoving(BlockPos start, BlockPos stop, IBlockState state) {
        //System.out.println("Start moving: ystart = " + start.getY() + ", ystop = " + stop.getY());
        movingY = start.getY();
        startY = start.getY();
        stopY = stop.getY();
        movingState = state;
        positions.clear();

        getBounds(start);
    }

 	// Always called on controller TE (bottom one)
    private void getBounds(BlockPos start) {
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        bounds = new Bounds();
        for (int a = 1; a < maxPlatformSize; a++) {
            BlockPos offset = start.offset(side, a);
            if (worldObj.getBlockState(offset) == movingState) {
                worldObj.setBlockToAir(offset);
                bounds.addPos(offset);
                positions.add(getPosAtY(offset, getPos().getY()));

                for (int b = 1; b <= (maxPlatformSize / 2); b++) {
                    BlockPos offsetLeft = offset.offset(side.rotateY(), b);
                    if (worldObj.getBlockState(offsetLeft) == movingState) {
                        worldObj.setBlockToAir(offsetLeft);
                        bounds.addPos(offsetLeft);
                        positions.add(getPosAtY(offsetLeft, getPos().getY()));
                    } else {
                        break;
                    }
                }

                for (int b = 1; b <= (maxPlatformSize / 2); b++) {
                    BlockPos offsetRight = offset.offset(side.rotateYCCW(), b);
                    if (worldObj.getBlockState(offsetRight) == movingState) {
                        worldObj.setBlockToAir(offsetRight);
                        bounds.addPos(offsetRight);
                        positions.add(getPosAtY(offsetRight, getPos().getY()));
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    public AxisAlignedBB getAABBBigMargin() {
        return new AxisAlignedBB(bounds.getMinX(), movingY-150, bounds.getMinZ(), bounds.getMaxX() + 1, movingY + 150, bounds.getMaxZ() + 1);
    }
    
    public AxisAlignedBB getAABBAboveElevator(double speed) {
        double o1;
        double o2;
        if (speed > 0) {
            o1 = -speed * 2;
            o2 = 0;
        } else {
            o1 = 0;
            o2 = -speed * 2;
        }
        return new AxisAlignedBB(bounds.getMinX(), movingY-1+o1, bounds.getMinZ(), bounds.getMaxX() + 1, movingY + 3+o2, bounds.getMaxZ() + 1);
    }
    
    public void markDirtyClient() {
        markDirty();
        if (worldObj != null) {
            worldObj.notifyBlockOfStateChange(getPos(), ModBlocks.elevator);
        }
    }

    public boolean isMoving() {
        return movingY >= 0;
    }

    public double getMovingY() {
        return movingY;
    }

    private void movePlatformHere() {
        // Try to find a platform and move it to this elevator.
        // What about TE blocks in front of platform?

        // First check if the platform is here already:
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        BlockPos frontPos = getPos().offset(side);
        if (isValidPlatformBlock(frontPos)) {
            // Platform is already here (or something is blocking here)
            return;
        }

        // Find where the platform is.
        BlockPos platformPos = findElevatorWithPlatform();
        if (platformPos == null) {
            // No elevator platform found
            return;
        }

        // Find the bottom elevator (this is the one doing the work).
        BlockPos controllerPos = findBottomElevator();
        TileEntityElevator controller = (TileEntityElevator) worldObj.getTileEntity(controllerPos);

        if (controller.isMoving()) {
            // Already moving, do nothing
            return;
        }
        IBlockState state = worldObj.getBlockState(platformPos.offset(side));
        controller.startMoving(platformPos, getPos(), state);
    }

 // Go to the specific level (levels start at 0)
    public void toLevel(int level) {
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        BlockPos controllerPos = findBottomElevator();
        for (int y = controllerPos.getY() ; y < worldObj.getHeight() ; y++) {
            BlockPos pos2 = getPosAtY(controllerPos, y);
            TileEntity te2 = worldObj.getTileEntity(pos2);
            if (worldObj.getBlockState(pos2).getBlock() == ModBlocks.elevator) {
	            if (te2 instanceof TileEntityElevator) {
	                EnumFacing side2 = worldObj.getBlockState(pos2).getValue(BlockElevator.FACING_HORIZ);
	                if (side == side2) {
	                    if (level == 0) {
	                        ((TileEntityElevator) te2).movePlatformHere();
	                        return;
	                    }
	                    level--;
	                }
	            }
            }
        }
    }

    public int getCurrentLevel() {
        BlockPos controllerPos = findBottomElevator();
        IBlockState blockState = worldObj.getBlockState(getPos());
        if (blockState.getBlock() != ModBlocks.elevator) {
        	return 0;
        }
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        TileEntity te = worldObj.getTileEntity(controllerPos);
        if (te instanceof TileEntityElevator) {
            TileEntityElevator controller = (TileEntityElevator) te;
            if (controller.cachedCurrent == -1) {
                int level = 0;
                for (int y = controllerPos.getY() ; y < worldObj.getHeight() ; y++) {
                    BlockPos pos2 = getPosAtY(controllerPos, y);
                    TileEntity te2 = worldObj.getTileEntity(pos2);
                    if (te2 instanceof TileEntityElevator) {
                        EnumFacing side2 = worldObj.getBlockState(pos2).getValue(BlockElevator.FACING_HORIZ);
                        if (side == side2) {
                            BlockPos frontPos = pos2.offset(side);
                            if (isValidPlatformBlock(frontPos)) {
                                controller.cachedCurrent = level;
                            }
                            level++;
                        }
                    }
                }
            }
            return controller.cachedCurrent;
        }
        return 0;
    }

    public int getLevelCount() {
        BlockPos controllerPos = findBottomElevator();
        EnumFacing side = worldObj.getBlockState(getPos()).getValue(BlockElevator.FACING_HORIZ);
        TileEntity te = worldObj.getTileEntity(controllerPos);
        if (te instanceof TileEntityElevator) {
            TileEntityElevator controller = (TileEntityElevator) te;
            if (controller.cachedLevels == 0) {
                for (int y = controllerPos.getY() ; y < worldObj.getHeight() ; y++) {
                    BlockPos pos2 = getPosAtY(controllerPos, y);
                    TileEntity te2 = worldObj.getTileEntity(pos2);
                    if (te2 instanceof TileEntityElevator) {
                        EnumFacing side2 = worldObj.getBlockState(pos2).getValue(BlockElevator.FACING_HORIZ);
                        if (side == side2) {
                            controller.cachedLevels++;
                        }
                    }
                }
            }
            return controller.cachedLevels;
        }
        return 0;
    }
    
    private BlockPos getPosAtY(BlockPos p, int y) {
        return new BlockPos(p.getX(), y, p.getZ());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (isMoving()) {
            return new AxisAlignedBB(getPos().add(-9, 0, -9), getPos().add(9, 255, 9));
        }
        return super.getRenderBoundingBox();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        if (isMoving()) {
            return 256 * 256;
        } else {
            return super.getMaxRenderDistanceSquared();
        }
    }

    private static short bytesToShort(byte b1, byte b2) {
        short s1 = (short) (b1 & 0xff);
        short s2 = (short) (b2 & 0xff);
        return (short) (s1 * 256 + s2);
    }

    private static byte shortToByte1(short s) {
        return (byte) ((s & 0xff00) >> 8);
    }

    private static byte shortToByte2(short s) {
        return (byte) (s & 0xff);
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void readCustomNBT(NBTTagCompound tagCompound) {
        super.readCustomNBT(tagCompound);

    	entitiesOnPlatformComplete = false;
        if (tagCompound.hasKey("players")) {
            entitiesOnPlatform.clear();
            WorldServer world = DimensionManager.getWorld(0);
            List<EntityPlayerMP> serverPlayers = world.getMinecraftServer().getPlayerList().getPlayerList();
            NBTTagList playerList = tagCompound.getTagList("players", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < playerList.tagCount(); i++) {
                NBTTagCompound p = playerList.getCompoundTagAt(i);
                long lsb = p.getLong("lsb");
                long msb = p.getLong("msb");
                UUID uuid = new UUID(msb, lsb);
                for (EntityPlayerMP serverPlayer : serverPlayers) {
                    if (serverPlayer.getGameProfile().getId().equals(uuid)) {
                        entitiesOnPlatform.add(serverPlayer);
                        break;
                    }
                }
            }
        }
        prevIn = tagCompound.getBoolean("prevIn");
        powered = tagCompound.getBoolean("powered");
        movingY = tagCompound.getDouble("movingY");
        startY = tagCompound.getInteger("startY");
        stopY = tagCompound.getInteger("stopY");
        byte[] byteArray = tagCompound.getByteArray("relcoords");
        positions.clear();
        int j = 0;
        for (int i = 0 ; i < byteArray.length / 6 ; i++) {
            short dx = bytesToShort(byteArray[j+0], byteArray[j+1]);
            short dy = bytesToShort(byteArray[j+2], byteArray[j+3]);
            short dz = bytesToShort(byteArray[j+4], byteArray[j+5]);
            j += 6;
            RelCoordinate c = new RelCoordinate(dx, dy, dz);
            positions.add(new BlockPos(getPos().getX() + c.getDx(), getPos().getY() + c.getDy(), getPos().getZ() + c.getDz()));
        }
        if (tagCompound.hasKey("bminX")) {
            bounds = new Bounds(tagCompound.getInteger("bminX"), tagCompound.getInteger("bminZ"), tagCompound.getInteger("bmaxX"), tagCompound.getInteger("bmaxZ"));
        }
        if (tagCompound.hasKey("movingBlock")) {
            String id = tagCompound.getString("movingBlock");
            int meta = tagCompound.getInteger("movingMeta");
            movingState = Block.REGISTRY.getObject(new ResourceLocation(id)).getStateFromMeta(meta);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tagCompound) {
        super.writeCustomNBT(tagCompound);
        
        tagCompound.setBoolean("powered", powered);
        tagCompound.setBoolean("prevIn", prevIn);
        tagCompound.setDouble("movingY", movingY);
        tagCompound.setInteger("startY", startY);
        tagCompound.setInteger("stopY", stopY);
        byte[] blocks = new byte[positions.size() * 6];
        int j = 0;
        for (BlockPos pos : positions) {
            RelCoordinate c = new RelCoordinate(pos.getX() - getPos().getX(), pos.getY() - getPos().getY(), pos.getZ() - getPos().getZ());
            blocks[j+0] = shortToByte1((short) c.getDx());
            blocks[j+1] = shortToByte2((short) c.getDx());
            blocks[j+2] = shortToByte1((short) c.getDy());
            blocks[j+3] = shortToByte2((short) c.getDy());
            blocks[j+4] = shortToByte1((short) c.getDz());
            blocks[j+5] = shortToByte2((short) c.getDz());
            j += 6;
        }
        if (bounds != null) {
            tagCompound.setInteger("bminX", bounds.getMinX());
            tagCompound.setInteger("bminZ", bounds.getMinZ());
            tagCompound.setInteger("bmaxX", bounds.getMaxX());
            tagCompound.setInteger("bmaxZ", bounds.getMaxZ());
        }
        tagCompound.setByteArray("relcoords", blocks);
        if (movingState != null) {
            tagCompound.setString("movingBlock", movingState.getBlock().getRegistryName().toString());
            tagCompound.setInteger("movingMeta", movingState.getBlock().getMetaFromState(movingState));
        }
    }

	public void updateFloors(boolean onlyButtons) {
		//onlyButtons = true;
		/*if(!onlyButtons)*/
		int floorIndex = 0;
		BlockPos controller = findBottomElevator();
		
		TileEntity con = getWorld().getTileEntity(controller);
		if(con ==null || !(con instanceof TileEntityElevator))return;
		TileEntityElevator bottom = (TileEntityElevator)con;
		if(!onlyButtons)bottom.floors.clear();
		List<TileEntityElevatorCaller> callers = Lists.newArrayList();
		for(int y = 0; y < bottom.getLevelCount(); y++){
			BlockPos pos = getPosAtY(bottom.getPos(), bottom.getPos().getY()+y);
			TileEntity tile = getWorld().getTileEntity(pos);
			if(tile !=null && tile instanceof TileEntityElevator){
				//TileEntityElevator ele = (TileEntityElevator)tile;
				for(EnumFacing face : EnumFacing.HORIZONTALS){
					TileEntity tile2 = getWorld().getTileEntity(pos.offset(face));
					if(!onlyButtons){
					if(tile2 !=null && tile2 instanceof TileEntityElevatorFloor){
						TileEntityElevatorFloor floor = (TileEntityElevatorFloor) tile2;
						bottom.floors.put(floorIndex, floor.getPos());
						floorIndex++;
					}
					}
					if(tile2 !=null && tile2 instanceof TileEntityElevatorCaller){
						callers.add((TileEntityElevatorCaller)tile2);
					}
				}
			}
		}
        for(TileEntityElevatorCaller caller : callers){
        	this.updateButtons(caller);
        }
	}
	
	public void updateButtons(TileEntityElevatorCaller caller){
		double buttonHeight = 0.06D;
        double buttonSpacing = 0.02D;
        TileEntityElevatorCaller.ElevatorButton[] elevatorButtons = new TileEntityElevatorCaller.ElevatorButton[floors.size()];
        int columns = (elevatorButtons.length - 1) / 12 + 1;
        List<Integer> floorNums = new ArrayList<Integer>(floors.keySet());
        for(int j = 0; j < columns; j++) {
            for(int i = j * 12; i < floors.size() && i < j * 12 + 12; i++) {
            	int lvl = floorNums.get(i);
            	int fl = floors.get(lvl).getY()-getPos().getY();
                elevatorButtons[i] = new TileEntityElevatorCaller.ElevatorButton(0.2D + 0.6D / columns * j, 0.5D + (Math.min(floors.size(), 12) - 2) * (buttonSpacing + buttonHeight) / 2 - i % 12 * (buttonHeight + buttonSpacing), 0.58D / columns, buttonHeight, lvl, lvl);
                elevatorButtons[i].setColor(fl == this.getCurrentLevel() ? 1 : 0);
                String name = ""+(lvl+1);
                BlockPos posf = floors.get(lvl);
                if(posf !=null){
                	TileEntity tile = getWorld().getTileEntity(posf);
                	if(tile !=null && tile instanceof TileEntityElevatorFloor){
                		name = ((TileEntityElevatorFloor)tile).getName();
                		if(Strings.isNullOrEmpty(name)){
                			name = ""+(lvl+1);
                		}
                	}
                }
                elevatorButtons[i].buttonText = name;
            }
        }
        caller.buttons = elevatorButtons;
        if(caller.getWorld() !=null && caller.getPos() !=null)
        	BlockUtil.markBlockForUpdate(caller.getWorld(), caller.getPos());
	}
	
}
