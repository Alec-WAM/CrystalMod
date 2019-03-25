package alec_wam.CrystalMod.items.tools.backpack.types;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.IBackpackBlockHandler;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpack;
import alec_wam.CrystalMod.items.tools.backpack.block.TileEntityBackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackEnderBuffer;
import alec_wam.CrystalMod.items.tools.backpack.gui.ContainerBackpackWirelessChest;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackEnderBuffer;
import alec_wam.CrystalMod.items.tools.backpack.gui.GuiBackpackWirelessChest;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackNormal.BlockHandlerNormal;
import alec_wam.CrystalMod.tiles.chest.wireless.RenderTileWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.TileWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.TileEntityEnderBuffer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BackpackWireless implements IBackpack {

	public final ResourceLocation ID = CrystalMod.resourceL("wireless");
	public final ResourceLocation TEXTURE = CrystalMod.resourceL("textures/model/backpack/wireless.png");
	
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
	
	public static enum ConnectionType {
		CHEST, BUFFER;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapabilityProvider(stack);
	}
	
	@Override
	public void update(ItemStack stack, World world, Entity entity,	int itemSlot, boolean isSelected) {}

	@Override
	public EnumActionResult itemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileWirelessChest){
			TileWirelessChest chest = (TileWirelessChest)tile;
			ConnectionType type = ConnectionType.CHEST;
			setConnectionType(type, stack);
			ItemNBTHelper.setInteger(stack, "Code", chest.code);
			return EnumActionResult.SUCCESS;
		}
		else if(tile !=null && tile instanceof TileEntityEnderBuffer){
			TileEntityEnderBuffer buffer = (TileEntityEnderBuffer)tile;
			ConnectionType type = ConnectionType.BUFFER;
			setConnectionType(type, stack);
			ItemNBTHelper.setInteger(stack, "Code", buffer.code);
			return EnumActionResult.SUCCESS;
		}
		if(state.getBlock() == Blocks.CAULDRON){
			//Remove any links
			int w = state.getValue(BlockCauldron.LEVEL);
			if(w > 0){
				Blocks.CAULDRON.setWaterLevel(world, pos, state, w - 1);
				setConnectionType(null, stack);
				ItemNBTHelper.setInteger(stack, "Code", -1);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> rightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return BackpackUtil.handleBackpackOpening(stack, world, player, hand, false);
	}

	public InventoryBackpack getInventory(EntityPlayer player){
		return new InventoryBackpack(player, BackpackUtil.getPlayerBackpack(player), 0);
	}
	
	public static void setConnectionType(ConnectionType type, ItemStack stack){
		ItemNBTHelper.setInteger(stack, "Type", type == null ? -1 : type.ordinal());
	}
	
	public static ConnectionType getConnection(ItemStack stack){
		int index = ItemNBTHelper.getInteger(stack, "Type", -1);
		if(index < 0 || index >=ConnectionType.values().length)return null;
		return ConnectionType.values()[index];
	}
	
	public static WirelessInventory getLinkedInventory(World world, ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "Code")){
			int code = ItemNBTHelper.getInteger(stack, "Code", 0);
			if(code < 0) return null;
			WirelessInventory inv = null;
			if(BackpackUtil.getOwner(stack) !=null){
				inv = WirelessChestManager.get(world).getPrivate(BackpackUtil.getOwner(stack)).getInventory(code);
			}
			if(inv == null){
				inv = WirelessChestManager.get(world).getInventory(code);
			}
			return inv;
		}
		return null;
	}
	
	public static EnderBuffer getLinkedBuffer(World world, ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, "Code")){
			int code = ItemNBTHelper.getInteger(stack, "Code", 0);
			if(code < 0) return null;
			EnderBuffer buffer = null;
			if(BackpackUtil.getOwner(stack) !=null){
				buffer = EnderBufferManager.get(world).getPrivate(BackpackUtil.getOwner(stack)).getBuffer(code);
			}
			if(buffer == null){
				buffer = EnderBufferManager.get(world).getBuffer(code);
			}
			return buffer;
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world) {
		InventoryBackpack inv = getInventory(player);
		ConnectionType type = getConnection(inv.getBackpack());
		if(type !=null && type == ConnectionType.CHEST){
			WirelessInventory wInv = getLinkedInventory(world, inv.getBackpack());
			if(wInv !=null){
				return new GuiBackpackWirelessChest(inv, wInv);
			}
		}
		if(type !=null && type == ConnectionType.BUFFER){
			EnderBuffer buffer = getLinkedBuffer(world, inv.getBackpack());
			if(buffer !=null){
				return new GuiBackpackEnderBuffer(inv, buffer);
			}
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world) {
		InventoryBackpack inv = getInventory(player);
		ConnectionType type = getConnection(inv.getBackpack());
		if(type !=null && type == ConnectionType.CHEST){
			WirelessInventory wInv = getLinkedInventory(world, inv.getBackpack());
			if(wInv !=null){
				return new ContainerBackpackWirelessChest(inv, wInv);
			}
		}
		if(type !=null && type == ConnectionType.BUFFER){
			EnderBuffer buffer = getLinkedBuffer(world, inv.getBackpack());
			if(buffer !=null){
				return new ContainerBackpackEnderBuffer(inv, buffer);
			}
		}
		return null;
	}

	@Override
	public int getUpgradeAmount(ItemStack stack) {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderExtras(ItemStack stack){
		ConnectionType type = getConnection(stack);
		if(type !=null && type == ConnectionType.CHEST){
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-0.5, -0.85, -0.15);
	        RenderTileWirelessChest.renderButtons(ItemNBTHelper.getInteger(stack, "Code", 0), 0, 0);
	        GlStateManager.popMatrix();
		}
		if(type !=null && type == ConnectionType.BUFFER){
			int code = ItemNBTHelper.getInteger(stack, "Code", 0);
			EnumDyeColor color1 = WirelessChestHelper.getDye1(code);
			EnumDyeColor color2 = WirelessChestHelper.getDye2(code);
			EnumDyeColor color3 = WirelessChestHelper.getDye3(code);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.48, 0.44);
			GlStateManager.scale(1.6, 0.2, 1.35);
			RenderUtil.renderItem(new ItemStack(Blocks.WOOL, 1, color1.getMetadata()), TransformType.FIXED);
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.62, 0.44);
			GlStateManager.scale(1.6, 0.2, 1.35);
			RenderUtil.renderItem(new ItemStack(Blocks.WOOL, 1, color2.getMetadata()), TransformType.FIXED);
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.76, 0.44);
			GlStateManager.scale(1.6, 0.2, 1.35);
			RenderUtil.renderItem(new ItemStack(Blocks.WOOL, 1, color3.getMetadata()), TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	public class CapabilityProvider implements ICapabilityProvider {

		protected final ItemStack container;

	    private CapabilityProvider(ItemStack container) {
	      this.container = container;
	    }
		
	    public World getWorld(Side side){
	    	if(side == Side.CLIENT){
	    		return CrystalMod.proxy.getClientWorld();
	    	}
	    	if(side == Side.SERVER){
	    		return CrystalMod.proxy.getWorld(0);
	    	}
	    	return null;
	    }
	    
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			ConnectionType type = getConnection(container);
			if(type !=null){
				if(type == ConnectionType.BUFFER){
					World world = getWorld(FMLCommonHandler.instance().getSide());
					if(world !=null){		
						EnderBuffer buffer = getLinkedBuffer(world, container);
						//Dont allow locked backpacks to interact with machines
						if(buffer !=null && BackpackUtil.getOwner(container) == null){
							return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityEnergy.ENERGY || capability == CapabilityCrystalEnergy.CENERGY;
						}
					}
				}
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			ConnectionType type = getConnection(container);
			if(type !=null){
				if(type == ConnectionType.BUFFER){
					World world = getWorld(FMLCommonHandler.instance().getSide());
					if(world !=null){		
						EnderBuffer buffer = getLinkedBuffer(world, container);
						//Dont allow locked backpacks to interact with machines
						if(buffer !=null && BackpackUtil.getOwner(container) == null){
							if(capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY){
								return (T) new IFluidHandlerItem(){

									@Override
									public IFluidTankProperties[] getTankProperties() {
										return buffer.tank.getTankProperties();
									}

									@Override
									public int fill(FluidStack resource, boolean doFill) {
										return buffer.tank.fill(resource, doFill);
									}

									@Override
									public FluidStack drain(FluidStack resource, boolean doDrain) {
										return buffer.tank.drain(resource, doDrain);
									}

									@Override
									public FluidStack drain(int maxDrain, boolean doDrain) {
										return buffer.tank.drain(maxDrain, doDrain);
									}

									@Override
									public ItemStack getContainer() {
										return container;
									}
									
								};							
							}
							if(capability == CapabilityEnergy.ENERGY){
								return CapabilityEnergy.ENERGY.cast(buffer.rfStorage);
							}
							if(capability == CapabilityCrystalEnergy.CENERGY){
								return CapabilityCrystalEnergy.CENERGY.cast(buffer.cuStorage);
							}
						}
					}
				}
			}
			return null;
		}

	}

	@Override
	public IBackpackBlockHandler getBlockHandler() {
		return null;
	}
	
	
}
