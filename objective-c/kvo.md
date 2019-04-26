## KVO

KVO的全称是Key-Value Observing，俗称“键值监听”，可以用于监听某个对象属性值的改变

### 原理

1. **添加观察者**
   当一个Person对象p，添加如下方法后，`p->isa`会指向一个`NSKVONotifying_Person`类对象，是通过runtime 生成的一个Person的子类，

`- (void)addObserver:(NSObject *)observer forKeyPath:(NSString *)keyPath options:(NSKeyValueObservingOptions)options context:(nullable void *)context;`

```

Person *p = [[Person alloc] init];
NSLog(@"%@",object_getClass(p));// Person
NSLog(@"%p",[p methodForSelector:@selector(setWeight:)]);
//先找到地址，再通过p (IMP)[地址]　得到-[Person setWeight:](int) at main.mm:35)


[p addObserver:nil forKeyPath:@"weight" options:(NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld) context:nil];

NSLog(@"%@",object_getClass(p)); //NSKVONotifying_Person
NSLog(@"%p",[p methodForSelector:@selector(setWeight:)]);
//先找到地址，再通过p (IMP)[地址]　得到 (Foundation`_NSSetIntValueAndNotify)
```

2. **修改属性值之后会通知观者者属性改变**

   ```
   p.weight = 100l
   ```

   NSKVONotifying_Person类会重新生成新setWeight方法，伪代码如下

   ```
   - (void)setWeight:(int)weight {
       _NSSetIntValueAndNotify();
   }
   // 伪代码
   void _NSSetIntValueAndNotify()
   {
       [self willChangeValueForKey:@"age"];
       [super setAge:age];
       [self didChangeValueForKey:@"age"];
   }
   - (void)didChangeValueForKey:(NSString *)key
   {
       // 通知监听器，某某属性值发生了改变
       [oberser observeValueForKeyPath:key ofObject:self change:nil context:nil];
   }
   ```

#### _NSSet*ValueAndNotify的内部实现

```
[self willChangeValueForKey:"<#(nonnull NSString *)#>"]
//原来的setter实现
[self didChangeValueForKey:<#(nonnull NSString *)#>]

调用didChangeValueForKey:后内部会调用observer的observeValueForKeyPath:ofObject:change:context:方法
```

使用了KVO监听的对象，除了setter方法，还会有以方法，可以通过runtim打印NSKVONotifying_Person的方法

```
class  混淆类名,屏蔽KVO细节
dealloc　//
_isKVOA
```



### 练习题

1. iOS用什么方式实现对一个对象的KVO？(KVO的本质是什么？)

   **Todo：**修改

   ![image-20190411233804301](/Users/hailong/Library/Application Support/typora-user-images/image-20190411233804301.png)

2. 如何手动触发KVO？

   ```
   willChangeValueForKey
   didChangeValueForKey
   必须同时使用，一个不调，不会触发
   ```

3. 直接修改成员变量会触发KVO么？