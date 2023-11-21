package bpm.vanilla.api.runtime.dto;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import bpm.fwr.api.beans.components.ImageComponent;
import bpm.vanilla.api.runtime.dto.ReportSheet.ReportWidgetType;

public class ReportImageWidget extends ReportWidget {

	private int width;
	private int height;
	private String src;
	
	public ReportImageWidget() {
		super();
	}

	public ReportImageWidget(int id, String type, int width, int height, String src) {
		super(id, type);
		this.width = width;
		this.height = height;
		this.src = src;
	}

	public ReportImageWidget(ImageComponent imageComponent,int id) {
		this.id = id;
		this.type = ReportWidgetType.IMAGE;
		this.width = 100;
		this.height = 100;
		this.src = imageComponent.getUrl();
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getSrc() {
		return src;
	}
	
	public ImageComponent createImageComponent(int row,int col) {
		ImageComponent image = new ImageComponent();
		
		image.setX(col);
		image.setY(row);
		
        try {
            BufferedImage bufimage = ImageIO.read(new ByteArrayInputStream(src.getBytes()));
            BufferedImage resizedBufImage = resizedImage(bufimage,width,height);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedBufImage,"png", os);
            String resizedBase64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
            image.setUrl(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return image;

	}
	
	private BufferedImage resizedImage(BufferedImage OldBufImage,int newWidth,int newHeight) {
		BufferedImage newBufImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = newBufImage.createGraphics();
        graphics2D.drawImage(OldBufImage, 0, 0, width, height, null);
        graphics2D.dispose();
		
		return newBufImage;
	}
	
}
