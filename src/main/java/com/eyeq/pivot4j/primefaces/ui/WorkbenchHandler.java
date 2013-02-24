package com.eyeq.pivot4j.primefaces.ui;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.extensions.model.layout.LayoutOptions;

@ManagedBean(name = "workbenchHandler")
@RequestScoped
public class WorkbenchHandler {

	@ManagedProperty(value = "#{settings}")
	private Settings settings;

	private boolean editorPaneVisible = false;

	private boolean navigatorPaneVisible = true;

	private LayoutOptions layoutOptions;

	private Locale locale;

	/**
	 * @return the layoutOptions
	 */
	public LayoutOptions getLayoutOptions() {
		if (layoutOptions == null) {
			this.layoutOptions = new LayoutOptions();

			LayoutOptions toolbarOptions = new LayoutOptions();
			toolbarOptions.addOption("resizable", false);
			toolbarOptions.addOption("closable", false);
			toolbarOptions.addOption("size", 36);

			layoutOptions.setNorthOptions(toolbarOptions);

			LayoutOptions navigatorOptions = new LayoutOptions();
			navigatorOptions.addOption("resizable", true);
			navigatorOptions.addOption("closable", true);
			navigatorOptions.addOption("slidable", true);
			navigatorOptions.addOption("size", 280);

			layoutOptions.setWestOptions(navigatorOptions);

			LayoutOptions childWestOptions = new LayoutOptions();
			navigatorOptions.setChildOptions(childWestOptions);

			LayoutOptions cubeListOptions = new LayoutOptions();
			cubeListOptions.addOption("resizable", false);
			cubeListOptions.addOption("closable", false);
			cubeListOptions.addOption("slidable", false);
			cubeListOptions.addOption("size", 38);

			childWestOptions.setNorthOptions(cubeListOptions);

			LayoutOptions targetTreeOptions = new LayoutOptions();
			targetTreeOptions.addOption("resizable", true);
			targetTreeOptions.addOption("closable", true);
			targetTreeOptions.addOption("slidable", true);
			targetTreeOptions.addOption("size", 340);

			childWestOptions.setSouthOptions(targetTreeOptions);

			LayoutOptions contentOptions = new LayoutOptions();
			layoutOptions.setCenterOptions(contentOptions);

			LayoutOptions childCenterOptions = new LayoutOptions();
			contentOptions.setChildOptions(childCenterOptions);

			LayoutOptions filterOptions = new LayoutOptions();
			filterOptions.addOption("resizable", false);
			filterOptions.addOption("closable", true);
			filterOptions.addOption("slidable", true);
			filterOptions.addOption("size", 38);

			childCenterOptions.setNorthOptions(filterOptions);

			LayoutOptions editorOptions = new LayoutOptions();
			editorOptions.addOption("resizable", true);
			editorOptions.addOption("closable", true);
			editorOptions.addOption("slidable", true);
			editorOptions.addOption("size", 180);

			childCenterOptions.setSouthOptions(editorOptions);

			LayoutOptions editorToolBarOptions = new LayoutOptions();
			editorToolBarOptions.addOption("resizable", false);
			editorToolBarOptions.addOption("closable", false);
			editorToolBarOptions.addOption("slidable", false);
			editorToolBarOptions.addOption("size", 38);

			editorOptions.setNorthOptions(editorToolBarOptions);

			LayoutOptions editorContentOptions = new LayoutOptions();
			editorContentOptions.addOption("resizable", false);
			editorContentOptions.addOption("closable", false);
			editorContentOptions.addOption("slidable", false);
			editorContentOptions.addOption("spacing_open", 0);
			editorContentOptions.addOption("spacing_closed", 0);

			editorOptions.setChildOptions(editorContentOptions);
		}

		return layoutOptions;
	}

	/**
	 * @return
	 */
	public Locale getLocale() {
		if (locale == null) {
			FacesContext context = FacesContext.getCurrentInstance();

			HttpSession session = (HttpSession) context.getExternalContext()
					.getSession(false);

			if (session != null) {
				String key = settings.get(Settings.LOCALE_ATTRIBUTE_NAME);

				if (key != null) {
					Object value = session.getAttribute(key);
					if (value instanceof Locale) {
						this.locale = (Locale) value;
					} else if (value != null) {
						String[] args = value.toString().split("_");

						if (args.length == 1) {
							this.locale = new Locale(args[0]);
						} else if (args.length == 2) {
							this.locale = new Locale(args[0], args[1]);
						} else if (args.length == 3) {
							this.locale = new Locale(args[0], args[1], args[2]);
						}
					}
				}
			}

			if (locale == null) {
				this.locale = context.getViewRoot().getLocale();
			}
		}

		return locale;
	}

	/**
	 * @return the editorPaneVisible
	 */
	public boolean isEditorPaneVisible() {
		return editorPaneVisible;
	}

	/**
	 * @param editorPaneVisible
	 *            the editorPaneVisible to set
	 */
	public void setEditorPaneVisible(boolean editorPaneVisible) {
		this.editorPaneVisible = editorPaneVisible;
	}

	/**
	 * @return the navigatorPaneVisible
	 */
	public boolean isNavigatorPaneVisible() {
		return navigatorPaneVisible;
	}

	/**
	 * @param navigatorPaneVisible
	 *            the navigatorPaneVisible to set
	 */
	public void setNavigatorPaneVisible(boolean navigatorPaneVisible) {
		this.navigatorPaneVisible = navigatorPaneVisible;
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
}
