package org.fasd.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class FMDTMapper {
	private List<DataSource> dataSources = new ArrayList<DataSource>();

	private List<OLAPRelation> listRelation = new ArrayList<OLAPRelation>();
	private MetaData source;
	private Set<String> groups;

	public FMDTMapper(MetaData source) throws Exception {
		this.source = source;
		if (source.getBusinessModels().isEmpty()) {
			throw new Exception(LanguageText.FMDTMapper_0);
		}
		groups = ((BusinessModel) source.getBusinessModels().iterator().next()).getGrants().keySet();
		build();
	}

	private void build() throws Exception {
		for (IBusinessModel model : source.getBusinessModels()) {

			for (IBusinessPackage p : model.getBusinessPackages("none")) { //$NON-NLS-1$

				HashMap<String, DataObject> tables = new HashMap<String, DataObject>();

				DataSource dataSource = new DataSource();
				dataSource.setDSName(p.getName());
				DataSourceConnection sock = null;

				for (IBusinessTable bt : p.getBusinessTables("none")) { //$NON-NLS-1$

					buildBt(dataSource, tables, sock, bt);

				}
				for (DataObject o : tables.values()) {
					dataSource.addDataObject(o);
				}
				dataSources.add(dataSource);

				for (Relation r : ((BusinessModel) model).getRelations()) {
					for (Join j : r.getJoins()) {

						OLAPRelation rel = new OLAPRelation();

						DataObject leftTable = dataSource.findDataObjectNamed(j.getLeftElement().getDataStream().getName());

						if (leftTable == null) {
							leftTable = createTable(j.getLeftElement().getDataStream());
							dataSource.addDataObject(leftTable);

						}
						rel.setLeftObjectItem(leftTable.findItemNamed(j.getLeftElement().getName()));

						DataObject rightTable = dataSource.findDataObjectNamed(j.getRightElement().getDataStream().getName());

						if (rightTable == null) {
							rightTable = createTable(j.getLeftElement().getDataStream());
							dataSource.addDataObject(rightTable);
						}
						rel.setRightObjectItem(rightTable.findItemNamed(j.getRightElement().getName()));

						if (rel.getLeftObjectItem() != null && rel.getRightObjectItem() != null) {
							boolean exists = false;

							for (DataObjectItem i : leftTable.getColumns()) {
								if (i.getOrigin().equals(j.getLeftElement())) {
									exists = true;
									break;
								}
							}
							if (!exists) {
								leftTable.addDataObjectItem(createCol(j.getLeftElement()));
							}

							exists = false;

							for (DataObjectItem i : rightTable.getColumns()) {
								if (i.getOrigin().equals(j.getRightElement())) {
									exists = true;
									break;
								}
							}
							if (!exists) {
								rightTable.addDataObjectItem(createCol(j.getRightElement()));
							}

							listRelation.add(rel);
						}

					}
				}
			}

		}
	}

	private DataObjectItem createCol(IDataStreamElement e) {
		DataObjectItem i = new DataObjectItem(e.getName());
		i.setClasse(e.getOrigin().getClassName());
		i.setDesc(e.getDescription());
		if (e.getOrigin().getName().contains(".")) { //$NON-NLS-1$
			i.setOrigin(e.getOrigin().getName().substring(e.getOrigin().getName().indexOf(".") + 1)); //$NON-NLS-1$
		} else {
			i.setOrigin(e.getOrigin().getName());
		}

		i.setSqlType(((SQLColumn) e.getOrigin()).getSqlType());
		return i;
	}

	private DataObject createTable(IDataStream dataStream) {
		DataObject obj = new DataObject(dataStream.getName());
		obj.setCreationStatement("select * from " + dataStream.getOrigin().getName()); //$NON-NLS-1$
		return obj;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public List<OLAPRelation> getRelations() {
		return listRelation;
	}

	private void buildBt(DataSource dataSource, HashMap<String, DataObject> tables, DataSourceConnection sock, IBusinessTable bt) throws Exception {
		for (IDataStreamElement e : bt.getColumns("none")) { //$NON-NLS-1$

			if (e instanceof ICalculatedElement) {

				ICalculatedElement calc = (ICalculatedElement) e;

				SQLTable t = (SQLTable) calc.getDataStream().getOrigin();

				if (tables.get(t.getName()) == null) {
					tables.put(t.getName(), new DataObject(t.getName()));
				}

				boolean exists = false;
				for (DataObjectItem i : tables.get(t.getName()).getColumns()) {
					if (i.getName().equals(calc.getName())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					DataObjectItem column = new DataObjectItem();
					column.setType("calculated"); //$NON-NLS-1$
					column.setName(calc.getName());
					column.setOrigin(calc.getFormula());
					column.setClasse("java.lang.Object"); //$NON-NLS-1$

					tables.get(t.getName()).addDataObjectItem(column);
				}

				continue;
			}

			SQLColumn c = (SQLColumn) e.getOrigin();
			SQLTable t = (SQLTable) c.getTable();

			if (sock == null) {
				sock = new DataSourceConnection();

				SQLConnection con = (SQLConnection) t.getConnection();

				for (DriverInfo d : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
					if (con.getDriverName().equals(d.getName())) {
						sock.setDriverFile(d.getFile());
						sock.setDriver(d.getClassName());

						if (con.isUseFullUrl()) {
							sock.setUrl(con.getFullUrl());
						} else {
							sock.setUrl(d.getUrlPrefix() + con.getHost() + ":" + con.getPortNumber() + "/" + con.getDataBaseName()); //$NON-NLS-1$ //$NON-NLS-2$
						}

						break;
					}

				}

				sock.setUser(con.getUsername());
				sock.setPass(con.getPassword());

				dataSource.setDriver(sock);
			}

			if (tables.get(t.getName()) == null) {
				tables.put(t.getName(), new DataObject(t.getName()));
			}

			boolean exists = false;
			for (DataObjectItem i : tables.get(t.getName()).getColumns()) {
				if (i.getName().equals(c.getName())) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				DataObjectItem i = new DataObjectItem(e.getName());
				i.setClasse(c.getClassName());
				i.setDesc(e.getDescription());
				if (c.getName().contains(".")) { //$NON-NLS-1$
					i.setOrigin(c.getName().substring(c.getName().indexOf(".") + 1)); //$NON-NLS-1$
				} else {
					i.setOrigin(c.getName());
				}

				i.setSqlType(c.getSqlType());

				tables.get(t.getName()).addDataObjectItem(i);
				tables.get(t.getName()).buildSelectStatement();
			}

		}

		for (IBusinessTable t : bt.getChilds("none")) { //$NON-NLS-1$
			buildBt(dataSource, tables, sock, t);
		}

	}
}
