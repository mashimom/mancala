package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.shimomoto.mancala.model.entity.User;
import org.shimomoto.mancala.model.entity.WaitRoom;
import org.shimomoto.mancala.repository.WaitRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
class WaitRoomService {

	@Autowired
	WaitRoomRepository repository;

	private WaitRoom createDefault() {
		final WaitRoom room = WaitRoom.builder()
						.roomName("Default")
						.build();
		return repository.save(room);
	}

	@NotNull
	public WaitRoom getFirstRoom() {
		return StreamUtils.stream(repository.findAll())
						.findFirst()
						.orElseGet(this::createDefault);
	}

	public boolean enter(final @NotNull WaitRoom room, final @NotNull User user) {
		return room.getSigned().add(user);
	}

	public Stream<List<User>> getPlayerPairs(final @NotNull WaitRoom room) {
		return StreamUtils.windowed(room.getSigned().stream(), 2);
	}
}
