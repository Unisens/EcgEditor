package de.fzi.ekgeditor.Gui.Forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import org.eclipse.swt.widgets.Text;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;

public class Dialog_GoToSample extends Dialog {
	private Shell dialogShell;
	private Text goToSampleText;
	
	public Dialog_GoToSample(Shell parent){
		super (parent,SWT.NONE);
	}
	
	public void open(){
//		SignalViewerModel signalViewerModel=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
//		SignalModel signalModel = Common.getInstance().signalModel;
//		long currentSample=Math.round(signalModel.getSamplingFrequency()*signalViewerModel.signalGraphPositionTime/1000);
//		if (currentSample<0){
//			currentSample=0;
//		}
//		int maxSamp=(int) signalModel.getMaxSamp();
		int width = 240;
		dialogShell = getParent();
		dialogShell.setText("Positionierung");
		GridLayout gridLayout = new GridLayout(2, true);
		dialogShell.setLayout(gridLayout);
		
		Label messageLabel = new Label(dialogShell, SWT.NONE);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1);
		messageLabel.setLayoutData(gridData);
		messageLabel.setText("Gehe zu Sample oder Zeit (hh:mm:ss.ms):  ");
		
		goToSampleText = new Text(dialogShell, SWT.NONE);
		gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = width;
		goToSampleText.setLayoutData(gridData);
		
		Button okButton = new Button(dialogShell, SWT.PUSH);
		okButton.setText("OK");
		gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false);
		gridData.widthHint = width / 2 - 10;
		okButton.setLayoutData(gridData);
		dialogShell.setDefaultButton(okButton);
		okButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent selectionEvent){
				SignalViewerModel signalViewerModel=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
				try {
					signalViewerModel.goToSample(Long.parseLong(goToSampleText.getText()));
				} catch (NumberFormatException e) {
					try {
						SimpleDateFormat simpleDateFormater = new SimpleDateFormat("hh:mm:ss.SSS");
						signalViewerModel.goToTime(simpleDateFormater.parse(goToSampleText.getText()).getTime() + 3600000);
					} catch (ParseException e2) {
						dialogShell.setVisible(false);
					}
					
				} 
				dialogShell.setVisible(false);
			}
		});
		
		Button cancleButton = new Button(dialogShell, SWT.PUSH);
		cancleButton.setText("Abbrechen");
		gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false);
		gridData.widthHint = width / 2 - 10;
		cancleButton.setLayoutData(gridData);
		
		cancleButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent selectionEvent){
				dialogShell.close();
			}
		});
		
		dialogShell.pack();
		dialogShell.open();
		
		Display display = dialogShell.getDisplay();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
	}
}
