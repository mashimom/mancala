package org.shimomoto.mancala.model.entity;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.PlayerRole;
import org.shimomoto.mancala.model.util.PublicIdSupplier;

import javax.persistence.*;
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
	Map<PlayerRole, String> playerNames =
					MapStream.of(
									PlayerRole.ONE, "Player 1",
									PlayerRole.TWO, "Player 2")
									.collect();
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	@Builder.Default
	Map<PlayerRole, Integer> winsByPlayer =
					MapStream.of(
									PlayerRole.ONE, 0,
									PlayerRole.TWO, 0)
									.collect();
}
