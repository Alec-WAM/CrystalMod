package alec_wam.CrystalMod.items.tools.backpack.types;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackBlockHandler;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpack;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackCrafting;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackCrafting;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BackpackCrafting implements IBackpack {

	public final ResourceLocation ID = CrystalMod.resourceL("crafting");
	public final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/model/backpack/crafting.png");
	
	public static final BackpackCrafting INSTANCE = new BackpackCrafting();
	
	@Override
	public ResourceLocation getID() {
		return ID;
	}
	
	@Override
	public ResourceLocation getTexture(ItemStack backpack, int type) {
		return TEXTURE;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(Item item){
		ModItems.initBasicModel(item);
	}
	
	@Override
	public void update(ItemStack stack, World world, Entity entity,	int itemSlot, boolean isSelected) {}

	@Override
	public EnumActionResult itemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> rightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return BackpackUtil.handleBackpackOpening(stack, world, player, hand, false);
	}

	public InventoryBackpack getInventory(EntityPlayer player){
		return new InventoryBackpack(player, BackpackUtil.getPlayerBackpack(player), 9);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		return new GuiBackpackCrafting(getInventory(player), player.inventory);
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		return new ContainerBackpackCrafting(getInventory(player), player.inventory);
	}

	@Override
	public int getUpgradeAmount(ItemStack stack) {
		return 0;
	}
	
	@Override
	public boolean createBackpackBlock(World world, BlockPos pos, EntityPlayer player, ItemStack stack){
		world.setBlockState(pos, ModBlocks.backpackCrafting.getDefaultState(), 3);
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityBackpackCrafting){
			TileEntityBackpackCrafting backpack = (TileEntityBackpackCrafting)tile;
			backpack.setFacing(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
			backpack.loadFromStack(stack);
			SoundType type = SoundType.WOOD;
			world.playSound(null, pos, type.getPlaceSound(), SoundCategory.BLOCKS, 0.6f, 0.8f);
			BlockUtil.markBlockForUpdate(world, pos);
		}
		return true;
	}
	
	private final BlockHandlerCrafting blockhandler = new BlockHandlerCrafting();
	
	public static class BlockHandlerCrafting implements IBackpackBlockHandler {

		@Override
		public TileEntityBackpack createTile(World world) {
			return new TileEntityBackpackCrafting();
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
			if(!player.isSneaking()){
				if(!world.isRemote){
					world.playSound(null, pos, ModSounds.backpack_zipper, SoundCategory.BLOCKS, 0.8F, 1F);
					player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_BACKPACK_BLOCK, world, pos.getX(), pos.getY(), pos.getZ());
				}
				return true;
			}
			return false;
		}
		
	}

	@Override
	public IBackpackBlockHandler getBlockHandler() {
		return blockhandler;
	}

}
