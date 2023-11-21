/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.faweb.server;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.fasd.olap.FAModel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.thoughtworks.xstream.XStream;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemProperties;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.Topx;
import bpm.fa.api.olap.projection.ProjectionMeasure;
import bpm.fa.api.olap.projection.ProjectionMeasureCondition;
import bpm.fa.api.olap.projection.ProjectionParser;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.olap.unitedolap.UnitedOlapStructure;
import bpm.fa.api.olap.xmla.XMLAMember;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.data.DataField;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.server.tools.DefaultTemplate;
import bpm.faweb.server.tools.FaWebActions;
import bpm.faweb.server.tools.FaWebComplexGrid;
import bpm.faweb.server.tools.FaWebServiceData;
import bpm.faweb.server.tools.PDFWriter;
import bpm.faweb.server.tools.RecupDim;
import bpm.faweb.server.tools.RecupInfosCube;
import bpm.faweb.server.tools.RecupMes;
import bpm.faweb.server.tools.ServerReportHelper;
import bpm.faweb.server.tools.XLSWriter;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.DrillDTO;
import bpm.faweb.shared.DrillParameterDTO;
import bpm.faweb.shared.FilterConfigDTO;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.IDirectoryDTO;
import bpm.faweb.shared.IDirectoryItemDTO;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.MapValues;
import bpm.faweb.shared.OpenLayer;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.SerieChart;
import bpm.faweb.shared.SortElement;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.GridComplex;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemOlapMember;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.faweb.shared.infoscube.MapInfo;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.Constants.OutputTypes;
import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.api.beans.components.TextHTMLComponent;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.Email;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.internal.ModelServiceProvider;
import bpm.united.olap.remote.internal.RuntimeServiceProvider;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.RemoteVanillaSystemManager;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * @author Charbonnier
 * 
 */
public class FaWebServiceImpl extends RemoteServiceServlet implements FaWebService {

	private static final long serialVersionUID = -1692771488341998233L;

	private Logger logger = Logger.getLogger(FaWebServiceImpl.class);

	private static String[] colors = { "ff0000", "00ff00", "0000ff", "555500", "005555", "550055" };

