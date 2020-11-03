package org.shimomoto.mancala.model.entity

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import spock.lang.Specification

import java.time.LocalDateTime

@JsonTest
class UserJsonSerializationIT extends Specification {

	@Autowired
	private ObjectMapper objectMapper;

	UUID id = UUID.fromString('1f0cdf01-527d-4345-96fe-3ff26735fbee')
	LocalDateTime ldt = LocalDateTime.of(2020, 11, 02, 14, 14, 14)
	String pid = 'HwzfAVJ9Q0WW_j_yZzX77g'

	def "id serialization works"() {
		given:
		User user = User.builder()
						.id(id)
						.screenName('kirk')
						.createdOn(ldt)
						.build()

		when:
		def result = objectMapper.writeValueAsString(user)

		then:
		result == """{"pid":"${pid}","screenName":"kirk","createdOn":"2020-11-02T14:14:14","gameCount":0,"winCount":0}"""
	}
}
