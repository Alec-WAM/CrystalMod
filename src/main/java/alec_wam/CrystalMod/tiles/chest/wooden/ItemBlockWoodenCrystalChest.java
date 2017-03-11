package alec_wam.CrystalMod.tiles.chest.wooden;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWoodenCrystalChest extends ItemBlock 
{
    public ItemBlockWoodenCrystalChest(Block block)
    {
        super(block);
        
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta)
    {
        return WoodenCrystalChestType.validateMeta(meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return super.getUnlocalizedName(itemstack) + "." + WoodenCrystalChestType.values()[itemstack.getMetadata()].name();
    }
}
