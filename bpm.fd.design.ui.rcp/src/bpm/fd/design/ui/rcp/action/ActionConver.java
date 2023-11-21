package bpm.fd.design.ui.rcp.action;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.nature.FdNature;

public class ActionConver {

	private int yHeight = 0, xWidht = 0, xPosition = 0, yPosition = 0;
	private FdProject fd;
	private MultiPageFdProject fdMulti;
	private FdModel currentModel;

	private String cssClasFold;

	public void run() throws Exception {

		fd = Activator.getDefault().getProject();

		InputDialog dial = new InputDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Project Convertion", "Converted Project Name", "conv_" + fd.getProjectDescriptor().getProjectName(), null);

		if(dial.open() != InputDialog.OK) {
			return;
		}

		String projName = dial.getValue();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot r = workspace.getRoot();

		IProject p = r.getProject(projName);

		if(p.exists()) {
			if(!MessageDialog.openConfirm(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Project Generation", "A project with the same already exists. Do you really want to replace it?")) {
				return;
			}
			else {
				p.delete(true, null);
			}
		}

		Dictionary dico = fd.getDictionary();
		FdProjectDescriptor fdpd = new FdProjectDescriptor();
		fdpd.setAuthor(fd.getProjectDescriptor().getAuthor());
		fdpd.setCreation(new Date());
		fdpd.setDictionaryName(dico.getName());
		fdpd.setModelName(fd.getProjectDescriptor().getModelName());
		fdpd.setProjectName(projName);
		fdpd.setProjectVersion(fd.getProjectDescriptor().getProjectVersion());

		fdpd.setInternalApiDesignVersion(FdProjectDescriptor.API_DESIGN_VERSION);

		fdMulti = new MultiPageFdProject(fdpd);
		fdMulti.setDictionary(dico);
		for(int res = 0; res < fd.getResources().size(); res++) {
			fdMulti.addResource(fd.getResources().get(res));
		}

		// fdmN = new FdModel(fd.getFdModel().getStructureFactory());
		int FoldSize = fd.getFdModel().getContent().size();

		for(int sizeFold = 0; sizeFold < FoldSize; sizeFold++) {
			if(fd.getFdModel().getContent().get(sizeFold) instanceof Folder) {
				Folder d = (Folder) fd.getFdModel().getContent().get(sizeFold);
				checkFolder(d);
			}
			else {
				Table contentTable = (Table) fd.getFdModel().getContent().get(sizeFold);
				for(int tableSize = 0; tableSize < contentTable.getDetailsRows().size(); tableSize++) {
					List<Cell> cellList = contentTable.getDetailsRows().get(tableSize);

					xPosition = 0;
					int yTemp = 0;

					for(int nbrCell = 0; nbrCell < cellList.size(); nbrCell++) {
						if(!cellList.get(nbrCell).getContent().isEmpty()) {
							IBaseElement cellComponent = cellList.get(nbrCell).getContent().get(0);

							checkInstanceOf(cellComponent);

							int yHeighttmp = yHeight;
							Cell newCell = (Cell) cellList.get(nbrCell);

							newCell.setSize(xWidht, yHeight);
							newCell.setPosition(xPosition, yPosition);
							currentModel.addToContent((IStructureElement) newCell);
							xPosition = xPosition + xWidht + 10;
							if(yHeighttmp > yTemp) {
								yTemp = yHeight + 10;
							}
							else {

							}

						}

					}
					yPosition = yPosition + yTemp;
				}
			}

		}

		// create eclipse project
		p = r.getProject(projName);
		p.create(null);
		p.open(null);

		IProjectDescription pD = p.getDescription();
		pD.setNatureIds(new String[] { FdNature.ID });
		p.setDescription(pD, null);

		// persist modelPages
		for(FdModel s : fdMulti.getPagesModels()) {

			IFile _f = p.getFile(s.getName() + ".freedashboard"); //$NON-NLS-1$
			try {
				Document d = DocumentHelper.createDocument(s.getElement());
				_f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
			} catch(Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}
		// persist dictionary
		IFile dicoFile = p.getFile(fdMulti.getProjectDescriptor().getDictionaryName() + ".dictionary"); //$NON-NLS-1$

		try {
			dicoFile.create(IOUtils.toInputStream(fdMulti.getDictionary().getElement().asXML(), "UTF-8"), true, null); //$NON-NLS-1$
		} catch(Exception e) {
			throw new Exception("", e);
		}
		// persist resources
		for(bpm.fd.api.core.model.resources.IResource res : fdMulti.getResources()) {
			IFile cssFile = p.getFile(res.getName());

			cssFile.create(new FileInputStream(res.getFile()), true, null);

			bpm.fd.api.core.model.resources.IResource resource = null;
			if(res instanceof FileCSS) {
				resource = new FileCSS(res.getName(), cssFile.getLocation().toFile());
			}
			else if(res instanceof FileImage) {
				resource = new FileImage(res.getName(), cssFile.getLocation().toFile());
			}
			else if(res instanceof FileJavaScript) {
				resource = new FileJavaScript(res.getName(), cssFile.getLocation().toFile());
			}
			else if(res instanceof FileProperties) {
				String locale = null;
				if(res.getName().contains("_")) {
					locale = res.getName().substring(0, res.getName().indexOf("_")); //$NON-NLS-1$
				}
				resource = new FileProperties(res.getName(), locale, cssFile.getLocation().toFile());
			}

			fdMulti.addResource(resource);
		}

		// persist main model
		IFile f = p.getFile(fdMulti.getProjectDescriptor().getModelName() + ".freedashboard"); //$NON-NLS-1$
		try {
			Document d = DocumentHelper.createDocument(fdMulti.getFdModel().getElement());
			f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
		} catch(Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		Activator.getDefault().openProject(fdMulti);

	}

	public void checkInstanceOf(IBaseElement clContent) throws Exception {

		if(clContent instanceof ComponentChartDefinition) {
			yHeight = ((ComponentChartDefinition) clContent).getHeight();
			xWidht = ((ComponentChartDefinition) clContent).getWidth();
		}
		else if(clContent instanceof LabelComponent) {
			yHeight = 90;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentPicture) {
			yHeight = 250;
			xWidht = 250;
		}
		else if(clContent instanceof ComponentFilterDefinition) {
			yHeight = 80;
			xWidht = 230;
		}
		else if(clContent instanceof ComponentMap) {
			yHeight = ((ComponentMap) clContent).getHeight();
			xWidht = ((ComponentMap) clContent).getWidth();
		}
		else if(clContent instanceof ComponentDataGrid) {
			yHeight = 150;
			xWidht = 350;
		}
		else if(clContent instanceof ComponentGauge) {
			yHeight = ((ComponentGauge) clContent).getHeight();
			xWidht = ((ComponentGauge) clContent).getWidth();
		}
		else if(clContent instanceof ComponentReport) {
			yHeight = 100;
			xWidht = 200;
		}
		else if(clContent instanceof ComponentKpi) {
			yHeight = 100;
			xWidht = 200;
		}
		else if(clContent instanceof ComponentJsp) {
			yHeight = 100;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentD4C) {
			yHeight = 100;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentFaView) {
			yHeight = 100;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentMapWMS) {
			yHeight = ((ComponentMapWMS) clContent).getHeight();
			xWidht = ((ComponentMapWMS) clContent).getWidth();
		}
		else if(clContent instanceof ComponentLink) {
			yHeight = 80;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentButtonDefinition) {
			yHeight = 100;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentTimer) {
			yHeight = 70;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentStyledTextInput) {
			yHeight = 70;
			xWidht = 150;
		}
		else if(clContent instanceof ComponentComment) {
			yHeight = 170;
			xWidht = 250;
		}
		else if(clContent instanceof ComponentFlourish) {
			yHeight = 100;
			xWidht = 150;
		}

	}

	public void checkTable(Table table, String pageName) throws Exception {

		for(int nbrCell = 0; nbrCell < table.getDetailsRows().size(); nbrCell++) {

			List<Cell> clL = new ArrayList<Cell>();
			List<Cell> cel = table.getDetailsRows().get(nbrCell);
			for(int cellSize = 0; cellSize < cel.size(); cellSize++) {
				Cell cl = cel.get(cellSize);
				IBaseElement contentCell;

				if(cl == null || cl.getContent().size() == 0 || cel.get(cellSize) == null) {

				}
				else {
					if(cl.getContent().size() == 0) {

					}
					else if(cl.getContent().size() != 0) {
						if(cl.getContent().size() == cellSize) {
							contentCell = cl.getContent().get(cellSize - 1);
						}
						else {
							contentCell = cl.getContent().get(cellSize);
						}

						checkInstanceOf(contentCell);
						cl.setSize(xWidht, yHeight);

						int xp = xPosition;
						int yp = yPosition;
						cl.setPosition(xp, yp);
						clL.add(cl);
						Cell newcl = cl;

						if(cellSize == 0) {
							newcl.setPosition(xPosition, yPosition);

							yPosition = newcl.getSize().y + 10;
						}
						else {
							newcl.setPosition(xPosition, yPosition);
							yPosition = yPosition + 10 + clL.get(cellSize).getSize().y;
						}
						currentModel.addToContent(newcl);
						// XXX
						// fdmN = new FdModel(fdMulti.getFdModel().getStructureFactory());
						// fdmN.addToContent(newcl);
						// fdmtemp.addToContent(newcl);

					}
				}
			}

		}
		// currentModel.setName(pageName);
		// fdmtemp.setName(pageName);

		yPosition = 0;
		xPosition = 0;

	}

	private void checkFolder(Folder d) throws Exception {
		cssClasFold = d.getCssClass();
		Folder newFolder = fdMulti.getFdModel().getStructureFactory().createFolder(d.getName());
		newFolder.setCssClass(cssClasFold);
		fdMulti.getFdModel().addToContent(newFolder);

		for(int fdSize = 0; fdSize < d.getContent().size(); fdSize++) {
			FolderPage folder = (FolderPage) d.getContent().get(fdSize);

			FolderPage newPage = fdMulti.getFdModel().getStructureFactory().createFolderPage(folder.getName());
			newPage.setName(folder.getName());
			newPage.setTitle(folder.getTitle());
			newFolder.addToContent(newPage);

			if(!folder.getContent().isEmpty()) {
				// fdmtemp = new FdModel(fdMulti.getFdModel().getStructureFactory());
				// fdmtemp.setName(folder.getContent().get(0).getName());
				// fdMulti.addPageModel(fdmtemp);
				// newPage.addToContent(fdmtemp);

				currentModel = new FdModel(fdMulti.getFdModel().getStructureFactory());
				currentModel.setName(folder.getContent().get(0).getName());
				fdMulti.addPageModel(currentModel);
				newPage.addToContent(currentModel);

			}

			checkFolderPage(folder, folder.getName());
		}

	}

	private void checkFolderPage(FolderPage folder, String pageName) throws Exception {

		for(int folderSize = 0; folderSize < folder.getContent().size(); folderSize++) {
			folder.getContent().get(folderSize);
			checkPage(folder, folder.getTitle(), pageName);
		}
	}

	private void checkPage(FolderPage folderPage, String pageTitle, String pageName) throws Exception {

		for(int pageSize = 0; pageSize < folderPage.getContent().size(); pageSize++) {
			checkTable((Table) ((FdModel) folderPage.getContent().get(pageSize)).getContent().get(pageSize), pageTitle);
		}
	}

}
