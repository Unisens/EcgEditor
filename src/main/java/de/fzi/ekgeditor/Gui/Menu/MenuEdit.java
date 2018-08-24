/** This class creates and manages all edit menu entries
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
import de.fzi.ekgeditor.Gui.Forms.Dialog_AddTriggerList;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;

public class MenuEdit implements TriggerModelListner{
	
	/** parent shell */
	private Shell 	shell 	= 	null;
	/** parent menu */
	public Menu	menu	=	null;
	/** link to common menu functionality */
	private MenuCommon mc = Common.getInstance().mc;
	
	private MenuItem m_EditDeleteTriggers;
	private MenuItem m_EditAddTrigger;
	private MenuItem m_EditSaveTriggersAs;
	private MenuItem m_EditSaveArtefactsAs;
	
	/** Standard constructor that creates standard main-menu items
	 * 
	 * @param mainMenu parent menu
	 */
	public MenuEdit(Menu mainMenu)
	{
		this.shell  = mainMenu.getShell();
		menu=mainMenu;
		MenuItem editItem = new MenuItem(mainMenu,SWT.CASCADE);
		editItem.setText("Bearbeiten");
		
		Common.getInstance().triggerModel.addTriggerModelListener(this);
		Menu editSubMenu = new Menu(shell,SWT.DROP_DOWN);
		editItem.setMenu(editSubMenu);
		ImageManager im = Common.getInstance().im;
		
		m_EditSaveTriggersAs = new MenuItem(editSubMenu,SWT.PUSH);
		m_EditSaveTriggersAs.setText("Triggerliste speichern unter...");
		m_EditSaveTriggersAs.setImage(im.get(ImageManager.ico_saveFile));
		m_EditSaveTriggersAs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				Button_editSaveTriggersAs_Clicked();
			}
		});
		m_EditSaveTriggersAs.setEnabled(false);
		
		m_EditSaveArtefactsAs = new MenuItem(editSubMenu,SWT.PUSH);
		m_EditSaveArtefactsAs.setText("Artefaktliste speichern unter...");
		m_EditSaveArtefactsAs.setImage(im.get(ImageManager.ico_saveFile));
		m_EditSaveArtefactsAs.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					Button_editSaveArtefactsAs_Clicked();
				}
		});
		m_EditSaveTriggersAs.setEnabled(false);
		
		MenuItem m_EditAddTriggerList = new MenuItem(editSubMenu,SWT.PUSH);
		m_EditAddTriggerList.setText("Triggerliste hinzufügen...");
		m_EditAddTriggerList.setImage(im.get(ImageManager.ico_paste));
		m_EditAddTriggerList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Button_editAddTriggerList_Clicked();
			}
		});
	}
	
	public void Button_editSaveTriggersAs_Clicked(){
		mc.menu_save_triggers_as();
	}
	
	public void Button_editSaveArtefactsAs_Clicked(){
		mc.menu_save_artefacts_as();
	}

	public void Button_editAddTriggerList_Clicked(){
		Dialog_AddTriggerList dialog = new Dialog_AddTriggerList(shell);
		dialog.open();
	}
	
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent) {
		TriggerModel triggerModel = (TriggerModel)triggerModelEvent.getSource();
		boolean isTriggerlistLoaded = triggerModel.getActiveEventEntry() != null;
		if (m_EditAddTrigger != null)	
			this.m_EditAddTrigger.setEnabled(isTriggerlistLoaded);
		if (m_EditDeleteTriggers != null)
			this.m_EditDeleteTriggers.setEnabled(isTriggerlistLoaded);
		if(m_EditSaveTriggersAs != null)
			this.m_EditSaveTriggersAs.setEnabled(isTriggerlistLoaded);
	}
	
}
