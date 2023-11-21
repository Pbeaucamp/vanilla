package bpm.workflow.runtime.model.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;

/**
 * Aggregate several Excel files coming from reports in only one 
 * @author Charles MARTIN
 *
 */
public class ExcelAggregateActivity extends AbstractActivity {

	private String path;
	private HashMap<String,String> nameTabs = new HashMap<String, String>();
	private static int number = 0;
	
	public ExcelAggregateActivity(){
		number++;
	}

	public ExcelAggregateActivity(String name) {

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

	}
	
	/**
	 * 
	 * @return the mappings between the ouput names of the reports and the tabs names
	 */
	public HashMap<String, String> getNameTabs() {
		return nameTabs;
	}

	/**
	 * Set the mappings
	 * @param nameTabs
	 */
	public void setNameTabs(HashMap<String, String> nameTabs) {
		this.nameTabs = nameTabs;
	}
	
	/**
	 * Add a new mapping between the output name and the tabname
	 * @param report (outputname)
	 * @param tabName
	 */
	public void addMapping(String report,String tabName){
		nameTabs.put(report, tabName);
	}
	
	
	/**
	 * Remove a mapping
	 * @param report (outputname)
	 */
	public void removeMapping(String report){
		nameTabs.remove(report);
	}

	public IActivity copy() {
		ExcelAggregateActivity a = new ExcelAggregateActivity();
		a.setName("copy of " + name);
		return a;
	}

	/**
	 * 
	 * @return the path of the final aggregated Excel file
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the path of the final aggregated Excel file
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (path == null) {
			buf.append("For activity " + name + ", the output name is not set.\n");
		}
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("excelAggregateActivity");
		if (path != null){
			e.addElement("path").setText(this.path);
			}
		if (!nameTabs.isEmpty()) {
			e.addElement("mapping");
			for (String key : nameTabs.keySet()) {
				
				if(!key.equalsIgnoreCase("") && !nameTabs.get(key).equalsIgnoreCase("")){
					Element map = e.element("mapping").addElement("map"); 
					map.addElement("key").setText(key);
				map.addElement("value").setText(nameTabs.get(key));
				}
			}
		}
		
		
		return e;
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				if(path.contains("{$")){
					path = workflowInstance.parseString(path);
				}
				FileOutputStream out = new FileOutputStream(path);

				WritableWorkbook workbookout = Workbook.createWorkbook(out);
				
				int i = 0;
				for(String reportfile : nameTabs.keySet()){

					String vanPath = workflowInstance.parseString(ListVariable.GENERATED_REPORTS_HOME);
					reportfile = workflowInstance.parseString(reportfile);

					Logger.getLogger(getClass()).info(vanPath +"Reports" + File.separator + reportfile);

					WritableSheet sheet = workbookout.createSheet(nameTabs.get(reportfile), i);

					List<jxl.write.Label> listeCell = new ArrayList<jxl.write.Label>();

					SAXReader saxReader = new SAXReader();
					Document document = saxReader.read(vanPath + File.separator + reportfile);

					Element root = document.getRootElement();

					Element worksheet = root.element("Worksheet");
					List<Element> listeele = worksheet.elements();

					Element table = worksheet.element("Table");

					List<Element> colonnes = table.elements("Column");

					int numCol = 0;
					for(Element colonne : colonnes){
						String colAt = ((Attribute)colonne.attributes().get(0)).getValue();

						numCol = numCol + 1;
					}

					List<Element> rows = table.elements("Row");

					int numRows = 0;
					for(Element row : rows){

						List<Element> cells  = row.elements("Cell");
						for(Element cell : cells){
							String col = cell.attribute("Index").getValue();


							Element data = cell.element("Data");
							String value;
							try {
								value = data.getStringValue();
							} catch (Exception e) {
								value = "";
							}

							jxl.write.Label cellule = new jxl.write.Label(Integer.parseInt(col),numRows,value);
							listeCell.add(cellule);
						}
						numRows = numRows + 1;
					}

					for(jxl.write.Label label : listeCell){
						sheet.addCell(label);
					}
					i++;
				}

				workbookout.write();

				workbookout.close();
				out.close();
				
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
