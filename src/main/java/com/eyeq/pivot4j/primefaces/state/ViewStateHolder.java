package com.eyeq.pivot4j.primefaces.state;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata;
import com.eyeq.pivot4j.primefaces.ui.Settings;

@ManagedBean(name = "viewStateHolder")
@SessionScoped
public class ViewStateHolder {

	private Map<String, ViewState> states = new HashMap<String, ViewState>();

	private static final long MINUTE = 60;

	protected Logger log = LoggerFactory.getLogger(getClass());

	@ManagedProperty(value = "#{settings}")
	private Settings settings;

	private Timer timer;

	private long checkInterval = 1 * MINUTE;

	private long keepAliveInterval = 1 * MINUTE;

	private long expires = 5 * MINUTE;

	private String sessionId;

	@PostConstruct
	protected void initialize() {
		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) context.getSession(true);
		this.sessionId = session.getId();

		if (log.isInfoEnabled()) {
			log.info("Initializing view state holder for session : "
					+ sessionId);
			log.info(String.format("Check interval : %d secs.", checkInterval));
			log.info(String.format("Keep alive interval : %d secs.",
					keepAliveInterval));
			log.info(String.format("Expires : %d secs.", expires));
		}

		// As there's a no reliable way to clean up resources on a view scoped
		// managed bean while the session is alive, we need to periodically
		// check for stale connections and close them.
		this.timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				checkAbandonedModels();
			}
		}, MINUTE * 1000, checkInterval * 1000);
	}

	@PreDestroy
	protected void destroy() {
		if (log.isInfoEnabled()) {
			log.info("Destroying view state holder for session : " + sessionId);
		}

		timer.cancel();
		timer.purge();

		clearStates();
	}

	protected synchronized void checkAbandonedModels() {
		if (log.isDebugEnabled()) {
			log.debug("Checking for abandoned view states for session : "
					+ sessionId);
			log.debug("Current view state count for session : " + states.size());
		}

		Set<String> keys = new HashSet<String>(states.keySet());

		Date now = new Date();

		for (String key : keys) {
			ViewState state = states.get(key);

			long elapsed = now.getTime() - state.getLastActive().getTime();

			if (expires * 1000 <= elapsed) {
				if (log.isInfoEnabled()) {
					log.info("Found an abandoned view sate : " + state);
				}

				unregisterState(state);
			}
		}
	}

	/**
	 * @param id
	 */
	public synchronized void keepAlive(String id) {
		ViewState state = getState(id);

		if (log.isDebugEnabled()) {
			log.debug("Received a keep alive request : " + state);
		}

		if (state != null) {
			state.update();
		}
	}

	/**
	 * @param id
	 * @return
	 */
	public ViewState getState(String id) {
		return states.get(id);
	}

	/**
	 * @param state
	 */
	public synchronized void registerState(ViewState state) {
		if (state == null) {
			throw new IllegalArgumentException(
					"Required argument 'state' is null.");
		}

		ViewState oldState = states.get(state.getId());
		if (oldState != null) {
			if (oldState == state) {
				return;
			}

			unregisterState(oldState);
		}

		states.put(state.getId(), state);

		if (log.isInfoEnabled()) {
			log.info("View state is registered : " + state);
			log.info("Current view state count for session : " + states.size());
		}
	}

	/**
	 * @param id
	 */
	public synchronized void unregisterState(String id) {
		if (id == null) {
			throw new IllegalArgumentException(
					"Required argument 'id' is null.");
		}

		ViewState state = states.get(id);

		if (state != null) {
			unregisterState(state);
		}
	}

	/**
	 * @param state
	 */
	protected synchronized void unregisterState(ViewState state) {
		PivotModel model = state.getModel();
		if (model != null && model.isInitialized()) {
			model.destroy();
		}

		states.remove(state.getId());

		if (log.isInfoEnabled()) {
			log.info("View state is unregistered : " + state);
			log.info("Current view state count for session : " + states.size());
		}
	}

	protected synchronized void clearStates() {
		for (ViewState state : states.values()) {
			unregisterState(state);
		}
	}

	/**
	 * Start a new view with an empty(default) connection info.
	 * 
	 * @return
	 */
	public void createView() {
		FacesContext context = FacesContext.getCurrentInstance();
		Flash flash = context.getExternalContext().getFlash();

		String viewId = UUID.randomUUID().toString();

		ConnectionMetadata connectionInfo = new ConnectionMetadata();

		flash.put("viewId", viewId);
		flash.put("connectionInfo", connectionInfo);

		String paramName = settings.get(Settings.VIEW_PARAMETER_NAME);

		StringBuilder builder = new StringBuilder();
		builder.append("view");
		builder.append("?faces-redirect=true");
		builder.append("&");
		builder.append(paramName);
		builder.append("=");
		builder.append(viewId);

		NavigationHandler navigationHandler = context.getApplication()
				.getNavigationHandler();
		navigationHandler.handleNavigation(context, null, builder.toString());
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
	 * @return the checkInterval
	 */
	public long getCheckInterval() {
		return checkInterval;
	}

	/**
	 * @param checkInterval
	 *            the checkInterval to set
	 */
	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	/**
	 * @return the keepAliveInterval
	 */
	public long getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * @param keepAliveInterval
	 *            the keepAliveInterval to set
	 */
	public void setKeepAliveInterval(long keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	/**
	 * @return the expires
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(long expires) {
		this.expires = expires;
	}
}