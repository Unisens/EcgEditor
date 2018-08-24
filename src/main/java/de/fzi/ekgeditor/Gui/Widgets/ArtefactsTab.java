package de.fzi.ekgeditor.Gui.Widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.ChannelData;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.events.ArtefactModelListener;
import de.fzi.ekgeditor.utils.TimeUtil;

public class ArtefactsTab implements ArtefactModelListener{
	private Table artefactsTable;
	private TableItem[] artefactTableItems;
	private TableItem selectedTableItem = null;
	private ArtefactModel artefactModel;
	private SignalViewerModel signalViewerModel;
	private final TableEditor editor;
	
	public ArtefactsTab(TabFolder tabFolder){
		this.artefactModel = Common.getInstance().artefactModel;
		this.signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		Common.getInstance().artefactModel.addArtefactModelListener(this);
		TabItem artefactsTabItem = new TabItem(tabFolder, SWT.NONE);
		artefactsTabItem.setText("Artefakte");
		
		Group artefactsGroup = new Group(tabFolder, SWT.NONE);
		artefactsGroup.setLayout(new GridLayout(1, true));
		artefactsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		artefactsTabItem.setControl(artefactsGroup);
		
		artefactsTable = new Table(artefactsGroup, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		artefactsTable.setHeaderVisible(true);
		artefactsTable.setLinesVisible(true);
		
		editor = new TableEditor (artefactsTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		artefactsTable.addListener (SWT.MouseDown, new Listener () {
			public void handleEvent (Event event) {
				Rectangle clientArea = artefactsTable.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = artefactsTable.getTopIndex ();
				while (index < artefactsTable.getItemCount ()) {
					boolean visible = false;
					final TableItem item = artefactsTable.getItem (index);
					for (int i=0; i<artefactsTable.getColumnCount (); i++) {
						Rectangle rect = item.getBounds (i);
						if (rect.contains (pt)) {
							TableItem selectedArtefactTableItem = artefactTableItems[index];
							Artefact selectedArtefact = (Artefact)selectedArtefactTableItem.getData();
							if(artefactModel.getSelectedArtefact() != null)
								artefactModel.getSelectedArtefact().setSelected(false);
							selectedArtefact.setSelected(true);
							artefactModel.setSelectedArtefact(selectedArtefact);
							long artefactStartTimeInMs = (long)(1000 * selectedArtefact.getStartEvent().getSampleStamp() / artefactModel.getActiveEventEntry().getSampleRate());
							if(!(artefactStartTimeInMs > signalViewerModel.getStartVisibleTimeInMs() && artefactStartTimeInMs < signalViewerModel.getEndVisibleTimeInMs())){
								long visibleTimeInMs = signalViewerModel.getEndVisibleTimeInMs() - signalViewerModel.getStartVisibleTimeInMs();
								long gotoTimeInMs = artefactStartTimeInMs - (long)(visibleTimeInMs/2);
								if( gotoTimeInMs < 0 )
									gotoTimeInMs = 0;
								signalViewerModel.goToTime(gotoTimeInMs);
							}
							
							final int column = i;
							if(column == 0){
								signalViewerModel.repaintSignalCanvas();
								return;
							}
							final Text text = new Text (artefactsTable, SWT.NONE);
							Listener textListener = new Listener () {
								public void handleEvent (final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											changeArtefact(item, column, text.getText());
											text.dispose ();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													changeArtefact(item, column, text.getText());
													//FALL THROUGH
												case SWT.TRAVERSE_ESCAPE:
													text.dispose ();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText (i));
							text.selectAll();
							text.setFocus ();
							signalViewerModel.repaintSignalCanvas();
							return;
						}
						if (!visible && rect.intersects (clientArea)) {
							visible = true;
						}
					}
					if (!visible) return;
					index++;
				}
			}
		});
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		artefactsTable.setLayoutData(data);

		TableColumn  columnArtefactNumber = new TableColumn(artefactsTable, SWT.LEFT);
		columnArtefactNumber.setText(" # ");
		
		TableColumn columnArtefactStartTime = new TableColumn(artefactsTable, SWT.LEFT);
		columnArtefactStartTime.setText("          Begin           ");
		
		TableColumn columnArtefactEndTime = new TableColumn(artefactsTable, SWT.LEFT);
		columnArtefactEndTime.setText("             Ende            ");
		
		TableColumn columnArtefactTyp = new TableColumn(artefactsTable, SWT.LEFT);
		columnArtefactTyp.setText("                    Typ                ");
		
		TableColumn columnArtefactChannels = new TableColumn(artefactsTable, SWT.LEFT);
		columnArtefactChannels.setText("                    Kanäle                 ");
		
		columnArtefactNumber.pack();
		columnArtefactStartTime.pack();
		columnArtefactEndTime.pack();
		columnArtefactTyp.pack();
		columnArtefactChannels.pack();
	}

	@Override
	public void activeArtefactEntryChanged() {
		EventEntry artefactEntry = artefactModel.getActiveEventEntry();
		if(artefactEntry != null){
			SignalModel signalModel = Common.getInstance().signalModel;
			String allChannelNames = getAllChannelsName();
			List<Artefact> artefacts = artefactModel.read(0, Long.MAX_VALUE);
			int index = 0;
			if(artefactTableItems != null){
				for(int i = 0; i < artefactTableItems.length; i++){
					artefactTableItems[i].dispose();
				}
			}
			if(artefacts.size() > 0)
				artefactTableItems = new TableItem[artefacts.size()];
			for(int i = 0; i < artefacts.size(); i++ ){
				Artefact artefact = artefacts.get(i);
				artefactTableItems[i] = new TableItem (artefactsTable, SWT.NONE);
				if(artefact.isSelected()){
					artefactsTable.select(index);
				}
				artefactTableItems[i].setText (0, "" + (index++));
				long startTimeInMs = (long)(((double)artefact.getStartEvent().getSampleStamp() / (double)artefactEntry.getSampleRate()) * 1000);
				artefactTableItems[i].setText (1, TimeUtil.getTimeString(startTimeInMs, true));
				long endTimeInMs = (long)(((double)artefact.getEndEvent().getSampleStamp() / (double)artefactEntry.getSampleRate()) * 1000);
				artefactTableItems[i].setText (2, TimeUtil.getTimeString(endTimeInMs, true));
				artefactTableItems[i].setText (3, artefact.getType());
				artefactTableItems[i].setText (4, artefact.getChannel() == -1 ? allChannelNames : signalModel.getChannelName(artefact.getChannel()));
				artefactTableItems[i].setData(artefact);
			}
		}
	}

	@Override
	public void artefactSelected() {
		Artefact selectedArtefact = artefactModel.getSelectedArtefact();
		if(selectedArtefact != null){
			int index = 0;
			for(TableItem artefactTableItem : artefactTableItems){
				Artefact currentArtefact = (Artefact)artefactTableItem.getData();
				if(currentArtefact == selectedArtefact){
					artefactsTable.select(index);
					artefactsTable.showSelection();
					selectedTableItem = artefactTableItem;
					artefactsTable.redraw();
				}
				index++;
			}
		}else{
			if(selectedTableItem != null){
				selectedTableItem = null;
			}
		}
		
		artefactsTable.redraw();
	}

	public void selectedArtefactModified(){
		Artefact selectedArtefact = artefactModel.getSelectedArtefact();
		EventEntry artefactEntry = artefactModel.getActiveEventEntry();
		if(selectedArtefact != null){
			if(selectedArtefact.isStartSelected()){
				long startTimeInMs = (long)(((double)selectedArtefact.getStartEvent().getSampleStamp() / (double)artefactEntry.getSampleRate()) * 1000);
				selectedTableItem.setText (1, TimeUtil.getTimeString(startTimeInMs, true));
			}else{
				long endTimeInMs = (long)(((double)selectedArtefact.getEndEvent().getSampleStamp() / (double)artefactEntry.getSampleRate()) * 1000);
				selectedTableItem.setText (2, TimeUtil.getTimeString(endTimeInMs, true));
			}
			artefactsTable.redraw();
		}
	}
	
	private void changeArtefact(TableItem tableItem, int column, String newValue){
		if(tableItem.getText(column) != newValue){
			Artefact artefact = (Artefact)tableItem.getData();
			
			switch (column) {
				case 1:
					if(TimeUtil.isValidTime(newValue)){
						long newStartTimeInMs = TimeUtil.getTimeInMs(newValue);
						artefact.getStartEvent().setSampleStamp((long)((newStartTimeInMs * artefactModel.getActiveEventEntry().getSampleRate())/1000));
					}else{
						return;
					}
					break;
				case 2:
					if(TimeUtil.isValidTime(newValue)){
						long newEndTimeInMs = TimeUtil.getTimeInMs(newValue);
						artefact.getEndEvent().setSampleStamp((long)((newEndTimeInMs * artefactModel.getActiveEventEntry().getSampleRate())/1000));
					}else{
						return;
					}
					break;
				case 3:
					artefact.setType(newValue);
					break;
				case 4:
					if(newValue.equalsIgnoreCase(getAllChannelsName())){
						artefact.setChannel(-1);
					}else{
						int channelIndex = Common.getInstance().signalModel.getChannelIndex(newValue);
						if(channelIndex != -1){
							artefact.setChannel(channelIndex);
						}else{
							return;
						}
					}
					break;
			}
			artefactModel.setActiveEventEntryChanged(true);
			tableItem.setText(column, newValue);
			signalViewerModel.repaintSignalCanvas();
		}
	}
  
	
	private String getAllChannelsName(){
		SignalModel signalModel = Common.getInstance().signalModel;
				String allChannelNames = "";
		for(ChannelData channelData : signalModel.getChannelList()){
			allChannelNames += channelData.getName() + ", ";
		}
		if(allChannelNames.length() > 2)
			allChannelNames = allChannelNames.substring(0, allChannelNames.length() - 2);
		return allChannelNames;
	}

}
