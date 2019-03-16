package com.rk.datamining;
/*
 * Test Cases for the Frequent Item Set Mining
 * Algorithm
 */
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
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
			e.printStackTrace();
		}
		assertEquals("Incorrect results", true, resultOk);
	}
}
