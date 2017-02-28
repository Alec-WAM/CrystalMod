package alec_wam.CrystalMod.tiles.machine.elevator;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.pipes.wireless.BlockWirelessPipeWrapper;
import alec_wam.CrystalMod.tiles.pipes.wireless.TileEntityPipeWrapper;
import alec_wam.CrystalMod.tiles.portal.TileTelePortal;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMiscCard extends Item implements ICustomModel {

	public ItemMiscCard(){
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(16);
		setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "misccard");
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		CardType type = CardType.byMetadata(stack.getMetadata());
		if(type == CardType.ELEVATOR && ItemNBTHelper.verifyExistance(stack, "elevatorx")){
			int x = ItemNBTHelper.getInteger(stack, "elevatorx", 0);
			int y = ItemNBTHelper.getInteger(stack, "elevatory", -1);
			int z = ItemNBTHelper.getInteger(stack, "elevatorz", 0);
			int dim = ItemNBTHelper.getInteger(stack, "elevatordim", 0);
			String displayName = new ItemStack(ModBlocks.elevator).getDisplayName();
			tooltip.add("Bound to "+displayName+" at "+x+" "+y+" "+z+" in dimension "+dim);
		}
		if(type == CardType.TELEPORT_PORTAL && ItemNBTHelper.verifyExistance(stack, "portalx")){
			int x = ItemNBTHelper.getInteger(stack, "portalx", 0);
			int y = ItemNBTHelper.getInteger(stack, "portaly", -1);
			int z = ItemNBTHelper.getInteger(stack, "portalz", 0);
			int dim = ItemNBTHelper.getInteger(stack, "portaldim", 0);
			tooltip.add("Bound to a Portal at "+x+" "+y+" "+z+" in dimension "+dim);
		}
		if(type == CardType.EPORTAL && ItemNBTHelper.verifyExistance(stack, BlockWirelessPipeWrapper.NBT_CON_X)){
			int x = ItemNBTHelper.getInteger(stack, BlockWirelessPipeWrapper.NBT_CON_X, 0);
			int y = ItemNBTHelper.getInteger(stack, BlockWirelessPipeWrapper.NBT_CON_Y, -1);
			int z = ItemNBTHelper.getInteger(stack, BlockWirelessPipeWrapper.NBT_CON_Z, 0);
			int dim = ItemNBTHelper.getInteger(stack, BlockWirelessPipeWrapper.NBT_CON_D, 0);
			String displayName = new ItemStack(ModBlocks.wirelessPipe).getDisplayName();
			tooltip.add("Bound to "+displayName+" at "+x+" "+y+" "+z+" in dimension "+dim);
		}
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		ItemStack stack = player.getHeldItem(hand);
		TileEntity tile = world.getTileEntity(pos);
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		boolean gotCoords = false;
		CardType type = CardType.byMetadata(stack.getMetadata());
		if(type !=null && type == CardType.ELEVATOR){
			if(tile !=null && tile instanceof TileEntityElevator){
				nbt.setInteger("elevatorx", pos.getX());
				nbt.setInteger("elevatory", pos.getY());
				nbt.setInteger("elevatorz", pos.getZ());
				nbt.setInteger("elevatordim", world.provider.getDimension());
				if(!world.isRemote)ChatUtil.sendChat(player, "Card set to "+pos.getX()+" "+pos.getY()+" "+pos.getZ());
				gotCoords = true;
			}
		}
		if(type !=null && type == CardType.EPORTAL){
			if(tile !=null && tile instanceof TileEntityPipeWrapper){
				nbt.setInteger(BlockWirelessPipeWrapper.NBT_CON_X, pos.getX());
				nbt.setInteger(BlockWirelessPipeWrapper.NBT_CON_Y, pos.getY());
				nbt.setInteger(BlockWirelessPipeWrapper.NBT_CON_Z, pos.getZ());
				nbt.setInteger(BlockWirelessPipeWrapper.NBT_CON_D, world.provider.getDimension());
				if(!world.isRemote)ChatUtil.sendChat(player, "Card set to "+pos.getX()+" "+pos.getY()+" "+pos.getZ());
				gotCoords = true;
			}
		}
		if(type !=null && type == CardType.TELEPORT_PORTAL){
			if(tile !=null && tile instanceof TileTelePortal){
				nbt.setTag("PortalPos", NBTUtil.createPosTag(pos));
				nbt.setInteger("PortalDim", world.provider.getDimension());
				if(!world.isRemote)ChatUtil.sendChat(player, "Card set to "+pos.getX()+" "+pos.getY()+" "+pos.getZ());
				gotCoords = true;
			}
		}
		if(!gotCoords){
			boolean cleared = false;
			if(type !=null && type == CardType.ELEVATOR){
				nbt.removeTag("elevatorx");
				nbt.removeTag("elevatory");
				nbt.removeTag("elevatorz");
				nbt.removeTag("elevatordim");
				cleared = true;
			}
			if(type !=null && type == CardType.TELEPORT_PORTAL){
				nbt.removeTag("PortalPos");
				nbt.removeTag("PortalDim");
				cleared = true;
			}
			if(type !=null && type == CardType.EPORTAL){
				nbt.removeTag(BlockWirelessPipeWrapper.NBT_CON_X);
				nbt.removeTag(BlockWirelessPipeWrapper.NBT_CON_Y);
				nbt.removeTag(BlockWirelessPipeWrapper.NBT_CON_Z);
				nbt.removeTag(BlockWirelessPipeWrapper.NBT_CON_D);
				cleared = true;
			}
			if(!world.isRemote && cleared)ChatUtil.sendChat(player, "Card Cleared");
		}
		stack.setTagCompound(nbt);
		return EnumActionResult.SUCCESS;
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        for(CardType type : CardType.values()){
        	 ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), new ModelResourceLocation(getRegistryName(), type.getUnlocalizedName()));
        }
    }
	
	public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getUnlocalizedName() + "." + CardType.byMetadata(i).getUnlocalizedName();
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < CardType.values().length; ++i)
        {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }
	
	public static enum CardType implements IStringSerializable
    {
        EPORTAL(0, "eportal"),
        CUBE(1, "pcube"),
        ELEVATOR(2, "elevator"),
        TELEPORT_PORTAL(3, "tportal");

        private static final CardType[] METADATA_LOOKUP = new CardType[values().length];
        private final int metadata;
        private final String unlocalizedName;

        private CardType(int dmg, String name)
        {
            this.metadata = dmg;
            this.unlocalizedName = name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        public String toString()
        {
            return this.unlocalizedName;
        }

        public static CardType byMetadata(int metadata)
        {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
            {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (CardType type : values())
            {
                METADATA_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
	
}
