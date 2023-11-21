package bpm.vanilla.platform.core.wrapper.servlets40.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.Formula;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;

public class FunctionsHelper {


public static List<IDataStreamElement> convertXMLtoColumns (String xmlColumns, BusinessPackage bPackage, String groupName){
	try{
		if(xmlColumns!=null && !xmlColumns.isEmpty())
			{
		
			Document doc = DocumentHelper.parseText(xmlColumns);
			
			List<IDataStreamElement> columns = new ArrayList<IDataStreamElement>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				String name =null;
				String table = null;
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("name")){
						name=child.getText();
					}
					if (child.getName().equals("table")){
						table=child.getText();
					}				
				}
				if (name!=null && table!=null){
					IBusinessTable btable = bPackage.getBusinessTable(groupName, table);
					columns.add(btable.getColumn(groupName, name));
				}				
			} 
			return columns;
		}   
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<IDataStreamElement>();
}



public static List<AggregateFormula> convertXMLtoAggregate (String xmlAggregates, BusinessPackage bPackage, String groupName){
	try{
		if(xmlAggregates!=null && !xmlAggregates.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlAggregates);
			
			List<AggregateFormula> formulas = new ArrayList<AggregateFormula>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				
				String colName =null;
				String table = null;
				String function =null;
				String outputName=null;
				String isCreated=null;
				
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("name")){
						colName=child.getText();
					}
					if (child.getName().equals("table")){
						table=child.getText();
					}
					if (child.getName().equals("value")){
						function=child.getText();
					}
					if (child.getName().equals("field")){
						outputName=child.getText();
					}
					if (child.getName().equals("created")){
						isCreated=child.getText();
					}
				}
				if(!isCreated.equals("True"))
				{
					if (colName!=null && table!=null && function!=null && outputName!=null){
						IBusinessTable btable = bPackage.getBusinessTable(groupName, table);
						IDataStreamElement col=btable.getColumn(groupName, colName);
						formulas.add( new AggregateFormula(function, col, outputName));
					}	
				}else
				{
					formulas.add(new AggregateFormula(function, createFormula(item), outputName));
				}
			}
			return formulas;
		}   
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<AggregateFormula>();
}


public static List<Prompt> convertXMLtoPrompts (String xmlPrompts, BusinessPackage bPackage, String groupName){
	try{
		if(xmlPrompts!=null && !xmlPrompts.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlPrompts);
			
			List<Prompt> prompts = new ArrayList<Prompt>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				
				String promptName =null;
				String table = null;
				String col=null;
				String operator =null;
				String isCreated=null;
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("name")){
						promptName=child.getText();
					}
					if (child.getName().equals("table")){
						table=child.getText();
					}
					if (child.getName().equals("operator")){
						operator=child.getText();
					}
					if (child.getName().equals("field")){
						col=child.getText();
					}
					if (child.getName().equals("created")){
						isCreated=child.getText();
					}
				}
				if(!isCreated.equals("True")){
					if (promptName!=null){
						IResource prompt= bPackage.getResourceByName(promptName);
						if (prompt!=null)
							prompts.add((Prompt)prompt);
					}	
				}else
				{
					Prompt p = new Prompt();
					
					
					p.setName(promptName);

					IBusinessTable btable = bPackage.getBusinessTable(groupName, table);
					p.setGotoDataStreamElement(btable.getColumn(groupName, col));
					p.setOrigin(btable.getColumn(groupName, col));
					p.setOperator(operator);
				
					prompts.add(p);															
				}				
			}
			return prompts;	   
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return  new ArrayList<Prompt>();
}


public static List<IFilter> convertXMLtoFilter (String xmlFilters, BusinessPackage bPackage, String groupName){
	try{
		if(xmlFilters!=null && !xmlFilters.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlFilters);
			
			List<IFilter> filters = new ArrayList<IFilter>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				
				String filterName =null;
				String table = null;
				String col=null;
				String operator =null;
				String value =null;
				String isCreated=null;
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("name")){
						filterName=child.getText();
					}
					if (child.getName().equals("table")){
						table=child.getText();
					}
					if (child.getName().equals("operator")){
						operator=child.getText();
					}
					if (child.getName().equals("field")){
						col=child.getText();
					}
					if (child.getName().equals("value")){
						value=child.getText();
					}
					if (child.getName().equals("created")){
						isCreated=child.getText();
					}
				}
				if(!isCreated.equals("True")){
					if (filterName!=null){
						IResource filter= bPackage.getResourceByName(filterName);
						if (filter!=null)
							filters.add((IFilter)filter);
					}
				}
				else 
				{
					if(operator!=null && !operator.isEmpty()){
						ComplexFilter complex = new ComplexFilter();
						complex.setName(filterName);
						complex.setOperator(operator);
						
						IBusinessTable btable = bPackage.getBusinessTable(groupName, table);
						complex.setOrigin(btable.getColumn(groupName, col));
						
						if (value.contains(",")){
							String[] listValues = value.split(",");
							for(int i=0; i<listValues.length; i++){
								complex.setValue(listValues[i]);
							}
						}else
							complex.setValue(value);
						
						filters.add((IFilter)complex);
					}
					else
					{
						SqlQueryFilter sql = new SqlQueryFilter();
						sql.setName(filterName);
			
						IBusinessTable btable = bPackage.getBusinessTable(groupName, table);
						sql.setOrigin(btable.getColumn(groupName, col));
						sql.setQuery(value);						
						filters.add((IFilter)sql);
					}											
				}
			}
			return filters;	 
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<IFilter>();
}
	

