package alec_wam.CrystalMod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Vector3f {

  public float x;
  public float y;
  public float z;

  public Vector3f() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Vector3f(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Vector3f(float[] array) {
	  this.x = array[0];
	  this.y = array[1];
	  this.z = array[2];
  }

  public Vector3f(Vector3f other) {
    this(other.x, other.y, other.z);
  }

  public Vector3f(BlockPos blockPos) {
    this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
  }

  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void set(float[] array) {
	  this.x = array[0];
	  this.y = array[1];
	  this.z = array[2];
  }

  public void set(Vector3f vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
  }

  public Vector3f add(Vector3f vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
    return this;
  }

  public Vector3f add(float x2, float y2, float z2) {
    x += x2;
    y += y2;
    z += z2;
    return this;
  }

  public void sub(Vector3f vec) {
    x -= vec.x;
    y -= vec.y;
    z -= vec.z;
  }
  
  public Vector3f subtract(Vector3f vec) {
	  x -= vec.x;
	  y -= vec.y;
	  z -= vec.z;
	  return this;
  }

  public Vector3f negate() {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }

  public Vector3f scale(float s) {
    x *= s;
    y *= s;
    z *= s;
    return this;
  }

  public void scale(float sx, float sy, float sz) {
    x *= sx;
    y *= sy;
    z *= sz;
  }

  public void normalize() {
    float scale = (float) (1.0f / Math.sqrt(x * x + y * y + z * z));
    scale(scale);
  }
  
  public Vector3f normal() {
    float scale = (float) (1.0 / Math.sqrt(x * x + y * y + z * z));
    scale(scale);
    return this;
  }

  public float dot(Vector3f other) {
    return x * other.x + y * other.y + z * other.z;
  }

  public void cross(Vector3f v1, Vector3f v2) {
    x = v1.y * v2.z - v1.z * v2.y;
    y = v2.x * v1.z - v2.z * v1.x;
    z = v1.x * v2.y - v1.y * v2.x;
  }
  
  public Vector3f crossProduct(Vector3f vec) {
      float d = y * vec.z - z * vec.y;
      float d1 = z * vec.x - x * vec.z;
      float d2 = x * vec.y - y * vec.x;
      x = d;
      y = d1;
      z = d2;
      return this;
  }

  public float lengthSquared() {
    return x * x + y * y + z * z;
  }

  public float length() {
    return (float) Math.sqrt(lengthSquared());
  }

  public float distanceSquared(Vector3f v) {
    float dx, dy, dz;
    dx = x - v.x;
    dy = y - v.y;
    dz = z - v.z;
    return (dx * dx + dy * dy + dz * dz);
  }

  public float distance(Vector3f v) {
    return (float) Math.sqrt(distanceSquared(v));
  }

  @Override
  public String toString() {
    return "Vector3f(" + x + ", " + y + ", " + z + ")";
  }

  public void abs() {
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
  }

  public Vec3d getVec3() {
    return new Vec3d(x, y, z);
  }
  
  public Vector3f copy(){
	  return new Vector3f(x, y, z);
  }
}
