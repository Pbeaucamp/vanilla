package bpm.birt.fusioncharts.core.xmldata;

public class XmlData {
	
	private static final String ENTETE = "<chart caption='Chart Title' xAxisName='' yAxisName='' numberPrefix='' useRoundEdges='0'>";
	private static final String ENTETE_GLASS = "<chart caption='Chart Title' xAxisName='' yAxisName='' numberPrefix='' useRoundEdges='1'>";
	private static final String ENTETE_PIE = "<chart caption='Chart Title' showPercentageInLabel='1' showLabels='0' showLegend='1'>";
	private static final String ENTETE_RADAR = "<chart caption='Chart Title'>";
	
	//Different type of serie for the preview
	private static final String SERIE1 = "<set label='A' value='6' /> <set label='B' value='4' /> <set label='C' value='12' /> <set label='D' value='8' /> <set label='E' value='10' />";
	private static final String CATEGORIES = "<categories><category  label='A' /><category label='B' /><category label='C' /><category label='D' /><category label='E' /></categories>";
	private static final String DATASET_BEGIN = "<dataset>";
	private static final String DATASET_END = "</dataset>";
	private static final String MS_SERIE1 = "<dataset seriesName='Serie 1' color='F6BD0F'><set value='6' /> <set value='4' /> <set value='12' /> <set value='8' /> <set value='10' /> </dataset>";
	private static final String MS_SERIE2 = "<dataset seriesName='Serie 2' color='AFD8F8'><set value='8' /> <set value='2' /> <set value='1' /> <set value='12' /> <set value='7' /> </dataset>";

	private static final String END = "</chart>";

	public static final String SIMPLE_DATA = ENTETE + SERIE1 + END;
	public static final String SIMPLE_DATA_GLASS = ENTETE_GLASS + SERIE1 + END;
	public static final String SIMPLE_DATA_PIE_DOUGHNUT = ENTETE_PIE + SERIE1 + END;
	
	public static final String MS_DATA_ONE_SERIE = ENTETE + CATEGORIES + MS_SERIE1 + END;
	public static final String MS_DATA_ONE_SERIE_GLASS = ENTETE_GLASS + CATEGORIES + MS_SERIE1 + END;
	
	public static final String MS_DATA_TWO_SERIE = ENTETE + CATEGORIES + MS_SERIE1 + MS_SERIE2 + END;
	public static final String MS_DATA_TWO_SERIE_GLASS = ENTETE_GLASS + CATEGORIES + MS_SERIE1 + MS_SERIE2 + END;
	public static final String MS_DATA_TWO_SERIE_RADAR = ENTETE_RADAR + CATEGORIES + MS_SERIE1 + MS_SERIE2 + END;
	
	public static final String MS_STACKED_DATA_TWO_SERIE = ENTETE + CATEGORIES + DATASET_BEGIN + MS_SERIE1 + MS_SERIE2 + DATASET_END + END;
	public static final String MS_STACKED_DATA_TWO_SERIE_GLASS = ENTETE_GLASS + CATEGORIES + DATASET_BEGIN + MS_SERIE1 + MS_SERIE2 + DATASET_END + END;
}
