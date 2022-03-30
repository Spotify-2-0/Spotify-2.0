package umcs.spotify.helper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;

public class Mapper {

    public static ModelMapper mapper() {
        var mapper = new ModelMapper();
        //mapper.createTypeMap(User.class, UserDto.class);

        return mapper;
    }
}
