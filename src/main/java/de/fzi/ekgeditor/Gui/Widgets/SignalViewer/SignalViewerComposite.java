package de.fzi.ekgeditor.Gui.Widgets.SignalViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Colors;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.SettingsEvent;
import de.fzi.ekgeditor.events.SettingsListener;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.events.ViewEvent;
import de.fzi.ekgeditor.events.ViewListener;
import de.fzi.ekgeditor.utils.TimeUtil;

public class SignalViewerComposite extends Composite implements SignalListener, ViewListener, SettingsListener
{
	public SignalViewerModel signalViewerModel = null;

	// private static final int OVERVIEW_GRAPH_HEIGHT = 80;
	private static final int OVERVIEW_GRAPH_HEIGHT = 20;

	private Composite graphComposite;
	private Composite emptyComposite;
	public Composite scrolledComposite;
	public SignalInfoComposite signalInfoComposite;
	private Canvas horizontalCanvas;
	private Canvas verticalCanvas;
	// private Canvas overviewTimeCanvas;
	public SignalViewerCanvas signalViewerCanvas;

	private ScrollBar verticalScrollbar;
	private ScrollBar horizontalScrollbar;

	private Point stringSize = null;


	public SignalViewerComposite(Composite parent, SignalModel signalModel)
	{
		super(parent, SWT.NULL);
		Display display = this.getDisplay();
		signalViewerModel = new SignalViewerModel(signalModel);
		this.stringSize = this.getStringSize(display);
	}

	public void init()
	{
		SignalViewerComposite self = this;
		signalViewerModel.setSignalViewerComposite(self);
		initGUI();

		this.signalViewerCanvas = new SignalViewerCanvas(scrolledComposite);
		this.signalViewerCanvas.setSignalViewerComposite(self);

		scrolledComposite.addControlListener(new ControlListener()
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
		verticalScrollbar = scrolledComposite.getVerticalBar();
		verticalScrollbar.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				vertScrollBarSelected(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				widgetSelected(arg0);
			}
		});

