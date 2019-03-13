package imageprocessor;

//Publicly facing methods in here

import java.awt.image.BufferedImage;

public abstract class ImageProcessor {
    
    //Resizes an image 'img' to width 'newWidth' and height 'newHeight'.
    //Resized image is reproduced as faithfully as possible to the original
    public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight){
        return ImageResizer.resize(img, newWidth, newHeight);
    }
    
    //Softens an image by a percentage amount 'amt'
    //Negative values for amt will sharpen the image
    public static BufferedImage soften(BufferedImage img, double amt){
        return ImageSoftener.soften(img, amt, 1.5); //~3% width by default
    }
    
    //Given radius is used instead of defeault 1.0
    public static BufferedImage soften(BufferedImage img, double amt, double radius){
        return ImageSoftener.soften(img, amt, radius+0.5); //~3% width by default
    }
    
    //Applies gamma 'amt' to provided image 'img'
    public static BufferedImage applyGamma(BufferedImage img, double amt){
        return ImageCorrection.correct(img, 1.0, amt);
    }
    
    //Applies contrast 'amt' to provided image 'img'
    public static BufferedImage applyContrast(BufferedImage img, double amt){
        return ImageCorrection.correct(img, amt, 1.0);
    }
    
    //Applies gamma 'gamma' and contrast 'contrast' to provided image 'img'
    public static BufferedImage correct(BufferedImage img, double contrast, double gamma){
        return ImageCorrection.correct(img, contrast, gamma);
    }
    
    //Applies a contrast to brighten the image such that there is no clipping
    public static BufferedImage maxCliplessContrast(BufferedImage img){
        return DynamicCorrection.maxCliplessContrast(img);
    }
    
    //Applies a contrast to brighten the image such that there is no clipping
    public static BufferedImage maxCliplessContrast(BufferedImage img, double gamma){
        return DynamicCorrection.maxCliplessContrast(img, gamma);
    }
    
    //Applies a contrast to brighten the image to a point where <5% of pixel components (R,G,B) clip
    public static BufferedImage maxLimitedClipContrast(BufferedImage img){
        return DynamicCorrection.maxLimitedClipContrast(img, 0.05);
    }
    
    //Allows a custom percentage limit 'lim' for maximum clipping
    public static BufferedImage maxLimitedClipContrast(BufferedImage img, double lim){
        return DynamicCorrection.maxLimitedClipContrast(img, lim);
    }
    
    //Allows a gamma to be specified
    public static BufferedImage maxLimitedClipContrastG(BufferedImage img, double gamma){
        return DynamicCorrection.maxLimitedClipContrast(img, 0.05, gamma);
    }
    
    //Allows a gamma and clipping limit to be specified
    public static BufferedImage maxLimitedClipContrastG(BufferedImage img, double gamma, double lim){
        return DynamicCorrection.maxLimitedClipContrast(img, lim, gamma);
    }
}
