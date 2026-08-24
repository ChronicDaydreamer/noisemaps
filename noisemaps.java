import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.io.*;
public class noisemaps extends JPanel{
    BufferedImage img;
    int WIDTH=1000;
    int HEIGHT=1000;
    int chunkSize=16;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, 0, 0, null);
    }

    public int noise(int x,int y){
        vector[][] gradientVects = new vector[this.WIDTH/this.chunkSize][this.HEIGHT/this.chunkSize];
        Random random = new Random();
        for(int gradX=0; gradX<this.WIDTH/this.chunkSize;gradX++){
            for(int gradY=0;gradY<this.HEIGHT/this.chunkSize;gradY++){
                gradientVects[gradX][gradY]=new vector(random.nextDouble(),random.nextDouble());
            }
        }
        return 1;
    }

    public void generateMap(){
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int x =0;x<WIDTH;x++){
            for(int y=0;y<HEIGHT;y++){
                Random random=new Random();
                int value=this.noise(x,y);
                int a=255;
                int r=value;
                int g=value;
                int b=value;
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
            map.display();
        });
    }
    
}