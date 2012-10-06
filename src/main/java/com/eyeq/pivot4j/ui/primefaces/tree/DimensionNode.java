package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.primefaces.model.TreeNode;

public class DimensionNode extends NavigatorNode {

	private Dimension dimension;

	/**
	 * @param parent
	 * @param dimension
	 */
	public DimensionNode(TreeNode parent, Dimension dimension) {
		setParent(parent);
		this.dimension = dimension;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "dimension";
	}

	/**
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public Dimension getData() {
		return dimension;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NavigatorNode#createChildren()
	 */
	@Override
	protected List<TreeNode> createChildren() {
		List<Hierarchy> hierarchies = dimension.getHierarchies();

		List<TreeNode> children = new ArrayList<TreeNode>(hierarchies.size());
		for (Hierarchy hierarchy : hierarchies) {
			children.add(new HierarchyNode(this, hierarchy));
		}

		return children;
	}
}
