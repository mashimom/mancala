package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.repository.UserRepository
import spock.lang.Specification
import spock.lang.Subject

class UserServiceSpec extends Specification {
	UserRepository repository = Mock(UserRepository)

	@Subject
	UserService service = new UserService(repository)

	def "create works"() {
		when:
		def result = service.create("Goku")

		then:
		result != null
		result.screenName == "Goku"
		and: 'interactions'
		1 * repository.save(_) >> { User u -> u }
		0 * _
	}

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

	def "scoreWin works"() {
		given:
		User u = User.builder()
						.screenName("Gohan")
						.build()

		when:
		service.scoreWin(u)

		then:
		u.gameCount == 1
		u.winCount == 1
	}

	def "scoreLoose works"() {
		given:
		User u = User.builder()
						.screenName("Gohan")
						.build()

		when:
		service.scoreLoose(u)

		then:
		u.gameCount == 1
		u.winCount == 0
	}

	def "scoreDraw works"() {
		given:
		User u = User.builder()
						.screenName("Gohan")
						.build()

		when:
		service.scoreDraw(u)

		then:
		u.gameCount == 1
		u.winCount == 1
	}
}
