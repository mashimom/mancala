package org.shimomoto.mancala.service

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
		1 * waitRoomService.enter(r, u) >> true
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
		1 * waitRoomService.enter(r, u) >> false
		0 * _
	}
}
