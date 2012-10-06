package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.Collections;
import java.util.List;

import org.olap4j.metadata.Level;
import org.primefaces.model.TreeNode;

public class LevelNode extends NavigatorNode {

	private Level level;

	/**
	 * @param parent
	 * @param level
	 */
	public LevelNode(TreeNode parent, Level level) {
		setParent(parent);
		this.level = level;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "level";
	}

	/**
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public Level getData() {
		return level;
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
