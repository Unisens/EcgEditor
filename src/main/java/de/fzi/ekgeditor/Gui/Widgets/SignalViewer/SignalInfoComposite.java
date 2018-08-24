/**
 * This class is part of the SignalViewer-Widget and shows all the information
 * underneath the graphics-canvas
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Widgets.SignalViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;
import de.fzi.ekgeditor.utils.TimeUtil;

public class SignalInfoComposite extends Composite implements SignalListener, ViewListener
{

	// private static int PRECISION = 1000;

	/** link to underlying dataModel */
	private SignalViewerModel dataModel = null;

	/** standard unit used */
	private String unit = "--";

	/** Label for Time */
	private Label currentTimeLabel = null;

	private Label heartFrequency = null;

	/** Label for Channel A */
	private Label channelA = null;

	/** Label for Channel B */
	private Label channelB = null;

	/** Label for Channel C */
	private Label channelC = null;

	/** Label for TimeValue */
	private Text currentTime = null;

	/** Label for Channel A Value */
	private Text channelAValue = null;

	/** Label for Channel B Value */
	private Text channelBValue = null;

	/** Label for Channel C Value */
	private Text channelCValue = null;

	private String noSignal = "----------------";

	/**
	 * Standard constructor
	 * 
	 * @param parent
	 *            parent composite (normally some form)
	 */
	public SignalInfoComposite(Composite parent, SignalViewerModel dataModel)
	{
		super(parent, SWT.NULL);
		this.dataModel = dataModel;
		initGUI();
		dataModel.signalModel.addSignalListener(this);
		dataModel.addViewListener(this);
	}

	/** inits all the labels with some default values */
	private void initChannelNames()
	{
		String nameA = noSignal;
		if (dataModel.getNumberOfVisibleChannels() >= 1)
		{
			nameA = dataModel.signalModel.getChannelName(dataModel.getRealChannelNumber(0));
		}
		this.setChannelAValue(null);
		String nameB = noSignal;
		if (dataModel.getNumberOfVisibleChannels() >= 2)
		{
			nameB = dataModel.signalModel.getChannelName(dataModel.getRealChannelNumber(1));
		}
		this.setChannelBValue(null);
		String nameC = noSignal;
		if (dataModel.getNumberOfVisibleChannels() >= 3)
		{
			nameC = dataModel.signalModel.getChannelName(dataModel.getRealChannelNumber(2));
		}
		this.setChannelCValue(null);

		if (dataModel.signalModel.isSignalLoaded())
		{
			unit = dataModel.signalModel.getUnit();
		}

		channelA.setText(nameA);
		channelB.setText(nameB);
		channelC.setText(nameC);
	}

	/** creates the gui elements */
	private void initGUI()
	{
		GridLayout gridLayout = new GridLayout(6, true);
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;

		this.setLayout(gridLayout);

		currentTimeLabel = new Label(this, SWT.FLAT);
		currentTimeLabel.setText("Aktuelle Cursor-Position: ");

		Label heartFrequencyLabel = new Label(this, SWT.LEFT);
		heartFrequencyLabel.setText("Herzfrequenz:");

		channelA = new Label(this, SWT.FLAT);
		channelB = new Label(this, SWT.FLAT);
		channelC = new Label(this, SWT.FLAT);
		channelA.setText("Text");

		initChannelNames();

		currentTime = new Text(this, SWT.NONE);
		currentTime.setEditable(false);
		currentTime.setText("-- ms");

		this.heartFrequency = new Label(this, SWT.NONE);
		heartFrequency.setText(Constants.undefined);

		String unittext = "-- " + unit;
		channelAValue = new Text(this, SWT.NONE);
		channelAValue.setEditable(false);
		channelAValue.setText(unittext);

		channelBValue = new Text(this, SWT.NONE);
		channelBValue.setEditable(false);
		channelBValue.setText(unittext);

		channelCValue = new Text(this, SWT.NONE);
		channelCValue.setEditable(false);
		channelCValue.setText(unittext);

		GridData gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 2;
		currentTimeLabel.setLayoutData(gridData);

		gridData = new GridData(GridData.BEGINNING);
		channelA.setLayoutData(gridData);

		gridData = new GridData(GridData.BEGINNING);
		channelB.setLayoutData(gridData);

		gridData = new GridData(GridData.BEGINNING);
		channelC.setLayoutData(gridData);

		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		currentTime.setLayoutData(gridData);

		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		channelAValue.setLayoutData(gridData);

		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		channelBValue.setLayoutData(gridData);

		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		channelCValue.setLayoutData(gridData);

		this.layout();

	}

	/** write some new time to the time-label */
	private void setCurrentTime(String s)
	{
		this.currentTime.setText(s);
	}

	/** writes a new value for channel A */
	private void setChannelAValue(Double currentValue)
	{
		String t = Common.Double2String(currentValue) + " " + unit;
		if (channelAValue != null)
		{
			this.channelAValue.setText(t);
		}
	}

	/** writes a new value for channel B */
	private void setChannelBValue(Double currentValue)
	{
		if (channelBValue != null)
			this.channelBValue.setText("" + Common.Double2String(currentValue) + " " + unit);
	}

	/** writes a new value for channel C */
	private void setChannelCValue(Double currentValue)
	{
		if (channelCValue != null)
			this.channelCValue.setText("" + Common.Double2String(currentValue) + " " + unit);
	}

	public void setHeartFrequency(double heartFrequency, boolean isSet)
	{
		if (isSet)
		{
			double round = (double) Common.signalRound;
			// heart frequency has always one decimal place
			round = 10.0;
			heartFrequency = Math.round(heartFrequency * round) / round;
			this.heartFrequency.setText(Double.toString(heartFrequency));
		} else
		{
			this.heartFrequency.setText(Constants.undefined);
		}
	}

	/**
	 * this method is called if the mouse is moved
	 * 
	 * @param mouseMoveXPosition
	 *            new X position of the mouse.
	 */
	public void mouseMoved(int mouseMoveXPosition)
	{
		double samplerate = Common.getInstance().signalModel.getSamplingFrequency();
		long offset  = dataModel.getOffset();
		
		if(mouseMoveXPosition >= dataModel.getReferencePulseWidth()){
			long t = dataModel.getTimeinMillisecsForPixel(mouseMoveXPosition);
			long sn = dataModel.getSampleNumber(mouseMoveXPosition, samplerate);
//			String time = TimeUtil.getFullTimeString(t + offset) + " Uhr, Sample #" + TimeUtil.getSampleString(sn);
			String time = TimeUtil.getFullDateString(t + offset, 0);
			
			setCurrentTime(time);
			// System.out.println(dataModel.getNumberOfChannels());
			if (dataModel.getNumberOfVisibleChannels() >= 1)
				setChannelAValue(dataModel.getValue(dataModel.getRealChannelNumber(0), mouseMoveXPosition, samplerate));
			if (dataModel.getNumberOfVisibleChannels() >= 2)
				setChannelBValue(dataModel.getValue(dataModel.getRealChannelNumber(1), mouseMoveXPosition, samplerate));
			if (dataModel.getNumberOfVisibleChannels() >= 3)
				setChannelCValue(dataModel.getValue(dataModel.getRealChannelNumber(2), mouseMoveXPosition, samplerate));
		}
	}

	/**
	 * this method is called if the underlying signal has changed.
	 * 
	 * @param e
	 *            Extra SignalEvent values
	 */
	public void signalChanged(SignalEvent e)
	{
		initChannelNames();
	}

	/*
	 * public void setSelectionStart(int selectionStartTime){
	 * this.selectionStartTime = selectionStartTime;
	 * this.selectionStart.setText(""+selectionStartTime+" ms"); } public void
	 * setSelectionMinValue(double minValue){ this.selectionMinValue = minValue ;
	 * this.minValue.setText(""+minValue+" mV"); } public void
	 * setSelectionEnd(int selectionEndTime){ this.selectionEndTime =
	 * selectionEndTime; this.selectionEnd.setText(""+selectionEndTime+" ms"); }
	 * public void setSelectionMaxValue(double maxValue){ this.selectionMaxValue =
	 * maxValue; this.maxValue.setText(""+maxValue+" mV"); } public void
	 * setSelectionDauern(){
	 * this.selectionDauern.setText(""+Math.abs(selectionStartTime -
	 * selectionEndTime)+" ms"); } public void setSelectionAmplitude(){ int amp =
	 * (int)(PRECISION*Math.abs(selectionMaxValue - selectionMinValue));
	 * this.selectionAmplitude.setText(""+((double)amp)/PRECISION+" mV"); }
	 */

	public void zoomChanged(ViewEvent e)
	{
	}

	public void viewSectionChanged(ViewEvent e)
	{
	}

	public void channelViewChanged(ViewEvent e)
	{
		initChannelNames();
	}

}
