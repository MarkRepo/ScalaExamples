package CompositeAndInherit

abstract class Element {
  def contents: Array[String]
  // 无参方法, 统一访问原则使无参方法可以在子类改为使用字段实现， scala中方法和字段属于同一作用域
  def height: Int = contents.length
  def width: Int = if (height == 0) 0 else contents(0).length
}
