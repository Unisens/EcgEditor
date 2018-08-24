package de.fzi.ekgeditor.tests;

import org.eclipse.swt.widgets.Display;

import de.fzi.ekgeditor.Gui.Forms.Dialog_Wait;

public class ThreadTest1 extends Thread 
{
	private Dialog_Wait w=null;
	private Display d=null;
	
	private static int i;
	private int loops;
	
	
	public void init(Dialog_Wait w,Display d)
	{
		this.w=w;
		this.d=d;
		i=0;
		loops=0;
	}
	
	private void setProgress()
	{
		if (!d.isDisposed())
		{
			d.syncExec(
					new Runnable()
					{
						public void run()
						{
								w.setProgress(i);
						}
					});
		}
		else
		{
			this.interrupt();
		}
	}
	private void setOK()
	{
		if (!d.isDisposed())
		{
			d.syncExec(
					new Runnable()
					{
						public void run()
						{
								w.setOkButton(true);
						}
					});
		}
		else
		{
			this.interrupt();
		}
	}
	
	public void run()
	{
		while(!isInterrupted() & (i<100))
		{
			i++;
			loops++;
			System.out.println(this.getName()+":"+loops+" "+i);
			
			setProgress();
			
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ex)
			{
				this.interrupt();
			}

		}
		System.out.println("Thread "+this.getName()+" beendet.("+loops+")");
		setOK();
	}
}
