package net.floodlightcontroller.egp.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.floodlightcontroller.egp.egpkeepalive.EGPKeepAlive;
import net.floodlightcontroller.egp.routing.RoutingTable;

public class AppHandleThread extends Thread {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private RoutingTable table;
	
	public AppHandleThread(ObjectInputStream ois, ObjectOutputStream oos, RoutingTable table) {
		this.ois = ois;
		this.oos = oos;
		this.table = table;
	}
	
	@Override
	public void run() {
		Integer priority = 32769;
		while(true) {
			try {
				String cmd = (String)ois.readObject();
				System.out.println(cmd);
				if (cmd.equals("Retransmit")) {
					Thread.sleep(20000);
					oos.writeObject("Route");
					oos.writeObject(table.getRoutes());
				}
				else {
					String switchid = (String)ois.readObject();
					String srcipv4 = (String)ois.readObject();
					String dstipv4 = (String)ois.readObject();
					String outport = (String)ois.readObject();
					if (cmd.equals("Create")) {
						String loginfo = "CreateFlowMods:" +
					 			 "\n---swichId: " + switchid + 
					 			 "\n---srcIp: " + srcipv4 +
					 			 "\n---dstIp: " + dstipv4 +
					 			 "\n---outPort:" + outport+"\n";
						System.out.println(loginfo);
						EGPKeepAlive.createFlowMods(switchid, srcipv4, dstipv4, null, null, null, Integer.parseInt(outport), priority);
					}
					else if (cmd.equals("Delete")) {
						String loginfo = "DeleteFlowMods:" +
					 			 "\n---swichId: " + switchid + 
					 			 "\n---srcIp: " + srcipv4 +
					 			 "\n---dstIp: " + dstipv4 +
					 			 "\n---outPort:" + outport+"\n";
						System.out.println(loginfo);
						EGPKeepAlive.deleteFlowMods(switchid, srcipv4, dstipv4, null, null, null, Integer.parseInt(outport), priority);
					}
				}
				Thread.sleep(1000);
			} catch (ClassNotFoundException | IOException | InterruptedException e) {
			}
		}
	}
}
