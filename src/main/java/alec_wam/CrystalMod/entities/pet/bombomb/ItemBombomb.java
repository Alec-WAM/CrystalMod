package alec_wam.CrystalMod.entities.pet.bombomb;

import java.util.List;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBombomb extends Item implements ICustomModel {
	
	public ItemBombomb(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "bombomb");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemRenderBombomb());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		if(!stack.hasTagCompound())return;
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		if(nbt.hasKey("EntityData")){
			NBTTagCompound entityNBT = nbt.getCompoundTag("EntityData");
			if(entityNBT.hasKey("Health")){
				tooltip.add("Health: "+entityNBT.getFloat("Health"));
			}
			if(entityNBT.hasKey("OwnerUUID")){
				String id = entityNBT.getString("OwnerUUID");
				if(!id.isEmpty() && UUIDUtils.isUUID(id)){
					tooltip.add("Owner: "+ProfileUtil.getUsername(UUIDUtils.fromString(id)));
				}
			}
			if(entityNBT.hasKey("Color")){
				tooltip.add("Color: "+EnumDyeColor.byDyeDamage(entityNBT.getByte("Color")).getName());
			}
		}
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack stack = playerIn.getHeldItem(hand);
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            pos = pos.offset(facing);
            double d0 = 0.0D;

            if (facing == EnumFacing.UP && iblockstate.getBlock() instanceof BlockFence) //Forge: Fix Vanilla bug comparing state instead of block
            {
                d0 = 0.5D;
            }
            
            EntityBombomb bombomb = new EntityBombomb(worldIn);
            
            if (bombomb != null)
            {
            	bombomb.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);
            	bombomb.setTamed(true);
            	bombomb.setOwnerId(EntityPlayer.getUUID(playerIn.getGameProfile()));
            	bombomb.loadFromItem(playerIn, stack);
            	worldIn.spawnEntity(bombomb);
                if (!playerIn.capabilities.isCreativeMode)
                {
                	ItemStackTools.incStackSize(stack, -1);
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }
	
}
