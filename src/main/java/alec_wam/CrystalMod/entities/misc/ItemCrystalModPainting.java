package alec_wam.CrystalMod.entities.misc;

import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCrystalModPainting extends Item
{
    public ItemCrystalModPainting()
    {
    	super();
        setCreativeTab(CreativeTabs.DECORATIONS);
        ModItems.registerItem(this, "crystalmodpainting");
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        BlockPos blockpos = pos.offset(facing);

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack))
        {
            EntityCrystalModPainting entityhanging = new EntityCrystalModPainting(worldIn, blockpos, facing);

            if (entityhanging != null && entityhanging.onValidSurface())
            {
                if (!worldIn.isRemote)
                {
                    entityhanging.playPlaceSound();
                    worldIn.spawnEntity(entityhanging);
                }
                
                itemstack.shrink(1);

                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

}
