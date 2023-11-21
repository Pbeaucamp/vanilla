package bpm.mdm.model.helper;

import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RowState;
import bpm.mdm.model.storage.IEntityStorage;

public class StorageHelper {

	
	static public void store(DiffResult result, IEntityStorage store , Entity entity) throws Exception{
		
		
		for(Row r : result.getRows()){
			if (result.getState(r) == RowState.NEW){
				store.createRow(r);
			}
			else if (result.getState(r) == RowState.UPDATE){
				store.updateRow(r);
			}
		}
		
		store.flush();
		
	}
}
