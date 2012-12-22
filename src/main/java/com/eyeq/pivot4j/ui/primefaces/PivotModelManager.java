package com.eyeq.pivot4j.ui.primefaces;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.QueryEvent;
import com.eyeq.pivot4j.QueryListener;
import com.eyeq.pivot4j.impl.PivotModelImpl;

@ManagedBean(name = "pivotModelManager")
@SessionScoped
public class PivotModelManager implements QueryListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private PivotModel model;

	private String sessionId;

	private String cubeName;

	@ManagedProperty(value = "#{dataSourceManager}")
	private DataSourceManager dataSourceManager;

	@PostConstruct
	protected void initialize() {
		if (logger.isInfoEnabled()) {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) context.getExternalContext()
					.getSession(true);

			this.sessionId = session.getId();
		}

		this.model = new PivotModelImpl(dataSourceManager.getDataSource());

		model.addQueryListener(this);
	}

	@PreDestroy
	protected void destroy() {
		if (logger.isInfoEnabled() && sessionId != null) {
			logger.info("Destroying existing pivot model instance  : "
					+ sessionId);

			this.sessionId = null;
		}

		model.destroy();
		model.removeQueryListener(this);
	}

	/**
	 * @return the model
	 */
	public PivotModel getModel() {
		return model;
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
	 * @return the cubeName
	 */
	public String getCubeName() {
		return cubeName;
	}

	/**
	 * @param cubeName
	 *            the cubeName to set
	 */
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public void onCubeChange() {
		if (model.isInitialized()) {
			model.destroy();
		}

		if (StringUtils.isEmpty(cubeName)) {
			return;
		}

		String mdx = String.format(
				"select {} on COLUMNS, {} on ROWS from [%s]", cubeName);

		model.setMdx(mdx);

		if (logger.isInfoEnabled()) {
			logger.info("Initializing new pivot model for session : "
					+ sessionId);
			logger.info("Initial MDX : " + mdx);
		}

		model.initialize();
	}

	/**
	 * @see com.eyeq.pivot4j.QueryListener#queryExecuted(com.eyeq.pivot4j.QueryEvent)
	 */
	@Override
	public void queryExecuted(QueryEvent e) {
		if (model.getCube() == null) {
			this.cubeName = null;
		} else {
			this.cubeName = model.getCube().getName();
		}
	}
}
