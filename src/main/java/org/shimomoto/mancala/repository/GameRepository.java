package org.shimomoto.mancala.repository;

import org.shimomoto.mancala.model.entity.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String> {
}
