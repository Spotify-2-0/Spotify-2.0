package umcs.spotify.services;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.stereotype.Service;
import umcs.spotify.entity.GeoLocation;

import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoService {


    public GeoLocation getLocationFromAddress(InetAddress address) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("geo/GeoLite2-City.mmdb")) {
            var dbReader = new DatabaseReader.Builder(stream).build();
            var city = dbReader.city(address);
            var country = city.getCountry();
            var latitude = city.getLocation().getLatitude();
            var longitude = city.getLocation().getLongitude();
            var continent = city.getContinent().getName();
            var radius = city.getLocation().getAccuracyRadius();

            var location = new GeoLocation();
            location.setCity(city.getCity().getName());
            location.setCountry(country.getName());
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setContinent(continent);
            location.setRadius(radius);
            return location;
        } catch (IOException | GeoIp2Exception e) {
            return null;
        }
    }
}
