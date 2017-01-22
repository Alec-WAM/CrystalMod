package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import java.util.List;

import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWirelessChestMinecart extends Item implements ICustomModel
{
    private static final IBehaviorDispenseItem CRYSTAL_MINECART_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem()
    {
        private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
            World world = source.getWorld();
            double d0 = source.getX() + (double)enumfacing.getFrontOffsetX() * 1.125D;
            double d1 = Math.floor(source.getY()) + (double)enumfacing.getFrontOffsetY();
            double d2 = source.getZ() + (double)enumfacing.getFrontOffsetZ() * 1.125D;
            BlockPos blockpos = source.getBlockPos().offset(enumfacing);
            IBlockState iblockstate = world.getBlockState(blockpos);
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)iblockstate.getValue(((BlockRailBase)iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double d3;

            if (BlockRailBase.isRailBlock(iblockstate))
            {
                if (blockrailbase$enumraildirection.isAscending())
                {
                    d3 = 0.6D;
                }
                else
                {
                    d3 = 0.1D;
                }
            }
            else
            {
                if (iblockstate.getMaterial() != Material.AIR || !BlockRailBase.isRailBlock(world.getBlockState(blockpos.down())))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                IBlockState iblockstate1 = world.getBlockState(blockpos.down());
                BlockRailBase.EnumRailDirection blockrailbase$enumraildirection1 = iblockstate1.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)iblockstate1.getValue(((BlockRailBase)iblockstate1.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;

                if (enumfacing != EnumFacing.DOWN && blockrailbase$enumraildirection1.isAscending())
                {
                    d3 = -0.4D;
                }
                else
                {
                    d3 = -0.9D;
                }
            }
            
            EntityWirelessChestMinecart entityminecart = new EntityWirelessChestMinecart(world, d0, d1 + d3, d2);
            entityminecart.setCode(ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0));
            String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
            if(UUIDUtils.isUUID(owner))entityminecart.setOwner(UUIDUtils.fromString(owner));
            
            if (stack.hasDisplayName())
            {
                entityminecart.setCustomNameTag(stack.getDisplayName());
            }

            world.spawnEntity(entityminecart);
            stack.splitStack(1);
            return stack;
        }
        /**
         * Play the dispense sound from the specified block.
         */
        protected void playDispenseSound(IBlockSource source)
        {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };

    public ItemWirelessChestMinecart()
    {
    	this.setHasSubtypes(true);
		this.setMaxDamage(0);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.TRANSPORTATION);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, CRYSTAL_MINECART_DISPENSER_BEHAVIOR);
        ModItems.registerItem(this, "minecart_wirelesschest");
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel() {
    	ModItems.initBasicModel(this);
    	ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemWirelessMinecartRender());
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
    	for(EnumDyeColor dye : EnumDyeColor.values()){
    		ItemStack stack = new ItemStack(this, 1, 0);
    		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, WirelessChestHelper.getDefaultCode(dye));
    		subItems.add(stack);
	    }
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	int code = ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, WirelessChestHelper.getDefaultCode(EnumDyeColor.WHITE));
    	
    	String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
		if(UUIDUtils.isUUID(owner)){
			String username = ProfileUtil.getUsername(UUIDUtils.fromString(owner));
			tooltip.add("Owner: " + username);
		}
		String color1 = ItemUtil.getDyeName(WirelessChestHelper.getDye1(code));
		String color2 = ItemUtil.getDyeName(WirelessChestHelper.getDye2(code));
		String color3 = ItemUtil.getDyeName(WirelessChestHelper.getDye3(code));
		tooltip.add("Code: " + color1 + " " + color2 + " " + color3);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (!BlockRailBase.isRailBlock(iblockstate))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            if (!worldIn.isRemote)
            {
                BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)iblockstate.getValue(((BlockRailBase)iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                double d0 = 0.0D;

                if (blockrailbase$enumraildirection.isAscending())
                {
                    d0 = 0.5D;
                }
                
                EntityWirelessChestMinecart entityminecart = new EntityWirelessChestMinecart(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.0625D + d0, (double)pos.getZ() + 0.5D);
                entityminecart.setCode(ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0));
                String owner = ItemNBTHelper.getString(stack, WirelessChestHelper.NBT_OWNER, "");
                if(UUIDUtils.isUUID(owner))entityminecart.setOwner(UUIDUtils.fromString(owner));
                
                if (stack.hasDisplayName())
                {
                    entityminecart.setCustomNameTag(stack.getDisplayName());
                }

                worldIn.spawnEntity(entityminecart);
            }

            ItemStackTools.incStackSize(stack, -1);
            return EnumActionResult.SUCCESS;
        }
    }
}