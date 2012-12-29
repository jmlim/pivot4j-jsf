package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.List;

import org.olap4j.metadata.MetadataElement;
import org.primefaces.model.TreeNode;

public abstract class NavigatorNode<T extends MetadataElement> implements
		TreeNode {

	private TreeNode parent;

	private T element;

	private NodeData data;

	private boolean expanded = false;

	private boolean selectable = false;

	private boolean selected = false;

	private List<TreeNode> children;

	private NodeFilter nodeFilter;

	/**
	 * @param element
	 */
	public NavigatorNode(T element) {
		this.element = element;
		this.data = createData(element);
	}

	/**
	 * @param element
	 * @return
	 */
	protected NodeData createData(T element) {
		return new NodeData(element);
	}

	/**
	 * @see org.primefaces.model.TreeNode#getParent()
	 */
	@Override
	public TreeNode getParent() {
		return parent;
	}

	public T getElement() {
		return element;
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
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public NodeData getData() {
		return data;
	}

	/**
	 * @return the nodeFilter
	 */
	public NodeFilter getNodeFilter() {
		return nodeFilter;
	}

	/**
	 * @param nodeFilter
	 *            the nodeFilter to set
	 */
	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		return getChildren().size();
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

	/**
	 * @param element
	 * @param child
	 * @return
	 */
	protected <TC extends MetadataElement> boolean configureChildNode(
			TC element, NavigatorNode<?> child) {
		child.setParent(this);

		if (nodeFilter != null) {
			if (nodeFilter.isVisible(element)) {
				child.setNodeFilter(nodeFilter);
				child.setExpanded(nodeFilter.isExpanded(element));
				child.setSelectable(nodeFilter.isSelectable(element));
				child.setSelected(nodeFilter.isSelected(element));

				NodeData data = child.getData();
				data.setSelected(nodeFilter.isActive(element));
			} else {
				return false;
			}
		}

		return true;
	}

	protected abstract List<TreeNode> createChildren();
}
