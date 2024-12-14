package common.models;

import common.exceptions.WrongArgumentsException;

import java.time.ZoneId;

public class TimeZone {

    ZoneId timeZoneId;

    public TimeZone() {
        timeZoneId = ZoneId.systemDefault();
    }

    public TimeZone(ZoneId timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String format(String timeZone) throws WrongArgumentsException {
        if (!timeZone.contains("/")) {
            throw new WrongArgumentsException();
        }

        String[] parts = timeZone.split("/");
        return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase()
                + "/" + parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
    }
}
