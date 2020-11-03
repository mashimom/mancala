package org.shimomoto.mancala.controller

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.service.UserFacade
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
		1 * facade.create("name") >> Mock(User)
	}

	def "new user creation fails for illegal screen name"() {
		when:
		controller.createUser(sn)

		then:
		thrown ResponseStatusException

		where:
		_ | sn
		0 | ""
		1 | " "
		2 | "\t"
		3 | " \n"
	}
}
