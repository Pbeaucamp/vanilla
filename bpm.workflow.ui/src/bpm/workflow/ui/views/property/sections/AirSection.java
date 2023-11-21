package bpm.workflow.ui.views.property.sections;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.workflow.runtime.model.activities.AirActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class AirSection extends AbstractPropertySection {

	private Node node;
	private int[] listId;
	private String[] listName;

	private CLabel labelAir, labelcomP4, labelcomP5, labelWriteImport;
	private Text select;
	private Combo choice, selectAir, selectModel, selectR;
	private Button btnApply, btnValid, btnEdit, btnBrowse, validPath;

	private Composite main, compR, compAir, compEditFile, compImport;

	public AirSection() {
	}

	/*
	 * Composite c: Choix Air / R Composite c1: Air choisi, affichage combo choix script Composite c2: Air choisi, affichage combo choix modele Composite c3: R choisi, affichage combo choix Ecrire/Importer Composite c4: R choisi, Ecrire choisi Composite c5: R choisi, Importation choisi
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		main = getWidgetFactory().createFlatFormComposite(parent);
		main.setLayout(new GridLayout());

		Composite compChoice = getWidgetFactory().createComposite(main, SWT.MIN);
		compChoice.setLayout(new GridLayout(2, false));

		CLabel labelChoice = getWidgetFactory().createCLabel(compChoice, Messages.AirSection_0);
		labelChoice.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 2));

		String[] choix = { "Air", "R" }; //$NON-NLS-1$  //$NON-NLS-2$

		choice = new Combo(compChoice, SWT.READ_ONLY);
		choice.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false));
		choice.setItems(choix);
		choice.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final AirActivity a = (AirActivity) node.getWorkflowObject();

				boolean compAirVisible = false;
				boolean compRVisible = false;
				if (choice.getSelectionIndex() == 0) {
					// AIR
					a.setChoice(0);
					compAirVisible = true;
				}
				else if (choice.getSelectionIndex() == 1) {
					a.setChoice(1);
					compRVisible = true;
				}

				updateUi(compAirVisible, compRVisible, false, false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		compAir = getWidgetFactory().createComposite(main, SWT.MIN);
		compAir.setLayout(new GridLayout(3, false));

		CLabel labelReqAir = getWidgetFactory().createCLabel(compAir, Messages.AirSection_1);
		labelReqAir.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		selectAir = new Combo(compAir, SWT.READ_ONLY);
		selectAir.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		RemoteSmartManager remoteSmart = Activator.getDefault().getAirRemote();
		try {
			List<RScript> ListScript = remoteSmart != null ? remoteSmart.getAllScripts() : new ArrayList<RScript>();
			List<String> ScriptTitle = new ArrayList<String>();
			String[] tabScriptTitle;
			int[] scriptId = new int[ListScript.size()];
			int cpt = 0;
			if (ListScript.size() == 0) {
				ScriptTitle.add("No R algorithm available"); //$NON-NLS-1$
			}
			else {
				for (RScript script : ListScript) {
					if (script.getScriptType().equals("R")) { //$NON-NLS-1$
						ScriptTitle.add(script.getName());
						scriptId[cpt] = script.getId();
						cpt++;
					}
				}
			}
			tabScriptTitle = new String[ScriptTitle.size()];
			for (int i = 0; i < ScriptTitle.size(); i++) {
				tabScriptTitle[i] = ScriptTitle.get(i);
			}

			selectAir.setItems(tabScriptTitle);
			listId = scriptId;
			listName = tabScriptTitle;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		btnApply = getWidgetFactory().createButton(compAir, Messages.AirSection_2, SWT.PUSH);
		btnApply.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		btnApply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AirActivity a = (AirActivity) node.getWorkflowObject();
				if (selectAir.getSelectionIndex() != -1) {
					a.setNameR(listName[selectAir.getSelectionIndex()]);
					a.setRId(listId[selectAir.getSelectionIndex()]);
					RemoteSmartManager remoteSmart = Activator.getDefault().getAirRemote();
					try {
						List<RScriptModel> listModel = remoteSmart.getRScriptModelsbyScript(a.getRId());
						String[] TabListModel = new String[listModel.size()];
						for (int i = 0; i < listModel.size(); i++) {
							TabListModel[i] = String.valueOf(listModel.get(i).getNumVersion());
						}

						selectModel.setItems(TabListModel);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					a.setNameR(null);
					a.setRId(-1);
				}
			}
		});

		// /////////////////////////////
		// ////////////C2///////////////
		// /////////////////////////////

		labelAir = getWidgetFactory().createCLabel(compAir, Messages.AirSection_3);
		labelAir.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		selectModel = new Combo(compAir, SWT.READ_ONLY);
		selectModel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		btnValid = getWidgetFactory().createButton(compAir, Messages.AirSection_4, SWT.PUSH);
		btnValid.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		btnValid.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AirActivity a = (AirActivity) node.getWorkflowObject();
				if (selectModel.getSelectionIndex() != -1) {
					String[] id = selectModel.getItems();
					a.setRSModelId(Integer.parseInt(id[selectModel.getSelectionIndex()]));
					((AirActivity) node.getWorkflowObject()).setChoice(0);
				}
				else {
					a.setRSModelId(-1);
				}
			}
		});

		// /////////////////////////////
		// ////////////C3///////////////
		// /////////////////////////////
		compR = getWidgetFactory().createComposite(main, SWT.TOP);
		compR.setLayout(new GridLayout(2, false));

		labelWriteImport = getWidgetFactory().createCLabel(compR, Messages.AirSection_5);
		labelWriteImport.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 2));

		String[] choixR = { Messages.AirSection_13, Messages.AirSection_14 };

		selectR = new Combo(compR, SWT.READ_ONLY);
		selectR.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		selectR.setItems(choixR);
		selectR.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean compEditFileVisible = false;
				boolean compImportVisible = false;
				if (selectR.getSelectionIndex() == 0) {
					compEditFileVisible = true;

					((AirActivity) node.getWorkflowObject()).setRChoice("E"); //$NON-NLS-1$
					if (((AirActivity) node.getWorkflowObject()).getScript() != null) {
						dialEcrire(((AirActivity) node.getWorkflowObject()).getScript());
					}
					else {
						dialEcrire();
					}
				}
				else {
					compImportVisible = true;
				}

				updateUi(false, true, compEditFileVisible, compImportVisible);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// /////////////////////////////
		// ////////////C4///////////////
		// /////////////////////////////
		compEditFile = getWidgetFactory().createComposite(compR, SWT.MIN);
		compEditFile.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		compEditFile.setLayout(new GridLayout(3, false));

		labelcomP4 = getWidgetFactory().createCLabel(compEditFile, Messages.AirSection_6);
		labelcomP4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		btnEdit = new Button(compEditFile, SWT.PUSH);
		btnEdit.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnEdit.setText(Messages.AirSection_7);
		btnEdit.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((AirActivity) node.getWorkflowObject()).getScript() != null) {
					dialEcrire(((AirActivity) node.getWorkflowObject()).getScript());
				}
				else {
					dialEcrire();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// /////////////////////////////
		// ////////////C5///////////////
		// /////////////////////////////
		compImport = getWidgetFactory().createComposite(compR, SWT.MIN);
		compImport.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		compImport.setLayout(new GridLayout(4, false));

		labelcomP5 = getWidgetFactory().createCLabel(compImport, Messages.AirSection_8);
		labelcomP5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		select = getWidgetFactory().createText(compImport, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		btnBrowse = new Button(compImport, SWT.PUSH);
		btnBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnBrowse.setText("..."); //$NON-NLS-1$
		btnBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				fd.setFilterExtensions(new String[] { "*.R" }); //$NON-NLS-1$

				String path = fd.open();

				if (fd != null) {
					select.setText(path);
				}
			}
		});

		validPath = new Button(compImport, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validPath.setText(Messages.AirSection_4);
		validPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((AirActivity) node.getWorkflowObject()).setPath(select.getText());
				String res = ""; //$NON-NLS-1$
				InputStreamReader flog = null;
				LineNumberReader llog = null;
				String myLine = null;
				try {
					flog = new InputStreamReader(new FileInputStream(select.getText()));
					llog = new LineNumberReader(flog);
					while ((myLine = llog.readLine()) != null) {
						res += myLine + "\n"; //$NON-NLS-1$
					}
				} catch (Exception ex) {
					// --- Gestion erreur lecture du fichier (fichier non existant, illisible, etc.)
					System.err.println("Error : " + ex.getMessage()); //$NON-NLS-1$
				}
				RScriptModel rs = new RScriptModel();
				rs.setScript(res);
				((AirActivity) node.getWorkflowObject()).setSaveScript(true);
				((AirActivity) node.getWorkflowObject()).setScript(rs);
				((AirActivity) node.getWorkflowObject()).setChoice(1);
				((AirActivity) node.getWorkflowObject()).setRChoice("I"); //$NON-NLS-1$
			}
		});

	}

	public void dialEcrire() {
		Shell sh = new Shell();
		sh.setText(Messages.AirSection_9);
		sh.setLayout(new FillLayout());

		Menu menu = new Menu(sh, SWT.BAR);
		MenuItem optionFichier = new MenuItem(menu, SWT.CASCADE);
		optionFichier.setText(Messages.AirSection_10);
		Menu menuFichier = new Menu(sh, SWT.DROP_DOWN);
		MenuItem optionSave = new MenuItem(menuFichier, SWT.PUSH);
		optionSave.setText(Messages.AirSection_11);
		optionFichier.setMenu(menuFichier);
		sh.setMenuBar(menu);

		final Text t = new Text(sh, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		t.setLayoutData(new GridData(GridData.FILL_BOTH));

		optionSave.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RScriptModel rs = new RScriptModel();
				rs.setScript(t.getText());
				((AirActivity) node.getWorkflowObject()).setScript(rs);
				((AirActivity) node.getWorkflowObject()).setSaveScript(true);
				labelcomP4.setText(Messages.AirSection_12);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		sh.open();
	}

	public void dialEcrire(RScriptModel rs) {
		Shell sh = new Shell();
		sh.setText(Messages.AirSection_9);
		sh.setLayout(new FillLayout());

		Menu menu = new Menu(sh, SWT.BAR);
		MenuItem optionFichier = new MenuItem(menu, SWT.CASCADE);
		optionFichier.setText(Messages.AirSection_10);
		Menu menuFichier = new Menu(sh, SWT.DROP_DOWN);
		MenuItem optionSave = new MenuItem(menuFichier, SWT.PUSH);
		optionSave.setText(Messages.AirSection_11);
		optionFichier.setMenu(menuFichier);
		sh.setMenuBar(menu);

		final Text t = new Text(sh, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		t.setText(rs.getScript());
		optionSave.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RScriptModel rs = new RScriptModel();
				rs.setScript(t.getText());
				rs.setDateVersion(new Date());
				rs.setNumVersion(1);
				((AirActivity) node.getWorkflowObject()).setScript(rs);
				((AirActivity) node.getWorkflowObject()).setSaveScript(true);
				labelcomP4.setText(Messages.AirSection_12);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		sh.open();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void refresh() {
		AirActivity a = (AirActivity) node.getWorkflowObject();
		if (a.getChoice() == 0) {
			// AIR
			choice.select(0);

			updateUi(true, false, false, false);

			RemoteSmartManager remoteSmart = Activator.getDefault().getAirRemote();
			try {
				List<RScript> ListScript = remoteSmart.getAllScripts();
				List<String> ScriptTitle = new ArrayList<String>();
				String[] TabScriptTitle;
				int[] ScriptId = new int[ListScript.size()];
				int cpt = 0;
				if (ListScript.size() == 0) {
					ScriptTitle.add("No R algorithm available"); //$NON-NLS-1$
				}
				else {
					for (RScript script : ListScript) {
						if (script.getScriptType().equals("R")) { //$NON-NLS-1$
							ScriptTitle.add(script.getName());
							ScriptId[cpt] = script.getId();
							cpt++;
						}
					}
				}
				TabScriptTitle = new String[ScriptTitle.size()];
				int selected = -1;
				for (int i = 0; i < ScriptTitle.size(); i++) {
					TabScriptTitle[i] = ScriptTitle.get(i);
					if (ScriptTitle.get(i).equals(a.getNameR())) {
						selected = i;
					}
				}

				selectAir.setItems(TabScriptTitle);
				if (selected != -1) {// Affiche le script déjà choisi
					selectAir.select(selected);
				}
				selectAir.setVisible(true);

				// Si un modele a deja ete choisi
				if (a.getRSModelId() != -1) {
					try {
						selected = -1;
						List<RScriptModel> listModel = remoteSmart.getRScriptModelsbyScript(a.getRId());
						String[] TabListModel = new String[listModel.size()];
						for (int i = 0; i < listModel.size(); i++) {
							TabListModel[i] = String.valueOf(listModel.get(i).getNumVersion());
							if (listModel.get(i).getNumVersion() == a.getRSModelId()) {
								selected = i;
							}
						}
						selectModel.setItems(TabListModel);
						selectModel.select(selected);
						selectModel.setVisible(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				listId = ScriptId;
				listName = TabScriptTitle;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (a.getChoice() == 1) {
			// R
			choice.select(1);

			boolean compEditFileVisible = false;
			boolean compImportVisible = false;
			if (a.getRChoice() != null) {
				if (a.getRChoice().equals("E")) { //$NON-NLS-1$
					compEditFileVisible = true;
					compImportVisible = false;

					selectR.select(0);
					labelcomP4.setText(Messages.AirSection_12);
				}
				else if (a.getRChoice().equals("I")) { //$NON-NLS-1$
					compEditFileVisible = false;
					compImportVisible = true;

					select.setText(a.getPath());

				}
			}

			updateUi(false, true, compEditFileVisible, compImportVisible);
		}
		else {
			updateUi(false, false, false, false);
		}
	}

	private void updateUi(boolean compAirVisible, boolean compRVisible, boolean compEditFileVisible, boolean compImportVisible) {
		compAir.setVisible(compAirVisible);
		GridData compAirData = ((GridData) compAir.getLayoutData());
		if (compAirData != null) {
			compAirData.exclude = !compAirVisible;
		}
		
		compR.setVisible(compRVisible);
		GridData compRData = ((GridData) compR.getLayoutData());
		if (compRData != null) {
			compRData.exclude = !compRVisible;
		}
		
		compEditFile.setVisible(compEditFileVisible);
		GridData compEditFileData = ((GridData) compEditFile.getLayoutData());
		if (compEditFileData != null) {
			compEditFileData.exclude = !compEditFileVisible;
		}
		
		compImport.setVisible(compImportVisible);
		GridData compImportData = ((GridData) compImport.getLayoutData());
		if (compImportData != null) {
			compImportData.exclude = !compImportVisible;
		}
		
		main.getParent().pack();
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}
}
