/** This Forms shows a dialog to select from different signal sources which are embedded in one
 * unisens-File
 */
package de.fzi.ekgeditor.Gui.Forms;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.unisens.SignalEntry;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.utils.TimeUtil;

public class Dialog_SignalEntry extends Dialog
{

	/**
	 * Save the result of the dialog; TempResult save the current selected Entry
	 * object
	 */
	private SignalEntry TempResult = null;

	/** Save the result of the dialog; result is used as the return variable */
	private SignalEntry result = null;

	/** List of all EKGLists */
	private List<SignalEntry> ecgList;

	/** Label for the name of the entry */
	public Label lname;

	/** Label for the least significant bit of the entry */
	public Label llsb;

	/** Label for the baseLine */
	public Label lbaseline;

	/**
	 * Label for acdZero Which value in data represents the 0 value is saved in
	 * lacdZero
	 */
	public Label lacdZero;

	/** Label for acdZeroResolution */
	public Label lacdZeroResolution;

	/** Label for the sampleRate */
	public Label lsampleRate;

	/** Label for the dataType */
	public Label ldatatype;

	/** Label for the unitType */
	public Label lunit;

	/** Label for the Length (Number of Samples */
	public Label llength;

	/** Label for the Length (Time) */
	public Label llengthT;

	public Table table_channel;

	/**
	 * Standard dialog settings constructor
	 * 
	 * @param parent
	 *            parent window of this dialog
	 * @param ecgList
	 *            List of ecgEntries to choose from
	 */
	public Dialog_SignalEntry(Shell parent, int style, List<SignalEntry> ecgList)
	{
		super(parent, style);
		this.ecgList = ecgList;
	}

	/** reference to our own dialog window */
	private Shell dialogShell = null;

	private Button buttonOkay;

