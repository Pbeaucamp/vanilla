package metadata.client.wizards.connection;

import org.eclipse.jface.wizard.Wizard;

import bpm.metadata.layer.physical.IConnection;

public class WizardConnection extends Wizard{

	private PageSqlConnection sqlPage;
	
	private IConnection connection;
	
		
	@Override
	public void addPages() {
		sqlPage = new PageSqlConnection("SqlConnection");
		addPage(sqlPage);
		
	}
	
	@Override
	public boolean performFinish() {
		connection =  sqlPage.getConnection();
		return true;
	}

	
	public IConnection getConnection(){
		return connection;
	}
}
