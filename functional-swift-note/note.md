#functional-swift

### 特质

1. 模块化　
   * 倾向于强调每个 程序都能够被反复分解为越来越小的**模块单元**，而所有这些块可以**通过函数装配**起来， 以定义一个完整的程序。当然，只有当我们能够避免在两个独立组件之间共享状态时， 才能将一个大型程序分解为更小的单元 
2. 对可变状态的谨慎处理 
   * 强调基于值编程的重要性,通过避免可变状态，函数式程序比其对应的命令式或者面向对象的程序更容易组合
3. 类型
   * 精心选择数据和函数的类型，将会有助于构建你的代码 

思想还是拆分、组合，拆分时谨慎的选择类型

```
   从头写到尾
   func canSafelyEngageShip(target: Ship) -> Bool {
        let dx = target.position.x - position.x
        let dy = target.position.y - position.y
        let targetDistance = sqrt(dx * dx + dy * dy)
        return targetDistance <= firingRange && targetDistance > unsafeRange
    }
    func canSafelyEngageShip1(target: Ship, friendly: Ship) -> Bool {
        let dx = target.position.x - position.x
        let dy = target.position.y - position.y
        let targetDistance = sqrt(dx * dx + dy * dy)
        
        let friendlyDx = friendly.position.x - target.position.x
        let friendlyDy = friendly.position.y - target.position.y
        let friendlyDistance = sqrt(friendlyDx * friendlyDx +
            friendlyDy * friendlyDy)
        return targetDistance <= firingRange && targetDistance > unsafeRange
            && (friendlyDistance > unsafeRange)
    }
优化　添加协助函数，更加清晰
    func canSafelyEngageShip2(target: Ship, friendly: Ship) -> Bool {
        let targetDistance = target.position.minus(position).length
        let friendlyDistance = friendly.position.minus(target.position).length
        return targetDistance <= firingRange
            && targetDistance > unsafeRange && (friendlyDistance > unsafeRange)
    }
继续优化　借助函数，选择函数类型，拆分、组合　
	typealias Region = (Position) -> Bool
	func canSafelyEngageShip(target: Ship, friendly: Ship) -> Bool {
        let rangeRegion = difference(circle(firingRange), minus: circle(unsafeRange))
        let firingRegion = shift(rangeRegion, offset: position)
        let friendlyRegion = shift(circle(unsafeRange),offset: friendly.position)
        let resultRegion = difference(firingRegion, minus: friendlyRegion)
        return resultRegion.lookup(target.position)
    }
继续优化　用结构体包装函数，实现时，提示更友好
    struct Region {
        let lookup: (Position) -> Bool
    }
    func canSafelyEngageShip(target: Ship, friendly: Ship) -> Bool {
        let rangeRegion = difference(circle(firingRange), minus: circle(unsafeRange))
        let firingRegion = shift(rangeRegion, offset: position)
        let friendlyRegion = shift(circle(unsafeRange),offset: friendly.position)
        let resultRegion = difference(firingRegion, minus: friendlyRegion)
        return resultRegion.lookup(target.position)
    }

```



### 封装core image

1. typealias Filter = (CIImage) -> CIImage   

2. //复合函数 组合滤镜

   func composeFilters(filter1: @escaping Filter , _ filter2 : @escaping Filter ) -> Filter {

   ​    return { image in

   ​        filter2 (filter1 (image))

   ​    }

   }

3. `>>>`操作符, 优先级别名

   1. ```
      infix operator >>> : HLPrecedence
      precedencegroup HLPrecedence { //定义运算符优先级ATPrecedence
          associativity: left//指定结合性
          higherThan: AdditionPrecedence//指定优先级
          lowerThan: MultiplicationPrecedence
      }
      func >>> ( filter1 : @escaping Filter , filter2 : @escaping Filter ) -> Filter {
          return { image in
              filter2 ( filter1 (image))
          }
      }
      //使用泛型之后更加强大
      func >>> <A, B, C>(f: @escaping (A) -> B, g: @escaping (B) -> C) -> (A) -> C {
          return { x in g(f(x)) }
      }
      
      ```

