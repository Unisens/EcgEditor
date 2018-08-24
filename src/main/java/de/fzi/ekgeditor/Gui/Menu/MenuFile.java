/** This class creates and manages all file menu entries
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.ImageManager;

public class MenuFile {
	/** link to common menu functionality */
	private MenuCommon mc = Common.getInstance().mc;
	
	/** Standard constructor that creates standard main-menu items
	 * 
	 * @param mainMenu parent menu
	 */
	public MenuFile(Menu mainMenu)
	{
		CreateFileMenu(mainMenu);
	}
	
	/** Creates all the file menu items
	 * 
	 * @param mainMenu parent menu
	 */
	private void CreateFileMenu(Menu mainMenu)
	{
		Shell shell = mainMenu.getShell();
		ImageManager im = Common.getInstance().im;
		
		MenuItem fileItem = new MenuItem(mainMenu,SWT.CASCADE);
		fileItem.setText("&Datei");
		
		Menu fileSubmenu = new Menu(shell,SWT.DROP_DOWN);
		fileItem.setMenu(fileSubmenu);
		
		MenuItem m_FileOpen = new MenuItem(fileSubmenu,SWT.PUSH);
		m_FileOpen.setText("Ö&ffnen ...");
		m_FileOpen.setImage(im.get(ImageManager.ico_openFile));
		m_FileOpen.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_fileOpen_Clicked();}});
		
		/*
		MenuItem m_FileSave = new MenuItem(fileSubmenu,SWT.PUSH);
		m_FileSave.setText("&Speichern");
		m_FileSave.setImage(im.get(ImageManager.ico_saveFile));
		m_FileSave.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_fileSave_Clicked();}});
		*/
		
		MenuItem m_FileSaveAs = new MenuItem(fileSubmenu,SWT.PUSH);
		m_FileSaveAs.setText("S&peichern unter ...");
		m_FileSaveAs.setImage(im.get(ImageManager.ico_saveFileAs));
		m_FileSaveAs.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_fileSaveAs_Clicked();}});
		
		MenuItem m_FileQuit = new MenuItem(fileSubmenu,SWT.PUSH);
		m_FileQuit.setText("&Beenden");
		m_FileQuit.setImage(im.get(ImageManager.ico_quit));
		m_FileQuit.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_quit_Clicked();}});
		
	}
	
	/** This method is called if quit was clicked */
	public void Button_quit_Clicked()
	{
		mc.menu_quit();
	}
	
	/** This method is called if File-Open was clicked */
	public void Button_fileOpen_Clicked()
	{
		mc.menu_FileOpen();
	}
	
	/** This method is called if File-Save was clicked */
	/*
	public void Button_fileSave_Clicked()
	{
		mc.menu_FileSave();
	}*/
	
	/** This method is called if File-SaveAs was clicked */
	public void Button_fileSaveAs_Clicked()
	{
		mc.menu_FileSaveAs();
	}
}
