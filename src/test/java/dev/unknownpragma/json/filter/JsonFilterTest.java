package com.sncf.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scnf.dev.json.filter.JsonFilter;

@RunWith(JUnit4.class)
public class JsonFilterTest {

	private static final Logger LOG = LoggerFactory.getLogger(JsonFilterTest.class);

	private final JsonFilter jf = new JsonFilter();

	@Test
	public void test1() throws Exception {
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("test1.json"));
		StringWriter writer = new StringWriter();

		jf.filter(reader, writer, "plop", null);

		LOG.info("res = {}", writer.toString());
	}

	@Test
	public void test2() throws Exception {
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("test2.json"));

		StringWriter writer = new StringWriter();

		jf.filter(reader, writer, "a(aa(aaa)),b(b1)", null);

		LOG.info("res = {}", writer.toString());
		
		Assert.assertEquals(fileToString("test2.result.json"), writer.toString());
	}

	@Test
	public void test3() throws Exception {
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("test3.json"));
		;
		StringWriter writer = new StringWriter();

		jf.filter(reader, writer, null, null);

		LOG.info("res = {}", writer.toString());
		// Assert.assertEquals(fileToString("test2.result.json"),writer.toString());
	}

	private String fileToString(String name) throws IOException {
		URL url = this.getClass().getResource(name);
		File file = new File(url.getFile());
		return FileUtils.readFileToString(file, Charset.defaultCharset());
	}
}
