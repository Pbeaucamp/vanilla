package bpm.fd.design.ui.rcp.dialogs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieMarker;
import bpm.fd.api.core.model.parsers.DictionaryParser;
import bpm.fd.api.core.model.parsers.FdModelParser;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.rcp.Messages;
import bpm.vanilla.platform.core.utils.IOWriter;

public class DialogOpenModel extends Dialog {
	private Text projectPath;
	private Text projectName;
	private File projectFolder;

	private IProject project;

	private MultiPageFdProject fdProject;

	public DialogOpenModel(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Canvas c = new Canvas(main, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		c.setBackgroundImage(reg.get(Icons.small_splash));

		Composite container = new Composite(main, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogOpenModel_0);

		projectName = new Text(container, SWT.BORDER);
		projectName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		projectName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if(fdProject != null) {
					fdProject.getProjectDescriptor().setProjectName(projectName.getText());
				}
				updateButtons();

			}
		});

		l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogOpenModel_1);

		projectPath = new Text(container, SWT.BORDER);
		projectPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		projectPath.setEnabled(false);

		Button bModel = new Button(container, SWT.PUSH);
		bModel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bModel.setText("..."); //$NON-NLS-1$
		bModel.addSelectionListener(new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				if(projectFolder != null) {
					fd.setFilterPath(projectFolder.getAbsolutePath());
				}
				fd.setFilterExtensions(new String[]{"*.project", "*.fdpackage"});
				String name = fd.open();

				if(name != null) {
					projectFolder = new File(name);
					try {
						if(name.endsWith(".fdpackage")) {

							File f = new File("temp");
							try {
								f.delete();
								f.mkdir();
							} catch(Exception ex) {
								
							}
							
							ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(name)));
							ZipEntry ze = zis.getNextEntry();
							String projectFolderName = "";
							
							while(ze != null) {

								String fileName = ze.getName();
								File newFile = new File("temp" + File.separator + fileName);
								new File(newFile.getParent()).mkdirs();
								projectFolderName = newFile.getParent();
								
								newFile.createNewFile();
								FileOutputStream fos = new FileOutputStream(newFile);

								int len;
								byte[] buffer = new byte[1024];
								while((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}

								fos.close();
								ze = zis.getNextEntry();
							}

							zis.closeEntry();
							zis.close();
							projectFolder = new File(projectFolderName);
						}
						else {
							projectFolder = new File(name.substring(0, name.lastIndexOf("\\.")));
						}

						checkProjectFolder();
						projectPath.setText(name);
					} catch(Exception ex) {
						fdProject = null;
						updateButtons();
						ex.printStackTrace();
						MessageDialog.openInformation(getShell(), "Checking folder content", "The folder " + name + " does not contain any FreeDashboard Project." + ex.getMessage());
					}
				}

			}

		});

		return container;
	}

	private void checkProjectFolder() throws Exception {
		fdProject = null;
		boolean modelExists = false;
		boolean dictionaryExists = false;

		List<String> modelNames = new ArrayList<String>();
		String dictionary = null;
		for(String s : projectFolder.list()) {
			if(s.endsWith(".freedashboard")) {
				modelNames.add(s);
				modelExists = true;
			}
			if(s.endsWith(".dictionary")) {
				dictionaryExists = true;
				dictionary = s;
			}
		}

		if(!modelExists) {
			projectName.setText("");
			updateButtons();
			throw new Exception("The given folder does not contains any FreeDashboard model.");
		}
		if(!dictionaryExists) {
			projectName.setText("");
			updateButtons();
			throw new Exception("The given folder does not contains any Dictionary definition.");
		}

		// look for the main model file
		File mainFile = null;

		for(String model : modelNames) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				FileInputStream fis = new FileInputStream(new File(projectFolder, model));
				IOWriter.write(fis, bos, true, true);

				Document modelDoc = DocumentHelper.parseText(bos.toString("UTF-8"));
				if(mainFile == null) {
					mainFile = new File(projectFolder, modelDoc.getRootElement().element("projectDescriptor").element("modelName").getText() + ".freedashboard");
					break;
				}
			} catch(Exception ex) {
				throw new Exception("Failed to check xml for file " + model);
			}

		}

		DictionaryParser dicParser = new DictionaryParser();
		Dictionary dico = dicParser.parse(new FileInputStream(new File(projectFolder, dictionary)));

		FactoryStructure factory = new FactoryStructure();

		FdModelParser modelParser = new FdModelParser(factory, dico);
		fdProject = (MultiPageFdProject) modelParser.parse(new FileInputStream(mainFile));
		fdProject.setDictionary(dico);

		for(String s : modelNames) {
			if(!s.equals(mainFile.getName())) {
				modelParser.parse(new FileInputStream(new File(projectFolder, s)));
				fdProject.addPageModel(modelParser.getModel());
			}
		}

		// resources
		for(String s : projectFolder.list()) {
			if(s.endsWith(".freedashboard") || s.endsWith(".dictionary")) {
				continue;
			}
			IResource r = null;
			if(s.endsWith(".css")) {
				r = new FileCSS(s, new File(projectFolder, s));
			}
			else if(s.endsWith(".js")) {
				r = new FileJavaScript(s, new File(projectFolder, s));
			}
			else if(s.endsWith(".properties")) {
				int i = s.indexOf("_");

				String localeName = null;
				if(i >= 0) {
					localeName = s.substring(0, i);
					fdProject.addLocale(localeName);
				}
				r = new FileProperties(s, localeName, new File(projectFolder, s));
			}
			else {
				r = new FileImage(s, new File(projectFolder, s));
			}
			if(r != null) {
				fdProject.addResource(r);
			}
		}

		// TODO Trick page models
		try {
			if(fdProject instanceof MultiPageFdProject) {
				if(fdProject.getFdModel() != null) {
					for(IBaseElement e : fdProject.getFdModel().getContent()) {
						if(e instanceof Folder) {
							for(IBaseElement _e : ((Folder) e).getContent()) {
								if(_e instanceof FolderPage) {
									((FolderPage) _e).getContent();
								}
							}
						}
					}
				}

				for(IComponentDefinition dg : fdProject.getDictionary().getComponents(ComponentDataGrid.class)) {
					((ComponentDataGrid) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentDataGrid) dg).getDrillInfo().getModelPageName()));
				}
				for(IComponentDefinition dg : fdProject.getDictionary().getComponents(ComponentMap.class)) {
					((ComponentMap) dg).getDrillInfo().setModelPage(((MultiPageFdProject) project).getPageModel(((ComponentMap) dg).getDrillInfo().getModelPageName()));
				}
				for(IComponentDefinition dg : fdProject.getDictionary().getComponents(ComponentChartDefinition.class)) {
					ChartDrillInfo drill = ((ComponentChartDefinition) dg).getDrillDatas();
					if(drill.getTypeTarget() == TypeTarget.TargetPopup && drill.getUrl() != null) {
						drill.setTargetModelPage(((MultiPageFdProject) project).getPageModel(drill.getUrl()));
					}
				}
				for(IComponentDefinition dg : fdProject.getDictionary().getComponents(ComponentOsmMap.class)) {
					for(OsmDataSerie serie : ((OsmData)((ComponentOsmMap)dg).getDatas()).getSeries()) {
						if(serie instanceof OsmDataSerieMarker) {
							if(((OsmDataSerieMarker)serie).getTargetPageName() != null) {
								String target = ((OsmDataSerieMarker)serie).getTargetPageName();
								((OsmDataSerieMarker)serie).setTargetPage(((MultiPageFdProject) project).getPageModel(target));
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		projectName.setText(fdProject.getProjectDescriptor().getProjectName());
	}

	private void updateButtons() {
		boolean canFInish = fdProject != null;
		getButton(IDialogConstants.OK_ID).setEnabled(canFInish);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot r = workspace.getRoot();

		project = r.getProject(projectName.getText());
		if(project.exists()) {
			if(MessageDialog.openQuestion(getShell(), Messages.DialogOpenModel_10, Messages.DialogOpenModel_11)) {
				try {
					project.delete(true, true, null);
				} catch(CoreException e) {
					e.printStackTrace();
				}
			}
			else {
				return;
			}
		}

		// rename the project
		fdProject.getProjectDescriptor().setProjectName(projectName.getText());

		try {
			project.create(null);
			project.open(null);
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {

			/*
			 * create the Model file
			 */
			IFile f = project.getFile(fdProject.getFdModel().getName() + ".freedashboard");
			try {
				Document d = DocumentHelper.createDocument(fdProject.getFdModel().getElement());
				f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
			} catch(Exception e) {
				throw new Exception(Messages.DialogOpenModel_19, e);
			}
			/*
			 * create each page model
			 */
			for(FdModel m : fdProject.getPagesModels()) {
				f = project.getFile(m.getName() + ".freedashboard");
				try {
					Document d = DocumentHelper.createDocument(m.getElement());
					f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
				} catch(Exception e) {
					throw new Exception(Messages.DialogOpenModel_19, e);
				}
			}
			/*
			 * create Dictionary
			 */
			f = project.getFile(fdProject.getProjectDescriptor().getDictionaryName() + ".dictionary"); //$NON-NLS-1$
			try {
				Document d = DocumentHelper.createDocument(fdProject.getDictionary().getElement());
				f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
			} catch(Exception e) {
				throw new Exception(Messages.DialogOpenModel_23, e);
			}
			/*
			 * create resource files
			 */
			for(IResource res : fdProject.getResources()) {
				f = project.getFile(res.getName());
				try {
					f.create(new FileInputStream(res.getFile()), true, null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

		} catch(Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogOpenModel_24, Messages.DialogOpenModel_25 + ex.getMessage());
			return;
		}
		super.okPressed();
	}

	public IProject getIProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText(Messages.DialogOpenModel_26);
		updateButtons();
	}

}
