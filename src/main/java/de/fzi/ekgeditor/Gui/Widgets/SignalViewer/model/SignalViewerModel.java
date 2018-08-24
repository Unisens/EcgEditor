package de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalInfoComposite;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerColors;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerComposite;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;
import de.fzi.ekgeditor.utils.Selection;

public class SignalViewerModel implements SignalListener
{
	public double[] ZOOM_LEVEL_X = new double[] { 12.5, 25, 50, 75 };
	public static final int ZOOM_LEVEL_X_LENGTH = 4;
	public static final int userZoomX = 3;
	public double[] ZOOM_LEVEL_Y = new double[] { 5, 10, 15, 20, 25 };
	public static final int ZOOM_LEVEL_Y_LENGTH = 5;
	public static final int userZoomY = 4;
	public final int[] ZOOM_LEVEL_GRID = new int[] { 17, 20, 25, 30, 35 };
	public int numberOfChannelsToActivate = 3;
	private int[] channelZeroLine = null;

	// Grid
	private int StaticGridSizeX = 5;
	// private int StaticGridSizeY=5;

	// private int zoomLevel = 1;
	private int zoomLevelX = 0; // Vorschub
	private int zoomLevelY = 0; // Amplitude
	// private int zoomLevelY = 4;
	private double gridSpacingX = 20;
	private double gridSpacingY = 20;
	// private int gridSpacingY = 20;
	private double pixelPerSecond = 100;
	private double pixelPerMilliVolt = 40;

	private long currentTime = 0;
	private long offsetTimestampStart = 0;
	private long currentSample = 0;

	private int numberOfVisibleChannels = 0;

	private int signalGraphPositionX = -1;
	private long signalGraphPositionTime = 0;
	private int signalGraphPositionY = -1;

	private boolean mouseMoved = false; // Bild für die ECG-Grafik, wenn die
										// Maus bewegt wird
	private int mouseMoveXPosition = -1; // Position der Maus, wenn die Maus
											// bewegt wird (in pixel)

	private boolean mouseDown = false;
	private int mouseLeftDownXPosition = -1;

	private int mouseRightDownXPosition = -1;
	private int mouseRightDownYPosition = -1;

	private Point mouseRightDownLocation = null;

	private boolean mouseUp = false;
	private int mouseLeftUpXPosition = -1;

	private Image graphImage = null;
	private Image currentImage = null;

	private boolean redraw = false;

	private boolean showGrid = true;
	private boolean showCenterLine = false;

	public boolean dynamicGrid = false;

	public SignalModel signalModel = null;

	public int fullHeightFactor = 30;
	public int layers = 1;

	private boolean paintChannel[];
	private boolean channelPolarityReversed[];

	public SignalViewerComposite signalViewerComposite = null;
	private SignalInfoComposite signalInfoComposite = null;

	private boolean keyCtrlPressed = false;

	public Selection selection = new Selection();

	private EventListenerList viewListeners = new EventListenerList();

	public void addViewListener(ViewListener listener)
	{
		viewListeners.add(ViewListener.class, listener);
	}

	public void removeViewListener(ViewListener listener)
	{
		viewListeners.remove(ViewListener.class, listener);
	}

	protected synchronized void notifyViewZoomChanged(ViewEvent e)
	{
		for (ViewListener l : viewListeners.getListeners(ViewListener.class))
			l.zoomChanged(e);
	}

	protected synchronized void notifyViewChannelChanged(ViewEvent e)
	{
		for (ViewListener l : viewListeners.getListeners(ViewListener.class))
			l.channelViewChanged(e);
	}

	public synchronized void notifyViewSectionChanged(ViewEvent e)
	{
		for (ViewListener l : viewListeners.getListeners(ViewListener.class))
			l.viewSectionChanged(e);
	}

