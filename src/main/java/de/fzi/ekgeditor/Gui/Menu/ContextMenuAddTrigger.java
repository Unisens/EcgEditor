package de.fzi.ekgeditor.Gui.Menu;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.TriggerType;

public class ContextMenuAddTrigger {
	private MenuCommon mc = Common.getInstance().mc;
	private MenuItem[] m_triggerClasses;

	public ContextMenuAddTrigger(){
		Menu contextMenuAddTrigger = new Menu(Display.getCurrent().getActiveShell(), SWT.POP_UP);
		List<TriggerType> triggerTypes = Common.getInstance().triggerClasses.getTriggerTypes();
		m_triggerClasses = new MenuItem[triggerTypes.size()];
		
		MenuItem title = new MenuItem(contextMenuAddTrigger, SWT.PUSH);
		title.setText("Trigger einfügen: ");
		title.setEnabled(true);
		title.setImage(Common.getInstance().im.get(ImageManager.ico_ecg_trigger_add_mode));
		
		for(int i = 0; i < m_triggerClasses.length; i++){
			TriggerType triggerType = triggerTypes.get(i);
			m_triggerClasses[i] = new MenuItem(contextMenuAddTrigger,SWT.PUSH);
			m_triggerClasses[i].setText(triggerType.getNotation()+ " " + triggerType.getComment());
			m_triggerClasses[i].setData(triggerType);
			m_triggerClasses[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					mc.menu_add_trigger_mode_add_trigger((TriggerType)((MenuItem)e.getSource()).getData());
				}
			});
			m_triggerClasses[i].setEnabled(true);
		}
		contextMenuAddTrigger.setVisible(true);
	}
}
