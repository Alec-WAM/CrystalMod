package alec_wam.CrystalMod.entities.misc;

import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityBambooBoat extends EntityBoat {

	public EntityBambooBoat(World worldIn) {
		super(worldIn);
	}
	
	public EntityBambooBoat(World worldIn, double x, double y, double z)
    {
		super(worldIn, x, y, z);
    }
	
	@Override
	public Item getItemBoat()
    {
		return ModItems.bambooBoat;
    }
	
	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        /*this.lastYd = this.motionY;

        if (!this.isRiding())
        {
            if (onGroundIn)
            {
                if (this.fallDistance > 3.0F)
                {
                    if (this.status != EntityBoat.Status.ON_LAND)
                    {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.fall(this.fallDistance, 1.0F);

                    if (!this.world.isRemote && !this.isDead)
                    {
                        this.setDead();

                        if (this.world.getGameRules().getBoolean("doEntityDrops"))
                        {
                            for (int i = 0; i < 3; ++i)
                            {
                                this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 1, this.getBoatType().getMetadata()), 0.0F);
                            }

                            for (int j = 0; j < 2; ++j)
                            {
                                this.dropItemWithOffset(Items.STICK, 1, 0.0F);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            }
            else if (this.world.getBlockState((new BlockPos(this)).down()).getMaterial() != Material.WATER && y < 0.0D)
            {
                this.fallDistance = (float)((double)this.fallDistance - y);
            }
        }*/
		super.updateFallState(y, onGroundIn, state, pos);
    }

}
