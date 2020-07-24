package org.shimomoto.mancala.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.internal.RawBoard;
import org.shimomoto.mancala.model.util.PublicId;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Game {
	@Id
	@Builder.Default
	String id = PublicId.generate();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Transient
	RawBoard board; //TODO: figure out persistence later
	LocalDateTime gameStart;
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	Map<Player, String> playerNames;
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	Map<Player, Integer> winsByPlayer;

	public boolean isFinished() {
		return board.isEndOfGame();
	}

	public Optional<Player> getWinner() {
		return Optional.of(board)
				.filter(RawBoard::isEndOfGame)
				.flatMap(RawBoard::getWinner);
	}

	public void move(final Player player, final int position) {
		board.move(player, position);
	}

}
