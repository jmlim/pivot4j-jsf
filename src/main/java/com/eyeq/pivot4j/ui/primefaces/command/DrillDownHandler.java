package com.eyeq.pivot4j.ui.primefaces.command;

import javax.faces.context.FacesContext;

import org.primefaces.component.commandbutton.CommandButton;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.Transform;
import com.eyeq.pivot4j.ui.BuildContext;

public interface DrillDownHandler<T extends Transform> {

	Class<T> getTransformType();

	/**
	 * @param context
	 * @return
	 */
	DrillDownParameters createDrillDownParameters(BuildContext context);

	/**
	 * @param parameters
	 * @param context
	 * @return
	 */
	CommandButton createCommand(DrillDownParameters parameters,
			FacesContext context);

	/**
	 * @param model
	 * @param context
	 */
	void executeCommand(PivotModel model, FacesContext context);
}
