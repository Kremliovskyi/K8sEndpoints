package getservicesinfo.models;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Endpoint implements Comparable<Endpoint> {

    private String name;
    private String version;
    private Set<PodInfo> pods ;

    public Endpoint(String name, String version, Set<PodInfo> pods) {
        this.name = name;
        this.version = version;
        this.pods = new TreeSet<>(pods);
    }

    public String getName() {
        return name;
    }

    public Endpoint setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Endpoint setVersion(String version) {
        this.version = version;
        return this;
    }

    public Set<PodInfo> getPods() {
        return pods;
    }

    public Endpoint setPods(Set<PodInfo> pods) {
        this.pods = pods;
        return this;
    }

    @Override
    public int compareTo(Endpoint endpoint) {
        return this.name.compareTo(endpoint.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return name.equals(endpoint.name) &&
                version.equals(endpoint.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }
}
