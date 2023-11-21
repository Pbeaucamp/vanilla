package bpm.vanilla.server.ui.views.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryTreeViewer;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;

public class RepositoryViewer extends Composite {

	private FormToolkit toolkit;
	private Text tUser, tPass, tUrl;
	private RepositoryTreeViewer repositoryViewer;
	private TopTenComposite topTenComposite;
	private Combo cRepo;

	private List<Repository> definitions;
	private boolean isShown = false;

	public RepositoryViewer(Composite parent, int style, FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		setLayoutData(new GridData(GridData.FILL_BOTH));
		setLayout(new GridLayout());

		toolkit.paintBordersFor(this);
		buildContent();
	}

	private void buildContent() {
		Section section = toolkit.createSection(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setLayout(new GridLayout());
		section.setText(Messages.RepositoryViewer_0);

		Composite main = toolkit.createComposite(section);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());

		createInfoBar(main);
		createTree(main);

		toolkit.paintBordersFor(main);

		section.setClient(main);
		loadInput();
	}

	@Override
	public boolean setFocus() {
		if (!isShown) {
			loadInput();
		}
		return false;
	}

	private void loadInput() {
		try {
			showHomeRepository();
		} catch (Exception e) {
			MessageDialog.openInformation(getShell(), Messages.RepositoryViewer_1, Messages.RepositoryViewer_2 + e.getMessage());
		}
	}

