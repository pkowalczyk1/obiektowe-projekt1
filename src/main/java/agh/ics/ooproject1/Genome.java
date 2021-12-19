package agh.ics.ooproject1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    }

    public int randomMove() {
        int ind = (int) (Math.random() * 32);
        return this.genome.get(ind);
    }

    public List<Integer> getGenes() {
        return this.genome;
    }
}
