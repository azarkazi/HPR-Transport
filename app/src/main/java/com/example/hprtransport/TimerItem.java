package com.example.hprtransport;

public class TimerItem {
    private String label;
    private long valueInSeconds;
    private String key;

    public TimerItem(String label, long valueInSeconds, String key) {
        this.label = label;
        this.valueInSeconds = valueInSeconds;
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public long getValueInSeconds() {
        return valueInSeconds;
    }

    public String getKey() {
        return key;
    }

    public void setValueInSeconds(long valueInSeconds) {
        this.valueInSeconds = valueInSeconds;
    }

    public String getFormattedValue() {
        if (valueInSeconds >= 3600) {
            long hours = valueInSeconds / 3600;
            return hours + (hours > 1 ? " Hours" : " Hour");
        } else if (valueInSeconds >= 60) {
            long minutes = valueInSeconds / 60;
            return minutes + (minutes > 1 ? " Minutes" : " Minute");
        } else {
            return valueInSeconds + " Seconds";
        }
    }
}
