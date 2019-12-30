## How to start:
To use the app you need to have Kubernetes configuration file in your machine. At the cold start application asks for this file location:

![cold start](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/choose-config.png)

After the path is submitted the app stores it in system user preferences for subsequent runs. 
The structure of the file is validated and data is started to fetch from Kubernetes with java client library - https://github.com/kubernetes-client/java

## Main screen:
Here is the main GUI that consists of the table with endpoint name taken from Helm and its version:

![main table](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/test-table.png)

It is possible to select rows from the table with the mouse left button, CTRL + A or Shift + Right Click shortcuts, but to copy it you need to make a right click of the mouse to open contextual menu and select "copy":

![copy main table](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/copy.png)

## Context switching:
The context buttons over the table are parsed from the config file and are used to fetch the data from corresponding cluster.

![context buttons](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/context-buttons.png)

The table content then will change to the endpoints in corresponding cluster. The blue color of the button highlights which context is currently selected:

![context selected](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/uat-context.png)

## Get All Pods:
You may see the table with all available pods by pressing "All Pods" button

## Refresh endpoints table content:
"Refresh" button will only refresh the content of the table of currently selected context.

## Pods information:
By selecting one endpoint and clicking "Info" button the new window with pods that are in this endpoint will open:
![pods info](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/pod-screen.png)

It is possible to copy row that will consist of "Pod name" and "IP" columns in the same way as copying endpoint rows from the main screen.

## Restart endpoint:
To restart endpoint you need to select it from the table and click "Restart" button below the table. After you confirm the action in the dialogue all Kubernetes pods will be deleted and Kubernetes will recreate them approximately in a minute or two.

## Search Logs:
It is possible to get the logs from the pod. For this you need to select a pod and click "Search Logs" button:
![search logs](https://github.com/Kremliovskyi/K8sEndpoints/blob/master/src/test/resources/log-options.png)

###### Date Picker:
It is possible to filter logs by date using date picker. Selecting some day from it will allow to get the logs from the selected day 12 A.M. till the current time. It will NOT be the logs for one particular day.

###### Tail Lines:
You may insert only numbers in this field limiting log records count to this number. It will be always only the latest logs from the current day. It is not possible to combine this field with date picker - there will be no error but logs will be shown for current day.

###### Filter by text:
If you enable "Search for specific log" checkbox the below options to filter logs by text will become available. Two options "Equal" and "Contains" are self explanatory. Each log record will be filtered to match the text you provide - either full equality or contains respectively. This operation may be combined with other options. One tip to remember - if you combine tail lines and this option then only the latest records first filtered by tail lines will be checked for the condition from this text searching option.
The log will be opened in the app that is default to .log files in the system. The file with logs is stored in the user temp folder and its name would be k8s-pod.log.
