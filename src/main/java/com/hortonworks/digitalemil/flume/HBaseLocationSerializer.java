package com.hortonworks.digitalemil.flume;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.flume.FlumeException;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;

public class HBaseLocationSerializer extends SkipableHBaseSerializer {
	@Override
	public List<Row> getActions() throws FlumeException {
		List<Row> actions = new LinkedList<Row>();
		if(skip(cf, col, payload))
			return actions;
		try {		
			Put put = new Put(rowKey);
			
			if(new String(cf).equals("w")) {
				put.add( cf, "map".getBytes(), payload);		
			}
			else {
			String doc= new String(payload);
			int r= doc.indexOf("=") + 1;
			doc= doc.substring(r, doc.length()-2);
			System.out.println("payload: "+doc);
			StringTokenizer st= new StringTokenizer(doc, ",");
			put.add( cf, "user".getBytes(), st.nextToken().getBytes());
			put.add( cf, "remoteaddress".getBytes(), st.nextToken().getBytes());
			put.add( cf, "latitude".getBytes(), st.nextToken().getBytes());
			put.add( cf, "longitude".getBytes(), st.nextToken().getBytes());
			put.add( cf, "altitude".getBytes(), st.nextToken().getBytes());
			put.add( cf, "accuracy".getBytes(), st.nextToken().getBytes());
			put.add( cf, "altitudeaccuracy".getBytes(), st.nextToken().getBytes());
			put.add( cf, "heading".getBytes(), st.nextToken().getBytes());
			put.add( cf, "speed".getBytes(), st.nextToken().getBytes());
			put.add( cf, "timestamp".getBytes(), st.nextToken().getBytes());
			}
			actions.add(put);
		} catch (Exception e) {
			throw new FlumeException("Error for Rowkey: " + new String(rowKey) + ", CF: "+new String(cf)+", Col: "+new String(col)+" value: "
					+ new String(payload));
		}
		return actions;
	}

	
	class Location {
		/*
		{0:0:0:0:0:0:0:1:1383927113712
        =guest,0:0:0:0:0:0:0:1,48.170228319829604,11.642405760703149,null,65,null,n
        ull,null,405619911973,1383927113712} 
        */
	}
}
