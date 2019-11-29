package getservicesinfo.configparser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import getservicesinfo.Main;

import java.io.*;
import java.util.List;
import java.util.Map;

import static io.kubernetes.client.util.KubeConfig.*;

public class ConfigParser {

    private static Config config;
    private File configFile;

    private ConfigParser() {
    }

    private static class InstanceHolder {
        private static ConfigParser configParser = new ConfigParser();
    }

    public static ConfigParser getInstance() {
        return InstanceHolder.configParser;
    }

    public boolean readConfigFile() {
        config = null;
        boolean shouldValidate = true;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            config = objectMapper.readValue(configFile, Config.class);
        } catch (Throwable exception) {
            Main.showAlert("File is not a valid config file");
            shouldValidate = false;
        }
        return shouldValidate && validateConfigFile();
    }

    public void forgetConfigFile() {
        configFile = null;
        config = null;
    }

    private boolean validateConfigFile() {
        boolean isConfigValid = false;
        if (config != null) {
            List<Cluster> clusters = config.getClusters();
            if (clusters != null && !clusters.isEmpty()) {
                Map<String, String> details = clusters.get(0).getCluster();
                if (details != null && !details.isEmpty() && details.get("server") != null) {
                    isConfigValid = true;
                }
            }
        }
        if (!isConfigValid) {
            Main.showAlert("Config file is not a valid Kubernetes config.");
        }
        return isConfigValid;
    }

    public String getCurrentContext() {
        return config.getCurrentContext();
    }

    public List<Context> getContextList() {
        return config.getContexts();
    }

    public FileReader getConfigFileReader() {
        FileReader fileInputStream = null;
        try {
            fileInputStream = new FileReader(configFile);
        } catch (FileNotFoundException e) {
            Main.showAlert("Unable to read config file");
        }
        return fileInputStream;
    }

    public boolean setConfigFile(File configFile) {
        this.configFile = configFile;
        return readConfigFile();
    }

    private File findConfigInHomeDir() {
        final File homeDir = findHomeDir();
        if (homeDir != null) {
            configFile = new File(new File(homeDir, KUBEDIR), KUBECONFIG);
            if (configFile.exists()) {
                return configFile;
            }
        }
        Main.showAlert("Could not find ~/.kube/config");
        return null;
    }

    private File findHomeDir() {
        final String envHome = System.getenv(ENV_HOME);
        if (envHome != null && envHome.length() > 0) {
            final File config = new File(envHome);
            if (config.exists()) {
                return config;
            }
        }
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            String homeDrive = System.getenv("HOMEDRIVE");
            String homePath = System.getenv("HOMEPATH");
            if (homeDrive != null
                    && homeDrive.length() > 0
                    && homePath != null
                    && homePath.length() > 0) {
                File homeDir = new File(new File(homeDrive), homePath);
                if (homeDir.exists()) {
                    return homeDir;
                }
            }
            String userProfile = System.getenv("USERPROFILE");
            if (userProfile != null && userProfile.length() > 0) {
                File profileDir = new File(userProfile);
                if (profileDir.exists()) {
                    return profileDir;
                }
            }
        }
        return null;
    }
}
