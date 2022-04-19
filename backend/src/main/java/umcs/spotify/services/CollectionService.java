package umcs.spotify.services;

import org.springframework.stereotype.Service;
import umcs.spotify.contract.CollectionRequest;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.entity.Collection;
import umcs.spotify.entity.CollectionType;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.CollectionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final Mapper mapper;

    public CollectionService(CollectionRepository collectionRepository, Mapper mapper) {
        this.collectionRepository = collectionRepository;
        this.mapper = mapper;
    }

    public List<CollectionDto> getCollections() {
        List<Collection> collections = collectionRepository.findAll();
        return collections.stream()
                .map(mapper::collectionToDto)
                .collect(Collectors.toList());
    }

    public CollectionDto getCollectionById(Long id) {
        Collection collection = collectionRepository.getById(id);

        return mapper.map(collection, CollectionDto.class);
    }

    public CollectionDto addCollection(CollectionRequest collectionRequest) {
        Collection collectionToSave = new Collection();
        collectionToSave.setName(collectionRequest.getName());
        collectionToSave.setType(collectionRequest.getType());
        collectionToSave.setAvatarPath("nothing");
        collectionToSave.setTracks(List.of());
        collectionToSave.setUsers(List.of());

        Collection savedCollection = collectionRepository.save(collectionToSave);
        return mapper.map(savedCollection, CollectionDto.class);
    }


    public void updateCollection(Long id, String name, CollectionType type, String avatarPath) {
        Collection collection = collectionRepository.getById(id);
        collection.setName(name);
        collection.setType(type);
        collection.setAvatarPath(avatarPath);
        collectionRepository.save(collection);
    }
  
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }
}
