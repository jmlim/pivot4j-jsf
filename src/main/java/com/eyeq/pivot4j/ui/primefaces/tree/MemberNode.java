package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;

import org.olap4j.OlapException;
import org.olap4j.metadata.Member;
import org.primefaces.model.TreeNode;

public class MemberNode extends NavigatorNode {

	private Member member;

	/**
	 * @param parent
	 * @param member
	 */
	public MemberNode(TreeNode parent, Member member) {
		setParent(parent);
		this.member = member;
	}

	/**
	 * @see org.primefaces.model.TreeNode#getType()
	 */
	@Override
	public String getType() {
		return "member";
	}

	/**
	 * @see org.primefaces.model.TreeNode#getData()
	 */
	@Override
	public Member getData() {
		return member;
	}

	/**
	 * @see org.primefaces.model.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		try {
			return member.getChildMemberCount() == 0;
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
			List<? extends Member> members = member.getChildMembers();

			List<TreeNode> children = new ArrayList<TreeNode>(members.size());
			for (Member member : members) {
				children.add(new MemberNode(this, member));
			}

			return children;
		} catch (OlapException e) {
			throw new FacesException(e);
		}
	}
}
