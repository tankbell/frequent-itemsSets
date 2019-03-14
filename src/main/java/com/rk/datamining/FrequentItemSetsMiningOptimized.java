package com.rk.datamining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public class FrequentItemSetsMiningOptimized {
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
	
	// Item Sets Frequency Table
	// Key : Set of SKUs with sizes ranging from 1 to given Limit [ITEMSETS_SIZE]
	// Value : Frequency of the Sets.
	// This data structure gets written to the output file and after each K
	// along with removing the items written from the map
	Map<Set<Integer>, Integer> itemSetsFrequencyTable = new HashMap<Set<Integer>, Integer>();
	
	Map<Integer, Set<Set<Integer>>> cardinalityMap = 
			new HashMap<Integer, Set<Set<Integer>>>();
	
	BufferedWriter bufferedWriter;
	
	void pruneNonfrequentItemSets() {
		Iterator<Map.Entry<Set<Integer>, Integer>> i = 
				itemSetsFrequencyTable.entrySet().iterator();
		while(i.hasNext())
		{
		   Map.Entry<Set<Integer>, Integer> entry = i.next();
		   if(entry.getValue().intValue() < SIGMA) i.remove();
		}
	}
	
	void createTransactions(List<String> transaction) {
		Set<Integer> set = new HashSet<Integer>();
		for (String item : transaction) {
			set.add(Integer.valueOf(item));
		}
		allTransactions.add(set);
	}
	
	void countSKUFrequency() {
		allTransactions
				.parallelStream()
				.flatMap(Set::stream)
				.collect(Collectors.toMap(sku -> sku, sku -> 1, Integer::sum))
				.forEach((k,v) -> {
					if (v >= SIGMA) itemSetsFrequencyTable.put(Set.of(k), v);
				});
	}
	
	void compareAndCountItemSetsFrequency(Set<Integer> itemSet) {
		System.out.println("Comparing and Counting " + itemSet.toString());
		allTransactions
		    .parallelStream()
		    .filter(item -> item.containsAll(itemSet))
		    .collect(Collectors.toMap(t -> itemSet, t -> 1, Integer::sum))
		    .forEach((k,v) -> {
				if (v >= SIGMA) {
					itemSetsFrequencyTable.put(k, v);
					System.out.println("SIGMA " + k.toString() + " " + v);
				}
			});
	}
	
	public void init() {
		try {
			Path filePath = Paths.get(input);
			Files.lines(filePath)
			     .parallel()
			     .map(line -> line.split("\\s+"))
			     .map(item -> Arrays.asList(item))
			     .collect(Collectors.toList())
			     .forEach(item -> createTransactions(item));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void writeToOutput() {
		try {
			for (Set<Integer> key : itemSetsFrequencyTable.keySet()) {
				bufferedWriter.write(String.valueOf(key.size()));
				bufferedWriter.append(',');
				bufferedWriter.append(itemSetsFrequencyTable.get(key).toString());
				bufferedWriter.append(',');
				bufferedWriter.append(key.toString());
				bufferedWriter.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Set<Set<Integer>> getPossibleItemSets(Set<Set<Integer>> prevItemSets,
			int cardinality) {
		Set<Set<Integer>> r = ConcurrentHashMap.newKeySet();
		prevItemSets.parallelStream()
		            .forEach(x -> {
		            	prevItemSets.stream()
		            	            .forEach(y -> {
		            	            	if (Sets.intersection(x, y).size() == cardinality-2) {
		            	            		r.add(Sets.union(x, y));
		            	            		//System.out.println("New " + r.toString());
		            	            	}
		            	            });
		            });
		return r;
	}
	
	Set<Set<Integer>> getItemSets(Set<Set<Integer>> itemSet) {
		Set<Set<Integer>> itemSets = new HashSet<Set<Integer>>();
		for (Set<Integer> item : itemSet) itemSets.add(item);
		return itemSets;
	}
	
	void run() {
		// Read file. Perform operation in parallel using Streams.
		// Load transactions in memory [Given file is 1.2MB in size]
		// If file is much bigger than this, read each time for the subsequent
		// operations.
		init();
		        
		// Here we count the frequency of each unique product SKU.
		// We update the frequency in a KV pair [HashMap:itemSetsFrequencyTable]
		// Perform operation in a Map Reduce fashion using Streams.
		countSKUFrequency();
		        
		// Here we prune out the non frequent item sets based on the given threshold
		// value : SIGMA. This would reduce the number of itemsets we consider
	    // for the next iterations [cardinality : currentCardinality + 1]
		//pruneNonfrequentItemSets();
		        
		// Initialize for K = 1 and iterate for the rest
		int k = 1;
		cardinalityMap.put(k, getItemSets(itemSetsFrequencyTable.keySet()));
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while (!cardinalityMap.get(k).isEmpty()) {
		    // As per the requirement in the given problem
		    // we must only write item sets of size 3 or more.
		    writeToOutput();
		    // Empty the map before the next iteration
		    // As we already wrote to the output. This
		    // would save some memory.
		    itemSetsFrequencyTable.clear();
		    // Reached the end , exit.
		    if (k == ITEMSETS_SIZE) break;
		    k = k + 1;
		    // Generate possible item sets from the pruned list
		    // of the previous iteration
		    Set<Set<Integer>> possibleItemSets = 
		    		getPossibleItemSets(cardinalityMap.get(k-1), k);
		    // Count the number of times this item set occurs in the transactions.
		    // Parallel Map Reduce using Streams.
		    for (Set<Integer> itemSet : possibleItemSets) {
		    	 compareAndCountItemSetsFrequency(itemSet);
		    }
		    cardinalityMap.remove(k-1); // Throw for garbage collection
		    possibleItemSets = null; // Throw for garbage collection
		    //pruneNonfrequentItemSets();
		    // Populate the possible item sets for the next iteration
		    cardinalityMap.put(k, getItemSets(itemSetsFrequencyTable.keySet()));
		}
		try {
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static void main(String[] args) {
		FrequentItemSetsMiningOptimized mining = new FrequentItemSetsMiningOptimized();
		if (0 < args.length) {
		    input = args[0];
		}
		mining.run();
	}
}
