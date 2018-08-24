package de.fzi.ekgeditor.Gui.Widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.EKGEditor;
import de.fzi.ekgeditor.Gui.Menu.MenuCommon;
import de.fzi.ekgeditor.data.ImageManager;

/**
 * This class manages the main toolbar of the main-window.
 *
 * @author glose
 * @version 0.2
 */
public class EKGToolbar{
	/** instance to MenuCommon-Manager to call common menu functions. */
	private MenuCommon mc = Common.getInstance().mc;
	private ToolBar toolBar;
	private ToolItem addTriggerItem;
	private ToolItem deleteTriggerItem;
	
	/** This Constructer inserts the toolbar into the main window (shell)
	 * 
	 * @param shell main window
	 */
	public EKGToolbar(Shell shell)
	{
		CreateToolBar(shell);
	}
	
	/** This method does all the magic creation and insertion of the toolbar.
	 * 
	 * @param shell main window
	 */
	public void CreateToolBar(Shell shell)
	{	
		ImageManager im = Common.getInstance().im;
		
		this.toolBar = new ToolBar (shell, SWT.FLAT);
		
		ToolItem toolbar_open = new ToolItem(toolBar,SWT.PUSH);
		toolbar_open.setImage (im.get(ImageManager.ico_openFile));
		toolbar_open.setToolTipText("Öffnet eine neue Datendatei");
		toolbar_open.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_fileOpen_Clicked();}});
		
		ToolItem toolbar_save = new ToolItem(toolBar,SWT.PUSH);
		toolbar_save.setImage (im.get(ImageManager.ico_saveFileAs));
		toolbar_save.setToolTipText("Speichert alle Änderungen in eine neue Datei");
		toolbar_save.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_fileSaveAs_Clicked();}});
		
		/*
		ToolItem toolbar_remove = new ToolItem(toolBar,SWT.PUSH);
		toolbar_remove.setImage (im.get(ImageManager.ico_remove));
		toolbar_remove.setToolTipText("Aktuelle Auswahl löschen.");
		toolbar_remove.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_remove_Clicked();}});
		*/
		
		ToolItem toolbar_settings = new ToolItem(toolBar,SWT.PUSH);
		toolbar_settings.setImage (im.get(ImageManager.ico_settings));
		toolbar_settings.setToolTipText("Einstellungen verändern");
		toolbar_settings.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_settings_Clicked();}});
		
		ToolItem toolbar_analysis = new ToolItem(toolBar,SWT.PUSH);
		toolbar_analysis.setImage (im.get(ImageManager.ico_analysis));
		toolbar_analysis.setToolTipText("Analyse des Signals durchführen");
		toolbar_analysis.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_analysis_Clicked();}});
		
		this.addTriggerItem = new ToolItem(toolBar,SWT.CHECK);
		this.addTriggerItem.setImage (im.get(ImageManager.ico_ecg_trigger_add_mode));
		this.addTriggerItem.setToolTipText("Trigger hinzufügen Modus");
		
		this.deleteTriggerItem = new ToolItem(toolBar,SWT.CHECK);
		this.deleteTriggerItem.setImage (im.get(ImageManager.ico_ecg_trigger_delete_mode));
		this.deleteTriggerItem.setToolTipText("Trigger löschen Modus");
		
		EKGToolbar.this.addTriggerItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EKGToolbar.this.deleteTriggerItem.setSelection(false);
				Common.getInstance().triggerModel.setDeleteTriggerMode(false);
				Button_trigger_add_mode_Clicked(e);
			}
		});
		
		this.deleteTriggerItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EKGToolbar.this.addTriggerItem.setSelection(false);
				Common.getInstance().triggerModel.setAddTriggerMode(false);
				Button_trigger_delete_mode_Clicked(e);
			}
		});
		
		ToolItem toolbar_help = new ToolItem(toolBar,SWT.PUSH);
		toolbar_help.setImage (im.get(ImageManager.ico_about));
		toolbar_help.setToolTipText("Über...");
		toolbar_help.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_info_Clicked();}});
		
		ToolItem toolbar_quit = new ToolItem(toolBar,SWT.PUSH);
		toolbar_quit.setImage (im.get(ImageManager.ico_quit));
		toolbar_quit.setToolTipText("Programm beenden");
		toolbar_quit.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_quit_Clicked();}});
		
		toolBar.pack ();
		//toolBar.setSize(10,10);
		
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 4;
		g.horizontalAlignment=SWT.BEGINNING;
		g.verticalAlignment=SWT.BEGINNING;
		toolBar.setLayoutData(g);
	}
	
	/** Handler for menuitem File Open
	 * 
	 */
	public void Button_fileOpen_Clicked()
	{
		mc.menu_FileOpen();
	}
	
	/** Handler for menuitem File Save
	 * 
	 */
	public void Button_fileSaveAs_Clicked()
	{
		mc.menu_FileSaveAs();
	}
	
	/** Handler for menuitem Selection Remove
	 * 
	 */
	/*
	public void Button_remove_Clicked()
	{
		mc.menu_FileSave();
	}
	*/
	
	/** Handler for menuitem Settings
	 * 
	 */
	public void Button_settings_Clicked()
	{
		mc.menu_settings();
	}
	
	/** Handler for menuitem Analysis
	 * 
	 */
	public void Button_analysis_Clicked()
	{
		mc.menu_analysis();
	}
	
	/** Handler for menuitem About
	 * 
	 */
	public void Button_info_Clicked()
	{
		mc.menu_Info();
	}
	
	/** Handler for menuitem Quit
	 * 
	 */
	public void Button_quit_Clicked()
	{
		mc.menu_quit();
	}
	
	public void Button_trigger_add_mode_Clicked(SelectionEvent e){
		boolean isChecked = ((ToolItem)e.getSource()).getSelection();
		mc.menu_set_add_trigger_mode(isChecked);
	}
	
	public void Button_trigger_delete_mode_Clicked(SelectionEvent e){
		boolean isChecked = ((ToolItem)e.getSource()).getSelection();
		mc.menu_set_delete_trigger_mode(isChecked);
	}
	
	public void selectAddTriggerOption()
	{
		this.addTriggerItem.setSelection(true);
		this.addTriggerItem.notifyListeners(SWT.Selection, new Event());
		this.toolBar.redraw();
	}
	
	public void selectDeleteTriggerOption()
	{
		this.deleteTriggerItem.setSelection(true);
		this.deleteTriggerItem.notifyListeners(SWT.Selection, new Event());
		this.toolBar.redraw();
	}
}
