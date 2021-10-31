// 样例类用来对对象进行模式匹配，对需要模式匹配的类加上case关键字。case 带来4个语法上的便利
// 1. 添加一个跟类同名的工厂方法, 如Var("x"), 而不用new Var("x")
// 2. 参数列表中的参数隐式获得了一个val前缀，因此会被当做字段处理
// 3. 编译器会以“自然”的方式实现toString, hashCode, equals。因此样例类实例总是以结构化的方式进行比较
// 4. 编译器会添加一个copy方法用于制作修改过的拷贝。这个方法可以用于制作除一两个属性不同之外其余完全相同的该类的新实例

// sealed 将样例类的超类标记位密封的，密封类除在同一个文件中定义的子类外，不能添加新的子类。
// 对继承自密封类的样例类做匹配，编译器会用警告消息标识出缺失的模式组合
sealed abstract class Expr
case class Var(Name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(op: String, arg: Expr) extends Expr
case class BinOp(op: String, left: Expr, right: Expr) extends Expr

object CasePatternMatch extends App {
  // 语法便利
  val v = Var("x")
  val op = BinOp("+", Number(1), v)
  println(op)
  println(op.right == Var("x"))
  println(op.copy(op = "-"))

  //  格式：选择器 match {可选分支}
  //  case分支格式： 模式 => 表达式
  // 1. match 是一个表达式，会有一个值，即匹配的case分支的值
  // 2. 分支按模式出现的顺序匹配，不会贯穿
  // 3. 如果没有一个模式匹配上，会抛出MatchError异常
  def simplifyTop(expr: Expr): Expr = expr match {
    // 构造方法模式，入参本身也是模式
    case UnOp("-", UnOp("-", e))  => e
    case BinOp("+", e, Number(0)) => e
    case BinOp("*", e, Number(1)) => e
    // _ 匹配任何值，但不会引入变量来指向这个值
    // 可以没有expr，这样是一个什么都不做的case
    case _ => expr
  }

  // 模式的种类：
  // 1. 通配模式(_)会匹配任何对象；还可以用来忽略某个对象中你并不关心的局部
  def general(expr: Expr) = expr match {
    case BinOp(_, _, _) => println(expr + " is a binary op")
    case _              =>
  }

  // 2. 常量模式仅匹配自己，任何字面量、val或单例对象都可以作为常量模式使用
  import math.{E, Pi}
  def describe(x: Any) = x match {
    case 5       => "five"
    case true    => "truth"
    case "hello" => "hi"
    case Nil     => "the empty list"
    case Pi      => "Pi = " + Pi
    case _       => "something else"
  }

  // 3. 变量模式匹配任何对象，将对应的变量绑定到配皮的对象上
  // 一个以小写字母大头的简单名称会被当做模式变量处理，所有其他引用都是常量
  // 使用小写进行常量模式匹配的2个技巧：如果常量是某个对象的字段，可以在字段名前加上限定词；使用反引号将名称抱起来,如`pi`
  def valPattern(d: Double) = d match {
    case 0  => "zero"
    case pi => "Pi = " + pi // 参考常量匹配的Pi
  }

  // 4. 构造方法模式，由一个名称和一组圆括号中的模式组成。假定名称指定是样例类，它会首先检查被匹配的对象是否是以这个名称命名的样例类的实例
  // 然后再检查这个对象的构造方法参数是否匹配圆括号额外给出的模式。参考simplifyTop，构造方法模式支持深度的匹配。

  // 5. 序列模式，跟序列类型匹配，如List，Array
  def seqPattern(x: Any) = x match {
    case List(0, _, _) => println("found it") // 匹配以0开始的3元素列表
    case List(0, _*)   => println("found it") // 匹配任意长度的以0开始的列表
    case _             =>
  }

  // 6. 元组模式
  def tupleDemo(expr: Any) = expr match {
    case (a, b, c) => println("matched" + a + b + c)
    case _         =>
  }

  // 7. 带类型的模式, 可以用带类型的模式来替代类型测试和类型转换
  def generalSize(x: Any) = x match {
    case s: String => s.length
    // Scala 采用了擦除式的泛型，意味着在运行时不会保留类型参数的信息，因此这里map的k，v类型不可知
    // 擦除规则的唯一例外是数组，scala和java都做了特殊处理
    case m: Map[_, _]     => m.size
    case a: Array[String] => a.length
    case _                => -1
  }
  // 类型测试和转换的啰嗦写法
  def generalSize2(x: Any) = {
    if (x.isInstanceOf[String]) {
      val s = x.asInstanceOf[String]
      s.length()
    } else -1
  }

  // 8. 变量绑定模式，如果匹配成功，就将匹配的对象赋值给这个变量
  def varBind(expr: Expr) = expr match {
    case UnOp("abs", e @ UnOp("abs", _)) =>
      e // 去除重复的绝对值操作， e 绑定到 UnOp("abs", _)表达式
    case _ =>
  }

  // 模式守卫出现在模式之后，并以if打头
  def simpleAdd(e: Expr) = e match {
    case BinOp("+", x, y) if x == y => //同一个模式变量在模式中只能出现一次
      BinOp("*", x, Number(2))
  }

  // 对表达式添加注解, unchecked 注解会压制编译器对密封类模式分支的覆盖完整性检查
  def desc(e: Expr): String = (e: @unchecked) match {
    case Number(_) => "a number"
    case Var(_)    => "a var"
  }

  // Option 可选值类型，其值有两种形式
  // Some(x), x是实际值
  // None, 代表没有值
  // 将可选值解开的最常见方式是通过模式匹配
  def show(x: Option[String]) = x match {
    case Some(s) => s
    case None    => "?"
  }

  // 除了match表达式外，其他能使用模式的地方
  // 1. 变量定义中的模式
  val myTuple = (123, "abc")
  // 使用模式解开tuple
  val (number, string) = myTuple
  // 析构样例类
  val exp = new BinOp("*", Number(5), Number(1))
  val BinOp(o, left, right) = exp // 定义了o，left， right三个变量
  // 2. 作为偏函数的case序列: 用花括号抱起来的一系列case，可以用在任何允许出现函数字面量的地方
  val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None    => 0
  }

  // 通过case 序列得到的是一个偏函数，如果这个函数应用到它不支持的值上，会产生一个运行时异常
  // 如果想检测某个偏函数是否对某个入参有定义，必须首先告诉编译器你知道你要处理的是偏函数
  // List[Int] => Int 涵盖了所有从整数列表到整数的函数，包括偏函数和全函数
  // 仅涵盖从整数列表到整数的类型写作PartialFunction[List[Int], Int]
  val second: List[Int] => Int = { // second 不是偏函数
    case x :: y :: _ => y
  }
  val secondP: PartialFunction[List[Int], Int] = { // secondP是偏函数
    case x :: y :: _ => y
  }
  // 偏函数定义了isDefindAt 方法，用来检测该函数是否对某个特定的值有定义
  println(secondP.isDefinedAt(List(5, 6, 7)))
  println(secondP.isDefinedAt(List()))

  // 3. for 表达式中的模式
  def forPattern(m: Map[String, String]) = {
    for ((k, v) <- m)
      println("k is" + k + ", v is " + v)
  }
  // 不能匹配给定模式的值会被直接丢弃
  val results = List(Some("apple"), None, Some("orange"))
  for (Some(fruit) <- results) println(fruit) // apple orange
}
