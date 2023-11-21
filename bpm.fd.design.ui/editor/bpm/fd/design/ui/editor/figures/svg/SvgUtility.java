package bpm.fd.design.ui.editor.figures.svg;

import java.awt.Point;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

/**
 * 
 * @author ely
 * 
 */
public class SvgUtility {

	private static HashMap<ChartNature, List<Image>> builtImages = new HashMap<ChartNature, List<Image>>();
	
	
	
	private static InputStream getSvgBaseFile(ChartNature nature) throws Exception{

		String natureFile = "";
		
		switch(nature.getNature()){
		case ChartNature.BAR:
			natureFile = Icons.SVG_BAR_2D;
			break;
		case ChartNature.HEAT_MAP:
			natureFile = Icons.SVG_HEATMAP;
			break;
		case ChartNature.BAR_3D:
			natureFile = Icons.SVG_MS_BAR3D;
			break;
		case ChartNature.SCATTER:
			natureFile = Icons.SVG_SCATTER;
			break;
		case ChartNature.BOX:
			natureFile = Icons.SVG_BOX;
			break;
		case ChartNature.BULLET:break;
		case ChartNature.COLUMN:
			natureFile = Icons.SVG_COLUMN_2D;
//			natureFile = "chart_column_ms_2D.png";
			break;
		case ChartNature.COLUMN_3D:
			natureFile = Icons.SVG_COLUMN_3D;
//			natureFile = "chart_column_ms_2D.png";
			break;
		case ChartNature.COLUMN_3D_LINE:
			natureFile = Icons.SVG_MS_COLUMN3D_LINE_DUAL;
			break;
		case ChartNature.COLUMN_3D_LINE_DUAL:
			natureFile = Icons.SVG_MS_COLUMN3D_LINE_DUAL;
			break;
		case ChartNature.SPARK:
			natureFile = Icons.SVG_SPARK;
			break;
		case ChartNature.COLUMN_3D_MULTI:
			natureFile = Icons.SVG_MS_COLUMN3D;
			break;
		case ChartNature.COLUMN_LINE_DUAL:
			natureFile = Icons.SVG_MS_COLUMN2D_LINE_DUAL;
			break;
		case ChartNature.COLUMN_MULTI:
			natureFile = Icons.SVG_MS_COLUMN2D;
			break;
		case ChartNature.DUAL_Y_2D_COMBINATION:
			natureFile = Icons.SVG_MS_COMBI2D_DUAL;
			break;
		case ChartNature.FUNNEL:
			natureFile = Icons.SVG_FUNNEL;
			break;
		case ChartNature.GAUGE:break;
		case ChartNature.LINE:
			natureFile = Icons.SVG_LINE_MULTI;
			break;
		case ChartNature.LINE_MULTI:
			natureFile = Icons.SVG_LINE_MULTI;
			break;
		case ChartNature.MARIMEKO:
			natureFile = Icons.SVG_MARIMEKKO;
			break;
		case ChartNature.PIE_3D:
			natureFile = Icons.SVG_PIE_3D;
			break;
		case ChartNature.PIE:
			natureFile = Icons.SVG_PIE_2D;
			break;
		case ChartNature.PYRAMID:
			natureFile = Icons.SVG_PYRAMID;
			break;
		case ChartNature.RADAR:
			natureFile = Icons.SVG_RADAR;
			break;
		case ChartNature.SINGLE_Y_2D_COMBINATION:
			natureFile = Icons.SVG_MS_COMBI2D;
			break;
		case ChartNature.SINGLE_Y_3D_COMBINATION:
			natureFile = Icons.SVG_MS_COMBI3D;
			break;
		case ChartNature.STACKED_2D_LINE_DUAL_Y:
			//natureFile = Icons.SVG_MS_S;
			break;
		case ChartNature.STACKED_AREA_2D:
			natureFile = Icons.SVG_MS_STACKED_AREA_2D;
			break;
		case ChartNature.STACKED_BAR:
			natureFile = Icons.SVG_MS_STACKED_BAR_2D;
			break;
		case ChartNature.STACKED_BAR_3D:
			natureFile = Icons.SVG_MS_STACKED_BAR_3D;
			break;
		case ChartNature.STACKED_COLUMN:
			natureFile = Icons.SVG_MS_STACKED_COL_2D;
			break;
		case ChartNature.STACKED_COLUMN_3D:
			natureFile = Icons.SVG_MS_STACKED_COL_3D;
			break;
		case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:
			natureFile = Icons.SVG_MS_STACKED_COL_3D_LINE_DUAL;
			break;

		default:
			//Activator.getDefault().getBundle().getResource("bpm/fd/design/ui/icons/"+  Icons.SVG_COLUMN_3D_LINE")
			//			return Activator.getDefault().getBundle().getEntry("/bpm/fd/design/ui/icons/" + Icons.SVG_COLUMN_3D_LINE).openStream();
		}
		URL url =  Activator.getDefault().getBundle().getResource("bpm/fd/design/ui/icons/"+  natureFile);
		url = FileLocator.resolve(url);
		return url.openConnection().getInputStream();

		//throw new Exception("Nature " + nature.toString() + " not supported");
	}
	
	private static Image getImage(ChartNature nature, Point size) throws Exception{
		if (builtImages.get(nature) == null){
			builtImages.put(nature, new ArrayList<Image>());
		}
		for(Image img : builtImages.get(nature)){
			org.eclipse.swt.graphics.Rectangle t = img.getBounds();
			
			if (t.width - 50 <= size.x && size.x <= t.width + 50 &&
				t.height - 50 <= size.y && size.y <= t.height+ 50){
				return img;
			}
		}
		
		//create the Image

//		JPEGTranscoder t = new JPEGTranscoder();
//
//		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
//		float width = size.x;
//		float height = size.y;
//
//		// ici la nouvelle image est sett�
//		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, width);
//		t.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, height);
//
//		InputStream svgSteam = getSvgBaseFile(nature);
//		TranscoderInput input = new TranscoderInput(svgSteam);
//		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		TranscoderOutput output = new TranscoderOutput(bos);
		System.out.println(nature.getChartJsName());
		try {
//			t.transcode(input, output);
//			Image img = new Image(Display.getDefault(),new ByteArrayInputStream(bos.toByteArray()));
//			
//			
//			builtImages.get(nature).add(img);
//			return img;
			return null;
		} catch (Throwable e) {
//			Image img = new Image(Display.getDefault(),svgSteam);
			
			
//			builtImages.get(nature).add(img);
//			return img;
			return null;
//			e.printStackTrace();
//			return null;
		}
		
	}

	public static ImageFigure createImage(ChartNature nature, Point preferedSize) {
		Image im = null;
		try{
			im = getImage(nature, preferedSize);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		ImageFigure iF = new ImageFigure();
		iF.setImage(im);
		// retourne l'image dans la bonne dimmension
		return iF;
	}

	
	public static void release(){
		synchronized (builtImages) {
			for(ChartNature k : builtImages.keySet()){
				for(Image i : builtImages.get(k)){
					i.dispose();
				}
				
			}
			builtImages.clear();
		}
		
	}
}
