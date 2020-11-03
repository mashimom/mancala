package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.model.entity.WaitRoom
import org.shimomoto.mancala.repository.WaitRoomRepository
import spock.lang.Specification
import spock.lang.Subject

class WaitRoomServiceSpec extends Specification {

	WaitRoomRepository repository = Mock(WaitRoomRepository)

	@Subject
	WaitRoomService service = new WaitRoomService(repository)

	User user = User.builder()
					.screenName("Bulma")
					.build()

	def "getFirst room works when there is one"() {
		given:
		WaitRoom wr = Mock(WaitRoom)

		when:
		def result = service.getFirstRoom()

		then:
		result == wr
		and: 'interactions'
		1 * repository.findAll() >> [wr]
	}

	def "getFirst room creates a room when ther isn't one"() {
		given:
		WaitRoom wr = Mock(WaitRoom)

		when:
		def result = service.getFirstRoom()

		then:
		result == wr
		and: 'interactions'
		1 * repository.findAll() >> []
		1 * repository.save(_) >> wr
	}

	def "waiting room is occupied"() {
		given:
		WaitRoom room = WaitRoom.builder().signed(user).build()

		when:
		def result = service.getUserWaiting(room)

		then:
		result.isPresent()
		result.get() == user
	}

	def "waiting room is vacant"() {
		given:
		WaitRoom room = WaitRoom.builder().build()

		when:
		def result = service.getUserWaiting(room)

		then:
		result.isEmpty()
	}

	def "Remove user from waiting room"() {
		given:
		WaitRoom room = WaitRoom.builder().signed(user).build()

		when:
		def result = service.remove(room)

		then:
		result.isPresent()
		result.get() == user
		room.signed == null
	}

	def "Remove user from empty waiting room"() {
		given:
		WaitRoom room = WaitRoom.builder().build()

		when:
		def result = service.remove(room)

		then:
		result.isEmpty()
		room.signed == null
	}

	def "Empty waiting room accepts a user"() {
		given:
		WaitRoom wr = WaitRoom.builder().build()

		expect:
		service.enter(wr, user)
	}

	def "Occupied waiting room does not accept user"() {
		given:
		User u = Mock(User)
		WaitRoom wr = WaitRoom.builder()
						.signed(u)
						.build()


		when:
		def result = service.enter(wr, user)

		then:
		!result
		wr.signed != user
	}

	def "Waiting room cannot be occupied by same user"() {
		given:
		WaitRoom wr = WaitRoom.builder().signed(user).build()

		when:
		def result = service.enter(wr, user)

		then:
		!result
		wr.signed == user
	}
}
