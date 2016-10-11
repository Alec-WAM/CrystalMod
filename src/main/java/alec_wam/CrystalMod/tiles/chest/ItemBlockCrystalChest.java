package alec_wam.CrystalMod.tiles.chest;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCrystalChest extends ItemBlock 
{
    public ItemBlockCrystalChest(Block block)
    {
        super(block);
        
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta)
    {
        return CrystalChestType.validateMeta(meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return super.getUnlocalizedName(itemstack) + "." + CrystalChestType.values()[itemstack.getMetadata()].name();
    }
}
