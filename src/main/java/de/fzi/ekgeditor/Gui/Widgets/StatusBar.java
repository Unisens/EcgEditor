/**
 * This class defines the status-Bar for the main-Window
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Layouts;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.events.CacheEvent;
import de.fzi.ekgeditor.events.CacheListener;

public class StatusBar implements CacheListener
{
	/** Some Label representing the statusBar */
	private Label statusLine;

	private SignalModel model;

//	private Shell shell;

	private Display display;

	/**
	 * Returns the text written in the statusbar
	 * 
	 * @return text written in the statusbar
	 */
	public String getText()
	{
		return statusLine.getText();
	}

	/**
	 * Sets the text written in the statusbar
	 * 
	 * @param message
	 *            Text to write to the statusbar
	 */
	public void setText(String message)
	{
		if (statusLine != null)
		{
			if (!statusLine.isDisposed())
			{
				this.statusLine.setText(message);
			}
		}
	}

	/**
	 * Standard constructor
	 * 
	 * @param shell
	 *            parent
	 */
	public StatusBar(Shell shell, SignalModel model)
	{
//		this.shell = shell;
		this.display = shell.getDisplay();
		this.model = model;
		model.addCacheListener(this);
		statusLine = new Label(shell, SWT.NONE);
		statusLine.setLayoutData(Layouts.GetLayoutFillOneRow(GridData.FILL_HORIZONTAL));
		statusLine.setText("");
	}

	public void PageLoad(CacheEvent e)
	{
		final String filltext = Common.Double2String(Math.round(model.getCacheFill() * 10.0) / 10.0);

		if (Constants.isDebug)
		{
			System.out.println("StatusBar, CacheEvent " + e.pageNumber);
			System.out.println("FillStatus:" + filltext);
		}

		if (!display.isDisposed())
		{
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					StatusBar.this.setText("Cache-Füllung: " + filltext + " %");
				}
			});
		}
	}

	public void CacheFlush(CacheEvent e)
	{
		if (!display.isDisposed())
		{
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					StatusBar.this.setText("Cache-Füllung: 0 %");
				}
			});
		}
	}
}
