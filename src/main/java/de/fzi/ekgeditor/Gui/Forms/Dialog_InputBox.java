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
import org.eclipse.swt.widgets.Slider;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.utils.TimeUtil;

public class Dialog_InputBox extends Dialog {

	private Integer result=null;
	private Slider s=null;
	private Label currentValue=null;
	private boolean openAsSampleSelect=false;
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_InputBox(Shell parent,int style)
	{
		super(parent,style);
	}
	
	/** Anonymous dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 **/
	public Dialog_InputBox(Shell parent){
		this (parent, SWT.SHELL_TRIM);
	}

	/** reference to our own dialog window */
	private Shell dialog = null;
	
	/** This method displays the dialog.
	 * 
	 * @return ID of pressed button (see SWT-Constants)
	 */
	public Integer open(String Title, String Text,int min,int max, int currentValueI,boolean openAsSampleSelect)
	{
		Shell parent = getParent();
		this.openAsSampleSelect=openAsSampleSelect;
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL|SWT.CENTER);
		dialog.setLayout (new GridLayout (2, false));
		dialog.setText(Title);
		
		Label l1 = new Label (dialog, SWT.NONE);
		l1.setText(Text);
		l1.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		
		s = new Slider(dialog,SWT.HORIZONTAL);
		s.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Scale_Changed();
			}
		});
		s.setMinimum(min);
		s.setMaximum(max);
		s.setPageIncrement(1);
		s.setIncrement(1);
		s.setSelection(currentValueI);
		s.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		
		Label l2 = new Label (dialog, SWT.NONE);
		l2.setText("Aktueller Wert :");
		l2.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		currentValue = new Label (dialog, SWT.NONE);
		//currentValue.setText(Integer.toString(currentValueI));
		setValue(currentValueI);
		currentValue.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		currentValue.setLayoutData(new GridData(500, 30));

		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		ok.setLayoutData(new GridData (SWT.LEFT, SWT.NONE, false, false));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
			}
		});
		
		Button cancel = new Button(dialog,SWT.PUSH);
		cancel.setText("Abbruch");
		cancel.setLayoutData(new GridData (SWT.RIGHT, SWT.NONE,false, false));
		cancel.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_Cancel_Pressed();
			}
		});
		
		dialog.setDefaultButton (ok);
		dialog.pack ();
		//dialog.setSize(300, 300);
		dialog.open();
		
		Display display = parent.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}
	
	private void setValue(long sample)
	{
		String Str;
		if (this.openAsSampleSelect)
		{
			Str=TimeUtil.getFullTimeString(Common.getInstance().signalModel.getTimeInMilliSecsForSample(sample));
		}
		else
		{
			Str=Long.toString(sample);
		}
		if (currentValue!=null)
		{
			currentValue.setText(Str);
		}
	}
	
	public void Scale_Changed()
	{
		long sample=s.getSelection();
		setValue(sample);
	}
	
	/** This method is called if button OK is pressed.
	 * It sets the return value to SWT.OK and closes the dialog. 
	 * 
	 */
	public void Button_OK_Pressed()
	{
		result=s.getSelection();
		dialog.close ();
	}
	
	/** This method is called if button cancel is pressed.
	 * It sets the return value to SWT.CANCEL and closes the dialog. 
	 * 
	 */
	public void Button_Cancel_Pressed()
	{
		result=null;
		dialog.close ();
	}
}
