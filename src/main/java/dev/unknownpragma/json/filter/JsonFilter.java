package dev.unknownpragma.json.filter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import dev.unknownpragma.json.filter.fieldfilter.FieldFilterParser;
import dev.unknownpragma.json.filter.fieldfilter.FieldFilterTree;

public class JsonFilter {

	private static final Logger LOG = LoggerFactory.getLogger(JsonFilter.class);

	public void filter(Reader in, Writer out, String includes, String excludes) throws JsonFilterException {
		try {
			// check parameters
			if (in == null) {
				throw new IllegalArgumentException("Le paramètre 'in' ne peut être null.");
			}
			if (out == null) {
				throw new IllegalArgumentException("Le paramètre 'out' ne peut être null.");
			}

			// create Jackson object
			JsonFactory fact = new JsonFactory();

			try (JsonParser parser = fact.createParser(in); JsonGenerator generator = fact.createGenerator(out)) {

				// parse filter parameters
				FieldFilterTree inTree = includes == null ? null : new FieldFilterParser(includes).parse();
				FieldFilterTree exTree = excludes == null ? null : new FieldFilterParser(excludes).parse();

				// create parser object
				JsonFilterParser jsp = new JsonFilterParser(parser, inTree, exTree);

				// start stream process
				while (jsp.nextToken() != null) {
					processToken(generator, jsp);
				}
				// end of stream
			}
		} catch (Exception e) {
			throw new JsonFilterException(includes, excludes, e);
		}
	}
	
	private void processToken(JsonGenerator generator, JsonFilterParser jfp) throws IOException {
		JsonParser parser = jfp.getJsonParser();
		
		if (jfp.isTokenExclude()) {
			LOG.debug("ignore    {} - attr:{} - val:{}", parser.getCurrentToken().name(), parser.getCurrentName(), parser.getValueAsString());
			jfp.skipValue();
		} else {
			if (jfp.isStructureFullyInclude()) {
				LOG.debug("copy full {} - attr:{} - val:{}", parser.getCurrentToken().name(), parser.getCurrentName(), parser.getValueAsString());
				generator.copyCurrentStructure(parser);
			} else {
				LOG.debug("copy      {} - attr:{} - val:{}", parser.getCurrentToken().name(), parser.getCurrentName(), parser.getValueAsString());
				generator.copyCurrentEvent(parser);
			}
		}
	}
}
