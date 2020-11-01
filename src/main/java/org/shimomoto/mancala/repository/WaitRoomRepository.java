package org.shimomoto.mancala.repository;

import org.shimomoto.mancala.model.entity.WaitRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WaitRoomRepository extends CrudRepository<WaitRoom, UUID> {
}
