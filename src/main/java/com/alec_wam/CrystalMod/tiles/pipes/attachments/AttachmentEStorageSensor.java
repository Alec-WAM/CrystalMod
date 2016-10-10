package com.alec_wam.CrystalMod.tiles.pipes.attachments;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.vector.Vector3f;

import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.network.CrystalModNetwork;
import com.alec_wam.CrystalMod.network.packets.PacketTileMessage;
import com.alec_wam.CrystalMod.tiles.pipes.ModelPipe;
import com.alec_wam.CrystalMod.tiles.pipes.ModelPipeBaked;
import com.alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.AttachmentUtil.AttachmentData;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.gui.ContainerAttachmentSensor;
import com.alec_wam.CrystalMod.tiles.pipes.attachments.gui.GuiAttachmentSensor;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;
import com.alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.client.RenderUtil;
import com.google.common.collect.Lists;

public class AttachmentEStorageSensor extends AttachmentData {

	public static enum CompareType{
		NOT("!=") {
			public boolean passes(int amount, int filter){
				return amount !=filter;
			}
		}, 
		EQUALS("=") {
			public boolean passes(int amount, int filter){
				return amount == filter;
			}
		}, 
		LESS("<") {
			public boolean passes(int amount, int filter){
				return amount < filter;
			}
		}, 
		GREATER(">") {
			public boolean passes(int amount, int filter){
				return amount > filter;
			}
		}, 
		LESS_EQUAL("<=") {
			public boolean passes(int amount, int filter){
				return amount <= filter;
			}
		}, 
		GREATER_EQUAL(">=") {
			public boolean passes(int amount, int filter){
				return amount >= filter;
			}
		};
		
		final String visual;
		CompareType(String visual){
			this.visual = visual;
		}
		
		public String getStringValue(){
			return visual;
		}
		
		public abstract boolean passes(int amount, int filter);
	}
	
	public ItemStack filterStack;
	public int filterAmount = 0;
	public CompareType compare = CompareType.GREATER;
	public int lastOutRedstonePower;
	public int outRedstonePower;
	public int redstonePower = 15;
	public boolean strongPower = false, useOre = false;
	
	@Override
	public String getID() {
		return "estorage.sensor";
	}

	@SideOnly(Side.CLIENT)
	public Object getGui(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new GuiAttachmentSensor(player, pipe, dir);
	}
	
