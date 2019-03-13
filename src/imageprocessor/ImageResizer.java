package imageprocessor;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import static imageprocessor.ImageUtils.*;

abstract class ImageResizer {
    
    static BufferedImage resize(BufferedImage img, int width, int height){
        BufferedImage imgNew = new BufferedImage(width, height, img.getType());
        
        //Get the Rasters
        Raster r1 = img.getRaster();
        WritableRaster r2 = imgNew.getRaster();
        
        //Vars
        double vx = 0.0; //Virtual x on original image
        double vy = 0.0; //Virtual y on original image
        
        final double vxstep = (double)img.getWidth()/(double)width;
        final double vystep = (double)img.getHeight()/(double)height;
        
        final double vxr = Math.max(vxstep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        final double vyr = Math.max(vystep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        
        //Begin
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                r2.setPixel(x, y, avgColInArea(r1, vx, vy, vxr, vyr));
                vy+=vystep;
            }
            vx+=vxstep;
            vy=0.0;
        }
        
        return imgNew;
    }
}
