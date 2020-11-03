package org.shimomoto.mancala.model.entity;

import com.codepoetics.protonpack.maps.MapStream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.shimomoto.mancala.model.domain.PlayerRole;
import org.shimomoto.mancala.model.transfer.PublicIdSerializer;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Game {

	@JsonProperty("pid")
	@JsonSerialize(using = PublicIdSerializer.class)
	@Id
	@Builder.Default
	UUID id = UUID.randomUUID();

	@JsonIgnore
	@Version
	@EqualsAndHashCode.Exclude
	@Builder.Default
	Long version = null;

	@Builder.Default
	@Embedded
	Board board = Board.builder().build();

	@CreatedDate
	@Builder.Default
	LocalDateTime gameStart = LocalDateTime.now();

	@Builder.Default
	boolean endOfGame = false;

	@NotNull
	@OneToOne(optional = false)
	User playerOne;

	@NotNull
	@OneToOne(optional = false)
	User playerTwo;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	@Builder.Default
	Map<PlayerRole, Integer> winsByPlayer =
					MapStream.of(
									PlayerRole.ONE, 0,
									PlayerRole.TWO, 0)
									.collect();
}
