package agh.ics.ooproject1;

import java.util.*;

public class Genome {
    private List<Integer> genome;

    public Genome(List<Integer> genome) {
        genome.sort(Comparator.comparingInt(o -> o));
        this.genome = genome;
    }

    public Genome() {
        this.genome = new ArrayList<>();
        for (int i=0; i<32; i++) {
            int gene = (int)(Math.random() * 8);
            this.genome.add(gene);
        }
        Collections.sort(this.genome);
    }

    public int randomMove() {
        int ind = (int) (Math.random() * 32);
        return this.genome.get(ind);
    }

    public List<Integer> getGenes() {
        return this.genome;
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
}
