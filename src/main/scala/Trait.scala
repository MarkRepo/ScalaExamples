package Trait

import scala.collection.mutable.ArrayBuffer

// 特质同时定义了一个类型, 适用多态和动态绑定，可以重写特质方法的实现, 这里类似于接口
// 可以用 extends 和 with 混入特质；如果还有超类，使用extends继承超类，使用with混入特质；对于多个特质，使用with子句添加
// 使用 extends 还隐式继承了特质的超类

// 特质和类的区别：
// 1. 特质不能有任何类参数
// 2. 类中super是静态绑定的，特质中super是动态绑定的

// 特质的用途
// 自动给类添加基于已有方法的新方法
// 用特质来丰富某个接口，只需定义一个拥有为数不多的抽象方法（接口中瘦的部分）和可能数量很多的具体方法的特质（这些具体方法基于那些抽象方法编写）
// 然后使用这个特质混入某个类，在类中时间接口中瘦的部分（抽象方法），得到一个拥有完整富接口实现的类, 参考如下示例：

class Point(val x: Int, val y: Int)

trait Rectangular {
  def topLeft: Point
  def bottomRight: Point
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
}

abstract class Component extends Rectangular
class Rectangle(val topLeft: Point, val bottomRight: Point) extends Rectangular

// Ordered 特质:
// 1. 类型参数：比较元素的类型
// 2. 定义compare, this == that -> 0, this < that -> <0, this > that -> >0
class Rational(val numer: Int, val denom: Int) extends Ordered[Rational] {
  def compare(that: Rational): Int =
    this.numer * that.denom - that.numer * this.denom
}

// 特质为类提供可叠加的修改
abstract class IntQueue {
  def get(): Int
  def put(x: Int)
}

class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put(x: Int) = { buf += x }
}

// 特质声明了一个超类，表示它只能被混入继承同样超类（IntQueue）的类
// abstract override,表示 特质必须混入某个拥有该方法具体定义的类中。
// super是动态绑定的, 只要在给出了该方法具体定义的特质或类之后混入，就可以正常工作
trait Doubleing extends IntQueue {
  abstract override def put(x: Int) = super.put(2 * x)
}

trait Incrementing extends IntQueue {
  abstract override def put(x: Int) = super.put(x + 1)
}

trait Filter extends IntQueue {
  abstract override def put(x: Int) =
    if (x >= 0) super.put(x)
}

// 在特质中，super调用的方法取决于类和混入该类的特质的线性化，是动态的，举例说明线性化：
class Animal {
  def Echo = printf("Animal")
}

trait Furry extends Animal {
  override def Echo = {
    printf("Furry -> ")
    super.Echo
  }
}

trait HasLeg extends Animal {
  override def Echo = {
    printf("HasLeg -> ")
    super.Echo
  }
}

trait FourLegged extends HasLeg {
  override def Echo = {
    printf("FourLegged -> ")
    super.Echo
  }
}

class Cat extends Animal with Furry with FourLegged {
  override def Echo = {
    printf("Cat -> ")
    super.Echo
  }
}

object Trait extends App {
  val rect = new Rectangle(new Point(1, 1), new Point(10, 10))
  println(rect.left + " " + rect.right + " " + rect.width)
  println(new Rational(1, 2) < new Rational(2, 3))
  // new 后面可以接 with 直接混入特质
  val queue = new BasicIntQueue with Doubleing
  queue.put(10)
  println(queue.get()) // 20
  // 特质出现的顺序是重要的，越靠右出现的特质越先起作用。
  // 如果在特质的方法中调用super，它将调用左侧紧挨着它的那个特质的方，因此使用super可以串联多个特质
  val q = new BasicIntQueue with Incrementing with Filter
  q.put(-1)
  q.put(0)
  q.put(1)
  println(q.get() + " " + q.get()) // 1 2
  // 线性化测试
  (new Cat).Echo
}
