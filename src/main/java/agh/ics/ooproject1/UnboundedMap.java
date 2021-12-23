package agh.ics.ooproject1;

public class UnboundedMap extends AbstractWorldMap {
    public UnboundedMap(int width, int height, double jungleRatio, int grassEnergy, int moveEnergy, int startEnergy, boolean isMagic) {
        this.width = width;
        this.height = height;
        this.jungleLowerLeft = new Vector2d((int)(width * (1-jungleRatio)/2), (int)(height * (1-jungleRatio)/2));
        this.jungleUpperRight = new Vector2d((int)(width * (1+jungleRatio)/2), (int)(height * (1+jungleRatio)/2));
        this.grassEnergy = grassEnergy;
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.isMagic = isMagic;
    }
}
