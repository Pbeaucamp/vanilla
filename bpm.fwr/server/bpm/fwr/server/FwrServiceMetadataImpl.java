package bpm.fwr.server;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.Column.SubType;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.DynamicPrompt;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.server.security.FwrSession;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrBusinessPackage;
import bpm.fwr.shared.models.metadata.FwrBusinessTable;
import bpm.fwr.shared.models.metadata.FwrSavedQuery;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.InfoUser;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.Formula;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

public class FwrServiceMetadataImpl extends RemoteServiceServlet implements FwrServiceMetadata {

	private static final long serialVersionUID = 532304739491498298L;

	private FwrSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FwrSession.class);
	}

	@Override
	public List<String> getValues(String name, String tName) throws ServiceException {
		FwrSession session = getSession();

		List<String> res = new ArrayList<String>();
		IBusinessTable t = null;
		for (IBusinessPackage pack : session.getBusinessPackage()) {
			t = pack.getBusinessTable(session.getCurrentGroup().getName(), tName);
			if (t != null) {
				break;
			}
		}
		try {
			IDataStreamElement d = t.getColumn(session.getCurrentGroup().getName(), name);
			getThreadLocalRequest().getSession().setAttribute("tname", tName);
			getThreadLocalRequest().getSession().setAttribute("cname", name);

			try {
				res = d.getDistinctValues();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (GrantException e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public void createFilter(String name, List<String> values, String operator) throws ServiceException {
		FwrSession session = getSession();

		String tname = (String) getThreadLocalRequest().getSession().getAttribute("tname");
		String cname = (String) getThreadLocalRequest().getSession().getAttribute("cname");

		FWRFilter f = new FWRFilter();
		f.setName(name);
		f.setTableName(tname);
		f.setColumnName(cname);
		f.setValues(values);
		f.setOperator(operator);

		session.add(f);
	}

	public void fillChilds(FwrBusinessTable currTable, List<IBusinessTable> childs, String group, Collection<Locale> locales) {

		for (IBusinessTable btable : childs) {
			Collection<IDataStreamElement> columns = btable.getColumns(group);
			List<Column> col = new ArrayList<Column>();

			for (IDataStreamElement column : columns) {
				Column c = null;

				try {
					if (column.isVisibleFor(group)) {

						try {
							c = new Column(column.getName(), column.getOriginName(), btable.getName(), column.getJavaClassName(), column.getDataStream().getName());
						} catch (Exception e) {
							c = new Column(column.getName(), column.getOriginName(), btable.getName(), "", column.getDataStream().getName());
						}

						c.setDescription(column.getDescription());

						for (Locale locale : locales) {

							if (column.getOuputName(locale) != null && !column.getOuputName(locale).equalsIgnoreCase("")) {
								c.addLocaleTitle(locale.getLanguage(), column.getOuputName(locale));
							} else {
								c.addLocaleTitle(locale.getLanguage(), column.getName());
							}
						}
						c.setType(getType(column.getType()));
//						c.setMeasureBehavior(column.getDefaultMeasureBehavior());
					} else {
						c = new Column(column.getName(), column.getOriginName(), btable.getName(), "java.lang.String", column.getDataStream().getName());
						for (Locale locale : locales) {

							if (column.getOuputName(locale) != null && !column.getOuputName(locale).equalsIgnoreCase("")) {
								c.addLocaleTitle(locale.getLanguage(), column.getOuputName(locale));
							} else {
								c.addLocaleTitle(locale.getLanguage(), column.getName());
							}
						}
						c.setType(getType(column.getType()));
//						c.setMeasureBehavior(column.getDefaultMeasureBehavior());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				col.add(c);
			}

			FwrBusinessTable currT = new FwrBusinessTable(btable.getName(), btable.getDescription(), col);
			for (Locale locale : locales) {

				if (((AbstractBusinessTable) (btable)).getOuputName(locale) != null && ((AbstractBusinessTable) (btable)).getOuputName(locale) != "") {
					currT.getTitles().put(locale.getLanguage(), ((AbstractBusinessTable) (btable)).getOuputName(locale));
				} else {
					currT.getTitles().put(locale.getLanguage(), currT.getName());
				}
			}

			List<IBusinessTable> other_childs = btable.getChilds(group);
			if (!childs.isEmpty()) {
				fillChilds(currT, other_childs, group, locales);
			}

			currTable.addChild(currT);
		}

	}

	@Override
	public List<String> getPromptList(FwrPrompt prompt, int metadataId, String pckgName, String modelName) throws ServiceException {
		FwrSession session = getSession();
		if (prompt instanceof DynamicPrompt) {
			try {
				DynamicPrompt dyn = (DynamicPrompt) prompt;

				DataSource datasource = new DataSource();
				datasource.setItemId(metadataId);
				datasource.setRepositoryId(session.getCurrentRepository().getId());
				datasource.setGroup(session.getCurrentGroup().getName());
				datasource.setBusinessPackage(pckgName);
				datasource.setBusinessModel(modelName);
				IBusinessPackage bpackage = getSelectedPackage(session.getRepositoryConnection(), datasource);

				IDataStreamElement selectedColumn = getSelectedColumns(bpackage, dyn.getColumn().getBusinessTableParent(), dyn.getColumn().getName(), session.getCurrentGroup().getName());
				if (selectedColumn != null) {
					return selectedColumn.getOrigin().getValues();
				}

				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				DataSource datasource = new DataSource();
				datasource.setItemId(metadataId);
				datasource.setRepositoryId(session.getCurrentRepository().getId());
				datasource.setGroup(session.getCurrentGroup().getName());
				datasource.setBusinessPackage(pckgName);
				datasource.setBusinessModel(modelName);
				IBusinessPackage pckg = getSelectedPackage(session.getRepositoryConnection(), datasource);

				Prompt p = (Prompt) pckg.getResourceByName(session.getCurrentGroup().getName(), prompt.getName());

				return p.getOrigin().getDistinctValues();

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public List<String> getCascadingPromptList(List<FwrPrompt> prompts, FwrPrompt prompt) throws ServiceException {
		FwrSession session = getSession();

		try {
			HashMap<String, String> parentValues = new HashMap<String, String>();
			getParentValues(parentValues, prompts, prompt, true);

			DataSource datasource = new DataSource();
			datasource.setItemId(prompt.getMetadataId());
			datasource.setRepositoryId(session.getCurrentRepository().getId());
			datasource.setGroup(session.getCurrentGroup().getName());
			datasource.setBusinessPackage(prompt.getPackageParent());
			datasource.setBusinessModel(prompt.getModelParent());
			IBusinessPackage pckg = getSelectedPackage(session.getRepositoryConnection(), datasource);

			Prompt p = (Prompt) pckg.getResourceByName(session.getCurrentGroup().getName(), prompt.getName());

			return p.getOrigin().getDistinctValues(parentValues);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void getParentValues(HashMap<String, String> parentValues, List<FwrPrompt> prompts, FwrPrompt prompt, boolean lastChild) {
		if (prompt.isChildPrompt()) {
			for (FwrPrompt prt : prompts) {
				if (prt.getName().equals(prompt.getParentPromptName())) {
					getParentValues(parentValues, prompts, prt, false);
					break;
				}
			}

			if (!lastChild) {
				if (prompt.getSelectedValues() != null && !prompt.getSelectedValues().isEmpty()) {
					parentValues.put(prompt.getOriginDataStreamName(), prompt.getSelectedValues().get(0));
				} else {
					parentValues.put(prompt.getOriginDataStreamName(), "");
				}
			}
		} else {
			if (prompt.getSelectedValues() != null && !prompt.getSelectedValues().isEmpty()) {
				parentValues.put(prompt.getOriginDataStreamName(), prompt.getSelectedValues().get(0));
			} else {
				parentValues.put(prompt.getOriginDataStreamName(), "");
			}
		}
	}

	public IBusinessPackage getSelectedPackage(IRepositoryApi sock, DataSource datasource) throws Exception {
		IRepository irep = new Repository(sock, IRepositoryApi.FMDT_TYPE);

		RepositoryItem item = irep.getItem(datasource.getItemId());
		String result = sock.getRepositoryService().loadModel(item);
		InputStream input = IOUtils.toInputStream(result, "UTF-8");

		Collection<IBusinessModel> bmodels = MetaDataReader.read(datasource.getGroup(), input, sock, false);
		IBusinessModel model = null;

		Iterator<IBusinessModel> itbm = bmodels.iterator();
		while (itbm.hasNext()) {
			IBusinessModel bmodel = (IBusinessModel) itbm.next();
			if (bmodel.getName().equals(datasource.getBusinessModel())) {
				model = bmodel;
			}
		}

		return model.getBusinessPackage(datasource.getBusinessPackage(), datasource.getGroup());
	}

	public Locale getLocaleForLanguage(String language) {
		Locale res = null;

		Locale[] l = Locale.getAvailableLocales();
		for (int i = 0; i < l.length; i++) {
			if (l[i].getLanguage().equalsIgnoreCase(language)) {
				res = l[i];
			}
		}

		return res;
	}

	@Override
	public void createCustomColor(String name, int r, int g, int b) throws ServiceException {
		FwrSession session = getSession();
		session.addColor(name, new java.awt.Color(r, g, b));
	}

	@Override
	public HashMap<String, Integer> getCustomColor(String colorName) throws ServiceException {
		FwrSession session = getSession();

		Color c = session.getColors().get(colorName);
		HashMap<String, Integer> colorDatas = new HashMap<String, Integer>();
		colorDatas.put("r", c.getRed());
		colorDatas.put("g", c.getGreen());
		colorDatas.put("b", c.getBlue());
		return colorDatas;
	}

	@Override
	public FWRReport loadReport(int itemId, String group) throws ServiceException {
		FwrSession session = getSession();

		RepositoryItem item = null;
		try {
			item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String itemXML = null;
		try {
			itemXML = session.getRepositoryConnection().getRepositoryService().loadModel(item);
		} catch (Exception e) {
			e.printStackTrace();
		}

		XStream xstream = new XStream();
		FWRReport report = (FWRReport) xstream.fromXML(itemXML);

		session.setServices(this);

		SaveOptions options = new SaveOptions();
		options.setName(item.getItemName());
		report.setSaveOptions(options);

		return report;
	}

	@Override
	public HashMap<String, String> getBirtTemplates() throws ServiceException {
		FwrSession session = getSession();

		HashMap<String, String> templates = new HashMap<String, String>();
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();

		try {
			IRepository rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.EXTERNAL_DOCUMENT, -1, IRepositoryApi.FORMAT_RPTTEMPLATE);

			for (RepositoryDirectory dir : rep.getRootDirectories()) {
				List<RepositoryItem> it = createTemplatesList(dir, rep);
				items.addAll(it);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return templates;
	}

	private List<RepositoryItem> createTemplatesList(RepositoryDirectory dir, IRepository rep) {
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();

		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryDirectory d : dirs) {
			List<RepositoryItem> it = createTemplatesList(d, rep);
			items.addAll(it);
		}
		try {
			items.addAll(rep.getItems(dir));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	public List<FwrBusinessModel> getMetadataContent(int metadataId, String group, boolean load) throws ServiceException {
		try {
			FwrSession session = getSession();

			boolean isOnOlap = false;
			if (session.getRepository() == null) {
				try {
					session.initRepository(IRepositoryApi.FMDT_TYPE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			List<FwrBusinessModel> res = new ArrayList<FwrBusinessModel>();
			try {
				if (load) {
					session.initRepository(IRepositoryApi.FMDT_TYPE);
				}
				IRepository irep = session.getRepository();

				RepositoryItem item = irep.getItem(metadataId);

				String result = session.getRepositoryConnection().getRepositoryService().loadModel(item);

				if (result.contains("<olapDataSource>")) {
					isOnOlap = true;
				}

				InputStream input = IOUtils.toInputStream(result, "UTF-8");

				// get models
				Collection<IBusinessModel> bmodels = MetaDataReader.read(session.getCurrentGroup().getName(), input, session.getRepositoryConnection(), false);
				session.setBusinessModel(bmodels);

				for (IBusinessModel bmodel : bmodels) {
					// get packages
					List<FwrBusinessPackage> pckg = new ArrayList<FwrBusinessPackage>();
					Collection<IBusinessPackage> bpackages = bmodel.getBusinessPackages(session.getCurrentGroup().getName());
					session.setBusinessPackage(bpackages);

					for (IBusinessPackage bpackage : bpackages) {

						if (bpackage.isOnOlapDataSource()) {
							isOnOlap = true;
						}

						FwrBusinessPackage currBP = new FwrBusinessPackage(bpackage.getName(), bpackage.getDescription());

						// for each language
						for (Locale locale : bmodel.getLocales()) {
							if ((bpackage).getOuputName(locale) != null && (bpackage).getOuputName(locale) != "") {
								currBP.getTitles().put(locale.getLanguage(), (bpackage).getOuputName(locale));
							} else {
								currBP.getTitles().put(locale.getLanguage(), currBP.getName());
							}
						}

						// Get prompt and filters
						List<FWRFilter> filters = new ArrayList<FWRFilter>();
						List<FwrPrompt> prompts = new ArrayList<FwrPrompt>();

						Collection<IResource> c = bpackage.getResources(session.getCurrentGroup().getName());

						// get tables
						List<FwrBusinessTable> fwrTables = new ArrayList<FwrBusinessTable>();
						Collection<IBusinessTable> tables = bpackage.getBusinessTables(group);

						for (IBusinessTable btable : tables) {
							FwrBusinessTable table = new FwrBusinessTable();
							table.setName(btable.getName());
							table.setDescription(btable.getDescription());

							for (Locale locale : bmodel.getLocales()) {
								if (((AbstractBusinessTable) btable).getOuputName(locale) != null && ((AbstractBusinessTable) btable).getOuputName(locale) != "") {
									table.getTitles().put(locale.getLanguage(), ((AbstractBusinessTable) btable).getOuputName(locale));
								} else {
									table.getTitles().put(locale.getLanguage(), table.getName());
								}
							}

							// get columns
							table.setColumns(getColumns(btable, group, bmodel.getLocales(), bpackage));

							List<IBusinessTable> childs = btable.getChilds(session.getCurrentGroup().getName());
							if (!childs.isEmpty()) {
								fillChilds(table, childs, session.getCurrentGroup().getName(), bmodel.getLocales());
							}
							fwrTables.add(table);
						}

						for (IResource ressource : c) {
							if (ressource instanceof IFilter) {
								IFilter f = (IFilter) ressource;
								FWRFilter filter = new FWRFilter(f.getName());
								filter.setMetadataId(metadataId);
								filter.setModelParent(bmodel.getName());
								filter.setPackageParent(bpackage.getName());
								filters.add(filter);
							} else if (ressource instanceof Prompt) {
								Prompt p = (Prompt) ressource;
								FwrPrompt fp = buildPrompt(session, metadataId, item, group, bmodel, bpackage, fwrTables, p, isOnOlap);
								prompts.add(fp);
							}
						}
						currBP.setFilters(filters);
						currBP.setPrompts(prompts);

						// Load Saved Query
						List<SavedQuery> savedQueries = bpackage.getSavedQueries();
						List<FwrSavedQuery> fwrSavedQueries = loadSavedQuery(session, item, bmodel, bpackage, fwrTables, group, savedQueries, isOnOlap);
						currBP.setSavedQueries(fwrSavedQueries);

						currBP.setBusinessTables(fwrTables);
						pckg.add(currBP);
					}

					FwrBusinessModel currBM = new FwrBusinessModel(bmodel.getName(), ((BusinessModel) bmodel).getDescription(), pckg);
					for (Locale locale : bmodel.getLocales()) {

						if (((BusinessModel) bmodel).getOuputName(locale) != null && ((BusinessModel) bmodel).getOuputName(locale) != "") {
							currBM.getTitles().put(locale.getLanguage(), ((BusinessModel) bmodel).getOuputName(locale));
						} else {
							currBM.getTitles().put(locale.getLanguage(), currBM.getName());
						}

						currBM.getLocales().put(locale.getLanguage(), locale.getDisplayName());
					}
					currBM.setOnOlap(isOnOlap);
					currBM.setRelations(getRelations(metadataId, bmodel, bmodel.getRelationStrategies()));
					res.add(currBM);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<FwrSavedQuery> loadSavedQuery(FwrSession session, RepositoryItem item, IBusinessModel bmodel, IBusinessPackage bpackage, List<FwrBusinessTable> fwrTables, String groupName, List<SavedQuery> savedQueries, boolean isOnOlap) throws Exception {
		List<FwrSavedQuery> fwrQueries = new ArrayList<FwrSavedQuery>();
		if (savedQueries != null) {
			for (SavedQuery savedQuery : savedQueries) {
				FwrSavedQuery fwrSavedQuery = new FwrSavedQuery(savedQuery.getName(), savedQuery.getDescription());

				QuerySql query = savedQuery.loadQuery(groupName, bpackage);

				List<IFilter> filters = query.getFilters() != null ? query.getFilters() : new ArrayList<IFilter>();
				for (IFilter f : filters) {
					FWRFilter filter = buildFilter(session, item.getId(), groupName, bmodel, bpackage, f);
					fwrSavedQuery.addFilter(filter);
				}

				List<Prompt> prompts = query.getPrompts() != null ? query.getPrompts() : new ArrayList<Prompt>();
				for (Prompt p : prompts) {
					FwrPrompt fp = buildPrompt(session, item.getId(), item, groupName, bmodel, bpackage, fwrTables, p, isOnOlap);
					fwrSavedQuery.addPrompt(fp);
				}
				List<IDataStreamElement> columns = query.getSelect() != null ? query.getSelect() : new ArrayList<IDataStreamElement>();
				for (IDataStreamElement column : columns) {
					IBusinessTable selectedTable = findTableForColumn(bpackage, column, groupName);
					if (selectedTable != null) {
						fwrSavedQuery.addColumn(buildColumn(column, selectedTable.getName(), groupName, bmodel.getLocales()));
					}
				}
				List<Formula> forms = query.getFormulas();
				for (Formula f : forms) {
					Column c = new Column();
					c.setCalculated(true);
					c.setName(f.getName());
					c.getTitles().put("fr", c.getName());
					c.getTitles().put("en", c.getName());
					c.getTitles().put("", c.getName());
					c.getTitles().put(null, c.getName());
					c.getTitles().put("Default", c.getName());
					c.setFormula(f.getFormula());
					c.setJavaClass("java.lang.String");
					c.setInvolvedDatastreams(f.getDataStreamInvolved());
					fwrSavedQuery.addColumn(c);
				}

				List<AggregateFormula> agg = query.getAggs();
				for (AggregateFormula f : agg) {
					Column c = new Column();
					c.setCalculated(true);
					c.setName(f.getFunction() + "(" + f.getCol().getName() + ")");
					c.getTitles().put("fr", c.getName());
					c.getTitles().put("en", c.getName());
					c.getTitles().put("", c.getName());
					c.getTitles().put(null, c.getName());
					c.getTitles().put("Default", c.getName());
					if (f.isBasedOnFormula())
						c.setFormula(f.getFunction() + "(" + ((ICalculatedElement) f.getCol()).getFormula() + ")");
					else
						c.setFormula(f.getFunction() + "(" + f.getCol().getOrigin().getName() + ")");
					c.setJavaClass("java.lang.String");
					c.setAgregate(true);
					c.setType(getType(f.getCol().getType()));

					List<String> dsi = new ArrayList<String>();
					if (f.isBasedOnFormula())
						dsi.addAll(f.getInvolvedDataStreamNames());
					else
						dsi.add(f.getCol().getDataStream().getName());

					c.setInvolvedDatastreams(dsi);
					fwrSavedQuery.addColumn(c);
				}

				fwrQueries.add(fwrSavedQuery);
			}
		}
		return fwrQueries;
	}

	private IBusinessTable findTableForColumn(IBusinessPackage bpackage, IDataStreamElement column, String group) {
		Collection<IBusinessTable> tables = bpackage.getBusinessTables(group);
		for (IBusinessTable btable : tables) {
			Collection<IDataStreamElement> columns = btable.getColumns(group);
			for (IDataStreamElement element : columns) {
				if (element.getName().equals(column.getName())) {
					return btable;
				}
			}
		}
		return null;
	}

	private List<FwrRelationStrategy> getRelations(int metadataId, IBusinessModel bmodel, List<RelationStrategy> relationStrategies) {
		List<FwrRelationStrategy> strategies = new ArrayList<FwrRelationStrategy>();
		if (relationStrategies != null) {
			for (RelationStrategy strat : relationStrategies) {
				FwrRelationStrategy newRelation = new FwrRelationStrategy();
				newRelation.setName(strat.getName());
				newRelation.setMetadataId(metadataId);
				newRelation.setModelParent(bmodel.getName());
				newRelation.setRelationKeys(strat.getRelationKeys());
				newRelation.setTableNames(strat.getTableNames());
				strategies.add(newRelation);
			}
		}
		return strategies;
	}

	private FwrPrompt buildPrompt(FwrSession session, int metadataId, RepositoryItem item, String group, IBusinessModel bmodel, IBusinessPackage bpackage, List<FwrBusinessTable> fwrTables, Prompt p, boolean isOnOlap) {

		DataSource datasource = new DataSource();
		datasource.setConnectionName("Default");
		datasource.setBusinessModel(bmodel.getName());
		datasource.setBusinessPackage(bpackage.getName());
		datasource.setGroup(group);
		datasource.setItemId(item.getId());
		datasource.setRepositoryId(session.getCurrentRepository().getId());
		datasource.setName("DatasourceParam_" + new Object().hashCode());
		datasource.setOnOlap(isOnOlap);
		datasource.setPassword(session.getUser().getPassword());
		datasource.setUrl(session.getCurrentRepository().getUrl());
		datasource.setUser(session.getUser().getLogin());

		List<Column> columns = new ArrayList<Column>();
		for (FwrBusinessTable table : fwrTables) {
			for (Column col : table.getColumns()) {
				if (col.getName().equals(p.getOriginDataStreamElementName())) {
					columns.add(col);
					break;
				}
			}
		}

		DataSet ds = new DataSet();
		// Parcours des tables et chope la colonne qui
		// va bien
		ds.setColumns(columns);
		ds.setDatasource(datasource);
		ds.setFilters(new ArrayList<String>());
		ds.setLanguage("fr");
		ds.setName("ParameterDS_" + new Object().hashCode());
		ds.setParent(null);
		ds.setPrompts(new ArrayList<FwrPrompt>());

		Prompt pr = (Prompt) bpackage.getResourceByName(session.getCurrentGroup().getName(), p.getName());
		if (pr != null) {
			FwrPrompt fp = new FwrPrompt();
			fp.setChildPrompt(p.isChildPrompt());
			fp.setParentPromptName(p.getParentPromptName());
			fp.setName(p.getName());
			fp.setOriginDataStreamName(p.getOrigin().getOriginName());
			fp.setQuestion(p.getQuestion());
			fp.setOperator(p.getOperator());
			try {
				fp.setValues(p.getOrigin().getDistinctValues());
			} catch (Exception e) {
				e.printStackTrace();
			}
			fp.setMetadataId(metadataId);
			fp.setModelParent(bmodel.getName());
			fp.setPackageParent(bpackage.getName());
			fp.setDataset(ds);

			return fp;
		} else {
			DynamicPrompt fp = new DynamicPrompt();
			fp.setChildPrompt(p.isChildPrompt());
			fp.setParentPromptName(p.getParentPromptName());
			fp.setName(p.getName());
			fp.setOriginDataStreamName(p.getOrigin().getOriginName());
			fp.setQuestion(p.getQuestion());
			fp.setOperator(p.getOperator());
			try {
				fp.setValues(p.getOrigin().getDistinctValues());
			} catch (Exception e) {
				e.printStackTrace();
			}
			fp.setMetadataId(metadataId);
			fp.setModelParent(bmodel.getName());
			fp.setPackageParent(bpackage.getName());
			fp.setDataset(ds);

			IBusinessTable selectedTable = findTableForColumn(bpackage, p.getOrigin(), group);
			if (selectedTable != null) {
				fp.setColumn(buildColumn(p.getOrigin(), selectedTable.getName(), group, bmodel.getLocales()));
			}
			return fp;
		}
	}

	private FWRFilter buildFilter(FwrSession session, int metadataId, String group, IBusinessModel bmodel, IBusinessPackage bpackage, IFilter f) {
		FWRFilter filter = new FWRFilter(f.getName());
		filter.setMetadataId(metadataId);
		filter.setModelParent(bmodel.getName());
		filter.setPackageParent(bpackage.getName());
		if (f instanceof ComplexFilter) {
			filter.setColumnName(((ComplexFilter) f).getOrigin().getName());
			filter.setOperator(((ComplexFilter) f).getOperator());
			IBusinessTable selectedTable = findTableForColumn(bpackage, f.getOrigin(), group);
			filter.setTableName(selectedTable.getName());
		} else if (f instanceof SqlQueryFilter) {
			filter.setOperator(((SqlQueryFilter) f).getQuery());

		}
		return filter;
	}

	private List<Column> getColumns(IBusinessTable btable, String group, Collection<Locale> locales, IBusinessPackage bpackage) throws ServiceException {

		Collection<IDataStreamElement> columns = btable.getColumns(group);

		Iterator<IDataStreamElement> itc = columns.iterator();
		List<Column> col = new ArrayList<Column>();

		while (itc.hasNext()) {
			IDataStreamElement column = (IDataStreamElement) itc.next();
			Column c = buildColumn(column, btable.getName(), group, locales);
			col.add(c);
		}
		return col;
	}

	private IDataStreamElement getSelectedColumns(IBusinessPackage bpackage, String selectedTableName, String selectedColumnName, String group) throws ServiceException {
		Collection<IBusinessTable> tables = bpackage.getBusinessTables(group);
		for (IBusinessTable btable : tables) {
			if (btable.getName().equals(selectedTableName)) {
				Collection<IDataStreamElement> columns = btable.getColumns(group);
				for (IDataStreamElement element : columns) {
					if (element.getName().equals(selectedColumnName)) {
						return element;
					}
				}
			}
		}
		return null;
	}

	private Column buildColumn(IDataStreamElement column, String tableName, String group, Collection<Locale> locales) {
		Column c = null;
		try {
			if (column.isVisibleFor(group)) {
				c = new Column(column.getName(), column.getOriginName(), tableName, "", column.getDataStream().getName());// column.getJavaClassName());
			} else {
				c = new Column(column.getName(), column.getOriginName(), tableName, "java.lang.String", column.getDataStream().getName());
			}

			c.setDescription(column.getDescription());

			for (Locale locale : locales) {

				if (column.getOuputName(locale) != null && !column.getOuputName(locale).equalsIgnoreCase("")) {
					c.addLocaleTitle(locale.getLanguage(), column.getOuputName(locale));
				} else {
					c.addLocaleTitle(locale.getLanguage(), column.getName());
				}
			}
			c.setType(getType(column.getType()));
//			c.setMeasureBehavior(column.getDefaultMeasureBehavior());

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (c.getTitles().isEmpty()) {
			c.getTitles().put("fr", c.getName());
			c.getTitles().put("en", c.getName());
			c.getTitles().put("", c.getName());
			c.getTitles().put(null, c.getName());
			c.getTitles().put("Default", c.getName());
		}
		return c;
	}

	private SubType getType(bpm.metadata.layer.logical.IDataStreamElement.SubType type) {
		switch (type) {
		case ADRESSE:
			return SubType.ADRESSE;
		case ZONEID:
			return SubType.ZONEID;
		case AVG:
			return SubType.AVG;
		case COMPLETE:
			return SubType.COMPLETE;
		case COUNT:
			return SubType.COUNT;
		case COUNTRY:
			return SubType.COUNTRY;
		case GEOLOCAL:
			return SubType.GEOLOCAL;
		case DAY:
			return SubType.DAY;
		case DIMENSION:
			return SubType.DIMENSION;
		case DISTINCT:
			return SubType.DISTINCT;
		case MAX:
			return SubType.MAX;
		case MIN:
			return SubType.MIN;
		case MONTH:
			return SubType.MONTH;
		case POSTALCODE:
			return SubType.POSTALCODE;
		case QUARTER:
			return SubType.QUARTER;
		case REGION:
			return SubType.REGION;
		case SUM:
			return SubType.SUM;
		case UNDEFINED:
			return SubType.UNDEFINED;
		case WEEK:
			return SubType.WEEK;
		case YEAR:
			return SubType.YEAR;
		case COMMUNE:
			return SubType.COMMUNE;
		case CONTINENT:
			return SubType.CONTINENT;
		case PROPERTY:
			return SubType.PROPERTY;
		default:
			break;
		}
		return null;
	}

	@Override
	public HashMap<String, Object> getFiltersAndPromptsForDataset(InfoUser infos, String pack, String model, String language, int metadataId) throws ServiceException {
		FwrSession session = getSession();

		HashMap<String, Object> resources = new HashMap<String, Object>();

		List<String> filters = new ArrayList<String>();
		HashMap<String, FwrPrompt> prompts = new HashMap<String, FwrPrompt>();
		List<String> dataSources = new ArrayList<String>();

		IBusinessPackage bpackage = null;
		if (session.getBusinessPackage() != null) {
			for (IBusinessPackage packa : session.getBusinessPackage()) {
				if (packa.getName().equals(pack)) {
					bpackage = packa;
					break;
				}
			}
		} else {
			getMetadataContent(metadataId, infos.getGroup().getName(), false);
			for (IBusinessPackage packa : session.getBusinessPackage()) {
				if (packa.getName().equals(pack)) {
					bpackage = packa;
					break;
				}
			}
		}

		session.setSelectedPackage(bpackage);

		Collection<IResource> c = bpackage.getResources(session.getCurrentGroup().getName());

		for (IResource res : c) {
			if (res instanceof IFilter) {
				IFilter f = (IFilter) res;
				filters.add(f.getName());
			}

			if (res instanceof Prompt) {
				Prompt p = (Prompt) res;
				FwrPrompt fp = new FwrPrompt();
				fp.setName(p.getName());
				fp.setQuestion(p.getQuestion());
				fp.setOperator(p.getOperator());
				fp.setMetadataId(metadataId);
				fp.setMetadataId(metadataId);
				fp.setModelParent(bpackage.getBusinessModel().getName());
				fp.setPackageParent(bpackage.getName());
				try {
					fp.setValues(p.getOrigin().getDistinctValues());
				} catch (Exception e) {
					e.printStackTrace();
				}
				prompts.put(fp.getName(), fp);
			}
		}

		List<String> connections = bpackage.getConnectionsNames(session.getCurrentGroup().getName());

		for (String ds : connections) {
			dataSources.add(ds);
		}

		resources.put("filters", filters);
		resources.put("prompts", prompts);
		resources.put("datasources", dataSources);

		return resources;
	}

	@Override
	public int saveComplexReport(FWRReport report, boolean update) throws ServiceException {
		FwrSession session = getSession();

		RepositoryItem item = null;
		if (update) {
			try {
				item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(report.getSaveOptions().getDirectoryItemid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int itemId = -1;
		try {
			XStream xstream = new XStream();
			String xml = xstream.toXML(report);

			if (item == null) {
				SaveOptions so = report.getSaveOptions();
				RepositoryDirectory dir = session.getRepositoryConnection().getRepositoryService().getDirectory(so.getDirectoryId());
				RepositoryItem p = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FWR_TYPE, -1, dir, so.getName(), so.getComment(), so.getInternalVersion(), so.getPublicVerson(), xml, true);
				itemId = p.getId();
			} else {
				session.getRepositoryConnection().getRepositoryService().updateModel(item, xml);
				itemId = item.getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemId;
	}

	@Override
	public boolean validateScript(String script) {
		Parser parser = new Parser(new CompilerEnvirons(), new ErrorReporter() {
			public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {

			}

			public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {
				return null;
			}

			public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {

			}
		});

		try {
			parser.parse(script, null, 0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void saveDataset(DataSet ds) throws ServiceException {
		FwrSession session = getSession();
		session.saveDataset(ds);
	}

	@Override
	public void saveJoinDataset(DataSet ds, List<DataSet> dss) throws ServiceException {
		FwrSession session = getSession();
		session.saveDataset(ds);
		session.saveDatasets(dss);
	}

	@Override
	public void saveComponentForPreview(IReportComponent component) throws ServiceException {
		FwrSession session = getSession();
		session.saveComponent(component);
	}

	@Override
	public DataSet loadDatasetFromQuery(int metadataid, String modelName, String packageName, String queryName) throws Exception {
		FwrSession session = getSession();
		DataSet dataset = new DataSet();

		DataSource datasource = new DataSource();
		datasource.setConnectionName("Default");
		datasource.setBusinessModel(modelName);
		datasource.setBusinessPackage(packageName);
		datasource.setGroup(session.getCurrentGroup().getName());
		datasource.setItemId(metadataid);
		datasource.setRepositoryId(session.getCurrentRepository().getId());
		datasource.setName("DatasourceParam_" + new Object().hashCode());
		datasource.setOnOlap(false);
		datasource.setPassword(session.getUser().getPassword());
		datasource.setUrl(session.getCurrentRepository().getUrl());
		datasource.setUser(session.getUser().getLogin());

		IRepository irep = session.getRepository();

		RepositoryItem item = irep.getItem(metadataid);

		String metadataName = item.getItemName();

		List<FwrBusinessModel> models = getMetadataContent(metadataid, session.getCurrentGroup().getName(), true);
		for (FwrBusinessModel model : models) {
			if (model.getName().equals(modelName)) {
				for (FwrBusinessPackage pack : model.getBusinessPackages()) {
					if (pack.getName().equals(packageName)) {
						for (FwrSavedQuery query : pack.getSavedQueries()) {
							if (query.getName().equals(queryName)) {
								dataset = convertDataset(query, datasource, pack.getFilters(), metadataName);
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return dataset;
	}

	private DataSet convertDataset(FwrSavedQuery query, DataSource datasource, List<FWRFilter> filters, String metadataName) {
		DataSet dataset = new DataSet();
		dataset.setName("dataset_" + System.currentTimeMillis());
		dataset.setLanguage("fr");
		dataset.setDatasource(datasource);
		dataset.setColumns(query.getColumns());
		for (Column col : dataset.getColumns()) {
			col.setMetadataId(dataset.getDatasource().getItemId());
			col.setMetadataParent(metadataName);
			col.setBusinessModelParent(dataset.getDatasource().getBusinessModel());
			col.setBusinessPackageParent(dataset.getDatasource().getBusinessPackage());
			col.setDatasetParent(dataset);
		}
		if (query.getPrompts() != null)
			dataset.setPrompts(query.getPrompts());
		if (query.getFilters() != null && !query.getFilters().isEmpty()) {
			for (FWRFilter queryfilter : query.getFilters()) {				
				if (filters != null && !filters.isEmpty()) {
					for (FWRFilter filter : filters) {
						if (queryfilter.getName().equals(filter.getName())) {
							dataset.addFwrFilter(filter);
							break;
						}
					}
				}
			}
		}
		return dataset;
	}

}
