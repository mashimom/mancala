package org.shimomoto.mancala.model.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class PublicIdUtils {
	public static String stringEncode(final @NotNull UUID uuid) {
		final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.asLongBuffer()
						.put(uuid.getMostSignificantBits())
						.put(uuid.getLeastSignificantBits());
		return Base64.encodeBase64URLSafeString(buffer.array());
	}

	public static Optional<UUID> stringDecode(final @Nullable String pid) {
		return Optional.ofNullable(pid)
						.filter(StringUtils::isNotBlank)
						.filter(s -> s.length() == 22)
						.map(Base64::decodeBase64)
						.map(ByteBuffer::wrap)
						.map(bb -> new UUID(bb.getLong(), bb.getLong()));
	}
}
