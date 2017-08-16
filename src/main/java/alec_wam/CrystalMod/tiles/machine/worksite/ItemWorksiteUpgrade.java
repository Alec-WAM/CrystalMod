package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.HashSet;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWorksiteUpgrade extends Item implements ICustomModel {

	public ItemWorksiteUpgrade() {
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "worksiteupgrade");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(WorksiteUpgrade type : WorksiteUpgrade.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.flag(), new ModelResourceLocation(getRegistryName(), "type="+type.flag()));
        }
    }

	public static WorksiteUpgrade getUpgrade(ItemStack stack) {
		if (ItemStackTools.isNullStack(stack) || stack.getItem() == null || !(stack.getItem() instanceof ItemWorksiteUpgrade)) {
			throw new RuntimeException(
					"Cannot retrieve worksite upgrade type for: " + stack
							+ ".  Null stack, or item, or mismatched item!");
		}
		return WorksiteUpgrade.values()[stack.getItemDamage()];
	}

	public static ItemStack getStack(WorksiteUpgrade upgrade) {
		return upgrade == null ? null : new ItemStack(ModItems.worksiteUpgrade, 1, upgrade.ordinal());
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName(par1ItemStack) + "."
				+ par1ItemStack.getItemDamage();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs p_150895_2_, NonNullList<ItemStack> list) {
		for (WorksiteUpgrade type : WorksiteUpgrade.values()) {
			list.add(new ItemStack(item, 1, type.flag()));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(world.isRemote){
			return EnumActionResult.PASS;
		}
		if (pos != null) {
			TileEntity te = player.getEntityWorld().getTileEntity(pos);
			if (te instanceof IWorkSite) {
				IWorkSite ws = (IWorkSite) te;
				ItemStack stack = player.getHeldItem(hand);
				WorksiteUpgrade upgrade = getUpgrade(stack);
				if (!ws.getValidUpgrades().contains(upgrade)) {
					return EnumActionResult.PASS;
				}
				HashSet<WorksiteUpgrade> wsug = new HashSet<WorksiteUpgrade>(
						ws.getUpgrades());
				if (wsug.contains(upgrade)) {
					return EnumActionResult.PASS;
				}
				for (WorksiteUpgrade ug : wsug) {
					if (ug.exclusive(upgrade)) {
						return EnumActionResult.PASS;// exclusive upgrade present, exit early
					}
				}
				for (WorksiteUpgrade ug : wsug) {
					if (upgrade.overrides(ug)) {
						ItemUtil.spawnItemInWorldWithRandomMotion(player.getEntityWorld(), getStack(ug), te.getPos());
						ws.removeUpgrade(ug);
					}
				}
				ws.addUpgrade(upgrade);
				ItemStackTools.incStackSize(stack, -1);
				if (ItemStackTools.isEmpty(stack)) {
					player.setHeldItem(hand, ItemStackTools.getEmptyStack());
				}
				player.openContainer.detectAndSendChanges();
				return EnumActionResult.SUCCESS;
			}
		}
        return EnumActionResult.PASS;
    }

}
