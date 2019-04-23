## Category

把oc分类代码转c++
`xcrun -sdk iphoneos clang -arch arm64 -rewrite-objc HLUnionTest+zhl.m -o HLUnionTest+zhl.cpp`
编译后的cpp文件与runtime中的源码有细微差别

底层结构可以查看runtime源码

```
struct category_t {
    const char *name;
    classref_t cls;
    struct method_list_t *instanceMethods;
    struct method_list_t *classMethods;
    struct protocol_list_t *protocols;
    struct property_list_t *instanceProperties;
    // Fields below this point are not always present on disk.
    struct property_list_t *_classProperties;
    method_list_t *methodsForMeta(bool isMeta) {
        if (isMeta) return classMethods;
        else return instanceMethods;
    }
    property_list_t *propertiesForMeta(bool isMeta, struct header_info *hi){
        if (!isMeta) return instanceProperties;
        else if (hi->info()->hasCategoryClassProperties()) return _classProperties;
        else return nil;
    }
};

static struct _category_t _OBJC_$_CATEGORY_HLUnionTest_$_zhl __attribute__ ((used, section ("__DATA,__objc_const"))) = 
{
	"HLUnionTest",
	0, // &OBJC_CLASS_$_HLUnionTest,
	(const struct _method_list_t *)&_OBJC_$_CATEGORY_INSTANCE_METHODS_HLUnionTest_$_zhl,//test1方法
	0,
	0,
	0,
};

struct _objc_method {
	struct objc_selector * _cmd;
	const char *method_type;
	void  *_imp;
};

static struct /*_method_list_t*/ {
	unsigned int entsize;  // sizeof(struct _objc_method)
	unsigned int method_count;
	struct _objc_method method_list[2];
} _OBJC_$_CATEGORY_INSTANCE_METHODS_HLUnionTest_$_zhl __attribute__ ((used, section ("__DATA,__objc_const"))) = {
	sizeof(_objc_method),
	2,
	{{(struct objc_selector *)"test1", "v16@0:8", (void *)_I_HLUnionTest_zhl_test1},
	{(struct objc_selector *)"test2", "v16@0:8", (void *)_I_HLUnionTest_zhl_test2}}
};

static void _I_HLUnionTest_zhl_test1(HLUnionTest * self, SEL _cmd) {
}

static void _I_HLUnionTest_zhl_test2(HLUnionTest * self, SEL _cmd) {
}
从这里_objc_method赋值也可以看出IMP是函数地址
```

分类结构如上，会存一些相关信息，

runtime源码入口
`objc-os.mm` => _objc_init/map_images/map_images_nolock
`objc-runtime-new.mm`=> _read_images/remethodizeClass/attachCategories/attachLists/realloc、memmove、memcpy

```
void _objc_init(void)
{
    static bool initialized = false;
    if (initialized) return;
    initialized = true;
    
    // fixme defer initialization until an objc-using image is found?
    environ_init();
    tls_init();
    static_init();
    lock_init();
    exception_init();

    _dyld_objc_notify_register(&map_images, load_images, unmap_image);
}

```

