package alec_wam.CrystalMod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Vector3d {

  public double x;
  public double y;
  public double z;

  public Vector3d() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Vector3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vector3d(float[] array) {
	  this.x = array[0];
	  this.y = array[1];
	  this.z = array[2];
  }

  public Vector3d(Vector3d other) {
    this(other.x, other.y, other.z);
  }

  public Vector3d(BlockPos blockPos) {
    this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
  }

  public void set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void set(float[] array) {
	  this.x = array[0];
	  this.y = array[1];
	  this.z = array[2];
  }

  public void set(Vector3d vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
  }

  public Vector3d add(Vector3d vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
    return this;
  }

  public Vector3d add(double x2, double y2, double z2) {
    x += x2;
    y += y2;
    z += z2;
    return this;
  }

  public void sub(Vector3d vec) {
    x -= vec.x;
    y -= vec.y;
    z -= vec.z;
  }
  
  public Vector3d subtract(Vector3d vec) {
	  x -= vec.x;
	  y -= vec.y;
	  z -= vec.z;
	  return this;
  }

  public Vector3d negate() {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }

  public Vector3d scale(double s) {
    x *= s;
    y *= s;
    z *= s;
    return this;
  }

  public void scale(double sx, double sy, double sz) {
    x *= sx;
    y *= sy;
    z *= sz;
  }

  public void normalize() {
    double scale = 1.0 / Math.sqrt(x * x + y * y + z * z);
    scale(scale);
  }
  
  public Vector3d normal() {
    double scale = 1.0 / Math.sqrt(x * x + y * y + z * z);
    scale(scale);
    return this;
  }

  public double dot(Vector3d other) {
    return x * other.x + y * other.y + z * other.z;
  }

  public void cross(Vector3d v1, Vector3d v2) {
    x = v1.y * v2.z - v1.z * v2.y;
    y = v2.x * v1.z - v2.z * v1.x;
    z = v1.x * v2.y - v1.y * v2.x;
  }
  
  public Vector3d crossProduct(Vector3d vec) {
      double d = y * vec.z - z * vec.y;
      double d1 = z * vec.x - x * vec.z;
      double d2 = x * vec.y - y * vec.x;
      x = d;
      y = d1;
      z = d2;
      return this;
  }

  public double lengthSquared() {
    return x * x + y * y + z * z;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public double distanceSquared(Vector3d v) {
    double dx, dy, dz;
    dx = x - v.x;
    dy = y - v.y;
    dz = z - v.z;
    return (dx * dx + dy * dy + dz * dz);
  }

  public double distance(Vector3d v) {
    return Math.sqrt(distanceSquared(v));
  }

  @Override
  public String toString() {
    return "Vector3d(" + x + ", " + y + ", " + z + ")";
  }

  public void abs() {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
  }

  public Vec3d getVec3() {
    return new Vec3d(x, y, z);
  }
  
  public Vector3d copy(){
	  return new Vector3d(x, y, z);
  }
}
