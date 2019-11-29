package getservicesinfo.configparser;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cluster {

    private String name;
    private Map<String, String> cluster;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getCluster() {
        return cluster;
    }

    public void setCluster(Map<String, String> cluster) {
        this.cluster = cluster;
    }
}
