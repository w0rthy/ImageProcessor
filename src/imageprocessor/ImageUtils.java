package imageprocessor;

import java.awt.image.Raster;

//Interface for enabling lambda functionality for some functions
interface IntMutator{int apply(int a);}

abstract class ImageUtils {
    //Gets decimal portion of a number
    public static double decimal(double a){
        return a-(int)a;
    }
    
    //Sums 2 Colors (a+b)
    static int[] colSum(int[] a, int[] b){
        return new int[] {a[0]+b[0],a[1]+b[1],a[2]+b[2],255};
    }
    
    //Difference between 2 Colors (a-b)
    static int[] colDiff(int[] a, int[] b){
        return new int[] {a[0]-b[0],a[1]-b[1],a[2]-b[2],255};
    }
    
    //Scales a color 'a' by factor 'b'
    static int[] colScale(int[] a, double b){
        return new int[] {(int)(a[0]*b),(int)(a[1]*b),(int)(a[2]*b),255};
    }
    
    //Clamps RGB components of a to 0-255
    static int colClamp(int a){
        return Math.min(Math.max(a, 0), 255);
    }
        //Full Color array version
    static int[] colClamp(int[] a){
        return new int[] {
        Math.min(Math.max(a[0], 0), 255),
        Math.min(Math.max(a[1], 0), 255),
        Math.min(Math.max(a[2], 0), 255),
        255};
    }
    
    //Returns the max R,G,B component from color a
    static int colMaxComp(int[] a){
        int r = a[0];
        int g = a[1];
        int b = a[2];
        return r > g ? (r > b ? r : b) : (g > b ? g : b);
    }
    
    //Returns the max R,G,B component from color a using a lookuptable 'tab'
    static int colMaxComp(int[] a, int[] tab){
        int r = tab[a[0]];
        int g = tab[a[1]];
        int b = tab[a[2]];
        return r > g ? (r > b ? r : b) : (g > b ? g : b);
    }
    
    //Applies a lookup table 'tab' to color 'c'
    static int[] applyLookupTable(int[] c, int[] tab){
        return new int[] {tab[c[0]],tab[c[1]],tab[c[2]],255};
    }
    
    //Constructs a lookup table with no mutations
    static int[] blankLookupTable(){
        int[] tmp = new int[256];
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = i;
        return tmp;
    }
    
    //Returns a new lookup table of size 256 using the given function
    //Intended to be utilized via lambda functions
    static int[] buildLookupTable(IntMutator f){
        int[] tmp = new int[256];
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = f.apply(i);
        return tmp;
    }
    
    //Applies the given function to an existing lookup table
    static int[] mutateLookupTable(int[] tab, IntMutator f){
        int[] tmp = new int[256];
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = f.apply(tab[i]);
        return tmp;
    }
    
    //Applies the second lookup table to the first lookup table
    static int[] combineLookupTable(int [] tab1, int[] tab2){
        int[] tmp = new int[256];
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = tab2[tab1[i]];
        return tmp;
    }
    
    //Clamps all values in a lookup table
    static int[] clampLookupTable(int[] tab){
        int[] tmp = new int[256];
        for(int i = 0; i < tmp.length; i++)
            tmp[i] = colClamp(tab[i]);
        return tmp;
    }
    
