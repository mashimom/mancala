package org.shimomoto.mancala.model.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

@UtilityClass
public class PublicId {

	public String generate() {
		return stringEncode(UUID.randomUUID());
	}

	private String stringEncode(final @NotNull UUID uuid) {
		final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		final Base64 encoder = new Base64(true);
		return encoder.encodeAsString(buffer.array());
	}
}
