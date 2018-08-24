package de.fzi.ekgeditor.Gui.Widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.unisens.EventEntry;
import org.unisens.SignalEntry;
import org.unisens.Unisens;
import org.unisens.ri.util.Utilities;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;
import de.fzi.ekgeditor.utils.TimeUtil;

public class SignalInfo implements SignalListener, TriggerModelListner{
	
	public Group signal_info_group;
	/** Label for the name of the entry */
	public Text signalCommentValue;

	/** Label for the least significant bit of the entry */
	public Label signalLsbValue;

	/** Label for the baseLine */
	public Label signalBaselineValue;

	/**
	 * Label for acdZero Which value in data represents the 0 value is saved in
	 * lacdZero
	 */
	public Label signalAdcZeroValue;

	/** Label for acdZeroResolution */
	public Label signalAdcResolutionValue;

	/** Label for the sampleRate */
	public Label signalSamplerateValue;

	/** Label for the dataType */
	public Label signalDataTypeValue;

	/** Label for the unitType */
	public Label signalUnitValue;

	/** Label for the Length (Number of Samples */
	public Label signalLenghtInSamplesValue;

	/** Label for the Length (Time) */
	public Label signalLenghtInTimeValue;

	public Table table_channel;
	
	public Group unisens_info_group;
	
	public Label measurementIdValue;
	
	public Label timestampStartValue;
	
	public Text unisensCommentValue;
	
	private Group trigger_info_group;
	
	public Text triggerCommentValue;
	
	public Label triggerSamplerateValue;
	
