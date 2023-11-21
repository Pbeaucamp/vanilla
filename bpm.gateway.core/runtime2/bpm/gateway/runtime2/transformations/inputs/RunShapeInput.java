package bpm.gateway.runtime2.transformations.inputs;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import bpm.gateway.core.server.file.FileShapeHelper;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapObject;
import bpm.vanilla.map.core.design.opengis.OpenGisMapObject;
import bpm.vanilla.map.remote.core.design.impl.RemoteOpenGisMapService;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RunShapeInput extends RuntimeStep{

	private List<List<Object>> entities;
	private int currentRow = 0;
	private int nbLines = 0;
	
	private RuntimeStep errorHandler;
	
	public RunShapeInput(IRepositoryContext repContext, FileInputShape transformation, int bufferSize) throws Exception {
		super(repContext, transformation, bufferSize);
		if (transformation.saveInNorparena() && getRepositoryContext() == null){
			throw new Exception("Cannot save in Norparena without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		FileInputShape shape = (FileInputShape)getTransformation();

		info(" Initing shape file...");
		
		if (shape.getTrashTransformation() != null){
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == shape.getTrashTransformation()){
					errorHandler = rs;
					break;
				}
			}
		}
		
		entities = FileShapeHelper.getValues(shape, 0, 0); //ShapeFileParser.parseShapeFile(shape.getDefinition(), null);
		
		if(entities != null && !entities.isEmpty()){
			nbLines = entities.size();
		}
		
		info(" Shape file inited.");
	}

	@Override
	public void performRow() throws Exception {
		if(nbLines == 0 || nbLines == currentRow){
			setEnd();
			return;
		}
		
		readedRows++;
		Row row = RowFactory.createRow(this);
		row.set(0, entities.get(currentRow).get(0));
		row.set(1, entities.get(currentRow).get(1));
		row.set(2, entities.get(currentRow).get(2));
		row.set(3, entities.get(currentRow).get(3));
		row.set(4, entities.get(currentRow).get(4));
		row.set(5, entities.get(currentRow).get(5));
//		row.set(0, entities.get(currentRow).getEntityId());
//		row.set(1, entities.get(currentRow).getName());
//		row.set(2, entities.get(currentRow).getType());
//		row.set(3, entities.get(currentRow).getCoordinatesAsString());
		
		writeRow(row);
		currentRow++;
	}

	@Override
	public void releaseResources() {
		entities.clear();
		entities = null;
		info(" Shape closed");
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException{
		boolean wrote = false;
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);
				wrote = true;
			}
		}
		
		if (wrote){
			writedRows++;
		}
	}
	
	@Override
	protected synchronized void setEnd() {
		FileInputShape shape = (FileInputShape)getTransformation();
		if(shape.saveInNorparena()){
			if(shape.isFromNewMap()){
				info(" Initing map service and creating new map.");
				
				RemoteOpenGisMapService openGisService = new RemoteOpenGisMapService();
				openGisService.configure(getRepositoryContext().getVanillaContext().getVanillaUrl());
				
				IOpenGisMapObject map = new OpenGisMapObject();
				map.setName(shape.getNewMapName());
				
				try {
					FileInputStream fis = new FileInputStream(new File(shape.getDefinition()));
					openGisService.addOpenGisMap(map, fis);
				} catch (Exception e) {
					e.printStackTrace();
					error(" Unable to save the new map in Norparena.", e);
				}
			}
			else {
				info(" Saving shape's entities into Norparena.");
				
				RemoteOpenGisMapService openGisService = new RemoteOpenGisMapService();
				openGisService.configure(getRepositoryContext().getVanillaContext().getVanillaUrl());
				
				try {
					IOpenGisMapObject map = openGisService.getOpenGisMapByDefinitionId(shape.getExistingMapId());
					if(map != null){
						FileInputStream fis = new FileInputStream(new File(shape.getDefinition()));
						openGisService.saveShapeFile(map.getId(), fis);
					}
					else {
						error(" Unable to save the entities into the map with id = " + shape.getExistingMapId() + " (Map not Found)");
					}
				} catch (Exception e) {
					e.printStackTrace();
					error(" Unable to save the entities into the map with id = " + shape.getExistingMapId(), e);
				}
			}
		}
		
		super.setEnd();
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}
}
