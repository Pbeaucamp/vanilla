package bpm.faweb.server.tools;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter {
	private   Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			Font.BOLD);
	private  Font originFont = new Font(Font.FontFamily.TIMES_ROMAN, 10,
			Font.ITALIC);
	private   Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD);

//	public static void main(String[] args) {
//		try {
//			/new PDFWriter(true, PageSize.A4, "dasds", "dasd", "dasdsadsa", "dsad");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public  PDFWriter(String url, boolean isLandscape,com.itextpdf.text.Rectangle pageSize, String title, String origin, String description, String filters, List<String> headerList, List<List<String>> cellList){
		try {
			Document document = new Document();
			if(isLandscape){
				document.setPageSize(pageSize.rotate());
			}else{
				document.setPageSize(pageSize);
			}
			
			PdfWriter.getInstance(document, new FileOutputStream(url));
			document.open();
			addTitlePage(document, title, origin, description, filters, headerList, cellList);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	

	// iText allows to add metadata to the PDF which can be viewed in your Adobe
	// Reader
	// under File -> Properties
	

	private   void addTitlePage(Document document, String title, String origin, String description, String filters, List<String> headerList, List<List<String>> cellList)
			throws DocumentException {
		Paragraph preface = new Paragraph();
		// We add one empty line
		addEmptyLine(preface, 1);
		// Lets write a big header
		Paragraph titlePar = new Paragraph(title, catFont);
		titlePar.setAlignment(Element.ALIGN_CENTER);
		preface.add(titlePar);
		
		Paragraph originPar = new Paragraph("Origin: " +  origin, originFont);
		originPar.setAlignment(Element.ALIGN_CENTER);
		preface.add(originPar);
		addEmptyLine(preface, 2);
		// Will create: Description: desc
		preface.add(new Paragraph("Description: " +  description, smallBold));
		// Will create: Date: _date
		preface.add(new Paragraph("Date: " +  new Date(), smallBold));
		// Will create: Filters: _filters
		preface.add(new Paragraph("Filters: " + filters, smallBold));
		
		addEmptyLine(preface, 2);
		
		document.add(preface);
		
		
		createTable(document, headerList, cellList);
	}

	

	private   void createTable(Document subCatPart, List<String> headerList, List<List<String>> cellList)
			throws DocumentException {
		PdfPTable table = new PdfPTable(headerList.size());
		table.setWidthPercentage(100);
		// t.setBorderColor(BaseColor.GRAY);
		// t.setPadding(4);
		// t.setSpacing(4);
		// t.setBorderWidth(1);

		
		
		
		for(String header : headerList){
			PdfPCell c1 = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
			c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
		}
		table.completeRow();

//		c1 = new PdfPCell(new Phrase("Table Header 2"));
//		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(c1);
//
//		c1 = new PdfPCell(new Phrase("Table Header 3"));
//		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(c1);
		
		table.setHeaderRows(1);
		

		for(List<String> cell : cellList){
			for(String s : cell){
				 PdfPCell pdfCell = new PdfPCell();
				 pdfCell.setPhrase(new Phrase(s.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
                 table.addCell(pdfCell);
			}
		}

		subCatPart.add(table);

	}

	private   void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
