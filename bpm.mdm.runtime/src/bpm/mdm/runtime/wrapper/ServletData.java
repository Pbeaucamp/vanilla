package bpm.mdm.runtime.wrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;
import bpm.mdm.model.Rule;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.api.IMdmProvider.ActionType;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.model.storage.EntityStorageStatistics;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.runtime.MdmRuntime;
import bpm.mdm.runtime.RuntimeStoreTreeIndexed;
import bpm.mdm.runtime.serializers.Reader;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ServletData extends HttpServlet{
	private XStream xstream;
	private IMdmProvider component;
	
	public ServletData(IMdmProvider component) {
		this.component = component ;
		
		xstream = new XStream();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			Object actionResult = null;
			
			ActionType type = (ActionType)action.getActionType();
			switch(type) {
				case GET_ENTITY_DATAS:
					actionResult = getDatas(args);
					break;
				case SAVE_DATAS:
					actionResult = saveDatas(args);
					break;
				case UPDATE_DATAS:
					actionResult = updateDatas(args);
					break;
				case DELETE_DATAS:
					actionResult = deleteDatas(args);
					break;
				case LOOKUP:
					actionResult = lookup(args);
					break;
				case GET_STORE_STATISTICS:
					actionResult = getStoreStats(args);
					break;
				case GET_INVALID_ROWS:
					actionResult = getInvalidRows(args);
					break;
			}
			
			if (actionResult != null && actionResult instanceof byte[]){
				
				IOWriter.write(new ByteArrayInputStream((byte[])actionResult), resp.getOutputStream(), true, true);
				
				//xstream.toXML(actionResult, resp.getWriter());

			}
			else if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
		} catch(Throwable ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			xstream.toXML(new Exception("Unexpected error occured on server side : " + ex.getMessage()), resp.getWriter());
		}
	}
	private List<Exception> saveDatas(XmlArgumentsHolder args) throws Exception{
		String entityName = (String)args.getArguments().get(0);
		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getName().equals(entityName)){
				entity = e;
				break;
			}
		}
		if (entity == null){
			throw new Exception("Entity " + entityName + " not found in the Model");
		}
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		byte[] row64 = (byte[])args.getArguments().get(1);
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(row64));
		DatasSerializer s = new DatasSerializer(((MdmRuntime)component).getConfig());
		List<HashMap<String, Serializable>>  datas = s.loadDatas(entity, bis);
		

		List<Row> rows = s.convert(entity, datas);
		
		RuntimeStoreTreeIndexed store = (RuntimeStoreTreeIndexed)((MdmRuntime)component).getStore(entity);//new RuntimeStore(((MdmRuntime)component).getConfig(), entity);

		//acquire lock
		store.acquire();
		
		List<Exception> errors = new ArrayList<Exception>();
		
		for(Row r : rows){
			try{
				store.createRow(r);	
			}catch(AbstractRowException ex){
				errors.add(ex);
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				store.release();
				throw new Exception("Unexpected error when saving row - " + ex.getMessage(), ex);
			}
			
		}
		try{
			
			store.flush();
		}catch(Exception ex){
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			throw new Exception("The server could not flush the datas - " + ex.getMessage(), ex);
		}finally{
			store.release();
		}
		
		
		return errors;
		
	}
	
	private List<Exception> updateDatas(XmlArgumentsHolder args) throws Exception{
		String entityName = (String)args.getArguments().get(0);
		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getName().equals(entityName)){
				entity = e;
				break;
			}
		}
		if (entity == null){
			throw new Exception("Entity " + entityName + " not found in the Model");
		}
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		byte[] row64 = (byte[])args.getArguments().get(1);
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(row64));
		DatasSerializer s = new DatasSerializer(((MdmRuntime)component).getConfig());
		List<HashMap<String, Serializable>>  datas = s.loadDatas(entity, bis);
		
		//s.saveEntity(entity, datas);
		

		List<Row> rows = s.convert(entity, datas);
		List<Exception> errors = new ArrayList<Exception>();
		RuntimeStoreTreeIndexed store = (RuntimeStoreTreeIndexed)((MdmRuntime)component).getStore(entity);//new RuntimeStore(((MdmRuntime)component).getConfig(), entity);
		store.acquire();
		
		for(Row r : rows){
			try{
				store.updateRow(r);	
			}catch(AbstractRowException ex){
				errors.add(ex);
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				store.release();
				throw new Exception("Unexpected error when saving row - " + ex.getMessage(), ex);
			}
		}
		
		try{
			
			store.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			store.release();
		}
		
		
		return errors;
		
	}
	
	private List<Exception> deleteDatas(XmlArgumentsHolder args) throws Exception{
		String entityName = (String)args.getArguments().get(0);
		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getName().equals(entityName)){
				entity = e;
				break;
			}
		}
		if (entity == null){
			throw new Exception("Entity " + entityName + " not found in the Model");
		}
	
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		byte[] row64 = (byte[])args.getArguments().get(1);
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(row64));
		DatasSerializer s = new DatasSerializer(((MdmRuntime)component).getConfig());
		List<HashMap<String, Serializable>>  datas = s.loadDatas(entity, bis);
		
		//s.saveEntity(entity, datas);
		

		List<Row> rows = s.convert(entity, datas);
		List<Exception> errors = new ArrayList<Exception>();
		RuntimeStoreTreeIndexed store = (RuntimeStoreTreeIndexed)((MdmRuntime)component).getStore(entity);//new RuntimeStore(((MdmRuntime)component).getConfig(), entity);
		store.acquire();
		
		for(Row r : rows){
			try{
				store.deleteRow(r);	
			}catch(AbstractRowException ex){
				errors.add(ex);
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				store.release();
				throw new Exception("Unexpected error when saving row - " + ex.getMessage(), ex);
			}
		}
		
		try{
			
			store.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			store.release();
		}
		
		
		return errors;
		
	}

	private Object getDatas(XmlArgumentsHolder args) throws Exception{
		String entityName = (String)args.getArguments().get(0);
		Integer chunkNumber = (Integer)args.getArguments().get(1);

		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getName().equals(entityName)){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Entity " + entityName + " not found in the Model");
		}
	
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		try {
	
			Reader r = ((MdmRuntime)component).getRuntimeSerializer().getReader(entity);
			
			try{
				Object o = r.readStreamDatas(chunkNumber);
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos);
				os.writeObject(o);
				os.close();
				return Base64.encodeBase64(bos.toByteArray());
			}catch(NonExistingChunkFileException f){
				return new byte[]{};
			}

		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Failed to read " + entityName +  " datas ", ex);
			throw ex;
		}
		
	}
	private Object lookup(XmlArgumentsHolder args) throws Exception{
		String entityUuid = (String)args.getArguments().get(0);
		HashMap<String, Serializable> primaryKey = (HashMap<String, Serializable>)args.getArguments().get(1);

		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getUuid().equals(entityUuid)){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Entity " + entityUuid + " not found in the Model");
		}
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		try {
			
			DatasSerializer s = new DatasSerializer(null);
			List<HashMap<String, Serializable>> l = new ArrayList<HashMap<String, Serializable>>();
			l.add(primaryKey);
			Row r = s.convert(entity, l).get(0);
			Row res = ((MdmRuntime)component).getStore(entity).lookup(r);
			if (res == null){
				return null;
			}
			try{
				List<Row> ll = new ArrayList<Row>();
				ll.add(res);
				Object o = s.convertToSerializable(entity, ll);				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos);
				os.writeObject(o);
				os.close();
				return Base64.encodeBase64(bos.toByteArray());
			}catch(Exception f){
				throw f;
			}


		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Failed to read " + entityUuid +  " datas ", ex);
			throw ex;
		}
		
	}
	
	private Object getStoreStats(XmlArgumentsHolder args) throws Exception{
		String entityName = (String)args.getArguments().get(0);
		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getName().equals(entityName)){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Entity " + entityName + " not found in the Model");
		}
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		try {
			
			return component.getStore(entity).getStorageStatistics();


		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Failed to read " + entityName +  " datas ", ex);
			throw ex;
		}
		
	}
	private Object getInvalidRows(XmlArgumentsHolder args) throws Exception{
		String entityUuid = (String)args.getArguments().get(0);
		Entity entity = null;
		for(Entity e : component.getModel().getEntities()){
			if (e.getUuid().equals(entityUuid)){
				entity = e;
				break;
			}
		}
		
		if (entity == null){
			throw new Exception("Entity {" + entityUuid + "} not found in the Model");
		}
		if (entity.getAttributesId().isEmpty()){
			throw new Exception("Datas operations are unavailabe until the entity has a primary key" );
		}
		
		IEntityStorage storage = component.getStore(entity);
		List<Row> rows = storage.getInvalidRows();
		
		try{
			DatasSerializer s = new DatasSerializer(null);
			Object o = s.convertToSerializable(entity, rows);				
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(o);
			os.close();
			return Base64.encodeBase64(bos.toByteArray());
		}catch(Exception ex){
			throw new Exception("Could not serialize invalid rows because " + ex.getMessage(), ex);
		}
		
	}
}
