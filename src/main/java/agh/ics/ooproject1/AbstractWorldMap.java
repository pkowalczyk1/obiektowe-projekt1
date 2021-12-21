package agh.ics.ooproject1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class AbstractWorldMap implements IPositionChangeObserver{
    protected Map<Vector2d, List<Animal>> animals = new LinkedHashMap<>();
    protected List<Animal> animalsList = new ArrayList<>();
    protected Map<Vector2d, Grass> grassFields = new ConcurrentHashMap<>();
    protected List<Animal> toPlace = new CopyOnWriteArrayList<>();
    protected int grassEnergy;
    public int moveEnergy;
    protected int startEnergy;
    protected int width;
    protected int height;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;
    public int animalCount = 0;
    public int grassCount = 0;

    public List<Animal> animalsAt(Vector2d position) {
        return animals.get(position);
    }

    public Grass grassAt(Vector2d position) {
        return grassFields.get(position);
    }

    public boolean canMoveTo(Vector2d position) {
        return true;
    }

    public List<Animal> getToPlace() {
        return toPlace;
    }

    public Map<Vector2d, Grass> getGrassFields() {
        return grassFields;
    }

    public boolean isOccupied(Vector2d position) {
        return animalsAt(position) != null || grassAt(position) != null;
    }

    public void placeJungleGrass() {
        int jungleSize = (jungleUpperRight.x-jungleLowerLeft.x) * (jungleUpperRight.y-jungleLowerLeft.y);
        int tries = 0;
        boolean placed = false;
        while (tries < jungleSize && !placed) {
            int x = (int) (Math.random()*(jungleUpperRight.x-jungleLowerLeft.x + 1) + jungleLowerLeft.x);
            int y = (int) (Math.random()*(jungleUpperRight.y-jungleLowerLeft.y + 1) + jungleLowerLeft.y);
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
            int x = (int) (Math.random()*width);
            int y = (int) (Math.random()*height);
            if (!isOccupied(new Vector2d(x, y)) && !isJungle(new Vector2d(x, y))) {
                place(new Grass(new Vector2d(x, y)));
                placed = true;
            }
            tries++;
        }
    }

    public boolean isJungle(Vector2d position) {
        return jungleLowerLeft.precedes(position) && jungleUpperRight.follows(position);
    }

    public void place(IWorldMapElement element) {
        if (element instanceof Grass) {
            grassFields.put(element.getPosition(), (Grass) element);
            grassCount++;
        }
        else if (element instanceof Animal) {
            animalCount++;
            animalsList.add((Animal) element);
            if (animals.get(element.getPosition()) == null) {
                List<Animal> newList = new ArrayList<>();
                newList.add((Animal) element);
                animals.put(element.getPosition(), newList);
                toPlace.add((Animal) element);
            }
            else {
                List<Animal> list = animals.get(element.getPosition());
                list.add((Animal) element);
                list.sort(Comparator.comparingInt(Animal::getEnergy));
                animals.put(element.getPosition(), list);
                if (list.get(list.size() - 1) == element) {
                    toPlace.add((Animal) element);
                }
            }

            ((Animal) element).addObserver(this);
        }
    }

    public void remove(IWorldMapElement element) {
        if (element instanceof Grass) {
            grassFields.remove(element.getPosition());
            grassCount--;
        }
        else if (element instanceof Animal) {
            animalCount--;
            animalsList.remove(element);
            toPlace.remove(element);
            List<Animal> animalsAtPos = animals.get(element.getPosition());
            animalsAtPos.remove(element);
            if (animalsAtPos.isEmpty()) {
                animals.remove(element.getPosition());
            }
            else {
                animals.put(element.getPosition(), animalsAtPos);
                toPlace.add(animalsAtPos.get(animalsAtPos.size() - 1));
            }
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        List<Animal> list = animals.get(oldPosition);
        toPlace.remove(animal);
        if (list.indexOf(animal) == list.size() - 1 && list.size() != 1) {
            toPlace.add(list.get(list.size() - 2));
        }
        list.remove(animal);
        if (list.isEmpty()) {
            animals.remove(oldPosition);
        }
        else {
            animals.put(oldPosition, list);
        }

        list = this.animals.get(newPosition);
        if (list == null) {
            List<Animal> newList = new ArrayList<>();
            newList.add(animal);
            toPlace.add(animal);
            animals.put(newPosition, newList);
        }
        else {
            list.add(animal);
            list.sort(Comparator.comparingInt(Animal::getEnergy));
            if (list.get(list.size() - 1) == animal) {
                toPlace.add(animal);
            }
            animals.put(newPosition, list);
        }
    }

    public void moveAllAnimals() {
        for (Animal animal : animalsList) {
            animal.move();
        }
    }

    public void removeDeadAnimals() {
        List<Animal> toDeletion = new ArrayList<>();
        List<Animal> toDeletionToPlace = new ArrayList<>();
        for (Animal animal : animalsList) {
            if (animal.getEnergy() <= 0) {
                toDeletion.add(animal);
            }
        }
//        System.out.println(toPlace);

        for (Animal animal : toPlace) {
            if (animal.getEnergy() <= 0) {
                toDeletionToPlace.add(animal);
            }
        }

        for (Animal animal : toDeletion) {
            animal.removeObserver(this);
            remove(animal);
        }

        for (Animal animal : toDeletionToPlace) {
            toPlace.remove(animal);
        }
    }

    public void eat() {
        List<Grass> toDeletion = new ArrayList<>();
        for (Grass grass : grassFields.values()) {
            List<Animal> animalsAtThisPos = animals.get(grass.getPosition());
            if (animalsAtThisPos != null && animalsAtThisPos.size() == 1) {
                animalsAtThisPos.get(0).increaseEnergy(grassEnergy);
                toDeletion.add(grass);
            }
            else if (animalsAtThisPos != null && animalsAtThisPos.size() > 1) {
//                animalsAtThisPos.sort(Comparator.comparingInt(Animal::getEnergy));
                int maxEnergy = animalsAtThisPos.get(animalsAtThisPos.size() - 1).getEnergy();
                List<Animal> maxEnergyAnimals = animalsAtThisPos.stream().filter(o -> o.getEnergy() == maxEnergy).collect(Collectors.toList());
                for (Animal animal : maxEnergyAnimals) {
                    animal.increaseEnergy(grassEnergy / maxEnergyAnimals.size());
                }
//                animalsAtThisPos.sort(Comparator.comparingInt(Animal::getEnergy));
//                this.animals.put(grass.getPosition(), animalsAtThisPos);
                toDeletion.add(grass);
            }
        }

        for (Grass grass : toDeletion) {
            remove(grass);
        }
    }

    public void spawn() {
        List<Animal> toSpawn = new ArrayList<>();
        for (List<Animal> animalsAtThisPos : animals.values()) {
            if (animalsAtThisPos.size() > 1) {
//                animalsAtThisPos.sort(Comparator.comparingInt(Animal::getEnergy));
                Animal animal1 = animalsAtThisPos.get(animalsAtThisPos.size() - 1);
                Animal animal2 = animalsAtThisPos.get(animalsAtThisPos.size() - 2);
                if (animal1.getEnergy() >= 30 && animal2.getEnergy() >= 30) {
                    toSpawn.add(spawnNewAnimal(animal1, animal2));
                }
            }
        }

        for (Animal animal : toSpawn) {
            place(animal);
        }
    }

    private Animal spawnNewAnimal(Animal animal1, Animal animal2) {
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

        animal1.decreaseEnergy(energy1);
        animal2.decreaseEnergy(energy2);
        return new Animal(animal1.getPosition(), this, new Genome(newGenome), energy1 + energy2);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void growGrass() {
        placeNormalGrass();
        placeJungleGrass();
    }
}
