package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.primefaces.model.TreeNode;

public class CubeNode extends NavigatorNode {

	private Cube cube;

	/**
	 * @param cube
	 */
	public CubeNode(Cube cube) {
		this.cube = cube;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "cube";
	}

	/**
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public Cube getData() {
		return cube;
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
		List<Dimension> dimensions = cube.getDimensions();

		List<TreeNode> children = new ArrayList<TreeNode>(dimensions.size());
		for (Dimension dimension : dimensions) {
			if (dimension.getHierarchies().size() == 1) {
				children.add(new HierarchyNode(this, dimension
						.getDefaultHierarchy()));
			} else {
				children.add(new DimensionNode(this, dimension));
			}
		}

		return children;
	}
}
