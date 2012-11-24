package com.eyeq.pivot4j.ui.primefaces.tree;

import java.util.Collections;
import java.util.List;

import org.olap4j.metadata.Member;
import org.primefaces.model.TreeNode;

public class MemberNode extends NavigatorNode<Member> {

	/**
	 * @param parent
	 * @param member
	 */
	public MemberNode(TreeNode parent, Member member) {
		super(member);
		setParent(parent);
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
