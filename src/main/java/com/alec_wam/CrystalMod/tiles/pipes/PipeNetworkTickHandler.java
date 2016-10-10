package com.alec_wam.CrystalMod.tiles.pipes;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class PipeNetworkTickHandler {

  public static final PipeNetworkTickHandler instance = new PipeNetworkTickHandler();

  public static interface TickListener {
    public void tickStart(TickEvent.ServerTickEvent evt);

    public void tickEnd(TickEvent.ServerTickEvent evt);
  }

  private final List<TickListener> listeners = new ArrayList<TickListener>();

  private final IdentityHashMap<AbstractPipeNetwork, Boolean> networks =
          new IdentityHashMap<AbstractPipeNetwork, Boolean>();

  public void addListener(TickListener listener) {
    listeners.add(listener);
  }

  public void removeListener(TickListener listener) {
    listeners.remove(listener);
  }

  public void registerNetwork(AbstractPipeNetwork cn) {
    networks.put(cn, Boolean.TRUE);
  }

  public void unregisterNetwork(AbstractPipeNetwork cn) {
    networks.remove(cn);
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if(event.phase == Phase.START) {
      tickStart(event);
    } else {
      tickEnd(event);
    }
  }

  public void tickStart(TickEvent.ServerTickEvent event) {
    for (TickListener h : listeners) {
      h.tickStart(event);
    }
  }

  public void tickEnd(TickEvent.ServerTickEvent event) {
    for (TickListener h : listeners) {
      h.tickEnd(event);
    }
    listeners.clear();
    for(AbstractPipeNetwork cn : networks.keySet()) {
      cn.doNetworkTick();
    }
  }

}