```
  if (cat->classMethods  ||  cat->protocols  
            ||  (hasClassProperties && cat->_classProperties)) 
        {
            addUnattachedCategoryForClass(cat, cls->ISA(), hi);
            if (cls->ISA()->isRealized()) {
                remethodizeClass(cls->ISA());
            }
            if (PrintConnecting) {
                _objc_inform("CLASS: found category +%s(%s)", 
                             cls->nameForLogging(), cat->name);
            }
        }

static void attachCategories(Class cls, category_list *cats, bool flush_caches)
{
    if (!cats) return;
    if (PrintReplacedMethods) printReplacements(cls, cats);

    bool isMeta = cls->isMetaClass();

    // fixme rearrange to remove these intermediate allocations
    //方法二维数组
    method_list_t **mlists = (method_list_t **)
        malloc(cats->count * sizeof(*mlists));
		//属性二维数组
    property_list_t **proplists = (property_list_t **)
        malloc(cats->count * sizeof(*proplists));
		//协议二维数组
    protocol_list_t **protolists = (protocol_list_t **)
        malloc(cats->count * sizeof(*protolists));

    // Count backwards through cats to get newest categories first
    int mcount = 0;
    int propcount = 0;
    int protocount = 0;
    int i = cats->count;
    bool fromBundle = NO;
    while (i--) {
    		//取出包含(分类和头信息)的实体包装类
        auto& entry = cats->list[i];

				//分类方法列表
        method_list_t *mlist = entry.cat->methodsForMeta(isMeta);
        if (mlist) {
        //方法列二维表数组
            mlists[mcount++] = mlist;
            fromBundle |= entry.hi->isBundle();
        }

        property_list_t *proplist = 
            entry.cat->propertiesForMeta(isMeta, entry.hi);
        if (proplist) {
            proplists[propcount++] = proplist;
        }

        protocol_list_t *protolist = entry.cat->protocols;
        if (protolist) {
            protolists[protocount++] = protolist;
        }
    }

//取出类的rw 含有类的方法列表、属性列表、协议列表 信息
    auto rw = cls->data();

//将分类的方法列表、属性列表、协议列表　附加到类的rw中

    prepareMethodLists(cls, mlists, mcount, NO, fromBundle);
    rw->methods.attachLists(mlists, mcount);
    free(mlists);
    if (flush_caches  &&  mcount > 0) flushCaches(cls);

    rw->properties.attachLists(proplists, propcount);
    free(proplists);

    rw->protocols.attachLists(protolists, protocount);
    free(protolists);
}

//memmove 与memcpy　可以看出，先移动再拷贝　会把新的分类(最后编译)中的方法列表、属性列表、协议列表放在数组的前面，因此同样的方法会优先调用分类
void attachLists(List* const * addedLists, uint32_t addedCount) {
    if (addedCount == 0) return;

    if (hasArray()) {
        // many lists -> many lists
        uint32_t oldCount = array()->count;
        uint32_t newCount = oldCount + addedCount;
        setArray((array_t *)realloc(array(), array_t::byteSize(newCount)));
        array()->count = newCount;
        memmove(array()->lists + addedCount, array()->lists, 
                oldCount * sizeof(array()->lists[0]));
        memcpy(array()->lists, addedLists, 
               addedCount * sizeof(array()->lists[0]));
    }
    else if (!list  &&  addedCount == 1) {
        // 0 lists -> 1 list
        list = addedLists[0];
    } 
    else {
        // 1 list -> many lists
        List* oldList = list;
        uint32_t oldCount = oldList ? 1 : 0;
        uint32_t newCount = oldCount + addedCount;
        setArray((array_t *)malloc(array_t::byteSize(newCount)));
        array()->count = newCount;
        if (oldList) array()->lists[addedCount] = oldList;
        memcpy(array()->lists, addedLists, 
               addedCount * sizeof(array()->lists[0]));
    }
}

```

### Category加载处理过程

1. 通过Runtime加载某个类的所有Category数据
2. 把所有Category的方法、属性、协议数据，合并到一个大数组中后面参与编译的Category数据，会在数组的前面
3. 将合并后的分类数据（方法、属性、协议），插入到类原来数据的前面



extension　只不过把.h中的声明放在.m中私有化　是编译时已合并到内存中，category通过runtime加载

memmove与memcpy区别

```
3412 memcpy 前两个  会变成3332
3413 memmove    3342
```

### load

+load方法会在runtime加载类、分类时自动调用(+load方法是根据方法地址直接调用，并不是经过objc_msgSend函数调用)，手动调用会经过objc_msgSend
**每个类、分类的+load，在程序运行过程中只调用一次**

**调用顺序**

1. 先调用类的+load
   按照编译先后顺序调用（先编译，先调用）
   调用子类的+load之前会先调用父类的+load(添加loadable_class列表时，先添加superclass)
2. 再调用分类的+load
   按照编译先后顺序调用（先编译，先调用，与普通方法不一样）
   会将分类的+load方法添加在loadable_categories中

```
_objc_init ->load_image->
		prepare_load_methods->
				schedule_class_load
				add_class_to_loadable_list
				add_category_to_loadable_list
		call_load_methods->
				call_class_loads
						(*load_method)(cls, SEL_load)
				call_category_loads
				

```



