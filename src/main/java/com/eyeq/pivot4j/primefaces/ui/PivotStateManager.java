package com.eyeq.pivot4j.primefaces.ui;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.olap4j.OlapDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.impl.PivotModelImpl;
import com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata;
import com.eyeq.pivot4j.primefaces.datasource.DataSourceManager;
import com.eyeq.pivot4j.primefaces.state.ViewState;
import com.eyeq.pivot4j.primefaces.state.ViewStateHolder;

@ManagedBean(name = "pivotStateManager")
@ViewScoped
public class PivotStateManager {

	@ManagedProperty(value = "#{settings}")
	private Settings settings;

	@ManagedProperty(value = "#{dataSourceManager}")
	private DataSourceManager dataSourceManager;

	@ManagedProperty(value = "#{viewStateHolder}")
	private ViewStateHolder viewStateHolder;

	private String viewId;

	@PostConstruct
	protected void initialize() {
		FacesContext context = FacesContext.getCurrentInstance();

		ExternalContext externalContext = context.getExternalContext();
		Flash flash = externalContext.getFlash();

		Map<String, String> parameters = externalContext
				.getRequestParameterMap();

		this.viewId = parameters
				.get(settings.get(Settings.VIEW_PARAMETER_NAME));

		if (viewId == null) {
			this.viewId = (String) flash.get("viewId");
		}

		ViewState state = null;

		Logger log = LoggerFactory.getLogger(getClass());

		if (viewId == null) {
			this.viewId = UUID.randomUUID().toString();
		} else {
			state = viewStateHolder.getState(viewId);
		}

		if (state == null) {
			ConnectionMetadata connectionInfo = (ConnectionMetadata) flash
					.get("connectionInfo");

			OlapDataSource dataSource = dataSourceManager
					.createDataSource(connectionInfo);

			if (dataSource == null) {
				if (log.isWarnEnabled()) {
					log.warn("Unable to create Creating a new view state : "
							+ viewId);
				}

				NavigationHandler navigationHandler = context.getApplication()
						.getNavigationHandler();

				navigationHandler.handleNavigation(context, null,
						"index?faces-redirect=true");
			} else {
				PivotModel model = new PivotModelImpl(dataSource);

				state = new ViewState(viewId, connectionInfo, model);

				viewStateHolder.registerState(state);

				if (log.isInfoEnabled()) {
					log.info("Created a new view state : " + viewId);
				}
			}
		} else if (log.isInfoEnabled()) {
			log.info("Using an existing view state : " + viewId);
		}
	}

	@PreDestroy
	public void destroy() {
		viewStateHolder.unregisterState(viewId);
	}

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return viewId;
	}

	protected ViewState getState() {
		return viewStateHolder.getState(viewId);
	}

	/**
	 * @return the model
	 */
	public PivotModel getModel() {
		ViewState state = getState();
		if (state == null) {
			return null;
		}

		return state.getModel();
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		ViewState state = getState();
		if (state == null) {
			return true;
		}

		return state.isReadOnly();
	}

	/**
	 * @return the dataSourceManager
	 */
	public DataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}

	/**
	 * @param dataSourceManager
	 *            the dataSourceManager to set
	 */
	public void setDataSourceManager(DataSourceManager dataSourceManager) {
		this.dataSourceManager = dataSourceManager;
	}

	/**
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * @return the viewStateHolder
	 */
	public ViewStateHolder getViewStateHolder() {
		return viewStateHolder;
	}

	/**
	 * @param viewStateHolder
	 *            the viewStateHolder to set
	 */
	public void setViewStateHolder(ViewStateHolder viewStateHolder) {
		this.viewStateHolder = viewStateHolder;
	}

	/**
	 * @return the rendererState
	 */
	public Serializable getRendererState() {
		ViewState state = getState();
		if (state == null) {
			return null;
		}

		return state.getRendererState();
	}

	/**
	 * @param rendererState
	 *            the rendererState to set
	 */
	public void setRendererState(Serializable rendererState) {
		ViewState state = getState();
		if (state == null) {
			return;
		}

		state.setRendererState(rendererState);
	}

	public ConnectionMetadata getConnectionInfo() {
		ViewState state = getState();
		if (state == null) {
			return null;
		}

		return state.getConnectionInfo();
	}

	public void keepAlive() {
		viewStateHolder.keepAlive(viewId);
	}
}
