package com.scnf.dev.json.filter.param;

import static com.scnf.dev.json.filter.param.ParamTokenType.COMMA;
import static com.scnf.dev.json.filter.param.ParamTokenType.FIELD_NAME;
import static com.scnf.dev.json.filter.param.ParamTokenType.OPEN_PARENTHESIS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamParser {

	private static final Logger LOG = LoggerFactory.getLogger(ParamParser.class);

	private String fieldsFilter;

	private int curPos = 0;

	private int parenthesisCount = 0;

	public ParamParser(String fieldsFilter) {
		if (fieldsFilter == null) {
			throw new IllegalArgumentException("'fieldsFilter' argument can't be null");
		}

		this.fieldsFilter = fieldsFilter;
	}

	public ParamTree parse() throws ParameterSyntaxException {
		ParamTree res = null;
		ParamToken token = null;
		ParamToken prevT = null;

		try {
			// walk through the string
			if (!fieldsFilter.isEmpty()) {
				token = nextToken();
				// create the root param tree
				res = new ParamTree(null, new ParamToken(null, null));
				ParamTree curTree = res;

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
			throw new ParameterSyntaxException(fieldsFilter, curPos, token, prevT, e);
		}

		LOG.debug("Parse field filter \"{}\" to : {}", fieldsFilter, res);

		return res;
	}

	private ParamToken nextToken() {
		ParamToken res = null;
		String str = "";

		for (int i = curPos; i < fieldsFilter.length(); i++) {
			char c = fieldsFilter.charAt(i);
			switch (c) {
			case ',':
			case '(':
			case ')':
				if (str.isEmpty()) {
					res = new ParamToken(String.valueOf(c));
				} else {
					res = new ParamToken(str);
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
			res = new ParamToken(str);
		}

		if (res != null) {
			curPos += res.getValue().length();
		}
		return res;

	}

	private ParamTree processToken(ParamTree curTree, ParamToken token, ParamToken prevT) {
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

	private ParamTree processParamToken(ParamTree curTree, ParamToken token, ParamToken prevT) {
		curTree.addChild(token);
		return curTree;
	}

	private ParamTree processCommaToken(ParamTree curTree, ParamToken token, ParamToken prevT) {
		if (prevT == null || (prevT.getType() == COMMA || prevT.getType() == OPEN_PARENTHESIS)) {
			throw new IllegalArgumentException("',' can't be after ',' , '(' or be at first position");
		}
		return curTree;
	}

	private ParamTree processOpenParenthesisToken(ParamTree curTree, ParamToken token, ParamToken prevT) {
		if (prevT == null || prevT.getType() != FIELD_NAME) {
			throw new IllegalArgumentException("'(' can't be aftet ',', '(', ')' or ba at first position.");
		}

		curTree = curTree.getChild(prevT);
		if (curTree == null) {
			throw new IllegalStateException("no child node with name '" + prevT.getValue() + "'");
		}

		return curTree;
	}

	private ParamTree processCloseParenthesisToken(ParamTree curTree, ParamToken token, ParamToken prevT) {
		if (prevT == null || prevT.getType() == COMMA || prevT.getType() == OPEN_PARENTHESIS) {
			throw new IllegalArgumentException("')' can't be after ',', '(' or be at first pos");
		}

		ParamTree parentTree = curTree.getParent();
		if (parentTree == null) {
			throw new IllegalArgumentException("no '(' matching ')'");
		}

		return parentTree;
	}
}
