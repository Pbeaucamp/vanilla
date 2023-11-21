package bpm.mdm.remote.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.model.runtime.exception.RowsExceptionHolder;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.remote.MdmRemote;
import bpm.mdm.remote.StoreReader;

public class TestConcurrency {
	public static class Saver extends Thread{
		
		IEntityStorage store;
		List<Row> rows;
	
		Saver(IEntityStorage store, List<Row> rows){
			this.store = store;
			this.rows = rows;
		}
		public void run(){
			System.out.println(getName() + " start");
			for(Row r : rows){
				try {
					store.createRow(r);
				} catch (Exception e) {
					
					e.printStackTrace();
					System.err.println(getName() + " canceled");
				}
				
				System.out.println(getName() + " row created");
				
				try {
					System.out.println(getName() + " flushing");
					store.flush();
					System.out.println(getName() + " flushed");
				}catch(RowsExceptionHolder e){
					
					System.out.println(getName() + " flushed rows exceptions");
				} catch (Exception e) {
					
					e.printStackTrace();
					System.out.println(getName() + " flushed failed");
				}
				
				try{
					Thread.sleep(((Double)(Math.random() * 5000)).longValue());
				}catch(Exception ex){
					
				}
			}
			
			System.out.println(getName() + " end");
		}
	}
	
	public static class Loader extends Thread{
		StoreReader reader;
		Entity entity;
		String thIde;
		Loader(StoreReader reader, Entity entity, 	String thIde){
			this.reader = reader;
			this.entity = entity;
			this.thIde = thIde;
		}
		
		public void run(){
			
			try{
				Thread.sleep(1000);
				reader.open();
				while(reader.hasNext()){
					Row r = reader.next();
					StringBuilder b = new StringBuilder();
					b.append(thIde + ":");
					
					for(Attribute a : entity.getAttributes()){
						b.append("\t");
						b.append(r.getValue(a) + "");
					}
					System.out.println(b.toString());
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Could not open StoreReader - " + ex.getMessage(), ex);
				return;
			}finally{
				try {
					reader.close();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
			
			
		}
	}
	
	
	
	public static void main(String[] args) {
		try{
			MdmRemote api = new MdmRemote("system", "system", "http://localhost:9191", null, null);
			api.loadModel();
			MdmRemote api2 = new MdmRemote("system", "system", "http://localhost:9191", null, null);
			
			Entity prod = null;
			Attribute id =  null;
			Attribute name =  null;
			Attribute price =  null;
			for(Entity e : api.getModel().getEntities()){
				if (e.getName().equals("produits")){
					prod = e;
					
					for(Attribute a : e.getAttributes()){
						if (a.getName().equals("id")){
							id = a;
						}
						else if (a.getName().equals("nom")){
							name = a;
						}
						else if (a.getName().equals("prix d'achat")){
							price = a;
						}
					}
					break;
				}
			}
			
			
			
			List<Row> l1 = new ArrayList<Row>();
			
			Row r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "1");
			r.setValue(name, "P1");
			r.setValue(price, 1);
			l1.add(r);
			
			r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "2");
			r.setValue(name, "P1");
			r.setValue(price, 2);
			l1.add(r);
			
			r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "3");
			r.setValue(name, "P1");
			r.setValue(price, 3);
			l1.add(r);
			
List<Row> l2 = new ArrayList<Row>();
			
			r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "1");
			r.setValue(name, "P2");
			r.setValue(price, 1);
			l2.add(r);
			
			r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "2");
			r.setValue(name, "P2");
			r.setValue(price, 2);
			l2.add(r);
			
			r = RuntimeFactory.eINSTANCE.createRow();
			r.setValue(id, "3");
			r.setValue(name, "P2");
			r.setValue(price, 3);
			l2.add(r);
			
			Saver s1 = new Saver(api.getStore(prod), l1);
			Saver s2 = new Saver(api2.getStore(prod), l2);
			Loader load1 = new Loader(api.createStoreReader(prod), prod, "reader1");
			
			s1.start();
			
			load1.start();
			s2.start();
			
			
		}catch(Throwable t){
			t.printStackTrace();
		}
		
		
	}
}
