package imageprocessor;

import java.awt.image.Raster;

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
        int at = 255;
        
        while(sum<target){
            sum+=hist[at];
            at--;
        }
        return at+1;
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
        int at = 255;
        
        while(sum<target){
            sum+=hist[at];
            at--;
        }
        return at+1;
    }
    
    //Gets the average R,G, or B component value of raster 'r' at resolution 'res'
    static int avgComponent(Raster r, int res){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        long sum = 0;
        long count = ((width+res-1)/res)*((height+res-1)/res)*3; //Formula to replace manually counting
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                sum+=tmp[0]+tmp[1]+tmp[2];
            }
        }
        return (int)(sum/count);
    }
    
    //Gets the average deviation of R,G, or B components of raster 'r' at resolution 'res' for provided average 'avg'
    static int avgDevComponent(Raster r, int res, int avg){
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Vars
        long count = ((width+res-1)/res)*((height+res-1)/res)*3; //Formula to replace manually counting
        long sum = 0;
        int[] tmp = new int[4];
        
        //Begin
        for(int x = 0; x < width; x+=res){
            for(int y = 0; y < height; y+=res){
                r.getPixel(x, y, tmp);
                //sum+=Math.abs(avg-tmp[0])+Math.abs(avg-tmp[1])+Math.abs(avg-tmp[2]);
                sum+=Math.abs(tmp[0]+tmp[1]+tmp[2]-avg*3);
            }
        }
        return (int)(sum/count);
    }
    
    //Performs the average itself
    static int avgDevComponent(Raster r, int res){
        return avgDevComponent(r, res, avgComponent(r, res));
    }
    
    //Class to hold data collected when performing a componentAnalysis on an image
    class ComponentAnalysis {
        int max = 0; //The max component value
        int avg = 0; //The average max component value (average color intensity)
        int avgdev = 0; //The average deviation of max component values (color intensities)
        int[] hist = null; //A histogram of max component values (color intensities)
    }
}
