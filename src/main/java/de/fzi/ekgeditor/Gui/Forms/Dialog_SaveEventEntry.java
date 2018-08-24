package de.fzi.ekgeditor.Gui.Forms;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.unisens.DuplicateIdException;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.EntryData;
import de.fzi.ekgeditor.data.IEventEntryModel;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;

public class Dialog_SaveEventEntry extends Dialog {
	private Shell dialogShell;
	private Group groupUnisens;
	private Text textSource;
	private Label labelSourceId;
	private Label labelSource;
	private Group groupSource;
	private Button buttonCancel;
	private Button buttonSave;
	private Composite compositeButtons;
	private Button buttonDefaultEcg;
	private Label labelId;
	private Text textSourceId;
	private Text textDelimiter;
	private Label labelDelimiter;
	private Text textComment;
	private Text textSampleRate;
	private Label labelComment;
	private Label labelSampleRate;
	private Button saveAsNewButton;
	private Text newEventEntryId;
	private Button saveAsExistingButton;
	private Combo existingEventEntriesId;
	
	private EventEntry eventEntry;
	private IEventEntryModel eventEntryModel;
	private String[] texts;
	
	public Dialog_SaveEventEntry(Shell parent, IEventEntryModel eventEntryModel){
		super(parent, 0);
		this.eventEntryModel = eventEntryModel;
		eventEntry = eventEntryModel.getActiveEventEntry();
		if(eventEntry.getContentClass().equalsIgnoreCase(UnisensAdapter.TRIGGER_CONTENT_CLASS))
			texts = new String[]{"Triggerliste speichern unter ...", "Als neue Triggerliste speichern ","Existierende Triggerliste überschreiben ", "- Triggerliste auswählen -"};
		else
			texts = new String[]{"Artefaktliste speichern unter ...", "Als neue Artefactliste speichern ","Existierende Artefactliste überschreiben ", "- Artefactliste auswählen -"};
		
	}
	
