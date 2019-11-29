package getservicesinfo.kubernetes;

import getservicesinfo.models.PodInfo;

class LogRequest {
    private boolean isEqual;
    private String log;
    private PodInfo podInfo;
    private Integer sinceSeconds;
    private Integer tailLines;

    private LogRequest() {
    }

    boolean isEqual() {
        return isEqual;
    }

    String getLog() {
        return log;
    }

    PodInfo getPodInfo() {
        return podInfo;
    }

    Integer getSinceSeconds() {
        return sinceSeconds;
    }

    Integer getTailLines() {
        return tailLines;
    }

    static class Builder {
        private Boolean isEqual;
        private String log;
        private PodInfo podInfo;
        private Integer sinceSeconds;
        private Integer tailLines;

        LogRequest build() {
            LogRequest logRequest = new LogRequest();
            logRequest.isEqual = isEqual;
            logRequest.log = log;
            logRequest.podInfo = copyPodInfo(podInfo);
            logRequest.sinceSeconds = sinceSeconds;
            logRequest.tailLines = tailLines;
            return logRequest;
        }

        Builder setEqual(Boolean equal) {
            isEqual = equal;
            return this;
        }

        Builder setLog(String log) {
            this.log = log;
            return this;
        }

        Builder setPodInfo(PodInfo podInfo) {
            this.podInfo = podInfo;
            return this;
        }

        Builder setSinceSeconds(Integer sinceSeconds) {
            this.sinceSeconds = sinceSeconds;
            return this;
        }

        Builder setTailLines(Integer tailLines) {
            this.tailLines = tailLines;
            return this;
        }

        private PodInfo copyPodInfo(PodInfo podInfo) {
            return new PodInfo()
                    .setName(podInfo.getName())
                    .setSelectedContainer(podInfo.getSelectedContainer())
                    .setPodCreationTimestamp(podInfo.getPodCreationTimestamp())
                    .setPodNameSpace(podInfo.getPodNameSpace())
                    .setIp(podInfo.getIp())
                    .setPhase(podInfo.getPhase());
        }
    }
}
