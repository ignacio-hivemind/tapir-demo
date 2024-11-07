package com.hivemind.app.model

case class Book(title: String, author: String, year: Int)

object Book {
  import sttp.tapir.Schema
  import zio.json.JsonCodec
  import sttp.tapir.generic.auto.*

  // derive tapir Schema for Book
  given JsonCodec[Book] = JsonCodec.derived[Book]
  given Schema[Book]    = Schema.derived[Book]
}
