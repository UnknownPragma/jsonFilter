package dev.unknownpragma.json.filter.fieldfilter;

public class FieldFilterSyntaxException extends Exception {

	private static final long serialVersionUID = 1007471999535811991L;

	public FieldFilterSyntaxException(FieldFilterTokenizer tokenizer, FieldFilterToken cur, FieldFilterToken prev, Throwable cause) {
		super("Syntax error in parameter '" + tokenizer.getFieldFilter() + "' at pos '" + tokenizer.getCurPos() + "' - current token = " + cur + " - previous token = " + prev, cause);
	}
}
