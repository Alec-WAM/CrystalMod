package alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.tiles.pipes.ModelPipeBaked;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.attachments.gui.ContainerAttachmantImport;
import alec_wam.CrystalMod.tiles.pipes.attachments.gui.GuiAttachmantImport;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class AttachmentEStorageImport extends AttachmentData {

	@Override
	public String getID() {
		return "estorage.import";
	}
	
	public InventoryBasic filters = new InventoryBasic("filter", false, 1);
	public RedstoneMode rMode = RedstoneMode.ON;
	public AttachmentIOType ioType = AttachmentIOType.ITEM;
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getGui(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new GuiAttachmantImport(player, pipe, dir);
	}
	
	@Override
	public Object getContainer(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new ContainerAttachmantImport(player, pipe, dir);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(filters.getStackInSlot(0) !=null){
			NBTTagCompound filterNBT = new NBTTagCompound();
			filters.getStackInSlot(0).writeToNBT(filterNBT);
			nbt.setTag("Filter", filterNBT);
		}
		nbt.setInteger("RedstoneMode", rMode.ordinal());
		nbt.setInteger("IOType", ioType.ordinal());
	}
	
	@Override
	public void loadFromNBT(NBTTagCompound nbt){
		super.loadFromNBT(nbt);
		if(nbt.hasKey("Filter")){
			NBTTagCompound filterNBT = nbt.getCompoundTag("Filter");
			filters.setInventorySlotContents(0, ItemStackTools.loadFromNBT(filterNBT));
		}else{
			filters.setInventorySlotContents(0, ItemStackTools.getEmptyStack());
		}

        if(nbt.hasKey("RedstoneMode")){
        	rMode = RedstoneMode.values()[nbt.getInteger("RedstoneMode") % RedstoneMode.values().length];
        }else{
        	rMode = RedstoneMode.ON;
        }
        if(nbt.hasKey("IOType")){
        	ioType = AttachmentIOType.values()[nbt.getInteger("IOType") % AttachmentIOType.values().length];
        }else{
        	ioType = AttachmentIOType.ITEM;
        }
	}
	
	
	@Override
	public void update(TileEntityPipe pipe, EnumFacing face){
		if(pipe == null || !(pipe instanceof TileEntityPipeEStorage))return;
		TileEntityPipeEStorage epipe = (TileEntityPipeEStorage) pipe;
		if(!pipe.getWorld().isRemote && epipe.network !=null && epipe.network instanceof EStorageNetwork){
			EStorageNetwork net = (EStorageNetwork) epipe.network;
			EnumFacing oDir = face.getOpposite();
			BlockPos pos = epipe.getPos().offset(face);
			if(rMode.passes(epipe.getWorld(), epipe.getPos())){
				IItemHandler handler = ItemUtil.getExternalItemHandler(epipe.getWorld(), pos, oDir);
				if(handler !=null && (ioType == AttachmentIOType.ITEM || ioType == AttachmentIOType.BOTH)){
					for(int slot = 0; slot < handler.getSlots(); slot++){
						ItemStack stack = handler.getStackInSlot(slot);
						if(!ItemStackTools.isNullStack(stack) && ItemUtil.passesFilter(stack, getFilter())){
							int extractAmount = 4;
							ItemStack extractResult = handler.extractItem(slot, extractAmount, true);
							if(!ItemStackTools.isNullStack(extractResult) && ItemStackTools.isNullStack(net.getItemStorage().addItem(extractResult, true))){
								net.getItemStorage().addItem(extractResult, false);
								handler.extractItem(slot, extractAmount, false);
							}
						}
					}
				} 
				IFluidHandler fHandler = FluidUtil.getExternalFluidHandler(epipe.getWorld(), pos, oDir);
				if(fHandler !=null && (ioType == AttachmentIOType.FLUID || ioType == AttachmentIOType.BOTH)){
					FluidStack contained = fHandler.drain(Fluid.BUCKET_VOLUME, false);
					if(contained !=null && contained.amount > 0 && FluidUtil.passesFilter(contained, getFilter())){
						if(net.getFluidStorage().addFluid(contained, true) > 0){
							fHandler.drain(net.getFluidStorage().addFluid(contained, false), true);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addQuads(FaceBakery faceBakery, List<BakedQuad> list, EnumFacing dir){
		ModelRotation modelRot = ModelRotation.X0_Y0;
        switch (dir.ordinal()) {
            case 0: {
                modelRot = ModelRotation.X270_Y0;
                break;
            }
            case 1: {
                modelRot = ModelRotation.X90_Y0;
                break;
            }
            case 2: {
                modelRot = ModelRotation.X180_Y0;
                break;
            }
            case 3: {
                modelRot = ModelRotation.X0_Y0;
                break;
            }
            case 4: {
                modelRot = ModelRotation.X0_Y90;
                break;
            }
            case 5: {
                modelRot = ModelRotation.X0_Y270;
                break;
            }
        }
		final BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);
        final TextureAtlasSprite iron = ModelPipeBaked.getIronSprite();
        
        TextureAtlasSprite spriteLapis = RenderUtil.getSprite("crystalmod:blocks/pipe/attachment/import");
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 12.0f, 12.0f), new Vector3f(12.0f, 12.0f, 13.0f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 12.0f), new Vector3f(12.0f, 4.0f, 13.0f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 12.0f), new Vector3f(12.0f, 12.0f, 13.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 12.0f), new Vector3f(12.0f, 12.0f, 13.0f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 12.0f), new Vector3f(4.0f, 12.0f, 13.0f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(12.0f, 4.0f, 12.0f), new Vector3f(12.0f, 12.0f, 13.0f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        
		list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 13.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 3.0f, 14.5f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, spriteLapis, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(3.0f, 13.0f, 14.5f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(13.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 14.0f, 14.5f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 14.5f), new Vector3f(14.0f, 2.0f, 16.0f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 14.5f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 14.5f), new Vector3f(14.0f, 14.0f, 16.0f), face, spriteLapis, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 14.5f), new Vector3f(2.0f, 14.0f, 16.0f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(14.0f, 2.0f, 14.5f), new Vector3f(14.0f, 14.0f, 16.0f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
	}
	
	@Override
	public List<AxisAlignedBB> getBoxes(EnumFacing dir) {
		List<AxisAlignedBB> list = Lists.newArrayList();
		float pixel = 1.0f / 16f;
		float minLarge = 2f;
		float maxLarge = 14f;
		float minSmall = 3f;
		float maxSmall = 13f;
		float minCon = 4f;
		float maxCon = 12f;
		if(dir == EnumFacing.UP){
			AxisAlignedBB bbLargeUp = new AxisAlignedBB(pixel * minLarge,
					pixel * 14.5f, pixel * minLarge, pixel * maxLarge,
					pixel * 16.0f, pixel * maxLarge);
			list.add(bbLargeUp);
			AxisAlignedBB bbSmallUp = new AxisAlignedBB(pixel * minSmall,
					pixel * 13.0f, pixel * minSmall, pixel * maxSmall,
					pixel * 14.5f, pixel * maxSmall);
			list.add(bbSmallUp);
			AxisAlignedBB bbConUp = new AxisAlignedBB(pixel * minCon,
					pixel * 12.0f, pixel * minCon, pixel * maxCon,
					pixel * 13.0f, pixel * maxCon);
			list.add(bbConUp);
		}
		if(dir == EnumFacing.DOWN){
			AxisAlignedBB bbLargeDown = new AxisAlignedBB(pixel * minLarge,
					pixel * 0.0f, pixel * minLarge, pixel * maxLarge,
					pixel * 1.5f, pixel * maxLarge);
			list.add(bbLargeDown);
			AxisAlignedBB bbSmallDown = new AxisAlignedBB(pixel * minSmall,
					pixel * 1.5f, pixel * minSmall, pixel * maxSmall,
					pixel * 3f, pixel * maxSmall);
			list.add(bbSmallDown);
			AxisAlignedBB bbConDown = new AxisAlignedBB(pixel * minCon,
					pixel * 3f, pixel * minCon, pixel * maxCon,
					pixel * 4f, pixel * maxCon);
			list.add(bbConDown);
		}
		if(dir == EnumFacing.NORTH){
			AxisAlignedBB bbLarge = new AxisAlignedBB(
					pixel * minLarge, pixel * minLarge, pixel * 0.0f,
					pixel * maxLarge, pixel * maxLarge, pixel * 1.5f);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(
					pixel * minSmall, pixel * minSmall, pixel * 1.5f,
					pixel * maxSmall, pixel * maxSmall, pixel * 3f);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(
					pixel * minCon, pixel * minCon, pixel * 3f,
					pixel * maxCon, pixel * maxCon, pixel * 4f);
			list.add(bbCon);
		}
		if(dir == EnumFacing.SOUTH){
			AxisAlignedBB bbLarge = new AxisAlignedBB(
					pixel * minLarge, pixel * minLarge, pixel * 14.5f,
					pixel * maxLarge, pixel * maxLarge, pixel * 16f);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(
					pixel * minSmall, pixel * minSmall, pixel * 13f,
					pixel * maxSmall, pixel * maxSmall, pixel * 14.5f);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(
					pixel * minCon, pixel * minCon, pixel * 12f,
					pixel * maxCon, pixel * maxCon, pixel * 13f);
			list.add(bbCon);
		}
		if(dir == EnumFacing.WEST){
			AxisAlignedBB bbLarge = new AxisAlignedBB(pixel * 0.0f,
					pixel * minLarge, pixel * minLarge, pixel * 1.5f,
					pixel * maxLarge, pixel * maxLarge);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(pixel * 1.5f,
					pixel * minSmall, pixel * minSmall, pixel * 3.0f,
					pixel * maxSmall, pixel * maxSmall);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(pixel * 3.0f,
					pixel * minCon, pixel * minCon, pixel * 4.0f,
					pixel * maxCon, pixel * maxCon);
			list.add(bbCon);
		}
		if(dir == EnumFacing.EAST){
			AxisAlignedBB bbLarge = new AxisAlignedBB(pixel * 14.5f,
					pixel * minLarge, pixel * minLarge, pixel * 16.0f,
					pixel * maxLarge, pixel * maxLarge);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(pixel * 13.0f,
					pixel * minSmall, pixel * minSmall, pixel * 14.5f,
					pixel * maxSmall, pixel * maxSmall);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(pixel * 12.0f,
					pixel * minCon, pixel * minCon, pixel * 13.0f,
					pixel * maxCon, pixel * maxCon);
			list.add(bbCon);
		}
		
		return list;
	}


	public ItemStack getFilter() {
		return filters.getStackInSlot(0);
	}
	
	@Override
	public boolean isPipeValid(TileEntityPipe pipe, EnumFacing side, ItemStack stack){
		return pipe instanceof TileEntityPipeEStorage;
	}
}
