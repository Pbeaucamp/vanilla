package bpm.birep.admin.client.composites;


/**
 * @deprecated Servers no more used within ES
 * @author ludo
 * delete candidate
 *
 */
public class CompositeServer {}
//extends Composite{
//	
//
//	private Text name;
//	private Text description;
//	private Text url;
//	private Combo type;
//	private Server server; 
//	
//	
//	public CompositeServer(Composite parent, int style) {
//		super(parent, style);
//		this.setLayout(new GridLayout(2, false));
//		createContent();
//	}
//	
//	public void setServer(Server s){
//		this.server = s;
//		fillDatas();
//	}
//	
//	
//	private void fillDatas(){
//		type.select(server.getType());
//		type.setEnabled(false);
//		name.setText(server.getName());
//		name.setEnabled(false);
//		if (server.getDescription() != null){
//			description.setText(server.getDescription());
//		}
//		
//		description.setEnabled(false);
//		url.setText(server.getUrl());
//		url.setEnabled(false);
//	}
//	
//	private void createContent(){
//		Label l = new Label(this, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Server Name");
//		
//		name = new Text(this, SWT.BORDER);
//		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		Label l2 = new Label(this, SWT.NONE);
//		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 3));
//		l2.setText("Description");
//		
//		description = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
//		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
//		
//		
//		Label l3 = new Label(this, SWT.NONE);
//		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l3.setText("Server Url");
//		
//		url = new Text(this, SWT.BORDER);
//		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
//		
//		Label l4 = new Label(this, SWT.NONE);
//		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l4.setText("Server Type");
//		
//		type = new Combo(this, SWT.READ_ONLY);
//		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		type.setItems(Server.TYPE_NAMES);
//	}
//}
