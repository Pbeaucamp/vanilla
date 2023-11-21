package bpm.profiling.ui.composite;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.PieDataset;

public class CompositePieChart extends Composite {
	private Label image;
	private int chartSizeX, chartSizeY;
//	private String itemName; 
	
	private JFreeChart chart;
	private PieDataset dataset;
	
	private String title;
	
	public CompositePieChart(Composite parent, PieDataset dataset, String title) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout());
		this.dataset = dataset;
//		this.itemName = itemName;
//		query = q;
		this.title = title;
		
		image = new Label(this, SWT.NONE);
		image.setLayoutData(new GridData(GridData.FILL_BOTH));
		getChart();
	}

	
	public void getChart() {
		//build name
	
		chart = ChartFactory.createPieChart
		(title,
		dataset, 
		true, 
		true, 
		false);

		chart.setBackgroundPaint(java.awt.Color.WHITE);

	}

	
	public void drawChart(Rectangle r) {
		this.layout();
		chartSizeX = r.width;///*1000;*/image.getParent().getSize().x;
		chartSizeY = r.height;///*1000;*/image.getParent().getSize().y;


		BufferedImage bufferedImage = chart.createBufferedImage(chartSizeX, chartSizeY, null);
		
		DataBuffer buffer = bufferedImage.getRaster().getDataBuffer();
		DataBufferInt intBuffer = (DataBufferInt) buffer;
		
		PaletteData paletteData = new PaletteData(0x00FF0000, 0x0000FF00, 0x000000FF);
		ImageData imageData = new ImageData(chartSizeX, chartSizeY, 32, paletteData);
		for (int bank = 0; bank < intBuffer.getNumBanks(); bank++) {
			int[] bankData = intBuffer.getData(bank);
			imageData.setPixels(0, bank, bankData.length, bankData, 0);     
		}
		image.setImage(new Image(image.getParent().getDisplay(), imageData));
	}
	
	protected void drawVoid() {
		image.setImage(null);
	}


	@Override
	public void dispose() {
		if (image != null && !image.isDisposed()){
			if (image.getImage() != null && !image.getImage().isDisposed()){
				image.getImage().dispose();
			}
		}
		super.dispose();
	}

}
