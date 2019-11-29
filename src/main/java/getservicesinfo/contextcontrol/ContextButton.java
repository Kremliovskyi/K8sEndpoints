package getservicesinfo.contextcontrol;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

class ContextButton extends Button {

    private boolean isClicked;
    private String contextName;

    ContextButton(String text) {
        super(text.toUpperCase());
        contextName = text;
        setAlignment(Pos.BOTTOM_CENTER);
    }

    boolean isClicked() {
        return isClicked;
    }

    void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    String getContextName() {
        return contextName;
    }
}
