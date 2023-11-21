package bpm.gateway.ui.palette.customizer.wizard.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.gateway.ui.palette.customizer.Activator;
import bpm.gateway.ui.palette.customizer.icons.IconsNames;
import bpm.gateway.ui.palette.customizer.utils.PaletteEntry;
import bpm.gateway.ui.palette.customizer.utils.PaletteXmlParser;
import bpm.gateway.ui.palette.customizer.wizard.PaletteWizard;

public class PaletteListPage extends WizardPage {

	private TreeViewer viewer;
	private LinkedHashMap<String, HashMap<String, List<PaletteEntry>>> palettes = new LinkedHashMap<String, HashMap<String, List<PaletteEntry>>>();
	private ComboViewer file;

	private HashMap<String, List<PaletteEntry>> palette;

	protected PaletteListPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		setControl(main);

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Select a Palette");

		file = new ComboViewer(main, SWT.READ_ONLY);
		file.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		file.setLabelProvider(new LabelProvider());
		file.setSorter(new ViewerSorter());
		file.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		file.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				String s = (String) ((IStructuredSelection) file.getSelection()).getFirstElement();
				palette = palettes.get(s);
				viewer.setInput(palette);

			}
		});

		Button create = new Button(main, SWT.PUSH);
		create.setToolTipText("Create a New Palette Layout");
		create.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.palette));
		create.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		create.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PaletteWizard wiz = new PaletteWizard();
				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.setMinimumPageSize(800, 600);

				if (d.open() == WizardDialog.OK) {
					loadPalettes();
					for (String s : (Collection<String>) file.getInput()) {
						if (s.equals(wiz.getPaletteName())) {
							file.setSelection(new StructuredSelection(s));
							break;
						}
					}
				}
			}
		});

		viewer = new TreeViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		viewer.getTree().setHeaderVisible(true);
		viewer.setAutoExpandLevel(2);
		viewer.setContentProvider(new ITreeContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				HashMap<String, List<PaletteEntry>> model = (HashMap<String, List<PaletteEntry>>) inputElement;
				return model.keySet().toArray(new String[model.size()]);
			}

			public boolean hasChildren(Object element) {
				if (element instanceof String) {
					return palette.get(element).size() > 0;
				}
				return false;
			}

			public Object getParent(Object element) {
				if (element instanceof PaletteEntry) {
					for (String k : palette.keySet()) {
						if (palette.get(k) == element) {
							return k;
						}
					}
				}
				return null;
			}

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof String) {
					List l = palette.get(parentElement);
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
		viewer.setInput(palette);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Folder Name");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry) {
					return "";
				}
				return super.getText(element);
			}
		});

		col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Transformation");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry) {
					return ((PaletteEntry) element).getTransformationClass().getSimpleName();
				}
				else {
					return "";
				}

			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof PaletteEntry) {
					return Activator.getDefault().getImageRegistry().get(PaletteEntry.keyImages.get(((PaletteEntry) element).getTransformationClass()));
				}
				else {
					return null;
				}

			}
		});

		col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Entry Name");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry) {
					return ((PaletteEntry) element).getEntryName();
				}
				else {
					return "";
				}
			}
		});

		col = new TreeViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Entry Description");
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PaletteEntry) {
					return ((PaletteEntry) element).getEntryDescription();
				}
				else {
					return "";
				}
			}
		});

		loadPalettes();

	}

	private void loadPalettes() {
		File folder = new File(Activator.PALETTE_FOLDER);
		palettes.clear();
		if (folder.list() != null) {
			for (String f : folder.list()) {
				if (!f.endsWith(".gtwpal")) {
					continue;
				}
				try {

					PaletteXmlParser parser = new PaletteXmlParser(new FileInputStream(folder.getPath() + "/" + f));
					palettes.put(parser.getPaletteName(), parser.getGroupsOrganization());

				} catch (Exception ex) {

				}
			}
			file.setInput(palettes.keySet());
		}
	}

	public HashMap<String, List<PaletteEntry>> getPalette() {
		return palette;
	}
}
