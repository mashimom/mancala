package org.shimomoto.mancala.model.transfer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDto {
	String pid;
	String screenName;
	LocalDateTime createdOn;
	Long gameCount;
	Long winCount;
}
