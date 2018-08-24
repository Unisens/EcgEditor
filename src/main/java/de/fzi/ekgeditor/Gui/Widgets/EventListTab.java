package de.fzi.ekgeditor.Gui.Widgets;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.unisens.Entry;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.ChannelData;
import de.fzi.ekgeditor.data.EventModel;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.events.ArtefactModelListener;
import de.fzi.ekgeditor.events.EventModelListener;
import de.fzi.ekgeditor.utils.TimeUtil;

public class EventListTab implements EventModelListener
{
	private Table eventListTable;
	private TableItem[] eventListTableItems;
	private TableItem selectedTableItem = null;
	private EventModel eventModel;
	private SignalViewerModel signalViewerModel;
	private final TableEditor editor;
	private Combo entryCombo;
	private Text entryTextComment;
	private Text entryTextContentClass;
	private Text entryTextName;
	private Text entryTextSamples;

	public EventListTab(TabFolder tabFolder)
	{
		// this.artefactModel = Common.getInstance().artefactModel;
		this.eventModel = Common.getInstance().eventModel;
		this.signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		this.eventModel.addEventModelListener(this);
		// Common.getInstance().artefactModel.addArtefactModelListener(this);
		TabItem artefactsTabItem = new TabItem(tabFolder, SWT.NONE);
		artefactsTabItem.setText("Ereignisse");

		Composite eventListComposite = new Composite(tabFolder, SWT.NONE);
		eventListComposite.setLayout(new GridLayout(2, false));
		eventListComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		artefactsTabItem.setControl(eventListComposite);

		eventListTable = new Table(eventListComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		eventListTable.setHeaderVisible(true);
		eventListTable.setLinesVisible(true);

		editor = new TableEditor(eventListTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		eventListTable.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				// Jump to the selected event
				TableItem[] selectedEventTableItems = eventListTable.getSelection();
				TableItem selectedEventTableItem = selectedEventTableItems[0];
				org.unisens.Event selectedEvent = (org.unisens.Event) selectedEventTableItem.getData();

				int offsetInMs = (int) (signalViewerModel.getEndVisibleTimeInMs() - signalViewerModel
						.getStartVisibleTimeInMs()) / 2;
				long gotoTimeInMs = (long) (((double) selectedEvent.getSampleStamp() / (double) eventModel.getEntry()
						.getSampleRate()) * 1000);
				if (gotoTimeInMs > offsetInMs)
				{
					gotoTimeInMs -= offsetInMs;
				}
				else
				{
					gotoTimeInMs = 0;
				}

				signalViewerModel.goToTime(gotoTimeInMs);
			}
		});

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		eventListTable.setLayoutData(data);

		TableColumn columnArtefactNumber = new TableColumn(eventListTable, SWT.LEFT);
		columnArtefactNumber.setText(" # ");

		TableColumn columnArtefactTime = new TableColumn(eventListTable, SWT.LEFT);
		columnArtefactTime.setText("          Zeitpunkt          ");

		TableColumn columnArtefactType = new TableColumn(eventListTable, SWT.LEFT);
		columnArtefactType.setText("          Typ          ");

		TableColumn columnArtefactComment = new TableColumn(eventListTable, SWT.LEFT);
		columnArtefactComment.setText("          Kommentar          ");

		columnArtefactNumber.pack();
		columnArtefactTime.pack();
		columnArtefactType.pack();
		columnArtefactComment.pack();

		// Add group and forms for EventEntry selection and information
		Group entryInfoGroup = new Group(eventListComposite, SWT.NONE);
		entryInfoGroup.setLayout(new GridLayout(1, true));
		entryInfoGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		entryInfoGroup.setText("Entry-Informationen");

