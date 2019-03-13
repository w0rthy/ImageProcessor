package imageprocessor;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

abstract class DynamicCorrection {
    static BufferedImage maxCliplessContrast(BufferedImage img){
        
        Raster r = img.getRaster();
        
        int maxcomp = ImageAnalysis.maxComponent(r, 4);
        
        double contrast = 255.0/(maxcomp);
        
        return ImageCorrection.correct(img, contrast, 1.0);
    }
    
    //Allows a gamma to be specified
    static BufferedImage maxCliplessContrast(BufferedImage img, double gamma){
        
        Raster r = img.getRaster();
        
        //Build a gamma lookup table
        double div = Math.pow(255.0, gamma)/255.0;
        int[] tab = ImageUtils.buildLookupTable(a -> (int)(Math.pow(a, gamma)/div));
        
        int maxcomp = ImageAnalysis.maxComponent(r, tab, 4);
        
        double contrast = 255.0/(maxcomp);
        
        return ImageCorrection.correct(img, contrast, gamma);
    }
    
    static BufferedImage maxLimitedClipContrast(BufferedImage img, double amt){
        
        Raster r = img.getRaster();
        
        int maxcomp = ImageAnalysis.componentPercentile(r, 4, amt);
        
        double contrast = 255.0/(maxcomp);
        
        return ImageCorrection.correct(img, contrast, 1.0);
    }
    
    //Allows a gamma to be specified
    static BufferedImage maxLimitedClipContrast(BufferedImage img, double amt, double gamma){
        
        Raster r = img.getRaster();
        
        //Build a gamma lookup table
        double div = Math.pow(255.0, gamma)/255.0;
        int[] tab = ImageUtils.buildLookupTable(a -> (int)(Math.pow(a, gamma)/div));
        
        int maxcomp = ImageAnalysis.componentPercentile(r, tab, 4, amt);
        
        double contrast = 255.0/(maxcomp);
        
        return ImageCorrection.correct(img, contrast, gamma);
    }
}
