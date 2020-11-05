package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.model.entity.WaitRoom
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.persistence.EntityNotFoundException

class UserFacadeSpec extends Specification {

	UserService service = Mock(UserService)
	WaitRoomService waitRoomService = Mock(WaitRoomService)
	GameService gameService = Mock(GameService)

	@Subject
	UserFacade facade = new UserFacade(service, waitRoomService, gameService)

	UUID id = UUID.fromString('311e68fd-0b4f-49b1-b28f-0f409490dec4')
	String pid = 'MR5o_QtPSbGyjw9AlJDexA'

	def "getPlayer work for valid pid"() {
		given:
		User u = User.builder()
						.screenName("somename")
						.build()

		when:
		def result = facade.getPlayer(pid)

		then:
		result.isPresent()
		result.get().screenName == u.screenName
		and: 'interactions'
		1 * service.getPlayer(id) >> Optional.of(u)
		0 * _
	}

	@Unroll
	def "pid #badId cannot be used on wait room"() {
		when:
		facade.waitRoom(badId)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		0 * _

		where:
		_ | badId
		0 | "bogus"
		1 | ""
		2 | null
		3 | "123456789012345678901234567890"
	}

	def "pid for non-existent user will not be accepted to enter waiting room"() {
		when:
		facade.waitRoom(pid)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getPlayer(id) >> Optional.empty() //not found
		0 * _
	}

	def "user enters wait room"() {
		given:
		User u = Mock(User)
		WaitRoom r = Mock(WaitRoom)

		when:
		def result = facade.waitRoom(pid)

		then:
		result
		and: 'interactions'
		1 * service.getPlayer(id) >> Optional.of(u)
		1 * waitRoomService.getFirstRoom() >> r
		1 * waitRoomService.getUserWaiting(r) >> Optional.empty()
		1 * waitRoomService.enter(r, u) >> true
		and: 'do not createUser a new game'
		0 * gameService.newGame(_, _)
		0 * _
	}

	def "user already in wait room"() {
		given:
		User u = Mock(User)
		WaitRoom r = Mock(WaitRoom)

		when:
		def result = facade.waitRoom(pid)

		then:
		!result
		and: 'interactions'
		1 * service.getPlayer(id) >> Optional.of(u)
		1 * waitRoomService.getFirstRoom() >> r
		1 * waitRoomService.getUserWaiting(r) >> Optional.of(u)
		and: 'explicitly not enter the room or createUser game'
		0 * waitRoomService.enter(r, u)
		0 * gameService.newGame(_, _)
		0 * _
	}

	def "another user in waiting room"() {
		given: 'two mock users and a mock waitroom'
		User u1 = Mock(User)
		User u2 = Mock(User)
		WaitRoom r = Mock(WaitRoom)

		when: 'a valid pid goes to waitroom'
		def result = facade.waitRoom(pid)

		then:
		result
		and: 'interactions'
		1 * service.getPlayer(id) >> Optional.of(u2)
		1 * waitRoomService.getFirstRoom() >> r
		1 * waitRoomService.getUserWaiting(r) >> Optional.of(u1)
		and: 'explicitly not enter the room but createUser game'
		0 * waitRoomService.enter(r, u2) >> false
		1 * gameService.newGame(u1, u2) >> Mock(Game)
		1 * waitRoomService.delete(r)
		0 * _
	}

	@Unroll
	def "create user from '#sn' name works"() {
		when:
		def result = facade.createUser(sn)

		then:
		result.isPresent()
		and: 'interactions'
		1 * service.create(sn) >> Mock(User)
		0 * _

		where:
		_ | sn
		0 | 'Goku'
		1 | '123deoliveira4'
		2 | '_+//'
		3 | '$fd=43'
	}

	@Unroll
	def "create user from '#sn' name fails"() {
		when:
		def result = facade.createUser(sn)

		then:
		result.isEmpty()
		and: 'interactions'
		0 * service.create(_)
		0 * _

		where:
		_ | sn
		0 | ''
		1 | '\t'
		2 | '\n'
		3 | '   '
	}
}
