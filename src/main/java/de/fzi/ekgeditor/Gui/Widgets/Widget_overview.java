/**
 * This class implements the preview-widget
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Widgets;

import java.util.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerCanvas;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerColors;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.FontsManager;
import de.fzi.ekgeditor.data.LayerBounds;
import de.fzi.ekgeditor.data.previewData;
import de.fzi.ekgeditor.events.CacheEvent;
import de.fzi.ekgeditor.events.CacheListener;
import de.fzi.ekgeditor.events.SettingsEvent;
import de.fzi.ekgeditor.events.SettingsListener;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;
import de.fzi.ekgeditor.tasks.PlaySignalTask;
import de.fzi.ekgeditor.utils.TimeUtil;

// SignalListener: get notified if signal changes
// ViewListener: get current position of edit window
// Settingslistener: get notified if some settings (like color) changes.
public class Widget_overview extends Canvas implements SignalListener, ViewListener, SettingsListener, CacheListener, MouseMoveListener
{

	/** modular for amplitude. Paint every x-values a grid line. */
	private final int verticalBarAfterSecs = 10;

	/** minimum value that can occur in this window */
	private double minValue = -5;

	/** maximum value that can occur in this window */
	private double maxValue = 5;

	/** link to underlying dataModel */
	private SignalViewerModel dataModel;

	private SignalViewerModel connecedMain;

	/**
	 * intern image. With this image we don't need to redraw everything
	 * everytime again
	 */
	private Image graphImage = null;

	/** length in seconds per layer */
	private final int layerLength = 1 * 60; // in one layer, we have 1 min of
											// signal

	private final int layers = 5; // we have 5 layers

	private int[][] layerCenter; // middle line for one layer

	private int[] layerGround;
	
	private long selectedStartTime;
	private long selectedEndTime;

	/**
	 * intern variable defining if we really need to draw everything new or if
	 * we can use our precalculated image
	 */
	private boolean redraw = true;

	private double heightMulti = 0;

	private double widthMulti = 0;

	private long lengthInSamplesPerLayer = 0;

	private double verticalBarAfterPixel = 0;

	private static long SAMPLE_START=0;
	private long sampleOffset = SAMPLE_START;
	
	private boolean playing = false;
	private Timer timer;

	/**
	 * Standard constructor for widget-overview
	 * 
	 * @param parent
	 *            parent composite (normally main-window or group)
	 * @param style
	 *            style to use for this widget
	 */
	public Widget_overview(Composite parent, int style, SignalViewerModel model, SignalViewerModel connectedMain)
	{
		super(parent, style | SWT.NO_BACKGROUND);

		this.setBackground(Common.getInstance().signalViewerColors
				.getSignalViewerColor(SignalViewerColors.colorBackground));

		addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				paint(e);
			}
		});

		addControlListener(new ControlListener()
		{
			public void controlMoved(ControlEvent arg0)
			{
				// Nothing to do
			}

			public void controlResized(ControlEvent arg0)
			{
				resized();
			}
		});

		this.addMouseMoveListener(this);
		this.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				mouseClick(e);
			}
		});

		this.dataModel = model;
		dataModel.signalModel.addSignalListener(this);
		this.connecedMain = connectedMain;
		connectedMain.addViewListener(this);
		dataModel.signalModel.addCacheListener(this);
		Common.getInstance().addSettingsListener(this);
	}

	public void mouseMove(MouseEvent e)
	{
		String txt = "";
		// long sN = (long) Math.round(sampleNumberForPixelX(e.x));
		if (this.dataModel.signalModel.isSignalLoaded())
		{
			long sN = sampleNumberForPixelPosition(e.x, e.y);
			txt = "Sample: " + Long.toString(sN) + "\n";
			long timeInMilliSeconds = this.dataModel.signalModel.getTimeInMilliSecsForSample(sN);
			txt = txt + "Zeit: " + TimeUtil.getFullTimeString(timeInMilliSeconds) + " \n\n";
			for (int channelCounter = 0; channelCounter < this.dataModel.signalModel.getNumberOfChannels(); channelCounter++)
			{
				txt = txt + "Kanel: " + this.dataModel.signalModel.getChannelName(channelCounter) + "\n";
				txt = txt + "Wert: " + Common.Double2String(this.dataModel.signalModel.getSample(sN, channelCounter))
						+ "\n\n";
			}
		}
		this.setToolTipText(txt);
	}

	public void mouseClick(MouseEvent e)
	{
		if (this.dataModel.signalModel.isSignalLoaded())
		{
			long sN = sampleNumberForPixelPosition(e.x, e.y);
			
			if (Constants.isDebug)
			{
				System.out.println("Goto Sample" + sN);
			}
			this.connecedMain.goToSample(sN);
		}
	}

	/**
	 * Calculate basic common used variables before a painting starts.
	 * 
	 * it calculates horizontal_line_step, vertical_line_step, heightMulti and
	 * widthMulti
	 */
	private void initPaint()
	{
		// spacing between two layers, top and bottom margin are calculated relatively to widget height
		int layerSpacing = (int) Math.round(this.getClientArea().height * 0.04); 
		int layerMargin = (int) Math.round(layerSpacing);
		int height = this.getClientArea().height - 2 * layerMargin - (layers - 1) * layerSpacing;
		int channels = this.dataModel.signalModel.getNumberOfChannels();
		double layerHeight = (double) ((double) height / (double) (layers));

		this.layerCenter = new int[layers][channels];
		this.layerGround = new int[layers];

		for (int layerCounter = 0; layerCounter < layers; layerCounter++)
		{
			double DlayerGround = (layerHeight * layerCounter) + layerMargin + layerSpacing * layerCounter;
			this.layerGround[layerCounter] = (int) Math.round(DlayerGround);

			for (int channelCounter = 0; channelCounter < channels; channelCounter++)
			{
				// Jeder Layer wird in soviele Bereiche geteilt, wie es Kanäle gibt. In die Mitte eines jeden Bereichs wird
				// die Mittellinie für das Signal gelegt
				double layerCenter = (double)((2 * channelCounter + 1)/(double)(2 * channels) * layerHeight) + DlayerGround;
				this.layerCenter[layerCounter][channelCounter] = (int) Math.round(layerCenter);
			}
		}

		// Pixel-Umrechnung innerhalb eines Layers:
		heightMulti = (layerHeight / ((double) (maxValue - minValue)));
		lengthInSamplesPerLayer = Math.round(Math.floor(this.dataModel.signalModel.getSamplingFrequency()* this.layerLength));
		widthMulti = ((double) this.getClientArea().width / (double) (lengthInSamplesPerLayer));
		verticalBarAfterPixel = dataModel.signalModel.getSamplingFrequency() * this.verticalBarAfterSecs;

	}

	/**
	 * Paints the complete signal to the widget
	 * 
	 * @param e
	 *            extra paint-information
	 */
	private void paint(PaintEvent e)
	{
		if (Constants.showPreview)
		{
			if (dataModel.signalModel.isSignalLoaded())
			{
				initPaint();
			}
			if (redraw)
			{
				if (graphImage != null)
				{
					graphImage.dispose();
				}

				Rectangle rect = new Rectangle(0, 0, this.getClientArea().width, this.getClientArea().height);
				graphImage = new Image(this.getDisplay(), rect);
				GC drawableGC = new GC(graphImage);
				drawableGC.setBackground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorBackground));
				drawableGC.fillRectangle(rect);

				if (dataModel.signalModel.isSignalLoaded())
				{
					paintGrid(drawableGC);
					paintSignal(drawableGC);
				} else
				{
					drawableGC.setForeground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorError));
					drawableGC.drawLine(0, 0, this.getClientArea().width, this.getClientArea().height);
					drawableGC.setFont(Common.getInstance().fontsManager.getSystemFont(FontsManager.fontSignalNotLoaded));
					// drawableGC.setForeground(Common.getInstance().)
					drawableGC.drawText("Signal nicht geladen", 0, 0);
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
		} else
		{
			Rectangle rect = new Rectangle(0, 0, this.getClientArea().width, this.getClientArea().height);
			e.gc.setBackground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorBackground));
			e.gc.fillRectangle(rect);

			e.gc.setForeground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorError));
			e.gc.drawLine(0, 0, this.getClientArea().width, this.getClientArea().height);
			e.gc.dispose();
		}
	}

	/**
	 * This method is called, if some zoom Event occurs.
	 * 
	 * @param e
	 *            extra ViewEvent-Information
	 */
	public void zoomChanged(ViewEvent e)
	{
		resized();
	}

	/**
	 * This method is called, if some viewSection Event occurs.
	 * 
	 * @param e
	 *            extra ViewEvent-Information
	 */
	public void viewSectionChanged(ViewEvent e)
	{
		this.redraw();
	}

	public void channelViewChanged(ViewEvent e)
	{
	}

	/**
	 * This method is called if the signal loaded by the data Model is changed
	 * 
	 * @param e
	 *            extra SignalEvent-Information
	 */
	public void signalChanged(SignalEvent e)
	{
		if (e.newSignal==Constants.NEWSIGNAL)
		{
			this.sampleOffset=SAMPLE_START;
		}
		resized();
	}

	/**
	 * This method is called if some settings have changed. e.g. color
	 * 
	 * @param e
	 *            Extra SettingsChanged-Information
	 */
	public void settingsChanged(SettingsEvent e)
	{
		resized();
	}

	public void CacheFlush(CacheEvent e)
	{
	}

	public void PageLoad(CacheEvent e)
	{
	}

	/**
	 * This method is called if the size of the window has changed. It completly
	 * redraws the window.
	 */
	public void resized()
	{
		redraw = true;
		this.redraw();
	}

	/**
	 * calculate the pixel for some signal value (feed/time)
	 * 
	 * @param value
	 *            value that should be transformed (time)
	 * @return pixel
	 */
	private double signalValueToPixelY(double value, int layer, int channel){
		return (-value * heightMulti) + this.layerCenter[layer][channel];
	}

	/**
	 * calculate the pixel X (time) for the sample number
	 * 
	 * @param sampleNumber
	 *            number of the sample
	 * @return pixel
	 */
	private int pixelXForSampleNumber(long sampleNumber)
	{
		sampleNumber = sampleNumber - sampleOffset;
		if (lengthInSamplesPerLayer > 0)
		{
			while (sampleNumber >= this.lengthInSamplesPerLayer)
			{
				sampleNumber = sampleNumber - lengthInSamplesPerLayer;
			}
		}
		double valuePoint = (double) ((sampleNumber) * widthMulti);

		return (int) Math.round(valuePoint);
	}

	public void goLeft()
	{
		this.sampleOffset = this.sampleOffset - this.lengthInSamplesPerLayer * layers;
		if (sampleOffset < SAMPLE_START)
		{
			sampleOffset = SAMPLE_START;;
		}

		resized();
	}

	public void goRight()
	{
		long newSampleOffset = this.sampleOffset + this.lengthInSamplesPerLayer * layers;
		if(this.dataModel.signalModel.getMaxSamp() > newSampleOffset){
			this.sampleOffset = newSampleOffset;
			resized();
		}
	}

	public void goCurrent()
	{
		int page = this.getPageForTime(this.connecedMain.signalViewerComposite.signalViewerCanvas.getCurrentStartTime());
		this.sampleOffset = page * this.lengthInSamplesPerLayer * layers;
		resized();
	}
	
	public void play(int millisec){
		if(millisec != 0){
			if(!playing){
				this.playing = true;
				timer = new Timer(true);
				timer.schedule(new PlaySignalTask(this), 100, millisec);
			}else{
				timer.cancel();
				timer = new Timer(true);
				timer.schedule(new PlaySignalTask(this), 100, millisec);
			}
		}else{
			timer.cancel();
			this.playing = false;
		}
	}

	public void toggleAmplitude()
	{
		if (maxValue == 10)
		{
			minValue = -5;
			maxValue = 5;
		}
		else if (maxValue == 2.5)
		{
			minValue = -10;
			maxValue = 10;
		}
		else
		{
			minValue = -2.5;
			maxValue = 2.5;
		}
		resized();
	}

	public int getLayerForPixelY(int PixelY)
	{
		int bestlayer = 0;
		int bestDiff = Integer.MAX_VALUE;

		if (this.layerCenter != null)
		{
			if (layers <= this.layerCenter.length)
			{
				for (int layerCount = 0; layerCount < layers; layerCount++)
				{
					int myY = this.layerCenter[layerCount][0];
					int diff = Math.abs(PixelY - myY);
					if (diff < bestDiff)
					{
						bestDiff = diff;
						bestlayer = layerCount;
					}
				}
			}
		}

		return bestlayer;
	}

	public int getLayerForTime(long time)
	{
		int layerNumber = 0;
		long layerLengthInMilliSecs = this.layerLength * 1000;
		while (time >= layerLengthInMilliSecs * layers)
		{
			time = time - layerLengthInMilliSecs * layers;
		}

		while (time > layerLengthInMilliSecs)
		{
			time = time - layerLengthInMilliSecs;
			layerNumber++;
		}

		return layerNumber;
	}

	public int getPageForTime(long time)
	{
		int page = 0;
		long layerLengthInMilliSecs = this.layerLength * 1000;
		while (time > layerLengthInMilliSecs * layers)
		{
			time = time - layerLengthInMilliSecs * layers;
			page++;
		}

		return page;
	}

	public int getPageForSample(long time)
	{
		int page = 0;
		while (time >= this.lengthInSamplesPerLayer * layers)
		{
			time = time - this.lengthInSamplesPerLayer * layers;
			page++;
		}

		return page;
	}

	/**
	 * calculate the pixel for some signal value (amplitude)
	 * 
	 * @param X
	 *            value that should be transformed (amplitude)
	 * @return pixel
	 */
	public long sampleNumberForPixelPosition(int X, int Y)
	{

		double value = sampleOffset;
		if (widthMulti != 0)
		{
			value = (double) (((double) X / widthMulti) + (double) sampleOffset);
		}

		int bestlayer = this.getLayerForPixelY(Y);
		return Math.round(Math.floor((value + this.lengthInSamplesPerLayer * bestlayer)));
	}

	/**
	 * this method paints the verical and horizontalLines
	 * 
	 * @param gc
	 *            Graphic-Context to paint in
	 */
	private void paintGrid(GC gc)
	{
		// paintHorizontalLines knows if it should draw itself or not, but has
		// always to be called 'cause of centerline
		// paintHorizontalLines(gc);

		// only paint verticalLines if we really want to show some grid
		if (this.dataModel.isGridShow())
		{
			paintVerticalLines(gc);
		}
	}

	private int pixelXForTime(long timeInMilliSec)
	{
		long sampleNumber = this.dataModel.signalModel.getSampleForTimeInMilliSeconds(timeInMilliSec);
		return this.pixelXForSampleNumber(sampleNumber);
	}

	private LayerBounds getLayerStartEnd(int layerNumberStart)
	{
		if (layerNumberStart < layers)
		{
			int layerStart = this.layerGround[layerNumberStart];

			int layerEnd;
			if ((layerNumberStart + 1) < layers)
			{
				layerEnd = this.layerGround[layerNumberStart + 1];
			} else
			{
				layerEnd = this.getClientArea().height;
			}

			return new LayerBounds(layerStart, layerEnd);
		} else
		{
			if (Constants.isDebug)
			{
				System.out.println("false layer, paintActualView "+layerNumberStart);
			}
			return null;
		}
	}

	private previewData calculatePageAndLayer(long time)
	{
		int layerNumber = this.getLayerForTime(time);
		int pageNumber = this.getPageForTime(time);

		return new previewData(pageNumber, layerNumber);
	}

	private int calculatePixel(long time, long timeStart, boolean start)
	{
		if (start)
		{
			if (time < timeStart)
			{
				time = timeStart;
			}
		} else
		{
			if (time > timeStart)
			{
				time = timeStart;
			}
		}
		int x = pixelXForTime(time);
		if (x < 0)
		{
			x = 0;
		}
		return x;
	}

	/**
	 * this method fills the current selection area with some special color
	 * 
	 * @param gc
	 *            Graphic-Context to paint in
	 */
	private void paintActualView(GC gc)
	{
		if (this.dataModel.signalModel.isSignalLoaded())
		{
			SignalViewerCanvas ViewerCanvas = this.connecedMain.signalViewerComposite.signalViewerCanvas;
			Color c = Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorSelectionLine);
			gc.setBackground(c);
			gc.setAlpha(50);

			selectedStartTime = ViewerCanvas.getCurrentStartTime();
			selectedEndTime = ViewerCanvas.getCurrentEndTime();

			previewData start = calculatePageAndLayer(selectedStartTime);
			previewData end = calculatePageAndLayer(selectedEndTime);
			int currentPage = this.getPageForSample(this.sampleOffset);

			int layerCounter = start.layerNumber;
			int pageCounter = start.pageNumber;
			
			while (pageCounter <= end.pageNumber)
			{
				while ((layerCounter < layers))
				{
					if ((pageCounter == end.pageNumber) & (layerCounter > end.layerNumber))
					{
						break;
					}
					if (pageCounter == currentPage)
					{
						LayerBounds lb = getLayerStartEnd(layerCounter);
						long LayerTimeStart = (pageCounter * layers + layerCounter) * layerLength * 1000;
						long LayerTimeEnd = ((pageCounter) * layers + layerCounter + 1) * layerLength * 1000 - 1;

						if (lb != null)
						{
							int x_start = calculatePixel(selectedStartTime, LayerTimeStart, true);
							int x_end = calculatePixel(selectedEndTime, LayerTimeEnd, false);

							int width = x_end - x_start;
							if (width < 0)
							{
								width = this.getClientArea().width;
							}

							gc.fillRectangle(x_start, lb.layerStart, width, lb.layerEnd - lb.layerStart);
						}
					}

					layerCounter++;
				} // end while

				layerCounter = 0;
				pageCounter++;

			}
		}
	}


	/**
	 * this method paints the verticalLines+centerline used for the grid
	 * 
	 * @param gc
	 *            Graphic-Context to paint in
	 */
	private void paintVerticalLines(GC gc)
	{
		gc.setForeground(Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorPreviewLine));

		// calculate the next round step number
		long sampleNumber = sampleOffset;
		boolean ok = true;
		int yint = pixelXForSampleNumber(sampleOffset);

		while (ok)
		{
			int yold = yint;
			yint = pixelXForSampleNumber(sampleNumber);
			if (yold > yint) // layer overflow ?
			{
				break;
			}
			gc.drawLine(yint, 0, yint, this.getClientArea().height);

			gc.setBackground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorBackground));
			long timeInMilliSecs = this.dataModel.signalModel.getTimeInMilliSecsForSample(sampleNumber);
			gc.drawString(TimeUtil.getTimeString(timeInMilliSecs, Constants.NoMilliSecs), yint, 0);

			sampleNumber += this.verticalBarAfterPixel;
		}
	}

	/**
	 * draws a line from point oldPoint to Point newPoint
	 * 
	 * @param gc
	 *            Graphic-Context to paint in
	 * @param oldPoint
	 *            source-point
	 * @param newPoint
	 *            destination-point
	 */
	private void drawLine(GC gc, Point oldPoint, Point newPoint)
	{
		if (oldPoint != null)
		{
			gc.drawLine(oldPoint.x, oldPoint.y, newPoint.x, newPoint.y);
		}
	}

	/**
	 * converts to double values to one point-structure
	 * 
	 * @param time
	 *            first value to save (X)
	 * @param value
	 *            second value to save (Y)
	 * @return Point containing this values
	 */
	private Point savePoint(double time, double value)
	{
		int tint = (int) Math.round(time);
		int vint = (int) Math.round(value);

		return new Point(tint, vint);
	}

	/**
	 * this method paints the whole signal
	 * 
	 * @param gc
	 *            Graphic-Context to paint in
	 */
	private void paintSignal(GC gc)
	{
		gc.setForeground(Common.getInstance().signalViewerColors.getChannelColor(0));
		for (int layerCounter = 0; layerCounter < layers; layerCounter++)
		{
			for (int channels = 0; channels < this.dataModel.signalModel.getNumberOfChannels(); channels++)
			{
				Point p = null;
				
				for (long sampleCounter = sampleOffset; sampleCounter < this.lengthInSamplesPerLayer + sampleOffset; sampleCounter++)
				{
					Point np = null;
					long sampleNr = Math.round(sampleCounter + this.lengthInSamplesPerLayer * layerCounter);

					gc.setForeground(dataModel.getColorForSample(sampleNr, channels));
					Double value = this.dataModel.signalModel.getSample(sampleNr, channels);
			
					if (value != null)
					{
						np = savePoint(pixelXForSampleNumber(sampleCounter), signalValueToPixelY(value, layerCounter, channels));
						drawLine(gc, p, np);
						p = np;
					}
					sampleCounter += 4;
				}
			}
		}

	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public long getSelectedStartTime() {
		return selectedStartTime;
	}

	public long getSelectedEndTime() {
		return selectedEndTime;
	}
}
