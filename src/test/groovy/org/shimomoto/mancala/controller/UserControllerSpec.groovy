package org.shimomoto.mancala.controller

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.service.UserFacade
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification
import spock.lang.Subject

class UserControllerSpec extends Specification {
	UserFacade facade = Mock(UserFacade)

	@Subject
	UserController controller = new UserController(facade)

	def "new user creation works"() {
		when:
		def result = controller.createUser("name")

		then:
		result != null
		and: 'interactions'
		1 * facade.createUser("name") >> Optional.of(Mock(User))
		0 * _
	}

	def "new user creation fails for illegal screen name"() {
		when:
		controller.createUser("bogus")

		then:
		def ex = thrown(ResponseStatusException)
		ex.status == HttpStatus.NOT_ACCEPTABLE
		and: 'interactions'
		1 * facade.createUser("bogus") >> Optional.empty()
		0 * _
	}

	def "new user creation fails for empty screen name"() {
		when:
		controller.createUser("  \t")

		then:
		def ex = thrown(ResponseStatusException)
		ex.status == HttpStatus.NOT_ACCEPTABLE
		and: 'interactions'
		0 * facade.createUser(_)
		0 * _
	}
}
