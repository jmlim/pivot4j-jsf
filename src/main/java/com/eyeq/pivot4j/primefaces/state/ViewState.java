package com.eyeq.pivot4j.primefaces.state;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata;

public class ViewState {

	private String id;

	private boolean readOnly = false;

	private Date lastActive = new Date();

	private ConnectionMetadata connectionInfo;

	private PivotModel model;

	private Serializable rendererState;

	/**
	 * @param id
	 */
	public ViewState(String id) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Required argument 'id' is missing.");
		}

		this.id = id;
	}

	/**
	 * @param id
	 * @param connectionInfo
	 * @param model
	 */
	public ViewState(String id, ConnectionMetadata connectionInfo,
			PivotModel model) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Required argument 'id' is missing.");
		}

		this.id = id;
		this.connectionInfo = connectionInfo;
		this.model = model;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the lastActive
	 */
	public Date getLastActive() {
		return lastActive;
	}

	/**
	 * @return the connectionInfo
	 */
	public ConnectionMetadata getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * @param connectionInfo
	 *            the connectionInfo to set
	 */
	public void setConnectionInfo(ConnectionMetadata connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly
	 *            the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
	 * @return the rendererState
	 */
	public Serializable getRendererState() {
		return rendererState;
	}

	/**
	 * @param rendererState
	 *            the rendererState to set
	 */
	public void setRendererState(Serializable rendererState) {
		this.rendererState = rendererState;
	}

	public void update() {
		this.lastActive = new Date();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("connectionInfo", connectionInfo)
				.append("lastActive", lastActive).toString();
	}
}
