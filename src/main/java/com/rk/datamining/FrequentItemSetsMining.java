package com.rk.datamining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.Sets;

/**
 * Frequent Item Sets Mining based on the
 * Apriori Algorithm.
 * @author Ram Kulathumani
 *
 */
public class FrequentItemSetsMining {
	// Default File input and output
	static String input = "src/test/resources/test.dat";
	static File output = new File("src/test/resources/output.txt");

    // Find item sets of size in the range [3, ITEMSETS_SIZE]
	// By default find item sets of size in the range [3,4]
	static int ITEMSETS_SIZE = 4;

	// Default SIGMA Value if the user has not specified
	// The frequency of the item sets should be atleast
	// equal to the value of SIGMA
	static int SIGMA = 4;

	// All transactions
	List<Set<Integer>> allTransactions = new ArrayList<Set<Integer>>();

	// A frequency table for itemsets.
	// Only hold in memory the item sets required for the 
	// current iteration.
	// Gets written to the output file.
	Map<Set<Integer>, Integer> itemSetsFrequencyTable = 
			new HashMap<Set<Integer>, Integer>();

	BufferedWriter bufferedWriter;

	/**
	 * Create the transactions set
	 * @param transaction The list of transactions
	 */
	void createTransactions(List<String> transaction) {
		Set<Integer> set = new HashSet<Integer>();
		for (String item : transaction) {
			set.add(Integer.valueOf(item));
		}
		allTransactions.add(set);
	}

	/**
	 * Count the frequency of each unique SKU.
	 * in parallel and compute the results in a
	 * map reduce fashion
	 */
	void countSKUFrequency() {
		allTransactions.parallelStream().flatMap(Set::stream)
				.collect(Collectors.toMap(sku -> sku, sku -> 1, Integer::sum)).forEach((k, v) -> {
					if (v >= SIGMA)
						itemSetsFrequencyTable.put(Set.of(k), v);
				});
	}

	/**
	 * Check the frequency of each item set in the 
	 * transactions. Runs in parallel and computes the
	 * result in a map reduce fashion
	 * @param itemSet The ItemSet of SKUs
	 */
	void compareAndCountItemSetsFrequency(Set<Integer> itemSet) {
		allTransactions.parallelStream().filter(item -> item.containsAll(itemSet))
				.collect(Collectors.toMap(t -> itemSet, t -> 1, Integer::sum)).forEach((k, v) -> {
					if (v >= SIGMA) {
						itemSetsFrequencyTable.put(k, v);
					}
				});
	}

	/**
	 * Read the transactions file.
	 */
	public void init() {
		try {
			Path filePath = Paths.get(input);
			Files.lines(filePath).parallel().map(line -> line.split("\\s+")).map(item -> Arrays.asList(item))
					.collect(Collectors.toSet()).forEach(item -> createTransactions(item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the Frequency Table to the output.
	 */
	void writeToOutput() {
		try {
			for (Set<Integer> key : itemSetsFrequencyTable.keySet()) {
				bufferedWriter.write("ItemSet Size:" + String.valueOf(key.size()));
				bufferedWriter.append(',');
				bufferedWriter.append("Frequency:" + itemSetsFrequencyTable.get(key).toString());
				bufferedWriter.append(',');
				bufferedWriter.append("ItemSet:" + key.toString());
				bufferedWriter.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the item sets of size K to a file
	 * Creates file with extension .serialized
	 * @param s The item set to be written
	 * @param o The ObjectOutputStream
	 */
	synchronized void writeItemSetToFile(Set<Integer> s,
			ObjectOutputStream o) {
		try {
			o.writeObject(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds the Item Sets before pruning based on
	 * the value of SIGMA. Writes output to file to
	 * save memory
	 * @param prevItemSets Set of SKU Values
	 * @param cardinality The dimension of the item set.
	 */
	void buildItemSets(Set<Set<Integer>> prevItemSets, int cardinality) {
		try {
			FileOutputStream f = 
					new FileOutputStream(new File("000000" + cardinality + ".serialized"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			prevItemSets.parallelStream().forEach(x -> {
				prevItemSets.stream().forEach(y -> {
					if (Sets.intersection(x, y).size() == cardinality - 2) {
						Set<Integer> itemSet = new HashSet<Integer>();
						itemSet.addAll(Sets.union(x, y));
						writeItemSetToFile(itemSet, o);
						itemSet = null;
						// This isn't really necessary.
						// Just trying everything possible to
						// keep the heap size in check. Did
						// help in the machine used for tests.
						System.gc();
					}
				});
			});
			o.writeObject(null);
			o.close();
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void run() {
		init();

		countSKUFrequency();

		// Initialize for K = 1 and iterate for the rest
		int k = 1;

		try {
			bufferedWriter = new BufferedWriter(new FileWriter(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (!itemSetsFrequencyTable.keySet().isEmpty()) { 
			writeToOutput();
			// Reached the end , exit.
			if (k == ITEMSETS_SIZE)
				break;
			k = k + 1;
			// Generate item sets from the pruned list
			// of the previous iteration
			buildItemSets(itemSetsFrequencyTable.keySet(), k);
			itemSetsFrequencyTable.clear();
			System.gc();
			FileInputStream f;
			boolean b = true;
			ObjectInputStream input;
			try {
				f = new FileInputStream("000000" + k + ".serialized");
				input = new ObjectInputStream(f);
				while (b) {
					Object object = input.readObject();
					if (object != null) {
						Set<Integer> itemSet = (Set<Integer>) object;
						compareAndCountItemSetsFrequency(itemSet);
					} else {
						b = false;
					}
				}
				input.close();
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		if (args.length == 1) {
			input = args[0];
		} else if (args.length == 2) {
			input = args[0];
			SIGMA = Integer.valueOf(args[1]);
		} else if (args.length == 3) {
			input = args[0];
			SIGMA = Integer.valueOf(args[1]);
			ITEMSETS_SIZE = Integer.valueOf(args[2]);
		}
		
		mining.run();
	}
}