	/**
	 * This method displays the dialog.
	 * 
	 * @return ID of pressed button (see SWT-Constants)
	 */
	public Object open()
	{
		Shell parent = getParent();

		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
		dialogShell.setLayout(new GridLayout(3, false));
		dialogShell.setText("Daten auswählen");

		Group groupData = new Group(dialogShell, SWT.NONE);
		groupData.setLayout(new GridLayout(1, false));
		groupData.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true, 3, 1));
		groupData.setText("EKG-Daten");

		Label labelEcgList = new Label(groupData, SWT.NONE);
		labelEcgList.setText(
				"Dieser Datensatz enthält verschiedene EKG-Daten. Wählen Sie bitte \n" +
				"aus folgender Liste den gewünschten Eintrag aus.");
		labelEcgList.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true, 1, 1));

		Combo comboEcgList = new Combo(groupData, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboEcgList.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true, 1, 1));
		for (SignalEntry se : ecgList)
		{
			comboEcgList.add(se.getId());
		}
		comboEcgList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ecgList_clicked(((Combo) e.widget).getText());
			}
		});

		Group groupEntry = new Group(dialogShell, SWT.NONE);
		groupEntry.setLayout(new GridLayout(2, false));
		groupEntry.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true, 3, 1));
		groupEntry.setText("Ausgewählte Daten");

		Label lnameH = new Label(groupEntry, SWT.NONE);
		lnameH.setText("Kommentar des Eintrags:");

		lname = new Label(groupEntry, SWT.NONE);
		lname.setText("Kein Eintrag ausgewählt");

		Label llsbH = new Label(groupEntry, SWT.NONE);
		llsbH.setText("Auflösung (LSB-Wert):");

		llsb = new Label(groupEntry, SWT.NONE);
		llsb.setText("Kein Eintrag ausgewählt");

		Label lbaselineH = new Label(groupEntry, SWT.NONE);
		lbaselineH.setText("Baseline:");

		lbaseline = new Label(groupEntry, SWT.NONE);
		lbaseline.setText("Kein Eintrag ausgewählt");

		Label lACDZeroH = new Label(groupEntry, SWT.NONE);
		lACDZeroH.setText("ADC-Zero:");

		lacdZero = new Label(groupEntry, SWT.NONE);
		lacdZero.setText("Kein Eintrag ausgewählt");

		Label lacdzeroResoltionH = new Label(groupEntry, SWT.NONE);
		lacdzeroResoltionH.setText("Quantisierung:");

		lacdZeroResolution = new Label(groupEntry, SWT.NONE);
		lacdZeroResolution.setText("Kein Eintrag ausgewählt");

		Label lsamplerateH = new Label(groupEntry, SWT.NONE);
		lsamplerateH.setText("Sample-Rate:");

		lsampleRate = new Label(groupEntry, SWT.NONE);
		lsampleRate.setText("Kein Eintrag ausgewählt");

		Label ldatatypH = new Label(groupEntry, SWT.NONE);
		ldatatypH.setText("Datentyp:");

		ldatatype = new Label(groupEntry, SWT.NONE);
		ldatatype.setText("Kein Eintrag ausgewählt");

		Label lunitH = new Label(groupEntry, SWT.NONE);
		lunitH.setText("Einheit:");

		lunit = new Label(groupEntry, SWT.NONE);
		lunit.setText("Kein Eintrag ausgewählt");

		Label llengthH = new Label(groupEntry, SWT.NONE);
		llengthH.setText("Länge (Anzahl Samples):");

		llength = new Label(groupEntry, SWT.NONE);
		llength.setText("Kein Eintrag ausgewählt");

		Label llengthHT = new Label(groupEntry, SWT.NONE);
		llengthHT.setText("Länge (Zeit):");

		llengthT = new Label(groupEntry, SWT.NONE);
		llengthT.setText("Kein Eintrag ausgewählt");

		table_channel = new Table(groupEntry, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table_channel.setHeaderVisible(true);

		TableColumn  columnChannelId = new TableColumn(table_channel, SWT.LEFT);
		columnChannelId.setText("#");
		columnChannelId.setWidth(30);
		
		TableColumn columnChannelName = new TableColumn(table_channel, SWT.LEFT);
		columnChannelName.setText("Kanäle");
		columnChannelName.setWidth(300);

		GridData g = new GridData(SWT.FILL);
		g.horizontalSpan = 2;
		g.verticalSpan = 3;
		g.minimumHeight = 100;
		g.grabExcessVerticalSpace = true;

		table_channel.setLayoutData(g);
		
		
		Label labelDummy = new Label(dialogShell, SWT.NONE);
		labelDummy.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true, false));

		
		buttonOkay = new Button(dialogShell, SWT.PUSH);
		buttonOkay.setText("OK");
		buttonOkay.setEnabled(false);
		buttonOkay.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true, false));
		buttonOkay.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_OK_Pressed();
			}
		});

		
		Button cancel = new Button(dialogShell, SWT.PUSH);
		cancel.setText("Abbruch");
		cancel.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true, false));
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button_Cancel_Pressed();
			}
		});

		dialogShell.setDefaultButton(buttonOkay);
		dialogShell.pack();
		dialogShell.open();

		Display display = parent.getDisplay();
		while (!dialogShell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	/**
	 * This method is called if button OK is pressed. It sets the return value
	 * to SWT.OK and closes the dialog.
	 * 
	 */
	public void Button_OK_Pressed()
	{
		result = TempResult;
		dialogShell.close();
	}

	/**
	 * This method is called if button cancel is pressed. It sets the return
	 * value to SWT.CANCEL and closes the dialog.
	 * 
	 */
	public void Button_Cancel_Pressed()
	{
		TempResult = null;
		result = null;
		dialogShell.close();
	}

	/**
	 * This method is called if some ECG-entry is selected.
	 * 
	 * @param s
	 *            String of the id of the ECG-entry
	 */
	public void ecgList_clicked(String s)
	{
		for (SignalEntry se : ecgList)
		{
			if (se.getId().compareTo(s) == 0)
			{
				TempResult = se;
				this.buttonOkay.setEnabled(true);
				break;
			}
		}

		lname.setText(TempResult.getComment());
		llsb.setText(Double.toString(TempResult.getLsbValue()) + " " + TempResult.getUnit());
		lbaseline.setText(Integer.toString(TempResult.getBaseline()));
		this.lacdZero.setText(Integer.toString(TempResult.getAdcZero()));
		this.lacdZeroResolution.setText(Integer.toString(TempResult.getAdcResolution()) + " bit");

		if (TempResult.getSampleRate() > 0)
		{
			this.lsampleRate.setText(Double.toString(TempResult.getSampleRate()) + " Hz");
		} else
		{
			this.lsampleRate.setText(Double.toString(1 / TempResult.getSampleRate()) + " Hz");
		}

		this.ldatatype.setText(TempResult.getDataType().toString());
		this.lunit.setText(TempResult.getUnit());
		this.llength.setText(Long.toString(TempResult.getCount()));
		long time = (long) (((double) TempResult.getCount() / (double) TempResult.getSampleRate()) * 1000);
		this.llengthT.setText(TimeUtil.getTimeString(time, Constants.withMilliSecs));

		table_channel.removeAll();
		int i = 1;
		for (String cname : TempResult.getChannelNames())
		{
			new TableItem(table_channel, SWT.LEFT).setText(new String[] {Integer.toString(i), cname});
			i++;
		}
	}
}