```
void load_images(const char *path __unused, const struct mach_header *mh){
    // Return without taking locks if there are no +load methods here.
    if (!hasLoadMethods((const headerType *)mh)) return;

    recursive_mutex_locker_t lock(loadMethodLock);

    // Discover load methods
    {
        mutex_locker_t lock2(runtimeLock);
        prepare_load_methods((const headerType *)mh);
    }

    // Call +load methods (without runtimeLock - re-entrant)
    call_load_methods();
}

//预加载会先加载+load方法
void prepare_load_methods(const headerType *mhdr)
{
    size_t count, i;

    runtimeLock.assertLocked();

    classref_t *classlist = 
        _getObjc2NonlazyClassList(mhdr, &count);
    for (i = 0; i < count; i++) {
        schedule_class_load(remapClass(classlist[i]));
    }
//所有的分类列表会调用add_category_to_loadable_list
    category_t **categorylist = _getObjc2NonlazyCategoryList(mhdr, &count);
    for (i = 0; i < count; i++) {
        category_t *cat = categorylist[i];
        Class cls = remapClass(cat->cls);
        if (!cls) continue;  // category for ignored weak-linked class
        realizeClass(cls);
        assert(cls->ISA()->isRealized());
        add_category_to_loadable_list(cat);
    }
}

static void schedule_class_load(Class cls){
    if (!cls) return;
    assert(cls->isRealized());  // _read_images should realize

    if (cls->data()->flags & RW_LOADED) return;

		//会先将父类的load方法添加到loadable_list
    // Ensure superclass-first ordering
    schedule_class_load(cls->superclass);
		//再将当前类添加到loadable_list
    add_class_to_loadable_list(cls);
    cls->setInfo(RW_LOADED); 
}


IMP _category_getLoadMethod(Category cat){
    runtimeLock.assertLocked();

    const method_list_t *mlist;

    mlist = cat->classMethods;
    if (mlist) {
        for (const auto& meth : *mlist) {
            const char *name = sel_cname(meth.name);
            if (0 == strcmp(name, "load")) {
                return meth.imp;
            }
        }
    }
    return nil;
}

void add_category_to_loadable_list(Category cat)
{
    IMP method;

    loadMethodLock.assertLocked();

		//得到＋load方法
    method = _category_getLoadMethod(cat);

    // Don't bother if cat has no +load method
    if (!method) return;

    if (PrintLoading) {
        _objc_inform("LOAD: category '%s(%s)' scheduled for +load", 
                     _category_getClassName(cat), _category_getName(cat));
    }
    
    if (loadable_categories_used == loadable_categories_allocated) {
        loadable_categories_allocated = loadable_categories_allocated*2 + 16;
        loadable_categories = (struct loadable_category *)
            realloc(loadable_categories,
                              loadable_categories_allocated *
                              sizeof(struct loadable_category));
    }

    loadable_categories[loadable_categories_used].cat = cat;
    loadable_categories[loadable_categories_used].method = method;
    loadable_categories_used++;
}


void call_load_methods(void)
{
    static bool loading = NO;
    bool more_categories;

    loadMethodLock.assertLocked();

    // Re-entrant calls do nothing; the outermost call will finish the job.
    if (loading) return;
    loading = YES;

    void *pool = objc_autoreleasePoolPush();

    do {
   //先调用完类的+load方法
        // 1. Repeatedly call class +loads until there aren't any more
        while (loadable_classes_used > 0) {
            call_class_loads();
           //在call_class_loads中遍历loadable_classes进行调用　(*load_method)(cls, SEL_load);
        }
	//调用分类的load方法
        // 2. Call category +loads ONCE
        more_categories = call_category_loads();

        // 3. Run more +loads if there are classes OR more untried categories
    } while (loadable_classes_used > 0  ||  more_categories);

    objc_autoreleasePoolPop(pool);

    loading = NO;
}

```

### initialize

+initialize方法会在类第一次接收到消息时调用

 	1. 先调用父类的+initialize，再调用子类的+initialize
 	2. (先初始化父类，再初始化子类，每个类只会初始化1次)

+load与+initial区别

+initialize和+load的很大区别是，
+initialize自动调用是通过**objc_msgSend**进行调用的，而+load是通过**函数地址直接调用**　
**如果子类没有实现+initialize，会调用父类的+initialize（所以父类的+initialize可能会被调用多次）**
**如果分类实现了+initialize，就覆盖类本身的+initialize调用**



