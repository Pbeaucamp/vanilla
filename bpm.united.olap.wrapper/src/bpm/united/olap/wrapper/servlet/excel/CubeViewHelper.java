package bpm.united.olap.wrapper.servlet.excel;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bpm.fwr.api.beans.Constants.Colors;
import bpm.fwr.api.beans.Constants.FontSizes;
import bpm.fwr.api.beans.Constants.FontTypes;
import bpm.fwr.api.beans.template.Style;
import bpm.united.olap.wrapper.fa.FdServlet;
import bpm.vanilla.platform.core.IVanillaAPI;

public class CubeViewHelper {

	public static String generateCubeViewXml(String name, ExcelSession session, IVanillaAPI vanillaApi) throws Exception {
		
		String xml = session.getCube().getView().getXML();
		
//		session.getSock().addCubeView(name, "", session.getGroup().getName(), session.getFasdItem(), session.getCubeName(), xml);
		
		xml = xml.replaceAll("</view>", "");
		
		String bytes = createCubeViewImage(name, session,vanillaApi);
		
		xml += "<rolodeximage>" + bytes + "</rolodeximage>\n";
		
		xml += "</view>\n";
		
		return xml;
	}
	
	private static String createCubeViewImage(String name, ExcelSession session, IVanillaAPI vanillaApi) throws Exception {
		
//		//infos for the report options
//		HashMap<String, Object> options = new HashMap<String, Object>();
		int height = (session.getCube().getLastResult().getRaw().size() * 30) + 100;
		int width = (session.getCube().getLastResult().getRaw().get(0).size() * 100) + 100;
//		options.put("pagesize", height + "]" + width);
//		options.put("orientation", "portrait");
//		HashMap<String, String>  res = new HashMap<String, String> ();
//		res.put("left", "0");
//		res.put("right", "0");
//		res.put("top", "0");
//		res.put("bottom", "0");
//		options.put("margins", res);
//		options.put("output", "HTML");
//		options.put("title", name);
//		options.put("description", "");
//		
//		String output = "html";
//		
//		Date date = new Date();
//		if(name == "")
//			name = "Report_" + date.getTime();
//		
//		/*--------- We create the report ----------*/
//		WysiwygReport report = new WysiwygReport();
//
//		TextHTMLComponent title = new TextHTMLComponent("<div style=\"text-align: center; font-size: 14px;\">" 
//				+ (String)options.get("title") + "</div>");
//		report.addComponent(title);
//		
//		TextHTMLComponent description = new TextHTMLComponent("<div style=\"margin-top: 20px; margin-bottom: 10px; margin-left: 5px; font-size: 12px;\">" 
//				+ (String)options.get("description") + "</div>");
//		report.addComponent(description);
//		
//		StringBuilder buffer = new StringBuilder();
//		buffer.append(createGridCss());
//		buffer.append(createGridHtml(session));
//		
//		TextHTMLComponent labelComponent = new TextHTMLComponent(buffer.toString());
//		report.addComponent(labelComponent);
//		
//		Container container = new Container();
//		container.setDatasets(new ArrayList<bpm.report.beans.dataset.DataSet>());
//		report.setContent(container);
//		
//		report.setOrientation((String)options.get("orientation"));
//		report.setOutput(output);
//		report.setTitle(new HashMap<String, String>());
//		report.setSubtitle(new HashMap<String, String>());
//		report.setMargins((HashMap<String, String>)options.get("margins"));
//		report.setPageSize((String)options.get("pagesize"));
//		report.setSaveOptions(new SaveOptions());
//		
//		DefaultTemplate template = new CubeViewHelper.DefaultTemplate();
//		report.setTitleStyle(template.getTitleStyle());
//		report.setSubTitleStyle(template.getSubTitleStyle());
//		
//		report.setDataCellsStyle(template.getDataStyle());
//		report.setHeaderCellsStyle(template.getHeaderStyle());
//		
//		report.setOddRowsBackgroundColor(template.getOddRowsBackgroundColor());
//		report.setColors(new HashMap<String, Color>());
//
//		ObjectIdentifier objectId = new ObjectIdentifier(session.getRepository().getId(), -1);
//		
//		String reportXML = ReportXml.getWysiwygReportXml(report);
//		InputStream in = IOUtils.toInputStream(reportXML, "UTF-8");
//		
//		//execute the report
//		
//		RemoteReportRuntime serverRemote = new RemoteReportRuntime(vanillaApi.getVanillaUrl(), session.getUser(), session.getPassword());
//		
//		ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null, session.getGroup().getId());
//		config.setOutputFormat("html");
//		
//		InputStream is = null;
//		is = serverRemote.runReport(config, in);
		
		String htmlcode = "";
//		try {
			htmlcode = createGridCss() + createGridHtml(session);//IOUtils.toString(in, "UTF-8");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		//create the image
		JEditorPane pane = new JEditorPane();   
		pane.setEditable(false);   
		pane.setContentType("text/html");
		htmlcode = htmlcode.substring(htmlcode.indexOf("<table"), htmlcode.indexOf("</table>") + "</table>".length());
		pane.setText(htmlcode);
		pane.setSize(new Dimension(width, height));   
		/*  
		* Create a BufferedImage  
		*/   
		BufferedImage image = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_INT_ARGB);   
		Graphics2D g = image.createGraphics();   
		/*  
		* Have the image painted by SwingUtilities  
		*/   
		JPanel contain = new JPanel();   
		SwingUtilities.paintComponent(g, pane, contain, 0, 0, image.getWidth(), image.getHeight());   
		g.dispose();   
		
