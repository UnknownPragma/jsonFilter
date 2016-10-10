package com.scnf.dev.json.filter.param;

import java.util.ArrayList;
import java.util.List;

public class ParamTree {

	private ParamToken data;

	private ParamTree parent;

	private List<ParamTree> children = new ArrayList<>();

	public ParamTree(ParamTree parent, ParamToken data) {
		this.parent = parent;
		this.data = data;

		if (data == null) {
			throw new IllegalArgumentException("'data' can't be null");
		}
	}

	public ParamTree addChild(ParamToken child) {
		if (getChild(child) != null) {
			throw new IllegalArgumentException("Le token " + child + " existe déjà dans le noeud " + this);
		}

		ParamTree childNode = new ParamTree(this, child);
		this.children.add(childNode);

		return childNode;
	}

	public ParamTree getChild(ParamToken child) {
		return getChild(child.getValue());
	}

	public ParamTree getChild(String childValue) {
		ParamTree res = null;
		for (ParamTree aChild : this.children) {
			if (aChild.getData().getValue() != null && aChild.getData().getValue().equals(childValue)) {
				res = aChild;
			}
		}
		return res;
	}

	public ParamTree getParent() {
		return parent;
	}

	public void setParent(ParamTree parent) {
		this.parent = parent;
	}

	public ParamToken getData() {
		return data;
	}

	public List<ParamTree> getChildren() {
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
			for (ParamTree pt : children) {
				if(first == true) {
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
