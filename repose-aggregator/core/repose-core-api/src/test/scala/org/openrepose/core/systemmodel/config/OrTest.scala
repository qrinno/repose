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

package org.openrepose.core.systemmodel.config

import javax.servlet.ServletInputStream
import org.junit.runner.RunWith
import org.openrepose.commons.utils.servlet.http.HttpServletRequestWrapper
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Matchers}
import org.springframework.mock.web.MockHttpServletRequest

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class OrTest extends FunSpec with Matchers with MockitoSugar {
  describe("evaluate") {
    it("should return false when all of the sub criteria are false") {
      val filterCriterion = new Or()
      filterCriterion.setFilterCriteria(List[FilterCriterion](
        new TestFilterCriterion(false),
        new TestFilterCriterion(false)
      ).asJava)
      val originalRequest = new MockHttpServletRequest
      val inputStream = mock[ServletInputStream]
      val httpServletRequestWrapper = new HttpServletRequestWrapper(originalRequest, inputStream)

      filterCriterion.evaluate(httpServletRequestWrapper) shouldBe false
    }

    Seq(
      (true, true, true),
      (true, false, false),
      (false, true, false),
      (false, false, true),
      (true, true, false),
      (true, false, true),
      (false, true, true)
    ).foreach { case (retOne, retTwo, retToo) =>
      it(s"should return true when any of the sub criteria are true [$retOne, $retTwo, $retToo]") {
        val filterCriterion = new Or()
        filterCriterion.setFilterCriteria(List[FilterCriterion](
          new TestFilterCriterion(retOne),
          new TestFilterCriterion(retTwo),
          new TestFilterCriterion(retToo)
        ).asJava)
        val originalRequest = new MockHttpServletRequest
        val inputStream = mock[ServletInputStream]
        val httpServletRequestWrapper = new HttpServletRequestWrapper(originalRequest, inputStream)

        filterCriterion.evaluate(httpServletRequestWrapper) shouldBe true
      }
    }
  }
}
