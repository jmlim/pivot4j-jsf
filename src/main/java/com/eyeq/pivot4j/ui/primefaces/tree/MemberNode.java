package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;

import org.olap4j.OlapException;
import org.olap4j.metadata.Member;
import org.primefaces.model.TreeNode;

public class MemberNode extends NavigatorNode<Member> {

	/**
	 * @param parent
	 * @param member
	 * @param selection
	 * @param updateSelection
	 */
	public MemberNode(Member member) {
		super(member);
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "member";
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
				MemberNode node = new MemberNode(member);

				if (configureChildNode(member, node)) {
					children.add(node);
				}
			}

			return children;
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}
}
