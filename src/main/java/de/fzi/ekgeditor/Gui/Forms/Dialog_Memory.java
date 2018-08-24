/** This Forms shows a dialog to query for some number
 */
package de.fzi.ekgeditor.Gui.Forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Dialog_Memory extends Dialog {

	private Integer result=null;
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_Memory(Shell parent,int style)
	{
		super(parent,style);
	}
	
	/** Anonymous dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 **/
	public Dialog_Memory(Shell parent)
	{
		this (parent, SWT.SHELL_TRIM);
	}

	/** reference to our own dialog window */
	private Shell dialog = null;
	
	private String MemoryInBytesToString(long m)
	{
		long bytes=m;
		long mB=Math.round(Math.floor(((double) bytes/(double) (1024*1024))));
		String res=Long.toString(mB)+" MB, ";
		bytes=bytes-1024*1024*mB;
		
		long kB=Math.round(Math.floor(((double) bytes/(double)1024)));
		res=res+Long.toString(kB)+" kBytes, ";
		bytes=bytes-1024*kB;
		
		res=res+Long.toString(bytes)+" Bytes. ";
		
		return res;
	}
	
	private void createLabel(String text,long value)
	{
		Label l1 = new Label (dialog, SWT.NONE);
		l1.setText(text);
		l1.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label l12 = new Label (dialog, SWT.NONE);
		l12.setText(MemoryInBytesToString(value));
		l12.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
	}
	/** This method displays the dialog.
	 * 
	 * @return ID of pressed button (see SWT-Constants)
	 */
	public Integer open()
	{
		Shell parent = getParent();
		Runtime r=Runtime.getRuntime();
		
		//System.out.println("Speicher: Total "+r.totalMemory()+" Byte, Frei:"+r.freeMemory()+" Byte Maximal:"+r.maxMemory()+" Byte.");
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL|SWT.CENTER);
		dialog.setLayout (new GridLayout (2, false));
		dialog.setText("Speicherinformation");
		
		createLabel("Benutzer Speicher:",r.totalMemory());
		createLabel("Freier Speicher:",r.freeMemory());
		createLabel("Maximal zur Verfügung stehender Speicher:",r.maxMemory());
		
	
		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		ok.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
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
	 * 
	 */
	public void Button_OK_Pressed()
	{
		result=SWT.OK;
		dialog.close ();
	}
}
