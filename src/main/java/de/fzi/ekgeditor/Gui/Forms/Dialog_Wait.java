/** This dialog shows a message that some operation is ongoing */
package de.fzi.ekgeditor.Gui.Forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class Dialog_Wait extends Dialog {

	private int min=0,max=1;
	private String text="";
	private ProgressBar progress=null;
	private Label start,end,current,procent;
	private Button ok=null;
	private Thread t;
	public Shell parent;
	
	/** Save the result of the dialog */
	private Integer result=null;
			
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_Wait(Shell parent,int style)
	{
		super(parent,style);
	}
	
	public Dialog_Wait(Shell parent,int min, int max, String text)
	{
		this (parent,0);
		
		this.max=max;
		this.min=min;
		this.text=text;
		this.t=null;
		this.parent=parent;
	}
	
	
	/** reference to our own dialog window */
	private Shell dialog = null;
	
	/** This method displays the dialog.
	 * 
	 * @return Number of pressed button (see SWT-Constants)
	 */
	public Object open()
	{
		Shell parent = getParent();
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.CENTER);
		dialog.setLayout (new GridLayout (3, true));
		dialog.setText("Bitte warten");
		
		Label l1 = new Label (dialog, SWT.NONE);
		l1.setText("Ihre angeforderte Operation wird ausgeführt.");
		l1.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,3,1));
		
		
		Label lnameH=new Label(dialog,SWT.NONE);
		lnameH.setText(text);
		lnameH.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,3,1));
		
		
		start=new Label(dialog,SWT.NONE);
		start.setText(Integer.toString(min));
		start.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		current=new Label(dialog,SWT.NONE);
		current.setText("");
		//current.setLayoutData(new GridData (SWT.NONE, SWT.CENTER, true, true));
		current.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		
		end=new Label(dialog,SWT.NONE);
		end.setText(Integer.toString(max));
		//end.setLayoutData(new GridData (SWT.NONE, SWT.RIGHT, false, false));
		end.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		
		progress = new ProgressBar(dialog,SWT.HORIZONTAL);
		progress.setMinimum(min);
		progress.setMaximum(max);
		progress.setSelection(min);
		progress.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,3,1));
		
		procent=new Label(dialog,SWT.NONE);
		procent.setText("");
		procent.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,3,1));
		
		Label lname=new Label(dialog,SWT.NONE);
		lname.setText("Bitte haben Sie einen Augenblick Geduld.");
		lname.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,3,1));
		
		ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		ok.setEnabled(false);
		ok.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
			}
		});
		
		Button cancel = new Button(dialog,SWT.PUSH);
		cancel.setText("Abbruch");
		//cancel.setLayoutData(new GridData (SWT.NONE, SWT.RIGHT, true, true));
		cancel.setLayoutData(new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1));
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
		
		if (t!=null)
		{
			t.interrupt();
		}
		
		return result;
	}
	
	public void setProgress(int value)
	{
		if ((progress!=null) & (current!=null))
		{
			if (!progress.isDisposed())
				progress.setSelection(value);
			if (!current.isDisposed())
			{
				current.setText(Integer.toString(value));
			}
			if (!procent.isDisposed())
			{
				double p= ((double)value-(double)min) / (double) (((double) max-(double) min)/(double) 100);
				procent.setText(Double.toString((double)Math.round(p*100)/(double)100)+" %");
			}
		}
	}
	
	public void setProgressSynchronized(final int value)
	{
		Display display=this.parent.getDisplay();
		try
		{
			if (!display.isDisposed())
			{
				display.asyncExec(
						new Runnable()
						{
							public void run()
							{
								Dialog_Wait.this.setProgress(value);
							}
						});
			}
		}
		catch (SWTException ex)
		{

		}
	}
	public void closeSynchronized()
	{
		Display display=this.parent.getDisplay();
		try
		{
			if (!display.isDisposed())
			{
				display.asyncExec(
						new Runnable()
						{
							public void run()
							{
								Dialog_Wait.this.close();
							}
						});
			}
		}
		catch (SWTException ex)
		{

		}
	}
	
	public void setOkButton(boolean enabled)
	{
		if (!ok.isDisposed())
		{
			ok.setEnabled(enabled);
		}
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
	
	/** This method is called if button cancel is pressend.
	 * It sets the return value to SWT.CANCEL and closes the dialog. 
	 * 
	 */
	public void Button_Cancel_Pressed()
	{
		result=SWT.CANCEL;
		dialog.close ();
	}
	
	public void close()
	{
		dialog.close();
	}
}
