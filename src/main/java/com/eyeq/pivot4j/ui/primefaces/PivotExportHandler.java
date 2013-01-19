package com.eyeq.pivot4j.ui.primefaces;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;

import com.eyeq.pivot4j.export.poi.ExcelExporter;
import com.eyeq.pivot4j.export.poi.Format;

@ManagedBean(name = "pivotExportHandler")
@RequestScoped
public class PivotExportHandler {

	@ManagedProperty(value = "#{pivotGridHandler}")
	private PivotGridHandler gridHandler;

	/**
	 * @return the gridHandler
	 */
	public PivotGridHandler getGridHandler() {
		return gridHandler;
	}

	/**
	 * @param gridHandler
	 *            the gridHandler to set
	 */
	public void setGridHandler(PivotGridHandler gridHandler) {
		this.gridHandler = gridHandler;
	}

	public void exportExcel() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		ExternalContext externalContext = context.getExternalContext();

		Map<String, String> parameters = externalContext
				.getRequestParameterMap();

		Format format;

		if (parameters.containsKey("format")) {
			format = Format.valueOf(parameters.get("format"));
		} else {
			format = Format.HSSF;
		}

		exportExcel(format);

		context.responseComplete();
	}

	/**
	 * @param format
	 * @throws IOException
	 */
	protected void exportExcel(Format format) throws IOException {
		ExcelExporter exporter = new ExcelExporter();

		FacesContext context = FacesContext.getCurrentInstance();

		String disposition = String.format("attachment; filename=\"%s.%s\"",
				gridHandler.getModel().getCube().getName(),
				format.getExtension());

		ExternalContext externalContext = context.getExternalContext();
		externalContext.setResponseHeader("Content-Disposition", disposition);
		externalContext.setResponseContentType(exporter.getContentType());

		OutputStream out = externalContext.getResponseOutputStream();

		try {
			exporter.setShowParentMembers(gridHandler.getShowParentMembers());
			exporter.setShowDimensionTitle(true);
			exporter.setHideSpans(gridHandler.getHideSpans());

			exporter.setFormat(format);
			exporter.setOutputStream(out);
			exporter.initialize();

			exporter.render(gridHandler.getModel());
		} finally {
			out.flush();
			IOUtils.closeQuietly(out);
		}
	}
}
