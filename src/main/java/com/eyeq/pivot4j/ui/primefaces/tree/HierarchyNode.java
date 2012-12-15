package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;

import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.primefaces.model.TreeNode;

public class HierarchyNode extends NavigatorNode<Hierarchy> {

	/**
	 * @param parent
	 * @param hierarchy
	 */
	public HierarchyNode(TreeNode parent, Hierarchy hierarchy) {
		super(hierarchy);
		setParent(parent);

		try {
			setExpanded(hierarchy.getDimension().getDimensionType() == Type.MEASURE);
		} catch (OlapException e) {
			throw new FacesException();
		}
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "hierarchy";
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
		NodeSelectionFilter filter = getNodeFilter();

		Hierarchy hierarchy = getElement();

		try {
			if (hierarchy.getDimension().getDimensionType() == Type.MEASURE) {
				List<? extends Member> members = hierarchy.getRootMembers();
				List<TreeNode> children = new ArrayList<TreeNode>(
						members.size());

				for (Member member : members) {
					if (filter == null || !filter.isSelected(member)) {
						MeasureNode node = new MeasureNode(this, member);
						node.setNodeFilter(filter);

						children.add(node);
					}
				}

				return children;
			} else {
				List<Level> levels = hierarchy.getLevels();
				List<TreeNode> children = new ArrayList<TreeNode>(levels.size());

				for (Level level : levels) {
					if (filter == null || !filter.isSelected(level)) {
						LevelNode node = new LevelNode(this, level);
						node.setNodeFilter(filter);

						children.add(node);
					}
				}

				return children;
			}
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}
}
