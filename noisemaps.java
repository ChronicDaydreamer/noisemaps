import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.io.*;
public class noisemaps extends JPanel{
    BufferedImage img;
    int WIDTH=1024;
    int HEIGHT=1024;
    int chunkSize=64;
    vector[][] gradientVectors;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, 0, 0, null);
    }
    public noisemaps(){
        vector[][] gradientVects = new vector[(this.WIDTH/this.chunkSize)+2][(this.HEIGHT/this.chunkSize)+2];
        long seed=0;
        Random random = new Random(seed);
        for(int gradX=0; gradX<(this.WIDTH/this.chunkSize)+2;gradX++){
            for(int gradY=0;gradY<(this.HEIGHT/this.chunkSize)+2;gradY++){
                double angle=random.nextDouble()*2*Math.PI;
                vector newVector=(new vector(Math.cos(angle), Math.sin(angle)));
                gradientVects[gradX][gradY]=newVector;
            }
        }
        this.gradientVectors=gradientVects;
    }

    public void printVectorArray(vector[][] array){
        for(int x=0; x<(this.WIDTH/this.chunkSize)+2;x++){
            for(int y=0;y<(this.HEIGHT/this.chunkSize)+2;y++){
                array[x][y].printVector();
            }
        }
    }

    public double lerp(double a, double b, double x){
        return a+x*(b-a);
    }
    public double fade(double x){
        return 6*Math.pow(x,5)-15*Math.pow(x,4)+10*Math.pow(x,3);
    }
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

    public double perlin(int x, int y, int octaves, double persistence){
        double total=0.0;
        double frequency=1.0;
        double amplitude=1.0;
        double maxVal=0.0;
        for(int i=0;i<octaves;i++){
            total+=perlinNoise(x*frequency,y*frequency,32)*amplitude;
            maxVal+=amplitude;
            amplitude*=persistence;
            frequency*=2;
        }
        return total/maxVal;
    }
    public void generateMap(){
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int x =0;x<WIDTH;x++){
            for(int y=0;y<HEIGHT;y++){
                //Random random=new Random();
                double value=this.perlin(x,y,8,0.5)*255;
                //System.out.println(value);
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