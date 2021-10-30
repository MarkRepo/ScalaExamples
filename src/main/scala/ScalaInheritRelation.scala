// 超类 Any，每个类都是Any的子类，Any定义了以下函数
/*
final def ==(that: Any): Boolean
final def !=(that: Any): Boolean
def equals(that: Any): Boolean // 可以重写equals 来改变 ==, != 的含义
def ##: Int
def hashCode: Int
def toString: Int
 */

// Any 有两个直接子类，AnyVal, AnyRef
// AnyVal 是scala中所有值类的父类，scala提供了9个内建的值类：
// Byte Short Char Int Long Double Boolean Int Unit 他们都是字面量，不能使用new来创建；不同的值类型之间存在隐式转换
// AnyRef: scala 中所有引用类的基类，java.lang.object 是AnyRef在java平台的实现

// Scala 中基本类型的装箱操作比Java中透明，区分两种相等性判断：
// 1. == 自然相等性，子类中重写了equals 改变 ==  的含义
// 2. eq 引用相等性

// Null 类是null引用的类型，他是每个引用类的子类，Null不兼容值类型
// Nothing 是每个其他类型的子类，不存在这个类型的任何值，常用于表示非正常终止的信号

// 定义自己的值类型
// 1. 必须有且仅有一个参数，并且在内部除def之外不能有其他任何东西
// 2. 不能有其他类扩展自值类，值类不能重新定义equals和hashCode
// 3. 继承自AnyVal，在唯一的参数前加上val

package ScalaInheritRelation

object InheritRelation extends App {
  // 编译后再java字节码中直接使用Int
  class Dollars(val amount: Int) extends AnyVal {
    override def toString() = "$" + amount
  }
  println(new Dollars(10000))

  // 使用微类型避免类型单一化
  class Anchor(val value: String) extends AnyVal
  class Style(val value: String) extends AnyVal
  class Text(val value: String) extends AnyVal
  class Html(val value: String) extends AnyVal

  println((new Anchor("abc")).value)
}
