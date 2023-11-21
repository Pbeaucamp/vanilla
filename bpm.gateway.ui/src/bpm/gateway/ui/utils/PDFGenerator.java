package bpm.gateway.ui.utils;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.Transformation;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFGenerator {

	private static final Font chapterTitleFont = new Font(FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static final Font sutitles = new Font(FontFamily.TIMES_ROMAN, 16, Font.BOLDITALIC);
	private static final Font normalFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static final Font linedFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);


	
	public static void generatePdf(DocumentGateway model, OutputStream output, HashMap<Class<?>, URL> imagePath, GatewayEditorPart editorPart){
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, output);
			
			document.open();
//			addMetaData(document);
//			addTitlePage(document);
			org.eclipse.swt.graphics.Image img = null;
			try{
				
				ImageLoader loader1 = new ImageLoader();
				img = editorPart.getModelAsImage();
				
				int width = (int)(document.getPageSize().getWidth() - document.getPageSize().getBorderWidth());
				
				
				if (width < img.getBounds().width){
					float coef = (float)((float)width ) / (float)img.getBounds().width;
					
					loader1.data = new ImageData[] { img.getImageData().scaledTo(width, (int)(img.getBounds().height * coef)) };

				}
				else{
										
					loader1.data = new ImageData[] { img.getImageData() };

				}
					loader1.save("__out.png", SWT.IMAGE_PNG); //$NON-NLS-1$
				
				addSchema(document, Image.getInstance("__out.png")); //$NON-NLS-1$
			}catch(Exception ex){
				ex.printStackTrace();
			}
			finally{
				File f = new File("__out.png"); //$NON-NLS-1$
				if (f.exists()){
					f.delete();
				}
				if (img != null){
					img.dispose();
				}
				
			}
			addServers(document, model);
			addSteps(document, model, imagePath);
			document.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private  static void addSchema(Document document, Image image){
		Chapter chap = new Chapter(Messages.PDFGenerator_0, 1);
		try {
			
			chap.add(Image.getInstance(image));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		try {
			document.add(chap);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	private  static void addServers(Document document, DocumentGateway model){
		int chapNum = 2;
		Chapter chap = new Chapter(Messages.PDFGenerator_4, chapNum);
		
		for(Server s : model.getResourceManager().getServers()){
			Section section = chap.addSection(s.getName());
			
			section.add(new Paragraph(" ")); //$NON-NLS-1$
			section.add(generateServersGeneric(s));
			section.add(new Paragraph(" ")); //$NON-NLS-1$
		}
		
		
		try {
			document.add(chap);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	private  static void addSteps(Document document, DocumentGateway model, HashMap<Class<?>, URL> imagePath){
		int chapNum = 3;
		Chapter chap = new Chapter(Messages.PDFGenerator_7, chapNum);
		
		for(Transformation t : model.getTransformations()){
			
			  
			Section section = chap.addSection(t.getName());
			
			try {
				section.add(0,Image.getInstance(imagePath.get(t.getClass())));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			
			
			section.add(new Paragraph(" ")); //$NON-NLS-1$
			section.add(generateStepGeneric(t, imagePath));
			section.add(new Paragraph(" ")); //$NON-NLS-1$
			section.add(new Paragraph(t.getAutoDocumentationDetails()));

			
			
		}
		try {
			document.add(chap);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	private static Paragraph generateStepGeneric(Transformation t, HashMap<Class<?>, URL> imagePath){
		Paragraph intro = new Paragraph();
		Phrase p = new Phrase(Messages.PDFGenerator_11, linedFont);
		intro.add(p);
		
		intro.add(new Phrase(t.getClass().getSimpleName(), normalFont));
		
		
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		
		p = new Phrase(Messages.PDFGenerator_13, linedFont);
		p.add(new Phrase(t.getDescription(), normalFont));
		
		intro.add(p);
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		
		return intro;
	}
	
	private static Paragraph generateServersGeneric(Server t){
		Paragraph intro = new Paragraph();
		Phrase p = new Phrase(Messages.PDFGenerator_15, linedFont);
		intro.add(p);
		intro.add(new Phrase(t.getClass().getSimpleName(), normalFont));
		
		
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		
//		p = new Phrase("Description : ", linedFont);
		intro.add(new Phrase(Messages.PDFGenerator_17, linedFont));
		intro.add(new Phrase(t.getDescription(), normalFont));
		
//		intro.add(p);
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		for(IServerConnection s : t.getConnections()){
			p = new Phrase(Messages.PDFGenerator_19 + s.getName(), linedFont);
			intro.add(p);
			intro.add(new Paragraph(s.getAutoDocumentationDetails()));
		}
		
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		
		
		return intro;
	}
}