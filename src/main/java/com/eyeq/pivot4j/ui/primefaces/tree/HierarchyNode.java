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

public class HierarchyNode extends NavigatorNode {

	private Hierarchy hierarchy;

	/**
	 * @param parent
	 * @param hierarchy
	 */
	public HierarchyNode(TreeNode parent, Hierarchy hierarchy) {
		setParent(parent);

		try {
			setExpanded(hierarchy.getDimension().getDimensionType() == Type.MEASURE);
		} catch (OlapException e) {
			throw new FacesException();
		}

		this.hierarchy = hierarchy;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "hierarchy";
	}

	/**
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public Hierarchy getData() {
		return hierarchy;
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

		try {
			if (hierarchy.getDimension().getDimensionType() == Type.MEASURE) {
				List<? extends Member> members = hierarchy.getRootMembers();
				List<TreeNode> children = new ArrayList<TreeNode>(
						members.size());

				for (Member member : members) {
					children.add(new MemberNode(this, member));
				}

				return children;
			} else {
				List<Level> levels = hierarchy.getLevels();
				List<TreeNode> children = new ArrayList<TreeNode>(levels.size());

				for (Level level : levels) {
					children.add(new LevelNode(this, level));
				}

				return children;
			}
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}
}
