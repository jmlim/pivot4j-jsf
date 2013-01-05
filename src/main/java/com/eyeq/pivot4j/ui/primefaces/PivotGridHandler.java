package com.eyeq.pivot4j.ui.primefaces;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.olap4j.Cell;
import org.olap4j.CellSetAxis;
import org.olap4j.OlapException;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.context.RequestContext;

import com.eyeq.pivot4j.ModelChangeEvent;
import com.eyeq.pivot4j.ModelChangeListener;
import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.QueryEvent;
import com.eyeq.pivot4j.QueryListener;
import com.eyeq.pivot4j.transform.NonEmpty;
import com.eyeq.pivot4j.transform.SwapAxes;
import com.eyeq.pivot4j.ui.PivotRenderer;
import com.eyeq.pivot4j.ui.command.BasicDrillThroughCommand;
import com.eyeq.pivot4j.ui.command.CellCommand;
import com.eyeq.pivot4j.ui.command.CellParameters;
import com.eyeq.pivot4j.ui.command.DrillDownCommand;

@ManagedBean(name = "pivotGridHandler")
@RequestScoped
public class PivotGridHandler implements QueryListener, ModelChangeListener {

	@ManagedProperty(value = "#{pivotModelManager.model}")
	private PivotModel model;

	@ManagedProperty(value = "#{navigatorHandler}")
	private NavigatorHandler navigator;

	private PrimeFacesPivotRenderer renderer;

	private String currentMdx;

	private Long duration;

	private DrillThroughDataModel drillThroughData;

	@PostConstruct
	protected void initialize() {
		model.addQueryListener(this);
		model.addModelChangeListener(this);

		FacesContext context = FacesContext.getCurrentInstance();

		this.renderer = new PrimeFacesPivotRenderer(context);

		renderer.initialize();
		renderer.addCommand(new DrillThroughCommandImpl(renderer));

		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();
		Serializable state = (Serializable) session.get("rendererState");

		if (state == null) {
			renderer.setShowDimensionTitle(true);
			renderer.setShowParentMembers(false);
			renderer.setHideSpans(false);
			renderer.setDrillDownMode(DrillDownCommand.MODE_POSITION);
			renderer.setEnableDrillThrough(false);
			renderer.setEnableColumnDrillDown(true);
			renderer.setEnableRowDrillDown(true);
		} else {
			renderer.restoreState(state);
		}
	}

	@PreDestroy
	protected void destroy() {
		model.removeQueryListener(this);
		model.removeModelChangeListener(this);

		if (drillThroughData != null) {
			drillThroughData.destroy();
		}
	}

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
	 * @return the navigator
	 */
	public NavigatorHandler getNavigator() {
		return navigator;
	}

	/**
	 * @param navigator
	 *            the navigator to set
	 */
	public void setNavigator(NavigatorHandler navigator) {
		this.navigator = navigator;
	}

	/**
	 * @return the pivotGrid
	 */
	public PanelGrid getPivotGrid() {
		return renderer.getComponent();
	}

