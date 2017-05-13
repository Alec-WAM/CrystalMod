package alec_wam.CrystalMod.tiles.darkinfection;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.darkinfection.BlockInfected.InfectedBlockType;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.BlockUtil.BlockFilter;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TileDarkInfection extends TileEntityMod implements IMessageHandler {

	private int currentRadius;
	private int currentOrb;
	private int delay;
	private int delayOrb;
	private List<BlockPos> toPlace = Lists.newArrayList(); 
	private List<BlockPos> toPlace2 = Lists.newArrayList(); 
	public boolean activated;
	public int openingPhase;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setBoolean("Activated", activated);
		nbt.setInteger("OpeningPhase", openingPhase);
		nbt.setInteger("Radius", currentRadius);
		nbt.setInteger("OrbSize", currentOrb);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.activated = nbt.getBoolean("Activated");
		this.openingPhase = nbt.getInteger("OpeningPhase");
		this.currentRadius = nbt.getInteger("Radius");
		this.currentOrb = nbt.getInteger("OrbSize");
	}
	
	@Override
	public void update(){
		super.update();
		
		if(!getWorld().isRemote){
			if(this.activated){
				if(this.activated){
					if(openingPhase < 8){
						if(this.shouldDoWorkThisTick(20)){
							openingPhase++;
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setInteger("Value", openingPhase);
							CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "OpeningPhase", nbt),  this);
						}
					}
				}
				if(openingPhase == 8){
					if(delay > 0){
						if(!getWorld().isBlockPowered(getPos()))delay--;
					}
					if(delayOrb > 0){
						if(!getWorld().isBlockPowered(getPos()))delayOrb--;
					}
					if(!toPlace.isEmpty()){
						if(delay <= 0){
							final int index = MathHelper.getInt(getWorld().rand, 0, toPlace.size()-1);
							final BlockPos toPlacePos = toPlace.get(index);
							toPlace.remove(index);
							IBlockState stone = ModBlocks.infectedBlock.getStateFromMeta(InfectedBlockType.NORMAL.getMeta());
							IBlockState cobble = ModBlocks.infectedBlock.getStateFromMeta(InfectedBlockType.CHISLED.getMeta());
							int type = MathHelper.getInt(getWorld().rand, 0, 1);
							
							IBlockState currentState = getWorld().getBlockState(toPlacePos);
							boolean okayToPlace = currentState.isFullBlock() && currentState.getBlockHardness(getWorld(), toPlacePos) > 0.0f;
							if(okayToPlace){
								world.setBlockState(toPlacePos, type == 0 ? stone : cobble);
							}
							delay = 20;
						}
					} else {
						if(Config.infectionRange <= 0 || currentRadius < Config.infectionRange){
							calcNextBorder();
						}
					}
					
					if(Config.infectionEncasingRange > 0 && currentRadius >= Config.infectionEncasingRange){
						if(!toPlace2.isEmpty()){
							if(delayOrb <= 0){
								final int index = MathHelper.getInt(getWorld().rand, 0, toPlace2.size()-1);
								final BlockPos toPlacePos = toPlace2.get(index);
								toPlace2.remove(index);
								IBlockState casing = ModBlocks.infectedBlock.getStateFromMeta(InfectedBlockType.CASING.getMeta());
								IBlockState currentState = getWorld().getBlockState(toPlacePos);
								boolean okayToPlace = getWorld().isAirBlock(toPlacePos) || currentState.getBlockHardness(getWorld(), toPlacePos) > 0.0f;
								if(okayToPlace){
									world.setBlockState(toPlacePos, casing);
								}
								delayOrb = 10;
							}
						} else {
							if(currentOrb < Config.infectionEncasingSize){
								calcNextOrb();
							}
						}
					}
				}
			}
		}
	}
	
	public void calcNextBorder(){
		currentRadius++;
		toPlace = BlockUtil.createSpecialOrb(world, pos, currentRadius, new BlockFilter(){

			@Override
			public boolean isValid(World world, BlockPos pos, IBlockState state) {
				if(!world.isAirBlock(pos)){
					Block block = state.getBlock();
					if(state.getMaterial() != Material.PLANTS){
						if(block.isFullBlock(state) && block.getBlockHardness(state, world, pos) >= 0.0F){
							return true;
						}
					}
				}
				return false;
			}
			
		});
	}
	
	public void calcNextOrb(){
		currentOrb++;
		toPlace2 = BlockUtil.createOrb(world, pos, currentOrb);
	}

	public int getOpeningPhase() {
		return openingPhase;
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("OpeningPhase")){
			this.openingPhase = messageData.getInteger("Value");
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
}
