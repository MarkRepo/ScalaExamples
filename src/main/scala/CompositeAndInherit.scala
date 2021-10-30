// 10 组合和继承
package CompositeAndInherit

import Element.elem

abstract class Element {
  def contents: Array[String]
  // 无参方法, 统一访问原则使无参方法可以在子类改为使用字段实现，而不影响用户使用
  // scala中只有两个命名空间：
  // 1. 值（方法、字段、包和单例对象）
  // 2. 类型(类和特质)
  def height: Int = contents.length
  def width: Int = contents(0).length

  def above(that: Element): Element = {
    val this1 = this widen that.width
    val that1 = that widen this.width
    elem(this1.contents ++ that1.contents)
  }

  def beside(that: Element): Element = {
    val this1 = this heighten that.height
    val that1 = that heighten this.height
    elem(
      // zip 将两个数组转成对偶，会扔掉多余的元素
      for ((line1, line2) <- this1.contents zip that1.contents)
        yield line1 + line2
    )
  }

  def widen(w: Int): Element =
    if (w <= width) this
    else {
      val left = elem(' ', (w - width) / 2, height)
      val right = elem(' ', w - width - left.width, height)
      left beside this beside right
    }

  def heighten(h: Int): Element =
    if (h <= height) this
    else {
      val top = elem(' ', width, (h - height) / 2)
      val bot = elem(' ', width, h - height - top.height)
      top above this above bot
    }
  override def toString(): String = contents mkString "\n"
}

// 1. 默认访问是public
// 2. 除私有成员外，其他成员都被继承
// 3. 可以重写或实现父类成员, 如果是重写，要加override
// 4. 参数化字段：将参数和字段合并，做法是在contents参数前面放一个val/var, 可以被private protect public 修饰
// 5. 调用超类构造方法：在父类后面的圆括号传递构造参数
// 6. 多态和动态绑定与cpp类似
// 7. final 可以使成员不被重写， 使类不被继承

// 使用工厂对象, 且把子类放到单例对象中，变成私有的
object Element {
  private class ArrayElement(val contents: Array[String]) extends Element {}

  private class LineElement(s: String) extends Element {
    val contents = Array(s)
    override def width = s.length()
    override def height = 1
  }

  private class UniformElement(
      ch: Char,
      override val width: Int,
      override val height: Int
  ) extends Element {
    private val line = ch.toString() * width
    def contents = Array.fill(height)(line)
  }

  def elem(contents: Array[String]): Element =
    new ArrayElement(contents)
  def elem(ch: Char, width: Int, height: Int): Element =
    new UniformElement(ch, width, height)
  def elem(s: String): Element =
    new LineElement(s)

  def main(args: Array[String]) {
    val hello = elem("hello")
    val world = elem(",world!")
    val name = elem('M', 4, 2)
    val addr = elem(Array("fujianshen", "sanmingshi"))
    val str = (hello above world) beside (name above addr)
    println(str)
  }
}
