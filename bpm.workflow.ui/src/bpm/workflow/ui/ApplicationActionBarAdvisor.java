package bpm.workflow.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.repository.ui.versionning.VersionningManager;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IFileServerGet;
import bpm.workflow.runtime.model.IFileServerPut;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.model.activities.CheckColumnActivity;
import bpm.workflow.runtime.model.activities.CheckPathActivity;
import bpm.workflow.runtime.model.activities.CheckTableActivity;
import bpm.workflow.runtime.model.activities.DeleteFileActivity;
import bpm.workflow.runtime.model.activities.DeleteFolderActivity;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.PingHostActivity;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.model.activities.StarterActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatExcelActivity;
import bpm.workflow.runtime.model.activities.filemanagement.ConcatPDFActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GatewayActivity;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.actions.ActionCopy;
import bpm.workflow.ui.actions.ActionSave;
import bpm.workflow.ui.dialogs.DialogMetrics;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowModelEditorPart;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.preferences.PreferencesConstants;
import bpm.workflow.ui.views.ResourceViewPart;
import bpm.workflow.ui.wizards.WorkflowWizard;
import bpm.workflow.ui.wizards.biwmanagement.ManagementWizard;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction createnew, preferences;
	private IWorkbenchAction exit, about, importModel, exportModel;
	private Action open, save, checkMetrics, saveAs, saveImage, savePDF, close, copy, paste, cut, documentProperties;
	private Action test;
	private IAction deploy;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		try {
			exit = ActionFactory.QUIT.create(window);
			register(exit);
		} catch(Exception e) {
			e.printStackTrace();
		}

		deploy = new Action(Messages.ApplicationActionBarAdvisor_6) {
			@Override
			public void run() {
				ManagementWizard wizard = new ManagementWizard();
				WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
				dial.open();
			}
		};

		importModel = ActionFactory.IMPORT.create(window);
		register(importModel);

		createnew = ActionFactory.NEW.create(window);
		createnew.setText(Messages.ApplicationActionBarAdvisor_0);
		register(createnew);

		preferences = ActionFactory.PREFERENCES.create(window);
		register(preferences);

		exportModel = ActionFactory.EXPORT.create(window);
		register(exportModel);

		about = ActionFactory.ABOUT.create(window);
		register(about);

		copy = new ActionCopy();

		paste = new Action(Messages.ApplicationActionBarAdvisor_1) {
			public void run() {}
		};

		cut = new Action(Messages.ApplicationActionBarAdvisor_2) {
			public void run() {}
		};

		save = new ActionSave();
		save.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SAVE_16));

		documentProperties = new Action(Messages.ApplicationActionBarAdvisor_3) {

			@Override
			public void run() {

				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if(part == null) {
					return;
				}

				WorkflowEditorInput doc = (WorkflowEditorInput) part.getEditorInput();

				if(doc == null) {
					return;
				}

				WorkflowWizard wizard = new WorkflowWizard(doc);

				wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

				WizardDialog dialog = new WizardDialog(sh, wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(""); //$NON-NLS-1$

				if(dialog.open() == WizardDialog.OK) {

				}

			}

		};

		saveAs = new Action(Messages.ApplicationActionBarAdvisor_4) {
			public void run() {
				IEditorPart p = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				p.doSaveAs();
			}
		};
		saveAs.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SAVE_AS_16));

		/*
		 * Save the workspace as an image
		 */
		saveImage = new Action(Messages.ApplicationActionBarAdvisor_11) {
			public void run() {
				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				String filePath = null;
				FileDialog fd = new FileDialog(sh, SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.jpg", "*.bmp", "*.tif", "*.png", "*.gif" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				filePath = fd.open();

				if(filePath != null) {
					try {

						IFigure figure = ((ScalableFreeformRootEditPart) WorkflowModelEditorPart.part).getLayer(LayerConstants.SCALABLE_LAYERS);

						Image img1 = new Image(null, new org.eclipse.swt.graphics.Rectangle(0, 0, figure.getBounds().width, figure.getBounds().height + 1));
						GC gc1 = new GC(img1);

						Graphics grap1 = new SWTGraphics(gc1);
						figure.paint(grap1);

						ImageLoader loader1 = new ImageLoader();
						loader1.data = new ImageData[] { img1.getImageData() };

						if(filePath.endsWith("bmp")) { //$NON-NLS-1$
							if(!filePath.endsWith(".bmp")) { //$NON-NLS-1$
								filePath += ".bmp"; //$NON-NLS-1$
							}
							loader1.save(filePath, SWT.IMAGE_BMP);
						}
						else if(filePath.endsWith("tif")) { //$NON-NLS-1$
							if(!filePath.endsWith(".tif")) { //$NON-NLS-1$
								filePath += ".tif"; //$NON-NLS-1$
							}
							loader1.save(filePath, SWT.IMAGE_TIFF);
						}
						else if(filePath.endsWith("png")) { //$NON-NLS-1$
							if(!filePath.endsWith(".png")) { //$NON-NLS-1$
								filePath += ".png"; //$NON-NLS-1$
							}
							loader1.save(filePath, SWT.IMAGE_PNG);
						}
						else if(filePath.endsWith("gif")) { //$NON-NLS-1$
							if(!filePath.endsWith(".gif")) { //$NON-NLS-1$
								filePath += ".gif"; //$NON-NLS-1$
							}
							loader1.save(filePath, SWT.IMAGE_GIF);
						}
						else if(filePath.endsWith("jpg")) { //$NON-NLS-1$
							if(!filePath.endsWith(".jpg")) { //$NON-NLS-1$
								filePath += ".jpg"; //$NON-NLS-1$
							}
							loader1.save(filePath, SWT.IMAGE_JPEG);
						}
						else {
							img1.dispose();
							gc1.dispose();
							return;
						}

						img1.dispose();
						gc1.dispose();

					} catch(Exception e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		saveImage.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.PAINT_16));

		/*
		 * Save the process definition in a PDF
		 */
		savePDF = new Action(Messages.ApplicationActionBarAdvisor_32) {
			public void run() {
				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				String filePath = null;
				FileDialog fd = new FileDialog(sh, SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.pdf" }); //$NON-NLS-1$
				filePath = fd.open();

				if(filePath != null) {
					try {

						if(!filePath.endsWith(".pdf")) { //$NON-NLS-1$
							filePath += ".pdf"; //$NON-NLS-1$
						}

						IFigure figure = ((ScalableFreeformRootEditPart) WorkflowModelEditorPart.part).getLayer(LayerConstants.SCALABLE_LAYERS);

						Image img1 = new Image(null, new org.eclipse.swt.graphics.Rectangle(0, 0, figure.getBounds().width, figure.getBounds().height + 1));
						GC gc1 = new GC(img1);

						Graphics grap1 = new SWTGraphics(gc1);
						figure.paint(grap1);
						ImageLoader loader1 = new ImageLoader();
						loader1.data = new ImageData[] { img1.getImageData() };
						String imagepath = filePath + ".jpg"; //$NON-NLS-1$
						loader1.save(imagepath, SWT.IMAGE_JPEG);
						img1.dispose();
						gc1.dispose();

						Font BOLD_UNDERLINED = new Font(Font.FontFamily.TIMES_ROMAN, 36, Font.BOLD | Font.UNDERLINE);

						Font NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 12);

						Document document = new Document();

						PdfWriter.getInstance(document, new FileOutputStream(filePath));

						document.open();

						com.itextpdf.text.Image imageBIW = com.itextpdf.text.Image.getInstance("icons/biw.gif"); //$NON-NLS-1$
						document.add(imageBIW);

						WorkflowModel modelactuel = Activator.getDefault().getCurrentInput().getWorkflowModel();

						Phrase director = new Phrase();
						director.add(new Chunk("                 ", NORMAL)); //$NON-NLS-1$
						director.add(new Chunk(modelactuel.getName(), BOLD_UNDERLINED));
						director.add(new Chunk(" , ", NORMAL)); //$NON-NLS-1$
						java.util.Date date = new java.util.Date();
						director.add(new Chunk(date.toLocaleString(), NORMAL));
						document.add(director);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);

						Phrase description = new Phrase();
						description.add(new Chunk(Messages.ApplicationActionBarAdvisor_40 + modelactuel.getDescription(), NORMAL));
						document.add(description);

						document.addTitle(modelactuel.getName());
						document.addSubject(modelactuel.getDescription());
						document.addProducer();

						com.itextpdf.text.Image imagePDF = com.itextpdf.text.Image.getInstance(imagepath);
						document.add(imagePDF);

						document.newPage();

						Phrase servertitle = new Phrase();
						servertitle.add(new Chunk(Messages.ApplicationActionBarAdvisor_41, BOLD_UNDERLINED));
						document.add(servertitle);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);

						for(Server server : ListServer.getInstance().getServers()) {
							Phrase serverdesc = new Phrase();
							serverdesc.add(new Chunk(Messages.ApplicationActionBarAdvisor_42 + server.getName(), NORMAL));
							document.add(serverdesc);
							document.add(Chunk.NEWLINE);

							Phrase serverurl = new Phrase();
							serverurl.add(new Chunk(Messages.ApplicationActionBarAdvisor_43 + server.getUrl(), NORMAL));
							document.add(serverurl);
							document.add(Chunk.NEWLINE);

							if(server instanceof FileServer) {
								Phrase serverrep = new Phrase();
								serverrep.add(new Chunk(Messages.ApplicationActionBarAdvisor_44 + ((FileServer) server).getRepertoireDef(), NORMAL));
								document.add(serverrep);
								document.add(Chunk.NEWLINE);

								Phrase serverport = new Phrase();
								serverport.add(new Chunk(Messages.ApplicationActionBarAdvisor_45 + ((FileServer) server).getPort(), NORMAL));
								document.add(serverport);
								document.add(Chunk.NEWLINE);
							}
							if(server instanceof FreemetricServer) {
								Phrase serverbase = new Phrase();
								serverbase.add(new Chunk(Messages.ApplicationActionBarAdvisor_46 + ((FreemetricServer) server).getDataBaseName(), NORMAL));
								document.add(serverbase);
								document.add(Chunk.NEWLINE);
							}
							if(server instanceof DataBaseServer) {
								Phrase serverbase = new Phrase();
								serverbase.add(new Chunk(Messages.ApplicationActionBarAdvisor_47 + ((DataBaseServer) server).getDataBaseName(), NORMAL));
								document.add(serverbase);
								document.add(Chunk.NEWLINE);
								Phrase serverport = new Phrase();
								serverport.add(new Chunk(Messages.ApplicationActionBarAdvisor_48 + ((DataBaseServer) server).getPort(), NORMAL));
								document.add(serverport);
								document.add(Chunk.NEWLINE);
							}
							document.add(Chunk.NEWLINE);
							document.add(Chunk.NEWLINE);

						}
						document.newPage();

						Phrase variablestitle = new Phrase();
						variablestitle.add(new Chunk(Messages.ApplicationActionBarAdvisor_49, BOLD_UNDERLINED));
						document.add(variablestitle);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);

						for(Variable variable : ListVariable.getInstance().getVariables()) {
							Phrase variablename = new Phrase();
							variablename.add(new Chunk(Messages.ApplicationActionBarAdvisor_50 + variable.getName(), NORMAL));
							document.add(variablename);
							document.add(Chunk.NEWLINE);

							Phrase variabletype = new Phrase();
							variabletype.add(new Chunk(Messages.ApplicationActionBarAdvisor_51 + variable.getType(), NORMAL));
							document.add(variabletype);
							document.add(Chunk.NEWLINE);

							Phrase variablevaleur = new Phrase();
							variablevaleur.add(new Chunk(Messages.ApplicationActionBarAdvisor_52 + variable.getValues().get(0), NORMAL));
							document.add(variablevaleur);

							document.add(Chunk.NEWLINE);
							document.add(Chunk.NEWLINE);

						}

						document.newPage();

						Phrase actvitestitle = new Phrase();
						actvitestitle.add(new Chunk(Messages.ApplicationActionBarAdvisor_53, BOLD_UNDERLINED));
						document.add(actvitestitle);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);
						document.add(Chunk.NEWLINE);

						for(IActivity activite : modelactuel.getActivities().values()) {

							Phrase activitename = new Phrase();
							activitename.add(new Chunk(Messages.ApplicationActionBarAdvisor_54 + activite.getName(), NORMAL));
							document.add(activitename);
							document.add(Chunk.NEWLINE);

							if(activite instanceof SqlActivity) {
								SqlActivity act = (SqlActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_55 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activitequery = new Phrase();
								activitequery.add(new Chunk(Messages.ApplicationActionBarAdvisor_56 + act.getQuery(), NORMAL));
								document.add(activitequery);
								document.add(Chunk.NEWLINE);

								for(String sqlfield : act.getMapping().keySet()) {
									Phrase activiteVariable = new Phrase();
									activiteVariable.add(new Chunk(Messages.ApplicationActionBarAdvisor_57 + act.getMapping().get(sqlfield), NORMAL));
									document.add(activiteVariable);
									document.add(Chunk.NEWLINE);
								}
							}

							if(activite instanceof BiWorkFlowActivity) {
								BiWorkFlowActivity act = (BiWorkFlowActivity) activite;

								Phrase activitequery = new Phrase();
								activitequery.add(new Chunk(Messages.ApplicationActionBarAdvisor_58 + act.getItemName(), NORMAL));
								document.add(activitequery);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof StarterActivity) {
								StarterActivity act = (StarterActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_59 + act.getPath(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_60 + act.getPlateforme(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

								Phrase activitetype = new Phrase();
								activitetype.add(new Chunk(Messages.ApplicationActionBarAdvisor_61 + act.getTypeEXE(), NORMAL));
								document.add(activitetype);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof ExcelAggregateActivity) {
								ExcelAggregateActivity act = (ExcelAggregateActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_62 + act.getPath(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof ConcatExcelActivity) {
								Phrase activiteserver = new Phrase();
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);
							}
							if(activite instanceof ConcatPDFActivity) {

								Phrase activiteserver = new Phrase();
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);
							}
							if(activite instanceof CalculationActivity) {
								CalculationActivity act = (CalculationActivity) activite;

								List<Script> scripts = act.getScripts();

								for(Script script : scripts) {

									Phrase activiteserver = new Phrase();
									activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_63 + script.getName(), NORMAL));
									document.add(activiteserver);
									document.add(Chunk.NEWLINE);

									Phrase activiteplatform = new Phrase();
									activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_64 + script.getScriptFunction(), NORMAL));
									document.add(activiteplatform);
									document.add(Chunk.NEWLINE);
								}
							}
							if(activite instanceof DeleteFileActivity) {
								DeleteFileActivity act = (DeleteFileActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_65 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_66 + act.getPath(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof DeleteFolderActivity) {
								DeleteFolderActivity act = (DeleteFolderActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_67 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_68 + act.getPath(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof GatewayActivity) {
								GatewayActivity act = (GatewayActivity) activite;

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_69 + act.getItemName(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							
							if(activite instanceof MetadataToD4CActivity) {
								MetadataToD4CActivity act = (MetadataToD4CActivity) activite;

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_69 + act.getQueryName(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							
							if(activite instanceof ReportActivity) {
								ReportActivity act = (ReportActivity) activite;

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_70 + act.getItemName(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof CheckPathActivity) {
								CheckPathActivity act = (CheckPathActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_71 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_72 + act.getPath(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof CheckTableActivity) {
								CheckTableActivity act = (CheckTableActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_73 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_74 + act.getPath(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

							}
							if(activite instanceof CheckColumnActivity) {
								CheckColumnActivity act = (CheckColumnActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_75 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activiteplatform = new Phrase();
								activiteplatform.add(new Chunk(Messages.ApplicationActionBarAdvisor_76 + act.getTable(), NORMAL));
								document.add(activiteplatform);
								document.add(Chunk.NEWLINE);

								Phrase activitecolonne = new Phrase();
								activitecolonne.add(new Chunk(Messages.ApplicationActionBarAdvisor_77 + act.getColumnName(), NORMAL));
								document.add(activitecolonne);
								document.add(Chunk.NEWLINE);

								Phrase activitetype = new Phrase();
								activitetype.add(new Chunk(Messages.ApplicationActionBarAdvisor_78 + act.getTypeOfColumn(), NORMAL));
								document.add(activitetype);
								document.add(Chunk.NEWLINE);

							}

							if(activite instanceof PingHostActivity) {
								PingHostActivity act = (PingHostActivity) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_79 + act.getPath(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

							}

							if(activite instanceof IFileServerGet) {
								IFileServerGet act = (IFileServerGet) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_80 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);

								Phrase activitepath = new Phrase();
								activitepath.add(new Chunk(Messages.ApplicationActionBarAdvisor_81 + act.getPathSpecific(), NORMAL));
								document.add(activitepath);
								document.add(Chunk.NEWLINE);

								Phrase activiteEx = new Phrase();
								activiteEx.add(new Chunk(Messages.ApplicationActionBarAdvisor_82 + act.getExtension(), NORMAL));
								document.add(activiteEx);
								document.add(Chunk.NEWLINE);

							}

							if(activite instanceof IFileServerPut) {
								IFileServerPut act = (IFileServerPut) activite;

								Phrase activiteserver = new Phrase();
								activiteserver.add(new Chunk(Messages.ApplicationActionBarAdvisor_83 + act.getServer().getName(), NORMAL));
								document.add(activiteserver);
								document.add(Chunk.NEWLINE);
							}

							document.add(Chunk.NEWLINE);
							document.add(Chunk.NEWLINE);

						}

						document.close();
						File todelete = new File(filePath + ".jpg"); //$NON-NLS-1$
						todelete.delete();

					} catch(Exception e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		savePDF.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.EXPORTPDF_16));

		/*
		 * Open a workflow (.biw)
		 */
		open = new Action(Messages.ApplicationActionBarAdvisor_92) {
			public void run() {

				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				FileDialog fd = new FileDialog(sh);
				fd.setFilterExtensions(new String[] { "*.biw*" }); //$NON-NLS-1$
				String fileName = fd.open();

				if(fileName == null) {
					return;
				}

				if(!fileName.contains(".biw")) { //$NON-NLS-1$
					fileName = fileName + ".biw"; //$NON-NLS-1$
				}

				try {

					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					page.openEditor(new WorkflowEditorInput(new File(fileName)), WorkflowMultiEditorPart.ID, true);
					addFileToList(fileName);

					Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(fileName) != null);
					ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
					if(v != null) {
						v.refresh();
					}

					ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
					SessionSourceProvider sourceProvider = (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
					sourceProvider.setModelOpened(true);

				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		};
		open.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.OPEN_16));

		close = new Action(Messages.ApplicationActionBarAdvisor_96) {
			public void run() {

				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

					IEditorPart editor = page.getActiveEditor();
					page.closeEditor(editor, true);

				} catch(Exception e) {
					e.printStackTrace();
				}

			}
		};
		close.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CLOSE_16));

		test = new Action(Messages.ApplicationActionBarAdvisor_97) {
			public void run() {
				WorkflowModel model = (WorkflowModel) Activator.getDefault().getCurrentModel();

				String msg = ""; //$NON-NLS-1$

				for(IActivity a : model.getActivities().values()) {
					msg += a.getProblems();
				}

				if(!msg.equals("")) { //$NON-NLS-1$
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ApplicationActionBarAdvisor_100, msg);
				}
				else {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ApplicationActionBarAdvisor_101, Messages.ApplicationActionBarAdvisor_102);
				}
			}
		};
		test.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.LOUPE));

		/*
		 * Check the metrics collected from the execution of a workflow (the target workflow has to be imported before)
		 */
		checkMetrics = new Action(Messages.ApplicationActionBarAdvisor_103) {
			public void run() {
				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				DialogMetrics dial = new DialogMetrics(sh, Activator.getDefault().getCurrentDirect());
				dial.open();

			}
		};
		checkMetrics.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.CHECKMETRICS_16));

	}

	protected void fillMenuBar(IMenuManager menuBar) {

		MenuManager recentMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_104, IWorkbenchActionConstants.MB_ADDITIONS);

		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_105, IWorkbenchActionConstants.M_FILE); //$NON-NLS-2$
		fileMenu.add(createnew);
		fileMenu.add(open);
		fileMenu.add(recentMenu);

		fileMenu.add(new Separator());
		fileMenu.add(close);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(new Separator());
		fileMenu.add(saveImage);
		fileMenu.add(savePDF);
		fileMenu.add(new Separator());

		fileMenu.add(exportModel);
		fileMenu.add(importModel);

		fileMenu.add(new Separator());
		fileMenu.add(preferences);

		fileMenu.add(new Separator());
		fileMenu.add(exit);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String[] recent = new String[] { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		for(final String s : recent) {
			if(!s.trim().equals("")) { //$NON-NLS-1$
				Action a;
				a = new Action(s) {

					public void run() {

						try {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							page.openEditor(new WorkflowEditorInput(new File(s)), WorkflowMultiEditorPart.ID, true);
							Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(s) != null);
							addFileToList(s);

							ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
							if(v != null) {
								v.refresh();
							}

						} catch(Exception e) {
							e.printStackTrace();
						}

					}

				};
				a.setText(s);
				recentMenu.add(a);
			}
		}

		menuBar.add(fileMenu);

		MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_106, IWorkbenchActionConstants.EDIT_START);
		menuBar.add(editMenu);
		editMenu.add(copy);
		editMenu.add(paste);
		editMenu.add(cut);
		editMenu.add(new Separator());
		editMenu.add(documentProperties);

		MenuManager deploymentMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_107, IWorkbenchActionConstants.IMPORT_EXT);
		menuBar.add(deploymentMenu);
		deploymentMenu.add(deploy);

		MenuManager MetricsMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_108, IWorkbenchActionConstants.ADD_TASK); //$NON-NLS-2$

		menuBar.add(MetricsMenu);
		MetricsMenu.add(checkMetrics);

		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_109, IWorkbenchActionConstants.M_HELP); //$NON-NLS-2$

		menuBar.add(helpMenu);
		helpMenu.add(about);

	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
		toolbar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));

		toolbar.add(createnew);
		toolbar.add(open);
		toolbar.add(saveAs);
		toolbar.add(save);
		toolbar.add(close);
		toolbar.add(test);
		// toolbar.add(alert);
	}

	private void addFileToList(String path) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(path))
				isEverListed = true;
		}

		if(!isEverListed) {
			list[4] = list[3];
			list[3] = list[2];
			list[2] = list[1];
			list[1] = list[0];
			list[0] = path;
		}

		store.setValue(PreferencesConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferencesConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferencesConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferencesConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferencesConstants.P_RECENTFILE5, list[4]);

	}
}
