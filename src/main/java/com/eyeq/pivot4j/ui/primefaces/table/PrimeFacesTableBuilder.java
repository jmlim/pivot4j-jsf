package com.eyeq.pivot4j.ui.primefaces.table;

import org.olap4j.Axis;
import org.olap4j.CellSetAxis;

import com.eyeq.pivot4j.ui.BuildContext;
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
	 * @see com.eyeq.pivot4j.ui.base.HtmlTableBuilder#createCell(com.eyeq.pivot4j.ui.BuildContext,
	 *      com.eyeq.pivot4j.ui.base.HtmlTableModel,
	 *      com.eyeq.pivot4j.ui.base.HtmlTableRow, int, int, int, int)
	 */
	@Override
	protected HtmlTableCell createCell(BuildContext context,
			HtmlTableModel table, HtmlTableRow row, int colIndex, int rowIndex,
			int colSpan, int rowSpan) {
		CellSetAxis axis = context.getAxis();

		String label = null;

		String style = null;
		String styleClass = null;

		boolean header = false;

		if (context.getCell() != null) {
			label = context.getCell().getFormattedValue();

			if (rowIndex % 2 == 0) {
				styleClass = "value-cell cell-even";
			} else {
				styleClass = "value-cell cell-odd";
			}
		} else if (context.getMember() != null) {
			label = context.getMember().getCaption();
			header = true;

			styleClass = "ui-widget-header";

			if (axis != null && axis.getAxisOrdinal() == Axis.ROWS) {
				int padding = 10 * (1 + context.getMember().getDepth());
				style = "padding-left: " + padding + "px";
				styleClass = "row-hdr-cell ui-widget-header";
			} else {
				styleClass = "col-hdr-cell";
			}
		} else if (context.getHierarchy() != null) {
			label = context.getHierarchy().getDimension().getCaption();
			header = true;
		}

		DrillableTableCell cell = new DrillableTableCell();

		cell.setLabel(label);
		cell.setColSpan(colSpan);
		cell.setRowSpan(rowSpan);
		cell.setHeader(header);
		cell.setStyle(style);
		cell.setStyleClass(styleClass);

		DrillDownParameters parameters = drillDownMode.getHandler()
				.createDrillDownParameters(context);
		cell.setDrillDownParameters(parameters);

		return cell;
	}
}
