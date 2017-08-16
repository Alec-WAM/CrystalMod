package alec_wam.CrystalMod.util;

import java.util.Formatter;
import java.util.Locale;

public class Quat4d
{
  public double x;
  public double y;
  public double z;
  public double s;
  public static final double SQRT2 = Math.sqrt(2.0D);
  
  public Quat4d()
  {
    this.s = 1.0D;
    this.x = 0.0D;
    this.y = 0.0D;
    this.z = 0.0D;
  }
  
  public Quat4d(Quat4d quat)
  {
    this.x = quat.x;
    this.y = quat.y;
    this.z = quat.z;
    this.s = quat.s;
  }
  
  public Quat4d(double d, double d1, double d2, double d3)
  {
    this.x = d1;
    this.y = d2;
    this.z = d3;
    this.s = d;
  }
  
  public Quat4d set(Quat4d quat)
  {
    this.x = quat.x;
    this.y = quat.y;
    this.z = quat.z;
    this.s = quat.s;
    
    return this;
  }
  
  public Quat4d set(double d, double d1, double d2, double d3)
  {
    this.x = d1;
    this.y = d2;
    this.z = d3;
    this.s = d;
    
    return this;
  }
  
  public static Quat4d aroundAxis(double ax, double ay, double az, double angle)
  {
    return new Quat4d().setAroundAxis(ax, ay, az, angle);
  }
  
  public static Quat4d aroundAxis(Vector3d axis, double angle)
  {
    return aroundAxis(axis.x, axis.y, axis.z, angle);
  }
  
  public Quat4d setAroundAxis(double ax, double ay, double az, double angle)
  {
    angle *= 0.5D;
    double d4 = Math.sin(angle);
    return set(Math.cos(angle), ax * d4, ay * d4, az * d4);
  }
  
  public Quat4d setAroundAxis(Vector3d axis, double angle)
  {
    return setAroundAxis(axis.x, axis.y, axis.z, angle);
  }
  
  public Quat4d multiply(Quat4d quat)
  {
    double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
    double d1 = this.s * quat.x + this.x * quat.s - this.y * quat.z + this.z * quat.y;
    double d2 = this.s * quat.y + this.x * quat.z + this.y * quat.s - this.z * quat.x;
    double d3 = this.s * quat.z - this.x * quat.y + this.y * quat.x + this.z * quat.s;
    this.s = d;
    this.x = d1;
    this.y = d2;
    this.z = d3;
    
    return this;
  }
  
  public Quat4d rightMultiply(Quat4d quat)
  {
    double d = this.s * quat.s - this.x * quat.x - this.y * quat.y - this.z * quat.z;
    double d1 = this.s * quat.x + this.x * quat.s + this.y * quat.z - this.z * quat.y;
    double d2 = this.s * quat.y - this.x * quat.z + this.y * quat.s + this.z * quat.x;
    double d3 = this.s * quat.z + this.x * quat.y - this.y * quat.x + this.z * quat.s;
    this.s = d;
    this.x = d1;
    this.y = d2;
    this.z = d3;
    
    return this;
  }
  
  public double mag()
  {
    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.s * this.s);
  }
  
  public Quat4d normalize()
  {
    double d = mag();
    if (d != 0.0D)
    {
      d = 1.0D / d;
      this.x *= d;
      this.y *= d;
      this.z *= d;
      this.s *= d;
    }
    return this;
  }
  
  public Quat4d copy()
  {
    return new Quat4d(this);
  }
  
  public void rotate(Vector3d vec)
  {
    double d = -this.x * vec.x - this.y * vec.y - this.z * vec.z;
    double d1 = this.s * vec.x + this.y * vec.z - this.z * vec.y;
    double d2 = this.s * vec.y - this.x * vec.z + this.z * vec.x;
    double d3 = this.s * vec.z + this.x * vec.y - this.y * vec.x;
    vec.x = (d1 * this.s - d * this.x - d2 * this.z + d3 * this.y);
    vec.y = (d2 * this.s - d * this.y + d1 * this.z - d3 * this.x);
    vec.z = (d3 * this.s - d * this.z - d1 * this.y + d2 * this.x);
  }
  
  @Override
public String toString()
  {
    StringBuilder stringbuilder = new StringBuilder();
    Formatter formatter = new Formatter(stringbuilder, Locale.US);
    formatter.format("Quaternion:\n", new Object[0]);
    formatter.format("  < %f %f %f %f >\n", new Object[] { Double.valueOf(this.s), Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z) });
    

    formatter.close();
    return stringbuilder.toString();
  }
}

