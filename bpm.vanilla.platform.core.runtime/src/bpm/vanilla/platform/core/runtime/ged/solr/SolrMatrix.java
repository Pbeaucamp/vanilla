package bpm.vanilla.platform.core.runtime.ged.solr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

public class SolrMatrix {

	private int nbRows = 0;
	private List<TreeMap<String,Double>> matrice = new ArrayList<TreeMap<String,Double>>();
	private List<String> IdList = new ArrayList<String>();
	private double[][] body;
	private String[] words;
	private String[] docIds;
	
	
	public SolrMatrix(HashMap<String,List<SolrKeyword>> preMatrice) {
		super();
		convert(preMatrice);
		
		this.nbRows = this.matrice.size();
		
		makeBody();
		makeWords();
		makeDocIds();
	}


	public int getNbRows() {
		return nbRows;
	}
	public void setNbRows(int nbRows) {
		this.nbRows = nbRows;
	}
	
	public List<TreeMap<String,Double>> getMatrice() {
		return matrice;
	}
	public void setMatrice(List<TreeMap<String,Double>> matrice) {
		this.matrice = matrice;
	}
	
	public double[][] getBody() {
		return body;
	}


	public void setBody(double[][] body) {
		this.body = body;
	}


	public String[] getWords() {
		return words;
	}


	public void setWords(String[] words) {
		this.words = words;
	}


	public String[] getDocIds() {
		return docIds;
	}


	public void setDocIds(String[] docIds) {
		this.docIds = docIds;
	}


	private void convert(HashMap<String,List<SolrKeyword>> preMatrice) {
		for(Entry<String, List<SolrKeyword>> row : preMatrice.entrySet()) { 
			addRow(row.getValue());
			nbRows = matrice.size();
			
			IdList.add(row.getKey());
		}
		
	}
	
	public void addRow(List<SolrKeyword> tokens){
		
		TreeMap<String, Double> map = new TreeMap<String, Double>();  //
		
		for(SolrKeyword token : tokens){  //on rentre la liste de token dans une map
			map.put(token.getName(), token.getProban());
		}
		
		
		if (nbRows < 1){
			//premier ajout de doc, donc on insere la map <K,V>
			matrice.add(map);
			
		}
		else {  //ce n'est pas le premier document
			
			for(Entry<String, Double> entry : matrice.get(0).entrySet()) {  //on ajoute a la nouvelle map, tous les mots de la matrice qu'elle ne
				if(!map.containsKey(entry.getKey())){					//contient pas avec une valeur nulle
					map.put(entry.getKey(), 0.0);
				}
			}
			
			//ensuite on parcours la map. Si le mot est deja dans la matrice on ajuste les poids sinon on l'ajoute vide
			
			for(Entry<String, Double> entry : map.entrySet()) {
			   String cle = entry.getKey();
			   if (!matrice.get(0).containsKey(cle)){
				   for (Map<String, Double> doc : matrice){
					   doc.put(cle, 0.0);
				   }
			   }
			}
			
			matrice.add(map);
		}
		
		
		
	}
	
	public void deleteRow(){
		//voir si nécessaire
	}
	
	public void exportXML(){
		XStream xStream = new XStream();
		xStream.alias("map", java.util.Map.class);
		String xml = xStream.toXML(matrice);
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( new InputSource( new StringReader( xml ) ) ); 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:/Users/MacBook/Documents/Matrice.xml"));
 
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		System.out.println("Saved");
	}
	
	
	private void makeDocIds() {
		docIds = new String[nbRows];
		for(int i=0; i<nbRows; i++) { 
			docIds[i] = IdList.get(i);
		}
	
	}


	private void makeWords() {

		int nbCols = matrice.get(0).size();
		words = new String[nbCols];
		int i=0;
		for(Entry<String, Double> name : matrice.get(0).entrySet()) { 
			words[i] = name.getKey();
			i++;
		}
	}


	private void makeBody() {
		int nbLignes = nbRows;
		int nbCols = matrice.get(0).size();
		body = new double[nbLignes][nbCols];
		int i=0, j=0;
		//corps tab
		for(i=0; i<nbLignes; i++) { 
			j=0;
			for(Entry<String, Double> name : matrice.get(i).entrySet()) { 
				body[i][j] = name.getValue();
				j++;
			}
		}	
		
	}

}
