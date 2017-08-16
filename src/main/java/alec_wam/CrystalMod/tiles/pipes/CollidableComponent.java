package alec_wam.CrystalMod.tiles.pipes;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class CollidableComponent {

  public final AxisAlignedBB bound;
  public final EnumFacing dir;
  public final Object data;

  public CollidableComponent(AxisAlignedBB bound, EnumFacing id, Object data) {
    this.bound = bound;
    this.dir = id;
    this.data = data;
  }

  @Override
  public String toString() {
    return "CollidableComponent [bound=" + bound + ", id=" + dir + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof CollidableComponent) {
      CollidableComponent other = (CollidableComponent) obj;
      return bound.equals(((CollidableComponent) obj).bound) && dir == other.dir;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bound == null) ? 0 : bound.hashCode());
    result = prime * result + ((dir == null) ? 0 : dir.hashCode());
    return result;
  }
}
