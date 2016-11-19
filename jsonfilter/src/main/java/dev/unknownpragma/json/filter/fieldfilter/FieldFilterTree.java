package dev.unknownpragma.json.filter.fieldfilter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

public class FieldFilterTree {

	private FieldFilterToken data;

	private FieldFilterTree parent;

	private List<FieldFilterTree> children = new ArrayList<>();

	private boolean includeField;

	public FieldFilterTree(FieldFilterTree parent, FieldFilterToken data, boolean includeField) {
		Validate.notNull(data, "'data' can't be null");

		this.parent = parent;
		this.data = data;
		this.includeField = includeField;
	}

	static FieldFilterTree createRoot() {
		return new FieldFilterTree(null, new FieldFilterToken(null, null), true);
	}

	public FieldFilterTree addChild(FieldFilterToken child, boolean includeField) {
		if (getChild(child) != null) {
			throw new IllegalArgumentException("Token " + child + " already exist in " + this);
		}

		FieldFilterTree childNode = new FieldFilterTree(this, child, includeField);
		this.children.add(childNode);

		return childNode;
	}

	public FieldFilterTree getChild(FieldFilterToken child) {
		return getChild(child.getValue());
	}

	public FieldFilterTree getChild(String childValue) {
		FieldFilterTree res = null;
		for (FieldFilterTree aChild : this.children) {
			if (aChild.getData().getValue() != null && aChild.getData().getValue().equals(childValue)) {
				res = aChild;
			}
		}
		return res;
	}

	public List<FieldFilterTree> getIncludeFieldChildren() {
		List<FieldFilterTree> res = new ArrayList<>();
		for (FieldFilterTree child : getChildren()) {
			if (child.isIncludeField()) {
				res.add(child);
			}
		}
		return res;
	}

	public FieldFilterTree getIncludeFieldChild(FieldFilterToken child) {
		FieldFilterTree res = getChild(child);
		if (res != null && !res.isIncludeField()) {
			res = null;
		}
		return res;
	}

	public FieldFilterTree getParent() {
		return parent;
	}

	public void setParent(FieldFilterTree parent) {
		this.parent = parent;
	}

	public FieldFilterToken getData() {
		return data;
	}

	public List<FieldFilterTree> getChildren() {
		return children;
	}

	public boolean isIncludeField() {
		return includeField;
	}

	public boolean isExcludeField() {
		return !includeField;
	}

	public boolean isExcludeLeafField() {
		return isExcludeField() && getChildren().isEmpty();
	}

	@Override
	public String toString() {
		String res = isIncludeField() ? "+" : "-";
		if (data.getValue() != null) {
			res = res + data.getValue();
		}
		if (children != null && !children.isEmpty()) {
			res = res + children;
		}

		return res;
	}

}
