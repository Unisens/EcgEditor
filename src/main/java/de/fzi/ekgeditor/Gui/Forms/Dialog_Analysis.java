package de.fzi.ekgeditor.Gui.Forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.unisens.DuplicateIdException;
import org.unisens.Entry;
import org.unisens.Event;
import org.unisens.EventEntry;
import org.unisens.MeasurementEntry;
import org.unisens.SignalEntry;
import org.unisens.Unisens;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.AnalysisProgressBar;
import de.fzi.ekgeditor.algorithm.Algorithm;
import de.fzi.ekgeditor.algorithm.AlgorithmEntry;
import de.fzi.ekgeditor.algorithm.AlgorithmEntry.EntryTypeEnum;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;

public class Dialog_Analysis extends Dialog {
	private Shell dialogShell;
	private Button okButton;
	private Button cancelButton;
	private Combo analysisAlgorithmsList;
	
	private Group algoSelectGroup;
	private Group algoConfigGroup;
	private Group algoConfigInputGroup;
	private Group algoConfigOutputGroup;

	private List<Combo> inputSignalNames;
	private List<Combo> inputChannels;
	
	
	public Dialog_Analysis(Shell parent, int style) {
		super(parent, style);
	}

	public Dialog_Analysis(Shell parent) {
		super(parent, SWT.NULL);
	}
	
