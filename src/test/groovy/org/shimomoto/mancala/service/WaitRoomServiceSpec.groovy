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

	def "User can enter"() {
		given:
		WaitRoom wr = WaitRoom.builder()
						.build()

		when:
		def result = service.enter(service.getFirstRoom(), user)

		then:
		result
		and: 'interactions'
		repository.findAll() >> [wr]
	}

	def "User already in waiting room"() {
		given:
		WaitRoom wr = WaitRoom.builder()
						.signed([user].toSet())
						.build()

		when:
		def result = service.enter(service.getFirstRoom(), user)

		then:
		!result
		and: 'interactions'
		repository.findAll() >> [wr]
	}
}
