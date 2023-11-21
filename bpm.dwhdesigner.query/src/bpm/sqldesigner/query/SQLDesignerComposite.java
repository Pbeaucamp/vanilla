package bpm.sqldesigner.query;

import java.util.List;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.sqldesigner.query.editor.SQLEditor;
import bpm.sqldesigner.query.editor.SQLRootPart;
import bpm.sqldesigner.query.editor.filter.FiltersComposite;
import bpm.sqldesigner.query.editor.filter.FiltersManagerComposite;
import bpm.sqldesigner.query.listener.DeleteListener;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Schema;
import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.filter.ColumnFilter;
import bpm.sqldesigner.query.model.filter.ColumnFiltersManager;
import bpm.sqldesigner.query.model.selected.SelectedColumnsManager;
import bpm.sqldesigner.query.output.SqlOutput;
import bpm.sqldesigner.query.output.SqlOutputManager;
import bpm.sqldesigner.query.palette.SQLDesignerPalette;
import bpm.sqldesigner.query.services.AbstractDatabaseService;
import bpm.sqldesigner.query.services.FactoryDatabaseService;

public class SQLDesignerComposite extends Composite {

	private EditDomain editDomain = new EditDomain();
	private SQLDesignerPalette palette;
	private SqlOutput textSQL;
	private SQLEditor viewer;
	private ToolBar toolbar;
	private DataBaseConnection dbc;
	private Composite paletteC;
	private Composite mainC;
	private FiltersComposite filterC;
	private Label imageTestQuery;
	private Composite editorC;
	private boolean filterCompositeEnabled = false;
	private FiltersManagerComposite filtersManagerC;
	private Group groupFilters;
	private Group groupFiltersManager;
	private Schema schema = null;
	
	private Button activateFilter;
	private Text tableNameFilter;
	
	private DocumentGateway document;

	public SQLDesignerComposite(Composite parent, int style,
			DataBaseConnection dbc, List<ColumnFilter> predefinedFilters, DocumentGateway document) {
		super(parent, SWT.NONE);
		this.dbc = dbc;
		this.document = document;
		initAll(predefinedFilters);
	}
	
	public SQLDesignerComposite(Composite parent, int style,
			Schema schema, List<ColumnFilter> predefinedFilters) {
		super(parent, SWT.NONE);
		this.schema = schema;
		initAll(predefinedFilters);
	}
	
