package com.hivemind.app.repository

import com.hivemind.app.model.Book
import zio.ZLayer

class BookRepository {

  private val book1: Book = Book("The Hobbit", "J.R.R. Tolkien", 1937)
  private val book2: Book = Book("The Lord of the Rings", "J.R.R. Tolkien", 1954)
  private val book3: Book = Book("The Silmarillion", "J.R.R. Tolkien", 1977)
  private val book4: Book = Book("Moby Dick", "Herman Melville", 1851)

  def getAllBooks: List[Book] = List(book1, book2, book3, book4)
}

object BookRepository {
  val live: ZLayer[Any, Nothing, BookRepository] = ZLayer.succeed(new BookRepository())
}
