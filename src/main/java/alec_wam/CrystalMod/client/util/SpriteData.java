package alec_wam.CrystalMod.client.util;

public class SpriteData {

	private double u;
	private double v;
	private double width;
	private double height;
	
	
	public SpriteData(double u, double v, double width, double height){
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}
	
	public double getU(){
		return u;
	}
	
	public double getV(){
		return v;
	}
	
	public double getWidth(){
		return width;
	}
	
	public double getHeight(){
		return height;
	}
	
}
