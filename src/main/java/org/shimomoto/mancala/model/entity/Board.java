package org.shimomoto.mancala.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.PlayerRole;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder({"currentPlayer", "turnCount", "pits"})
@Data
@Embeddable
public class Board {
	@Builder.Default
	int turnCount = 1;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	PlayerRole currentPlayer = PlayerRole.ONE;

	//saving as binary on table for now
	@Builder.Default
	int[] pits = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
}