	public void open(){
		Shell parent = getParent();
		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		GridLayout dialogShellLayout = new GridLayout();
		dialogShellLayout.numColumns = 2;
		dialogShellLayout.makeColumnsEqualWidth = false;
		dialogShell.setLayout(dialogShellLayout);
		dialogShell.setText(texts[0]);
		dialogShell.layout();
		dialogShell.pack();			
		dialogShell.setSize(490, 370);
	
		groupUnisens = new Group(dialogShell, SWT.NONE);
		GridLayout groupUnisensLayout = new GridLayout();
		groupUnisensLayout.makeColumnsEqualWidth = true;
		groupUnisensLayout.numColumns = 2;
		groupUnisens.setLayout(groupUnisensLayout);
		GridData groupUnisensLData = new GridData();
		groupUnisensLData.horizontalSpan = 2;
		groupUnisensLData.grabExcessHorizontalSpace = true;
		groupUnisensLData.grabExcessVerticalSpace = false;
		groupUnisens.setLayoutData(groupUnisensLData);
		groupUnisens.setText("Format-Informationen");
			
		labelSampleRate = new Label(groupUnisens, SWT.NONE);
		GridData labelSampleRateLData = new GridData();
		labelSampleRateLData.horizontalAlignment = GridData.BEGINNING;
		labelSampleRateLData.grabExcessHorizontalSpace = true;
		labelSampleRate.setLayoutData(labelSampleRateLData);
		labelSampleRate.setText("Sampling-Rate:");

		textSampleRate = new Text(groupUnisens, SWT.NONE);
		GridData textSampleRateLData = new GridData();
		textSampleRateLData.heightHint = 18;
		textSampleRateLData.widthHint = 80;
		textSampleRate.setLayoutData(textSampleRateLData);
	
		labelId = new Label(groupUnisens, SWT.NONE);
		labelId.setText("Unisens-ID");
		GridData labelIdLData = new GridData();
		labelIdLData.horizontalSpan = 2;
		labelId.setLayoutData(labelIdLData);

		
		saveAsNewButton = new Button(groupUnisens, SWT.RADIO);
		saveAsNewButton.setText(texts[1]);
		saveAsNewButton.setSelection(true);
		saveAsNewButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				existingEventEntriesId.select(0);
				existingEventEntriesId.setEnabled(false);
				newEventEntryId.setEnabled(true);
				if(eventEntry != null){
					textComment.setText(eventEntry.getComment() != null ? eventEntry.getComment() : "");
					textSampleRate.setText(eventEntry.getSampleRate()+"");
					textSource.setText(eventEntry.getSource() != null ? eventEntry.getSource() : "");
					textSourceId.setText(eventEntry.getSourceId() != null ? eventEntry.getSourceId() : "");
				}
			}
		});
		
		newEventEntryId = new Text(groupUnisens, SWT.NONE);
		GridData newEventEntryIdLData = new GridData();
		newEventEntryIdLData.widthHint = 170;
		newEventEntryId.setLayoutData(newEventEntryIdLData);
		
		saveAsExistingButton = new Button(groupUnisens, SWT.RADIO);
		saveAsExistingButton.setText(texts[2]);
		saveAsExistingButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				existingEventEntriesId.setEnabled(true);
				newEventEntryId.setEnabled(false);
			}
		});
		existingEventEntriesId = new Combo(groupUnisens, SWT.LEFT|SWT.READ_ONLY);
		existingEventEntriesId.add(texts[3]);
		existingEventEntriesId.select(0);
		existingEventEntriesId.setEnabled(false);
		for (EventEntry eventEntry : eventEntryModel.getEventEntries()) {
			existingEventEntriesId.add(eventEntry.getId());
		}
		if(existingEventEntriesId.getItemCount() == 1){
			saveAsExistingButton.setEnabled(false);
		}
		existingEventEntriesId.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				Combo eventEntriesCombo = (Combo)event.getSource();
				if(eventEntriesCombo.getSelectionIndex() != 0){
					String selectedEventEntryId = eventEntriesCombo.getItem(eventEntriesCombo.getSelectionIndex());
					EventEntry selectedEventEntry = eventEntryModel.getEventEntry(selectedEventEntryId);
					if(selectedEventEntry != null){
						textComment.setText(selectedEventEntry.getComment() != null ? selectedEventEntry.getComment() : "");
						textSampleRate.setText(selectedEventEntry.getSampleRate()+"");
						textSource.setText(selectedEventEntry.getSource() != null ? selectedEventEntry.getSource() : "");
						textSourceId.setText(selectedEventEntry.getSourceId() != null ? selectedEventEntry.getSourceId() : "");
					}
				}else{
					if(eventEntry != null){
						textComment.setText(eventEntry.getComment() != null ? eventEntry.getComment() : "");
						textSampleRate.setText(eventEntry.getSampleRate()+"");
						textSource.setText(eventEntry.getSource() != null ? eventEntry.getSource() : "");
						textSourceId.setText(eventEntry.getSourceId() != null ? eventEntry.getSourceId() : "");
					}
				}
				
			}
		});
		
		labelDelimiter = new Label(groupUnisens, SWT.NONE);
		labelDelimiter.setText("Trennzeichen:");
			
		textDelimiter = new Text(groupUnisens, SWT.NONE);
		GridData textDelimiterLData = new GridData();
		textDelimiterLData.widthHint = 20;
		textDelimiterLData.heightHint = 18;
		textDelimiter.setLayoutData(textDelimiterLData);
		textDelimiter.setText(";");

		groupSource = new Group(dialogShell, SWT.NONE);
		GridLayout groupSourceLayout = new GridLayout();
		groupSourceLayout.makeColumnsEqualWidth = true;
		groupSourceLayout.numColumns = 2;
		groupSource.setLayout(groupSourceLayout);
		GridData groupSourceLData = new GridData();
		groupSourceLData.horizontalSpan = 2;
		groupSourceLData.horizontalAlignment = GridData.FILL;
		groupSource.setLayoutData(groupSourceLData);
		groupSource.setText("Weitere Angaben");
		
		labelSource = new Label(groupSource, SWT.NONE);
		GridData labelSourceLData = new GridData();
		labelSourceLData.widthHint = 100;
		labelSource.setLayoutData(labelSourceLData);
		labelSource.setText("Quelle:");
	
		labelSourceId = new Label(groupSource, SWT.NONE);
		labelSourceId.setText("Quellen-ID:");
	
		GridData textSourceLData = new GridData();
		textSourceLData.horizontalAlignment = GridData.FILL;
		textSource = new Text(groupSource, SWT.NONE);
		textSource.setLayoutData(textSourceLData);
	
		GridData textSourceIdLData = new GridData();
		textSourceIdLData.widthHint = 100;
		textSourceId = new Text(groupSource, SWT.NONE);
		textSourceId.setLayoutData(textSourceIdLData);
	
		labelComment = new Label(groupSource, SWT.NONE);
		GridData labelCommentLData = new GridData();
		labelCommentLData.horizontalSpan = 2;
		labelComment.setLayoutData(labelCommentLData);
		labelComment.setText("Kommentar:");
	
		GridData textCommentLData = new GridData();
		textCommentLData.horizontalAlignment = GridData.FILL;
		textCommentLData.horizontalSpan = 2;
		textComment = new Text(groupSource, SWT.NONE);
		textComment.setLayoutData(textCommentLData);
	
		buttonDefaultEcg = new Button(groupSource, SWT.CHECK | SWT.LEFT);
		GridData buttonDefaultEcgLData = new GridData();
		buttonDefaultEcgLData.horizontalSpan = 2;
		buttonDefaultEcg.setLayoutData(buttonDefaultEcgLData);
		buttonDefaultEcg.setText("Diese EventEntry in die Gruppe defaultEcg setzen");
		buttonDefaultEcg.setSelection(true);
	
		compositeButtons = new Composite(dialogShell, SWT.RIGHT);
		GridLayout compositeButtonsLayout = new GridLayout();
		compositeButtonsLayout.makeColumnsEqualWidth = true;
		compositeButtonsLayout.numColumns = 2;
		GridData compositeButtonsLData = new GridData();
		compositeButtonsLData.horizontalAlignment = GridData.FILL;
		compositeButtonsLData.grabExcessHorizontalSpace = true;
		compositeButtons.setLayoutData(compositeButtonsLData);
		compositeButtons.setLayout(compositeButtonsLayout);
			
		buttonSave = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
		GridData buttonSaveLData = new GridData();
		buttonSaveLData.horizontalAlignment = GridData.FILL;
		buttonSaveLData.grabExcessHorizontalSpace = true;
		buttonSave.setLayoutData(buttonSaveLData);
		buttonSave.setText("Speichern");
		buttonSave.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try {
					Button_Ok_Pressed();
				} catch (DuplicateIdException e2) {
					Common.getInstance().ShowErrorBox("Duplicate id exception", "Invalid id");
					e2.printStackTrace();
				}catch (IOException e2) {
					Common.getInstance().ShowErrorBox("IO Exception", "Unerwartete IO Exception bei Speichern der EventEntry");
					e2.printStackTrace();
				}
			}
		});
		
		buttonCancel = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
		GridData buttonCancelLData = new GridData();
		buttonCancelLData.horizontalAlignment = GridData.FILL;
		buttonCancel.setLayoutData(buttonCancelLData);
		buttonCancel.setText("Abbrechen");
		buttonCancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_Cancel_Pressed();
			}
		});
		
		if(eventEntry != null){
			this.textComment.setText(eventEntry.getComment() != null ? eventEntry.getComment() : "");
			this.textSampleRate.setText(eventEntry.getSampleRate()+"");
			this.textSource.setText(eventEntry.getSource() != null ? eventEntry.getSource() : "");
			this.textSourceId.setText(eventEntry.getSourceId() != null ? eventEntry.getSourceId() : "");
		}
		
		dialogShell.setLocation(getParent().toDisplay(100, 100));
		dialogShell.open();
		while (!dialogShell.isDisposed()) {
			if (!dialogShell.getDisplay().readAndDispatch()) 
				dialogShell.getDisplay().sleep();
		}
	}
	
	private void Button_Ok_Pressed()throws DuplicateIdException, IOException{
		String id = saveAsNewButton.getSelection() ? newEventEntryId.getText() : existingEventEntriesId.getItem(existingEventEntriesId.getSelectionIndex());
		EntryData entryData = new EntryData(id, Double.parseDouble(textSampleRate.getText()), eventEntry.getContentClass(), textSource.getText(), textSourceId.getText(), textComment.getText());
		eventEntryModel.saveActiveEventEntryAs(entryData, buttonDefaultEcg.getSelection());
		dialogShell.close();
	}
	
	private void Button_Cancel_Pressed(){
		if(eventEntryModel.isActiveEventEntryTemp()){
			eventEntryModel.removeActiveEventEntry();
		}
		dialogShell.close();
	}
}
