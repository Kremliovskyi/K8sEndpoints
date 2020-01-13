package getservicesinfo.models;

import getservicesinfo.Utils;
import org.joda.time.DateTime;

public class ServiceInfo implements Comparable<ServiceInfo> {

    private String name;
    private String ip;
    private String ports;
    private String serviceNameSpace;
    private String serviceCreationTimestamp;

    public ServiceInfo(String name, String ip, String ports, String serviceNameSpace, DateTime serviceCreationTimestamp) {
        this.name = name;
        this.ip = ip;
        this.ports = ports;
        this.serviceNameSpace = serviceNameSpace;
        this.serviceCreationTimestamp = Utils.normalizeDate(serviceCreationTimestamp);
    }

    public String getName() {
        return name;
    }

    public ServiceInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ServiceInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getPorts() {
        return ports;
    }

    public ServiceInfo setPorts(String ports) {
        this.ports = ports;
        return this;
    }

    public String getServiceNameSpace() {
        return serviceNameSpace;
    }

    public ServiceInfo setServiceNameSpace(String serviceNameSpace) {
        this.serviceNameSpace = serviceNameSpace;
        return this;
    }

    public String getServiceCreationTimestamp() {
        return serviceCreationTimestamp;
    }

    public ServiceInfo setServiceCreationTimestamp(String serviceCreationTimestamp) {
        this.serviceCreationTimestamp = serviceCreationTimestamp;
        return this;
    }

    @Override
    public int compareTo(ServiceInfo serviceInfo) {
        return this.name.compareTo(serviceInfo.getName());
    }
}
