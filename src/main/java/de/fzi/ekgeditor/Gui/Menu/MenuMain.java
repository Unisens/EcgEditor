/** This class creates and manages the whole main menu
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.Gui.Menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;

public class MenuMain {
	/** reference to ourself */
	private static MenuMain	mainMenu = null;
	private Menu menu;
	private MenuAnalysis analysisMenu;
	
	/** return the main menu */
	public static MenuMain getMainMenu() {
		return mainMenu;
	}
	
	public MenuAnalysis getMenuAnalysis(){
		return analysisMenu;
	}
	
	/** Standard constructor */
	public MenuMain()
	{
		CreateMainMenu();
		mainMenu = this;
	}
	
	/** Create all the different submenu items */
	private void CreateMainMenu()
	{
		Shell shell = Common.getInstance().mainForm.mainWindow;
		
		menu = new Menu(shell,SWT.BAR);
		shell.setMenuBar(menu);
				
		new MenuFile(menu);
		new MenuEdit(menu);
		new MenuView(menu);
		new MenuSettings(menu);
		analysisMenu = new MenuAnalysis(menu);
		new MenuHelp(menu);
	}
}
