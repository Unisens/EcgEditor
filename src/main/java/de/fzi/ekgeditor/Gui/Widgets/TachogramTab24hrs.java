package de.fzi.ekgeditor.Gui.Widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.unisens.Event;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerCanvas;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerColors;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.FontsManager;
import de.fzi.ekgeditor.data.LayerBounds;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.data.previewData;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;
import de.fzi.ekgeditor.utils.TimeUtil;

public class TachogramTab24hrs implements TriggerModelListner, ViewListener, MouseMoveListener{
	private Button playStop;
	private Scale playSpeedScale;
	private Canvas tachogramCanvas;
	private boolean redraw;
	private TriggerModel triggerModel;
	private SignalViewerModel signalViewerModel;
	private Image graphImage;
	private final int layerLength = 1 * 60 * 60 * 24; // in one layer, we have 12 min of
	private final int layers = 1; // we have 5 layers
	private int[][] layerCenter; // middle line for one layer
	private int[] layerGround;
	//private double heightMulti = 0;
	private double widthMulti[];
	private long lengthInSamplesPerLayer[];
	/** modular for amplitude. Paint every x-values a grid line. */
	//private final int verticalBarAfterSecs = 10;
	//private double verticalBarAfterPixel = 0;
	/** minimum value that can occur in this window */
//	private double minValue = -5;
	/** maximum value that can occur in this window */
	//private double maxValue = 5;
	private long[] sampleOffset = new long[]{0,0};
	private int[] availableAmplitudes = new int[]{1, 2, 5, 10};
	private int currentAmplitudeIndex = 0;
	private long selectedStartTime;
	private long selectedEndTime;
	private int maxGapInSeconds = 5;
	
	public TachogramTab24hrs(TabFolder tabFolder){
		init(tabFolder);
	}
	
