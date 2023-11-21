package bpm.mdm.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;

public class RowUtil {


	public static List<Object> extractPrimaryKey(Entity entity, Row row){
		List<Attribute> pkA = entity.getAttributesId();
		if (pkA.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		List<Object> l = new ArrayList<Object>();
		
		for(Attribute a : pkA){
			l.add(row.getValue(a));
		}
		return l;
	}
	
	
	public static String generateUUID(Entity entity, Row row) throws Exception{
		List<Object> vals = extractPrimaryKey(entity, row);
		if (vals.isEmpty()){
			throw new Exception("Cannot generate primary for an ENtity without identifier attributes");
		}
		
		StringBuilder b = new StringBuilder();
		boolean first = true;
		for(Object o : vals){
			if (!first){
				b.append("-");
			}
			else{
				first = false;
			}
			if (o instanceof Date){
				b.append("" + ((Date)o).getTime());
			}
			else{
				b.append("" + o.toString());
			}
			
		}
		
		
		
		return UUID.nameUUIDFromBytes(b.toString().getBytes()).toString();
		
	}
}
