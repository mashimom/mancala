package org.shimomoto.mancala.transformer.api;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.transfer.UserDto;
import org.shimomoto.mancala.model.util.PublicIdUtils;

import java.util.Optional;

@UtilityClass
public class UserTransformer {

	public static Optional<UserDto> toDto(@Nullable final User user) {
		return Optional.ofNullable(user)
						.map(u -> UserDto.builder()
										.pid(PublicIdUtils.stringEncode(u.getId()))
										.screenName(u.getScreenName())
										.createdOn(u.getCreatedOn())
										.gameCount(u.getGameCount())
										.winCount(u.getWinCount())
										.build());
	}
}
