package de.fzi.ekgeditor.Gui.Widgets.SignalViewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.unisens.Event;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Menu.ContextMenu;
import de.fzi.ekgeditor.Gui.Widgets.EKGToolbar;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.controller.MouseController;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.AnnotationCode;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.Colors;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.Constants_AnnotationCodes;
import de.fzi.ekgeditor.data.Cursors;
import de.fzi.ekgeditor.data.FontsManager;
import de.fzi.ekgeditor.data.TriggerDataVisible;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.utils.RangeDiff;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.TimeUtil;

public class SignalViewerCanvas extends Canvas implements SignalListener
{

	private static final int NULL_LINE_WIDTH = 2;

	private Display display = null;

	private int signalGraphWidth = 300;

	private int signalGraphHeight = 300;

	private int repaintHeight = 0;

	private boolean resized = false;

	private long startTime = 0;

	private long endTime = 0;

	private long currentTimeStart = 0;

	private long currentTimeEnd = 0;

	private SignalViewerModel signalViewerModel = null;

	private SignalViewerComposite composite = null;

	private Selection tempSelection = null;

	private ArrayList<TriggerDataVisible> visibleTriggers = new ArrayList<TriggerDataVisible>();

	private ContextMenu contextMenu;

	public SignalViewerCanvas(Composite parent)
	{
		super(parent, SWT.NO_BACKGROUND | SWT.BORDER);
		// super(parent , SWT.BORDER);
		this.display = this.getDisplay();

		contextMenu = new ContextMenu(this.getShell());
		this.setMenu(contextMenu.getContextMenu());

		this.addMouseListener(contextMenu);
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent arg0)
			{

			}
		});

		this.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				paint(e);
			}
		});

		this.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.CTRL)
				{
					Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setKeyCtrlPressed(true);
				}
				else if(e.keyCode == 32)
				{
					TriggerModel triggerModel = Common.getInstance().triggerModel;
					EKGToolbar toolbar = Common.getInstance().mainForm.ekgToolbar;
					if(triggerModel.isAddTriggerMode())
					{
						toolbar.selectDeleteTriggerOption();
					}
					else if(triggerModel.isDeleteTriggerMode())
					{
						toolbar.selectAddTriggerOption();
						
					}
				}
			}

			public void keyReleased(KeyEvent e)
			{
				if (e.keyCode == SWT.CTRL)
				{
					Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.setKeyCtrlPressed(false);
				}
			}
		});
	}

	public void setSignalViewerComposite(SignalViewerComposite composite)
	{
		if (signalViewerModel != null)
		{
			signalViewerModel.signalModel.removeSignalListener(this);
		}
		this.composite = composite;
		this.signalViewerModel = composite.signalViewerModel;
		signalViewerModel.signalModel.addSignalListener(this);

		this.addMouseMoveListener(MouseController.newInstance(signalViewerModel));
		this.addMouseListener(MouseController.newInstance(signalViewerModel));
	}

	private long paintProgress(GC gc, long lastpaint)
	{
		long result = lastpaint;
		long currentTime = System.currentTimeMillis();

		if (currentTime - lastpaint > Constants.paintProgressTime)
		{
			Font f2 = Common.getInstance().fontsManager.getSystemFont(FontsManager.fontSignalNotLoaded);
			gc.setFont(f2);
			gc.setForeground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorError));
			gc.drawText("Bitte warten ...", 0, 0);
			result = currentTime;
		}

		return result;
	}

	private void paint(PaintEvent e)
	{
		long lastPaintTime = System.currentTimeMillis();

		GC gc = e.gc;
		int repaintOffset = 0;
		int repaintWidth = this.getSize().x;
		this.repaintHeight = this.getSize().y;
		if (repaintOffset < 0)
			repaintOffset = 0;
		if ((!resized) && !signalViewerModel.isRedraw())
		{
			if (signalViewerModel.isMouseDown())
			{
				gc.drawImage(signalViewerModel.getCurrentImage(), repaintOffset, 0);
				return;
			}
			if (signalViewerModel.isMouseUp())
			{
				gc.drawImage(signalViewerModel.getCurrentImage(), repaintOffset, 0);
				this.drawCursor(gc);
				signalViewerModel.setMouseUp(false);
				return;
			}

			// Draw current Cursor
			if ((signalViewerModel.isMouseMoved()) && (signalViewerModel.getCurrentImage() != null)
					&& signalViewerModel.getMouseMoveXPosition() > 0)
			{
				gc.drawImage(signalViewerModel.getCurrentImage(), repaintOffset, 0);
				this.drawCursor(gc);
				return;
			}

			gc.drawImage(signalViewerModel.getCurrentImage(), repaintOffset, 0);
			return;
		}

		signalViewerModel.resetAllImages();
		/* setzt die Nullinie aller Channels */

		signalViewerModel.setChannelZeroLine();
		// this.setChannelZeroLine(dataModel.getNumberOfVisibleChannels());

		Rectangle rect = new Rectangle(0, 0, repaintWidth, this.repaintHeight);
		Image newImage = new Image(display, rect);

		lastPaintTime = paintProgress(gc, lastPaintTime);

		signalViewerModel.setGraphImage(newImage);
		GC drawableGC = new GC(newImage);

		startTime = signalViewerModel.getTimeinMillisecsForPixel(repaintOffset);
		endTime = signalViewerModel.getTimeinMillisecsForPixel(repaintWidth);

		paintGrid(drawableGC, repaintOffset, repaintWidth);
		if (signalViewerModel.signalModel.isSignalLoaded())
		{
			if (Common.getInstance().triggerModel.getActiveEventEntry() != null)
			{
				paintTriggers(drawableGC, repaintOffset, repaintWidth);
				// lastPaintTime = paintProgress(gc, lastPaintTime);
			}
			if (Common.getInstance().artefactModel.getActiveEventEntry() != null)
			{
				paintArtefacts(drawableGC, repaintOffset, repaintWidth);
			}
			lastPaintTime = paintProgress(gc, lastPaintTime);
			lastPaintTime = paintSignal(drawableGC, repaintOffset, repaintWidth, gc, lastPaintTime);

		}
		else
		{
			final String message = "kein Signal geladen";

			drawableGC.setForeground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorError));
			Font f = Common.getInstance().fontsManager.getSystemFont(FontsManager.fontSignalNotLoaded);
			drawableGC.setFont(f);

			Point messageSize = drawableGC.stringExtent(message);
			int w = (repaintWidth - messageSize.x) / 2;
			int h = (repaintHeight - messageSize.y) / 2;
			if (w < 0)
			{
				w = 0;
			}
			if (h < 0)
			{
				h = 0;
			}
			drawableGC.drawText(message, w, h, true);
		}

		drawableGC.dispose();
		drawableGC = null;
		// Feste Bestandteile abgearbeitet

		// Variable Bestandteile
		signalViewerModel.setCurrentImage(new Image(Display.getDefault(), newImage, SWT.IMAGE_COPY));
		drawableGC = new GC(signalViewerModel.getCurrentImage());
		this.drawSelectionLine(drawableGC, signalViewerModel.selection, startTime, endTime,
				Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorSelectionLine));

		drawableGC.dispose();
		drawableGC = null;

		// Das finale Bild plotten:
		gc.drawImage(signalViewerModel.getCurrentImage(), repaintOffset, 0);
		if (resized)
		{
			resized = false;
		}
		signalViewerModel.setRedraw(false);
	}

	private void paintTriggers(GC gc, int repaintOffset, int repaintWidth)
	{
		visibleTriggers.clear();

		this.composite.signalInfoComposite.setHeartFrequency(0, Constants.ArgumentNotSet);

		EventEntry activeTrigger = Common.getInstance().triggerModel.getActiveEventEntry();
		EventEntry secondaryTrigger = Common.getInstance().triggerModel.getSecondaryEventEntry();

		if (activeTrigger != null)
		{
			gc.setForeground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorTriggerForeground));
			gc.setBackground(Common.getInstance().signalViewerColors
					.getSignalViewerColor(SignalViewerColors.colorTriggerBackground));
			gc.setFont(Common.getInstance().fontsManager.getSystemFont(FontsManager.fontTrigger));

			boolean firstBeatSet = false;
			long firstBeatTime = Long.MIN_VALUE;
			long lastBeatTime = Long.MIN_VALUE;
			long beatCount = 0;

			double samplerate = activeTrigger.getSampleRate();
			int referencePulseWidth = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel
					.getReferencePulseWidth();
			long startEventSample = signalViewerModel.getSampleNumber(referencePulseWidth, samplerate);
			long endEventSample = signalViewerModel.getSampleNumber(repaintWidth, samplerate);

			List<Event> triggers = Common.getInstance().triggerModel.readEvents(startEventSample, endEventSample);
			for (Event currentEvent : triggers)
			{
				long sampleNr = currentEvent.getSampleStamp();

				long timeInSignalSamples = (long) (((double) sampleNr / samplerate) * (double) signalViewerModel.signalModel
						.getSamplingFrequency());
				long timeInMilliSeconds = this.signalViewerModel.signalModel
						.getTimeInMilliSecsForSample(timeInSignalSamples);
				int pixel = signalViewerModel.getPixelForSampleNumber(sampleNr, activeTrigger.getSampleRate());

				if (!firstBeatSet)
				{
					firstBeatTime = timeInMilliSeconds;
					lastBeatTime = timeInMilliSeconds;
					firstBeatSet = true;
					beatCount++;
				}

				if (timeInMilliSeconds > lastBeatTime)
				{
					lastBeatTime = timeInMilliSeconds;
					beatCount++;
				}

				String triggerName = currentEvent.getType();
				Point triggerSize = gc.stringExtent(triggerName);

				// short line below trigger name
				gc.drawLine(pixel, triggerSize.y + 2, pixel, triggerSize.y + 12);
				gc.drawString(triggerName, pixel - (triggerSize.x / 2), 0, true);

				visibleTriggers.add(new TriggerDataVisible(currentEvent, pixel, timeInMilliSeconds,
						timeInSignalSamples, activeTrigger.getComment()));
			}
			double heartFrequency = Double.MIN_VALUE;

			if (beatCount > 1)
			{
				heartFrequency = ((double) (beatCount - 1) / (double) (lastBeatTime - firstBeatTime)) * (double) 60000;
				if (Constants.printHeartFrequence)
					System.out.println(firstBeatTime + " - " + lastBeatTime + " Anzahl:" + beatCount + " HerzFrequenz:"
							+ heartFrequency);
			}
			else
			{
				if (Constants.printHeartFrequence)
					System.out.println("Keine Herzfrequenz.");
			}

			if (secondaryTrigger != null)
			{
				gc.setAlpha(150);
				gc.setForeground(Common.getInstance().signalViewerColors
						.getSignalViewerColor(SignalViewerColors.colorSecondaryTriggerForeground));
				gc.setBackground(Common.getInstance().signalViewerColors
						.getSignalViewerColor(SignalViewerColors.colorTriggerBackground));
				gc.setFont(Common.getInstance().fontsManager.getSystemFont(FontsManager.fontTrigger));
				samplerate = secondaryTrigger.getSampleRate();
				startEventSample = signalViewerModel.getSampleNumber(referencePulseWidth, samplerate);
				endEventSample = signalViewerModel.getSampleNumber(repaintWidth, samplerate);

				List<Event> secondaryTriggers = Common.getInstance().triggerModel.readSecondaryTriggerlistEvents(
						startEventSample, endEventSample);
				for (Event currentEvent : secondaryTriggers)
				{
					long sampleNr = currentEvent.getSampleStamp();

					long timeInSignalSamples = (long) (((double) sampleNr / samplerate) * (double) signalViewerModel.signalModel
							.getSamplingFrequency());
					long timeInMilliSeconds = this.signalViewerModel.signalModel
							.getTimeInMilliSecsForSample(timeInSignalSamples);
					int pixel = signalViewerModel.getPixelForSampleNumber(sampleNr, secondaryTrigger.getSampleRate());

					String triggerName = currentEvent.getType();
					Point triggerSize = gc.stringExtent(triggerName);

					// short line below trigger name
					gc.drawLine(pixel, triggerSize.y + 2, pixel, triggerSize.y + 12);
					gc.drawString(triggerName, pixel - (triggerSize.x / 2), 0, true);

					visibleTriggers.add(new TriggerDataVisible(currentEvent, pixel, timeInMilliSeconds,
							timeInSignalSamples, secondaryTrigger.getComment()));
				}
				gc.setAlpha(255);
			}

			this.composite.signalInfoComposite.setHeartFrequency(heartFrequency, beatCount > 1);
		}
	}

	private void paintArtefacts(GC gc, int repaintOffset, int repaintWidth)
	{
		gc.setAlpha(100);
		EventEntry activeArtefactEntry = Common.getInstance().artefactModel.getActiveEventEntry();
		double samplingFrequency = activeArtefactEntry.getSampleRate();

		if (activeArtefactEntry != null)
		{
			int referencePulseWidth = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel
					.getReferencePulseWidth();
			long startVisibleSamplestamp = signalViewerModel.getSampleNumber(referencePulseWidth, samplingFrequency);
			long endVisibleSamplestamp = signalViewerModel.getSampleNumber(repaintWidth, samplingFrequency);
			List<Artefact> artefacts = Common.getInstance().artefactModel.read(startVisibleSamplestamp,
					endVisibleSamplestamp);
			for (int i = 0; i < artefacts.size(); i++)
			{
				Artefact artefact = artefacts.get(i);
				Event artefactStartEvent = artefact.getStartEvent();
				Event artefactEndEvent = artefact.getEndEvent();

				long artefactStartSamplestamp = artefactStartEvent.getSampleStamp() > startVisibleSamplestamp ? artefactStartEvent
						.getSampleStamp() : startVisibleSamplestamp;
				long artefactEndSamplestamp = artefactEndEvent.getSampleStamp() > endVisibleSamplestamp ? endVisibleSamplestamp
						: artefactEndEvent.getSampleStamp();

				int artefactStartXinPixel = signalViewerModel.getPixelForSampleNumber(artefactStartSamplestamp,
						samplingFrequency);
				int artefactWidthInPixel = signalViewerModel.getPixelForSampleNumber(artefactEndSamplestamp,
						samplingFrequency) - artefactStartXinPixel;
				int artefactHeightInPixel = 4;
				Color artefactColor = artefact.getColor();
				int artefactTypeIndex = artefact.getTypeIndex();
				int artefactChannel = artefact.getChannel();
				Color foregroundColor = gc.getForeground();
				int lineStyle = gc.getLineStyle();
				int lineWidth = gc.getLineWidth();
				gc.setForeground(Colors.Black);
				gc.setLineStyle(SWT.LINE_SOLID | SWT.LINE_DASH);
				gc.setLineWidth(2);
				if (artefactChannel == -1)
				{
					for (int k = 0; k < this.signalViewerModel.getNumberOfVisibleChannels(); k++)
					{
						int artefactStartYinPixelOffset = signalViewerModel.getChannelZeroLine(k)
								- signalViewerModel.getSignalGraphPositionY() - 50;
						int artefactStartYinPixel = artefactStartYinPixelOffset + artefactTypeIndex
								* (artefactHeightInPixel + 1);
						gc.setBackground(artefactColor);
						gc.fillRectangle(artefactStartXinPixel + 1, artefactStartYinPixel, artefactWidthInPixel,
								artefactHeightInPixel);

						if (artefact.isSelected()
								&& (artefact.getSelectedChannel() == k || artefact.getSelectedChannel() == -1))
						{
							gc.drawRectangle(artefactStartXinPixel, artefactStartYinPixel - 1,
									artefactWidthInPixel + 2, artefactHeightInPixel + 2);
						}
					}
				}
				else
				{
					if (signalViewerModel.isPaintChannel(artefactChannel))
					{
						int virtualChannel = signalViewerModel.getVirtualChannelNumber(artefactChannel);
						int artefactStartYinPixelOffset = signalViewerModel.getChannelZeroLine(virtualChannel)
								- signalViewerModel.getSignalGraphPositionY() - 50;
						int artefactStartYinPixel = artefactStartYinPixelOffset + artefactTypeIndex
								* (artefactHeightInPixel + 1);
						gc.setBackground(artefactColor);
						gc.fillRectangle(artefactStartXinPixel + 1, artefactStartYinPixel, artefactWidthInPixel,
								artefactHeightInPixel);
						if (artefact.isSelected())
						{
							gc.drawRectangle(artefactStartXinPixel, artefactStartYinPixel - 1,
									artefactWidthInPixel + 2, artefactHeightInPixel + 2);
						}
					}
				}
				gc.setForeground(foregroundColor);
				gc.setLineStyle(lineStyle);
				gc.setLineWidth(lineWidth);
			}
		}
		gc.setAlpha(255);
	}

	private void paintGrid(GC gc, int repaintOffset, int repaintWidth)
	{
		gc.setLineWidth(1);
		gc.setForeground(Common.getInstance().signalViewerColors
				.getSignalViewerColor(SignalViewerColors.colorGridForeground1));
		gc.setBackground(Common.getInstance().signalViewerColors
				.getSignalViewerColor(SignalViewerColors.colorBackground));

		double gridSpacingX;
		double gridSpacingY;
		if (signalViewerModel.dynamicGrid)
		{
			gridSpacingX = this.signalViewerModel.getGridSpacingX();
			gridSpacingY = this.signalViewerModel.getGridSpacingY();
		}
		else
		{
			gridSpacingX = this.signalViewerModel.getStaticGridSize();
			gridSpacingY = this.signalViewerModel.getStaticGridSize();
		}
		int canvasHeight = this.getSize().y;

		// Fill Background up
		gc.fillRectangle(0, 0, repaintWidth, canvasHeight);

		if (this.signalViewerModel.isGridShow())
		{
			gc.setLineStyle(SWT.LINE_SOLID);

			// print lines for grid, if the grid spacing is big enough
			if (gridSpacingX > 1 && gridSpacingY > 1)
			{
				// draw 1mm lines
				drawHorizontalGrid(gc, gridSpacingY, Common.getInstance().signalViewerColors.getGridColor(3));
				drawVerticalGrid(gc, gridSpacingX, Common.getInstance().signalViewerColors.getGridColor(3));

				// draw 5mm lines
				drawHorizontalGrid(gc, gridSpacingY * 5, Common.getInstance().signalViewerColors.getGridColor(1));
				drawVerticalGrid(gc, gridSpacingX * 5, Common.getInstance().signalViewerColors.getGridColor(1));

				// draw 10mm lines
				drawHorizontalGrid(gc, gridSpacingY * 10, Common.getInstance().signalViewerColors.getGridColor(2));
				drawVerticalGrid(gc, gridSpacingX * 10, Common.getInstance().signalViewerColors.getGridColor(2));
			}
		}

		// Zeichne Null-Linie
		if (signalViewerModel.signalModel.isSignalLoaded())
		{
			int zeroLine = 0;
			Font font_channelName = Common.getInstance().fontsManager.getSystemFont(FontsManager.fontChannelName);
			Font font_legend = Common.getInstance().fontsManager.getSystemFont(FontsManager.fontLegend);

			for (int k = 0; k < this.signalViewerModel.getNumberOfVisibleChannels(); k++)
			{
				Integer currentChannel = signalViewerModel.getRealChannelNumber(k);

				if (currentChannel != null)
				{
					zeroLine = signalViewerModel.getChannelZeroLine(k) - signalViewerModel.getSignalGraphPositionY();

					gc.setForeground(Common.getInstance().signalViewerColors
							.getSignalViewerColor(SignalViewerColors.colorZeroLine));
					if (signalViewerModel.isShowCenterLine())
					{
						// gc.setLineWidth(NULL_LINE_WIDTH);
						gc.setForeground(Common.getInstance().signalViewerColors.getGridColor(NULL_LINE_WIDTH));
						gc.drawLine(0, zeroLine, repaintWidth, zeroLine);
					}

					gc.setForeground(Common.getInstance().signalViewerColors.getChannelColor(k));
					gc.setFont(font_channelName);
					gc.drawString(signalViewerModel.signalModel.getChannelName(currentChannel),
							5 - signalViewerModel.getSignalGraphPositionX(), zeroLine + 25);

				}
			}

			// Legende
			if (signalViewerModel.dynamicGrid)
			{
				double millivolPerKasten = (double) signalViewerModel.getGridSpacingY()
						/ (double) signalViewerModel.getPixelPerMilliVolt();
				gc.setFont(font_legend);

				gc.setForeground(Common.getInstance().signalViewerColors
						.getSignalViewerColor(SignalViewerColors.colorLegend));
				gc.drawString("] " + millivolPerKasten + " mv", 5 - signalViewerModel.getSignalGraphPositionX(),
						zeroLine + 100);
			}

			if (signalViewerModel.isShowCenterLine())
			{
				// Mittellinie
				// TODO Bei mehr Kanälen ändern
				if (this.signalViewerModel.getNumberOfVisibleChannels() == 2)
				{
					gc.setForeground(Common.getInstance().signalViewerColors
							.getSignalViewerColor(SignalViewerColors.colorDashLine));
					gc.setLineStyle(SWT.LINE_DASH);
					gc.setLineWidth(2);
					int middleDashLine = (signalViewerModel.getChannelZeroLine(0) + signalViewerModel
							.getChannelZeroLine(1)) / 2 - signalViewerModel.getSignalGraphPositionY();
					gc.drawLine(0, middleDashLine, repaintWidth, middleDashLine);
				}
			}
			gc.setLineStyle(SWT.LINE_SOLID);
			gc.setLineWidth(1);
		} // endif isSignalLoaded
	}

	/**
	 * draws horizontal lines for grid
	 * 
	 * @param gc
	 * @param gridSpacing
	 *            spacing between lines
	 * @param lineColor
	 *            line color
	 */
	private void drawHorizontalGrid(GC gc, double gridSpacing, Color lineColor)
	{
		int nextHorizontalLine = -this.signalViewerModel.getSignalGraphPositionY();
		int canvasHeight = this.getSize().y;
		int canvasWidth = this.getSize().x;

		while (nextHorizontalLine >= 0)
		{
			gc.setForeground(lineColor);
			gc.drawLine(0, nextHorizontalLine, canvasWidth, nextHorizontalLine);
			nextHorizontalLine -= gridSpacing;
		}

		nextHorizontalLine = -this.signalViewerModel.getSignalGraphPositionY();
		while (nextHorizontalLine <= canvasHeight)
		{
			gc.setForeground(lineColor);
			gc.drawLine(0, nextHorizontalLine, canvasWidth, nextHorizontalLine);
			nextHorizontalLine += gridSpacing;
		}
	}


	/**
	 * draws vertical lines for grid
	 * 
	 * @param gc
	 * @param gridSpacing
	 *            spacing between lines
	 * @param lineColor
	 *            line color
	 */
	private void drawVerticalGrid(GC gc, double gridSpacing, Color lineColor)
	{
		int nextVerticalLine = (int) (gridSpacing - (this.signalViewerModel.getTimeinMillisecsForPixel(0)
				* signalViewerModel.getPixelPerSecond() / 1000)
				% gridSpacing);
		int canvasHeight = this.getSize().y;
		int canvasWidth = this.getSize().x;

		while (nextVerticalLine <= canvasWidth)
		{
			gc.setForeground(lineColor);
			gc.drawLine((int) nextVerticalLine, 0, (int) nextVerticalLine, canvasHeight);
			nextVerticalLine += gridSpacing;
		}
	}

	// Der Signal zeichnen
	private long paintSignal(GC g, int repaintOffset, int repaintWidth, GC directPaint, long lastpaintTime)
	{
		if (signalViewerModel.signalModel.isSignalLoaded())
		{

			int startOffsetX = 0;

			g.setForeground(Common.getInstance().signalViewerColors.getChannelColor(1));
			g.setLineWidth(1);

			int lastX = 0;
			Integer lastY = null;

			int x;
			int y;
			Double value;
			// int currentSample = 0;
			// int lastSample = -1;

			int tPixPerSecf = signalViewerModel.getReferencePulseWidth() / 3;
			startOffsetX = signalViewerModel.getReferencePulseWidth();

			double tPixPerMv = signalViewerModel.getPixelPerMilliVolt();

			if (currentTimeStart != signalViewerModel.getTimeinMillisecsForPixel(repaintOffset + startOffsetX))
				signalViewerModel.notifyViewSectionChanged(new ViewEvent(this));

			this.currentTimeStart = signalViewerModel.getTimeinMillisecsForPixel(repaintOffset + startOffsetX);
			this.currentTimeEnd = signalViewerModel.getTimeinMillisecsForPixel(repaintOffset + repaintWidth);


			// Jeder Kanal durchlaufen
			for (int k = 0; k < this.signalViewerModel.getNumberOfVisibleChannels(); k++)
			{
				Integer currentChannel = signalViewerModel.getRealChannelNumber(k);
				if (currentChannel != null)
				{
					int polarity = signalViewerModel.isChannelPolarityReversed(currentChannel) ? -1 : 1;
					int currentZeroLine = signalViewerModel.getChannelZeroLine(k);

					// set reference pulse (Kalibrierzange):
					g.setForeground(Common.getInstance().signalViewerColors.getChannelColor(k));
					int tcurrentZero = currentZeroLine - signalViewerModel.getSignalGraphPositionY();

					// forward
					g.drawLine(0, tcurrentZero, tPixPerSecf, tcurrentZero);
					int tValue = (int) (tcurrentZero - tPixPerMv);

					// up
					g.drawLine(tPixPerSecf, tcurrentZero, tPixPerSecf, tValue);

					// KaliberZacke
					g.drawLine(tPixPerSecf, tValue, 2 * tPixPerSecf, tValue);

					// down
					g.drawLine(2 * tPixPerSecf, tValue, 2 * tPixPerSecf, tcurrentZero);

					// forward
					g.drawLine(2 * tPixPerSecf, tcurrentZero, 3 * tPixPerSecf, tcurrentZero);

					lastX = startOffsetX;
					// Ecg-Signal zeichnen
					for (int i = startOffsetX; i <= repaintWidth; i++)
					{
						long time = signalViewerModel.getTimeinMillisecsForPixel(repaintOffset + i);
						g.setForeground(signalViewerModel.getColorForTime(time, k));

						value = signalViewerModel.getValue(currentChannel, repaintOffset + i,
								Common.getInstance().signalModel.getSamplingFrequency());
						lastpaintTime = this.paintProgress(directPaint, lastpaintTime);
						x = i;

						if (value != null)
						{
							value *= polarity;
							y = signalViewerModel.getPixel(k, value);
							if (i == startOffsetX)
							{
								lastY = y;
							}
							if (lastY == null)
							{
								lastY = y;
							}
							g.drawLine(lastX, lastY, x, y);
							lastY = y;
						}
						else
						{
							lastY = null;
						}
						lastX = x;
					}
					lastX = startOffsetX;
					lastY = null;
				} // currentChannel!=null

				lastpaintTime = this.paintProgress(directPaint, lastpaintTime);
			}
			g.setForeground(Common.getInstance().signalViewerColors.getChannelColor(1));
		}

		return lastpaintTime;
	}

	private void drawVerticalLine(GC drawableGC, int positionX, Color c)
	{
		drawableGC.setForeground(c);
		drawableGC.drawLine(positionX, 0, positionX, this.getSize().y);
		this.signalViewerModel.setMouseMoved(false);
	}

	private void drawSelectionLine(GC drawableGC, long selectionStartMs, long selectionEndMs, Color color)
	{
		if ((selectionStartMs != Constants.notSelected) & (selectionEndMs != Constants.notSelected))
		{
			try
			{
				int XS = signalViewerModel.getPixelForTimeInMilliSeconds(selectionStartMs);
				int XE = signalViewerModel.getPixelForTimeInMilliSeconds(selectionEndMs);

				if (color == null)
				{
					color = Common.getInstance().signalViewerColors
							.getSignalViewerColor(SignalViewerColors.colorSelectionLine);
				}
				drawableGC.setBackground(color);
				drawableGC.setAlpha(50);
				drawableGC.fillRectangle(XS, 0, XE - XS, this.getSize().y);
				this.signalViewerModel.setMouseMoved(false);

				// EKG-Zirkel: Herzfrequenz und ms
				drawableGC.setAlpha(255);
				;
				drawableGC.setForeground(color);
				drawableGC.drawString((selectionEndMs - selectionStartMs) + " ms\n"
						+ (60000 / (selectionEndMs - selectionStartMs)) + " BPM", XS + 5, this.getSize().y - 36, true);
			}
			catch (Exception e)
			{
			}
		}
	}

	private void drawSelectionLine(GC g, Selection selection)
	{
		drawSelectionLine(g, selection.getSelectionStart(), selection.getSelectionEnd(), null);
	}

	private void drawSelectionLine(GC g, Selection selection, long startTime, long EndTime, Color c)
	{
		if (selection.isSelected())
		{
			long ss = selection.getSelectionStart();
			long se = selection.getSelectionEnd();

			if ((ss < startTime) & (se > startTime))
			{
				ss = startTime;
			}
			if (se > EndTime)
			{
				se = EndTime;
			}

			if ((ss >= startTime) & (se <= EndTime))
			{
				drawSelectionLine(g, ss, se, c);
				return;
			}
			else
			{
				return;
			}
		}
		else
		{
			return;
		}
	}

	public void setResized(boolean resized)
	{
		this.resized = resized;
	}

	/*
	 * public void mouseDoubleClick(MouseEvent e) { } public void
	 * mouseDown(MouseEvent e) { } public void mouseUp(MouseEvent e) { } public
	 * void widgetSelected(SelectionEvent e){ } public void
	 * widgetDefaultSelected(SelectionEvent e){ }
	 */

	public void setCurrentImageWithSelection()
	{
		Image i = new Image(display, signalViewerModel.getGraphImage(), SWT.IMAGE_COPY);
		GC g = new GC(i);

		int x = signalViewerModel.getPixelForTimeInMilliSeconds(signalViewerModel.selection.getSelectionStart());
		drawVerticalLine(g, x,
				Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorSelectionStart));
		drawCurrentSelection(g, signalViewerModel.selection);
		g.dispose();
		g = null;
		signalViewerModel.setCurrentImage(i);
	}

	private void drawCurrentSelection(GC g, Selection selection)
	{
		this.drawSelectionLine(g, selection);
		this.redraw();
	}

	private void drawCursor(GC g)
	{
		Color c = Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorCursor);
		this.drawVerticalLine(g, signalViewerModel.getMouseMoveXPosition(), c);
	}

	public void OnMouseDown()
	{
		this.setCursor(Cursors.sizeCursor);
		long timePointinMillisecs = signalViewerModel.getTimeinMillisecsForPixel(signalViewerModel
				.getMouseLeftDownXPosition());
		RangeDiff r = signalViewerModel.selection.calcDiffToStartOrEndPoint(timePointinMillisecs);
		if (!Selection.atStartOrEndPoint(r))
		{
			signalViewerModel.selection.unSelect();
			signalViewerModel.selection.setSelectionStart(timePointinMillisecs);

			Image i = new Image(display, signalViewerModel.getGraphImage(), SWT.IMAGE_COPY);
			GC g = new GC(i);

			this.drawVerticalLine(g, signalViewerModel.getMouseLeftDownXPosition(),
					Common.getInstance().signalViewerColors.getSignalViewerColor(SignalViewerColors.colorSelectionLine));


			g.dispose();
			g = null;
			signalViewerModel.setCurrentImage(i);
			tempSelection = (Selection) signalViewerModel.selection.clone();
		}
		else
		{
			signalViewerModel.selection = signalViewerModel.selection.RangeSelection(r);
			tempSelection = (Selection) signalViewerModel.selection.clone();
			tempSelection.addSelectionPoint(timePointinMillisecs);
			// setCurrentImageWithSelection();
		}
		this.redraw();
	}

	public void OnMouseUp()
	{
		setSelection(tempSelection);
		this.setCursor(Cursors.defaultCursor);
	}

	public void setSelection(Selection s)
	{
		if (tempSelection != null)
		{
			signalViewerModel.selection.setSelection(s.getSelectionStart(), s.getSelectionEnd());

			setCurrentImageWithSelection();
			this.redraw();
		}
	}

	public void OnMouseMoved(int mouseMoveXPosition, int mouseMoveYPosition)
	{
		if (signalViewerModel.isMouseDown())
		{
			this.setCursor(Cursors.sizeCursor);
			ArtefactModel artefactModel = Common.getInstance().artefactModel;
			Artefact selectedArtefact = artefactModel.getSelectedArtefact();
			if (selectedArtefact != null && (selectedArtefact.isStartSelected() || selectedArtefact.isEndSelected()))
			{
				long samplestamp = signalViewerModel.getSampleNumber(mouseMoveXPosition,
						Common.getInstance().artefactModel.getActiveEventEntry().getSampleRate());
				artefactModel.changeSelectedArtefactStartOrEnd(samplestamp);
				artefactModel.setActiveEventEntryChanged(true);
				artefactModel.setModifyingSelectedArtefact(true);
				Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
			}
			else
			{
				tempSelection = (Selection) signalViewerModel.selection.clone();
				long timeInMilliSecs = signalViewerModel.getTimeinMillisecsForPixel(signalViewerModel
						.getMouseMoveXPosition());
				tempSelection.addSelectionPoint(timeInMilliSecs);
				Image i = new Image(display, signalViewerModel.getGraphImage(), SWT.IMAGE_COPY);
				GC g = new GC(i);
				drawCurrentSelection(g, tempSelection);
				g.dispose();
				g = null;
				signalViewerModel.setCurrentImage(i);
			}
		}
		else
		{
			if (signalViewerModel.selection.atStartOrEndPoint(signalViewerModel
					.getTimeinMillisecsForPixel(signalViewerModel.getMouseMoveXPosition()))
					|| Common.getInstance().artefactModel.isAtStartOrEndOfSelectedArtefact(mouseMoveXPosition))
			{
				this.setCursor(Cursors.sizeCursor);
			}
			else
			{
				this.setCursor(Cursors.defaultCursor);
			}


			// tool tip with trigger information
			if (mouseMoveYPosition < 20)
			{
				TriggerDataVisible triggerDataVisible = Common.getNextTrigger(
						signalViewerModel.getMouseMoveXPosition(), this.visibleTriggers);
				String tooltipText = "";
				String channelName = "";

				if (triggerDataVisible != null)
				{
					Event event = triggerDataVisible.event;
					AnnotationCode annotationCode = Constants_AnnotationCodes
							.getAnnotationToCode(triggerDataVisible.event.getType());
					if (triggerDataVisible != null)
					{
						if (triggerDataVisible.triggerChannelName != null)
						{
							channelName = triggerDataVisible.triggerChannelName != null ? "("
									+ triggerDataVisible.triggerChannelName + ")" : "";
						}

						String sampleNr = triggerDataVisible.sample + "";
						String type = (annotationCode != null) ? String.format("Type: %s, %s", annotationCode.name,
								annotationCode.comment) : "";
						tooltipText = String.format("Trigger %s\n%s\nSample #%s\n%s", channelName,
								TimeUtil.getFullDateString(triggerDataVisible.time + signalViewerModel.getOffset(), 0),
								sampleNr, type);
					}
					if (event.getComment().length() > 0)
					{
						tooltipText = tooltipText + "\nKommentar:" + event.getComment();
					}
				}
				else
				{
					tooltipText = "";
				}
				this.setToolTipText(tooltipText);
			}
			else
				this.setToolTipText("");
		}
		this.redraw();
	}

	public int getSignalGraphWidth()
	{
		return signalGraphWidth;
	}

	public void setSignalGraphWidth(int signalGraphSizeX)
	{
		this.signalGraphWidth = signalGraphSizeX;
	}

	public int getSignalGraphHeight()
	{
		return signalGraphHeight;
	}

	public void setSignalGraphHeight(int signalGraphSizeY)
	{
		this.signalGraphHeight = signalGraphSizeY;
	}

	public long getCurrentStartTime()
	{
		return currentTimeStart;
	}

	public long getCurrentEndTime()
	{
		return currentTimeEnd;
	}

	public void signalChanged(SignalEvent e)
	{
		// most is done in paint

		if (tempSelection != null)
		{
			tempSelection.unSelect();

		}
	}

	public void unselectTempSelection()
	{
		if (tempSelection != null)
			tempSelection.unSelect();
	}

	public ContextMenu getContextMenu()
	{
		return contextMenu;
	}
}
