package umcs.spotify.helper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umcs.spotify.dto.*;
import umcs.spotify.entity.*;

import java.time.Duration;
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

        var builderConfiguration = mapper.getConfiguration().copy()
                .setDestinationNameTransformer(NameTransformers.builder())
                .setDestinationNamingConvention(NamingConventions.builder());

        mapper.createTypeMap(AudioTrack.class, AudioTrackDto.class)
                .addMappings(map -> map.using(durationConverter).map(AudioTrack::getDuration, AudioTrackDto::setDuration));

        mapper.createTypeMap(User.class, UserDto.UserDtoBuilder.class, builderConfiguration);
        mapper.createTypeMap(UserActivityEntry.class, UserActivityEntryDto.UserActivityEntryDtoBuilder.class, builderConfiguration);
        mapper.createTypeMap(Collection.class, CollectionDto.CollectionDtoBuilder.class, builderConfiguration)
                .addMappings(
                    mapping -> mapping.using(new UsersListConverter()).map(Collection::getUsers, CollectionDto.CollectionDtoBuilder::users)
                )
                .addMappings(
                    mapping -> mapping.using(new TracksListConverter()).map(Collection::getTracks, CollectionDto.CollectionDtoBuilder::tracks)
                );

        return mapper;
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return MAPPER.map(source, destinationType);
    }

    public UserDto userToDto(User user) {
        return MAPPER.map(user, UserDto.UserDtoBuilder.class).build();
    }

    public CollectionDto collectionToDto(Collection collection) {
        return MAPPER.map(collection, CollectionDto.CollectionDtoBuilder.class).build();
    }

    public Page<UserActivityEntryDto> mapUserActivityPageToDto(Page<UserActivityEntry> entities) {
        return entities.map(entity -> MAPPER.map(entity, UserActivityEntryDto.UserActivityEntryDtoBuilder.class).build());
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
                return new AudioTrackDto(
                        audioTrack.getId(),
                        audioTrack.getName(),
                        audioTrack.getDuration().toMillis(),
                        audioTrack.getViews(),
                        audioTrack.getPublishedDate(),
                        genres.stream().map(genre -> new GenreDto(genre.getId(), genre.getName())).collect(Collectors.toList())
                );
            }).collect(Collectors.toList());
        }
    }

}
