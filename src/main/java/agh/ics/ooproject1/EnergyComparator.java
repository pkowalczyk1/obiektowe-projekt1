package agh.ics.ooproject1;

import java.util.Comparator;

public class EnergyComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal animal1, Animal animal2) {
        if (animal1.getEnergy() == animal2.getEnergy()) {
            if (animal1.equals(animal2)) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            return animal1.getEnergy() - animal2.getEnergy();
        }
    }
}
