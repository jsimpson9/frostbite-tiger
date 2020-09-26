package com.frostytiger;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

//
// Taken from:
//
// http://javagamexyz.blogspot.com/2013/03/terrain-generation.html
//
// Modified to not depend on gdx Random / MathUtils.
// Modified to expose parameters to callers.
//
//
public class MidpointDisplacement {

    public int n;
    public int wmult, hmult;
  
    private float _smoothness;

    // private float[] _thresholds;

    static public Random random = ThreadLocalRandom.current();

    private static long random(long start, long end) {
        return start + (long)(random.nextDouble() * (end - start));
    }

    private static float random(float range) {
        return random.nextFloat() * range;
    }

    private static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    public MidpointDisplacement(int detail, 
                                int widthMult, 
                                int heightMult,
                                float smoothness ) {

        //
        // n partly controls the size of the map, 
        // but mostly controls the level of detail available
        // n = 7;

        n = detail;

        //
        // wmult and hmult are the width and height multipliers.  
        // They set how separate regions there are
        // wmult=6;
        // hmult=4;

        wmult = widthMult;
        hmult = heightMult;

        //
        // Smoothness controls how smooth the resultant terain is.  Higher = more smooth
        //
        // smoothness = 2f;
        _smoothness = smoothness;


        // _thresholds = thresholds;

    }
 
    private int width;
    private int height;

    public int getWidth()   { return width; }
    public int getHeight()  { return height; }

    public int[][] getMap(float[] thresholds) {
   
        // get the dimensions of the map
        // int power = MyMath.pow(2,n);     // orig
        // int power = 2 << (n-1);
        int power = (int)Math.pow(2,n);

        width = wmult*power + 1;
        height = hmult*power + 1;
  
        // System.out.println(
        //    "INFO Map Generation: width " + width + " height " + height);

        // initialize arrays to hold values 
        float[][] map = new float[width][height];
        int[][] returnMap = new int[width][height];
   
   
        // int step = power/2;
        int step = power/2;

        float sum;
        int count;
   
        // h determines the fineness of the scale it is working on.  After every step, h
        // is decreased by a factor of "_smoothness"
        float h = 1;
   
        // Initialize the grid points

        for (int i=0; i<width; i+=2*step) {
            for (int j=0; j<height; j+=2*step) {
                map[i][j] = random(2*h);
            }
        }
 
        // Do the rest of the magic

        while (step > 0) {   

            // Diamond step

            for (int x = step; x < width; x+=2*step) {
                for (int y = step; y < height; y+=2*step) {
                    sum = map[x-step][y-step] + //down-left
                    map[x-step][y+step] + //up-left
                    map[x+step][y-step] + //down-right
                    map[x+step][y+step];  //up-right
                    map[x][y] = sum/4 + random(-h,h);
                }
            }
    
            // Square step

            for (int x = 0; x < width; x+=step) {
                for (int y = step*(1-(x/step)%2); y<height; y+=2*step) {
                    sum = 0;
                    count = 0;
                    if (x-step >= 0) {
                        sum+=map[x-step][y];
                        count++;
                    }
                    if (x+step < width) {
                        sum+=map[x+step][y];
                        count++;
                    }
                    if (y-step >= 0) {
                        sum+=map[x][y-step];
                        count++;
                    }
                    if (y+step < height) {
                        sum+=map[x][y+step];
                        count++;
                    }
                    if (count > 0) map[x][y] = sum/count + random(-h,h);
                    else map[x][y] = 0;
                }
     
            }
            h /= _smoothness;
            step /= 2;
        }
   
        // Normalize the map

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (float[] row : map) {
            for (float d : row) {
                if (d > max) max = d;
                if (d < min) min = d;
            }
        }
   
        // Use the thresholds to fill in the return map

        for(int row = 0; row < map.length; row++){
            for(int col = 0; col < map[row].length; col++){
                map[row][col] = (map[row][col]-min)/(max-min);

                for(int i = thresholds.length - 1; i > -1; i--) {
                    if(map[row][col] < thresholds[i]) {
                        returnMap[row][col] = i;
                    }
                }

            }
        }
 
        return returnMap;
    }

}
