package org.shimomoto.mancala.model.util


import spock.lang.Specification

class PublicIdUtilsSpec extends Specification {

	def "encoding and decoding works with known good fixed values"() {
		given: 'known symmetrical representations'
		def id = UUID.fromString('655f0eae-a392-4863-afed-8801d07af4f5')
		def pid = 'ZV8OrqOSSGOv7YgB0Hr09Q'

		expect:
		PublicIdUtils.stringEncode(id) == pid
		PublicIdUtils.stringDecode(pid).get() == id
	}

	def "encoding and decoding works in bulk"() {
		given:
		def ids = (1..1_000).collect { UUID.randomUUID() }

		when:
		def pids = ids.collect { PublicIdUtils.stringEncode(it) }

		then:
		pids.toSet().size() == 1_000

		when:
		def result = pids.collect { PublicIdUtils.stringDecode(it) }

		then:
		result*.isPresent().toSet() == [true].toSet()
		result*.get() == ids
	}
}