	public SignalInfo(TabFolder tab){
		Common.getInstance().signalModel.addSignalListener(this);
		Common.getInstance().triggerModel.addTriggerModelListener(this);
		
		TabItem tabItem_signalInfo = new TabItem(tab, SWT.NONE);
		tabItem_signalInfo.setText("Signalinformationen");
		
		Group tabitem_group_signalinfo = new Group(tab, SWT.NONE);
		tabitem_group_signalinfo.setLayout(new GridLayout(3, true));
		tabitem_group_signalinfo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tabItem_signalInfo.setControl(tabitem_group_signalinfo);
		
		signal_info_group = new Group(tabitem_group_signalinfo, SWT.NONE);
		signal_info_group.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_VERTICAL|GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 400;
		signal_info_group.setLayoutData(gd);
		signal_info_group.setText("Signalinformationen");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		gd.verticalAlignment = SWT.TOP;
		
		Label signalCommentLabel = new Label(signal_info_group, SWT.NONE);
		signalCommentLabel.setText("Kommentar des Eintrags:");
		signalCommentLabel.setLayoutData(gd);
		
		signalCommentValue = new Text(signal_info_group, SWT.READ_ONLY|SWT.H_SCROLL);
		signalCommentValue.setText("Kein Eintrag ausgewählt");
		signalCommentValue.setLayoutData(gd);
		
		Label signalLsbLabel = new Label(signal_info_group, SWT.NONE);
		signalLsbLabel.setText("Auflösung (LSB-Wert):");

		signalLsbValue = new Label(signal_info_group, SWT.NONE);
		signalLsbValue.setText("Kein Eintrag ausgewählt");

		Label signalBaselineLabel = new Label(signal_info_group, SWT.NONE);
		signalBaselineLabel.setText("Baseline:");

		signalBaselineValue = new Label(signal_info_group, SWT.NONE);
		signalBaselineValue.setText("Kein Eintrag ausgewählt");

		Label signalAdcZeroLabel = new Label(signal_info_group, SWT.NONE);
		signalAdcZeroLabel.setText("ADC-Zero:");

		signalAdcZeroValue = new Label(signal_info_group, SWT.NONE);
		signalAdcZeroValue.setText("Kein Eintrag ausgewählt");

		Label signalAdcResolutionLabel = new Label(signal_info_group, SWT.NONE);
		signalAdcResolutionLabel.setText("Quantisierung:");

		signalAdcResolutionValue = new Label(signal_info_group, SWT.NONE);
		signalAdcResolutionValue.setText("Kein Eintrag ausgewählt");

		Label signalSamplerateLabel = new Label(signal_info_group, SWT.NONE);
		signalSamplerateLabel.setText("Sample-Rate:");

		signalSamplerateValue = new Label(signal_info_group, SWT.NONE);
		signalSamplerateValue.setText("Kein Eintrag ausgewählt");

		Label signalDataTypeLabel = new Label(signal_info_group, SWT.NONE);
		signalDataTypeLabel.setText("Datentyp:");

		signalDataTypeValue = new Label(signal_info_group, SWT.NONE);
		signalDataTypeValue.setText("Kein Eintrag ausgewählt");

		Label signalUnitLabel = new Label(signal_info_group, SWT.NONE);
		signalUnitLabel.setText("Einheit:");

		signalUnitValue = new Label(signal_info_group, SWT.NONE);
		signalUnitValue.setText("Kein Eintrag ausgewählt");

		Label signalLenghtInSamplesLabel = new Label(signal_info_group, SWT.NONE);
		signalLenghtInSamplesLabel.setText("Länge (Anzahl Samples):");

		signalLenghtInSamplesValue = new Label(signal_info_group, SWT.NONE);
		signalLenghtInSamplesValue.setText("Kein Eintrag ausgewählt");

		Label signalLenghtInTimeLabel = new Label(signal_info_group, SWT.NONE);
		signalLenghtInTimeLabel.setText("Länge (Zeit):");

		signalLenghtInTimeValue = new Label(signal_info_group, SWT.NONE);
		signalLenghtInTimeValue.setText("Kein Eintrag ausgewählt");

		table_channel = new Table(signal_info_group, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table_channel.setHeaderVisible(true);

		TableColumn  columnChannelId = new TableColumn(table_channel, SWT.LEFT);
		columnChannelId.setText("#");
		columnChannelId.setWidth(30);
		
		TableColumn columnChannelName = new TableColumn(table_channel, SWT.LEFT);
		columnChannelName.setText("Kanäle");
		columnChannelName.setWidth(330);

		GridData g = new GridData(SWT.FILL);
		g.horizontalSpan = 2;
		g.verticalSpan = 3;
		g.minimumHeight = 100;
		g.grabExcessVerticalSpace = true;
		table_channel.setLayoutData(g);
		
		unisens_info_group = new Group(tabitem_group_signalinfo, SWT.NONE);
		unisens_info_group.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_VERTICAL|GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 400;
		unisens_info_group.setLayoutData(gd);
		unisens_info_group.setText("Datensatzname");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		gd.verticalAlignment = SWT.TOP;
		
		Label unisensCommentLabel = new Label(unisens_info_group, SWT.NONE);
		unisensCommentLabel.setText("Kommentar:");
		unisensCommentLabel.setLayoutData(gd);
		
		unisensCommentValue = new Text(unisens_info_group, SWT.READ_ONLY|SWT.H_SCROLL);
		unisensCommentValue.setLayoutData(gd);
		unisensCommentValue.setText("Kein Eintrag ausgewählt");
		
		Label measermentIdLabel = new Label(unisens_info_group, SWT.NONE);
		measermentIdLabel.setText("Measerment Id:");

		measurementIdValue = new Label(unisens_info_group, SWT.NONE);
		measurementIdValue.setText("Kein Eintrag ausgewählt");
		
		Label timestampStartLabel = new Label(unisens_info_group, SWT.NONE);
		timestampStartLabel.setText("Timestamp Start:");

		timestampStartValue = new Label(unisens_info_group, SWT.NONE);
		timestampStartValue.setText("Kein Eintrag ausgewählt");
		
		trigger_info_group = new Group(tabitem_group_signalinfo, SWT.NONE);
		trigger_info_group.setLayout(new GridLayout(2, true));
		gd = new GridData(GridData.FILL_VERTICAL|GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 400;
		trigger_info_group.setLayoutData(gd);
		trigger_info_group.setText("Triggerinformationen");
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 20;
		gd.verticalAlignment = SWT.TOP;
		
		Label triggerCommentLabel = new Label(trigger_info_group, SWT.NONE);
		triggerCommentLabel.setText("Kommentar des Eintrags:");
		triggerCommentLabel.setLayoutData(gd);
		
		triggerCommentValue = new Text(trigger_info_group, SWT.READ_ONLY|SWT.H_SCROLL);
		triggerCommentValue.setText("Kein Eintrag ausgewählt");
		triggerCommentValue.setLayoutData(gd);
		
		Label triggerSamplerateLabel = new Label(trigger_info_group, SWT.NONE);
		triggerSamplerateLabel.setText("Sample-Rate:");

		triggerSamplerateValue = new Label(trigger_info_group, SWT.NONE);
		triggerSamplerateValue.setText("Kein Eintrag ausgewählt");
	}
	
	@Override
	public void signalChanged(SignalEvent e) {
		if(e.newSignal){
			SignalEntry signalEntry = ((SignalModel)e.getSource()).getSignal();
			setSignal(signalEntry);
			setUnisens(signalEntry.getUnisens());
		}
	}
	
	@Override
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent) {
		TriggerModel triggerModel = Common.getInstance().triggerModel;
		EventEntry activeTriggerEntry = triggerModel.getActiveEventEntry();
		if(activeTriggerEntry != null){
			trigger_info_group.setText(activeTriggerEntry.getId());
			triggerCommentValue.setText(activeTriggerEntry.getComment() != null ? activeTriggerEntry.getComment() : "");
			triggerSamplerateValue.setText(Double.toString(activeTriggerEntry.getSampleRate()));
		}else{
			trigger_info_group.setText("Triggerinformationen");
			triggerCommentValue.setText("Kein Eintrag ausgewählt");
			triggerSamplerateValue.setText("Kein Eintrag ausgewählt");
		}
	}

	private void setSignal(SignalEntry signalEntry){
		signal_info_group.setText(signalEntry.getId());
		signalCommentValue.setText(signalEntry.getComment() != null ? signalEntry.getComment() : "");
		signalLsbValue.setText(Double.toString(signalEntry.getLsbValue()));
		signalBaselineValue.setText(Integer.toString(signalEntry.getBaseline()));
		this.signalAdcZeroValue.setText(Integer.toString(signalEntry.getAdcZero()));
		this.signalAdcResolutionValue.setText(Integer.toString(signalEntry.getAdcResolution()) + " bit");

		if (signalEntry.getSampleRate() > 0){
			this.signalSamplerateValue.setText(Double.toString(signalEntry.getSampleRate()) + " Hz");
		} else{
			this.signalSamplerateValue.setText(Double.toString(1 / signalEntry.getSampleRate()) + " Hz");
		}

		this.signalDataTypeValue.setText(signalEntry.getDataType().toString());
		this.signalUnitValue.setText(signalEntry.getUnit() != null ? signalEntry.getUnit() : "");
		this.signalLenghtInSamplesValue.setText(Long.toString(signalEntry.getCount()));
		long time = (long) (((double) signalEntry.getCount() / (double) signalEntry.getSampleRate()) * 1000);
		this.signalLenghtInTimeValue.setText(TimeUtil.getTimeString(time, Constants.withMilliSecs));

		table_channel.removeAll();
		int i = 1;
		for (String cname : signalEntry.getChannelNames()){
			new TableItem(table_channel, SWT.LEFT).setText(new String[] {Integer.toString(i), cname});
			i++;
		}
	}
	
	private void setUnisens(Unisens unisens){
		String unisensPath = unisens.getPath().substring(0, unisens.getPath().length() - 1);
		String unisensFolderName = "";
		if(unisensPath.lastIndexOf(System.getProperty("file.separator")) != -1)
			unisensFolderName = unisensPath.substring(1 + unisensPath.lastIndexOf(System.getProperty("file.separator")));
		unisens_info_group.setText(unisensFolderName);
		measurementIdValue.setText(unisens.getMeasurementId() != null ? unisens.getMeasurementId() : "n/a");
		unisensCommentValue.setText(unisens.getComment() != null ? unisens.getComment() : "" );
		if(unisens.getTimestampStart() != null)
			timestampStartValue.setText(Utilities.convertDateToString(unisens.getTimestampStart()));
		else
			timestampStartValue.setText("n/a");
	}
}
