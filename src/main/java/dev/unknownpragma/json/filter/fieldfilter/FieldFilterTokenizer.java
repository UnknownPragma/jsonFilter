package dev.unknownpragma.json.filter.fieldfilter;

public class FieldFilterTokenizer {

	private String fieldFilter;

	private int curPos = 0;

	FieldFilterTokenizer(String fieldFilter) {
		this.fieldFilter = fieldFilter;
	}

	FieldFilterToken nextToken() {
		FieldFilterToken res = null;
		String str = "";

		for (int i = curPos; i < fieldFilter.length(); i++) {
			char c = fieldFilter.charAt(i);
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

	String getFieldFilter() {
		return fieldFilter;
	}

	int getCurPos() {
		return curPos;
	}

	
}
