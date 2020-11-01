package org.shimomoto.mancala.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
class UserService {

	@Autowired
	UserRepository repo;

	public Optional<User> getPlayer(final UUID id) {
		return repo.findById(id);
	}
}
