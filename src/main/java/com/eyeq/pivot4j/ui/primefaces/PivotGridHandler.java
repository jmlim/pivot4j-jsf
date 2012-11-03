package com.eyeq.pivot4j.ui.primefaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.olap4j.metadata.Member;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.TreeNode;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.NonEmpty;
import com.eyeq.pivot4j.transform.SwapAxes;
import com.eyeq.pivot4j.ui.PivotRenderer;
import com.eyeq.pivot4j.ui.command.CellCommand;
import com.eyeq.pivot4j.ui.command.CellParameters;
import com.eyeq.pivot4j.ui.command.DrillDownCommand;

@ManagedBean
@RequestScoped
public class PivotGridHandler {

	@ManagedProperty(value = "#{pivotModelManager.model}")
	private PivotModel model;

	private PanelGrid pivotGrid;

	@ManagedProperty(value = "#{navigatorHandler}")
	private NavigatorHandler navigator;

	private String drillDownMode = DrillDownCommand.MODE_POSITION;

	private boolean showParentMembers = false;

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
		return pivotGrid;
	}

	/**
	 * @param pivotGrid
	 *            the pivotGrid to set
	 */
	public void setPivotGrid(PanelGrid pivotGrid) {
		this.pivotGrid = pivotGrid;
	}

	protected PivotRenderer createRenderer() {
		FacesContext context = FacesContext.getCurrentInstance();

		PivotRenderer renderer = new PrimeFacesPivotRenderer(context, pivotGrid);
		renderer.initialize();

		renderer.setShowDimensionTitle(true);
		renderer.setShowParentMembers(showParentMembers);
		renderer.setHideSpans(hideSpans);
		renderer.setDrillDownMode(drillDownMode);
		renderer.setEnableColumnDrillDown(true);
		renderer.setEnableRowDrillDown(true);

		return renderer;
	}

	public void render() {
		PivotRenderer renderer = createRenderer();
		renderer.render(model);
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

		PivotRenderer renderer = createRenderer();

		CellCommand command = renderer.getCommand(requestParameters
				.get("command"));
		command.execute(model, parameters);
	}

	public void executeMdx() {
		String oldMdx = model.getCurrentMdx();

		try {
			model.setMdx(currentMdx);
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
		}
	}

	public void toggleNonEmpty() {
		NonEmpty transform = model.getTransform(NonEmpty.class);
		if (transform.isNonEmpty() != nonEmpty) {
			transform.setNonEmpty(nonEmpty);
		}
	}

	/**
	 * @return the drillDownMode
	 */
	public String getDrillDownMode() {
		return drillDownMode;
	}

	/**
	 * @param drillDownMode
	 *            the drillDownMode to set
	 */
	public void setDrillDownMode(String drillDownMode) {
		this.drillDownMode = drillDownMode;
	}

	/**
	 * @param e
	 */
	public void onLevelDrop(DragDropEvent e) {
		// there should be a cleaner way to get data from dropped component.
		// it's a limitation on PFs' side :
		// http://code.google.com/p/primefaces/issues/detail?id=2781
		String[] segments = e.getDragId().split(":");
		String[] indexSegments = segments[segments.length - 2].split("_");

		List<Integer> indexes = new ArrayList<Integer>(indexSegments.length);
		for (String index : indexSegments) {
			indexes.add(Integer.parseInt(index));
		}

		TreeNode node = findDraggedNode(navigator.getRootNode(), indexes);
		Member member = (Member) node.getData();

		//TODO
		String id = createIdFromUniqueName(member.getUniqueName());
		System.out.println(id);
	}

	/**
	 * @param name
	 * @return
	 */
	private String createIdFromUniqueName(String name) {
		return name.replaceAll("[\\[\\]]", "").replaceAll("[\\s\\.]", "_")
				.toLowerCase();
	}

	/**
	 * @param parent
	 * @param indexes
	 * @return
	 */
	protected TreeNode findDraggedNode(TreeNode parent, List<Integer> indexes) {
		if (indexes.size() > 1) {
			return findDraggedNode(parent.getChildren().get(indexes.get(0)),
					indexes.subList(1, indexes.size()));
		} else {
			return parent.getChildren().get(indexes.get(0));
		}
	}

	/**
	 * @param event
	 */
	public void onColumnReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex()
				+ ", Column index: " + event.getColumnIndex()
				+ ", Sender index: " + event.getSenderColumnIndex());

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, message);
	}
}
