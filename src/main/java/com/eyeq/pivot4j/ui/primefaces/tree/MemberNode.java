package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;

import org.olap4j.OlapException;
import org.olap4j.metadata.Member;
import org.primefaces.model.TreeNode;

import com.eyeq.pivot4j.util.MemberSelection;

public class MemberNode extends NavigatorNode<Member> {

	private MemberSelection selection;

	/**
	 * @param parent
	 * @param member
	 * @param selection
	 */
	public MemberNode(TreeNode parent, Member member, MemberSelection selection) {
		super(member);
		setParent(parent);

		this.selection = selection;

		com.eyeq.pivot4j.util.TreeNode<Member> node = selection
				.findChild(member);
		if (node != null) {
			boolean selected = selection.isSelected(member);

			setExpanded(selected && node.getChildCount() > 0);
			setSelectable(!selected);

			getData().setSelected(selected);
		} else {
			setSelectable(true);
		}
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "member";
	}

	/**
	 * @return the selection
	 */
	protected MemberSelection getSelection() {
		return selection;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		try {
			return getElement().getChildMemberCount() == 0;
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.tree.NavigatorNode#createChildren()
	 */
	@Override
	protected List<TreeNode> createChildren() {
		try {
			List<? extends Member> members = getElement().getChildMembers();

			List<TreeNode> children = new ArrayList<TreeNode>(members.size());

			for (Member member : members) {
				MemberNode node = new MemberNode(this, member, selection);
				children.add(node);
			}

			return children;
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}
}
