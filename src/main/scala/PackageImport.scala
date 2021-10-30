// 将代码放进包的两种方式：
// 1. package 子句
// 2. package 子句后加上一段用花括号包起来的代码块,这使得可以在一个文件包含多个包的内容
package bobs.navi {
  class Navi
}

// 对类和包的精简访问
package bobsrockets {
  package navigation {
    class Navigator {
      // 同一个包，不需要前缀
      val map = new StartMap
    }
    class StartMap
  }

  class Ship {
    // 包自身也可以在包含它的包内不带前缀访问，如navigation
    val nav = new navigation.Navigator
  }

  package fleets {
    // 对于花括号打包语法，所有在包外的作用域内可被访问的名称，在包内也可以访问到
    class Fleet {
      def addShip() = { new Ship }

    }
  }
}

package bobsrockets.fleets {
  class Fleet2 {
    // 不能编译
    // def addShip() = new Ship
  }
}

// 嵌套访问
package bobsrockets {
  package navigation {
    package launch {
      class Booster1
    }
    class MissionControl {
      val booster1 = new launch.Booster1
      val booster2 = new bobsrockets.launch.Booster2
      // 顶层包都是 _root_ 的成员
      val booster3 = new _root_.launch.Booster3
    }
  }
  package launch {
    class Booster2
  }
}

// scala 的import可以出现在任何地方，引入任何值，引入包本身，重命名或者隐藏指定的成员
// 单类型引入
import bob.Fruit
// 按需引入
import bob._
// 静态字段引入
import bob.Fruits._

// 包对象，使除了包、特质、孤立对象以外的东西，比如函数，变量等，可以放到包内
package object global {
  def showFruit(fruit: Fruit) = {
    // 引入值
    import fruit._
    println(name + "s are " + color)
  }
}

// import 选择子句,选择器可以包含：
// 1. 简单的名称x
// 2. 重命名子句： x => y
// 3. 隐藏子句： x => _
// 4. 捕获所有的 _

// 访问修饰符
// 1. private: private 成员只在包含该定义的类或对象内部可见，内部类也可见
// 2. protected: protected 成员只能从定义该成员的子类访问
// 3. private[X]或protected[X]: 对此成员的访问限制“上至” X都是私有或受保护的，其中X表示某个包含该定义的包、类或单例对象
// (可以把作用域想象成一个个嵌套的圈，private[X]可以理解为在X圈内可见，圈外为private； protected同理)
// 4. 伴生对象和其对应的类共享访问权限
object Importer extends App {
  global.showFruit(Apple)
}
