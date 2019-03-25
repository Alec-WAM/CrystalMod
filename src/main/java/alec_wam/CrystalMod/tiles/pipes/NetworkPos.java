package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.math.BlockPos;

public class NetworkPos {
	public BlockPos pos;
	public int dim;
	
	public NetworkPos(BlockPos pos, int dim){
		this.pos = pos;
		this.dim = dim;
	}
	
	@Override
    public boolean equals(Object other) {
        if (!(other instanceof NetworkPos)) {
            return false;
        }

        return pos.equals(((NetworkPos)other).pos) && dim == ((NetworkPos)other).dim;
    }
	
	@Override
    public int hashCode() {
        int result = pos.hashCode();
        result = 31 * result + dim;
        return result;
    } 
}
