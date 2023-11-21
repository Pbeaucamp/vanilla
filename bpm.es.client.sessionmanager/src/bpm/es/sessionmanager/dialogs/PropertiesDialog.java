package bpm.es.sessionmanager.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.VanillaLogsProps;



public class PropertiesDialog extends Dialog {

	private Display display;
	
	private List<VanillaLogsProps> props;
	private TableViewer viewer;
	
	public PropertiesDialog(Shell parentShell, List<VanillaLogsProps> props) {
		super(parentShell);
		this.props = props;
		this.display = parentShell.getDisplay();
	}

	protected void initializeBounds() {
		getShell().setSize(500, 600);
	}

	protected Control createDialogArea(Composite parent) {
//	    Composite main = new Composite(parent, SWT.NONE);
//	     FillLayout fl = new FillLayout();
//	     fl.type = SWT.VERTICAL;
//	      main.setLayout(fl);
//	    main.setFont(parent.getFont());
//
////	      ScrolledComposite scrollParent = new ScrolledComposite(main,
////	    		  SWT.V_SCROLL | SWT.H_SCROLL);
////	      Composite par = new Composite(scrollParent, SWT.NONE);
////	      scrollParent.setContent(par);
////	      scrollParent.setAlwaysShowScrollBars(true);
////	      par.setLayout(new GridLayout());
//
//	      Composite par = new Composite(parent, SWT.NONE);
//	      par.setLayout(new GridLayout());
//	    
//	      Label l = new Label(parent, SWT.NONE);
//	      l.setText(props.get(0).getName());
//	      
//	      Text t = new Text(par, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//	      t.setText(props.get(0).getValue());
//	      GridData data = new GridData(GridData.FILL_BOTH);
//	      data.widthHint = 400;
//	      data.heightHint = 400;
//	      t.setLayoutData(data);
//
////	      Point minSize = parent.computeSize(200, 200);
////	      scrollParent.setExpandVertical(true);
////	      scrollParent.setMinWidth(minSize.x);
////	      scrollParent.setExpandHorizontal(true);
////	      scrollParent.setMinHeight(minSize.y);

		Composite main = new Composite(parent, SWT.NONE /*SWT.H_SCROLL | SWT.V_SCROLL*/);
		main.setLayout(new FillLayout());
//		main.setLayout(new GridLayout(2, false));
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for (VanillaLogsProps prop : props) {
			
		      Composite par = new Composite(main, SWT.NONE);
		      par.setLayout(new GridLayout());
			
			Label l = new Label(par, SWT.NONE);
			l.setLayoutData(new GridData());
			l.setText(prop.getName());
			
			Text t = new Text(par, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			//t.setLayoutData(new GridData(GridData.FILL_BOTH));
		      GridData data = new GridData(GridData.FILL_BOTH);
		      data.widthHint = 450;
		      data.heightHint = 450;
		      t.setLayoutData(data);
			t.setEditable(false);
			t.setText(prop.getValue());
		}
		
//		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL | SWT.FULL_SELECTION);
//		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		viewer.setContentProvider(new IStructuredContentProvider() {
//			
//			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//			}
//			
//			public void dispose() {
//			}
//			
//			public Object[] getElements(Object inputElement) {
//				List<VanillaLogsProps> props = (List<VanillaLogsProps>) inputElement;
//				
//				return props.toArray();
//			}
//		});
//		
//		viewer.setLabelProvider(new ITableLabelProvider(){
//
//			public Image getColumnImage(Object element, int columnIndex) {
//				return null;
//			}
//
//			public String getColumnText(Object element, int columnIndex) {
//				VanillaLogsProps props = (VanillaLogsProps) element;
//				
//				switch(columnIndex){
//					case 0:
//						return props.getName();
//					case 1:
//						return props.getValue();
//				}
//				return "";
//			}
//
//			public void addListener(ILabelProviderListener listener) {
//			}
//			public void dispose() {
//			}
//			public boolean isLabelProperty(Object element, String property) {
//				return false;
//			}
//			public void removeListener(ILabelProviderListener listener) {
//			}
//		});
//		
//		TableColumn colName = new TableColumn(viewer.getTable(), SWT.NONE);
//		colName.setText("Name");
//		colName.setWidth(100);
//		
//		TableColumn colValue = new TableColumn(viewer.getTable(), SWT.MULTI);
//		colValue.setText("Value");
//		colValue.setWidth(500);
//		
////		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.NONE);
////		colName.getColumn().setText("Name");
////		colName.getColumn().setWidth(100);
////
////		TableViewerColumn colValue = new TableViewerColumn(viewer, SWT.NONE);
////		colValue.getColumn().setText("Value");
////		colValue.getColumn().setWidth(150);
//		
//		Listener paintListener = new Listener() {
//		      public void handleEvent(Event event) {
//		        switch (event.type) {
//		        case SWT.MeasureItem: {
//		          TableItem item = (TableItem) event.item;
//		          String text = getText(item, event.index);
//		          Point size = event.gc.textExtent(text);
//		          event.width = size.x;
//		          event.height = Math.max(event.height, size.y);
//		          break;
//		        }
//		        case SWT.PaintItem: {
//		          TableItem item = (TableItem) event.item;
//		          String text = getText(item, event.index);
//		          Point size = event.gc.textExtent(text);
//		          int offset2 = event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0;
//		          event.gc.drawText(text, event.x, event.y + offset2, true);
//		          break;
//		        }
//		        case SWT.EraseItem: {
//		          event.detail &= ~SWT.FOREGROUND;
//		          break;
//		        }
//		        }
//		      }
//
//		      String getText(TableItem item, int column) {
//		        String text = item.getText(column);
//		        if (column != 0) {
//		          int index = viewer.getTable().indexOf(item);
//		          if ((index + column) % 3 == 1) {
//		            text += "\nnew line";
//		          }
//		          if ((index + column) % 3 == 2) {
//		            text += "\nnew line\nnew line";
//		          }
//		        }
//		        return text;
//		      }
//		    };
//		    
//		viewer.getTable().addListener(SWT.MeasureItem, paintListener);
//		viewer.getTable().addListener(SWT.PaintItem, paintListener);
//		viewer.getTable().addListener(SWT.EraseItem, paintListener);
//		
//		viewer.setInput(props);
		
		return main;
	}
}

