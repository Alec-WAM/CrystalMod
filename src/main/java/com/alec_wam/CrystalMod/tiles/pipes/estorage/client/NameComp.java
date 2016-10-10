package com.alec_wam.CrystalMod.tiles.pipes.estorage.client;

import java.text.Collator;
import java.util.Comparator;

import com.alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork.ItemStackData;

public class NameComp implements Comparator<ItemStackData> {
  protected final Collator collator;

  public NameComp(Collator collator) {
    this.collator = collator;
  }

  @Override
  public int compare(ItemStackData a, ItemStackData b) {
    String nameA = a.getUnlocName();
    String nameB = b.getUnlocName();
    return (collator == null) ? 0 : collator.compare(nameA, nameB);
  }

}
