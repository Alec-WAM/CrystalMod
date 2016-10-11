package alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.text.Collator;

import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public class ModComp extends NameComp {

  public ModComp(Collator collator) {
    super(collator);
  }

  @Override
  public int compare(ItemStackData a, ItemStackData b) {
	  String modIdA = a.getModId();
	  String modIdB = b.getModId();
	  int res = collator.compare(modIdA, modIdB);
	  if (res == 0) {
		  res = super.compare(a, b);
	  }
	  return res;
  }

}
