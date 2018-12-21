package alec_wam.CrystalMod.entities.minions;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.pet.bombomb.EntityBombomb;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMinionStaff extends Item {

	public ItemMinionStaff(){
		super();
		setFull3D();
		setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "minionstaff");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		ItemStack held = player.getHeldItem(hand);
		if(player.isSneaking()){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(held);
			nbt.removeTag("WorksitePos");
			held.setTagCompound(nbt);
			player.setHeldItem(hand, held);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		ItemStack held = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.worksite){
			if(player.isSneaking()){
				NBTTagCompound nbt = ItemNBTHelper.getCompound(held);
				nbt.setTag("WorksitePos", NBTUtil.createPosTag(pos));
				held.setTagCompound(nbt);
				player.setHeldItem(hand, held);
				return EnumActionResult.SUCCESS;
			}
		}
        return EnumActionResult.PASS;
    }
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
		if ((!entity.isDead) && entity.getEntityWorld() !=null)
		{
			if(((entity instanceof EntityMinionBase)) && ((EntityMinionBase)entity).isOwner(player)){
				EntityMinionBase minion = (EntityMinionBase)entity;
				MinionType type = MinionType.BASIC;
				if(minion instanceof EntityMinionWorker){
					type = MinionType.WORKER;
				} else if(minion instanceof EntityMinionWarrior){
					type = MinionType.WARRIOR;
				}
				if(!entity.getEntityWorld().isRemote){
					ItemStack drop = ItemMinion.createMinion(type);
					minion.saveToItem(player, drop);
					minion.dropItem(drop, false, false);
					minion.setDead();
				}
				return true;
			}
			if(((entity instanceof EntityBombomb)) && ((EntityBombomb)entity).isOwner(player)){
				EntityBombomb bombomb = (EntityBombomb)entity;
				
				if(!entity.getEntityWorld().isRemote){
					ItemStack drop = new ItemStack(ModItems.bombomb);
					bombomb.saveToItem(player, drop);
					bombomb.entityDropItem(drop, 0.0f);
					bombomb.setDead();
				}
				return true;
			}
		}
        return false;
    }
	
}
