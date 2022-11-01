package kitchenpos.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface MenuRepository extends Repository<Menu, Long> {

    Menu save(Menu menu);

    Optional<Menu> findById(Long id);

    List<Menu> findAll();

    long countByIdIn(List<Long> ids);
}
