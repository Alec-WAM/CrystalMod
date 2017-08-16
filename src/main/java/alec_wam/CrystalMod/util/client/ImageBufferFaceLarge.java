package alec_wam.CrystalMod.util.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ImageBufferFaceLarge implements IImageBuffer
{
    private int imageWidth;
    private int imageHeight;
    public ImageBufferFaceLarge(int w, int h){
    	this.imageWidth = w;
    	this.imageHeight = h;
    }

    @Override
	public BufferedImage parseUserSkin(BufferedImage p_78432_1_)
    {
        if (p_78432_1_ == null)
        {
            return null;
        }
        else
        {
            BufferedImage bufferedimage1 = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            Graphics graphics = bufferedimage1.getGraphics();
            graphics.drawImage(p_78432_1_, 0, 0, (ImageObserver)null);
            graphics.dispose();
            ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
            return bufferedimage1;
        }
    }

    @Override
	public void skinAvailable() {}
}
