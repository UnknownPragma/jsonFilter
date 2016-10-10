package com.scnf.dev.json.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.scnf.dev.json.filter.param.ParamTree;

public class JsonFilterParser {

	private static final Logger LOG = LoggerFactory.getLogger(JsonFilterParser.class);
	
	private JsonParser jsonParser;

	private ParamTree curIncNode;

	private ParamTree parentIncNode;

	private ParamTree excludes;

	private String currentFieldName = null;

	private boolean structureFullyInclude = false;
	
	private boolean tokenExclude = false;

	public JsonFilterParser(JsonParser parser, ParamTree includes, ParamTree excludes) {
		this.jsonParser = parser;
		this.curIncNode = includes;
		this.parentIncNode = null;
		this.excludes = excludes;

	}

	public JsonToken nextToken() throws IOException {
		JsonToken t = jsonParser.nextToken();

		structureFullyInclude = false;
		tokenExclude = false;
		
		// si c'est un nom de champs
		if (JsonToken.FIELD_NAME.equals(t)) {
			processFieldToken();
		} else if (JsonToken.START_OBJECT.equals(t) && !jsonParser.getParsingContext().getParent().inArray()) {
			processStartObject();
		} else if (JsonToken.END_OBJECT.equals(t) && !jsonParser.getParsingContext().inArray()) {
			processEndObject();
		} else if (JsonToken.START_ARRAY.equals(t)) {
			processStartObject();
		} else if (JsonToken.END_ARRAY.equals(t)) {
			processEndObject();
		}
		
		LOG.debug("processed " + t + " '" + currentFieldName + "' - curnode " + curIncNode);
		
		return t;
	}

	private boolean isFieldSkipped() throws IOException {
		boolean skip = true;

		// if includes is null ignore it else
		if (curIncNode != null) {
			// look if this field is include
			ParamTree tmpIncludes = curIncNode.getChild(currentFieldName);
			if (tmpIncludes != null) {
				skip = false;
			}
		} else {
			skip = false;
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
			if (curIncNode == null || curIncNode.getChild(currentFieldName).getChildren().isEmpty()) {
				structureFullyInclude = true;
			}
		}
	}

	private void processStartObject() {
		// if no includes where specifed we ignore it
		if (curIncNode != null && currentFieldName != null) {
			// go down the tree following the field name
			ParamTree tmpIncludes = curIncNode.getChild(currentFieldName);
			parentIncNode = curIncNode;
			curIncNode = tmpIncludes;
		}
	}

	private void processEndObject() {
		// if no includes where specifed we ignore it
		if (parentIncNode != null) {
			// go up the param tree from the current field name
			ParamTree tmpIncludes = parentIncNode.getParent();
			curIncNode = parentIncNode;
			parentIncNode = tmpIncludes;
			
			currentFieldName = curIncNode.getData().getValue(); 
		}
	}

	public JsonParser getJsonParser() {
		return jsonParser;
	}

	public ParamTree getIncludes() {
		return curIncNode;
	}

	public ParamTree getExcludes() {
		return excludes;
	}

	public boolean isStructureFullyInclude() {
		return structureFullyInclude;
	}

	public boolean isTokenExclude() {
		return tokenExclude;
	}
	
}
