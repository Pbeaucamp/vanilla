package bpm.gateway.ui.dialogs.database.wizard.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.tools.database.DBConverterManager;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gatewaywizard.GatewayInformationPage;
import bpm.gateway.ui.i18n.Messages;

public class DBMigrationWizard extends Wizard {

	private static final String selectionTitle = Messages.DBMigrationWizard_0;
	private static final String selectionDescription = Messages.DBMigrationWizard_1;
	private static final String contentTitle = Messages.DBMigrationWizard_2;
	private static final String contentDescription = Messages.DBMigrationWizard_3;
	private static final String optionTitle = Messages.DBMigrationWizard_4;
//	private static final String optionDescription = "Generation Option";

	
	private static final String INFORMATION_PAGE_NAME = Messages.DBMigrationWizard_5;
	private static final String INFORMATION_PAGE_DESCRIPTION = Messages.DBMigrationWizard_6;
	private static final String INFORMATION_PAGE_TITLE = Messages.DBMigrationWizard_7;

	
	private GatewayInformationPage docPage;
	private DatabaseSelectionPage selectionPage;
	private DatabaseTableSelectionPage contentPage;
	private DBConverterManager converterManager = new DBConverterManager("resources/conversion"); //$NON-NLS-1$
	private GenerationOptionPage optionPage;
	
	
	
