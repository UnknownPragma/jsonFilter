package dev.unknownpragma.json.filter.fieldfilter;

public class FieldFilterSyntaxException extends Exception {

	private static final long serialVersionUID = 1007471999535811991L;

	public FieldFilterSyntaxException(String paramValue, int pos, FieldFilterToken cur, FieldFilterToken prev, Throwable cause) {
		super("Syntax error in parameter '" + paramValue + "' at pos '" + pos + "' - current token = " + cur + " - previous token = " + prev, cause);
	}
}
