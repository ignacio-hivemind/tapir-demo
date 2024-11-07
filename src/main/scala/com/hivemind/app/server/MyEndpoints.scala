package com.hivemind.app.server

import com.hivemind.app.model.Book

import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server.ServerEndpoint
import zio.*
import sttp.tapir.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter

trait MyEndpoints {
  val myEndpoints: List[AnyEndpoint]
  val swaggerEndpoints: List[ServerEndpoint[Any, Task]]
}

object MyEndpoints extends MyEndpoints {
  private val exampleBook1 = Book("The Hobbit", "J.R.R. Tolkien", 1937)
  private val exampleBook2 = Book("The Lord of the Rings", "J.R.R. Tolkien", 1954)

  val bookEndpoint: Endpoint[Unit, Unit, String, Book, Any] =
    endpoint.get
      .in("book")
      .errorOut(stringBody)
      .out(jsonBody[Book].example(exampleBook1))

  val booksEndpoint: Endpoint[Unit, Unit, String, List[Book], Any] =
    endpoint.get
      .in("books")
      .errorOut(stringBody)
      .out(jsonBody[List[Book]].example(List(exampleBook1, exampleBook2)))

  override val myEndpoints: List[AnyEndpoint] = List(bookEndpoint, booksEndpoint)

  override val swaggerEndpoints: List[ServerEndpoint[Any, Task]] =
    SwaggerInterpreter().fromEndpoints[Task](myEndpoints, "My Tapir HTTP App", "1.0")

  val live: ZLayer[Any, Nothing, MyEndpoints] =
    ZLayer.fromFunction(() => MyEndpoints)
}
