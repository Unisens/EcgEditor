package de.fzi.ekgeditor.tests;

import java.util.List;

import org.unisens.SignalEntry;
import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.ri.UnisensImpl;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;

public class libPerformance {

	public static void copyUnisensPerf(Unisens unisens,String filename) throws UnisensParseException
	{
		Unisens copyDestination = new UnisensImpl(filename);
		copyDestination.setMeasurementId(unisens.getMeasurementId());
		copyDestination.setComment(unisens.getComment());
		copyDestination.setDuration(unisens.getDuration());

		List<SignalEntry> entries=UnisensAdapter.getECGEntries(unisens);
		try{

			for (SignalEntry s:entries)
			{
//				copyDestination.createSignalEntry(
//						s.getId(), 
//						s.getChannelNames(),
//						s.getDataType(),
//						s.getSampleRate());
				
				System.out.println(s.getCount()+ " kopieren ("+s.getId()+" )");
				long timeStart=System.currentTimeMillis();
				copySignalEntryPerf(s,copyDestination);
				long timeEnd=System.currentTimeMillis();
				long diff=timeEnd-timeStart;
				System.out.println(s.getId()+ " kopiert ("+(diff)+" )");
			}

			copyDestination.save();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	private static void copySignalEntryPerf(SignalEntry s,Unisens copyDestination)
	{
		long allcopied=0;
		
		try
		{
			// set header for new signal-entry
			SignalEntry scopy=copyDestination.createSignalEntry(
					s.getId(), 
					s.getChannelNames(),
					s.getDataType(),
					s.getSampleRate());

			// prepare to copy data
			long samples=s.getCount();
			s.resetPos();long pos=0;

			while (pos<samples)
			{
				// calculate std. length
				int length=Constants.stdSampleBlock;
				if (pos+length>samples)
				{
					length=(int) (samples-pos);
				}

				if (length>0)
				{

					System.out.print("*");
					scopy.append(s.read(pos,length));
					System.out.print("#");

					pos=pos+length;
					allcopied=allcopied+length;
					
				} // endif length>0
			} // end while pos<samples
		} // end try
		catch (Exception ex)
		{
			System.out.println("Copy SignalEntry:"+ex.getMessage());
		}
		
		System.out.println(allcopied+" Samples kopiert.");
	} // end method copySignalEntry
}
