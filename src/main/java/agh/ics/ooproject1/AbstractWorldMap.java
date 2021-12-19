package agh.ics.ooproject1;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class AbstractWorldMap implements IPositionChangeObserver{
    protected Map<Vector2d, List<Animal>> animals = new LinkedHashMap<>();
    protected List<Animal> animalsList = new ArrayList<>();
    protected Map<Vector2d, Grass> grassFields = new LinkedHashMap<>();
    protected int grassEnergy;
    public int moveEnergy;
    protected int startEnergy;
    protected int width;
    protected int height;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;

    public List<Animal> animalsAt(Vector2d position) {
        return animals.get(position);
    }

    public Grass grassAt(Vector2d position) {
        return grassFields.get(position);
    }

    public boolean isOccupied(Vector2d position) {
        return animalsAt(position) != null || grassAt(position) != null;
    }

    public void placeJungleGrass() {
        int jungleSize = (this.jungleUpperRight.x-this.jungleLowerLeft.x) * (this.jungleUpperRight.y-this.jungleLowerLeft.y);
        int tries = 0;
        boolean placed = false;
        while (tries < jungleSize && !placed) {
            int x = (int) (Math.random()*(this.jungleUpperRight.x-this.jungleLowerLeft.x + 1) + this.jungleLowerLeft.x);
            int y = (int) (Math.random()*(this.jungleUpperRight.y-this.jungleLowerLeft.y + 1) + this.jungleLowerLeft.y);
            if (!isOccupied(new Vector2d(x, y))) {
                place(new Grass(new Vector2d(x, y)));
                placed = true;
            }
            tries++;
        }
    }

    public void placeNormalGrass() {
        int mapSize = width * height;
        int tries = 0;
        boolean placed = false;
        while (tries < mapSize && !placed) {
            int x = (int) (Math.random()*this.width);
            int y = (int) (Math.random()*this.height);
            if (!isOccupied(new Vector2d(x, y)) && !isJungle(new Vector2d(x, y))) {
                place(new Grass(new Vector2d(x, y)));
                placed = true;
            }
            tries++;
        }
    }

    public boolean isJungle(Vector2d position) {
        return this.jungleLowerLeft.precedes(position) && this.jungleUpperRight.follows(position);
    }

    public void place(IWorldMapElement element) {
        if (element instanceof Grass) {
            this.grassFields.put(element.getPosition(), (Grass) element);
        }
        else if (element instanceof Animal) {
            this.animalsList.add((Animal) element);
            if (this.animals.get(element.getPosition()) == null) {
                List<Animal> newList = new ArrayList<>();
                newList.add((Animal) element);
                this.animals.put(element.getPosition(), newList);
            }
            else {
                List<Animal> list = this.animals.get(element.getPosition());
                list.add((Animal) element);
                this.animals.put(element.getPosition(), list);
            }

            ((Animal) element).addObserver(this);
        }
    }

    public void remove(IWorldMapElement element) {
        if (element instanceof Grass) {
            this.grassFields.remove(element.getPosition());
        }
        else if (element instanceof Animal) {
            this.animalsList.remove(element);
            List<Animal> animalsAtPos = this.animals.get(element.getPosition());
            animalsAtPos.remove(element);
            if (animalsAtPos.isEmpty()) {
                this.animals.remove(element.getPosition());
            }
            else {
                this.animals.put(element.getPosition(), animalsAtPos);
            }
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        List<Animal> list = this.animals.get(oldPosition);
        list.remove(animal);
        if (list.isEmpty()) {
            this.animals.remove(oldPosition);
        }
        else {
            this.animals.put(oldPosition, list);
        }

        list = this.animals.get(newPosition);
        if (list == null) {
            List<Animal> newList = new ArrayList<>();
            newList.add(animal);
            this.animals.put(animal.getPosition(), newList);
        }
        else {
            list.add(animal);
            this.animals.put(newPosition, list);
        }
    }

    public void moveAllAnimals() {
        for (Animal animal : this.animalsList) {
            animal.move();
        }
    }

    public void removeDeadAnimals() {
        List<Animal> toDeletion = new ArrayList<>();
        for (Animal animal : this.animalsList) {
            if (animal.getEnergy() <= 0) {
                toDeletion.add(animal);
            }
        }

        for (Animal animal : toDeletion) {
            animal.removeObserver(this);
            remove(animal);
        }
    }

    public void eat() {
        List<Grass> toDeletion = new ArrayList<>();
        for (Grass grass : this.grassFields.values()) {
            List<Animal> animalsAtThisPos = this.animals.get(grass.getPosition());
            if (animalsAtThisPos != null && animalsAtThisPos.size() == 1) {
                animalsAtThisPos.get(0).increaseEnergy(this.grassEnergy);
                toDeletion.add(grass);
            }
            else if (animalsAtThisPos != null && animalsAtThisPos.size() > 1) {
                animalsAtThisPos.sort(Comparator.comparingInt(Animal::getEnergy));
                int maxEnergy = animalsAtThisPos.get(animalsAtThisPos.size() - 1).getEnergy();
                animalsAtThisPos = animalsAtThisPos.stream().filter(o -> o.getEnergy() == maxEnergy).collect(Collectors.toList());
                for (Animal animal : animalsAtThisPos) {
                    animal.increaseEnergy(this.grassEnergy / animalsAtThisPos.size());
                }
                toDeletion.add(grass);
            }
        }

        for (Grass grass : toDeletion) {
            remove(grass);
        }
    }

    public void spawn() {
        for (List<Animal> animalsAtThisPos : this.animals.values()) {
            if (animalsAtThisPos.size() > 1) {
                animalsAtThisPos.sort(Comparator.comparingInt(Animal::getEnergy));
                Animal animal1 = animalsAtThisPos.get(animalsAtThisPos.size() - 1);
                Animal animal2 = animalsAtThisPos.get(animalsAtThisPos.size() - 2);
                if (animal1.getEnergy() >= this.startEnergy * 0.5 && animal2.getEnergy() >= this.startEnergy * 0.5) {
                    spawnNewAnimal(animal1, animal2);
                }
            }
        }
    }

    private void spawnNewAnimal(Animal animal1, Animal animal2) {
        int energy1 = animal1.getEnergy() / 4;
        int energy2 = animal2.getEnergy() / 4;
        int sliceIndex = (int) ((double) animal1.getEnergy() / (animal1.getEnergy() + animal2.getEnergy()) * 32);
        int check = (int) (Math.random() * 2);
        List<Integer> newGenome;
        List<Integer> genomePart1;
        List<Integer> genomePart2;

        if (check == 1) {
            genomePart1 = animal1.getGenome().subList(0, sliceIndex + 1);
            genomePart2 = animal2.getGenome().subList(sliceIndex + 1, animal2.getGenome().size());
        }
        else {
            sliceIndex = 32 - sliceIndex;
            genomePart1 = animal2.getGenome().subList(0, sliceIndex + 1);
            genomePart2 = animal1.getGenome().subList(sliceIndex + 1, animal1.getGenome().size());
        }
        newGenome = Stream.concat(genomePart1.stream(), genomePart2.stream()).collect(Collectors.toList());

        place(new Animal(animal1.getPosition(), this, new Genome(newGenome), energy1 + energy2));
        animal1.decreaseEnergy(energy1);
        animal2.decreaseEnergy(energy2);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void growGrass() {
        placeNormalGrass();
        placeJungleGrass();
    }
}
