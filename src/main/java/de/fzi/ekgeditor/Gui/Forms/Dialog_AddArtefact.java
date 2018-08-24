package de.fzi.ekgeditor.Gui.Forms;

import java.util.List;

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
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.ChannelData;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.utils.Selection;

public class Dialog_AddArtefact extends Dialog {
	private Shell dialogShell;
	private Button[] channels;
	private Label typeLabel;
	private Text typeValueText;
	private Button okButton;
	private Button cancelButton;
	
	public Dialog_AddArtefact(Shell parent) {
		super(parent, 0);
	}
	
	public void open(){
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setRedraw(false);
		SignalModel signalModel = Common.getInstance().signalModel;
		List<ChannelData> channelsData = signalModel.getChannelList();

		dialogShell = getParent();
		GridLayout dialogShellLayout = new GridLayout();
		dialogShellLayout.numColumns = 2;
		dialogShellLayout.makeColumnsEqualWidth = false;
		dialogShell.setLayout(dialogShellLayout);
		dialogShell.setText("Artefakt hinzufügen");
		dialogShell.layout();
		dialogShell.pack();			
		dialogShell.setSize(220, 140);
		
		channels = new Button[channelsData.size()];
		for(int i = 0 ; i < channels.length ; i++){
			channels[i] = new Button(dialogShell, SWT.CHECK);
			channels[i].setData(channelsData.get(i));
			channels[i].setText(channelsData.get(i).getName());
			GridData channelLayoutData = new GridData();
			channelLayoutData.horizontalSpan = 2;
			channelLayoutData.grabExcessHorizontalSpace = true;
			channelLayoutData.horizontalAlignment = GridData.FILL;
			channels[i].setLayoutData(channelLayoutData);
		}
		
		typeLabel = new Label(dialogShell, SWT.NONE);
		GridData typeLabelLData = new GridData();
		typeLabelLData.horizontalSpan = 1;
		typeLabelLData.grabExcessHorizontalSpace = true;
		typeLabelLData.horizontalAlignment = GridData.FILL;
		typeLabel.setLayoutData(typeLabelLData);
		typeLabel.setText("Typ: ");
		
		typeValueText = new Text(dialogShell, SWT.NONE);
		GridData typeValueTextLData = new GridData();
		typeValueTextLData.heightHint = 20;
		typeValueTextLData.widthHint = 100;
		typeValueText.setLayoutData(typeValueTextLData);
		
		okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
		GridData okButtonLData = new GridData();
		okButtonLData.horizontalAlignment = GridData.FILL;
		okButtonLData.grabExcessHorizontalSpace = true;
		okButton.setLayoutData(okButtonLData);
		okButton.setText("Ok");
		okButton.setEnabled(true);
		okButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e){
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
		ArtefactModel artefactModel = Common.getInstance().artefactModel;
		EventEntry artefactEventEntry = artefactModel.getActiveEventEntry();
		if(artefactEventEntry == null){
			artefactModel.setTempArtefactEntryAsActive();
			artefactEventEntry = artefactModel.getActiveEventEntry();
		}
		Selection selection = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getSelection();
		long startEventSample = (long)(selection.getSelectionStart() * artefactEventEntry.getSampleRate() / 1000);
		long endEventSample = (long)(selection.getSelectionEnd() * artefactEventEntry.getSampleRate() / 1000);
		boolean allChannelsSelected = true;
		for(int i = 0; i < channels.length; i++){
			if(!channels[i].getSelection()){
				allChannelsSelected = false;
				break;
			}
		}
		if(allChannelsSelected){
			Event startEvent = new Event(startEventSample, String.format("(%s", typeValueText.getText().trim()), "");
			Event endEvent = new Event(endEventSample, String.format(")%s", typeValueText.getText().trim()), "");
			Artefact artefact = new Artefact(startEvent, endEvent);
			artefactModel.add(artefact);
		}else{
			for(int i = 0; i < channels.length; i++){
				if(channels[i].getSelection()){
					Event startEvent = new Event(startEventSample, String.format("(%s00%s", typeValueText.getText().trim(), String.valueOf(i + 1)), "");
					Event endEvent = new Event(endEventSample, String.format(")%s00%s", typeValueText.getText().trim(), String.valueOf(i + 1)), "");
					Artefact artefact = new Artefact(startEvent, endEvent);
					artefactModel.add(artefact);
				}
			}
		}
		
		getParent().dispose();
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	private void buttonCancelPressed(){
		getParent().dispose();
	}
}
