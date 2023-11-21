package bpm.gwt.commons.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.MetadataHelper;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtDatabaseInfoRow;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.fmdt.FmdtNode;
import bpm.gwt.commons.shared.fmdt.FmdtPackage;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.MetaDataReader;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.MetaDataException;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.IDataStreamElement.SubType;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.FmdtDriller;
import bpm.vanilla.platform.core.beans.FmdtDrillerFilter;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.fmdt.ColumnType;
import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.D4CHelper;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class FmdtServicesImpl extends RemoteServiceServlet implements FmdtServices {

	private static final long serialVersionUID = 1L;

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	@Override
	public String exportToXLS(FmdtRow columnNames, FmdtTable table, String name, String format, String separator) throws ServiceException {
		CommonSession session = getSession();

		if (format.equalsIgnoreCase(CommonConstants.FORMAT_XLS)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			WritableWorkbook workbook = null;
			try {
				workbook = Workbook.createWorkbook(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			WritableSheet sheet = workbook.createSheet(name, 0);

			WritableCellFormat formatHeader = new WritableCellFormat();
			try {
				formatHeader.setBackground(Colour.GRAY_25);
				formatHeader.setAlignment(Alignment.CENTRE);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}

			WritableCellFormat formatCell = new WritableCellFormat();
			try {
				formatCell.setAlignment(Alignment.CENTRE);
				formatCell.setWrap(true);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}

			for (int i = 0; i < columnNames.getValues().size(); i++) {
				Label l = new Label(i, 0, columnNames.getValues().get(i));
				l.setCellFormat(formatHeader);
				try {
					sheet.addCell(l);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			sheet.setColumnView(0, 20);

			for (int i = 0; i < table.getDatas().size(); i++) {
				for (int j = 0; j < table.getDatas().get(i).getValues().size(); j++) {
					Label l = new Label(j, i + 1, table.getDatas().get(i).getValues().get(j));
					l.setCellFormat(formatCell);
					try {
						sheet.addCell(l);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				sheet.setColumnView(i + 1, 20);
			}

			try {
				workbook.write();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			session.addStream(name, CommonConstants.FORMAT_XLS, is);
			return name;
		}
		else if (format.equalsIgnoreCase(CommonConstants.FORMAT_CSV)) {
			name = name + "_" + new Object().hashCode();

			ByteArrayInputStream byteArrayIs = buildCsv(columnNames, table, separator);

			session.addStream(name, format, byteArrayIs);
			return name;
		}
		else if (format.equalsIgnoreCase(CommonConstants.FORMAT_WEKA)) {
			Instances data;
			FastVector atts = new FastVector();
			List<String> types = defineAttributes(table, atts);
			data = new Instances(name, atts, 0);

			addAttributeValue(data, table, types);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);

			try {
				saver.setDestination(out);
				saver.writeBatch();
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			name = name + "_" + new Object().hashCode();

			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(out.toByteArray());
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			session.addStream(name, format, byteArrayIs);
			return name;
		}

		throw new ServiceException("Unable to create the requested document.");
	}

	private ByteArrayInputStream buildCsv(FmdtRow columnNames, FmdtTable table, String separator) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		StringBuffer firstLine = new StringBuffer();
		for (int i = 0; i < columnNames.getValues().size(); i++) {
			if (i != 0) {
				firstLine.append(separator);
			}
			firstLine.append(columnNames.getValues().get(i));
			if (i == columnNames.getValues().size() - 1) {
				firstLine.append("\n");
			}
		}

		try {
			os.write(firstLine.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < table.getDatas().size(); i++) {

			StringBuffer line = new StringBuffer();

			FmdtRow row = table.getDatas().get(i);
			for (int j = 0; j < row.getValues().size(); j++) {
				if (j != 0) {
					line.append(separator);
				}
				line.append("\"" + table.getDatas().get(i).getValues().get(j) + "\"");
				if (j == table.getDatas().get(i).getValues().size() - 1) {
					line.append("\n");
				}
			}

			try {
				os.write(line.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayIs;
	}

	@Override
	public String exportToXLS(FmdtTable table, String name, String format, String separator) throws ServiceException {
		CommonSession session = getSession();

		if (format.equalsIgnoreCase(CommonConstants.FORMAT_XLS)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			WritableWorkbook workbook = null;
			try {
				workbook = Workbook.createWorkbook(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			WritableSheet sheet = workbook.createSheet(name, 0);

			WritableCellFormat formatHeader = new WritableCellFormat();
			try {
				WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10);
				cellFont.setBoldStyle(WritableFont.BOLD);

				formatHeader.setFont(cellFont);
				formatHeader.setAlignment(Alignment.LEFT);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}

			WritableCellFormat formatCell = new WritableCellFormat();
			try {
				formatCell.setAlignment(Alignment.CENTRE);
				formatCell.setWrap(true);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}
			sheet.setColumnView(0, 20);

			for (int i = 0; i < table.getDatas().size(); i++) {
				for (int j = 0; j < table.getDatas().get(i).getValues().size(); j++) {
					Label l = new Label(j, i + 1, table.getDatas().get(i).getValues().get(j));
					if (j == 0)
						l.setCellFormat(formatHeader);
					else
						l.setCellFormat(formatCell);
					try {
						sheet.addCell(l);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				sheet.setColumnView(i + 1, 20);
			}

			try {
				workbook.write();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			session.addStream(name, CommonConstants.FORMAT_XLS, is);
			return name;
		}
		else if (format.equalsIgnoreCase(CommonConstants.FORMAT_CSV)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			StringBuffer firstLine = new StringBuffer();
			try {
				os.write(firstLine.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < table.getDatas().size(); i++) {

				StringBuffer line = new StringBuffer();

				FmdtRow row = table.getDatas().get(i);
				for (int j = 0; j < row.getValues().size(); j++) {
					if (j != 0) {
						line.append(separator);
					}
					line.append(table.getDatas().get(i).getValues().get(j));
					if (j == table.getDatas().get(i).getValues().size() - 1) {
						line.append("\n");
					}
				}

				try {
					os.write(line.toString().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			name = name + "_" + new Object().hashCode();

			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			session.addStream(name, format, byteArrayIs);
			return name;
		}

		else if (format.equalsIgnoreCase(CommonConstants.FORMAT_WEKA)) {
			Instances data;
			FastVector atts = new FastVector();
			List<String> types = defineAttributes(table, atts);
			data = new Instances(table.getName(), atts, 0);

			addAttributeValue(data, table, types);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);

			try {
				saver.setDestination(out);
				saver.writeBatch();
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			name = name + "_" + new Object().hashCode();

			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(out.toByteArray());
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			session.addStream(name, format, byteArrayIs);
			return name;
		}

		throw new ServiceException("Unable to create the requested document.");
	}

	@Override
	public int save(int selectedDirectoryId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts) throws ServiceException {
		CommonSession session = getSession();
		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());

		// We check if there is already a saved query with this name
		if (pack.getSavedQueries() != null) {
			for (SavedQuery query : pack.getSavedQueries()) {
				if (query.getName().equals(fmdtDriller.getName())) {
					throw new ServiceException("A query with this name already exist.");
				}
			}
		}

		QuerySql query = getQuerySql(fmdtDriller.getBuilders().get(0), pack);
		SavedQuery savedQuery = new SavedQuery(fmdtDriller.getName(), fmdtDriller.getDescription(), query);
		savedQuery.setCharts(charts);
		pack.addSavedQuery(savedQuery);
		String xmlMetadata = null;
		try {
			xmlMetadata = session.getMetadata(fmdtDriller.getMetadataId()).get(0).getModel().getXml(false);
			session.getRepositoryConnection().getRepositoryService().updateModel(session.getRepositoryConnection().getRepositoryService().getDirectoryItem(fmdtDriller.getMetadataId()), xmlMetadata);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	private List<String> defineAttributes(FmdtTable table, FastVector atts) {
		List<String> attrTyrpe = new ArrayList<String>();
		List<String> columnNames = table.getDatas().get(0).getValues();
		for (int i = 0; i < columnNames.size(); i++) {
			String colName = columnNames.get(i);
			if (i < table.getDatabaseColumns().getColumnType().size()) {
				String type = table.getDatabaseColumns().getColumnType().get(i);
				switch (type) {

				case "java.lang.String":
					atts.addElement(new Attribute(colName, (FastVector) null));
					attrTyrpe.add("STRING");
					break;

				case "java.math.BigDecimal":
				case "java.lang.Boolean":
				case "java.lang.Integer":
				case "java.lang.Long":
				case "java.lang.Float":
				case "java.lang.Double":
					atts.addElement(new Attribute(colName));
					attrTyrpe.add("NUMERIC");
					break;

				case "java.sql.Date":
				case "java.sql.Time":
				case "java.sql.Timestamp":
					String format = extractTimestampInput(table.getDatas().get(1).getValues().get(i));
					if (format != null)
						atts.addElement(new Attribute(colName, format));
					else
						atts.addElement(new Attribute(colName, (FastVector) null));

					attrTyrpe.add("DATE");
					break;

				default:
					atts.addElement(new Attribute(colName, (FastVector) null));
					attrTyrpe.add("STRING");
					break;
				}
			}
			else {
				for (FmdtData column : table.getColumns()) {
					if (column.getLabel().equals(colName)) {
						if (column instanceof FmdtAggregate) {
							atts.addElement(new Attribute(colName));
							attrTyrpe.add("NUMERIC");
							break;
						}
						else if (column instanceof FmdtFormula) {
							atts.addElement(new Attribute(colName, (FastVector) null));
							attrTyrpe.add("STRING");
							break;
						}
					}
				}
			}
		}
		return attrTyrpe;
	}

	private void addAttributeValue(Instances data, FmdtTable table, List<String> types) {
		for (int i = 1; i < table.getDatas().size(); i++) {
			FmdtRow row = table.getDatas().get(i);
			double[] vals = new double[data.numAttributes()];

			for (int j = 0; j < row.getValues().size(); j++) {
				String type = types.get(j);
				String value = row.getValues().get(j);

				switch (type) {
				case "NUMERIC":
					try {
						vals[j] = Double.parseDouble(value);
					} catch (Exception e) {
						vals[j] = 0;
					}
					break;

				case "STRING":
					try {
						vals[j] = data.attribute(j).addStringValue(value);
					} catch (Exception e) {
						vals[j] = data.attribute(j).addStringValue("");
					}
					break;

				case "DATE":
					try {
						vals[j] = data.attribute(j).parseDate(value);
					} catch (Exception e) {
						vals[j] = 0;
					}
					break;

				default:
					vals[j] = 0;
					break;
				}
			}
			Instance inst = new Instance(1.0, vals);
			data.add(inst);
		}
	}

	String extractTimestampInput(String strDate) {
		final List<String> dateFormats = Arrays.asList("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy");

		for (String format : dateFormats) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				sdf.parse(strDate);
				return format;
			} catch (Exception e) {
				// intentionally empty
			}
		}
		return null;
	}

	@Override
	public void update(int directoryItemId, int metadataId, FmdtQueryDriller fmdtDriller, List<SavedChart> charts) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());

		QuerySql sqlquery = getQuerySql(fmdtDriller.getBuilders().get(0), pack);

		List<SavedQuery> queries = pack.getSavedQueries();
		SavedQuery savedQuery = new SavedQuery(fmdtDriller.getName(), fmdtDriller.getDescription(), sqlquery);
		savedQuery.setCharts(charts);

		for (SavedQuery query : queries) {
			if (query.getName().equals(fmdtDriller.getName())) {
				pack.removeSavedQuery(query);
				break;
			}
		}
		pack.addSavedQuery(savedQuery);

		String xmlMetadata = null;
		try {
			xmlMetadata = session.getMetadata(fmdtDriller.getMetadataId()).get(0).getModel().getXml(false);
			session.getRepositoryConnection().getRepositoryService().updateModel(session.getRepositoryConnection().getRepositoryService().getDirectoryItem(fmdtDriller.getMetadataId()), xmlMetadata);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void delete(int directoryItemId) throws ServiceException {
		CommonSession s = getSession();

		RepositoryItem directoryItem = null;
		try {
			directoryItem = s.getRepositoryConnection().getRepositoryService().getDirectoryItem(directoryItemId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get this item with id = " + directoryItemId);
		}

		try {
			s.getRepositoryConnection().getRepositoryService().delete(directoryItem);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete this item : " + directoryItem.getName());
		}
	}

	@Override
	public List<FmdtModel> getMetadataInfos(int metadataId) throws ServiceException {
		CommonSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		RepositoryItem i = null;
		try {
			i = sock.getRepositoryService().getDirectoryItem(metadataId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FmdtModel>();
		}

		System.out.println("Metadata loaded");

		String result = null;
		try {
			result = sock.getRepositoryService().loadModel(i);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FmdtModel>();
		}

		System.out.println("Model loaded");

		List<IBusinessModel> bModels = null;
		try {
			bModels = MetaDataReader.read(session.getCurrentGroup().getName(), IOUtils.toInputStream(result, "UTF-8"), sock, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}

		System.out.println("Model read");

		List<FmdtModel> models = new ArrayList<FmdtModel>();
		if (bModels != null) {
			for (IBusinessModel bmod : bModels) {
				FmdtModel modelDTO = new FmdtModel();
				modelDTO.setName(bmod.getName());

				List<IBusinessPackage> packages = bmod.getBusinessPackages(session.getCurrentGroup().getName());
				for (IBusinessPackage pack : packages) {
					if (pack.isExplorable()) {

						FmdtPackage packDTO = new FmdtPackage();
						packDTO.setName(pack.getName());

						List<String> connections = new ArrayList<String>();
						for (String connec : pack.getConnectionsNames(session.getCurrentGroup().getName())) {
							connections.add(connec);
						}
						packDTO.setConnections(connections);

						modelDTO.addPackage(packDTO);
					}
				}

				models.add(modelDTO);
			}
		}

		System.out.println("Client element created");

		session.addMetadata(metadataId, bModels);
		return models;
	}

	@Override
	public List<FmdtModel> getMetadataInfos(int metadataId, String vanillaUrl, String login, String password, Group group, Repository repository) throws ServiceException {
		CommonSession session = getSession();
		IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(vanillaUrl, login, password), group, repository));

		RepositoryItem i = null;
		try {
			i = sock.getRepositoryService().getDirectoryItem(metadataId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FmdtModel>();
		}

		System.out.println("Metadata loaded");

		String result = null;
		try {
			result = sock.getRepositoryService().loadModel(i);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FmdtModel>();
		}

		System.out.println("Model loaded");

		List<IBusinessModel> bModels = null;
		try {
			bModels = MetaDataReader.read(group.getName(), IOUtils.toInputStream(result, "UTF-8"), sock, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FmdtModel>();
		}

		System.out.println("Model read");

		List<FmdtModel> models = new ArrayList<FmdtModel>();
		if (bModels != null) {
			for (IBusinessModel bmod : bModels) {
				FmdtModel modelDTO = new FmdtModel();
				modelDTO.setName(bmod.getName());

				List<IBusinessPackage> packages = bmod.getBusinessPackages(group.getName());
				for (IBusinessPackage pack : packages) {
					if (pack.isExplorable()) {

						FmdtPackage packDTO = new FmdtPackage();
						packDTO.setName(pack.getName());

						List<String> connections = new ArrayList<String>();
						for (String connec : pack.getConnectionsNames(group.getName())) {
							connections.add(connec);
						}
						packDTO.setConnections(connections);

						modelDTO.addPackage(packDTO);
					}
				}

				models.add(modelDTO);
			}
		}

		System.out.println("Client element created");

		session.addMetadata(metadataId, bModels);
		return models;
	}

	@Override
	public FmdtTable getTable(FmdtDriller fmdtDriller) throws ServiceException {
		CommonSession session = getSession();

		List<IDataStreamElement> queryElements = new ArrayList<IDataStreamElement>();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		IBusinessTable t = pack.getBusinessTable(session.getCurrentGroup().getName(), fmdtDriller.getTableName());

		for (IDataStreamElement elem : t.getColumnsOrdered(session.getCurrentGroup().getName())) {
			queryElements.add(elem);
		}

		HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), t);

		List<IDataStreamElement> relatedFields = new ArrayList<IDataStreamElement>();
		for (IBusinessTable tab : relations.keySet()) {
			List<Relation> rel = relations.get(tab);
			for (Relation r : rel) {

				IDataStream leftTable = r.getLeftTable();
				boolean requested = false;
				for (IDataStreamElement ds : leftTable.getElements()) {
					if (t.getColumns(session.getCurrentGroup().getName()).contains(ds)) {
						requested = true;
						break;
					}
				}

				if (requested) {
					for (Join j : r.getJoins()) {
						relatedFields.add(j.getLeftElement());
						if (!queryElements.contains(j.getLeftElement())) {
							queryElements.add(j.getLeftElement());
						}
					}
				}

				else {
					for (Join j : r.getJoins()) {
						relatedFields.add(j.getRightElement());
						if (!queryElements.contains(j.getRightElement())) {
							queryElements.add(j.getRightElement());
						}
					}
				}

			}
		}

		HashMap<String, List<String>> relatedFieldsTables = new HashMap<String, List<String>>();
		for (IDataStreamElement element : relatedFields) {
			if (relatedFieldsTables.keySet().contains(element.getName())) {
				relatedFieldsTables.get(element.getName()).add(element.getDataStream().getName());
			}
			else {
				List<String> tables = new ArrayList<String>();
				tables.add(element.getDataStream().getName());
				relatedFieldsTables.put(element.getName(), tables);
			}
		}

		// recr�er la requete (champs + champs des relations)
		IQuery query = SqlQueryBuilder.getQuery(session.getCurrentGroup().getName(), queryElements, null, new ArrayList<AggregateFormula>(), null, null, null);
		List<FmdtRow> datas = new ArrayList<FmdtRow>();
		VanillaJdbcConnection connec = null;
		try {
			connec = ((SQLConnection) pack.getConnection(session.getCurrentGroup().getName(), fmdtDriller.getConnection())).getJdbcConnection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		VanillaPreparedStatement stmt = createStmt(connec);
		try {
			String q = pack.getQuery(null, session.getVanillaContext(), query, new ArrayList<List<String>>()).replace("`", "\"");
			ResultSet res = stmt.executeQuery(q);
			while (res.next()) {
				FmdtRow d = new FmdtRow();
				for (int i = 1; i < queryElements.size() + 1; i++) {
					d.getValues().add(res.getString(i));
				}
				d.setOriginalValues(new ArrayList<String>(d.getValues()));
				datas.add(d);
			}
			res.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			stmt.close();
			ConnectionManager.getInstance().returnJdbcConnection(connec);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String tableName = "";

		FmdtRow columnsName = new FmdtRow();
		FmdtDatabaseInfoRow databaseColumns = new FmdtDatabaseInfoRow();
		for (IDataStreamElement col : queryElements) {
			columnsName.getValues().add(col.getName());
			databaseColumns.getValues().add(col.getOrigin().getName());
			try {
				databaseColumns.getColumnType().add(col.getJavaClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			tableName = col.getOrigin().getTable().getName();
		}

		datas.add(0, columnsName);

		FmdtTable clientTable = new FmdtTable();
		clientTable.setDatabaseTable(tableName);
		clientTable.setDatabaseColumns(databaseColumns);
		clientTable.setName(fmdtDriller.getTableName());
		clientTable.setDatas(datas);
		clientTable.setRelatedTables(relatedFieldsTables);
		clientTable.setEditable(t.isEditable());
		try {
			clientTable.setQuery(pack.getQuery(null, session.getVanillaContext(), query, new ArrayList<List<String>>()).replace("`", "\""));
			clientTable.setQueryXml(((QuerySql) query).getXml());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientTable;

	}

	@Override
	public List<String> getTablesName(FmdtDriller fmdtDriller) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack != null) {
			List<String> tablesName = new ArrayList<String>();
			List<IBusinessTable> tables = pack.getFirstAccessibleTables(session.getCurrentGroup().getName());
			for (IBusinessTable t : tables) {
				tablesName.add(t.getName());
			}

			return tablesName;
		}
		return new ArrayList<String>();
	}

	private IBusinessPackage findBusinessPackage(CommonSession session, int metadataId, String modelName, String packageName) throws ServiceException {
		List<IBusinessModel> models = session.getMetadata(metadataId);
		if (models != null) {
			for (IBusinessModel mod : models) {
				if (mod.getName().equals(modelName)) {
					return mod.getBusinessPackage(packageName, session.getCurrentGroup().getName());
				}
			}
		}
		else {
			getMetadataInfos(metadataId);
			models = session.getMetadata(metadataId);
			for (IBusinessModel mod : models) {
				if (mod.getName().equals(modelName)) {
					return mod.getBusinessPackage(packageName, session.getCurrentGroup().getName());
				}
			}
		}

		return null;
	}

	@Override
	public FmdtTable drillOnTable(FmdtQueryDriller fmdtDriller, String originTable, String destinationTable, HashMap<String, String> values) throws ServiceException {
		CommonSession session = getSession();

		FmdtTable clientTable = new FmdtTable();
		clientTable.setName(originTable);

		List<IDataStreamElement> queryElements = new ArrayList<IDataStreamElement>();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		IBusinessTable originElem = pack.getBusinessTable(session.getCurrentGroup().getName(), originTable);
		IBusinessTable destElem = pack.getBusinessTable(session.getCurrentGroup().getName(), destinationTable);

		for (IDataStreamElement elem : destElem.getColumnsOrdered(session.getCurrentGroup().getName())) {
			queryElements.add(elem);
		}

		List<IFilter> filters = new ArrayList<IFilter>();

		HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), destElem);

		List<IDataStreamElement> relatedFields = new ArrayList<IDataStreamElement>();
		for (IBusinessTable tab : relations.keySet()) {
			List<Relation> rel = relations.get(tab);
			for (Relation r : rel) {
				IDataStream leftTable = r.getLeftTable();
				boolean requested = false;
				for (IDataStreamElement ds : leftTable.getElements()) {
					if (destElem.getColumns(session.getCurrentGroup().getName()).contains(ds)) {
						requested = true;
						break;
					}
				}

				if (requested) {
					for (Join j : r.getJoins()) {
						relatedFields.add(j.getLeftElement());
						if (!queryElements.contains(j.getLeftElement())) {
							queryElements.add(j.getLeftElement());
						}
					}
				}

				else {
					for (Join j : r.getJoins()) {
						relatedFields.add(j.getRightElement());
						if (!queryElements.contains(j.getRightElement())) {
							queryElements.add(j.getRightElement());
						}
					}
				}

			}
		}

		// filters
		HashMap<IBusinessTable, List<Relation>> relationsDrill = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), originElem);
		for (IBusinessTable tab : relationsDrill.keySet()) {
			if (tab.getName().equals(destElem.getName())) {
				List<Relation> rel = relationsDrill.get(tab);
				for (Relation r : rel) {

					IDataStream leftTable = r.getLeftTable();
					boolean requested = false;
					for (IDataStreamElement ds : leftTable.getElements()) {
						if (originElem.getColumns(session.getCurrentGroup().getName()).contains(ds)) {
							requested = true;
							break;
						}
					}

					if (requested) {
						for (Join j : r.getJoins()) {
							Filter f = new Filter();
							f.setOrigin(j.getRightElement());
							for (String val : values.keySet()) {
								if (val.equals(j.getLeftElement().getOuputName())) {
									List<String> v = new ArrayList<String>();
									v.add(values.get(val));
									f.setValues(v);
									break;
								}
							}
							filters.add(f);
						}
					}

					else {
						for (Join j : r.getJoins()) {
							Filter f = new Filter();
							f.setOrigin(j.getLeftElement());
							for (String val : values.keySet()) {
								if (val.equals(j.getRightElement().getOuputName())) {
									List<String> v = new ArrayList<String>();
									v.add(values.get(val));
									f.setValues(v);
									break;
								}
							}
							filters.add(f);
						}
					}

				}
			}
		}

		HashMap<String, List<String>> relatedFieldsTables = new HashMap<String, List<String>>();
		for (IDataStreamElement element : relatedFields) {
			if (relatedFieldsTables.keySet().contains(element.getName())) {
				relatedFieldsTables.get(element.getName()).add(findTableName(element, pack, session.getCurrentGroup().getName()));
			}
			else {
				List<String> tables = new ArrayList<String>();
				tables.add(findTableName(element, pack, session.getCurrentGroup().getName()));
				relatedFieldsTables.put(element.getName(), tables);
			}
		}

		List<Filter> oldFilters = new ArrayList<Filter>();
		for (FmdtDrillerFilter fil : clientTable.getFilters()) {
			IDataStreamElement elem = null;
			try {
				elem = destElem.getColumn(session.getCurrentGroup().getName(), fil.getOrigin());
			} catch (GrantException e) {
				e.printStackTrace();
			}

			if (elem != null) {

				Filter f = new Filter();
				f.setOrigin(elem);
				List<String> vals = new ArrayList<String>();
				vals.add(fil.getValue());
				f.setValues(vals);

				oldFilters.add(f);
			}
		}

		for (IFilter f : filters) {
			FmdtDrillerFilter dto = new FmdtDrillerFilter();
			dto.setOrigin(f.getOrigin().getName());
			dto.setValue(((Filter) f).getValues().get(0));
			dto.setName(dto.getOrigin() + dto.getValue());
			clientTable.addFilter(dto);
		}

		filters.addAll(oldFilters);

		IQuery query = SqlQueryBuilder.getQuery(session.getCurrentGroup().getName(), queryElements, null, new ArrayList<AggregateFormula>(), null, filters, null);
		List<FmdtRow> datas = new ArrayList<FmdtRow>();// null;
		VanillaJdbcConnection connec = null;
		try {
			connec = ((SQLConnection) pack.getConnection(session.getCurrentGroup().getName(), fmdtDriller.getConnection())).getJdbcConnection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		VanillaPreparedStatement stmt = createStmt(connec);
		try {
			String q = pack.getQuery(null, session.getVanillaContext(), query, new ArrayList<List<String>>()).replace("`", "\"");
			ResultSet res = stmt.executeQuery(q);
			while (res.next()) {
				FmdtRow d = new FmdtRow();
				for (int i = 1; i < queryElements.size() + 1; i++) {
					d.getValues().add(res.getString(i));
				}
				d.setOriginalValues(new ArrayList<String>(d.getValues()));
				datas.add(d);
			}
			res.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			stmt.close();
			ConnectionManager.getInstance().returnJdbcConnection(connec);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String tableName = destinationTable;
		FmdtRow columnsName = new FmdtRow();
		FmdtDatabaseInfoRow databaseColumns = new FmdtDatabaseInfoRow();
		List<FmdtData> listColumns = new ArrayList<FmdtData>();
		for (IDataStreamElement col : queryElements) {
			columnsName.getValues().add(col.getName());
			databaseColumns.getValues().add(col.getOrigin().getName());
			try {
				databaseColumns.getColumnType().add(col.getJavaClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			listColumns.add(new FmdtColumn(col.getName(), col.getOriginName(), col.getDescription(), tableName));
		}

		datas.add(0, columnsName);
		clientTable.setDatabaseTable(tableName);
		clientTable.setDatabaseColumns(databaseColumns);
		clientTable.setEditable(destElem.isEditable());
		clientTable.setDatas(datas);
		clientTable.setColumns(listColumns);
		clientTable.setRelatedTables(relatedFieldsTables);
		try {
			clientTable.setQuery(pack.getQuery(null, session.getVanillaContext(), query, new ArrayList<List<String>>()).replace("`", "\""));
			clientTable.setQueryXml(((QuerySql) query).getXml());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientTable;
	}

	@Override
	public FmdtTable manageFilter(FmdtDriller fmdtDriller, FmdtTable table, FmdtDrillerFilter filter, boolean add) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		SqlQueryDigester dig = null;
		try {
			dig = new SqlQueryDigester(IOUtils.toInputStream(table.getQueryXml()), session.getCurrentGroup().getName(), pack);
		} catch (Exception e) {
			e.printStackTrace();

		}

		QuerySql q = null;
		try {
			q = dig.getModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// R�cup�ration des champs des relations s'ils n'existent pas dans
		// la table
		if (q.getSelect().size() != table.getDatas().get(0).getValues().size()) {
			IBusinessTable t = pack.getBusinessTable(session.getCurrentGroup().getName(), table.getName());
			List<IDataStreamElement> queryElements = new ArrayList<IDataStreamElement>();
			for (IDataStreamElement elem : t.getColumnsOrdered(session.getCurrentGroup().getName())) {
				queryElements.add(elem);
			}
			HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), t);
			// pour chaque relation, recup champ + valeur (ce sera cach�)
			List<IDataStreamElement> relatedFields = new ArrayList<IDataStreamElement>();
			for (IBusinessTable tab : relations.keySet()) {
				List<Relation> rel = relations.get(tab);
				for (Relation r : rel) {

					IDataStream leftTable = r.getLeftTable();
					boolean requested = false;
					for (IDataStreamElement ds : leftTable.getElements()) {
						if (t.getColumns(session.getCurrentGroup().getName()).contains(ds)) {
							requested = true;
							break;
						}
					}

					if (requested) {
						for (Join j : r.getJoins()) {
							relatedFields.add(j.getLeftElement());
							if (!queryElements.contains(j.getLeftElement())) {
								queryElements.add(j.getLeftElement());
							}
						}
					}

					else {
						for (Join j : r.getJoins()) {
							relatedFields.add(j.getRightElement());
							if (!queryElements.contains(j.getRightElement())) {
								queryElements.add(j.getRightElement());
							}
						}
					}

				}
			}
			q.getSelect().clear();
			q.getSelect().addAll(queryElements);
		}

		IBusinessTable businessTable = pack.getBusinessTable(session.getCurrentGroup().getName(), table.getName());

		if (filter != null && add) {
			table.addFilter(filter);
		}
		else if (filter != null && !add) {
			table.removeFilter(filter.getName());
		}

		// ----------------------------
		q.clearFilter();
		for (FmdtDrillerFilter fil : table.getFilters()) {
			IDataStreamElement elem = null;
			try {
				elem = businessTable.getColumn(session.getCurrentGroup().getName(), fil.getOrigin());
			} catch (GrantException e) {
				e.printStackTrace();
			}

			if (elem != null) {

				Filter f = new Filter();
				f.setOrigin(elem);
				List<String> values = new ArrayList<String>();
				values.add(fil.getValue());
				f.setValues(values);

				q.addFilter(f);
			}
		}

		// ----------------------------
		List<List<String>> data = null;
		try {
			data = pack.executeQuery(null, session.getVanillaContext(), fmdtDriller.getConnection(), q, new ArrayList<List<String>>());
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<FmdtRow> datas = new ArrayList<FmdtRow>();

		for (List<String> d : data) {
			FmdtRow row = new FmdtRow();
			for (String s : d) {
				row.getValues().add(s);
			}
			datas.add(row);
		}

		FmdtRow colName = new FmdtRow();
		for (IDataStreamElement col : q.getSelect()) {
			colName.getValues().add(col.getName());
		}

		datas.add(0, colName);

		try {
			table.setQuery(pack.getQuery(null, session.getVanillaContext(), q, new ArrayList<List<String>>()).replace("`", "\""));
			table.setQueryXml(((QuerySql) q).getXml());
		} catch (Exception e) {
			e.printStackTrace();
		}

		table.setDatas(datas);
		return table;
	}

	@Override
	public FmdtQueryDriller openView(RepositoryItem item) throws ServiceException {
		CommonSession session = getSession();
		getMetadataInfos(item.getId());

		IBusinessPackage packag = findBusinessPackage(session, item.getId(), item.getPublicVersion(), item.getInternalVersion());
		List<SavedQuery> queries = packag.getSavedQueries();
		SavedQuery query = null;
		for (SavedQuery q : queries) {
			if (q.getName().equals(item.getItemName())) {
				query = q;
				break;
			}
		}
		FmdtQueryDriller fmdtDriller = new FmdtQueryDriller();
		fmdtDriller.setName(item.getItemName());
		fmdtDriller.setMetadataId(item.getId());
		fmdtDriller.setModelName(item.getPublicVersion());
		fmdtDriller.setPackageName(item.getInternalVersion());

		QuerySql sqlquery = null;
		try {
			sqlquery = query.loadQuery(session.getCurrentGroup().getName(), packag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sqlquery != null)
			fmdtDriller.addBuilders(convertQueryToBuilder(sqlquery, packag, session.getCurrentGroup().getName()));

		if (query.hasChart()) {
			List<SavedChart> charts = query.loadChart();
			fmdtDriller.setCharts(charts);
		}
		return fmdtDriller;
	}

	@Override
	public FmdtQueryBuilder loadMetadataView(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt, Dataset dataset) throws ServiceException {
		try {
			CommonSession session = getSession();

			Group group = session.getVanillaApi().getVanillaSecurityManager().getGroupById(datasourceFmdt.getGroupId());
			session.setCurrentGroup(group);

			getMetadataInfos(datasourceFmdt.getItemId());

			IBusinessPackage packag = findBusinessPackage(session, datasourceFmdt.getItemId(), datasourceFmdt.getBusinessModel(), datasourceFmdt.getBusinessPackage());

			FmdtQueryDriller fmdtDriller = new FmdtQueryDriller();
			fmdtDriller.setName(dataset.getName());
			fmdtDriller.setMetadataId(datasourceFmdt.getItemId());
			fmdtDriller.setModelName(datasourceFmdt.getBusinessModel());
			fmdtDriller.setPackageName(datasourceFmdt.getBusinessPackage());

			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(dataset.getRequest(), "UTF-8"), group.getName(), packag);

			QuerySql sqlquery = dig.getModel();
			return convertQueryToBuilder(sqlquery, packag, session.getCurrentGroup().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}

	}

	@Override
	public FmdtTable addOrderBy(FmdtDriller fmdtDriller, FmdtTable table, String field, boolean group) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		SqlQueryDigester dig = null;
		try {
			dig = new SqlQueryDigester(IOUtils.toInputStream(table.getQueryXml()), session.getCurrentGroup().getName(), pack);
		} catch (Exception e) {
			e.printStackTrace();
		}

		QuerySql q = null;
		try {
			q = dig.getModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (q.getSelect().size() != table.getDatas().get(0).getValues().size()) {
			IBusinessTable t = pack.getBusinessTable(session.getCurrentGroup().getName(), table.getName());
			List<IDataStreamElement> queryElements = new ArrayList<IDataStreamElement>();
			for (IDataStreamElement elem : t.getColumnsOrdered(session.getCurrentGroup().getName())) {
				queryElements.add(elem);
			}
			HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), t);
			// pour chaque relation, recup champ + valeur (ce sera cach�)
			List<String> relatedTables = new ArrayList<String>();
			List<String> relatedFields = new ArrayList<String>();
			for (IBusinessTable tab : relations.keySet()) {
				List<Relation> rel = relations.get(tab);
				for (Relation r : rel) {
					if (!relatedTables.contains(tab.getName())) {
						relatedTables.add(tab.getName());
					}

					IDataStream leftTable = r.getLeftTable();
					boolean requested = false;
					for (IDataStreamElement ds : leftTable.getElements()) {
						if (t.getColumns(session.getCurrentGroup().getName()).contains(ds)) {
							requested = true;
							break;
						}
					}

					if (requested) {
						for (Join j : r.getJoins()) {
							relatedFields.add(j.getLeftName());
							if (!queryElements.contains(j.getLeftElement())) {
								queryElements.add(j.getLeftElement());
							}
						}
					}

					else {
						for (Join j : r.getJoins()) {
							relatedFields.add(j.getRightName());
							if (!queryElements.contains(j.getRightElement())) {
								queryElements.add(j.getRightElement());
							}
						}
					}
				}
			}
			q.getSelect().clear();
			q.getSelect().addAll(queryElements);
		}

		IBusinessTable businessTable = pack.getBusinessTable(session.getCurrentGroup().getName(), table.getName());

		IDataStreamElement elem = null;
		try {
			elem = businessTable.getColumn(session.getCurrentGroup().getName(), field);
		} catch (GrantException e) {
			e.printStackTrace();
		}

		List<Ordonable> orders = q.getOrderBy();
		orders.add(elem);
		q.setOrberBy(orders);

		if (group) {
			List<IDataStreamElement> elements = q.getSelect();
			int ind = 0;
			for (int i = 0; i < elements.size(); i++) {
				IDataStreamElement el = elements.get(i);
				if (el.getName().equalsIgnoreCase(field)) {
					ind = i;
					break;
				}
			}
			if (table.isGroup()) {
				int taille = table.getGroupNames().size();
				IDataStreamElement el = q.getSelect().remove(ind);
				q.getSelect().add(taille, el);
			}
			else {
				IDataStreamElement el = q.getSelect().remove(ind);
				q.getSelect().add(0, el);
			}
		}

		List<List<String>> data = null;
		try {
			data = pack.executeQuery(null, session.getVanillaContext(), fmdtDriller.getConnection(), q, new ArrayList<List<String>>());
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<FmdtRow> datas = new ArrayList<FmdtRow>();

		for (List<String> d : data) {
			FmdtRow row = new FmdtRow();
			for (String s : d) {
				row.getValues().add(s);
			}
			datas.add(row);
		}

		FmdtRow colName = new FmdtRow();
		for (IDataStreamElement col : q.getSelect()) {
			colName.getValues().add(col.getName());
		}

		datas.add(0, colName);

		try {
			table.setQuery(pack.getQuery(null, session.getVanillaContext(), q, new ArrayList<List<String>>()).replace("`", "\""));
			table.setQueryXml(((QuerySql) q).getXml());
		} catch (Exception e) {
			e.printStackTrace();
		}

		table.setDatas(datas);
		if (group) {
			table.setGroup(true);
			table.getGroupNames().add(field);
		}
		else {
			table.getOrderNames().add(field);
		}
		return table;
	}

	public VanillaPreparedStatement createStmt(VanillaJdbcConnection connection) {
		VanillaJdbcConnection con = null;
		try {
			con = connection;
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		VanillaPreparedStatement stmt = null;
		try {
			stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
		} catch (SQLException e) {
			try {
				stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				stmt.setFetchSize(1);
			} catch (Exception ex) {
				try {
					stmt.setFetchSize(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return stmt;
	}

	@Override
	public FmdtTable updateValues(FmdtDriller fmdtDriller, FmdtTable table) throws ServiceException {

		List<FmdtRow> toDelete = new ArrayList<FmdtRow>();
		List<FmdtRow> toUpdate = new ArrayList<FmdtRow>();
		List<FmdtRow> toAdd = new ArrayList<FmdtRow>();

		for (int i = 1; i < table.getDatas().size(); i++) {
			FmdtRow row = table.getDatas().get(i);
			if (row.getType() == FmdtRow.Type.ADDED) {
				toAdd.add(row);
			}
			else if (row.getType() == FmdtRow.Type.DELETED) {
				toDelete.add(row);
			}
			else if (row.getType() == FmdtRow.Type.UPDATED) {
				toUpdate.add(row);
			}
		}

		addRows(fmdtDriller, table, toAdd);
		deleteRows(fmdtDriller, table, toDelete);
		updateRows(fmdtDriller, table, toUpdate);

		return getTable(fmdtDriller);
	}

	private void updateRows(FmdtDriller fmdtDriller, FmdtTable table, List<FmdtRow> toUpdate) throws ServiceException {

		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());

		try {
			SQLConnection con = (SQLConnection) pack.getConnection("none", null);
		
			for (FmdtRow row : toUpdate) {
				StringBuffer buf = new StringBuffer();
				buf.append("Update " + table.getDatabaseTable() + " set ");
				for (int i = 0; i < table.getDatabaseColumns().getValues().size(); i++) {
					if (i > 0) {
						buf.append(",");
					}
					String columnName = table.getDatabaseColumns().getValues().get(i);
					//Ugly trick for postgressql to remove tablename
					if (con.getDriverName().toLowerCase().contains("postgres")) {
						columnName = columnName.replace(table.getDatabaseTable() + ".", "");
					}
					
					buf.append(columnName + " = " + getColumnValue(table.getDatabaseColumns().getColumnType().get(i), row.getValues().get(i)));
				}
				buf.append(" where ");
				for (int i = 0; i < table.getDatabaseColumns().getValues().size(); i++) {
					if (i > 0) {
						buf.append(" and ");
					}
					buf.append(table.getDatabaseColumns().getValues().get(i) + " = " + getColumnValue(table.getDatabaseColumns().getColumnType().get(i), row.getOriginalValues().get(i)));
				}

				System.out.println(buf.toString());
				
				VanillaJdbcConnection sock = con.getJdbcConnection();
				VanillaPreparedStatement stmt = sock.prepareQuery(buf.toString());

				stmt.executeUpdate();

				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(sock);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getColumnValue(String columnType, String value) {
		if (columnType.equalsIgnoreCase("java.lang.Integer") || columnType.equalsIgnoreCase("java.lang.Float") || columnType.equalsIgnoreCase("java.lang.Double")) {
			return value;
		}
		return "'" + value + "'";
	}

	private void deleteRows(FmdtDriller fmdtDriller, FmdtTable table, List<FmdtRow> toDelete) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());

		for (FmdtRow row : toDelete) {
			StringBuffer buf = new StringBuffer();
			buf.append("Delete From " + table.getDatabaseTable() + " where ");
			for (int i = 0; i < table.getDatabaseColumns().getValues().size(); i++) {
				if (i > 0) {
					buf.append(" and ");
				}
				buf.append(table.getDatabaseColumns().getValues().get(i) + " = " + getColumnValue(table.getDatabaseColumns().getColumnType().get(i), row.getOriginalValues().get(i)));
			}

			System.out.println(buf.toString());
			try {
				SQLConnection con = (SQLConnection) pack.getConnection("none", null);
				VanillaJdbcConnection sock = con.getJdbcConnection();
				VanillaPreparedStatement stmt = sock.prepareQuery(buf.toString());

				stmt.executeUpdate();

				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(sock);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void addRows(FmdtDriller fmdtDriller, FmdtTable table, List<FmdtRow> toAdd) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());

		for (FmdtRow row : toAdd) {
			StringBuffer buf = new StringBuffer();
			buf.append("insert into " + table.getDatabaseTable() + "(");
			for (int i = 0; i < table.getDatabaseColumns().getValues().size(); i++) {
				if (i > 0) {
					buf.append(",");
				}
				buf.append(table.getDatabaseColumns().getValues().get(i));
			}

			buf.append(") values (");

			for (int i = 0; i < table.getDatabaseColumns().getValues().size(); i++) {
				if (i > 0) {
					buf.append(",");
				}
				buf.append(getColumnValue(table.getDatabaseColumns().getColumnType().get(i), row.getValues().get(i)));
			}

			buf.append(")");

			System.out.println(buf.toString());
			try {
				SQLConnection con = (SQLConnection) pack.getConnection("none", null);
				VanillaJdbcConnection sock = con.getJdbcConnection();
				VanillaPreparedStatement stmt = sock.prepareQuery(buf.toString());

				stmt.executeUpdate();

				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(sock);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public FmdtQueryDatas getTables(FmdtQueryDriller fmdtDriller) throws ServiceException {

		CommonSession session = getSession();

		FmdtQueryDatas datas = new FmdtQueryDatas();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}
		List<IBusinessTable> tables = pack.getExplorableTables(session.getCurrentGroup().getName());

		if (tables.isEmpty())
			System.out.println("Not explorable tables");

		List<FmdtTableStruct> tablesStruct = new ArrayList<FmdtTableStruct>();
		for (IBusinessTable table : tables) {
			FmdtTableStruct tableStruct = new FmdtTableStruct(table.getName(), ((AbstractBusinessTable) table).getOuputName(), table.getDescription(), null);
			List<FmdtColumn> columns = new ArrayList<FmdtColumn>();
			for (IDataStreamElement col : table.getColumns(session.getCurrentGroup().getName())) {
				FmdtColumn column = new FmdtColumn(col.getName(), col.getOuputName(), col.getDescription(), table.getName(), col.getDataStream().getName());

				if (col instanceof ICalculatedElement) {
					column.setFormula(((ICalculatedElement) col).getFormula());
				}
				column.setOriginName(col.getOriginName());
				try {
					column.setSqlType(((SQLColumn) col.getOrigin()).getSqlType());
					column.setJavaType(((SQLColumn) col.getOrigin()).getClassName());
				} catch (Exception e) {
				}
				column.setType(getColumnType(col.getType()));
				column.setD4ctypes(col.getD4cTypes());

				columns.add(column);
			}
			tableStruct.setColumns(columns);

			//XXX
			for (IBusinessTable subt : table.getChilds(session.getCurrentGroup().getName())) {
				FmdtTableStruct subStruct = new FmdtTableStruct(subt.getName(), ((AbstractBusinessTable) subt).getOuputName(), subt.getDescription(), null);
				List<FmdtColumn> subcols = new ArrayList<FmdtColumn>();
				for (IDataStreamElement subcol : subt.getColumns(session.getCurrentGroup().getName())) {
					FmdtColumn subc = new FmdtColumn(subcol.getName(), subcol.getOuputName(), subcol.getDescription(), subt.getName(), subcol.getDataStream().getName());

					if (subcol instanceof ICalculatedElement) {
						subc.setFormula(((ICalculatedElement) subcol).getFormula());
					}
					subc.setOriginName(subcol.getOriginName());
					try {
						subc.setSqlType(((SQLColumn) subcol.getOrigin()).getSqlType());
						subc.setJavaType(((SQLColumn) subcol.getOrigin()).getClassName());
					} catch (Exception e) {
					}
					subc.setType(getColumnType(subcol.getType()));
					subc.setD4ctypes(subcol.getD4cTypes());

					subcols.add(subc);
				}
				subStruct.setColumns(subcols);
				tableStruct.getSubTables().add(subStruct);
			}

			tablesStruct.add(tableStruct);
		}
		datas.setTables(tablesStruct);

		List<IResource> resources = pack.getResources(session.getCurrentGroup().getName());

		List<FmdtFilter> filters = new ArrayList<FmdtFilter>();

		for (IResource resource : resources) {
			FmdtFilter filter = new FmdtFilter(resource.getName(), resource.getOutputName(), "", resource.getXml(), resource.isGrantedFor(session.getCurrentGroup().getName()));

			if (resource instanceof Filter) {
				FmdtColumn column = new FmdtColumn(((Filter) resource).getOrigin().getName(), ((Filter) resource).getOrigin().getOuputName(), ((Filter) resource).getOrigin().getDescription(), ((Filter) resource).getOrigin().getDataStream().getName());
				filter.setColumn(column);
				filter.setOperator("IN");
				filter.setValues(((Filter) resource).getValues());
				filter.setType(FmdtConstant.FILTER);
			}
			else if (resource instanceof ComplexFilter) {
				FmdtColumn column = new FmdtColumn(((ComplexFilter) resource).getOrigin().getName(), ((ComplexFilter) resource).getOrigin().getOuputName(), ((ComplexFilter) resource).getOrigin().getDescription(), ((ComplexFilter) resource).getOrigin().getDataStream().getName());
				filter.setColumn(column);
				filter.setOperator(((ComplexFilter) resource).getOperator());
				filter.setValues(((ComplexFilter) resource).getValue());
				filter.setType(FmdtConstant.FILTERCOMPLEX);
			}
			else if (resource instanceof SqlQueryFilter) {
				FmdtColumn column = new FmdtColumn(((SqlQueryFilter) resource).getOrigin().getName(), ((SqlQueryFilter) resource).getOrigin().getOuputName(), ((SqlQueryFilter) resource).getOrigin().getDescription(), ((SqlQueryFilter) resource).getOrigin().getDataStream().getName());
				filter.setColumn(column);
				filter.setOperator(((SqlQueryFilter) resource).getQuery());
				filter.setValues(new ArrayList<String>());
				filter.setType(FmdtConstant.FILTERSQL);
			}
			else if (resource instanceof Prompt) {
				FmdtColumn column = null;
				try {
					column = new FmdtColumn(((Prompt) resource).getOrigin().getName(), ((Prompt) resource).getOrigin().getOuputName(), ((Prompt) resource).getOrigin().getDescription(), ((Prompt) resource).getOrigin().getDataStream().getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				filter.setColumn(column);
				filter.setOperator(((Prompt) resource).getOperator());
				filter.setValues(new ArrayList<String>());
				filter.setType(FmdtConstant.FILTERPROMPT);
			}
			filters.add(filter);
		}
		datas.setFilters(filters);

		return datas;
	}

	private ColumnType getColumnType(SubType type) {
		switch (type.getParentType()) {
		case UNDEFINED:
			return ColumnType.UNDEFINED;
		case DATE:
			return ColumnType.DIMENSION;
		case MEASURE:
			return ColumnType.MEASURE;
		case GEO:
			return ColumnType.DIMENSION;
		case DIMENSION:
			return ColumnType.DIMENSION;
		case PROPERTY:
			return ColumnType.PROPERTY;
		default:
			break;
		}
		return ColumnType.UNDEFINED;
	}

	@Override
	public List<FmdtTable> getRequestValue(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted) throws ServiceException {

		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		List<IResource> resources = new ArrayList<IResource>();

		List<IDataStreamElement> columns = convertColumns(builder.getColumns(), pack, groupName);
		List<AggregateFormula> aggregates = convertAggregate(builder.getAggregates(), pack, groupName);
		List<Prompt> prompts = convertPrompts(builder.getPromptFilters(), (BusinessPackage) pack, groupName, resources);
		List<IFilter> filters = convertFilter(builder.getFilters(), (BusinessPackage) pack, groupName, resources);
		List<Formula> formulas = convertformula(builder.getFormulas(), resources);

		List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();

		HashMap<Prompt, List<String>> promptMap = convertPromptValue(builder.getPromptFilters(), (BusinessPackage) pack, groupName);

		List<Ordonable> ordonables = new ArrayList<Ordonable>();

		for (FmdtData data : builder.getListColumns()) {
			if (data instanceof FmdtColumn) {
				for (IDataStreamElement column : columns) {
					if (data.getName().equals(column.getName())) {
						ordonables.add((Ordonable) column);
						break;
					}
				}
			}
			if (data instanceof FmdtFormula) {
				for (Formula formula : formulas) {
					if (data.getName().equals(formula.getName()) && ((FmdtFormula) data).getScript().equals(formula.getFormula())) {
						ordonables.add((Ordonable) formula);
						break;
					}
				}
			}
		}

		HashMap<String, List<String>> relatedFieldsTables = new HashMap<String, List<String>>();
		for (FmdtColumn column : builder.getColumns()) {
			IBusinessTable t = pack.getBusinessTable(groupName, column.getTableName());
			try {
				HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), t);

				for (IBusinessTable tab : relations.keySet()) {
					List<Relation> rel = relations.get(tab);
					for (Relation r : rel) {

						IDataStream leftTable = r.getLeftTable();
						for (IDataStreamElement ds : leftTable.getElements()) {
							if (column.getName().equals(ds.getName())) {
								for (Join j : r.getJoins()) {
									IDataStreamElement element = null;
									if (j.getLeftElement().getName().equals(column.getName()))
										element = j.getRightElement();
									else if (j.getRightElement().getName().equals(column.getName()))
										element = j.getLeftElement();

									if (element != null) {
										if (relatedFieldsTables.keySet().contains(column.getName())) {
											String tableName = findTableName(element, pack, groupName);
											if (tableName != null && !relatedFieldsTables.get(column.getName()).contains(tableName))
												relatedFieldsTables.get(column.getName()).add(tableName);
										}
										else {
											List<String> tables = new ArrayList<String>();
											String tableName = findTableName(element, pack, groupName);
											if (tableName != null)
												tables.add(tableName);
											relatedFieldsTables.put(column.getName(), tables);
										}
									}

								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		}
		QuerySql query = (QuerySql) SqlQueryBuilder.getQuery(groupName, columns, new HashMap<ListOfValue, String>(), aggregates, ordonables, filters, prompts, formulas, selectedStrategies);

		if (builder.isDistinct()) {
			query.setDistinct(true);
		}
		if (builder.isLimit()) {
			query.setLimit(builder.getNbLimit());
		}
		for (IResource r : resources) {
			query.addDesignTimeResource(r);
		}

		List<FmdtRow> datas = new ArrayList<FmdtRow>();
		String conName = ((SQLDataSource) ((BusinessPackage) pack).getDataSources(groupName).get(0)).getConnections().get(0).getName();

		try {

			IRepositoryContext ctx = new BaseRepositoryContext(session.getVanillaContext(), session.getCurrentGroup(), session.getCurrentRepository());
			EffectiveQuery sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, query, groupName, false, promptMap);
			String valueQuery = sqlQuery.getGeneratedQuery();
			List<List<String>> values = ((BusinessPackage) pack).executeQuery(0, conName, valueQuery);

			for (List<String> value : values) {
				FmdtRow d = new FmdtRow();
				for (String col : value) {
					d.getValues().add(col);
				}
				d.setOriginalValues(new ArrayList<String>(d.getValues()));
				datas.add(d);
			}

			String tableName = "";

			List<FmdtData> listColumns = new ArrayList<FmdtData>();
			FmdtRow columnsName = new FmdtRow();
			FmdtDatabaseInfoRow databaseColumns = new FmdtDatabaseInfoRow();
			for (IDataStreamElement col : columns) {
				columnsName.getValues().add(col.getOuputName());
				try {
					databaseColumns.getValues().add(col.getOrigin().getName());
					databaseColumns.getColumnType().add(col.getJavaClassName());
					tableName = col.getOrigin().getTable().getName();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			for (Formula fo : formulas) {
				columnsName.getValues().add(fo.getName());
			}
			for (AggregateFormula agg : aggregates) {
				columnsName.getValues().add(agg.getOutputName());
			}

			for (FmdtColumn col : builder.getColumns()) {
				listColumns.add(new FmdtColumn(col.getName(), col.getLabel(), col.getDescription(), col.getTableName()));
			}
			for (FmdtFormula fo : builder.getFormulas()) {
				listColumns.add(new FmdtFormula(fo.getName(), fo.getDescription(), fo.getScript(), fo.getDataStreamInvolved()));
			}
			for (FmdtAggregate agg : builder.getAggregates()) {
				listColumns.add(new FmdtAggregate(agg.getCol(), agg.getTable(), agg.getOutputName(), agg.getDescription(), agg.getOperator()));
			}

			datas.add(0, columnsName);

			FmdtTable clientTable = new FmdtTable();
			clientTable.setDatabaseTable(tableName);
			clientTable.setDatabaseColumns(databaseColumns);
			clientTable.setName(fmdtDriller.getName());
			clientTable.setDatas(datas);
			clientTable.setRelatedTables(relatedFieldsTables);
			clientTable.setColumns(listColumns);
			clientTable.setEditable(false);
			try {
				clientTable.setQuery(valueQuery);
				clientTable.setQueryXml(((QuerySql) query).getXml());
			} catch (Exception e) {
				e.printStackTrace();
				clientTable.setQuery(sqlQuery.getGeneratedQuery());
			}
			List<FmdtTable> tables = new ArrayList<FmdtTable>();
			tables.add(new FmdtTable(clientTable));

			if (formatted && !aggregates.isEmpty()) {
				List<FmdtRow> datasformatted = new ArrayList<FmdtRow>();
				for (List<String> value : values) {
					FmdtRow d = new FmdtRow();
					for (String col : value) {
						d.getValues().add(col);
					}
					d.setOriginalValues(new ArrayList<String>(d.getValues()));
					datasformatted.add(d);
				}

				List<IDataStreamElement> columnsAdded = new ArrayList<IDataStreamElement>();
				List<Ordonable> ordonablesTemp = new ArrayList<Ordonable>();
				for (int i = 0; i < columns.size() - 1; i++) {
					columnsAdded.add(columns.get(i));
					ordonablesTemp.add(i, (Ordonable) columns.get(i));

					QuerySql querytemp = (QuerySql) SqlQueryBuilder.getQuery(groupName, columnsAdded, new HashMap<ListOfValue, String>(), aggregates, ordonablesTemp, filters, prompts, new ArrayList<Formula>(), selectedStrategies);
					querytemp.setDistinct(query.getDistinct());
					querytemp.setLimit(query.getLimit());

					EffectiveQuery sqlQueryTemp = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, querytemp, groupName, false, promptMap);
					String valueQuerytemp = sqlQueryTemp.getGeneratedQuery();
					List<List<String>> valuesAdded = ((BusinessPackage) pack).executeQuery(0, conName, valueQuerytemp);
					addRowAggregate(datasformatted, valuesAdded, columnsAdded.size() - 1, columns.size() + formulas.size());
				}
				for (FmdtRow row : datasformatted) {
					try {
						if (row.getValues().get(columns.size() - 1) != null && !row.getValues().get(columns.size() - 1).equals("")) {
							for (int i = 0; i < columns.size() - 1; i++) {
								row.getValues().set(i, "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				datasformatted.add(0, columnsName);
				clientTable.setDatas(datasformatted);
				tables.add(new FmdtTable(clientTable));
			}
			return tables;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<FmdtTable>();
	}

	@Override
	public List<ChartData> getChartData(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller, Boolean formatted) throws ServiceException {
		List<ChartData> chartData = new ArrayList<ChartData>();
		try {
			List<FmdtTable> tables = getRequestValue(builder, fmdtDriller, formatted);
			FmdtTable table = tables.get(0);

			List<FmdtRow> rows = table.getDatas();
			rows.remove(rows.get(0));

			for (FmdtRow row : rows) {
				String groupValue = "";
				List<String> serieValues = new ArrayList<String>();
				for (int i = 0; i < row.getValues().size(); i++) {
					if (i == 0) {
						groupValue = row.getValues().get(i);
					}
					else {
						serieValues.add(row.getValues().get(i));
					}
				}

				chartData.add(new ChartData(groupValue, serieValues));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chartData;
	}

	@Override
	public String getRequest(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName) throws ServiceException {

		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		IBusinessPackage pack = findBusinessPackage(session, metadataId, modelName, packageName);
		if (pack == null) {
			return null;
		}

		HashMap<Prompt, List<String>> promptMap = convertPromptValue(builder.getPromptFilters(), (BusinessPackage) pack, groupName);

		QuerySql query = getQuerySql(builder, pack);

		IRepositoryContext ctx = new BaseRepositoryContext(session.getVanillaContext(), session.getCurrentGroup(), session.getCurrentRepository());
		EffectiveQuery sqlQuery;
		try {
			sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, query, groupName, false, promptMap, false);

			return sqlQuery.getGeneratedQuery();
		} catch (MetaDataException e) {
			e.printStackTrace();
			return new String();
		}
	}

	private List<IDataStreamElement> convertColumns(List<FmdtColumn> fmdtColumns, IBusinessPackage bPackage, String groupName) {
		try {
			if (fmdtColumns != null && !fmdtColumns.isEmpty()) {
				List<IDataStreamElement> columns = new ArrayList<IDataStreamElement>();
				for (FmdtColumn fmdtcolumn : fmdtColumns) {
					try {
						IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtcolumn.getTableName());
						columns.add(btable.getColumn(groupName, fmdtcolumn.getName()));
					} catch (Exception e) {
					}
				}
				return columns;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<IDataStreamElement>();
	}

	private List<AggregateFormula> convertAggregate(List<FmdtAggregate> fmdtAggregates, IBusinessPackage bPackage, String groupName) {
		try {
			if (fmdtAggregates != null && !fmdtAggregates.isEmpty()) {
				List<AggregateFormula> aggregates = new ArrayList<AggregateFormula>();
				for (FmdtAggregate fmdtAggregate : fmdtAggregates) {
					if (!fmdtAggregate.isBasedOnFormula()) {
						IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtAggregate.getTable());
						IDataStreamElement col = btable.getColumn(groupName, fmdtAggregate.getCol());
						aggregates.add(new AggregateFormula(fmdtAggregate.getOperator(), col, fmdtAggregate.getOutputName()));
					}
					else {
						aggregates.add(new AggregateFormula(fmdtAggregate.getOperator(), new Formula(fmdtAggregate.getName(), fmdtAggregate.getFunction(), fmdtAggregate.getFormulaData()), fmdtAggregate.getOutputName()));
					}
				}
				return aggregates;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<AggregateFormula>();
	}

	private List<Prompt> convertPrompts(List<FmdtFilter> fmdtPrompts, BusinessPackage bPackage, String groupName, List<IResource> resourcesAdded) {
		try {
			if (fmdtPrompts != null && !fmdtPrompts.isEmpty()) {
				List<Prompt> prompts = new ArrayList<Prompt>();
				for (FmdtFilter fmdtPrompt : fmdtPrompts) {
					IResource prompt = bPackage.getResourceByName(fmdtPrompt.getName());
					if (prompt != null) {
						prompts.add((Prompt) prompt);
					}
					else {
						Prompt p = new Prompt();
						p.setName(fmdtPrompt.getName());

						IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtPrompt.getColumn().getTableName());
						p.setGotoDataStreamElement(btable.getColumn(groupName, fmdtPrompt.getColumn().getName()));
						p.setOrigin(btable.getColumn(groupName, fmdtPrompt.getColumn().getName()));
						p.setOperator(fmdtPrompt.getOperator());

						if (fmdtPrompt.isCreate())
							resourcesAdded.add(p);
						else
							prompts.add(p);
					}
				}
				return prompts;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Prompt>();
	}

	private List<IFilter> convertFilter(List<FmdtFilter> fmdtFilters, BusinessPackage bPackage, String groupName, List<IResource> resourcesAdded) {
		try {
			if (fmdtFilters != null && !fmdtFilters.isEmpty()) {
				List<IFilter> filters = new ArrayList<IFilter>();

				for (FmdtFilter fmdtFilter : fmdtFilters) {
					IResource filter = bPackage.getResourceByName(fmdtFilter.getName());
					if (filter != null)
						filters.add((IFilter) filter);
					else {
						if (fmdtFilter.getType().equals(FmdtConstant.FILTERCOMPLEX)) {
							ComplexFilter complex = new ComplexFilter();
							complex.setName(fmdtFilter.getName());
							complex.setOperator(fmdtFilter.getOperator());

							IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtFilter.getColumn().getTableName());
							complex.setOrigin(btable.getColumn(groupName, fmdtFilter.getColumn().getName()));
							for (String value : fmdtFilter.getValues()) {
								complex.setValue(value);
							}

							if (fmdtFilter.isCreate())
								resourcesAdded.add(complex);
							else
								filters.add((IFilter) complex);

						}
						else if (fmdtFilter.getType().equals(FmdtConstant.FILTERSQL)) {

							try {
								SqlQueryFilter sql = new SqlQueryFilter();
								sql.setName(fmdtFilter.getName());

								IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtFilter.getColumn().getTableName());
								sql.setOrigin(btable.getColumn(groupName, fmdtFilter.getColumn().getName()));
								sql.setQuery(fmdtFilter.getQuery());

								if (fmdtFilter.isCreate())
									resourcesAdded.add(sql);
								else
									filters.add((IFilter) sql);
							} catch (Exception e) {
								System.out.println("table not found " + fmdtFilter.getColumn().getTableName());
								e.printStackTrace();
							}
						}
					}
				}
				return filters;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<IFilter>();
	}

	private List<Formula> convertformula(List<FmdtFormula> fmdtFormulas, List<IResource> resourcesAdded) {
		try {
			if (fmdtFormulas != null && !fmdtFormulas.isEmpty()) {
				List<Formula> formulas = new ArrayList<Formula>();

				for (FmdtFormula fmdtFormula : fmdtFormulas) {
					Formula formula = new Formula(fmdtFormula.getName(), fmdtFormula.getScript(), fmdtFormula.getDataStreamInvolved());
					formulas.add(formula);
				}
				return formulas;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Formula>();
	}

	private HashMap<Prompt, List<String>> convertPromptValue(List<FmdtFilter> fmdtPrompts, BusinessPackage bPackage, String groupName) {
		try {
			if (fmdtPrompts != null && !fmdtPrompts.isEmpty()) {

				HashMap<Prompt, List<String>> promptValues = new HashMap<Prompt, List<String>>();

				for (FmdtFilter fmdtPrompt : fmdtPrompts) {
					IResource prompt = bPackage.getResourceByName(fmdtPrompt.getName());
					if (prompt != null) {
						promptValues.put((Prompt) prompt, fmdtPrompt.getValues());
					}
					else {
						Prompt p = new Prompt();
						p.setName(fmdtPrompt.getName());

						IBusinessTable btable = bPackage.getBusinessTable(groupName, fmdtPrompt.getColumn().getTableName());
						p.setGotoDataStreamElement(btable.getColumn(groupName, fmdtPrompt.getColumn().getName()));
						p.setOrigin(btable.getColumn(groupName, fmdtPrompt.getColumn().getName()));
						p.setOperator(fmdtPrompt.getOperator());
						promptValues.put(p, fmdtPrompt.getValues());
					}
				}
				return promptValues;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<Prompt, List<String>>();
	}

	private String findTableName(IDataStreamElement column, IBusinessPackage bPackage, String groupName) {
		for (IBusinessTable table : bPackage.getBusinessTables(groupName)) {
			if (table.getColumns(groupName).contains(column))
				return table.getName();
		}
		return null;
	}

	private QuerySql getQuerySql(FmdtQueryBuilder builder, IBusinessPackage pack) throws ServiceException {

		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		List<IResource> resourcesAdded = new ArrayList<IResource>();

		List<IDataStreamElement> columns = convertColumns(builder.getColumns(), pack, groupName);
		List<AggregateFormula> aggregates = convertAggregate(builder.getAggregates(), pack, groupName);
		List<Prompt> prompts = convertPrompts(builder.getPromptFilters(), (BusinessPackage) pack, groupName, resourcesAdded);
		List<IFilter> filters = convertFilter(builder.getFilters(), (BusinessPackage) pack, groupName, resourcesAdded);
		List<Formula> formulas = convertformula(builder.getFormulas(), resourcesAdded);

		List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();

		List<Ordonable> ordonables = new ArrayList<Ordonable>();

		for (FmdtData data : builder.getListColumns()) {
			if (data instanceof FmdtColumn) {
				for (IDataStreamElement column : columns) {
					if (data.getName().equals(column.getName())) {
						ordonables.add((Ordonable) column);
						break;
					}
				}
			}
			if (data instanceof FmdtFormula) {
				for (Formula formula : formulas) {
					if (data.getName().equals(formula.getName()) && ((FmdtFormula) data).getScript().equals(formula.getFormula())) {
						ordonables.add((Ordonable) formula);
						break;
					}
				}
			}
			if (data instanceof FmdtAggregate) {
				for (AggregateFormula agg : aggregates) {
					if (data.getName().equals(agg.getOutputName()))
						if (((FmdtAggregate) data).getFunction() == null) {
							ordonables.add((Ordonable) agg.getCol());
							break;
						}
						else if (((FmdtAggregate) data).getFunction().equals(((ICalculatedElement) agg.getCol()).getFormula()) && ((FmdtAggregate) data).getFormulaData().equals(agg.getInvolvedDataStreamNames())) {
							ordonables.add(((Ordonable) new Formula(agg.getOutputName(), ((FmdtAggregate) data).getFunction(), agg.getInvolvedDataStreamNames())));
							break;
						}
				}
			}
		}

		QuerySql query = (QuerySql) SqlQueryBuilder.getQuery(groupName, columns, new HashMap<ListOfValue, String>(), aggregates, ordonables, filters, prompts, formulas, selectedStrategies);

		if (builder.isDistinct()) {
			query.setDistinct(true);
		}
		if (builder.isLimit()) {
			query.setLimit(builder.getNbLimit());
		}

		for (IResource r : resourcesAdded) {
			query.addDesignTimeResource(r);
		}
		return query;
	}

	private FmdtQueryBuilder convertQueryToBuilder(QuerySql query, IBusinessPackage pack, String groupName) {
		FmdtQueryBuilder builder = new FmdtQueryBuilder();

		List<FmdtData> listColumns = new ArrayList<FmdtData>();
		List<FmdtFilter> listFilters = new ArrayList<FmdtFilter>();

		for (Ordonable ord : query.getOrderBy()) {
			if (!checkAgg(ord, query.getAggs(), listColumns, pack, groupName)) {

				if (ord instanceof IDataStreamElement) {
					IDataStreamElement c = (IDataStreamElement) ord;
					FmdtColumn column = new FmdtColumn(c.getName(), c.getOuputName(), c.getDescription(), getBusinessTableName(pack, c.getName(), c.getOriginName(), groupName), c.getDataStream().getName());
					if (c instanceof ICalculatedElement)
						column.setFormula(((ICalculatedElement) c).getFormula());

					column.setOriginName(c.getOriginName());
					column.setType(getColumnType(c.getType()));
					column.setD4ctypes(c.getD4cTypes());
					try {
						column.setSqlType(((SQLColumn) c.getOrigin()).getSqlType());
						column.setJavaType(((SQLColumn) c.getOrigin()).getClassName());
					} catch (Exception e) {

					}
					column.setType(getColumnType(c.getType()));

					listColumns.add(column);
				}
				if (ord instanceof Formula) {
					Formula f = (Formula) ord;
					FmdtFormula formula = new FmdtFormula(f.getName(), "", f.getFormula(), f.getDataStreamInvolved());
					formula.setCreated(true);

					listColumns.add(formula);
				}
			}
		}
		for (IFilter f : query.getFilters()) {
			FmdtColumn col = dataStreamToFmdt(f.getOrigin(), pack, groupName);
			if (f instanceof ComplexFilter)
				if (!query.getDesignTimeResources().contains((ComplexFilter) f))
					listFilters.add(new FmdtFilter(f.getName(), f.getOutputName(), "", col, ((ComplexFilter) f).getOperator(), ((ComplexFilter) f).getValue(), FmdtConstant.FILTERCOMPLEX));
			if (f instanceof SqlQueryFilter)
				if (!query.getDesignTimeResources().contains((SqlQueryFilter) f))
					listFilters.add(new FmdtFilter(f.getName(), f.getOutputName(), "", ((SqlQueryFilter) f).getQuery(), col, FmdtConstant.FILTERSQL));
		}
		for (Prompt f : query.getPrompts()) {
			FmdtColumn col = dataStreamToFmdt(f.getOrigin(), pack, groupName);
			if (!query.getDesignTimeResources().contains((Prompt) f))
				listFilters.add(new FmdtFilter(f.getName(), f.getOutputName(), "", col, ((Prompt) f).getOperator(), new ArrayList<String>(), FmdtConstant.FILTERPROMPT));
		}

		for (IResource r : query.getDesignTimeResources()) {
			FmdtFilter filter = null;
			if (r instanceof ComplexFilter) {
				FmdtColumn col = dataStreamToFmdt(((ComplexFilter) r).getOrigin(), pack, groupName);
				filter = new FmdtFilter(r.getName(), r.getOutputName(), "", col, ((ComplexFilter) r).getOperator(), ((ComplexFilter) r).getValue(), FmdtConstant.FILTERCOMPLEX);
			}
			if (r instanceof SqlQueryFilter) {
				FmdtColumn col = dataStreamToFmdt(((SqlQueryFilter) r).getOrigin(), pack, groupName);
				filter = new FmdtFilter(r.getName(), r.getOutputName(), "", ((SqlQueryFilter) r).getQuery(), col, FmdtConstant.FILTERSQL);
			}
			if (r instanceof Prompt) {
				FmdtColumn col = dataStreamToFmdt(((Prompt) r).getOrigin(), pack, groupName);
				filter = new FmdtFilter(r.getName(), r.getOutputName(), "", col, ((Prompt) r).getOperator(), new ArrayList<String>(), FmdtConstant.FILTERPROMPT);
			}
			if (filter != null) {
				filter.setCreate(true);
				listFilters.add(filter);
			}
		}

		builder.setListColumns(listColumns);
		builder.setListFilters(listFilters);

		builder.setDistinct(query.getDistinct());
		if (query.getLimit() > 0) {
			builder.setLimit(true);
			builder.setNbLimit(query.getLimit());
		}
		return builder;
	}

	private String getBusinessTableName(IBusinessPackage pack, String col, String originName, String groupName) {
		for (IBusinessTable table : pack.getExplorableTables(groupName)) {
			for (IDataStreamElement column : table.getColumns(groupName)) {
				if (column.getName().equals(col) && (originName == null || originName.isEmpty() || column.getOriginName().equals(originName)))
					return table.getName();
			}
			for(IBusinessTable sub : table.getChilds(groupName)) {
				for (IDataStreamElement column : sub.getColumns(groupName)) {
					if (column.getName().equals(col) && (originName == null || originName.isEmpty() || column.getOriginName().equals(originName)))
						return sub.getName();
				}
			}
		}
		return null;
	}

	private FmdtColumn dataStreamToFmdt(IDataStreamElement col, IBusinessPackage pack, String groupName) {
		FmdtColumn column = new FmdtColumn(col.getName(), col.getOuputName(), col.getDescription(), getBusinessTableName(pack, col.getName(), col.getOriginName(), groupName), col.getDataStream().getOrigin().getName());
		if (col instanceof ICalculatedElement)
			column.setFormula(((ICalculatedElement) col).getFormula());
		column.setOriginName(col.getOriginName());

		return column;
	}

	private Boolean checkAgg(Ordonable ord, List<AggregateFormula> aggregates, List<FmdtData> listColumns, IBusinessPackage pack, String groupName) {
		if (ord instanceof AggregateFormula) {
			AggregateFormula aggre = (AggregateFormula) ord;
			if (aggre.isBasedOnFormula()) {
				FmdtAggregate aggregate = new FmdtAggregate();
				aggregate.setName(aggre.getCol().getName());
				aggregate.setOutputName(aggre.getOutputName());
				aggregate.setOperator(aggre.getFunction());
				aggregate.setFunction(((ICalculatedElement) aggre.getCol()).getFormula());
				aggregate.setFormulaData(aggre.getInvolvedDataStreamNames());
				aggregate.setBasedOnFormula(true);
				listColumns.add(aggregate);
				return true;
			}
			else {
				FmdtAggregate aggregate = new FmdtAggregate();
				aggregate.setCol(aggre.getCol().getName());
				aggregate.setName(aggre.getOutputName());
				aggregate.setOutputName(aggre.getOutputName());
				aggregate.setTable(getBusinessTableName(pack, aggre.getCol().getName(), aggre.getCol().getOriginName(), groupName));
				aggregate.setOperator(aggre.getFunction());
				aggregate.setBasedOnFormula(false);
				try {
					aggregate.setSqlType(((SQLColumn) aggre.getCol().getOrigin()).getSqlType());
					aggregate.setJavaType(((SQLColumn) aggre.getCol().getOrigin()).getClassName());
				} catch (Exception e) {
				}
				listColumns.add(aggregate);
				return true;
			}
		}
		else {
			for (AggregateFormula agg : aggregates) {
				if (ord instanceof Formula) {
					if (((Formula) ord).getFormula().equals(agg.getFunction())) {
						FmdtAggregate aggregate = new FmdtAggregate();
						aggregate.setName(agg.getCol().getName());
						aggregate.setOutputName(agg.getOutputName());
						aggregate.setOperator(agg.getFunction());
						aggregate.setFunction(((ICalculatedElement) agg.getCol()).getFormula());
						aggregate.setFormulaData(agg.getInvolvedDataStreamNames());
						aggregate.setBasedOnFormula(true);
						listColumns.add(aggregate);
						return true;
					}
				}
				else {
					if ((agg.getCol() != null && ((IDataStreamElement) ord).getName().equals(agg.getCol().getName())) || ((IDataStreamElement) ord).getName().equals(agg.getOutputName())) {
						FmdtAggregate aggregate = new FmdtAggregate();
						aggregate.setCol(agg.getCol().getName());
						aggregate.setName(agg.getOutputName());
						aggregate.setOutputName(agg.getOutputName());
						aggregate.setTable(getBusinessTableName(pack, agg.getCol().getName(), agg.getCol().getOriginName(), groupName));
						aggregate.setOperator(agg.getFunction());
						aggregate.setBasedOnFormula(false);
						try {
							aggregate.setSqlType(((SQLColumn) agg.getCol().getOrigin()).getSqlType());
							aggregate.setJavaType(((SQLColumn) agg.getCol().getOrigin()).getClassName());
						} catch (Exception e) {
						}
						listColumns.add(aggregate);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public FmdtTable getGraphicTable(FmdtData colGroup, List<FmdtData> colSeries, final List<String> serieNames, String operation, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder) throws ServiceException {
		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		List<FmdtColumn> cols = new ArrayList<FmdtColumn>();
		List<FmdtFormula> formulas = new ArrayList<FmdtFormula>();
		List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();
		List<FmdtData> ordonable = new ArrayList<FmdtData>();

		ordonable.add(colGroup);

		if (colGroup instanceof FmdtColumn)
			cols.add((FmdtColumn) colGroup);
		else if (colGroup instanceof FmdtFormula)
			formulas.add((FmdtFormula) colGroup);
		else if (colGroup instanceof FmdtAggregate)
			aggregates.add((FmdtAggregate) colGroup);

		for (FmdtData column : colSeries) {
			if (column instanceof FmdtColumn) {
				FmdtColumn col = (FmdtColumn) column;
				aggregates.add(new FmdtAggregate(col.getName(), col.getTableName(), col.getLabel(), col.getDescription(), operation));
			}
			else if (column instanceof FmdtFormula) {
				FmdtFormula col = (FmdtFormula) column;
				aggregates.add(new FmdtAggregate(col.getScript(), col.getLabel(), col.getDescription(), operation, col.getDataStreamInvolved()));
			}
			else if (column instanceof FmdtAggregate) {
				FmdtAggregate aggregate = (FmdtAggregate) column;
				List<FmdtAggregate> aggr = new ArrayList<FmdtAggregate>();
				aggr.add(aggregate);
				List<AggregateFormula> aggs = convertAggregate(aggr, pack, groupName);
				AggregateFormula agg = aggs.get(0);
				StringBuffer buf = new StringBuffer();

				if (agg.getCol() instanceof ICalculatedElement) {
					if (agg.getFunction().equals(AggregateFormula.COUNT_DISTINCT)) {
						buf.append(" COUNT(DISTINCT (" + ((ICalculatedElement) agg.getCol()).getFormula() + "))");
					}
					else {
						buf.append(agg.getFunction() + "(" + ((ICalculatedElement) agg.getCol()).getFormula() + ")");
					}
					aggregates.add(new FmdtAggregate(buf.toString(), aggregate.getOutputName(), aggregate.getDescription(), operation, aggregate.getFormulaData()));

				}
				else {
					if (agg.getFunction().trim().equals(AggregateFormula.COUNT_DISTINCT)) {
						buf.append("COUNT(DISTINCT(`" + agg.getCol().getDataStream().getName() + "`." + agg.getCol().getOrigin().getShortName() + "))");
					}
					else {
						buf.append(agg.getFunction() + "(`" + agg.getCol().getDataStream().getName() + "`." + agg.getCol().getOrigin().getShortName() + ")");
					}
					List<String> dataStreams = new ArrayList<String>();
					dataStreams.add(aggregate.getCol());

					aggregates.add(new FmdtAggregate(buf.toString(), aggregate.getOutputName(), aggregate.getDescription(), operation, dataStreams));
				}
			}
		}
		builder.setColumns(cols);
		builder.setFormulas(formulas);
		builder.setAggregates(aggregates);
		builder.setListColumns(ordonable);

		HashMap<Prompt, List<String>> promptMap = convertPromptValue(builder.getPromptFilters(), (BusinessPackage) pack, groupName);

		QuerySql query = getQuerySql(builder, pack);
		String conName = ((SQLDataSource) ((BusinessPackage) pack).getDataSources(groupName).get(0)).getConnections().get(0).getName();

		IRepositoryContext ctx = new BaseRepositoryContext(session.getVanillaContext(), session.getCurrentGroup(), session.getCurrentRepository());
		EffectiveQuery sqlQuery;
		List<FmdtRow> datas = new ArrayList<FmdtRow>();

		try {
			sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, query, groupName, false, promptMap);

			List<List<String>> values = ((BusinessPackage) pack).executeQuery(0, conName, sqlQuery.getGeneratedQuery());

			for (List<String> value : values) {
				FmdtRow d = new FmdtRow();
				for (String col : value) {
					d.getValues().add(col);
				}
				d.setOriginalValues(new ArrayList<String>(d.getValues()));
				datas.add(d);
			}

			FmdtTable clientTable = new FmdtTable();
			clientTable.setName(fmdtDriller.getName());
			clientTable.setDatas(datas);
			clientTable.setEditable(false);
			try {
				clientTable.setQuery(pack.getQuery(null, session.getVanillaContext(), query, new ArrayList<List<String>>()).replace("`", "\""));
				clientTable.setQueryXml(((QuerySql) query).getXml());
			} catch (Exception e) {
				e.printStackTrace();
				clientTable.setQuery(sqlQuery.getGeneratedQuery());
			}
			return clientTable;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new FmdtTable();
	}

	@Override
	public String createCube(List<FmdtDimension> dimensions, List<FmdtData> measures, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder, String title, String description, String local) throws ServiceException {
		try {
			CommonSession session = getSession();
			FAModel model = generateCube(dimensions, measures, fmdtDriller, builder, title, description, local);
			XStream stream = new XStream();
			String modelXml = stream.toXML(model);
			session.setCube(modelXml);
			VanillaSession s = session.getSystemManager().getSession(session.getVanillaSessionId());
			s.setCubeXml(modelXml);
			session.getSystemManager().updateSession(s, session.getVanillaSessionId());

			return modelXml;

		} catch (Exception e) {
			String msg = "Failed to generate cube : " + e.getMessage();
			e.printStackTrace();
			throw new ServiceException(msg);
		}

	}

	private FAModel generateCube(List<FmdtDimension> dimensions, List<FmdtData> measures, FmdtQueryDriller fmdtDriller, FmdtQueryBuilder builder, String title, String description, String local) throws ServiceException {
		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return null;
		}

		List<FmdtColumn> cols = new ArrayList<FmdtColumn>();
		List<FmdtFormula> formulas = new ArrayList<FmdtFormula>();
		List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();

		for (FmdtData column : measures) {
			if (column instanceof FmdtColumn) {
				FmdtColumn col = (FmdtColumn) column;
				aggregates.add(new FmdtAggregate(col.getName(), col.getTableName(), col.getLabel(), col.getDescription(), col.getMeasOp()));
			}
			else if (column instanceof FmdtFormula) {
				FmdtFormula col = (FmdtFormula) column;
				aggregates.add(new FmdtAggregate(col.getScript(), col.getLabel(), col.getDescription(), col.getMeasOp(), col.getDataStreamInvolved()));
			}
			else if (column instanceof FmdtAggregate) {
				FmdtAggregate aggregate = (FmdtAggregate) column;
				if (aggregate.isBasedOnFormula()) {
					aggregates.add(new FmdtAggregate(aggregate.getFunction(), aggregate.getOutputName(), aggregate.getDescription(), column.getMeasOp(), aggregate.getFormulaData()));

				}
				else {
					aggregates.add(new FmdtAggregate(aggregate.getCol(), aggregate.getTable(), aggregate.getOutputName(), aggregate.getDescription(), column.getMeasOp()));
				}
			}
		}

		for (FmdtData column : builder.getListColumns()) {
			if (column instanceof FmdtColumn) {
				cols.add((FmdtColumn) column);
			}
			else if (column instanceof FmdtFormula) {
				formulas.add((FmdtFormula) column);
			}
			else if (column instanceof FmdtAggregate) {
				FmdtAggregate aggregate = (FmdtAggregate) column;
				if (aggregate.isBasedOnFormula()) {
					formulas.add(new FmdtFormula(aggregate.getOutputName(), aggregate.getDescription(), aggregate.getFunction(), aggregate.getFormulaData()));

				}
				else {
					cols.add(new FmdtColumn(aggregate.getCol(), aggregate.getOutputName(), aggregate.getDescription(), aggregate.getTable(), aggregate.getTable()));
				}
			}
		}

		builder.setColumns(cols);
		builder.setFormulas(formulas);
		builder.setAggregates(aggregates);

		QuerySql query = getQuerySql(builder, pack);

		String olapQuery;

		try {
			olapQuery = query.getXml();

			String conName = ((SQLDataSource) ((BusinessPackage) pack).getDataSources(groupName).get(0)).getConnections().get(0).getName();

			Properties properties = new Properties();

			properties.put("URL", session.getVanillaRuntimeUrl());
			properties.put("USER", session.getUser().getLogin());
			properties.put("PASSWORD", session.getUser().getPassword());
			properties.put("VANILLA_URL", session.getVanillaContext().getVanillaUrl());
			properties.put("REPOSITORY_ID", session.getCurrentRepository().getId());
			properties.put("DIRECTORY_ITEM_ID", fmdtDriller.getMetadataId());
			properties.put("BUSINESS_MODEL", fmdtDriller.getModelName());
			properties.put("BUSINESS_PACKAGE", fmdtDriller.getPackageName());
			properties.put("GROUP_NAME", groupName);
			properties.put("CONNECTION_NAME", conName);
			properties.put("IS_ENCRYPTED", false);

			DatasourceOda source = new DatasourceOda();
			source.setName(title);
			source.setOdaDatasourceExtensionId("bpm.metadata.birt.oda.runtime");
			source.setPublicProperties(properties);

			DataObjectOda object = new DataObjectOda(source);
			object.setOdaDatasetExtensionId("bpm.metadata.birt.oda.runtime.dataSet");
			object.setQueryText(olapQuery);
			object.setName(title);
			object.setDataObjectType("fact");

			source.addDataObject(object);

			OLAPSchema shema = new OLAPSchema();
			OLAPCube cube = new OLAPCube();

			for (FmdtDimension dimension : dimensions) {
				OLAPDimension olapDimension = new OLAPDimension();
				olapDimension.setName(dimension.getName());
				olapDimension.setCaption(dimension.getName());
				OLAPHierarchy hierarchy = new OLAPHierarchy(dimension.getHierarchyName());
				hierarchy.setCaption(dimension.getHierarchyName());
				for (FmdtData level : dimension.getLevels()) {
					OLAPLevel lev = new OLAPLevel(level.getName());
					DataObjectItem item = new DataObjectItem(level.getName());
					item.setSqlType(level.getSqlType());
					item.setOrigin(level.getName());
					object.addDataObjectItem(item);
					item.setParent(object);
					lev.setItem(item);
					lev.setCaption(level.getLabel());
					lev.setParent(hierarchy);
					hierarchy.addLevel(lev);
				}
				olapDimension.addHierarchy(hierarchy);
				shema.addDimension(olapDimension);
				cube.addDim(olapDimension);
			}

			for (FmdtData measure : measures) {
				OLAPMeasure meas = new OLAPMeasure(measure.getName());
				DataObjectItem origin = new DataObjectItem(measure.getName());
				origin.setOrigin(measure.getName());
				origin.setSqlType(measure.getSqlType());
				object.addDataObjectItem(origin);
				meas.setOrigin(origin);
				meas.setAggregator(measure.getMeasOp());
				meas.setColumnName(measure.getName());
				meas.setCaption(measure.getLabel());
				shema.addMeasure(meas);
				cube.addMes(meas);
			}

			cube.setName(title);
			cube.setDescription(description);
			cube.setFactDataObjectId(source.getDataObjects().get(0).getId());

			shema.addCube(cube);

			FAModel model = new FAModel();
			model.addDataSource(source);
			model.setOLAPSchema(shema);
			model.setName(title);

			return model;
		} catch (Exception e) {
			String msg = "Failed to generate cube : " + e.getMessage();
			e.printStackTrace();
			throw new ServiceException(msg);
		}
	}

	@Override
	public int saveCube(int selectedDirectoryId, String name, String description, Group group, String model) throws ServiceException {
		CommonSession session = getSession();
		XStream stream = new XStream();
		FAModel faModel = (FAModel) stream.fromXML(model);
		try {
			RepositoryItem item = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FASD_TYPE, -1, session.getRepositoryConnection().getRepositoryService().getDirectory(selectedDirectoryId), name, description, "", "", faModel.getFAXML(), true);
			session.getRepositoryConnection().getAdminService().addGroupForItem(group.getId(), item.getId());
			return item.getId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	@Override
	public String generateCubeUrl(String local, HashMap<String, String> parameters, int dest) throws ServiceException {
		String destUrl;
		if (dest == FmdtConstant.DEST_FWR)
			destUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FWR);
		else
			destUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);
		String url = forwardSecurityUrl(destUrl, local, parameters);
		return url;
	}

	public String forwardSecurityUrl(String url, String paramLocal, HashMap<String, String> parameters) throws ServiceException {

		CommonSession session = getSession();

		try {
			String sessionId = session.getVanillaSessionId();

			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			if (session.getCurrentGroup() != null) /* modif kmo 30/09 */
				url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			if (session.getCurrentRepository() != null) /* modif kmo 30/09 */
				url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
			url += "&locale=" + paramLocal;
			for (String param : parameters.keySet()) {
				url += "&" + param + "=" + parameters.get(param);
			}

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			throw new ServiceException(msg);
		}
	}

	private void addRowAggregate(List<FmdtRow> datas, List<List<String>> values, int indexCol, int indexVal) {
		try {
			int lastrow = 0;
			for (List<String> value : values) {
				FmdtRow d = new FmdtRow();
				d.setTypeRow(FmdtRow.LABEL);
				d.setLevel(indexCol);
				for (String v : datas.get(0).getValues()) {
					d.getValues().add("");
				}
				d.getValues().set(indexCol, value.get(indexCol));
				int index = indexVal;
				for (int i = indexCol + 1; i < value.size(); i++) {
					d.getValues().set(index, value.get(i));
					index++;
				}
				d.setOriginalValues(new ArrayList<String>(d.getValues()));
				for (int i = lastrow; i < datas.size(); i++) {
					if (datas.get(i).getValues().get(indexCol).equals(d.getValues().get(indexCol))) {
						datas.add(i, d);
						lastrow = i;
						break;
					}
				}
			}

		} catch (Exception e) {

		}
	}

	@Override
	public Double[][] launchRFunctions(List<List<Double>> varX, List<List<Double>> varY, String function, int nbcluster) throws Exception {
		return getSession().getVanillaApi().getVanillaExternalAccessibilityManager().launchRFonctions(varX, varY, function, nbcluster);
	}

	@Override
	public Dataset getDatasetFromFmdtQuery(FmdtQueryBuilder builder, int metadataId, String modelName, String packageName) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, metadataId, modelName, packageName);
		if (pack == null) {
			return null;
		}

		QuerySql query = getQuerySql(builder, pack);
		return buildDatasetFromQuery(query);
	}

	@Override
	public Dataset buildDatasetFromSavedQuery(int metadataId, String modelName, String packageName, String queryName) throws ServiceException {
		CommonSession session = getSession();

		IBusinessPackage pack = findBusinessPackage(session, metadataId, modelName, packageName);
		if (pack == null) {
			return null;
		}

		List<SavedQuery> queries = pack.getSavedQueries();
		SavedQuery savedQuery = null;
		for (SavedQuery q : queries) {
			if (q.getName().equals(queryName)) {
				savedQuery = q;
				break;
			}
		}

		try {
			QuerySql query = savedQuery.loadQuery(session.getCurrentGroup().getName(), pack);
			return buildDatasetFromQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Dataset buildDatasetFromQuery(QuerySql query) {
		String queryXml = query.getXml();

		// create the dataset columns
		List<DataColumn> columns = new ArrayList<DataColumn>();

		for (IDataStreamElement elem : query.getSelect()) {
			DataColumn col = new DataColumn();
			col.setColumnLabel(elem.getOuputName());
			col.setColumnName(elem.getName());

			FunctionalType ft = getFunctionalType(elem.getType());

			col.setFt(ft);
			col.setTypes(elem.getD4cTypes());
			columns.add(col);
		}

		for (AggregateFormula elem : query.getAggs()) {
			DataColumn col = new DataColumn();
			col.setColumnLabel(elem.getOutputName());
			col.setColumnName(elem.getOutputName());
			// type ???
			columns.add(col);
		}

		for (Formula elem : query.getFormulas()) {
			DataColumn col = new DataColumn();
			col.setColumnLabel(elem.getOutputName());
			col.setColumnName(elem.getName());
			// type ???
			columns.add(col);
		}

		Dataset dataset = new Dataset();
		dataset.setMetacolumns(columns);
		dataset.setRequest(queryXml);

		return dataset;
	}

	private FunctionalType getFunctionalType(SubType type) {
		switch (type) {
		case ADRESSE:
			return FunctionalType.ADRESSE;
		case COMMUNE:
			return FunctionalType.COMMUNE;
		case COUNTRY:
			return FunctionalType.PAYS;
		case GEOLOCAL:
			return FunctionalType.GEOLOCAL;
		case LATITUDE:
			return FunctionalType.LATITUDE;
		case LONGITUDE:
			return FunctionalType.LONGITUDE;
		case ZONEID:
			return FunctionalType.ZONEID;
		case POSTALCODE:
			return FunctionalType.CODE_POSTAL;
		case YEAR:
			return FunctionalType.ANNEE;
		case MONTH:
			return FunctionalType.MOIS;
		case REGION:
			return FunctionalType.REGION;
		case DIMENSION:
			return FunctionalType.DIMENSION;
		case QUARTER:
			return FunctionalType.TRIMESTRE;
		case WEEK:
			return FunctionalType.SEMAINE;
		case AVG:
			return FunctionalType.AVG;
		case SUM:
			return FunctionalType.SUM;
		case MAX:
			return FunctionalType.MAX;
		case MIN:
			return FunctionalType.MIN;
		case COUNT:
			return FunctionalType.COUNT;
		default:
			break;
		}
		return null;
	}

	@Override
	public List<DatabaseTable> getDatabaseStructure(Datasource datasource, boolean managePostgres) throws ServiceException {
		try {
			return getSession().getVanillaApi().getVanillaPreferencesManager().getDatabaseStructure(datasource, managePostgres);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<DatabaseTable> getDatabaseStructure(D4C d4c, String organisation, Datasource datasource, boolean managePostgres) throws ServiceException {
		try {
			List<DatabaseTable> datastoreTables = getSession().getVanillaApi().getVanillaPreferencesManager().getDatabaseStructure(datasource, managePostgres);
			HashMap<String, DatabaseTable> tablesHash = new HashMap<String, DatabaseTable>();
			if (datastoreTables != null) {
				for (DatabaseTable table : datastoreTables) {
					tablesHash.put(table.getName().replace("public.", ""), table);
				}
			}
			
			List<DatabaseTable> tables = new ArrayList<DatabaseTable>();
			
			// We need to filter and customize table name according to D4C datasets
			D4CHelper d4cHelper = new D4CHelper(d4c.getUrl(), organisation, d4c.getLogin(), d4c.getPassword());
			List<CkanPackage> packages = d4cHelper.getCkanPackagesByChunk(organisation, 1000, 0);
			if (packages != null) {
				for (CkanPackage pack : packages) {
					
					if (pack.getResources() != null) {
						for (CkanResource resource : pack.getResources()) {
							if (resource.isDatastoreActive()) {
								DatabaseTable table = tablesHash.get(resource.getId());
								if (table != null && table.getColumns() != null && !table.getColumns().isEmpty()) {
									
									Iterator<DatabaseColumn> it = table.getColumns().iterator();
									while (it.hasNext()) {
										DatabaseColumn item = it.next();
									    if (item.getName().equals("_id") || item.getName().equals("_full_text")) {
									        it.remove();
									    }
									}
									
									table.setCustomName(pack.getName() + " " + resource.getName());
									tables.add(table);
								}
							}
						}
					}
				}
			}
			
			return tables;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<FmdtNode> launchDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names) throws Exception {
		File file = null;
		try {
			String filename = getSession().getVanillaApi().getVanillaExternalAccessibilityManager().launchRDecisionTree(varX, varY, train, names);
			file = new File("R-3.2.2\\bin\\" + filename);
			// file = new File("C:/Users/KMO/Documents/" + filename);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);

			Element root = doc.getRootElement();
			List<FmdtNode> nodes = convListNode(root, null);

			return nodes;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(file.getAbsolutePath());
			throw new ServiceException("file not found : " + file.getAbsolutePath());
		}
	}

	private List<FmdtNode> convListNode(Element element, String parent) {
		List<FmdtNode> nodes = new ArrayList<FmdtNode>();
		if (element.getName().equals("Node")) {
			FmdtNode node = new FmdtNode();
			node.setParentId(parent);
			node.setId(element.attributeValue("id"));
			node.setDefaultChildId(element.attributeValue("defaultChild"));
			List<FmdtNode> listChild = new ArrayList<FmdtNode>();
			for (Object obj : element.elements()) {
				Element child = (Element) obj;
				if (child.getName().equals("ScoreDistribution")) {
					node.addScore(child.attributeValue("value"), child.attributeValue("recordCount"));
				}
				if (child.getName().equals("CompoundPredicate")) {
					Element predicate = (Element) child.elements().get(0);
					node.setField(predicate.attributeValue("field"));
					node.setOperator(predicate.attributeValue("operator"));
					node.setValue(predicate.attributeValue("value"));
				}
				if (child.getName().equals("Node")) {
					listChild.addAll(convListNode(child, node.getId()));
				}
			}
			node.setChilds(listChild);
			nodes.add(node);
		}
		else {
			for (Object obj : element.elements()) {
				Element elem = (Element) obj;
				nodes.addAll(convListNode(elem, null));
			}
		}
		return nodes;
	}

	@Override
	public FmdtQueryDatas getMetadataData(FmdtQueryDriller driller, DatasourceFmdt datasourceFmdt) throws ServiceException {
		CommonSession session = getSession();

		//TODO: Very bad practice - Need to find another solution
//		try {
//			session.initSession(session.getVanillaApi().getVanillaSecurityManager().getGroupById(datasourceFmdt.getGroupId()), session.getVanillaApi().getVanillaRepositoryManager().getRepositoryById(datasourceFmdt.getRepositoryId()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return getTables(driller);
	}

	@Override
	public void updateData(FmdtTable currentTable, FmdtRow currentValue, FmdtDriller drill) throws ServiceException {

		List<FmdtRow> toUpdate = new ArrayList<FmdtRow>();
		toUpdate.add(currentValue);

		updateRows(drill, currentTable, toUpdate);
	}

	/**
	 * Methods used by WebDashboard and WebMetadata The classes used here should
	 * go, at the end, in a new project called bpm.metadata.core and used by
	 * metadata and FMDTDriller (change above)
	 * 
	 */
	@Override
	public List<Metadata> getMetadatas() throws ServiceException {
		CommonSession session = getSession();

		try {
			bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(session.getRepositoryConnection(), IRepositoryApi.FMDT_TYPE);

			List<Metadata> metadatas = new ArrayList<Metadata>();
			for (RepositoryDirectory rootDir : repository.getRootDirectories()) {
				getDirectoryContent(metadatas, repository, rootDir);
			}
			return metadatas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get availables metadatas.");
		}
	}

	private void getDirectoryContent(List<Metadata> metadatas, IRepository repository, RepositoryDirectory directory) throws Exception {
		List<RepositoryItem> items = repository.getItems(directory);

		for (RepositoryItem item : items) {
			Metadata metadata = new Metadata();
			metadata.setItemId(item.getId());
			metadata.setName(item.getItemName());
			metadata.setDescription(item.getComment());
			metadatas.add(metadata);
		}

		List<RepositoryDirectory> dirs = repository.getChildDirectories(directory);
		if (dirs != null) {
			for (RepositoryDirectory childDir : dirs) {
				getDirectoryContent(metadatas, repository, childDir);
			}
		}
	}

	@Override
	public Metadata openMetadata(RepositoryItem item) throws ServiceException {
		CommonSession session = getSession();

		try {
			String modelXml = "";
			try {
				modelXml = session.getRepositoryConnection().getRepositoryService().loadModel(item);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to load item with id " + item.getId() + " : " + e.getMessage());
			}

			MetaData metadataToConvert = null;
			try {
				MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(modelXml, "UTF-8"), new MetaDataBuilder(session.getRepositoryConnection()));
				metadataToConvert = dig.getModel(session.getRepositoryConnection(), session.getCurrentGroup().getName());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to build item with id " + item.getId() + " : " + e.getMessage());
			}

			Metadata metadata = MetadataHelper.convertMetadata(item, metadataToConvert, session.getCurrentGroup().getName());
			if (metadata.getD4cServerId() != null && metadata.getD4cServerId() > 0) {
				// We need to get the D4C server
				List<D4C> d4cServers = session.getVanillaApi().getExternalManager().getD4CDefinitions();
				if (d4cServers != null) {
					for (D4C d4cServer : d4cServers) {
						if (d4cServer.getId() == metadata.getD4cServerId()) {
							metadata.setD4cServer(d4cServer);
							break;
						}
					}
				}
			}
			return metadata;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to open the metadata with id " + item.getId() + " : " + e.getMessage());
		}
	}

	@Override
	public void exportToCkan(String resourceName, CkanPackage pack, FmdtRow columnNames, FmdtTable table, String separator, FmdtQueryBuilder builder) throws ServiceException {
		ByteArrayInputStream is = buildCsv(columnNames, table, ",");

		CkanHelper ckanHelper = new CkanHelper(null, null, null);
		try {
			CkanPackage p = ckanHelper.uploadCkanFile(resourceName, pack, is);

			Thread.sleep(10000);

			CkanPackage packag = ckanHelper.getCkanPackage(p.getName());
			CkanResource res = null;
			for (CkanResource r : packag.getResources()) {
				if (r.getFormat().equalsIgnoreCase("csv")) {
					res = r;
					break;
				}
			}

			String stringfields = ckanHelper.getResourceFields(res.getId());
			if (stringfields.startsWith("200")) {
				stringfields = stringfields.substring(4, stringfields.length());
			}
			JSONObject json = new JSONObject(stringfields);
			JSONArray fields = json.getJSONObject("result").getJSONArray("fields");
			JSONArray filtered = new JSONArray();
			for (int i = 0; i < fields.length(); i++) {
				JSONObject f = fields.getJSONObject(i);
				String colName = f.getString("id");
				if (colName.equals("_id")) {
					continue;
				}

				// XXX set the type

				for (FmdtColumn col : builder.getColumns()) {
					if (col.getLabel().equals(colName)) {
						List<String> types = new ArrayList<>();
						if (col.getD4ctypes() != null) {
							try {
								if (col.getD4ctypes().isDateDebut()) {
									types.add("<!--startDate-->");
								}
								if (col.getD4ctypes().isDateFin()) {
									types.add("<!--endDate-->");
								}
								if (col.getD4ctypes().isDateHeure()) {
									types.add("<!--timeserie_precision-->");
								}
								if (col.getD4ctypes().isDatePonctuelle()) {
									types.add("<!--date-->");
								}
								if (col.getD4ctypes().isFacette()) {
									types.add("<!--facet-->");
								}
								if (col.getD4ctypes().isFacetteMultiple()) {
									types.add("<!--disjunctive-->");
								}
								if (col.getD4ctypes().isFriseDate()) {
									types.add("<!--date_timeline-->");
								}
								if (col.getD4ctypes().isFriseDescription()) {
									types.add("<!--descr_for_timeLine-->");
								}
								if (col.getD4ctypes().isFriseLibelle()) {
									types.add("<!--title_for_timeLine-->");
								}
								if (col.getD4ctypes().isImages()) {
									types.add("<!--images-->");
								}
								if (col.getD4ctypes().isInfobulle()) {
									types.add("<!--tooltip-->");
								}
								if (col.getD4ctypes().isNuageDeMot()) {
									types.add("<!--wordcount-->");
								}
								if (col.getD4ctypes().isNuageDeMotNombre()) {
									types.add("<!--wordcountNumber-->");
								}
								if (col.getD4ctypes().isTableau()) {
									types.add("<!--table-->");
								}
								if (col.getD4ctypes().isTri()) {
									types.add("<!--sortable-->");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (f.isNull("info")) {
							f.put("info", new JSONObject());
							f.getJSONObject("info").put("notes", "");
						}
						String notes = f.getJSONObject("info").getString("notes");
						for (String type : types) {
							if (!notes.contains(type)) {
								if (!notes.isEmpty()) {
									notes += ",";
								}
								notes += type;
							}
						}
						f.getJSONObject("info").put("notes", notes);
						break;
					}
				}

				filtered.put(f);
			}

			json = new JSONObject();
			json.put("resource_id", res.getId());
			json.put("force", "true");
			json.put("fields", filtered);

			ckanHelper.updateResourceDatastore(json);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to export to CKan: " + e.getMessage());
		}
	}

	@Override
	public void createDataPreparation(String name, FmdtQueryDriller fmdtDriller) throws ServiceException {
		try {
			CommonSession session = getSession();

			Datasource datasource = new Datasource();
			datasource.setName(name);
			datasource.setType(DatasourceType.FMDT);
			datasource.setIdAuthor(getSession().getUser().getId());

			DatasourceFmdt fmdtDs = new DatasourceFmdt();
			fmdtDs.setBusinessModel(fmdtDriller.getModelName());
			fmdtDs.setBusinessPackage(fmdtDriller.getPackageName());
			fmdtDs.setGroupId(session.getCurrentGroup().getId());
			fmdtDs.setItemId(fmdtDriller.getMetadataId());
			fmdtDs.setPassword(session.getUser().getPassword());
			fmdtDs.setRepositoryId(session.getCurrentRepository().getId());
			fmdtDs.setUrl(session.getVanillaRuntimeUrl());
			fmdtDs.setUser(session.getUser().getLogin());

			datasource.setObject(fmdtDs);

			Dataset dataset = new Dataset();

			dataset = getDatasetFromFmdtQuery(fmdtDriller.getBuilder(), fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
			dataset.setName(name);
			dataset.setDatasource(datasource);
			dataset = session.getVanillaApi().getVanillaPreferencesManager().addDataset(dataset);

			session.createDataPreparation(name, dataset.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create DataPreparation : " + e.getMessage());
		}
	}

	@Override
	public int getRequestCount(FmdtQueryBuilder builder, FmdtQueryDriller fmdtDriller) throws ServiceException {
		CommonSession session = getSession();

		String groupName = session.getCurrentGroup().getName();

		IBusinessPackage pack = findBusinessPackage(session, fmdtDriller.getMetadataId(), fmdtDriller.getModelName(), fmdtDriller.getPackageName());
		if (pack == null) {
			return 0;
		}

		List<IResource> resources = new ArrayList<IResource>();

		List<IDataStreamElement> columns = convertColumns(builder.getColumns(), pack, groupName);
		List<AggregateFormula> aggregates = convertAggregate(builder.getAggregates(), pack, groupName);
		List<Prompt> prompts = convertPrompts(builder.getPromptFilters(), (BusinessPackage) pack, groupName, resources);
		List<IFilter> filters = convertFilter(builder.getFilters(), (BusinessPackage) pack, groupName, resources);
		List<Formula> formulas = convertformula(builder.getFormulas(), resources);

		List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();

		HashMap<Prompt, List<String>> promptMap = convertPromptValue(builder.getPromptFilters(), (BusinessPackage) pack, groupName);

		List<Ordonable> ordonables = new ArrayList<Ordonable>();

		for (FmdtData data : builder.getListColumns()) {
			if (data instanceof FmdtColumn) {
				for (IDataStreamElement column : columns) {
					if (data.getName().equals(column.getName())) {
						ordonables.add((Ordonable) column);
						break;
					}
				}
			}
			if (data instanceof FmdtFormula) {
				for (Formula formula : formulas) {
					if (data.getName().equals(formula.getName()) && ((FmdtFormula) data).getScript().equals(formula.getFormula())) {
						ordonables.add((Ordonable) formula);
						break;
					}
				}
			}
		}

		HashMap<String, List<String>> relatedFieldsTables = new HashMap<String, List<String>>();
		for (FmdtColumn column : builder.getColumns()) {
			IBusinessTable t = pack.getBusinessTable(groupName, column.getTableName());
			HashMap<IBusinessTable, List<Relation>> relations = pack.getRelationsForExplorablesTable(session.getCurrentGroup().getName(), t);

			for (IBusinessTable tab : relations.keySet()) {
				List<Relation> rel = relations.get(tab);
				for (Relation r : rel) {

					IDataStream leftTable = r.getLeftTable();
					for (IDataStreamElement ds : leftTable.getElements()) {
						if (column.getName().equals(ds.getName())) {
							for (Join j : r.getJoins()) {
								IDataStreamElement element = null;
								if (j.getLeftElement().getName().equals(column.getName()))
									element = j.getRightElement();
								else if (j.getRightElement().getName().equals(column.getName()))
									element = j.getLeftElement();

								if (element != null) {
									if (relatedFieldsTables.keySet().contains(column.getName())) {
										String tableName = findTableName(element, pack, groupName);
										if (tableName != null && !relatedFieldsTables.get(column.getName()).contains(tableName))
											relatedFieldsTables.get(column.getName()).add(tableName);
									}
									else {
										List<String> tables = new ArrayList<String>();
										String tableName = findTableName(element, pack, groupName);
										if (tableName != null)
											tables.add(tableName);
										relatedFieldsTables.put(column.getName(), tables);
									}
								}

							}
						}
					}
				}
			}
		}
		QuerySql query = (QuerySql) SqlQueryBuilder.getQuery(groupName, columns, new HashMap<ListOfValue, String>(), aggregates, ordonables, filters, prompts, formulas, selectedStrategies);

		if (builder.isDistinct()) {
			query.setDistinct(true);
		}
		if (builder.isLimit()) {
			query.setLimit(builder.getNbLimit());
		}
		for (IResource r : resources) {
			query.addDesignTimeResource(r);
		}

		try {

			IRepositoryContext ctx = new BaseRepositoryContext(session.getVanillaContext(), session.getCurrentGroup(), session.getCurrentRepository());
			EffectiveQuery sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, query, groupName, false, promptMap);
			String valueQuery = sqlQuery.getGeneratedQuery();
			return pack.countQuery(0, "Default", valueQuery);
		}
		catch(Exception e) {
			return 0;
		}
	}
	
	@Override
	public MetadataData getTableData(Datasource datasource, DatabaseTable selectedTable, DatabaseColumn selectedColumn, int limit, boolean distinct) throws ServiceException {
		getSession();

		try {
			DatasourceJdbc jdbcSource = (DatasourceJdbc) datasource.getObject();

			String query = buildQuery(selectedTable, selectedColumn, limit, distinct);

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = jdbcConnection.prepareQuery(query);
			ResultSet jdbcResult = rs.executeQuery(query);
			ResultSetMetaData jdbcMeta = jdbcResult.getMetaData();

			List<DatabaseColumn> columns = new ArrayList<>();
			for (int i = 1; i <= jdbcMeta.getColumnCount(); i++) {

				for (DatabaseColumn col : selectedTable.getColumns()) {
					if (col.getName().equals(jdbcMeta.getColumnName(i))) {
						columns.add(col);
						break;
					}
				}
			}

			MetadataData data = new MetadataData(columns);

			while (jdbcResult.next()) {
				Row row = new Row();
				for (DatabaseColumn col : columns) {

					String value = MetadataHelper.getValue(col.getName(), col.getType(), jdbcResult);
					row.addValue(value);
				}
				data.addRow(row);
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the data : " + e.getMessage());
		}
	}
	
	private String buildQuery(DatabaseTable selectedTable, DatabaseColumn column, int limit, boolean distinct) throws Exception {
		// We use this variable to set the get the SQL table name because the table name could have a custom define name
		// We get it from the column
		String tableName = null;
		
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT ");

		if (column != null) {
			if (distinct) {
				buf.append(" DISTINCT ");
			}
			buf.append(column.getName());
			
			tableName = column.getParentSQLOriginName();
		}
		else {
			boolean first = true;
			for (DatabaseColumn col : selectedTable.getColumns()) {
				if (first) {
					buf.append(col.getName());
					first = false;
				}
				else {
					buf.append(", " + col.getName());
				}
				
				// If there is multiple table, we throw an exception as we don't support it for now
				// TODO: Support multiple tables - We should use the metadata query generator
				if (tableName != null && col.getParentSQLOriginName() != null && !tableName.equals(col.getParentSQLOriginName())) {
					throw new Exception("We don't support the preview for multiple BusinessTable based on multiple SQL Tables");
				}
				
				tableName = col.getParentSQLOriginName();
			}
		}
		
		tableName = transformTableName(tableName != null ? tableName : selectedTable.getName());
		
		buf.append(" FROM " + tableName);
		if (column != null) {
			buf.append(" ORDER BY " + column.getName());
		}
		buf.append(" LIMIT " + limit);
		return buf.toString();
	}
	
	private String transformTableName(String tableName) {
		// Shitty trick for postgres if public schema exist
		if (tableName.contains("public") && !tableName.contains("\"")) {
			tableName = "public.\"" + tableName.replace("public.", "") + "\"";
		}
		return tableName;
	}
}
