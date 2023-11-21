package bpm.gwt.aklabox.commons.server.servlets;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import bpm.aklabox.workflow.core.IAklaflowConstant;

public class ScannerServlet{

	private Scanner scanner;
	private String fileName;
	
	public ScannerServlet() throws Exception {
		fileName = IAklaflowConstant.CELL_DESTINATION + "Scan_" + System.currentTimeMillis();
		scanner = Scanner.getDevice();
		scanner.addListener(new ScannerListener() {
		
			@Override
			public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {
				if (type.equals(ScannerIOMetadata.ACQUIRED)) {
					BufferedImage image = metadata.getImage();
					System.out.println("Have an image now!");
					try {
						
						
						File f = new File(fileName);
						ImageIO.write(image, "png", f);
						
						
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new Exception(e.getMessage());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					
				}
				else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
					
					try {
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
					System.err.println(metadata.getStateStr());
				}
				else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
					metadata.getException().printStackTrace();
					try {
						throw new Exception(metadata.getException().getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		scanner.acquire();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

	
	

	
}
