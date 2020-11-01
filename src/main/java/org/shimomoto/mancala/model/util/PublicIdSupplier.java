package org.shimomoto.mancala.model.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

import static org.shimomoto.mancala.model.util.PublicIdUtils.stringEncode;

@UtilityClass
public class PublicIdSupplier {

	public String get() {
		return stringEncode(UUID.randomUUID());
	}

}