	/**
	 * ugly....
	 * @return
	 */
	public SQLDesignerPalette getPalette(){
		return palette;
	}
	public void initAll(List<ColumnFilter> predefinedFilters){

		ColumnFiltersManager.getInstance().setPredefinedFilters(
				predefinedFilters);

		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		SashForm sf = new SashForm(this, SWT.HORIZONTAL);
		sf.setLayoutData(new GridData(GridData.FILL_BOTH));

		paletteC = new Composite(sf, SWT.BORDER);
		paletteC.setLayout(new GridLayout(1, true));
		paletteC.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL,
				false, true, 1, 1));

		mainC = new Composite(sf, SWT.BORDER);
		mainC.setLayout(new GridLayout(1, false));
		mainC.setLayoutData(new GridData(GridData.FILL_BOTH));
		initToolBar();
		initEditor();
		initFilters();
		initPalette();
		initThumbnail();
		initZoom();
		initFilterButton();

		editDomain.setPaletteViewer(palette);
		editDomain.setPaletteRoot(palette.getPaletteRoot());

		initSQLOutput();
		addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				dispose();

			}

		});

		sf.setWeights(new int[] { 1, 4 });
	}

	
	
	private void initFilters(){
		Composite main = new Composite(paletteC, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		activateFilter = new Button(main, SWT.CHECK);
		activateFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		activateFilter.setText("Filter on Table Name");
		activateFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableNameFilter.setEnabled(activateFilter.getSelection());
			}
		});
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Table Name");
		
		tableNameFilter = new Text(main, SWT.BORDER);
		tableNameFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		tableNameFilter.setEnabled(false);
		
		tableNameFilter.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				palette.filterOnName(tableNameFilter.getText());
				
			}
		});
		
	}
	private void initFilterButton() {

		Listener filtersManagerListener = new Listener() {
			public void handleEvent(Event event) {

				if (!filterCompositeEnabled) {
					initFiltersManager();
					filtersManagerC.getParent().getParent().layout();
				}

			}
		};

		new ToolItem(toolbar, SWT.SEPARATOR);

		ToolItem toolItem4 = new ToolItem(toolbar, SWT.NONE);
		toolItem4.setImage(Activator.getDefault().getImageRegistry().get(
				"filter"));
		toolItem4.setToolTipText("Filters Manager");
		toolItem4.addListener(SWT.Selection, filtersManagerListener);

	}

	protected void initSQLOutput() {
		Composite sqlOutputC = new Composite(this, SWT.NONE);
		sqlOutputC.setLayout(new GridLayout(3, false));
		sqlOutputC.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 1, 1));

		textSQL = SqlOutputManager.getInstance(sqlOutputC, SWT.MULTI
				| SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textSQL.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button testButton = new Button(sqlOutputC, SWT.PUSH);
		testButton.setText("Test Query");

		if (schema == null) {

			testButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					AbstractDatabaseService dS = FactoryDatabaseService.getDataBaseService(dbc);

					try {
						dS.connect(document);
					} catch (ServerException e) {
						e.printStackTrace();
					}

					boolean b = dS.testQuery(textSQL.getText());
					dS.disconnect();

					setValidQuery(b);
				}
			});
		}else
			testButton.setEnabled(false);

		imageTestQuery = new Label(sqlOutputC, SWT.NONE);
		imageTestQuery.setImage(Activator.getDefault().getImageRegistry().get(
				"valid"));
		imageTestQuery.setVisible(false);

	}

	protected void setValidQuery(boolean b) {
		if (b) {
			imageTestQuery.setImage(Activator.getDefault().getImageRegistry()
					.get("valid"));
			imageTestQuery.setVisible(true);
		} else {
			imageTestQuery.setImage(Activator.getDefault().getImageRegistry()
					.get("invalid"));
			imageTestQuery.setVisible(true);
		}
		imageTestQuery.redraw();
	}

	public void setImageTestQueryVisible(boolean b) {
		imageTestQuery.setVisible(b);

	}

	protected void initEditor() {

		editorC = new Composite(mainC, SWT.NONE);
		editorC.setLayout(new GridLayout(1, false));
		editorC.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new SQLEditor(editorC, editDomain);

		viewer.setEditDomain(editDomain);
		editDomain.addViewer(viewer);

		Node model = new Schema();
		viewer.setContents(model);

	}

	protected void initToolBar() {
		toolbar = new ToolBar(mainC, SWT.FLAT);
		toolbar.setBounds(0, 0, 200, 70);

		Listener undoListener = new Listener() {
			public void handleEvent(Event event) {
				editDomain.getCommandStack().undo();
			}
		};
		Listener redoListener = new Listener() {
			public void handleEvent(Event event) {
				editDomain.getCommandStack().redo();
			}
		};

		Listener deleteListener = new DeleteListener(this);

		ToolItem toolItem1 = new ToolItem(toolbar, SWT.NONE);
		toolItem1.setImage(Activator.getDefault().getImageRegistry()
				.get("undo"));
		toolItem1.setToolTipText("Undo");
		toolItem1.addListener(SWT.Selection, undoListener);

		ToolItem toolItem2 = new ToolItem(toolbar, SWT.NONE);
		toolItem2.setImage(Activator.getDefault().getImageRegistry()
				.get("redo"));
		toolItem2.setToolTipText("Redo");
		toolItem2.addListener(SWT.Selection, redoListener);

		ToolItem toolItem3 = new ToolItem(toolbar, SWT.NONE);
		toolItem3.setImage(Activator.getDefault().getImageRegistry().get(
				"delete"));
		toolItem3.setToolTipText("Delete");
		toolItem3.addListener(SWT.Selection, deleteListener);

	}

	public void initFilter() {

		groupFilters = new Group(mainC, SWT.SHADOW_ETCHED_IN);
		groupFilters.setLayout(new GridLayout(1, false));
		groupFilters.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		filterC = new FiltersComposite(groupFilters, SWT.NONE, this);
		filterC.setLayout(new GridLayout(5, false));
		filterC.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		filterC.setVisible(false);

		filterC.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				groupFilters.dispose();
				filterCompositeEnabled = false;
			}
		});
	}

	public void initFiltersManager() {

		if (!filterCompositeEnabled) {
			groupFiltersManager = new Group(mainC, SWT.SHADOW_ETCHED_IN);
			groupFiltersManager.setLayout(new GridLayout(1, false));
			groupFiltersManager.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			groupFiltersManager.setText("Filters Manager");

			filterCompositeEnabled = true;
			filtersManagerC = new FiltersManagerComposite(groupFiltersManager,
					SWT.NONE, this);
			filtersManagerC.setLayout(new GridLayout(1, false));
			filtersManagerC
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			filtersManagerC.setVisible(true);

			filtersManagerC.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					filterCompositeEnabled = false;
					groupFiltersManager.dispose();
				}
			});
		}
	}

	protected void initZoom() {
		Listener zoomInListener = new Listener() {
			public void handleEvent(Event event) {
				ZoomManager zm = viewer.getZoomManager();
				if (zm.canZoomIn()) {
					zm.zoomIn();
				}
			}
		};

		Listener zoomOutListener = new Listener() {
			public void handleEvent(Event event) {
				ZoomManager zm = viewer.getZoomManager();
				if (zm.canZoomOut()) {
					zm.zoomOut();
				}
			}
		};

		new ToolItem(toolbar, SWT.SEPARATOR);

		ToolItem zoomIn = new ToolItem(toolbar, SWT.NONE);
		zoomIn
				.setImage(Activator.getDefault().getImageRegistry().get(
						"zoomIn"));
		zoomIn.setToolTipText("Zoom In");
		zoomIn.addListener(SWT.Selection, zoomInListener);

		ToolItem zoomOut = new ToolItem(toolbar, SWT.NONE);
		zoomOut.setImage(Activator.getDefault().getImageRegistry().get(
				"zoomOut"));
		zoomOut.setToolTipText("Zoom Out");
		zoomOut.addListener(SWT.Selection, zoomOutListener);
	}

	// if schema is null, the palette is initialized from database
	protected void initPalette() {

		CLabel l = new CLabel(paletteC, SWT.NONE);
		l.setText("Design");
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false));
		l.setBackground(new Color[] {
				Display.getDefault().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				Display.getDefault().getSystemColor(SWT.COLOR_BLUE),
				Display.getDefault().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT) }, new int[] { 90,
				100 });

		if (schema != null)
			palette = new SQLDesignerPalette(schema, document);
		else
			palette = new SQLDesignerPalette(dbc, document);

		palette.createControl(paletteC);
		palette.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		setBackground(palette.getControl().getBackground());
	}

	protected void initThumbnail() {
		Canvas canvas = new Canvas(paletteC, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL, GridData.END, true,
				false));
		LightweightSystem lws = new LightweightSystem(canvas);

		ScrollableThumbnail thumbnail = new ScrollableThumbnail(
				(Viewport) ((SQLRootPart) viewer.getRootEditPart())
						.getFigure());
		thumbnail.setSource(((SQLRootPart) viewer.getRootEditPart())
				.getLayer(LayerConstants.PRINTABLE_LAYERS));

		lws.setContents(thumbnail);
	}

	public EditDomain getDomain() {
		return editDomain;
	}

	public SQLEditor getEditor() {
		return viewer;
	}

	public String getQuery() {
		return textSQL.getText().replace('\n', ' ');
	}

	@Override
	public void dispose() {
		super.dispose();
		SqlOutputManager.dispose();
		ConnectionsManager.dispose();
		ColumnFiltersManager.dispose();
		SelectedColumnsManager.dispose();
	}

	public FiltersComposite addFilterComposite(Column model) {
		if (!filterCompositeEnabled) {
			initFilter();
			filterCompositeEnabled = true;
			filterC.openFilterComposite(model);
			filterC.getParent().getParent().layout();
			return filterC;
		}
		return null;
	}

	public void setGroupFiltersText(String text) {
		groupFilters.setText(text);
	}

}