	public void signalChanged(SignalEvent e)
	{
		this.signalGraphPositionTime = 0;

		this.numberOfVisibleChannels = 0;
		int numberOfChannels = signalModel.getNumberOfChannels();

		paintChannel = new boolean[numberOfChannels];
		for (int i = 0; i < numberOfChannels; i++)
		{
			paintChannel[i] = false;
		}
		// activate first 3 channels
		for (int i = 0; i < numberOfChannelsToActivate; i++)
		{
			if ((numberOfChannels - 1) >= i)
			{
				paintChannel[i] = true;
				this.numberOfVisibleChannels++;
			}
			else
			{
				break;
			}
		}

		channelPolarityReversed = new boolean[numberOfChannels];
		for (int i = 0; i < numberOfChannels; i++)
		{
			channelPolarityReversed[i] = false;
		}

		// Calculate timestampStart offset
		if (this.signalModel.getSignal() != null)
		{
			try
			{
				offsetTimestampStart = this.signalModel.getSignal().getUnisens().getTimestampStart().getTime();
			}
			catch(NullPointerException npe)
			{
				offsetTimestampStart = 0;
				System.out.println("TimestampStart is missing!");
			}
		}
		
		
		this.selection.unSelect();
	}

	public int getFullHeight()
	{
		if (signalModel.isSignalLoaded())
		{
			return (int) Math.round(getPixelPerMilliVolt() * this.fullHeightFactor);
		}
		else
		{
			return 0;
		}
	}

	public boolean isRedraw()
	{
		return redraw;
	}


	public void setRedraw(boolean redraw)
	{
		this.redraw = redraw;
	}

	public void setSelection(Selection helperSelection)
	{
		selection = helperSelection;
		this.signalViewerComposite.signalViewerCanvas.setSelection(helperSelection);
		notifyViewSectionChanged(new ViewEvent(this));
	}

	public Selection getSelection()
	{
		return this.selection;
	}

	public int getStaticGridSize()
	{
		// GridSizeX=GridSizeY
		return StaticGridSizeX;
	}

	public void setStaticGridSize(int GridSize)
	{
		StaticGridSizeX = GridSize;
		// StaticGridSizeY=GridSize;
	}

	public boolean isPaintChannel(int i)
	{
		return paintChannel[i];
	}

	public boolean isChannelPolarityReversed(int i)
	{
		return channelPolarityReversed[i];
	}

	public void setPaintChannel(int i, boolean paint)
	{
		this.paintChannel[i] = paint;
		if (paint)
			++this.numberOfVisibleChannels;
		else
			--this.numberOfVisibleChannels;
		this.redraw = true;
		this.signalViewerComposite.signalViewerCanvas.redraw();

		this.notifyViewChannelChanged(new ViewEvent(this));
	}

	public void setChannelPolarityReversed(int i, boolean reversed)
	{
		channelPolarityReversed[i] = reversed;
		this.redraw = true;
		this.signalViewerComposite.signalViewerCanvas.redraw();
		this.notifyViewChannelChanged(new ViewEvent(this));
	}

	public void repaintSignalCanvas()
	{
		this.redraw = true;
		if (!signalViewerComposite.signalViewerCanvas.isDisposed())
			signalViewerComposite.signalViewerCanvas.redraw();
	}

	public Point getMouseRightDownLocation()
	{
		return mouseRightDownLocation;
	}

	public int getMouseRightDownXPosition()
	{
		return mouseRightDownXPosition;
	}

	public int getMouseRightDownYPosition()
	{
		return mouseRightDownYPosition;
	}

	public void setMouseRightDownXPosition(int mouseRightDownXPosition)
	{
		this.mouseRightDownXPosition = mouseRightDownXPosition;
	}

	public void setMouseDown(boolean mouseDown)
	{
		this.mouseDown = mouseDown;
	}

	public boolean isMouseUp()
	{
		return mouseUp;
	}

	public void setMouseUp(boolean mouseUp)
	{
		this.mouseUp = mouseUp;
	}

