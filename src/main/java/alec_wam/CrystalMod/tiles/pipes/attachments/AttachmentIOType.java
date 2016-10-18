package alec_wam.CrystalMod.tiles.pipes.attachments;

public enum AttachmentIOType {
	ITEM, FLUID, BOTH;
	
	public AttachmentIOType getNext(){
		return (this == ITEM) ? FLUID : (this == FLUID) ? BOTH : ITEM;
	}
}
