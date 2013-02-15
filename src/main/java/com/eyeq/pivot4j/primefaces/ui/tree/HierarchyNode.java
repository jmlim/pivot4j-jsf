package com.eyeq.pivot4j.primefaces.ui.tree;

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
	 * @param hierarchy
	 */
	public HierarchyNode(Hierarchy hierarchy) {
		super(hierarchy);
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
	 * @see com.eyeq.pivot4j.primefaces.ui.tree.NavigatorNode#createChildren()
	 */
	@Override
	protected List<TreeNode> createChildren() {
		Hierarchy hierarchy = getElement();

		try {
			if (hierarchy.getDimension().getDimensionType() == Type.MEASURE) {
				List<? extends Member> members = hierarchy.getRootMembers();
				List<TreeNode> children = new ArrayList<TreeNode>(
						members.size());

				for (Member member : members) {
					MeasureNode node = new MeasureNode(this, member);

					if (configureChildNode(member, node)) {
						children.add(node);
					}
				}

				return children;
			} else {
				List<Level> levels = hierarchy.getLevels();
				List<TreeNode> children = new ArrayList<TreeNode>(levels.size());

				for (Level level : levels) {
					LevelNode node = new LevelNode(level);

					if (configureChildNode(level, node)) {
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
