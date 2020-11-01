package org.shimomoto.mancala.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class User {
	@Id
	@Builder.Default
	UUID id = UUID.randomUUID();
	@NotBlank
	String screenName;
	@Builder.Default
	LocalDateTime createdOn = LocalDateTime.now();
	@Builder.Default
	Long gameCount = 0L;
	@Builder.Default
	Long winCount = 0L;
}
