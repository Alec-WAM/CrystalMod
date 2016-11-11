package alec_wam.CrystalMod.entities.minions;

import java.util.List;
import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

public class ItemMinion extends Item implements ICustomModel {
	
	public ItemMinion(){
		super();
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "minion");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRender(getRegistryName().getResourcePath(), new ItemRenderMinion());
	}
	
	public static ItemStack createMinion(MinionType type){
   	 	ItemStack stack = new ItemStack(ModItems.minion);
   	 	ItemNBTHelper.setString(stack, "MinionType", type.name());
   	 	return stack;
    }
	
	public static MinionType getType(ItemStack stack){
		MinionType type = MinionType.BASIC;
        if(ItemNBTHelper.verifyExistance(stack, "MinionType")){
        	String stype = ItemNBTHelper.getString(stack, "MinionType", "basic");
        	s : for(MinionType m : MinionType.values()){
        		if(m.name().equalsIgnoreCase(stype)){
        			type = m;
        			break s;
        		}
        	}
        }
        return type;
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(MinionType type : MinionType.values())
		list.add(createMinion(type));
	}
	
	public String getUnlocalizedName(ItemStack stack){
		return super.getUnlocalizedName(stack)+"."+getType(stack).name().toLowerCase();
	}
	
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
			/*UUID uuid = UUIDUtils.fromString(nbt.getString("OwnerUUID"));
			if(uuid !=null){
				tooltip.add(""+ProfileUtil.getUsername(uuid));
			}*/
		}
    }
	
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
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

            MinionType type = getType(stack);
            EntityMinionBase minion = null;
            try
            {
            	minion = type.getEntityClass().getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldIn});
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            if (minion != null && type !=MinionType.BASIC)
            {
            	minion.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);
            	minion.setTamed(true);
            	minion.setOwnerId(EntityPlayer.getUUID(playerIn.getGameProfile()));
            	minion.loadFromItem(playerIn, stack);
            	worldIn.spawnEntityInWorld(minion);
                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
            }

            return EnumActionResult.SUCCESS;
        }
    }
	
}
