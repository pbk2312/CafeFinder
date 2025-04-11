package CafeFinder.cafe.util;

import CafeFinder.cafe.exception.InvalidLocationFormatException;

public class LocationParser {

    public LocationParser() {
    }


    public static Double[] parse(String location) {
        if (location == null || !location.contains(",")) {
            throw new InvalidLocationFormatException();
        }
        String[] parts = location.split(",");
        try {
            Double latitude = Double.parseDouble(parts[0].trim());
            Double longitude = Double.parseDouble(parts[1].trim());
            return new Double[]{latitude, longitude};
        } catch (NumberFormatException e) {
            throw new InvalidLocationFormatException();
        }
    }

}
