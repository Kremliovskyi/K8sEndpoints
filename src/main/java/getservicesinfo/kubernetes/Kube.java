package getservicesinfo.kubernetes;

import getservicesinfo.Main;
import getservicesinfo.configparser.ConfigParser;
import getservicesinfo.models.Endpoint;
import getservicesinfo.models.PodInfo;
import io.kubernetes.client.ApiCallback;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1EndpointAddress;
import io.kubernetes.client.models.V1EndpointSubset;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1EndpointsList;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ObjectReference;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodStatus;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import javafx.scene.control.ProgressBar;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Kube {

    private String ip;
    private Integer port;
    private String podName;
    private String podNameSpace;
    private String endpointName;
    private String version;
    private DateTime podCreationTimestamp;
    private String phase;
    private V1PodList v1PodList;
    private Set<Endpoint> endpoints = new TreeSet<>();
    private Set<PodInfo> pods = new TreeSet<>();
    private CoreV1Api api;
    private CountDownLatch latch = new CountDownLatch(1);
    private String currentContext;

    public Kube(String context) throws Throwable {
        getEndpointInfo(context);
    }

    public void getEndpointInfo(String context) throws Throwable {
        currentContext = context;
        KubeConfig kc = KubeConfig.loadKubeConfig(ConfigParser.getInstance().getConfigFileReader());
        kc.setContext(context);
        ApiClient client = ClientBuilder.kubeconfig(kc).build();
        Configuration.setDefaultApiClient(client);
        api = new CoreV1Api();
        fillInfo(api);
    }

    public Set<PodInfo> getAllPodsInfo() {
        Set<PodInfo> podInfoSet = new TreeSet<>();
        try {
            V1PodList v1PodList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null);
            v1PodList.getItems().forEach(v1Pod -> {
                String name;
                String ip;
                String namespace;
                String phase;
                DateTime creationTimestamp;
                V1ObjectMeta v1ObjectMeta = v1Pod.getMetadata();
                V1PodStatus v1PodStatus = v1Pod.getStatus();
                V1PodSpec v1PodSpec = v1Pod.getSpec();
                if (v1ObjectMeta != null && v1PodStatus != null && v1PodSpec != null) {
                    name = v1ObjectMeta.getName();
                    ip = v1PodStatus.getPodIP();
                    namespace = v1ObjectMeta.getNamespace();
                    creationTimestamp = v1ObjectMeta.getCreationTimestamp();
                    phase = v1PodStatus.getPhase();
                    PodInfo podInfo = new PodInfo(name, ip, namespace, creationTimestamp, phase);
                    podInfoSet.add(podInfo);
                }
            });
        } catch (ApiException e) {
            Main.showAlert(e.getMessage());
        }
        return podInfoSet;
    }

    private void fillInfo(CoreV1Api api) throws Throwable {
        getDetailedPodsInfo(api);
        V1EndpointsList v1EndpointsList = api.listEndpointsForAllNamespaces(null, null, null, null, null, null, null, null);
        v1EndpointsList.getItems().forEach(endpoint -> {
            List<V1EndpointSubset> subsets = endpoint.getSubsets();
            if (subsets != null) {
                subsets.forEach(v1EndpointSubset -> {
                    v1EndpointSubset.getPorts().stream()
                            .filter(v1EndpointPort -> {
                                String portName = v1EndpointPort.getName();
                                return portName != null && portName.contains("http");
                            }).findFirst().ifPresent(v1EndpointPort -> {
                        port = v1EndpointPort.getPort();
                    });
                    List<V1EndpointAddress> addresses = v1EndpointSubset.getAddresses();
                    if (addresses != null) {
                        getPodInfo(addresses);
                        getEndpointInfo(endpoint);
                        resetVariables();
                    }
                });
            }
        });
        v1PodList = null;
        latch = new CountDownLatch(1);
    }

    private void getDetailedPodsInfo(CoreV1Api api) throws Throwable {
        api.listPodForAllNamespacesAsync(null, null, null, null, null, null, null, null,
                new ApiCallback<V1PodList>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                        throw new RuntimeException("V1PodList was not retrieved. Try to refresh.");
                    }

                    @Override
                    public void onSuccess(V1PodList result, int statusCode, Map<String, List<String>> responseHeaders) {
                        v1PodList = result;
                        latch.countDown();
                    }

                    @Override
                    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                    }

                    @Override
                    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                    }
                });
    }

    private void getPodInfo(List<V1EndpointAddress> addresses) {
        try {
            if (!latch.await(3L, TimeUnit.MINUTES)) {
                throw new RuntimeException("V1PodList was not retrieved. Try to refresh.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        addresses.forEach(v1EndpointAddress -> {
            ip = v1EndpointAddress.getIp();
            V1ObjectReference targetRef = v1EndpointAddress.getTargetRef();
            if (targetRef != null) {
                podName = targetRef.getName();
                podNameSpace = targetRef.getNamespace();
                v1PodList.getItems().stream().filter(v1Pod -> {
                    boolean result = false;
                    V1ObjectMeta v1ObjectMeta = v1Pod.getMetadata();
                    if (v1ObjectMeta != null) {
                        String name = v1ObjectMeta.getName();
                        if (name != null) {
                            result = name.equals(podName);
                        }
                    }
                    return result;
                }).findFirst().ifPresent(v1Pod -> {
                    V1ObjectMeta v1ObjectMeta = v1Pod.getMetadata();
                    if (v1ObjectMeta != null) {
                        podCreationTimestamp = v1ObjectMeta.getCreationTimestamp();
                    }
                    V1PodStatus v1PodStatus = v1Pod.getStatus();
                    if (v1PodStatus != null) {
                        phase = v1PodStatus.getPhase();
                    }
                });
                pods.add(new PodInfo(podName, ip + ":" + port, podNameSpace, podCreationTimestamp, phase));
            }
        });
    }

    private void getEndpointInfo(V1Endpoints endpoint) {
        V1ObjectMeta metadata = endpoint.getMetadata();
        if (metadata != null) {
            Map<String, String> labels = metadata.getLabels();
            if (labels != null) {
                version = labels.get("helm.sh/chart");
                if (version != null) {
                    int i = 0;
                    while (i < version.length() && !Character.isDigit(version.charAt(i))) i++;
                    version = version.substring(i);
                }
                endpointName = labels.get("app.kubernetes.io/name");
                if (endpointName != null && version != null) {
                    endpoints.add(new Endpoint(endpointName, version, pods));
                    pods.clear();
                }
            }
        }
    }

    private void resetVariables() {
        podName = null;
        ip = null;
        port = 0;
        version = null;
        podCreationTimestamp = null;
        phase = null;
        endpointName = null;
        podNameSpace = null;
    }

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void resetEndpoints() {
        endpoints.clear();
    }

    public void findPodLogs(PodInfo podInfo,
                            boolean isEqual,
                            String log,
                            Integer sinceSeconds,
                            Integer tailLines,
                            ProgressBar progressBar) {
        LogRequest logRequest = new LogRequest.Builder()
                .setPodInfo(podInfo)
                .setEqual(isEqual)
                .setLog(log)
                .setSinceSeconds(sinceSeconds)
                .setTailLines(tailLines)
                .build();
        new LogsProcessor(progressBar, this)
                .setLogRequest(logRequest)
                .findPodLogs();
    }

    public void restartEndpoint(Endpoint endpoint) {
        endpoint.getPods().forEach(podInfo -> {
            try {
                api.deleteNamespacedPodAsync(podInfo.getName(), podInfo.getPodNameSpace(), null, null, null,
                        0, null, null, null);
            } catch (Throwable ignore) {
            }
        });
    }

    public String getCurrentContext() {
        return currentContext;
    }

    CoreV1Api getApi() {
        return api;
    }
}