```
/***********************************************************************
* class_initialize.  Send the '+initialize' message on demand to any
* uninitialized class. Force initialization of superclasses first.
**********************************************************************/
void _class_initialize(Class cls)
{
    assert(!cls->isMetaClass());

    Class supercls;
    bool reallyInitialize = NO;

    // Make sure super is done initializing BEFORE beginning to initialize cls.
    // See note about deadlock above.
    supercls = cls->superclass;
    if (supercls  &&  !supercls->isInitialized()) {
        _class_initialize(supercls);
    }
    
    // Try to atomically set CLS_INITIALIZING.
    {
        monitor_locker_t lock(classInitLock);
        if (!cls->isInitialized() && !cls->isInitializing()) {
            cls->setInitializing();
            reallyInitialize = YES;
        }
    }
    
    if (reallyInitialize) {
        // We successfully set the CLS_INITIALIZING bit. Initialize the class.
        
        // Record that we're initializing this class so we can message it.
        _setThisThreadIsInitializingClass(cls);

        if (MultithreadedForkChild) {
            // LOL JK we don't really call +initialize methods after fork().
            performForkChildInitialize(cls, supercls);
            return;
        }
        
        // Send the +initialize message.
        // Note that +initialize is sent to the superclass (again) if 
        // this class doesn't implement +initialize. 2157218
        if (PrintInitializing) {
            _objc_inform("INITIALIZE: thread %p: calling +[%s initialize]",
                         pthread_self(), cls->nameForLogging());
        }

        // Exceptions: A +initialize call that throws an exception 
        // is deemed to be a complete and successful +initialize.
        //
        // Only __OBJC2__ adds these handlers. !__OBJC2__ has a
        // bootstrapping problem of this versus CF's call to
        // objc_exception_set_functions().
        @try{
            callInitialize(cls);

            if (PrintInitializing) {
                _objc_inform("INITIALIZE: thread %p: finished +[%s initialize]",
                             pthread_self(), cls->nameForLogging());
            }
        }@catch (...) {
            if (PrintInitializing) {
                _objc_inform("INITIALIZE: thread %p: +[%s initialize] "
                             "threw an exception",
                             pthread_self(), cls->nameForLogging());
            }
            @throw;
        }@finally{
            // Done initializing.
            lockAndFinishInitializing(cls, supercls);
        }
        return;
    }
    
    else if (cls->isInitializing()) {
        // We couldn't set INITIALIZING because INITIALIZING was already set.
        // If this thread set it earlier, continue normally.
        // If some other thread set it, block until initialize is done.
        // It's ok if INITIALIZING changes to INITIALIZED while we're here, 
        //   because we safely check for INITIALIZED inside the lock 
        //   before blocking.
        if (_thisThreadIsInitializingClass(cls)) {
            return;
        } else if (!MultithreadedForkChild) {
            waitForInitializeToComplete(cls);
            return;
        } else {
            // We're on the child side of fork(), facing a class that
            // was initializing by some other thread when fork() was called.
            _setThisThreadIsInitializingClass(cls);
            performForkChildInitialize(cls, supercls);
        }
    }
    
    else if (cls->isInitialized()) {
        // Set CLS_INITIALIZING failed because someone else already 
        //   initialized the class. Continue normally.
        // NOTE this check must come AFTER the ISINITIALIZING case.
        // Otherwise: Another thread is initializing this class. ISINITIALIZED 
        //   is false. Skip this clause. Then the other thread finishes 
        //   initialization and sets INITIALIZING=no and INITIALIZED=yes. 
        //   Skip the ISINITIALIZING clause. Die horribly.
        return;
    }
    
    else {
        // We shouldn't be here. 
        _objc_fatal("thread-safe class init in objc runtime is buggy!");
    }
}

```



### 关联对象

全局对象字典，线程安全问题

添加关联对象
void objc_setAssociatedObject(id object, const void * key,id value, objc_AssociationPolicy policy)

获得关联对象
id objc_getAssociatedObject(id object, const void * key)

移除所有的关联对象
void objc_removeAssociatedObjects(id object)

| **objc_AssociationPolicy**        | **对应的修饰符**  |
| --------------------------------- | ----------------- |
| OBJC_ASSOCIATION_ASSIGN           | assign            |
| OBJC_ASSOCIATION_RETAIN_NONATOMIC | strong, nonatomic |
| OBJC_ASSOCIATION_COPY_NONATOMIC   | copy, nonatomic   |
| OBJC_ASSOCIATION_RETAIN           | strong, atomic    |
| OBJC_ASSOCIATION_COPY             | copy, atomic      |

