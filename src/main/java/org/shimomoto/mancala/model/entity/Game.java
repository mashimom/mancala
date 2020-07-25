package org.shimomoto.mancala.model.entity;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.util.PublicIdSupplier;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyEnumerated;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Game {
	@Id
	@Builder.Default
	String id = PublicIdSupplier.get();
	@Builder.Default
	@Embedded
	Board board = Board.builder().build();
	@Builder.Default
	LocalDateTime gameStart = LocalDateTime.now();
	@Builder.Default
	boolean endOfGame = false;
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	@Builder.Default
	Map<Player, String> playerNames =
			MapStream.of(
					Player.ONE, "Player 1",
					Player.TWO, "Player 2")
					.collect();
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	@Builder.Default
	Map<Player, Integer> winsByPlayer =
			MapStream.of(
					Player.ONE, 0,
					Player.TWO, 0)
					.collect();
}
