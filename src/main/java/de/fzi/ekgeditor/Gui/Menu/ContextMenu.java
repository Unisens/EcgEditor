package de.fzi.ekgeditor.Gui.Menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;
import de.fzi.ekgeditor.utils.Selection;

public class ContextMenu implements TriggerModelListner, MouseListener{
	private Menu contextMenu;
	private MenuItem m_EditDeleteTriggers;
	private MenuItem m_EditAddTrigger;
	private MenuItem m_EditAddArtefact;
	private MenuItem m_EditDeleteArtefact;
	
	public ContextMenu(Shell shell) {
		Common.getInstance().triggerModel.addTriggerModelListener(this);
		contextMenu = new Menu(shell, SWT.POP_UP);
		ImageManager im = Common.getInstance().im;
		if (Constants.cutPaste){
			MenuItem m_EditCut = new MenuItem(contextMenu,SWT.PUSH);
			m_EditCut.setText("Ausschneiden");
			m_EditCut.setImage(im.get(ImageManager.ico_cut));
			m_EditCut.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					Common.getInstance().NotYetImplemented();
				}
			});

			MenuItem m_EditPaste = new MenuItem(contextMenu,SWT.PUSH);
			m_EditPaste.setText("Einfügen");
			m_EditPaste.setImage(im.get(ImageManager.ico_paste));
			m_EditPaste.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e){
					Common.getInstance().NotYetImplemented();
				}
			});
		}
		
		MenuItem m_EditDelete = new MenuItem(contextMenu,SWT.PUSH);
		m_EditDelete.setText("Löschen...");
		m_EditDelete.setImage(im.get(ImageManager.ico_remove));
		m_EditDelete.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				Common.getInstance().mc.menu_remove();
			}
		});
		
		m_EditDeleteTriggers = new MenuItem(contextMenu,SWT.PUSH);
		m_EditDeleteTriggers.setText("Trigger löschen...");
		m_EditDeleteTriggers.setImage(im.get(ImageManager.ico_remove));
		m_EditDeleteTriggers.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Selection selection = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getSelection();
				Common.getInstance().mc.menu_delete_triggers(selection.getSelectionStart(), selection.getSelectionEnd());
			}
		});
		m_EditDeleteTriggers.setEnabled(false);
		
		m_EditAddTrigger = new MenuItem(contextMenu,SWT.PUSH);
		m_EditAddTrigger.setText("Trigger hinzufügen...");
		m_EditAddTrigger.setImage(im.get(ImageManager.ico_remove));
		m_EditAddTrigger.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				Common.getInstance().mc.menu_add_trigger();
			}
		});
		m_EditAddTrigger.setEnabled(true);
		
		m_EditAddArtefact = new MenuItem(contextMenu,SWT.PUSH);
		m_EditAddArtefact.setText("Artefakt hinzufügen...");
		m_EditAddArtefact.setImage(im.get(ImageManager.ico_remove));
		m_EditAddArtefact.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Common.getInstance().mc.menu_add_artefact();
			}
		});
		m_EditAddArtefact.setEnabled(true);
		
		m_EditDeleteArtefact = new MenuItem(contextMenu,SWT.PUSH);
		m_EditDeleteArtefact.setText("Artefakt löschen...");
		m_EditDeleteArtefact.setImage(im.get(ImageManager.ico_remove));
		m_EditDeleteArtefact.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				Common.getInstance().mc.menu_delete_artefact();
			}
		});
		m_EditDeleteArtefact.setEnabled(false);
	}
	
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent) {
		TriggerModel triggerModel = (TriggerModel)triggerModelEvent.getSource();
		boolean isTriggerlistLoaded = triggerModel.getActiveEventEntry() != null;
		if (m_EditDeleteTriggers != null)
			this.m_EditDeleteTriggers.setEnabled(isTriggerlistLoaded);
	}
	
	
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if(Common.getInstance().artefactModel.getActiveEventEntry() != null && Common.getInstance().artefactModel.getArtefactForPoint(e.x, e.y) != null)
			m_EditDeleteArtefact.setEnabled(true);
		else
			m_EditDeleteArtefact.setEnabled(false);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		
	}

	public Menu getContextMenu(){
		return contextMenu;
	}
}
