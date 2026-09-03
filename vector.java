public class vector {
    double[] vals;

    public vector(double... args) {
        this.vals=args;
    }
    public double dot(vector vector2){
        if(this.vals.length!=vector2.vals.length){
            System.out.println("These vectors are not the same size");
            return 0.0;
        }
        double dotProd=0;
        for(int x=0;x<this.vals.length;x++){
            dotProd+=this.vals[x]*vector2.vals[x];
        }
        return dotProd;
    }
    public void printVector(){
        for(int x =0;x<this.vals.length;x++){
            System.out.println(this.vals[x]);
        }
    }
    public double getX(){
        return this.vals[0];
    }
    public double getY(){
        return this.vals[1];
    }
    public double getMagnitude(){
        double mag=0.0;
        for(int x=0;x<this.vals.length;x++){
            mag+=Math.pow(this.vals[x],2);
        }
        return Math.sqrt(mag);
    }
    public vector normalize(){
        double mag=this.getMagnitude();
        for(int x =0; x<this.vals.length;x++){
            this.vals[x]=this.vals[x]/mag;
        }
        return this;
    }
    public static void main(String args[]){
        vector vector=new vector(0.6517509285982154,0.758433073561118);
        System.out.println(vector.getMagnitude());
        vector=vector.normalize();
        System.out.println(vector.getMagnitude());
        System.out.println(vector.getX());
        System.out.println(vector.getY());
    }
}
