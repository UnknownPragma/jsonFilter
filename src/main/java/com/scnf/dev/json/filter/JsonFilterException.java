package com.scnf.dev.json.filter;

public class JsonFilterException extends Exception {

	private static final long serialVersionUID = 1206403796907620822L;

	public JsonFilterException(String includeFields, String excludeFields, Throwable cause) {
		super(String.format("Erreur lors de l'application du filtre json [includes={}, excludes={}]",includeFields, excludeFields), cause);
	}

}
