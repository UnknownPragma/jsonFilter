package dev.unknownpragma.json.filter.fieldfilter;

import static dev.unknownpragma.json.filter.fieldfilter.FieldFilterTokenType.*;

public class FieldFilterToken {
	
	private FieldFilterTokenType type;

	private String value;
		
	public FieldFilterToken(FieldFilterTokenType type, String value) {
		this.type = type;
		this.value = value;
			
		if(type == FIELD_NAME && (value == null || value.isEmpty())) {
			throw new IllegalArgumentException("token '" + FIELD_NAME.name() + "' must have a value." );
		}		
	}
	
	public FieldFilterToken(String value) {
		this(FieldFilterTokenType.fromValue(value),value);
	}
	
	public FieldFilterTokenType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
