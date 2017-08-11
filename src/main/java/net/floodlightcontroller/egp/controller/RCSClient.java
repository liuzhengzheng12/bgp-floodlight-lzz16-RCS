package net.floodlightcontroller.egp.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import net.floodlightcontroller.egp.config.AllConfig;
import net.floodlightcontroller.egp.egpkeepalive.KeepAliveTimerThread;
import net.floodlightcontroller.egp.routing.RoutingTable;

public class RCSClient implements Runnable {
	private String server_ip;
	private Integer server_port;
	private RoutingTable table;
	private AllConfig config;
	private KeepAliveTimerThread timerThread;
	
	public RCSClient(String server_ip, Integer server_port, RoutingTable table, AllConfig config,  KeepAliveTimerThread timerThread) {
		this.server_ip = server_ip;
		this.server_port = server_port;
		this.config = config;
		this.table = table;
		this.timerThread = timerThread;
	}

	
	@SuppressWarnings("resource")
	@Override
	public void run() {
		 Socket socket = null;
		 ObjectInputStream ois = null;
		 ObjectOutputStream oos = null;
		 
		 //每隔1秒尝试建立与RCS的连接
		 boolean flag = false;
		 while(!flag) {
			 try {
				flag = true;
				socket = new Socket(InetAddress.getByName(server_ip), server_port);
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Trying to connect the RCS");
				flag = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		 }
		 System.out.println("Connected to the RCS");
		 
		try {
			//发送AS号,IP地址,交换机号
			oos.writeObject(config.getLocalId());
			oos.writeObject(config.getLocalAs().get(0).getDstIp());
			oos.writeObject(config.getLocalAs().get(0).getOutPort().get(0).getSwitchId());
			
			//发送边和对应的出端口号
			//传送边的数量
			Integer edge_num = config.getListController().size();
			oos.writeObject(edge_num);
			while((edge_num--) != 0) {
				oos.writeObject(config.getListController().get(edge_num).getId());
				oos.writeObject(config.getListController().get(edge_num).getListLink().get(0).getLocalSwitchPort());
				oos.writeObject(config.getListController().get(edge_num).getCs());
				oos.writeObject(config.getListController().get(edge_num).getIp());
			}
			
			//等到BGP路由收敛时， 把路由表发送给RCSApp
			oos.writeObject("Route");
			oos.writeObject(table.getRoutes());
			Thread handleThread = new RCSHandleThread(ois, oos, table);
			handleThread.start();
			timerThread.setObjectOutputStream(oos);	
		} catch (IOException e) {
		}
	}

}
