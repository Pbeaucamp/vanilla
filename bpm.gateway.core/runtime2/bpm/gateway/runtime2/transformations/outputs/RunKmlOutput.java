package bpm.gateway.runtime2.transformations.outputs;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.core.transformations.kml.KmlObjectType;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.tools.StringParser;

public class RunKmlOutput extends RuntimeStep {

	private Integer indexAltitude;
	private Integer indexLongitude;
	private Integer indexLatitude;
	private Integer indexName;
	private Integer indexDescription;

	private Document document;
	private Element root;
	private Element holder;

	private List elements;
	private Integer id = null;

	private XMLWriter xwriter;
	private KmlObjectType objectType;

	private HashMap<String, Object[]> coordinatesMap = new HashMap<String, Object[]>();

	private class F extends OutputFormat {
		private OutputFormat iner = OutputFormat.createPrettyPrint();

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#getAttributeQuoteCharacter()
		 */
		@Override
		public char getAttributeQuoteCharacter() {
			return iner.getAttributeQuoteCharacter();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#getEncoding()
		 */
		@Override
		public String getEncoding() {
			return iner.getEncoding();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#getIndent()
		 */
		@Override
		public String getIndent() {

			return iner.getIndent();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#getLineSeparator()
		 */
		@Override
		public String getLineSeparator() {

			return iner.getLineSeparator();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#getNewLineAfterNTags()
		 */
		@Override
		public int getNewLineAfterNTags() {

			return iner.getNewLineAfterNTags();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isExpandEmptyElements()
		 */
		@Override
		public boolean isExpandEmptyElements() {

			return iner.isExpandEmptyElements();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isNewLineAfterDeclaration()
		 */
		@Override
		public boolean isNewLineAfterDeclaration() {

			return iner.isNewLineAfterDeclaration();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isNewlines()
		 */
		@Override
		public boolean isNewlines() {

			return iner.isNewlines();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isOmitEncoding()
		 */
		@Override
		public boolean isOmitEncoding() {

			return iner.isOmitEncoding();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isPadText()
		 */
		@Override
		public boolean isPadText() {

			return iner.isPadText();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isSuppressDeclaration()
		 */
		@Override
		public boolean isSuppressDeclaration() {

			return iner.isSuppressDeclaration();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isTrimText()
		 */
		@Override
		public boolean isTrimText() {

			return iner.isTrimText();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#isXHTML()
		 */
		@Override
		public boolean isXHTML() {

			return iner.isXHTML();
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#parseOptions(java.lang.String[], int)
		 */
		@Override
		public int parseOptions(String[] arg0, int arg1) {

			return iner.parseOptions(arg0, arg1);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setAttributeQuoteCharacter(char)
		 */
		@Override
		public void setAttributeQuoteCharacter(char quoteChar) {

			iner.setAttributeQuoteCharacter(quoteChar);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setEncoding(java.lang.String)
		 */
		@Override
		public void setEncoding(String encoding) {

			iner.setEncoding(encoding);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setExpandEmptyElements(boolean)
		 */
		@Override
		public void setExpandEmptyElements(boolean expandEmptyElements) {

			iner.setExpandEmptyElements(expandEmptyElements);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setIndent(boolean)
		 */
		@Override
		public void setIndent(boolean doIndent) {

			iner.setIndent(doIndent);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setIndent(java.lang.String)
		 */
		@Override
		public void setIndent(String indent) {

			iner.setIndent(indent);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setIndentSize(int)
		 */
		@Override
		public void setIndentSize(int arg0) {

			iner.setIndentSize(arg0);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setLineSeparator(java.lang.String)
		 */
		@Override
		public void setLineSeparator(String separator) {

			iner.setLineSeparator(separator);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setNewLineAfterDeclaration(boolean)
		 */
		@Override
		public void setNewLineAfterDeclaration(boolean newLineAfterDeclaration) {

			iner.setNewLineAfterDeclaration(newLineAfterDeclaration);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setNewLineAfterNTags(int)
		 */
		@Override
		public void setNewLineAfterNTags(int tagCount) {

			iner.setNewLineAfterNTags(tagCount);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setNewlines(boolean)
		 */
		@Override
		public void setNewlines(boolean newlines) {

			iner.setNewlines(newlines);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setOmitEncoding(boolean)
		 */
		@Override
		public void setOmitEncoding(boolean omitEncoding) {

			iner.setOmitEncoding(omitEncoding);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setPadText(boolean)
		 */
		@Override
		public void setPadText(boolean padText) {

			iner.setPadText(padText);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setSuppressDeclaration(boolean)
		 */
		@Override
		public void setSuppressDeclaration(boolean suppressDeclaration) {

			iner.setSuppressDeclaration(suppressDeclaration);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setTrimText(boolean)
		 */
		@Override
		public void setTrimText(boolean trimText) {

			iner.setTrimText(trimText);
		}

		/*
		 * (non-Javadoc)
		 * @see org.dom4j.io.OutputFormat#setXHTML(boolean)
		 */
		@Override
		public void setXHTML(boolean xhtml) {

			iner.setXHTML(xhtml);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#clone()
		 */
		@Override
		protected Object clone() throws CloneNotSupportedException {

			return super.clone();
		}

	}

	public RunKmlOutput(KMLOutput transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		KMLOutput fXml = (KMLOutput) getTransformation();
		String fName = null;
		try {
			fName = getTransformation().getDocument().getStringParser().getValue(fXml.getDocument(), fXml.getDefinition());
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Wrong file name.");

		}
		try {
			xwriter = new XMLWriter(new FileOutputStream(fName), new F());
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error when creating file writer " + fName, ex);
		}
		document = DocumentHelper.createDocument();
		root = DocumentHelper.createElement("kml");
		root.addAttribute("xmlns", "http://www.opengis.net/kml/2.2");
		document.add(root);

		// TODO get RootPath forkml/Document
		holder = DocumentHelper.createElement("Document");
		root.add(holder);

		objectType = fXml.getKmlObjectType();

		HashMap map = new HashMap();
		map.put("kml", "http://www.opengis.net/kml/2.2");

		indexAltitude = fXml.getCoordinateAltitudeIndex();
		indexLongitude = fXml.getCoordinateLongitudeIndex();
		indexLatitude = fXml.getCoordinateLatitudeIndex();
		indexName = fXml.getNameIndex();
		indexDescription = fXml.getDescriptionIndex();

		if(objectType == null) {
			Exception ex = new Exception("No Object Type selected in the definition");
			error(ex.getMessage());
			throw ex;
		}

		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if(areInputStepAllProcessed()) {
			if(inputEmpty()) {
				setEnd();
			}
		}

		if(isEnd() && inputEmpty()) {
			return;
		}

		if(!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}

		Row row = readRow();

		if(row == null) {
			return;
		}

		String key = row.get(indexName).toString();

		StringBuffer buf = new StringBuffer();
		if(indexAltitude == null) {
			buf.append(row.get(indexLongitude).toString());
			buf.append(",");
			buf.append(row.get(indexLatitude).toString());

		}
		else {
			buf.append(row.get(indexLongitude).toString());
			buf.append(",");
			buf.append(row.get(indexLatitude).toString());
			buf.append(",");
			buf.append(row.get(indexAltitude).toString());

		}

		for(String k : coordinatesMap.keySet()) {
			if(k.equals(key)) {
				key = k;
				break;
			}
		}
		switch(objectType) {
			case Point:
				insertPoint(key, row.get(indexDescription).toString(), buf.toString(), coordinatesMap.get(key) == null);
				break;
			case Line:
				insertLine(key, row.get(indexDescription).toString(), buf.toString(), coordinatesMap.get(key) == null);
				break;
			case Polygone:
				insertPolygone(key, row.get(indexDescription).toString(), buf.toString(), coordinatesMap.get(key) == null);
				break;
		}
	}

	private void insertPoint(String key, String description, String coordinates, boolean create) {
		Element placemark = null;
		if(create) {
			placemark = DocumentHelper.createElement("Placemark");
			placemark.addElement("name").setText(key);
			if(description != null) {
				placemark.addElement("description").setText(description);
			}
			coordinatesMap.put(key, new Object[] { placemark });
			holder.add(placemark);
		}
		else {
			placemark = (Element) coordinatesMap.get(key)[0];
		}
		writedRows++;
		Element e = DocumentHelper.createElement("Point");
		e.addElement("coordinates").setText(coordinates);
		placemark.add(e);

	}

	private void insertLine(String key, String description, String coordinates, boolean create) {
		Element placemark = null;
		if(create) {
			placemark = DocumentHelper.createElement("Placemark");
			placemark.addElement("name").setText(key);
			if(description != null) {
				placemark.addElement("description").setText(description);
			}

			holder.add(placemark);
			Element e = DocumentHelper.createElement("LineString");
			Element coord = e.addElement("coordinates");
			coord.setText("\r\n" + coordinates);
			placemark.add(e);
			coordinatesMap.put(key, new Object[] { placemark, coord });
			writedRows++;
		}
		else {
			Element coord = (Element) coordinatesMap.get(key)[1];
			coord.setText(coord.getText() + "\r\n" + coordinates);
		}

	}

	private void insertPolygone(String key, String description, String coordinates, boolean create) {
		Element placemark = null;
		if(create) {
			placemark = DocumentHelper.createElement("Placemark");
			placemark.addElement("name").setText(key);
			if(description != null) {
				placemark.addElement("description").setText(description);
			}

			holder.add(placemark);
			Element poly = DocumentHelper.createElement("Polygon");
			placemark.add(poly);
			Element outerBondary = DocumentHelper.createElement("outerBoundaryIs");
			poly.add(outerBondary);

			Element e = DocumentHelper.createElement("LinearRing");
			outerBondary.add(e);

			Element coord = e.addElement("coordinates");
			coord.setText("\n" + coordinates);

			coordinatesMap.put(key, new Object[] { placemark, coord });
			writedRows++;
		}
		else {
			Element coord = (Element) coordinatesMap.get(key)[1];
			coord.setText(coord.getText() + "\n" + coordinates);
		}

	}

	@Override
	public void releaseResources() {

		try {
			xwriter.write(document);
			xwriter.close();
		} catch(Exception ex) {
			ex.printStackTrace();
			error("Problem when writing xml :" + ex.getMessage(), ex);
		}

		document = null;

		info(" resources released");

	}

}
