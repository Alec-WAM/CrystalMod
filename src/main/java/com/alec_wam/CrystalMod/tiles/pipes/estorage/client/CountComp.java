package com.alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.text.Collator;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public class CountComp extends NameComp {

  public CountComp(Collator collator) {
    super(collator);
  }

  @Override
  public int compare(ItemStackData a, ItemStackData b) {
	  int res = (b.stack == null ? 0 : b.stack.stackSize) - (a.stack == null ? 0 : a.stack.stackSize);
	  if(res == 0) {
	      res = super.compare(a, b);
	  }
	  return res;
  }

}
