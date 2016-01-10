package com.scholarscore.models.notification;

import java.util.Objects;

/**
 * Created by markroper on 1/9/16.
 */
public class NotificationWindow {
    private Duration window;
    //If true, the notification trigger value represents percent change from beggining of window to end.
    //If false or null, the notification trigger value is a magnitude of the change in absolute terms.
    private Boolean triggerIsPercent;

    public Duration getWindow() {
        return window;
    }

    public void setWindow(Duration window) {
        this.window = window;
    }

    public Boolean getTriggerIsPercent() {
        return triggerIsPercent;
    }

    public void setTriggerIsPercent(Boolean triggerIsPercent) {
        this.triggerIsPercent = triggerIsPercent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(window, triggerIsPercent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NotificationWindow other = (NotificationWindow) obj;
        return Objects.equals(this.window, other.window)
                && Objects.equals(this.triggerIsPercent, other.triggerIsPercent);
    }
}
