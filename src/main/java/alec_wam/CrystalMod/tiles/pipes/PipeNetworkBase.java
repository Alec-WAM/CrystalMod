package alec_wam.CrystalMod.tiles.pipes;

import java.util.HashMap;
import java.util.Map;

import alec_wam.CrystalMod.tiles.pipes.item.PipeNetworkItem;

public class PipeNetworkBase<P extends TileEntityPipeBase> {

	public Map<NetworkPos, P> pipeMap;
	
	public PipeNetworkBase(){
		pipeMap = new HashMap<NetworkPos, P>();
	}
	
	@SuppressWarnings("unchecked")
	public boolean addPipe(TileEntityPipeBase pipe){
		NetworkPos pos = pipe.getNetworkPos();
		if(!pipeMap.containsKey(pos)){
			pipeMap.put(pos, (P)pipe);
			return true;
		}		
		return false;
	}
	
	public boolean removePipe(TileEntityPipeBase pipe) {
		NetworkPos pos = pipe.getNetworkPos();
		if(pipeMap.containsKey(pos)){
			return pipeMap.remove(pos) !=null;
		}	
		return false;
	}
	
	public void resetNetwork(){
		for(P pipe : pipeMap.values()){
			pipe.setNetwork(null);
		}
		pipeMap.clear();

		PipeNetworkTickHandler.INSTANCE.NETWORKS.remove(this);
	}
	
	/**
	 * @return Number of pipes in network
	 */
	public int getSize() {
		return pipeMap.size();
	}

	public void tick() {
		//System.out.println("TICK");
	}
	
}
