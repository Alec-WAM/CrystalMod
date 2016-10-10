package com.alec_wam.CrystalMod.client.util;

public class SpriteData {

	private double u;
	private double v;
	private int width;
	private int height;
	
	
	public SpriteData(double u, double v, int width, int height){
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
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
}
