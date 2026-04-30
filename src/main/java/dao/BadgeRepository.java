package dao;

import Entites.Badge;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO pour les entités de type Badge
 */

public interface BadgeRepository extends CrudRepository<Badge, Long> {
}
