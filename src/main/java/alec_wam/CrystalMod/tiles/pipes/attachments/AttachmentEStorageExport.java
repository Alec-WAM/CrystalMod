package alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.vector.Vector3f;

import alec_wam.CrystalMod.tiles.BasicItemHandler;
import alec_wam.CrystalMod.tiles.IItemValidator;
import alec_wam.CrystalMod.tiles.pipes.ModelPipe;
import alec_wam.CrystalMod.tiles.pipes.ModelPipeBaked;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import alec_wam.CrystalMod.tiles.pipes.attachments.gui.ContainerAttachmentExport;
import alec_wam.CrystalMod.tiles.pipes.attachments.gui.GuiAttachmentExport;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

import com.google.common.collect.Lists;

public class AttachmentEStorageExport extends AttachmentData {

	public BasicItemHandler filters = new BasicItemHandler(10, new IItemValidator[0]);
	
	public RedstoneMode rMode = RedstoneMode.ON;
	
	@Override
	public String getID() {
		return "estorage.export";
	}

	@SideOnly(Side.CLIENT)
	public Object getGui(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new GuiAttachmentExport(player, pipe, dir);
	}
	
	public Object getContainer(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new ContainerAttachmentExport(player, pipe, dir);
	}
	
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < filters.getSlots(); i++) {
            if (filters.getStackInSlot(i) != null) {
                NBTTagCompound compoundTag = new NBTTagCompound();

                compoundTag.setInteger("Slot", i);

                filters.getStackInSlot(i).writeToNBT(compoundTag);

                tagList.appendTag(compoundTag);
            }
        }

        nbt.setTag("Inventory", tagList);
        nbt.setInteger("RedstoneMode", rMode.ordinal());
	}
	
	public void loadFromNBT(NBTTagCompound nbt){
		super.loadFromNBT(nbt);
    	NBTTagList tagList = nbt.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); i++) {
            int slot = tagList.getCompoundTagAt(i).getInteger("Slot");

            ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

            filters.insertItem(slot, stack, false);
        }
        if(nbt.hasKey("RedstoneMode")){
        	rMode = RedstoneMode.values()[nbt.getInteger("RedstoneMode") % RedstoneMode.values().length];
        }else{
        	rMode = RedstoneMode.ON;
        }
    }
	
	public void update(TileEntityPipe pipe, EnumFacing face){
		if(pipe == null || !(pipe instanceof TileEntityPipeEStorage))return;
		TileEntityPipeEStorage epipe = (TileEntityPipeEStorage) pipe;
		if(!pipe.getWorld().isRemote && epipe.network !=null && epipe.network instanceof EStorageNetwork){
			EStorageNetwork net = (EStorageNetwork) epipe.network;
			EnumFacing oDir = face.getOpposite();
			TileEntity tile = pipe.getWorld().getTileEntity(pipe.getPos().offset(face));
			if(tile !=null && rMode.passes(epipe.getWorld(), epipe.getPos())){
				for (int i = 0; i < filters.getSlots(); ++i) {
	                ItemStack slot = filters.getStackInSlot(i);

	                if (slot != null) {
	                	ItemStack cop = slot.copy();
	                	cop.stackSize = 1;
	                	ItemStack took = net.getItemStorage().removeItem(cop, true);
	                	if(took !=null){
	                		if(ItemUtil.doInsertItem(tile, slot, oDir, false) == cop.stackSize){
	                			ItemUtil.doInsertItem(tile, slot, oDir, true);
	                			net.getItemStorage().removeItem(cop, false);
	                		}
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
        
        TextureAtlasSprite spriteLapis = RenderUtil.getSprite("minecraft:blocks/iron_block");
        
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 14.0f, 12f), new Vector3f(14.0f, 14.0f, 13f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 12f), new Vector3f(14.0f, 2.0f, 13f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 12f), new Vector3f(14.0f, 14.0f, 13f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 12f), new Vector3f(14.0f, 14.0f, 13.0f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(2.0f, 2.0f, 12f), new Vector3f(2.0f, 14.0f, 13f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(14.0f, 2.0f, 12f), new Vector3f(14.0f, 14.0f, 13f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
	
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 13.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 3.0f, 14.5f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(3.0f, 3.0f, 13.0f), new Vector3f(3.0f, 13.0f, 14.5f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(13.0f, 3.0f, 13.0f), new Vector3f(13.0f, 13.0f, 14.5f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        
        
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 12.0f, 14.5f), new Vector3f(12.0f, 12.0f, 16.0f), face, iron, EnumFacing.UP, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 14.5f), new Vector3f(12.0f, 4.0f, 16.0f), face, iron, EnumFacing.DOWN, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 14.5f), new Vector3f(12.0f, 12.0f, 16.0f), face, iron, EnumFacing.NORTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 14.5f), new Vector3f(12.0f, 12.0f, 16.0f), face, spriteLapis, EnumFacing.SOUTH, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0f, 4.0f, 14.5f), new Vector3f(4.0f, 12.0f, 16.0f), face, iron, EnumFacing.WEST, modelRot, (BlockPartRotation)null, true, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(12.0f, 4.0f, 14.5f), new Vector3f(12.0f, 12.0f, 16.0f), face, iron, EnumFacing.EAST, modelRot, (BlockPartRotation)null, true, true));
        
		
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
					pixel * 12f, pixel * minLarge, pixel * maxLarge,
					pixel * 13f, pixel * maxLarge);
			list.add(bbLargeUp);
			AxisAlignedBB bbSmallUp = new AxisAlignedBB(pixel * minSmall,
					pixel * 13.0f, pixel * minSmall, pixel * maxSmall,
					pixel * 14.5f, pixel * maxSmall);
			list.add(bbSmallUp);
			AxisAlignedBB bbConUp = new AxisAlignedBB(pixel * minCon,
					pixel * 14.5f, pixel * minCon, pixel * maxCon,
					pixel * 16.0f, pixel * maxCon);
			list.add(bbConUp);
		}
		if(dir == EnumFacing.DOWN){
			AxisAlignedBB bbLargeDown = new AxisAlignedBB(pixel * minLarge,
					pixel * 3f, pixel * minLarge, pixel * maxLarge,
					pixel * 4f, pixel * maxLarge);
			list.add(bbLargeDown);
			AxisAlignedBB bbSmallDown = new AxisAlignedBB(pixel * minSmall,
					pixel * 1.5f, pixel * minSmall, pixel * maxSmall,
					pixel * 3f, pixel * maxSmall);
			list.add(bbSmallDown);
			AxisAlignedBB bbConDown = new AxisAlignedBB(pixel * minCon,
					pixel * 0f, pixel * minCon, pixel * maxCon,
					pixel * 1.5f, pixel * maxCon);
			list.add(bbConDown);
		}
		if(dir == EnumFacing.NORTH){
			AxisAlignedBB bbLarge = new AxisAlignedBB(
					pixel * minLarge, pixel * minLarge, pixel * 3f,
					pixel * maxLarge, pixel * maxLarge, pixel * 4f);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(
					pixel * minSmall, pixel * minSmall, pixel * 1.5f,
					pixel * maxSmall, pixel * maxSmall, pixel * 3f);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(
					pixel * minCon, pixel * minCon, pixel * 0f,
					pixel * maxCon, pixel * maxCon, pixel * 1.5f);
			list.add(bbCon);
		}
		if(dir == EnumFacing.SOUTH){
			AxisAlignedBB bbLarge = new AxisAlignedBB(
					pixel * minLarge, pixel * minLarge, pixel * 12f,
					pixel * maxLarge, pixel * maxLarge, pixel * 13f);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(
					pixel * minSmall, pixel * minSmall, pixel * 13f,
					pixel * maxSmall, pixel * maxSmall, pixel * 14.5f);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(
					pixel * minCon, pixel * minCon, pixel * 14.5f,
					pixel * maxCon, pixel * maxCon, pixel * 16f);
			list.add(bbCon);
		}
		if(dir == EnumFacing.WEST){
			AxisAlignedBB bbLarge = new AxisAlignedBB(pixel * 3f,
					pixel * minLarge, pixel * minLarge, pixel * 4f,
					pixel * maxLarge, pixel * maxLarge);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(pixel * 1.5f,
					pixel * minSmall, pixel * minSmall, pixel * 3.0f,
					pixel * maxSmall, pixel * maxSmall);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(pixel * 0f,
					pixel * minCon, pixel * minCon, pixel * 1.5f,
					pixel * maxCon, pixel * maxCon);
			list.add(bbCon);
		}
		if(dir == EnumFacing.EAST){
			AxisAlignedBB bbLarge = new AxisAlignedBB(pixel * 12f,
					pixel * minLarge, pixel * minLarge, pixel * 13f,
					pixel * maxLarge, pixel * maxLarge);
			list.add(bbLarge);
			AxisAlignedBB bbSmall = new AxisAlignedBB(pixel * 13.0f,
					pixel * minSmall, pixel * minSmall, pixel * 14.5f,
					pixel * maxSmall, pixel * maxSmall);
			list.add(bbSmall);
			AxisAlignedBB bbCon = new AxisAlignedBB(pixel * 14.5f,
					pixel * minCon, pixel * minCon, pixel * 16f,
					pixel * maxCon, pixel * maxCon);
			list.add(bbCon);
		}
		
		return list;
	}
	
	public boolean isPipeValid(TileEntityPipe pipe, EnumFacing side, ItemStack stack){
		return pipe instanceof TileEntityPipeEStorage;
	}
}
