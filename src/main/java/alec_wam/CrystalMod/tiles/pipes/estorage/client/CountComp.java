package alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.text.Collator;

import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;

public class CountComp extends NameComp {

  public CountComp(Collator collator) {
    super(collator);
  }

  @Override
  public int compare(ItemStackData a, ItemStackData b) {
	  int res = (b.getAmount()) - (a.getAmount());
	  if(res == 0) {
	      res = super.compare(a, b);
	  }
	  return res;
  }

}
