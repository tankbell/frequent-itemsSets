package com.rk.datamining;
/*
 * Test Cases for the Frequent Item Set Mining
 * Algorithm
 */
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class FrequentItemSetsMiningTest {
	/*
	@Test
	public void testAlgorithm() {
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		mining.run(new File("src/test/resources/test.dat"));
		assertEquals("Incorrect Frequent Sets Map Size", mining.itemSetsFrequency.size(), 1);
		ImmutableSet<Integer> r = ImmutableSet.of(80,1001,1003);
		assertNotNull("Missing valid Frequent Set Map Key", mining.itemSetsFrequency.get(r));
		int x = mining.itemSetsFrequency.get(r);
		assertEquals("Incorrect Frequent Set Map Value", x, 3);
	}
	
	@Test
	public void testGetCombinations() {
		Set<Integer> s = new HashSet<Integer>();
		s.add(10);
		s.add(20);
		s.add(30);
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		Set<Set<Integer>> r = mining.getCombinations(s, 2);
		assertEquals("Incorrect Combinations", r.size(), 3);
	}
	
	@Test
	public void testUpdateItemSetsFrequencyMap() {
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		mining.itemSetsFrequency.put(ImmutableSet.of(1001), 20);
		mining.updateItemSetsFrequencyMap(ImmutableSet.of(1001));
		
		mining.updateItemSetsFrequencyMap(ImmutableSet.of(101));
		
		int r1 = mining.itemSetsFrequency.get(ImmutableSet.of(1001));
		int r2 = mining.itemSetsFrequency.get(ImmutableSet.of(101));
		
		assertEquals("Error updating Frequency Map", r1, 21);
		assertEquals("Error initializing Frequency Map Count", r2, 1);
	}
	
	@Test
	public void testPruneFrequencyMap() {
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		mining.itemSetsFrequency.put(ImmutableSet.of(1001), 2);
		mining.itemSetsFrequency.put(ImmutableSet.of(1003), 3);
		mining.SIGMA = 3;
		
		mining.pruneFrequencyMap();
		assertEquals("Error Pruning Frequency Map", mining.itemSetsFrequency.size(), 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGuavaSets() {
		Set<Integer> A = Set.of(10,20);
		Set<Integer> B = Set.of(20,30);
		Set<Integer> r = null;
		//if (Sets.intersection(A, B).size() > 0) {
			r = Sets.union(A,B);
		//}
		System.out.println(r.toString());
		
	}
	*/
	/*
	@Test
	public void testGetPossibleItemSets() {
		FrequentItemSetsMiningOptimized mining = new FrequentItemSetsMiningOptimized();
		Set<Set<Integer>> prevItemSets = new HashSet();
		Set<Integer> a = Set.of(10);
		Set<Integer> b = Set.of(20);
		Set<Integer> c = Set.of(30);
		prevItemSets.add(a);
		prevItemSets.add(b);
		prevItemSets.add(c);
		Set<Set<Integer>> r = mining.getPossibleItemSets(prevItemSets, 2);
		//for (Set<Integer> s : r) System.out.println(s.toString());
		
		Set<Set<Integer>> prevItemSets2 = new HashSet();
		Set<Integer> d = Set.of(10,20);
		Set<Integer> e = Set.of(20,30);
		Set<Integer> f = Set.of(30,50);
		prevItemSets2.add(d);
		prevItemSets2.add(e);
		prevItemSets2.add(f);
		Set<Set<Integer>> r2 = mining.getPossibleItemSets(prevItemSets2, 3);
		//for (Set<Integer> s2 : r2) System.out.println(s2.toString());
		
		Set<Set<Integer>> prevItemSets3 = new HashSet();
		Set<Integer> x = Set.of(10,20,30);
		Set<Integer> y = Set.of(20,30,60);
		Set<Integer> z = Set.of(30,60,250);
		prevItemSets3.add(x);
		prevItemSets3.add(y);
		prevItemSets3.add(z);
		Set<Set<Integer>> r3 = mining.getPossibleItemSets(prevItemSets3, 4);
		//for (Set<Integer> s3 : r3) System.out.println(s3.toString());
		
		TreeSet<Integer> t = new TreeSet<Integer>();
		t.add(50);
		t.add(40);
		t.add(25);
		
		//System.out.println(t.toString());
		
		Set<Integer> X = Set.of(77,88,99);
		Set<Integer> Y = Set.of(88,99,60);
		X.addAll(Y);
		System.out.println("Last " + X.toString());
		
	}
	*/
}
