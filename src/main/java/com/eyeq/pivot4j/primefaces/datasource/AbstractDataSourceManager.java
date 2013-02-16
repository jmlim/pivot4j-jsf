package com.eyeq.pivot4j.primefaces.datasource;

import org.olap4j.OlapDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataSourceManager implements DataSourceManager {

	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @see com.eyeq.pivot4j.primefaces.datasource.DataSourceManager#createDataSource(com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata)
	 */
	@Override
	public OlapDataSource createDataSource(ConnectionMetadata connectionInfo) {
		if (log.isInfoEnabled()) {
			if (connectionInfo == null) {
				log.info("Create a new OLAP data source with default settings.");
			} else {
				log.info("Create a new OLAP data source with connection info : "
						+ connectionInfo);
			}
		}

		return doCreateDataSource(connectionInfo);
	}

	/**
	 * @param connectionInfo
	 * @return
	 */
	protected abstract OlapDataSource doCreateDataSource(
			ConnectionMetadata connectionInfo);
}