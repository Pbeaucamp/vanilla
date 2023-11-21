package bpm.gateway.ui.dialogs.utils.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.core.transformations.utils.DefinitionXsdOutput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogCalculationEditor;

public class DialogXSDDefinition extends Dialog {

	private static final String XSD = "xsd"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$
	private static final String COLUMN_ID = "columnID"; //$NON-NLS-1$
	private static final String INPUT_OUTPUT = "input_output"; //$NON-NLS-1$
	private static final String INPUT_COLUMN = "inputColumn"; //$NON-NLS-1$
	private static final String PARENT_COLUMN = "parentColumn"; //$NON-NLS-1$
	private static final String CHILD_COLUMN = "childColumn"; //$NON-NLS-1$

	private FileXML fileXML;
	private boolean isFromOutput;

	private TreeViewer xsdViewer;
	private ComboBoxViewerCellEditor typeCbo;
	private ComboBoxCellEditor outputCbo;
	private ComboBoxViewerCellEditor inputColumnCbo;
	private ComboBoxViewerCellEditor parentCbo;
	private ComboBoxViewerCellEditor childCbo;

	public DialogXSDDefinition(Shell parentShell, FileXML fileXML) {
		super(parentShell);
		this.fileXML = fileXML;
		if (fileXML instanceof FileOutputXML) {
			this.isFromOutput = true;
		}
		else {
			this.isFromOutput = false;
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		xsdViewer = new TreeViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		xsdViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		xsdViewer.setContentProvider(xsdContentProvider);

		buildTreeColumn();

		xsdViewer.setInput(fileXML);

		return main;
	}

	private void buildTreeColumn() {
		TreeViewerColumn colXSD = new TreeViewerColumn(xsdViewer, SWT.NONE);
		colXSD.getColumn().setText(Messages.DialogXSDDefinition_0);
		colXSD.getColumn().setWidth(200);
		colXSD.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof DefinitionXSD) {
					return ((DefinitionXSD) element).getName();
				}
//				else if(element instanceof AttributeXSD) {
//					return ((AttributeXSD) element).getName();
//				}
				return ""; //$NON-NLS-1$
			}
		});

		TreeViewerColumn colType = new TreeViewerColumn(xsdViewer, SWT.NONE);
		colType.getColumn().setText(Messages.DialogXSDDefinition_9);
		colType.getColumn().setWidth(150);
		colType.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof DefinitionXSD) {
					return ((DefinitionXSD) element).getType();
				}
				return ""; //$NON-NLS-1$
			}
		});

		if (isFromOutput) {
			TreeViewerColumn colOutput = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colOutput.getColumn().setText(Messages.DialogXSDDefinition_11);
			colOutput.getColumn().setWidth(150);
			colOutput.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXSD) {
						return ((DefinitionXSD) element).getOutputName();
					}
					return ""; //$NON-NLS-1$
				}
			});

			TreeViewerColumn colInputMapping = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colInputMapping.getColumn().setText(Messages.DialogXSDDefinition_13);
			colInputMapping.getColumn().setWidth(150);
			colInputMapping.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXsdOutput) {
						return ((DefinitionXsdOutput) element).getInputColumn() != null ? ((DefinitionXsdOutput) element).getInputColumn() : ""; //$NON-NLS-1$
					}
					return ""; //$NON-NLS-1$
				}

			});

			TreeViewerColumn colChildId = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colChildId.getColumn().setText(Messages.DialogXSDDefinition_16);
			colChildId.getColumn().setWidth(150);
			colChildId.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXsdOutput) {
						return ((DefinitionXsdOutput) element).getChildColumn() != null ? ((DefinitionXsdOutput) element).getChildColumn() : ""; //$NON-NLS-1$
					}
					return ""; //$NON-NLS-1$
				}

			});

			TreeViewerColumn colParentId = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colParentId.getColumn().setText(Messages.DialogXSDDefinition_19);
			colParentId.getColumn().setWidth(150);
			colParentId.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXsdOutput) {
						return ((DefinitionXsdOutput) element).getParentColumn() != null ? ((DefinitionXsdOutput) element).getParentColumn() : ""; //$NON-NLS-1$
					}
					return ""; //$NON-NLS-1$
				}

			});

			xsdViewer.setColumnProperties(new String[] { XSD, TYPE, INPUT_OUTPUT, INPUT_COLUMN, CHILD_COLUMN, PARENT_COLUMN });

			typeCbo = new ComboBoxViewerCellEditor(xsdViewer.getTree());
			typeCbo.setContenProvider(new ArrayContentProvider());
			typeCbo.setLabelProvider(new LabelProvider());
			typeCbo.setInput(DefinitionXSD.TYPES);

			List<String> inputs = new ArrayList<String>();
			if (fileXML.getInputs() != null) {
				for (Transformation output : fileXML.getInputs()) {
					inputs.add(output.getName());
				}
			}

			outputCbo = new ComboBoxCellEditor(xsdViewer.getTree(), inputs.toArray(new String[inputs.size()]));

			inputColumnCbo = new ComboBoxViewerCellEditor(xsdViewer.getTree());
			inputColumnCbo.setContenProvider(new StreamElementProvider(true));
			inputColumnCbo.setLabelProvider(streamElementProvider);

			childCbo = new ComboBoxViewerCellEditor(xsdViewer.getTree());
			childCbo.setContenProvider(new StreamElementProvider(false));
			childCbo.setLabelProvider(streamElementProvider);

			parentCbo = new ComboBoxViewerCellEditor(xsdViewer.getTree());
			parentCbo.setContenProvider(new StreamElementProvider(false));
			parentCbo.setLabelProvider(streamElementProvider);

			xsdViewer.setCellEditors(new CellEditor[] { new TextCellEditor(xsdViewer.getTree()), typeCbo, outputCbo, inputColumnCbo, childCbo, parentCbo });
		}
		else {
			TreeViewerColumn colOutput = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colOutput.getColumn().setText(Messages.DialogXSDDefinition_22);
			colOutput.getColumn().setWidth(150);
			colOutput.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXSD) {
						return ((DefinitionXSD) element).getOutputName();
					}
					return ""; //$NON-NLS-1$
				}
			});

			TreeViewerColumn colColumnID = new TreeViewerColumn(xsdViewer, SWT.NONE);
			colColumnID.getColumn().setText(Messages.DialogXSDDefinition_24);
			colColumnID.getColumn().setWidth(150);
			colColumnID.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof DefinitionXSD) {
						return ((DefinitionXSD) element).getColumnId() != null ? ((DefinitionXSD) element).getColumnId().getName() : ""; //$NON-NLS-1$
					}
					return ""; //$NON-NLS-1$
				}

			});

			xsdViewer.setColumnProperties(new String[] { XSD, TYPE, INPUT_OUTPUT, COLUMN_ID });

			typeCbo = new ComboBoxViewerCellEditor(xsdViewer.getTree());
			typeCbo.setContenProvider(new ArrayContentProvider());
			typeCbo.setLabelProvider(new LabelProvider());
			typeCbo.setInput(DefinitionXSD.TYPES);

			List<String> outputs = new ArrayList<String>();
			if (fileXML.getOutputs() != null) {
				for (Transformation output : fileXML.getOutputs()) {
					outputs.add(output.getName());
				}
			}
			outputCbo = new ComboBoxCellEditor(xsdViewer.getTree(), outputs.toArray(new String[outputs.size()]));

			xsdViewer.setCellEditors(new CellEditor[] { new TextCellEditor(xsdViewer.getTree()), typeCbo, outputCbo, new ParameterDialogCellEditor(xsdViewer.getTree()) });
		}

		xsdViewer.setCellModifier(new XSDCellModifier());
		xsdViewer.getTree().setHeaderVisible(true);
		xsdViewer.getTree().setLinesVisible(true);
	}

	private List<StreamElement> getStreamElements(DefinitionXsdOutput def, boolean getParentElements) {
		if (getParentElements && def.getParent() != null && def.getParent().getOutputName() != null && fileXML.getInputs() != null) {
			if (def.getParent().getType().equals(DefinitionXSD.ITERABLE)) {
				for (Transformation transfo : fileXML.getInputs()) {
					if (transfo.getName().equals(def.getParent().getOutputName())) {
						List<StreamElement> elements = null;
						try {
							elements = transfo.getDescriptor(fileXML) != null ? transfo.getDescriptor(fileXML).getStreamElements() : new ArrayList<StreamElement>();
						} catch (ServerException e) {
							e.printStackTrace();
						}
						if (elements == null) {
							elements = new ArrayList<StreamElement>();
						}
						return elements;
					}
				}
			}
			else {
				if (def.getParent() instanceof DefinitionXsdOutput) {
					return getStreamElements((DefinitionXsdOutput) def.getParent(), getParentElements);
				}
			}
		}
		else if (!getParentElements && def.getOutputName() != null && fileXML.getInputs() != null) {
			for (Transformation transfo : fileXML.getInputs()) {
				if (transfo.getName().equals(def.getOutputName())) {
					List<StreamElement> elements = null;
					try {
						elements = transfo.getDescriptor(fileXML) != null ? transfo.getDescriptor(fileXML).getStreamElements() : new ArrayList<StreamElement>();
					} catch (ServerException e) {
						e.printStackTrace();
					}
					if (elements == null) {
						elements = new ArrayList<StreamElement>();
					}
					return elements;
				}
			}
		}
		return new ArrayList<StreamElement>();
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(1100, 600);
		getShell().setText(Messages.DialogXSDDefinition_27);

		xsdViewer.setInput(fileXML);
	}

	private ITreeContentProvider xsdContentProvider = new ITreeContentProvider() {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof FileXML) {
				List<DefinitionXSD> defs = new ArrayList<DefinitionXSD>();
				defs.add(fileXML.getRootElement());
				return defs != null ? defs.toArray(new DefinitionXSD[defs.size()]) : new DefinitionXSD[0];
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof DefinitionXSD) {
				return ((DefinitionXSD) element).getChilds() != null && !((DefinitionXSD) element).getChilds().isEmpty();
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof DefinitionXSD) {
				List<DefinitionXSD> defs = ((DefinitionXSD) parentElement).getChilds();
//				List<AttributeXSD> attrs = ((DefinitionXSD) parentElement).getAttributes();

//				List<Object> childs = new ArrayList<Object>();
//				childs.addAll(defs);
//				childs.addAll(attrs);
				return defs.toArray(new DefinitionXSD[defs.size()]);
			}
			return new Object[0];
		}
	};

	private class XSDCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			if (!(element instanceof DefinitionXSD)) {
				return false;
			}
			else if(((DefinitionXSD)element).isAttribute()) {
				return false;
			}
			
			if (property.equals(XSD)) {
				return false;
			}
			else if (property.equals(TYPE)) {
				return true;
			}
			else if (property.equals(COLUMN_ID) || property.equals(INPUT_OUTPUT)) {
				DefinitionXSD definition = (DefinitionXSD) element;
				if (definition.getType().equals(DefinitionXSD.ITERABLE)) {
					return true;
				}
				return false;
			}

			if (element instanceof DefinitionXsdOutput) {
				DefinitionXsdOutput defOutput = (DefinitionXsdOutput) element;
				if (property.equals(PARENT_COLUMN)) {
					if (defOutput.getType().equals(DefinitionXSD.ITERABLE)) {
						parentCbo.setInput(defOutput);
						return true;
					}
				}
				else if (property.equals(CHILD_COLUMN)) {
					if (defOutput.getType().equals(DefinitionXSD.ITERABLE)) {
						childCbo.setInput(defOutput);
						return true;
					}
				}
				else if (property.equals(INPUT_COLUMN)) {
					inputColumnCbo.setInput(defOutput);
					return true;
				}
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			if (element instanceof DefinitionXSD) {
				DefinitionXSD definition = (DefinitionXSD) element;
				if (property.equals(XSD)) {
					return definition.getName();
				}
				else if (property.equals(TYPE)) {
					for (int i = 0; i < DefinitionXSD.TYPES.length; i++) {
						if (definition.getType().equals(DefinitionXSD.TYPES[i])) {
							return i;
						}
					}
					return -1;
				}

				if (definition instanceof DefinitionXsdOutput) {
					DefinitionXsdOutput defOutput = (DefinitionXsdOutput) definition;
					if (property.equals(INPUT_OUTPUT)) {
						for (int i = 0; i < fileXML.getOutputs().size(); i++) {
							if (fileXML.getInputs().get(i).getName().equals(definition.getOutputName())) {
								return i;
							}
						}
						return 0;
					}
					else if (property.equals(INPUT_COLUMN)) {
						List<StreamElement> elements = getStreamElements(defOutput, true);
						for (int i = 0; i < elements.size(); i++) {
							if (elements.get(i).name.equals(defOutput.getInputColumn())) {
								return i;
							}
						}
						return 0;
					}
					else if (property.equals(PARENT_COLUMN)) {
						List<StreamElement> elements = getStreamElements(defOutput, false);
						for (int i = 0; i < elements.size(); i++) {
							if (elements.get(i).name.equals(defOutput.getParentColumn())) {
								return i;
							}
						}
						return 0;
					}
					else if (property.equals(CHILD_COLUMN)) {
						List<StreamElement> elements = getStreamElements(defOutput, false);
						for (int i = 0; i < elements.size(); i++) {
							if (elements.get(i).name.equals(defOutput.getChildColumn())) {
								return i;
							}
						}
						return 0;
					}
				}
				else {
					if (property.equals(INPUT_OUTPUT)) {
						for (int i = 0; i < fileXML.getOutputs().size(); i++) {
							if (fileXML.getOutputs().get(i).getName().equals(definition.getOutputName())) {
								return i;
							}
						}
						return 0;
					}
					else if (property.equals(COLUMN_ID)) {
						return definition.getColumnId() != null ? definition.getColumnId().getName() : ""; //$NON-NLS-1$
					}
				}
			}
//			else if (element instanceof AttributeXSD) {
//				if (property.equals(XSD)) {
//					return ((AttributeXSD) element).getName();
//				}
//			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof TreeItem && ((TreeItem) element).getData() instanceof DefinitionXSD) {
				DefinitionXSD definition = (DefinitionXSD) ((TreeItem) element).getData();
				if (property.equals(TYPE)) {
					if (value != null && !((String) value).isEmpty()) {
						definition.setType((String) value);
					}
					xsdViewer.refresh();
				}

				if (definition instanceof DefinitionXsdOutput) {
					DefinitionXsdOutput defOutput = (DefinitionXsdOutput) definition;
					if (property.equals(INPUT_OUTPUT)) {
						if (((Integer) value) < 0) {
							return;
						}
						definition.setOutputName(fileXML.getInputs().get((Integer) value).getName());
						xsdViewer.refresh();
					}
					else if (property.equals(INPUT_COLUMN)) {
						if (value == null || !(value instanceof StreamElement)) {
							return;
						}
						defOutput.setInputColumn(((StreamElement) value).name);
						xsdViewer.refresh();
					}
					else if (property.equals(PARENT_COLUMN)) {
						if (value == null || !(value instanceof StreamElement)) {
							return;
						}
						defOutput.setParentColumn(((StreamElement) value).name);
						xsdViewer.refresh();
					}
					else if (property.equals(CHILD_COLUMN)) {
						if (value == null || !(value instanceof StreamElement)) {
							return;
						}
						defOutput.setChildColumn(((StreamElement) value).name);
						xsdViewer.refresh();
					}
				}
				else {
					if (property.equals(INPUT_OUTPUT)) {
						if (((Integer) value) < 0) {
							return;
						}
						definition.setOutputName(fileXML.getOutputs().get((Integer) value).getName());
						xsdViewer.refresh();
					}
					else if (property.equals(COLUMN_ID)) {
						xsdViewer.refresh();
					}
				}
			}
		}
	}

	private class ParameterDialogCellEditor extends DialogCellEditor {

		public ParameterDialogCellEditor(Composite parent) {
			super(parent);
		}

		@Override
		protected Object openDialogBox(Control arg0) {
			Object o = ((IStructuredSelection) xsdViewer.getSelection()).getFirstElement();
			if (o instanceof DefinitionXSD) {
				DialogCalculationEditor d = new DialogCalculationEditor(getShell(), fileXML, (DefinitionXSD) o, ((DefinitionXSD) o).getColumnId());
				if (d.open() == Dialog.OK) {

				}
			}

			return null;
		}
	}

	private LabelProvider streamElementProvider = new LabelProvider() {

		@Override
		public String getText(Object element) {
			if (element instanceof StreamElement) {
				return ((StreamElement) element).name;
			}
			return ""; //$NON-NLS-1$
		}
	};

	private class StreamElementProvider implements IStructuredContentProvider {

		private boolean getParentElements;

		public StreamElementProvider(boolean getParentElements) {
			this.getParentElements = getParentElements;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof DefinitionXsdOutput) {
				List<StreamElement> elements = getStreamElements((DefinitionXsdOutput) inputElement, getParentElements);
				return elements.toArray(new StreamElement[elements.size()]);
			}
			return null;
		}
	}
}
