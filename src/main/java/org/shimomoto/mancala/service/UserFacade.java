package org.shimomoto.mancala.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.transfer.UserDto;
import org.shimomoto.mancala.model.util.PublicIdUtils;
import org.shimomoto.mancala.transformer.api.UserTransformer;
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

	/**
	 * This a an exception safe way to find a player.
	 *
	 * @param pid public id of a player
	 * @return player when found or empty when not
	 */
	public Optional<UserDto> getPlayer(final @Nullable String pid) {
		return Optional.ofNullable(pid)
						.flatMap(PublicIdUtils::stringDecode)
						.flatMap(service::getPlayer)
						.flatMap(UserTransformer::toDto);
	}

	public boolean waitRoom(final @Nullable String pid) {
		final User user = Optional.ofNullable(pid)
						.flatMap(PublicIdUtils::stringDecode)
						.flatMap(service::getPlayer)
						.orElseThrow(() -> new EntityNotFoundException(format("Could not find a player with pid {0}", pid)));
		return waitRoomService.enter(user);
	}
}
