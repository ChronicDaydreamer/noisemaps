public class Plate {
    vector coordinates;
    vector direction;
    int type;
    vector chunkCoords;
    public Plate(vector coordinates, vector direction, int plateType, vector chunckCoords){
        this.coordinates=coordinates;
        this.direction=direction;
        this.type=plateType;
        this.chunkCoords=chunkCoords;
    }
    public vector getCoordinates(){
        return this.coordinates;
    }
    public vector getDirection(){
        return this.direction;
    }
    public int getType(){
        return this.type;
    }
    public void printPlate(){
        System.out.println("The current plate has coordinates: ("+this.coordinates.getX()+", "+this.coordinates.getY()+")\nThe current plate has direction: ("+this.direction.getX()+", "+this.direction.getY()+")");
    }
    public double platePressureAtPoint(vector point, Plate otherPlate){
        //otherPlate.printPlate();
        vector newCurrentCoords=this.coordinates.addVector(this.direction);
        //System.out.println(newCurrentCoords.getX());
        vector newOtherPlateCoords=otherPlate.getCoordinates().addVector(otherPlate.getDirection());
        double pressure=newCurrentCoords.getDistance(newOtherPlateCoords)-this.coordinates.getDistance(otherPlate.getCoordinates());
        return pressure;
    }
}
