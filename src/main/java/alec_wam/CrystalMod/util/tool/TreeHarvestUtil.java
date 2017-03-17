package alec_wam.CrystalMod.util.tool;

import java.util.Stack;

import alec_wam.CrystalMod.util.HarvestResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TreeHarvestUtil {
 
  private int horizontalRange;
  private int verticalRange;
  private BlockPos origin;

  public TreeHarvestUtil() {
  }
  
  public static class TreeData {
	  private int leaves;
	  private boolean isValid;
	  
	  public TreeData(int leaves, boolean valid){
		  this.leaves = leaves;
		  this.isValid = valid;
	  }

	public int getLeaves() {
		return leaves;
	}

	public boolean isValid() {
		return isValid;
	}
  }
  
  //TinkersConstruct LumberAxe
  public static TreeData isFullTree(World world, BlockPos origin, BaseHarvestTarget harvest) {
	  BlockPos pos = null;
	  Stack<BlockPos> candidates = new Stack<BlockPos>();
	  candidates.add(origin);

	  while(!candidates.isEmpty()) {
		  BlockPos candidate = candidates.pop();
		  if((pos == null || candidate.getY() > pos.getY()) && harvest.isTarget(world.getBlockState(candidate))) {
			  pos = candidate.up();
			  while(harvest.isTarget(world.getBlockState(pos))) {
				  pos = pos.up();
			  }
			  candidates.add(pos.north());
			  candidates.add(pos.east());
			  candidates.add(pos.south());
			  candidates.add(pos.west());
		  }
	  }

	  if(pos == null) {
		  return new TreeData(0, false);
	  }

	  int d = 3;
	  int o = -1;
	  int leaves = 0;
	  for(int x = 0; x < d; x++) {
		  for(int y = 0; y < d; y++) {
			  for(int z = 0; z < d; z++) {
				  BlockPos leaf = pos.add(o + x, o + y, o + z);
				  IBlockState state = world.getBlockState(leaf);
				  if(isLeaves(state, world, leaf)) {
					  if(++leaves >= 5) {
						  return new TreeData(leaves, true);
					  }
				  }
			  }
		  }
	  }
	  return new TreeData(0, false);
  }

  public void harvest(World world, BlockPos loc, int size, boolean ignoreMeta, BlockPos bc, HarvestResult res) {
    horizontalRange = size + 7;
    verticalRange = 30;
    harvest(world, loc, bc, res, ignoreMeta);
  }

  public void harvest(World world, BlockPos bc, HarvestResult res) {
    horizontalRange = 12;
    verticalRange = 30;
    origin = new BlockPos(bc);
    IBlockState wood = world.getBlockState(bc);
    harvestUp(world, bc, res, new HarvestTarget(wood));
  }

  private void harvest(World world, BlockPos origin, BlockPos bc, HarvestResult res, boolean ignoreMeta) {
    this.origin = new BlockPos(origin);
    IBlockState wood = world.getBlockState(bc);
    if (ignoreMeta) {
      harvestUp(world, bc, res, new BaseHarvestTarget(wood.getBlock()));
    } else {
      harvestUp(world, bc, res, new HarvestTarget(wood));
    }
  }

  protected void harvestUp(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {

    if (!isInHarvestBounds(bc) || res.getHarvestedBlocks().contains(bc)) {
      return;
    }
    IBlockState bs = world.getBlockState(bc);    
    boolean isLeaves = isLeaves(bs, world, bc);
    if (target.isTarget(bs) || isLeaves) {
      res.getHarvestedBlocks().add(bc);
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (dir != EnumFacing.DOWN) {
          harvestUp(world, bc.offset(dir), res, target);
        }
      }
    } else {
      // check the sides for connected wood
      harvestAdjacentWood(world, bc, res, target);
      // and another check for large oaks, where wood can be surrounded by
      // leaves
      
      for(EnumFacing dir : EnumFacing.HORIZONTALS) {
        BlockPos loc = bc.offset(dir);
        IBlockState locBS = world.getBlockState(loc);        
        if (isLeaves(locBS, world, loc)) {
          harvestAdjacentWood(world, loc, res, target);
        }
      }
    }

  }

  public static boolean isLeaves(IBlockState bs, IBlockAccess world, BlockPos pos) {
    return bs.getMaterial() == Material.LEAVES || bs.getBlock() instanceof BlockLeaves || bs.getBlock().isLeaves(bs, world, pos);
  }

  private void harvestAdjacentWood(World world, BlockPos bc, HarvestResult res, BaseHarvestTarget target) {    
    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos targ = bc.offset(dir);
      if(target.isTarget(world.getBlockState(targ))) {
        harvestUp(world, targ, res, target);
      }
    }
  }

  private boolean isInHarvestBounds(BlockPos bc) {

    int dist = Math.abs(origin.getX() - bc.getX());
    if (dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.getZ() - bc.getZ());
    if (dist > horizontalRange) {
      return false;
    }
    dist = Math.abs(origin.getY() - bc.getY());
    if (dist > verticalRange) {
      return false;
    }
    return true;
  }

  private static final class HarvestTarget extends BaseHarvestTarget {

    IBlockState bs;
    EnumType variant;

    HarvestTarget(IBlockState bs) {
      super(bs.getBlock());
      this.bs = bs;
      variant = getVariant(bs);
    }

    public static EnumType getVariant(IBlockState bs) {
      EnumType v = null;
      try {
        v = bs.getValue(BlockNewLog.VARIANT);
      } catch(Exception e) {        
      }
      if (v == null) {
        try {
          v = bs.getValue(BlockOldLog.VARIANT);
        } catch(Exception e) {        
        }
      }
      return v;
    }

    @Override
    boolean isTarget(IBlockState bs) {
      if (variant == null) {
        return super.isTarget(bs);
      }
      return super.isTarget(bs) && variant == getVariant(bs);
    }
  }

  public static class BaseHarvestTarget {

    private final Block wood;

    public BaseHarvestTarget(Block wood) {
      this.wood = wood;
    }

    boolean isTarget(IBlockState bs) {
      return bs.getBlock() == wood;
    }
  }

}
