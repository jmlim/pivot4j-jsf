package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.primefaces.model.TreeNode;

public class DimensionNode extends NavigatorNode<Dimension> {

	/**
	 * @param parent
	 * @param dimension
	 */
	public DimensionNode(TreeNode parent, Dimension dimension) {
		super(dimension);
		setParent(parent);
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "dimension";
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
		List<Hierarchy> hierarchies = getElement().getHierarchies();

		NodeSelectionFilter filter = getNodeFilter();

		List<TreeNode> children = new ArrayList<TreeNode>(hierarchies.size());
		for (Hierarchy hierarchy : hierarchies) {
			HierarchyNode node = new HierarchyNode(this, hierarchy);
			node.setNodeFilter(filter);

			if (filter != null) {
				node.getData().setSelected(filter.isSelected(hierarchy));
			}

			children.add(node);
		}

		return children;
	}
}
