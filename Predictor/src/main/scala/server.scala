import java.io.{InputStream, OutputStream}
import java.net.InetSocketAddress


import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

object SimpleHttpServer {

  def main(args: Array[String]) {
    val server = HttpServer.create(new InetSocketAddress(8000), 0)
    server.createContext("/", new RootHandler())
    server.setExecutor(null)

    server.start()

    println("Hit any key to exit...")

    System.in.read()
    server.stop(0)
  }
}

class RootHandler extends HttpHandler {

  val myapp: SimpleApp = new SimpleApp()

  myapp.init("gold")
  myapp.init("oil")
  myapp.init("usdcad")

  def handle(t: HttpExchange) {
    println("******************** REQUEST START ********************")

    println("Request is "  + t.getRequestURI().toString)

    val q = t.getRequestURI().toString.split("/")

    val instrument = q(1)

    val period = q(2)

    var prediction: Double = 0.0

    if (instrument.toString == "USD_CAD") {
      prediction = myapp.currencyModels(period.toInt)
    }
    else if (instrument.toString == "WTICO_USD") {
      prediction = myapp.oilModels(period.toInt)
    }
    else if (instrument.toString == "XAU_USD") {
      prediction = myapp.goldModels(period.toInt)
    }

    sendResponse(t, prediction)
    println("********************* REQUEST END *********************")
  }

  private def displayPayload(body: InputStream): Unit = {
    println()
    println("******************** REQUEST START ********************")
    println()
    copyStream(body, System.out)
    println()
    println("********************* REQUEST END *********************")
    println()
  }

  private def copyStream(in: InputStream, out: OutputStream) {
    Iterator
      .continually(in.read)
      .takeWhile(-1 !=)
      .foreach(out.write)
  }

  private def sendResponse(t: HttpExchange, prediction: Double) {
    val response = prediction.toString
    t.getResponseHeaders().add("Access-Control-Allow-Origin", "*")
      t.sendResponseHeaders(200, response.length())
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}