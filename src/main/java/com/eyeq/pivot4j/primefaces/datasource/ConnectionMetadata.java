package com.eyeq.pivot4j.primefaces.datasource;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ConnectionMetadata implements Serializable {

	private static final long serialVersionUID = 1613489385973603487L;

	private String cubeName;

	private String catalogName;

	/**
	 * @param id
	 */
	public ConnectionMetadata() {
	}

	/**
	 * @param catalogName
	 * @param cubeName
	 */
	public ConnectionMetadata(String catalogName, String cubeName) {
		this.catalogName = catalogName;
		this.cubeName = cubeName;
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

	/**
	 * @return the catalogName
	 */
	public String getCatalogName() {
		return catalogName;
	}

	/**
	 * @param catalogName
	 *            the catalogName to set
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("cubeName", cubeName)
				.append("catalogName", catalogName).toString();
	}
}
