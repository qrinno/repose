/*
 * _=_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=
 * Repose
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Copyright (C) 2010 - 2015 Rackspace US, Inc.
 * _-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_=_
 */
package features.services.atomfeed

import features.filters.keystonev2.AtomFeedResponseSimulator
import groovy.xml.MarkupBuilder
import org.junit.experimental.categories.Category
import org.openrepose.framework.test.ReposeValveTest
import org.rackspace.deproxy.Request
import org.rackspace.deproxy.Response
import scaffold.category.Slow
import org.openrepose.framework.test.mocks.MockIdentityV2Service
import org.rackspace.deproxy.Deproxy
import org.rackspace.deproxy.Endpoint

import java.util.concurrent.TimeUnit

import static javax.servlet.http.HttpServletResponse.SC_OK

@Category(Slow.class)
class AtomFeedServiceTest extends ReposeValveTest {
    Endpoint originEndpoint
    Endpoint atomEndpoint
    MockIdentityV2Service fakeIdentityV2Service
    AtomFeedResponseSimulator fakeAtomFeed

    def setup() {
        deproxy = new Deproxy()
        reposeLogSearch.cleanLog()

        int atomPort = properties.atomPort
        fakeAtomFeed = new AtomFeedResponseSimulator(atomPort)
        atomEndpoint = deproxy.addEndpoint(atomPort, 'atom service', null, fakeAtomFeed.handler)

        Map params = properties.defaultTemplateParams
        repose.configurationProvider.applyConfigs("common", params)
        repose.configurationProvider.applyConfigs("features/filters/keystonev2/common", params)
        repose.configurationProvider.applyConfigs("features/filters/keystonev2/atom", params)
        repose.start()

        originEndpoint = deproxy.addEndpoint(properties.targetPort, 'origin service')

        fakeIdentityV2Service = new MockIdentityV2Service(properties.identityPort, properties.targetPort)
        deproxy.addEndpoint(properties.identityPort, 'identity service', null, fakeIdentityV2Service.handler)

        repose.waitForNon500FromUrl(reposeEndpoint)
    }

    def cleanup() {
        deproxy?.shutdown()
        repose?.stop()
    }

//    def "when an atom feed entry is received, it is passed to the filter"() {
//        given: "there is an atom feed entry available for consumption"
//        def atomFeedEntry = fakeAtomFeed.atomEntryForTokenInvalidation(id: 'urn:uuid:101')
//        atomEndpoint.defaultHandler = fakeAtomFeed.handlerWithEntry(atomFeedEntry)
//
//        when: "we wait for the Keystone V2 filter to read the feed"
//        reposeLogSearch.awaitByString("</atom:entry>", 1, 11, TimeUnit.SECONDS)
//        atomEndpoint.defaultHandler = fakeAtomFeed.handler
//
//        then: "the Keystone V2 filter logs receiving the atom feed entry"
//        AtomFeedResponseSimulator.buildXmlToString(atomFeedEntry).eachLine { line ->
//            assert reposeLogSearch.searchByString(line.trim()).size() == 1
//            true
//        }
//    }
//
//    def "when multiple atom feed entries are received, they are passed in-order to the filter"() {
//        given: "there is a list of atom feed entries available for consumption"
//        List<String> ids = (201..210).collect {it as String}
//        atomEndpoint.defaultHandler = fakeAtomFeed.handlerWithEntries(
//                ids.collect { fakeAtomFeed.atomEntryForTokenInvalidation(id: "urn:uuid:$it") })
//
//        when: "we wait for the Keystone V2 filter to read the feed"
//        reposeLogSearch.awaitByString("</atom:entry>", ids.size(), 11, TimeUnit.SECONDS)
//        atomEndpoint.defaultHandler = fakeAtomFeed.handler
//
//        then: "the Keystone V2 filter logs receiving the atom feed entries in order"
//        def logLines = reposeLogSearch.searchByString("<atom:id>.*</atom:id>")
//        logLines.size() == ids.size()
//        logLines.collect { (it =~ /\s*<atom:id>urn:uuid:(\d+)<\/atom:id>.*/)[0][1] } == ids
//    }
//
//    // todo: this test does not actually create a multi-page feed, but rather, replaces the feed at the base URI
//    def "when multiple pages of atom feed entries are received, they are all processed by the filter"() {
//        given: "there is a list of atom feed entries available for consumption"
//        List<String> ids = (301..325).collect {it as String}
//        atomEndpoint.defaultHandler = fakeAtomFeed.handlerWithEntries(
//                ids.collect { fakeAtomFeed.atomEntryForTokenInvalidation(id: "urn:uuid:$it") })
//
//        when: "we wait for the Keystone V2 filter to read the feed"
//        reposeLogSearch.awaitByString("</atom:entry>", ids.size(), 11, TimeUnit.SECONDS)
//        atomEndpoint.defaultHandler = fakeAtomFeed.handler
//
//        then: "the Keystone V2 filter logs receiving the atom feed entries in order"
//        def logLines = reposeLogSearch.searchByString("<atom:id>.*</atom:id>")
//        logLines.size() == ids.size()
//        logLines.collect { (it =~ /\s*<atom:id>urn:uuid:(\d+)<\/atom:id>.*/)[0][1] } == ids
//
//        when: "there are more entries on the next page"
//        def moreIds = (401..425).collect {it as String}
//        atomEndpoint.defaultHandler = fakeAtomFeed.handlerWithEntries(
//                moreIds.collect { fakeAtomFeed.atomEntryForTokenInvalidation(id: "urn:uuid:$it") })
//
//        and: "we wait for the Keystone V2 filter to read the feed"
//        reposeLogSearch.awaitByString("</atom:entry>", ids.size() + moreIds.size(), 11, TimeUnit.SECONDS)
//        atomEndpoint.defaultHandler = fakeAtomFeed.handler
//
//        then: "the Keystone V2 filter logs receiving the atom feed entries in order"
//        def moreLogLines = reposeLogSearch.searchByString("<atom:id>.*4\\d{2}</atom:id>")
//        moreLogLines.size() == moreIds.size()
//        moreLogLines.collect { (it =~ /\s*<atom:id>urn:uuid:(\d+)<\/atom:id>.*/)[0][1] } == moreIds
//    }

