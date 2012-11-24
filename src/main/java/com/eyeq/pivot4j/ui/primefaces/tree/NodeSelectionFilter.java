package com.eyeq.pivot4j.ui.primefaces.tree;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

public interface NodeSelectionFilter {

	/**
	 * @param dimension
	 * @return
	 */
	boolean isSelected(Dimension dimension);

	/**
	 * @param hierarchy
	 * @return
	 */
	boolean isSelected(Hierarchy hierarchy);

	/**
	 * @param level
	 * @return
	 */
	boolean isSelected(Level level);

	/**
	 * @param member
	 * @return
	 */
	boolean isSelected(Member member);
}
