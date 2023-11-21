/*
 * Copyright 2007 BPM-conseil.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bpm.map.viewer.web.client.I18N;

import com.google.gwt.i18n.client.Constants;

/**
 * @author KMO
 *
 */
public interface LabelsConstants extends Constants {
	
	//ere added 
	String Error();
	String Edit();

	String Name();

	String Cancel();
	String Refresh();

	String Author();
	String Description();
	String Delete();
	String Information();

	String NoData();
	String NoResult();

	String Ok();

	
	String LogOut();

	String Copyright();

	String Expend();
	String Collapse();

	//map
	String Map();
	String UnableToLoadMaps();
	String MapName();
	String AddANewMap();
	String UnableToAddAMap();
	String ConfirmDeleteMap();
	String UnableToDeleteMap();
	String DeleteMapSuccessfull();


//ajoute par kmo
	String MapParent();
	String MapType();
	String Polygon();
	String Point();
	String EditMap();
	String Viewer();
	String Designer();
	String MetricName();
	String AxisName();
	String ObservatoryThemes();
	
	String reduce();
	String restore();
	String changeDate();
	String Print();
	String repartition();
	String AxeFilterEdit();
	String FileDataEdit();
	String MapList();
	String NoMap();

	String AvailableIcon();
	String OwnIcon();
	String LayerType();
	String Color();
	String Layers();
	String NoBackMap();
	
	String FromTo();
	String ValueDay();
	String Evolution();
	
	String MapDescription();
	String ExportMap();
	String MapTitle();
	String AddMapToGed();
}