	private HashMap<String, String> labels = new HashMap<String, String>();
//	private List<List<String>> mapColors = new ArrayList<List<String>>();

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public Integer initSession() throws ServiceException {
		// We create an empty session
		try {
			FaWebSession session = null;
			try {
				session = getSession();
			} catch (Exception e) {
			}

			if (session == null) {
				String sessionId = CommonSessionManager.createSession(FaWebSession.class);
				CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
				session = CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FaWebSession.class);
				session.setLocation(getServletContext().getRealPath(File.separator));
			}

			return session.initSession();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	private FaWebSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FaWebSession.class);
	}

	@Override
	public TreeParentDTO getRepositories(int keySession, int type) throws ServiceException {
		FaWebSession session = getSession();
		TreeParentDTO res = null;
		try {
			IRepository rep = null;
			switch (type) {
			case 0:
				rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.FASD_TYPE);
				break;

			case 1:
				rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.FAV_TYPE);
				break;

			case 2:
				rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.FD_TYPE);
				break;

			case 3:
				rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.PROJECTION_TYPE);
				break;

			default:
				break;
			}

			res = FaWebServiceData.getContentsByType(session, keySession, rep, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public List<String> browseFASDModel(int keySession, int modelId) throws ServiceException {
		FaWebSession session = getSession();

		if (session.getRepository() == null) {
			session.initRepository(IRepositoryApi.FASD_TYPE);
		}

		List<String> listCubeNames = new ArrayList<String>();
		try {
			listCubeNames = FaWebActions.loadModel(keySession, session.getCurrentGroup(), modelId, session, session.getLocation(), session.getInfosOlap(keySession));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return listCubeNames;
	}

	@Override
	public InfosReport loadSavedView(int keySession, String viewKey, HashMap<String, String> parameters) {
		try {
			FaWebSession session = getSession();
			InfosOlap infos = session.getInfosOlap(keySession);
			GridCube gc = null;
			try {
				List<Calcul> calculs = FaWebActions.writeView(viewKey, session, session.getInfosOlap(keySession), parameters);
				gc = getTable(session.getInfosOlap(keySession));
				if (calculs != null)
					gc.setCalculs(calculs);

				infos.getInfosReport().setGrid(gc);

				try {
					infos.getInfosReport().setChartInfos(FaWebActions.writeChart(viewKey, session, session.getInfosOlap(keySession)));
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}

				try {
					infos.getInfosReport().setMapOptions(FaWebActions.writeMap(viewKey, session, session.getInfosOlap(keySession)));
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				
				try {
					infos.getInfosReport().setCustomSizes(FaWebActions.getCustomSizesFromView(viewKey, session, session.getInfosOlap(keySession)));
					infos.getInfosReport().setSizeLoaded(true);
				} catch(Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			return infos.getInfosReport();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ItemView> setViewsService(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		session.getCubeViews(keySession).clear();

		List<ItemView> result = new ArrayList<ItemView>();
		HashMap<RepositoryItem, byte[]> views = new HashMap<RepositoryItem, byte[]>();

		IRepositoryApi sock = session.getRepositoryConnection();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			views = sock.getRepositoryService().getCubeViewsWithImageBytes(infos.getCubeName(), infos.getSelectedFasd());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String tempPath = getServletContext().getRealPath(File.separator);

		for (RepositoryItem view : views.keySet()) {
			session.getCubeViews(keySession).add(view);

			String resStringBytes = "";
			String leftImage = "";
			String rightImage = "";

			File dir = new File(tempPath + File.separator + "rolodexImages");
			if (!dir.exists()) {
				dir.mkdir();
			}

			String path = tempPath + File.separator + "rolodexImages" + File.separator + view.getId() + view.getItemName();

			if (views.get(view) != null) {

				byte[] reb = views.get(view);

				InputStream in = new ByteArrayInputStream(reb);
				BufferedImage bImageFromConvert = null;
				try {
					bImageFromConvert = ImageIO.read(in);
				} catch (IOException e) {
					e.printStackTrace();
				}

				resStringBytes = path + ".png";

				FileOutputStream out = null;
				try {
					out = new FileOutputStream(new File(resStringBytes));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					ImageIO.write(bImageFromConvert, "png", out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				leftImage = path + "left.png";
				rightImage = path + "right.png";

				// Image de Gauche
				AffineTransform transformLeft = new AffineTransform();
				transformLeft.scale(0.7, 0.7);
				transformLeft.shear(0, -0.5);
				transformLeft.translate(0, 90);
				BufferedImageOp bio = new AffineTransformOp(transformLeft, AffineTransformOp.TYPE_BILINEAR);

				BufferedImage leftBufImg = bio.filter(bImageFromConvert, null);
				try {
					out = new FileOutputStream(new File(leftImage));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					ImageIO.write(leftBufImg, "png", out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// image de droite
				AffineTransform transformRight = new AffineTransform();
				transformRight.scale(0.7, 0.7);
				transformRight.shear(0, 0.5);
				transformRight.translate(0, -40);

				BufferedImageOp opRight = new AffineTransformOp(transformRight, AffineTransformOp.TYPE_BILINEAR);
				BufferedImage rightBufImg = opRight.filter(bImageFromConvert, null);

				try {
					out = new FileOutputStream(new File(rightImage));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					ImageIO.write(rightBufImg, "png", out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				session.getCreatedFiles(keySession).add(resStringBytes);
				session.getCreatedFiles(keySession).add(leftImage);
				session.getCreatedFiles(keySession).add(rightImage);

				resStringBytes = "/" + view.getId() + view.getItemName() + ".png";
				leftImage = "/" + view.getId() + view.getItemName() + "left.png";
				rightImage = "/" + view.getId() + view.getItemName() + "right.png";
			}

			ItemView viewItem = new ItemView(view.getItemName(), view.getItemName(), view.getId(), resStringBytes, leftImage, rightImage);
			viewItem.setDirectoryItemId(view.getId());
			result.add(viewItem);
		}

		return result;
	}

	@Override
	public InfosReport getInfosCubeService(int keySession, bpm.faweb.shared.ItemCube cube, String datasource) throws ServiceException {
		FaWebSession session = getSession();

		browseFASDModel(keySession, cube.getFASDParentId());

		InfosOlap infos = session.getInfosOlap(keySession);
		infos.setCubeName(cube.getName());
		session.setSelectedDatasource(keySession, datasource);

		InfosReport resut = null;
		try {
			resut = RecupInfosCube.openNew(keySession, cube.getName(), session.getCurrentGroup().getName(), session, this, infos);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resut;
	}

	public GridCube getTable(InfosOlap session) {
		session.setFirstValue(true);
		session.getInfosReport().setMeasuresDisplay(new ArrayList<String>());

		List<ArrayList<Item>> tab = new ArrayList<ArrayList<Item>>();
		tab = session.getRes().getRaw();

		List<ArrayList<ItemCube>> items = new ArrayList<ArrayList<ItemCube>>();
		HashMap<String, ItemElement> itemsByUname = new HashMap<String, ItemElement>();

		int i = 0;
		for (ArrayList<Item> itable : tab) {// row
			items.add(i, new ArrayList<ItemCube>());

			int j = 0;
			for (Item item : itable) { // col
				if (item instanceof ItemNull) {
					ItemCube itc = new ItemCube("", "ItemNull");
					items.get(i).add(j, itc);

				}
				else if (item instanceof ItemElement) {
					ItemElement itemElement = (ItemElement) item;

					if (session.getMeasures().contains(itemElement.getLabel()) && (!session.getInfosReport().getMeasuresDisplay().contains(itemElement.getLabel()))) {
						session.getInfosReport().setInCol(itemElement.isCol());
						session.getInfosReport().addMeasureDisplay(itemElement.getLabel());
					}

					if (itemElement.getDataMember() instanceof XMLAMember) {
						((XMLAMember) itemElement.getDataMember()).setUname(((XMLAMember) itemElement.getDataMember()).getUniqueName().replace("&", "&amp;"));
					}

					String uname = itemElement.getDataMember().getUniqueName();
					ItemCube itc = new ItemCube(itemElement.getLabel(), uname, "ItemElement");
					if (itemElement.getDataMember().getLevel() != null) {
						itc.setLevelUname(itemElement.getDataMember().getLevel().getUniqueName());
					}
					items.get(i).add(j, itc);

					itemsByUname.put(uname, itemElement);
				}
				else if (item instanceof ItemValue) {
					if (session.isFirstValue()) {
						session.getInfosReport().setJFirst(j);
						session.getInfosReport().setIFirst(i);
						session.setFirstValue(false);
					}
					ItemValue itv = (ItemValue) item;
					String v = itv.getValue();

					float test = 0.0f;
					try {
						if (v != null && !v.equalsIgnoreCase("")) {
							try {
								test = Float.parseFloat(v.replaceAll(",", ".").replace(" ", ""));
							} catch (Exception e) {
								test = NumberFormat.getInstance().parse(v.replaceAll(",", ".").replace(" ", "")).floatValue();
							}
						}
						else if (v != null && !itv.getLabel().equalsIgnoreCase("")) {
							test = Float.parseFloat(itv.getLabel());
						}
					} catch (Exception e) {
						test = Float.parseFloat(v.replaceAll(",", ""));
					}

					ItemCube itc;
					if (Math.abs(test - 0.0001f) > 0.0001f || itv.getLabel().contains("%")) {
						itc = new ItemCube(itv.getLabel(), "ItemValue");
						try {
							float va = Float.parseFloat(v);
							itc.setValue(va);
						} catch (Exception e) {

						}

					}
					else {
						itc = new ItemCube(" ", "ItemValue");
						itc.setValue(0);
					}

					items.get(i).add(j, itc);
				}
				else if (item instanceof ItemProperties) {
					ItemCube itc = null;
					if (item.getLabel().equalsIgnoreCase("")) {
						itc = new ItemCube("", "ItemNull");
					}
					else {
						itc = new ItemCube(item.getLabel(), "uname", "ItemProperties");
					}
					items.get(i).add(j, itc);
				}
				else {
					ItemCube itc = new ItemCube("", "ItemNull");
					items.get(i).add(j, itc);
				}

				j++;
			}

			i++;
		}

		GridCube gc = new GridCube();

		List<String> cols = new ArrayList<String>(session.getCube().getMdx().getCols());
		List<String> rows = new ArrayList<String>(session.getCube().getMdx().getRows());

		gc.setQueryCols(cols);
		gc.setQueryRows(rows);

		gc.setItems(items);
		gc.setProperties(session.getCube().getShowProperties());
		gc.setZero(session.getCube().getShowEmpty());
		gc.setNbMeasures(session.getInfosReport().getMeasuresDisplay().size());
		List<bpm.faweb.shared.infoscube.Topx> topx = new ArrayList<bpm.faweb.shared.infoscube.Topx>();
		for (Topx t : session.getCube().getTopx()) {
			bpm.faweb.shared.infoscube.Topx top = new bpm.faweb.shared.infoscube.Topx();
			top.setCount(t.getCount());
			top.setElementName(t.getElementName());
			top.setElementTarget(t.getElementTarget());
			top.setOnRow(t.isOnRow());
			topx.add(top);
		}
		gc.setTopx(topx);
		gc.setPersonalNames(session.getCube().getPersonalNames());
		gc.setPercentMeasures(session.getCube().getPercentMeasures());

		session.setFirstValue(true);
		session.setItemsByUname(itemsByUname);
		return gc;
	}

	@Override
	public GridComplex getComplexGridService(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		return FaWebComplexGrid.getComplexGrid(session, session.getInfosOlap(keySession));
	}

	@Override
	public InfosReport swapAxesService(int keySession, boolean isProjection) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();
		try {
			infos.getCube().setApplyProjection(isProjection);
			infos.getCube().swapAxes();
			infos.setRes(cube.doQuery());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().undo();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport refreshService(int keySession, boolean isProjection) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			infos.getCube().setApplyProjection(isProjection);
			infos.getCube().restore();
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().undo();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport drillService(int keySession, int row, int cell, Projection projection) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		ItemElement it;
		boolean added = false;
		try {
			List<ArrayList<Item>> tp;
			if (projection != null) {
				tp = infos.getCube().getLastProjectionResult().getRaw();
				infos.getCube().setApplyProjection(true);
			}
			else {
				tp = infos.getCube().getLastResult().getRaw();
				infos.getCube().setApplyProjection(false);
			}
			it = (ItemElement) tp.get(row).get(cell);
			if (!it.isDrilled()) {
				added = infos.getCube().drilldown(it);
				infos.setRes(infos.getCube().doQuery());
			}
			else {
				infos.getCube().drillup(it);
				infos.setRes(infos.getCube().doQuery());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}

		try {
			if (added) {
				OLAPMember themb = it.getDataMember();
				session.getOlapMembers(keySession).remove(themb);

				Collection<OLAPMember> sons = themb.getMembers();
				List<ItemOlapMember> ressons = new ArrayList<ItemOlapMember>();
				for (OLAPMember currm : sons) {
					session.addOlapMember(keySession, currm);
					ressons.add(new ItemOlapMember(currm.getCaption(), currm.getUniqueName(), currm.getHierarchy(), false));
				}
				infos.getInfosReport().setNewmembers(ressons);
				infos.getInfosReport().setMembrerdrill(it.getDataMember().getUniqueName());
				infos.getInfosReport().setDimchange(true);
			}
			else {
				infos.getInfosReport().setDimchange(false);

			}

			infos.getInfosReport().setGrid(getTable(infos));

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return infos.getInfosReport();
	}

	@Override
	public InfosReport drillAllService(int keySession, int row, int cell, Projection projection, boolean isDrillDown) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		ItemElement it;
		try {
			List<ArrayList<Item>> tp;
			if (projection != null) {
				tp = infos.getCube().getLastProjectionResult().getRaw();
				infos.getCube().setApplyProjection(true);
			}
			else {
				tp = infos.getCube().getLastResult().getRaw();
				infos.getCube().setApplyProjection(false);
			}
			it = (ItemElement) tp.get(row).get(cell);

			OLAPMember parent = null;

			if (row > 0 && cell > 0) {
				OLAPMember currentMember = it.getDataMember();
				String currentUname = currentMember.getUniqueName();
				String parentUname = currentUname.substring(0, currentUname.lastIndexOf(".["));
				parent = infos.getCube().findOLAPMember(parentUname);

				if (parent != null) {
					infos = drillAll(parent.getMembers(), keySession, it.isCol(), isDrillDown);
				}
			}
			else {
				if ((!it.isDrilled() && isDrillDown) || (it.isDrilled() && !isDrillDown))
					return drillService(keySession, row, cell, projection);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}
		return infos.getInfosReport();
	}

	private InfosOlap drillAll(Collection<OLAPMember> childsMember, int keySession, boolean isCol, boolean isDrillDown) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		HashMap<ItemElement, Boolean> membersAdded = new HashMap<ItemElement, Boolean>();

		try {
			for (OLAPMember member : childsMember) {
				ItemElement childItem = new ItemElement(member.getUniqueName(), isCol);

				if (isDrillDown) {
					membersAdded.put(childItem, infos.getCube().drilldown(childItem));
				}
				else {
					infos.getCube().drillup(childItem);
				}
			}
			infos.setRes(infos.getCube().doQuery());

		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}
		try {
			if (isDrillDown) {
				for (ItemElement childItem : membersAdded.keySet()) {
					if (membersAdded.get(childItem)) {
						OLAPMember themb = childItem.getDataMember();
						session.getOlapMembers(keySession).remove(themb);

						Collection<OLAPMember> sons = themb.getMembers();
						List<ItemOlapMember> ressons = new ArrayList<ItemOlapMember>();
						for (OLAPMember currm : sons) {
							session.addOlapMember(keySession, currm);
							ressons.add(new ItemOlapMember(currm.getCaption(), currm.getUniqueName(), currm.getHierarchy(), false));
						}
						infos.getInfosReport().setNewmembers(ressons);
						infos.getInfosReport().setMembrerdrill(childItem.getDataMember().getUniqueName());
						infos.getInfosReport().setDimchange(true);
					}
					else {
						infos.getInfosReport().setDimchange(false);

					}
					infos.getInfosReport().setGrid(getTable(infos));
				}
			}
			else {
				infos.getInfosReport().setDimchange(false);
				infos.getInfosReport().setGrid(getTable(infos));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infos;
	}

	@Override
	public DrillInformations drillThroughService(int keySession, int row, int cell, Projection projection) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPResult res = null;
		try {
			List<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			ItemValue it = (ItemValue) tp.get(row).get(cell);
			if (projection != null) {
				bpm.fa.api.olap.projection.Projection faProj = new bpm.fa.api.olap.projection.Projection();
				faProj.setFasdId(session.getInfosOlap(keySession).getSelectedFasd().getId());
				faProj.setCubeName(session.getInfosOlap(keySession).getCubeName());
				faProj.setName(projection.getName());
				faProj.setProjectionMeasures(createFaProjectionMeasures(projection));
				faProj.setType(projection.getType());
				res = infos.getCube().drillthrough(it, 1, faProj);
			}
			else {
				res = infos.getCube().drillthrough(it, 1);
			}

			infos.setRes(res);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		List<ArrayList<Item>> tab = new ArrayList<ArrayList<Item>>();
		tab = res.getRaw();

		List<List<String>> items = new ArrayList<List<String>>();

		for (int i = 0; i < res.getYFixed(); i++) {
			ArrayList<Item> ytable = tab.get(i);

			ArrayList<String> ligne = new ArrayList<String>();
			for (int j = 0; j < res.getXFixed(); j++) { // col
				ligne.add(j, ((ItemValue) ytable.get(j)).getLabel());
			}
			items.add(i, ligne);

		}

		int key = new Object().hashCode();

		List<String> columns = items.get(0);
		int size = items.size() - 1;

		// Remove the first line which contain the names of columns
		items.remove(0);

		session.stockDrill(key, items);

		return new DrillInformations(key, columns, size);
	}

	@Override
	public HashMap<String, String> drillMultipleService(int keySession, int row, int cell) throws ServiceException {
		FaWebSession session = getSession();

		InfosOlap infos = session.getInfosOlap(keySession);

		ItemValue it = (ItemValue) infos.getCube().getLastResult().getRaw().get(row).get(cell);

		it.setPosition(row, cell);

		List<String> urls = infos.getCube().getDrillsUrl(it);

		HashMap<String, String> res = new HashMap<String, String>();

		String fawebUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);
		IRepositoryContext ctx = session.getRepositoryConnection().getContext();

		for (String url : urls) {

			try {
				String[] urlParts = url.split(";");

				String generatedUrl = fawebUrl;
				generatedUrl += "?bpm.vanilla.sessionId=" + ((RemoteVanillaSystemManager) session.getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();
				generatedUrl += "&bpm.vanilla.groupId=" + ctx.getGroup().getId();
				generatedUrl += "&bpm.vanilla.repositoryId=" + ctx.getRepository().getId();
				generatedUrl += "&bpm.vanilla.fasd.id=" + session.getInfosOlap(keySession).getSelectedFasd().getId();
				generatedUrl += "&bpm.vanilla.cubename=" + URLEncoder.encode(urlParts[0], "UTF-8");
				generatedUrl += "&bpm.vanilla.dimension=" + URLEncoder.encode(urlParts[1], "UTF-8");
				generatedUrl += "&bpm.vanilla.member=" + URLEncoder.encode(urlParts[2], "UTF-8");

				res.put(urlParts[0] + " - " + urlParts[1], generatedUrl);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	@Override
	public List<String> UndoRedoService(int keySession, int type) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		switch (type) {
		case 1:
			infos.getCube().undo();
			try {
				infos.setRes(infos.getCube().doQuery());
				infos.getInfosReport().setGrid(getTable(infos));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			infos.getCube().redo();
			try {
				infos.setRes(infos.getCube().doQuery());
				infos.getInfosReport().setGrid(getTable(infos));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		return infos.getCube().getMdx().getWhere();
	}

	@Override
	public InfosReport addService(int keySession, String source, int row, int cell, boolean before) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			List<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			if (before) {
				ItemElement ittarget = (ItemElement) tp.get(row).get(cell);
				ItemElement itsource = new ItemElement(source, ittarget.isCol());
				infos.getCube().addBefore(itsource, ittarget);
			}
			else {
				ItemElement ittarget;
				ittarget = (ItemElement) tp.get(row).get(cell);
				ItemElement itsource = new ItemElement(source, ittarget.isCol());
				infos.getCube().addAfter(itsource, ittarget);
			}
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e1) {
			e1.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport addService(int keySession, List<String> items, boolean col) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);

		List<ItemElement> added = new ArrayList<ItemElement>();
		try {
			for (String itemName : items) {
				ItemElement itsource = new ItemElement(itemName, col);
				infos.getCube().add(itsource);
				added.add(itsource);
				infos.setRes(infos.getCube().doQuery());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			for (ItemElement it : added) {
				infos.getCube().remove(it);
				try {
					infos.setRes(infos.getCube().doQuery());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));

		return infos.getInfosReport();
	}

	@Override
	public InfosReport addService(int keySession, int rows, int cells, int rowt, int cellt, boolean before) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			List<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			ItemElement ittarget = (ItemElement) tp.get(rowt).get(cellt);
			ItemElement tmp = (ItemElement) tp.get(rows).get(cells);
			String source = tmp.getDataMember().getUniqueName();
			ItemElement itsource = new ItemElement(source, ittarget.isCol());
			if (before) {
				infos.getCube().addBefore(itsource, ittarget);
			}
			else {
				infos.getCube().addAfter(itsource, ittarget);
			}
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e1) {
			e1.printStackTrace();
			infos.getCube().undo();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport moveService(int keySession, int rows, int cells, int rowt, int cellt, boolean before) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			List<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			ItemElement ittarget = (ItemElement) tp.get(rowt).get(cellt);
			ItemElement tmp = (ItemElement) tp.get(rows).get(cells);
			String source = tmp.getDataMember().getUniqueName();
			ItemElement itsource = new ItemElement(source, ittarget.isCol());
			if (before) {
				infos.getCube().moveBefore(itsource, ittarget);
			}
			else {
				infos.getCube().moveAfter(itsource, ittarget);
			}
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e1) {
			e1.printStackTrace();
			infos.getCube().undo();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport filterService(int keySession, int row, int cell) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			ArrayList<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			if ((tp.get(row).get(cell)) instanceof ItemElement) {
				ItemElement it = (ItemElement) (tp.get(row)).get(cell);
				infos.getCube().addWhere(it);
				infos.setRes(infos.getCube().doQuery());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport filterService(int keySession, String uname) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			FaWebActions.filterItem(uname, session, infos);
		} catch (Exception ex) {
			infos.getCube().getLastResult();
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport filterService(int keySession, List<String> filters) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);

		HashMap<String, ItemElement> items = new HashMap<String, ItemElement>();
		for (String uname : filters) {
			try {
				ItemElement ite = FaWebActions.filterItem(uname, session, infos);
				items.put(ite.getDataMember().getUniqueName(), ite);
			} catch (WhereClauseException ex) {
				ex.printStackTrace();

				infos.getCube().getLastResult();
				throw new ServiceException(ex.getMessage(), ex);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(e.getMessage(), e);
			}
		}

		if (infos.getFiltersByUname().size() != 0) {
			infos.getFiltersByUname().putAll(items);
		}
		else {
			infos.setFiltersByUname(items);
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport removefilterService(int keySession, String uname) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		HashMap<String, ItemElement> items = infos.getFiltersByUname();
		try {
			if (items != null && !items.isEmpty()) {
				infos.getCube().removeWhere((ItemElement) items.get(uname));
				infos.setRes(infos.getCube().doQuery());
				items.remove(uname);
			}
			else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport removeService(int keySession, int row, int cell) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			ArrayList<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
			ItemElement it = (ItemElement) ((ArrayList<Item>) tp.get(row)).get(cell);
			infos.getCube().remove(it);
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception ex) {
			ex.printStackTrace();
			infos.getCube().undo();
			return null;
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport removeService(int keySession, List<String> items) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		HashMap<String, ItemElement> itemsByUname = infos.getItemsByUname();

		boolean removeOk = true;
		int nbRemoved = 0;

		if (session.getInfosOlap(keySession).getInfosReport().getFailedUnames() != null) {
			session.getInfosOlap(keySession).getInfosReport().getFailedUnames().clear();
		}

		for (String currUname : items) {
			if (removeOk) {
				ItemElement item = itemsByUname.get(currUname);

				if (item != null) {
					try {
						removeOk = infos.getCube().remove(item);
						if (removeOk) {
							nbRemoved++;
						}
						else {
							session.getInfosOlap(keySession).getInfosReport().addFailedUname(currUname);
						}
					} catch (RuntimeException e) {
						session.getInfosOlap(keySession).getInfosReport().addFailedUname(currUname);
						e.printStackTrace();
					}
				}
				else {
					for (String key : itemsByUname.keySet()) {
						if (key.contains(currUname)) {
							item = new ItemElement(currUname, (itemsByUname.get(key)).isCol());
							break;
						}
					}

					try {
						// This should happens when we try to remove some
						// ".children" elements
						if (item == null) {
							for (String key : itemsByUname.keySet()) {
								if (currUname.startsWith(key)) {
									item = new ItemElement(currUname, (itemsByUname.get(key)).isCol());
									break;
								}
							}
						}
						removeOk = infos.getCube().remove(item);
						if (removeOk) {
							nbRemoved++;
						}
						else {
							session.getInfosOlap(keySession).getInfosReport().addFailedUname(currUname);
						}
					} catch (RuntimeException e) {
						session.getInfosOlap(keySession).getInfosReport().addFailedUname(currUname);
						e.printStackTrace();
					}

				}
			}
		}

		if (removeOk) {
			try {
				infos.setRes(infos.getCube().doQuery());
				infos.getInfosReport().setGrid(getTable(infos));
				return infos.getInfosReport();
			} catch (Exception e) {
				e.printStackTrace();
				infos.getCube().undo();
				return null;
			}
		}
		else {
			for (int i = 0; i < nbRemoved; i++) {
				infos.getCube().undo();
			}
			return null;
		}
	}

	@Override
	public String setDataService(int keySession, String d) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);

		int nbOfCategories = 0;
		String data = d;

		// Les cat�gories = les mesures affich�es
		data += "<categories font='Arial' fontSize='11' fontColor='000000'>\n";

		List<ArrayList<Item>> tab = new ArrayList<ArrayList<Item>>();
		tab = infos.getCube().getLastResult().getRaw();

		int nbMes = infos.getInfosReport().getMeasuresDisplay().size();

		for (ArrayList<Item> ytable : tab) {
			for (int j = 0; j < 2; j++) { // col
				if (ytable.get(j) instanceof ItemElement && (!infos.getMeasures().contains(((ItemElement) ytable.get(j)).getLabel())) && (!((ItemElement) ytable.get(j)).getLabel().equals(""))) {
					data += "<category name='" + ((ItemElement) ytable.get(j)).getLabel() + "'/>\n";
					nbOfCategories++;
				}
			}
		}

		data += "</categories>\n";

		int rowofvalues = infos.getInfosReport().getIFirst();
		int colofvalues = infos.getInfosReport().getJFirst();

		if (!infos.getInfosReport().isInCol()) {
			for (int n = 0; n < nbMes; n++) {
				data += "<dataset seriesname='" + infos.getInfosReport().getMeasuresDisplay().get(n) + "' color='" + colors[n] + "'>\n";
				for (int m = 0; m < nbOfCategories; m++) {
					data += "<set value='" + ((ItemValue) tab.get(rowofvalues + n + nbMes * m).get(colofvalues)).getValue() + "'/>\n";
				}
				data += "</dataset>\n";
			}
		}
		else {
			for (int n = 0; n < nbMes; n++) {
				data += "<dataset seriesname='" + infos.getInfosReport().getMeasuresDisplay().get(n) + "' color='" + colors[n] + "'>\n";
				for (int m = 0; m < nbOfCategories; m++) {
					data += "<set value='" + ((ItemValue) (tab.get(rowofvalues + m)).get(colofvalues + n)).getValue() + "'/>\n";
				}
				data += "</dataset>\n";
			}
		}
		data += "</graph>";

		return data;
	}

	@Override
	public InfosReport showEmptyService(int keySession, boolean isProjection) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			infos.getCube().setApplyProjection(isProjection);
			boolean show = !infos.getCube().getShowEmpty();
			infos.getCube().setshowEmpty(show);
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public int saveService(int keySession, String name, String group, ChartParameters chartParams, List<Calcul> calculs, int dirId, String comment, String internalVersion, String publicVersion, String gridHtml, InfosReport infosReport, MapOptions mapOptions) throws ServiceException {
		int res = -1;
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);

		IRepositoryApi sock = session.getRepositoryConnection();

		dirId = infos.getSelectedFasd().getDirectoryId();

		try {

			String cubeViewXml = createCubeViewXml(infos, calculs, chartParams, infosReport, mapOptions);
			RepositoryDirectory target = session.getRepository().getDirectory(dirId);

			String bytes = createImageUsingFWR("html", name, infos, gridHtml);

			cubeViewXml += "<rolodeximage>" + bytes + "</rolodeximage>\n";

			cubeViewXml += "</view>\n";

			cubeViewXml = "<fav><name>" + name + "</name><cubename>" + infos.getCubeName() + "</cubename><fasdid>" + infos.getSelectedFasd().getId() + "</fasdid>" + cubeViewXml + "</fav>";

			sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FAV_TYPE, -1, target, name, comment, internalVersion, publicVersion, cubeViewXml, true);

			res = 2;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return res;
	}

	private String createImageUsingFWR(String outputCaps, String name, InfosOlap infos, String gridHtml) throws Exception {
		int height = (infos.getRes().getRaw().size() * 30) + 100;
		int width = (infos.getRes().getRaw().get(0).size() * 100) + 100;

		HashMap<String, String> res = new HashMap<String, String>();
		res.put("left", "0");
		res.put("right", "0");
		res.put("top", "0");
		res.put("bottom", "0");

		try {
			if (name == null || name.isEmpty())
				name = "Report_" + new Date().getTime();

			InputStream is = generateFreeWebReport(name, "", CommonConstants.FORMAT_HTML, res, "portrait", height + "]" + width, gridHtml, null);

			try {
				getThreadLocalRequest().getSession().setAttribute("output", outputCaps.toLowerCase());
				getThreadLocalRequest().getSession().setAttribute("reportName", name);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("An error happend.");
			}

			String htmlcode = IOUtils.toString(is, "UTF-8");

			JEditorPane pane = new JEditorPane();
			pane.setEditable(false);
			pane.setContentType("text/html");
			htmlcode = htmlcode.substring(htmlcode.indexOf("<table"), htmlcode.indexOf("</table>") + "</table>".length());
			pane.setText(htmlcode);
			pane.setSize(new Dimension(width, height));
			/*
			 * Create a BufferedImage
			 */
			BufferedImage image = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			/*
			 * Have the image painted by SwingUtilities
			 */
			JPanel container = new JPanel();
			SwingUtilities.paintComponent(g, pane, container, 0, 0, image.getWidth(), image.getHeight());
			g.dispose();

			// rendre le fond transparent
			ImageFilter filter = new RGBImageFilter() {
				public final int filterRGB(int x, int y, int rgb) {
					ColorModel cm = ColorModel.getRGBdefault();
					int alpha = cm.getAlpha(rgb);
					int rouge = cm.getRed(rgb);
					int vert = cm.getGreen(rgb);
					int bleu = cm.getBlue(rgb);
					if (rouge == 255 && vert == 255 && bleu == 255) {
						alpha = 0 & 0xFF;
						return alpha | rouge | vert | bleu;
					}
					else
						return rgb;
				}
			};
			ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
			Image i = Toolkit.getDefaultToolkit().createImage(ip);

			BufferedImage dest = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = dest.createGraphics();
			g2.drawImage(i, 0, 0, null);
			g2.dispose();

			dest = resize(dest, 256, 256);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(dest, "png", out);

			byte[] imgBytes = out.toByteArray();

			String bytes = "";
			for (byte b : imgBytes) {
				bytes += ":" + b;
			}
			bytes = bytes.substring(1);

			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error happend during snapshot creation.");
		}
	}

	private String createCubeViewXml(InfosOlap infos, List<Calcul> calculs, ChartParameters chartParams, InfosReport infosReport, MapOptions mapOptions) {
		String cubeViewXml = infos.getCube().getView().getXML();

		if (calculs != null) {
			cubeViewXml = cubeViewXml.replaceAll("</view>", "");
			String calcXml = "";
			for (Calcul c : calculs) {
				calcXml += "	\t<calcul>\n";
				calcXml += " 		\t<operator>" + c.getOperator() + "</operator>\n";
				calcXml += " 		\t<orientation>" + c.getOrientation() + "</orientation>\n";
				calcXml += "  		\t<fields>\n";

				for (String f : c.getFields()) {
					calcXml += " 		\t\t<field>" + f + "</field>\n";
				}
				calcXml += "  		\t</fields>\n";

				calcXml += " 		\t<constant>" + c.getConstant() + "</constant>\n";
				calcXml += " 		\t<title>" + c.getTitle() + "</title>\n";
				calcXml += " 	\t</calcul>\n";

			}

			cubeViewXml += calcXml;

		}

		if (chartParams != null) {
			if (chartParams.getSelectedGroup() != null) {
				String chartXml = "";

				chartXml += "	\t<chart>\n";

				chartXml += "		\t<title>" + chartParams.getChartTitle() + "</title>\n";

				chartXml += "		\t<groups>\n";
				for (String chartGroup : chartParams.getSelectedGroup()) {
					chartXml += "			\t<group>" + chartGroup + "</group>\n";
				}
				chartXml += "		\t</groups>\n";

				chartXml += "		\t<datas>\n";
				for (String chartData : chartParams.getSelectedData()) {
					chartXml += "			\t<data>" + chartData + "</data>\n";
				}
				chartXml += "		\t</datas>\n";

				chartXml += "		\t<filters>\n";
				for (String f : chartParams.getSelectedChartFilters()) {
					chartXml += "			\t<filter>" + f + "</filter>\n";
				}
				chartXml += "		\t</filters>\n";

				chartXml += "		\t<measure>" + chartParams.getMeasure() + "</measure>\n";

				chartXml += "		\t<type>" + chartParams.getChartType() + "</type>\n";

				chartXml += "		\t<renderer>" + chartParams.getDefaultRenderer() + "</renderer>\n";

				chartXml += "	\t</chart>\n";

				cubeViewXml += chartXml;
			}
		}
		
		if(!infosReport.getCustomSizes().isEmpty()) {
			String xml = "	\t<sizes>\n";
			for(int col : infosReport.getCustomSizes().keySet()) {
				xml += "		\t<size><sizeCol>" + col + "</sizeCol>";
				xml += "<sizeWidth>" + infosReport.getCustomSizes().get(col) + "</sizeWidth></size>\n";
			}
			xml += "	\t</sizes>\n";
			cubeViewXml += xml;
		}
		
		if (mapOptions != null) {
//			String mapInfo = (MapInformation) xstream.fromXML(dp.getMapModel());
			String mapXml = new XStream().toXML(mapOptions);
			StringBuffer buf = new StringBuffer();
			buf.append("	\t<mapInfo>\n");
			buf.append(mapXml);
			buf.append("	\t</mapInfo>\n");

			cubeViewXml += buf.toString();
		}

		return cubeViewXml;
	}

	@Override
	public ExportResult exportCube(InfoShareCube infoShare, String chartSVG, String gridHtml) throws ServiceException {
		FaWebSession session = getSession();

		String name = infoShare.getTitle();
		if (name != null) {
			name = name.replaceAll("[^\\p{L}\\p{Nd}]+", "");
		}

		String outputType = "Unknown";
		if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_PDF)) {
			outputType = OutputTypes.PDF;
		}
		else if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_XLS)) {
			outputType = OutputTypes.EXCEL;
		}
		else if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_HTML)) {
			outputType = OutputTypes.HTML;
		}

		try {
			if (name == null || name.isEmpty())
				name = "Report_" + new Date().getTime();

			String orientation = infoShare.isPortrait() ? "portrait" : "landscape";

			InputStream is = generateFreeWebReport(infoShare.getTitle(), infoShare.getDescription(), outputType, infoShare.getMargins(), orientation, "A4", gridHtml, chartSVG);

			ObjectInputStream reportsStream = new ObjectInputStream();

			byte currentXMLBytes[] = IOUtils.toByteArray(is);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			reportsStream.addStream(infoShare.getFormat(), byteArrayIs);
			is.close();

			session.addReport(name, reportsStream);

			if (infoShare.getTypeShare() == TypeShare.EXPORT || infoShare.getTypeShare() == TypeShare.GED) {
				return new ExportResult(name);
			}
			else if (infoShare.getTypeShare() == TypeShare.EMAIL) {
				return session.sendMails(infoShare, name, TypeExport.CUBE);
			}
			else {
				throw new ServiceException("This type of share is not supported yet.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to generate a Free Web Report.", e);
		}
	}

	private InputStream generateFreeWebReport(String txtTitle, String txtDescription, String format, HashMap<String, String> margins, String orientation, String pageSize, String gridHtml, String chartSVG) throws Exception {
		FaWebSession session = getSession();
		format = format.toLowerCase();

		/*--------- We create the report ----------*/
		FWRReport report = new FWRReport();

		TextHTMLComponent title = new TextHTMLComponent("<div style=\"text-align: center; font-size: 14px;\">" + txtTitle + "</div>");
		report.addComponent(title);

		TextHTMLComponent description = new TextHTMLComponent("<div style=\"margin-top: 20px; margin-bottom: 10px; margin-left: 5px; font-size: 12px;\">" + txtDescription + "</div>");
		report.addComponent(description);

		StringBuilder buffer = new StringBuilder();
		buffer.append(createGridCss());
		buffer.append(gridHtml);

		TextHTMLComponent labelComponent = new TextHTMLComponent(buffer.toString());
		report.addComponent(labelComponent);

		if (chartSVG != null) {
			ImageComponent imageComponent = new ImageComponent("img_" + new Object().hashCode(), "", "SVG", chartSVG);
			report.addComponent(imageComponent);
		}

		report.setOrientation(orientation.equalsIgnoreCase("landscape") ? Orientation.LANDSCAPE : Orientation.PORTRAIT);
		report.setOutput(format);
		report.setTitle(new HashMap<String, String>());
		report.setSubtitle(new HashMap<String, String>());
		report.setMargins(margins);
		report.setPageSize(pageSize);
		report.setSaveOptions(new SaveOptions());

		DefaultTemplate template = new DefaultTemplate();
		report.setTitleStyle(template.getTitleStyle());
		report.setSubTitleStyle(template.getSubTitleStyle());
		ObjectIdentifier objectId = new ObjectIdentifier(session.getCurrentRepository().getId(), -1);

		XStream xstream = new XStream();
		String reportXML = xstream.toXML(report);
		InputStream in = IOUtils.toInputStream(reportXML, "UTF-8");

		ServerReportHelper serverReport = new ServerReportHelper(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

		return serverReport.runReport(format, in, session.getCurrentGroup().getId(), objectId, session.getUser());
	}

	@Override
	public InfosReport propertiesService(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		try {
			boolean show = !infos.getCube().getShowProperties();
			infos.getCube().setshowProperties(show);
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public List<ItemOlapMember> addChildsService(int keySession, ItemDim itemdim) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		return RecupDim.addChilds(keySession, itemdim, infos.getCube(), session);
	}

	@Override
	public String mdxService(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		try {
			return cube.getQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ExportResult drillThroughExportXls(InfoShareCube infoShare) throws ServiceException {
		FaWebSession session = getSession();

		String name = infoShare.getTitle();
		if (name != null) {
			name = name.replaceAll("[^\\p{L}\\p{Nd}]+", "");
		}

		DrillInformations drillInfo = infoShare.getDrillInfo();
		List<List<String>> drillValues = session.getDrill(drillInfo.getKey());
		// We put the columns names
		// drillValues.add(0, drillInfo.getColumns());

		ByteArrayInputStream byteArrayIs = null;
		if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_XLS)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			WritableWorkbook workbook = null;
			try {
				workbook = Workbook.createWorkbook(os);
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

			for (int i = 0; i < drillInfo.getColumns().size(); i++) {
				Label l = new Label(i, 0, drillInfo.getColumns().get(i));
				l.setCellFormat(formatHeader);
				try {
					sheet.addCell(l);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}

			for (int i = 0; i < drillValues.size(); i++) {
				for (int j = 0; j < drillValues.get(i).size(); j++) {
					Label l = new Label(j, i + 1, drillValues.get(i).get(j));
					/*
					 * if (i == 0) { l.setCellFormat(formatHeader); }
					 * 
					 * else { l.setCellFormat(formatCell); }
					 */
					l.setCellFormat(formatCell);
					try {
						sheet.addCell(l);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				sheet.setColumnView(i, 20);
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

			byteArrayIs = new ByteArrayInputStream(os.toByteArray());
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_CSV)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			StringBuffer colnames = new StringBuffer();
			for (int i = 0; i < drillInfo.getColumns().size(); i++) {
				if (i != 0) {
					colnames.append(infoShare.getSeparator());
				}
				colnames.append(drillInfo.getColumns().get(i));
				if (i == drillInfo.getColumns().size() - 1) {
					colnames.append("\n");
				}
			}
			try {
				os.write(colnames.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < drillValues.size(); i++) {

				StringBuffer line = new StringBuffer();

				for (int j = 0; j < drillValues.get(i).size(); j++) {
					if (j != 0) {
						line.append(infoShare.getSeparator());
					}
					line.append(drillValues.get(i).get(j));
					if (j == drillValues.get(i).size() - 1) {
						line.append("\n");
					}
				}

				try {
					os.write(line.toString().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byteArrayIs = new ByteArrayInputStream(os.toByteArray());
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (infoShare.getFormat().equalsIgnoreCase(CommonConstants.FORMAT_WEKA)) {
			Instances data;
			FastVector atts = new FastVector();
			List<String> types = defineAttributes(drillInfo, drillValues, atts);
			data = new Instances(name, atts, 0);

			addAttributeValue(data, drillValues, types);

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

			byteArrayIs = new ByteArrayInputStream(out.toByteArray());
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// session.addStream(name, infoShare.getFormat(), byteArrayIs);
			// return name;
		}

		if (byteArrayIs == null) {
			throw new ServiceException("Unable to create the requested document.");
		}

		ObjectInputStream reportsStream = new ObjectInputStream();
		reportsStream.addStream(infoShare.getFormat(), byteArrayIs);

		session.addReport(name, reportsStream);

		if (infoShare.getTypeShare() == TypeShare.EXPORT) {
			return new ExportResult(name);
		}
		else if (infoShare.getTypeShare() == TypeShare.EMAIL) {
			return session.sendMails(infoShare, name, TypeExport.DRILLTHROUGH);
		}
		else {
			throw new ServiceException("This type of share is not supported yet.");
		}
	}

	private List<String> defineAttributes(DrillInformations drillInfo, List<List<String>> drillValues, FastVector atts) {
		// FastVector atts = new FastVector();
		List<String> attrTyrpe = new ArrayList<String>();
		List<String> columnNames = drillInfo.getColumns();
		for (int i = 0; i < columnNames.size(); i++) {
			String colName = columnNames.get(i);

			String value = drillValues.get(1).get(i);

			if (tryParseDouble(value)) {
				atts.addElement(new Attribute(colName));
				attrTyrpe.add("NUMERIC");
			}
			else {
				atts.addElement(new Attribute(colName, (FastVector) null));
				attrTyrpe.add("STRING");
			}

		}
		return attrTyrpe;
	}

	/*
	 * 
	 * case "java.lang.String": atts.addElement(new Attribute(colName,
	 * (FastVector) null)); attrTyrpe.add("STRING"); break;
	 * 
	 * case "java.lang.Double": atts.addElement(new Attribute(colName));
	 * attrTyrpe.add("NUMERIC"); break;
	 */

	private void addAttributeValue(Instances data, List<List<String>> drillValues, List<String> types) {
		for (int i = 1; i < drillValues.size(); i++) {
			List<String> row = drillValues.get(i);
			double[] vals = new double[data.numAttributes()];

			for (int j = 0; j < row.size(); j++) {
				String type = types.get(j);
				String value = row.get(j);

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
				/*
				 * case "DATE": try{ vals[j] =
				 * data.attribute(j).parseDate(value); }catch(Exception e){
				 * vals[j] = 0; } break;
				 */
				default:
					vals[j] = 0;
					break;
				}
			}
			Instance inst = new Instance(1.0, vals);
			data.add(inst);
		}
	}

	boolean tryParseDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/*
	 * boolean tryParseInt(String value) { try { Integer.parseInt(value); return
	 * true; } catch(NumberFormatException nfe) { return false; } }
	 */

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
	public InfosReport addMultiple(int keySession, List<String> rows, List<String> cols, List<String> filters) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);

		List<ItemElement> elements = new ArrayList<ItemElement>();

		List<String> beforeRows = new ArrayList<String>();
		List<String> beforeCols = new ArrayList<String>();
		List<String> beforeWhere = new ArrayList<String>();

		beforeRows.addAll(infos.getCube().getMdx().getRows());
		beforeCols.addAll(infos.getCube().getMdx().getCols());
		beforeWhere.addAll(infos.getCube().getMdx().getWhere());

		infos.getCube().getMdx().getCols().clear();
		infos.getCube().getMdx().getRows().clear();
		infos.getCube().getMdx().getWhere().clear();

		for (String row : rows) {
			ItemElement e = new ItemElement(row, false);
			infos.getCube().getMdx().addrow(e.getDataMember().getUniqueName());
			elements.add(e);
		}

		for (String col : cols) {
			ItemElement e = new ItemElement(col, true);
			infos.getCube().getMdx().addcol(e.getDataMember().getUniqueName());
			elements.add(e);
		}

		if (filters.size() > 0) {
			try {
				infos.setInfosReport(filterService(keySession, filters));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		try {
			infos.setRes(infos.getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().getMdx().getCols().clear();
			infos.getCube().getMdx().getRows().clear();
			infos.getCube().getMdx().getWhere().clear();
			for (String row : beforeRows) {
				ItemElement ie = new ItemElement(row, false);
				infos.getCube().getMdx().addrow(ie.getDataMember().getUniqueName());
				elements.add(ie);
			}
			for (String col : beforeCols) {
				ItemElement ie = new ItemElement(col, true);
				infos.getCube().getMdx().addcol(ie.getDataMember().getUniqueName());
				elements.add(ie);
			}
			if (beforeWhere.size() > 0) {
				try {
					infos.setInfosReport(filterService(keySession, beforeWhere));
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		}
		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport addTopx(int keySession, String element, String target, int count) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		try {
			boolean exist = false;
			Topx topX = null;
			for (Topx t : cube.getTopx()) {
				if (t.getElementName().equals(element)) {
					exist = true;
					topX = t;
					break;
				}
			}
			if (exist) {
				cube.removeTopx(topX);
			}
			cube.addTopx(new Topx(element, target, count, true));

			infos.setRes((cube).doQuery());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport removeTopx(int keySession, String element) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		Topx top = null;
		for (Topx t : cube.getTopx()) {
			if (element.equals(t.getElementName())) {
				top = t;
				break;
			}
		}

		try {
			cube.removeTopx(top);
			infos.setRes(cube.doQuery());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport addPersonalName(int keySession, String uname, String pname) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		cube.getPersonalNames().put(uname, pname);
		return infos.getInfosReport();
	}

	@Override
	public InfosReport removePersonalName(int keySession, String uname) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		cube.getPersonalNames().remove(uname);
		return infos.getInfosReport();
	}

	@Override
	public InfosReport addPercentMeasures(int keySession, HashMap<String, Boolean> percentMeasures) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		cube.getPercentMeasures().clear();
		for (String perc : percentMeasures.keySet()) {
			cube.addPercentMeasure(perc, percentMeasures.get(perc));
		}

		try {
			infos.setRes(cube.doQuery());
		} catch (Exception e) {
			e.printStackTrace();

			infos.getCube().getLastResult();
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public InfosReport setShowTotals(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();

		cube.setShowTotals(!cube.isShowTotals());

		try {
			infos.setRes(cube.getLastResult());
		} catch (Exception e) {
			e.printStackTrace();
			infos.getCube().getLastResult();
			return null;
		}

		infos.getInfosReport().setGrid(getTable(infos));
		return infos.getInfosReport();
	}

	@Override
	public List<String> searchDimensions(int keySession, String word, String level) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPCube cube = infos.getCube();
		List<String> results = new ArrayList<String>();

		try {
			results = cube.searchOnDimensions(word, level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public List<IDirectoryDTO> getImagesForBackground() throws ServiceException {
		FaWebSession session = getSession();

		String[] allowedTypes = { IRepositoryApi.FORMAT_BMP, IRepositoryApi.FORMAT_GIF, IRepositoryApi.FORMAT_JPEG, IRepositoryApi.FORMAT_JPG, IRepositoryApi.FORMAT_PNG, IRepositoryApi.FORMAT_TIF };

		List<IDirectoryDTO> directories = new ArrayList<IDirectoryDTO>();
		int passage = 0;
		for (String modelType : allowedTypes) {
			try {

				IRepository rep = new Repository(session.getRepositoryConnection(), IRepositoryApi.EXTERNAL_DOCUMENT, -1, modelType);
				if (passage == 0) {
					for (RepositoryDirectory dir : rep.getRootDirectories()) {
						IDirectoryDTO dirDTO = new IDirectoryDTO();
						dirDTO.setId(dir.getId());
						dirDTO.setName(dir.getName());
						directories.add(dirDTO);
						getDirectoryChildren(dirDTO, dir, rep);
					}
				}
				else {
					for (RepositoryDirectory dir : rep.getRootDirectories()) {
						for (IDirectoryDTO dirDTO : directories) {
							if (dirDTO.getId() == dir.getId()) {
								getExistingDirectoryChildren(dirDTO, dir, rep);
							}
						}
					}
				}
				passage++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return directories;
	}

	private void getExistingDirectoryChildren(IDirectoryDTO dirDTO, RepositoryDirectory dir, IRepository rep) {
		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (RepositoryDirectory subdir : dirs) {
			for (Object obj : dirDTO.getChildren()) {
				if (obj instanceof IDirectoryDTO) {
					if (((IDirectoryDTO) obj).getId() == subdir.getId()) {
						getExistingDirectoryChildren((IDirectoryDTO) obj, subdir, rep);
					}
				}
			}
		}

		List<RepositoryItem> items = null;
		try {
			items = rep.getItems(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (RepositoryItem item : items) {
			IDirectoryItemDTO itemDTO = new IDirectoryItemDTO();
			itemDTO.setDirectoryId(dir.getId());
			itemDTO.setId(item.getId());
			itemDTO.setName(item.getItemName());
			dirDTO.addChild(itemDTO);
		}
	}

	private List<RepositoryDirectory> getDirectoryChildren(IDirectoryDTO dirDTO, RepositoryDirectory dir, IRepository rep) {
		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (RepositoryDirectory subdir : dirs) {
			IDirectoryDTO subdirDTO = new IDirectoryDTO();
			subdirDTO.setId(subdir.getId());
			subdirDTO.setName(subdir.getName());
			getDirectoryChildren(subdirDTO, subdir, rep);
			dirDTO.addChild(subdirDTO);
		}

		List<RepositoryItem> items = null;
		try {
			items = rep.getItems(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (RepositoryItem item : items) {
			IDirectoryItemDTO itemDTO = new IDirectoryItemDTO();
			itemDTO.setDirectoryId(dir.getId());
			itemDTO.setId(item.getId());
			itemDTO.setName(item.getItemName());
			dirDTO.addChild(itemDTO);
		}

		return null;
	}

	@Override
	public List<ItemDim> getReporterSubItems(int keySession, String uname, String hiera) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap olap = session.getInfosOlap(keySession);
		HashMap<String, String> childs = null;
		try {
			childs = olap.getCube().findChildsForReporter(uname);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<ItemDim> results = new ArrayList<ItemDim>();
		for (String child : childs.keySet()) {
			String[] s = child.split("\\.");
			String name = s[s.length - 1].replace("[", "");
			name = name.replace("]", "");
			name = name.replace("&amp;", "");
			ItemDim dim = new ItemDim(name, child, childs.get(child), false);
			results.add(dim);
		}

		return results;
	}

	@Override
	public void setReportTitle(int keySession, String reportTitle) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap olap = session.getInfosOlap(keySession);
		olap.getCube().setReportTitle(reportTitle);
	}

	@Override
	public void addViewParameters(int keySession, List<ParameterDTO> parameters) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap olap = session.getInfosOlap(keySession);
		olap.getCube().getParameters().clear();

		for (ParameterDTO paramDTO : parameters) {
			olap.getCube().addParameter(paramDTO.getName(), paramDTO.getValue(), paramDTO.getLevel());
		}
	}

	@Override
	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap olap = session.getInfosOlap(keySession);
		return olap.getCube().getLevels();
	}

	@Override
	public HashMap<ParameterDTO, List<String>> getParametersForView(int keySession, String viewName, String cubeName, int fasdId, boolean fromPortal) throws ServiceException {
		FaWebSession session = getSession();

		HashMap<ParameterDTO, List<String>> parameters = new HashMap<ParameterDTO, List<String>>();
		try {
			RepositoryItem selectedView = null;
			if (fromPortal) {
				session.setCubeViews(keySession, session.getRepositoryConnection().getRepositoryService().getCubeViews(cubeName, session.getRepositoryConnection().getRepositoryService().getDirectoryItem(fasdId)));

				if (session.getRepository() == null) {
					session.initRepository(IRepositoryApi.FASD_TYPE);
				}
				FaWebActions.writeRepository(keySession, session.getCurrentGroup(), fasdId, session, session.getLocation(), session.getInfosOlap(keySession));
				FaWebActions.setCube(keySession, cubeName, session, session.getInfosOlap(keySession), "", "");
			}

			for (RepositoryItem view : session.getCubeViews(keySession)) {
				if (viewName.equalsIgnoreCase(view.getItemName())) {
					selectedView = view;
					break;
				}
			}

			String fav = session.getRepositoryConnection().getRepositoryService().loadModel(selectedView);

			Document document = DocumentHelper.parseText(fav);
			Element root = document.getRootElement();

			String view = root.element("view").asXML();

			DigesterCubeView dig = new DigesterCubeView(view);
			RepositoryCubeView repcubeview = dig.getCubeView();

			for (Parameter param : repcubeview.getParameters()) {
				ParameterDTO p = new ParameterDTO();
				p.setName(param.getName());
				p.setLevel(param.getLevel());
				parameters.put(p, session.getInfosOlap(keySession).getCube().getParametersValues(param.getLevel()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameters;
	}

	private BufferedImage resize(BufferedImage image, int width, int height) {

		double scalex = (double) width / image.getWidth();
		double scaley = (double) height / image.getHeight();
		double sc = Math.min(scalex, scaley);

		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Image img = image.getScaledInstance((int) (image.getWidth() * sc), (int) (image.getHeight() * sc), Image.SCALE_SMOOTH);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(img, 0, 0, null);

		// center the image
		BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform tra = null;

		if (scalex < scaley) {
			double tr = image.getHeight() * sc;
			double traaa = (height - tr) / 2;
			if (tr != 0) {
				tra = AffineTransform.getTranslateInstance(0, traaa);
				AffineTransformOp opLeft = new AffineTransformOp(tra, AffineTransformOp.TYPE_BILINEAR);
				resizedImage = opLeft.filter(resizedImage, img2);
			}
		}
		else {
			double tr = image.getWidth() * sc;
			double traaa = (width - tr) / 2;
			if (tr != 0) {
				tra = AffineTransform.getTranslateInstance(traaa, 0);
				AffineTransformOp opLeft = new AffineTransformOp(tra, AffineTransformOp.TYPE_BILINEAR);
				resizedImage = opLeft.filter(resizedImage, img2);
			}
		}

		return resizedImage;
	}

	@Override
	public void createSnapshot(int keySession, String name, ChartParameters chartParams, List<Calcul> calculs, String gridHtml, InfosReport infosReport, MapOptions mapOptions) throws ServiceException {
		FaWebSession session = getSession();

		String viewImg = "";
		try {
			viewImg = createImageUsingFWR("html", name, session.getInfosOlap(keySession), gridHtml);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage());
		}

		String cubeView = createCubeViewXml(session.getInfosOlap(keySession), calculs, chartParams, infosReport, mapOptions);
		cubeView += "<rolodeximage>" + viewImg + "</rolodeximage>\n";
		cubeView += "</view>\n";

		cubeView = "<fav><name>" + name + "</name><cubename>" + session.getInfosOlap(keySession).getCubeName() + "</cubename><fasdid>" + session.getInfosOlap(keySession).getSelectedFasd().getId() + "</fasdid>" + cubeView + "</fav>";

		String vanillaFilePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		String filepath = vanillaFilePath + File.separator + "FreeAnalysisWeb" + File.separator;

		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdir();
		}

		File viewFile = new File(filepath + name + ".xml");
		try {
			FileWriter writer = new FileWriter(viewFile);
			writer.write(cubeView);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Cookie cookie = null;
		try {
			String filename = viewFile.getCanonicalPath();
			filename = filename.replace("\\", "\\\\");
			cookie = new Cookie("cubeview" + session.getInfosOlap(keySession).getCubeName() + name, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cookie.setMaxAge(60 * 60 * 24 * 3);
		cookie.setPath("/");
		getThreadLocalResponse().addCookie(cookie);
	}

	@Override
	public List<ItemView> getSnapshots(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		Cookie[] cookies = getThreadLocalRequest().getCookies();
		List<ItemView> cubeViews = new ArrayList<ItemView>();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cook : cookies) {
				if (cook.getName().contains("cubeview") && cook.getName().contains(session.getInfosOlap(keySession).getCubeName())) {
					ItemView view = new ItemView();
					if (cook.getValue() != null && cook.getValue().equals("removed")) {
						continue;
					}
					try {
						File file = new File(cook.getValue());
						FileReader reader = new FileReader(file);
						BufferedReader buf = new BufferedReader(reader);
						String line = "";
						String xml = "";
						while ((line = buf.readLine()) != null) {
							xml += line;
						}

						buf.close();

						view.setName(cook.getName().substring(("cubeview" + session.getInfosOlap(keySession).getCubeName()).length()));
						view.setXml(xml);
						view.setSnapshot(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					cubeViews.add(view);
				}
			}
		}
		return cubeViews;
	}

	@Override
	public String saveSnapshots(int keySession, List<ItemView> views) {
		try {
			String result = "Snapshots saved with success";
			FaWebSession session = getSession();

			InfosOlap infos = session.getInfosOlap(keySession);

			IRepositoryApi sock = session.getRepositoryConnection();

			RepositoryDirectory target = session.getRepository().getDirectory(infos.getSelectedFasd().getDirectoryId());

			for (ItemView view : views) {
				try {
					sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FAV_TYPE, -1, target, view.getName(), "", "", "", view.getXml(), true);
				} catch (Exception e) {
					result = e.getMessage();
					e.printStackTrace();
					logger.error("An error occured while saving the snapshot", e);
				}
			}
			try {
				removeSnapShots(keySession, views);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("An error occured while saving the snapshot", e);
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occured while saving the snapshot", e);
		}
		return "An error occured while saving the snapshot";
	}

	private void removeSnapShots(int keySession, List<ItemView> views) throws ServiceException {
		FaWebSession session = getSession();
		Cookie[] cookies = getThreadLocalRequest().getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cook : cookies) {
				if (cook.getName().contains("cubeview") && cook.getName().contains(session.getInfosOlap(keySession).getCubeName())) {
					ItemView view = new ItemView();

					try {
						File file = new File(cook.getValue());
						FileReader reader = new FileReader(file);
						BufferedReader buf = new BufferedReader(reader);
						String line = "";
						String xml = "";
						while ((line = buf.readLine()) != null) {
							xml += line;
						}

						buf.close();

						view.setName(cook.getName().substring(("cubeview" + session.getInfosOlap(keySession).getCubeName()).length()));
						view.setXml(xml);
						view.setSnapshot(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					for (ItemView v : views) {
						if (view.getName().equals(v.getName())) {
							cook.setMaxAge(0);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public String createDashboard(int keySession, List<ItemView> views) throws ServiceException {
		FaWebSession session = getSession();
		FdProject project = dashboardCreation(keySession, views);
		session.setFdProject(keySession, project);

		return "success";
	}

	private FdProject dashboardCreation(int keySession, List<ItemView> views) throws ServiceException {
		FaWebSession session = getSession();

		FdProjectDescriptor desc = new FdProjectDescriptor();
		desc.setProjectName("FaWebProject" + new Object().hashCode());
		FdProject project = new MultiPageFdProject(desc);

		project.addResource(new FileCSS("styleCss", new File(session.getLocation() + File.separator + "resources" + File.separator + "multi_page.css")));

		Dictionary dico = new Dictionary();
		dico.setName("dictionnaryFromFaWeb_" + session.getCurrentGroup().getName());

		FdModel model = project.getFdModel();
		createViewComponents(keySession, views, dico, model, project, session);

		project.setDictionary(dico);
		return project;
	}

	private void createViewComponents(int keySession, List<ItemView> views, Dictionary dico, FdModel model, FdProject project, FaWebSession session) {
		Folder folder = model.getStructureFactory().createFolder("folder");

		folder.setCssClass("menu");
		Properties propertiesFile = new Properties();
		for (ItemView view : views) {
			// create the component
			ComponentFaView compFa = new ComponentFaView(view.getName() + "_" + new Object().hashCode(), dico);
			compFa.setDirectoryItemId(view.getDirectoryItemId());
			try {
				dico.addComponent(compFa);
			} catch (DictionaryException e) {
				e.printStackTrace();
			}

			// create the folderpage
			FolderPage page = model.getStructureFactory().createFolderPage(view.getName());

			FdModel subModel = new FdModel(project.getFdModel().getStructureFactory());
			subModel.setName("model_" + view.getName());

			try {
				((MultiPageFdProject) project).addPageModel(subModel);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create the table
			Table table = model.getStructureFactory().createTable("table" + view.getName());

			// get the parameters
			LinkedHashMap<ComponentParameter, ComponentFilterDefinition> hashParams = new LinkedHashMap<ComponentParameter, ComponentFilterDefinition>();
			List<ComponentParameter> fdParams = new ArrayList<ComponentParameter>();
			int i = 1;

			RepositoryItem actual = null;
			for (RepositoryItem cubV : session.getCubeViews(keySession)) {
				if (cubV.getItemName().equals(view.getName())) {
					actual = cubV;
					break;
				}
			}

			RepositoryCubeView cubeView = null;
			try {
				String xml = session.getRepositoryConnection().getRepositoryService().loadModel(actual);

				Document document = DocumentHelper.parseText(xml);
				Element root = document.getRootElement();

				xml = root.element("view").asXML();

				DigesterCubeView dig = new DigesterCubeView(xml);
				cubeView = dig.getCubeView();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (cubeView.getRowCount() != 0) {
				ReportOptions opt = new ReportOptions();
				opt.setHeight(cubeView.getRowCount() * 22 + 37);
				opt.setWidth(cubeView.getColCount() * 81 + 36);
				compFa.getOptions().clear();
				compFa.getOptions().add(opt);
			}

			for (Parameter param : cubeView.getParameters()) {

				// create the datasource and the dataset
				Properties datasourceProps = new Properties();
				datasourceProps.setProperty("P_COLUMN_TYPE", "String;");

				List<String> values = session.getInfosOlap(keySession).getCube().getParametersValues(param.getLevel());
				String colVal = "";
				for (String val : values) {
					colVal += val + ";";
				}

				datasourceProps.setProperty("P_COLUMN_VALUES", colVal);
				datasourceProps.setProperty("P_COLUMN_NAMES", param.getLevel() + ";");
				datasourceProps.setProperty("P_COLUMN_NUMBER", "1");
				DataSource datasource = new DataSource(dico, param.getName() + "_datasource", "bpm.inlinedatas.oda.driver.runtime", "bpm.inlinedatas.oda.driver.runtime", datasourceProps, new Properties());

				try {
					dico.addDataSource(datasource);
				} catch (DictionaryException e2) {
					e2.printStackTrace();
				}

				String queryText = param.getLevel() + ";}++No filter++}";
				DataSet dataset = new DataSet(param.getName() + "dataset", "bpm.inlinedatas.oda.driver.runtime.dataSet", "bpm.inlinedatas.oda.driver.runtime", new Properties(), new Properties(), queryText, datasource);
				try {
					dataset.buildDescriptor(datasource);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					dico.addDataSet(dataset);
				} catch (DictionaryException e1) {
					e1.printStackTrace();
				}

				FilterData datas = new FilterData();
				datas.setDataSet(dataset);
				datas.setColumnLabelIndex(1);
				datas.setColumnOrderIndex(1);
				datas.setColumnValueIndex(1);

				// create the filter
				ComponentFilterDefinition filter = new ComponentFilterDefinition(param.getName(), dico);
				filter.setComponentDatas(datas);
				try {
					filter.setRenderer(FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX));
					dico.addComponent(filter);
				} catch (DictionaryException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// create the parameter
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), i);
				fdParams.add(p);
				i++;
				hashParams.put(p, filter);
			}

			if (fdParams.size() > 0) {
				compFa.defineParameter(fdParams);
			}

			for (ComponentParameter def : hashParams.keySet()) {

				LabelComponent label = new LabelComponent("label_" + def.getName(), dico);
				LabelOptions opt = new LabelOptions();
				opt.setText(def.getName());

				propertiesFile.put("label_" + def.getName() + ".text", def.getName());

				try {
					dico.addComponent(label);
				} catch (DictionaryException e) {
					e.printStackTrace();
				}

				Cell cellLbl = model.getStructureFactory().createCell("cell" + def.getName() + "_label", 1, 1);
				List<Cell> cellsLbl = new ArrayList<Cell>();
				cellsLbl.add(cellLbl);
				table.addDetailsRow(cellsLbl);
				cellLbl.addBaseElementToContent(label);

				Cell cell = model.getStructureFactory().createCell("cell" + def.getName(), 1, 1);
				List<Cell> cells = new ArrayList<Cell>();
				cells.add(cell);
				table.addDetailsRow(cells);
				cell.addBaseElementToContent(hashParams.get(def));
			}

			Cell cell = model.getStructureFactory().createCell("cell" + view.getName(), 1, 1);
			List<Cell> cells = new ArrayList<Cell>();
			cells.add(cell);
			table.addDetailsRow(cells);
			cell.addBaseElementToContent(compFa);
			for (ComponentParameter def : hashParams.keySet()) {
				cell.getConfig(compFa).setParameterOrigin(def, hashParams.get(def).getName());
			}

			// add all to the page
			subModel.addToContent(table);
			page.addToContent(subModel);
			folder.addToContent(page);

		}

		File file = new File(getServletContext().getRealPath(File.separator) + "/temp/components.properties");
		try {
			propertiesFile.store(new FileWriter(file), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileProperties props = new FileProperties("components.properties", Locale.getDefault().getDisplayName(), file);
		project.addResource(props);

		model.addToContent(folder);
	}

	@Override
	public String saveDashboard(int keySession, List<ItemView> views, String name, String group, String comment, String publicVersion, String internalVersion, int dirId) throws Exception {
		FaWebSession session = getSession();
		FdProject project = dashboardCreation(keySession, views);
		ModelLoader.save(project, session.getRepositoryConnection(), session.getRepository().getDirectory(dirId), group, name, project.getDictionary().getName());

		return "success";
	}

	@Override
	public MapInfo getMapInfo(String uname, String elementName) throws ServiceException {
		MapInfo results = new MapInfo();
		try {

			FaWebSession session = getSession();
			InfosOlap infos = session.getInfosOlap();
			for (String measure : infos.getInfosReport().getMeasuresDisplay()) {
				results.addMeasure((String) measure);
			}

			for (List<Item> raws : infos.getRes().getRaw()) {
				for (Item item : raws) {
					if (item instanceof ItemElement) {
						ItemElement element = (ItemElement) item;
						if (!results.getMeasures().contains(element.getDataMember().getName()) && !element.getDataMember().getUniqueName().contains(uname) && !results.getDimensions().contains(element.getDataMember().getName())) {
							results.addDimension(element.getDataMember().getUniqueName());
						}
					}
				}
			}

			String vanillaRuntimeUrl = session.getVanillaRuntimeUrl();
			IMapDefinitionService mapDefinitionService = null;

			try {
				mapDefinitionService = new RemoteMapDefinitionService();
				mapDefinitionService.configure(vanillaRuntimeUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (MapVanilla map : mapDefinitionService.getAllMapsVanilla()) {
				results.addMap(map);
			}

			return results;
		} catch (Throwable e) {
			e.printStackTrace();
			return results;
		}
	}

//	private List<ColorRange> buildColor(List<List<String>> colors) {
//		List<ColorRange> colorRanges = new ArrayList<ColorRange>();
//		for (List<String> list : colors) {
//			String name = list.get(0);
//			String hexa = list.get(1);
//			int min = Integer.parseInt(list.get(2));
//			int max = Integer.parseInt(list.get(3));
//			colorRanges.add(new ColorRange(name, hexa, min, max));
//		}
//		return colorRanges;
//	}

	/**
	 * 
	 * This method build the Javascript for the FusionMap It browse the cube and
	 * the select the good data for the selected measure and dimension
	 * 
	 * @param selectedMeasure
	 * @param selectedDimension
	 * @param res
	 * @param mapDefinitionService
	 * @param fusionMapObject
	 * @param uname
	 * @return
	 */
//	private String buildMapData(String selectedMeasure, String selectedDimension, OLAPResult res, IMapDefinitionService mapDefinitionService, IFusionMapObject fusionMapObject, List<String> measureDisplay, List<ColorRange> colors, String uname) {
//
//		// We get the cube result
//		List<ArrayList<Item>> itemRaws = res.getRaw();
//
//		// We use that to check if our selected dimension is in row or in column
//		boolean geolocDimensionIsCol = false;
//		boolean geolocDimensionIsRow = false;
//
//		boolean selectedDimensionIsRow = false;
//		boolean selectedDimensionIsCol = false;
//		int indexSelectedDimension = -1;
//		int indexSelectedDimensionMin = -1;
//		int indexSelectedDimensionMax = -1;
//		List<Integer> listIndexDimension = new ArrayList<Integer>();
//
//		boolean selectedMeasureIsRow = false;
//		boolean selectedMeasureIsCol = false;
//		int indexSelectedMeasure = -1;
//		int indexSelectedMeasureMin = -1;
//		int indexSelectedMeasureMax = -1;
//		List<Integer> listIndexMeasure = new ArrayList<Integer>();
//
//		int lineIndex = 0;
//		for (ArrayList<Item> items : itemRaws) {
//			int columnIndex = 0;
//			for (Item item : items) {
//				if (item instanceof ItemElement) {
//					ItemElement element = (ItemElement) item;
//					if (element.getDataMember().getUniqueName().equals(uname)) {
//						if (element.isCol()) {
//							geolocDimensionIsCol = true;
//						}
//						else {
//							geolocDimensionIsRow = true;
//						}
//					}
//					else if (element.getDataMember().getName().equals(selectedDimension)) {
//						if (element.isCol()) {
//							if (indexSelectedMeasureMin == -1) {
//								selectedDimensionIsCol = true;
//								if (indexSelectedDimensionMin == -1) {
//									indexSelectedDimensionMin = columnIndex;
//								}
//								indexSelectedDimensionMax = columnIndex;
//								listIndexDimension.add(columnIndex);
//							}
//							else if (columnIndex >= indexSelectedMeasureMin && columnIndex <= indexSelectedMeasureMax) {
//								indexSelectedDimension = columnIndex;
//								indexSelectedMeasure = columnIndex;
//								selectedDimensionIsCol = true;
//							}
//						}
//						else {
//							selectedDimensionIsRow = true;
//						}
//					}
//					else if (element.getDataMember().getName().equals(selectedMeasure)) {
//						if (element.isCol()) {
//							selectedMeasureIsCol = true;
//							if (lineIndex == 0) {
//								if (indexSelectedDimensionMin == -1) {
//									if (indexSelectedMeasureMin == -1) {
//										indexSelectedMeasureMin = columnIndex;
//									}
//									indexSelectedMeasureMax = columnIndex;
//									listIndexMeasure.add(columnIndex);
//								}
//								else if (columnIndex >= indexSelectedDimensionMin && columnIndex <= indexSelectedDimensionMax) {
//									indexSelectedMeasure = columnIndex;
//									indexSelectedDimension = columnIndex;
//								}
//							}
//							else {
//								listIndexMeasure.add(columnIndex);
//							}
//						}
//						else {
//							selectedMeasureIsRow = true;
//						}
//					}
//				}
//				columnIndex++;
//			}
//			lineIndex++;
//		}
//
//		if (indexSelectedDimensionMax == indexSelectedDimensionMin && indexSelectedDimensionMax != -1) {
//			indexSelectedDimension = indexSelectedDimensionMax;
//		}
//		if (indexSelectedMeasureMax == indexSelectedMeasureMin && indexSelectedMeasureMax != -1) {
//			indexSelectedMeasure = indexSelectedMeasureMax;
//		}
//
//		if (geolocDimensionIsRow) {
//			return buildWithDimensionInRow(uname, selectedDimension, selectedDimensionIsCol, selectedDimensionIsRow, indexSelectedDimension, indexSelectedMeasure, selectedMeasure, selectedMeasureIsCol, selectedMeasureIsRow, itemRaws, colors, mapDefinitionService, fusionMapObject, measureDisplay);
//		}
//		else if (geolocDimensionIsCol) {
//			return buildWithDimensionInCol(uname, selectedDimension, selectedDimensionIsCol, selectedDimensionIsRow, indexSelectedDimension, listIndexDimension, indexSelectedMeasure, listIndexMeasure, selectedMeasure, selectedMeasureIsCol, selectedMeasureIsRow, itemRaws, colors, mapDefinitionService, fusionMapObject, measureDisplay);
//		}
//		return null;
//	}

//	private String buildWithDimensionInCol(String uname, String selectedDimension, boolean selectedDimensionIsCol, boolean selectedDimensionIsRow, int indexSelectedDimension, List<Integer> listIndexDimension, int indexSelectedMeasure, List<Integer> listIndexMeasure, String selectedMeasure, boolean selectedMeasureIsCol, boolean selectedMeasureIsRow, List<ArrayList<Item>> itemRaws, List<ColorRange> colors, IMapDefinitionService mapDefinitionService, IFusionMapObject fusionMapObject, List<String> measureDisplay) {
//
//		FusionMapXmlBuilder builder = new FusionMapXmlBuilder("", colors, true);
//
//		// We use that to keep the dimension we want to put on a map and the
//		// zone associated
//		// We use that if the selectedDimension is in row
//		boolean dimensionToKeep = false;
//		boolean measureToKeep = false;
//
//		// We use that if the selectedDimension is in column
//		List<IFusionMapSpecificationEntity> entitiesToKeep = new ArrayList<IFusionMapSpecificationEntity>();
//		List<Integer> indexOfEntities = new ArrayList<Integer>();
//
//		String labelTemp = null;
//
//		HashMap<String, Entity> entitiesValues = new HashMap<String, Entity>();
//
//		for (ArrayList<Item> raw : itemRaws) {
//			int indexColumn = 0;
//			dimensionToKeep = false;
//			measureToKeep = false;
//			for (Item item : raw) {
//				if (item instanceof ItemElement) {
//					ItemElement element = (ItemElement) item;
//
//					if (!element.getDataMember().getUniqueName().equals(uname)) {
//						if (element.getDataMember().getUniqueName().contains(uname)) {
//							List<IFusionMapSpecificationEntity> entities = new ArrayList<IFusionMapSpecificationEntity>();
//							String label = "";
//							int index = 0;
//							if (element.getDataMember() != null && element.getDataMember().getPropertiesName() != null) {
//								for (String prop : element.getDataMember().getPropertiesName()) {
//									if (prop.equals("geolocalizableProperty")) {
//										label = element.getDataMember().getPropertiesValue()[index];
//										break;
//									}
//									index++;
//								}
//							}
//							if (!label.equals("")) {
//								labelTemp = label;
//								try {
//									entities = findZonesByLabel(mapDefinitionService, element.getDataMember().getName());
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//							for (IFusionMapSpecificationEntity entity : entities) {
//								if (entity.getFusionMapObjectId() == fusionMapObject.getId()) {
//									boolean found = false;
//									for (IFusionMapSpecificationEntity entityTemp : entitiesToKeep) {
//										if (entityTemp.getFusionMapInternalId().equals(entity.getFusionMapInternalId())) {
//											found = true;
//											break;
//										}
//									}
//									if (!found) {
//										entitiesToKeep.add(entity);
//										indexOfEntities.add(indexColumn);
//									}
//								}
//							}
//						}
//						else if (selectedDimension.equals(element.getDataMember().getName())) {
//							dimensionToKeep = true;
//						}
//						else if (selectedMeasure.equals(element.getDataMember().getName())) {
//							measureToKeep = true;
//						}
//					}
//					else {
//						break;
//					}
//				}
//				else if (item instanceof ItemValue) {
//					ItemValue itemValue = (ItemValue) item;
//
//					if (selectedDimensionIsCol && selectedMeasureIsCol) {
//						int index = indexOfEntities.indexOf(indexColumn);
//						if (index != -1 && listIndexMeasure.contains(indexColumn) && listIndexDimension.contains(indexColumn)) {
//							String valueToShow = itemValue.getValue();
//							if (valueToShow.equals("1.2345E-8")) {
//								valueToShow = "0";
//							}
//
//							// Link for the drillDown
//							String id = "id_" + entitiesToKeep.get(index).getId();
//							labels.put(id, labelTemp);
//							if (entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()) == null) {
//								Entity entity = new Entity(id, entitiesToKeep.get(index).getFusionMapInternalId(), valueToShow);
//								entitiesValues.put(entitiesToKeep.get(index).getFusionMapInternalId(), entity);
//							}
//							else {
//								entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()).addValue(valueToShow);
//							}
//						}
//					}
//					else if (selectedDimensionIsRow && selectedMeasureIsCol) {
//						if (dimensionToKeep) {
//							int index = indexOfEntities.indexOf(indexColumn);
//							if (index != -1 && listIndexMeasure.contains(indexColumn)) {
//								String valueToShow = itemValue.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//
//								// Link for the drillDown
//								String id = "id_" + entitiesToKeep.get(index).getId();
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, entitiesToKeep.get(index).getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(entitiesToKeep.get(index).getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()).addValue(valueToShow);
//								}
//							}
//						}
//					}
//					else if (selectedDimensionIsCol && selectedMeasureIsRow) {
//						if (measureToKeep) {
//							int index = indexOfEntities.indexOf(indexColumn);
//							if (index != -1 && listIndexDimension.contains(indexColumn)) {
//								String valueToShow = itemValue.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//
//								// Link for the drillDown
//								String id = "id_" + entitiesToKeep.get(index).getId();
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, entitiesToKeep.get(index).getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(entitiesToKeep.get(index).getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()).addValue(valueToShow);
//								}
//							}
//						}
//					}
//					else {
//						if (measureToKeep && dimensionToKeep) {
//							int index = indexOfEntities.indexOf(indexColumn);
//							if (index != -1) {
//								String valueToShow = itemValue.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//
//								// Link for the drillDown
//								String id = "id_" + entitiesToKeep.get(index).getId();
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, entitiesToKeep.get(index).getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(entitiesToKeep.get(index).getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(entitiesToKeep.get(index).getFusionMapInternalId()).addValue(valueToShow);
//								}
//							}
//						}
//					}
//				}
//				indexColumn++;
//			}
//		}
//
//		for (Entry<String, Entity> entry : entitiesValues.entrySet()) {
//			Entity entity = entry.getValue();
//			String linkDrillDown = buildLinkDrillDown(entity.getId());
//			builder.addEntity(entity.getInternalId(), entity.getValue(), linkDrillDown);
//		}
//
//		return builder.close();
//	}
//
//	private String buildWithDimensionInRow(String uname, String selectedDimension, boolean selectedDimensionIsCol, boolean selectedDimensionIsRow, int indexSelectedDimension, int indexSelectedMeasure, String selectedMeasure, boolean selectedMeasureIsCol, boolean selectedMeasureIsRow, List<ArrayList<Item>> itemRaws, List<ColorRange> colors, IMapDefinitionService mapDefinitionService, IFusionMapObject fusionMapObject, List<String> measureDisplay) {
//
//		FusionMapXmlBuilder builder = new FusionMapXmlBuilder("", colors, true);
//
//		// We use that to keep the dimension we want to put on a map and the
//		// zone associated
//		// We use that if the selectedDimension is in row
//		IFusionMapSpecificationEntity mapZoneToKeep = null;
//		boolean dimensionToKeep = false;
//		String dimensionToKeepName = "";
//
//		// We use that if the selectedDimension is in column
//		// List<IFusionMapSpecificationEntity> entitiesToKeep = new
//		// ArrayList<IFusionMapSpecificationEntity>();
//		// List<Integer> indexOfEntities = new ArrayList<Integer>();
//
//		String labelTemp = null;
//
//		HashMap<String, Entity> entitiesValues = new HashMap<String, Entity>();
//
//		for (ArrayList<Item> raw : itemRaws) {
//			int i = 0;
//			for (Item item : raw) {
//				if (mapZoneToKeep == null) {
//					if (item instanceof ItemElement) {
//						ItemElement element = (ItemElement) item;
//						if (element.getDataMember().getUniqueName().equals(uname)) {
//							break;
//						}
//						else if (element.getDataMember().getName().equals(selectedDimension)) {
//							if (selectedDimensionIsCol) {
//								break;
//							}
//						}
//						else if (element.getDataMember().getName().equals(selectedMeasure)) {
//							if (selectedMeasureIsCol) {
//								break;
//							}
//						}
//						else {
//							if (element.getDataMember().getUniqueName().contains(uname)) {
//								if (!element.getDataMember().getName().equals(dimensionToKeepName)) {
//									List<IFusionMapSpecificationEntity> entities = new ArrayList<IFusionMapSpecificationEntity>();
//									String label = "";
//									int index = 0;
//									if (element.getDataMember() != null && element.getDataMember().getPropertiesName() != null) {
//										for (String prop : element.getDataMember().getPropertiesName()) {
//											if (prop.equals("geolocalizableProperty")) {
//												label = element.getDataMember().getPropertiesValue()[index];
//												break;
//											}
//											index++;
//										}
//									}
//									if (!label.equals("")) {
//										labelTemp = label;
//										try {
//											entities = findZonesByLabel(mapDefinitionService, label);
//										} catch (Exception e) {
//											e.printStackTrace();
//										}
//									}
//									for (IFusionMapSpecificationEntity entity : entities) {
//										if (entity.getFusionMapObjectId() == fusionMapObject.getId()) {
//											mapZoneToKeep = entity;
//											dimensionToKeepName = element.getDataMember().getName();
//											break;
//										}
//									}
//									if (mapZoneToKeep == null) {
//										break;
//									}
//								}
//							}
//						}
//					}
//				}
//				else {
//					if (item instanceof ItemElement) {
//						ItemElement element = (ItemElement) item;
//
//						if (selectedDimensionIsCol && selectedMeasureIsCol) {
//							// Do Nothing
//						}
//						else if (selectedDimensionIsRow && selectedMeasureIsCol) {
//							if (element.getDataMember().getName().equals(selectedDimension)) {
//								dimensionToKeep = true;
//							}
//						}
//						else if (selectedDimensionIsCol && selectedMeasureIsRow) {
//							boolean found = false;
//							for (String measure : measureDisplay) {
//								if (element.getDataMember().getName().equals(measure)) {
//									found = true;
//									break;
//								}
//							}
//							if (found && !element.getDataMember().getName().equals(selectedMeasure)) {
//								break;
//							}
//						}
//						else {
//							boolean found = false;
//							for (String measure : measureDisplay) {
//								if (element.getDataMember().getName().equals(measure)) {
//									found = true;
//									break;
//								}
//							}
//							if (found && !element.getDataMember().getName().equals(selectedMeasure)) {
//								break;
//							}
//							if (element.getDataMember().getName().equals(selectedDimension)) {
//								dimensionToKeep = true;
//							}
//						}
//					}
//					else if (item instanceof ItemValue) {
//						ItemValue value = (ItemValue) item;
//						String id = "id_" + mapZoneToKeep.getId();
//						if (selectedDimensionIsCol && selectedMeasureIsCol) {
//							if (i == indexSelectedDimension && i == indexSelectedMeasure) {
//								String valueToShow = value.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, mapZoneToKeep.getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(mapZoneToKeep.getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()).addValue(valueToShow);
//								}
//								mapZoneToKeep = null;
//								break;
//							}
//						}
//						else if (selectedDimensionIsRow && selectedMeasureIsCol) {
//							if (i == indexSelectedMeasure && dimensionToKeep) {
//								String valueToShow = value.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, mapZoneToKeep.getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(mapZoneToKeep.getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()).addValue(valueToShow);
//								}
//								mapZoneToKeep = null;
//								dimensionToKeep = false;
//								break;
//							}
//						}
//						else if (selectedDimensionIsCol && selectedMeasureIsRow) {
//							if (i == indexSelectedDimension) {
//								String valueToShow = value.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, mapZoneToKeep.getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(mapZoneToKeep.getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()).addValue(valueToShow);
//								}
//								mapZoneToKeep = null;
//								break;
//							}
//						}
//						else {
//							if (dimensionToKeep) {
//								String valueToShow = value.getValue();
//								if (valueToShow.equals("1.2345E-8")) {
//									valueToShow = "0";
//								}
//								labels.put(id, labelTemp);
//								if (entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()) == null) {
//									Entity entity = new Entity(id, mapZoneToKeep.getFusionMapInternalId(), valueToShow);
//									entitiesValues.put(mapZoneToKeep.getFusionMapInternalId(), entity);
//								}
//								else {
//									entitiesValues.get(mapZoneToKeep.getFusionMapInternalId()).addValue(valueToShow);
//								}
//								mapZoneToKeep = null;
//								dimensionToKeep = false;
//								break;
//							}
//						}
//					}
//				}
//				i++;
//			}
//		}
//
//		for (Entry<String, Entity> entry : entitiesValues.entrySet()) {
//			Entity entity = entry.getValue();
//			String linkDrillDown = buildLinkDrillDown(entity.getId());
//			builder.addEntity(entity.getInternalId(), entity.getValue(), linkDrillDown);
//		}
//
//		return builder.close();
//	}
//
//	private String buildLinkDrillDown(String internalId) {
//		StringBuilder builder = new StringBuilder();
//		builder.append("javascript:drillDown(\\\"");
//		builder.append(internalId);
//		builder.append("\\\")");
//		return builder.toString();
//	}
//
//	private List<IFusionMapObject> findMapByLabel(IMapDefinitionService mapDefinitionService, IFusionMapRegistry mapRegistry, String label) throws Exception {
//		IAddress address = mapDefinitionService.getAddressByLabel(label);
//
//		List<IFusionMapObject> maps = new ArrayList<IFusionMapObject>();
//		if (address == null) {
//			return maps;
//		}
//		else {
//			for (IMapDefinition mapDef : address.getMaps()) {
//				maps.add(mapDef.getFusionMapObject());
//			}
//			return maps;
//		}
//	}
//
//	private List<IFusionMapSpecificationEntity> findZonesByLabel(IMapDefinitionService mapDefinitionService, String label) throws Exception {
//		IAddress address = mapDefinitionService.getAddressByLabel(label);
//		if (address == null) {
//			return new ArrayList<IFusionMapSpecificationEntity>();
//		}
//		else {
//			return address.getAddressZones();
//		}
//	}
//
//	private String buildHtml(String vanillaRuntimeUrl, String swfFileName, String mapData, IFusionMapObject map) {
//		StringBuffer buf = new StringBuffer();
//		buf.append("<html>\n");
//		buf.append("	<head>\n");
//		buf.append("		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n");
//		buf.append("		<title>FusionMap JSP</title>\n");
//		buf.append(" 		<script language=\"JavaScript\" src=\"" + vanillaRuntimeUrl + "/fusionMap/Maps/FusionMaps.js\"></script>\n");
//		buf.append("		<script language=\"javascript\">\n");
//		buf.append("			function drillDown(internalId){\n");
//		buf.append("				window.parent.drillMap(internalId);\n");
//		buf.append("			}\n");
//		buf.append("		</script>\n");
//		buf.append("	</head>\n");
//		buf.append("	<body>\n");
//		buf.append("            <div id=\"mapdiv\" align=\"center\">\n");
//		buf.append("               FusionMaps.\n");
//		buf.append("           </div>\n");
//		buf.append("           <script type=\"text/javascript\">\n");
//		for (IFusionMapSpecificationEntity entities : map.getSpecificationsEntities()) {
//			buf.append("           		var id_" + entities.getId() + " = \"id_" + entities.getId() + "\";\n");
//		}
//		buf.append("                var map = new FusionMaps(\"" + vanillaRuntimeUrl + "/fusionMap/Maps/" + swfFileName + "\", \"Map\", \"600\", \"500\", \"0\", \"0\");\n");
//		buf.append("                map.setDataXML(\"" + mapData + "\");\n");
//		buf.append("                map.render(\"mapdiv\");\n");
//		buf.append("            </script>\n");
//		buf.append("	</body>\n");
//		buf.append("</html>\n");
//		return buf.toString();
//	}
//
//	private String writeIntoFusionMapJsp(String user, String html, FaWebSession session) {
//		String fileName = "FusionMap_" + user + "_" + System.currentTimeMillis() + ".html";
//		String path = session.getLocation();
//		File folder = new File(path + "FusionMaps");
//		if (!folder.exists()) {
//			if (folder.mkdirs()) {
//				logger.info("Folder with path " + folder.getAbsolutePath() + " created with success");
//			}
//			else {
//				logger.error("Cannot create folder with path " + folder.getAbsolutePath());
//			}
//		}
//		File file = new File(path + "FusionMaps/" + fileName);
//		try {
//			// Convert the string to a byte array.
//			byte data[] = html.getBytes();
//
//			OutputStream out = null;
//			try {
//				out = new BufferedOutputStream(new FileOutputStream(file));
//				out.write(data, 0, data.length);
//			} catch (IOException x) {
//				System.err.println(x);
//			} finally {
//				if (out != null) {
//					try {
//						out.flush();
//						out.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			file.deleteOnExit();
//		} catch (Exception e) {
//			logger.error("Error generating file with file name " + file.getAbsolutePath() + "\n" + e.getMessage(), e);
//			e.printStackTrace();
//		}
//		return fileName;
//	}
//
//	@Override
//	public List<List<String>> drillMap(int keySession, String selectedMeasure, String selectedDimension, String internalId) throws ServiceException {
//		String selectedLabel = labels.get(internalId);
//		String selectedElementUname = "";
//		String selectedElementName = "";
//
//		FaWebSession session = getSession();
//		InfosOlap infos = session.getInfosOlap(keySession);
//		OLAPResult res = infos.getRes();
//
//		// We get the displayed measure
//		List<String> measuresDiplay = infos.getInfosReport().getMeasuresDisplay();
//
//		// We init the mapDefinitionService and the FusionMapFactory
//		String vanillaRuntimeUrl = session.getVanillaRuntimeUrl();
//
//		ArrayList<ArrayList<Item>> itemRaws = res.getRaw();
//		boolean found = false;
//		for (ArrayList<Item> items : itemRaws) {
//			for (Item item : items) {
//				if (item instanceof ItemElement) {
//					ItemElement element = (ItemElement) item;
//					int index = 0;
//					if (element.getDataMember() != null && element.getDataMember().getPropertiesName() != null) {
//						for (String prop : element.getDataMember().getPropertiesName()) {
//							if (prop.equals("geolocalizableProperty")) {
//								if (selectedLabel.equals(element.getDataMember().getPropertiesValue()[index])) {
//									selectedElementUname = element.getDataMember().getUniqueName();
//									selectedElementName = element.getDataMember().getName();
//									found = true;
//								}
//								break;
//							}
//							index++;
//						}
//					}
//					if (found) {
//						break;
//					}
//				}
//			}
//			if (found) {
//				break;
//			}
//		}
//
//		IMapDefinitionService mapDefinitionService = null;
//		IFusionMapRegistry mapRegistry = null;
//		try {
//			mapRegistry = new RemoteFusionMapRegistry();
//			mapRegistry.configure(vanillaRuntimeUrl);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			mapDefinitionService = new RemoteMapDefinitionService();
//			mapDefinitionService.configure(vanillaRuntimeUrl);
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//
//		List<IFusionMapObject> fusionMaps = new ArrayList<IFusionMapObject>();
//		try {
//			fusionMaps = findMapByLabel(mapDefinitionService, mapRegistry, selectedLabel);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		List<List<String>> results = new ArrayList<List<String>>();
//
//		List<String> mapName = new ArrayList<String>();
//		List<String> nameAndUname = new ArrayList<String>();
//		List<String> mapAvailables = new ArrayList<String>();
//
//		if (fusionMaps != null) {
//			nameAndUname.add(selectedElementName);
//			nameAndUname.add(selectedElementUname);
//
//			for (IFusionMapObject map : fusionMaps) {
//				mapAvailables.add(map.getSwfFileName());
//			}
//
//			List<ColorRange> colorRanges = buildColor(mapColors);
//			String mapData = buildMapData(selectedMeasure, selectedDimension, res, mapDefinitionService, fusionMaps.get(0), measuresDiplay, colorRanges, selectedElementUname);
//
//			String html = buildHtml(vanillaRuntimeUrl, fusionMaps.get(0).getSwfFileName(), mapData, fusionMaps.get(0));
//
//			mapName.add(writeIntoFusionMapJsp(session.getUser().getLogin(), html, session));
//
//			results.add(mapName);
//			results.add(nameAndUname);
//			results.add(mapAvailables);
//
//			return results;
//		}
//		else {
//			return results;
//		}
//	}
//
//	private class Entity {
//		private String id;
//		private String internalId;
//		private String value;
//
//		public Entity(String id, String internalId, String value) {
//			this.id = id;
//			this.internalId = internalId;
//			this.value = value;
//		}
//
//		public String getId() {
//			return id;
//		}
//
//		public String getInternalId() {
//			return internalId;
//		}
//
//		public String getValue() {
//			return value;
//		}
//
//		public void addValue(String value) {
//			try {
//				Double valueInDoubleToAdd = Double.parseDouble(value);
//				Double valueInDouble = Double.parseDouble(this.value);
//				this.value = String.valueOf(valueInDouble + valueInDoubleToAdd);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	@Override
	public void closeCube(int keySession) {
		try {
			FaWebSession session = getSession();

			if (session != null) {
				for (String file : session.getCreatedFiles(keySession)) {
					File f = new File(file);
					try {
						f.delete();
					} catch (Exception e) {
					}
				}
			}

			if (session != null && session.getInfosOlap(keySession) != null && session.getInfosOlap(keySession).getCube() != null) {
				InfosOlap infos = session.getInfosOlap(keySession);
				try {
					infos.getCube().close();
				} catch (Exception e) {
				}
			}

			session.removeSession(keySession);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Comment addAnnotations(int keySession, String annote) throws ServiceException {
		FaWebSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		RepositoryItem directoryItem = session.getInfosOlap(keySession).getSelectedFasd();

		Date now = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);

		Date yearFromNow = cal.getTime();

		Comment comment = new Comment();
		comment.setBeginDate(now);
		comment.setCreationDate(now);
		comment.setEndDate(yearFromNow);
		comment.setComment(annote);
		comment.setCreatorId(session.getUser().getId());
		comment.setObjectId(directoryItem.getId());
		comment.setType(Comment.ITEM);

		List<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(session.getCurrentGroup().getId());

		try {
			sock.getDocumentationService().addOrUpdateComment(comment, groupIds);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return comment;
	}

	@Override
	public int getCurrentCubeItemId(int keySession) throws ServiceException {
		FaWebSession session = getSession();
		return session.getInfosOlap(keySession).getSelectedFasd().getId();
	}

	@Override
	public InfosReport openCubeFromPortal(int keySession, int fasdId, String cubeName, String dimName, String memberName) throws ServiceException {
		FaWebSession session = getSession();
		if (session.getRepository() == null) {
			session.initRepository(IRepositoryApi.FASD_TYPE);
		}
		InfosReport result;
		try {
			result = RecupInfosCube.openCubeFromPortal(keySession, session, fasdId, cubeName, session.getLocation(), this, session.getInfosOlap(keySession), dimName, memberName);
			if (session.getInfosOlap(keySession).getFiltersByUname() != null && session.getInfosOlap(keySession).getFiltersByUname().size() > 0) {

				List<String> wheres = new ArrayList<String>();
				for (String w : session.getInfosOlap(keySession).getFiltersByUname().keySet()) {
					wheres.add(w);
				}

				result.setWheres(wheres);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	@Override
	public InfosReport openViewFromPortal(int keySession, int fasdId, String cubeName, String viewName, HashMap<String, String> parameters) throws ServiceException {
		FaWebSession session = getSession();
		if (session.getRepository() == null) {
			session.initRepository(IRepositoryApi.FASD_TYPE);
		}
		InfosReport result = openViewFromPortal(keySession, session, fasdId, cubeName, session.getLocation(), viewName, this, session.getInfosOlap(keySession), parameters);
		return result;
	}

	private InfosReport openViewFromPortal(int keySession, FaWebSession session, int fasdItemId, String cubeName, String location, String viewName, FaWebServiceImpl parent, InfosOlap infos, HashMap<String, String> parameters) {
		GridCube gc = null;
		try {

			infos.setSelectedFasd(session.getRepository().getItem(fasdItemId));
			infos.setCubeName(cubeName);
			infos.getInfosReport().setChartInfos(FaWebActions.writeChart(viewName, session, infos));

			try {
				infos.getInfosReport().setMapOptions(FaWebActions.writeMap(viewName, session, session.getInfosOlap(keySession)));
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
			try {
				infos.getInfosReport().setCustomSizes(FaWebActions.getCustomSizesFromView(viewName, session, session.getInfosOlap(keySession)));
				infos.getInfosReport().setSizeLoaded(true);
			} catch(Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

			FaWebActions.writeRepository(keySession, session.getCurrentGroup(), fasdItemId, session, location, infos);
			FaWebActions.setCube(keySession, cubeName, session, infos, "", "");

			List<Calcul> calculs = FaWebActions.writeView(viewName, session, infos, parameters);
			gc = getTable(infos);
			if (calculs != null) {
				gc.setCalculs(calculs);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		InfosReport curr = infos.getInfosReport();
		curr.setCubeName(cubeName);
		curr.setMeasuresGroup(RecupMes.recupMes(infos.getCube(), session, infos));
		curr.setGrid(gc);
		curr.setDims(RecupDim.recupDim(keySession, infos.getCube(), session));

		return curr;
	}

	private String createGridCss() {
		StringBuffer buf = new StringBuffer();
		buf.append("<style>\n");
		buf.append(".cubeView {\n" + "vertical-align: middle;\n" + "table-layout: fixed;\n" + "}\n" + ".gridItemValueBold {\n" + "font-weight: bold;\n" + "}\n" + ".gridItemNull {\n" + "display: table-cell;\n" + "font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "font-size: 11px;\n" + "text-align: center;\n" + "}\n" + ".gridItemValue {\n" + "display: table-cell;\n" + "font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "font-size: 11px;\n" + "background-color: white;\n" + "text-align: center;\n" + "vertical-align: middle;\n" + "cursor: pointer;\n" + "overflow: hidden;\n" + "white-space: nowrap;\n" + "}\n" + ".gridItemBorder {\n" + "border-top: 2px groove;\n" + "border-left: 2px groove;\n" + "}\n" + ".leftGridSpanItemBorder {\n" + "border-left: 2px groove;\n" + "}\n" + ".rightGridSpanItemBorder {\n" + "border-top: 2px groove;\n" + "}\n" + ".lastRowItemBorder {\n" + "border-bottom: 2px groove;\n" + "}\n" + ".lastColItemBorder {\n"
				+ "border-right: 2px groove;\n" + "}\n" + ".draggableGridItem {\n" + "table-layout: fixed;\n" + "margin: auto;\n" + "display: table-cell;\n" + "font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "font-size: 11px;\n" + "background-color: #C3DAF9;\n" + "text-align: center;\n" + "vertical-align: middle;\n" + "overflow: hidden;\n" + "white-space: nowrap;\n" + "}\n" + ".measureGridItem {\n" + "table-layout: fixed;\n" + "margin: auto;\n" + "display: table-cell;\n" + "font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "font-size: 11px;\n" + "background-color: #FFCC66;\n" + "text-align: center;\n" + "vertical-align: middle;\n" + "overflow: hidden;\n" + "white-space: nowrap;\n" + "}\n" + ".gridTotalItem {\n" + "margin: auto;\n" + "display: table-cell;\n" + "font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "font-size: 11px;\n" + "background-color: #C3DAF9;\n" + "text-align: center;\n"
				+ "vertical-align: middle;\n" + "}\n");

		buf.append("</style>\n");
		return buf.toString();
	}

	@Override
	public List<List<DataField>> drillThroughProjection(int keySession, int row, int cell, Projection projection) throws ServiceException {

		FaWebSession session = getSession();
		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPResult res = null;
		try {
			if (projection != null) {
				List<ArrayList<Item>> tp = infos.getCube().getLastProjectionResult().getRaw();
				ItemValue it = (ItemValue) tp.get(row).get(cell);
				bpm.fa.api.olap.projection.Projection faProj = new bpm.fa.api.olap.projection.Projection();
				faProj.setFasdId(session.getInfosOlap(keySession).getSelectedFasd().getId());
				faProj.setCubeName(session.getInfosOlap(keySession).getCubeName());
				faProj.setName(projection.getName());
				faProj.setProjectionMeasures(createFaProjectionMeasures(projection));
				faProj.setType(projection.getType());
				if (projection.getType().equals(Projection.TYPE_EXTRAPOLATION)) {
					faProj.setEndDate(projection.getEndDate());
					faProj.setStartDate(projection.getStartDate());
					faProj.setProjectionLevel(projection.getProjectionLevel());
				}
				res = infos.getCube().drillthrough(it, 1, faProj);
			}
			else {
				getGridCubeForActualQuery(keySession, false, false);
				List<ArrayList<Item>> tp = infos.getCube().getLastProjectionResult().getRaw();
				ItemValue it = (ItemValue) tp.get(row).get(cell);
				res = infos.getCube().drillthrough(it, 1);
			}

			infos.setRes(res);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		List<ArrayList<Item>> tab = new ArrayList<ArrayList<Item>>();
		tab = res.getRaw();

		List<List<DataField>> items = new ArrayList<List<DataField>>();

		List<Item> headers = tab.get(0);

		for (int i = 1; i < res.getYFixed(); i++) {
			ArrayList<Item> ytable = tab.get(i);

			ArrayList<DataField> ligne = new ArrayList<DataField>();
			for (int j = 0; j < res.getXFixed(); j++) {

				DataField field = new DataField();
				String name = headers.get(j).getLabel();
				field.setName(name);
				field.setValue(((ItemValue) ytable.get(j)).getLabel());
				ligne.add(field);
			}
			items.add(ligne);

		}

		return items;
	}

	@Override
	public InfosReport createNewProjection(int keySession, Projection proj) throws ServiceException {
		FaWebSession session = getSession();
		bpm.fa.api.olap.projection.Projection projection = new bpm.fa.api.olap.projection.Projection();
		projection.setFasdId(session.getInfosOlap(keySession).getSelectedFasd().getId());
		projection.setCubeName(session.getInfosOlap(keySession).getCubeName());
		projection.setType(proj.getType());

		if (projection.getType().equals(Projection.TYPE_WHATIF)) {

			projection.setName(proj.getName());
			projection.setProjectionMeasures(createFaProjectionMeasures(proj));

			InfosReport infos = session.getInfosOlap(keySession).getInfosReport();

			session.getInfosOlap(keySession).getCube().setProjection(projection);
			try {
				session.getInfosOlap(keySession).setRes(session.getInfosOlap(keySession).getCube().doQuery());

				infos.setProjection(true);
				infos.setGrid(getTable(session.getInfosOlap(keySession)));

			} catch (Exception e) {
				e.printStackTrace();
			}

			return infos;
		}

		else {
			projection.setEndDate(proj.getEndDate());
			projection.setStartDate(proj.getStartDate());
			projection.setProjectionLevel(proj.getProjectionLevel());
			projection.setName(proj.getName());
			projection.setProjectionMeasures(createFaProjectionMeasures(proj));

			InfosReport infos = session.getInfosOlap(keySession).getInfosReport();
			session.getInfosOlap(keySession).getCube().setProjection(projection);

			try {
				projection = session.getInfosOlap(keySession).getCube().createForecastData();
				session.getInfosOlap(keySession).setRes(session.getInfosOlap(keySession).getCube().doQuery());
			} catch (Exception e) {
				e.printStackTrace();
			}

			infos.setProjection(true);
			infos.setGrid(getTable(session.getInfosOlap(keySession)));

			return infos;
		}
	}

	private List<ProjectionMeasure> createFaProjectionMeasures(Projection proj) {

		List<ProjectionMeasure> projMes = new ArrayList<ProjectionMeasure>();

		for (bpm.faweb.client.projection.ProjectionMeasure pm : proj.getMeasureFormulas()) {
			ProjectionMeasure projM = new ProjectionMeasure();
			projM.setFormula(pm.getFormula());
			projM.setUname(pm.getUname());
			projM.setConditions(createFaProjectionConditions(pm, projM));
			projMes.add(projM);

			if (proj.getType().equals(Projection.TYPE_WHATIF)) {
				ProjectionMeasureCondition cond = new ProjectionMeasureCondition();
				cond.setFormula(pm.getFormula());
				cond.addMemberUname("[Default]");
				projM.addCondition(cond);
			}
		}

		return projMes;
	}

	private List<ProjectionMeasureCondition> createFaProjectionConditions(bpm.faweb.client.projection.ProjectionMeasure pm, ProjectionMeasure faPm) {

		List<ProjectionMeasureCondition> conditions = new ArrayList<ProjectionMeasureCondition>();

		for (bpm.faweb.client.projection.ProjectionMeasureCondition pmc : pm.getConditions()) {

			ProjectionMeasureCondition cond = new ProjectionMeasureCondition();
			cond.setFormula(pmc.getFormula());

			for (String uname : pmc.getMemberUnames()) {
				cond.addMemberUname(uname);
			}
			conditions.add(cond);

		}

		return conditions;
	}

	@Override
	public void saveProjection(int keySession, Projection proj, String name, String comment, String internalVersion, String publicVersion, String group, int directoryId) throws ServiceException {

		FaWebSession session = getSession();
		bpm.fa.api.olap.projection.Projection projection = new bpm.fa.api.olap.projection.Projection();
		projection.setFasdId(session.getInfosOlap(keySession).getSelectedFasd().getId());
		projection.setCubeName(session.getInfosOlap(keySession).getCubeName());
		projection.setName(proj.getName());

		projection.setProjectionMeasures(createFaProjectionMeasures(proj));
		projection.setType(proj.getType());
		if (projection.getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			projection.setEndDate(proj.getEndDate());
			projection.setStartDate(proj.getStartDate());
			projection.setProjectionLevel(proj.getProjectionLevel());
		}

		RepositoryDirectory target = null;
		try {
			target = session.getRepository().getDirectory(directoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String xml = ProjectionParser.getXml(projection);

		try {
			session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.PROJECTION_TYPE, -1, target, proj.getName(), proj.getComment(), internalVersion, publicVersion, xml, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public InfosReport loadProjection(int keySession, int projectionId) throws ServiceException {
		FaWebSession session = getSession();

		bpm.fa.api.olap.projection.Projection faProj = null;
		RepositoryItem projItem = null;
		try {
			projItem = session.getRepository().getItem(projectionId);

			String xml = session.getRepositoryConnection().getRepositoryService().loadModel(projItem);
			faProj = ProjectionParser.parse(xml);
			faProj.setName(projItem.getItemName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Projection resProj = createClientProjection(faProj);
		resProj.setName(projItem.getItemName());

		session.getInfosOlap(keySession).getCube().setProjection(faProj);

		try {
			session.getInfosOlap(keySession).setRes(session.getInfosOlap(keySession).getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}

		session.getInfosOlap(keySession).getInfosReport().setGrid(getTable(session.getInfosOlap(keySession)));
		session.getInfosOlap(keySession).getInfosReport().setActualProjection(resProj);

		return session.getInfosOlap(keySession).getInfosReport();
	}

	private Projection createClientProjection(bpm.fa.api.olap.projection.Projection faProj) {

		Projection res = new Projection();

		res.setName(faProj.getName());
		res.setType(faProj.getType());

		if (faProj.getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			res.setEndDate(faProj.getEndDate());
			res.setStartDate(faProj.getStartDate());
			res.setProjectionLevel(faProj.getProjectionLevel());
		}

		for (ProjectionMeasure projMes : faProj.getProjectionMeasures()) {
			bpm.faweb.client.projection.ProjectionMeasure resMes = new bpm.faweb.client.projection.ProjectionMeasure();
			resMes.setFormula(projMes.getFormula());
			resMes.setUname(projMes.getUname());

			bpm.faweb.client.projection.ProjectionMeasureCondition defCond = new bpm.faweb.client.projection.ProjectionMeasureCondition();
			defCond.setFormula(projMes.getFormula());
			defCond.setDefault(true);

			for (ProjectionMeasureCondition cond : projMes.getConditions()) {
				bpm.faweb.client.projection.ProjectionMeasureCondition resCond = new bpm.faweb.client.projection.ProjectionMeasureCondition();
				resCond.setFormula(cond.getFormula());
				for (String uname : cond.getMemberUnames()) {
					resCond.addMemberUname(uname);
				}
				resMes.addCondition(resCond);
			}
			res.addMeasureFormula(resMes);
		}

		return res;
	}

	@Override
	public GridCube getGridCubeForActualQuery(int keySession, boolean isProjection, boolean refresh) throws ServiceException {
		FaWebSession session = getSession();

		session.getInfosOlap(keySession).getCube().setApplyProjection(isProjection);
		try {
			if (refresh) {
				session.getInfosOlap(keySession).getCube().restore();
			}
			session.getInfosOlap(keySession).setRes(session.getInfosOlap(keySession).getCube().doQuery());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getTable(session.getInfosOlap(keySession));
	}

	@Override
	public InfosReport saveFilterConfig(int keySession, FilterConfigDTO config, boolean execute) throws Throwable {
		FaWebSession session = getSession();

		int fasdId = session.getInfosOlap(keySession).getSelectedFasd().getId();
		String cubeName = session.getInfosOlap(keySession).getCubeName();

		String key = generateFilterConfigKey(fasdId, cubeName, config);

		config.setFasdId(fasdId);
		config.setCubeName(cubeName);

		String vanillaFiles = getServletContext().getRealPath("/");
		String filePath = vanillaFiles + File.separator + "FileFilterConfig/" + key;

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(filePath)));
		out.write(config.getXml());
		out.flush();
		out.close();

		session.getInfosOlap(keySession).getCube().getMdx().getWhere().clear();
		session.getInfosOlap(keySession).getFiltersByUname().clear();
		List<WhereClauseException> exs = new ArrayList<WhereClauseException>();

		for (String fil : config.getFilters()) {
			try {
				FaWebActions.filterItem(fil, session, session.getInfosOlap(keySession));
			} catch (WhereClauseException e) {
				exs.add(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<String> allowedFilters = config.getFilters();

		if (exs.size() > 0) {
			if (session.getInfosOlap(keySession).getInfosReport().getFailedUnames() == null) {
				session.getInfosOlap(keySession).getInfosReport().setFailedUnames(new ArrayList<String>());
			}
			for (WhereClauseException e : exs) {
				session.getInfosOlap(keySession).getInfosReport().getFailedUnames().addAll(e.getWhereUname());
				allowedFilters.removeAll(e.getWhereUname());
			}
		}

		session.getInfosOlap(keySession).getCube().getMdx().getWhere().clear();
		session.getInfosOlap(keySession).getInfosReport().setWheres(allowedFilters);
		return filterService(keySession, allowedFilters);
	}

	private String generateFilterConfigKey(int fasdId, String cubeName, FilterConfigDTO config) {
		StringBuilder buf = new StringBuilder();
		buf.append("FilterConfigFile");
		buf.append("-" + fasdId);
		buf.append("-" + cubeName);
		if (config != null) {
			buf.append("-" + config.getName());
		}
		return buf.toString();
	}

	@Override
	public List<FilterConfigDTO> getFilterConfigs(int keySession) throws Throwable {
		FaWebSession session = getSession();

		int fasdId = session.getInfosOlap(keySession).getSelectedFasd().getId();
		String cubeName = session.getInfosOlap(keySession).getCubeName();

		String key = generateFilterConfigKey(fasdId, cubeName, null);

		String vanillaFiles = getServletContext().getRealPath("/");
		String filePath = vanillaFiles + "FileFilterConfig/";

		List<FilterConfigDTO> result = new ArrayList<FilterConfigDTO>();

		try {
			File configDir = new File(filePath);
			if (!configDir.exists()) {
				configDir.mkdir();
			}
			for (File confFile : configDir.listFiles()) {
				if (confFile.getName().startsWith(key)) {

					FilterConfigDTO dto = parseConfigFile(confFile);
					result.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private FilterConfigDTO parseConfigFile(File confFile) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(confFile));

		String res = "";

		String line;
		while ((line = reader.readLine()) != null) {
			res += line;
		}

		reader.close();

		FilterConfigDTO result = new FilterConfigDTO();

		Document doc = DocumentHelper.parseText(res);

		Element elem = doc.getRootElement();

		result.setName(elem.elementText("name"));
		result.setComment(elem.elementText("comment"));
		result.setFasdId(Integer.parseInt(elem.elementText("fasdid")));
		result.setCubeName(elem.elementText("cubename"));

		for (Element e : (List<Element>) elem.element("filters").elements("filter")) {

			String filter = e.getText();
			result.addFilter(filter);
		}

		return result;
	}

	@Override
	public InfosReport loadFilterConfig(int keySession, FilterConfigDTO config) throws Throwable {
		FaWebSession session = getSession();
		session.getInfosOlap(keySession).getFiltersByUname().clear();
		session.getInfosOlap(keySession).getInfosReport().setFailedUnames(new ArrayList<String>());

		List<WhereClauseException> exs = new ArrayList<WhereClauseException>();

		for (String fil : config.getFilters()) {
			try {
				FaWebActions.filterItem(fil, session, session.getInfosOlap(keySession));
			} catch (WhereClauseException e) {
				exs.add(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<String> allowedFilters = config.getFilters();

		if (exs.size() > 0) {
			if (session.getInfosOlap(keySession).getInfosReport().getFailedUnames() == null) {
				session.getInfosOlap(keySession).getInfosReport().setFailedUnames(new ArrayList<String>());
			}
			for (WhereClauseException e : exs) {
				session.getInfosOlap(keySession).getInfosReport().getFailedUnames().addAll(e.getWhereUname());
				allowedFilters.remove(e.getWhereUname().get(0));
			}
		}

		session.getInfosOlap(keySession).getCube().getMdx().getWhere().clear();
		session.getInfosOlap(keySession).getInfosReport().setWheres(allowedFilters);
		return filterService(keySession, allowedFilters);
	}

	@Override
	public void deleteFilterConfig(FilterConfigDTO config) throws Throwable {
		int fasdId = config.getFasdId();
		String cubeName = config.getCubeName();

		String key = generateFilterConfigKey(fasdId, cubeName, config);
		String vanillaFiles = getServletContext().getRealPath("/");
		String filePath = vanillaFiles + File.separator + "FileFilterConfig/" + key;

		File file = new File(filePath);
		file.delete();
	}

	@Override
	public List<DrillParameterDTO> getPossibleValuesForParameter(int keySession, List<DrillParameterDTO> paramaters) throws Throwable {
		FaWebSession session = getSession();
		for (DrillParameterDTO dto : paramaters) {

			String level = null;

			LOOK: for (bpm.fa.api.olap.Dimension dim : session.getInfosOlap(keySession).getCube().getDimensions()) {
				if (dim.getName().equals(dto.getDimension())) {
					for (Level lvl : dim.getHierarchies().iterator().next().getLevel()) {
						if (lvl.getName().equals(dto.getLevel())) {
							level = lvl.getUniqueName();
							break LOOK;
						}
					}
				}
			}

			dto.setPossibleValues(session.getInfosOlap(keySession).getCube().getParametersValues(level));
		}
		return paramaters;
	}

	@Override
	public String runDrill(int keySession, DrillDTO drill) throws Throwable {
		FaWebSession session = getSession();
		String fawebUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);
		IRepositoryContext ctx = session.getRepositoryConnection().getContext();

		String generatedUrl = fawebUrl;

		if (drill.getType() == DrillDTO.CUBE) {

			generatedUrl += "?bpm.vanilla.sessionId=" + ((RemoteVanillaSystemManager) session.getVanillaApi().getVanillaSystemManager()).getCurrentSessionId();
			generatedUrl += "&bpm.vanilla.groupId=" + ctx.getGroup().getId();
			generatedUrl += "&bpm.vanilla.repositoryId=" + ctx.getRepository().getId();
			generatedUrl += "&bpm.vanilla.fasd.id=" + drill.getItemId();
			generatedUrl += "&bpm.vanilla.cubename=" + URLEncoder.encode(drill.getCubeName(), "UTF-8");
			generatedUrl += "&bpm.vanilla.dimension=" + URLEncoder.encode("true", "UTF-8");

			if (session.getInfosOlap(keySession).getFiltersByUname().keySet().size() > 0) {
				generatedUrl += "&bpm.vanilla.filters=";
				boolean first = true;
				for (String f : session.getInfosOlap(keySession).getFiltersByUname().keySet()) {
					if (first) {
						first = false;
					}
					else {
						generatedUrl += "!separator!";
					}
					generatedUrl += f;
				}
			}

			// generatedUrl += "&bpm.vanilla.member=" +
			// URLEncoder.encode(urlParts[2], "UTF-8");

			return generatedUrl;
		}
		else {
			RemoteReportRuntime remote = new RemoteReportRuntime(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

			List<VanillaGroupParameter> parameters = null;

			if (drill.getParameters() != null && drill.getParameters().size() > 0) {
				parameters = new RemoteVanillaParameterComponent(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword()).getParameters(new ReportRuntimeConfig(new ObjectIdentifier(session.getRepositoryConnection().getContext().getRepository().getId(), drill.getItemId()), null, session.getCurrentGroup().getId()));

				for (VanillaGroupParameter p : parameters) {
					for (VanillaParameter pa : p.getParameters()) {
						for (DrillParameterDTO dto : drill.getParameters()) {
							if (dto.getName().equals(pa.getName())) {
								pa.addSelectedValue(dto.getValue());
								break;
							}
						}
					}
				}
			}

			ReportRuntimeConfig config = new ReportRuntimeConfig(new ObjectIdentifier(session.getRepositoryConnection().getContext().getRepository().getId(), drill.getItemId()), parameters, session.getCurrentGroup().getId());

			config.setOutputFormat("html");

			InputStream is = remote.runReport(config, session.getUser());
			byte[] bytes = IOUtils.toByteArray(is);
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);

			String key = new Object().hashCode() + "";

			session.setReport(keySession, key, in);
			is.close();

			return fawebUrl + "/ReportServlet?reportKey=" + key + "&keySession=" + keySession;
		}
	}

	@Override
	public List<GroupChart> executeQuery(int keySession, List<String> groups, List<String> datas, List<String> filters, String measure) throws ServiceException {
		FaWebSession session = getSession();
		InfosOlap olap = session.getInfosOlap(keySession);

		OLAPCube cube = olap.getCube();
		String oldmdx = null;
		try {
			oldmdx = cube.getQuery();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		OLAPQuery mdx = new OLAPQuery("[" + olap.getCubeName() + "]", cube.getDimensions(), cube);

		for (String u : groups) {
			mdx.addcol(u);
		}

		for (String s : datas) {
			mdx.addcol(s);
		}

		for (String f : filters) {
			try {
				mdx.addWhere(f);
			} catch (WhereClauseException e) {
				e.printStackTrace();
			}
		}

		mdx.addrow(measure);

		OLAPResult ores = null;
		try {
			ores = cube.doQuery(mdx.getMDX());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (datas != null && datas.size() == 0) {
			datas.addAll(groups);
		}
		else if (groups != null && groups.size() == 0) {
			groups.addAll(datas);
		}

		List<GroupChart> groupsChart = new ArrayList<GroupChart>();
		if (ores != null) {
			ArrayList<ArrayList<Item>> table = ores.getRaw();

			int nbGroup = 0;
			int maxPart = 0;
			for (String gr : groups) {
				int nbPart = gr.split("\\.").length;
				if (maxPart < nbPart) {
					nbGroup++;
					maxPart = nbPart;
				}
			}

			int nbSeries = 0;
			int maxPartS = 0;
			for (String ser : datas) {
				int nbSer = ser.split("\\.").length;
				if (maxPartS < nbSer) {
					nbSeries++;
					maxPartS = nbSer;
				}
			}

			int nbCellPerRow = table.get(0).size();
			int lastRow = table.size() - 1;

			GroupChart currentGroup = null;
			String currentSerieLabel = "";
			for (int i = 1; i < nbCellPerRow; i++) {

				for (int j = 0; j < nbGroup; j++) {
					if (table.get(j).get(i) instanceof ItemElement) {
						String group = ((ItemElement) table.get(j).get(i)).getDataMember().getUniqueName();

						currentGroup = new GroupChart(group);
						currentGroup.setMeasure(measure);
						groupsChart.add(currentGroup);
					}
				}

				for (int k = 0; k < nbSeries; k++) {
					if (table.get(nbGroup + k).get(i) instanceof ItemElement) {
						String value = (((ItemValue) table.get(lastRow).get(i)).getValue() != null) ? ((ItemValue) table.get(lastRow).get(i)).getValue() : "0";

						if (value.equals("1.2345E-8")) {
							value = "0";
						}

						SerieChart serie = new SerieChart(table.get(nbGroup + k).get(i).getLabel(), value);
						currentGroup.addSeries(serie);
					}
					else {
						String value = (((ItemValue) table.get(lastRow).get(i)).getValue() != null) ? ((ItemValue) table.get(lastRow).get(i)).getValue() : "0";

						SerieChart serie = new SerieChart(currentSerieLabel, value);
						currentGroup.addSeries(serie);
					}
				}
			}
		}

		try {
			cube.doQuery(oldmdx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return groupsChart;
	}

	@Override
	public boolean canComment(int keySession, int fasdId) throws ServiceException {
		FaWebSession session = getSession();
		if (session.getInfosOlap(keySession).getSelectedFasd() != null) {
			RepositoryItem item;
			try {
				item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(fasdId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the item.", e);
			}
			return item.isCommentable();
		}
		return false;
	}

	@Override
	public String exportFromDrilThroughPDF(boolean isLandscape, int pageSize, String title, String origin, String description, String filters, List<String> headerList, List<List<String>> cellList) throws Exception {
		Rectangle size;
		switch (pageSize) {
		case 0:
			size = PageSize.LETTER;
			break;
		case 1:
			size = PageSize.TABLOID;
			break;
		case 2:
			size = PageSize.LEGAL;
			break;
		case 3:
			size = PageSize.A3;
			break;
		case 4:
			size = PageSize.A4;
			break;
		case 5:
			size = PageSize.A5;
			break;
		case 6:
			size = PageSize.B4;
			break;
		case 7:
			size = PageSize.B5;
			break;
		case 8:
			size = PageSize.POSTCARD;
			break;
		default:
			size = PageSize.A4;
			break;
		}
		String exportedFiles = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path") + title + System.currentTimeMillis() + ".pdf";
		// FOR TOMCAT
		new PDFWriter(exportedFiles, isLandscape, size, title, origin, description, filters, headerList, cellList);

		return exportedFiles;
	}

	@Override
	public String exportFromDrillThroughXLS(String title, String description, String origin, String filters, List<String> headerList, List<List<String>> cellList) throws Exception {
		String exportedFiles = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path") + title + System.currentTimeMillis() + ".xlsx";
		new XLSWriter(exportedFiles, title, description, origin, filters, headerList, cellList);
		return exportedFiles;
	}

	public OpenLayer buildOpenLayerMap(int keySession, Integer id, String label, String uname) throws Exception {
		OpenLayer openLayer = new OpenLayer();
		FaWebSession session = getSession();
		String vanillaRuntimeUrl = session.getVanillaRuntimeUrl();

		IMapDefinitionService mapDefinitionService = null;
		IFusionMapRegistry mapRegistry = null;
		try {
			mapRegistry = new RemoteFusionMapRegistry();
			mapRegistry.configure(vanillaRuntimeUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mapDefinitionService = new RemoteMapDefinitionService();
			mapDefinitionService.configure(vanillaRuntimeUrl);
			IAddress address = mapDefinitionService.getAddressByLabel(label);

			for (IAddress a : mapDefinitionService.getAddressChild(address)) {
				openLayer.addAddressChild(a.getId(), a.getLabel());
			}
			String[] layers = null;
			String[] urls = null;
			String type = "";
			String name = "";
			for (IMapDefinition m : address.getMaps()) {
				if (m.getId().equals(id)) {
					IOpenLayersMapObject open = m.getOpenLayersMapObject();
					for (IOpenLayersMapObjectProperty prop : open.getProperties()) {
						if (prop.getName().equals("layers")) {
							layers = prop.getValue().split(";");
						}
						else if (prop.getName().equals("type")) {
							type = prop.getValue();
						}
						else if (prop.getName().equals("url")) {
							urls = prop.getValue().split(";");
						}
						else if (prop.getName().equals("name")) {
							name = prop.getValue();
						}
					}
				}

				openLayer.setBaseLayerUrl(urls[0]);
				openLayer.setVectorLayerUrl(urls[1]);
				openLayer.setFeatureType(layers[0]);
				openLayer.setLayers(layers[1]);
				openLayer.setWidth(50);
				openLayer.setHeight(50);
				openLayer.setBaseLayerName(name);// base name
				openLayer.setType(type);// type

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		InfosOlap infos = session.getInfosOlap(keySession);
		OLAPResult res = infos.getRes();

		String selectedLabel = "";
		ArrayList<ArrayList<Item>> itemRaws = res.getRaw();
		boolean found = false;
		for (ArrayList<Item> items : itemRaws) {
			for (Item item : items) {
				if (item instanceof ItemElement) {
					ItemElement element = (ItemElement) item;
					if (uname.equals(element.getDataMember().getUniqueName())) {
						int index = 0;
						if (element.getDataMember() != null && element.getDataMember().getPropertiesName() != null) {
							for (String prop : element.getDataMember().getPropertiesName()) {
								if (prop.equals("geolocalizableProperty")) {
									selectedLabel = element.getDataMember().getPropertiesValue()[index];
								}
								index++;
							}
						}
						found = true;
						if (found) {
							break;
						}
					}
				}
			}
			if (found) {
				break;
			}
		}

		return openLayer;
	}

	@Override
	public List<List<String>> getMapDefinitions(int keySession, String label) throws Exception {
		List<List<String>> listMap = new ArrayList<List<String>>();
		FaWebSession session = getSession();
		String vanillaRuntimeUrl = session.getVanillaRuntimeUrl();

		IMapDefinitionService mapDefinitionService = null;
		IFusionMapRegistry mapRegistry = null;
		try {
			mapRegistry = new RemoteFusionMapRegistry();
			mapRegistry.configure(vanillaRuntimeUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mapDefinitionService = new RemoteMapDefinitionService();
			mapDefinitionService.configure(vanillaRuntimeUrl);

			IAddress address = mapDefinitionService.getAddressByLabel(label);

			if (address != null && address.getMaps() != null) {
				for (IMapDefinition map : address.getMaps()) {
					List<String> m = new ArrayList<String>();
					m.add(String.valueOf(map.getId()));
					m.add(map.getLabel());

					if (map.getFusionMapObject() != null) {
						m.add("Fusion");
					}
					else {
						m.add("OpenLayer");
					}

					listMap.add(m);
				}
			}

			for (MapVanilla map : mapDefinitionService.getAllMapsVanilla()) {
				List<String> m = new ArrayList<String>();
				m.add(String.valueOf(map.getId()));
				m.add(map.getName());
				m.add("Osm");
				listMap.add(m);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return listMap;
	}

	@Override
	public DrillInformations applyDrillFilter(DrillInformations drillInfo, List<DrillthroughFilter> filters) throws ServiceException {
		FaWebSession session = getSession();
		return session.applyDrillFilter(drillInfo, filters);
	}

	@Override
	public List<List<String>> getPartOfDrills(int key, int start, int length, Integer indexSort, boolean ascending, boolean changed) throws Throwable {
		FaWebSession session = getSession();
		return session.getRange(key, start, start + length, indexSort, ascending, changed);
	}

	@Override
	public InfosReport setSortingElements(int keySession, HashMap<String, String> sortElements) throws Exception {
		try {
			FaWebSession session = getSession();
			InfosOlap infos = session.getInfosOlap(keySession);
			infos.getCube().setSorting(sortElements);

			infos.setRes(infos.getCube().doQuery());

			infos.getInfosReport().setGrid(getTable(infos));

			List<SortElement> sorts = new ArrayList<SortElement>();

			for (String uname : sortElements.keySet()) {
				SortElement eleme = new SortElement();
				eleme.setUname(uname);
				eleme.setType(sortElements.get(uname));
				sorts.add(eleme);
			}

			infos.getInfosReport().setSortElements(sorts);

			return infos.getInfosReport();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateView(int keySession, ChartParameters chartParams, String gridHtml, List<Calcul> calculs, InfosReport infosReport, MapOptions mapOptions) throws Exception {
		try {
			FaWebSession session = getSession();
			InfosOlap infos = session.getInfosOlap(keySession);

			IRepositoryApi sock = session.getRepositoryConnection();

			RepositoryItem oldView = sock.getRepositoryService().getDirectoryItem(infos.getViewId());

			String cubeViewXml = createCubeViewXml(infos, calculs, chartParams, infosReport, mapOptions);

			String bytes = createImageUsingFWR("html", oldView.getName(), infos, gridHtml);

			cubeViewXml += "<rolodeximage>" + bytes + "</rolodeximage>\n";

			cubeViewXml += "</view>\n";

			cubeViewXml = "<fav><name>" + oldView.getName() + "</name><cubename>" + infos.getCubeName() + "</cubename><fasdid>" + infos.getSelectedFasd().getId() + "</fasdid>" + cubeViewXml + "</fav>";

			sock.getRepositoryService().updateModel(oldView, cubeViewXml);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<MapValues> getOsmValues(int keySession, String uname, String selectedMeasure, int datasetId, String selectedDimension, List<List<String>> colors) throws Exception {
		try {
			FaWebSession session = getSession();
			InfosOlap olap = session.getInfosOlap(keySession);

			RemoteMapDefinitionService mapDefinitionService = new RemoteMapDefinitionService();
			mapDefinitionService.configure(session.getVanillaRuntimeUrl());

			MapDataSet dataset = mapDefinitionService.getMapDataSetById(datasetId).get(0);
			MapVanilla map = mapDefinitionService.getMapVanillaById(dataset.getIdMapVanilla()).get(0);

//			mapColors = colors;

			OLAPCube cube = olap.getCube();
			String oldmdx = null;
			try {
				oldmdx = cube.getQuery();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			OLAPQuery mdx = new OLAPQuery("[" + olap.getCubeName() + "]", cube.getDimensions(), cube);

			if(!selectedMeasure.contains("[Measures].")) {
				selectedMeasure = "[Measures].[" + selectedMeasure + "]";
			}
			
			mdx.addrow(selectedMeasure);
			mdx.addcol(uname + ".children");
			boolean props = false;
			if (cube.getShowProperties()) {
				cube.setshowProperties(false);
				props = true;
			}

			OLAPResult result = cube.doQuery(mdx.getMDX());

			// generate the values
			HashMap<String, MapValues> vals = new HashMap<String, MapValues>();

			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(
			// map.getDataSet().getDataSource().getUrl(),
			// map.getDataSet().getDataSource().getLogin(),
			// map.getDataSet().getDataSource().getMdp(),
			// map.getDataSet().getDataSource().getDriver());
					dataset.getDataSource().getUrl(), dataset.getDataSource().getLogin(), dataset.getDataSource().getMdp(), dataset.getDataSource().getDriver());

			// VanillaPreparedStatement stmt =
			// connection.prepareQuery(map.getDataSet().getQuery());
			VanillaPreparedStatement stmt = connection.prepareQuery(dataset.getQuery());

			// map the zones
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				// String zoneId = rs.getString(map.getDataSet().getIdZone());
				// String lat = rs.getString(map.getDataSet().getLatitude());
				// String longi = rs.getString(map.getDataSet().getLongitude());
				String zoneId = rs.getString(dataset.getIdZone());
				String lat = rs.getString(dataset.getLatitude());
				String longi = rs.getString(dataset.getLongitude());

				if (vals.get(zoneId) == null) {
					MapValues v = new MapValues();
					v.setGeoId(zoneId);
					v.setMap(map);
					vals.put(zoneId, v);
				}
				vals.get(zoneId).addLatitude(lat);
				vals.get(zoneId).addLongitude(longi);

			}

			rs.close();
			stmt.close();
			ConnectionManager.getInstance().returnJdbcConnection(connection);

			// get from the cube
			for (int j = result.getYFixed(); j < result.getRaw().get(0).size(); j++) {
				String zoneId = ((ItemElement) result.getRaw().get(result.getXFixed() - 1).get(j)).getDataMember().getPropertiesValue()[0];
				String val = ((ItemValue) result.getRaw().get(result.getXFixed()).get(j)).getValue();

				if (vals.get(zoneId) != null) {
					vals.get(zoneId).setValue(val);
				}
			}

			cube.setshowProperties(props);
			try {
				cube.doQuery(oldmdx);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return new ArrayList<MapValues>(vals.values());
		} catch(Throwable e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	@Override
	public InfosReport openCubeFromFMDTWeb(int keySession, String sessionId) throws ServiceException {
		try {
			CommonSession session = CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
			String cubeXml = session.getSystemManager().getSession(sessionId).getCubeXml();
			XStream stream = new XStream();
			FAModel model = (FAModel) stream.fromXML(cubeXml);

			UnitedOlapLoader loader = UnitedOlapLoaderFactory.getLoader();

			IRuntimeContext rctx = new RuntimeContext(session.getVanillaContext().getLogin(), session.getVanillaContext().getPassword(), session.getCurrentGroup().getName(), session.getCurrentGroup().getId());

			ModelServiceProvider modelservice = new ModelServiceProvider();

			modelservice.init(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

			RuntimeServiceProvider runtimeProvider = new RuntimeServiceProvider(null);
			runtimeProvider.init(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

			UnitedOlapServiceProvider.getInstance().init(runtimeProvider, modelservice);
			String schemaId = null;

			schemaId = loader.loadModel(model, rctx, null);
			List<UnitedOlapStructure> structs = new ArrayList<UnitedOlapStructure>();

			if (schemaId != null) {
				InfosOlap infos = new InfosOlap();
				structs = loader.getStructures(schemaId);
				OLAPCube cube = structs.get(0).createCube(rctx);
				infos.setCube(cube);
				infos.setRes(infos.getCube().doQuery());
				InfosReport curr = infos.getInfosReport();
				curr.setCubeName(model.getName());
				curr.setMeasuresGroup(RecupMes.recupMes(infos.getCube(), getSession(), infos));
				curr.setGrid(getTable(infos));
				curr.setDims(RecupDim.recupDim(keySession, infos.getCube(), getSession()));
				getSession().addInfosOlap(keySession, infos);

				return curr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new InfosReport();
	}

	@Override
	public DatasourceCsv uploadCsv(int keySession, String name, boolean hasHeader, String separator, DrillInformations drillInfos) throws Exception {
		FaWebSession session = getSession();
		
		try {
//			int row = session.getInfosOlap(keySession).getRes().getXFixed();
//			int cell = session.getInfosOlap(keySession).getRes().getYFixed();
			
//			DrillInformations drillInfos = drillThroughService(keySession, row, cell, null);
//			OLAPResult result = session.getInfosOlap(keySession).getCube().getLastResult();
//			session.getInfosOlap(keySession).setRes(result);
			
			String format = "csv";
			
			InfoShareCube infoShare = new InfoShareCube(TypeShare.EXPORT, TypeExport.CUBE, name, "", "", format, separator, false, new ArrayList<Group>(), new ArrayList<Email>(), "", false);
			infoShare.setDrillInformations(drillInfos);
			ExportResult exportResult = drillThroughExportXls(infoShare);
			
			String filePath = saveFile(session, keySession, name, format, exportResult);
			
			DatasourceCsv datasource = new DatasourceCsv();
			datasource.setFilePath(filePath);
			datasource.setHasHeader(hasHeader);
			datasource.setSeparator(separator);
			datasource.setSourceType("disk");
			return datasource;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to share to AIR : " + e.getMessage());
		}
	}

	private String saveFile(FaWebSession session, int keySession, String name, String format, ExportResult result) throws IOException {
		ByteArrayInputStream is = session.getReport(result.getReportName()).getStream(format);
		
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		String relPath = "/air_files/" + new Date().getTime() + "_" + name + "." + format;
		
		Path path = Paths.get(basePath + relPath);
	    
	    Files.copy(is, path);
		return relPath;
	}

}