4. 柯里化　一个接受多参数的函数变换为一系列只接受单个参数的函数，这个过程被称为柯里化 (Currying)

   ```
   func add(_ x:Int) -> ((Int) -> Int) {
       return { y in
           return x + y
       }
   }
   省略写法　
   func add2(_ x:Int) -> ((Int) -> Int) {
       return { y in x + y }
   }
   add(1)(2)
   ```

5. 仔细相想过滤函数正是柯里化的结果　**多参转为了单参**

   ```
   typealias Filter = (CIImage) -> CIImage
   func blur(radius: Double) -> Filter { }
   func colorGenerator(_ color: UIColor) -> Filter { }
   func compositeSourceOver(_ overlay: CIImage) -> Filter { }
   func colorOverlay(color:UIColor)->Filter { 
   ```

6. 优点

   * 安全  从类型上看
   * 模块化 　组合使用
   * 清晰易懂 　使用封装之后的函数，不关心内部实现

### Map、Filter 和 Reduce

1. map 泛型使之应用更广泛

   ```
   func map<Element, T>(xs: [Element], transform: (Element) -> T) -> [T] {
       var result: [T] = []
       for x in xs {
           result.append(transform(x))
       }
       return result
   }
   extension Array {
       func map<T>(transform: (Element) -> T) -> [T] {
           var result: [T] = []
           for x in self {
               result.append(transform(x))
           }
           return result
       }
   }
   ```

2. Filter    类似map

   ```
   extension Array {
       func filter (includeElement: (Element) -> Bool) -> [Element] {
           var result: [Element] = []
           //for中where可以学习一下
           for x in self where includeElement(x) {
               result.append(x)
           }
           return result
       }
   }
   
   ```

3. Reduce

   * 将变量 result 初始化为某个值。随后对输入数组 xs 的每一项进行遍历，最后以某种方式更新结果

   ```
   extension Array {
       func reduce<T>(initial: T, combine: (T, Element) -> T) -> T {
           var result = initial
           for x in self {
               result = combine(result, x)
           }
           return result
       }
       //Reduce 实现map　　主要还是初始一个空数组，
       //但运行期间大量复制生成的数组
   	func mapUsingReduce<T>(transform: (Element) -> T) -> [T] {
           return reduce([]) { result, x in
               return result + [transform(x)]
           }
       }
   	//Reduce 实现filter
   	func filterUsingReduce(includeElement: (Element) -> Bool) -> [Element] {
           return reduce([]) { result, x in
       	    return includeElement(x) ? result+[x] : result
           }
       }
   }
   ```

4. 泛型和 Any 类型

   * 泛型可以用于定义灵活的函数，类型检查仍然由编译器负责;而 Any 类型则可以避开 Swift 的类型系统 (所以应该尽可能避免使用)。

### 可选值　？

1. **??**

   ```
   infix operator ??
   func ??<T>(optional: T?, defaultValue: T) -> T {
       if let x = optional {
           return x
       } else {
           return defaultValue
       }
   }
   使用@autoclosure优化，当defaultValue是一个表达式或函数时，不会立即对defaultValue求值
   func ??<T>(optional: T?, defaultValue: @autoclosure () -> T) -> T {
       if let x = optional {
           return x
       } else {
           return defaultValue()
       }
   }
   ```

2. **可选值链**  当任意一个组成项失败时，整条语句链将返回 nil

   ```
   if let myState = order.person?.address?.state { 
   	print("This order will be shipped to \(myState)")
   } else {
   	print("Unknown person, address, or state.")
   }
   ```

3. 分支上的可选值

   ```
   let a: Int? = 20
   switch a {
   case 0?:
       print(5)
   case (1..<10)?:
       print("1..<10")
   case .some(let x):
       print("x",x)
   case .none:
       print("nil")
   }
   ```

4. **guard** 让控制流比嵌套 if let 语句时更简单

5. 可选映射

   ```
   extension Optional {
       func map<U>(transform: (Wrapped) -> U) -> U? {
           guard let x = self else { return nil }
           return transform(x)
       }
   	func flatMap<U>(f: (Wrapped) -> U?) -> U? {
           guard let x = self else { return nil }
           return f(x)
       }
   }
   ```

   可选值的运算等操作可以用这个map映射

