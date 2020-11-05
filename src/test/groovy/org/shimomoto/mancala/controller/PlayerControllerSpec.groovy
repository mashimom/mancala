package org.shimomoto.mancala.controller

import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.service.GameFacade
import org.shimomoto.mancala.service.UserFacade
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException
import java.util.stream.Collectors

class PlayerControllerSpec extends Specification {
	GameFacade gameFacade = Mock(GameFacade)

	UserFacade facade = Mock(UserFacade)

	@Subject
	PlayerController controller = new PlayerController(facade, gameFacade)

	UUID id = UUID.fromString('3b90eec8-21cf-4d38-8de7-468c470c43b0')
	String pid = 'O5DuyCHPTTiN50aMRwxDsA'

	def "fetching existing player works"() {
		given:
		User u = Mock(User)
		when:
		def result = controller.fetch(pid)
		println id
		println pid

		then:
		result == u
		and: 'interactions'
		1 * facade.getPlayer(pid) >> Optional.of(u)
		0 * _
	}

	def "fetching non-existing player fails"() {
		when:
		controller.fetch("bogus")

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.getPlayer(_) >> Optional.empty()
		0 * _
	}

	def "player goes to wait room"() {
		when:
		controller.waitRoom(pid)

		then:
		notThrown ResponseStatusException
		and: 'interactions'
		1 * facade.waitRoom(pid) >> true
		0 * _
	}

	def "nonexistent player cannot go to wait room"() {
		when:
		controller.waitRoom("bogus")

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.waitRoom(_) >> { throw new EntityNotFoundException("some message") }
		0 * _
	}

	def "player cannot go to wait room twice"() {
		when:
		controller.waitRoom(pid)

		then:
		def ex = thrown(ResponseStatusException)
		ex.status == HttpStatus.NOT_ACCEPTABLE
		1 * facade.waitRoom(pid) >> false
		0 * _
	}

	def "listGames works"() {
		given:
		def games = (1..2).collect {
			Game.builder()
							.playerOne(Mock(User))
							.playerTwo(Mock(User))
							.build()
		}.reverse()

		when:
		def result = controller.listGames('someid').collect(Collectors.toList())

		then:
		result.toSet() == games.toSet()
		result == games.reverse()
		and: 'interactions'
		1 * gameFacade.getAllByUser('someid') >> games.stream()
	}
}