	/**
	 * @param pivotGrid
	 *            the pivotGrid to set
	 */
	public void setPivotGrid(PanelGrid pivotGrid) {
		renderer.setComponent(pivotGrid);
	}

	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
	}

	public boolean isValid() {
		if (!model.isInitialized()) {
			return false;
		}

		List<CellSetAxis> axes = model.getCellSet().getAxes();
		if (axes.size() < 2) {
			return false;
		}

		return axes.get(0).getPositionCount() > 0
				&& axes.get(1).getPositionCount() > 0;
	}

	public void render() {
		if (model.isInitialized()) {
			renderer.render(model);

			FacesContext context = FacesContext.getCurrentInstance();

			Map<String, Object> session = context.getExternalContext()
					.getSessionMap();

			Serializable state = renderer.bookmarkState();
			session.put("rendererState", state);
		}
	}

	public void executeCommand() {
		FacesContext context = FacesContext.getCurrentInstance();

		Map<String, String> requestParameters = context.getExternalContext()
				.getRequestParameterMap();

		CellParameters parameters = new CellParameters();

		if (requestParameters.containsKey("axis")) {
			parameters.setAxisOrdinal(Integer.parseInt(requestParameters
					.get("axis")));
		}

		if (requestParameters.containsKey("position")) {
			parameters.setPositionOrdinal(Integer.parseInt(requestParameters
					.get("position")));
		}

		if (requestParameters.containsKey("member")) {
			parameters.setMemberOrdinal(Integer.parseInt(requestParameters
					.get("member")));
		}

		if (requestParameters.containsKey("hierarchy")) {
			parameters.setHierarchyOrdinal(Integer.parseInt(requestParameters
					.get("hierarchy")));
		}

		if (requestParameters.containsKey("cell")) {
			parameters.setCellOrdinal(Integer.parseInt(requestParameters
					.get("cell")));
		}

		CellCommand<?> command = renderer.getCommand(requestParameters
				.get("command"));
		command.execute(model, parameters);
	}

	public void executeMdx() {
		String oldMdx = model.getCurrentMdx();

		try {
			model.setMdx(currentMdx);

			if (!model.isInitialized()) {
				model.initialize();
			}
		} catch (Exception e) {
			FacesContext context = FacesContext.getCurrentInstance();

			String msg = "Failed to execute the MDX query.";

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, msg, e.getMessage()));

			model.setMdx(oldMdx);
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
		return renderer.getShowParentMembers();
	}

	/**
	 * @param showParentMembers
	 *            the showParentMembers to set
	 */
	public void setShowParentMembers(boolean showParentMembers) {
		renderer.setShowParentMembers(showParentMembers);
	}

	/**
	 * @return the hideSpans
	 */
	public boolean getHideSpans() {
		return renderer.getHideSpans();
	}

	/**
	 * @param hideSpans
	 *            the hideSpans to set
	 */
	public void setHideSpans(boolean hideSpans) {
		renderer.setHideSpans(hideSpans);
	}

	/**
	 * @return the drillThrough
	 */
	public boolean getDrillThrough() {
		return renderer.getEnableDrillThrough();
	}

	/**
	 * @param drillThrough
	 *            the drillThrough to set
	 */
	public void setDrillThrough(boolean drillThrough) {
		renderer.setEnableDrillThrough(drillThrough);

		resetDrillThrough();
	}

	public void resetDrillThrough() {
		setDrillThroughOrdinal(null);
		setDrillThroughRows(null);
	}

	/**
	 * @return the drillDownMode
	 */
	public String getDrillDownMode() {
		return renderer.getDrillDownMode();
	}

	/**
	 * @param drillDownMode
	 *            the drillDownMode to set
	 */
	public void setDrillDownMode(String drillDownMode) {
		renderer.setDrillDownMode(drillDownMode);
	}

	/**
	 * @return the swapAxes
	 */
	public boolean getSwapAxes() {
		if (!model.isInitialized()) {
			return false;
		}

		SwapAxes transform = model.getTransform(SwapAxes.class);
		return transform.isSwapAxes();
	}

	/**
	 * @param swapAxes
	 *            the swapAxes to set
	 */
	public void setSwapAxes(boolean swapAxes) {
		SwapAxes transform = model.getTransform(SwapAxes.class);
		transform.setSwapAxes(swapAxes);
	}

	/**
	 * @return the nonEmpty
	 */
	public boolean getNonEmpty() {
		if (!model.isInitialized()) {
			return false;
		}

		NonEmpty transform = model.getTransform(NonEmpty.class);
		return transform.isNonEmpty();
	}

	/**
	 * @param nonEmpty
	 *            the nonEmpty to set
	 */
	public void setNonEmpty(boolean nonEmpty) {
		NonEmpty transform = model.getTransform(NonEmpty.class);
		transform.setNonEmpty(nonEmpty);
	}

	/**
	 * @return the drillThroughOrdinal
	 */
	protected Integer getDrillThroughOrdinal() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();

		return (Integer) session.get("drillThroughOrdinal");
	}

	/**
	 * @param drillThroughOrdinal
	 *            the drillThroughOrdinal to set
	 */
	protected void setDrillThroughOrdinal(Integer drillThroughOrdinal) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();

		if (drillThroughOrdinal == null) {
			session.remove("drillThroughOrdinal");
		} else {
			session.put("drillThroughOrdinal", drillThroughOrdinal);
		}
	}

	/**
	 * @return the drillThroughRows
	 */
	protected Integer getDrillThroughRows() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();

		return (Integer) session.get("drillThroughRows");
	}

	/**
	 * @param drillThroughRows
	 *            the drillThroughRows to set
	 */
	protected void setDrillThroughRows(Integer drillThroughRows) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();

		if (drillThroughRows == null) {
			session.remove("drillThroughRows");
		} else {
			session.put("drillThroughRows", drillThroughRows);
		}
	}

	/**
	 * @return the drillThroughData
	 */
	public DrillThroughDataModel getDrillThroughData() {
		if (drillThroughData == null && model.isInitialized()) {
			Integer drillThroughOrdinal = getDrillThroughOrdinal();
			Integer drillThroughRows = getDrillThroughRows();

			if (drillThroughOrdinal == null || drillThroughRows == null) {
				return null;
			}

			Cell cell = model.getCellSet().getCell(drillThroughOrdinal);

			ResultSet resultSet;

			try {
				resultSet = cell.drillThrough();
			} catch (OlapException e) {
				throw new FacesException(e);
			}

			if (resultSet != null) {
				this.drillThroughData = new DrillThroughDataModel(resultSet);

				drillThroughData.setRowCount(drillThroughRows);
				drillThroughData.setPageSize(15);
			}
		}

		return drillThroughData;
	}

	/**
	 * @see com.eyeq.pivot4j.QueryListener#queryExecuted(com.eyeq.pivot4j.QueryEvent)
	 */
	@Override
	public void queryExecuted(QueryEvent e) {
		this.duration = e.getDuration();
	}

	/**
	 * @see com.eyeq.pivot4j.ModelChangeListener#modelInitialized(com.eyeq.pivot4j.ModelChangeEvent)
	 */
	@Override
	public void modelInitialized(ModelChangeEvent e) {
	}

	/**
	 * @see com.eyeq.pivot4j.ModelChangeListener#modelDestroyed(com.eyeq.pivot4j.ModelChangeEvent)
	 */
	@Override
	public void modelDestroyed(ModelChangeEvent e) {
	}

	/**
	 * @see com.eyeq.pivot4j.ModelChangeListener#modelChanged(com.eyeq.pivot4j.ModelChangeEvent)
	 */
	@Override
	public void modelChanged(ModelChangeEvent e) {
	}

	/**
	 * @see com.eyeq.pivot4j.ModelChangeListener#structureChanged(com.eyeq.pivot4j.ModelChangeEvent)
	 */
	@Override
	public void structureChanged(ModelChangeEvent e) {
		render();
	}

	/**
	 * Workaround to implement lazy rendering due to limitation in Olap4J's API
	 * :
	 * 
	 * @see http://sourceforge.net/p/olap4j/bugs/15/
	 */
	class DrillThroughCommandImpl extends BasicDrillThroughCommand {

		/**
		 * @param renderer
		 */
		public DrillThroughCommandImpl(PivotRenderer renderer) {
			super(renderer);
		}

		/**
		 * @see com.eyeq.pivot4j.ui.command.BasicDrillThroughCommand#execute(com.eyeq.pivot4j.PivotModel,
		 *      com.eyeq.pivot4j.ui.command.CellParameters)
		 */
		@Override
		public ResultSet execute(PivotModel model, CellParameters parameters) {
			Integer drillThroughRows = null;
			Integer drillThroughOrdinal = null;

			ResultSet result = super.execute(model, parameters);

			if (result != null) {
				int totalRows = 0;

				try {
					while (result.next()) {
						totalRows++;
					}
				} catch (SQLException e) {
					throw new FacesException(e);
				} finally {
					try {
						result.close();
					} catch (SQLException e) {
					}
				}

				drillThroughRows = totalRows;
				drillThroughOrdinal = parameters.getCellOrdinal();

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("drillThrough();");
			}

			PivotGridHandler.this.setDrillThroughRows(drillThroughRows);
			PivotGridHandler.this.setDrillThroughOrdinal(drillThroughOrdinal);

			return result;
		}
	}
}
