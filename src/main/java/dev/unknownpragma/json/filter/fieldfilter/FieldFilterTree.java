package dev.unknownpragma.json.filter.fieldfilter;

import java.util.ArrayList;
import java.util.List;

public class FieldFilterTree {

	private FieldFilterToken data;

	private FieldFilterTree parent;

	private List<FieldFilterTree> children = new ArrayList<>();

	public FieldFilterTree(FieldFilterTree parent, FieldFilterToken data) {
		this.parent = parent;
		this.data = data;

		if (data == null) {
			throw new IllegalArgumentException("'data' can't be null");
		}
	}

	public FieldFilterTree addChild(FieldFilterToken child) {
		if (getChild(child) != null) {
			throw new IllegalArgumentException("Le token " + child + " existe déjà dans le noeud " + this);
		}

		FieldFilterTree childNode = new FieldFilterTree(this, child);
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

	public String desc() {
		String res = "";
		boolean isRoot = false;
		if (data.getValue() != null) {
			res += data.getValue();
		} else {
			isRoot = true;
		}

		if (!children.isEmpty()) {
			res += isRoot?"":"(";
			boolean first = true;
			for (FieldFilterTree pt : children) {
				if(first) {
					first = false;
				} else {
					res += ",";
				}
				res += pt.desc();
			}
			res += isRoot?"":")";
		}

		return res;		
	}

	@Override
	public String toString() {
		return data.getValue() + children;
	}
	
	
}
