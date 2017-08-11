package net.floodlightcontroller.egp.config;

import java.io.Serializable;

import net.floodlightcontroller.egp.routing.HopSwitch;

public class LocalAsPortConfig implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8172277093715323846L;
	private String switchId;
    private String switchPort;

    public String getSwitchId() {
        return switchId;
    }

    public String getSwitchPort() {
        return switchPort;
    }

    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    public void setSwitchPort(String switchPort) {
        this.switchPort = switchPort;
    }

    public HopSwitch getHopSwitch() {
        return new HopSwitch(switchId, switchPort);
    }
}
