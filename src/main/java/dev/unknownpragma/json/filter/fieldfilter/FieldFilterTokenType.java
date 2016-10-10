package dev.unknownpragma.json.filter.fieldfilter;

public enum FieldFilterTokenType {
	COMMA, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, FIELD_NAME, INCLUDE_ALL, EXCLUDE_ALL;
		
	public static FieldFilterTokenType fromValue(String value) {
		FieldFilterTokenType t = FIELD_NAME;
		
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
