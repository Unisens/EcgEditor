/** This class creates and manages the analysis menu in the main-menu
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

public class MenuAnalysis {
	
	/** link to our common-menu entries class */ 
	private MenuCommon mc = Common.getInstance().mc;
	private MenuItem analysisItem;
	/** Standard constructor
	 * 
	 * @param mainMenu parent (normally main-) menu
	 */
	public MenuAnalysis(Menu mainMenu)
	{
		CreateAnalysisMenu(mainMenu);
	}
	
	/** This method creates the whole Analysis menu 
	 * 
	 * @param mainMenu parent menu
	 */
	private void CreateAnalysisMenu(Menu mainMenu)
	{
		Shell shell = mainMenu.getShell();
		ImageManager im = Common.getInstance().im;
		
		analysisItem = new MenuItem(mainMenu,SWT.CASCADE);
		analysisItem.setText("Analyse");
		analysisItem.setEnabled(false);
		
		Menu analysisSubmenu = new Menu(shell,SWT.DROP_DOWN);
		analysisItem.setMenu(analysisSubmenu);
		
		MenuItem m_Analysis = new MenuItem(analysisSubmenu,SWT.PUSH);
		m_Analysis.setText("Analyse ...");
		m_Analysis.setImage(im.get(ImageManager.ico_analysis));
		m_Analysis.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button_Anaysis_Clicked();
			}
		});
	}
	
	/** This method is called, if the menuitem analysis is clicked */
	private void Button_Anaysis_Clicked()
	{
		mc.menu_analysis();
	}
	
	public void setEnabled(boolean enabled){
		analysisItem.setEnabled(enabled);
	}
}

