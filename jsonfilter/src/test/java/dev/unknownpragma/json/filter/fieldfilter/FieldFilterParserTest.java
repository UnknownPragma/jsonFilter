package dev.unknownpragma.json.filter.fieldfilter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FieldFilterParserTest {

	@Test
	public void test() throws FieldFilterSyntaxException {

		Assert.assertEquals("+", new FieldFilterParser(null, null).parse().toString());
		Assert.assertEquals("+", new FieldFilterParser("", null).parse().toString());
		Assert.assertEquals("+", new FieldFilterParser(null, "").parse().toString());
		Assert.assertEquals("+", new FieldFilterParser(" ", "").parse().toString());

		List<String[]> entries = new ArrayList<String[]>();

		// around ","
		entries.add(ArrayUtils.toArray(",", null, ""));
		entries.add(ArrayUtils.toArray("p,", null, ""));
		entries.add(ArrayUtils.toArray("p,,", null, ""));
		entries.add(ArrayUtils.toArray("p(,", null, ""));

		// around "("
		entries.add(ArrayUtils.toArray("(", null, ""));
		entries.add(ArrayUtils.toArray("p(", null, ""));
		entries.add(ArrayUtils.toArray("p((", null, ""));
		entries.add(ArrayUtils.toArray("p,(", null, ""));
		entries.add(ArrayUtils.toArray("p(p)(", null, ""));
		entries.add(ArrayUtils.toArray("p(p(p)", null, ""));

		// aroune ")"
		entries.add(ArrayUtils.toArray(")", null, ""));
		entries.add(ArrayUtils.toArray("p)", null, ""));
		entries.add(ArrayUtils.toArray("p()", null, ""));
		entries.add(ArrayUtils.toArray("p,)", null, ""));
		entries.add(ArrayUtils.toArray("p(p))", null, ""));

		for (String[] entry : entries) {
			try {
				new FieldFilterParser(entry[0], entry[1]).parse();
				Assert.fail("Test should failed with param : " + entry[0]);
			} catch (FieldFilterSyntaxException e) {
				Assert.assertTrue(e.getClass().equals(FieldFilterSyntaxException.class));
			}
		}

		entries.clear();
		entries.add(ArrayUtils.toArray("a", null, "+[+a]"));
		entries.add(ArrayUtils.toArray("a,b", null, "+[+a, +b]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa))", null, "+[+a[+aa[+aaa]]]"));
		entries.add(ArrayUtils.toArray("a(aa),b", null, "+[+a[+aa], +b]"));
		entries.add(ArrayUtils.toArray("a(aa,ab),b", null, "+[+a[+aa, +ab], +b]"));
		entries.add(ArrayUtils.toArray("a(aa,ab),b(ba)", null, "+[+a[+aa, +ab], +b[+ba]]"));
		entries.add(ArrayUtils.toArray("a(aa,ab(aba,abb),ac),b(ba)", null, "+[+a[+aa, +ab[+aba, +abb], +ac], +b[+ba]]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa))", "a(ab)", "+[+a[+aa[+aaa]]]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa))", "a(aa(aab))", "+[+a[+aa[+aaa]]]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa))", "a(ab(aba, abb(abba, abbb)))", "+[+a[+aa[+aaa]]]"));
		entries.add(ArrayUtils.toArray("a,b,c", "d", "+[+a, +b, +c]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa)),b(ba(baa))", "a(aa),b(bb(bba))", "+[+a[+aa[+aaa]], +b[+ba[+baa]]]"));
		entries.add(ArrayUtils.toArray("a(aa(aaa))", "a(aa(aaa(aaac)))", "+[+a[+aa[+aaa[-aaac]]]]"));
		entries.add(ArrayUtils.toArray("a(aa,ab)", "a(aa(aac(aaca,aacb(aacba,aacbb))),ab,ac(aca,acb))", "+[+a[+aa[-aac[-aaca, -aacb[-aacba, -aacbb]]], +ab]]"));
		for (String[] entry : entries) {
			Assert.assertEquals(entry[2], new FieldFilterParser(entry[0], entry[1]).parse().toString());
		}

	}
}
