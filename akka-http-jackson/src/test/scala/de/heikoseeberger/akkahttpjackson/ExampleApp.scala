/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.akkahttpjackson

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.{ ActorMaterializer, Materializer }
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object ExampleApp {

  final case class Foo(bar: String)

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()

    // provide an implicit ObjectMapper if you want serialization/deserialization to use it
    // instead of a default ObjectMapper configured only with DefaultScalaModule provided
    // by JacksonSupport
    //
    // for example:
    //
    // implicit val objectMapper = new ObjectMapper()
    //   .registerModule(DefaultScalaModule)
    //   .registerModule(new GuavaModule())

    Http().bindAndHandle(route, "127.0.0.1", 8000)

    StdIn.readLine("Hit ENTER to exit")
    Await.ready(system.terminate(), Duration.Inf)
  }

  def route(implicit mat: Materializer) = {
    import Directives._
    import JacksonSupport._

    pathSingleSlash {
      post {
        entity(as[Foo]) { foo =>
          complete {
            foo
          }
        }
      }
    }
  }
}
