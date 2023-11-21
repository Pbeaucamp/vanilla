package groupviewer.figure;

import groupviewer.Messages;
import groupviewer.layouts.SimpleBorder;

import java.util.ArrayList;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import bpm.vanilla.platform.core.beans.Group;

public class GroupFigure extends Item implements ActionListener {

	/**
	 * A Clickable Button with two different State : expand / collapse.
	 * @author admin
	 *
	 */
	class ExpandButton extends Clickable {
		
		private Image expand;
		private Image hide;
		private Label lab = new Label();
		
		/**
		 * Create a new Button with the default state 
		 * true = Expand
		 * false = collapse
		 * 
		 * @param isExpand - the default State.
		 */
		public ExpandButton(boolean isExpand) {
			super();
			ImageData expanddata = new ImageData(getClass()
					.getResourceAsStream("/icons/button_expand.gif")); //$NON-NLS-1$

			ImageData hideData = new ImageData(getClass().getResourceAsStream(
					"/icons/button_hide.gif")); //$NON-NLS-1$

			expand = new Image(null, expanddata);
			hide = new Image(null, hideData);

			init(isExpand);

			setStyle(Clickable.STYLE_TOGGLE);
			setContents(lab);
		}

		private void init(boolean isExpand) {
			if (isExpand){
				lab = new Label(expand);
			}
			else{
				lab = new Label(hide);
			}
		}

		private void changeIco() {
			if (lab.getIcon().equals(expand))
				lab.setIcon(hide);
			else
				lab.setIcon(expand);
		}

		public void setTitle(String title) {
			lab.setText(title);
		}

		@Override
		protected void fireActionPerformed() {
			super.fireActionPerformed();
			changeIco();
		}

	}
	
	private String title;
	private Group group;
	private ExpandButton btn;
	private ArrayList<IFigure> userList = new ArrayList<IFigure>(2);
	private Figure userContainer;
	
	public GroupFigure(Group group) {
		super();
		setBackgroundColor(ColorConstants.white);
		setOpaque(true);
		this.group = group;
		this.title = group.getName();
		
		btn = new ExpandButton(false);	
		add(btn);
		
		setBorder(new SimpleBorder());
		
		setToolTip(initToolTip());
		initContainers();
		initListeners();
		updateTitle();
	}
	/**
	 * Create a new Expandable container
	 */
	private void initContainers() {
		userContainer = new Figure();
		GridLayout userContainerlayout = new GridLayout();
		userContainerlayout.marginHeight = 0;
		userContainerlayout.marginWidth = 0;
		userContainer.setLayoutManager(userContainerlayout);
	}
	/**
	 * Add a listener for the Expand Button.
	 */
	private void initListeners() {
		btn.addActionListener(this);
	}
	/**
	 * Remove Listeners from the figure.
	 */
	public void removeListeners(){	
		btn.removeActionListener(this);
	}
	/**
	 * Create a ToolTip which displays Group Details.
	 * 
	 * @return - the ToolTip created
	 */
	private Figure initToolTip() {
		Figure tip = new Figure();
		tip.setLayoutManager(new GridLayout());
		tip.add(new Label(Messages.Figure_GroupFigure_2));
		tip.add(new Label(group.getName()));
		tip.add(new Label(Messages.Figure_GroupFigure_3));
		tip.add(new Label(group.getCreation().toString()));
		tip.add(new Label(Messages.Figure_GroupFigure_4));
		tip.add(new Label(group.getComment()));
		return tip;
	}
		
	/**
	 * Show / hide Expandable container
	 */
	private void showUsers() {
		if (getChildren().contains(userContainer)) 
			remove(userContainer);
		 else{
			add(userContainer);
		 }
	}

	/**
	 * Add a user figure into the Expandable container
	 * @param user
	 */
	public void addUser(UserFigure user) {
		userList.add(user);
		if (!userContainer.getChildren().contains(user)) {
			userContainer.add(user, new GridData(SWT.FILL, SWT.BEGINNING, true,false));
		}
		updateTitle();
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	private void updateTitle() {
		
		int n = userList.size();
		if (userList.isEmpty())
			 n = 0;
		btn.setTitle(Messages.Figure_GroupFigure_5+n+")");//$NON-NLS-1$
	}

	@Override
	Image getTitleIcon() {
		return null;
	}

	@Override
	String getTitle() {
		return this.title;
	}
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(btn)){
			showUsers();
			//System.out.println("user width : "+userContainer.getPreferredSize().width);
			//System.out.println("this width : "+this.getPreferredSize().width);
		}		
	}
}
