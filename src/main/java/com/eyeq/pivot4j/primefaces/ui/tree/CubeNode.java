package com.eyeq.pivot4j.primefaces.ui.tree;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.MetadataElement;
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
	 * @see com.eyeq.pivot4j.primefaces.ui.tree.NavigatorNode#createChildren()
	 */
	@Override
	protected List<TreeNode> createChildren() {
		List<Dimension> dimensions = getElement().getDimensions();

		List<TreeNode> children = new ArrayList<TreeNode>(dimensions.size());
		for (Dimension dimension : dimensions) {
			MetadataElement element;

			NavigatorNode<?> node;

			if (dimension.getHierarchies().size() == 1) {
				element = dimension.getDefaultHierarchy();

				node = new HierarchyNode((Hierarchy) element);
			} else {
				element = dimension;

				node = new DimensionNode(dimension);
			}

			if (configureChildNode(element, node)) {
				children.add(node);
			}
		}

		return children;
	}
}
