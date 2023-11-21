package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.Prompt;
import bpm.metadata.ui.query.composite.CompositeSqlQuery;
import bpm.metadata.ui.query.composite.IContextManager;
import bpm.metadata.ui.query.dialogs.DialogQueryPrompt;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;

public class DialogQueryBuilder extends Dialog implements IContextManager {

	private IBusinessPackage pack;
	private Combo groupNames, userNames, connections;
	private CompositeSqlQuery queryComposite;
	private ConcurrentHashMap<Prompt, List<String>> promptMap = new ConcurrentHashMap<Prompt, List<String>>(); 
	private static final String NO_USER = "--- None ---";
	private static final String NO_GROUP = "--- None ---";
	
	private List<User> users;
	
	@Override
	protected void initializeBounds() {
		fillDatas();
		getShell().setSize(900, 600);
	}

	public DialogQueryBuilder(Shell parentShell, IBusinessPackage pack) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.pack = pack;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite settings = new Composite(container, SWT.NONE);
		settings.setLayout(new GridLayout(2, false));
		settings.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Label l9 = new Label(settings, SWT.NONE);
		l9.setLayoutData(new GridData());
		l9.setText(Messages.DialogQueryBuilder_2);
		
		groupNames = new Combo(settings, SWT.READ_ONLY);
		groupNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		List<String> nms = new ArrayList<String>();
		nms.add(NO_GROUP);
		nms.addAll(getGroups());
		
		groupNames.setItems(nms.toArray(new String[nms.size()]));
		groupNames.select(0);
		groupNames.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});

		Label lblUsers = new Label(settings, SWT.NONE);
		lblUsers.setLayoutData(new GridData());
		lblUsers.setText("Users");
		
		userNames = new Combo(settings, SWT.READ_ONLY);
		userNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		nms = new ArrayList<String>();
		nms.add(NO_USER);
		nms.addAll(getUsers());
		
		userNames.setItems(nms.toArray(new String[nms.size()]));
		userNames.select(0);
		
		Label l0 = new Label(settings, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.DialogQueryBuilder_0); //$NON-NLS-1$
		
		connections = new Combo(settings, SWT.READ_ONLY);
		connections.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		List<String> conNames = ((SQLDataSource)pack.getDataSources("none").get(0)).getConnectionNames(); //$NON-NLS-1$
		connections.setItems(conNames.toArray(new String[conNames.size()]));
		
		queryComposite = new CompositeSqlQuery(container, SWT.NONE, true, this);
		queryComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Button  run = new Button(container, SWT.PUSH);
		run.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		run.setText(Messages.DialogQueryBuilder_23); //$NON-NLS-1$
		run.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				QuerySql query = queryComposite.createFmdtQuery();
				
				DialogBrowse dial;
				try {
					String groupName = null;
					if (groupNames.getSelectionIndex() != 0){
						groupName = groupNames.getText();
					}
					else{
						groupName = "none";
					}

					if (!promptMap.isEmpty()){
						for(Prompt p : promptMap.keySet()) {
							if(!query.getPrompts().contains(p)) {
								promptMap.remove(p);
							}
						}
					}	
					if (!query.getPrompts().isEmpty()) {
						DialogQueryPrompt d = new DialogQueryPrompt(getShell(), query, promptMap);
						d.open();
					}
					
					IRepositoryContext ctx = Activator.getDefault().getRepositoryContext();
					IVanillaContext vCtx = getVanillaContext();
					EffectiveQuery sqlQuery = SqlQueryGenerator.getQuery(
							ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, 
							vCtx != null ? vCtx : (ctx != null ? ctx.getVanillaContext() : null), 
							pack, 
							query, 
							groupName, 
							false, 
							promptMap);
					
					
					List<List<String>> values = pack.executeQuery(query.getLimit(), 
							connections.getText(), 
							sqlQuery.getGeneratedQuery());
					if (values.isEmpty()){
						MessageDialog.openInformation(getShell(), "Query Result", "No result for this query");
						return;
					}
					
					List<Ordonable> colNames = new ArrayList<Ordonable>();
					colNames.addAll(query.getSelect());
					colNames.addAll(query.getFormulas());
					
					dial = new DialogBrowse(
							getShell(), values, colNames, query.getAggs()	);
					dial.open();
				} catch (Exception e1) {
					e1.printStackTrace();
					
					DialogError dialError = new DialogError(getShell(), Messages.DialogQueryBuilder_25 + e1.getMessage());
					dialError.open();
				}
			}
			
		});
		
		return parent;
	}
	
	private void fillDatas(){
		if (!groupNames.getText().equals(NO_GROUP)){ //$NON-NLS-1$
			List<String> conNames = ((SQLDataSource)pack.getDataSources("none").get(0)).getConnectionNames(groupNames.getText()); //$NON-NLS-1$
			connections.setItems(conNames.toArray(new String[conNames.size()]));
		}
		else{
			List<String> conNames = ((SQLDataSource)pack.getDataSources("none").get(0)).getConnectionNames(); //$NON-NLS-1$
			connections.setItems(conNames.toArray(new String[conNames.size()]));
		}
		connections.select(0);
		String groupName = null;
		if (groupNames.getSelectionIndex() != 0){
			groupName = groupNames.getText();
		}
		else{
			groupName = "none";
		}
		try {
			
			queryComposite.setConnection(pack, groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getGroups(){
		return GroupHelper.getGroups(0, 0);
	}
	
	private List<String> getUsers(){
		this.users = GroupHelper.getUsers(0, 0);
		
		List<String> userNames = new ArrayList<>();
		if (users != null) {
			for (User user : users) {
				userNames.add(user.getLogin());
			}
		}
		return userNames;
	}

	@Override
	public IVanillaContext getVanillaContext() {
		User user = null;
		if (userNames.getSelectionIndex() != 0){
			user = getUser(userNames.getText());
		}
		
		IRepositoryContext ctx = Activator.getDefault().getRepositoryContext();
		IVanillaContext vCtx = ctx != null ? ctx.getVanillaContext() : null;
		return vCtx != null ? new BaseVanillaContext(vCtx.getVanillaUrl(), user != null ? user.getLogin() : vCtx.getLogin(), user != null ? user.getPassword() : vCtx.getPassword()) : null;
	}
	
	private User getUser(String login) {
		if (users != null) {
			for (User user : users) {
				if (user.getLogin().equals(login)) {
					return user;
				}
			}
		}
		return null;
	}
}
