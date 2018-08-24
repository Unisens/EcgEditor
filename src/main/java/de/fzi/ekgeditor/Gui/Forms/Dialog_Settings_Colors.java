/**
 * This class represents the program color settings dialog.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.Gui.Forms;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerColors;
import de.fzi.ekgeditor.data.Colors;
import de.fzi.ekgeditor.data.ImageManager;

public class Dialog_Settings_Colors extends Dialog {

	/** Save the result of the dialog */
	private Object result;
	/** List for all channel-colors */
	private ArrayList<Canvas> cColor=new ArrayList<Canvas>();
	/** List for all system colors */
	private ArrayList<Canvas> sColor=new ArrayList<Canvas>();
	
	/** List for all current selected channelColors */ 
	public ArrayList<Color> myColor=new ArrayList<Color>();
	/** List for all current selected systemColors */
	public ArrayList<Color> mySystemColor=new ArrayList<Color>();
	
	private final int canvasWidth=30;
	private final int canvasHeight=30;
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_Settings_Colors(Shell parent,int style)
	{
		super(parent,style);
	}
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 **/
	public Dialog_Settings_Colors(Shell parent){
		this (parent, SWT.SHELL_TRIM);
	}
	
	/** reference to our own dialog window */
	private Shell dialog = null;
	
	/** This method displays the dialog.
	 * 
	 * @return ID of pressed button (see SWT-Constants)
	 */
	public Object open()
	{
		Shell parent = getParent();
		Common common=Common.getInstance();
		SignalViewerColors signalViewerColors = common.signalViewerColors;
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL|SWT.CENTER);
		dialog.setLayout (new GridLayout (2, false));
		dialog.setText("Farbumgebung");
		dialog.setImage(Common.getInstance().im.get(ImageManager.ico_cut));

		
		Label l1 = new Label (dialog, SWT.NONE);
		l1.setText("In diesem Dialog finden Sie alle Farbeinstellungen.");
		l1.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true));
				
		Group gColor = new Group(dialog,SWT.SHADOW_IN);
		gColor.setText("Farben");
		gColor.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,false,false,2,1));
		gColor.setLayout(new GridLayout (8, false));
		
		for (int currentChannel=0;currentChannel<SignalViewerColors.maxChannelColors;currentChannel++)
		{
			String channelName="Kanal "+Integer.toString(currentChannel);
			
			Button button_color_channel = new Button(gColor,SWT.PUSH);
			button_color_channel.setText(channelName);
			button_color_channel.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
			
			final int tempI=currentChannel;
			button_color_channel.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					button_color_channel_Clicked(tempI);
				}
			});
			
			
			Canvas lColor=new Canvas(gColor,SWT.NONE);
			//lColor.setLayoutData(new GridData(SWT.None,SWT.RIGHT,false,false,1,1));
			lColor.setLayoutData(new GridData(canvasWidth,canvasHeight));
			lColor.setToolTipText("Aktuelle Farbe von "+channelName);
			lColor.setSize(5, 5);
			myColor.add(Common.getInstance().signalViewerColors.getChannelColor(currentChannel));
			lColor.setBackground(myColor.get(currentChannel));
			cColor.add(lColor);
			
		}
		
		for (int signalViewColorIndex=0;signalViewColorIndex<signalViewerColors.getSignalViewerColorCount();signalViewColorIndex++)
		{
			String signalViewerColorName=SignalViewerColors.SignalViewerColorNames[signalViewColorIndex];
			
			Button button_signalViewerColor = new Button(gColor,SWT.PUSH);
			button_signalViewerColor.setText(signalViewerColorName);
			button_signalViewerColor.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
			
			final int tempI=signalViewColorIndex;
			button_signalViewerColor.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					button_color_system_Clicked(tempI);
				}
			});
			
			
			Canvas lColor=new Canvas(gColor,SWT.NONE);
			//lColor.setLayoutData(new GridData(SWT.NONE,SWT.NONE,false,false,1,1));
			lColor.setLayoutData(new GridData(canvasWidth,canvasHeight));
			lColor.setToolTipText("Aktuelle Farbe von "+signalViewerColorName);
			lColor.setSize(300, 300);
			mySystemColor.add(Common.getInstance().signalViewerColors.getSignalViewerColor(signalViewColorIndex));
			lColor.setBackground(mySystemColor.get(signalViewColorIndex));
			sColor.add(lColor);
		}
		
		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		ok.setLayoutData(new GridData (SWT.LEFT, SWT.NONE,false, false,1,1));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
			}
		});
		
		Button cancel = new Button(dialog,SWT.PUSH);
		cancel.setText("Abbruch");
		cancel.setLayoutData(new GridData (SWT.RIGHT,SWT.None, false, false,1,1));
		cancel.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_Cancel_Pressed();
			}
		});
		
		dialog.setDefaultButton (ok);
		dialog.pack ();
		dialog.open();
		
		Display display = parent.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}

	/** This method is called if button OK is pressed.
	 * It sets the return value to SWT.OK and closes the dialog. 
	 */
	public void Button_OK_Pressed()
	{
		result=SWT.OK;
		dialog.close ();
	}
	
	/** This method is called if button cancel is pressend.
	 * It sets the return value to SWT.CANCEL and closes the dialog. 
	 */
	public void Button_Cancel_Pressed()
	{
		result=SWT.CANCEL;
		dialog.close ();
	}
	
	/** This method is called if the color button for channel channel is clicked
	 * 
	 * @param channel The channel for which the color button was clicked
	 */
	public void button_color_channel_Clicked(int channel)
	{
		ColorDialog cd=new ColorDialog(dialog);
		Color tC=myColor.get(channel);
		if (tC==null)
		{
			tC=Colors.Blue;
		}
		cd.setRGB(tC.getRGB());
		RGB rgbValue = cd.open();
		if (rgbValue!=null)
		{
			myColor.set(channel, new Color(null,rgbValue));
			cColor.get(channel).setBackground(myColor.get(channel));
		}
		
	}
	
	/** This method is called if the color button for some system color is clicked
	 * 
	 * @param systemColorID The colorID of the SystemColor for which the color button was clicked
	 */
	public void button_color_system_Clicked(int systemColorID)
	{
		ColorDialog cd=new ColorDialog(dialog);
		Color tC=mySystemColor.get(systemColorID);
		if (tC==null)
		{
			tC=Colors.Blue;
		}
		cd.setRGB(tC.getRGB());
		RGB rgbValue = cd.open();
		if (rgbValue!=null)
		{
			mySystemColor.set(systemColorID, new Color(null,rgbValue));
			sColor.get(systemColorID).setBackground(mySystemColor.get(systemColorID));
		}
		
	}
}