	@Override
	public boolean performFinish() {
		Properties definitionProp = selectionPage.getPageProperties();
		List<String> tableToCreate = contentPage.getTablesToCreate();
		Properties optionProp = optionPage.getPageProperties();
		
		
		/*
		 * create extraction Transfos
		 */
		List<Transformation> selectionTransfo = getSelectionTransfos(definitionProp, tableToCreate);
		DataBaseServer targetServer = (DataBaseServer)ResourceManager.getInstance().getServer(definitionProp.getProperty("targetServer")); //$NON-NLS-1$
		
		
		/*
		 * create Tables
		 */
		
		if (MessageDialog.openQuestion(getShell(), Messages.DBMigrationWizard_9, Messages.DBMigrationWizard_10)){
			try{
				createTables(targetServer, optionProp.getProperty("creationScript")); //$NON-NLS-1$
				MessageDialog.openInformation(getShell(), Messages.DBMigrationWizard_12, Messages.DBMigrationWizard_13);
			}catch(Exception ex){
				ex.printStackTrace();
				if (MessageDialog.openQuestion(getShell(), Messages.DBMigrationWizard_14, Messages.DBMigrationWizard_16 + ex.getMessage() + Messages.DBMigrationWizard_17)){
					return false;
				}
				
				
			}
		}
		
		
		
		
		/*
		 * create Table creation Transfos
		 */
		List<Transformation> insertTransfo = new ArrayList<Transformation>();
		String[] sc =optionProp.getProperty("creationScript").replaceAll("\n", "").split(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for(Transformation t : selectionTransfo){
			
			try{
				if (t.getDescriptor(t) == null){
					t.initDescriptor();
				}
			}catch(Exception ex){
				
			}
			
			
			DataBaseOutputStream out = new DataBaseOutputStream();
			out.setServer(targetServer);
			out.setName("insert " + t.getName().substring(t.getName().toLowerCase().indexOf(" from ") + " from ".length())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			try{
				DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
				for(StreamElement e : t.getDescriptor(t).getStreamElements()){
					StreamElement c = e.clone(out.getName(), t.getName());
					desc.addColumn(c);
					
				}
				
				
				
				if (t instanceof FileXLS){
					String s = sc[selectionTransfo.indexOf(t)];
					s = s.substring(s.indexOf("CREATE TABLE ") + 13 , s.indexOf("(")).trim(); //$NON-NLS-1$ //$NON-NLS-2$
					if (!s.equals(((FileXLS)t).getSheetName())){
						out.setDefinition("select * from " + s, desc); //$NON-NLS-1$
					}
					else{
						out.setDefinition("select * from " + ((FileXLS)t).getSheetName(), desc); //$NON-NLS-1$
					}
					
				}
				else{
					out.setDefinition(((DataStream)t).getDefinition(), desc);
				}
				out.initDescriptor();
				
				
				t.addOutput(out);
				out.addInput(t);
				for(int i = 0; i < t.getDescriptor(t).getColumnCount(); i++){
					
					for(int j = 0; j < out.getDescriptor(out).getColumnCount(); j++){
						
						if (t.getDescriptor(t).getStreamElements().get(i).name.equals(out.getDescriptor(out).getStreamElements().get(j).name)){
							out.createMapping(t, i, j);
							break;
						}
						
					}
					
				}
				insertTransfo.add(out);
			}catch(Exception e){
				e.printStackTrace();
			}

			
		}
		
		
		
		/*
		 * generating ScriptFile
		 */
		File f = new File(optionProp.getProperty("creationFile")); //$NON-NLS-1$
		
		try{
			if (!f.exists()){
				f.createNewFile();
			}
			BufferedWriter bos = new BufferedWriter(new FileWriter(f));
			bos.write(optionProp.getProperty("creationScript")); //$NON-NLS-1$
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DBMigrationWizard_15, e.getMessage());
			return false;
		}
		
		/*
		 * generating ScriptFile
		 */
		f = new File(optionProp.getProperty("truncateFile")); //$NON-NLS-1$
		
		try{
			if (!f.exists()){
				f.createNewFile();
			}
			BufferedWriter bos = new BufferedWriter(new FileWriter(f));
			
			for(String s : contentPage.getTablesToCreate()){
				bos.write("TRUNCATE TABLE " + s + ";\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DBMigrationWizard_19, e.getMessage());
			return false;
		}
		
		
		/*
		 * DocumentGateway creation
		 */
		if (optionProp.getProperty("multipleTransformation") != null){ //$NON-NLS-1$
			
			for(Transformation t : selectionTransfo){
				
				DocumentGateway doc = new DocumentGateway();
				
				Properties docProp = docPage.getProperties();
				doc.setAuthor(docProp.getProperty("author")); //$NON-NLS-1$
				doc.setDescription(docProp.getProperty("description")); //$NON-NLS-1$
				doc.setName(docProp.getProperty("name") + "_" +((DataBaseOutputStream)t.getOutputs().get(0)).getTableName()); //$NON-NLS-1$ //$NON-NLS-2$
				doc.setMode(docProp.getProperty("mode")); //$NON-NLS-1$
				
				doc.addTransformation(t);
				t.setPositionX(350);
				t.setPositionY(150);
				
				t.getOutputs().get(0).setPositionX(650);
				t.getOutputs().get(0).setPositionY(150);
				
				doc.addTransformation(t.getOutputs().get(0));
				
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					String fName = docProp.getProperty("fileName"); //$NON-NLS-1$
					if (fName.contains(".gateway")){ //$NON-NLS-1$
						fName = fName.substring(0, fName.indexOf(".gateway")) +"_" + ((DataBaseOutputStream)t.getOutputs().get(0)).getTableName() + ".gateway"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					else{
						fName = fName +"_" + ((DataBaseOutputStream)t.getOutputs().get(0)).getTableName() + ".gateway"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					page.openEditor(new GatewayEditorInput( fName , doc), GatewayEditorPart.ID, false);
					
				} catch (PartInitException e) {
					
					e.printStackTrace();
				}
				
				
			}
			return true;
			
		}
		else{
			DocumentGateway doc = new DocumentGateway();
			
			Properties docProp = docPage.getProperties();
			doc.setAuthor(docProp.getProperty("author")); //$NON-NLS-1$
			doc.setDescription(docProp.getProperty("description")); //$NON-NLS-1$
			doc.setName(docProp.getProperty("name")); //$NON-NLS-1$
			doc.setMode(docProp.getProperty("mode")); //$NON-NLS-1$
			
			
			
			
			int i = 1;
			

			for(Transformation t : selectionTransfo){
				doc.addTransformation(t);
				t.setPositionX(350);
				t.setPositionY(i * 150);
				i++;
			}
			i = 1;
			for(Transformation t : insertTransfo){
				doc.addTransformation(t);
				t.setPositionX(650);
				t.setPositionY(i * 150);
				i++;
			}
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				page.openEditor(new GatewayEditorInput(docProp.getProperty("fileName"), doc), GatewayEditorPart.ID, false); //$NON-NLS-1$
				return true;
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		
		

		
		return false;
	}
	private String createTables(DataBaseServer targetServer, String scripts) throws Exception{
		Connection con = ((DataBaseConnection)targetServer.getCurrentConnection(null)).getSocket(null);
		boolean error = false;
		Statement stmt = con.createStatement();
		
		StringBuffer buf = new StringBuffer();
		
		for(String s : scripts.split(";")){ //$NON-NLS-1$
			if (s.trim().isEmpty()){
				continue;
			}
			try{
				stmt.execute(s + ";"); //$NON-NLS-1$
			}catch(Exception ex){
				ex.printStackTrace();
				buf.append(Messages.DBMigrationWizard_29 + ex.getMessage()+ "\n"); //$NON-NLS-1$
				error = true;
			}
			
			
		}
		
		if (error){
			throw new Exception(buf.toString());
		}
		return buf.toString();
		
	}
	@Override
	public boolean canFinish() {
		for(IWizardPage p : getPages()){
			if (!p.isPageComplete()){
				return false;
			}
		}
		return true;
	}
	@Override
	public void addPages() {
		docPage = new GatewayInformationPage(INFORMATION_PAGE_NAME);
		docPage.setDescription(INFORMATION_PAGE_DESCRIPTION);
		docPage.setTitle(INFORMATION_PAGE_TITLE);
		addPage(docPage);
		
		selectionPage = new DatabaseSelectionPage(selectionTitle);
		selectionPage.setDescription(selectionDescription);
		selectionPage.setTitle(selectionTitle);
		addPage(selectionPage);
		
		
		contentPage = new DatabaseTableSelectionPage(contentTitle);
		contentPage.setDescription(contentDescription);
		contentPage.setTitle(contentTitle);
		addPage(contentPage);
		
		optionPage = new GenerationOptionPage(optionTitle);
		optionPage.setDescription(optionTitle);
		optionPage.setTitle(optionTitle);
		addPage(optionPage);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectionPage){
			
			Properties p = selectionPage.getPageProperties();
			try {
				contentPage.createInput(p);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DBMigrationWizard_38, e.getMessage());
			}
		}
		else if (page == contentPage){
			optionPage.setScript(createScript());
		}
		return super.getNextPage(page);
	}

	
	
	
	private List<Transformation> getDatabaseSelectionTransfos(Properties p, List<String> tableNames){
		DataBaseServer server = (DataBaseServer)ResourceManager.getInstance().getServer(p.getProperty("sourceServer")); //$NON-NLS-1$
		Connection c = ((DataBaseConnection)server.getCurrentConnection(null)).getSocket(null);
		
		List<Transformation> l = new ArrayList<Transformation>();
		for(String s : tableNames){
			StringBuffer buf = new StringBuffer();
			buf.append("select "); //$NON-NLS-1$
			DataBaseInputStream tr = new DataBaseInputStream();
			tr.setServer(server);
			
			String table = new String(s);
			if (p.getProperty("sourceSchema") != null && !"".equals(p.getProperty("sourceSchema"))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				table = p.getProperty("sourceSchema") + "." + table;  //$NON-NLS-1$ //$NON-NLS-2$
			}
			tr.setName(Messages.DBMigrationWizard_8 + s);
			
			
			
			try{
				DatabaseMetaData dmd = c.getMetaData();
				ResultSet colRs = dmd.getColumns(c.getCatalog(), p.getProperty("sourceSchema"),s, "%"); //$NON-NLS-1$ //$NON-NLS-2$
				boolean first = true;
				
				while(colRs.next()){
					if (first){
						first = false;
					}
					else{
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append(s + "." + colRs.getString("COLUMN_NAME")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				colRs.close();
				
			}catch(Exception e){
				
			}
			buf.append(" from " + table); //$NON-NLS-1$
			tr.setDefinition(buf.toString());
			l.add(tr);
			
		}

		
		
		
		return l;
	}
	
	
	private List<Transformation> getXlsSelectionTransfos(Properties p, List<String> tableNames){
		List<Transformation> l = new ArrayList<Transformation>();
		for(String sheetName : tableNames){
			
			FileInputXLS tr = new FileInputXLS();
			

			tr.setDefinition(p.getProperty("sourceXlsFile")); //$NON-NLS-1$
			tr.setEncoding(p.getProperty("sourceXlsEncoding")); //$NON-NLS-1$
			tr.setSheetName(sheetName);
			tr.setSkipFirstRow(p.getProperty("skipFirstRow")); //$NON-NLS-1$
			tr.setName(Messages.DBMigrationWizard_8 + sheetName);
			tr.initDescriptor();
			l.add(tr);
		}
		
		return l;
	}
	
	private List<Transformation> getSelectionTransfos(Properties p, List<String> tableNames){
		if (p.getProperty("sourceXlsFile") != null){ //$NON-NLS-1$
			return getXlsSelectionTransfos(p, tableNames);
		}
		else{
			return getDatabaseSelectionTransfos(p, tableNames);
		}
		
	}
	
	
	private String createScript(DataStream stream, String from, String targetSchema) throws Exception{
		if (stream.getDescriptor(stream) == null){
			stream.initDescriptor();
		}
		StreamDescriptor desc = stream.getDescriptor(stream);
		
		
		StringBuffer buf = new StringBuffer("CREATE TABLE " ); //$NON-NLS-1$
		
		
		if (targetSchema != null && !targetSchema.trim().equals("")){ //$NON-NLS-1$
			buf.append( targetSchema + "." + stream.getName().substring("Extract from ".length()) + " ("); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else{
			buf.append( stream.getName().substring("Extract from ".length()) + " ("); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		boolean first = true;
		for(StreamElement e : desc.getStreamElements()){
			if (first){
				first = false;
			}
			else{
				buf.append(",  "); //$NON-NLS-1$
			}
			
			String type = e.typeName;
			if (from != null && !"".equals(from.trim())){ //$NON-NLS-1$
				type = converterManager.getTypeSyntax(from, e.typeName);
			}
			else{
				type = e.typeName;
			}
			
			if (converterManager.hasPrecision(from, e.typeName)){
				if (e.precision != null ){
					if (e.typeName.toLowerCase().contains("text")){ //$NON-NLS-1$
						buf.append(e.name +  " " + type); //$NON-NLS-1$
					}
					else{
						if (e.decimal != null && e.decimal.intValue() != 0){
							buf.append(e.name +  " " + type + "(" + e.precision + "," +e.decimal +")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
						else{
							buf.append(e.name +  " " + type + "(" + e.precision + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					
					
				}
				else{
					buf.append(e.name +  " " + converterManager.getTypeSyntax(from, e.typeName)); //$NON-NLS-1$
				}
			}
			
			
			else{
				String a = converterManager.getTypeSyntax(from, e.typeName);
				if (a == null || "".equals(a)){ //$NON-NLS-1$
					a = type;
				}
				
				buf.append(e.name +  " " + a); //$NON-NLS-1$
//				if (e.precision != null && e.precision > 0){
//					buf.append("(" + e.precision + ")");
//				}
			}
		
		}
		
		buf.append(")"); //$NON-NLS-1$
		
		return buf.toString();
	}
	
	
	private String createScript(){
		/*
		 * create Table creation Transfos
		 */
		Properties definitionProp = selectionPage.getPageProperties();
		List<String> tableToCreate = contentPage.getTablesToCreate();
		
		
		
		StringBuffer buf = new StringBuffer();
		for(Transformation t : getSelectionTransfos(definitionProp, tableToCreate)){
			try {
				buf.append(createScript((DataStream)t, definitionProp.getProperty("migrationScript"), definitionProp.getProperty("targetSchema")) + ";\r\n\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return buf.toString();
	}
}
