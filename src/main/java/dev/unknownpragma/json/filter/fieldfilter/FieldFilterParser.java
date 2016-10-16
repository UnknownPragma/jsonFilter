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

	private int parenthesisCount = 0;

	public FieldFilterParser(String incFields, String excFields) {
		this.incFields = new FieldFilterTokenizer(StringUtils.trimToEmpty(incFields));
		this.excFields = new FieldFilterTokenizer(StringUtils.trimToEmpty(excFields));
	}

	public FieldFilterTree parse() throws FieldFilterSyntaxException {
		FieldFilterTree res = FieldFilterTree.createRoot();
		FieldFilterTree curTree = res;
		FieldFilterToken token = incFields.nextToken();
		FieldFilterToken prevT = null;

		try {
			// start loop
			while (token != null) {
				curTree = processToken(curTree, token, prevT);
				// then current token become previous and fetch next one
				prevT = token;
				token = incFields.nextToken();
			}

			// final condition
			if (prevT != null && (prevT.getType() == OPEN_PARENTHESIS || prevT.getType() == COMMA)) {
				throw new IllegalArgumentException("Filter cannot and with a ',' or a '('.");
			}

			if (parenthesisCount != 0) {
				throw new IllegalArgumentException("No matching between '(' and ')' - diff=" + parenthesisCount);
			}

		} catch (

		Exception e) {
			throw new FieldFilterSyntaxException(incFields, token, prevT, e);
		}

		LOG.debug("Parse field filter \"{}\" to : {}", incFields, res);

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
