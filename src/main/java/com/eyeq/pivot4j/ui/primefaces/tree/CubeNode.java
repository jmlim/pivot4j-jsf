package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.primefaces.model.TreeNode;

public class CubeNode extends NavigatorNode<Cube> {

	/**
	 * @param cube
	 */
	public CubeNode(Cube cube) {
		super(cube);
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "cube";
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
		List<Dimension> dimensions = getElement().getDimensions();

		NodeSelectionFilter filter = getNodeFilter();

		List<TreeNode> children = new ArrayList<TreeNode>(dimensions.size());
		for (Dimension dimension : dimensions) {
			if (dimension.getHierarchies().size() == 1) {
				Hierarchy hierarchy = dimension.getDefaultHierarchy();

				HierarchyNode node = new HierarchyNode(this, hierarchy);
				node.setNodeFilter(filter);

				if (filter != null) {
					node.getData().setSelected(filter.isSelected(hierarchy));
				}

				children.add(node);
			} else {
				DimensionNode node = new DimensionNode(this, dimension);

				node.setNodeFilter(filter);

				if (filter != null) {
					node.getData().setSelected(filter.isSelected(dimension));
				}

				children.add(node);
			}
		}

		return children;
	}
}
