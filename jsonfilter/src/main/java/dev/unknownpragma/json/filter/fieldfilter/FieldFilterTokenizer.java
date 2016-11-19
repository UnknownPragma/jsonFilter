package dev.unknownpragma.json.filter.fieldfilter;

class FieldFilterTokenizer {

	private String fieldFilter;

	private int curPos = 0;

	private FieldFilterToken prefetchToken = null;

	private boolean prefetch = false;
	
	FieldFilterTokenizer(String fieldFilter) {
		this.fieldFilter = fieldFilter;
	}

	FieldFilterToken nextToken() {
		if (prefetch) {
			prefetch = false;
			return prefetchToken;
		} 
		
		FieldFilterToken res = null;
		String str = "";
		for (int i = curPos; i < fieldFilter.length(); i++) {
			char c = fieldFilter.charAt(i);
			if (c == ',' || c == '(' || c == ')') {
				if (str.isEmpty()) {
					res = new FieldFilterToken(String.valueOf(c));
				} else {
					res = new FieldFilterToken(str);						
				}
				str = "";
				break;
			} else {
				str = str.concat(String.valueOf(c));				
			}
		}

		// final condition
		if (str.length() > 0) {
			res = new FieldFilterToken(str);
		}
		
		if(res != null) {
			curPos += res.getValue().length();
		}
		
		return res;
	}

	public FieldFilterToken prefetchToken() {
		if(!prefetch) {
			prefetchToken = nextToken();
			prefetch = true;
		}		
		return prefetchToken;
	}
	
	String getFieldFilter() {
		return fieldFilter;
	}

	int getCurPos() {
		return curPos;
	}

	@Override
	public String toString() {
		return "FieldFilterTokenizer [fieldFilter=" + fieldFilter + "]";
	}
}
