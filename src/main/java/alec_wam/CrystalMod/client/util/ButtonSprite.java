package alec_wam.CrystalMod.client.util;

public enum ButtonSprite {
	SOUND_MASTER(0, 0), SOUND_MUSIC(1, 0), SOUND_RECORD(2, 0), SOUND_WEATHER(3, 0), SOUND_BLOCKS(4, 0), SOUND_HOSTILE(5, 0), SOUND_PASSIVE(6, 0), SOUND_PLAYER(7, 0), SOUND_AMBIENT(8, 0);
	
	private int x, y;
	ButtonSprite(int x, int y){
		this.x = x; this.y = y;
	}
	
	public int getX(){
		return x;		
	}
	
	public int getY(){
		return y;
	}
}
