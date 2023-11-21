package bpm.gateway.core.server.vanilla;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;

public class DataPreparationHelper {

	public static DefaultStreamDescriptor createStreamDescriptor(DataPreparationInput transfo) throws Exception {
		DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

		HashMap<String, StreamElement> structure = new HashMap<String, StreamElement>();
		HashMap<StreamElement, byte[]> flagsType = new HashMap<StreamElement, byte[]>();

		DataPreparation selectedDataPrep = transfo.getSelectedDataPrep();
		if (selectedDataPrep == null) {
			IDataVizComponent datavizRemote = new RemoteDataVizComponent(new RemoteRepositoryApi(transfo.getDocument().getRepositoryContext()));

			try {
				List<DataPreparation> datapreps = datavizRemote.getDataPreparations();
				for (DataPreparation d : datapreps) {
					if (d.getId() == Integer.parseInt(transfo.getDataprepid())) {
						selectedDataPrep = d;
						transfo.setSelectedDataPrep(d);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Integer,Float,Double,Boolean
		int k = 0;
		for (DataColumn col : selectedDataPrep.getDataset().getMetacolumns()) {
			if (k < selectedDataPrep.getDataset().getMetacolumns().size()) {
				StreamElement e = new StreamElement();
				e.name = col.getColumnName();
				e.transfoName = transfo.getName();
				e.originTransfo = transfo.getName();
				e.typeName = col.getColumnTypeName();
				structure.put(col.getColumnName(), e);
				flagsType.put(e, new byte[] { -1, -1, -1, -1 });
				descriptor.addColumn(e);
			}
			k++;
		}

		return descriptor;
	}

	/**
	 * 
	 * @param transfo
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> getValues(DataPreparationInput transfo, int firstRow, int maxRows) throws Exception {
		if (transfo.getSelectedDataPrep() == null) {
			return null;
		}

		IDataVizComponent datavizRemote = new RemoteDataVizComponent(new RemoteRepositoryApi(transfo.getDocument().getRepositoryContext()));
		DataPreparationResult result = datavizRemote.executeDataPreparation(transfo.getSelectedDataPrep());
		List<DataColumn> columns = transfo.getSelectedDataPrep().getDataset().getMetacolumns();

		int readedRow = 0;
		int currentRow = 0;

		List<List<Object>> values = new ArrayList<List<Object>>();
		Iterator<Map<DataColumn, Serializable>> csvIterator = result.getValues().iterator();

		try {
			while (csvIterator.hasNext() && currentRow < maxRows) {

				Map<DataColumn, Serializable> recordData = csvIterator.next();
				List<Object> row = new ArrayList<Object>();

				for (DataColumn col : columns) {
					try {
						Serializable value = recordData.get(col);
						row.add(value != null ? value.toString() : "");
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
						Activator.getLogger().warn("DataPreparation helper error");
						row.add(null);
					}
				}

				readedRow++;

				if (readedRow <= firstRow) {
					continue;
				}
				values.add(row);
				currentRow++;
			}

		} catch (Exception e) {
			throw e;
		}

		return values;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, DataPreparationInput transfo) throws Exception {
		int colPos = transfo.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();

		IDataVizComponent datavizRemote = new RemoteDataVizComponent(new RemoteRepositoryApi(transfo.getDocument().getRepositoryContext()));
		DataPreparationResult result = datavizRemote.executeDataPreparation(transfo.getSelectedDataPrep());
		List<DataColumn> columns = transfo.getSelectedDataPrep().getDataset().getMetacolumns();
		
		Iterator<Map<DataColumn, Serializable>> csvIterator = result.getValues().iterator();
		try {
			DataColumn selectedCol = columns.get(colPos);
			
			while (csvIterator.hasNext()) {
				Map<DataColumn, Serializable> recordData = csvIterator.next();
				
				String value = recordData.get(selectedCol) != null ? recordData.get(selectedCol).toString() : "";
				boolean present = false;
				for (List<Object> l : values) {
					if (l.get(0).equals(value)) {
						l.set(1, (Integer) l.get(1) + 1);
						present = true;
						break;
					}
				}

				if (!present) {
					List<Object> l = new ArrayList<Object>();
					l.add(value);
					l.add(1);
					values.add(l);
				}

			}

		} catch (Exception e) {
			throw e;
		}

		return values;
	}
}
