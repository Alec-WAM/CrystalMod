package com.alec_wam.CrystalMod.tiles.pipes;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RaytraceResult {

  public static RaytraceResult getClosestHit(Vec3d origin, Collection<RaytraceResult> candidates) {
    double minLengthSquared = Double.POSITIVE_INFINITY;
    RaytraceResult closest = null;

    for (RaytraceResult candidate : candidates) {
      RayTraceResult hit = candidate.rayTraceResult;
      if(hit != null) {
        double lengthSquared = hit.hitVec.squareDistanceTo(origin);
        if(lengthSquared < minLengthSquared) {
          minLengthSquared = lengthSquared;
          closest = candidate;
        }
      }
    }
    return closest;
  }

  public static void sort(final Vec3d origin, List<RaytraceResult> toSort) {
    if(origin == null || toSort == null) {
      return;
    }
    Collections.sort(toSort, new Comparator<RaytraceResult>() {
      @Override
      public int compare(RaytraceResult o1, RaytraceResult o2) {
        return Double.compare(o1.getDistanceTo(origin), o2.getDistanceTo(origin));
      }
    });
  }

  public final CollidableComponent component;
  public final net.minecraft.util.math.RayTraceResult rayTraceResult;

  public RaytraceResult(CollidableComponent component, net.minecraft.util.math.RayTraceResult rayTraceResult) {
    this.component = component;
    this.rayTraceResult = rayTraceResult;
  }

  public double getDistanceTo(Vec3d origin) {
    if(rayTraceResult == null || origin == null) {
      return Double.MAX_VALUE;
    }
    return rayTraceResult.hitVec.squareDistanceTo(origin);
  }

}
