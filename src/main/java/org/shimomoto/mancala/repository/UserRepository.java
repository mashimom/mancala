package org.shimomoto.mancala.repository;

import org.shimomoto.mancala.model.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
}
