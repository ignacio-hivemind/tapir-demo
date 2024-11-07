package com.hivemind.app.service

import com.hivemind.app.model.Book
import com.hivemind.app.repository.BookRepository
import com.hivemind.app.server.MyEndpoints
import sttp.tapir.server.ServerEndpoint
import zio.{ZIO, ZLayer}

class BookService(bookRepo: BookRepository) {

  private val book1 = bookRepo.getAllBooks.head

  private val bookZIO: zio.Task[Either[String, Book]] = ZIO.succeed(Right(book1))

  private val booksZIO: zio.Task[Either[String, List[Book]]] =
    ZIO.succeed(Right(bookRepo.getAllBooks))

  private val bookService: ServerEndpoint[Any, zio.Task] =
    MyEndpoints.bookEndpoint.serverLogic[zio.Task](Unit => bookZIO)

  private val booksService: ServerEndpoint[Any, zio.Task] =
    MyEndpoints.booksEndpoint.serverLogic[zio.Task](Unit => booksZIO)

  val myServices: List[ServerEndpoint[Any, zio.Task]] = List(bookService, booksService)
}

object BookService {
  val live: ZLayer[BookRepository, Nothing, BookService] = ZLayer.fromFunction(BookService(_))
}
