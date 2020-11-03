package org.shimomoto.mancala.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.shimomoto.mancala.model.transfer.PublicIdSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class WaitRoom {

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

	@Nullable
	@OneToOne
	User signed;

	@CreatedDate
	@Builder.Default
	LocalDateTime createdOn = LocalDateTime.now();

	@LastModifiedDate
	@Builder.Default
	LocalDateTime lastUpdated = LocalDateTime.now();
}
