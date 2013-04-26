package org.apache.zookeeper.server.persistence;

import org.apache.zookeeper.server.PurgeTxnLog;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


public class DisklessPurgeSnapshot implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(DisklessPurgeSnapshot.class);
public void run()
{
while(true)
{
	try
	{
	PurgeTxnLog.purge(FileTxnSnapLog.ssnapLog,FileTxnSnapLog.ssnapLog, 3);
	Thread.sleep(60000);
	}
	catch(Exception e)
	{
		LOG.error(e.toString());
	}
}
}
}
