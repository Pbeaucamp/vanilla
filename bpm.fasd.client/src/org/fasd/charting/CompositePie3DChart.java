package org.fasd.charting;

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

public class CompositePie3DChart extends Composite {
	private Label image;
	private int chartSizeX, chartSizeY;
	private String itemName;

	private JFreeChart chart;
	private PieDataset dataset;

	public CompositePie3DChart(Composite parent, PieDataset dataset, String itemName) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout());
		this.dataset = dataset;
		this.itemName = itemName;

		image = new Label(this, SWT.NONE);
		image.setLayoutData(new GridData(GridData.FILL_BOTH));
		getChart();
	}

	public void getChart() {
		chart = ChartFactory.createPieChart3D("Distinct Values of " + itemName, dataset, false, // include //$NON-NLS-1$
																								// legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(java.awt.Color.WHITE);

	}

	public void drawChart(Rectangle r) {
		this.layout();
		chartSizeX = r.width;
		chartSizeY = r.height;

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

}