	private void init(TabFolder tabFolder){
		triggerModel = Common.getInstance().triggerModel;
		triggerModel.addTriggerModelListener(this);
		signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		signalViewerModel.addViewListener(this);
		maxGapInSeconds = Integer.parseInt(Common.getInstance().reg.reg.getProperty(Registry.prop_tachogramMaxinalGapInSeconds));
		TabItem tachogramTabItem = new TabItem(tabFolder, SWT.NONE);
		tachogramTabItem.setText("Tachogramm (24h)");
		Group tachogramGroup = new Group(tabFolder, SWT.NONE);
		tachogramGroup.setLayout(new GridLayout(8, false));
		tachogramTabItem.setControl(tachogramGroup);

		Button goLeft = new Button(tachogramGroup, SWT.PUSH);
		goLeft.setText("Rückwärts");
		goLeft.setLayoutData(new GridData(SWT.NONE, SWT.LEFT, false, false, 1, 1));
		goLeft.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				Button_goLeft_Pressed();
			}
		});

		Button goCurrent = new Button(tachogramGroup, SWT.PUSH);
		goCurrent.setText("Zentrieren");
		goCurrent.setLayoutData(new GridData(SWT.NONE, SWT.LEFT, false, false, 1, 1));
		goCurrent.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_goCurrent_Pressed();
			}
		});

		Button goRight = new Button(tachogramGroup, SWT.PUSH);
		goRight.setText("Vorwärts");
		goRight.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		goRight.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_goRight_Pressed();
			}
		});
		
		playStop = new Button(tachogramGroup, SWT.PUSH);
		playStop.setText("Abspielen");
		playStop.setToolTipText("Automatisch blättern");
		playStop.setEnabled(false);
		playStop.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
		playStop.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_playStop_Pressed();
			}
		});
		Label slowerPlayLabel = new Label(tachogramGroup, SWT.NONE);
		slowerPlayLabel.setText("langsamer");
		
		playSpeedScale = new Scale(tachogramGroup, SWT.HORIZONTAL);
		playSpeedScale.setToolTipText("Geschwindigkeit");
		GridData gd = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
		gd.widthHint = 100;
		gd.heightHint = 25;
		playSpeedScale.setLayoutData(gd);
		playSpeedScale.setSize(100, 10);
		playSpeedScale.setMinimum(0);
		playSpeedScale.setMaximum(4);
		playSpeedScale.setPageIncrement(1);
		playSpeedScale.setSelection(2);
		playSpeedScale.setEnabled(false);
				
		playSpeedScale.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_playSpeedScale_Pressed();
			}
		});
		
		Label fasterPlayLabel = new Label(tachogramGroup, SWT.NONE);
		fasterPlayLabel.setText("schneller");
		
		Button changeAmplitude = new Button(tachogramGroup, SWT.PUSH);
		changeAmplitude.setText("Amplitude");
		changeAmplitude.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false, 1, 1));
		changeAmplitude.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_changeAmplitude_Pressed();
			}
		});
		tachogramCanvas = new Canvas(tachogramGroup, SWT.NONE | SWT.NO_BACKGROUND); 
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 8;
		tachogramCanvas.setLayoutData(gridData);
		tachogramCanvas.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e){
				paint(e);
			}
		});
		tachogramCanvas.addMouseListener(new MouseAdapter(){
			public void mouseDoubleClick(MouseEvent e)
			{
				mouseClick(e);
			}
		});
		tachogramCanvas.addMouseMoveListener(this);
		tachogramCanvas.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent arg0){
				// Nothing to do
			}

			public void controlResized(ControlEvent arg0){
				resized();
			}
		});
	}

	private void paint(PaintEvent e) {
		if(triggerModel.getActiveEventEntry() != null){
			initPaint();
		}
		if (redraw) {
			if (graphImage != null){
				graphImage.dispose();
			}

			Rectangle rect = new Rectangle(0, 0, tachogramCanvas.getClientArea().width, tachogramCanvas.getClientArea().height);
			graphImage = new Image(tachogramCanvas.getDisplay(), rect);
			GC drawableGC = new GC(graphImage);
			drawableGC.setBackground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorBackground));
			drawableGC.fillRectangle(rect);

			if (triggerModel.getActiveEventEntry() != null){
				paintGrid(drawableGC);
				paintTachogram(drawableGC);
			} else {
				drawableGC.setForeground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorError));
				drawableGC.drawLine(0, 0, tachogramCanvas.getClientArea().width, tachogramCanvas.getClientArea().height);
				drawableGC.setFont(Common.getInstance().fontsManager.getSystemFont(FontsManager.fontSignalNotLoaded));
				drawableGC.drawText("Triggerliste nicht geladen", 0, 0);
			}

			drawableGC.dispose();
		}

		// Just plot graph image
		redraw = false;

		Image currentImage = new Image(Display.getDefault(), graphImage, SWT.IMAGE_COPY);
		GC drawableGC = new GC(currentImage);
		drawableGC.drawImage(currentImage, 0, 0);
		paintActualView(drawableGC);
		drawableGC.dispose();
		drawableGC = null;

		e.gc.drawImage(currentImage, 0, 0);
		currentImage.dispose();
		currentImage = null;
		e.gc.dispose();
	}
	private void initPaint(){
		// spacing between two layers, top and bottom margin are calculated relatively to widget height
		int layerSpacing = (int) Math.round(tachogramCanvas.getClientArea().height * 0.01); 
		int layerMargin = (int) Math.round(tachogramCanvas.getClientArea().height * 0.04);
		int height = tachogramCanvas.getClientArea().height - 2 * layerMargin - (layers - 1) * layerSpacing;
		int countOfVisibleTriggerlist = triggerModel.getCountOfVisibleTriggerlist();
		double layerHeight = (double) ((double) height / (double) (layers));

		this.layerCenter = new int[layers][countOfVisibleTriggerlist];
		this.layerGround = new int[layers];

		for (int layerCounter = 0; layerCounter < layers; layerCounter++){
			double layerGround = (layerHeight * layerCounter) + layerMargin + layerSpacing * layerCounter;
			this.layerGround[layerCounter] = (int) Math.round(layerGround);

			for (int triggerNumber = 0; triggerNumber < countOfVisibleTriggerlist; triggerNumber++){
				double layerCenter = (double)(( triggerNumber + 1)/(double)( countOfVisibleTriggerlist) * layerHeight) + layerGround;
				this.layerCenter[layerCounter][triggerNumber] = (int) Math.round(layerCenter);
			}
		}

		// Pixel-Umrechnung innerhalb eines Layers:
		//heightMulti = (layerHeight / ((double) (maxValue - minValue)));
		long activeTriggerlistLengthInSamplesPerLayer = Math.round(Math.floor(triggerModel.getActiveEventEntry().getSampleRate() * this.layerLength));
		long secondaryTriggerlistLengthInSamplesPerLayer = triggerModel.getSecondaryEventEntry() != null ? Math.round(Math.floor(triggerModel.getSecondaryEventEntry().getSampleRate() * this.layerLength)) : 0;
		lengthInSamplesPerLayer = new long[]{activeTriggerlistLengthInSamplesPerLayer, secondaryTriggerlistLengthInSamplesPerLayer};
		double activeTriggerlistWidthMulti = ((double) tachogramCanvas.getClientArea().width / (double) (lengthInSamplesPerLayer[0]));
		double secondaryTriggerlistWidthMulti = triggerModel.getSecondaryEventEntry() != null ? ((double) tachogramCanvas.getClientArea().width / (double) (lengthInSamplesPerLayer[1])) : 0;
		
		widthMulti = new double[]{activeTriggerlistWidthMulti, secondaryTriggerlistWidthMulti};
		//verticalBarAfterPixel = triggerModel.getActiveEventEntry().getSampleRate() * this.verticalBarAfterSecs;

	}

	private void paintGrid(GC gc){
		gc.setForeground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorPreviewLine));
		long sampleNumber = sampleOffset[0];
		for(int i = 0; i < 6; i++){
			int verticalLinePixel = pixelXForSampleNumber(sampleNumber, 0);
			gc.drawLine(verticalLinePixel, 0, verticalLinePixel, tachogramCanvas.getClientArea().height);
			gc.setBackground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorBackground));
			long timeInMilliSecs = (long)(1000 * sampleNumber / triggerModel.getActiveEventEntry().getSampleRate());
			gc.drawString(TimeUtil.getTimeString(timeInMilliSecs, Constants.NoMilliSecs), verticalLinePixel, 0);
			sampleNumber += (long)((this.layerLength / 6) * triggerModel.getActiveEventEntry().getSampleRate());
		}
	}
	
	private void paintTachogram(GC gc) {
		int countOfVisibleTriggerlist = triggerModel.getCountOfVisibleTriggerlist();
		for (int layerCounter = 0; layerCounter < layers; layerCounter++){
			gc.setForeground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorTriggerForeground));
			for (int triggerlistNumber = 0; triggerlistNumber < countOfVisibleTriggerlist; triggerlistNumber++){
				long startSample = sampleOffset[triggerlistNumber] + lengthInSamplesPerLayer[triggerlistNumber] * layerCounter;
				long endSample = lengthInSamplesPerLayer[triggerlistNumber] + startSample;
				
				Point lastPoint = null;
				List<Event> triggers;
				EventEntry triggerEntry;
				if(triggerlistNumber == 0){
					triggerEntry = triggerModel.getActiveEventEntry();
					triggers = triggerModel.readEvents(startSample, endSample);
					gc.setAlpha(255);
				}else{
					triggerEntry = triggerModel.getSecondaryEventEntry();
					triggers = triggerModel.readSecondaryTriggerlistEvents(startSample, endSample);
					gc.setAlpha(100);
				}
				
				for (int i = 1; i < triggers.size() ; i++){
					Event startTrigger = triggers.get(i - 1);
					Event endTrigger = triggers.get(i);
					
					long x = endTrigger.getSampleStamp();
					long y = endTrigger.getSampleStamp() - startTrigger.getSampleStamp();
					int  gapBetweenStartAndEndTriggerInSeconds= (int) ((double)y / triggerEntry.getSampleRate()) ; 
					if(gapBetweenStartAndEndTriggerInSeconds < maxGapInSeconds){
						Point currentPoint = new Point((int)Math.round(pixelXForSampleNumber(x, triggerlistNumber)), (int)Math.round(signalValueToPixelY(y, layerCounter, triggerlistNumber)));
						if (lastPoint != null ){
							gc.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
						}
						lastPoint = currentPoint;
					}else{
						lastPoint = new Point((int)Math.round(pixelXForSampleNumber(x, triggerlistNumber)), (int)Math.round(signalValueToPixelY(0, layerCounter, triggerlistNumber)));;
					}	
					
				}
			}
			gc.setAlpha(255);
		}
	}
	
	private void paintActualView(GC gc){
		if (this.triggerModel.getActiveEventEntry() != null){
			SignalViewerCanvas signalViewerCanvas = this.signalViewerModel.signalViewerComposite.signalViewerCanvas;
			Color selectionLineColor = Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorSelectionLine);
			gc.setBackground(selectionLineColor);
			gc.setAlpha(50);

			selectedStartTime = signalViewerCanvas.getCurrentStartTime();
			selectedEndTime = signalViewerCanvas.getCurrentEndTime();

			previewData start = calculatePageAndLayer(selectedStartTime);
			previewData end = calculatePageAndLayer(selectedEndTime);
			int currentPage = this.getPageForSample(this.sampleOffset[0], 0);

			int layerCounter = start.layerNumber;
			int pageCounter = start.pageNumber;
			
			while (pageCounter <= end.pageNumber){
				while ((layerCounter < layers)){
					if ((pageCounter == end.pageNumber) & (layerCounter > end.layerNumber)){
						break;
					}
					if (pageCounter == currentPage){
						LayerBounds layerBounds = getLayerStartEnd(layerCounter);
						long layerTimeStart = (pageCounter * layers + layerCounter) * layerLength * 1000;
						long layerTimeEnd = ((pageCounter) * layers + layerCounter + 1) * layerLength * 1000 - 1;
						
						if (layerBounds != null){
							int x_start = calculatePixel(selectedStartTime, layerTimeStart, true, 0);
							int x_end = calculatePixel(selectedEndTime, layerTimeEnd, false, 0);
							int width = x_end - x_start;
							
							if (width < 0){
								width = this.tachogramCanvas.getClientArea().width;
							}

							gc.fillRectangle(x_start, layerBounds.layerStart, width, layerBounds.layerEnd - layerBounds.layerStart);
						}
					}

					layerCounter++;
				} // end while

				layerCounter = 0;
				pageCounter++;

			}
		}
	}
	
	private previewData calculatePageAndLayer(long time){
		int layerNumber = this.getLayerForTime(time);
		int pageNumber = this.getPageForTime(time);

		return new previewData(pageNumber, layerNumber);
	}
	
	public int getLayerForTime(long time){
		int layerNumber = 0;
		long layerLengthInMilliSecs = this.layerLength * 1000;
		while( time >= layerLengthInMilliSecs * layers){
			time = time - layerLengthInMilliSecs * layers;
		}

		while (time > layerLengthInMilliSecs){
			time = time - layerLengthInMilliSecs;
			layerNumber++;
		}

		return layerNumber;
	}

	public int getPageForTime(long time){
		int page = 0;
		long layerLengthInMilliSecs = this.layerLength * 1000;
		while (time > layerLengthInMilliSecs * layers){
			time = time - layerLengthInMilliSecs * layers;
			page++;
		}

		return page;
	}
	
	public int getPageForSample(long sampleNumber, int triggerNumber){
		int page = 0;
		while (sampleNumber >= this.lengthInSamplesPerLayer[triggerNumber] * layers){
			sampleNumber = sampleNumber - this.lengthInSamplesPerLayer[triggerNumber] * layers;
			page++;
		}

		return page;
	}
	
	private LayerBounds getLayerStartEnd(int layerNumberStart){
		if (layerNumberStart < layers){
			int layerStart = this.layerGround[layerNumberStart];

			int layerEnd;
			if ((layerNumberStart + 1) < layers){
				layerEnd = this.layerGround[layerNumberStart + 1];
			} else {
				layerEnd = this.tachogramCanvas.getClientArea().height;
			}

			return new LayerBounds(layerStart, layerEnd);
		} else {
			return null;
		}
	}
	
	private int calculatePixel(long selectedTime, long boundsTime, boolean start, int triggerlistNumber){
		if (start){
			if (selectedTime < boundsTime){
				selectedTime = boundsTime;
			}
		} else {
			if (selectedTime > boundsTime){
				selectedTime = boundsTime;
			}
		}
		int x = pixelXForTime(selectedTime, triggerlistNumber);
		if (x < 0){
			x = 0;
		}
		return x;
	}
	
	private int pixelXForTime(long timeInMilliSec, int triggerlistNumber){
		double sampleRate = triggerlistNumber == 0 ? triggerModel.getActiveEventEntry().getSampleRate() : triggerModel.getSecondaryEventEntry().getSampleRate();
		long sampleNumber = (long)(timeInMilliSec * sampleRate / 1000);
		return this.pixelXForSampleNumber(sampleNumber, triggerlistNumber);
	}
		
	private int pixelXForSampleNumber(long sampleNumber, int triggerlistNumber){
		sampleNumber = sampleNumber - sampleOffset[triggerlistNumber];
		if (lengthInSamplesPerLayer[triggerlistNumber] > 0)
		{
			while (sampleNumber >= this.lengthInSamplesPerLayer[triggerlistNumber])
			{
				sampleNumber = sampleNumber - lengthInSamplesPerLayer[triggerlistNumber];
			}
		}
		double valuePoint = (double) ((sampleNumber) * widthMulti[triggerlistNumber]);

		return (int) Math.round(valuePoint);
	}
	
	private double signalValueToPixelY(double value, int layer, int triggerlistNumber){
		return this.layerCenter[layer][triggerlistNumber] - value/availableAmplitudes[currentAmplitudeIndex];
	}

	private void resized(){
		redraw = true;
		tachogramCanvas.redraw();
	}

	public void mouseClick(MouseEvent e){
		if (this.triggerModel.getActiveEventEntry() != null){
			long sampleNumber = signalSampleNumberForPixelPosition(e.x, e.y);
			this.signalViewerModel.goToSample(sampleNumber);
		}
	}
	
	public void mouseMove(MouseEvent e){
		String txt = "";
		if (triggerModel.getActiveEventEntry() != null){
			long sampleNumber = signalSampleNumberForPixelPosition(e.x, e.y);
			txt = "Sample: " + Long.toString(sampleNumber) + "\n";
			long timeInMilliSeconds = this.signalViewerModel.signalModel.getTimeInMilliSecsForSample(sampleNumber);
			txt = txt + "Zeit: " + TimeUtil.getFullTimeString(timeInMilliSeconds) + " \n\n";
		}
		this.tachogramCanvas.setToolTipText(txt);
	}
	
	public long signalSampleNumberForPixelPosition(int x, int y){
		double sampleNumber = sampleOffset[0];
		if (widthMulti[0] != 0){
			sampleNumber = (double) (((double) x / widthMulti[0]) + (double) sampleOffset[0]);
		}

		int bestlayer = this.getLayerForPixelY(y);
		sampleNumber += (this.lengthInSamplesPerLayer[0] * bestlayer);
		sampleNumber *= (signalViewerModel.signalModel.getSamplingFrequency() / triggerModel.getActiveEventEntry().getSampleRate());
		return Math.round(Math.floor(sampleNumber));
	}
	
	public int getLayerForPixelY(int pixelY){
		int bestlayer = 0;
		int bestDiff = Integer.MAX_VALUE;

		if (this.layerCenter != null){
			if (layers <= this.layerCenter.length){
				for (int layerCount = 0; layerCount < layers; layerCount++){
					int myY = this.layerCenter[layerCount][0];
					int diff = Math.abs(pixelY - myY);
					if (diff < bestDiff){
						bestDiff = diff;
						bestlayer = layerCount;
					}
				}
			}
		}

		return bestlayer;
	}
	
	protected void Button_playStop_Pressed() {
		
	}

	protected void Button_goRight_Pressed() {
		boolean changed = false;
		for(int i = 0 ; i < sampleOffset.length; i++){
			long newSampleOffset = this.sampleOffset[i] + this.lengthInSamplesPerLayer[i] * layers;
			if(signalViewerModel.signalModel.getMaxSamp() > newSampleOffset){
				this.sampleOffset[i] = newSampleOffset;
				changed = true;
			}	
		}
		if(changed)
			resized();
	}

	protected void Button_goCurrent_Pressed() {
		int page = this.getPageForTime(this.signalViewerModel.signalViewerComposite.signalViewerCanvas.getCurrentStartTime());
		for(int i = 0 ; i < sampleOffset.length; i++){
			sampleOffset[0] = page * this.lengthInSamplesPerLayer[0] * layers;
		}
		resized();
	}

	protected void Button_goLeft_Pressed() {
		for(int i = 0 ; i < sampleOffset.length; i++){
			this.sampleOffset[i] = this.sampleOffset[i] - this.lengthInSamplesPerLayer[i] * layers;
			if (sampleOffset[i] < 0){
				sampleOffset[i] = 0;
			}
		}
		resized();
		
	}

	protected void Button_playSpeedScale_Pressed() {
		
	}

	private void Button_changeAmplitude_Pressed() {
		currentAmplitudeIndex = (currentAmplitudeIndex + 1) % availableAmplitudes.length;
		resized();
	}

	@Override
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent) {
		resized();
	}

	@Override
	public void channelViewChanged(ViewEvent e) {
		
	}

	@Override
	public void viewSectionChanged(ViewEvent e) {
		this.tachogramCanvas.redraw();
	}

	@Override
	public void zoomChanged(ViewEvent e) {
		resized();
	}
	
}
