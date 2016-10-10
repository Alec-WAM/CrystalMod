package com.alec_wam.CrystalMod.client.util.comp.book;

import com.alec_wam.CrystalMod.client.util.comp.BaseComponent;

public class BlankPage extends BaseComponent {

	public BlankPage() {
		super(0, 0);
	}

	@Override
	public int getWidth() {
		return 220;
	}

	@Override
	public int getHeight() {
		return 200;
	}

}
