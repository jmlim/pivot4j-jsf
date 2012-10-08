package com.eyeq.pivot4j.ui.primefaces.table;

import org.olap4j.Axis;
import org.olap4j.CellSetAxis;

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
	 * @see com.eyeq.pivot4j.ui.html.HtmlTableBuilder#createRow(com.eyeq.pivot4j.
	 *      ui.BuildContext, com.eyeq.pivot4j.ui.html.HtmlTableModel, int)
	 */
	@Override
	protected HtmlTableRow createRow(BuildContext context,
			HtmlTableModel table, int rowIndex) {
		HtmlTableRow row = super.createRow(context, table, rowIndex);

		if (rowIndex % 2 == 0) {
			row.setStyleClass("ui-datatable-even");
		} else {
			row.setStyleClass("ui-datatable-odd");
		}

		return row;
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
		CellSetAxis axis = context.getAxis();

		String style = null;
		String styleClass = null;

		if (context.getCell() != null) {
			if (rowIndex % 2 == 0) {
				styleClass = "value-cell cell-even";
			} else {
				styleClass = "value-cell cell-odd";
			}
		} else if (context.getMember() != null) {
			styleClass = "ui-widget-header";

			if (axis != null && axis.getAxisOrdinal() == Axis.ROWS) {
				int padding = 10 * (1 + context.getMember().getDepth());
				style = "padding-left: " + padding + "px";
				styleClass = "row-hdr-cell ui-widget-header";
			} else {
				styleClass = "col-hdr-cell";
			}
		}

		DrillableTableCell cell = new DrillableTableCell(type);

		cell.setColSpan(colSpan);
		cell.setRowSpan(rowSpan);
		cell.setHeader(type != CellType.Value);
		cell.setStyle(style);
		cell.setStyleClass(styleClass);

		DrillDownParameters parameters = drillDownMode.getHandler()
				.createDrillDownParameters(context);
		cell.setDrillDownParameters(parameters);

		return cell;
	}
}
