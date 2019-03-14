package com.rk.datamining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

public class FrequentItemSetsMiningFinal {
	// Default File input and output
	static String input = "src/test/resources/test.dat";
	static File output = new File("src/test/resources/output.txt");

	// Find item sets of size in the range [3, ITEMSETS_SIZE]
	// By default find item sets of size in the range [3,4]
	int ITEMSETS_SIZE = 4;

	// Default SIGMA Value if the user has not specified
	// The frequency of the item sets should be atleast
	// equal to the value of SIGMA
	int SIGMA = 4;

	// All transactions
	List<Set<Integer>> allTransactions = new ArrayList<Set<Integer>>();

	Map<Set<Integer>, Integer> itemSetsFrequencyTable = new HashMap<Set<Integer>, Integer>();

	BufferedWriter bufferedWriter;

	void createTransactions(List<String> transaction) {
		Set<Integer> set = new HashSet<Integer>();
		for (String item : transaction) {
			set.add(Integer.valueOf(item));
		}
		allTransactions.add(set);
	}

	void countSKUFrequency() {
		allTransactions.parallelStream().flatMap(Set::stream)
				.collect(Collectors.toMap(sku -> sku, sku -> 1, Integer::sum)).forEach((k, v) -> {
					if (v >= SIGMA)
						itemSetsFrequencyTable.put(Set.of(k), v);
				});
	}

	void compareAndCountItemSetsFrequency(Set<Integer> itemSet) {
		allTransactions.parallelStream().filter(item -> item.containsAll(itemSet))
				.collect(Collectors.toMap(t -> itemSet, t -> 1, Integer::sum)).forEach((k, v) -> {
					if (v >= SIGMA) {
						itemSetsFrequencyTable.put(k, v);
					}
				});
	}

	public void init() {
		try {
			Path filePath = Paths.get(input);
			Files.lines(filePath).parallel().map(line -> line.split("\\s+")).map(item -> Arrays.asList(item))
					.collect(Collectors.toList()).forEach(item -> createTransactions(item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	void buildPossibleItemSets(Set<Set<Integer>> prevItemSets, int cardinality) {
		try {
			FileOutputStream f = new FileOutputStream(new File("000000" + cardinality + ".serialized"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			prevItemSets.parallelStream().forEach(x -> {
				prevItemSets.stream().forEach(y -> {
					if (Sets.intersection(x, y).size() == cardinality - 2) {
						Set<Integer> s = Sets.newConcurrentHashSet(Sets.union(x, y));
						try {
							synchronized (this) {
								o.writeObject(s);
							}
							s = null;
							System.gc();
						} catch (IOException e) {
							e.printStackTrace();
						}
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
			// Generate possible item sets from the pruned list
			// of the previous iteration
			buildPossibleItemSets(itemSetsFrequencyTable.keySet(), k);
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
		FrequentItemSetsMiningFinal mining = new FrequentItemSetsMiningFinal();
		if (0 < args.length) {
			input = args[0];
		}
		mining.run();
	}
}
