package HelloWorld
object HelloWorld {
  def main(args: Array[String]) = {
    for (arg <- args)
      println(arg)
    println("hello world!")
  }
}
