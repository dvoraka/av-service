package dvoraka.avservice.db

import dvoraka.avservice.db.configuration.SolrConfig
import dvoraka.avservice.db.model.MessageInfoDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.solr.core.SolrTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

/**
 * Solr performance testing.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
@Title('Solr document inserting performance test')
class SolrPSpec extends Specification {

    @Autowired
    SolrTemplate solrTemplate


    def "warm up"() {
        when:
            1.times { // currently it doesn't work without 'times' (probably bug)
                solrTemplate.saveBean(genDocument())
                solrTemplate.commit()
            }

        then:
            notThrown(Exception)
    }

    @Ignore
    @Unroll
    def "save documents w/o: #cycles"() {
        when:
            cycles.times {
                solrTemplate.saveBean(genDocument())
            }

        then:
            notThrown(Exception)

        where:
            cycles << [10, 100, 1000, 10_000]
    }

    @Ignore
    @Unroll
    def "save documents in collection w/o: #cycles"() {
        given:
            Collection<MessageInfoDocument> documents = new ArrayList<>(cycles)

        when:
            cycles.times {
                documents.add(genDocument())
            }
            solrTemplate.saveBeans(documents)

        then:
            notThrown(Exception)

        where:
            cycles << [10, 1000, 10_000, 100_000]
    }

    @Ignore
    @Unroll
    def "save documents soft: #cycles"() {
        when:
            cycles.times {
                solrTemplate.saveBean(genDocument())
                solrTemplate.softCommit()
            }

        then:
            notThrown(Exception)

        where:
            cycles << [10, 100, 1000, 10_000]
    }

    @Ignore
    @Unroll
    def "save documents hard: #cycles"() {
        when:
            cycles.times {
                solrTemplate.saveBean(genDocument())
                solrTemplate.commit()
            }

        then:
            notThrown(Exception)

        where:
            cycles << [10, 100, 1000]
    }

    @Ignore
    @Unroll
    def "hard commit after (#run. run)"() {
        given:
            int documents = 100_000

        when:
            documents.times {
                solrTemplate.saveBean(genDocument())
            }

            solrTemplate.commit()

        then:
            notThrown(Exception)

        where:
            run << [1, 2, 3, 4, 5]
    }

    @Ignore
    def "insert 1 M documents"() {
        given:
            long start = System.currentTimeMillis()
            int documentCount = 100_000
            Collection<MessageInfoDocument> documents = new ArrayList<>(documentCount)

        when:
            documentCount.times {
                documents.add(genDocument())
            }
            println("In collection: " + getMsFromStart(start))

            solrTemplate.saveBeans(documents)
            println("In Solr: " + getMsFromStart(start))

            solrTemplate.commit()
            println("After commit: " + getMsFromStart(start))

        then:
            notThrown(Exception)

        where:
            i << (1..10)
    }

    long getMsFromStart(long start) {
        return (System.currentTimeMillis() - start)
    }

    MessageInfoDocument genDocument() {

        MessageInfoDocument document = new MessageInfoDocument()
        document.setId(UUID.randomUUID().toString())
        document.setUuid(UUID.randomUUID().toString())
        document.setCreated(new Date())
        document.setServiceId("TEST")
        document.setSource("PERF-TEST")

        return document
    }
}
