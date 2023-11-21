package bpm.birt.fusionmaps.ui.generator;

import java.util.List;

import org.eclipse.birt.report.designer.ui.views.attributes.AttributesUtil;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;

public class FusionmapsGeneralPage extends AttributesUtil.PageWrapper{
	protected FormToolkit toolkit;
	protected Object input;
	protected Composite contentpane;
	private Text txtJavascript;
	
	public void buildUI( Composite parent )
	{
		if ( toolkit == null )
		{
			toolkit = new FormToolkit( Display.getCurrent( ) );
			toolkit.setBorderStyle( SWT.NULL );
		}
		
		Control[] children = parent.getChildren( );
		
		if ( children != null && children.length > 0 )
		{
			contentpane = (Composite) children[children.length - 1];
			
			GridLayout layout = new GridLayout( 2, false );
			layout.marginLeft = 8;
			layout.verticalSpacing = 12;
			contentpane.setLayout( layout );
			
			toolkit.createLabel( contentpane, "Text Content test:" ); //$NON-NLS-1$
			txtJavascript = toolkit.createText( contentpane, "" ); //$NON-NLS-1$
			GridData gd = new GridData( );
			gd.widthHint = 200;
			
			txtJavascript.setLayoutData( gd );
			txtJavascript.addFocusListener( new FocusAdapter( ) {
			
				public void focusLost( org.eclipse.swt.events.FocusEvent e )
				{
					updateModel( FusionmapsItem.VANILLA_RUNTIME_URL_PROP );
				};
			} );
			
		}
	}
	
	public void setInput( Object input )
	{
		this.input = input;
	}
	
	public void dispose( )
	{
		if ( toolkit != null )
		{
			toolkit.dispose( );
		}
	}
	
	private void adaptFormStyle( Composite comp )
	{
		Control[] children = comp.getChildren( );
		for ( int i = 0; i < children.length; i++ )
		{
			if ( children[i] instanceof Composite )
			{
				adaptFormStyle( (Composite) children[i] );
			}
		}
		
		toolkit.paintBordersFor( comp );
		toolkit.adapt( comp );
	}
	
	protected FusionmapsItem getItem( )
	{
		Object element = input;
		
		if ( input instanceof List && ( (List) input ).size( ) > 0 )
		{
			element = ( (List) input ).get( 0 );
		}
		
		if ( element instanceof ExtendedItemHandle )
		{
			try
			{
				return (FusionmapsItem) ( (ExtendedItemHandle) element ).getReportItem( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
		
		return null;
	}
	
	public void refresh( )
	{
		if ( contentpane != null && !contentpane.isDisposed( ) )
		{
			if ( toolkit == null )
			{
				toolkit = new FormToolkit( Display.getCurrent( ) );
				toolkit.setBorderStyle( SWT.NULL );
			}
			
			adaptFormStyle( contentpane );
			
			updateUI( );
		}
	}
	
	public void postElementEvent( )
	{
		if ( contentpane != null && !contentpane.isDisposed( ) )
		{
			updateUI( );
		}
	}
	
	private void updateModel( String prop )
	{
		FusionmapsItem item = getItem( );
		
		if ( item != null )
		{
			try
			{
				if ( FusionmapsItem.VANILLA_RUNTIME_URL_PROP.equals( prop ) )
				{
					item.setVanillaRuntimeURL( txtJavascript.getText( ) );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
	}
	
	protected void updateUI( )
	{
		FusionmapsItem item = getItem( );
		
		if ( item != null )
		{
			String text = item.getVanillaRuntimeURL();
			txtJavascript.setText( text == null ? "" : text ); //$NON-NLS-1$
		}
	}

}
