package net.floodlightcontroller.egp.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemoteControllerConfig implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7289824482420279010L;
	private String id;
    private String ip;
    private int port;
    private String cs;
    private List<RemoteControllerLinkConfig> listLink = new ArrayList<RemoteControllerLinkConfig>();

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public List<RemoteControllerLinkConfig> getListLink() {
        return listLink;
    }

    public String getCs() {
        return cs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setListLink(List<RemoteControllerLinkConfig> listLink) {
        this.listLink = listLink;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }
}
