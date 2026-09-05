import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;
import java.awt.*;
import java.io.*;
/*TODO:
    -new system of generating and storing the gradient vectors
    -rework the printArray() method to be more generalized for any array of vectors
*/
public class noisemaps extends JPanel{
    BufferedImage img;
    int WIDTH=1024;
    int HEIGHT=1024;
    int chunkSize=16;
    vector[][] gradientVectors;
    vector[][] worleySeeds;
    Random random;
    long seed;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, 0, 0, null);
    }

    /*
    This is the constructor for the noisemap object, which sets the seed for the map, the random for the map, and the 2d array of gradient vectors
    generates each gradient vector by first generating a new random angle, then turning that angle into a 2d vector
    returns nothing 
    */
    public noisemaps(){
        vector[][] gradientVects = new vector[(this.WIDTH*4)][(this.HEIGHT*4)];
        this.seed=0;
        this.random = new Random(seed);
        for(int gradX=0; gradX<(this.WIDTH*4);gradX++){
            for(int gradY=0;gradY<(this.HEIGHT*4);gradY++){
                double angle=this.random.nextDouble()*2*Math.PI;
                vector newVector=(new vector(Math.cos(angle), Math.sin(angle)));
                gradientVects[gradX][gradY]=newVector;
            }
        }
        this.gradientVectors=gradientVects;
    }
    /*
    This is a helper method which will print out all of the gradient vectors using the printVector() method in vector.java
    returns nothing.
    */
    public void printVectorArray(vector[][] array){
        for(int x=0; x<(this.WIDTH*4);x++){
            for(int y=0;y<(this.HEIGHT*4);y++){
                array[x][y].printVector();
            }
        }
    }

    /*
    This method linearly interpolates between 2 scalars, a and b, and picks the point between them, x, to get the value at that x 
    returns a double
    */
    public double lerp(double a, double b, double x){
        return a+x*(b-a);
    }

    /*
    this is a method which takes in a coordinate between 0 and 1, and returns a new coordinate using 6x^5-15x^4+10x^3, specified by ken perlin
    returns a double
    */
    public double fade(double x){
        return 6*Math.pow(x,5)-15*Math.pow(x,4)+10*Math.pow(x,3);
    }

    /*
    This is the method which actually calculates the noise value at a specific (x,y)
    First finds the specific u,v coordinates by scaling x and y to a specific chunk(i.e. x is 0.5 into the specific chunk)
    then calculates which grad vector to use.
    next it finds the dot products of (u,v) and each of the corner gradient vectors
    Finally, linearly interpolates between each dot product to find the average, and returns this value
    returns the average as a double
    */
    public double perlinNoise(double x,double y, int chunkSize){
        double u=fade(((double)(x%chunkSize))/chunkSize);
        double v=fade(((double)(y%chunkSize))/chunkSize);
        int xGradGrid=(int)(x/chunkSize);
        int yGradGrid=(int)(y/chunkSize);
        double g1=this.gradientVectors[xGradGrid][yGradGrid].dot(new vector(u,v));
        double g2=this.gradientVectors[xGradGrid+1][yGradGrid].dot(new vector(u-1,v));
        double g3=this.gradientVectors[xGradGrid][yGradGrid+1].dot(new vector(u,v-1));
        double g4=this.gradientVectors[xGradGrid+1][yGradGrid+1].dot(new vector(u-1,v-1));

        double x1=lerp(g1,g2,u);
        double x2=lerp(g3,g4,u);
        double average=lerp(x1,x2,v);
        return (average+1)/2;
    }

    /*
    This is a method which calculates a bunch of perlin noise octaves and stacks them on top of eachother to produce smoother noise
    it does this by passing into the perlinNoise() method for each octave, multiplying the frequency and amplitude of each new pass.
    */
    public double perlin(int x, int y, int octaves, double persistence){
        double total=0.0;
        double frequency=1.0;
        double amplitude=1.0;
        double maxVal=0.0;
        for(int i=0;i<octaves;i++){
            total+=perlinNoise(x*frequency,y*frequency,128)*amplitude;
            maxVal+=amplitude;
            amplitude*=persistence;
            frequency*=2;
        }
        return total/maxVal;
    }

    /*
    This is a method which calculates the specific worley noise value at a specific x,y point. Basically, when needed, it generates a large grid of random vectors, which are used as the worley seeds
    then it takes the distance from the current x,y point and finds the closest seed. the entire grid of x,y is split into chunks, each of which contains one seed. We loop through a 3x3
    grid of chunks and find which seed the current x,y point is closest to. Finally we return the distance between x,y and the closest seed. This method also wraps around the map, so the produced
    noisemap is entirely continuous.
    */
    public double worleyNoise(int x, int y, int seedNum){
        int gridLength=(int)(Math.ceil(Math.sqrt(seedNum)));
        if(this.worleySeeds==null){
            vector[][] seedVector=new vector[this.WIDTH][this.HEIGHT];
            for(int i=0;i<this.WIDTH;i++){
                for(int j=0;j<this.HEIGHT;j++){
                    seedVector[i][j]=(new vector(this.random.nextDouble(this.WIDTH/gridLength)/(this.WIDTH/gridLength), this.random.nextDouble(this.HEIGHT/gridLength)/(this.WIDTH/gridLength)));
                }
            }
            this.worleySeeds=seedVector;
        }
        double u=(double)(x%(this.WIDTH/gridLength))/(this.WIDTH/gridLength);
        double v=(double)(y%(this.HEIGHT/gridLength))/(this.HEIGHT/gridLength);
        vector currentVector=new vector(u,v);
        int currentSeedGridCoordX=x/(this.WIDTH/gridLength);
        int currentSeedGridCoordY=y/(this.HEIGHT/gridLength);
        
        double distance=100000.0;
        int xGrid=0;
        int yGrid=0;
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
                xGrid=currentSeedGridCoordX+i;
                yGrid=currentSeedGridCoordY+j;
                if(currentSeedGridCoordX+i<0){
                    xGrid=gridLength-1;
                }
                if(currentSeedGridCoordX+i>gridLength-1){
                    xGrid=0;
                }
                if(currentSeedGridCoordY+j<0){
                    yGrid=gridLength-1;
                }
                if(currentSeedGridCoordY+j>gridLength-1){
                    yGrid=0;
                }
                if(new vector(currentVector.getX()-i,currentVector.getY()-j).getDistance(new vector(this.worleySeeds[xGrid][yGrid].getX(),this.worleySeeds[xGrid][yGrid].getY()))<distance){
                    distance=new vector(currentVector.getX()-i,currentVector.getY()-j).getDistance(new vector(this.worleySeeds[xGrid][yGrid].getX(),this.worleySeeds[xGrid][yGrid].getY()));
                }
            }
        }
        
        //System.out.println(distance);
        if(distance>1.0){
            distance=1.0;
        }

        return distance;
    }

    /*
    This method creates a bunch of worley noise maps and layers them over eachother, which is done by calling into the worleyNoise method an octaves number of times. 
    Each subsequent octave counts for less towards the final returned noise value.
    */
    public double worley(int x, int y, int octaves, double persistence, int seedNum){
        double total=0.0;
        int frequency=1;
        double maxValue=0.0;
        double amplitude=1.0;
        for(int i=0;i<octaves;i++){
            total+=worleyNoise(x,y,seedNum*frequency)*amplitude;
            frequency*=2;
            maxValue+=amplitude;
            amplitude*=persistence;
        }
        return total/maxValue;
    } 
    /*
    This generates a bufferedImage object of the actual noisemap by generating noise value between 0-255, and passing that value into each of the rgb channels.
    returns nothing 
    */
    public void generateMap(){
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int x =0;x<WIDTH;x++){
            for(int y=0;y<HEIGHT;y++){
                //double value=this.perlin(x,y,8,0.6)*255;
                double worley=this.worley(x, y, 2,0.2,25);
                //double worley=this.worleyNoise(x, y, 16);
                double perlin=this.perlin(x,y,10,0.6);
                double value=((worley))*255;
                int a=255;
                int r=(int)value;
                int g=(int)value;
                int b=(int)value;
                int rgbValue = (a << 24) | (r << 16) | (g << 8) | b;
                newImage.setRGB(x, y, rgbValue);
            }
        }
        this.img=newImage;
    }

    public void display(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        JFrame frame = new JFrame("BufferedImage Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            noisemaps map=new noisemaps();
            map.generateMap();
            //map.printVectorArray(map.gradientVectors);
            map.display();
        });
    }
    
}