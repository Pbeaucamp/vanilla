package bpm.gateway.core.server.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.tools.Utils;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.vanilla.map.core.design.opengis.IOpenGisCoordinate;
import bpm.vanilla.map.core.design.opengis.IOpenGisMapEntity;
import bpm.vanilla.map.core.design.opengis.ShapeFileParser;

/**
 * Helper to get Metadata From a SHAPE file or get its values
 * 
 * @author SVI
 * 
 */
public class FileShapeHelper {

	/**
	 * Create a Stream Descriptor for a Shape File
	 * 
	 * @param transfo
	 * @param numberRow
	 * @throws Exception
	 */
	public static void createStreamDescriptor(FileInputShape transfo, int numberRow) throws Exception {
		try {
			DefaultStreamDescriptor desc = new DefaultStreamDescriptor();

			File tmpFile = Utils.createTmpFile(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), "shp");
			List<IOpenGisMapEntity> entities = ShapeFileParser.parseShapeFile(tmpFile.getAbsolutePath(), null);
			if (entities == null) {
				return;
			}

			StreamElement colEntityId = new StreamElement();
			colEntityId.name = "Entity ID";
			colEntityId.transfoName = transfo.getName();
			colEntityId.originTransfo = transfo.getName();
			colEntityId.className = String.class.getName();
			desc.addColumn(colEntityId);

			StreamElement colName = new StreamElement();
			colName.name = "Name";
			colName.transfoName = transfo.getName();
			colName.originTransfo = transfo.getName();
			colName.className = String.class.getName();
			desc.addColumn(colName);

			StreamElement colType = new StreamElement();
			colType.name = "Type";
			colType.transfoName = transfo.getName();
			colType.originTransfo = transfo.getName();
			colType.className = String.class.getName();
			desc.addColumn(colType);

			// StreamElement colCoordinates = new StreamElement();
			// colCoordinates.name = "Coordinates";
			// colCoordinates.transfoName = transfo.getName();
			// colCoordinates.originTransfo = transfo.getName();
			// colCoordinates.className = String.class.getName();
			// desc.addColumn(colCoordinates);

			StreamElement colLatitude = new StreamElement();
			colLatitude.name = "Latitude";
			colLatitude.transfoName = transfo.getName();
			colLatitude.originTransfo = transfo.getName();
			colLatitude.className = String.class.getName();
			desc.addColumn(colLatitude);

			StreamElement colLongitude = new StreamElement();
			colLongitude.name = "Longitude";
			colLongitude.transfoName = transfo.getName();
			colLongitude.originTransfo = transfo.getName();
			colLongitude.className = String.class.getName();
			desc.addColumn(colLongitude);

			StreamElement colAltitude = new StreamElement();
			colAltitude.name = "Altitude";
			colAltitude.transfoName = transfo.getName();
			colAltitude.originTransfo = transfo.getName();
			colAltitude.className = String.class.getName();
			desc.addColumn(colAltitude);

			transfo.setDescriptor(desc);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param transfo
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> getValues(FileShape transfo, int firstRow, int maxRows) throws Exception {
		int readedRow = 0;
		int currentRow = 0;

		List<List<Object>> values = new ArrayList<List<Object>>();
		try {

			File tmpFile = Utils.createTmpFile(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), "shp");
			List<IOpenGisMapEntity> entities = ShapeFileParser.parseShapeFile(tmpFile.getAbsolutePath(), null);
			if (entities == null) {
				return values;
			}

			for (IOpenGisMapEntity entity : entities) {
				for (IOpenGisCoordinate coord : entity.getCoordinates()) {
					readedRow++;

					if (readedRow <= firstRow) {
						continue;
					}
					if (maxRows > 0 && currentRow > maxRows) {
						break;
					}

					List<Object> row = new ArrayList<Object>();
					row.add(entity.getEntityId());
					row.add(entity.getName());
					row.add(entity.getType());
					// row.add(entity.getCoordinatesAsString());
					row.add(String.valueOf(coord.getLocY()));
					row.add(String.valueOf(coord.getLocX()));
					row.add(String.valueOf(coord.getLocZ()));

					values.add(row);
					currentRow++;
				}

			}
		} catch (Exception e) {
			throw e;
		}

		return values;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, FileShape stream) throws Exception {
		// TODO: Get distinct value
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();

		// BufferedReader br = null;
		// try {
		// Reader in = new
		// InputStreamReader(((AbstractFileServer)stream.getServer()).getInpuStream(stream),
		// stream.getEncoding());
		// br = new BufferedReader(in);
		//
		// String line = null;
		// if (stream instanceof FileInputCSV &&
		// ((FileInputCSV)stream).isSkipFirstRow()){
		// line = br.readLine();
		// }
		// else if (stream instanceof FileOutputCSV &&
		// ((FileOutputCSV)stream).isContainHeader()){
		// line = br.readLine();
		// }
		// while ((line = br.readLine()) != null ){
		// String[] lineSplit = line.split(stream.getSeparator() + "", -1);
		//
		//
		//
		// String value = lineSplit[colPos];
		// boolean present = false;
		// for(List<Object> l : values){
		// if (l.get(0).equals(value)){
		// l.set(1, (Integer)l.get(1) + 1);
		// present = true;
		// break;
		// }
		// }
		//
		// if (!present){
		// List<Object> l = new ArrayList<Object>();
		// l.add(value);
		// l.add(1);
		// values.add(l);
		// }
		//
		// }
		//
		// }catch (Exception e) {
		// throw e;
		// }finally{
		// if (br != null){
		// br.close();
		// }
		// }

		return values;
	}
}