		//rendre le fond transparent
		ImageFilter filter = new RGBImageFilter()
	    {
	      public final int filterRGB(int x, int y, int rgb)
	      {
	    	  ColorModel cm = ColorModel.getRGBdefault();
	    	  int alpha = cm.getAlpha(rgb);
	          int rouge = cm.getRed(rgb);
	          int vert = cm.getGreen(rgb);
	          int bleu = cm.getBlue(rgb);
	          if (rouge == 255 && vert == 255 && bleu == 255) {
	               alpha = 0 & 0xFF;
	               return  alpha | rouge | vert | bleu;
	          } else
	                   return rgb;
	      }
	    };
	    ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
	    Image i = Toolkit.getDefaultToolkit().createImage(ip);
	    
	    BufferedImage dest = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = dest.createGraphics();
	    g2.drawImage(i, 0, 0, null);
	    g2.dispose();
		
	    dest = resize(dest, 256, 256);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(dest, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] imgBytes = out.toByteArray();
		
		String bytes = "";
		
		for(byte b : imgBytes) {
			bytes += ":" + b;
		}
		
		bytes = bytes.substring(1);
		
		return bytes;
		
	}
	
	private static Object createGridCss() {
		StringBuffer buf = new StringBuffer();
		buf.append("<style>\n");
		buf.append(".cubeView {\n"+
					"vertical-align: middle;\n"+
					"table-layout: fixed;\n"+
				"}\n"+
				".gridItemValueBold {\n"+
					"font-weight: bold;\n"+
				"}\n"+
				".gridItemNull {\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"text-align: center;\n"+
				"}\n"+
				".gridItemValue {\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: white;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"cursor: pointer;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".gridItemBorder {\n"+
					"border-top: 2px groove;\n"+
					"border-left: 2px groove;\n"+
				"}\n"+
				".leftGridSpanItemBorder {\n"+
					"border-left: 2px groove;\n"+
				"}\n"+
				".rightGridSpanItemBorder {\n"+
					"border-top: 2px groove;\n"+
				"}\n"+
				".lastRowItemBorder {\n"+
					"border-bottom: 2px groove;\n"+
				"}\n"+
				".lastColItemBorder {\n"+
					"border-right: 2px groove;\n"+
				"}\n"+
				".draggableGridItem {\n"+
					"table-layout: fixed;\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #C3DAF9;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".measureGridItem {\n"+
					"table-layout: fixed;\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #FFCC66;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".gridTotalItem {\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #C3DAF9;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
				"}\n");
		
		buf.append("</style>\n");
		return buf.toString();
	}

	private static String createGridHtml(ExcelSession session) {
		String html = FdServlet.getHtml(FdServlet.getTable(session.getCube().getLastResult()), null, null, null, null, null);
		return html;
	}

	private static BufferedImage resize(BufferedImage image, int width, int height) { 
		
		double scalex = (double) width / image.getWidth();
		double scaley = (double) height / image.getHeight();
		double sc = Math.min(scalex, scaley);
		
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); 
		Image img = image.getScaledInstance((int) (image.getWidth() * sc), (int) (image.getHeight() * sc), Image.SCALE_SMOOTH);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		
		//center the image
		BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform tra = null;
		
		if(scalex < scaley) {
			double tr = image.getHeight() * sc;
			double traaa = (height - tr) / 2;
			if(tr != 0) {
				tra = AffineTransform.getTranslateInstance(0,traaa);
				AffineTransformOp opLeft = new AffineTransformOp(tra, AffineTransformOp.TYPE_BILINEAR);
				resizedImage = opLeft.filter(resizedImage, img2);
			}
		}
		else {
			double tr = image.getWidth() * sc;
			double traaa = (width - tr) / 2;
			if(tr != 0) {
				tra = AffineTransform.getTranslateInstance(traaa,0);
				AffineTransformOp opLeft = new AffineTransformOp(tra, AffineTransformOp.TYPE_BILINEAR);
				resizedImage = opLeft.filter(resizedImage, img2);
			}
		}
		
		
		return resizedImage; 
	}
	
	private static class DefaultTemplate {
		
		public DefaultTemplate(){ }
		
		public Style getDataStyle() {
			Style defaultDataStyle = new Style(false);
			defaultDataStyle.setBackgroundColor(Colors.WHITE);
			defaultDataStyle.setTextColor(Colors.BLACK);
			defaultDataStyle.setFontType(FontTypes.VERDANA);
			defaultDataStyle.setFontSize(FontSizes.T8);
			
			return defaultDataStyle;	
		}

		public Style getHeaderStyle() {
			Style defaultHeaderStyle = new Style(false);
			defaultHeaderStyle.setBackgroundColor(Colors.GRAY);
			defaultHeaderStyle.setTextColor(Colors.WHITE);
			defaultHeaderStyle.setFontType(FontTypes.VERDANA);
			defaultHeaderStyle.setFontSize(FontSizes.T10);
			
			return defaultHeaderStyle;
		}

		public Style getSubTitleStyle() {
			Style defaultSubTitleStyle = new Style(false);
			defaultSubTitleStyle.setBackgroundColor(Colors.WHITE);
			defaultSubTitleStyle.setTextColor(Colors.GRAY);
			defaultSubTitleStyle.setFontType(FontTypes.COMIC);
			defaultSubTitleStyle.setFontSize(FontSizes.T8);
			
			return defaultSubTitleStyle;
			
			
		}

		public Style getTitleStyle() {
			Style defaultTitleStyle = new Style(false);
			defaultTitleStyle.setBackgroundColor(Colors.BLACK);
			defaultTitleStyle.setTextColor(Colors.WHITE);
			defaultTitleStyle.setFontType(FontTypes.COMIC);
			defaultTitleStyle.setFontSize(FontSizes.T14);
			return defaultTitleStyle;
		}

		public String getOddRowsBackgroundColor() {
			return Colors.GRAY;
		}
	
	}
}
