package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.repository.UserRepository
import spock.lang.Specification
import spock.lang.Subject

class UserServiceSpec extends Specification {
	UserRepository repository = Mock(UserRepository)

	@Subject
	UserService service = new UserService(repository)

	def "getPlayer works"() {
		given:
		UUID id = UUID.fromString('34b08922-8b1d-4378-90cb-18857e012bbc')
		User u = Mock(User)

		when:
		def result = service.getPlayer(id)

		then:
		result.isPresent()
		result.get() == u
		and: 'interactions'
		1 * repository.findById(id) >> Optional.of(u)
		0 * _
	}
}