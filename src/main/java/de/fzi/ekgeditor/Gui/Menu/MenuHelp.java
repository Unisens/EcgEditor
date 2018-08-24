/** This class creates and manages all help menu entries
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Forms.Dialog_Memory;
import de.fzi.ekgeditor.Gui.Forms.Dialog_licence;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.utils.TimeUtil;

public class MenuHelp {
	/** link to common menu functionality */
	private MenuCommon mc	= 	Common.getInstance().mc;
	
	/** Standard constructor that creates standard main-menu items
	 * 
	 * @param mainMenu parent menu
	 */
	public MenuHelp(Menu mainMenu)
	{
		CreateHelpMenu(mainMenu);
	}
	
	/** Creates all the help menu items
	 * 
	 * @param mainMenu parent menu
	 */
	private void CreateHelpMenu(Menu mainMenu)
	{
		Shell shell = mainMenu.getShell();
		ImageManager im = Common.getInstance().im;
		
		MenuItem helpItem = new MenuItem(mainMenu,SWT.CASCADE);
		helpItem.setText("&Hilfe");
				
		Menu helpSubMenu = new Menu(shell,SWT.DROP_DOWN);
		helpItem.setMenu(helpSubMenu);
		
		if (Constants.isDebug)
		{
			MenuItem m_HelpDebug = new MenuItem(helpSubMenu,SWT.PUSH);
			m_HelpDebug.setText("Debug");
			m_HelpDebug.setImage(im.get(ImageManager.ico_debug));
			m_HelpDebug.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_Debug_Clicked();}});
		}
		
		MenuItem m_HelpInfo = new MenuItem(helpSubMenu,SWT.PUSH);
		m_HelpInfo.setText("Über...");
		m_HelpInfo.setImage(im.get(ImageManager.ico_about));
		m_HelpInfo.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_info_Clicked();}});
		
		MenuItem m_HelpUnisens = new MenuItem(helpSubMenu,SWT.PUSH);
		m_HelpUnisens.setText("Unisens...");
		m_HelpUnisens.setImage(im.get(ImageManager.ico_unisens));
		m_HelpUnisens.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_unisens_Clicked();}});
		
		MenuItem m_Memory = new MenuItem(helpSubMenu,SWT.PUSH);
		m_Memory.setText("Speicher...");
		m_Memory.setImage(im.get(ImageManager.ico_memory));
		m_Memory.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_memory_Clicked();}});
		
		/*
		MenuItem m_licence = new MenuItem(helpSubMenu,SWT.PUSH);
		m_licence.setText("Lizenz...");
		m_licence.setImage(im.get(ImageManager.ico_license));
		m_licence.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_licence_Clicked();}});
		*/
	}
	
	/** This method is called if info was clicked */
	public void Button_info_Clicked()
	{
		mc.menu_Info();
	}
	
	/** This method is called if info was clicked */
	public void Button_unisens_Clicked()
	{
		mc.menu_Unisens();
	}
	
	/** This method is called if license was clicked */
	public void Button_licence_Clicked()
	{
		Dialog_licence dL=new Dialog_licence(new Shell(Display.getCurrent(), SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL|SWT.CENTER));
		dL.open();
	}
	
	/** This method is called if licence was clicked */
	public void Button_memory_Clicked()
	{
		Dialog_Memory dL=new Dialog_Memory(Common.getInstance().mainForm.mainWindow.getShell());
		dL.open();
	}
	
	
	/** This method is called if debug was clicked */
	public void Button_Debug_Clicked()
	{
		/*
		Common.getInstance().signalModel.removedSelection.printOut();
		System.out.println("Konsolidiert:");
		Common.getInstance().signalModel.removedSelection.getConsolidatedList().printOut();
		
		System.out.println("Selektion:"+Common.getInstance().mainForm.signalViewerComposite.dataModel.selection);
		*/
		int rP=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getReferencePulseWidth();
		long tVor=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getTimeinMillisecsForPixel(rP);
		Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.goToSample(5000);
		//Common.getInstance().mainForm.signalViewerComposite.gotoPixel(100000);
		long tN=Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getTimeinMillisecsForPixel(rP);
		System.out.println("Zeit Vorher:"+TimeUtil.getFullTimeString(tVor)+" Nachher:"+TimeUtil.getFullTimeString(tN));
		
		throw new RuntimeException("Thread Dämon Test");
	}
}
