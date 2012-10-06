package com.eyeq.pivot4j.ui.primefaces.command;

import java.util.Map;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;
import org.primefaces.component.commandbutton.CommandButton;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.DrillExpandMember;
import com.eyeq.pivot4j.ui.BuildContext;

public class DrillDownOnMemberHandler extends
		AbstractDrillDownHandler<DrillExpandMember> {

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#getTransformType()
	 */
	@Override
	public Class<DrillExpandMember> getTransformType() {
		return DrillExpandMember.class;
	}

	/**
	 * @see com.eyeq.pivot4j.ui.primefaces.command.DrillDownHandler#createDrillDownParameters(com.eyeq.pivot4j.ui.BuildContext)
	 */
	@Override
	public DrillDownParameters createDrillDownParameters(BuildContext context) {
		Member member = context.getMember();
		if (member == null) {
			return null;
		}

		Position position;

		CellSetAxis axis = context.getAxis();
		if (axis.getAxisOrdinal() == Axis.ROWS) {
			position = context.getRowPosition();
		} else if (axis.getAxisOrdinal() == Axis.COLUMNS) {
			position = context.getColumnPosition();
		} else {
			return null;
		}

		DrillExpandMember transform = createTransform(context);

		boolean drillDown;
		if (transform.canExpand(member)) {
			drillDown = true;
		} else if (transform.canCollapse(member)) {
			drillDown = false;
		} else {
			return null;
		}

		DrillDownParameters parameters = new DrillDownParameters();
		parameters.setAxisOrdinal(axis.getAxisOrdinal().axisOrdinal());
		parameters.setPositionOrdinal(position.getOrdinal());
		parameters.setMemberUniqueName(member.getUniqueName());
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
			button.setIcon("ui-icon-carat-1-e");
		} else {
			button.setIcon("ui-icon-carat-1-s");
		}

		UIParameter axisParam = new UIParameter();
		axisParam.setName("axis");
		axisParam.setValue(parameters.getAxisOrdinal());

		button.getChildren().add(axisParam);

		UIParameter positionParam = new UIParameter();
		positionParam.setName("position");
		positionParam.setValue(parameters.getPositionOrdinal());

		button.getChildren().add(positionParam);

		UIParameter memberParam = new UIParameter();
		memberParam.setName("member");
		memberParam.setValue(parameters.getMemberUniqueName());

		button.getChildren().add(memberParam);

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

		int axisOrdinal = Integer.parseInt(parameters.get("axis"));
		int positionOrdinal = Integer.parseInt(parameters.get("position"));

		String memberName = parameters.get("member");

		boolean drillDown = Boolean.parseBoolean(parameters.get("drillDown"));

		CellSet cellSet = model.getCellSet();

		CellSetAxis axis = cellSet.getAxes().get(axisOrdinal);
		Position position = axis.getPositions().get(positionOrdinal);

		Member member = null;

		for (Member m : position.getMembers()) {
			if (m.getUniqueName().equals(memberName)) {
				member = m;
				break;
			}
		}

		DrillExpandMember transform = model.getTransform(getTransformType());
		if (drillDown) {
			transform.expand(member);
		} else {
			transform.collapse(member);
		}
	}
}
