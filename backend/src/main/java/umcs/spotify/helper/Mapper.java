package umcs.spotify.helper;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;

@Component
public class Mapper {

    private static final ModelMapper MAPPER = mapper();

    private static ModelMapper mapper() {
        var mapper = new ModelMapper();
        // maping here
        return mapper;
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return MAPPER.map(source, destinationType);
    }

    public <D, T> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass) {
        return entities.map(objectEntity -> MAPPER.map(objectEntity, dtoClass));
    }
}
