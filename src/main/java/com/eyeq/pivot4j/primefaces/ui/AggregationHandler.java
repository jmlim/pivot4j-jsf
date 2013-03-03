package com.eyeq.pivot4j.primefaces.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.olap4j.Axis;

import com.eyeq.pivot4j.ui.aggregator.AggregatorFactory;
import com.eyeq.pivot4j.ui.aggregator.AggregatorPosition;

@ManagedBean(name = "aggregationHandler")
@RequestScoped
public class AggregationHandler {

	private static final String NONE = "NONE";

	@ManagedProperty(value = "#{pivotGridHandler.renderer}")
	private PrimeFacesPivotRenderer renderer;

	private List<SelectItem> aggregations;

	private String columnAggregation = NONE;

	private String columnHierarchyAggregation = NONE;

	private String columnMemberAggregation = NONE;

	private String rowAggregation = NONE;

	private String rowHierarchyAggregation = NONE;

	private String rowMemberAggregation = NONE;

	@PostConstruct
	protected void initialize() {
		this.columnAggregation = StringUtils.defaultString(renderer
				.getAggregatorName(Axis.COLUMNS, AggregatorPosition.Grand),
				NONE);
		this.columnHierarchyAggregation = StringUtils.defaultString(renderer
				.getAggregatorName(Axis.COLUMNS, AggregatorPosition.Hierarchy),
				NONE);
		this.columnMemberAggregation = StringUtils.defaultString(renderer
				.getAggregatorName(Axis.COLUMNS, AggregatorPosition.Member),
				NONE);
		this.rowAggregation = StringUtils
				.defaultString(renderer.getAggregatorName(Axis.ROWS,
						AggregatorPosition.Grand), NONE);
		this.rowHierarchyAggregation = StringUtils.defaultString(renderer
				.getAggregatorName(Axis.ROWS, AggregatorPosition.Hierarchy),
				NONE);
		this.rowMemberAggregation = StringUtils.defaultString(renderer
				.getAggregatorName(Axis.ROWS, AggregatorPosition.Member), NONE);
	}

	/**
	 * @param axis
	 * @param position
	 * @return
	 */
	protected String getAggregatorName(Axis axis, AggregatorPosition position) {
		String name = renderer.getAggregatorName(axis, position);
		if (name == null) {
			name = NONE;
		}

		return name;
	}

	/**
	 * @return the aggregations
	 */
	public List<SelectItem> getAggregations() {
		if (aggregations == null) {
			FacesContext context = FacesContext.getCurrentInstance();

			ResourceBundle bundle = context.getApplication().getResourceBundle(
					context, "msg");

			this.aggregations = new ArrayList<SelectItem>();

			aggregations.add(new SelectItem(NONE, bundle
					.getString("label.none")));

			AggregatorFactory factory = renderer.getAggregatorFactory();

			if (factory != null) {
				List<String> names = factory.getAvailableAggregations();
				for (String name : names) {
					String label = bundle.getString("label.aggregation.type."
							+ name);

					aggregations.add(new SelectItem(name, label));
				}
			}
		}

		return aggregations;
	}

	public void apply() {
		if (NONE.equals(columnAggregation)) {
			renderer.setAggregatorName(Axis.COLUMNS, AggregatorPosition.Grand,
					null);
		} else {
			renderer.setAggregatorName(Axis.COLUMNS, AggregatorPosition.Grand,
					columnAggregation);
		}

		if (NONE.equals(columnHierarchyAggregation)) {
			renderer.setAggregatorName(Axis.COLUMNS,
					AggregatorPosition.Hierarchy, null);
		} else {
			renderer.setAggregatorName(Axis.COLUMNS,
					AggregatorPosition.Hierarchy, columnHierarchyAggregation);
		}

		if (NONE.equals(columnMemberAggregation)) {
			renderer.setAggregatorName(Axis.COLUMNS, AggregatorPosition.Member,
					null);
		} else {
			renderer.setAggregatorName(Axis.COLUMNS, AggregatorPosition.Member,
					columnMemberAggregation);
		}

		if (NONE.equals(rowAggregation)) {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Grand,
					null);
		} else {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Grand,
					rowAggregation);
		}

		if (NONE.equals(rowHierarchyAggregation)) {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Hierarchy,
					null);
		} else {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Hierarchy,
					rowHierarchyAggregation);
		}

		if (NONE.equals(rowMemberAggregation)) {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Member,
					null);
		} else {
			renderer.setAggregatorName(Axis.ROWS, AggregatorPosition.Member,
					rowMemberAggregation);
		}
	}

	/**
	 * @return the renderer
	 */
	public PrimeFacesPivotRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer
	 *            the renderer to set
	 */
	public void setRenderer(PrimeFacesPivotRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return the columnAggregation
	 */
	public String getColumnAggregation() {
		return columnAggregation;
	}

	/**
	 * @param columnAggregation
	 *            the columnAggregation to set
	 */
	public void setColumnAggregation(String columnAggregation) {
		this.columnAggregation = columnAggregation;
	}

	/**
	 * @return the columnHierarchyAggregation
	 */
	public String getColumnHierarchyAggregation() {
		return columnHierarchyAggregation;
	}

	/**
	 * @param columnHierarchyAggregation
	 *            the columnHierarchyAggregation to set
	 */
	public void setColumnHierarchyAggregation(String columnHierarchyAggregation) {
		this.columnHierarchyAggregation = columnHierarchyAggregation;
	}

	/**
	 * @return the columnMemberAggregation
	 */
	public String getColumnMemberAggregation() {
		return columnMemberAggregation;
	}

	/**
	 * @param columnMemberAggregation
	 *            the columnMemberAggregation to set
	 */
	public void setColumnMemberAggregation(String columnMemberAggregation) {
		this.columnMemberAggregation = columnMemberAggregation;
	}

	/**
	 * @return the rowAggregation
	 */
	public String getRowAggregation() {
		return rowAggregation;
	}

	/**
	 * @param rowAggregation
	 *            the rowAggregation to set
	 */
	public void setRowAggregation(String rowAggregation) {
		this.rowAggregation = rowAggregation;
	}

	/**
	 * @return the rowHierarchyAggregation
	 */
	public String getRowHierarchyAggregation() {
		return rowHierarchyAggregation;
	}

	/**
	 * @param rowHierarchyAggregation
	 *            the rowHierarchyAggregation to set
	 */
	public void setRowHierarchyAggregation(String rowHierarchyAggregation) {
		this.rowHierarchyAggregation = rowHierarchyAggregation;
	}

	/**
	 * @return the rowMemberAggregation
	 */
	public String getRowMemberAggregation() {
		return rowMemberAggregation;
	}

	/**
	 * @param rowMemberAggregation
	 *            the rowMemberAggregation to set
	 */
	public void setRowMemberAggregation(String rowMemberAggregation) {
		this.rowMemberAggregation = rowMemberAggregation;
	}
}
