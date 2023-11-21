package metadata.client.wizards.big;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import metadataclient.Activator;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.utils.IOWriter;

public class BigWizard extends Wizard{
	private ConfigurationPage confPage;
	
	
	@Override
	public void addPages() {
		super.addPages();
		confPage = new ConfigurationPage("Conf");
		confPage.setTitle("Gateway Transformation Definition");
		confPage.setDescription("Select the BusinessTable that will be extracted and configure the outputs for them.");
		addPage(confPage);
		
	}
	@Override
	public boolean performFinish() {
		try{
			File f = confPage.getFile();
			if (f.exists()){
				if (!MessageDialog.openQuestion(getShell(), "Gateway Transformation Generation", "The file " + f.getAbsolutePath() + " already exists. Are you sure you want to override it?")){
					return false;
				}
			}
			
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(Activator.getDefault().getVanillaContext());
			User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(Activator.getDefault().getVanillaContext().getLogin());
			
			RemoteGatewayComponent rem = new RemoteGatewayComponent(
					Activator.getDefault().getVanillaContext());
			
			byte[] datas = rem.generateFmdtExtractionTransformation(confPage.createConfig(), user);

			InputStream is = new ByteArrayInputStream(Base64.decodeBase64(datas));
			
			FileOutputStream bos = new FileOutputStream(f);
			IOWriter.write(is, bos, true, true);
			
			MessageDialog.openInformation(getShell(), "Gateway Transformation Generation", "The transformatio has been successfully created in " + f.getAbsolutePath() + ".");
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Gateway Transformation Generation", "The gateway transformation could not be created: \n" + ex.getMessage());
			return false;
		}
		
		
	}

}