		horizontalScrollbar = scrolledComposite.getHorizontalBar();
		horizontalScrollbar.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				horScrollBarSelected(true);
			}

			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				widgetSelected(arg0);
			}
		});

		Common.getInstance().signalModel.addSignalListener(this);
		signalViewerModel.addViewListener(this);
		Common.getInstance().addSettingsListener(this);
		// this.colored();
	}

	private void initGUI()
	{
		graphComposite = new Composite(this, SWT.NULL);
		emptyComposite = new Composite(graphComposite, SWT.NULL);
		horizontalCanvas = new Canvas(graphComposite, SWT.NULL);
		verticalCanvas = new Canvas(graphComposite, SWT.NULL);
		scrolledComposite = new Composite(graphComposite, SWT.V_SCROLL | SWT.H_SCROLL);

		graphComposite.setSize(new Point(627, 317));

		GridData emptyCompositeLData = new GridData();
		emptyCompositeLData.verticalAlignment = GridData.BEGINNING;
		emptyCompositeLData.horizontalAlignment = GridData.BEGINNING;
		emptyCompositeLData.widthHint = this.stringSize.x + 7;
		emptyCompositeLData.heightHint = this.stringSize.y + 7;
		emptyCompositeLData.horizontalIndent = 0;
		emptyCompositeLData.horizontalSpan = 1;
		emptyCompositeLData.verticalSpan = 1;
		emptyCompositeLData.grabExcessHorizontalSpace = false;
		emptyCompositeLData.grabExcessVerticalSpace = false;
		emptyComposite.setLayoutData(emptyCompositeLData);
		emptyComposite.setSize(new org.eclipse.swt.graphics.Point(32, 32));
		FillLayout emptyCompositeLayout = new FillLayout(256);
		emptyComposite.setLayout(emptyCompositeLayout);
		emptyCompositeLayout.type = SWT.HORIZONTAL;
		emptyCompositeLayout.marginWidth = 0;
		emptyCompositeLayout.marginHeight = 0;
		emptyCompositeLayout.spacing = 0;
		emptyComposite.layout();

		GridData horizontalCanvasLData = new GridData();
		horizontalCanvasLData.verticalAlignment = GridData.BEGINNING;
		horizontalCanvasLData.horizontalAlignment = GridData.FILL;
		horizontalCanvasLData.widthHint = -1;
		horizontalCanvasLData.heightHint = this.stringSize.y + 7;
		horizontalCanvasLData.horizontalIndent = 2;
		horizontalCanvasLData.horizontalSpan = 1;
		horizontalCanvasLData.verticalSpan = 1;
		horizontalCanvasLData.grabExcessHorizontalSpace = true;
		horizontalCanvasLData.grabExcessVerticalSpace = false;
		horizontalCanvas.setLayoutData(horizontalCanvasLData);
		horizontalCanvas.setSize(new Point(611, 32));
		horizontalCanvas.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent evt)
			{
				horizontalCanvasPaintControl(evt);
			}
		});

		GridData verticalCanvasLData = new GridData();
		verticalCanvasLData.verticalAlignment = GridData.FILL;
		verticalCanvasLData.horizontalAlignment = GridData.BEGINNING;
		verticalCanvasLData.widthHint = this.stringSize.x + 7;
		verticalCanvasLData.heightHint = -1;
		verticalCanvasLData.horizontalIndent = 0;
		verticalCanvasLData.horizontalSpan = 1;
		verticalCanvasLData.verticalSpan = 1;
		verticalCanvasLData.grabExcessHorizontalSpace = false;
		verticalCanvasLData.grabExcessVerticalSpace = true;
		verticalCanvas.setLayoutData(verticalCanvasLData);
		verticalCanvas.setSize(new org.eclipse.swt.graphics.Point(32, 301));
		verticalCanvas.addPaintListener(new PaintListener()
		{
			// nicht implementiert für zwei Kanäle, es ist implementiert nur für
			// einen Kanal
			public void paintControl(PaintEvent evt)
			{
				// verticalCanvasPaintControl(evt);
			}
		});

		GridData scrolledCompositeLData = new GridData();
		scrolledCompositeLData.verticalAlignment = GridData.FILL;
		scrolledCompositeLData.horizontalAlignment = GridData.FILL;
		scrolledCompositeLData.widthHint = -1;
		scrolledCompositeLData.heightHint = -1;
		scrolledCompositeLData.horizontalIndent = 0;
		scrolledCompositeLData.horizontalSpan = 1;
		scrolledCompositeLData.verticalSpan = 1;
		scrolledCompositeLData.grabExcessHorizontalSpace = true;
		scrolledCompositeLData.grabExcessVerticalSpace = true;
		scrolledComposite.setLayoutData(scrolledCompositeLData);


		scrolledComposite.setSize(600, 400);
		FillLayout scrolledCompositeLayout = new FillLayout();
		scrolledCompositeLayout.type = SWT.HORIZONTAL;
		scrolledCompositeLayout.marginWidth = 0;
		scrolledCompositeLayout.marginHeight = 0;
		scrolledCompositeLayout.spacing = 0;
		scrolledComposite.setLayout(scrolledCompositeLayout);

		emptyComposite = new Composite(graphComposite, SWT.NULL);

		emptyCompositeLData = new GridData();
		emptyCompositeLData.verticalAlignment = GridData.BEGINNING;
		emptyCompositeLData.horizontalAlignment = GridData.BEGINNING;
		emptyCompositeLData.widthHint = this.stringSize.x + 7;
		emptyCompositeLData.heightHint = this.stringSize.y + 7;
		emptyCompositeLData.horizontalIndent = 0;
		emptyCompositeLData.horizontalSpan = 1;
		emptyCompositeLData.verticalSpan = 1;
		emptyCompositeLData.grabExcessHorizontalSpace = false;
		emptyCompositeLData.grabExcessVerticalSpace = false;
		emptyComposite.setLayoutData(emptyCompositeLData);
		emptyComposite.setSize(new org.eclipse.swt.graphics.Point(32, 32));
		emptyCompositeLayout = new FillLayout(256);
		emptyComposite.setLayout(emptyCompositeLayout);
		emptyCompositeLayout.type = SWT.HORIZONTAL;
		emptyCompositeLayout.marginWidth = 0;
		emptyCompositeLayout.marginHeight = 0;
		emptyCompositeLayout.spacing = 0;
		emptyComposite.layout();

		GridLayout graphCompositeLayout = new GridLayout(2, true);
		graphComposite.setLayout(graphCompositeLayout);
		graphCompositeLayout.marginWidth = 0;
		graphCompositeLayout.marginHeight = 0;
		graphCompositeLayout.numColumns = 2;
		graphCompositeLayout.makeColumnsEqualWidth = false;
		graphCompositeLayout.horizontalSpacing = 0;
		graphCompositeLayout.verticalSpacing = 0;
		graphComposite.layout();

		this.signalInfoComposite = new SignalInfoComposite(graphComposite, signalViewerModel);
		signalViewerModel.setSignalInfoComposite(signalInfoComposite);
		GridData signalInfoCompositeLData = new GridData();
		signalInfoCompositeLData.verticalAlignment = GridData.BEGINNING;
		signalInfoCompositeLData.horizontalAlignment = GridData.FILL;
		signalInfoCompositeLData.widthHint = -1;
		signalInfoCompositeLData.heightHint = OVERVIEW_GRAPH_HEIGHT + this.stringSize.y + 7;
		signalInfoCompositeLData.horizontalIndent = 0;
		signalInfoCompositeLData.horizontalSpan = 1;
		signalInfoCompositeLData.verticalSpan = 1;
		signalInfoCompositeLData.grabExcessHorizontalSpace = true;
		signalInfoCompositeLData.grabExcessVerticalSpace = false;
		signalInfoComposite.setLayoutData(signalInfoCompositeLData);
		/*
		 * 
		 * GridLayout signalInfoCompositeLayout = new GridLayout(1, false);
		 * signalInfoComposite.setLayout(signalInfoCompositeLayout);
		 * signalInfoCompositeLayout.marginWidth = 0;
		 * signalInfoCompositeLayout.marginHeight = 0;
		 * signalInfoCompositeLayout.numColumns = 1;
		 * signalInfoCompositeLayout.makeColumnsEqualWidth = false;
		 * signalInfoCompositeLayout.horizontalSpacing = 0;
		 * signalInfoCompositeLayout.verticalSpacing = 0;
		 * signalInfoComposite.layout();
		 */

		/*
		 * this.overviewTimeCanvas = new Canvas(this.signalInfoComposite,
		 * SWT.NULL); GridData overviewTimeCanvasLData = new GridData();
		 * overviewTimeCanvasLData.verticalAlignment = GridData.BEGINNING;
		 * overviewTimeCanvasLData.horizontalAlignment = GridData.FILL;
		 * overviewTimeCanvasLData.widthHint = -1;
		 * overviewTimeCanvasLData.heightHint = this.stringSize.y+7;
		 * overviewTimeCanvasLData.horizontalIndent = 0;
		 * overviewTimeCanvasLData.horizontalSpan = 1;
		 * overviewTimeCanvasLData.verticalSpan = 1;
		 * overviewTimeCanvasLData.grabExcessHorizontalSpace = true;
		 * overviewTimeCanvasLData.grabExcessVerticalSpace = false;
		 * overviewTimeCanvas.setLayoutData(overviewTimeCanvasLData);
		 * overviewTimeCanvas.setSize(new
		 * org.eclipse.swt.graphics.Point(611,32));
		 * overviewTimeCanvas.addPaintListener( new PaintListener() { public
		 * void paintControl(PaintEvent evt) {
		 * overviewTimeCanvasPaintControl(evt); } });
		 */


		/**
		 * this.overviewGraphComposite = new
		 * Composite(this.overviewComposite,SWT.NULL); GridData
		 * overviewGraphCompositeLData = new GridData();
		 * overviewGraphCompositeLData.verticalAlignment = GridData.BEGINNING;
		 * overviewGraphCompositeLData.horizontalAlignment = GridData.FILL;
		 * overviewGraphCompositeLData.widthHint = -1;
		 * overviewGraphCompositeLData.heightHint = OVERVIEW_GRAPH_HEIGHT;
		 * overviewGraphCompositeLData.horizontalIndent = 0;
		 * overviewGraphCompositeLData.horizontalSpan = 1;
		 * overviewGraphCompositeLData.verticalSpan = 1;
		 * overviewGraphCompositeLData.grabExcessHorizontalSpace = true;
		 * overviewGraphCompositeLData.grabExcessVerticalSpace = false;
		 * overviewGraphComposite.setLayoutData(overviewGraphCompositeLData);
		 * overviewGraphComposite.setSize(new
		 * org.eclipse.swt.graphics.Point(611,OVERVIEW_GRAPH_HEIGHT));
		 * GridLayout overviewGraphCompositeLayout = new GridLayout(2 , true);
		 * overviewGraphComposite.setLayout(overviewGraphCompositeLayout);
		 * overviewGraphCompositeLayout.marginWidth = 0;
		 * overviewGraphCompositeLayout.marginHeight = 0;
		 * overviewGraphComposite.layout();
		 **/


		FillLayout thisLayout = new FillLayout();
		thisLayout.type = SWT.VERTICAL;
		thisLayout.marginWidth = 0;
		thisLayout.marginHeight = 0;
		thisLayout.spacing = 0;

		signalInfoComposite.layout();
		graphComposite.layout();
		this.setLayout(thisLayout);
		this.layout();

	}

	/*
	 * private void colored(){
	 * horizontalCanvas.setBackground(dataModel.colorDarkRed);
	 * verticalCanvas.setBackground(dataModel.colorDarkRed);
	 * graphComposite.setBackground(dataModel.colorGreen);
	 * signalInfoComposite.setBackground(dataModel.colorCyan);
	 * signalViewerCanvas.setBackground(dataModel.colorDarkBlue);
	 * 
	 * }
	 */

	private void horizontalCanvasPaintControl(PaintEvent e)
	{
		int lastLabelX = Integer.MIN_VALUE;

		GC g = e.gc;

		// int pixelPerSecond = (int) dataModel.getPixelPerSecond();

		int repaintWidth = this.scrolledComposite.getClientArea().width;

		// long
		// time=dataModel.getTimeInMilliSecForSampleNumber(Constants.preSignalTime);
		
		long offset = signalViewerModel.getOffset();
		long time = signalViewerModel.getTimeinMillisecsForPixel(0) + offset;
		int nextVerticalLine = Integer.MIN_VALUE;
		
		// Round time to one sec, because the time markers should be every full second.
		time = time / 1000;
		time = time * 1000;

		Point strSize = null;

		while (true)
		{
			if (nextVerticalLine > (repaintWidth))
			{
				break;
			}
			
			// Calculate the position of the next vertical line and read the time at that point.
			nextVerticalLine = signalViewerModel.getPixelForTimeInMilliSeconds(time - offset);
			String timeStr = TimeUtil.getTimeString(time, Constants.NoMilliSecs);

			strSize = g.stringExtent(timeStr);
			int x = nextVerticalLine - (strSize.x / 2);

			if (lastLabelX + 100 < x)
			{
				g.setForeground(Colors.Blue);
				g.drawString(timeStr, x, 1, true);
				g.drawLine(nextVerticalLine, 2 + strSize.y, nextVerticalLine, 4 + strSize.y);
				lastLabelX = x;
			}
			else
			{
				g.drawLine(nextVerticalLine, strSize.y, nextVerticalLine, 6 + strSize.y);
			}

			// Forward one second
			time = time + 1000;
		}
	}

	private void resized()
	{
		initSignalViewerCompositeProperties(false, false);
		this.signalInfoComposite.update();

		signalViewerCanvas.setResized(true);
		signalViewerCanvas.redraw();
		horizontalCanvas.redraw();
		verticalCanvas.redraw();

	}

	private void initSignalViewerCompositeProperties(boolean resetScrollbarSelection, boolean resetSelection)
	{
		int width = 0;
		int height = 0;
		width = (int) ((signalViewerModel.signalModel.getMaxSamp() * signalViewerModel.getPixelPerSample(Common
				.getInstance().signalModel.getSamplingFrequency()))
				+ signalViewerModel.getReferencePulseWidth() + 200);
		height = this.scrolledComposite.getClientArea().height;

		Rectangle size = this.scrolledComposite.getClientArea();
		size.width += verticalScrollbar.getSize().x;
		size.height += horizontalScrollbar.getSize().y;
		width += verticalScrollbar.getSize().x + 2;
		height += horizontalScrollbar.getSize().y;

		double horScrollbarPercent = ((double) horizontalScrollbar.getSelection())
				/ ((double) horizontalScrollbar.getMaximum());


		int newHorSelection = (int) (width * horScrollbarPercent);

		if (resetScrollbarSelection)
		{
			newHorSelection = (int) Math.round((signalViewerModel.getPixelPerSecond() * signalViewerModel
					.getStartVisibleTimeInMs()) / 1000);
		}

		horizontalScrollbar.setMaximum(width);
		horizontalScrollbar.setThumb(Math.min(size.width, width));

		horizontalScrollbar.setPageIncrement((int) Math.round(Math.min(size.width, width)
				* Constants.pageIncrementFactor));
		int horInc = Math.max(1, size.width / 10);
		horizontalScrollbar.setIncrement(horInc);
		horizontalScrollbar.setSelection(newHorSelection);

		// Höhe berechnen: Darzustellender Bereich: -5 mV bis +5 mV
		double h = signalViewerModel.getFullHeight() - this.signalViewerCanvas.getBounds().height;
		verticalScrollbar.setEnabled(true);
		if (h <= 0)
		{
			verticalScrollbar.setEnabled(false);
			h = 1;
		}
		verticalScrollbar.setMinimum(0);
		verticalScrollbar.setMaximum((int) h);
		signalViewerModel.setChannelZeroLine();

		if (resetScrollbarSelection)
		{
			verticalScrollbar.setSelection((int) h / 2);

		}
		signalViewerModel.setSignalGraphPositionY(verticalScrollbar.getSelection());

		signalViewerCanvas.setSignalGraphWidth(width);
		signalViewerCanvas.setSignalGraphHeight(height);

		horScrollBarSelected(false);

		// Selection beim neuen Laden einer Datei löschen.
		if (resetSelection)
		{
			signalViewerModel.selection.unSelect();
		}

	}

	private void vertScrollBarSelected(boolean redraw)
	{
		int posY = verticalScrollbar.getSelection();
		signalViewerModel.setSignalGraphPositionY(posY);

		if (redraw)
		{
			signalViewerModel.setRedraw(true);
			signalViewerModel.OnVertScrollBarSelected();
			verticalCanvas.redraw();
		}
	}

	private void horScrollBarSelected(boolean redraw)
	{
		int posX = horizontalScrollbar.getSelection();
		signalViewerModel.setSignalGraphPositionX(posX);

		if (redraw)
		{
			signalViewerModel.setRedraw(true);
			signalViewerCanvas.redraw();
			horizontalCanvas.redraw();
		}

	}

	public void gotoPixel(int pixel)
	{
		horizontalScrollbar.setSelection(pixel);
		horScrollBarSelected(true);
	}

	private Point getStringSize(Display display)
	{
		Image image = null;
		GC gc = null;
		try
		{
			image = new Image(display, new Rectangle(0, 0, 200, 100));
			gc = new GC(image);

			return gc.stringExtent("-9.5");

		}
		catch (Exception e)
		{
			if (gc != null)
			{
				try
				{
					gc.dispose();
				}
				catch (Exception e2)
				{
				}
			}
			if (image != null)
			{
				try
				{
					image.dispose();
				}
				catch (Exception e2)
				{
				}
			}

		}

		return new Point(50, 13);
	}

	public void signalChanged(SignalEvent e)
	{
		this.initSignalViewerCompositeProperties(true, true);
		redrawComponents();
	}

	public void viewSectionChanged(ViewEvent e)
	{
		// redrawComponents();
	}

	public void zoomChanged(ViewEvent e)
	{
		this.initSignalViewerCompositeProperties(true, false);
		redrawComponents();
	}

	public void channelViewChanged(ViewEvent e)
	{
	}

	public void settingsChanged(SettingsEvent e)
	{
		redrawComponents();
	}

	public void redrawComponents()
	{
		this.signalInfoComposite.update();

		signalViewerModel.setRedraw(true);
		this.signalViewerCanvas.redraw();
		this.signalViewerCanvas.update();

		horizontalCanvas.redraw();
		verticalCanvas.redraw();

	}

	public SignalInfoComposite getSignalInfo()
	{
		return signalInfoComposite;
	}

	public SignalViewerCanvas getSignalViewerCanvas()
	{
		return signalViewerCanvas;
	}
}
