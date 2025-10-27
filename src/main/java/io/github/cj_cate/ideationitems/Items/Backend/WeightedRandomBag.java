package io.github.cj_cate.ideationitems.Items.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class WeightedRandomBag<T> implements Supplier<T> {

    private record Entry<T>(double weight, T object) { }

    final private List<Entry<T>> entries = new ArrayList<>();
    private double accumulatedWeight;
    final private Random rand = new Random();

//    public void dumpEntries() {
//        if(entries.isEmpty()) { return; }
//        System.out.println("total weight: " + accumulatedWeight);
//        for(Entry<T> entry : entries) {
//            System.out.println(entry.object + ": " + entry.weight);
//        }
//    }

    public void add(T object, double weight) {
        if (weight < 0) throw new IllegalArgumentException("weight cannot be negative");
        accumulatedWeight += weight;
        entries.add(new Entry<>(weight, object));
    }

    @Override
    public T get() {
        double r = rand.nextDouble() * accumulatedWeight;
        double currentAccumulatedWeight = 0;
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            currentAccumulatedWeight += entry.weight;
            if (currentAccumulatedWeight >= r) {
                return entries.get(i).object;
            }
        }
        return null; //should only happen when there are no entries
    }

}