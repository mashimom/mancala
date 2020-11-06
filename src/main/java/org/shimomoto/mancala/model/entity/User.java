package org.shimomoto.mancala.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.transfer.PublicIdSerializer;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonRootName(value = "Player")
@JsonPropertyOrder({"pid", "screenName", "createdOn", "gameCount", "winCount"})
@Data
@Entity
public class User {

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

	@NotBlank
	String screenName;

	@CreatedDate
	@Builder.Default
	LocalDateTime createdOn = LocalDateTime.now();

	@Builder.Default
	Long gameCount = 0L;

	@Builder.Default
	Long winCount = 0L;
}
