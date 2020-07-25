package org.shimomoto.mancala.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.Player;

import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Embeddable
public class Board {
	@Builder.Default
	int turnCount = 1;
	@Builder.Default
	Player currentPlayer = Player.ONE;
	@Builder.Default
	int[] pits = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
}
