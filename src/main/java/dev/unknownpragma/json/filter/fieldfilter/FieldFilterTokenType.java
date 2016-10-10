package com.scnf.dev.json.filter.param;

public enum ParamTokenType {
	COMMA, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, FIELD_NAME, INCLUDE_ALL, EXCLUDE_ALL;
		
	public static ParamTokenType fromValue(String value) {
		ParamTokenType t = FIELD_NAME;
		
		if(",".equals(value)) {
			t = COMMA;
		} else if("(".equals(value)) {
			t = OPEN_PARENTHESIS;
		} else if(")".equals(value)) {
			t = CLOSE_PARENTHESIS;
		}
		
		return t;
	}
}
