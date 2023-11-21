package bpm.gwt.aklabox.commons.server.methods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

public class PDFTextExtractor extends PDFTextStripper {

	private String[] queryWords;
	private List<OCRSearchResult> results = new ArrayList<OCRSearchResult>();
	private int pageNo = 1;

	private PDPage actualPage;
	
	private OCRSearchResult currentWord;
	List<TextPosition> positions;
	
	public PDFTextExtractor() throws IOException {
		super.setSortByPosition(true);
	}

	public List<OCRSearchResult> searchLocation(String location, String query) throws Exception {
		query = query.replace(" ", ",");
		PDDocument document = null;
		queryWords = query.split(",");

		File input = new File(location);
		document = PDDocument.load(input,"");
//		if (document.isEncrypted()) {
//			try {
//				document.decrypt("");
//			} catch (InvalidPasswordException e) {
//				e.printStackTrace();
//				throw e;
//			}
//		}
		PDPageTree allPages = document.getDocumentCatalog().getPages();

		for (int i = 0; i < allPages.getCount(); i++) {
			PDPage page = (PDPage) allPages.get(i);
			actualPage = page;
			//Iterator<PDStream> contents = page.getContentStreams();

			//if (contents != null) {
			 this.setStartPage(i+1);
		        this.setEndPage(i+1);
		        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
		        writeText(document,dummy); // This call starts the parsing process and calls writeString repeatedly.
		 
				//this.processPage(page);
			//}
			pageNo += 1;
		}

//		System.out.println(wordList);
//
//		for (String s : wordList) {
//			System.out.println(s);
//			int pageNumber = Integer.parseInt(s.substring(1, s.indexOf(")"))) + 1;
//			String points = s.substring(s.indexOf("[") + 1, s.indexOf("]")).replace(" ", "");
//			double x = Double.parseDouble(points.substring(0, points.indexOf(":")));
//			double y = Double.parseDouble(points.substring(points.indexOf(":") + 1, points.length()));
//			float fontSize = Float.parseFloat(s.substring(s.indexOf("{") + 1, s.indexOf("}")));
//			results.add(new OCRSearchResult(query, pageNumber, x, y, fontSize, width, height));
//		}

		document.close();

//		dumpResult();
		
		return results;
	}