	public int getMouseLeftUpXPosition()
	{
		return mouseLeftUpXPosition;
	}

	public Image getCurrentImage()
	{
		return currentImage;
	}

	public void setCurrentImage(Image currentImage)
	{
		resetCurrentImage();
		this.currentImage = currentImage;
	}

	public boolean isMouseDown()
	{
		return mouseDown;
	}

	public int getMouseLeftDownXPosition()
	{
		return mouseLeftDownXPosition;
	}

	public void resetAllImages()
	{
		resetGraphImage();
		resetCurrentImage();
	}

	public void resetGraphImage()
	{
		if (graphImage != null)
		{
			this.graphImage.dispose();
			graphImage = null;
		}
	}

	public void resetCurrentImage()
	{
		if (currentImage != null)
		{
			this.currentImage.dispose();
			currentImage = null;
		}
	}

	public Image getGraphImage()
	{
		return graphImage;
	}

	public void setGraphImage(Image graphImage)
	{
		resetGraphImage();
		this.graphImage = graphImage;
	}

	public int getMouseMoveXPosition()
	{
		return mouseMoveXPosition;
	}

	public void setMouseMoveXPosition(int mouseMoveXPosition)
	{
		this.mouseMoveXPosition = mouseMoveXPosition;
	}

	public boolean isMouseMoved()
	{
		return mouseMoved;
	}

	public void setMouseMoved(boolean mouseMoved)
	{
		this.mouseMoved = mouseMoved;
	}

	public int getNumberOfVisibleChannels()
	{
		return numberOfVisibleChannels;
	}

	public void setNumberOfVisibleChannels(int numberOfVisibleChannels)
	{
		this.numberOfVisibleChannels = numberOfVisibleChannels;
	}

	public int getSignalGraphPositionY()
	{
		return signalGraphPositionY;
	}

	public void setSignalGraphPositionY(int signalGraphPositionY)
	{
		this.signalGraphPositionY = signalGraphPositionY;
	}

	public int getReferencePulseWidth() // Kalibrierzacke
	{
		/*
		 * int tPixPerSecf=this.getPixelPerSecond()/3; int
		 * tPixPerSecz=tPixPerSecf+this.getPixelPerSecond(); int
		 * KaliberZacke=tPixPerSecz+2*tPixPerSecf;
		 * 
		 * return KaliberZacke;
		 */

		return 25;
		// return 250;
		// return 0;
	}

	public long getTimeinMillisecsForPixel(long pixelNummer)
	{
		double pixelNumber = (double) (getSignalGraphPositionX() + pixelNummer - getReferencePulseWidth());
		if (pixelNumber < 0)
			pixelNumber = 0;
		currentTime = Math.round(((double) 1000 * (pixelNumber / this.getPixelPerSecond())));

		return currentTime;
	}
	
	/**
	 * Calculates the timestampStart offset
	 * @return offset in ms
	 */
	public long getOffset()
	{
		return offsetTimestampStart;
	}

	public int getPixelForSampleNumber(long sampleNumber, double samplingFrequency)
	{
		// double samplingFrequency = signalModel.getSamplingFrequency();
		if (samplingFrequency != 0)
		{
			double temp = ((double) sampleNumber / samplingFrequency) * this.getPixelPerSecond()
					+ this.getReferencePulseWidth();
			return (int) Math.round(Math.floor(temp - getSignalGraphPositionX()));
		}
		else
		{
			return 0;
		}

	}

	public int getPixelForTimeInMilliSeconds(long timeInMilliSeconds)
	{
		long pixel = Math.round(((double) timeInMilliSeconds / (double) 1000) * this.getPixelPerSecond()
				+ this.getReferencePulseWidth());
		return (int) (pixel - getSignalGraphPositionX());
	}

