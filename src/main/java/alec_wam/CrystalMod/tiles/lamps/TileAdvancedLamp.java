package alec_wam.CrystalMod.tiles.lamps;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.lamps.BlockAdvancedLamp.LampType;
import alec_wam.CrystalMod.tiles.lamps.BlockFakeLight.LightType;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileAdvancedLamp extends TileEntityMod {

	public boolean init = false;
	public int delay;
	@Override
    public void update() {
		super.update();
        //if (!getWorld().isRemote) {
            if (!init) {
                init = true;
                updateLightBlocks(init);
            } else if (init) {
                // We are lit, check that our blocks are still there.
            	delay--;
                if (delay <= 0) {
                	delay = 10;
                    updateLightBlocks(init);
                }
            }
        //}
    }
	
	public int getRadius(){
		return 10;
	}

    private void updateLightBlocks(boolean lit) {
        int radius = getRadius();
        int count = 0;
        for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					BlockPos pos2 = new BlockPos(x, y, z).add(pos.getX(), pos.getY(), pos.getZ());
					int squareDistance = (x)*(x) + (y) * (y) + (z) * (z);
					if(squareDistance <= radius) {
						if(lit){
							if(!isInvisibleLight(pos2) && getWorld().isAirBlock(pos2)){
								setInvisibleBlock(pos2);
								getWorld().checkLight(pos2);
								count++;
							}
						} else {
							if(isInvisibleLight(pos2)){
								getWorld().setBlockState(pos2, Blocks.AIR.getDefaultState(), 3);
								getWorld().checkLight(pos2);
								count++;
							}
						}
					}
				}
			}
		}
		
		if(lit && count > 0){
			ModLogger.info("Placed "+count+" light blocks");
		}
		if(!lit && count > 0){
			ModLogger.info("Removed "+count+" light blocks");
		}
    }

    private boolean setInvisibleBlock(BlockPos npos) {
    	boolean dark = false;
    	IBlockState thisState = getWorld().getBlockState(getPos());
    	if(thisState.getBlock() == ModBlocks.advancedLamp){
    		dark = thisState.getValue(BlockAdvancedLamp.TYPE) == LampType.DARK;
    	}
    	IBlockState place = ModBlocks.fakeLight.getDefaultState().withProperty(BlockFakeLight.TYPE, dark ? LightType.DARK : LightType.LIGHT);
    	
        return getWorld().setBlockState(npos, place, 3);
    }

    private boolean isInvisibleLight(BlockPos lpos) {
    	return getWorld().getBlockState(lpos).getBlock() == ModBlocks.fakeLight;
    }

    public void onBlockBreak() {
        updateLightBlocks(false);
    }
	
}
