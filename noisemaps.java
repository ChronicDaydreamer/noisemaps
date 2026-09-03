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
                vector newVector=(new vector(random.nextDouble(2)-1,random.nextDouble(2)-1)).normalize();
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
    public double noise(int x,int y){
        double u=((double)(x%this.chunkSize))/this.chunkSize;
        double v=((double)(y%this.chunkSize))/this.chunkSize;

        vector currentCoords=new vector(u,v).normalize();
        int xGradGrid=x/this.chunkSize;
        int yGradGrid=y/this.chunkSize;
        double g1=this.gradientVectors[xGradGrid][yGradGrid].dot(currentCoords);
        double g2=this.gradientVectors[xGradGrid+1][yGradGrid].dot(currentCoords);
        double g3=this.gradientVectors[xGradGrid][yGradGrid+1].dot(currentCoords);
        double g4=this.gradientVectors[xGradGrid+1][yGradGrid+1].dot(currentCoords);

        double x1=lerp(g1,g2,u);
        double x2=lerp(g3,g4,u);
        double average=lerp(x1,x2,v);

        /*System.out.println("new SYstem");
        //System.out.println(this.gradientVectors[xGradGrid][yGradGrid].getX());
        //System.out.println(this.gradientVectors[xGradGrid][yGradGrid].getY());
        //System.out.println(this.gradientVectors[xGradGrid+1][yGradGrid].getX());
        //System.out.println(this.gradientVectors[xGradGrid+1][yGradGrid].getY());
        //System.out.println(this.gradientVectors[xGradGrid][yGradGrid+1].getX());
        //System.out.println(this.gradientVectors[xGradGrid][yGradGrid+1].getY());
        System.out.println(this.gradientVectors[xGradGrid+1][yGradGrid+1].getX());
        System.out.println(this.gradientVectors[xGradGrid+1][yGradGrid+1].getY());
        System.out.println(this.gradientVectors[xGradGrid+1][yGradGrid+1].getMagnitude());
        System.out.println(u);
        System.out.println(v);
        System.out.println(xGradGrid);
        System.out.println(yGradGrid);
        System.out.println(x1);
        System.out.println(x2);
        System.out.println(g1);
        System.out.println(g2);
        System.out.println(g3);
        System.out.println(g4);
        System.out.println(average);*/
        //System.out.println(average);
        return (average+1)/2;
    }

    public void generateMap(){
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int x =0;x<WIDTH;x++){
            for(int y=0;y<HEIGHT;y++){
                //Random random=new Random();
                double value=this.noise(x,y)*255;
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