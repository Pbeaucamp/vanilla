package bpm.birep.admin.client.helpers;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adminbirep.Messages;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFGenerator {

	private static final Font normalFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static final Font linedFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);
	private static final Font colorFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE, BaseColor.ORANGE);

	public static void generatePdf(OutputStream output, HashMap<Integer, URL> imagePath, TreeParent<?> root) {
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, output);

			document.open();
			addSchema(document, root.getChildren());
			
			List<Chapter> chapters = new ArrayList<Chapter>();
			addRepository(chapters, imagePath, Messages.PDFGenerator_0, 2, root.getChildren());
			
			for(Chapter chap : chapters) {
				try {
					document.add(chap);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			document.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void addSchema(Document document, Object[] items) {
		Chapter chap = new Chapter(Messages.PDFGenerator_1, 1);
		chap.add(new Paragraph(" ")); //$NON-NLS-1$
		try {
			for(Object obj : items) {
				if (obj instanceof TreeDirectory) {
					TreeDirectory treeDirectory = (TreeDirectory) obj;

					chap.add(new Phrase(Messages.PDFGenerator_2 + treeDirectory.getName() + "\n")); //$NON-NLS-1$
	
					addLevel(chap, 1, treeDirectory.getChildren());
				}
				else if (obj instanceof TreeItem) {
					TreeItem treeItem = (TreeItem) obj;

					chap.add(new Phrase(treeItem.getName() + "\n")); //$NON-NLS-1$
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			document.add(chap);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	private static void addLevel(Chapter chap, int levelNumber, Object[] items) {
		String previous = "  "; //$NON-NLS-1$
		for(int i=0; i<levelNumber; i++) {
			previous += "    "; //$NON-NLS-1$
		}
		previous += "|__"; //$NON-NLS-1$
		
		if(items != null) {
			for(Object obj : items) {
				if (obj instanceof TreeDirectory) {
					TreeDirectory treeDirectory = (TreeDirectory) obj;
	
					chap.add(new Phrase(previous + Messages.PDFGenerator_3 + treeDirectory.getName() + "\n")); //$NON-NLS-1$
	
					addLevel(chap, levelNumber+1, treeDirectory.getChildren());
				}
				else if (obj instanceof TreeItem) {
					TreeItem treeItem = (TreeItem) obj;
	
					chap.add(new Phrase(previous + " " + treeItem.getName() + "\n")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	private static void addRepository(List<Chapter> chaps, HashMap<Integer, URL> imagePath, String parentName, int number, Object[] items) {
		if(items != null && items.length != 0) {
			Chapter chap = new Chapter(new Paragraph(new Phrase(Messages.PDFGenerator_12 + parentName, colorFont)), number);
			chaps.add(chap);
			chap.add(new Paragraph(" ")); //$NON-NLS-1$
	
			for (Object obj : items) {
				if (obj instanceof TreeDirectory) {
					TreeDirectory treeDirectory = (TreeDirectory) obj;
	
					Section section = chap.addSection(Messages.PDFGenerator_13 + treeDirectory.getName());
	
					try {
						section.add(0, Image.getInstance(imagePath.get(-1)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					section.add(generateStepGeneric(treeDirectory));
	
					addRepository(chaps, imagePath, treeDirectory.getName(), number + 1, treeDirectory.getChildren());
				}
				else if (obj instanceof TreeItem) {
					TreeItem treeItem = (TreeItem) obj;
	
					Section section = chap.addSection(treeItem.getName());
	
					try {
						section.add(0, Image.getInstance(imagePath.get(treeItem.getItem().getType())));
					} catch (Exception e) {
						e.printStackTrace();
					}
	
					section.add(generateStepGeneric(treeItem));
				}
			}
		}
	}

	private static Paragraph generateStepGeneric(TreeItem t) {
		Paragraph intro = new Paragraph();
		intro.add(new Phrase(Messages.PDFGenerator_14, linedFont));
		intro.add(new Phrase(IRepositoryApi.TYPES_NAMES[t.getItem().getType()], normalFont));

		intro.add(new Paragraph(" ")); //$NON-NLS-1$

		if(t.getItem().getComment() != null && !t.getItem().getComment().isEmpty()) {
			intro.add(new Phrase(Messages.PDFGenerator_15, linedFont));
			intro.add(new Phrase(t.getItem().getComment(), normalFont));
		}
		
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		intro.add(new Paragraph(" ")); //$NON-NLS-1$

		return intro;
	}

	private static Paragraph generateStepGeneric(TreeDirectory t) {
		Paragraph intro = new Paragraph();
		intro.add(new Phrase(Messages.PDFGenerator_16, linedFont));
		intro.add(new Phrase(Messages.PDFGenerator_17, normalFont));

		intro.add(new Paragraph(" ")); //$NON-NLS-1$

		if(t.getDirectory().getComment() != null && !t.getDirectory().getComment().isEmpty()) {
			intro.add(new Phrase(Messages.PDFGenerator_18, linedFont));
			intro.add(new Phrase(t.getDirectory().getComment(), normalFont));
		}
		
		intro.add(new Paragraph(" ")); //$NON-NLS-1$
		intro.add(new Paragraph(" ")); //$NON-NLS-1$

		return intro;
	}
}