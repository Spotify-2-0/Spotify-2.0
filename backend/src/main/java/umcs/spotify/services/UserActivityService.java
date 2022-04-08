package umcs.spotify.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import umcs.spotify.contract.UserActivityRequest;
import umcs.spotify.dto.UserActivityEntryDto;
import umcs.spotify.entity.User;
import umcs.spotify.entity.UserActivityEntry;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.FormValidatorHelper;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.UserActivityRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UserActivityService {

    private final UserActivityRepository activityRepository;
    private final GeoService geoService;
    private final Mapper mapper;

    public UserActivityService(
        UserActivityRepository activityRepository,
        GeoService geoService,
        Mapper mapper
    ) {
        this.activityRepository = activityRepository;
        this.geoService = geoService;
        this.mapper = mapper;
    }

    public Page<UserActivityEntryDto> getUserActivity(UserActivityRequest request, Errors errors) {
        if (errors.hasFieldErrors()) {
            throw new RestException(BAD_REQUEST, FormValidatorHelper.returnFormattedErrors(errors));
        }

        var email = ContextUserAccessor.getCurrentUserEmail();
        var page = PageRequest.of(request.getPage(), request.getPageSize());
        var activities = activityRepository.findByUserEmail(email, page);
        return mapper.mapEntityPageIntoDtoPage(activities, UserActivityEntryDto.class);
    }

    @Async
    public void addActivity(User user, String activity, String ip) {
        var inet = parseAddress(ip);
        var userActivity = new UserActivityEntry();
        userActivity.setUser(user);
        userActivity.setIp(ip);
        userActivity.setOccurrenceTimestamp(System.currentTimeMillis());
        userActivity.setActivity(activity);
        if (inet != null) {
            var geoLocation = geoService.getLocationFromAddress(inet);
            if (geoLocation != null) {
                userActivity.setLocation(geoLocation);
            }
        }

        activityRepository.save(userActivity);
    }

    private InetAddress parseAddress(String ip) {
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
