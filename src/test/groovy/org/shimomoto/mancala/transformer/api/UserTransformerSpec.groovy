package org.shimomoto.mancala.transformer.api

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.model.util.PublicIdUtils
import spock.lang.Specification

class UserTransformerSpec extends Specification {

	def "toDto when null is provided"() {
		expect:
		UserTransformer.toDto(null).isEmpty()
	}

	def "toDto when proper user is provided"() {
		given:
		User u = User.builder()
						.screenName("Goku")
						.build()

		when:
		def result = UserTransformer.toDto(u)

		then:
		result.isPresent()
		result.get().pid == PublicIdUtils.stringEncode(u.id)
		result.get().screenName == "Goku"
		result.get().createdOn == u.createdOn
		result.get().gameCount == u.gameCount
		result.get().winCount == u.winCount
	}
}
