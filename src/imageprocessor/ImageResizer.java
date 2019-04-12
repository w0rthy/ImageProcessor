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
        
        final double vxstep = (double)(img.getWidth()-1)/(double)(width-1);
        final double vystep = (double)(img.getHeight()-1)/(double)(height-1);
        
        final double vxr = Math.max(vxstep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        final double vyr = Math.max(vystep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        
        double vx = vxr; //Virtual x on original image
        double vy; //Virtual y on original image (initialized below)
        
        //Begin
        
        for(int x = 0; x < width; x++){
            vy = vyr;
            for(int y = 0; y < height; y++){
                r2.setPixel(x, y, avgColInArea(r1, vx, vy, vxr, vyr));
                vy+=vystep;
            }
            vx+=vxstep;
        }
        
        return imgNew;
    }
    
    //TODO: Must consider whether the functionality offered by this function has been provided in the correct way
    //  An alternative could be a resizeSubImage(img,width,height,subx,suby,subw,subh)
    //      This provides effectively the same functionality but in a more straight forward way
    static BufferedImage zoom(BufferedImage img, int width, int height, int zx, int zy, double zoom){
        BufferedImage imgNew = new BufferedImage(width, height, img.getType());
        
        //Get the Rasters
        Raster r1 = img.getRaster();
        WritableRaster r2 = imgNew.getRaster();
        
        //Vars
        
        int vw = (int)(img.getWidth()/Math.max(zoom,1.0)); //Width of original image for zoom location
        int vh = (int)(img.getHeight()/Math.max(zoom,1.0)); //Height of original image for zoom location
        
        //Ratio of ratios: If r > 1.0, then the image we want has a wider aspect ratio than the original image
        double r = ((double)width/(double)height)/((double)vw/(double)vh);
        
        //Correct the dimensions accordingly
        if(r>1.0){
            //vh = (int)(vh/r);
            vw = (int)(vw*r);
        }else{
            //vw = (int)(vw/r);
            vh = (int)(vh/r);
        }
        
        final double vxstep = (double)(vw-1)/(double)(width-1);
        final double vystep = (double)(vh-1)/(double)(height-1);
        
        final double vxr = Math.max(vxstep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        final double vyr = Math.max(vystep/2.0,0.5); //Radius should at least cover 1 pixel (improves upscaling quality)
        
        //Set Boundaries For Zooming
        //int[] oobCol = new int[]{0,0,0,255}; //Out of bounds color
        int btlx = Math.max((int)Math.ceil(-zx/vxstep),0); //Boundary Top Left X
        int btly = Math.max((int)Math.ceil(-zy/vystep),0); //Boundary Top Left Y
        int bbrx = Math.min((int)Math.floor((img.getWidth()-zx)/vxstep),width); //Boundary Bot Right X
        int bbry = Math.min((int)Math.floor((img.getHeight()-zy)/vystep),height); //Boundary Bot Right Y
        
        //Set Starting Positions
        double vx = zx + vxr + btlx*vxstep; //Virtual x on original image
        double vy; //Virtual y on original image (initialized below)
        
        //Begin
        for(int x = btlx; x < bbrx; x++){
            vy = zy + vyr + btly*vystep;
            for(int y = btly; y < bbry; y++){
                r2.setPixel(x, y, avgColInArea(r1, vx, vy, vxr, vyr));
                vy+=vystep;
            }
            vx+=vxstep;
        }
        
        return imgNew;
    }
}
