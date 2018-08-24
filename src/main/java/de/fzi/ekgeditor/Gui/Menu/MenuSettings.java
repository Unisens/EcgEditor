/** This class creates and manages all settings menu entries
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

public class MenuSettings {
	/** link to common menu functionality */
	private MenuCommon mc = Common.getInstance().mc;
	
	/** Standard constructor that creates standard main-menu items
	 * 
	 * @param mainMenu parent menu
	 */
	public MenuSettings(Menu mainMenu)
	{	
		CreateViewMenu(mainMenu);
	}
	
	/** Creates all the settings menu items
	 * 
	 * @param mainMenu parent menu
	 */
	private void CreateViewMenu(Menu mainMenu)
	{
		Shell shell = mainMenu.getShell();
		ImageManager im = Common.getInstance().im;
		
		MenuItem fileItem = new MenuItem(mainMenu,SWT.CASCADE);
		fileItem.setText("Einstellungen");
		
		Menu fileSubmenu = new Menu(shell,SWT.DROP_DOWN);
		fileItem.setMenu(fileSubmenu);
		
		MenuItem m_FileOpen = new MenuItem(fileSubmenu,SWT.PUSH);
		m_FileOpen.setText("Einstellungen ...");
		m_FileOpen.setImage(im.get(ImageManager.ico_settings));
		m_FileOpen.addSelectionListener(new SelectionAdapter() {public void widgetSelected(SelectionEvent e) {Button_Settings_Clicked();}});
	}
	
	/** This method is called if settings was clicked */
	private void Button_Settings_Clicked()
	{
		mc.menu_settings();
	}
}
