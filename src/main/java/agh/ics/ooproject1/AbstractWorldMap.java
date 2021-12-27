package agh.ics.ooproject1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class AbstractWorldMap implements IPositionChangeObserver{
    //lists and maps for animals and grass
    protected Map<Vector2d, List<Animal>> animals = new LinkedHashMap<>();
    protected List<Animal> animalsList = new ArrayList<>();
    protected Map<Vector2d, Grass> grassFields = new ConcurrentHashMap<>();
    protected List<Animal> toPlace = new CopyOnWriteArrayList<>();

    //map parameters
    protected int grassEnergy;
    public int moveEnergy;
    protected int startEnergy;
    protected int width;
    protected int height;
    public boolean isMagic;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;

    //statistics
    public int epoch = 0;
    public int animalCount = 0;
    public int grassCount = 0;
    public double energyAvg = startEnergy;
    public double childrenAvg = 0;
    public double lifeSpanAvg = 0;
    public int deadAnimalsCount = 0;
    protected Map<Genome, Integer> genomesCount = new LinkedHashMap<>();
    private final List<String> statisticsHist = new ArrayList<>();
    private final List<Double> statisticsAverage = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0);
    public Genome mostCommonGenome;

    //variables for tracking animal
    public Animal selectedAnimal = null;
    public int trackStartTime = -1;

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

    public List<Animal> getAnimalsList() {
        return animalsList;
    }

    public boolean isOccupied(Vector2d position) {
        return animalsAt(position) != null || grassAt(position) != null;
    }

    public Grass placeJungleGrass() {
        int jungleSize = (jungleUpperRight.x-jungleLowerLeft.x) * (jungleUpperRight.y-jungleLowerLeft.y);
        int tries = 0;
        boolean placed = false;
        Grass newGrass = null;
        //try random position for new grass (jungleSize) times, if not found, don't place since
        //jungle is almost full
        while (tries < jungleSize && !placed) {
            int x = (int) (Math.random()*(jungleUpperRight.x-jungleLowerLeft.x + 1) + jungleLowerLeft.x);
            int y = (int) (Math.random()*(jungleUpperRight.y-jungleLowerLeft.y + 1) + jungleLowerLeft.y);
            if (!isOccupied(new Vector2d(x, y))) {
                newGrass = new Grass(new Vector2d(x, y));
                placeGrass(newGrass);
                placed = true;
            }
            tries++;
        }

        return newGrass;
    }

    public Grass placeNormalGrass() {
        int mapSize = width * height;
        int tries = 0;
        boolean placed = false;
        Grass newGrass = null;
        //try random position for new grass (width*height) times, if not found, don't place grass
        //since map is almost full
        while (tries < mapSize && !placed) {
            int x = (int) (Math.random()*width);
            int y = (int) (Math.random()*height);
            if (!isOccupied(new Vector2d(x, y)) && !isJungle(new Vector2d(x, y))) {
                newGrass = new Grass(new Vector2d(x, y));
                placeGrass(newGrass);
                placed = true;
            }
            tries++;
        }

        return newGrass;
    }

    public boolean isJungle(Vector2d position) {
        return jungleLowerLeft.precedes(position) && jungleUpperRight.follows(position);
    }

    public void placeGrass(Grass grass) {
        grassFields.put(grass.getPosition(), grass);
        grassCount++;
    }

    public void placeAnimal(Animal animal) {
        animalCount++;
        if (genomesCount.get(animal.getGenome()) == null) {
            genomesCount.put(animal.getGenome(), 1);
        }
        else {
            int count = genomesCount.get(animal.getGenome());
            genomesCount.put(animal.getGenome(), count + 1);
        }

        animalsList.add(animal);
        if (animals.get(animal.getPosition()) == null) {
            List<Animal> newList = new ArrayList<>();
            newList.add(animal);
            animals.put(animal.getPosition(), newList);
            toPlace.add(animal);
        }
        else {
            List<Animal> list = animals.get(animal.getPosition());
            list.add(animal);
            list.sort(Comparator.comparingInt(Animal::getEnergy));
            animals.put(animal.getPosition(), list);
            if (list.get(list.size() - 1) == animal) {
                toPlace.add(animal);
            }
        }

        animal.addObserver(this);
    }

    public void removeGrass(Grass grass) {
        grassFields.remove(grass.getPosition());
        grassCount--;
    }

    public void removeAnimal(Animal animal) {
        animalCount--;
        int count = genomesCount.get(animal.getGenome());
        if (count == 1) {
            genomesCount.remove(animal.getGenome());
        }
        else {
            genomesCount.put(animal.getGenome(), count - 1);
        }
        animalsList.remove(animal);
        toPlace.remove(animal);
        List<Animal> animalsAtPos = animals.get(animal.getPosition());
        animalsAtPos.remove(animal);
        if (animalsAtPos.isEmpty()) {
            animals.remove(animal.getPosition());
        }
        else {
            animals.put(animal.getPosition(), animalsAtPos);
            toPlace.add(animalsAtPos.get(animalsAtPos.size() - 1));
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        //remove from old position
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

        //add to new position
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

    public void energyChanged(Animal animal) {
        List<Animal> list = animals.get(animal.getPosition());
        list.sort(Comparator.comparingInt(Animal::getEnergy));
        animals.put(animal.getPosition(), list);
    }

    public void moveAllAnimals() {
        for (Animal animal : animalsList) {
            animal.move(animal.getGenome().randomMove());
        }
    }

    public void removeDeadAnimals() {
        List<Animal> toDeletion = new ArrayList<>();
        List<Animal> toDeletionToPlace = new ArrayList<>();
        for (Animal animal : animalsList) {
            if (animal.getEnergy() <= 0) {
                lifeSpanAvg = (lifeSpanAvg*deadAnimalsCount + epoch - animal.getEpochBorn()) / (deadAnimalsCount+1);
                deadAnimalsCount++;
                animal.setEpochDied(epoch);
                toDeletion.add(animal);
            }
        }

        for (Animal animal : toPlace) {
            if (animal.getEnergy() <= 0) {
                toDeletionToPlace.add(animal);
            }
        }

        for (Animal animal : toDeletion) {
            animal.removeObserver(this);
            removeAnimal(animal);
        }

        for (Animal animal : toDeletionToPlace) {
            toPlace.remove(animal);
        }
    }

    //returns list of grass to remove from gui
    public List<Grass> eat() {
        List<Grass> toDeletion = new ArrayList<>();
        for (Grass grass : grassFields.values()) {
            List<Animal> animalsAtThisPos = animals.get(grass.getPosition());
            if (animalsAtThisPos != null && animalsAtThisPos.size() == 1) {
                animalsAtThisPos.get(0).increaseEnergy(grassEnergy);
                toDeletion.add(grass);
            }
            else if (animalsAtThisPos != null && animalsAtThisPos.size() > 1) {
                int maxEnergy = animalsAtThisPos.get(animalsAtThisPos.size() - 1).getEnergy();
                List<Animal> maxEnergyAnimals = animalsAtThisPos
                        .stream()
                        .filter(o -> o.getEnergy() == maxEnergy)
                        .collect(Collectors.toList());
                for (Animal animal : maxEnergyAnimals) {
                    animal.increaseEnergy(grassEnergy / maxEnergyAnimals.size());
                }
                toDeletion.add(grass);
            }
        }

        for (Grass grass : toDeletion) {
            removeGrass(grass);
        }

        return toDeletion;
    }

    public void spawn() {
        List<Animal> toSpawn = new ArrayList<>();
        for (List<Animal> animalsAtThisPos : animals.values()) {
            if (animalsAtThisPos.size() > 1) {
                Animal animal1 = animalsAtThisPos.get(animalsAtThisPos.size() - 1);
                Animal animal2 = animalsAtThisPos.get(animalsAtThisPos.size() - 2);
                if (animal1.getEnergy() >= startEnergy / 2 && animal2.getEnergy() >= startEnergy / 2) {
                    toSpawn.add(spawnNewAnimal(animal1, animal2));
                    animal1.incrementChildren();
                    animal2.incrementChildren();
                }
            }
        }

        for (Animal animal : toSpawn) {
            placeAnimal(animal);
        }
    }

    public Animal spawnNewAnimal(Animal animal1, Animal animal2) {
        int energy1 = animal1.getEnergy() / 4;
        int energy2 = animal2.getEnergy() / 4;
        int sliceIndex = (int) ((double) animal1.getEnergy() / (animal1.getEnergy() + animal2.getEnergy()) * 32);
        int check = (int) (Math.random() * 2);
        List<Integer> newGenome;
        List<Integer> genomePart1;
        List<Integer> genomePart2;

        if (check == 1) {
            genomePart1 = animal1.getGenome().getGenes().subList(0, sliceIndex + 1);
            genomePart2 = animal2.getGenome().getGenes().subList(sliceIndex + 1, animal2.getGenome().getGenes().size());
        }
        else {
            sliceIndex = 32 - sliceIndex;
            genomePart1 = animal2.getGenome().getGenes().subList(0, sliceIndex + 1);
            genomePart2 = animal1.getGenome().getGenes().subList(sliceIndex + 1, animal1.getGenome().getGenes().size());
        }
        newGenome = Stream.concat(genomePart1.stream(), genomePart2.stream()).collect(Collectors.toList());
        Collections.sort(newGenome);

        animal1.decreaseEnergy(energy1);
        animal2.decreaseEnergy(energy2);
        Animal newAnimal = new Animal(animal1.getPosition(), this, new Genome(newGenome), energy1 + energy2, epoch);

        //increment selected animal descendant counter and mark new animal as selected descendant
        if (selectedAnimal != null && ((animal1.isSelectedDescendant && animal1.getEpochBorn() > trackStartTime)
                || (animal2.isSelectedDescendant && animal2.getEpochBorn() > trackStartTime))) {
            selectedAnimal.incrementDescendants();
            newAnimal.isSelectedDescendant = true;
        }

        //increment selected animal children and descendant counter and mark new animal as selected descendant
        if (animal1 == selectedAnimal || animal2 == selectedAnimal) {
            selectedAnimal.incrementDescendants();
            selectedAnimal.incrementTrackedChildren();
            newAnimal.isSelectedDescendant = true;
        }

        return newAnimal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    //returns list of new grass to show in gui
    public List<Grass> growGrass() {
        Grass grass1 = placeNormalGrass();
        Grass grass2 = placeJungleGrass();
        List<Grass> toPlace = new ArrayList<>();
        if (grass1 != null) {
            toPlace.add(grass1);
        }
        if (grass2 != null) {
            toPlace.add(grass2);
        }

        return toPlace;
    }

    public void updateStatisticsHist() {
        //add new entry to statistics history list
        String newStat = epoch + "," + animalCount + "," + grassCount + "," + energyAvg + "," + lifeSpanAvg + "," + childrenAvg;
        statisticsHist.add(newStat);

        double animalCountAvg = statisticsAverage.get(0);
        double grassCountAvg = statisticsAverage.get(1);
        double energyAvgAvg = statisticsAverage.get(2);
        double lifespanAvgAvg = statisticsAverage.get(3);
        double childrenAvgAvg = statisticsAverage.get(4);

        statisticsAverage.set(0, (animalCountAvg*epoch + animalCount) / (epoch + 1));
        statisticsAverage.set(1, (grassCountAvg*epoch + grassCount) / (epoch + 1));
        statisticsAverage.set(2, (energyAvgAvg*epoch + energyAvg) / (epoch + 1));
        statisticsAverage.set(3, (lifespanAvgAvg*epoch + lifeSpanAvg) / (epoch + 1));
        statisticsAverage.set(4, (childrenAvgAvg*epoch + childrenAvg) / (epoch + 1));
    }

    public void saveLogs(String fileName) {
        File outputFile = new File(fileName + ".csv");
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            pw.println("epoch,animalCount,grassCount,energyAvg,lifespanAvg,childrenAvg");
            statisticsHist.forEach(pw::println);
            List<String> averageStrings =  statisticsAverage.stream().map(Object::toString).collect(Collectors.toList());
            String average = "averages," + String.join(",", averageStrings);
            pw.println(average);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot create file, didn't save");
            e.printStackTrace();
        }
    }
}