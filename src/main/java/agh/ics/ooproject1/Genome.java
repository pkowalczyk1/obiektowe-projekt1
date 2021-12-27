package agh.ics.ooproject1;

import java.util.*;

public class Genome {
    private final List<Integer> genome;

    //constructor with genome as parameter
    public Genome(List<Integer> genome) {
        genome.sort(Comparator.comparingInt(o -> o));
        this.genome = genome;
    }

    //constructor with random genome
    public Genome() {
        genome = new ArrayList<>();
        for (int i=0; i<32; i++) {
            int gene = (int)(Math.random() * 8);
            genome.add(gene);
        }
        Collections.sort(genome);
    }

    public int randomMove() {
        int ind = (int) (Math.random() * 32);
        return genome.get(ind);
    }

    public List<Integer> getGenes() {
        return genome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Genome genome1 = (Genome) o;
        return Objects.equals(genome, genome1.genome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genome);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        for (Integer i : genome) {
            builder.append(i.toString());
        }

        return builder.toString();
    }
}
