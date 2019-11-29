package getservicesinfo.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PodInfo implements Comparable<PodInfo>{

    private String name;
    private String ip;
    private String podNameSpace;
    private String podCreationTimestamp;
    private String phase;
    private String selectedContainer;

    public PodInfo(String name, String ip, String podNameSpace, DateTime podCreationTimestamp, String phase) {
        this.name = name;
        this.ip = ip;
        this.podNameSpace = podNameSpace;
        this.podCreationTimestamp = normalizeDate(podCreationTimestamp);
        this.phase = phase != null ? phase : "";
    }

    public String getName() {
        return name;
    }

    public PodInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public PodInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getPodNameSpace() {
        return podNameSpace;
    }

    public PodInfo setPodNameSpace(String podNameSpace) {
        this.podNameSpace = podNameSpace;
        return this;
    }

    public String getPodCreationTimestamp() {
        return podCreationTimestamp;
    }

    public PodInfo setPodCreationTimestamp(String podCreationTimestamp) {
        this.podCreationTimestamp = podCreationTimestamp;
        return this;
    }

    public String getPhase() {
        return phase;
    }

    public PodInfo setPhase(String phase) {
        this.phase = phase;
        return this;
    }

    public String getSelectedContainer() {
        return selectedContainer;
    }

    public PodInfo setSelectedContainer(String selectedContainer) {
        this.selectedContainer = selectedContainer;
        return this;
    }

    @Override
    public int compareTo(PodInfo podInfo) {
        return this.name.compareTo(podInfo.getName());
    }

    private String normalizeDate(DateTime creationTimestamp) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");
        return creationTimestamp != null ? dtf.print(creationTimestamp) : "";
    }
}
