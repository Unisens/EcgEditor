/** This dialog shows a message that some operation is ongoing */
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

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.TimeUtil;

public class Dialog_PreviewCut extends Dialog {

	private Button ok=null;
	private Thread t;
	
	/** Save the result of the dialog */
	private Integer result=null;
			
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 * @param style window style (should normally be set to dialog
	 **/
	public Dialog_PreviewCut(Shell parent,int style)
	{
		super(parent,style);
	}
	
	/** Standard dialog settings constructor
	 * 
	 * @param parent parent window of this dialog
	 **/
	public Dialog_PreviewCut(Shell parent){
		this (parent,SWT.SHELL_TRIM);
	}
	
	public Dialog_PreviewCut(Shell parent,String text){
		this (parent,0);
	}
	
	
	/** reference to our own dialog window */
	private Shell dialog = null;
	
	/** This method displays the dialog.
	 * 
	 * @return Number of pressed button (see SWT-Constants)
	 */
	
	private void createSampleLabels(String text,long sampleATime, SignalViewerModel model)
	{
		long sampleA=model.signalModel.getSampleForTimeInMilliSeconds(sampleATime);
		double slopeA=model.signalModel.getSlope(sampleA, 0);
		double valueA=model.signalModel.getSample(sampleA, 0);
		
		Label lsample=new Label(dialog,SWT.NONE);
		lsample.setText(text);
		lsample.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lvalueLabel2=new Label(dialog,SWT.NONE);
		lvalueLabel2.setText(TimeUtil.getFullTimeString(sampleATime));
		lvalueLabel2.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lvalueLabel3=new Label(dialog,SWT.NONE);
		lvalueLabel3.setText("#"+Long.toString(sampleA));
		lvalueLabel3.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lvalueLabel=new Label(dialog,SWT.NONE);
		lvalueLabel.setText("Wert:");
		lvalueLabel.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lvalue=new Label(dialog,SWT.NONE);
		lvalue.setText(Common.Double2String(valueA));
		lvalue.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		
		Label lslopeLabel=new Label(dialog,SWT.NONE);
		lslopeLabel.setText("Steigung:");
		lslopeLabel.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lslope=new Label(dialog,SWT.NONE);
		lslope.setText(Common.Double2String(slopeA));
		lslope.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
		
		
	}
	public Integer open(Selection orig,Selection newSel,SignalViewerModel model)
	{
		Shell parent = getParent();
		
		dialog = new Shell (parent,SWT.DIALOG_TRIM|SWT.CENTER);
		dialog.setLayout (new GridLayout (3, true));
		dialog.setText("Automatische Schneideanpassung");
		
		Label l1 = new Label (dialog, SWT.NONE);
		l1.setText("Bitte überprüfen Sie die Ergebnisse der automatischen Schneideanpassung.");
		l1.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,3,1));
		
		createSampleLabels("Schnittzeitpunkt A (original) :",orig.getSelectionStart(),model);
		createSampleLabels("Schnittzeitpunkt B (original) :",orig.getSelectionEnd(),model);
		
		createSampleLabels("Schnittzeitpunkt A (angepasst) :",newSel.getSelectionStart(),model);
		createSampleLabels("Schnittzeitpunkt B (angepasst) :",newSel.getSelectionEnd(),model);
		
		long sampleA=model.signalModel.getSampleForTimeInMilliSeconds(newSel.getSelectionStart());
		long sampleB=model.signalModel.getSampleForTimeInMilliSeconds(newSel.getSelectionEnd());
		double valueA=model.signalModel.getSample(sampleA, 0);
		double valueB=model.signalModel.getSample(sampleB, 0);
		
		Label lslopeLabel=new Label(dialog,SWT.NONE);
		lslopeLabel.setText("Neue Steigung (nach Schnitt):");
		lslopeLabel.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		
		Label lslope=new Label(dialog,SWT.NONE);
		double slopeA=model.signalModel.getSlope(valueA, valueB);
		lslope.setText(Double.toString(slopeA));
		lslope.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,2,1));
								
		ok = new Button (dialog, SWT.PUSH);
		ok.setText ("Übernehmen (Automatik)");
		ok.setEnabled(true);
		ok.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_OK_Pressed();
			}
		});
		
		Button manualOK = new Button (dialog, SWT.PUSH);
		manualOK.setText ("Übernehmen (Manuell)");
		manualOK.setEnabled(true);
		manualOK.setLayoutData(new GridData(SWT.FILL,SWT.LEFT,true,true,1,1));
		manualOK.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				Button_ManualOK_Pressed();
			}
		});
		
		Button cancel = new Button(dialog,SWT.PUSH);
		cancel.setText("Abbruch");
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
	
	public void Button_ManualOK_Pressed()
	{
		result=SWT.YES;
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
}
