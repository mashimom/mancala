package org.shimomoto.mancala.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.entity.WaitRoom;
import org.shimomoto.mancala.model.util.PublicIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

import static java.text.MessageFormat.format;

/**
 * Should be used only by externally facing controllers, REST or otherwise.
 * This facade serves the purpose of hiding the complexity of the underlying (potentially multiple) services,
 * it also is the start of transactions.
 * It takes ids and values as parameters, return entities in most cases.
 * It may be considered a "public super service" that keeps "little services" private.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
@Service
public class UserFacade {

	@Autowired
	UserService service;

	@Autowired
	WaitRoomService waitRoomService;

	@Autowired
	GameService gameService;

	@NotNull
	private Optional<User> getUser(@Nullable final String pid) {
		return Optional.ofNullable(pid)
						.flatMap(PublicIdUtils::stringDecode)
						.flatMap(service::getPlayer);
	}

	/**
	 * This a an exception safe way to find a player by public id.
	 *
	 * @param pid public id of a player
	 * @return player when found or empty when not
	 */
	public Optional<User> getPlayer(final @Nullable String pid) {
		return getUser(pid);
	}

	/**
	 * This method is doing a bit more than it should, since async match making is not used.
	 * A user enters the waiting room if it is empty or is not occupied by themselves.
	 * In the case the waiting room is occupied by another user, a new game is created.
	 * All users should be able to see their own games by other means.
	 *
	 * @param pid public id of a valid user/player
	 * @return true if the user entered the waiting room or had a new game created
	 */
	public boolean waitRoom(final @Nullable String pid) {
		final User user = getUser(pid)
						.orElseThrow(() -> new EntityNotFoundException(format("Could not find a player with pid {0}", pid)));
		final WaitRoom room = waitRoomService.getFirstRoom();

		final Optional<User> signed = waitRoomService.getUserWaiting(room);
		if (signed.isEmpty()) {
			return waitRoomService.enter(room, user);
		}
		if (signed.get().equals(user)) {
			return false;
		}
		gameService.newGame(signed.get(), user);
		return true;
	}

	public Optional<User> createUser(final @Nullable String screenName) {
		return Optional.ofNullable(screenName)
						.filter(StringUtils::isNotBlank)
						.map(service::create);
	}

}
