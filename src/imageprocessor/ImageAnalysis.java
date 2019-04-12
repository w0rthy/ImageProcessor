package imageprocessor;

import java.awt.image.Raster;

//All component-wise analysis done here is based completely on the max of the R,G, and B components of pixels.
//  The idea is that the maximum of the three components can be used to determine the intensity of that color.
//  The ratios between the three components determines the color itself.
//  That is, the color (100,50,0) is a less intense version of the color (200,100,0)
//      (By this logic, Brown is a less intense version of Orange)
//  
//  Calculating the average max component will result in the average color intensity as opposed to calculating
//      the average component value simply determines how 'white' an image is.
//  My reasoning for this is that (255,0,0) is the brightest, most intense version of red that can be produced.
//      Therefore, any pixel with this color should be considered as having maximum intensity so as not to
//          penalize colors for not being a shade of grey.

//TODO: All functions in this class will be replaced by simpler versions utilizing a new function which uses the
//  ComponentAnalysis class to perform every analysis function in one run through an image. The simplified functions
//      will merely have to return the correct information from the resulting object.
abstract class ImageAnalysis {
    
    //Gets the max R,G, or B component in an image, at resolution res (1 = all pixels checked, 2 = half)
    static int maxComponent(Raster r, int res){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        int max = 0;
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //max = Math.max(Math.max(Math.max(max, tmp[0]), tmp[1]), tmp[2]);
                max = ImageUtils.colMaxComp(tmp);
                if(max==255) //Short-circuit
                    return max;
            }
        }
        return max;
    }
    
    //Utilizes a provided lookup table, tab
    static int maxComponent(Raster r, int[] tab, int res){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        int max = 0;
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //max = Math.max(Math.max(Math.max(max, tab[tmp[0]]), tab[tmp[1]]), tab[tmp[2]]);
                max = ImageUtils.colMaxComp(tmp,tab);
                if(max==255) //Short-circuit
                    return max;
            }
        }
        return max;
    }
    
    //Gets the R,G, or B component value at the top 'amt' percentile of an image at resolution 'res'
    static int componentPercentile(Raster r, int res, double amt){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        int[] hist = new int[256]; //Histogram for components
        int count = ((width+res-1)/res)*((height+res-1)/res); //Formula to replace manually counting
        int[] tmp = new int[4];
        
        //Begin
        //Calc Avg
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //hist[tmp[0]]++;
                //hist[tmp[1]]++;
                //hist[tmp[2]]++;
                //hist[Math.max(Math.max(tmp[0],tmp[1]),tmp[2])]++;
                hist[ImageUtils.colMaxComp(tmp)]++;
            }
        }
        
        //Find the correct val
        int target = (int)(count*amt);
        int sum = 0;
        int at = 256;
        
        while(at>0 && sum<target){
            at--;
            sum+=hist[at];
        }
        return at>255?255:at; //Cap at 255
    }
    
    //Utilizes a provided lookup table, tab
    static int componentPercentile(Raster r, int[] tab, int res, double amt){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        int[] hist = new int[256]; //Histogram for components
        int count = ((width+res-1)/res)*((height+res-1)/res); //Formula to replace manually counting
        int[] tmp = new int[4];
        
        //Begin
        //Calc Avg
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //hist[tab[tmp[0]]]++;
                //hist[tab[tmp[1]]]++;
                //hist[tab[tmp[2]]]++;
                //hist[Math.max(Math.max(tab[tmp[0]],tab[tmp[1]]),tab[tmp[2]])]++;
                hist[ImageUtils.colMaxComp(tmp,tab)]++;
            }
        }
        
        //Find the correct val
        int target = (int)(count*amt);
        int sum = 0;
        int at = 256;
        
        while(at>0 && sum<target){
            at--;
            sum+=hist[at];
        }
        return at>255?255:at; //Cap at 255
    }
    
    //Gets the average max R,G, or B component value of raster 'r' at resolution 'res'
    static double avgComponent(Raster r, int res){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        long sum = 0;
        long count = ((width+res-1)/res)*((height+res-1)/res); //Formula to replace manually counting
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                sum+=ImageUtils.colMaxComp(tmp);
                //sum+=tmp[0]+tmp[1]+tmp[2];
            }
        }
        return (double)sum/(double)count;
    }
    
    //Gets the average deviation of R,G, or B component maximums of raster 'r' at resolution 'res' for provided average 'avg'
    static double avgDevComponent(Raster r, int res, double avg){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        double sum = 0;
        long count = ((width+res-1)/res)*((height+res-1)/res); //Formula to replace manually counting
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //sum+=Math.abs(avg-tmp[0])+Math.abs(avg-tmp[1])+Math.abs(avg-tmp[2]);
                //sum+=Math.abs(tmp[0]+tmp[1]+tmp[2]-avg*3.0);
                sum+=Math.abs(ImageUtils.colMaxComp(tmp)-avg);
            }
        }
        return sum/(double)count;
    }
    
    //Performs the average itself
    static double avgDevComponent(Raster r, int res){
        return avgDevComponent(r, res, avgComponent(r, res));
    }
    
    //Class to hold data collected when performing a componentAnalysis on an image
    class ComponentAnalysis {
        int max = 0; //The max component value
        double avg = 0; //The average max component value (average color intensity)
        double avgdev = 0; //The average deviation of max component values (color intensities)
        int[] hist = null; //A histogram of max component values (color intensities)
    }
}
