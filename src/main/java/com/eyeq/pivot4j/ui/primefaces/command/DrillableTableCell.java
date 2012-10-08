package com.eyeq.pivot4j.ui.primefaces.command;

import com.eyeq.pivot4j.ui.CellType;
import com.eyeq.pivot4j.ui.html.HtmlTableCell;

public class DrillableTableCell extends HtmlTableCell {

	private static final long serialVersionUID = 3778436905387338131L;

	private DrillDownParameters drillDownParameters;

	/**
	 * @param type
	 */
	public DrillableTableCell(CellType type) {
		super(type);
	}

	/**
	 * @return the drillDownParameters
	 */
	public DrillDownParameters getDrillDownParameters() {
		return drillDownParameters;
	}

	/**
	 * @param drillDownParameters
	 *            the drillDownParameters to set
	 */
	public void setDrillDownParameters(DrillDownParameters drillDownParameters) {
		this.drillDownParameters = drillDownParameters;
	}
}
