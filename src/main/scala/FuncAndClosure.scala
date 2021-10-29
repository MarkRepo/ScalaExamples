package FuncAndClosure

import scala.io.Source

// 8.1. 方法
object LongLines {
  def processFile(filename: String, width: Int) = {
    val source = Source.fromFile(filename)
    // getLines 会去掉换行符
    for (line <- source.getLines())
      processLines(filename, width, line)
  }

  private def processLines(filename: String, width: Int, line: String) = {
    if (line.length > width)
      println(filename + ":" + line.trim)
  }
}

// 8.2  局部函数
object LocalLongLines {
  def processFile(filename: String, width: Int) = {
    // 局部函数可以访问包含他们的函数的参数
    def processLines(line: String) = {
      if (line.length > width)
        println(filename + ":" + line.trim)
    }

    val source = Source.fromFile(filename)
    for (line <- source.getLines())
      processLines(line)
  }
}

// 8.3 一等函数：函数字面量 (x: Int) => x + 1
// 函数字面量被编译成类，并在运行时实例化成函数值。每个函数值都是某个扩展自scala包的 FunctionN 系列当中的一个特质的类的实例。
// 函数值类似于对象，存在于运行时，且可以存放在变量中，并使用圆括号调用
object FuncValue extends App {
  var increase = (x: Int) => x + 1
  println("10 increase 1 = " + increase(10))
  val someNumbers = List(-11, -10, -5, 0, 5, 10)
  someNumbers.foreach((x: Int) => println(x))
  someNumbers.filter((x: Int) => x > 0).foreach(println)

  // 8.4 函数字面量简写形式
  // 8.4.1 略去参数类型声明
  someNumbers.filter((x) => x > 0)
  // 8.4.2 省去某个靠类型推断的参数两侧的圆括号
  someNumbers.filter(x => x > 0)
  // 8.5 占位符：用来表示一个或多个参数
  someNumbers.filter(_ > 0)
  // 使用括号给出参数类型，多个下划线表示多个参数
  val f = (_: Int) + (_: Int)
  println("f(5 + 5) = " + f(5, 5))
  // 8.6 部分应用函数： 用下划线替换整个参数列表
  someNumbers.foreach(println _) // 注意下划线前面有空格
  def sum(a: Int, b: Int, c: Int) = a + b + c
  // 背后发生的事情：a变量指向一个函数值对象，这个函数值是一个从scala编译器自动从sum _ 这个部分应用函数表达式生成的类的实例
  // 由编译器生成的这个类有一个接收三个参数的apply方法，apply内部只是简单地将参数转发给sum。
  // 同时，这也是一种将def定义的函数变成函数值的方式。def定义的方法或局部函数不能赋值给变量或者作为函数参数传递，但是变成函数值就可以。
  val a = sum _
  println(a(1, 2, 3))
  val b = sum(1, _: Int, 3)
  println(b(2))
  // 在明确需要函数的地方，如果部分应用函数表达式不给出任何参数，那么下划线可去掉，如
  someNumbers.foreach(println)
  // 8.7 闭包: 捕获自由变量从而闭合该函数字面量，捕获的是变量本身，而不是变量引用的值，如：
  var more = 1
  val addMore = (x: Int) => x + more
  more = 9999
  println(addMore(10)); // 10009
  // 闭包内部的改变，外部也能看到
  val numbers = List(-11, -10, -5, 0, 5, 10)
  var nsum = 0
  numbers.foreach(nsum += _) // sum 被捕获后被修改
  println(nsum)
  // 闭包引用的实例是在闭包创建时活跃的那个
  def makeIncreaser(more: Int) = (x: Int) => x + more
  val inc1 = makeIncreaser(1)
  val inc999 = makeIncreaser(999)
}

// 8.8 特殊的函数调用形式
object SpecialFunc {
  def main(args: Array[String]) = {
    // 最后一个参数可被重复,重复参数的类型是声明类型的Seq
    def echo(args: String*) =
      for (arg <- args) println(arg)
    echo()
    echo("hello")
    echo("hello", "world")
    val arr = Array("hello", "world")
    echo(arr: _*) // 将数组的每个参数传给echo

    // 带名字的参数，可以用不同的顺序传递参数。如果与按位置参数一起混用，按位置参数要在前面
    def speed(distance: Float, time: Float) = distance / time
    println(speed(100, 10))
    println(speed(time = 10, distance = 100))

    // 默认参数
    def printTime2(out: java.io.PrintStream = Console.out, divisor: Int = 1) =
      out.println("time = " + System.currentTimeMillis() / divisor)
    // 使用带名字参数，可以显示给出任意一个，另一个使用默认参数
    printTime2(out = Console.err)
    printTime2(divisor = 1000)
  }
}

// 8.9 尾递归：在最后一步调用自己。
// scala编译器能够检测到尾递归并将它替换成跳到函数的最开始，并在跳转之前更新参数,即不会创建新的调用栈
object TailRecursion extends App {
  def isGoodEnough(guess: Double) = guess < 0.000001
  def improve(guess: Double) = guess / 2
  def approximate(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else approximate(improve(guess))

  // 运行后查看栈, 非尾递归，有多级调用栈
  def boom(x: Int): Int =
    if (x == 0) throw new Exception("boom!")
    else boom(x - 1) + 1

  // 尾递归，如果要关闭，传递参数 -g:notailcalls 给scalac
  def bang(x: Int): Int =
    if (x == 0) throw new Exception("bang!")
    else bang(x - 1)

  //boom(3)
  bang(5)
  // scala 只能对那些直接尾递归调用直接的函数做优化，下面两个示例不能优化：
  def isEven(x: Int): Boolean = if (x == 0) true else isOdd(x - 1)
  def isOdd(x: Int): Boolean = if (x == 0) false else isEven(x - 1)

  def funcValue = nestedFunc _
  def nestedFunc(x: Int): Unit = {
    if (x != 0) { println(x); funcValue(x - 1) }
  }
}

object FindLongLines {
  def main(args: Array[String]) = {
    val width = args(0).toInt
    for (arg <- args.drop(1)) {
      LongLines.processFile(arg, width)
      LocalLongLines.processFile(arg, width)
    }
  }
}
