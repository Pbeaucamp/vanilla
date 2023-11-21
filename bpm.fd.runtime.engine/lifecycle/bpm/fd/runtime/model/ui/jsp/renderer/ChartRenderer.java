package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.IComponentRenderer;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentRChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.DrillState;
import bpm.fd.runtime.model.datas.ChartAggregationer;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSerie;
import bpm.fd.runtime.model.datas.ChartJsonHelper;
import bpm.fd.runtime.model.datas.ChartXml;
import bpm.fd.runtime.model.datas.ConvertDataSet;
import bpm.fd.runtime.model.datas.HighChartsGenerator;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class ChartRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentChartDefinition> {
	public String getHTML(Rectangle layout, ComponentChartDefinition chart, DashState state, IResultSet datas, boolean refresh) {
		
		bpm.fd.api.core.model.components.definition.chart.ChartRenderer type =  ((bpm.fd.api.core.model.components.definition.chart.ChartRenderer) chart.getRenderer()) ;
		
		switch (((bpm.fd.api.core.model.components.definition.chart.ChartRenderer) chart.getRenderer()).getRendererStyle()) {
		case bpm.fd.api.core.model.components.definition.chart.ChartRenderer.FUSION_CHART:
			//return generateFusionChart(layout, chart, state, datas, refresh);
			return generateChartJs(layout, chart, state, datas, refresh);
		case bpm.fd.api.core.model.components.definition.chart.ChartRenderer.HIGHCHART:
			return generateHighCharts(layout, chart, state, datas, refresh);
		case bpm.fd.api.core.model.components.definition.chart.ChartRenderer.RCHART:
			return PrintRChart(layout, (ComponentRChartDefinition) chart, state, datas, refresh);
	}


		return null;
	}

	private String generateChartJs(Rectangle layout, ComponentChartDefinition chart, DashState state, IResultSet datas, boolean refresh) {
		StringBuffer buf = new StringBuffer();
		
		String json = "";
		try {
			HashMap<DataAggregation, DataSerie> aggregateData = null;
			try {
				aggregateData = ChartAggregationer.aggregateDatas(state.getDrillState(chart.getName()), chart, datas);
			} catch(Exception e) { }
			if (aggregateData == null) {
				json = "\"\"";
			}
			else {
				json = ChartJsonHelper.getJson(chart, state, aggregateData, refresh);
			}
		} catch(Exception e) {
			e.printStackTrace();
			json = "\"\"";
		}
		
		boolean displayLegend = displayLegend(chart);

		System.out.println("Display legend: " + displayLegend);
		System.out.println(chart.getName() + " json : ");
		System.out.println(json);
		
		if(!refresh) {
			buf.append(getComponentDefinitionDivStart(layout, chart, "overflow:hidden;"));

			int height = displayLegend ? layout.height - 100 : layout.height;
			buf.append("<div style=\"width:" + layout.width + "px !important; height:" + height + "px !important;\">");
			buf.append(getComponentDefinitionCanvas(layout, chart, "overflow:auto;", displayLegend));
			buf.append("</div>");
			
			String legendId = "js-legend-" + new Object().hashCode();
			
	//		buf.append(getComponentDefinitionDivEnd());
			buf.append("<div id=\"" + legendId + "\" class=\"chart-legend\" style=\"width: 100%; height: 100px; overflow: auto;\"></div>\n");
			buf.append("<script type=\"text/javascript\">\n");
			buf.append("    fdObjects[\"" + chart.getName() + "\"]= new FdChart(\"" + chart.getName() + "_canvas\", " + json + ", \"" + legendId + "\")" + ";\n");
			buf.append("</script>\n");
			buf.append(getComponentDefinitionDivEnd());
		}
		else {
			return json;
		}
		return buf.toString();
	}

	private boolean displayLegend(ComponentChartDefinition chart) {
		for (IComponentOptions option : chart.getOptions()) {
			if (option instanceof GenericOptions) {
				return ((GenericOptions) option).isDynamicLegend();
			}
		}
		return false;
	}

	private String PrintRChart(Rectangle layout, ComponentRChartDefinition chart, DashState state, IResultSet datas, boolean refresh) {
		// TODO Auto-generated method stub
		
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, chart, "overflow:auto;"));
		String html = null;
		try{
			IRepositoryContext ctx = state.getDashInstance().getDashBoard().getCtx();
			

			RemoteAdminManager manag =  new RemoteAdminManager(ctx.getVanillaContext().getVanillaUrl(), null, Locale.getDefault());
			
			RemoteVanillaPlatform platform = new RemoteVanillaPlatform(ctx.getVanillaContext());
			
			User user = platform.getVanillaSecurityManager().getUserByLogin(ctx.getVanillaContext().getLogin());
			String sessionId = manag.connect(user);
			ISmartManager manager = new RemoteSmartManager(ctx.getVanillaContext().getVanillaUrl(), sessionId, Locale.getDefault());
			
			
			ArrayList<Dataset> dataSetList = new ArrayList<Dataset>() ;
			dataSetList.add( ConvertDataSet.convertDataset(((ChartData) chart.getDatas()).getDataSet(), state.getDashInstance().getDashBoard().getProject().getDictionary() , platform) ) ; // ((ChartData) chart.getDatas()).getDataSet()
			manager.addDatasetstoR( dataSetList  );
				
			// Chargement du fichier R contenant la fonction
			/*RScriptModel model = new RScriptModel();
			String script =  "library(ggplot2) \n Revenu_Origine=aggregate(HHIncomeMid ~ Gender + Race1 ,data = Nhanes[which(Nhanes$HHIncome != 'NA'),], mean)\nRevenu_Origine$HHIncomeMid=as.integer(Revenu_Origine$HHIncomeMid)\n ggplot(Revenu_Origine,aes(Race1,HHIncomeMid,fill=Gender)) + geom_bar(stat='identity', position=(position_dodge(width=0.8))) + labs(title='Revenus selon sexe et origine', x='Origine', y='Revenus annuels du foyer') + theme_minimal() + geom_text(aes(label=HHIncomeMid),vjust=-0.5,size=3,position = position_dodge(width=0.8))";//"library(ggplot2)\n ggplot(data="+ dataSetName  +", aes(x="+dataSetName+"$Height, y="+ dataSetName+"$Weight ))+" +"geom_point(aes(color="+dataSetName+"$Gender)"  +") + theme_bw()\n" ;//+"labs(x='Taille (cm)', y='Poids (kg)')\n";
			model.setScript( script );
			model = manager.executeScriptR(model, new ArrayList<Parameter>());
			String result_image[] = model.getOutputFiles() ; 
			html = result_image[0];*/
			 
			// Debug
			String dataSetName = ((ChartData) chart.getDatas()).getDataSet().getName();
			String axeXField = ((ChartData) chart.getDatas()).getAxeXFieldName();
			String axeYField = ((ChartData) chart.getDatas()).getAxeYFieldName();
			String chartType =	 chart.getRChartNature().getNatureName();
			System.out.println("\ndatasetName: "+dataSetName);
			System.out.println("Group Field : "+((ChartData) chart.getDatas()).getGroupFieldName() );
			System.out.println("axeXField: "+axeXField);
			System.out.println("axeYField: "+ axeYField );
			
			GenericNonPieOptions genericNonPieOptions = (GenericNonPieOptions) chart.getOptions(GenericNonPieOptions.class);
			GenericOptions genericOptions = (GenericOptions) chart.getOptions(GenericOptions.class);
			
		
			String xAxixLabel = genericNonPieOptions.getxAxisName() ;
			String yAxisLabel = genericNonPieOptions.getSYAxisName();
			boolean showLabel = genericOptions.isShowLabel() ;
			boolean dynamic   = genericOptions.isDynamicLegend() ;
			boolean separationB = genericOptions.isSeparationBar() ;
			boolean density = genericOptions.isDensity() ;
			int binsNumber = genericOptions.getBins() ; 
			List<String> list = ((ChartData) chart.getDatas()).getSelectedColumns()  ;
			
			if( list != null ){
				
				for(int i=0 ; i<list.size() ; i++ ){
					System.out.println("column selectionné : " + list.get(i) );
				}	
			}
			else{
				 System.out.println("List of column vide ");
			}
			
			
			System.out.println("Chart type: "+ chartType );
			System.out.println("Title: "+ genericOptions.getCaption() );
			System.out.println("xAxixLabel: "+xAxixLabel);
			System.out.println("yAxisLabel: "+ yAxisLabel );
			System.out.println("showLabel: "+showLabel);
			System.out.println("dynamic: "+ dynamic );
			System.out.println("separationBar: "+ separationB );
			System.out.println("density: "+ density );
			System.out.println("Nombre de Bins: "+ binsNumber );
			
			
			System.out.println("Generated script : \n<< " + generateScriptR(chart) +" \n>>" );
			// a SUPP
			
			//Execution deu script r 
			RScriptModel model = new RScriptModel();
			String script = generateScriptR(chart) ; 
			model.setScript( script );
			model = manager.executeScriptR(model, new ArrayList<Parameter>());
			//System.out.println("Log r: " + model.getOutputLog() );
			String result_image[] = model.getOutputFiles() ; 
			html = result_image[0];
			
			/*
			File file = new File("C:/Users/BPM-FIX2/Desktop/imageChart.svg");
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(html.getBytes());
			fos.close();
			System.out.println("\nscript r executé : \n << "+model.getScript()+" \n>>");*/
 
		}catch(Exception ex){
			ex.printStackTrace();
			html = "Unable to generate markdown : <br>" + ex.getMessage();
		}
		if (refresh){
			return html;
		}
		else{
			buf.append(html);
		}
		
		
		buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + chart.getName() + "\"]= new FdMarkdown(\"" + chart.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
		
		//return null;
	}

	/**
	 *	Genere le srcipt R avec les parametres issus du composant Chart 
	 * @param chart
	 * @return
	 */
	public String generateScriptR(ComponentRChartDefinition chart)
	{
		GenericNonPieOptions genericNonPieOptions = (GenericNonPieOptions) chart.getOptions(GenericNonPieOptions.class);
		GenericOptions genericOptions = (GenericOptions) chart.getOptions(GenericOptions.class);
		String dataSetName = ((ChartData) chart.getDatas()).getDataSet().getName();
		String group	  = checkAxisValue( ((ChartData) chart.getDatas()).getGroupFieldName() ) ;
		String axeXField =checkAxisValue ( ((ChartData) chart.getDatas()).getAxeXFieldName() ) ;
		String axeYField =  checkAxisValue ( ((ChartData) chart.getDatas()).getAxeYFieldName() ) ; // (genericNonPieOptions==null)? "NULL":  
		String xAxixLabel =checkAxisLabel( genericNonPieOptions.getxAxisName() , "Titre axe x")  ;
		String yAxisLabel = checkAxisLabel ( genericNonPieOptions.getSYAxisName(), "Titre axe y" );
		String title 	  = checkAxisLabel ( genericOptions.getCaption(), "Titre graphique" ) ;
		boolean showLegend = genericOptions.isShowLabel() ;
		boolean isdynamic   = genericOptions.isDynamicLegend() ;
		boolean separationB = genericOptions.isSeparationBar() ;
		boolean density = genericOptions.isDensity() ;
		int binsNumber = genericOptions.getBins() ;
		String separationBar = ""+ separationB; separationBar = separationBar.toUpperCase() ;
		String densityString = density+"" ; densityString =  densityString.toUpperCase() ;
		String BinsHisto = "NULL";
		if( binsNumber> 0 ){
			BinsHisto=""+binsNumber;
		}
		
		String showLegendString = ""+showLegend ; showLegendString = showLegendString.toUpperCase(); // R logical constants TRUE and FALSE are in upper case ---> need to convert the java's ones in upperCase
		String isdynamicString = ""+isdynamic ; isdynamicString=isdynamicString.toUpperCase() ;
		//isdynamicString = "TRUE";
		String chartType =	 chart.getRChartNature().getNatureName();
		

		/* String script ="\nlibrary(ggplot2) \n library(plotly)\n";
		// script += String.format(" if(\"%s\"==\"Nuage\"){\n graphe<-ggplot(data=%s, aes(x=%s, y=%s )) + geom_point(aes(color=%s))+ theme_bw() \n if(legend==%s){\n  graphe<-graphe + guides(color=FALSE) \n   }\n  }", chartType  , dataSetName,axeXField,axeYField , group,showLegendString);
		
		script += String.format("\n if(\"%s\"==\"Ligne\"){\n  ggplot(data=%s, aes(x=%s, y=%s, group=%s,color=%s )) + geom_line(aes(color=%s),position='identity')+ theme_minimal() + labs(title=\"%s\", x=\"%s\", y=\"%s\") }"  ,chartType, dataSetName,axeXField,axeYField , group,group,group,title,xAxixLabel,yAxisLabel);
		*/
		//script += String.format("\n if(%s){\n ggplotly(graphe)\n  }"  ,isdynamicString);
		//script += String.format("\n else{\n graphe \n}" );
		
		String script ="\nlibrary(ggplot2)\nlibrary(plotly)\n";
		 //script +="\n Revenu_Origine=aggregate(HHIncomeMid ~ Gender + Race1 ,data = Nhanes[which(Nhanes$HHIncome != 'NA'),], mean)\n";
		 //script += String.format("\n if(\"%s\"==\"Ligne\"){\n  graphe <- ggplot(data=Revenu_Origine, aes(x=%s, y=%s, group=%s,color=%s )) + geom_line(aes(color=%s),position='identity')+ theme_minimal()+ coord_cartesian(ylim = c(0, 60000)) \n if(!%s){\n  graphe<-graphe + guides(color=FALSE)() \n   }\n \n}"  ,chartType,axeXField,axeYField , group,group,group,showLegendString); //  + labs(title=\"%s\", x=\"%s\", y=\"%s\") // title,xAxixLabel,yAxisLabel  ,  ne sert a rien car les axes sont choisis en foction des colonnes

		script += String.format("if(\"%s\"==\"Nuage\"){\ngraphe<-ggplot(data=%s, aes(x=%s, y=%s )) + geom_point(aes(color=%s))+ theme_bw() \n if(!%s){\n  graphe<-graphe + guides(color=FALSE) \n   }\n  }", chartType  , dataSetName,axeXField,axeYField , group,showLegendString);
		script += String.format("\nif(\"%s\"==\"Ligne\"){\ngraphe<-ggplot(data=%s, aes(x=%s, y=%s, group=%s,color=%s )) + geom_line(aes(color=%s),position='identity')+ theme_minimal() \n if(!%s){\n  graphe<-graphe + guides(color=FALSE) \n }\n}"  ,chartType, dataSetName,axeXField,axeYField , group,group,group,showLegendString); //  + labs(title=\"%s\", x=\"%s\", y=\"%s\") // title,xAxixLabel,yAxisLabel  ,  ne sert a rien car les axes sont choisis en foction des colonnes sauf si necessiter de modifier
		
		// Bar 
		script += String.format("\nif( \"%s\"== \"Bar_V\" || \"%s\"== \"Bar_H\" ){"  ,chartType ,chartType );
		script += String.format("\nif(!%s){\ngraphe<-ggplot(data =%s, aes (x=%s, y=%s,fill=%s)) + geom_bar(stat='identity') +theme_minimal()+labs(title=\"%s\", x=\"%s\", y=\"%s\") \n} " , separationBar ,  dataSetName,axeXField,axeYField , group,title,xAxixLabel,yAxisLabel);
		script += String.format("\nelse{\ngraphe<-ggplot(data = %s, aes(x=%s, y=%s,fill=%s)) + geom_bar(stat='identity',position=(position_dodge(width=0.8)))+ theme_minimal()   \n}" , dataSetName,axeXField,axeYField , group ); //title,xAxixLabel,yAxisLabel // +labs(title=\"%s\", x=\"%s\", y=\"%s\") 
		script += String.format("\nif(!%s){\n  graphe<-graphe + guides(color=FALSE) \n   }\n  " , showLegendString);
		script += String.format("if(\"%s\"== \"Bar_H\"){\n graphe<-graphe+ coord_flip()  \n}" , chartType ); // 
		script += String.format("\n}" ); // Fin de if bar_

		// Histogramme
		script += String.format("\nif(\"%s\" == \"Histogramme\"){" ,chartType  );
		//script += String.format("\n if (!\"%s\"){ \n" , densityString ); 
		script += String.format("\ngraphe<-ggplot(data=%s,aes(x=%s)) + geom_histogram(aes(y=..density..),colour=\"white\",fill=\"cornflowerblue\",alpha=0.5,bins=%s)\n",dataSetName, axeXField,BinsHisto); // Fin de if bar_
		script += String.format("graphe<-graphe+ geom_line(col=\"orange\",stat=\"density\") + theme_bw()"  ); 
		//script += String.format(" else{\n" );
		//script += String.format(" graphe=ggplot(data=%s,aes(x=%s)) + geom_histogram(aes(y=..density..),colour=\"white\",fill=\"cornflowerblue\",alpha=0.5,bins=30) + \n",dataSetName,axeXField);
		script += String.format(" \n}" ); // Fin de if Histo

		//PIe avec Geom bar malformé
		// Demo
		/*script += String.format("\nif(\"%s\" == \"Pie\"){ \n" ,chartType  );
		script += String.format("df <- data.frame(group = c(\"Male\", \"Female\", \"Child\"), value = c(25, 25, 50)) \n" );
		script += String.format("graphe<- ggplot(df, aes(x=\"\", y=value, fill=group))+geom_bar(width = 1, stat = \"identity\") + coord_polar(\"y\", start=0) \n" );
		script += String.format("\n}" ); // Fin de if Pie
		
		*/
		
		script += String.format("\nif(\"%s\" == \"Pie\"){ \n" ,chartType  );
		script += String.format("library(ggplot2) \n" );
		//ggplot(%s, aes(x=%s, y=%s, fill=%s)) geom_bar(width = 1, stat = "identity")
		script += String.format("graphe<-ggplot(%s, aes(x=\"\", y=%s, fill=%s))+geom_bar(width = 1, stat = \"identity\") + coord_polar(\"y\", start=0) \n" ,dataSetName,axeXField,group  );
		script += String.format("\n}" ); // Fin de if Pie
		
		
		//PIe avec plotly s'ouvre dans une autre  page web car dynamique
		/*script += String.format("\n library(plotly) \n" ); // Fin de if Histo
		script += String.format("\nif(\"%s\" == \"Pie\"){ \n" ,chartType  );
		script += String.format("USPersonalExpenditure <-data.frame(\"Categorie\"=rownames(USPersonalExpenditure), USPersonalExpenditure) \n");
		script += String.format("data <- USPersonalExpenditure[,c('Categorie', 'X1960')]\n" );
		script += String.format("graphe<-plot_ly(data, labels = ~Categorie, values = ~X1960, type = 'pie') \n" );
		script += String.format("}\n" ); // Fin de if Pie
		 */
		
		//Boxplot
		script += String.format("\n if(\"%s\" == \"Bloxplot\"){ \n" ,chartType  );
		script += String.format("graphe<-ggplot( data=%s, aes(x=%s , y=%s) )  \n" , dataSetName ,axeXField,axeYField );
		script += String.format("if( \"%s\" != \"NULL\" ){\n" , group); //
		script += String.format("graphe<-graphe+ geom_boxplot(aes(color=%s) )  \n" , group );
		script += String.format("graphe<-graphe+ facet_grid(.~%s) \n", group );
		script += String.format("}\n" );
		script += String.format("else{\n" ); // Fin de if Boxplot
		script += String.format("graphe<-graphe+ geom_boxplot(aes(color=\"brown\") )  \n"  );
		script += String.format("\n}\n" ); 
		script += String.format("graphe<-graphe+ theme_classic()\n" );
		script += String.format("graphe<-graphe+ labs( x=\"%s\", y=\"%s\") \n",xAxixLabel,yAxisLabel );
		script += String.format("\n}" ); // Fin de if Boxplot
		
		// HeatMap
		script += String.format("\nif(\"%s\" == \"HeatMap\"){ \n" , chartType );
		script += String.format("graphe<- ggplot(data=%s, aes(x=%s,y=%s) )  \n" , dataSetName , axeXField,axeYField );
		script += String.format("graphe<- graphe+ geom_tile( aes(fill=%s) ) + theme_minimal()" ,group );
		script += String.format("\n}" ); // Fin de Heapmap
		
		// TreeMap
		script += String.format("\nif(\"%s\"==\"TreeMap\"){\n" , chartType );
		script += String.format("library(treemapify)\n"  );
		script += String.format("treemap<-treemapify(%s,area=\"%s\",fill=\"%s\", group=\"%s\")\n" ,dataSetName ,axeXField,axeYField,group);//, label=\"NULL\"
		script += String.format("graphe<-ggplotify(treemap)}\n"  );
		
		// TreeMap
		script += String.format("\nif(\"%s\"==\"Correlation\"){\n" , chartType );
		script += String.format("\n"  );
		script += String.format("treemap<-treemapify(%s,area=\"%s\",fill=\"%s\", group=\"%s\")\n" ,dataSetName ,axeXField,axeYField,group);//, label=\"NULL\"
		script += String.format("graphe<-ggplotify(treemap) \n"  );

		
		script += String.format("\n}" ); // Fin de Heapmap	
		
		
		/*// avec geom_treemap
		script += String.format("\nif(\"%s\"==\"TreeMap\"){\n" , chartType );
		script += String.format("nax=na.omit(Nhanes$%s)\n nay=na.omit(Nhanes$%s)\n nay=nay/100\n nay=nay[nay>0]\n nax=nax[1:10]\n nay=nay[1:10]\n",axeXField,axeYField );
		script += String.format("graphe<-ggplot(%s,aes(area=nax,fill=nay, label=nax))+geom_treemap()" , dataSetName   );
		script += String.format("\n}" ); // Fin de Heapmap
		*/
		/*
		// Avec fonction treemap , marche mais erreur : couldn't open the connection marche v
		script += String.format("\nif(\"%s\" == \"TreeMap\"){\n" , chartType );
		script += String.format("library(treemap)\ntrmp=aggregate(ID ~ Gender + Race1 ,Nhanes, length)\n" );
		script += String.format("treemap(trmp,index=c(\"Race1\",\"Gender\"),vSize=\"ID\",type=\"index\", title=\"Répartition du nombre de personnes interrogées\")" );//, dataSetName , axeXField,axeYField,group );
		script += String.format("\n}" ); // Fin de Heapmap
		*/
		/*
		 * 		script += String.format("\n if(\"%s\" == \"TreeMap\"){\n" , chartType );
		script += String.format("library(treemap)\ngraphe<- treemap(%s, index=c(\"%s\",\"%s\") ,vSize=\"%s\" , type=\"index\", title=\"Titre Treemap\"  )\n" );//, dataSetName , axeXField,axeYField,group );
		script += String.format("\n}" ); // Fin de Heapmap
		 */
		
		// Affichage du graphe
		script += String.format("\n if(%s){\n ggplotly(graphe)\n  }"  ,isdynamicString);
		script += String.format("\n else{\ngraphe\n}" );
		//script+= String.format(" chartR(donnees=%s, \"%s\", axe_X=%s$%s, axe_Y=%s$%s, dynamique=%s, groupe=%s$%s, titre=\"%s\", lab_X=\"%s\", lab_Y=\"%s\",legende=%s)",dataSetName,chartType,dataSetName,axeXField,dataSetName,axeYField,isdynamicString,dataSetName,group,title,xAxixLabel,yAxisLabel,showLegendString )  ;
		return script;
	}
	
	/*
	 * Axis value shoulb be Null or contains a string
	 * It can't be empty 
	 */
	public String checkAxisValue( String axe){
		if( axe != null && axe.isEmpty() ){
			axe="NULL";
			return axe;
		}
		if( axe==null) return "NULL";
		return axe;
	}
	public String checkAxisLabel( String enteredUser, String defaultValue ){
		if(  enteredUser.isEmpty() ){
			return defaultValue;
		}
		return enteredUser;
	}
	
	private String generateHighCharts(Rectangle layout, ComponentChartDefinition chart, DashState state, IResultSet datas, boolean refresh) {
		try {
			StringBuffer buf = new StringBuffer();
			if(! refresh) {
				
				buf.append(getComponentDefinitionDivStart(layout, chart));
				
				buf.append(getComponentDefinitionDivEnd());
				
				buf.append("\n<script type=\"text/javascript\">\n");
				buf.append("$('#"+chart.getId()+"').highcharts({");
				buf.append(new HighChartsGenerator(chart, state).getXml(ChartAggregationer.aggregateDatas(state.getDrillState(chart.getName()), chart, datas)));
				buf.append("});");
				buf.append("</script>\n");
			}
			else {
				buf.append("\n<script type=\"text/javascript\">\n");
				buf.append("$('#"+chart.getId()+"').highcharts({");
				buf.append(new HighChartsGenerator(chart, state).getXml(ChartAggregationer.aggregateDatas(state.getDrillState(chart.getName()), chart, datas)));
				buf.append("});");
				buf.append("</script>\n");	
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getJavaScriptFdObjectVariable(ComponentChartDefinition chart) {
		switch (((bpm.fd.api.core.model.components.definition.chart.ChartRenderer) chart.getRenderer()).getRendererStyle()) {
		case bpm.fd.api.core.model.components.definition.chart.ChartRenderer.FUSION_CHART:

			String jsVar = "chart_" + chart.getId();
			StringBuffer buf = new StringBuffer();
			buf.append("\n<script type=\"text/javascript\">\n");
			buf.append("     var " + jsVar + " = new FusionCharts(\"../../FusionCharts/" + ChartRenderer.getSwf(chart.getNature(), chart.getRenderer()) + "\", \"" + chart.getId() + "_fs\", \"" + chart.getWidth() + "\", \"" + chart.getHeight() + "\", \"0\", \"0\");\n");

			buf.append("     " + jsVar + ".setTransparent(true);\n");
			//XXX
			buf.append("     " + jsVar + ".configure(\"ChartNoDataText\",\"Votre sélection n'affiche aucun résultat\");\n");
			buf.append("     " + jsVar + ".configure(\"InvalidXMLText\",\"Veuillez cliquer sur le graphique précédent pour le mois voulu pour afficher les données\");\n");

			// buf.append(jsVar + ".render(\"" + chart.getName() + "\");\n");

			buf.append("     " + jsVar + ".configureLink ({swfUrl : \"../../FusionCharts/" + ChartRenderer.getSwf(chart.getNature(), chart.getRenderer()) + "\",overlayButton:{message: 'close',fontColor : '880000',bgColor:'FFEEEE',borderColor: '660000'}}, 0);\n");

			buf.append("     fdObjects[\"" + chart.getId() + "\"]=new FdChart(" + jsVar + ", '" + chart.getName() + "', \"\");");
			buf.append("</script>\n");
			return buf.toString();
		case bpm.fd.api.core.model.components.definition.chart.ChartRenderer.HIGHCHART:

			String jsVar2 = "chart_" + chart.getId();
			StringBuffer buf2 = new StringBuffer();
			buf2.append("\n<script type=\"text/javascript\">\n");
			
			buf2.append("     fdObjects[\"" + chart.getId() + "\"]=new FdHighChart(" + jsVar2 + ", '" + chart.getName() + "', \"\");");
			
			buf2.append("</script>\n");

			return buf2.toString();
		}
		return null;
	}

	private String generateFusionChart(Rectangle layout, ComponentChartDefinition chart, DashState state, IResultSet datas, boolean refresh) {

		Logger.getLogger(getClass()).debug("Generate fusionChart for : " + chart.getName());
		
		String xml = null;
		try {
			xml = new ChartXml(chart, state).getXml(ChartAggregationer.aggregateDatas(state.getDrillState(chart.getName()), chart, datas));
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer buf = new StringBuffer();
		String jsVar = "chart_" + chart.getId();
		if (!refresh) {

			StringBuffer attributes = new StringBuffer();
			if (chart.getCssClass() != null && !chart.getClass().equals("null") && !chart.getCssClass().isEmpty()) {
				attributes.append(" class='" + chart.getCssClass() + "' ");
			}

			for (ElementsEventType type : chart.getEventsType()) {
				if (chart.getJavaScript(type) != null) {
					attributes.append(type.name() + "='" + chart.getJavaScript(type) + "' ");
				}
			}

			buf.append(getComponentDefinitionDivStart(layout, chart));
			buf.append(getComponentDefinitionDivEnd());
			boolean useDrillUp = false;
			// we add a div to control the drillUp
			if (chart.getDrillDatas().getTypeTarget() == TypeTarget.Dimension) {
				// buf.append("$(\"#" + chart.getName() +
				// "\").append('<div id=\"" + chart.getName() + "_button" +
				// "\" >hello world</div>');\n");
				//				
				// buf.append("$(\"#" + chart.getName() +
				// "_button\").click(function(){drillUp(\"" + chart.getName() +
				// "\");});\n");

				Rectangle r = new Rectangle(layout);
				r.height = 33;
				// r.width = 32;
				// r.x = 0;
				r.y = r.y - r.height;

				buf.append("<div id='" + chart.getName() + "_button' class='breadcrumb' " + getLayoutStyleCss(r, null) + ">\n");
				try {
					buf.append(generateArianeHtml(chart.getName(), state.getDrillState(chart.getName())));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				buf.append("</div>\n");
				useDrillUp = true;

			}
			buf.append("\n<script type=\"text/javascript\">\n");
			buf.append("     var " + jsVar + " = new FusionCharts(\"../../FusionCharts/" + getSwf(chart.getNature(), chart.getRenderer()) + "\", \"" + chart.getId() + "_fs\", \"");

			if (layout != null) {
				buf.append(layout.width - 20 + "\", \"" + layout.height + "\", \"0\", \"0\");\n");
			} else {
				buf.append(chart.getWidth() - 20 + "\", \"" + chart.getHeight() + "\", \"0\", \"0\");\n");
			}

			buf.append("     " + jsVar + ".setTransparent(true);\n");
			//XXX
			buf.append("     " + jsVar + ".configure(\"ChartNoDataText\",\"Votre sélection n'affiche aucun résultat\");\n");
			buf.append("     " + jsVar + ".configure(\"InvalidXMLText\",\"Veuillez cliquer sur le graphique précédent pour le mois voulu pour afficher les données\");\n");
			buf.append(jsVar + ".render(\"" + chart.getName() + "\");\n");

			buf.append("     fdObjects[\"" + chart.getId() + "\"]=new FdChart(" + jsVar + ", '" + chart.getName() + "', \"" + xml + "\",");
			if (layout != null) {
				buf.append(layout.width - 20 + ", " + layout.height + ");\n");
			} else {
				buf.append(chart.getWidth() - 20 + ", " + chart.getHeight() + ");\n");
			}
			if (useDrillUp) {
				// buf.append("$(\"#" + chart.getName() +
				// "_button\").click(function(){drillUp(\"" + chart.getName() +
				// "\");});\n");
				// buf.append("$(\"#" + chart.getName() +
				// "_button\").css('z-index','1');\n");
			}

			buf.append("</script>\n");
		} else {
			buf.append(xml);
		}

		return buf.toString();
	}

	private static String convertColorToHex(Color col) {
		String c = "";
		String t = Integer.toHexString(col.getRed());
		if (t.length() == 1) {
			t = "0" + t;
		}
		c = c + t;

		t = Integer.toHexString(col.getGreen());
		if (t.length() == 1) {
			t = "0" + t;
		}
		c = c + t;

		t = Integer.toHexString(col.getBlue());
		if (t.length() == 1) {
			t = "0" + t;
		}
		c = c + t;
		return c;
	}

	private static String generateDataFilterString(IChartData datas) {
		StringBuffer buf = new StringBuffer();

		boolean firstAgg = true;
		for (DataAggregation a : datas.getAggregation()) {
			if (firstAgg) {
				firstAgg = false;
			} else {
				buf.append(";");
			}

			boolean firstFilter = true;
			for (IComponentDataFilter f : a.getFilter()) {
				if (firstFilter) {
					firstFilter = false;
				} else {
					buf.append("+");
				}
				buf.append(f.toString());
			}
			// ensure that we are empty
			if (firstFilter) {
				firstAgg = true;
			}
		}

		return buf.toString();
	}

	public static String getSwf(ChartNature nature, IComponentRenderer renderer) {
		switch (nature.getNature()) {
		case ChartNature.BAR:
			return "Bar2D.swf";
		case ChartNature.MSBAR2D:
			return "MsBar2D.swf";
		case ChartNature.HEAT_MAP:
			return "heatmap.swf";
		case ChartNature.COLUMN:
			return "Column2D.swf";
		case ChartNature.COLUMN_3D:
			return "Column3D.swf";
		case ChartNature.FUNNEL:
			return "Funnel.swf";
		case ChartNature.GAUGE:
			return "AngularGauge.swf";
		case ChartNature.LINE:
//			return "Line.swf";
			return "Spline.swf";
		case ChartNature.PIE:
			return "Pie2D.swf";
		case ChartNature.PIE_3D:
			return "Pie3D.swf";
		case ChartNature.PYRAMID:
			return "Pyramid.swf";
		case ChartNature.RADAR:
			return "Radar.swf";
		case ChartNature.STACKED_BAR:
			return "StackedBar2D.swf";
		case ChartNature.STACKED_BAR_3D:
			return "StackedBar3D.swf";
		case ChartNature.STACKED_COLUMN:
			return "StackedColumn2D.swf";
		case ChartNature.STACKED_COLUMN_3D:
			return "StackedColumn3D.swf";
		case ChartNature.COLUMN_3D_MULTI:
			return "MSColumn3D.swf";
		case ChartNature.COLUMN_MULTI:
			return "MSColumn2D.swf";
		case ChartNature.LINE_MULTI:
			return "MSLine.swf";
		case ChartNature.COLUMN_3D_LINE:
			return "MSColumnLine3D.swf";
		case ChartNature.COLUMN_3D_LINE_DUAL:
			return "MSColumn3DLineDY.swf";
		case ChartNature.COLUMN_LINE_DUAL:
			return "MSCombiDY2D.swf";
		case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:
			return "StackedColumn3DLineDY.swf";
		case ChartNature.BAR_3D:
			return "MSBar3D.swf";
		case ChartNature.STACKED_AREA_2D:
			return "MSArea.swf";
		case ChartNature.MARIMEKO:
			return "Marimekko.swf";
		case ChartNature.SINGLE_Y_2D_COMBINATION:
			return "MSCombi2D.swf";
		case ChartNature.SINGLE_Y_3D_COMBINATION:
			return "MSCombi3D.swf";
		case ChartNature.STACKED_2D_LINE_DUAL_Y:
			return "StackedColumn2DLine.swf";
		case ChartNature.DUAL_Y_2D_COMBINATION:
			return "MSCombiDY2D.swf";
		case ChartNature.SPARK:
			return "sparkcolumn.swf";
		case ChartNature.SCATTER:
			return "scatter.swf";
		case ChartNature.BOX:
			return "boxandwhisker2d.swf";
		}
		return "";
	}

	/**
	 * generate html for chart using dimension
	 * 
	 * @param componentName
	 * @param state
	 * @return
	 */
	public static String generateArianeHtml(String componentName, DrillState state) {
		StringBuffer buf = new StringBuffer();

		buf.append("<ul>");
		for (int i = -1; i < state.getCurrentLevel(); i++) {
			if (i == -1) {
				buf.append("<li class=\"first\" >");
			} else {
				buf.append("<li>");
			}
			if (i + 1 == state.getCurrentLevel()) {
				buf.append("<span title=\"Current Level\">" + state.getLevelValue(i) + "</span><span class=\"end\" ></span>\n");
			} else {
				buf.append("<a href=\"Javascript:drillUp('" + componentName + "', " + i + ");\" title=\"Drill Up to level " + (i + 1) + "\">" + state.getLevelValue(i) + "</a><span class=\"end\" ></span>\n");
			}

			buf.append("</li>");
		}

		buf.append("</ul>");

		return buf.toString();

	}
	

	
	
	
}