	public void open() {
			Shell parent = getParent();
			
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setText("Analyse-Algorithmen auswählen");
			dialogShell.setSize(700, 700);
			{
				algoSelectGroup = new Group(dialogShell, SWT.NONE);
				RowLayout algoGroupLayout = new RowLayout(org.eclipse.swt.SWT.HORIZONTAL);
				algoSelectGroup.setLayout(algoGroupLayout);
				FormData algoGroupLData = new FormData();
				algoGroupLData.left =  new FormAttachment(0, 1000, 12);
				algoGroupLData.top =  new FormAttachment(0, 1000, 12);
				algoGroupLData.right =  new FormAttachment(1000, 1000, -12);
				algoSelectGroup.setLayoutData(algoGroupLData);
				algoSelectGroup.setText("Algorithmus auswählen");
			}
			{
				algoConfigGroup = new Group(dialogShell, SWT.NONE);
				FillLayout algoConfigGroupLayout = new FillLayout();
				algoConfigGroupLayout.type = SWT.VERTICAL;
				algoConfigGroupLayout.marginHeight = 10;
				algoConfigGroupLayout.marginWidth = 10;
				algoConfigGroup.setLayout(algoConfigGroupLayout);
				FormData algoConfigGroupLData = new FormData();
				algoConfigGroupLData.left =  new FormAttachment(0, 1000, 12);
				algoConfigGroupLData.top =  new FormAttachment(algoSelectGroup,0);
				algoConfigGroupLData.right =  new FormAttachment(1000, 1000, -12);
				algoConfigGroupLData.bottom =  new FormAttachment(1000, 1000, -40);
				algoConfigGroup.setLayoutData(algoConfigGroupLData);
				algoConfigGroup.setText("Algorithmus konfigurieren");
				{
					algoConfigInputGroup = new Group(algoConfigGroup, SWT.NONE);
					RowLayout algoConfigInputGroupLayout = new RowLayout();
					algoConfigInputGroupLayout.wrap = true;
					algoConfigInputGroupLayout.pack = false;
					algoConfigInputGroupLayout.type = SWT.HORIZONTAL;
					algoConfigInputGroupLayout.marginHeight = 10;
					algoConfigInputGroupLayout.marginWidth = 10;
					algoConfigInputGroup.setLayout(algoConfigInputGroupLayout);
					algoConfigInputGroup.setText("Eingangssignale");
					algoConfigInputGroup.setSize(SWT.DEFAULT, 100);
				}
				{
					algoConfigOutputGroup = new Group(algoConfigGroup, SWT.NONE);
					RowLayout algoConfigOutputGroupLayout = new RowLayout();
					algoConfigOutputGroupLayout.wrap = true;
					algoConfigOutputGroupLayout.pack = false;
					algoConfigOutputGroupLayout.type = SWT.HORIZONTAL;
					algoConfigOutputGroupLayout.marginHeight = 10;
					algoConfigOutputGroupLayout.marginWidth = 10;
					algoConfigOutputGroup.setLayout(algoConfigOutputGroupLayout);
					algoConfigOutputGroup.setText("Ergebnisse");
				}
			}
			
			cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			cancelButton.setText("Abbrechen");
			FormData cancelButtonLData = new FormData();
			cancelButtonLData.width = 83;
			cancelButtonLData.height = 23;
			cancelButtonLData.left =  new FormAttachment(0, 1000, 12);
			cancelButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
			cancelButton.setLayoutData(cancelButtonLData);
			
			cancelButton.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					Button_Cancel_Pressed();
				}
			});
			
			okButton = new Button(dialogShell, SWT.PUSH);
			okButton.setText("Start");
			FormData okButtonLData = new FormData();
			okButtonLData.width = 83;
			okButtonLData.height = 23;
			okButtonLData.bottom =  new FormAttachment(1000, 1000, -12);
			okButtonLData.right =  new FormAttachment(1000, 1000, -12);
			okButton.setLayoutData(okButtonLData);
			okButton.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try {
						Button_OK_Pressed();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
				}
			});
			okButton.pack(true);
			
			dialogShell.setDefaultButton(okButton);
			
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			fillAnalysisAlgorithmsList();
			
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
	}
	
	private void fillAnalysisAlgorithmsList(){
		analysisAlgorithmsList = new Combo(algoSelectGroup, SWT.READ_ONLY);
		List<Algorithm> availableAlgorithms = Common.getInstance().algorithmsConfig.getAvailableAlgorithms();
		for (int i = 0; i < availableAlgorithms.size(); i++) {
			if(!checkSampleRates(availableAlgorithms.get(i))){ 
				continue;
			}
			analysisAlgorithmsList.add(availableAlgorithms.get(i).getName());
		}
		
		analysisAlgorithmsList.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent evt) {
				activateAnalysisAlgorithm(((Combo)evt.widget));
			}
		});
		
		dialogShell.layout(true);
	}
	
	private boolean checkSampleRates(Algorithm algo) {
		List<SignalEntry> signalEntries = UnisensAdapter.getSignalEntries(Common.getInstance().unisens);
		List<AlgorithmEntry> algorithmInputEntries = algo.getInputEntries();
		outer:
		for(int i = 0; i < algorithmInputEntries.size(); i++) {
			AlgorithmEntry algorithmInputEntry = algorithmInputEntries.get(i);
			if (algorithmInputEntry.getEntryTypeEnum() != AlgorithmEntry.EntryTypeEnum.SIGNAL){ 
				continue;
			}
			
			for(SignalEntry signalEntry : signalEntries){
				String contentClass = signalEntry.getContentClass();
				if(algorithmInputEntry.getContentClass() != AlgorithmEntry.ContentClass.toContentClass(contentClass)) { 
					continue; 
				}

				if (algorithmInputEntry.checkSampleRateSupported((int)signalEntry.getSampleRate())) {
					// found signal which supports one of the framerates for this input signal
					// continue on to next input signal
					continue outer;
				}
			}
			// if signal with correct framerate was found, continue outer; was already called
			// so this point shouldn't be reached
			return false;
		}
		
		return true;
	}
	
	private void activateAnalysisAlgorithm(Combo combo){
		String name = combo.getText();
		Common.getInstance().algorithmsConfig.activateAlgorithm(name);

		fillOptions(algoConfigInputGroup, Common.getInstance().algorithmsConfig.getActiveAlgorithm().getInputEntries());
		fillOutputOptions(algoConfigOutputGroup, Common.getInstance().algorithmsConfig.getActiveAlgorithm().getOutputEntries());

		// needed update display after changes
		algoConfigGroup.layout(true);
	}
	
	private void fillOutputOptions(Composite parent, List<AlgorithmEntry> outputEntry) {
		removeChildren(parent);
		
		for(AlgorithmEntry algortithmEntry : outputEntry) {
			if(algortithmEntry.getEntryTypeEnum() == EntryTypeEnum.EVENT || algortithmEntry.getEntryTypeEnum() == EntryTypeEnum.TIMERANGE){
				Label label = new Label(parent, SWT.NONE);
				RowData labelLData = new RowData(parent.getSize().x - 50, SWT.DEFAULT);
				label.setLayoutData(labelLData);
				label.setText(algortithmEntry.getName());
			}
		}
		parent.layout(true);
	}
	
	private void fillOptions(Composite parent, List<AlgorithmEntry> algorithmEntries) {
		removeChildren(parent);
		inputSignalNames = new ArrayList<Combo>();
		inputChannels = new ArrayList<Combo>();

		
		RowData rowData = new RowData((parent.getSize().x / 3) - 50, SWT.DEFAULT);
		for(AlgorithmEntry algorithmEntry : algorithmEntries){
			Label label = new Label(parent, SWT.BORDER);
			label.setLayoutData(rowData);
			label.setText(algorithmEntry.getName());
			
			Combo signalNames = new Combo(parent, SWT.READ_ONLY|SWT.BORDER);
			signalNames.setLayoutData(rowData);
			
			List<SignalEntry> signalEntries = UnisensAdapter.getSignalEntries(Common.getInstance().unisens);
			for(SignalEntry signalEntry : signalEntries) {
				String contentClass = signalEntry.getContentClass();
				if (AlgorithmEntry.ContentClass.toContentClass(contentClass) != algorithmEntry.getContentClass()) { 
					continue;
				}
				if(!algorithmEntry.checkSampleRateSupported((int)signalEntry.getSampleRate())) { 
					continue;
				}
				signalNames.add(signalEntry.getId());
			}
			signalNames.select(0);
			signalNames.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					int index = inputSignalNames.indexOf(evt.widget);
					changeChannelSelect(index, ((Combo)evt.widget).getText());
				}
			});
			
			inputSignalNames.add(signalNames);
			
			// display combo-box to select channel
			Combo channelNumber = new Combo(parent, SWT.READ_ONLY|SWT.BORDER);
			channelNumber.setLayoutData(rowData);
			
			inputChannels.add(channelNumber);
			
			changeChannelSelect(inputChannels.size() - 1, signalNames.getText());
			
		}
		parent.layout(true);
	}
	
	private void changeChannelSelect(int index, String signalId) {
		// clear combo-box
		// Combo combo = inputChannels.get(index);
		inputChannels.get(index).removeAll();

		Entry entry = Common.getInstance().unisens.getEntry(signalId);
		if (!(entry instanceof MeasurementEntry)) {
			inputChannels.get(index).setEnabled(false);
			return;
		}
		
		// refill
		MeasurementEntry mEntry = (MeasurementEntry)entry;
		inputChannels.get(index).setEnabled(true);
		for(int i = 0; i < mEntry.getChannelCount(); i++) {
			inputChannels.get(index).add(mEntry.getChannelNames()[i]);
		}
		inputChannels.get(index).select(0);
	}
	
	private void removeChildren(Composite parent) {
		Control children[] = parent.getChildren();
		for(int i = 0; i < children.length; i++) {
			children[i].dispose();
		}
	}
	
	private void Button_OK_Pressed() throws Exception{
		Algorithm algorithm = Common.getInstance().algorithmsConfig.getActiveAlgorithm();
		Unisens unisens = Common.getInstance().unisens;
		List<AlgorithmEntry> algorithmInputEntries = algorithm.getInputEntries();
		for(int i = 0; i < algorithmInputEntries.size(); i++) {
			algorithm.setInputSignalToAlgorithmInputEntry(algorithmInputEntries.get(i),(SignalEntry)unisens.getEntry(inputSignalNames.get(i).getText()), inputChannels.get(i).getSelectionIndex());
		}
		
		try {
			this.dialogShell.setVisible(false);
			Display.getCurrent().update();
			final AnalysisProgressBar analysisProgressBar = new AnalysisProgressBar();
			analysisProgressBar.setText("Signal analysieren ...");
			final int maxSelection = analysisProgressBar.getMaximum();
			
			algorithm.start();
			long sampleCount = algorithm.getSampleCount();
			
			while(algorithm.getCurrentSample() < sampleCount){
				if(analysisProgressBar.isDisposed())
					break;
				final int selection = (int)((double)((double)algorithm.getCurrentSample()/(double)sampleCount) * maxSelection);
				analysisProgressBar.setSelection(selection);
				Display.getCurrent().update();
				Thread.sleep(100);
			}
			
			if(algorithm.getCurrentSample() ==  algorithm.getSampleCount()){
				Display.getCurrent().asyncExec(new Runnable(){
					public void run(){
						if(!analysisProgressBar.isDisposed()){
							analysisProgressBar.setSelection(analysisProgressBar.getMaximum());
							Display.getCurrent().update();
						}
					}
				});
				analysisProgressBar.dispose();
				
				List<Event> events = algorithm.getEvents();
				if(events.size() !=  0)
					saveTriggerlist(events);
				
				List<Artefact> artefacts = algorithm.getArtefacts();
				if(artefacts.size() != 0)
					saveArtefacts(artefacts);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void saveTriggerlist(List<Event> events) throws IOException {
		try {
			TriggerModel triggerModel = Common.getInstance().triggerModel;
			EventEntry tempEventEntry = Common.getInstance().signalModel.getSignal().getUnisens().createEventEntry("temp_events"+System.currentTimeMillis() +".csv", Common.getInstance().signalModel.getSignal().getSampleRate());
			tempEventEntry.setContentClass(UnisensAdapter.TRIGGER_CONTENT_CLASS);
			triggerModel.addTempTriggerEntry(tempEventEntry, events);
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
		} catch (DuplicateIdException e) {
			e.printStackTrace();
		}
	}
	
	private void saveArtefacts(List<Artefact> artefacts) throws IOException {
		ArtefactModel artefactModel = Common.getInstance().artefactModel;
		artefactModel.setTempArtefactEntryAsActive();
		artefactModel.add(artefacts);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
	}
	
	private void Button_Cancel_Pressed(){
		dialogShell.close();
		dialogShell.dispose();
	}
}
