package dev.unknownpragma.json.filter.fieldfilter;

import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.COMMA;
import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.FIELD_NAME;
import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.OPEN_PARENTHESIS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldFilterParser {

	private static final Logger LOG = LoggerFactory.getLogger(FieldFilterParser.class);

	private String fieldsFilter;

	private int curPos = 0;

	private int parenthesisCount = 0;

	public FieldFilterParser(String fieldsFilter) {
		if (fieldsFilter == null) {
			throw new IllegalArgumentException("'fieldsFilter' argument can't be null");
		}

		this.fieldsFilter = fieldsFilter;
	}

	public FieldFilterTree parse() throws FieldFilterSyntaxException {
		FieldFilterTree res = null;
		FieldFilterToken token = null;
		FieldFilterToken prevT = null;

		try {
			// walk through the string
			if (!fieldsFilter.isEmpty()) {
				token = nextToken();
				// create the root param tree
				res = new FieldFilterTree(null, new FieldFilterToken(null, null));
				FieldFilterTree curTree = res;

				// start loop
				while (token != null) {
					curTree = processToken(curTree, token, prevT);
					// then current token become previous and fetch next one
					prevT = token;
					token = nextToken();
				}

				// final condition
				if (prevT != null && (prevT.getType() == OPEN_PARENTHESIS || prevT.getType() == COMMA)) {
					throw new IllegalArgumentException("Filter cannot and with a ',' or a '('.");
				}

				if (parenthesisCount != 0) {
					throw new IllegalArgumentException("No matching between '(' and ')' - diff=" + parenthesisCount);
				}
			}

		} catch (Exception e) {
			throw new FieldFilterSyntaxException(fieldsFilter, curPos, token, prevT, e);
		}

		LOG.debug("Parse field filter \"{}\" to : {}", fieldsFilter, res);

		return res;
	}

	private FieldFilterToken nextToken() {
		FieldFilterToken res = null;
		String str = "";

		for (int i = curPos; i < fieldsFilter.length(); i++) {
			char c = fieldsFilter.charAt(i);
			switch (c) {
			case ',':
			case '(':
			case ')':
				if (str.isEmpty()) {
					res = new FieldFilterToken(String.valueOf(c));
				} else {
					res = new FieldFilterToken(str);
					str = "";
				}
				break;
			default:
				str = str + c;
				break;
			}

			// if found a result leave loop
			if (res != null) {
				break;
			}
		}

		// final condition
		if (str.length() > 0) {
			res = new FieldFilterToken(str);
		}

		if (res != null) {
			curPos += res.getValue().length();
		}
		return res;

	}

	private FieldFilterTree processToken(FieldFilterTree curTree, FieldFilterToken token, FieldFilterToken prevT) {
		switch (token.getType()) {
		case FIELD_NAME:
			curTree = processParamToken(curTree, token, prevT);
			break;
		case COMMA:
			curTree = processCommaToken(curTree, token, prevT);
			break;
		case CLOSE_PARENTHESIS:
			curTree = processCloseParenthesisToken(curTree, token, prevT);
			parenthesisCount--;
			break;
		case OPEN_PARENTHESIS:
			curTree = processOpenParenthesisToken(curTree, token, prevT);
			parenthesisCount++;
			break;
		default:
			throw new IllegalArgumentException("Unknow token type : " + token.getType());
		}
		return curTree;
	}

	private FieldFilterTree processParamToken(FieldFilterTree curTree, FieldFilterToken token, FieldFilterToken prevT) {
		curTree.addChild(token);
		return curTree;
	}

	private FieldFilterTree processCommaToken(FieldFilterTree curTree, FieldFilterToken token, FieldFilterToken prevT) {
		if (prevT == null || (prevT.getType() == COMMA || prevT.getType() == OPEN_PARENTHESIS)) {
			throw new IllegalArgumentException("',' can't be after ',' , '(' or be at first position");
		}
		return curTree;
	}

	private FieldFilterTree processOpenParenthesisToken(FieldFilterTree curTree, FieldFilterToken token, FieldFilterToken prevT) {
		if (prevT == null || prevT.getType() != FIELD_NAME) {
			throw new IllegalArgumentException("'(' can't be aftet ',', '(', ')' or ba at first position.");
		}

		curTree = curTree.getChild(prevT);
		if (curTree == null) {
			throw new IllegalStateException("no child node with name '" + prevT.getValue() + "'");
		}

		return curTree;
	}

	private FieldFilterTree processCloseParenthesisToken(FieldFilterTree curTree, FieldFilterToken token, FieldFilterToken prevT) {
		if (prevT == null || prevT.getType() == COMMA || prevT.getType() == OPEN_PARENTHESIS) {
			throw new IllegalArgumentException("')' can't be after ',', '(' or be at first pos");
		}

		FieldFilterTree parentTree = curTree.getParent();
		if (parentTree == null) {
			throw new IllegalArgumentException("no '(' matching ')'");
		}

		return parentTree;
	}
}
