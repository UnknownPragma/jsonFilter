package dev.unknownpragma.json.filter.fieldfilter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import dev.unknownpragma.json.filter.fieldfilter.FieldFilterParser;
import dev.unknownpragma.json.filter.fieldfilter.FieldFilterSyntaxException;

@RunWith(JUnit4.class)
public class FieldFilterParserTest {

	@Test
	public void test() throws FieldFilterSyntaxException {
		Assert.assertNull(new FieldFilterParser("").parse());
		
		try {
			new FieldFilterParser(null);
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
				new FieldFilterParser(entry).parse();
				Assert.fail("Test should failed with param : " + entry);
			} catch (FieldFilterSyntaxException e) {
				Assert.assertTrue(e.getClass().equals(FieldFilterSyntaxException.class));
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
			Assert.assertEquals(entry, new FieldFilterParser(entry).parse().desc());
		}
	}
}