#### **关联对象key常用用法**

```
static void *MyKey = &MyKey;
objc_setAssociatedObject(obj, MyKey, value, OBJC_ASSOCIATION_RETAIN_NONATOMIC)
objc_getAssociatedObject(obj, MyKey)

static char MyKey;
//&MyKey 取MyKey地址值，没必要赋值
objc_setAssociatedObject(obj, &MyKey, value, OBJC_ASSOCIATION_RETAIN_NONATOMIC)
objc_getAssociatedObject(obj, &MyKey)

使用属性名作为key，传字符串常量
objc_setAssociatedObject(obj, @"property", value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
objc_getAssociatedObject(obj, @"property");

使用get方法的@selecor作为key，在getter中可以用_cmd简写
objc_setAssociatedObject(obj, @selector(getter), value, OBJC_ASSOCIATION_RETAIN_NONATOMIC)
objc_getAssociatedObject(obj, @selector(getter))

```





#### 关联对象原理

```
class AssociationsManager {
    // associative references: object pointer -> PtrPtrHashMap.
    static AssociationsHashMap *_map;
public:
    AssociationsManager()   { AssociationsManagerLock.lock(); }
    ~AssociationsManager()  { AssociationsManagerLock.unlock(); }
    
    AssociationsHashMap &associations() {
        if (_map == NULL)
            _map = new AssociationsHashMap();
        return *_map;
    }
};

template <class _Key, class _Tp, class _Hash = hash<_Key>, class _Pred = equal_to<_Key>,
          class _Alloc = allocator<pair<const _Key, _Tp> > >
class _LIBCPP_TEMPLATE_VIS unordered_map
{
public:
    // types
    typedef _Key                                           key_type;
    typedef _Tp                                            mapped_type;
    typedef _Hash                                          hasher;
    typedef _Pred                                          key_equal;
    typedef _Alloc                                         allocator_type;
    typedef pair<const key_type, mapped_type>              value_type;
    typedef pair<key_type, mapped_type>                    __nc_value_type;
    typedef value_type&                                    reference;
    typedef const value_type&                              const_reference;
};

其中pair
template <class _T1, class _T2>
struct pair{
    typedef _T1 first_type;
    typedef _T2 second_type;
    _T1 first;
    _T2 second;
}

// disguised_ptr_t->key_type
// ObjectAssociationMap * ->mapped_type
// DisguisedPointerHash -> hasher
// DisguisedPointerEqual -> key_equal
// AssociationsHashMapAllocator -> allocator_type
class AssociationsHashMap : public unordered_map<disguised_ptr_t, ObjectAssociationMap *, DisguisedPointerHash, DisguisedPointerEqual, AssociationsHashMapAllocator> {
public:
    void *operator new(size_t n) { return ::malloc(n); }
    void operator delete(void *ptr) { ::free(ptr); }
};
//故其中有parir<disguised_ptr_t,ObjectAssociationMap*>　value_type
    
class ObjectAssociationMap : public std::map<void *, ObjcAssociation, ObjectPointerLess, ObjectAssociationMapAllocator> {
public:
    void *operator new(size_t n) { return ::malloc(n); }
    void operator delete(void *ptr) { ::free(ptr); }
};

class ObjcAssociation {
    uintptr_t _policy;
    id _value;
public:
    ObjcAssociation(uintptr_t policy, id value) : _policy(policy), _value(value) {}
    ObjcAssociation() : _policy(0), _value(nil) {}

    uintptr_t policy() const { return _policy; }
    id value() const { return _value; }

    bool hasValue() { return _value != nil; }
};


```

![](/Users/hailong/Library/Application Support/typora-user-images/image-20190420222904298.png)

![image-20190420223102768](/Users/hailong/Library/Application Support/typora-user-images/image-20190420223102768.png)



### 练习题

1. Category的使用场合是什么？
2. Category的实现原理
3. Category和Class Extension的区别是什么？
4. Category中有load方法吗？load方法是什么时候调用的？load 方法能继承吗？
5. load、initialize方法的区别什么？它们在category中的调用的顺序？以及出现继承时他们之间的调用过程？
   ![](/Users/hailong/Library/Application Support/typora-user-images/image-20190420162435709.png)
6. Category能否添加成员变量？如果可以，如何给Category添加成员变量？