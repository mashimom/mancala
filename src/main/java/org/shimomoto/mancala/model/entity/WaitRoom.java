package org.shimomoto.mancala.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class WaitRoom {
	@Id
	@Builder.Default
	UUID id = UUID.randomUUID();

	@Version
	@EqualsAndHashCode.Exclude
	@Builder.Default
	Long version = null;

	@NotBlank
	String roomName;

	@OneToMany(mappedBy = "waitRoom")
	@Builder.Default
	Set<User> signed = new HashSet<>();

	@CreatedDate
	@Builder.Default
	LocalDateTime createdOn = LocalDateTime.now();

	@LastModifiedDate
	@Builder.Default
	LocalDateTime lastUpdated = LocalDateTime.now();
}
