package bpm.birep.admin.client.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;


public class DialogLdap extends Dialog {

	private Text url, username, password, node;
	private Combo authentication;
	
	public static final int GROUPS = 0;
	public static final int USERS = 1;
	
	private int type = -1;
	
	private List<Object> list;
	
	public DialogLdap(Shell parentShell, int type) {
		super(parentShell);
	
		this.type = type;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("LDAP server Url"); //$NON-NLS-1$
		
		url = new Text(c, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.setText("ldap://localhost:10389"); //$NON-NLS-1$
		
		Label l1 = new Label(c, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText("Login"); //$NON-NLS-1$
		
		username = new Text(c, SWT.BORDER);
		username.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		username.setText("uid=admin,ou=system"); //$NON-NLS-1$
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText("Password"); //$NON-NLS-1$
		
		password = new Text(c, SWT.BORDER);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText("moi"); //$NON-NLS-1$
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText("Authentication Method"); //$NON-NLS-1$
		
		authentication = new Combo(c, SWT.BORDER);
		authentication.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		authentication.setItems(new String[]{"simple"}); //$NON-NLS-1$
		
		Label l4 = new Label(c, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText("Search Node"); //$NON-NLS-1$
		
		node = new Text(c, SWT.BORDER);
		node.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		if (type == GROUPS){
			node.setText("ou=groups,dc=example,dc=com"); //$NON-NLS-1$
		}
		else{
			node.setText("ou=users,dc=example,dc=com"); //$NON-NLS-1$
		}
		
		
		return c;
	}

	@Override
	protected void okPressed() {
		
		try {
			list = getFromLdap(node.getText(), url.getText(), authentication.getText(), username.getText(), password.getText());
		} catch (Exception e) {
			MessageDialog.openError(getShell(),"error", e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
		}
		
		super.okPressed();
	}

	
	public List<Object> getValues(){
		return list;
	}
	
	private List<Object> getFromLdap(String node, String url, String auth, String user, String password) throws Exception{
		List<Object> result = new ArrayList<Object>();
		
		Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" ); //$NON-NLS-1$
	    env.put(Context.PROVIDER_URL, url );
	    env.put(Context.SECURITY_AUTHENTICATION, auth );
	    env.put(Context.SECURITY_PRINCIPAL, user );
	    env.put(Context.SECURITY_CREDENTIALS, password ); 			
	    
	    DirContext ctx = new InitialDirContext(env);
		
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		NamingEnumeration resultat = null;
		
		
		if (type == GROUPS){
			resultat = ctx.search(node, "(cn=*)",ctls); //$NON-NLS-1$
		}
		else if (type == USERS){
			resultat = ctx.search(node, "(uid=*)",ctls); //$NON-NLS-1$
		}
		else{
			throw new Exception("Invalid type"); //$NON-NLS-1$
		}
		while (resultat!=null && resultat.hasMoreElements()) {
		      
			SearchResult sr = (SearchResult) resultat.next();
		    Attributes attrs = sr.getAttributes();
		 		     
		      if (type == GROUPS){
		    	  Group g = new Group();
		    	  g.setName(sr.getName().substring(3));
		    	  result.add(g);
		      }
		      else if (type == USERS){
		    	  User u = new User();
		    	  u.setName(sr.getName().substring(4));

		    	  Attribute at = attrs.get("userPassword"); //$NON-NLS-1$
		    	  byte [] encPassword = (byte[])at.get();
		    	  String encStrPassword = new String(encPassword);
		    	  //System.out.println("pass: " + encStrPassword);
		    	  u.setPassword(encStrPassword);
		    	  u.setDatePasswordModification(new Date());
		    	  result.add(u);
		      }
		     
		 }
			   
	
		return result;
	}
	
}
