package net.floodlightcontroller.egp.routing;

import java.util.HashMap;
import java.util.Map;

//路由列表：映射关系<RoutingIndex, RoutingTableEntry>
public class RoutingList {

    Map<RoutingIndex, RoutingTableEntry> map = new HashMap<RoutingIndex, RoutingTableEntry>();
    
    //添加或更新entry
    public synchronized boolean add(RoutingTableEntry entry) {
        RoutingTableEntry tmp = map.get(entry.getIndex());
        if (tmp == null) {
            map.put(entry.getIndex(), entry);
            return true;
        }  else {
            if (tmp.isEmpty() || tmp.getTimestamp() < entry.getTimestamp()) {
                map.put(entry.getIndex(), entry);
                return true;
            }  else {
                return false;
            }
        }
    }
}