	public Long getTimeInMilliSecForSampleNumber(long sampleNr)
	{
		double samplingFrequency = signalModel.getSamplingFrequency();
		if (samplingFrequency != 0)
		{
			return Math.round(Math.floor((((double) sampleNr / samplingFrequency) * (double) 1000)));
		}
		else
		{
			return null;
		}
	}

	public long getSampleNumber(long pixelNumber, double samplingFrequency)
	{
		return (long) ((pixelNumber - getReferencePulseWidth() + getSignalGraphPositionX()) / getPixelPerSample(samplingFrequency));
	}

	public Double getValue(int channel, long pixelNumber, double samplingFrequency)
	{
		if (signalModel != null)
		{
			try
			{
				currentSample = getSampleNumber(pixelNumber, samplingFrequency);

				if ((currentSample >= 0) & (currentSample < signalModel.getMaxSamp()))
				{
					// currentValue
					// =((double)ecgSignalData[channel][currentSample])/1000;
					// currentValue
					// =((double)ecgSignalData[currentSample][channel]);

					return signalModel.getSample(currentSample, channel);
				}
				else
				{
					return null;
				}
			}
			catch (ArrayIndexOutOfBoundsException ex)
			{
				ex.printStackTrace();
				System.out.println(signalModel.getSignalBuffer().getPrevious().getStartSample() + " "
						+ signalModel.getSignalBuffer().getCurrent().getStartSample() + " "
						+ signalModel.getSignalBuffer().getNext().getStartSample());
				System.exit(0);
				return null;
			}
		}
		return null;
	}


	// returns the real channel number in response to the virtual (paint
	// activated channel number)
	// return null if channel could not be found
	public Integer getRealChannelNumber(int virtualChannelNumber)
	{
		int virtualNumber = 0 - 1;
		int currentChannel = 0;
		while (signalModel.isSignalLoaded())
		{
			while (!this.isPaintChannel(currentChannel))
			{
				currentChannel++;
			}

			virtualNumber++;

			if (currentChannel >= signalModel.getNumberOfChannels())
			{
				return null;
			}
			else
			{
				if (virtualNumber == virtualChannelNumber)
				{
					return currentChannel;
				}
			}

			currentChannel++;
		}

		return null;
	}

	public Integer getVirtualChannelNumber(int realChannelNumber)
	{
		int virtualChannel = -1;
		if (!paintChannel[realChannelNumber])
			return null;
		for (int i = 0; i <= realChannelNumber; i++)
			if (paintChannel[i])
				virtualChannel++;
		return virtualChannel;
	}

	public Double getSlope(int channelNumber, int pixelNumber, double samplingFrequency)
	{
		long sampleNumber = this.getSampleNumber(pixelNumber, samplingFrequency);
		return signalModel.getSlope(sampleNumber, channelNumber);
	}


	public int getSignalGraphPositionX()
	{
		return signalGraphPositionX;
	}

	public void setSignalGraphPositionX(int signalGraphPositionX)
	{
		this.signalGraphPositionX = signalGraphPositionX;
		this.signalGraphPositionTime = this.getTimeinMillisecsForPixel(this.getReferencePulseWidth());
	}

	public SignalInfoComposite getSignalInfoComposite()
	{
		return signalInfoComposite;
	}

	public void setSignalInfoComposite(SignalInfoComposite signalInfoComposite)
	{
		this.signalInfoComposite = signalInfoComposite;
	}

	public void setSignalViewerComposite(SignalViewerComposite signalViewerComposite)
	{
		this.signalViewerComposite = signalViewerComposite;
	}

	public void mouseMoved(int mouseMoveXPosition, int mouseMoveYPosition)
	{
		if (signalInfoComposite != null)
		{
			this.signalInfoComposite.mouseMoved(mouseMoveXPosition);
		}

		this.mouseMoved = true;
		this.mouseMoveXPosition = mouseMoveXPosition;
		this.signalViewerComposite.signalViewerCanvas.OnMouseMoved(mouseMoveXPosition, mouseMoveYPosition);
	}

