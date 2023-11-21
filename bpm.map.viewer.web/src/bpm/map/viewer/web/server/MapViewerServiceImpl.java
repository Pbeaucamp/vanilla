package bpm.map.viewer.web.server;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.map.viewer.web.client.services.MapViewerService;

public class MapViewerServiceImpl extends RemoteServiceServlet implements MapViewerService {

	private static final long serialVersionUID = -660218264074823448L;

	private MapViewerSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), MapViewerSession.class);
	}

	@Override
	public List<ComplexMap> getMaps() throws ServiceException {
		MapViewerSession session = getSession();
		List<ComplexMap> lesMaps;
		
		try {
			 lesMaps = session.getManager().getAllComplexMaps();
			 return lesMaps;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of maps : " + e.getMessage());
		}
	}

	@Override
	public void addOrEditMap(ComplexMap map) throws ServiceException {
		MapViewerSession session = getSession();

		try {
			if (map.getId() > 0) {
				session.getManager().updateComplexMap(map);
			}
			else {
				session.getManager().saveComplexMap(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Map.", e);
		}
		 
		
	}
	
	@Override
	public void deleteMap(ComplexMap selectedMap) throws ServiceException {
		MapViewerSession session = getSession();

		try {
			//deleteMapDataSet(selectedMap.getDataSet());
			session.getManager().deleteComplexMap(selectedMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Map.", e);
		}
	}
	
	@Override
	public List<MetricValue> getValuesByDateAxisMetric(Date date, Level level, int metricId, int groupId) throws Exception {
		return getSession().getManager().getValuesByDateAndLevelAndMetric(date, level.getId(), metricId, groupId);
	}


	@Override
	public List<MapZoneValue> getMapValues(Date startDate, Date endDate, int id, Level object, int groupId) throws Exception {
		return getSession().getManager().getMapZoneValues(id, object.getId(), startDate, endDate, groupId);
	}
	
	@Override
	public List<ComplexObjectViewer> getMultiMapValues(Date startDate, Date endDate, List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, List<LevelMember> filterLevelMembers, int groupId) throws Exception {
		
		return getSession().getManager().getMultiMapZoneValues(metrics, levels, startDate, endDate, filterLevelMembers, getSession().getRepositoryConnection().getContext().getGroup().getId());
	}

	@Override
	public AxisInfo getAxisInfo(int idAxis) throws Exception {
		return getSession().getManager().getLoaderAxe(idAxis);
	}
	
	@Override
	public String exportToImage(String name, String img, String type) throws ServiceException {
		CommonSession session = getSession();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(Base64.decodeBase64(img.replace("data:image/png;base64,", "")));
			session.addStream(name, type, is);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		

		return name;

	}
	
	@Override
	public String exportToPdf(String title, String text, String author, String img, String type) throws Exception {
		CommonSession session = getSession();

		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		document.addPage( page );
		PDRectangle pageSize = page.getMediaBox();

		// Create a new font object selecting one of the PDF base fonts
		PDFont fontBold = PDType1Font.HELVETICA_BOLD;
		PDFont font = PDType1Font.HELVETICA;
		PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		int nbline = 5;
		Point2D.Float pageCenter = getCenter(page, false);

		// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
		contentStream.beginText();
		contentStream.setFont( fontBold, 36 );
		// We use the text's width to place it at the center of the page
	    float stringWidth = fontBold.getStringWidth(title) * 36 / 1000F;
	    float textX = pageCenter.x - stringWidth / 2F;
        float textY = pageCenter.y;
        contentStream.setTextMatrix(Matrix.getTranslateInstance(textX, textY+200));
		contentStream.showText(title);
		contentStream.endText();
		
		// Make sure that the content stream is closed:
//		contentStream.close();
		
//		PDPageContentStream contentStream2 = new PDPageContentStream(document, page, AppendMode.APPEND, true);
		float lineSize = page.getMediaBox().getWidth() - 100;
		contentStream.setFont( font, 12 );
		for(String line : text.split("\n")){
			for(String wrapline : getWrappedLine(line.split(" "), lineSize, font, 12)){
				contentStream.beginText();
				contentStream.newLineAtOffset(50, pageCenter.y - 15*(++nbline));
				contentStream.showText(wrapline);
				contentStream.endText();
			}
		}
		//Date
		DateFormat dtf = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
		String date = dtf.format(new Date());
		contentStream.beginText();
		contentStream.setFont( fontItalic, 12 );
		contentStream.newLineAtOffset(pageSize.getWidth() - 50 - (fontItalic.getStringWidth(date) * 12 / 1000F), pageSize.getHeight() - 50);
		contentStream.showText(date);
		contentStream.endText();
		
		//Author
		contentStream.beginText();
		contentStream.setFont( fontItalic, 12 );
		contentStream.newLineAtOffset(pageSize.getWidth() - 50 - (fontItalic.getStringWidth(author) * 12 / 1000F), pageSize.getHeight() - 65);
		contentStream.showText(author);
		contentStream.endText();
		

		// Make sure that the content stream is closed:
		contentStream.close();
		
		File temp = File.createTempFile("tempfile", ".png"); 
		BufferedImage image = null;
	    byte[] imageByte;
	    try {
	        imageByte = Base64.decodeBase64(img.replace("data:image/png;base64,", "").getBytes());
	        
	        FileOutputStream fos = new FileOutputStream(temp);
	        fos.write(imageByte);
	        fos.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	    PDPage page2 = new PDPage(PDRectangle.A4);
	    page2.setRotation(90);
	    document.addPage(page2);
		PDImageXObject pdImage =  PDImageXObject.createFromFileByContent(temp, document);
		PDPageContentStream contentStream2 = new PDPageContentStream(document, page2);
		PDRectangle pageSize2 = page2.getMediaBox();
		float scale = 1f;
		float wratio = (pageSize2.getHeight() - 100) / (float)pdImage.getWidth();
		float hratio = (pageSize2.getWidth() - 100) / (float)pdImage.getHeight();
		if(Math.abs(wratio - 1) > Math.abs(hratio - 1)){
		 	scale = wratio;
		} else {
			scale = hratio;
		}
		
		Point2D.Float page2Center = getCenter(page2, true);
		AffineTransform at = new AffineTransform(pdImage.getHeight()*scale, 0, 0, pdImage.getWidth()*scale, page2Center.y, page2Center.x);
		at.rotate(Math.toRadians(90));
		at.translate(-0.5, -0.5);
//		contentStream2.drawImage(pdImage, page2Center.y - pdImage.getWidth()*scale/2, page2Center.x - pdImage.getHeight()*scale/2, pdImage.getWidth()*scale, pdImage.getHeight()*scale);
		contentStream2.drawXObject(pdImage, at);
		contentStream2.close();
		
		// Save the results and ensure that the document is properly closed:		
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		document.save(outputStream);
		document.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(outputStream.toByteArray());

		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		title = title.replace(" ", "_");
		session.addStream(title, type, bais);

		return title;

	}
	
	private List<String> getWrappedLine(String[] splitLine, float lineSize, PDFont font, int fontSize) throws Exception {
		List<String> result = new ArrayList<>();
//		String[] split = line.split(" ");
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for(i=0; i< splitLine.length; i++){
			StringBuffer temp = new StringBuffer(buf.toString());
			temp.append(splitLine[i]);
			if(font.getStringWidth(temp.toString()) * fontSize / 1000F > lineSize){
				break;
			}
			
			buf.append(splitLine[i] + " ");
		}
		if(i == splitLine.length ){
			result.add(buf.toString());
		} else {
			result.add(buf.toString());
			result.addAll(getWrappedLine(Arrays.copyOfRange(splitLine, i, splitLine.length), lineSize, font, fontSize));
		}
		
		return result;
	}

	private Point2D.Float getCenter(PDPage page, boolean rotated) {
	    PDRectangle pageSize = page.getMediaBox();
	    float pageWidth = rotated ? pageSize.getHeight() : pageSize.getWidth();
	    float pageHeight = rotated ? pageSize.getWidth() : pageSize.getHeight();

	    return new Point2D.Float(pageWidth / 2F, pageHeight / 2F);
	}
}
