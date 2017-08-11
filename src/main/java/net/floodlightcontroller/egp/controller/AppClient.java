package net.floodlightcontroller.egp.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import net.floodlightcontroller.egp.config.AllConfig;
import net.floodlightcontroller.egp.egpkeepalive.KeepAliveTimerThread;
import net.floodlightcontroller.egp.routing.RoutingTable;

public class AppClient implements Runnable {
	private String server_ip;
	private Integer server_port;
	private RoutingTable table;
	private AllConfig config;
	private KeepAliveTimerThread timerThread;
	
	public AppClient(String server_ip, Integer server_port, RoutingTable table, AllConfig config,  KeepAliveTimerThread timerThread) {
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
		 
		 //每隔1秒尝试建立与AppServer的连接
		 boolean flag = false;
		 while(!flag) {
			 try {
				flag = true;
				socket = new Socket(InetAddress.getByName(server_ip), server_port);
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Trying to connect the RCSApp");
				flag = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		 }
		 System.out.println("Connected to the RCSApp");
		 
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
			}
			
			//等到BGP路由收敛时， 把路由表发送给RCSApp
			oos.writeObject(table.getRoutes());
			Thread handleThread = new AppHandleThread(ois, oos, table);
			handleThread.start();
			timerThread.setObjectOutputStream(oos);	
		} catch (IOException e) {
		}
	}

}
