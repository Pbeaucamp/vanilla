package bpm.mdm.model.serialisation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.api.IEntitySerializer;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RuntimeFactory;

public class DatasSerializer implements IEntitySerializer{

//	private class Datas implements Serializable{
//		private List<HashMap<String, Serializable>> rows;
//		public Datas(List<HashMap<String, Serializable>> rows){
//			this.rows = rows;
//		}
//	}
	private MdmConfiguration conf;
	public DatasSerializer(MdmConfiguration conf){
		this.conf = conf;
	}
	@Override
	public List<HashMap<String, Serializable>> loadEntity(Entity entity) throws Exception {
		String fname = conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store";
		File f = new File(fname);
		if (!f.exists() || !f.isFile()){
			return new ArrayList();
		}
		
		return loadDatas(entity, new FileInputStream(f));
		
		
		
	}
	public void saveDatas(Entity entity,  List<HashMap<String, Serializable>> datas, OutputStream output)
			throws Exception {

//		Datas dt = new Datas(datas);
		ObjectOutputStream os = new ObjectOutputStream(output) ;
		try{
			os.writeObject(datas);
		}catch(Exception ex){
			throw ex;
		}finally{
			os.close();
		}
		
		
	}

	@Override
	public void saveEntity(Entity entity,  List<HashMap<String, Serializable>> datas)
			throws Exception {
		String fname = conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store";
		

		saveDatas(entity, datas, new FileOutputStream(fname));
		
	}
	
	
	
	
	public List<HashMap<String, Serializable>> loadDatas(Entity entitye, InputStream is) throws Exception{
		ObjectInputStream ois = new ObjectInputStream(is);
		Object o = ois.readObject();
		ois.close();
		
		List<HashMap<String, Serializable>> dt = (List<HashMap<String, Serializable>>)o;
		
		
		return dt;
	}
	public List<HashMap<String, Serializable>> convertToSerializable(Entity entity, List<Row> toAdd) {
		List<HashMap<String, Serializable>> l = new ArrayList<HashMap<String, Serializable>>();
		
		for(Row r : toAdd){
			HashMap<String, Serializable> row = new HashMap<String, Serializable>();
			
			for(Attribute a : entity.getAttributes()){
				Object o = r.getValue(a);
				if (o != null && o instanceof Serializable){
					row.put(a.getUuid(), (Serializable)o);
				}
			}
			l.add(row);
		}
		
		
		return l;
	}
	public List<Row> convert(Entity entity,
			List<HashMap<String, Serializable>> datas) {
		
		
		List<Row> res = new ArrayList<Row>();
		if (datas == null){
			return res;
		}
		for(HashMap<String, Serializable> r : datas){
			Row row = RuntimeFactory.eINSTANCE.createRow();
			
			for(String s : r.keySet()){
				Attribute attribute = null;
				for(Attribute a : entity.getAttributes()){
					if (a.getUuid().equals(s)){
						attribute = a;
						break;
					}
				}
				row.setValue(attribute, r.get(s));
			}
			res.add(row);
		}
		
		return res;
	}

	
}
