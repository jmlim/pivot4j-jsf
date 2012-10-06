package com.eyeq.pivot4j.ui.primefaces.command;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.primefaces.component.commandbutton.CommandButton;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.DrillReplace;
import com.eyeq.pivot4j.ui.BuildContext;

public class DrillReplaceHandler extends AbstractDrillDownHandler<DrillReplace> {

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#getTransformType()
	 */
	@Override
	public Class<DrillReplace> getTransformType() {
		return DrillReplace.class;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#createDrillDownParameters(com.eyeq.pivot4j.ui.BuildContext)
	 */
	@Override
	public DrillDownParameters createDrillDownParameters(BuildContext context) {
		Hierarchy hierarchy = context.getHierarchy();
		Member member = context.getMember();

		if (member == null && hierarchy == null) {
			return null;
		}

		Position position = null;

		CellSetAxis axis = context.getAxis();
		if (axis.getAxisOrdinal() == Axis.ROWS) {
			position = context.getRowPosition();
		} else if (axis.getAxisOrdinal() == Axis.COLUMNS) {
			position = context.getColumnPosition();
		}

		DrillReplace transform = createTransform(context);

		boolean drillDown;
		if (member != null && transform.canDrillDown(member)) {
			drillDown = true;
		} else if (hierarchy != null && transform.canDrillUp(hierarchy)) {
			drillDown = false;
		} else {
			return null;
		}

		DrillDownParameters parameters = new DrillDownParameters();
		parameters.setAxisOrdinal(axis.getAxisOrdinal().axisOrdinal());

		if (member != null) {
			parameters.setPositionOrdinal(position.getOrdinal());
			parameters.setMemberUniqueName(member.getUniqueName());
		}

		if (hierarchy != null) {
			parameters.setHierarchyUniqueName(hierarchy.getUniqueName());
		}

		parameters.setDrillDown(drillDown);

		return parameters;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#createCommand(com.eyeq.pivot4j.ui.primefaces.command.DrillDownParameters,
	 *      javax.faces.context.FacesContext)
	 */
	@Override
	public CommandButton createCommand(DrillDownParameters parameters,
			FacesContext context) {
		CommandButton button = new CommandButton();
		button.setStyleClass("member-icon");

		if (parameters.isDrillDown()) {
			button.setIcon("ui-icon-arrowthick-1-e");
		} else {
			button.setIcon("ui-icon-arrowthick-1-n");
		}

		UIParameter axisParam = new UIParameter();
		axisParam.setName("axis");
		axisParam.setValue(parameters.getAxisOrdinal());

		button.getChildren().add(axisParam);

		if (parameters.getMemberUniqueName() != null) {
			UIParameter positionParam = new UIParameter();
			positionParam.setName("position");
			positionParam.setValue(parameters.getPositionOrdinal());

			button.getChildren().add(positionParam);

			UIParameter memberParam = new UIParameter();
			memberParam.setName("member");
			memberParam.setValue(parameters.getMemberUniqueName());

			button.getChildren().add(memberParam);
		} else {
			UIParameter hierarchyParam = new UIParameter();
			hierarchyParam.setName("hierarchy");
			hierarchyParam.setValue(parameters.getHierarchyUniqueName());

			button.getChildren().add(hierarchyParam);
		}

		UIParameter drillDownParam = new UIParameter();
		drillDownParam.setName("drillDown");
		drillDownParam.setValue(parameters.isDrillDown());

		button.getChildren().add(drillDownParam);

		return button;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#executeCommand(com.eyeq.pivot4j.PivotModel,
	 *      javax.faces.context.FacesContext)
	 */
	@Override
	public void executeCommand(PivotModel model, FacesContext context) {
		Map<String, String> parameters = context.getExternalContext()
				.getRequestParameterMap();

		boolean drillDown = Boolean.parseBoolean(parameters.get("drillDown"));

		int axisOrdinal = Integer.parseInt(parameters.get("axis"));

		CellSet cellSet = model.getCellSet();
		CellSetAxis axis = cellSet.getAxes().get(axisOrdinal);

		DrillReplace transform = model.getTransform(getTransformType());

		String memberName = parameters.get("member");
		if (memberName != null) {
			int positionOrdinal = Integer.parseInt(parameters.get("position"));
			Position position = axis.getPositions().get(positionOrdinal);

			Member member = null;
			for (Member m : position.getMembers()) {
				if (m.getUniqueName().equals(memberName)) {
					member = m;
					break;
				}
			}

			if (drillDown) {
				transform.drillDown(member);
			}
		} else {
			String hierarchyName = parameters.get("hierarchy");

			List<Member> members = axis.getPositions().get(0).getMembers();

			Hierarchy hierarchy = null;
			for (Member m : members) {
				if (m.getHierarchy().getUniqueName().equals(hierarchyName)) {
					hierarchy = m.getHierarchy();
					break;
				}
			}

			if (!drillDown) {
				transform.drillUp(hierarchy);
			}
		}
	}
}
