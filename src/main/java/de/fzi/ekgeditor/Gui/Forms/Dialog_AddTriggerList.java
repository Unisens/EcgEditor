package de.fzi.ekgeditor.Gui.Forms;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.EntryData;



public class Dialog_AddTriggerList extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Text textFileName;
	private Button buttonFileName;
	private Group groupUnisens;
	private Text textTypeLength;
	private Label labelCommentLength;
	private Label labelTypeLength;
	private Button buttonHasComment;
	private Text textSource;
	private Label labelSourceId;
	private Label labelSource;
	private Group groupSource;
	private Button buttonCancel;
	private Button buttonSave;
	private Composite compositeFile;
	private Composite compositeEmpty1;
	private Composite compositeButtons;
	private Button buttonDefaultEcg;
	private Text textId;
	private Label labelId;
	private Text textSourceId;
	private Text textDelimiter;
	private Label labelDelimiter;
	private Text textCommentLength;
	private Text textComment;
	private Text textSampleRate;
	private Label labelComment;
	private Label labelSampleRate;
	private Label labelInfo;
	private String savePath;
	private String saveFile;
	private EntryData triggerListData;

	
	public Dialog_AddTriggerList(Shell parent) {
		super(parent, 0);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			
			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.numColumns = 2;
			dialogShellLayout.makeColumnsEqualWidth = false;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setText("Triggerliste hinzufügen");
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(440, 470);
			{
				labelInfo = new Label(dialogShell, SWT.NONE);
				GridData labelInfoLData = new GridData();
				labelInfoLData.horizontalSpan = 2;
				labelInfoLData.grabExcessHorizontalSpace = true;
				labelInfoLData.horizontalAlignment = GridData.FILL;
				labelInfo.setLayoutData(labelInfoLData);
				labelInfo.setText("Fügen Sie diesem Datensatz eine vorhandene Triggerliste im CSV-Format zu.");
			}
			{
				compositeFile = new Composite(dialogShell, SWT.NONE);
				GridLayout compositeFileLayout = new GridLayout();
				compositeFileLayout.numColumns = 2;
				GridData compositeFileLData = new GridData();
				compositeFileLData.horizontalAlignment = GridData.FILL;
				compositeFileLData.horizontalSpan = 2;
				compositeFile.setLayoutData(compositeFileLData);
				compositeFile.setLayout(compositeFileLayout);
				{
					textFileName = new Text(compositeFile, SWT.BORDER);
					GridData textFileNameLData = new GridData();
					textFileNameLData.horizontalAlignment = GridData.FILL;
					textFileNameLData.grabExcessHorizontalSpace = true;
					textFileNameLData.widthHint = 18;
					textFileName.setLayoutData(textFileNameLData);
				}
				{
					buttonFileName = new Button(compositeFile, SWT.PUSH | SWT.CENTER);
					GridData button1LData = new GridData();
					button1LData.horizontalAlignment = GridData.END;
					//				buttonFileNameLData.horizontalAlignment = GridData.END;
					buttonFileName.setLayoutData(button1LData);
					buttonFileName.setText("Durchsuchen...");
					buttonFileName.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							
							Button_FileName_Pressed();
						}
					});
					
				}
			}
			{
				groupUnisens = new Group(dialogShell, SWT.NONE);
				GridLayout groupUnisensLayout = new GridLayout();
				groupUnisensLayout.makeColumnsEqualWidth = true;
				groupUnisensLayout.numColumns = 2;
				groupUnisens.setLayout(groupUnisensLayout);
				GridData groupUnisensLData = new GridData();
				groupUnisensLData.horizontalSpan = 2;
				groupUnisensLData.horizontalAlignment = GridData.FILL;
				groupUnisensLData.grabExcessHorizontalSpace = true;
				groupUnisens.setLayoutData(groupUnisensLData);
				groupUnisens.setText("Format-Informationen");
				{
					labelSampleRate = new Label(groupUnisens, SWT.NONE);
					GridData labelSampleRateLData = new GridData();
					labelSampleRateLData.horizontalAlignment = GridData.FILL;
					labelSampleRateLData.grabExcessHorizontalSpace = true;
					labelSampleRate.setLayoutData(labelSampleRateLData);
					labelSampleRate.setText("Sampling-Rate:");
				}
				{
					labelId = new Label(groupUnisens, SWT.NONE);
					labelId.setText("Unisens-ID:");
				}
				{
					textSampleRate = new Text(groupUnisens, SWT.BORDER);
					GridData textSampleRateLData = new GridData();
					textSampleRateLData.widthHint = 80;
					textSampleRate.setLayoutData(textSampleRateLData);
					textSampleRate.setText("");
					textSampleRate.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							button_Okay_Check();
						}
					});
				}
				{
					GridData textIdLData = new GridData();
					textIdLData.horizontalAlignment = GridData.FILL;
					textId = new Text(groupUnisens, SWT.BORDER);
					textId.setLayoutData(textIdLData);
					textId.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							button_Okay_Check();
						}
					});
				}
				{
					labelTypeLength = new Label(groupUnisens, SWT.NONE);
					labelTypeLength.setText("Länge einer Annotation:");
				}
				{
					labelDelimiter = new Label(groupUnisens, SWT.NONE);
					labelDelimiter.setText("Trennzeichen:");
				}
				{
					textTypeLength = new Text(groupUnisens, SWT.BORDER);
					GridData textTypeLengthLData = new GridData();
					textTypeLengthLData.widthHint = 20;
					textTypeLength.setLayoutData(textTypeLengthLData);
					textTypeLength.setText("1");
					textTypeLength.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							button_Okay_Check();
						}
					});
				}
				{
					textDelimiter = new Text(groupUnisens, SWT.BORDER);
					GridData textDelimiterLData = new GridData();
					textDelimiterLData.widthHint = 20;
					textDelimiter.setLayoutData(textDelimiterLData);
					textDelimiter.setText(";");
					textDelimiter.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							button_Okay_Check();
						}
					});
				}
				{
					buttonHasComment = new Button(groupUnisens, SWT.CHECK | SWT.LEFT);
					GridData buttonHasCommentLData = new GridData();
					buttonHasCommentLData.horizontalSpan = 2;
					buttonHasComment.setLayoutData(buttonHasCommentLData);
					buttonHasComment.setText("Triggerliste enthält Kommentare");
					buttonHasComment.setEnabled(false);
				}
				{
					labelCommentLength = new Label(groupUnisens, SWT.NONE);
					GridData labelCommentLengthLData = new GridData();
					labelCommentLengthLData.horizontalSpan = 2;
					labelCommentLength.setLayoutData(labelCommentLengthLData);
					labelCommentLength.setText("Maximale Länge eines Kommentars:");
					labelCommentLength.setEnabled(false);
				}
				{
					textCommentLength = new Text(groupUnisens, SWT.BORDER);
					GridData textCommentLengthLData = new GridData();
					textCommentLengthLData.widthHint = 20;
					textCommentLength.setLayoutData(textCommentLengthLData);
					textCommentLength.setText("0");
					textCommentLength.setEnabled(false);
					textCommentLength.addModifyListener(new ModifyListener()
					{
						public void modifyText(ModifyEvent e)
						{
							
							button_Okay_Check();
						}
					});
				}
			}
			{
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
				{
					labelSource = new Label(groupSource, SWT.NONE);
					GridData labelSourceLData = new GridData();
					labelSourceLData.horizontalAlignment = GridData.FILL;
					labelSourceLData.grabExcessHorizontalSpace = true;
					labelSource.setLayoutData(labelSourceLData);
					labelSource.setText("Quelle:");
				}
				{
					labelSourceId = new Label(groupSource, SWT.NONE);
					labelSourceId.setText("Quellen-ID:");
				}
				{
					GridData textSourceLData = new GridData();
					textSourceLData.horizontalAlignment = GridData.FILL;
					textSource = new Text(groupSource, SWT.BORDER);
					textSource.setLayoutData(textSourceLData);
				}
				{
					GridData textSourceIdLData = new GridData();
					textSourceIdLData.horizontalAlignment = GridData.FILL;
					textSourceId = new Text(groupSource, SWT.BORDER);
					textSourceId.setLayoutData(textSourceIdLData);
				}
				{
					labelComment = new Label(groupSource, SWT.NONE);
					GridData labelCommentLData = new GridData();
					labelCommentLData.horizontalSpan = 2;
					labelComment.setLayoutData(labelCommentLData);
					labelComment.setText("Kommentar:");
				}
				{
					GridData textCommentLData = new GridData();
					textCommentLData.horizontalAlignment = GridData.FILL;
					textCommentLData.horizontalSpan = 2;
					textComment = new Text(groupSource, SWT.BORDER);
					textComment.setLayoutData(textCommentLData);
				}
				{
					buttonDefaultEcg = new Button(groupSource, SWT.CHECK | SWT.LEFT);
					GridData buttonDefaultEcgLData = new GridData();
					buttonDefaultEcgLData.horizontalSpan = 2;
					buttonDefaultEcg.setLayoutData(buttonDefaultEcgLData);
					buttonDefaultEcg.setText("Diese Triggerliste in die Gruppe defaultEcg setzen");
					buttonDefaultEcg.setSelection(true);
				}
			}
			{
				compositeEmpty1 = new Composite(dialogShell, SWT.NONE);
				GridLayout compositeEmpty1Layout = new GridLayout();
				compositeEmpty1Layout.makeColumnsEqualWidth = true;
				GridData compositeEmpty1LData = new GridData();
				compositeEmpty1LData.horizontalAlignment = GridData.FILL;
				compositeEmpty1LData.grabExcessHorizontalSpace = true;
				compositeEmpty1.setLayoutData(compositeEmpty1LData);
				compositeEmpty1.setLayout(compositeEmpty1Layout);
			}
			{
				compositeButtons = new Composite(dialogShell, SWT.RIGHT);
				GridLayout compositeButtonsLayout = new GridLayout();
				compositeButtonsLayout.makeColumnsEqualWidth = true;
				compositeButtonsLayout.numColumns = 2;
				GridData compositeButtonsLData = new GridData();
				compositeButtonsLData.horizontalAlignment = GridData.FILL;
				compositeButtonsLData.grabExcessHorizontalSpace = true;
				compositeButtons.setLayoutData(compositeButtonsLData);
				compositeButtons.setLayout(compositeButtonsLayout);
				{
					buttonSave = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
					GridData buttonSaveLData = new GridData();
					buttonSaveLData.horizontalAlignment = GridData.FILL;
					buttonSaveLData.grabExcessHorizontalSpace = true;
					buttonSave.setLayoutData(buttonSaveLData);
					buttonSave.setText("Speichern");
					buttonSave.setEnabled(false);
					buttonSave.addSelectionListener(new SelectionAdapter()
					{
						public void widgetSelected(SelectionEvent e)
						{
							Button_Okay_Pressed();
						}
					});
				}
				{
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
				}
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Button_FileName_Pressed()
	{
		FileDialog fileDialog = new FileDialog(this.dialogShell, SWT.MULTI);
        fileDialog.setFilterNames(new String[]{"Triggerlisten"});
        fileDialog.setFilterExtensions(new String[]{"*.csv"});
        
        fileDialog.open();
        
        this.savePath = fileDialog.getFilterPath();
        this.saveFile = fileDialog.getFileName();
        
        if (this.textId.getText().isEmpty())
        {
        	this.textId.setText(this.saveFile);
        }
        
        String sep = System.getProperty("file.separator");
        this.textFileName.setText(this.savePath + sep + this.saveFile);
        this.button_Okay_Check();
	}

	public void Button_Cancel_Pressed()
	{
		dialogShell.close();
	}
	
	
	private void button_Okay_Check()
	{
		if (this.textDelimiter.getCharCount() == 1  &&
				this.textId.getCharCount() > 4  &&
				this.textId.getText().contains(".csv")  &&
				this.textFileName.getCharCount() > 0  &&
				this.textCommentLength.getCharCount() > 0  &&
				this.textTypeLength.getCharCount() > 0  &&
				this.textSampleRate.getCharCount() > 0) 
		{
			if (Integer.parseInt(this.textCommentLength.getText()) >= 0  &&
					Integer.parseInt(this.textTypeLength.getText()) >= 0  &&
					Double.parseDouble(this.textSampleRate.getText()) > 0)
			{
				this.buttonSave.setEnabled(true);
			}
			else
			{
				this.buttonSave.setEnabled(false);
			}
		}
		else
		{
			this.buttonSave.setEnabled(false);
		}
	}

	public void Button_Okay_Pressed()
	{
		triggerListData = new EntryData();
		
		//SignalModel signalModel = Common.getInstance().signalModel;
		
		triggerListData.setComment(this.textComment.getText());
		triggerListData.setContentClass("TRIGGER");
		triggerListData.setId(this.textId.getText());
		triggerListData.setSampleRate(Double.parseDouble(this.textSampleRate.getText()));
		triggerListData.setSource(this.textSource.getText());
		triggerListData.setSourceId(this.textSourceId.getText());
		triggerListData.setTypeLength(Integer.parseInt(this.textTypeLength.getText()));
		triggerListData.setCommentLength(Integer.parseInt(this.textCommentLength.getText()));
		
		Common.getInstance().triggerModel.addEventEntry(triggerListData, this.textDelimiter.getText(), this.saveFile, this.savePath, this.buttonDefaultEcg.getSelection()); 
		//signalModel.addTriggerList(triggerListData, this.textDelimiter.getText(), this.saveFile, this.savePath, this.buttonDefaultEcg.getSelection()); 
		
		dialogShell.close();
	}
}
