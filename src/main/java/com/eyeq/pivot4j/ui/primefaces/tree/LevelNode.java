package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.Collections;
import java.util.List;

import org.olap4j.metadata.Level;
import org.primefaces.model.TreeNode;

public class LevelNode extends NavigatorNode<Level> {

	/**
	 * @param parent
	 * @param level
	 */
	public LevelNode(TreeNode parent, Level level) {
		super(level);
		setParent(parent);
	}

	/**
	 * @param level
	 * @return
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NavigatorNode#createData(org.olap4j.metadata.MetadataElement)
	 */
	@Override
	protected NodeData createData(Level level) {
		return new LevelNodeData(level);
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "level";
	}

	/**
	 * @see org.primefaces.model.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NavigatorNode#createChildren()
	 */
	@Override
	protected List<TreeNode> createChildren() {
		return Collections.emptyList();
	}
}
