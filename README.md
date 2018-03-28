## Code samples

My [blog](https://www.tobyhobson.co.uk/) covers various Scala related topics mostly around functional programming. 
Here are some code examples ********illustrating the ideas I describe in my blog. Check out my blog to find out more:

**[www.tobyhobson.co.uk](https://www.tobyhobson.co.uk/)**

#### Typeclasses

Type classes are everywhere in the Scala ecosystem. If you want to learn advanced libraries like Scalaz, Cats or Shapeless 
you need to know about them. Even if you don't plan to use these libraries you can (and probably should) 
use type classes in your own applications. Type classes are borrowed from Haskell and are sometimes called "ad-hoc polymorphism"

Examples illustrating why type classes are so powerful can be found [here](src/main/scala/uk/co/tobyhobson/Typeclasses.scala)

Check out the supporting blog [post](https://www.tobyhobson.co.uk/type-classes-for-beginners/)

#### Functors

You're probably familiar with the map method in the Scala standard library. Collections, Futures and Options all have a map 
method but unfortunately there's no base class for mappable types, making it hard to write generic code. 
Fortunately [Cats](https://typelevel.org/cats/) includes a type class and various implementations which gives us this "base type"

Examples of Functors including composition can be found [here](src/main/scala/uk/co/tobyhobson/Functors.scala)

Check out the supporting blog [post](https://www.tobyhobson.co.uk/scala-cats-functor/)

#### Monads and Monad transformers

Functors compose, so long as we only use `map()` we can compose any combination of Functors e.g. `List[Option[A]]`. However
Monads (types with `flatMap()`) don't necessarily compose without an additional data type known as a *monad transformer*

Examples of monad transformers (OptionT and EitherT) including workarounds for variance issues can be found 
[here](src/main/scala/uk/co/tobyhobson/MonadTransformers.scala)

Check out the supporting blog posts [here](https://www.tobyhobson.co.uk/what-is-a-monad/) 
and [here](https://www.tobyhobson.co.uk/scala-monad-transformers/)