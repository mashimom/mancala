package org.shimomoto.mancala.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
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

	public User create(final @NotNull String screenName) {
		final User user = User.builder()
						.screenName(screenName)
						.build();
		repo.save(user);
		return user;
	}

	public Optional<User> getPlayer(final UUID id) {
		return repo.findById(id);
	}

	public void scoreWin(final @NotNull User user) {
		user.setGameCount(user.getGameCount() + 1);
		user.setWinCount(user.getWinCount() + 1);
	}

	public void scoreLoose(final @NotNull User user) {
		user.setGameCount(user.getGameCount() + 1);
	}

	public void scoreDraw(final @NotNull User user) {
		scoreWin(user);
	}
}
