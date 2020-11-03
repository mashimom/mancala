package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.entity.WaitRoom;
import org.shimomoto.mancala.repository.WaitRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
class WaitRoomService {

	@Autowired
	WaitRoomRepository repository;

	private WaitRoom createDefault() {
		final WaitRoom room = WaitRoom.builder()
						.build();
		return repository.save(room);
	}

	@NotNull
	public WaitRoom getFirstRoom() {
		//hacking a single entity repo, the best solution would be to queue users and then consume from queue to create games
		return StreamUtils.stream(repository.findAll())
						.findFirst()
						.orElseGet(this::createDefault);
	}

	public Optional<User> getUserWaiting(@Nullable final WaitRoom room) {
		return Optional.ofNullable(room)
						.map(WaitRoom::getSigned);
	}

	public boolean enter(final @NotNull WaitRoom room, final @NotNull User user) {
		final Optional<User> signed = Optional.of(room)
						.map(WaitRoom::getSigned);
		if (signed.isPresent()) {
			return false;
		}
		room.setSigned(user);
		return true;
	}

	public Optional<User> remove(@NotNull final WaitRoom room) {
		final Optional<User> signed = Optional.of(room)
						.map(WaitRoom::getSigned);
		if (signed.isPresent()) {
			room.setSigned(null);
		}
		return signed;
	}
}
