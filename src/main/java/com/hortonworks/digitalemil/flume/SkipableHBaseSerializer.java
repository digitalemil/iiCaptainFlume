package com.hortonworks.digitalemil.flume;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.HbaseEventSerializer;
import org.apache.flume.sink.hbase.SimpleRowKeyGenerator;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;

import com.google.common.base.Charsets;

/**
 
 */
public class SkipableHBaseSerializer implements HbaseEventSerializer {
	protected byte[] cf;
	protected byte[] col;
	protected byte[] payload;
	protected byte[] rowKey;
	protected byte[] incCol;
	protected byte[] incrementRow;
	protected String skipCF, skipCol;

	public boolean skip(byte[] cf, byte[] col, byte[] payload) {
		boolean ret= false;
		
		if(cf!= null && (new String(cf)).equals(skipCF))
			ret= true;
		
		return ret;
	}
	
	@Override
	public void configure(Context context) {
		try {
			skipCF= context.getString("columnFamily");
		}
		catch(Exception e) {
			skipCF= null;
		}
		try {
			skipCol= context.getString("column");
		}
		catch(Exception e) {
			skipCol= null;
		}
//		System.out.println("Will skip: "+new String(table)+" "+skipCF+ " "+skipCol);
	}
	
	@Override
	public void configure(ComponentConfiguration conf) {
	}

	@Override
	public void initialize(Event event, byte[] cf) {
		rowKey = event.getHeaders().get("RowKey").getBytes();
		this.cf = event.getHeaders().get("ColumnFamily").getBytes();
		col = event.getHeaders().get("Column").getBytes();
		this.payload = event.getBody();
	}

	@Override
	public List<Row> getActions() throws FlumeException {
		List<Row> actions = new LinkedList<Row>();
		if(skip(cf, col, payload))
			return actions;
		try {		
			Put put = new Put(rowKey);
			put.add( cf, col, payload);
			actions.add(put);
		} catch (Exception e) {
			throw new FlumeException("Error for Rowkey: " + new String(rowKey) + ", CF: "+new String(cf)+", Col: "+new String(col)+" value: "
					+ new String(payload));
		}
		return actions;
	}

	@Override
	public String toString() {
		return "MyHBaseSerializer [col=" + Arrays.toString(col) + ", payload="
				//+ Arrays.toString(payload) + ", rowKey="
				+ Arrays.toString(rowKey) + ", incCol="
				+ Arrays.toString(incCol) + ", incrementRow="
				+ Arrays.toString(incrementRow) + "]";
	}

	public enum KeyType {
		FROMHEADER;
	}

	@Override
	public void close() {
	}

	@Override
	public List<Increment> getIncrements() {
		List<Increment> incs = new LinkedList<Increment>();
		if (incCol != null) {
			Increment inc = new Increment(incrementRow);
			inc.addColumn(cf, incCol, 1);
			incs.add(inc);
		}
		return incs;
	}

}
