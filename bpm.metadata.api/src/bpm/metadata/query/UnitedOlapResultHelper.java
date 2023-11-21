package bpm.metadata.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.tools.Log;
import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.ItemResultCell;
import bpm.united.olap.api.result.impl.ValueResultCell;

public class UnitedOlapResultHelper {

	
	private static List<List> createFmdtResultFromOlapResult(OlapResult res, UnitedOlapQuery query, boolean valueAsString) {
		Date start = new Date();
		Log.getLogger().debug("Start parsing olapResult");
		
		//find the first valueCell
		int iFirst = -1;
		int jFirst = -1;
		int i = 0;
		for(ResultLine line : res.getLines()) {
			if(jFirst == - 1) {
				int j = 0;
				for(ResultCell cell : line.getCells()) {
					if(cell instanceof ItemResultCell) {
						jFirst = j;
						break;
					}
					j++;
				}
			}
			
			if(line.getCells().get(jFirst) instanceof ValueResultCell) {
				iFirst = i;
				break;
			}
			i++;
		}
		
		//find number of measure
		int nbMeasure = 0;
		for(IDataStreamElement elem : query.getSelect()) {
			if(elem.getOrigin().getName().startsWith("[Measures]")) {
				nbMeasure++;
			}
		}
		
		//look for each resultCell
		List<List> table = new ArrayList<List>();
		for(i = iFirst ; i < res.getLines().size() ; i = i + nbMeasure) {
			ResultLine line = res.getLines().get(i);
			for(int j = jFirst ; j < line.getCells().size() ; j++) {
				ValueResultCell cell = (ValueResultCell) line.getCells().get(j);
				if(isValidCell(cell, query)) {
					List<Object> resultLine = new ArrayList<Object>();
					
					IExternalQueryIdentifier identifier = cell.getExternalIdentifier();
					
					for(IDataStreamElement elem : query.getSelect()) {
						String lookedName = elem.getOrigin().getName();
						if(lookedName.startsWith("[Measures]")) {
							if(identifier.getSelectElements().keySet().contains(lookedName)) {
								if (valueAsString){
									resultLine.add(cell.getFormatedValue());	
								}
								else{
									resultLine.add(cell.getValue());
								}
								
							}
							else {
								for(int l = i+1 ; l < i + nbMeasure ; l++) {
									ValueResultCell mesResCell = (ValueResultCell) res.getLines().get(l).getCells().get(j);
									if(mesResCell.getExternalIdentifier().getSelectElements().keySet().contains(lookedName)) {
										
										if (valueAsString){
											resultLine.add(mesResCell.getFormatedValue());
										}
										else{
											resultLine.add(mesResCell.getValue());
										}
										
										
										break;
									}
								}
							}
						}
						else {
							resultLine.add(identifier.getSelectElements().get(lookedName));
						}
					}
					
					table.add(resultLine);
				}
			}
		}
		
		Log.getLogger().debug("Parse Olap result finished in : " + (new Date().getTime() - start.getTime()) + " ms");
		
		return table;
	}
	
	public static List<List<String>> createFmdtResultFromOlapResultAsString(OlapResult res, UnitedOlapQuery query) {
		return (List)createFmdtResultFromOlapResult(res, query, true);
	}
	public static List<List<Object>> createFmdtResultFromOlapResult(OlapResult res, UnitedOlapQuery query) {
		return (List)createFmdtResultFromOlapResult(res, query, false);
		
	}

	private static boolean isValidCell(ValueResultCell cell, UnitedOlapQuery query) {
		for(IDataStreamElement elem : query.getSelect()) {
			if(!elem.getOrigin().getName().startsWith("[Measures]")) {
				if(!cell.getExternalIdentifier().getSelectElements().keySet().contains(elem.getOrigin().getName())) {
					return false;
				}
			}
		}
		return true;
	}

}
