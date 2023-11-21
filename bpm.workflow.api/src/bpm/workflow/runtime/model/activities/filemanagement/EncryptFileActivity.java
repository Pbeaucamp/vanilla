package bpm.workflow.runtime.model.activities.filemanagement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.utils.PGPFileProcessor;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IEncrypt;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.variables.ListVariable;

public class EncryptFileActivity extends AbstractActivity implements IOutputProvider, IAcceptInput, IEncrypt {

	private String pathToEncrypt;
	private String pathToStore = ListVariable.VANILLA_TEMPORARY_FILES;
	private String outputName;
	
	private String generatedFilePath;
	
	private static int number = 0;
	
	private boolean encrypt = false;
	private String publicKeyPath;
	private String publicKeyName;
	private String privateKeyPath;
	private String privateKeyName;
	private String password;

	public EncryptFileActivity() {
		number++;
	}

	/**
	 * Create a mail activity with the specified name
	 * @param name
	 */
	public EncryptFileActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		generatedFilePath = this.getId() + "_outputpath";
		number++;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		generatedFilePath = this.getId() + "_outputpath";
	}

	public EncryptFileActivity copy() {
		EncryptFileActivity a = new EncryptFileActivity();

		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("encryptFileActivity");
		e.addElement("pathtostore").setText(pathToStore);
		e.addElement("pathtoencrypt").setText(pathToEncrypt);
		e.addElement("encrypt").setText(encrypt + "");
		e.addElement("publicKeyPath").setText(publicKeyPath != null ? publicKeyPath : "");
		e.addElement("privateKeyPath").setText(privateKeyPath != null ? privateKeyPath : "");
		e.addElement("publicKeyName").setText(publicKeyName != null ? publicKeyName : "");
		e.addElement("privateKeyName").setText(privateKeyName != null ? privateKeyName : "");
		e.addElement("password").setText(password != null ? password : "");
		if(outputName != null) {
			e.addElement("outputName").setText(outputName);
		}
		
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if(pathToEncrypt == null || pathToEncrypt.isEmpty()) {
			buf.append("For activity " + name + ", no input file path is set.\n");
		}

		return buf.toString();
	}

	public Class<?> getServerClass() {
		return FileServer.class;
	}

	public void decreaseNumber() {
		number--;
	}

	public String getPathToStore() {
		return pathToStore;
	}

	public void setPathToStore(String pathToStore) {
		this.pathToStore = pathToStore;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				pathToStore = workflowInstance.parseString(pathToStore);

				if(!pathToStore.endsWith(File.separator)) {
					pathToStore += File.separator;
				}
				
				String pathout = pathToStore + outputName;
	
				String path = "";
				try {
					path = workflowInstance.parseString(pathToEncrypt);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				if (path.equalsIgnoreCase("")) {
					throw new Exception("No available file to attach from previews steps");
				}
				else {
					File fileToEncrypt = new File(path);
					if(!fileToEncrypt.exists()) {
						throw new Exception("No available file to attach from previews steps");
					}
					
					String pathToKey = "";
					if(encrypt && (publicKeyPath == null || publicKeyPath.isEmpty() || publicKeyName == null || publicKeyName.isEmpty())) {
						throw new Exception("The Public Key path is not set. Unable to encrypt/decrypt the file.");
					}
					else if(!encrypt && (privateKeyPath == null || privateKeyPath.isEmpty() || privateKeyName == null || privateKeyName.isEmpty() || password == null || password.isEmpty())) {
						throw new Exception("The Private Key path or the Password is not set. Unable to encrypt/decrypt the file.");
					}
					
					if(encrypt) {
						pathToKey = workflowInstance.parseString(publicKeyPath);
						if(!pathToKey.endsWith(File.separator)) {
							pathToKey += File.separator;
						}
						pathToKey += publicKeyName;
					}
					else {
						pathToKey = workflowInstance.parseString(privateKeyPath);
						if(!pathToKey.endsWith(File.separator)) {
							pathToKey += File.separator;
						}
						pathToKey += privateKeyName;
					}
					
					if(pathToKey.isEmpty()) {
						throw new Exception("The path to the key (public or private) is corrupted. Please check the workflow model.");
					}

					FileInputStream fis = new FileInputStream(fileToEncrypt);
					ByteArrayOutputStream bos = null;
					if(encrypt) {
						bos = PGPFileProcessor.encrypt(pathToKey, fis, false, true);
					}
					else {
						bos = PGPFileProcessor.decrypt(pathToKey, fis, password);
					}

					if(bos == null) {
						throw new Exception("A problem occured during encrypting.");
					}
					
					File folderEncrypt = new File(pathToStore);
					folderEncrypt.mkdirs();
						
					File fileOut = new File(pathout);
					if(!fileOut.exists()) {
						fileOut.createNewFile();
					}
					
					OutputStream outputStream = new FileOutputStream (fileOut); 
					bos.writeTo(outputStream);
					bos.close();
					outputStream.close();
				}


				workflowInstance.getOrCreateVariable(getOutputVariable()).addValue(pathout);

				activityResult = true;
			} catch(Throwable e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
	}

	@Override
	public void addInput(String input) {
		this.pathToEncrypt = input;
	}

	@Override
	public List<String> getInputs() {
		List<String> inputs = new ArrayList<String>();
		if(pathToEncrypt != null) {
			inputs.add(pathToEncrypt);
		}
		return inputs;
	}

	@Override
	public void removeInput(String input) {
		if(input.equals(pathToEncrypt)) {
			this.pathToEncrypt = null;
		}
	}

	@Override
	public void setInputs(List<String> inputs) {
		if(inputs != null && !inputs.isEmpty()) {
			this.pathToEncrypt = inputs.get(0);
		}
	}

	@Override
	public String getOutputVariable() {
		return generatedFilePath;
	}

	@Override
	public boolean isEncrypt() {
		return encrypt;
	}

	@Override
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}
	
	public void setEncrypt(String encrypt) {
		this.encrypt = Boolean.parseBoolean(encrypt);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	@Override
	public String getPublicKeyPath() {
		return publicKeyPath;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	@Override
	public void setPublicKeyPath(String publicKeyPath) {
		this.publicKeyPath = publicKeyPath;
	}

	@Override
	public String getPrivateKeyName() {
		return privateKeyName;
	}

	@Override
	public String getPublicKeyName() {
		return publicKeyName;
	}

	@Override
	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}

	@Override
	public void setPublicKeyName(String publicKeyName) {
		this.publicKeyName = publicKeyName;
	}

}
