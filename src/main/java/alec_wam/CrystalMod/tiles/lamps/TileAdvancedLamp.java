package alec_wam.CrystalMod.tiles.lamps;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.lamps.BlockAdvancedLamp.LampType;
import alec_wam.CrystalMod.tiles.lamps.BlockFakeLight.LightType;
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
        if (!getWorld().isRemote) {
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
        }
    }

    private void updateLightBlocks(boolean lit) {
        int radius = 10;
        //TODO Make radius bigger for dark lamp
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					BlockPos pos2 = new BlockPos(x, y, z).add(pos.getX(), pos.getY(), pos.getZ());
					int squareDistance = (x)*(x) + (y) * (y) + (z) * (z);
					if(squareDistance <= radius) {
						if(lit){
							if(!isInvisibleLight(pos2) && getWorld().isAirBlock(pos2)){
								setInvisibleBlock(pos2);
							}
						} else {
							if(isInvisibleLight(pos2)){
								getWorld().setBlockToAir(pos2);
							}
						}
					}
				}
			}
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