    def "when new events are posted to the feed, they are all processed by the filter"() {
        given:
        def params = fakeAtomFeed.defaultParams + fakeAtomFeed.DEFAULT_FEED_PARAMS
        def initialFeedPg1 =
            fakeAtomFeed.buildXmlToString { MarkupBuilder xmlBuilder ->
                xmlBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

                xmlBuilder.'feed'(xmlns: "http://www.w3.org/2005/Atom") {
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "current")
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "self")
                    'id'(params.id)
                    'title'(type: "text", "feed")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=urn:uuid:1&amp;limit=1&amp;search=&amp;direction=backward", rel: "next")
                    'updated'(params.time)
                    fakeAtomFeed.atomEntryForUserUpdate(id: "urn:uuid:2")(xmlBuilder)
                }

                xmlBuilder
            }

        def initialFeedPg2 =
            fakeAtomFeed.buildXmlToString { MarkupBuilder xmlBuilder ->
                xmlBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

                xmlBuilder.'feed'(xmlns: "http://www.w3.org/2005/Atom") {
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "current")
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "self")
                    'id'(params.id)
                    'title'(type: "text", "feed")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=last&amp;limit=1&amp;search=&amp;direction=backward", rel: "last")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=urn:uuid:2&amp;limit=1&amp;search=&amp;direction=forward", rel: "previous")
                    'updated'(params.time)
                    fakeAtomFeed.atomEntryForUserUpdate(id: "urn:uuid:1")(xmlBuilder)
                }

                xmlBuilder
            }

        def updatedFeedPg1 =
            fakeAtomFeed.buildXmlToString { MarkupBuilder xmlBuilder ->
                xmlBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

                xmlBuilder.'feed'(xmlns: "http://www.w3.org/2005/Atom") {
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "current")
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "self")
                    'id'(params.id)
                    'title'(type: "text", "feed")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=urn:uuid:2&amp;limit=1&amp;search=&amp;direction=backward", rel: "next")
                    'updated'(params.time)
                    fakeAtomFeed.atomEntryForUserUpdate(id: "urn:uuid:3")(xmlBuilder)
                }

                xmlBuilder
            }

        def updatedFeedPg2 =
            fakeAtomFeed.buildXmlToString { MarkupBuilder xmlBuilder ->
                xmlBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

                xmlBuilder.'feed'(xmlns: "http://www.w3.org/2005/Atom") {
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "current")
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "self")
                    'id'(params.id)
                    'title'(type: "text", "feed")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=urn:uuid:1&amp;limit=1&amp;search=&amp;direction=backward", rel: "next")
                    'updated'(params.time)
                    fakeAtomFeed.atomEntryForUserUpdate(id: "urn:uuid:2")(xmlBuilder)
                }

                xmlBuilder
            }

        def updatedFeedPg3 =
            fakeAtomFeed.buildXmlToString { MarkupBuilder xmlBuilder ->
                xmlBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

                xmlBuilder.'feed'(xmlns: "http://www.w3.org/2005/Atom") {
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "current")
                    'link'(href: "http://localhost:${params.atomPort}/feed/", rel: "self")
                    'id'(params.id)
                    'title'(type: "text", "feed")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=last&amp;limit=1&amp;search=&amp;direction=backward", rel: "last")
                    'link'(href: "http://localhost:${params.atomPort}/feed/?marker=urn:uuid:2&amp;limit=1&amp;search=&amp;direction=forward", rel: "previous")
                    'updated'(params.time)
                    fakeAtomFeed.atomEntryForUserUpdate(id: "urn:uuid:1")(xmlBuilder)
                }

                xmlBuilder
            }

        when:
        atomEndpoint.defaultHandler = { Request request ->
            if(request.path.contains("marker=urn:uuid:1")) {
                new Response(SC_OK,
                    null,
                    fakeAtomFeed.headers,
                    initialFeedPg2)
            } else {
                new Response(SC_OK,
                    null,
                    fakeAtomFeed.headers,
                    initialFeedPg1)
            }
        }
        reposeLogSearch.awaitByString("</atom:entry>", 2, 11, TimeUnit.SECONDS)

        and:
        atomEndpoint.defaultHandler = { Request request ->
            if(request.path.contains("marker=urn:uuid:1")) {
                new Response(SC_OK,
                    null,
                    fakeAtomFeed.headers,
                    updatedFeedPg3)
            } else if(request.path.contains("marker=urn:uuid:2")) {
                new Response(SC_OK,
                    null,
                    fakeAtomFeed.headers,
                    updatedFeedPg2)
            } else {
                new Response(SC_OK,
                    null,
                    fakeAtomFeed.headers,
                    updatedFeedPg1)
            }
        }
        reposeLogSearch.awaitByString("</atom:entry>", 3, 11, TimeUnit.SECONDS)

        then:
        def logLines = reposeLogSearch.searchByString(">.*\\d+</atom:id>")
        logLines.size() == 3
        logLines.collect { (it =~ /\s*>urn:uuid:(\d+)<\/atom:id>.*/)[0][1] } == [2, 1, 3]
    }
}