	@Override
    protected void writeString(String string,List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.println("String[" + text.getXDirAdj()+","+text.getYDirAdj()+" fs="+text.getFontSizeInPt()+" xscale="+text.getXScale()+" height="+text.getHeightDir()+" space="+text.getWidthOfSpace()+" width="+text.getWidthDirAdj()+" ] "+text.getUnicode());
        }
    }

	@Override
	protected void processTextPosition(TextPosition text) {

		String tChar = text.getUnicode();
//		System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space=" + text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + "]" + text.getCharacter());
		String REGEX = "[\\s,.\\[\\](:;!?)/]";
		
//		dumpInfo(text);
		
		
		
		if(tChar.matches(REGEX)) {
			//do the things
			//create the wordObject
			if(currentWord != null) {		
				for(String word : queryWords) {
					if(currentWord.getQuery().equalsIgnoreCase(word)) {
						currentWord.setxEnd((int)text.getXDirAdj());
						currentWord.setyEnd((int)text.getYDirAdj()+2);
						
						currentWord.setPageHeight((int)actualPage.getArtBox().getHeight());
						currentWord.setPageWidth((int)actualPage.getArtBox().getWidth());
						
						results.add(currentWord);
						
//						dumpEverything();
						
						break;
					}
				}
			}
			
			positions = null;
			currentWord=null;
		}
		else {
			if(currentWord == null) {
				currentWord = new OCRSearchResult();
				currentWord.setFontSize((int)text.getFontSize());
				currentWord.setX((int)text.getXDirAdj());
				currentWord.setY((int)text.getYDirAdj() - (int)text.getYScale()+2);
				currentWord.setPageNumber(pageNo);
				
				positions = new ArrayList<TextPosition>();
				
				
			}
			currentWord.setQuery(currentWord.getQuery()+tChar);
			
			positions.add(text);
			
		}
		
		
//		lineMatch = matchCharLine(text);
//		if ((!tChar.matches(REGEX)) && (!Character.isWhitespace(c))) {
//			if ((!is1stChar) && (lineMatch == true)) {
//				appendChar(tChar);
//			}
//			else if (is1stChar == true) {
//				setWordCoord(text, tChar, String.valueOf(text.getFontSize()));
//			}
//		}
//		else {
//			endWord();
//		}
	}

	private void dumpEverything() {
		for(TextPosition pos : positions) {
			dumpInfo(pos);
		}
		dumpResult(currentWord);
	}

	private void dumpInfo(TextPosition text) {

		StringBuilder buf = new StringBuilder();
		
		buf.append("char = " + text.getUnicode() + "\n");
		buf.append("getDir = " + text.getDir() + "\n");
		buf.append("getFontSize = " + text.getFontSize() + "\n");
		buf.append("getFontSizeInPt = " + text.getFontSizeInPt() + "\n");
		buf.append("getHeight = " + text.getHeight() + "\n");
		buf.append("getHeightDir = " + text.getHeightDir() + "\n");
		buf.append("getWidth = " + text.getWidth() + "\n");
		buf.append("getWidthDirAdj = " + text.getWidthDirAdj() + "\n");
		buf.append("getWidthOfSpace = " + text.getWidthOfSpace() + "\n");
		buf.append("getX = " + text.getX() + "\n");
		buf.append("getXDirAdj = " + text.getXDirAdj() + "\n");
		buf.append("getXScale = " + text.getXScale() + "\n");
		buf.append("getY = " + text.getY() + "\n");
		buf.append("getYDirAdj = " + text.getYDirAdj() + "\n");
		buf.append("getYScale = " + text.getYScale() + "\n");
		buf.append("getTextPos = " + text.getTextMatrix() + "\n");
		
		buf.append("\n\n");
		System.out.println(buf.toString());
		
	}
	
	private void dumpResult(OCRSearchResult res) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("===============RESULT===============");
		
		buf.append("x = " + res.getX() + "\n");
		buf.append("y = " + res.getY() + "\n");
		buf.append("xend = " + res.getxEnd() + "\n");
		buf.append("yend = " + res.getyEnd() + "\n");
		buf.append("fontsize = " + res.getFontSize() + "\n");
		buf.append("height = " + res.getPageHeight() + "\n");
		buf.append("width = " + res.getPageWidth() + "\n");
		
		buf.append("\n\n");
		System.out.println(buf.toString());

	}

//	protected void appendChar(String tChar) {
//		tWord.append(tChar);
//		is1stChar = false;
//	}
//
//	protected void setWordCoord(TextPosition text, String tChar, String fontSize) {
//		tWord.append("(").append(pageNo).append(")").append("{" + fontSize + "}[").append(roundVal(Float.valueOf(text.getXDirAdj()))).append(" : ").append(roundVal(Float.valueOf(text.getYDirAdj()))).append("] ").append(tChar);
//		is1stChar = false;
//	}
//
//	protected void endWord() {
//		String newWord = tWord.toString().replaceAll("[^\\x00-\\x7F]", "");
//		String sWord = newWord.substring(newWord.lastIndexOf(' ') + 1);
//		if (!"".equals(sWord)) {
//			for(String searchedWord : queryWords) {
//				if(!newWord.equalsIgnoreCase(sWord)) {
//					wordList.remove(tWord);
//				}
//			}
//		}
//		tWord.delete(0, tWord.length());
//		is1stChar = true;
//	}
//
//	protected boolean matchCharLine(TextPosition text) {
//		Double yVal = roundVal(Float.valueOf(text.getYDirAdj()));
//		if (yVal.doubleValue() == lastYVal) {
//			return true;
//		}
//		lastYVal = yVal.doubleValue();
//		endWord();
//		return false;
//	}
//
//	protected Double roundVal(Float yVal) {
//		
//		return Double.parseDouble(Math.round(yVal) + "");
//		
////		DecimalFormat rounded = new DecimalFormat("0.0'0'");
////		Double yValDub = new Double(rounded.format(yVal));
////		return yValDub;
//	}
}