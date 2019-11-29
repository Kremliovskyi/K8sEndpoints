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
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import javafx.scene.control.ProgressBar;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;
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

    public Kube(String context) {
        getEndpointInfo(context);
    }

    public void getEndpointInfo(String context) {
        currentContext = context;
        try {
            KubeConfig kc = KubeConfig.loadKubeConfig(ConfigParser.getInstance().getConfigFileReader());
            kc.setContext(context);
            ApiClient client = ClientBuilder.kubeconfig(kc).build();
            Configuration.setDefaultApiClient(client);
            api = new CoreV1Api();
            fillInfo(api);
        } catch (IOException | ApiException e) {
            Main.showAlert(e.getMessage());
        }
    }

    private void fillInfo(CoreV1Api api) throws ApiException {
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

    private void getDetailedPodsInfo(CoreV1Api api) throws ApiException {
        api.listPodForAllNamespacesAsync(null, null, null, null, null, null, null, null,
                new ApiCallback<V1PodList>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                        Main.showAlert("V1PodList was not retrieved. Try to refresh.");
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
            latch.await(15L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Main.showAlert("V1PodList was not retrieved. Try to refresh.");
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
                api.deleteNamespacedPodAsync(podInfo.getName(), podInfo.getPodNameSpace(), null, new V1DeleteOptions(), null,
                        null, null, null, null);
            } catch (Throwable ignore) {}
        });
    }

    public String getCurrentContext() {
        return currentContext;
    }

    CoreV1Api getApi() {
        return api;
    }
}
