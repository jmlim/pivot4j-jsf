package com.eyeq.pivot4j.ui.primefaces;

import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.primefaces.component.column.Column;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.NonEmpty;
import com.eyeq.pivot4j.transform.SwapAxes;
import com.eyeq.pivot4j.ui.html.HtmlTableBuilder;
import com.eyeq.pivot4j.ui.html.HtmlTableCell;
import com.eyeq.pivot4j.ui.html.HtmlTableModel;
import com.eyeq.pivot4j.ui.html.HtmlTableRow;
import com.eyeq.pivot4j.ui.primefaces.command.DrillDownMode;
import com.eyeq.pivot4j.ui.primefaces.command.DrillDownParameters;
import com.eyeq.pivot4j.ui.primefaces.command.DrillableTableCell;
import com.eyeq.pivot4j.ui.primefaces.table.PrimeFacesTableBuilder;

@ManagedBean
@RequestScoped
public class PivotGridHandler {

	@ManagedProperty(value = "#{pivotModelManager.model}")
	private PivotModel model;

	private PanelGrid pivotGrid;

	private DrillDownMode drillDownMode = DrillDownMode.Position;

	private boolean showParentMembers = false;

	private boolean showDimensionTitle = true;

	private boolean hideSpans = false;

	private boolean swapAxes = false;

	private boolean nonEmpty = false;

	private String currentMdx;

	/**
	 * @return the model
	 */
	public PivotModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(PivotModel model) {
		this.model = model;
	}

	/**
	 * @return the pivotGrid
	 */
	public PanelGrid getPivotGrid() {
		return pivotGrid;
	}

	/**
	 * @param pivotGrid
	 *            the pivotGrid to set
	 */
	public void setPivotGrid(PanelGrid pivotGrid) {
		if (this.pivotGrid == null) {
			this.pivotGrid = pivotGrid;
			buildPivotGrid();
		}

		this.pivotGrid = pivotGrid;
	}

	public void buildPivotGrid() {
		HtmlTableBuilder builder = new PrimeFacesTableBuilder(drillDownMode);
		builder.setShowDimensionTitle(showDimensionTitle);
		builder.setShowParentMembers(showParentMembers);
		builder.setHideSpans(hideSpans);

		HtmlTableModel table = builder.build(model);

		pivotGrid.getChildren().clear();

		HtmlPanelGroup header = new HtmlPanelGroup();
		header.setId("pivot-header");

		pivotGrid.getFacets().put("header", header);

		FacesContext context = FacesContext.getCurrentInstance();

		buildTableRows(header, table.getHeaders(), "chdr-drill", context);
		buildTableRows(pivotGrid, table.getRows(), "rhdr-drill", context);
	}

	/**
	 * @param parent
	 * @param rows
	 * @param commandPrefix
	 * @param context
	 */
	protected void buildTableRows(UIComponent parent, List<HtmlTableRow> rows,
			String commandPrefix, FacesContext context) {
		Application application = context.getApplication();

		ExpressionFactory factory = application.getExpressionFactory();

		int index = 0;
		for (HtmlTableRow row : rows) {
			Row tableRow = new Row();

			for (HtmlTableCell cell : row.getCells()) {
				Column column = new Column();
				column.setColspan(cell.getColSpan());
				column.setRowspan(cell.getRowSpan());
				column.setStyle(cell.getStyle());
				column.setStyleClass(cell.getStyleClass());

				if (cell instanceof DrillableTableCell) {
					DrillableTableCell drillableCell = (DrillableTableCell) cell;
					DrillDownParameters parameters = drillableCell
							.getDrillDownParameters();

					if (parameters != null) {
						CommandButton command = drillDownMode.getHandler()
								.createCommand(parameters, context);
						command.setId(commandPrefix + (index++));

						MethodExpression expression = factory
								.createMethodExpression(context.getELContext(),
										"#{pivotGridHandler.executeCommand}",
										Void.class, new Class<?>[0]);
						command.setActionExpression(expression);
						command.setUpdate(":grid-form,:editor-form");

						column.getChildren().add(command);
					}
				}

				HtmlOutputText label = new HtmlOutputText();
				label.setValue(cell.getLabel());

				column.getChildren().add(label);
				tableRow.getChildren().add(column);
			}

			parent.getChildren().add(tableRow);
		}
	}

	public void executeCommand() {
		drillDownMode.getHandler().executeCommand(model,
				FacesContext.getCurrentInstance());
		buildPivotGrid();
	}

	public void executeMdx() {
		String oldMdx = model.getCurrentMdx();

		try {
			model.setMdx(currentMdx);
			buildPivotGrid();
		} catch (Exception e) {
			FacesContext context = FacesContext.getCurrentInstance();

			String msg = "Failed to execute the MDX query.";

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, msg, e.getMessage()));

			model.setMdx(oldMdx);
			buildPivotGrid();
		}
	}

	/**
	 * @return the currentMdx
	 */
	public String getCurrentMdx() {
		return model.getCurrentMdx();
	}

	/**
	 * @param currentMdx
	 */
	public void setCurrentMdx(String currentMdx) {
		this.currentMdx = currentMdx;
	}

	/**
	 * @return the showParentMembers
	 */
	public boolean getShowParentMembers() {
		return showParentMembers;
	}

	/**
	 * @param showParentMembers
	 *            the showParentMembers to set
	 */
	public void setShowParentMembers(boolean showParentMembers) {
		this.showParentMembers = showParentMembers;
	}

	/**
	 * @return the showDimensionTitle
	 */
	public boolean getShowDimensionTitle() {
		return showDimensionTitle;
	}

	/**
	 * @param showDimensionTitle
	 *            the showDimensionTitle to set
	 */
	public void setShowDimensionTitle(boolean showDimensionTitle) {
		this.showDimensionTitle = showDimensionTitle;
	}

	/**
	 * @return the hideSpans
	 */
	public boolean getHideSpans() {
		return hideSpans;
	}

	/**
	 * @param hideSpans
	 *            the hideSpans to set
	 */
	public void setHideSpans(boolean hideSpans) {
		this.hideSpans = hideSpans;
	}

	/**
	 * @return the swapAxes
	 */
	public boolean getSwapAxes() {
		return swapAxes;
	}

	/**
	 * @param swapAxes
	 *            the swapAxes to set
	 */
	public void setSwapAxes(boolean swapAxes) {
		this.swapAxes = swapAxes;
	}

	/**
	 * @return the nonEmpty
	 */
	public boolean getNonEmpty() {
		return nonEmpty;
	}

	/**
	 * @param nonEmpty
	 *            the nonEmpty to set
	 */
	public void setNonEmpty(boolean nonEmpty) {
		this.nonEmpty = nonEmpty;
	}

	public void toggleSwapAxes() {
		SwapAxes transform = model.getTransform(SwapAxes.class);
		if (transform.isSwapAxes() != swapAxes) {
			transform.setSwapAxes(swapAxes);
			buildPivotGrid();
		}
	}

	public void toggleNonEmpty() {
		NonEmpty transform = model.getTransform(NonEmpty.class);
		if (transform.isNonEmpty() != nonEmpty) {
			transform.setNonEmpty(nonEmpty);
			buildPivotGrid();
		}
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
}
