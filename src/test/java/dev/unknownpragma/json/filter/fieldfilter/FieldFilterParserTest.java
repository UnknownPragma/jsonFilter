package com.sncf.dev.json.filter.param;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.scnf.dev.json.filter.param.ParamParser;
import com.scnf.dev.json.filter.param.ParameterSyntaxException;

@RunWith(JUnit4.class)
public class ParamParserTest {

	@Test
	public void test() throws ParameterSyntaxException {
		Assert.assertNull(new ParamParser("").parse());
		
		try {
			new ParamParser(null);
			Assert.fail("Test should failed with param : null");
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getClass().equals(IllegalArgumentException.class));
		}

		List<String> entries = new ArrayList<String>();

		// around ","
		entries.add(",");
		entries.add("p,");
		entries.add("p,,");
		entries.add("p(,");

		// around "("
		entries.add("(");
		entries.add("p(");
		entries.add("p((");
		entries.add("p,(");
		entries.add("p(p)(");
		entries.add("p(p(p)");

		// aroune ")"
		entries.add(")");
		entries.add("p)");
		entries.add("p()");
		entries.add("p,)");
		entries.add("p(p))");

		for (String entry : entries) {
			try {
				new ParamParser(entry).parse();
				Assert.fail("Test should failed with param : " + entry);
			} catch (ParameterSyntaxException e) {
				Assert.assertTrue(e.getClass().equals(ParameterSyntaxException.class));
			}
		}

		entries.clear();
		entries.add("a");
		entries.add("a,b");
		entries.add("a(aa(aaa))");
		entries.add("a(aa),b");
		entries.add("a(aa,ab),b");
		entries.add("a(aa,ab),b(ba)");
		entries.add("a(aa,ab(aba,abb),ac),b(ba)");
		for (String entry : entries) {
			Assert.assertEquals(entry, new ParamParser(entry).parse().desc());
		}
	}
}
