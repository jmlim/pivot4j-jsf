package com.eyeq.pivot4j.ui.primefaces.command;

import com.eyeq.pivot4j.transform.Transform;

public enum DrillDownMode {

	Member(new DrillDownOnMemberHandler()), Position(
			new DrillDownOnPositionHandler()), Replace(
			new DrillReplaceHandler());

	private DrillDownHandler<? extends Transform> handler;

	DrillDownMode(DrillDownHandler<? extends Transform> handler) {
		this.handler = handler;
	}

	public DrillDownHandler<? extends Transform> getHandler() {
		return handler;
	}
}