    //WILL OPTIMIZE FURTHER
    //Given a raster, floating point x and y coords, and x and y radii, calculate the average color
    static int[] avgColInArea(Raster r, double x, double y, double rx, double ry){
        ColorMixer cm = new ColorMixer(); //Used to average colors from multiple pixels
        
        //NOTES: (int)coord is the pixel that coord falls within (whether an x or y coord)
        
        int[] tmp = new int[4];
        
        int width = r.getWidth();
        int height = r.getHeight();
        
        //Top Left Corner
        double tlx = Math.max(x-rx, 0.0); //Absolute x pos of TL
        double tly = Math.max(y-ry, 0.0); //Absolute y pos of TL
        int tlxP = (int)tlx; //x Pixel of TL
        int tlyP = (int)tly; //y Pixel of TL
        
        //Bottom Right Corner
        double brx = Math.min(x+rx, width); //Absolute x pos of BR
        double bry = Math.min(y+ry, height); //Absolute y pos of BR (Instead of subtracting by epsilon, can reduce xP/yP by 1 if == width/height
        int brxP = (int)Math.ceil(brx)-1; //x Pixel of BR
        int bryP = (int)Math.ceil(bry)-1; //y Pixel of BR
        
        //Count of intermediary pixels
        int intx = brxP-tlxP-1;
        int inty = bryP-tlxP-1;
        
        //Horizontal and vertical amounts for 4 sides
        double amtLeft;
        double amtRight = decimal(brx);
        double amtTop;
        double amtBot = decimal(bry);
        
        //Correct for rounding errors
        if(amtRight==0.0)
            amtRight=1.0;
        
        if(amtBot==0.0)
            amtBot=1.0;
        
        //If same vertical pixel, don't differentiate left and right amts
        if(tlyP==bryP)
            amtLeft = amtRight;
        else
            amtLeft = 1.0-decimal(tlx);
        
        //If same horizontal pixel, don't differentiate top and bot amts
        if(tlxP==brxP)
            amtTop = amtBot;
        else
            amtTop = 1.0-decimal(tly);
        
        
        
        //Top Left
        cm.add(r.getPixel(tlxP, tlyP, tmp),amtTop*amtLeft);
        
        //Bottom Right (if Tl and BR are not in same pixel)
        if(tlxP!=brxP || tlyP!=bryP){
            cm.add(r.getPixel(brxP, bryP, tmp),amtBot*amtRight);
            
            //Top Right + Bottom Left (if TL and BR are in different x AND y coords)
            if(tlxP!=brxP && tlyP!=bryP){
                cm.add(r.getPixel(brxP, tlyP, tmp),amtTop*amtRight); //Top Right
                cm.add(r.getPixel(tlxP, bryP, tmp),amtBot*amtLeft); //Bottom Left
                
                //Middle (if intx > 0 AND inty > 0)
                if(intx>0 && inty>0){
                    for(int i = tlxP+1; i < brxP; i++){
                        for(int j = tlyP+1; j < bryP; j++){
                            cm.add(r.getPixel(i, j, tmp)); //Full value for pixels in the middle
                        }
                    }
                }
            }
            
            //Top (if intx > 0) (Bottom as well if tl and br also in diff y pixels)
            if(intx>0){
                if(tlyP==bryP){ //Top Only
                    for(int i = tlxP+1; i < brxP; i++)
                        cm.add(r.getPixel(i, tlyP, tmp),amtTop);
                }else{ //Top and Bottom
                    for(int i = tlxP+1; i < brxP; i++){
                        cm.add(r.getPixel(i, tlyP, tmp),amtTop);
                        cm.add(r.getPixel(i, bryP, tmp),amtBot);
                    }
                }
            }
        
            //Left (if inty > 0) (Right as well if tl and br also in diff x pixels)
            if(inty>0){
                if(tlxP==brxP){ //Left Only
                    for(int i = tlyP+1; i < bryP; i++)
                        cm.add(r.getPixel(tlxP, i, tmp),amtLeft);
                }else{ //Left and Right
                    for(int i = tlyP+1; i < bryP; i++){
                        cm.add(r.getPixel(tlxP, i, tmp),amtLeft);
                        cm.add(r.getPixel(brxP, i, tmp),amtRight);
                    }
                }
            }
        }
        return cm.resultArrInt();
    }
    
    //When given an int position, move the origin of the call to the center of that pixel
    static int[] avgColInArea(Raster r, int x, int y, double rx, double ry){
        return avgColInArea(r, (double)x+0.5, (double)y+0.5, rx, ry);
    }
}
