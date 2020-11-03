package org.shimomoto.mancala.repository;

import org.shimomoto.mancala.model.entity.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GameRepository extends CrudRepository<Game, UUID> {
}
