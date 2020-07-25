package org.shimomoto.mancala.model.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

@UtilityClass
public class PublicIdSupplier {

	public String get() {
		return stringEncode(UUID.randomUUID());
	}

	private String stringEncode(final @NotNull UUID uuid) {
		final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.asLongBuffer()
				.put(uuid.getMostSignificantBits())
				.put(uuid.getLeastSignificantBits());
		return encodeBase64URLSafeString(buffer.array());
	}
}
