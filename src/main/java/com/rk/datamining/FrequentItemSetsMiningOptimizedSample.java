package com.rk.datamining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class FrequentItemSetsMiningOptimizedSample {
	Map<ImmutableSet<Integer>, Integer> itemSetsFrequency =
			new HashMap<ImmutableSet<Integer>, Integer>();
	// By default find tripletons of transactions if no user argument
	// is specified.
	int ITEMSETS_SIZE = 3;
	// Default SIGMA Value if the user has not specified
	int SIGMA = 3;
	// Default File input and output
	static String input = "src/test/resources/test.dat";
	static File output = new File("src/test/resources/output.txt");

	/**
	 * Gets all combinations of a given size
	 * @param i The immutable set for which we need
	 *          to get all the combinations for.
	 * @param k The size of the combinations.
	 * @return  The combinations of the given size.
	 */
	Set<Set<Integer>> getCombinations(Set<Integer> i,
			int k) {
		if (i.size() < k) return null;
		return Sets.combinations(i, k);
	}
	
	/**
	 * Updates the ItemSetFrequencyMap with the count of the
	 * given ItemSet.
	 * @param itemSet The immutable itemSet Key of the Map.
	 */
	void updateItemSetsFrequencyMap(ImmutableSet<Integer> itemSet) {
		if (itemSetsFrequency.containsKey(itemSet)) {
			int c = itemSetsFrequency.get(itemSet);
			itemSetsFrequency.put(itemSet, c + 1);
		} else {
			itemSetsFrequency.put(itemSet, 1);
		}
	}
	
	/**
	 * Prune the Frequency Map according to the given
	 * Threshold value (SIGMA) 
	 */
	void pruneFrequencyMap() {
		Iterator<Entry<ImmutableSet<Integer>, Integer>> it = 
				itemSetsFrequency.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ImmutableSet<Integer>, Integer> kv = 
					(Map.Entry<ImmutableSet<Integer>, Integer>)it.next();
			if (kv.getValue() < SIGMA) it.remove();
		}
	}
	
	void writeOutput() {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = 
					new BufferedWriter(new FileWriter(output));
			Iterator<Entry<ImmutableSet<Integer>, Integer>> it = 
					itemSetsFrequency.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<ImmutableSet<Integer>, Integer> kv = 
						(Map.Entry<ImmutableSet<Integer>, Integer>)it.next();
				bufferedWriter.write(String.valueOf(kv.getKey().size()));
				bufferedWriter.append(',');
				bufferedWriter.append(kv.getValue().toString());
				bufferedWriter.append(',');
				bufferedWriter.append(kv.getKey().toString());
				bufferedWriter.append("\n");
			}
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read input file. Also computes the frequency of
	 * itemsets upto size n.
	 * @param input The file containing
	 *              the transaction.
	 */
	void run(String input) {
		try {
			Path filePath = Paths.get(input);
			Files.lines(filePath)
			     .parallel()
			     .forEach(transaction -> {
				String[] str = transaction.split("\\s+");
				Set<Integer> items = new HashSet<Integer>();
				for (int i = 0; i < str.length ; i++) {
					if (str[i].isEmpty()) continue;
					items.add(Integer.parseInt(str[i]));
				}
				Set<Set<Integer>> itemSets = 
						getCombinations(items, ITEMSETS_SIZE);
				if (itemSets == null) return;
				for (Set<Integer> ele : itemSets) {
		    		ImmutableSet<Integer> combination = ImmutableSet.<Integer>builder()
		    		           .addAll(ele)
		    		           .build();
		    		updateItemSetsFrequencyMap(combination);
		    	}
			});
			//bufferedReader.close();
			pruneFrequencyMap();
			writeOutput();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FrequentItemSetsMiningOptimizedSample mining = new FrequentItemSetsMiningOptimizedSample();
		if (0 < args.length) {
		    input = args[0];
		}
		long st = System.currentTimeMillis();
        mining.run(input);
        long et = System.currentTimeMillis() - st;
        System.out.println(et);
	}
}
