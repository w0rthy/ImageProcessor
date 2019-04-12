package imageprocessor;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;


//Exposed Internal Methods
public abstract class ImageFunctions {
    
    //Calculates the contrast multiplier that would be used by the DynamicCorrection implementation of this algorithm
    public static double calcMaxCliplessContrastG(BufferedImage img, double gamma, double lim){
        Raster r = img.getRaster();
        
        //Build a gamma lookup table
        double div = Math.pow(255.0, gamma)/255.0;
        int[] tab = ImageUtils.buildLookupTable(a -> (int)(Math.pow(a, gamma)/div));
        
        int maxcomp = ImageAnalysis.componentPercentile(r, tab, 4, lim);
        
        return 255.0/(maxcomp);
    }
    
    public static double calcMaxCliplessContrast(BufferedImage img, double lim){
        Raster r = img.getRaster();
        
        int maxcomp = ImageAnalysis.componentPercentile(r, 4, lim);
        
        return 255.0/(maxcomp);
    }
    
    public static double calcMaxCliplessContrastG(BufferedImage img, double gamma){
        return calcMaxCliplessContrastG(img, 0.05, gamma);
    }
    
    public static double calcMaxCliplessContrast(BufferedImage img){
        return calcMaxCliplessContrast(img, 0.05);
    }
}
