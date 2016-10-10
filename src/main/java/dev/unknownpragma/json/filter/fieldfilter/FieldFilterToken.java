package com.scnf.dev.json.filter.param;

import static com.scnf.dev.json.filter.param.ParamTokenType.*;

public class ParamToken {
	
	private ParamTokenType type;

	private String value;
		
	public ParamToken(ParamTokenType type, String value) {
		this.type = type;
		this.value = value;
			
		if(type == FIELD_NAME && (value == null || value.isEmpty())) {
			throw new IllegalArgumentException("token '" + FIELD_NAME.name() + "' must have a value." );
		}		
	}
	
	public ParamToken(String value) {
		this(ParamTokenType.fromValue(value),value);
	}
	
	public ParamTokenType getType() {
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
