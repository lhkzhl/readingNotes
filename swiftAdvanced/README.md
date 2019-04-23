### 术语

**值 (value)** 是不变的，永久的，它从不会改变

**字面量 (literal)** 

**变量(variable)**  var x = [1,2]

**常量变量 (constant variables)**  let

**值类型 (value type)** 如 结构体 (struct) 和枚举 (enum) 

1. 当你把一个结构体变量赋值给另一个，那么这两个变量将会包含同样的值。你可以将它理解为内容被复制了一遍，但是更精确地描述的话，是被赋值的变量与另外的那个变量包含了同样的值。​ 	

**引用 (reference)**  它是一个 “指向” 另一个值的值。两个引用可能会指向同一个值，这引入了一种可能性，那就是这个值可能会被程序的两个不同的部分所改变。

**引用类型 (reference type)** 类 (class) 

1. 你不能在一个变量里直接持有一个类的实例 (我们偶尔可能会把这个实例称作对象 (object)
2. 对于一个类的实例，我们只能在**变量里持有对它的引用**，然后使用这个引用来访问它

引用类型具有**同一性 (identity)**  可以使用 === 来检查两个变量是否确实引用了同一个对象

1. **===** 运算符实际做的是询问 “这两个变量是不是持有同样的引用”
2. == 有时候被称为结构相等，而 === 则被称为指针相等或者引用相等。

一个引用变量也可以用 let 来声明，这样做会使**引用变为常量**

我们通过值类型是否执行**深复制**来对它们分类，判断它们是否具有**值语义 (value semantics)**。这种复制可能是在赋值新变量时就发生的，也可能会延迟到变量内容发生变更的时候再发生。

**浅复制 (shallow copy)** 如果我们的结构体中包含有引用类型，在将结构体赋值给一个新变量时所发生的复制行为中，这些引用类型的内容是不会被自动复制一份的，只有引用本身会被复制

**高阶函数 (higher-order function)** 一个函数接受别的函数作为参数 (比如 map 函数接受一个转换函数，并将其应用到数组中的所有元素上)，或者一个函数的返回值是函数

**闭包 (closure)** 如果一个函数被定义在外层作用域中，但是**被传递出这个作用域** (比如把这个函数被作为其他函的返回值返回时)，它将能够 **“捕获” 局部变量**。这些局部变量将存在于函数中，不会随着局部作用域的结束而消亡，函数也将持有它们的状态。这种行为的变量被称为 “闭合变量”

函数可以通过 **func** 关键字来定义，也可以通过 **{ }** 这样的简短的**闭包表达式 (closureexpression)** 来定义

使用 func 关键字定义的函数，如果**它包含了外部的变量**，那么它也是一个闭包。

**函数是引用类型**

**方法 (method)**  定义在类或者协议中的函数,它们有一个隐式的 self 参数

**柯里化函数 (curried function)** 一个函数不是接受多个参数，而是只接受部分参数，然后返回一个**接受其余参数的函数**

**自由函数 (free function)** 不是方法的函数

自由函数和那些在结构体上调用的方法是**静态派发 (statically dispatched)** 的 在编译的时候就已经确定了 

1. 编译器可能能够**内联 (inline)** 这些函数，完全不去做函数调用，而是将函数调用替换为函数中需要执行的代码。优化器还还能够帮助丢弃或者简化那些在编译时就能确定不会被实际执行的代

**多态 (polymorphic)** 特性的手段

1. **子类型和方法重写 (overriding)**
2. **函数重载 (overloading)** 为不同的类型多次写同一个函数的行为
3. **泛型**





### Swift 风格指南

* →  对于命名，在使用时能**清晰表意**是最重要。因为API**被使用的次数要远远多于被声明的 次数**，所以我们应当从使用者的⻆度来考虑它们的名字。尽快熟悉 Swift API 设计准则，   并且在你自己的代码中坚持使用这些准则。
* →  **简洁**经常有助于代码清晰，但是简洁本身不应该独自成为我们编码的目标。 
* →  务必为函数添加文档**注释—特别是泛型函数**。 
* →  类型使用大写字母开头，函数、变量和枚举成员使用小写字母开头，两者都使用驼峰式 命名法。 
* →  使用**类型推断**。省略掉显而易⻅的类型会有助于提高可读性。 
* →  如果**存在歧义或者在进行定义的时候不要使用类型推断**。(比如func就需要显式地指定 返回类型) 
* →  **优先选择结构体**，只在确实需要使用到类特有的特性或者是引用语义时才使用类。 
* →  除非你的设计就是希望某个类被继承使用，否则都应该将它们标记为**fi􏰀nal**。 
* →  除非一个闭包后面立即跟随有左括号，否则都应该使用**尾随闭包(trailingclosure)**的语法。 
* →  使用**guard**来提早退出方法。 
* →  **避免对可选值进行强制解包和隐式强制解包**。它们偶尔有用，但是经常需要使用它们的话往往意味着有其他不妥的地方。
* →  **不要写重复的代码**。如果你发现你写了好几次类似的代码片段的话，试着将它们提取到 一个函数里，并且考虑将这个函数转化为协议扩展的可能性。 
* →  **试着去使用map和reduce**，但这不是强制的。当合适的时候，使用for循环也无可厚非。高阶函数的意义是让代码可读性更高。但是如果使用 reduce 的场景难以理解的话， 强行使用往往事与愿违，这种时候简单的 for 循环可能会更清晰。 
* →  试着去使用不可变值:除非你需要改变某个值，否则都应该使用**let**来声明变量。不过 如果能让代码更加清晰高效的话，也可以选择使用可变的版本。**用函数将可变的部分封 装起来，可以把它带来的副作用进行隔离。** 
* →  Swift的泛型可能会导致非常⻓的函数签名。坏消息是我们现在除了将函数声明强制写成几行以外，对此并没有什么好办法。我们会在示例代码中在这点上保持一贯性，这样 你能看到我们是如何处理这个问题的。 
* →  除非你确实需要，否则**不要使用self.**。不过在闭包表达式中，self是被强制使用的，这 是一个清晰的信号，表明闭包将会捕获 self。 
* →  **尽可能地对现有的类型和协议进行扩展**，而不是写一些全局函数。这有助于提高可读性， 让别人更容易发现你的代码。





### 内建集合类型

#### Array

1. let /var区别 
2. 值类型，深拷贝    而NSArray 类，引用类型  
3. 写时复制















2. 值类型，深拷贝    而NSArray 类，引用类型  

3. **写时复制**  可以保证只在必要的时候进行复制

4. ？可选值  安全问题 

5. map 

   1. 短，更清晰
   2. let 声明，不再改变它的值，也不需要显式指明类型
   3. map实现简单  [源码实现](https://github.com/apple/swift/blob/swift-4.0-branch/stdlib/public/core/Sequence.swift)
   4. 其他好用的函数式编程

6. Filter

   1. 会创建一个全新的数组，并且会对数组中的每个元素都进行操作
   2. 有时会使用contains代替 

7. Reduce

8. FlatMap

   1. 想要对一个数组用一个函数进行 map，但是这个变形函数返回的是另一个数组，
      而不是单独的元素

   2. ```
      let markdownFiles: [String] = // ...
      let nestedLinks = markdownFiles.map(extractLinks)
      let links = nestedLinks.joined()
      􏰁fatMap 将这两个操作合并为一个步骤。
      markdownFiles.􏰁atMap(links) 将直接把所有 Markdown 文件中的所有 URL 放到一个单独的数组里并返回。
      ```

   3. 将不同数组里的元素进行合并

   4. ```
      let suits = ["♠", "♥", "♣", "♦"]
      let ranks = ["J","Q","K","A"]
      let result = suits.flatMap { suit in
      		ranks.map { rank in (suit, rank)
      	}
      }
      ```

9. forEach

   1. **return** forEach中return，不会结束循环，仅仅是不执行闭包后面的代码

10. 切片

    1. 与原数组共用同一块内存



#### Dictioinary

1. 我们使用下标的方式可以得到某个设置的值。字典查找将返回的是**可选值** ,与数组不一样
2. updateValue/remove(at: ) 有时候也很有用
3. **Hashable**协议 字典其实就是**哈希表** 标准库中所有的基本数据类型都是遵守 Hashable 协议的，它们包括字符串，整数，浮点数以及布尔值。**不带有关联值的枚举类型**也会自动遵守 Hashable。 
   1. 当你使用不具有值语义的类型 (比如可变的对象) 作为字典的键时，需要特别小心。如果你在将一个对象用作字典键后，改变了它的内容，它的哈希值和/或相等特性往往也会发生改变，会造成存储错误

#### Set

1. 通过哈希表实现，集合元素需要满足**Hashable**协议

2. 遵守 ExpressibleByArrayLiteral 协议，可能字面量方式初始化

   1. ```
      let naturals: Set = [1, 2, 3, 2]
      ```

3. 集合代数 **补集**/**交集**/**并集**   SetAlgebra

4. **IndexSet/CharacterSet**

   1. ```
      var indices = IndexSet()
      indices.insert(integersIn: 1..<5)
      indices.insert(integersIn: 11..<15)
      let evenIndices = indices.filter { $0 % 2 == 0 } // [2, 4, 12, 14]
      ```

#### Range

1. Range (由 ..< 创建的半开范围) 和 ClosedRange (由 ... 创建的闭合范围)。两者都有一个 Bound 的泛型参数:对于 Bound 的唯一的要求是它必须遵守 Comparable 协议。

   1. ```
      单边的范围
      let fromZero = 0...
      let upToZ = ..<Character("z")
      ```

   2. 

2. → 只有半开范围能表达空间隔(也就是下界和上界相等的情况，比如5..<5)。 

   → 只有闭合范围能包括其元素类型所能表达的最大值(比如0...Int.max)。而半开范围则要 求范围上界是一个比自身所包含的最大值还要大 1 的值。 

   ```
   let fromA: PartialRangeFrom<Character> = Character("a")...
   let throughZ: PartialRangeThrough<Character> = ...Character("z") 
   let upto10: PartialRangeUpTo<Int> = ..<10
   以上不能被迭代
   let fromFive: CountablePartialRangeFrom<Int> = 5... 可以被迭代，但是需要break
   ```

   ```
   对于下界缺失的部分范围，relative(to:) 方法会把集合类型的 startIndex 作为范围下界。对于 上界缺失的部分范围，同样，它会使用 endIndex 作为上界
   let arr = [1,2,3,4] 
   arr[2...] // [3, 4] 
   arr[..<1] // [1] 
   arr[1...2] // [2, 3]
   arr[...] // [1, 2, 3, 4]这种无界范围还不是有效的 RangeExpression类型，不过它应该会在今后遵守 RangeExpression 协议。)
   ```

### 集合类型协议

#### Sequence

1. ```
   protocol Sequence {
   	associatedtype Iterator: IteratorProtocol func makeIterator() -> Iterator
   	// ...
   }
   ```

   ```
   for element in someSequence { 
   	doSomething(with: element)
   }
   ```

#### 迭代器

1. ```
   protocol IteratorProtocol { 
   	associatedtype Element
       mutating func next() -> Element?
   }
   比如 String 的迭代器的元素类型是 Character
   ```

   ```
   public protocol Sequence { 
   	associatedtype Element
   	associatedtype Iterator: IteratorProtocol where Iterator.Element == Element // ...
   }
   ```

2. mutating的意义  迭代器的本质是存在状态的，几乎所有有意义的迭代器都会要求可变状态

   ```
   struct FibsIterator: IteratorProtocol { 
   	var state = (0, 1)
   	mutating func next() -> Int? {
           let upcomingNumber = state.0 state = (state.1, state.0 + state.1) 
           return 	upcomingNumber
   	} 
   }
   ```

3. 迭代器和值语义

   1. **AnyIterator** 是一个对别的迭代器进行封装的迭代器，它可以用来将原来的迭代器的具体类型 “抹消” 掉

      AnyIterator 进行封装的做法是将另外的迭代器包装到一个内部的对象中，而这个对象是**引**
      **用类型**     而**AnyIterator**是一个**结构体**

      ```
      func fibsIterator() -> AnyIterator<Int> { var state = (0, 1)
      return AnyIterator {
      let upcomingNumber = state.0 state = (state.1, state.0 + state.1) return upcomingNumber
      } }
      ```

   2. **AnySequence** 创建序列

      ```
      let fibsSequence = AnySequence(fibsIterator) 
      Array(fibsSequence.prefix(10)) // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
      ```

      ```
      sequence(state: (0,1)) { (state) -> Int? in
          let upcomintNumber = state.0
          state = (state.1,state.0 + state.1)
          return upcomintNumber
      }
      ```



#### 无限序列

      1. sequence 对于 next 闭包的使用是被延迟的。也就是说，序列的下一个值不会被预先计算，它只在调用者需要的时候生成
    
      2. 序列和集合来说，它们之间的一个重要区别就是**序列可以是无限的**，而集合则不行。

#### 不稳定序列

   1. Sequence 协议并不关心遵守该协议的类型是否会在迭代后将序列的元素销毁。也就
      是说，请不要假设对一个序列进行多次的 for-in 循环将继续之前的循环迭代或者是从
      头再次开始:
   2. 一个非集合的序列可能会在第二次 for-in 循环时产生随机的序列元素
   3. 如果一个序列遵守 Collection 协议的话，那就可以肯定这个序列是稳定的了，因为 Collection
      在这方面进行了保证



####子序列 

   ```
   Sequence 还有另外一个关联类型 SubSequence
   protocol Sequence {
       associatedtype Element
       associatedtype Iterator: IteratorProtocol where Iterator.Element == Element 
       associatedtype SubSequence
       // ...
   }
   ```

   * 如果你没有明确指定 SubSequence 的类型，那么编译器会将它推断为AnySequence<Iterator.Element>，这是因为 Sequence 以这个类型作为返回值，为上述方法提供了默认的实现。如果你想要使用你自己的子序列类型，你必须为这些方法提供自定义实现。

#### 链表

1. 在这里使用 **indirect** 关键字可以告诉编译器这个枚举值 node 应该被看做引用

   ```
   enum List<Element> {
   	case end
   	indirect case node(Element, next: List<Element>) 
   }
   ```

2. 遵守Sequence

   ```
   extension List: IteratorProtocol, Sequence { 
   	mutating func next() -> Element? {
   		return pop() 
   	}
   }
   ```

#### 集合类型

1.  是那些稳定的序列，它们能够被多次遍历且保持一致
2. 集合中的元素也可以通过下标索引的方式被获取到
3. 和序列不同，集合类型不能是无限的
4. 除了 Array，Dictionary，Set，String 和它的各种方式以外，
   另外还有 **CountableRange** 和 **UnsafeBufferPointer** 也是集合类型，还有来自Foundation的Data，IndexSet

**队列**，实现可以和算法中结合起来对比

1. ```
   /// ⼀一个能够将元素⼊入队和出队的类型 
   protocol Queue {
       /// 在 `self` 中所持有的元素的类型
       associatedtype Element
       /// 将 `newElement` ⼊入队到 `self`
       mutating func enqueue(_ newElement: Element) /// 从 `self` 出队⼀一个元素
       mutating func dequeue() -> Element? 
   }
   ```

#### 字面量

1. ExpressibleByArrayLiteral/ExpressibleByIntegerLiteral

2. [1, 2, 3] 并不是一个数组，它只是一个 “数组字面量”，是一种写法

3. ```
   let queue: FIFOQueue = [1,2,3]
   ```

#### 关联类型

#### 索引

1. ```
   protocol Collection {
   	subscript(position: Index) -> Element { get }
   }
   ```

2.  Swift 这样一⻔ “安全” 语言不把所有可能失败的操作用可选值或者错误包装起来的原因：“如果所有 API 都可能失败，那你就没法儿写 代码了。你需要有一个基本盘的东西可以依赖，并且信任这些操作的正确性”，否则你的代码将 会深陷安全检查的囹圄。 

#### 自定义索引 todo

切片





### 双重可选值

1. ```
   let stringNumbers = ["1", "2", "three"]
   let maybeInts = stringNumbers.map { Int($0) }
   for maybeInt in maybeInts {
       // maybeInt 是⼀一个 Int? 值
       // 得到两个整数值和⼀一个 `nil`
       print(maybeInt)
   }//Optional(1) Optional(2) nil
   
   这是仍然是Optional(1) Optional(2) nil  ,nil 与.some(nil)是不一样的
   var iterator = maybeInts.makeIterator()
   while let maybeInt = iterator.next() {
       print(maybeInt, terminator: " ")
   }//Optional(1) Optional(2) nil
   
   使用case,只解包Int值
   for case let i? in maybeInts {
       // i 将是 Int 值，⽽而不不是 Int? print(i, terminator: " ")
       print(i)
   }
   for case let .some(i) in maybeInts {
       print(i)
   }
   ```

2. Swift 在区分各种” 无 “类型上非常严密。出了 nil 和 Never，还有 Void，Void 是空元组 (tuple) 的一种写法:

3. ```
   public enum Never { }
   func unimplemented() -> Never {
   	fatalError("This code path is not implemented yet.")
   }
   
   public typealias Void = ()
   
   Swift 对 “东西不存在”(nil)，“存在且为空”(Void) 以及“不可能发生” (Never) 这几个概念进行了仔细的区分。
   ```

#### 可选链

1. ```
   let str: String? = "Never say never"
   let lower = str?.uppercased().lowercased() // Optional("never say never")
   ```

2. ```
   extension Int { 
   var half: Int? {
   	guard self <-1 || self > 1 else { return nil }
   		return self / 2 
   	}
   }
   
   20.half?.half?.half // Optional(2)
   是Int?而不是 Int???。后一种类型可以给我们更多的信息，比如说可选链是在哪个部分解包失败的，但是 这也会让结果非常难以处理，从而让可选链一开始时给我们带来的便利性损失殆尽。
   ```

   ？运算

   ```
   var a: Int? = 5 
   a? = 10
   a // Optional(10)
   
   var b: Int? = nil 
   b? = 10  //b为nil时，什么也不做了
   b // nil
   
   struct Person {
       var name: String
       var age: Int
   }
   var optionalLisa: Person? = Person(name: "Lisa Simpson", age: 8)
   //理解这咱写法
   optionalLisa?.age += 1
   ```

   #### ??

   ```
   if let n=i ?? j {
   	// 和 if i != nil || j != nil 类似
   	print(n) 
   }
   ```

   双重嵌套

   ```
   let s1: String?? = nil // nil
   (s1 ?? "inner") ?? "outer" // inner
   
   let s2: String?? = .some(nil) // Optional(nil)
   (s2 ?? "inner") ?? "outer" // outer
   这是注意的是，(s2 ?? "inner")是String?类型
   ```

   移除可选警告的一种方法

   ```
   public func ???<T>(optional: T?, defaultValue: @autoclosure () -> String) -> String {
   	switch optional {
   	case let value?: return String(describing: value) 
   	case nil: return defaultValue()
   	}
   }
   ```

   Optional的map

   ```
   extension Optional {
   func map<U>(transform: (Wrapped) -> U) -> U? {
   	if let value = self {
   		return transform(value)
   	}
   	return nil
   	}
    }
   ```

   compactMap过滤非nil值

   可选值判等

   ```
   let regex = "^Hello$" // ...
   if regex.first == "^" {　//隐式转换if regex.first == Optional("^") { //or:==.some("^")
   	// 只匹配字符串串开头
   }
   
   //原理
   func ==<T: Equatable>(lhs: T?, rhs: T?) -> Bool { 
   	switch (lhs, rhs) {
   	case (nil, nil): return true
   	case let (x?, y?): return x == y
   	case (_?, nil), (nil, _?): return false
   	}
   }
   ```

   dict与nil

   ```
   var dictWithNils: [String: Int?] = [ "one": 1,"two": 2,"none": nil]
   
   dictWithNils["two"] = nil 
    // ["none": nil, "one": Optional(1)]
   
   
   dictWithNils["two"] = Optional(nil)
   dictWithNils["two"] = .some(nil)
   dictWithNils["two"]? = nil
   // ["none": nil, "one": Optional(1), "two": nil]
   
   ```

   Equatable 和 ==  swift4.2中已遵守Equatable协议

   ```
   extension Optional : Equatable where Wrapped : Equatable {
   }
   ```

   隐式解包可选值

   1. oc遗留
   2. 短暂为nil，使用时非nil

   隐式可选值行为

   **你不能将一个隐式解包的值通过 inout 的方法传递给一个函数:**



### 结构体和类

1. **结构体(和枚举)是值类型，而类是引用类型**。在设计结构体时，我们可以要求编译器保 证不可变性。而对于类来说，我们就得自己来确保这件事情 
2. 内存的管理方式有所不同。结构体可以被直接持有及访问，但是类的实例只能通过引用 来间接地访问。结构体不会被引用，但是会被复制。也就是说，结构体的持有者是唯一 的，但是类的实例却能有很多个持有者。 
3.   使用类，我们可以通过继承来共享代码。而结构体(以及枚举)是不能被继承的。想要在 不同的结构体或者枚举之间共享代码，我们需要使用不同的技术，比如像是组合、泛型 以及协议扩展等。 



当你将一个结构体赋值给一个新的变量时，Swift 会自动对它进行复制。虽然听起来这会很昂贵，不过大部分的复制都会被编译器优化掉，Swift 也竭尽全力让这些复制操作更加高效。实际上，标准库中有很多结构体都使用了写时复制的技术进行实现，

结构体通过在**extension**扩展中定义自定义方法，我们就可以同时保留原来的初始化方法

#### 可变语义

可以为可变变量 screen添加 **didSet**，这样每当 screen 改变时，这个代码块都将被调用

 ```
var screen = Rectangle(width: 320, height: 480) {
	didSet {
		print("Screen changed: \(screen)") 
	}
}
 ```



虽然语义上来说，我们将整个结构体替换为了新的结构体，但是一般来说这不会损失性能，编译器可以原地进行变更。由于这个结构体没有其他所有者，实际上我们没有必要进行复制。不过如果有多个持有者的话，重新赋值意味着发生复制。对于写时复制的结构体，工作方式又会略有不同 



#### mutating

mutating 同时也是 willSet 和 didSet “知道” 合适进行调用的依据:任何 mutating 方法的调用或者隐式的可变 setter 都会触发这两个事件。

sort 和 sorted 的名字选择是有所考究的，确切说，它们的名字遵循了 Swift API 设计 准则。拥有副作用的方法应该用一个祈使动词短语来表示，比如 sort。而不可变的版 本应该以 -ed 或者 -ing 结尾 



mutating 标记的方法也就是结构体上的普通方法，只不过隐式的 self 被标记为了 inout 而已。



#### 写时复制

1. 通过**isKnownUniquelyReferenced**函数来检查某个引用只有一个持有者(只对**Swift**类起作用)

   * 持有_dataForWriting变量，是对原变量的复制,并修改原变量

   * ```
     var _dataForWriting: NSMutableData {
     	mutating get {
     		if !isKnownUniquelyReferenced(&_data) {
     			_data = Box(_data.unbox.mutableCopy() as! NSMutableData) 
     			print("Making a copy")
     		}
     		return _data.unbox 
     	}
     }
     ```

   * 在写操作时，使用_dataForWriting

   * 数组与字典的区别

     * 使用数组下标访问元素时，我们是直接访问内存的位置

     * 字典的下标将会在字典中寻找值，然后将它返回。因为我们是在值语义下处理，所以返回的是找到的值的复制，不会被唯一引用了

     * ```
       struct ContainerStruct<A> { 
       	var storage: A 
       	subscript(s: String) -> A {
       		get { return storage }
       		set { storage = newValue } 
       	}
       }
       var d = ContainerStruct(storage: COWStruct())
       d.storage.change() // No copy 
       d["test"].change() // Copy
       ```

       **Array** 的下标使用了特别的处理,Array 通过使用**地址器** (**addressors**) 的方式实现下标,地址器允许对内存进行直接访问。数组的下标并不是返回元素，而是返回一个元素的地址器。

       **Swift 团队提到过它们希望提取该技术的范式，并将其应用在字典上。**





#### 闭包和可变性

1. Swift 的结构体一般被存储在栈上，而非堆上。不过对于可变结构体，这其实是一种优化:默认情况下结构体是存储在堆上的，但是在绝大多数时候，这个优化会生效，并将结构体存储到栈上。编译器这么做是因为**那些被逃逸闭包捕获的变量需要在栈帧之外依然存在**。当编译器侦测到结构体变量被一个函数闭合的时候，优化将不再生效，此时这个结构体将存储在堆上。这样一来，在我们的例子里，就算 uniqueIntegerProvider 退出了作用域，i 也将继续存在。

#### 内存

**weak** 引用,Swift 中的弱引用是趋零的:当一个弱引用变量所引用的对象被释放时，这个变量将被自动设为 nil。这也是弱引用必须被声明为可选值的原因。

**unowned** 引用,对每个 unowned 的引用，Swift 运行时将为这个对象维护另外一个引用计数。当所有的 strong引用消失时，对象将把它的资源 (比如对其他对象的引用) 释放掉,不过，这个对象本身的内存将继续存在，直到所有的 unowned 引用也都消失

在 **unowned** 和 **weak** 之间进行选择

* 取决于生命周期，如果这些对象的生命周期互不相关，也就是说，你不能保证哪一个对象存在的时间会比另一个⻓，那么弱引用就是唯一的选择。
* 如果你可以保证非强引用对象拥有和强引用对象同样或者更⻓的生命周期的话，unowned 引用通常会更方便一些，不需要处理可选值，可以let声明
* unowned 引用要比 weak 引用少一些性能损耗，因此访问一个 unowned 引用的属性或者调用它上面的方法都会稍微快一些;不过，这个因素应该只在性能非常重要的代码路径上才需要被考虑。
* 建议在 unowned 也可以使用的情况下，还是去选择用 weak。weak 将强制我们在所有使用的地方都去检查引用是否依然有效。我们可能会时不时地对一些代码进行重构，而这可能会导致我们之前对于对象生命周期的假设失效。

#### 闭包和内存

1. 闭包可以捕获变量

2. 循环引用　A->B->B.闭包->A

3. 捕获列表   

4. ```
   window?.onRotate = { [weak view] in
   	print("We now also need to update the view: \(view)")
   }
   
   捕获列表也可以用来初始化新的变量。比如，如果我们想要用一个 weak 变量来引用窗口，我 们可以将它在捕获列表中进行初始化，我们甚至可以定义完全不相关的变量
   window?.onRotate = { [weak view, weak myWindow=window, x=5*5] in
   	print("We now also need to update the view: \(view)")
       print("Because the window \(myWindow) changed")
   }
   ```



### 编码和解码  Codable

  ```
/// 某个类型可以将⾃自身编码为⼀一种外部表示。
public protocol Encodable {
	/// 将值编码到给定的 encoder 中。
	public func encode(to encoder: Encoder) throws 
}
/// 某个类型可以从外部表示中解码得到⾃自身。
public protocol Decodable {
	/// 通过从给定的 decoder 中解码来创建新的实例例。
	public init(from decoder: Decoder) throws
}

public typealias Codable = Decodable & Encodable
  ```

#### JSONEncoder

1. 只有必须的初始化方法 **(required initializers)** 能满足协议的要求，而这类必须的初始化方法不能在扩展中进行添加; 它们必须直接在类的定义中直接进行声明。 
2. 在 Swift 4 中，我们不能为一个非 􏰀nal 的类添加 Codable 特性





### 函数

1. 函数可以像Int或者String那样被赋值给变量，也可以作为另一个函数的输入参数，或 者另一个函数的返回值来使用。 

   ```
   func printInt(i: Int) {
   	print("you passed \(i)") 
   }
   let funVar = printInt
   funVar(2) // you passed 2
   不能在 funVar 调用时包含参数标签，而在 printInt 的调用 (像是 printInt(i: 2)) 却要求有参数标签。Swift 只允许在函数声明中包含标签，这些标签不是函数类型的一部分
   ```

   

2. 函数能够捕获存在于其局部作用域之外的变量。 

   1. 在编程术语里，一个函数和它所捕获的变量环境组合起来被称为闭包

3. 有两种方法可以创建函数，一种是使用func关键字，另一种是{}。在Swift中，后一种 

   被称为闭包表达式。 

   使用闭包表达式来定义的函数可以被想成**函数的字面量**，与 func 相比较，它的区别在于闭包表达式是匿名的，它们没有被赋予一个名字。使用它们的方式只有在它们被创建时将其赋值给一个变量

   ```
   func doubler(i: Int) -> Int {
   	returni*2
   }
   [1, 2, 3, 4].map(doubler) // [2, 4, 6, 8]
   
   let doublerAlt = { (i: Int) -> Int in return i*2 }
   [1, 2, 3, 4].map(doublerAlt) // [2, 4, 6, 8]
   
   [1, 2, 3].map { $0 * 2 } // [2, 4, 6]
   ```

   1. 如果你将闭包作为参数传递，并且你不再用这个闭包做其他事情的话，就没有必要现将 它存储到一个局部变量中。可以想象一下比如 5*i 这样的数值表达式，你可以把它直接 传递给一个接受 Int 的函数，而不必先将它计算并存储到变量里。 

   2. 如果编译器可以从上下文中推断出类型的话，你就不需要指明它了。在我们的例子中， 从数组元素的类型可以推断出传递给 map 的函数接受 Int 作为参数，从闭包的乘法结果 的类型可以推断出闭包返回的也是 Int。 

   3. 如果闭包表达式的主体部分只包括一个单一的表达式的话，它将自动返回这个表达式的 结果，你可以不写 return。 

   4. Swift会自动为函数的参数提供简写形式，$0代表第一个参数，$1代表第二个参数，以 此类推。 

   5. 如果函数的最后一个参数是闭包表达式的话，你可以将这个闭包表达式移到函数调用的 圆括号的外部。这样的尾随闭包语法在多行的闭包表达式中表现非常好，因为它看起来 更接近于装配了一个普通的函数定义，或者是像 if (expr) { } 这样的执行块的表达形式。 

   6. 最后，如果一个函数除了闭包表达式外没有别的参数，那么方法名后面的调用时的圆括 号也可以一并省略。 

   7. ```
      [1, 2, 3].map( { (i: Int) -> Int in return i * 2 } ) 
      [1, 2, 3].map( { i in return i * 2 } )
      [1, 2, 3].map( { i in i * 2 } )
      [1, 2, 3].map( { $0 * 2 } )
      [1, 2, 3].map() { $0 * 2 } [1,2,3].map{$0*2}
      ```

      









### 推荐的书

[《集合类型优化》](https://objccn.io/products/optimizing-collections/)

其中有队列的实现/swift算法也有，多种实现，看看