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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;

public class FrequentItemSetsMiningTest {
	@Test
	public void testAlgorithm() {
		FrequentItemSetsMining mining = new FrequentItemSetsMining();
		mining.run();
		boolean resultOk = false;
		File op = new File("src/test/resources/output.txt");
		File exp = new File("src/test/resources/expected.txt");
		try {
			resultOk = FileUtils.contentEquals(op, exp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("Incorrect results", true, resultOk);
	}
}
