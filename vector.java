public class vector {
    double[] vals;

    public vector(double... args) {
        this.vals=args;
    }
    public double dot(vector vector1, vector vector2){
        if(vector1.vals.length!=vector2.vals.length){
            System.out.println("These vectors are not the same size");
            return 0.0;
        }
        double dotProd=0;
        for(int x=0;x<vector1.vals.length;x++){
            dotProd+=vector1.vals[x]*vector2.vals[x];
        }
        return dotProd;
    }
}
