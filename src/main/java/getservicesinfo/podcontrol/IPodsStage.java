package getservicesinfo.podcontrol;

import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.PodInfo;

public interface IPodsStage {

    Kube getKube();

    PodInfo getSelectedPod();
}
