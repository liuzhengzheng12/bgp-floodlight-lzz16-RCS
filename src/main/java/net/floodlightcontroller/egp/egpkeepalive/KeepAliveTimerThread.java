 package net.floodlightcontroller.egp.egpkeepalive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import net.floodlightcontroller.egp.event.LinkDownEvent;
import net.floodlightcontroller.egp.event.LinkUpEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeepAliveTimerThread extends Thread {
	protected static long timer = 2;
	private static ObjectOutputStream oos;
	private static Logger logger = LoggerFactory.getLogger("egp.egpkeepalive.KeepAliveTimerThread"); 
	
	public void setObjectOutputStream(ObjectOutputStream oos) {
		this.oos = oos;
	}
	
	public void run() {
		while (true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long currentTime = System.currentTimeMillis();
			for(Map.Entry<String,Long> entry : EGPKeepAlive.timermap.entrySet()){
				String key = entry.getKey();
				Long val = entry.getValue();
				if (timer < (currentTime - val) / 1000) {
					if (EGPKeepAlive.statusmap.get(key).booleanValue() == true){
						logger.warn("Link from {} is down!", key);
						try {
							File file = new File("time.txt");
							FileWriter fw = new FileWriter(file, true);
							PrintWriter pw = new PrintWriter(fw);
							pw.println(currentTime);
							pw.flush();
							fw.flush();
							pw.close();
							fw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						String switchPortArray[] = key.split(": ");
						EGPKeepAlive.controllerMain.switchPortToRemoteController(switchPortArray[0], switchPortArray[1])
									.getReceiveEvent().addEvent(new LinkDownEvent());
						EGPKeepAlive.statusmap.put(key, Boolean.valueOf(false));
						if (oos == null) {
							logger.warn("can't connect to the RCSApp");
						}
						else {
							try {
								oos.writeObject("LinkDown");
								oos.writeObject(switchPortArray[0]);
								oos.writeObject(switchPortArray[1]);
								logger.info("write to RCSApp", switchPortArray[0]);
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}
					}
				}
				else{
					if (EGPKeepAlive.statusmap.get(key).booleanValue() == false){
						logger.warn("Link from {} is up!", key);
						String switchPortArray[] = key.split(": ");
						EGPKeepAlive.controllerMain.switchPortToRemoteController(switchPortArray[0], switchPortArray[1])
									.getReceiveEvent().addEvent(new LinkUpEvent());
						EGPKeepAlive.statusmap.put(key, Boolean.valueOf(true));
						if (oos == null) {
							logger.warn("can't connect to the RCSApp");
						}
						else {
							try {
								oos.writeObject("LinkUp");
								oos.writeObject(switchPortArray[0]);
								oos.writeObject(switchPortArray[1]);
								logger.info("write to RCSApp", switchPortArray[0]);
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}
					}
				}
			}
			
		}
	}
}
