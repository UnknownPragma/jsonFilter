package dev.unknownpragma.json.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import dev.unknownpragma.json.filter.fieldfilter.FieldFilterTree;

import static com.fasterxml.jackson.core.JsonToken.*;

public class JsonFilterParser {

	private static final Logger LOG = LoggerFactory.getLogger(JsonFilterParser.class);

	private JsonParser jsonParser;

	private FieldFilterTree curNode;

	private FieldFilterTree parentNode;

	private String currentFieldName = null;

	private boolean structureFullyInclude = false;

	private boolean tokenExclude = false;

	public JsonFilterParser(JsonParser parser, FieldFilterTree tree) {
		this.jsonParser = parser;
		this.curNode = tree;
		this.parentNode = null;
	}

	public JsonToken nextToken() throws IOException {
		JsonToken t = jsonParser.nextToken();

		structureFullyInclude = false;
		tokenExclude = false;

		// si c'est un nom de champs
		if (FIELD_NAME == t) {
			processFieldToken();
		} else if (START_ARRAY == t || (START_OBJECT == t && !jsonParser.getParsingContext().getParent().inArray())) {
			processStartObject();
		} else if (END_ARRAY == t || END_OBJECT == t && !jsonParser.getParsingContext().inArray()) {
			processEndObject();		
		}
		
		LOG.debug("processed {} '{}' - curNode {}", t, currentFieldName, curNode);

		return t;
	}

	private boolean isFieldSkipped() throws IOException {
		boolean skip = false;

		FieldFilterTree fieldNode = curNode.getChild(currentFieldName);
		// if the node has this field as an exclude child and this field has no child
		if ((fieldNode != null && fieldNode.isExcludeLeafField())
				// or if the field is not a child but there is other include field
				|| (fieldNode == null && !curNode.getIncludeFieldChildren().isEmpty())) {
			skip = true;
		}
		return skip;
	}

	public void skipValue() throws IOException {
		// fetch the value
		JsonToken t = jsonParser.nextToken();
		// if this is an arry or an object
		if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) {
			// skip it
			jsonParser.skipChildren();
		}
	}

	private void processFieldToken() throws IOException {
		currentFieldName = jsonParser.getCurrentName();

		if (isFieldSkipped()) {
			tokenExclude = true;
		} else {
			FieldFilterTree fieldNode = curNode.getChild(currentFieldName);
			// if there no field or if this field has no children
			if (fieldNode == null || fieldNode.getChildren().isEmpty()) {
				structureFullyInclude = true;
			}
		}
	}

	private void processStartObject() {
		// If it is a sub object
		if (currentFieldName != null) {
			// go down the tree following the field name
			FieldFilterTree tmpIncludes = curNode.getChild(currentFieldName);
			parentNode = curNode;
			curNode = tmpIncludes;
		}
	}

	private void processEndObject() {
		// if no includes where specifed we ignore it
		if (parentNode != null) {
			// go up the param tree from the current field name
			FieldFilterTree tmpIncludes = parentNode.getParent();
			curNode = parentNode;
			parentNode = tmpIncludes;

			currentFieldName = curNode.getData().getValue();
		}
	}

	public JsonParser getJsonParser() {
		return jsonParser;
	}

	public boolean isStructureFullyInclude() {
		return structureFullyInclude;
	}

	public boolean isTokenExclude() {
		return tokenExclude;
	}

}
