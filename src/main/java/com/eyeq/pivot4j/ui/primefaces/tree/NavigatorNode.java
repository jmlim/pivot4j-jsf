package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.List;

import org.primefaces.model.TreeNode;

public abstract class NavigatorNode implements TreeNode {

	private TreeNode parent;

	private boolean expanded = false;

	private boolean selectable = false;

	private boolean selected = false;

	private List<TreeNode> children;

	/**
	 * @see org.primefaces.model.TreeNode#getParent()
	 */
	@Override
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * @see org.primefaces.model.TreeNode#setParent(org.primefaces.model.TreeNode)
	 */
	@Override
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isExpanded()
	 */
	@Override
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @see org.primefaces.model.TreeNode#setExpanded(boolean)
	 */
	@Override
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isSelectable()
	 */
	@Override
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @see org.primefaces.model.TreeNode#setSelectable(boolean)
	 */
	@Override
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @see org.primefaces.model.TreeNode#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		if (children == null) {
			this.children = createChildren();
		}
		return children.size();
	}

	/**
	 * @see org.primefaces.model.TreeNode#getChildren()
	 */
	@Override
	public List<TreeNode> getChildren() {
		if (children == null) {
			this.children = createChildren();
		}
		return children;
	}

	protected abstract List<TreeNode> createChildren();
}
