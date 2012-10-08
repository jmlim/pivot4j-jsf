package com.eyeq.pivot4j.ui.primefaces.table;

import com.eyeq.pivot4j.ui.BuildContext;
import com.eyeq.pivot4j.ui.CellType;
import com.eyeq.pivot4j.ui.html.HtmlTableBuilder;
import com.eyeq.pivot4j.ui.html.HtmlTableCell;
import com.eyeq.pivot4j.ui.html.HtmlTableModel;
import com.eyeq.pivot4j.ui.html.HtmlTableRow;
import com.eyeq.pivot4j.ui.primefaces.command.DrillDownMode;
import com.eyeq.pivot4j.ui.primefaces.command.DrillDownParameters;
import com.eyeq.pivot4j.ui.primefaces.command.DrillableTableCell;

public class PrimeFacesTableBuilder extends HtmlTableBuilder {

	private DrillDownMode drillDownMode;

	/**
	 * @param drillDownMode
	 */
	public PrimeFacesTableBuilder(DrillDownMode drillDownMode) {
		this.drillDownMode = drillDownMode;

		setColumnTitleStyleClass("col-hdr-cell");
		setColumnHeaderStyleClass("col-hdr-cell");
		setRowTitleStyleClass("row-hdr-cell ui-widget-header");
		setRowHeaderStyleClass("row-hdr-cell ui-widget-header");
		setEvenRowStyleClass("ui-datatable-even");
		setOddRowStyleClass("ui-datatable-odd");
	}

	/**
	 * @return the drillDownMode
	 */
	public DrillDownMode getDrillDownMode() {
		return drillDownMode;
	}

	/**
	 * @param drillDownMode
	 *            the drillDownMode to set
	 */
	public void setDrillDownMode(DrillDownMode drillDownMode) {
		this.drillDownMode = drillDownMode;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.html.HtmlTableBuilder#createCell(com.eyeq.pivot4j.ui.BuildContext,
	 *      com.eyeq.pivot4j.ui.html.HtmlTableModel,
	 *      com.eyeq.pivot4j.ui.html.HtmlTableRow, com.eyeq.pivot4j.ui.CellType,
	 *      int, int, int, int)
	 */
	@Override
	protected HtmlTableCell createCell(BuildContext context,
			HtmlTableModel table, HtmlTableRow row, CellType type,
			int colIndex, int rowIndex, int colSpan, int rowSpan) {
		DrillableTableCell cell = new DrillableTableCell(type);

		cell.setColSpan(colSpan);
		cell.setRowSpan(rowSpan);

		return cell;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.html.HtmlTableBuilder#configureCell(com.eyeq.pivot4j.ui.BuildContext,
	 *      com.eyeq.pivot4j.ui.html.HtmlTableModel,
	 *      com.eyeq.pivot4j.ui.html.HtmlTableRow, int, int,
	 *      com.eyeq.pivot4j.ui.html.HtmlTableCell)
	 */
	@Override
	protected void configureCell(BuildContext context, HtmlTableModel table,
			HtmlTableRow row, int colIndex, int rowIndex, HtmlTableCell cell) {
		super.configureCell(context, table, row, colIndex, rowIndex, cell);

		if (cell.getType() == CellType.Value) {
			// PrimeFaces' Row class doesn't have the styleClass property.
			if (rowIndex % 2 == 0) {
				cell.setStyleClass("value-cell cell-even");
			} else {
				cell.setStyleClass("value-cell cell-odd");
			}
		}

		DrillDownParameters parameters = drillDownMode.getHandler()
				.createDrillDownParameters(context);
		((DrillableTableCell) cell).setDrillDownParameters(parameters);
	}
}
