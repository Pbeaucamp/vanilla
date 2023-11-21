package bpm.fmloader.client.panel;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.infos.InfosUser;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HeaderInfosPanel extends HorizontalPanel {

	public HeaderInfosPanel() {
		super();
		this.setSize("95%", "80px");
		
		Label lblUser = new Label(Constantes.LBL.selectedUser() + " : " + InfosUser.getInstance().getUser().getName());
		Label lblGroup = new Label(Constantes.LBL.selectedGroup() + " : " + InfosUser.getInstance().getSelectedGroup().getName());
		Label lblObs = new Label(Constantes.LBL.selectedObservatoire() + " : ");// + InfosUser.getInstance().getSelectedObservatoire().getName());
		Label lblTheme = new Label();//Constantes.LBL.selectedTheme() + " : " + InfosUser.getInstance().getSelectedTheme().getName());
		Label lblPeriod = new Label(Constantes.LBL.selectedPeriode() + " : " + Constantes.periodes.get(InfosUser.getInstance().getSelectedPeriode()));
		
		VerticalPanel userGroupPanel = new VerticalPanel();
		VerticalPanel obsThemePanel = new VerticalPanel();
		VerticalPanel periodPanel = new VerticalPanel();
		
		this.addStyleName("datasPagePanel");
		userGroupPanel.addStyleName("datasPagePanel");
		obsThemePanel.addStyleName("datasPagePanel");
		periodPanel.addStyleName("datasPagePanel");
		
		userGroupPanel.add(lblUser);
		userGroupPanel.add(lblGroup);
		obsThemePanel.add(lblObs);
		obsThemePanel.add(lblTheme);
		periodPanel.add(lblPeriod);
		
		userGroupPanel.setSize(Window.getClientWidth() / 4.2 + "px", "80px");
		obsThemePanel.setSize(Window.getClientWidth() / 4.2 + "px", "80px");
		periodPanel.setSize(Window.getClientWidth() / 4.2 + "px", "80px");
		
		this.add(userGroupPanel);
		this.add(obsThemePanel);
		this.add(periodPanel);
		
		if(InfosUser.getInstance().getSelectedDate() != null) {
			String dateString = InfosUser.getInstance().getDateString();
			Label lblDate = new Label(Constantes.LBL.selectedDate() + " : " + dateString);
			periodPanel.add(lblDate);
		}
	}

	
	
}
