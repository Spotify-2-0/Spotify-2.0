package umcs.spotify.helper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umcs.spotify.dto.AddTrackDataToSelectDto;
import umcs.spotify.dto.*;
import umcs.spotify.entity.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {

    private static final ModelMapper MAPPER = mapper();

    private static ModelMapper mapper() {
        var mapper = new ModelMapper();
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        Converter<Duration, Long> durationConverter = ctx -> ctx.getSource().toMillis();
        Converter<LocalDateTime, Long> dateToMillisConverter = ctx -> ctx.getSource()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        var builderConfiguration = mapper.getConfiguration().copy()
                .setDestinationNameTransformer(NameTransformers.builder())
                .setDestinationNamingConvention(NamingConventions.builder());

        mapper.createTypeMap(AudioTrack.class, AudioTrackDto.class)
                .addMappings(map -> map.using(durationConverter).map(AudioTrack::getDuration, AudioTrackDto::setDuration));

        mapper.createTypeMap(User.class, UserDto.UserDtoBuilder.class, builderConfiguration);
        mapper.createTypeMap(UserActivityEntry.class, UserActivityEntryDto.class)
                .addMappings(map -> map.using(dateToMillisConverter)
                                .map(UserActivityEntry::getOccurrenceDate, UserActivityEntryDto::setOccurrenceDate));
        mapper.createTypeMap(Collection.class, CollectionDto.class)
                .addMappings(map -> map.using(durationConverter).map(Collection::getDuration, CollectionDto::setDuration))
                .addMappings(map -> map.map(Collection::getViews, CollectionDto::setViews))
                .addMappings(
                    mapping -> mapping.using(new UsersListConverter()).map(Collection::getUsers, CollectionDto::setUsers)
                )
                .addMappings(
                    mapping -> mapping.using(new TracksListConverter()).map(Collection::getTracks, CollectionDto::setTracks)
                );
        mapper.createTypeMap(Collection.class, AddTrackDataToSelectDto.class)
                .addMappings(map -> map.map(Collection::getId, AddTrackDataToSelectDto::setId))
                .addMappings(map -> map.map(Collection::getName, AddTrackDataToSelectDto::setName));

        return mapper;
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return MAPPER.map(source, destinationType);
    }

    public UserDto userToDto(User user) {
        return MAPPER.map(user, UserDto.UserDtoBuilder.class).build();
    }

    public CollectionDto collectionToDto(Collection collection) {
        return MAPPER.map(collection, CollectionDto.class);
    }

    public AddTrackDataToSelectDto collectionToAddTrackUserResponse(final Collection collection) {
        return MAPPER.map(collection, AddTrackDataToSelectDto.class);
    }

    public Page<UserActivityEntryDto> mapUserActivityPageToDto(Page<UserActivityEntry> entities) {
        return entities.map(entity -> MAPPER.map(entity, UserActivityEntryDto.class));
    }

    public <D, T> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass) {
        return entities.map(objectEntity -> MAPPER.map(objectEntity, dtoClass));
    }

    public static class UsersListConverter extends AbstractConverter<List<User>, List<UserDto>> {
        @Override
        protected List<UserDto> convert(List<User> source) {
            return source.stream().map(user ->
                new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.isEmailConfirmed()
                )
            ).collect(Collectors.toList());
        }
    }

    public static class TracksListConverter extends AbstractConverter<List<AudioTrack>, List<AudioTrackDto>> {
        @Override
        protected List<AudioTrackDto> convert(List<AudioTrack> source) {
            return source.stream().map(audioTrack -> {
                List<Genre> genres = audioTrack.getGenres();
                List<User> artists = audioTrack.getArtists();
                return new AudioTrackDto(
                        audioTrack.getId(),
                        audioTrack.getName(),
                        audioTrack.getDuration().toMillis(),
                        audioTrack.getViews(),
                        audioTrack.getPublishedDate(),
                        genres.stream().map(genre -> new GenreDto(genre.getId(), genre.getName())).collect(Collectors.toList()),
                        artists.stream().map(user -> new UserDto(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getDisplayName(),
                                user.getEmail(),
                                user.isEmailConfirmed()
                        )).collect(Collectors.toList())
                );
            }).collect(Collectors.toList());
        }
    }

}
