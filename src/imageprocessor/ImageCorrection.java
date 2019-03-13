package imageprocessor;

import java.awt.image.BufferedImage;
import static imageprocessor.ImageUtils.*;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

abstract class ImageCorrection {
    
    //Applies gamma 'gamma' and contrast 'contrast' to given image 'img'
    static BufferedImage correct(BufferedImage img, double contrast, double gamma){
        int width = img.getWidth();
        int height = img.getHeight();
        
        //Make the new image
        BufferedImage imgNew = new BufferedImage(width, height, img.getType());
        
        //Get the Rasters
        Raster r1 = img.getRaster();
        WritableRaster r2 = imgNew.getRaster();

        //Build lookup table
        int[] tab = blankLookupTable();
        
        //Do gamma
        if(gamma!=1.0){
            double div = Math.pow(255.0,gamma)/255.0; //Div for amt
            tab = mutateLookupTable(tab, a -> (int)(Math.pow(a, gamma)/div));
        }
        //Do contrast
        if(contrast!=1.0)
            tab = mutateLookupTable(tab, a -> (int)(a*contrast));
        
        //Clamp it
        tab = clampLookupTable(tab);
        
        //Vars
        int[] c1 = new int[4]; //Used for getPixel
        
        //Begin
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                r2.setPixel(x, y, applyLookupTable(r1.getPixel(x, y, c1), tab));
            }
        }
        
        return imgNew;
    }
}
