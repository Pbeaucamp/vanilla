package bpm.workflow.runtime.model.activities.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;


public class ConcatExcelActivity extends ConcatPDFActivity {
	
	public ConcatExcelActivity() {
		super();
	}
	
	public ConcatExcelActivity(String string) {
		super(string);
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("concatExcelActivity");
	
		return e;
	}
	
	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				pathToStore = workflowInstance.parseString(pathToStore);
				
				String temporarypathtostore = pathToStore + new Object().hashCode() + File.separator + outputName;
				String pathout = pathToStore + outputName;
				
				try {
					File pathoutFile = new File(pathToStore);
					pathoutFile.mkdir();
					
					File fileOut = new File(pathout);
					if (fileOut.exists()) {
							fileOut.delete();			
					}
					new File(temporarypathtostore).getParentFile().mkdir();
				}
				catch (Exception e) {
				}

				if(!filesToConcat.isEmpty()){
					InputStream old = null;
					Workbook workbookin = null;

					try{
						old = new FileInputStream(pathout);
						workbookin = Workbook.getWorkbook(old);
						old.close();
						File f = new File(pathout);
						f.renameTo(new File(temporarypathtostore));
					}
					catch(Exception e){
						old = null;
					}

					List<List<String[]>> cells = new ArrayList<List<String[]>>();
					List<String> nameTabs = new ArrayList<String>();

					int nombretab = 0;

					if(old != null){
						try {
							int nbTabs = workbookin.getNumberOfSheets();
							nombretab = nbTabs;

							for(int itab = 0;itab<nbTabs;itab++){
								Sheet in = workbookin.getSheet(itab);
								List<String[]> tab = new ArrayList<String[]>();
								nameTabs.add(in.getName());

								int nberRows = in.getRows();
								int nberCol = in.getColumns();

								for(int i=0;i<nberRows;i++){
									for(int j =0;j<nberCol;j++){
										String value = in.getCell(j,i).getContents();

										String[] cellule = new String[3];
										cellule[0] = String.valueOf(j);
										cellule[1] = String.valueOf(i);
										cellule[2] = value;

										tab.add(cellule);

									}
								}
								cells.add(tab);
							}
							old.close();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}

					File f = new File(pathout);
					f.renameTo(new File(temporarypathtostore));

					FileOutputStream out = new FileOutputStream(temporarypathtostore);
					WritableWorkbook workbookout = Workbook.createWorkbook(out);
					if(cells.size() != 0){
						for(int itab = 0;itab<cells.size();itab++){
							WritableSheet sheetOut = workbookout.createSheet(nameTabs.get(itab), itab);
							for(String[] valeur : cells.get(itab)){

								String col = valeur[0];
								String ligne = valeur[1];
								String valuu = valeur[2];
								jxl.write.Label label = new jxl.write.Label(Integer.parseInt(col),Integer.parseInt(ligne),valuu);
								sheetOut.addCell(label);
							}
						}
					}


					for (String file : filesToConcat) {
						file = workflowInstance.parseString(file);
						String namofTheSheet = file.substring(file.lastIndexOf("/")+1, file.lastIndexOf("."));
						nombretab++;
						WritableSheet sheetOut = workbookout.createSheet(namofTheSheet, nombretab);

						FileInputStream input = new FileInputStream(file);
						Workbook myworkbook = Workbook.getWorkbook(input);
						Sheet mysheetIn = myworkbook.getSheet(0);

						int nberRows = mysheetIn.getRows();
						int nberCol = mysheetIn.getColumns();

						for(int i=0;i<nberRows;i++){
							for(int j =0;j<nberCol;j++){
								String value = mysheetIn.getCell(j,i).getContents();
								jxl.write.Label label = new jxl.write.Label(j,i,value);
								sheetOut.addCell(label);
							}
						}
						input.close();
					}

					workbookout.write();
					workbookout.close();
					out.close();

					File _f = new File(temporarypathtostore);
					_f.renameTo(new File(pathout));
					
					new File(temporarypathtostore).getParentFile().delete();
				}
				
				
				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
	}

}