	public Object getContainer(EntityPlayer player, TileEntityPipeEStorage pipe, EnumFacing dir){
		return new ContainerAttachmentSensor(player, pipe, dir);
	}
	
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(filterStack !=null){
			NBTTagCompound nbtStack = filterStack.writeToNBT(new NBTTagCompound());
			nbt.setTag("FilterStack", nbtStack);
		}
		nbt.setInteger("FilterAmt", filterAmount);
		nbt.setInteger("Compare", compare.ordinal());
		nbt.setInteger("Redstone", redstonePower);
		nbt.setBoolean("Strong", strongPower);
		nbt.setBoolean("Ore", useOre);
	}
	
	public void loadFromNBT(NBTTagCompound nbt){
		super.loadFromNBT(nbt);
		if(nbt.hasKey("FilterStack")){
			filterStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("FilterStack"));
		}
		filterAmount = nbt.getInteger("FilterAmt");
		compare = CompareType.values()[nbt.getInteger("Compare") % CompareType.values().length];
		redstonePower = nbt.getInteger("Redstone");
		strongPower = nbt.getBoolean("Strong");
		useOre = nbt.getBoolean("Ore");
    }
	
	@Override
	public void handleMessage(TileEntityPipe pipe, EnumFacing dir, NBTTagCompound nbt){
		if(nbt.hasKey("FilterStack")){
			filterStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("FilterStack"));
		}
		if(nbt.hasKey("FilterAmt")){
			this.filterAmount = nbt.getInteger("FilterAmt");
		}
		if(nbt.hasKey("Compare")){
			this.compare = CompareType.values()[nbt.getInteger("Compare") % CompareType.values().length];
		}
		if(nbt.hasKey("Redstone")){
			this.redstonePower = nbt.getInteger("Redstone");
		}
		if(nbt.hasKey("Strong")){
			this.strongPower = nbt.getBoolean("Strong");
		}
		if(nbt.hasKey("Ore")){
			this.useOre = nbt.getBoolean("Ore");
		}
		
		if(nbt.hasKey("RedstoneOutput")){
			this.outRedstonePower = nbt.getInteger("RedstoneOutput");
			if(this.lastOutRedstonePower !=this.outRedstonePower){
				this.lastOutRedstonePower = this.outRedstonePower;
				notifyBlocks(pipe.getWorld(), pipe.getPos(), dir);
			}
		}
	}
	
	public void update(TileEntityPipe pipe, EnumFacing face){
		super.update(pipe, face);
		if(pipe == null || !(pipe instanceof TileEntityPipeEStorage)){
			this.outRedstonePower = 0;
			if(this.lastOutRedstonePower !=this.outRedstonePower){
				this.lastOutRedstonePower = this.outRedstonePower;
				notifyBlocks(pipe.getWorld(), pipe.getPos(), face);
			}
			return;
		}
		TileEntityPipeEStorage epipe = (TileEntityPipeEStorage) pipe;
		
		if(!pipe.getWorld().isRemote){
			outRedstonePower = 0;
			if(epipe.network !=null && epipe.network instanceof EStorageNetwork){
				EStorageNetwork net = (EStorageNetwork) epipe.network;
				if(filterStack !=null){
					int amount = 0;
					if(useOre){
						Iterator<ItemStackData> iData = net.items.iterator();
						while(iData.hasNext()) {
							ItemStackData data = iData.next();
							if (data !=null && data.stack != null && ItemUtil.stackMatchUseOre(filterStack, data.stack)) {
								amount+=data.getAmount();
							}
						}
					} else {
						ItemStackData data = net.getData(filterStack);
						if(data !=null){
							amount = data.getAmount();
						}
					}
					if(compare !=null){
						if(compare.passes(amount, filterAmount)){
							outRedstonePower = redstonePower;
						}
					}
				}
			}

			if(this.lastOutRedstonePower !=this.outRedstonePower){
				this.lastOutRedstonePower = this.outRedstonePower;
				notifyBlocks(pipe.getWorld(), pipe.getPos(), face);
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Dir", face.getIndex());
				nbt.setInteger("RedstoneOutput", outRedstonePower);
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(pipe.getPos(), "Attachment", nbt), pipe);
			}
		}
	}
	
	public void notifyBlocks(World world, BlockPos pos, EnumFacing dir){
		BlockPos bc2 = pos.offset(dir);
		if (world.isBlockLoaded(bc2)) {
			world.notifyBlockOfStateChange(bc2, ModBlocks.crystalPipe);
			IBlockState bs = world.getBlockState(bc2);
			if (bs.isBlockNormalCube()) {
				for (EnumFacing dir2 : EnumFacing.VALUES) {
					BlockPos bc3 = bc2.offset(dir2);
					if (!bc3.equals(pos) && world.isBlockLoaded(bc3)) {
						world.notifyBlockOfStateChange(bc3, ModBlocks.crystalPipe);
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
        
        TextureAtlasSprite spriteLapis = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/redstone_block");
        if(spriteLapis == null){
        	spriteLapis = RenderUtil.getMissingSprite();
        }
        
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
	
	public boolean isPipeValid(TileEntityPipe pipe, EnumFacing side, ItemStack stack){
		return pipe instanceof TileEntityPipeEStorage;
	}
	
	public int getRedstonePower(TileEntityPipe pipe, EnumFacing face, boolean strong){
		return (strong == strongPower) ? outRedstonePower : 0;
	}
	
	public boolean canConnectRedstone(TileEntityPipe pipe, EnumFacing dir){
		return true;
	}
}