	public void OnLeftMouseDown(int mouseLeftDownXPosition)
	{
		this.mouseDown = true;
		this.mouseLeftDownXPosition = mouseLeftDownXPosition;
		this.signalViewerComposite.signalViewerCanvas.OnMouseDown();
	}

	public void OnRightMouseDown(Point mouseRightDownLocation, int mouseRightDownXPosition, int mouseRightDownYPosition)
	{
		this.mouseRightDownXPosition = mouseRightDownXPosition;
		this.mouseRightDownYPosition = mouseRightDownYPosition;
		this.mouseRightDownLocation = mouseRightDownLocation;

	}

	public void OnLeftMouseUp(int mouseLeftUpXPosition)
	{
		this.mouseDown = false;
		this.mouseUp = true;
		this.mouseLeftUpXPosition = mouseLeftUpXPosition;
		this.signalViewerComposite.signalViewerCanvas.OnMouseUp();
	}

	public void OnVertScrollBarSelected()
	{
		resetMouseValue();
		if (currentImage != null)
		{
			currentImage.dispose();
		}
		this.currentImage = null;
		this.signalViewerComposite.signalViewerCanvas.redraw();
	}

	public void resetMouseValue()
	{
		this.mouseDown = false;
		this.mouseUp = false;
		this.mouseMoved = false;
		this.mouseLeftDownXPosition = -1;
		this.mouseLeftUpXPosition = -1;
		this.mouseRightDownXPosition = -1;
	}

	public SignalViewerModel(SignalModel signalM)
	{
		// this.setZoomLevel(4,NOTIFICATION);
		this.signalModel = signalM;
		this.signalModel.addSignalViewerModel(this);
		this.setZoomLevelX(this.zoomLevelX, Constants.NO_NOTIFICATION);
		this.setZoomLevelY(this.zoomLevelY, Constants.NOTIFICATION);

		String prop_dynamicGrid = "dynamicGrid";
		this.dynamicGrid = Boolean.parseBoolean((Common.getInstance().reg.reg.getProperty(prop_dynamicGrid)));
		String prop_gridSize = "gridSize";
		String dummy = Common.getInstance().reg.reg.getProperty(prop_gridSize);
		if (dummy != null)
		{
			Integer gridSize = Integer.parseInt(dummy);
			if (gridSize != null)
			{
				this.setStaticGridSize(gridSize);
			}
		}
	}

	public boolean isGridShow()
	{
		return this.showGrid;
	}

