package ControlAbstract

import java.io.{File, PrintWriter}

// 将函数分为不变的公共部分，即函数体，可变部分为参数，参数可以是函数值。
// 高阶函数的好处是可以用来创建减少代码重复的控制抽象
// 9.1 减少代码重复
object FileMatcher {
  private def filesHere = (new java.io.File("src/main/scala/")).listFiles()
  private def filesMatching(matcher: String => Boolean) =
    for (file <- filesHere; if matcher(file.getName))
      yield file
  def filesEnding(query: String) =
    filesMatching(_.endsWith(query))
  def filesContaining(query: String) =
    filesMatching(_.contains(query))
  def filesRegex(query: String) =
    filesMatching(_.matches(query))
}

object ControlAbstract extends App {
  val filesEndingWithScala = FileMatcher.filesEnding(".scala")
  for (file <- filesEndingWithScala)
    println(file.getName)

  // 9.2 高阶函数本身作为API，简化调用方代码， 如exists
  def containsNeg(nums: List[Int]) = nums.exists(_ < 0)
  def containsOdd(nums: List[Int]) = nums.exists(_ % 2 == 1)

  val someNumbers = List(-10, -5, 0, 5, 10)
  println(containsOdd(someNumbers))
  println(containsNeg(someNumbers))

  // 9.3 柯里化，支持多个参数列表
  def curriedSum(x: Int)(y: Int) = x + y
  println(curriedSum(1)(2)) // 第一次调用返回了一个函数值
  // 获取第一次调用后的函数值的引用
  val onePlus = curriedSum(1) _ // 下划线代表第二个参数列表
  println(onePlus(2)) // 3

  // 9.4 编写新的控制结构：创建接收函数作为入参的方法
  def withPrintWriter(file: File)(op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }
  val file = new File("date.txt")
  // 对于只传入一个参数的方法调用，可以使用花括号代替圆括号，从而。
  // 因此为了更像是语言自带的控制抽象（可以在花括号中编写函数字面量），柯里化withPrintWriter
  withPrintWriter(file) { writer => writer.println(new java.util.Date) }

  // 柯里化 twice
  def curriedTwice(x: Double)(op: Double => Double) = op(op(x))
  println(curriedTwice(2) { x => x * x })

  // 9.5 传名参数，使花括号中间的代码不需要传入参数
  var assertionEnabled = true
  def myAssert(predicate: () => Boolean) =
    if (assertionEnabled && !predicate())
      throw new AssertionError

  def byNameAssert(predicate: => Boolean) = // 去掉了 ()
    if (assertionEnabled && !predicate)
      throw new AssertionError

  def boolAssert(predicate: Boolean) =
    if (assertionEnabled && !predicate)
      throw new AssertionError

  myAssert(() => 5 > 3) // 写法繁琐

  assertionEnabled = false;
  val x = 5
  byNameAssert(x / 0 == 0) // 传给byNameAssert是一个函数值， x / 0 == 0 在该函数值内部求值
  boolAssert(x / 0 == 0) // x / 0 == 0 表达式会先于boolAssert求值，引起副作用
}
