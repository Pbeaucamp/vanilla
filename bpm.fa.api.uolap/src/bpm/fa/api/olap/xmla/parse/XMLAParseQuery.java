package bpm.fa.api.olap.xmla.parse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.xmla.XMLAException;
import bpm.fa.api.olap.xmla.XMLAMember;

public class XMLAParseQuery {
	
	private XMLAResult res = null;
	private String xml = "";
	
	public XMLAParseQuery(InputStream is, String type) throws XMLAException {
//		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		Digester dig = new Digester();
		dig.setValidating(false);
	
		String root = "";
		
		
		if (type.equalsIgnoreCase(XMLAConnection.MicrosoftProvider))
			root = "soap:Envelope/soap:Body/ExecuteResponse/return/root";
		else if (type.equalsIgnoreCase(XMLAConnection.QuartetFsProvider))
			root = "SOAP-ENV:Envelope/SOAP-ENV:Body/xmla:ExecuteResponse/xmla:return/root";
		else if (type.equalsIgnoreCase(XMLAConnection.MondrianProvider))
			root = "SOAP-ENV:Envelope/SOAP-ENV:Body/cxmla:ExecuteResponse/cxmla:return/root";
		else if (type.equalsIgnoreCase(XMLAConnection.HyperionProvider)){
			root = "SOAP-ENV:Envelope/SOAP-ENV:Body/m:ExecuteResponse/m:return/root";
		}
		else if (type.equalsIgnoreCase(XMLAConnection.IcCube)){
//			root = "soap:Envelope/soap:Body/XMLA:ExecuteResponse/XMLA:return/root";
			root = "soap:Envelope/soap:Body/XMLA:ExecuteResponse/XMLA:return/root";
		}
		else {
			throw new XMLAException("Error, provider not recognised", null);
		}
		
		
		dig.addObjectCreate(root, XMLAResult.class);
		
		dig.addObjectCreate(root + "/Axes/Axis", XMLAAxis.class);
			dig.addSetProperties(root + "/Axes/Axis", "name", "name");
		
			dig.addObjectCreate(root + "/Axes/Axis/Tuples/Tuple", XMLATuple.class);
		
				dig.addObjectCreate(root + "/Axes/Axis/Tuples/Tuple/Member", XMLAMember.class);
				if (type.equalsIgnoreCase(XMLAConnection.HyperionProvider)){
					dig.addSetProperties(root + "/Axes/Axis/Tuples/Tuple/Member", "Hierarchy", "hieraName");
				}
				
				//dig.addSetProperties(root + "/Axes/Axis/Tuples/Tuple/Member", "Hierarchy", "hiera");
				dig.addCallMethod(root + "/Axes/Axis/Tuples/Tuple/Member/UName", "setUname", 0);
				dig.addCallMethod(root + "/Axes/Axis/Tuples/Tuple/Member/Caption", "setName", 0);
				dig.addCallMethod(root + "/Axes/Axis/Tuples/Tuple/Member/Caption", "setCaption", 0);
				//dig.addCallMethod(root + "/Axes/Axis/Tuples/Tuple/Member/LName", "setLevelName", 0);
				dig.addCallMethod(root + "/Axes/Axis/Tuples/Tuple/Member/LNum", "setLevelDepth", 0);
				dig.addSetNext(root + "/Axes/Axis/Tuples/Tuple/Member", "addMember");
		
			dig.addSetNext(root + "/Axes/Axis/Tuples/Tuple", "addTuple");
		
		dig.addSetNext(root + "/Axes/Axis", "addAxis");
		
		dig.addObjectCreate(root + "/CellData/Cell", XMLACell.class);
			dig.addSetProperties(root + "/CellData/Cell", "CellOrdinal", "ordinal");
			dig.addCallMethod(root + "/CellData/Cell/Value", "setValue", 0);
			dig.addCallMethod(root + "/CellData/Cell/FmtValue", "setFvalue", 0);
		dig.addSetNext(root + "/CellData/Cell", "addCell");
		String buf = "";
		try {
			buf = IOUtils.toString(is, "UTF-8");;
			res = (XMLAResult) dig.parse(IOUtils.toInputStream(buf, "UTF-8"));
			is.close();

			HashMap<String, List<OLAPMember>> membersAll = new HashMap<String, List<OLAPMember>>();
			
			for(XMLAAxis ax : res.getAxes()){
				for(XMLATuple tu : ax.getTuples()){
					
					
					for(OLAPMember m : tu.getMembers()){
						if (membersAll.get(((XMLAMember)m).getHieraName()) == null){
							membersAll.put(((XMLAMember)m).getHieraName(), new ArrayList<OLAPMember>()); 
						}
						
						membersAll.get(((XMLAMember)m).getHieraName()).add(m);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			try{
				XMLWriter w = new XMLWriter(System.err, OutputFormat.createPrettyPrint());
				Document doc = DocumentHelper.parseText(buf);
				Element q = doc.getRootElement().element(root);
				w.write(doc);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			
			
			throw new XMLAException("Error while parsing XMLA response", e);
		} 
	}
	
	
	public XMLAResult getResult() {
		
		return res;
	}
}

