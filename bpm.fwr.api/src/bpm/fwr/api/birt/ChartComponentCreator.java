package bpm.fwr.api.birt;

import java.util.List;

import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.DataPointComponent;
import org.eclipse.birt.chart.model.attribute.DataPointComponentType;
import org.eclipse.birt.chart.model.attribute.DataType;
import org.eclipse.birt.chart.model.attribute.Direction;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointComponentImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaNumberFormatSpecifierImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.BaseSampleData;
import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.OrthogonalSampleData;
import org.eclipse.birt.chart.model.data.Query;
import org.eclipse.birt.chart.model.data.SampleData;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.AreaSeries;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.ScatterSeries;
import org.eclipse.birt.chart.model.type.impl.AreaSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.ScatterSeriesImpl;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;

import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.ChartTypes;
import bpm.fwr.api.beans.dataset.Column;

public class ChartComponentCreator implements IComponentCreator<ChartComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, ChartComponent component, String outputFormat) throws Exception {
		if(component.getChartType().getType().equals(ChartTypes.PIE.getType())){
			return createPieChart(designHandle, designFactory, selectedLanguage, component);
		}
		else {
			return createBarChart(designHandle, designFactory, selectedLanguage, component);
		}
	}
	
	private ExtendedItemHandle createPieChart(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, ChartComponent component) throws Exception {
		String chartName = "Chart" + new Object().hashCode();
		ExtendedItemHandle eih = designFactory.newExtendedItem(chartName, "Chart");
		try {
			eih.setHeight(component.getHeight() + "pt");
			eih.setWidth(component.getWidth() + "pt");
			if (component.getDataset() != null && !component.getDataset().equals("")) {
				DataSetHandle temp = designHandle.findDataSet(component.getDataset().getName());
				eih.setDataSet(temp);
			}
			else {
				DataSetHandle temp = designHandle.findDataSet("ds");
				eih.setDataSet(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		eih.setProperty("outputFormat", "PNG");
		PropertyHandle computedSet = eih.getColumnBindings();

		String ord = component.getColumnGroup().getTitle(selectedLanguage);

		ComputedColumn cs1 = StructureFactory.createComputedColumn();
		cs1.setName(ord);
		cs1.setDataType("string");
		cs1.setExpression("dataSetRow[\"" + component.getColumnGroup().getDatasetRowExpr(selectedLanguage) + "\"]");
		computedSet.addItem(cs1);

		List<Column> DetailCol = component.getColumnDetails();

		for (Column abc : DetailCol) {
			ComputedColumn cs2 = StructureFactory.createComputedColumn();
			cs2.setName(abc.getTitle(selectedLanguage));
//			cs2.setDataType("float");
			if (abc.isCalculated()) {
				cs2.setExpression(abc.getExpression());
			}
			else {
				cs2.setExpression("dataSetRow[\"" + abc.getDatasetRowExpr(selectedLanguage) + "\"]");
			}
			computedSet.addItem(cs2);
			break;
		}

		ChartWithoutAxes cwoaPie = ChartWithoutAxesImpl.create();

		cwoaPie.setType(component.getChartType().getType());

		cwoaPie.setSubType("Standard Pie Chart");
		cwoaPie.getTitle().setVisible(true);

		cwoaPie.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
		cwoaPie.getLegend().setDirection(Direction.TOP_BOTTOM_LITERAL);
		cwoaPie.getLegend().setPosition(Position.BELOW_LITERAL);
		cwoaPie.getLegend().setOrientation(Orientation.HORIZONTAL_LITERAL);

		cwoaPie.getTitle().getLabel().getCaption().setValue(component.getChartTitle());

		cwoaPie.getTitle().getOutline().setVisible(false);

		cwoaPie.getBlock().setBounds(BoundsImpl.create(0, 0, component.getWidth(), component.getHeight()));
		cwoaPie.getBlock().getOutline().setVisible(false);
		cwoaPie.getBlock().setBackground(ColorDefinitionImpl.create(255, 255, 255));

		cwoaPie.getPlot().getClientArea().setBackground(ColorDefinitionImpl.TRANSPARENT());
		cwoaPie.getLegend().getClientArea().setBackground(ColorDefinitionImpl.TRANSPARENT());

		SampleData sd = DataFactory.eINSTANCE.createSampleData();
		BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData();
		sdBase.setDataSetRepresentation("Category-A, Category-B");
		sd.getBaseSampleData().add(sdBase);

		OrthogonalSampleData sdOrthogonal = DataFactory.eINSTANCE.createOrthogonalSampleData();
		sdOrthogonal.setDataSetRepresentation("4,12");
		sdOrthogonal.setSeriesDefinitionIndex(0);
		sd.getOrthogonalSampleData().add(sdOrthogonal);

		cwoaPie.setSampleData(sd);

		Series seCategory = SeriesImpl.create();

		Query query = QueryImpl.create("row[\"" + component.getColumnGroup().getTitle(selectedLanguage) + "\"]");
		query.setDefinition("row[\"" + component.getColumnGroup().getTitle(selectedLanguage) + "\"]");
		seCategory.getDataDefinition().add(query);

		SeriesDefinition series = SeriesDefinitionImpl.create();
		series.getSeries().add(seCategory);

		series.getGrouping().setEnabled(true);
		series.getGrouping().setGroupType(DataType.TEXT_LITERAL);

		series.getGrouping().setAggregateExpression(component.getChartOperation());

		series.getGrouping().setGroupingInterval(0);

		cwoaPie.getSeriesDefinitions().add(series);

		PieSeries ps = (PieSeries) PieSeriesImpl.create();
		Query query2;

		query2 = QueryImpl.create("row[\"" + component.getColumnDetails().get(0).getTitle(selectedLanguage) + "\"]");
		query2.setDefinition("row[\"" + component.getColumnDetails().get(0).getTitle(selectedLanguage) + "\"]");
		ps.getDataDefinition().add(query2);

		SeriesDefinition seGroup = SeriesDefinitionImpl.create();
		series.getSeriesPalette().shift(0);
		series.getSeriesDefinitions().add(seGroup);
		seGroup.getSeries().add(ps);
		DataPointComponent dpc = DataPointComponentImpl.create(DataPointComponentType.PERCENTILE_ORTHOGONAL_VALUE_LITERAL, JavaNumberFormatSpecifierImpl.create("##.##%"));

		ps.setLabelPosition(Position.OUTSIDE_LITERAL);
		ps.setLeaderLineStyle(LeaderLineStyle.FIXED_LENGTH_LITERAL);
		ps.setLeaderLineLength(20.0);
		ps.setExplosion(5);
		ps.setRatio(0.8);

		ps.getDataPoint().getComponents().clear();
		ps.getDataPoint().getComponents().add(dpc);
		ps.getLabel().setVisible(true);

		try {
			// Add chart instance to IReportItem
			eih.getReportItem().setProperty("chart.instance", cwoaPie);
		} catch (ExtendedElementException e) {
			e.printStackTrace();
		}

		return eih;
	}
	
	private ExtendedItemHandle createBarChart(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, ChartComponent component) {
		String chartName = "Chart" + new Object().hashCode();
		ExtendedItemHandle eih = designFactory.newExtendedItem(chartName, "Chart");

		String ord = component.getColumnGroup().getTitle(selectedLanguage);
		String operation = component.getChartOperation();
		String type = component.getChartType().getType();
		String title = component.getChartTitle();

		List<Column> DetailCol = component.getColumnDetails();
		try {
			eih.setHeight(component.getHeight() + "pt");
			eih.setWidth(component.getWidth() + "pt");
			if (component.getDataset() != null && !component.getDataset().equals("")) {
				DataSetHandle temp = designHandle.findDataSet(component.getDataset().getName());
				eih.setDataSet(temp);
			}
			else {
				DataSetHandle temp = designHandle.findDataSet("ds");
				eih.setDataSet(temp);
			}
			eih.setProperty("outputFormat", "PNG");

			PropertyHandle cs = eih.getColumnBindings();
			ComputedColumn cs1 = StructureFactory.createComputedColumn();
			cs1.setName(ord);
			cs1.setDataType("string");
			cs1.setExpression("dataSetRow[\"" + component.getColumnGroup().getDatasetRowExpr(selectedLanguage) + "\"]");
			cs.addItem(cs1);

			for (Column abc : DetailCol) {
				ComputedColumn cs2 = StructureFactory.createComputedColumn();
				cs2.setName(abc.getTitle(selectedLanguage));
//				cs2.setDataType("float");
				if (abc.isCalculated()) {
					cs2.setExpression(abc.getExpression() != null ? abc.getExpression() : "dataSetRow[\"" + abc.getDatasetRowExpr(selectedLanguage) + "\"]");
				}
				else {
					cs2.setExpression("dataSetRow[\"" + abc.getDatasetRowExpr(selectedLanguage) + "\"]");
				}
				cs.addItem(cs2);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// BAR CHARTS ARE BASED ON CHARTS THAT CONTAIN AXES

		ChartWithAxes cwaBar = ChartWithAxesImpl.create();
		cwaBar.setType("Bar Chart"); 
		cwaBar.setSubType("Side-by-side");
		cwaBar.getBlock().setBounds(BoundsImpl.create(0, 0, component.getWidth() - 50, component.getHeight()));

		SampleData sd = DataFactory.eINSTANCE.createSampleData();
		BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData();
		sdBase.setDataSetRepresentation("Category-A, Category-B");
		sd.getBaseSampleData().add(sdBase);

		OrthogonalSampleData sdOrthogonal = DataFactory.eINSTANCE.createOrthogonalSampleData();
		sdOrthogonal.setDataSetRepresentation("4, 12");
		sdOrthogonal.setSeriesDefinitionIndex(0);
		sd.getOrthogonalSampleData().add(sdOrthogonal);
		cwaBar.setSampleData(sd);

		cwaBar.getBlock().setBackground(ColorDefinitionImpl.create(255, 255, 255));
		cwaBar.getBlock().getOutline().setVisible(false);

		if (type.equalsIgnoreCase(ChartTypes.BAR.getType()) || type.equalsIgnoreCase(ChartTypes.AREA.getType())) {
			cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
		}

		// CUSTOMIZE THE PLOT

		Plot p = cwaBar.getPlot();
		p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 255));

		p.getOutline().setVisible(false);
		cwaBar.getTitle().getLabel().getCaption().setValue(title);

		// CUSTOMIZE THE LEGEND

		Legend lg = cwaBar.getLegend();
		lg.getText().getFont().setSize(12);
		lg.getInsets().set(10, 5, 0, 0);
		lg.setAnchor(Anchor.SOUTH_LITERAL);
		lg.setOrientation(Orientation.VERTICAL_LITERAL);
		lg.setPosition(Position.BELOW_LITERAL);

		// CUSTOMIZE THE X-AXIS

		Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
		xAxisPrimary.setType(AxisType.TEXT_LITERAL);
		xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
		xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
		xAxisPrimary.getLabel().getCaption().getFont().setRotation(45);
		xAxisPrimary.getTitle().setVisible(false);

		// CUSTOMIZE THE Y-AXIS

		Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
		yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);

		// CREATE THE CATEGORY BASE SERIES

		Series seCategory = SeriesImpl.create();
		Query xQ = QueryImpl.create("row[\"" + ord + "\"]");
		xQ.setDefinition("row[\"" + ord + "\"]");
		seCategory.getDataDefinition().add(xQ);

		// CREATE THE VALUE ORTHOGONAL SERIES
		int i = 0;
		for (Column abc : DetailCol) {
			Series bs1 = null;

			if (type.equalsIgnoreCase(ChartTypes.BAR.getType())) {
				bs1 = (BarSeries) BarSeriesImpl.create();
				bs1.setSeriesIdentifier(abc.getTitle(component.getLanguage()));
				Query yQ = QueryImpl.create("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				yQ.setDefinition("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				bs1.getDataDefinition().add(yQ);
				((BarSeries) bs1).setRiserOutline(null);
				bs1.getLabel().setVisible(true);
				bs1.setLabelPosition(Position.OUTSIDE_LITERAL);
				bs1.getLabel().getCaption().getFont().setRotation(45);
			}
			else if (type.equalsIgnoreCase(ChartTypes.LINE.getType())) {
				bs1 = (LineSeries) LineSeriesImpl.create();
				bs1.setSeriesIdentifier(abc.getTitle(component.getLanguage()));
				Query yQ = QueryImpl.create("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				yQ.setDefinition("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				bs1.getDataDefinition().add(yQ);

				bs1.getLabel().setVisible(true);
				bs1.getLabel().getCaption().getFont().setRotation(45);
			}
			else if (type.equalsIgnoreCase(ChartTypes.AREA.getType())) {
				bs1 = (AreaSeries) AreaSeriesImpl.create();
				bs1.setSeriesIdentifier(abc.getTitle(component.getLanguage()));
				Query yQ = QueryImpl.create("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				yQ.setDefinition("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				bs1.getDataDefinition().add(yQ);

				bs1.getLabel().setVisible(true);
				bs1.getLabel().getCaption().getFont().setRotation(45);
			}
			else if (type.equalsIgnoreCase(ChartTypes.SCATTER.getType())) {
				bs1 = (ScatterSeries) ScatterSeriesImpl.create();
				bs1.setSeriesIdentifier(abc.getTitle(component.getLanguage()));
				Query yQ = QueryImpl.create("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				yQ.setDefinition("row[\"" + abc.getTitle(selectedLanguage) + "\"]");
				bs1.getDataDefinition().add(yQ);

				bs1.getLabel().setVisible(true);
				bs1.getLabel().getCaption().getFont().setRotation(45);

				((ScatterSeries) bs1).getMarkers().get(0).setType(MarkerType.FOUR_DIAMONDS_LITERAL);
			}
			SeriesDefinition sdY = SeriesDefinitionImpl.create();
			sdY.getSeriesPalette().shift(i); // SET THE COLOR IN THE PALETTE
			yAxisPrimary.getSeriesDefinitions().add(sdY);
			sdY.getSeries().add(bs1);
			i++;
		}

		// WRAP THE BASE SERIES IN THE X-AXIS SERIES DEFINITION

		SeriesDefinition sdX = SeriesDefinitionImpl.create();

		// Les 2 lignes suivantes sont � commenter avec une source SQL
		sdX.getGrouping().setEnabled(true);
		sdX.getGrouping().setAggregateExpression(operation);

		sdX.getGrouping().setGroupingInterval(0);

		sdX.getSeriesPalette().shift(0);
		xAxisPrimary.getSeriesDefinitions().add(sdX);
		sdX.getSeries().add(seCategory);

		try {
			eih.getReportItem().setProperty("chart.instance", cwaBar);
		} catch (ExtendedElementException e) {
			e.printStackTrace();
		}

		return eih;

	}

}
