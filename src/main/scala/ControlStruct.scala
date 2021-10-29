// scala 内建控制结构，都会返回一个值
package ControlStruct

object ControlStruct {
  def main(args: Array[String]) {
    // if 表达式，支持指令式风格和函数式风格
    val filename = if (!args.isEmpty) args(0) else "default.txt"
    println(filename)
    println("gcd 15 5 is: " + gcdLoop(15, 5))
    doWhile(5)
  }

  // while 循环返回值类型Unit,不是表达式， Uint有一个唯一的值 ()
  // while 和 do while与var都是指令式风格，应该尽量避免，用其他方式替代，比如迭代
  // 赋值语句的返回值是Unit(), 与java，c++不同
  def gcdLoop(x: Long, y: Long): Long = {
    var a = x
    var b = y
    while( a != 0) {
      val tmp = a
      a = b % a
      b = tmp
    }
    b
  }

  // do while 循环, 返回Unit，不是表达式
  def doWhile(times: Int) {
    var line = 0
    do {
      println(line)
      line += 1
    } while(line < times)
  }
}
