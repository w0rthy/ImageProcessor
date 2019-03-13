package imageprocessor;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import static imageprocessor.ImageUtils.*;

abstract class ImageSoftener {
    
    static BufferedImage soften(BufferedImage img, double amt, double radius){
        int width = img.getWidth();
        int height = img.getHeight();
        
        BufferedImage imgNew = new BufferedImage(width, height, img.getType());
        
        //Get the Rasters
        Raster r1 = img.getRaster();
        WritableRaster r2 = imgNew.getRaster();
        
        //Vars
        int[] c1 = new int[4]; //Old col
        int[] c2; //New col
        int[] diff; //Difference between the two
        
        //Begin
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                r1.getPixel(x, y, c1); //Get Current col
                c2 = avgColInArea(r1, x, y, radius, radius); //Find average in radius
                diff = colDiff(c2, c1); //Find diff from c1 to c2
                r2.setPixel(x, y, colClamp(colSum(c1, colScale(diff, amt)))); //Scale, add, and clamp the diff, then set
            }
        }
        
        return imgNew;
    }
}