		entryCombo = new Combo(entryInfoGroup, SWT.READ_ONLY);
		entryCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				System.out.println("Es wurde " + entryCombo.getText() + " gewählt.");
				// TODO Liste laden
				readEventEntry(entryCombo.getText());
			}
		});
		
		GridData infoData = new GridData(SWT.FILL, SWT.TOP, true, false);

		
		entryCombo.setToolTipText("Wählen Sie ein EventEntry aus.");
		entryCombo.setLayoutData(infoData);
		entryCombo.pack();
		
		entryTextComment = new Text(entryInfoGroup, SWT.NONE);
		entryTextComment.setLayoutData(infoData);
		entryTextComment.pack();
		
		entryTextContentClass = new Text(entryInfoGroup, SWT.NONE);
		entryTextContentClass.setLayoutData(infoData);
		entryTextContentClass.pack();
		
		entryTextName = new Text(entryInfoGroup, SWT.NONE);
		entryTextName.setLayoutData(infoData);
		entryTextName.pack();

		entryTextSamples = new Text(entryInfoGroup, SWT.NONE);
		entryTextSamples.setLayoutData(infoData);
		entryTextSamples.pack();
	}


	public void readEventEntry(String entryId)
	{
		eventModel.readData(entryId);
		// See ArtefactsTab.java, activeArtefactEntryChanged()

		List<org.unisens.Event> eventList = eventModel.getEventList();
		int index = 0;

		// Empty the table, if it is not null
		if(eventListTableItems != null){
			for(int i = 0; i < eventListTableItems.length; i++){
				eventListTableItems[i].dispose();
			}
		}
		
		if (eventList.size() > 0)
		{
			eventListTableItems = new TableItem[eventList.size()];
		}

		for (int i = 0; i < eventList.size(); i++)
		{
			org.unisens.Event event = eventList.get(i);
			eventListTableItems[i] = new TableItem(eventListTable, SWT.NONE);
			// if (artefact.isSelected())
			// {
			// artefactsTable.select(index);
			// }
			eventListTableItems[i].setText(0, "" + (index++));
			long startTimeInMs = (long) (((double) event.getSampleStamp() / (double) eventModel.getEntry()
					.getSampleRate()) * 1000);
			eventListTableItems[i].setText(1, TimeUtil.getTimeString(startTimeInMs, true));
			eventListTableItems[i].setText(2, event.getType());
			eventListTableItems[i].setText(3, event.getComment());
			eventListTableItems[i].setData(event);
		}

		if (eventModel.getEntry().getComment() != null)
		{
			entryTextComment.setText("Kommentar: " + eventModel.getEntry().getComment());
		}
		
		if (eventModel.getEntry().getContentClass() != null)
		{
			entryTextContentClass.setText("Klasse: " + eventModel.getEntry().getContentClass());
		}
		
		if (eventModel.getEntry().getName() != null)
		{
			entryTextName.setText("Name: " + eventModel.getEntry().getName());
		}
		
		entryTextSamples.setText("Anzahl Events: " + eventModel.getEntry().getCount());
	}

	@Override
	public void activeEventEntryChanged()
	{
		List<Entry> entryList = Common.getInstance().unisens.getEntries();
		for (Entry e : entryList)
		{
			if (e instanceof EventEntry)
			{
				entryCombo.add(e.getId());
			}
		}
		entryCombo.pack();
	}

	@Override
	public void eventSelected()
	{
		// TODO Auto-generated method stub

	}


	// @Override
	// public void activeArtefactEntryChanged()
	// {
	// EventEntry artefactEntry = artefactModel.getActiveEventEntry();
	// if (artefactEntry != null)
	// {
	// SignalModel signalModel = Common.getInstance().signalModel;
	// String allChannelNames = getAllChannelsName();
	// List<Artefact> artefacts = artefactModel.read(0, Long.MAX_VALUE);
	// int index = 0;
	// if (artefactTableItems != null)
	// {
	// for (int i = 0; i < artefactTableItems.length; i++)
	// {
	// artefactTableItems[i].dispose();
	// }
	// }
	// if (artefacts.size() > 0)
	// artefactTableItems = new TableItem[artefacts.size()];
	// for (int i = 0; i < artefacts.size(); i++)
	// {
	// Artefact artefact = artefacts.get(i);
	// artefactTableItems[i] = new TableItem(eventListTable, SWT.NONE);
	// if (artefact.isSelected())
	// {
	// eventListTable.select(index);
	// }
	// artefactTableItems[i].setText(0, "" + (index++));
	// long startTimeInMs = (long) (((double)
	// artefact.getStartEvent().getSampleStamp() / (double) artefactEntry
	// .getSampleRate()) * 1000);
	// artefactTableItems[i].setText(1, TimeUtil.getTimeString(startTimeInMs,
	// true));
	// long endTimeInMs = (long) (((double)
	// artefact.getEndEvent().getSampleStamp() / (double) artefactEntry
	// .getSampleRate()) * 1000);
	// artefactTableItems[i].setText(2, TimeUtil.getTimeString(endTimeInMs,
	// true));
	// artefactTableItems[i].setText(3, artefact.getType());
	// artefactTableItems[i].setText(4, artefact.getChannel() == -1 ?
	// allChannelNames : signalModel
	// .getChannelName(artefact.getChannel()));
	// artefactTableItems[i].setData(artefact);
	// }
	// }
	// }
	//
	// @Override
	// public void artefactSelected()
	// {
	// Artefact selectedArtefact = artefactModel.getSelectedArtefact();
	// if (selectedArtefact != null)
	// {
	// int index = 0;
	// for (TableItem artefactTableItem : artefactTableItems)
	// {
	// Artefact currentArtefact = (Artefact) artefactTableItem.getData();
	// if (currentArtefact == selectedArtefact)
	// {
	// eventListTable.select(index);
	// eventListTable.showSelection();
	// selectedTableItem = artefactTableItem;
	// eventListTable.redraw();
	// }
	// index++;
	// }
	// }
	// else
	// {
	// if (selectedTableItem != null)
	// {
	// selectedTableItem = null;
	// }
	// }
	//
	// eventListTable.redraw();
	// }
	//
	// public void selectedArtefactModified()
	// {
	// Artefact selectedArtefact = artefactModel.getSelectedArtefact();
	// EventEntry artefactEntry = artefactModel.getActiveEventEntry();
	// if (selectedArtefact != null)
	// {
	// if (selectedArtefact.isStartSelected())
	// {
	// long startTimeInMs = (long) (((double)
	// selectedArtefact.getStartEvent().getSampleStamp() / (double)
	// artefactEntry
	// .getSampleRate()) * 1000);
	// selectedTableItem.setText(1, TimeUtil.getTimeString(startTimeInMs,
	// true));
	// }
	// else
	// {
	// long endTimeInMs = (long) (((double)
	// selectedArtefact.getEndEvent().getSampleStamp() / (double) artefactEntry
	// .getSampleRate()) * 1000);
	// selectedTableItem.setText(2, TimeUtil.getTimeString(endTimeInMs, true));
	// }
	// eventListTable.redraw();
	// }
	// }
	//
	// private void changeArtefact(TableItem tableItem, int column, String
	// newValue)
	// {
	// if (tableItem.getText(column) != newValue)
	// {
	// Artefact artefact = (Artefact) tableItem.getData();
	//
	// switch (column)
	// {
	// case 1:
	// if (TimeUtil.isValidTime(newValue))
	// {
	// long newStartTimeInMs = TimeUtil.getTimeInMs(newValue);
	// artefact
	// .getStartEvent()
	// .setSampleStamp(
	// (long) ((newStartTimeInMs *
	// artefactModel.getActiveEventEntry().getSampleRate()) / 1000));
	// }
	// else
	// {
	// return;
	// }
	// break;
	// case 2:
	// if (TimeUtil.isValidTime(newValue))
	// {
	// long newEndTimeInMs = TimeUtil.getTimeInMs(newValue);
	// artefact.getEndEvent().setSampleStamp(
	// (long) ((newEndTimeInMs *
	// artefactModel.getActiveEventEntry().getSampleRate()) / 1000));
	// }
	// else
	// {
	// return;
	// }
	// break;
	// case 3:
	// artefact.setType(newValue);
	// break;
	// case 4:
	// if (newValue.equalsIgnoreCase(getAllChannelsName()))
	// {
	// artefact.setChannel(-1);
	// }
	// else
	// {
	// int channelIndex =
	// Common.getInstance().signalModel.getChannelIndex(newValue);
	// if (channelIndex != -1)
	// {
	// artefact.setChannel(channelIndex);
	// }
	// else
	// {
	// return;
	// }
	// }
	// break;
	// }
	// artefactModel.setActiveEventEntryChanged(true);
	// tableItem.setText(column, newValue);
	// signalViewerModel.repaintSignalCanvas();
	// }
	// }
	//
	//
	// private String getAllChannelsName()
	// {
	// SignalModel signalModel = Common.getInstance().signalModel;
	// String allChannelNames = "";
	// for (ChannelData channelData : signalModel.getChannelList())
	// {
	// allChannelNames += channelData.getName() + ", ";
	// }
	// if (allChannelNames.length() > 2)
	// allChannelNames = allChannelNames.substring(0, allChannelNames.length() -
	// 2);
	// return allChannelNames;
	// }

}
