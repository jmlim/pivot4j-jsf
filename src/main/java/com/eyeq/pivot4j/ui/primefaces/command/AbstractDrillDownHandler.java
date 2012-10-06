package com.eyeq.pivot4j.ui.primefaces.command;

import com.eyeq.pivot4j.transform.Transform;
import com.eyeq.pivot4j.ui.BuildContext;

public abstract class AbstractDrillDownHandler<T extends Transform> implements
		DrillDownHandler<T> {

	/**
	 * @param context
	 * @return
	 */
	protected T createTransform(BuildContext context) {
		return context.getModel().getTransform(getTransformType());
	}
}
