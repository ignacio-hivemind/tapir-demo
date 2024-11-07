package com.hivemind.app

import com.hivemind.app.model.DefectHandler
import com.hivemind.app.repository.BookRepository
import com.hivemind.app.server.MyEndpoints
import com.hivemind.app.service.BookService
import sttp.tapir.server.interceptor.cors.CORSConfig.AllowedOrigin
import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.server.ziohttp
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.ztapir.RIOMonadError
import zio.*
import zio.http.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

import java.io.IOException

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = SLF4J.slf4j(LogFormat.colored)

  given RIOMonadError[Any] = new RIOMonadError[Any]

  private val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

  private val options: ZioHttpServerOptions[Any] = ZioHttpServerOptions.customiseInterceptors
    .exceptionHandler(new DefectHandler())
    .corsInterceptor(
      CORSInterceptor.customOrThrow(
        CORSConfig.default.copy(
          allowedOrigin = AllowedOrigin.All,
        ),
      ),
    )
    .options

  private val myAppLogic: ZIO[Server & MyEndpoints & BookService & BookRepository, IOException, Unit] =
    for {
      _            <- Console.printLine(s"Starting server ...")
      endpoints    <- ZIO.service[MyEndpoints]
      booksService <- ZIO.service[BookService]
      httpApp       = ZioHttpInterpreter(options).toHttp(endpoints.swaggerEndpoints ++ booksService.myServices)
      actualPort   <- Server.install(httpApp)
      _            <- Console.printLine(s"Server started ...")
      _            <- Console.printLine(s"Go to http://localhost:$actualPort/docs to open SwaggerUI")
      _            <- ZIO.never
    } yield ()

  override def run: RIO[ZIOAppArgs & Scope, ExitCode] =
    myAppLogic
      .provide(
        MyEndpoints.live,
        Server.defaultWithPort(port),
        BookService.live,
        BookRepository.live,
      )
      .exitCode
}
