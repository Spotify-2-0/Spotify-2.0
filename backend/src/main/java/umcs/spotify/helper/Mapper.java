package umcs.spotify.helper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.internal.util.Assert;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import umcs.spotify.dto.UserActivityEntryDto;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;
import umcs.spotify.entity.UserActivityEntry;

@Component
public class Mapper {

    private static final ModelMapper MAPPER = mapper();

    private static ModelMapper mapper() {
        var mapper = new ModelMapper();
        // maping here

        var builderConfiguration = mapper.getConfiguration().copy()
                .setDestinationNameTransformer(NameTransformers.builder())
                .setDestinationNamingConvention(NamingConventions.builder());

        mapper.createTypeMap(User.class, UserDto.UserDtoBuilder.class, builderConfiguration);
        mapper.createTypeMap(User.class, UserActivityEntryDto.UserActivityEntryDtoBuilder.class, builderConfiguration);
        return mapper;
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return MAPPER.map(source, destinationType);
    }

    public UserDto userToDto(User user) {
        return MAPPER.map(user, UserDto.UserDtoBuilder.class).build();
    }

    public Page<UserActivityEntryDto> mapUserActivityPageToDto(Page<UserActivityEntry> entities) {
        return entities.map(entity -> MAPPER.map(
            UserActivityEntry.class,
            UserActivityEntryDto.UserActivityEntryDtoBuilder.class
        ).build());
    }

    public <D, T> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass) {
        return entities.map(objectEntity -> MAPPER.map(objectEntity, dtoClass));
    }
}
