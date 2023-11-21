package bpm.vanilla.platform.core.runtime.tools;

import java.io.Serializable;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;

public class DataTypeManager {

	public static void convertResultTypes(DatasetResultQuery result) {
		for(DataColumn col : result.getDataset().getMetacolumns()) {
			if(col.getCustomDataType() != null) {
				List<Serializable> values = result.getResult().get(col.getColumnName());
				int i = 0;
				for(Serializable val : values) {
					try {
						switch(col.getCustomDataType()) {
							case DATE:
								
								break;
							case DECIMAL:
								Serializable newVal;
								try {
									newVal = Double.parseDouble(val.toString());
									values.set(i, newVal);
								} catch(NumberFormatException e) {
									newVal = Double.parseDouble(val.toString().replace(",", "."));
									values.set(i, newVal);
								}
								break;
							case INT:
								newVal = Integer.parseInt(val.toString());
								values.set(i, newVal);
								break;
							case STRING:
								newVal = String.valueOf(val.toString());
								values.set(i, newVal);
								break;
						}
					} catch(Exception e) {

					}
					i++;
				}
			}
		}
	}
	
}
