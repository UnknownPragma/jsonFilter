package dev.unknownpragma.json.filter.fieldfilter;

import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.COMMA;
import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.FIELD_NAME;
import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.OPEN_PARENTHESIS;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldFilterParser {

	private static final Logger LOG = LoggerFactory.getLogger(FieldFilterParser.class);

	private FieldFilterTokenizer incFields;

	private FieldFilterTokenizer excFields;

	// parsing context properties /////////////////////////////

	private FieldFilterTokenizer curFieldsFilter = null;

	private int parenthesisCount = 0;

	private FieldFilterTree curTree = null;

	private FieldFilterToken curToken = null;

	private FieldFilterToken prevToken = null;

	private boolean includeField;

	private int skipCounterRef;

	private boolean skip;

	public FieldFilterParser(String incFields, String excFields) {
		this.incFields = new FieldFilterTokenizer(StringUtils.trimToEmpty(incFields));
		this.excFields = new FieldFilterTokenizer(StringUtils.trimToEmpty(excFields));
	}

	public FieldFilterTree parse() throws FieldFilterSyntaxException {
		FieldFilterTree res = FieldFilterTree.createRoot();
		curTree = res;

		try {
			// parse includes fields
			includeField = true;
			curFieldsFilter = incFields;
			LOG.debug("Parse include filter : {}", incFields);
			parseFieldFilter();

			// parse excludes fields
			curTree = res;
			includeField = false;
			LOG.debug("Parse exclude filter : {}", excFields);
			curFieldsFilter = excFields;
			parseFieldFilter();

		} catch (Exception e) {
			throw new FieldFilterSyntaxException(curFieldsFilter, curToken, prevToken, e);
		}

		LOG.debug("Parsing res : {}", res);

		return res;

	}

	private void parseFieldFilter() {
		curToken = curFieldsFilter.nextToken();
		while (curToken != null) {
			processToken();
			// then current token become previous and fetch next one
			prevToken = curToken;
			curToken = curFieldsFilter.nextToken();
		}

		// final condition
		if (prevToken != null && (prevToken.getType() == OPEN_PARENTHESIS || prevToken.getType() == COMMA)) {
			throw new IllegalArgumentException("Filter cannot and with a ',' or a '('.");
		}

		if (parenthesisCount != 0) {
			throw new IllegalArgumentException("No matching between '(' and ')' - diff=" + parenthesisCount);
		}
	}

	private void processToken() {
		switch (curToken.getType()) {
		case FIELD_NAME:
			processParamToken();
			break;
		case COMMA:
			processCommaToken();
			break;
		case CLOSE_PARENTHESIS:
			processCloseParenthesisToken();
			break;
		case OPEN_PARENTHESIS:
			processOpenParenthesisToken();
			break;
		default:
			throw new IllegalArgumentException("Unknow token type : " + curToken.getType());
		}
	}

	private void processParamToken() {
		// if we are not in a skip mode
		if (!skip) {
			// exclude token case
			if (!includeField) {
				// go on only if there is no include child at this level
				if (curTree.getIncludeFieldChildren().isEmpty()) {
					curTree.addChild(curToken, includeField);
				} else if(curTree.getIncludeFieldChild(curToken) != null) {
					//there is an include child with the same name do nothing but let go further
				} else {
					skipFieldAndChild();
					LOG.debug("ignoring '{}' field exlusion and all child field because other field is already include", curToken.getValue());
				}
			} else {
				curTree.addChild(curToken, includeField);
			}
		}
	}

	private void processCommaToken() {
		if (prevToken == null || (prevToken.getType() == COMMA || prevToken.getType() == OPEN_PARENTHESIS)) {
			throw new IllegalArgumentException("',' can't be after ',' , '(' or be at first position");
		}
	}

	private void processOpenParenthesisToken() {
		if (prevToken == null || prevToken.getType() != FIELD_NAME) {
			throw new IllegalArgumentException("'(' can't be aftet ',', '(', ')' or ba at first position.");
		}

		if (!skip) {
			curTree = curTree.getChild(prevToken);
			if (curTree == null) {
				throw new IllegalStateException("no child node with name '" + prevToken.getValue() + "'");
			}
		}
		parenthesisCount++;
	}

	private void processCloseParenthesisToken() {
		if (prevToken == null || prevToken.getType() == COMMA || prevToken.getType() == OPEN_PARENTHESIS) {
			throw new IllegalArgumentException("')' can't be after ',', '(' or be at first pos");
		}

		if (!skip) {
			FieldFilterTree parentTree = curTree.getParent();
			if (parentTree == null) {
				throw new IllegalArgumentException("no '(' matching ')'");
			}
			curTree = parentTree;
		}

		if (skip && parenthesisCount <= skipCounterRef) {
			skip = false;
			LOG.debug("Leave the skip mode (parenthesis count = {}, skip counter ref = {})", parenthesisCount, skipCounterRef);
		}
		
		parenthesisCount--;
		
	}

	private void skipFieldAndChild() {
		if (FIELD_NAME != curToken.getType()) {
			throw new IllegalStateException("Current token is not a field name (curToken=" + curToken + ")");
		}

		// look at the next token
		FieldFilterToken nextT = curFieldsFilter.prefetchToken();
		// enter skip mode only if next token is open parentethis
		if (nextT != null && OPEN_PARENTHESIS == nextT.getType()) {
			skip = true;
			// keep a ref to the parentethis counter to leave the skip mode
			skipCounterRef = parenthesisCount + 1;
			LOG.debug("Enter skip mode (ref counter = {})", skipCounterRef);
		}

	}
}