public static List<ListOfValue> convertXMLtoListOfValue(String xmlListOfValues, BusinessPackage bPackage, String groupName){
	try{
		if(xmlListOfValues!=null && !xmlListOfValues.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlListOfValues);
			
			List<ListOfValue> listOfvalues = new ArrayList<ListOfValue>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				
				String filterName =null;
				String operator =null;
				String outputName=null;
				String value= null;
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("name")){
						filterName=child.getText();
					}		
				}
				if (filterName!=null){
					IResource filter= bPackage.getResourceByName(filterName);
					if (filter!=null)
						listOfvalues.add((ListOfValue)filter);
				}	
				//si n'existe pas créer
			}
			return listOfvalues;	
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<ListOfValue>();
}


public static List<Formula> convertXMLtoformula(String xmlFormulas){
	try{
		if(xmlFormulas!=null && !xmlFormulas.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlFormulas);
			
			List<Formula> formulas = new ArrayList<Formula>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				/*
				String value=null;
				String name=null;
				String elements =null;
				List<String> formulaElement = new ArrayList<String>();
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					
					if (child.getName().equals("name")){
						name=child.getText();
					}
					
					if (child.getName().equals("operator")){
						value=child.getText();
					}
					
					if (child.getName().equals("table")){
						elements=child.getText();
						if (elements.contains(",")){
							String[] listValues = elements.split(",");
							for(int i=0; i<listValues.length; i++){
								formulaElement.add(listValues[i]);
							}
						}else
							formulaElement.add(elements);
					}		
				}
				
				formulas.add(new Formula(name, value, formulaElement));
				*/
				formulas.add(createFormula(item));
			}
			return formulas;	   
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<Formula>();
}

private static Formula createFormula(Element item){
	
	String value=null;
	String name=null;
	String elements =null;
	List<String> formulaElement = new ArrayList<String>();
		
	for (Object objChild : item.elements()){
		Element child = (Element)objChild;		
		
		if (child.getName().equals("name")){
			name=child.getText();
		}
		
		if (child.getName().equals("operator")){
			value=child.getText();
		}
		
		if (child.getName().equals("table")){
			elements=child.getText();
			if (elements.contains(",")){
				String[] listValues = elements.split(",");
				for(int i=0; i<listValues.length; i++){
					formulaElement.add(listValues[i]);
				}
			}else
				formulaElement.add(elements);
		}		
	}
	return new Formula(name, value, formulaElement);
}



public static List<List<String>> convertXMLtoPromptValue (String xmlPromptValues){
	try{
		if(xmlPromptValues!=null && !xmlPromptValues.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlPromptValues);
			
			List<List<String>> promptValues = new ArrayList<List<String>>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				String value=null;
				List<String> promptvalue = new ArrayList<String>();
				
			//	String operator =null;
			//	String outputName=null;
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;		
					if (child.getName().equals("value")){
						value=child.getText();
						if (value.contains(",")){
							String[] listValues = value.split(",");
							for(int i=0; i<listValues.length; i++){
								promptvalue.add(listValues[i]);
							}
						}else
							promptvalue.add(value);
					}		
				}
				promptValues.add(promptvalue);
				
				//si n'existe pas créer
			}
			return promptValues;	   
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return new ArrayList<List<String>>();
}


public static HashMap<Prompt, List<String>> convertPromptValue (String xmlPromptValues, List<Prompt> prompts, BusinessPackage bPackage){
	try{
		if(xmlPromptValues!=null && !xmlPromptValues.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlPromptValues);
			
			HashMap<Prompt, List<String>> promptValues = new HashMap<Prompt, List<String>>();
			
		//	List<List<String>> promptValues = new ArrayList<List<String>>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				String value=null;
				String promptName =null;
				List<String> promptvalue = new ArrayList<String>();
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;	
					
					if (child.getName().equals("name")){
							promptName=child.getText();
					}		
					if (child.getName().equals("value")){
							value=child.getText();
					}
														
				}	
				if (promptName!=null && value!=null){
					
					Prompt promptKey= (Prompt) bPackage.getResourceByName(promptName);
					if (promptKey==null){
						for(Prompt prompt : prompts){
							if(prompt.getName().equals(promptName)){
								promptKey=prompt;
								break;
							}
						}
					}											
					if (value.contains(",")){
						String[] listValues = value.split(",");
						for(int i=0; i<listValues.length; i++){
							promptvalue.add(listValues[i]);
						}
					}else
						promptvalue.add(value);
					
					promptValues.put(promptKey, promptvalue);
					
					}				
				}
			
			return promptValues;	  
			}	
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return  new HashMap<Prompt, List<String>>();
}


public static List<String> convertListOrdonnable (String xmlOrdonable, BusinessPackage bPackage){
	try{
		if(xmlOrdonable!=null && !xmlOrdonable.isEmpty())
		{
			Document doc = DocumentHelper.parseText(xmlOrdonable);
			
			List <String> listOrdonable = new ArrayList<String>();
			
		//	List<List<String>> promptValues = new ArrayList<List<String>>();
			
			Element racine = doc.getRootElement();
			for (Object obj : racine.elements()){
				Element item = (Element)obj;
				String name=null;
					
				for (Object objChild : item.elements()){
					Element child = (Element)objChild;	
					
					if (child.getName().equals("name")){
							name=child.getText();
					}					
				}	
				listOrdonable.add(name);
			}
			return listOrdonable;	  
		}	
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return  new ArrayList<String>();
}


public static String convertResponseToXml (List<List<String>> response){
	
	Document doc = DocumentHelper.createDocument();
	Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_EXECUTEQUERY);
	
	//Element items = root.addElement("items");
	for (List<String> row: response){
		Element rowItem = root.addElement("row");
		for(String cell : row){
			if (cell!=null)
				rowItem.addElement("cell").setText(cell);
			else
				rowItem.addElement("cell").setText("");
		}		
	}
	
	return doc.asXML();
	
}


	
}
