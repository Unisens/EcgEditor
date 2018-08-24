package de.fzi.ekgeditor.Gui.Widgets.SignalViewer.controller;



import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Menu.ContextMenuAddTrigger;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.TriggerModel;

public class MouseController implements MouseMoveListener, MouseListener{
	private static MouseController mouseController = null;
	private SignalViewerModel dataModel = null;
	
	public static MouseController newInstance(SignalViewerModel dataModel){
		if(mouseController == null)
			return new MouseController(dataModel);
		else
			return mouseController;
	}
	
	private MouseController (SignalViewerModel dataModel){
		this.dataModel = dataModel;
	}
	
	public void mouseMove(MouseEvent e){
		dataModel.mouseMoved(e.x, e.y);
	}
	
	public void mouseDoubleClick(MouseEvent e) {
	       
    }
    public void mouseDown(MouseEvent e) {
    	TriggerModel triggerModel = Common.getInstance().triggerModel;
    	if(e.button == 1){
    		if(triggerModel.isAddTriggerMode()){
				dataModel.setMouseLeftDownXPosition(e.x);
				new ContextMenuAddTrigger();
				return;
			}
			if(triggerModel.getActiveEventEntry() != null && triggerModel.isDeleteTriggerMode()){
				Common.getInstance().mc.menu_delete_trigger_after_confirm(dataModel.getTimeinMillisecsForPixel(e.x));
				return;
			}
			
			
			ArtefactModel artefactModel = Common.getInstance().artefactModel;
			if(artefactModel.getActiveEventEntry() != null){
				Artefact selectedArtefact = artefactModel.getSelectedArtefact();
				if(selectedArtefact != null){
					if(artefactModel.isAtStartOrEndOfSelectedArtefact(e.x)){
						artefactModel.selectStartOrEndOfSelectedArtefact(e.x);
						this.dataModel.OnLeftMouseDown(e.x);
						return;
					}else{
						selectedArtefact.setStartSelected(false);
						selectedArtefact.setEndSelected(false);
					}
				}
				
				Artefact artefact = artefactModel.getArtefactForPoint(e.x, e.y);
				if(artefact != null){
					if(artefact.isSelected()){
						if(artefact.getChannel() != -1){
							artefact.setSelected(false);
							artefactModel.setSelectedArtefact(null);
						}
					}else{
						if(artefactModel.getSelectedArtefact() != null){
							artefactModel.getSelectedArtefact().setSelected(false);
						}
						artefact.setSelected(true);
						artefactModel.setSelectedArtefact(artefact);
					}
					Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.getSelection().unSelect();
					Common.getInstance().mainForm.signalViewerComposite.signalViewerCanvas.unselectTempSelection();
				}else{
					this.dataModel.OnLeftMouseDown(e.x);
				}
			}else{
				this.dataModel.OnLeftMouseDown(e.x);
			}
			
			Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.repaintSignalCanvas();
		}
		if(e.button == 3){
			Shell shell = Display.getCurrent().getShells()[0];
			Point point = shell.computeSize(e.x, e.y);
			point = new Point(point.x + shell.getLocation().x , point.y + shell.getLocation().y);
			this.dataModel.OnRightMouseDown(point, e.x, e.y);
		}
    }
    
    public void mouseUp(MouseEvent e) {
    	if(e.button == 1){
    		this.dataModel.OnLeftMouseUp(e.x);
    		if(Common.getInstance().artefactModel.isModifyingSelectedArtefact()){
    			Common.getInstance().artefactModel.setModifyingSelectedArtefact(false);
    		}
    	}
    }
}