	private void createInfoBar(Composite main) {
		Section infoBar = toolkit.createSection(main, Section.EXPANDED | Section.SHORT_TITLE_BAR | Section.TWISTIE);
		infoBar.setText(Messages.RepositoryViewer_5);
		infoBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		infoBar.setLayout(new GridLayout());

		Composite compositeBar = toolkit.createComposite(infoBar);
		compositeBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeBar.setLayout(new GridLayout(2, false));

		Label l = new Label(compositeBar, SWT.NONE);
		l.setText(Messages.RepositoryViewer_6);

		tUser = new Text(compositeBar, SWT.BORDER);
		tUser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l1 = new Label(compositeBar, SWT.NONE);
		l1.setText(Messages.RepositoryViewer_7);

		tPass = new Text(compositeBar, SWT.BORDER | SWT.PASSWORD);
		tPass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l2 = new Label(compositeBar, SWT.NONE);
		l2.setText(Messages.RepositoryViewer_8);

		tUrl = new Text(compositeBar, SWT.BORDER);
		tUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l3 = new Label(compositeBar, SWT.NONE);
		l3.setText(Messages.RepositoryViewer_9);

		cRepo = new Combo(compositeBar, SWT.READ_ONLY);
		cRepo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cRepo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});

		infoBar.setClient(compositeBar);
	}

	private void createTree(Composite main) {
		ISelectionChangedListener selLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof RepositoryItem)) {
					if (event.getSource() == repositoryViewer) {
						topTenComposite.removeSelectionChangedListener(this);
						StructuredSelection sel = new StructuredSelection();
						topTenComposite.setSelection(sel);
						topTenComposite.addSelectionChangedListener(this);
					}
					return;
				}

				if (event.getSource() == repositoryViewer) {
					topTenComposite.removeSelectionChangedListener(this);
					topTenComposite.setSelection(event.getSelection(), true);
					topTenComposite.addSelectionChangedListener(this);
				}
				else if (event.getSource() == topTenComposite.getTopTenViewer()) {
					repositoryViewer.removeSelectionChangedListener(this);
					repositoryViewer.setSelection(event.getSelection(), true);
					repositoryViewer.addSelectionChangedListener(this);
				}
			}
		};

		Section treeSection = toolkit.createSection(main, Section.EXPANDED | Section.SHORT_TITLE_BAR | Section.TWISTIE);
		treeSection.setText(Messages.RepositoryViewer_10);
		treeSection.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeSection.setLayout(new GridLayout());

		createRepositoryToolBar(treeSection);

		TabFolder folder = new TabFolder(treeSection, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Composite compositeTree = toolkit.createComposite(folder);
		compositeTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeTree.setLayout(new GridLayout());

		repositoryViewer = new RepositoryTreeViewer(compositeTree, SWT.BORDER);
		repositoryViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		repositoryViewer.addSelectionChangedListener(selLst);

		TabItem tree = new TabItem(folder, SWT.NONE);
		tree.setText(Messages.RepositoryViewer_14);
		tree.setControl(compositeTree);

		Composite compositeTopTen = toolkit.createComposite(folder);
		compositeTopTen.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeTopTen.setLayout(new GridLayout());

		topTenComposite = new TopTenComposite(toolkit, compositeTopTen, SWT.NONE);
		topTenComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		topTenComposite.setLayout(new GridLayout());
		topTenComposite.addSelectionChangedListener(selLst);

		TabItem topTen = new TabItem(folder, SWT.NONE);
		topTen.setText(Messages.RepositoryViewer_15);
		topTen.setControl(compositeTopTen);

		treeSection.setClient(folder);
	}

	private void createRepositoryToolBar(Section section) {
		ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);

		ToolItem tRefresh = new ToolItem(tbar, SWT.NULL);
		tRefresh.setImage(Activator.getDefault().getImageRegistry().get(Icons.REFRESH));
		tRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});

		section.setTextClient(tbar);
	}

	private void refresh() {
		try {
			int index = cRepo.getSelectionIndex();
			Repository def = definitions.get(index);
			setInput(def, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.RepositoryViewer_3, Messages.RepositoryViewer_4 + ex.getMessage());
		}
	}

	private void showHomeRepository() throws Exception {
		bpm.vanilla.server.client.ui.clustering.menu.Activator activator = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();

		tUser.setText(activator.getVanillaContext().getLogin());
		tUser.setEditable(false);

		tPass.setText(activator.getVanillaContext().getPassword());
		tPass.setEditable(false);

		tUrl.setText(activator.getVanillaContext().getVanillaUrl());
		tUrl.setEditable(false);

		definitions = getRepositories(activator.getVanillaContext());
		String[] reps = prepareDataForCombo(definitions);
		cRepo.setItems(reps);
		cRepo.select(0);

		setInput(definitions.get(0), false);

		isShown = true;
	}

	private void setInput(Repository selectedRepository, boolean refresh) throws Exception {
		bpm.vanilla.server.client.ui.clustering.menu.Activator activator = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();
		if (activator.getVanillaApi() == null) {
			isShown = false;
			throw new Exception(Messages.RepositoryViewer_11);
		}

		Activator.getDefault().setSelectedRepository(selectedRepository);

		IRepository repository = connectToInputRepository(activator.getVanillaContext(), selectedRepository);
		repositoryViewer.setInput(repository);
		if (refresh) {
			topTenComposite.refresh();
		}
		else {
			topTenComposite.loadInput(null, null, null, null);
		}
	}

	private String[] prepareDataForCombo(List<Repository> defs) {
		List<String> strs = new ArrayList<String>();

		for (Repository def : defs) {
			strs.add(def.getName());
		}

		return strs.toArray(new String[strs.size()]);
	}

	private List<Repository> getRepositories(IVanillaContext ctx) throws Exception {
		IVanillaAPI api = new RemoteVanillaPlatform(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
		List<Repository> definitions = api.getVanillaRepositoryManager().getRepositories();
		return definitions;
	}

	private bpm.vanilla.platform.core.repository.Repository connectToInputRepository(IVanillaContext vanillaCtx, Repository definition) throws Exception {
		try {
			Group group = new Group();
			group.setId(-1);
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, definition);
			IRepositoryApi sock = new RemoteRepositoryApi(ctx);

			return new bpm.vanilla.platform.core.repository.Repository(sock);
		} catch (Exception ex) {
			throw new Exception(Messages.RepositoryViewer_12 + definition.getUrl() + Messages.RepositoryViewer_13 + vanillaCtx.getLogin());
		}
	}

	public RepositoryTreeViewer getRepositoryViewer() {
		return repositoryViewer;
	}

	public TableViewer getTopTenViewer() {
		return topTenComposite.getTopTenViewer();
	}
}