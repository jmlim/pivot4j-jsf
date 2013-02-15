package com.eyeq.pivot4j.primefaces.ui;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;

import org.apache.commons.lang.NullArgumentException;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class DrillThroughDataModel extends LazyDataModel<Map<String, Object>> {

	private static final long serialVersionUID = 2554173601960871316L;

	private static final String ROW_KEY = "_id";

	private transient ResultSet resultSet;

	private List<DataColumn> columns;

	/**
	 * @param resultSet
	 */
	public DrillThroughDataModel(ResultSet resultSet) {
		if (resultSet == null) {
			throw new NullArgumentException("resultSet");
		}

		this.resultSet = resultSet;
	}

	public List<DataColumn> getColumns() {
		if (columns == null) {
			try {
				ResultSetMetaData metadata = resultSet.getMetaData();

				int count = metadata.getColumnCount();

				this.columns = new ArrayList<DataColumn>(count);

				columns.add(new DataColumn("#", ROW_KEY));

				for (int i = 1; i <= count; i++) {
					columns.add(new DataColumn(metadata.getColumnLabel(i),
							metadata.getColumnName(i)));
				}
			} catch (SQLException e) {
				throw new FacesException(e);
			}
		}

		return columns;
	}

	/**
	 * @see org.primefaces.model.LazyDataModel#getRowKey(java.lang.Object)
	 */
	@Override
	public Object getRowKey(Map<String, Object> row) {
		return row.get(ROW_KEY);
	}

	/**
	 * @see org.primefaces.model.LazyDataModel#load(int, int, java.lang.String,
	 *      org.primefaces.model.SortOrder, java.util.Map)
	 */
	public List<Map<String, Object>> load(int first, int pageSize,
			String sortField, SortOrder sortOrder, Map<String, String> filters) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
				pageSize);

		try {
			int rowIndex = 0;
			while (rowIndex < first) {
				if (!resultSet.next()) {
					return Collections.emptyList();
				}

				rowIndex++;
			}

			List<DataColumn> columns = getColumns();

			for (int i = 0; i < pageSize; i++) {
				if (resultSet.next()) {
					Map<String, Object> row = new HashMap<String, Object>(
							columns.size() + 1);

					for (DataColumn column : columns) {
						if (ROW_KEY.equals(column.getName())) {
							row.put(ROW_KEY, rowIndex + i + 1);
						} else {
							row.put(column.getName(),
									resultSet.getObject(column.getName()));
						}
					}

					data.add(row);
				} else {
					break;
				}
			}
		} catch (SQLException e) {
			throw new FacesException(e);
		}

		return data;
	}

	public void destroy() {
		try {
			resultSet.close();
		} catch (SQLException e) {
			throw new FacesException(e);
		}
	}

	public static class DataColumn {

		String label;

		String name;

		/**
		 * @param label
		 * @param name
		 */
		DataColumn(String label, String name) {
			this.label = label;
			this.name = name;
		}

		/**
		 * @return label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @return name
		 */
		public String getName() {
			return name;
		}
	}
}
