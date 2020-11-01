package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.entity.User
import spock.lang.Specification
import spock.lang.Subject

class UserFacadeSpec extends Specification {

	UserService service = Mock(UserService)

	@Subject
	UserFacade facade = new UserFacade(service)

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
}
