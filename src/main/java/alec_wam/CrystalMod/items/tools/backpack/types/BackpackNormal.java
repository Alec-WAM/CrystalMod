package alec_wam.CrystalMod.items.tools.backpack.types;

import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.tools.backpack.BackpackOpenSourceItem;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackBlockHandler;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackInventory;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackNormal.CrystalBackpackType;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpack;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BackpackNormal implements IBackpackInventory {

	public final ResourceLocation ID = CrystalMod.resourceL("normal");
	public final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/model/backpack/normal.png");
	public final Map<CrystalBackpackType, ResourceLocation> TEXTURES = Maps.newHashMap();
	
	public static final BackpackNormal INSTANCE = new BackpackNormal();
	
	public BackpackNormal(){
		for(CrystalBackpackType type : CrystalBackpackType.values()){
			TEXTURES.put(type, CrystalMod.resourceL("textures/model/backpack/normal_"+type.getUnlocalizedName()+".png"));
		}
		TEXTURES.put(CrystalBackpackType.NORMAL, TEXTURE);
	}
	
	@Override
	public ResourceLocation getID() {
		return ID;
	}
	
	@Override
	public ResourceLocation getTexture(ItemStack stack, int type) {
		return TEXTURES.get(CrystalBackpackType.byMetadata(stack.getMetadata()));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(Item item){
		for(int i = 0; i < CrystalBackpackType.values().length; i++){
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
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

	public static int getSizeOfInventory(ItemStack backpack){
		return 27+(9*backpack.getItemDamage());
	}
	
	@Override
	public NormalInventoryBackpack getInventory(EntityPlayer player, ItemStack backpack){
		return new NormalInventoryBackpack(player, backpack, Math.min(getSizeOfInventory(backpack), 72));
	}
	
	@Override
	public NormalInventoryBackpack getInventory(ItemStack backpack){
		return new NormalInventoryBackpack(backpack, Math.min(getSizeOfInventory(backpack), 72));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		ItemStack backpack = BackpackUtil.getPlayerBackpack(player);
		BackpackOpenSourceItem source = new BackpackOpenSourceItem(player, backpack);
		return new GuiBackpackNormal(getInventory(player, backpack), player.inventory, source);
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		return new ContainerBackpackNormal(getInventory(player, BackpackUtil.getPlayerBackpack(player)), player.inventory);
	}
	
	@Override
	public boolean handleItemPickup(EntityItemPickupEvent event, EntityPlayer player, ItemStack backpack){
		NormalInventoryBackpack inventory = getInventory(player, backpack);
		InventoryBackpackUpgrades upgradeInv = getUpgradeInventory(player, backpack);
		if(upgradeInv !=null && upgradeInv.hasUpgrade(BackpackUpgrade.HOPPER)){
			EntityItem itemEntity = event.getItem();
			if(ItemStackTools.isValid(itemEntity.getEntityItem()) && inventory !=null){
				ItemStack item = ItemStackTools.safeCopy(itemEntity.getEntityItem());
				
				ItemStack filterStack = upgradeInv.getUpgradeStack(InventoryBackpackUpgrades.SLOT_FILTER_HOPPER);
				if(ItemStackTools.isValid(filterStack) && filterStack.getItem() instanceof ItemPipeFilter){
					if(!ItemUtil.passesFilter(item, filterStack)){
						return false;
					}
				}
				
				ItemStack filterStack2 = upgradeInv.getUpgradeStack(InventoryBackpackUpgrades.SLOT_FILTER_VOID);
				if(ItemStackTools.isValid(filterStack2) && filterStack2.getItem() instanceof ItemPipeFilter){
					if(ItemUtil.passesFilter(item, filterStack2)){
						itemEntity.setDead();
						event.setResult(Result.ALLOW);
						return false;
					}
				}
				
				int insert = 0;
				
				fill : for(int i = 0; i < inventory.getSize(); i++){
					ItemStack invStack = inventory.getStackInSlot(i);
					if(ItemStackTools.isValid(invStack)){
						if(ItemUtil.canCombine(invStack, item)){
							final int freeSpace = Math.min(inventory.getInventoryStackLimit(), invStack.getMaxStackSize()) - ItemStackTools.getStackSize(invStack);
							if(freeSpace > 0){
								int add = Math.min(freeSpace, ItemStackTools.getStackSize(item));
								if(add > 0){
									ItemStackTools.incStackSize(invStack, add);
									ItemStackTools.incStackSize(item, -add);
									insert+=add;
									if(ItemStackTools.isEmpty(item)){
										break fill;
									}
								}
							}
						}
					} else {
						final int freeSpace = Math.min(inventory.getInventoryStackLimit(), item.getMaxStackSize()) - ItemStackTools.getStackSize(item);
						if(freeSpace > 0){
							int add = Math.min(freeSpace, ItemStackTools.getStackSize(item));
							if(add > 0){
								inventory.setInventorySlotContents(i, ItemUtil.copy(item, add));;
								ItemStackTools.incStackSize(item, -add);
								insert+=add;
								if(ItemStackTools.isEmpty(item)){
									break fill;
								}
							}
						}
					}
				}
				if(insert > 0){
					itemEntity.setEntityItemStack(ItemStackTools.incStackSize(itemEntity.getEntityItem(), -insert));
					inventory.markDirty();
					inventory.guiSave(player);
					ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
					if(exPlayer !=null && exPlayer.getInventory() !=null){
						exPlayer.getInventory().setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
					}
					event.setResult(Result.ALLOW);
					if(!player.world.isRemote){
						NBTTagCompound data = new NBTTagCompound();
						data.setInteger("CollectedID", itemEntity.getEntityId());
						CrystalModNetwork.sendToAllAround(new PacketEntityMessage(player, "#PickupEffects#", data), player);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getUpgradeAmount(ItemStack stack) {
		if(stack.getMetadata() > 0){
			return Math.min(stack.getMetadata(), 5);
		}
		return 0;
	}
	
	//BLOCK HANDLING
	@Override
	public boolean createBackpackBlock(World world, BlockPos pos, EntityPlayer player, ItemStack stack){
		world.setBlockState(pos, ModBlocks.backpackNormal.getDefaultState(), 3);
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityBackpackNormal){
			TileEntityBackpackNormal backpack = (TileEntityBackpackNormal)tile;
			backpack.setFacing(player.getHorizontalFacing().getOpposite().getHorizontalIndex());
			backpack.loadFromStack(stack);
			SoundType type = SoundType.CLOTH;
			world.playSound(null, pos, type.getPlaceSound(), SoundCategory.BLOCKS, 0.6f, 0.8f);
			BlockUtil.markBlockForUpdate(world, pos);
		}
		return true;
	}
	
	private final BlockHandlerNormal blockhandler = new BlockHandlerNormal();
	
	public static class BlockHandlerNormal implements IBackpackBlockHandler {

		@Override
		public TileEntityBackpack createTile(World world) {
			return new TileEntityBackpackNormal();
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
