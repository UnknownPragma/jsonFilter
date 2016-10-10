package com.scnf.dev.json.filter.param;

public class ParameterSyntaxException extends Exception {

	private static final long serialVersionUID = 1007471999535811991L;

	public ParameterSyntaxException(String paramValue, int pos, ParamToken cur, ParamToken prev, Throwable cause) {
		super("Syntax error in parameter '" + paramValue + "' at pos '" + pos + "' - current token = " + cur + " - previous token = " + prev, cause);
	}
}