	public void setShowGrid(boolean show, boolean notification)
	{
		if (this.showGrid == show)
			return;
		this.showGrid = show;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	/*
	 * public void setZoomLevel(int level,boolean notification) { if (level < 0)
	 * level = 0; if (level >= ZOOM_LEVEL_GRID.length) level =
	 * ZOOM_LEVEL_GRID.length-1;
	 * 
	 * this.zoomLevel = level;
	 * this.setGridSpacingX(ZOOM_LEVEL_GRID[this.zoomLevel],NO_NOTIFICATION);
	 * this.setGridSpacingY(ZOOM_LEVEL_GRID[this.zoomLevel],NO_NOTIFICATION);
	 * this.setPixelPerSecond(ZOOM_LEVEL_X[this.zoomLevel],NO_NOTIFICATION);
	 * this.setPixelPerMilliVolt(ZOOM_LEVEL_Y[this.zoomLevel],NO_NOTIFICATION);
	 * 
	 * if (notification) {this.notifyViewChanged(new ViewEvent(this));} }
	 */

	public void setZoomLevelX(int level, boolean notification)
	{
		if (level < 0)
			level = 0;
		// if (level >= ZOOM_LEVEL_GRID.length) level =
		// ZOOM_LEVEL_GRID.length-1;
		if (level >= ZOOM_LEVEL_X.length)
			level = ZOOM_LEVEL_X.length - 1;

		this.zoomLevelX = level;
		this.setGridSpacingX(ZOOM_LEVEL_GRID[this.zoomLevelX], Constants.NO_NOTIFICATION);
		this.setPixelPerSecond(ZOOM_LEVEL_X[this.zoomLevelX], Constants.NO_NOTIFICATION);

		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public int getZoomLevelX()
	{
		return zoomLevelX;
	}

	public void setZoomLevelY(int level, boolean notification)
	{
		if (level < 0)
			level = 0;
		// if (level >= ZOOM_LEVEL_GRID.length) level =
		// ZOOM_LEVEL_GRID.length-1;
		if (level >= ZOOM_LEVEL_Y.length)
			level = ZOOM_LEVEL_Y.length - 1;

		this.zoomLevelY = level;
		this.setGridSpacingY(ZOOM_LEVEL_GRID[this.zoomLevelY], Constants.NO_NOTIFICATION);
		this.setPixelPerMilliVolt(ZOOM_LEVEL_Y[this.zoomLevelY], Constants.NO_NOTIFICATION);

		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public int getZoomLevelY()
	{
		return zoomLevelY;
	}

	public double getPixelPerMilliVolt()
	{
		if (this.dynamicGrid)
		{
			return pixelPerMilliVolt;
		}
		else
		{
			// 1 mm = Constants.stdGridSizeY Pixel
			// 25 mm/mv --> PixelPerMilivolt = Constants.stdGridSizeY*zoomLevel

			return this.getStaticGridSize() * ZOOM_LEVEL_Y[this.zoomLevelY];
		}
	}

	public double getPixelPerSecond()
	{
		if (this.dynamicGrid)
		{
			return pixelPerSecond;
		}
		else
		{
			// 1 mm = Constants.stdGridSizeX
			return this.getStaticGridSize() * ZOOM_LEVEL_X[this.zoomLevelX];
		}
	}

	public double getPixelPerSample(double samplingFrequency)
	{
		return ((double) this.getPixelPerSecond()) / (samplingFrequency);
	}

	public double getGridSpacingX()
	{
		return gridSpacingX;
	}

	public double getGridSpacingY()
	{
		return gridSpacingY;
	}

	/*
	 * public void zoomIn(boolean notification) {
	 * setZoomLevel(this.zoomLevel+1,notification); }
	 */

	public void zoomInX(boolean notification)
	{
		setZoomLevelX(this.zoomLevelX + 1, notification);
	}

	/*
	 * public void zoomOut(boolean notification) {
	 * setZoomLevel(this.zoomLevel-1,notification); }
	 */

	public void zoomOutX(boolean notification)
	{
		setZoomLevelX(this.zoomLevelX - 1, notification);
	}

	public void setGridSpacingX(double spacing, boolean notification)
	{
		if (this.gridSpacingX == spacing)
			return;
		this.gridSpacingX = spacing;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public void setGridSpacingY(double spacing, boolean notification)
	{
		if (this.gridSpacingY == spacing)
			return;

		this.gridSpacingY = spacing;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public void setPixelPerMilliVolt(double pixel, boolean notification)
	{
		if (this.pixelPerMilliVolt == pixel)
			return;

		this.pixelPerMilliVolt = pixel;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public void setPixelPerSecond(double pixel, boolean notification)
	{
		if (this.pixelPerSecond == pixel)
			return;

		this.pixelPerSecond = pixel;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public boolean isShowCenterLine()
	{
		return showCenterLine;
	}

	public void setShowCenterLine(boolean showCenterLine, boolean notification)
	{
		if (this.showCenterLine == showCenterLine)
		{
			return;
		}
		this.showCenterLine = showCenterLine;
		if (notification)
		{
			this.notifyViewZoomChanged(new ViewEvent(this));
		}
	}

	public void setChannelZeroLine()
	{
		int numberOfChannels = this.getNumberOfVisibleChannels();
		int height = this.signalViewerComposite.signalViewerCanvas.getBounds().height;

		if (numberOfChannels > 0)
		{
			channelZeroLine = new int[numberOfChannels];

			int h = getFullHeight();// +height;
			int scroll = (h - height) / 2;
			if (scroll <= 0)
			{
				scroll = 1;
			}
			for (int i = 0; i < numberOfChannels; i++)
			{
				channelZeroLine[i] = (int) (i * (height / numberOfChannels) + (height / numberOfChannels / 2)) + scroll;
				// channelZeroLine[i]=(int)( (i)*h/numberOfChannels+(h/2) );
			}
		}
	}

	public int getChannelZeroLine(int channelNumber)
	{
		return channelZeroLine[channelNumber];
	}

	public int getVisibleChannelForPixel(int pixelHight)
	{
		for (int i = 0; i < channelZeroLine.length; i++)
		{
			if (pixelHight < channelZeroLine[i] - getSignalGraphPositionY()
					|| (i < channelZeroLine.length - 1 && channelZeroLine[i] - getSignalGraphPositionY() < pixelHight && pixelHight < (channelZeroLine[i]
							+ channelZeroLine[i + 1] - 2 * getSignalGraphPositionY()) / 2))
			{
				return i;
			}
		}
		return channelZeroLine.length - 1;
	}

	public int getPixel(int currentChannel, double value)
	{
		if (signalModel.getNumberOfChannels() > currentChannel)
		{
			return (int) (this.getChannelZeroLine(currentChannel) - getSignalGraphPositionY() - value
					* getPixelPerMilliVolt());
		}
		else
			return 0;
	}

	private void goPixel(int pixel)
	{
		int referencePulse = this.signalViewerComposite.signalViewerModel.getReferencePulseWidth();
		this.signalViewerComposite.gotoPixel(pixel - referencePulse);
	}

	public void goToSample(long newSample)
	{
		// ResetAll
		this.setSignalGraphPositionX(0);
		int pixel = this.getPixelForSampleNumber(newSample, signalModel.getSamplingFrequency());
		goPixel(pixel);
	}

	public void goToTime(long timeInMilliSeconds)
	{
		this.setSignalGraphPositionX(0);
		int pixel = this.getPixelForTimeInMilliSeconds(timeInMilliSeconds);
		goPixel(pixel);
	}

	public Color getColorForTime(long time, int realChannel)
	{
		Color res = null;

		if (signalModel.getRemovedSelection().inList(time))
		{
			res = Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorDeletedSection);
		}
		else if (signalModel.getTestdataSelection().inList(time))
		{
			res = Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorExportedSection);
		}
		else
		{
			res = Common.getInstance().signalViewerColors.getChannelColor(realChannel);
		}

		return res;
	}

	public Color getColorForSample(long sample, int realChannel)
	{
		return getColorForTime(this.getTimeInMilliSecForSampleNumber(sample), realChannel);
	}

	public void finalize() throws Throwable
	{
		try
		{
			if (signalModel != null)
			{
				signalModel.finalize();
				signalModel = null;
			}

			if (Constants.isDebug)
			{
				System.out.println("SignalViewerModel finalisiert.");
			}
		}
		catch (Exception e)
		{
			super.finalize();
		}
	}

	public void setMouseLeftDownXPosition(int mouseLeftDownXPosition)
	{
		this.mouseLeftDownXPosition = mouseLeftDownXPosition;
	}

	public long getStartVisibleTimeInMs()
	{
		return signalGraphPositionTime;
	}

	public long getEndVisibleTimeInMs()
	{
		return this.getTimeinMillisecsForPixel(signalViewerComposite.getSignalViewerCanvas().getBounds().width);
	}

	public boolean isKeyCtrlPressed()
	{
		return keyCtrlPressed;
	}

	public void setKeyCtrlPressed(boolean keyCtrlPressed)
	{
		this.keyCtrlPressed = keyCtrlPressed;
	}
}
