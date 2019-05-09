package alec_wam.CrystalMod.tiles.chests.wireless;

import alec_wam.CrystalMod.util.math.Quat4d;
import alec_wam.CrystalMod.util.math.Vector3d;

public class DyeButton
{    
    public DyeButton(int index)
    {
        button = index;
        
        verts = new Vector3d[8];
        verts[0] = new Vector3d(0, -0.25, -0.0625);
        verts[1] = new Vector3d(0.125, -0.25, -0.0625);
        verts[2] = new Vector3d(0.125, -0.25, 0);
        verts[3] = new Vector3d(0, -0.25, 0);
        verts[4] = new Vector3d(0, 0, -0.0625);
        verts[5] = new Vector3d(0.125, 0, -0.0625);
        verts[6] = new Vector3d(0.125, 0, 0);
        verts[7] = new Vector3d(0, 0, 0);
        
        for(int i = 0; i < 8; i++)
        {
            verts[i].add(0.25 + 0.1875*index, -0.375, 0.9375);
        }

        Quat4d quat2 = Quat4d.aroundAxis(1, 0, 0, -0.5 * 3.14159);
        for(int i = 0; i < 8; i++)
        {
            quat2.rotate(verts[i]);
        }
    }
    
    private DyeButton()
    {}
    
    public void rotateMeta(int angle)
    {
        rotate(0.5, 0, 0.5, 0, 1, 0, angle * -0.5 * 3.14159);
    }
    
    /**
     * 
     * @param ax
     * @param ay
     * @param az
     * @param angle in radians
     */
    public void rotate(double px, double py, double pz, double ax, double ay, double az, double angle)
    {
        Quat4d quat = Quat4d.aroundAxis(ax, ay, az, angle);
        for(int i = 0; i < 8; i++)
        {
            verts[i].add(-px, -py, -pz);
            quat.rotate(verts[i]);
            verts[i].add(px, py, pz);
        }
    }
    
    public DyeButton copy()
    {
        DyeButton newbutton = new DyeButton();
        newbutton.button = button;
        newbutton.verts = new Vector3d[8];

        for(int i = 0; i < 8; i++)
        {
            newbutton.verts[i] = verts[i].copy();        
        }
        
        return newbutton;
    }
    
    public void flipCoords(int ax, int ay, int az)
    {
        for(int i = 0; i < 8; i++)
        {
            verts[i].add(ax, ay, az);
        }
    }
    
    public int button;
    public Vector3d[] verts;
    
    public Vector3d getMin()
    {
        int minindex = 0;
        double mindist = 100;
        for(int i = 0; i < 8; i++)
        {
            double dist = verts[i].x + verts[i].y + verts[i].z;
            if(dist < mindist)
            {
                mindist = dist;
                minindex = i;
            }
        }
        
        return verts[minindex];
    }
    
    public Vector3d getMax()
    {
        int maxindex = 0;
        double maxdist = 0;
        for(int i = 0; i < 8; i++)
        {
            double dist = verts[i].x + verts[i].y + verts[i].z;
            if(dist > maxdist)
            {
                maxdist = dist;
                maxindex = i;
            }
        }
        
        return verts[maxindex];
    }
}
