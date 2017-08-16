package alec_wam.CrystalMod.tiles.playercube;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPlayerCubePortal extends TileEntityMod {

	private GameProfile owner;
	public FakeChunk mobileChunk;
	public boolean firstInit = false;
	public String cubeID;
	public boolean isLocked;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(this.mobileChunk != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream(mobileChunk.getMemoryUsage());
	        DataOutputStream out = new DataOutputStream(baos);
	        try {
	            ChunkIO.writeAll(out, mobileChunk);
	            out.flush();
	            out.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        nbt.setByteArray("chunk", baos.toByteArray());
	        
	        if (!mobileChunk.chunkTileEntityMap.isEmpty()) {
	            NBTTagList tileEntities = new NBTTagList();
	            for (TileEntity tileentity : mobileChunk.chunkTileEntityMap.values()) {
	            	if(tileentity instanceof TileEntityPlayerCubePortal)continue;
	                NBTTagCompound comp = new NBTTagCompound();
	                tileentity.writeToNBT(comp);
	                tileEntities.appendTag(comp);
	            }
	            nbt.setTag("tileent", tileEntities);
	        }
		}
        
        if(owner !=null){
        	nbt.setString("Name", owner.getName());
	        final UUID id = owner.getId();
	        if (id != null) {
	        	nbt.setLong("UUIDL", id.getLeastSignificantBits());
	        	nbt.setLong("UUIDU", id.getMostSignificantBits());
	        }
        }
        if(!Strings.isNullOrEmpty(cubeID))nbt.setString("CubeID", cubeID);
        nbt.setBoolean("IsLocked", isLocked);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);

        if (nbt.hasKey("Name")) {
        	final String name = nbt.getString("Name");
            UUID uuid = null;
            if (nbt.hasKey("UUIDL")) {
                uuid = new UUID(nbt.getLong("UUIDU"), nbt.getLong("UUIDL"));
            }
            if (Strings.isNullOrEmpty(name)) {
                this.owner = null;
            }
            else owner = new GameProfile(uuid, name);
        }
        
        if (mobileChunk == null) {
            if (getWorld() != null) {
                if (getWorld().isRemote) {
                    initClient();
                } else {
                    initCommon();
                }
            }
        }

        if(nbt.hasKey("chunk") && mobileChunk !=null){
	        byte[] ab = nbt.getByteArray("chunk");
	        ByteArrayInputStream bais = new ByteArrayInputStream(ab);
	        DataInputStream in = new DataInputStream(bais);
	        try {
	            ChunkIO.read(in, mobileChunk);
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }

        if(nbt.hasKey("tileent") && mobileChunk !=null){
	        NBTTagList tiles = nbt.getTagList("tileent", 10);
	        if (tiles != null) {
	            for (int i = 0; i < tiles.tagCount(); i++) {
	                try {
	                    NBTTagCompound comp = tiles.getCompoundTagAt(i);
	                    TileEntity tileentity = TileEntity.create(getWorld(), comp);
	                    mobileChunk.setTileEntity(tileentity.getPos(), tileentity);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }
        }
        
        cubeID = nbt.getString("CubeID");
        if(mobileChunk !=null){
        	this.mobileChunk.setChunkModified();
        }
        this.isLocked = nbt.getBoolean("IsLocked");
    }
	
	public PlayerCube getCube(){
		return CubeManager.getInstance().getCube(getOwner(), cubeID);
	}
	
	public void clearCube(){
		if(mobileChunk !=null){
			this.mobileChunk.clear();
			if(!getWorld().isRemote){
				this.sendUpdatePackets();
			}
		}
	}
	
	public void assemble(){
		if(getCube() == null){
			return;
		}
		//getCube().spawnBlock = getCube().minBlock.add(8, 0, 8);
		TileEntity tileentity;
        BlockPos iPos;//this.getPos().offset(EnumFacing.UP);
        World wor = CubeManager.getInstance().getWorld();
        for(int y = 1; y < 15; y++){
        	for(int x = 1; x < 15; x++){
        		for(int z = 1; z < 15; z++){
		            iPos = getCube().minBlock.add(x, y, z);
		            
		            if(wor.getBlockState(iPos).getBlock() !=ModBlocks.cubeBlock){
		            	@SuppressWarnings("deprecation")
						IBlockState state = wor.getBlockState(iPos).getBlock().getActualState(wor.getBlockState(iPos), CubeManager.getInstance().getWorld(), iPos);
			            tileentity = wor.getTileEntity(iPos);
			            if (tileentity != null || state.getBlock().hasTileEntity(state) && (tileentity = wor.getTileEntity(iPos)) != null) {
			                tileentity.validate();
			            }
			            if (mobileChunk.addBlockWithState(iPos, state)) {
			                TileEntity tileClone = tileentity;
			                mobileChunk.setTileEntity(iPos, tileClone);
			            }
		            }
        		}
        	}
        }
		
		mobileChunk.setChunkModified();
		mobileChunk.onChunkLoad();
		
		/*if(!getWorld().isRemote){
			this.sendUpdatePackets();
		}*/
	}
	
	
	@SideOnly(Side.CLIENT)
    private void initClient() {
        mobileChunk = new FakeChunkClient(CubeManager.getInstance().getWorld(), this);
    }

    private void initCommon() {
        mobileChunk = new FakeChunkServer(CubeManager.getInstance().getWorld(), this);
    }
	
	public void setOwner(EntityPlayer player){
		this.owner = player.getGameProfile();
	}
	
	public boolean isOwner(EntityPlayer player){
		if(getOwner() !=null){
			return getOwner().equals(player.getGameProfile());
		}
		return false;
	}
	
	public GameProfile getOwner(){
		return owner;
	}

	
	public void sendUpdatePackets(){
		if(!getWorld().isRemote){
			if(this.mobileChunk instanceof FakeChunkServer){
            	Collection<BlockPos> list = ((FakeChunkServer)mobileChunk).getSendQueue();
    			ChunkBlockUpdateMessage msg = new ChunkBlockUpdateMessage(mobileChunk, list);
                CrystalModNetwork.sendToAllAround(msg, this);
                TileEntitiesMessage msg2 = new TileEntitiesMessage(mobileChunk);
                CrystalModNetwork.sendToAllAround(msg2, this);
                list.clear();
            }
		}
	}

	@Override
	public void update() {
		super.update();
		if(!firstInit){
			if(this.mobileChunk == null){
				if (getWorld().isRemote) {
		            initClient();
		        } else {
		            initCommon();
		            assemble();
		        }
			}
			firstInit = true;
		}
		
		if(this.getWorld() !=null){
			if (mobileChunk.isModified) {
	            mobileChunk.isModified = false;
	            sendUpdatePackets();
	        }
			if(!getWorld().isRemote){
				if(getCube() !=null && !getCube().watchers.contains(this)){
					getCube().watchers.add(this);
				}
			}
		}
	}
	
	@Override
	public void invalidate(){
		super.invalidate();
		if(getCube() !=null){
			getCube().watchers.remove(this);
		}
	}
	
	@Override
	public void onChunkUnload(){
		super.onChunkUnload();
		if(getCube() !=null){
			getCube().watchers.remove(this);
		}
	}
	
}
