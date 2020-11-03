package org.shimomoto.mancala.model.transfer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

import static org.shimomoto.mancala.model.util.PublicIdUtils.stringEncode;

@Slf4j
public class PublicIdSerializer extends JsonSerializer<UUID> {

	@Override
	public void serialize(final UUID id, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
					throws IOException, JsonProcessingException {
		final String pid = stringEncode(id);
		log.info("Serialized {} to {}", id, pid);
		jsonGenerator.writeString(pid);
	}
}
