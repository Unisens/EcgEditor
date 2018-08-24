package de.fzi.ekgeditor.Gui.Forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.unisens.Event;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.TriggerModel;

public class Dialog_AddTrigger extends Dialog {
	private Shell dialogShell;
	private Label samplestampLabel;
	private Label samplestampValue;
	private Label typeLabel;
	private Text typeValueText;
	private Label commentLabel;
	private Text commentValueText;
	private Button okButton;
	private Button cancelButton;
	
	public Dialog_AddTrigger(Shell parent) {
		super(parent, 0);
	}
	
	public void open(){
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setRedraw(false);
		SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		int mouseRightDownXPosition = signalViewerModel.getMouseRightDownXPosition();
		long samplestamp = 0;
		if(triggerModel.getActiveEventEntry() != null)
			samplestamp = signalViewerModel.getSampleNumber(mouseRightDownXPosition, Common.getInstance().triggerModel.getActiveEventEntry().getSampleRate());
		else
			samplestamp = signalViewerModel.getSampleNumber(mouseRightDownXPosition, Common.getInstance().signalModel.getSignal().getSampleRate());
		dialogShell = getParent();
		GridLayout dialogShellLayout = new GridLayout();
		dialogShellLayout.numColumns = 2;
		dialogShellLayout.makeColumnsEqualWidth = false;
		dialogShell.setLayout(dialogShellLayout);
		dialogShell.setText("Trigger hinzufügen");
		dialogShell.layout();
		dialogShell.pack();			
		dialogShell.setSize(220, 140);
		
		samplestampLabel = new Label(dialogShell, SWT.NONE);
		GridData samplestampLabelLData = new GridData();
		samplestampLabelLData.horizontalSpan = 1;
		samplestampLabelLData.grabExcessHorizontalSpace = true;
		samplestampLabelLData.horizontalAlignment = GridData.FILL;
		samplestampLabel.setLayoutData(samplestampLabelLData);
		samplestampLabel.setText("Samplestamp");
		
		samplestampValue = new Label(dialogShell, SWT.NONE);
		GridData samplestampValueLData = new GridData();
		samplestampValueLData.horizontalSpan = 1;
		samplestampValueLData.grabExcessHorizontalSpace = true;
		samplestampValueLData.horizontalAlignment = GridData.FILL;
		samplestampValue.setLayoutData(samplestampValueLData);
		samplestampValue.setText(""+samplestamp);
		
		typeLabel = new Label(dialogShell, SWT.NONE);
		GridData typeLabelLData = new GridData();
		typeLabelLData.horizontalSpan = 1;
		typeLabelLData.grabExcessHorizontalSpace = true;
		typeLabelLData.horizontalAlignment = GridData.FILL;
		typeLabel.setLayoutData(typeLabelLData);
		typeLabel.setText("Typ");
		
		typeValueText = new Text(dialogShell, SWT.NONE);
		GridData typeValueTextLData = new GridData();
		typeValueTextLData.heightHint = 20;
		typeValueTextLData.widthHint = 100;
		typeValueText.setLayoutData(typeValueTextLData);
		
		commentLabel = new Label(dialogShell, SWT.NONE);
		GridData commentLabelLData = new GridData();
		commentLabelLData.horizontalSpan = 1;
		commentLabelLData.grabExcessHorizontalSpace = true;
		commentLabelLData.horizontalAlignment = GridData.FILL;
		commentLabel.setLayoutData(typeLabelLData);
		commentLabel.setText("Kommentar");
		
		commentValueText = new Text(dialogShell, SWT.NONE);
		GridData commentValueTextLData = new GridData();
		commentValueTextLData.heightHint = 20;
		commentValueTextLData.widthHint = 100;
		commentValueText.setLayoutData(typeValueTextLData);
		
		okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
		GridData okButtonLData = new GridData();
		okButtonLData.horizontalAlignment = GridData.FILL;
		okButtonLData.grabExcessHorizontalSpace = true;
		okButton.setLayoutData(okButtonLData);
		okButton.setText("Ok");
		okButton.setEnabled(true);
		okButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				buttonOkPressed();
			}
		});
		
		cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
		GridData cancelButtonLData = new GridData();
		cancelButtonLData.horizontalAlignment = GridData.FILL;
		cancelButton.setLayoutData(cancelButtonLData);
		cancelButton.setText("Abbrechen");
		cancelButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				buttonCancelPressed();
			}
		});
		dialogShell.setLocation(Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getMouseRightDownLocation());
		dialogShell.open();
	}
	
	private void buttonOkPressed(){
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		if(triggerModel.getActiveEventEntry() == null)
			triggerModel.setTempTriggerEntryAsActive();
		Event event = new Event(Long.parseLong(samplestampValue.getText()), typeValueText.getText(), commentValueText.getText());
		Common.getInstance().triggerModel.addEvent(event);
		getParent().dispose();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	private void buttonCancelPressed(){
		getParent().dispose();
	}
}