6. 选择显式的可选类型更符合 Swift 增强**静态安全**的特性,有助于避免由缺失值导致的意外崩溃

   ```
   if ([ someString rangeOfString:@"swift"].location != NSNotFound) {
   	NSLog(@"Someone mentioned swift!"); 
   }
   一眼看不出问题，但someString==nil时，if中语句会执行
   ```



### QuickCheck

[SwiftCheck](https://github.com/typelift/SwiftCheck)

1. `func arbitrary() -> Self ` 随机

2. `func smaller() -> Self?`范围缩小

3. 结构体包装　

   ```
   struct ArbitraryInstance<T> {
       let arbitrary: () -> T
       let smaller: (T) -> T?
   }
   ```

4. **疑问** 可以实现啊

   ```
   extension Array:Arbitrary where Element:Arbitrary {
       static func arbitrary() -> [Element] {
       // ...
           return [Element.arbitrary(),Element.arbitrary()]
       }
   }
   ```

5. Check 检验, 根据判断条件打印结果

   `func check2<A:Arbitrary>(message:String,_ property:(A) -> Bool) `

   `func checkHelper<A>(arbitraryInstance: ArbitraryInstance<A>, _ property: (A) -> Bool, _ message: String) -> ()`



### 不可变性的价值

1. 变量和引用
   1. let 不可变变量不能被赋以新值,在阅读代码时，let更容易使用理解
   2. let 作用域
      * 修饰类/结构体　
      * 修饰成员变量
   3. let降低耦合
2. 值类型与引用类型
   1. 在 Swift 中，结构体并不是唯一的值类型。事实上，Swift 几乎所有类型都是值类型，包括数
      组，字典，数值，布尔值，元组和枚举 。只有类 (class) 是一个例外
   2. 其实数组，字典，数值，布尔值，元组和枚举 都是结构体
   3. Swift 中比起引用类型更倾向于优先选择值类型一样
   4. 
3. **注意**：如果我们在结构体中保存了一个对象，引用是不可变的，但是对象本身却可以
   改变
   1. Swift数组就是这样的:它们使用低层级的可变数据结构，但提供一个高效且不可变的接
      口。这里使用了一个被称为写入时复制 (copy-on-write) 的技术
4. 引用透明函数:输入值相同则得到的输出值一定相同的函数
5. 错误处理　rethrows　＋　try



### 枚举  "和类型"

1. 与oc不同，与整数没有关系

2. 关联值＋泛型

   * ```
     enum Result<T> {
     	case Success(T)
     	case Error(ErrorType)
     }
     
     func ??<T>(result: Result<T>, handleError: ErrorType -> T) -> T { switch result {
     case let .Success(value): return value
     caselet .Error(error): return handleError(error)
     } }
     ```

3. **同构** 如果两个类型 A 和 B 在相互转换时不会丢失任何信息

   ```
   f : A -> B
   g: B -> A
   对 x: A ,调用g(f(x)) = x
   对 y: B，调用f(g(y)) = y
   ```

   使用枚举和多元组定义的类型有时候也被称作**代数数据类型** (algebraic data types)，因为它们
   就像自然数一样，具有代数学结构。

4. 枚举实现BinarySearchTree注意点

   * 递归思想

   * 插入时，值类型的改

   * ```
     mutating func insert(_ value:Element) {
         switch self {
         case .Leaf:
             self = BinarySearchTree(value)
        //注意这是是var     
         case .Node(var left,let node,var right):
             if value < node {
                  left.insert(value)
     
             } else if value > node {
                 return right.insert(value)
             } else {
     //            相等怎么办？,暂不考虑
     //			学数据结构和算法时再回来完善
             }
     //            值类型赋值，
             self = BinarySearchTree.Node(left: left, node: node, right: right)
         }
     }
     ```




### 纯函数式数据结构 (Purely Functional Data Structures)

1. 枚举递归　**BinarySearchTree**

2. ***Trie***，又称字典树、单词查找树或键树，是一种特定类型的有序树，通常
   被用于搜索由一连串字符组成的字符串

3. 快排的另一种实现方式,容易忘记把left/right再排序

   ```
   func qsort(input:[Int]) -> [Int] {
       guard let (head,tail) = input.decompose else {
           return []
       }
       let left = tail.filter { $0 < head}
       let right = tail.filter { $0 >= head}
   //    容易忘qsort
       return qsort(input: left) + [head] + qsort(input: right)
   }
   ```

4. 纯函数式数据结构



### 图表

1. 组合的思想，要做什么，拆分成什么，怎么组合起来？

   ```
   /// 图形的形状
   enum Primitive {//原始的
       case Ellipse
       case Rectangle
       case Text(String)
   }
   ///图表，图形和图表组合而成
   indirect enum Diagram {
       case Prim(CGSize, Primitive)
       case Beside(Diagram, Diagram)
       case Below(Diagram, Diagram)
       case Attributed(Attribute, Diagram)
       case Align(CGVector, Diagram)
   ｝
   /// 图表绘制属性
   enum Attribute {
   //    现在只支持 FillColor,可以扩展
       case FillColor(UIColor)
   }
   核心代码，自定义view去绘制　diagram，遇到包含diagram的diagram时，递归去绘制
   func draw(_ bounds: CGRect, _ diagram: Diagram) {
       switch diagram {
       case .Prim(let size, .Ellipse ):
           let frame = size.fit(CGVector(dx: 0.5, dy: 0.5), bounds)
           fillEllipse(in: frame)
       case .Prim(let size, .Rectangle):
           let frame = size.fit(CGVector(dx: 0.5, dy: 0.5), bounds)
           fill(frame)
       case .Prim(let size, .Text(let text)):
           let frame = size.fit(CGVector(dx: 0.5, dy: 0.5), bounds)
           let font = UIFont.systemFont(ofSize: 12)
           let attributes = [NSAttributedStringKey.font: font]
           let attributedText = NSAttributedString(string: text, attributes: attributes)
           attributedText.draw(in: frame)
       case .Attributed(.FillColor(let color), let d):
           saveGState()
           color.set()
           draw(bounds, d)
           restoreGState()
       case.Beside(let left, let right):
           let (lFrame, rFrame) = bounds.split(
               left .size.width/diagram.size.width, edge: .minXEdge)
           draw(lFrame, left)
           draw(rFrame, right)
       case .Below(let top, let bottom):
           let (lFrame, rFrame) = bounds.split(
               bottom.size.height/diagram.size.height, edge: .minYEdge)
           draw(lFrame, bottom)
           draw(rFrame, top)
       case .Align(let vec, let diagram):
           let frame = diagram.size.fit(vec, bounds)
           draw(frame, diagram)
       }
   }
   ```

   ![](./images/draw.png)

2. 额外的**组合算子**(函数)，更便于拆分、计算、使用

3. 遇到问题`override func draw(_ rect: CGRect) {`背景色，可以在初始时，或`addSubView`时设置



### 迭代器和序列

**感觉这一章应多看苹果的文档**

##### [IteratorProtocol](apple-reference-documentation://hs4mpnEfOq)

1. 可以实现各种迭代器，eg:CountdownGenerator/PowerGenerator

   ```
   while let i = generator.next() {
   	//code
   }
   ```

2. 给迭代器加过滤条件

   ```
   仅演示一个例子，注意条件，小心死循环
   func findPower(predicate:((Decimal) -> Bool)) -> Decimal? {
       while let power = next() {
           if predicate(power) {
              return power
           }
       }
       return nil
   }
   mutating func find(predicate:((Element) -> Bool)) -> Element? {
       while let ele = next() {
           if predicate(ele) {
               return ele
           }
       }
       return nil
   }
   
   ```

3. associatedtype　关联类型，生成各种类型的迭代器

4. 组合，**可以给自身加一些限制**

   ```
   class LimitGenerator<G:GeneratorType>: GeneratorType {
       var limit = 0
       var generator:G
       init(limit:Int,generator:G) {
           self.limit = limit
           self.generator = generator
       }
       func next() -> G.Element? {
           guard limit > 0 else {
               return nil
           }
           limit -= 1
           return generator.next()
       }
   }
   ```

5. AnyIterator,传入一个Iterator

   ```
   public init<I>(_ base: I) where Element == I.Element, I : IteratorProtocol
   ```

6. 迭代器的组合运算 **+**

   ```
   func +<G:IteratorProtocol,H:IteratorProtocol>(g:G,h:H) -> AnyIterator<G.Element> where G.Element == H.Element  {
       var g = g
       var h = h
       return AnyIterator {
           return g.next() ?? h.next()
       }
   }
   ```

##### [Sequence](apple-reference-documentation://hsApfy9noD)

1. 基本定义，cmd点击跳转api头文件里时，需要滑动才能找到

   ```
   func makeIterator() -> Self.Iterator
   associatedtype Iterator
   associatedtype Element
   ```

2. 还有map等各种方法，不一一举例

###### 结合起来

1. 组合起来直接使用for循环

   ```
   struct Countdown: Sequence {
       let start: Int
   
       func makeIterator() -> CountdownIterator {
           return CountdownIterator(self)
       }
   }
   struct CountdownIterator: IteratorProtocol {
       let countdown: Countdown
       var times = 0
   
       init(_ countdown: Countdown) {
           self.countdown = countdown
       }
   
       mutating func next() -> Int? {
           let nextNumber = countdown.start - times
           guard nextNumber > 0
               else { return nil }
   
           times += 1
           return nextNumber
       }
   }
   
   //使用
   let threeTwoOne = Countdown(start: 3)
   for count in threeTwoOne {
       print("\(count)...")
   }
   // Prints "3..."
   // Prints "2..."
   // Prints "1..."
   ```

2. 树遍历　与　smaller的迭代器使用



### 解析器(*Parser*)组合算子

1. 解析器合并

### 构建一个表格应用



文中多次提到　Haskell有机会可以了解一下





#calculator　字符串解析计算实现原理总结

1. 先使用parser对字符串进行解析，中间使用多咱函数式运算，解析成一个Expression
2. 然后对Expression进行运算
3. 最难的部分还是字符串解析这块，虽然有几个点没有弄懂但还是受益匪浅





没懂的地方

1. 虽然使用lazy可以叫名死循环，但是为什么要用parser呢？难道是为了防止别的地方用这个/

   ```
       static var primitiveParser: Parser<Expression> {
   //        lazy　使用lazy巧妙的避免死循环
           return intParser <|> referenceParser <|> functionParser <|> lazy(parser).parenthesized
       }
   ```

2. `let multiplier = curry( {($0,$1)} ) <^> (string("*") <|> string("/")) <*> primitiveParser`

真不容易弄懂





# 总结

1. 在面向对象的编程思想中, 我们将要解决的一个个问题, 抽象成一个个类, 通过给类定义属性和方法, 让类帮助我们解决需要处理的问题.(其实面向对象也叫命令式编程, 就像给对象下一个个命令)
2. 而在函数式编程中, 我们则通过函数描述我们要解决的问题, 以及解决问题需要怎样的方案.函数本身可以作为变量, 作为参数, 作为返回值
3. 









```
func curry<A, B, C>(_ f: @escaping (A, B) -> C) -> (A) -> (B) -> C {
    return { a in { b in f(a, b) } }
}
let multiplier = curry({ ($0, $1) }) <^> (string("*") <|> string("/")) <*> primitiveParser
可以转化成
let multiplier = strParser.map { (str) -> (Expression) -> (String,Expression) in
            return curry { (a, b) -> (String,Expression) in
                return (a,b)
            }(str)
        } <*> primitiveParser



其中
primitiveParser: Parser<Expression>
(string("*") <|> string("/"))　是 Parser<String>类型
let multiplier: Parser<(String, Expression)>

infix operator <^>: SequencePrecedence
func <^><A, B>(lhs: @escaping (A) -> B, rhs: Parser<A>) -> Parser<B> {
    return rhs.map(lhs)
}

```

