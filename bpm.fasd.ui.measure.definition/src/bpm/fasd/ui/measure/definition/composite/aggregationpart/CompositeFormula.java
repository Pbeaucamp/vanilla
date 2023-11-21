package bpm.fasd.ui.measure.definition.composite.aggregationpart;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;



public class CompositeFormula extends Composite {
	
	private static final String[] functionsName={"abs(value:Double)", "avg(value:Double)", "max(value:Double)", "min(value:Double)"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static final String[] functions={"abs()", "avg()", "max()", "min()"};	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	private static final String[] financialName=
		{   "ddb(cost:double,salvage:double,life:double,period:int)",
			"ipmt(rate:double,per:int,nPer:int,pv:double,fv:double,due:int)",
			"nper(rate:double,pmt:double,pv:double,fv:double,due:int)",
			"percent(denom:double,num:double,valueIfZero:double)",
			"pmt(rate:double,nper:int,pv:double,fv:double,due:int)",
			"ppmt(rate:double,per:int,nPer:int,pv:double,fv:double,due:int)",
			"pv(rate:double,nPer:int,pmt:double,fv:double,due:int)",
			"sln(cost:Double,salvage:Double,life:Double)",
		    "syd(cost:Double,salvage:Double,life:Double,period:Double)"
		};
	private static final String[] financial=
		{
			"ddb()",
			"ipmt()",
			"nper()",
			"percent()",
			"pmt()",
			"ppmt()",
			"pv()",
			"sln()",
			"syd()"
		};
	
	private static final String[] statisticName={"acos(x:number)", "asin(x:number)", "atan(x:number)", "cos(x:number)", "log(x:number)", "sin(x:number)", "tan(x:number)"};
	private static final String[] statistic={"acos()", "asin()", "atan()", "cos()", "log()", "sin()", "tan()"};
	
	
	private static final String[] operators={"(", "+", "-", "*", "/", "^", ")"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	private TreeViewer viewer = null;
	private Text textArea = null;
	private FAModel model;
	private ModifyListenerNotifier listener;
	//private DataObject data;

	public CompositeFormula(Composite parent, int style, FAModel model, ModifyListenerNotifier listener) {
		super(parent, style);
		this.model = model;
		this.listener = listener;
		initialize();
	}
	
//	public void setDataObject(DataObject d){
//		data = d;
//	}

	public String getFormula(){
		return textArea.getText();
	}
	
	public void clear(){
		textArea.setText(""); //$NON-NLS-1$
	}
	
	private void initialize() {
		GridLayout layout = new GridLayout(2, false);
		this.setLayout(layout);
		
		Label lb1 = new Label(this, SWT.NONE);
		lb1.setLayoutData(new GridData());;
		lb1.setText(Messages.CompositeFormula_16);
		
		Label lb2 = new Label(this, SWT.NONE);
		lb2.setLayoutData(new GridData());;
		lb2.setText(Messages.CompositeFormula_0);
		
		viewer = new TreeViewer(this, SWT.BORDER | SWT.RESIZE);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL));
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewerLabelProvider());
		viewer.setAutoExpandLevel(2);
		TreeParent input = createModel(model.getOLAPSchema());
		viewer.setInput(input);
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				Object o = ss.getFirstElement();
				
				if (o instanceof TreeFunction){
					textArea.setText(textArea.getText() + ((TreeFunction)o).getFunction());
				}
				else if (o instanceof TreeOperator){
					textArea.setText(textArea.getText() + ((TreeOperator)o).getName());
				}
				else if (o instanceof TreeMes){
					textArea.setText(textArea.getText() + "[Measures]." + "[" + ((TreeMes)o).getOLAPMeasure().getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
			}
			
		});
		
		
		
		textArea = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.RESIZE);
		textArea.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL));
		textArea.addModifyListener(listener);
		//textArea.setEditable(false);
		
		Label lbl = new Label(this,SWT.NONE);
		lbl.setLayoutData(new GridData());
		lbl.setText("      "); //$NON-NLS-1$
		
		Button but = new Button(this, SWT.PUSH);
		but.setLayoutData(new GridData(SWT.CENTER));
		but.setText(Messages.CompositeFormula_22);
		but.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				textArea.setText(""); //$NON-NLS-1$
			}
			
		});
	}
	
	public void setFormula(String formula){
		textArea.setText(formula);
	}

	private TreeParent createModel(OLAPSchema schema){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		TreeParent functions = new TreeParent(Messages.CompositeFormula_25);
		for(int i=0; i<CompositeFormula.functions.length ; i++)
			functions.addChild(new TreeFunction(CompositeFormula.functionsName[i],CompositeFormula.functions[i]));
		root.addChild(functions);
		
		TreeParent financials = new TreeParent("Financial");
		for(int i=0; i<CompositeFormula.financial.length ; i++)
			financials.addChild(new TreeFunction(CompositeFormula.financialName[i],CompositeFormula.financial[i]));
		root.addChild(financials);
		
		TreeParent statistics = new TreeParent("Statistics");
		for(int i=0; i<CompositeFormula.statistic.length ; i++)
			statistics.addChild(new TreeFunction(CompositeFormula.statisticName[i],CompositeFormula.statistic[i]));
		root.addChild(statistics);
		
		TreeParent operators = new TreeParent(Messages.CompositeFormula_26);
		for(String s : CompositeFormula.operators)
			operators.addChild(new TreeOperator(s));
		root.addChild(operators);
		TreeParent measures = new TreeParent(Messages.CompositeFormula_27);
		for(OLAPMeasure m : schema.getMeasures()){
//			if (m.getType().equals("physical")){
//				if (m.getOrigin().getParent() == data)
//					measures.addChild(new TreeMeasure(m));
//			}
//			else
				measures.addChild(new TreeMes(m));
				
		}
		root.addChild(measures);
		
		return root;
	}
	
	
	class TreeFunction extends TreeParent{
		private String function;
		
		public TreeFunction(String name, String function){
			super(name);
			this.function = function;
		}
		
		public String getFunction(){
			return function;
		}
		
	}

	class TreeOperator extends TreeParent{

		public TreeOperator(String name) {
			super(name);
		}
		
		public String getOperator(){
			return getName();
		}
		
	}


	class ViewerLabelProvider extends LabelProvider{
		public String getText(Object obj) {
			if (obj instanceof OLAPMeasure)
				return ((OLAPMeasure)obj).getName();
			return obj.toString();
		}

		public Image getImage(Object obj) {
			if (obj instanceof TreeMes) {
				try {
					return new Image(CompositeFormula.this.getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_measure.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			else if(obj instanceof TreeOperator){
				try {
					return null;
					//return new Image(CompositeFormula.this.getDisplay(),"icons/obj_operator.png");
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			else if (obj instanceof TreeFunction){
				try {
					return new Image(CompositeFormula.this.getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_function.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			else if (obj instanceof TreeParent) {
				try {
					return new Image(CompositeFormula.this.getDisplay(),Platform.getInstallLocation().getURL().getPath() +"icons/obj_element.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			return null;
		}
		
	}
	
	public class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider{

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
        
		public void dispose() {
		}
        
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}
        
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
        
		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}

        public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}
		
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
