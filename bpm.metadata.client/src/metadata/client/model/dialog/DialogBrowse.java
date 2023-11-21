package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeTableBrowser;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class DialogBrowse extends Dialog {
	@Override
	
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogBrowse_0); //$NON-NLS-1$
	}

	private CompositeTableBrowser composite;
	private List<Ordonable> cols;
	private IBusinessPackage pack;
	private HashMap<ListOfValue, String> lov;
	private List<Prompt>prompt;
	private List<List<String>> promptValues;
	private List<AggregateFormula> aggs;
	private List<List<String>> values;
	List<IFilter> filters ;
	private String groupName;
	
	private int limit = 0;
	private boolean distinct = false;
	
	public DialogBrowse(Shell parentShell, String groupName, List<Ordonable> cols, HashMap<ListOfValue, String> lov, List<AggregateFormula> aggs, List<IFilter> filters, List<Prompt> prompt, List<List<String>> promptValues, IBusinessPackage pack, int limit, boolean distinct) {
		super(parentShell);
		this.filters = filters;
		this.cols = cols;
		this.pack = pack;
		this.lov = lov;
		this.aggs = aggs;
		this.prompt = prompt;
		this.promptValues = promptValues;
		this.limit = limit;
		this.distinct = distinct;
		this.groupName = groupName;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogBrowse(Shell parentShell, List<List<String>> values) {
		super(parentShell);
		this.values = values;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogBrowse(Shell parentShell, List<List<String>> values, List<Ordonable> colNames, List<AggregateFormula> list) {
		super(parentShell);
		this.values = values;
		this.cols = colNames;
		this.aggs = list;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		List<String> colNames = new ArrayList<String>();
		
		if (cols != null){
			for(Ordonable e : cols){
				colNames.add(e.getOutputName());
			}
		}
		
		if (aggs != null){
			for(AggregateFormula f : aggs){
				colNames.add(f.getFunction() + f.getCol().getName());
			}
		}
		
		
		try {
			if (values != null){
				if(colNames == null || colNames.isEmpty()) {
					composite = new CompositeTableBrowser(parent, SWT.NONE, null, values);
				}
				else {
					composite = new CompositeTableBrowser(parent, SWT.NONE, colNames, values);
				}
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			}else{
				
				List<IDataStreamElement> elements = new ArrayList<IDataStreamElement>();
				for(Ordonable o : cols ) {
					elements.add((IDataStreamElement) o);
				}
				
				IQuery q = SqlQueryBuilder.getQuery(groupName, elements, lov, aggs, null, filters, prompt);
				String conName = ((SQLDataSource)pack.getDataSources(groupName).get(0)).getConnections().get(0).getName();
				if (q instanceof QuerySql){
					((QuerySql)q).setLimit(limit);
					((QuerySql)q).setDistinct(distinct);
				}
				
				composite = new CompositeTableBrowser(parent, SWT.NONE, colNames, pack.executeQuery(null, null, conName, q, promptValues));
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			}
			
		} catch (Exception e) {
			Activator.getLogger().error(Messages.DialogBrowse_1, e); //$NON-NLS-1$
			e.printStackTrace();
			
			MessageDialog.openError(getShell(), Messages.DialogBrowse_2, e.getMessage()); //$NON-NLS-1$
		}
		
		
		
		return parent;
	}
}
