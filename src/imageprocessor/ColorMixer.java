package imageprocessor;

import java.awt.Color;

//Used to average colors with dynamic weights
public class ColorMixer{
    double rsum = 0.0;
    double gsum = 0.0;
    double bsum = 0.0;
    double count = 0.0;

    public void add(Color c){
        rsum+=c.getRed();
        gsum+=c.getGreen();
        bsum+=c.getBlue();
        count+=1.0;
    }

    public void add(Color c, double mul){
        rsum+=c.getRed()*mul;
        gsum+=c.getGreen()*mul;
        bsum+=c.getBlue()*mul;
        count+=mul;
    }

    public void add(int[] c){
        rsum+=c[0];
        gsum+=c[1];
        bsum+=c[2];
        count+=1.0;
    }

    public void add(int[]  c, double mul){
        rsum+=c[0]*mul;
        gsum+=c[1]*mul;
        bsum+=c[2]*mul;
        count+=mul;
    }

    public void reset(){
        rsum = 0.0;
        gsum = 0.0;
        bsum = 0.0;
        count = 0.0;
    }

    public Color result(){
        return new Color((int)(rsum/count),(int)(gsum/count),(int)(bsum/count),255);
    }

    public int[] resultArrInt(){
        return new int[] {(int)(rsum/count),(int)(gsum/count),(int)(bsum/count),255};
    }
}