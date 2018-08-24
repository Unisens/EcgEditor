/**
 * This class represents the program font settings dialog.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.Gui.Forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.FontsManager;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.events.SettingsEvent;

public class Dialog_Settings_Fonts extends Dialog {

	/** Save the result of the dialog */
	private Object result=null;
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_Settings_Fonts(Shell parent,int style)
	{
		super(parent,style);
	}
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 **/
	public Dialog_Settings_Fonts(Shell parent){
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
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL|SWT.CENTER);
		dialog.setLayout (new GridLayout (2, false));
		dialog.setText("Schriften");
		dialog.setImage(Common.getInstance().im.get(ImageManager.ico_cut));
				
		Group gFonts = new Group(dialog,SWT.SHADOW_IN);
		gFonts.setText("Schriften");
		gFonts.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		gFonts.setLayout(new GridLayout (4, false));
		
		for (int currentFont=0;currentFont<FontsManager.maxFonts;currentFont++)
		{
			String fontUseName=FontsManager.FontNames[currentFont];
			
			Button button_color_channel = new Button(gFonts,SWT.PUSH);
			button_color_channel.setText(fontUseName);
			button_color_channel.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
			
			final int tempI=currentFont;
			button_color_channel.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					button_font_Clicked(tempI);
				}
			});

			//this.myFont.add(Common.getInstance().fontsManager.getSystemFont(currentFont).getFontData());			
		}
				
		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		ok.setLayoutData(new GridData (SWT.FILL, SWT.LEFT,true,true));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
			}
		});
		
		/*Button cancel = new Button(dialog,SWT.PUSH);
		cancel.setText("Abbruch");
		cancel.setLayoutData(new GridData (SWT.FILL, SWT.RIGHT, false, false));
		cancel.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_Cancel_Pressed();
			}
		});*/
		
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
	
	/** This method is called if the font button is clicked
	 * 
	 * @param fontNumber ID of the System-font to change.
	 */
	public void button_font_Clicked(int fontNumber)
	{
		FontDialog fd = new FontDialog(this.dialog);
		fd.setFontList(Common.getInstance().fontsManager.getSystemFont(fontNumber).getFontData());
				//myFont.get(fontNumber));
		Object res=fd.open();
		if (res!=null)
		{
			FontData[] fontdat=new FontData[1];
			fontdat[0]=(FontData)res;

			Font newFont = new Font(null,fontdat);
			Common.getInstance().fontsManager.setSystemFont(fontNumber, newFont);
			String RegistryKey=Registry.prop_font+Integer.toString(fontNumber);
			Common.getInstance().reg.saveFontToRegistry(newFont,RegistryKey);

			Common.getInstance().reg.save();
			Common.getInstance().notifySettingsChanged(new SettingsEvent(this));
			result=res;
		}
	}
}
