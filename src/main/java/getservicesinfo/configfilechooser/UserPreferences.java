package getservicesinfo.configfilechooser;

import java.io.File;
import java.util.prefs.Preferences;

public class UserPreferences {

    private static final String CONFIG_FILE_PREFERENCE_KEY = "kubernetesConfigFileLocation";
    private Preferences preferences = Preferences.userRoot();

    public UserPreferences() {
        try {
            preferences = Preferences.userRoot();
        } catch (SecurityException ignore){}
    }

    public String getConfigFileLocation() {
        String result = null;
        if (preferences != null) {
            result = preferences.get(CONFIG_FILE_PREFERENCE_KEY, null);
        }
        return result;
    }

    public void removeConfigFileLocation() {
        if (preferences != null) {
            preferences.remove(CONFIG_FILE_PREFERENCE_KEY);
        }
    }

    void saveConfigFileLocation(File configFile) {
        if (preferences != null) {
            preferences.put(CONFIG_FILE_PREFERENCE_KEY, configFile.getPath());
        }
    }
}
