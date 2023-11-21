package bpm.fa.api.utils.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXParseException;

import bpm.fa.api.olap.OLAPChart;
import bpm.fa.api.repository.RepositoryCubeView;

/**
 * To digest the xml definition of an ICubeView and return 
 * an RepositoryCubeView
 * @author ereynard
 */
public class DigesterCubeView {

	private RepositoryCubeView view;

	/**
	 * Digest the repository.xml given 
	 * @param the xml comming from the Repository
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings(value = "unchecked")
	public DigesterCubeView(String xml) throws Exception {
		InputStream is = IOUtils.toInputStream(xml, "UTF-8");

		Digester dig = new Digester();
		dig.setValidating(false);
		//dig.setLogger(null);
		dig.setErrorHandler(new ErrorHandler());
		dig.setClassLoader(DigesterCubeView.class.getClassLoader());
		String root = "";
		if (xml.contains("<fav>")){
			root = "fav/view";	
		}
		else{
			root = "view";
		}
		
		

		dig.addObjectCreate(root, RepositoryCubeView.class);
			dig.addCallMethod(root + "/col", "addCol", 0);
			dig.addCallMethod(root + "/row", "addRow", 0);
			dig.addCallMethod(root + "/where", "addWhere", 0);
			dig.addCallMethod(root + "/topx", "addTopx", 3);
				dig.addCallParam(root + "/topx/element", 0);
				dig.addCallParam(root + "/topx/target", 1);
				dig.addCallParam(root + "/topx/count", 2);
			dig.addCallMethod(root + "/personalname", "addPersonalName", 2);
				dig.addCallParam(root + "/personalname/uname", 0);
				dig.addCallParam(root + "/personalname/personal", 1);
			dig.addCallMethod(root + "/percent", "addPercentMeasure", 2);
				dig.addCallParam(root + "/percent/measure", 0);
				dig.addCallParam(root + "/percent/showmeasure", 1);
			dig.addCallMethod(root + "/reporttitle", "setReportTitle", 0);
			dig.addCallMethod(root + "/showtotals", "isShowTotals", 0);
			dig.addCallMethod(root + "/parameters/parameter", "addParameter", 3);
				dig.addCallParam(root + "/parameters/parameter/name", 0);
				dig.addCallParam(root + "/parameters/parameter/value", 1);
				dig.addCallParam(root + "/parameters/parameter/level", 2);
			dig.addCallMethod(root + "/rowcount", "setRowCount", 0);
			dig.addCallMethod(root + "/colcount", "setColCount", 0);
			dig.addCallMethod(root + "/showempty", "setShowEmpty", 0);
			dig.addCallMethod(root + "/showprops", "setShowProps", 0);
			dig.addCallMethod(root + "/sortelement", "addSortElement", 2);
				dig.addCallParam(root + "/sortelement/uname", 0);
				dig.addCallParam(root + "/sortelement/type", 1);
			dig.addCallMethod(root + "/base64img", "setBase64img", 0);
			OLAPChart.digest(dig, root + "/chart");
			dig.addSetNext(root + "/chart", "setChart");

		try {
			
			view = (RepositoryCubeView) dig.parse(is);
			if (is != null){
				is.close();
			}
			//map = (HashMap<String, OLAPStructure>) dig.parse(f);
		} catch (Exception e) {
			if (is != null){
				try {
					is.close();
					e.printStackTrace();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
			
		}
	}

	public RepositoryCubeView getCubeView() {
		return view;
	}
	
	
	public class ErrorHandler implements org.xml.sax.ErrorHandler {
		public void warning(SAXParseException ex) throws SAXParseException {
			throw ex;
		}
		
		public void error(SAXParseException ex) throws SAXParseException {
			throw ex;
		}
		
		public void fatalError(SAXParseException ex) throws SAXParseException {
			throw ex;
		}
	}
}
