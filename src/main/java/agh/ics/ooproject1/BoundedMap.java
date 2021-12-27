package agh.ics.ooproject1;

public class BoundedMap extends AbstractWorldMap {
    public BoundedMap(int width, int height, double jungleRatio, int grassEnergy, int moveEnergy, int startEnergy, boolean isMagic) {
        this.width = width;
        this.height = height;
        jungleRatio = Math.sqrt(jungleRatio);
        this.jungleLowerLeft = new Vector2d((int)(width * (1-jungleRatio)/2), (int)(height * (1-jungleRatio)/2));
        this.jungleUpperRight = new Vector2d((int)(width * (1+jungleRatio)/2), (int)(height * (1+jungleRatio)/2));
        this.grassEnergy = grassEnergy;
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.isMagic = isMagic;
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(new Vector2d(width - 1, height - 1)) && position.follows(new Vector2d(0, 0));
    }
}
