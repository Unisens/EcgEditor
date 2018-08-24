package de.fzi.ekgeditor.Gui.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class AnalysisProgressBar {
	private Shell shell;
	private ProgressBar progressBar;
	
	public AnalysisProgressBar(){
	
		this.shell = new Shell(Display.getCurrent());
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
 		rowLayout.marginLeft = 10;
 		rowLayout.marginTop = 10;
 		rowLayout.marginRight = 10;
 		rowLayout.marginBottom = 10;
 		rowLayout.spacing = 5;
 		shell.setLayout(rowLayout);
 		progressBar  = new ProgressBar(shell,SWT.HORIZONTAL);
		progressBar.setMinimum(0);
		progressBar.setMaximum(200);
		progressBar.setLayoutData(new RowData(400, 20));
		shell.layout();
		shell.pack();
		shell.open();
	}
	
	public int getMaximum(){
		return progressBar.getMaximum();
	}
	
	public void setSelection(int selection){
		progressBar.setSelection(selection);
	}
	
	public void setText(String text){
		shell.setText(text);
	}
	
	public void dispose(){
		shell.dispose();
	}
	
	public boolean isDisposed(){
		return shell.isDisposed();	
	}
}
