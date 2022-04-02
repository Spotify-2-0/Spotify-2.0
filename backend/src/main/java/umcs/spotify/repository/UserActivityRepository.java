package umcs.spotify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import umcs.spotify.entity.UserActivityEntry;

@Repository
public interface UserActivityRepository
        extends PagingAndSortingRepository<UserActivityEntry, Long> {

    Page<UserActivityEntry> findByUserId(long userId, Pageable pageable);
    Page<UserActivityEntry> findByUserEmail(String userId, Pageable pageable);

}
