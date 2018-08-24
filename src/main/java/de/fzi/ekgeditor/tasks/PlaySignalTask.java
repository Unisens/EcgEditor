package de.fzi.ekgeditor.tasks;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import de.fzi.ekgeditor.Gui.Widgets.Widget_overview;

public class PlaySignalTask extends TimerTask {
	private Widget_overview widgetOverview;
	
	public PlaySignalTask(Widget_overview widgetOverview){
		super();
		this.widgetOverview = widgetOverview;
		
	}
	@Override
	public void run() {
		Display.getDefault().asyncExec(new Runnable(){
							public void run(){
								widgetOverview.goRight();
							}
						});
	}

}
