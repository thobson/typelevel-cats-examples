## Cats Code samples

My [blog](https://tobyhobson.com/) covers various Scala related topics mostly around functional programming. 
Here are some code examples illustrating the ideas I describe in my blog. Check out my blog to find out more:

**[tobyhobson.com](https://tobyhobson.com/)**

#### Typeclasses

Type classes are everywhere in the Scala ecosystem. If you want to learn advanced libraries like Scalaz, Cats or Shapeless 
you need to know about them. Even if you don't plan to use these libraries you can (and probably should) 
use type classes in your own applications. Type classes are borrowed from Haskell and are sometimes called "ad-hoc polymorphism"

Examples illustrating why type classes are so powerful can be found [here](src/main/scala/uk/co/tobyhobson/Typeclasses.scala)

Check out the supporting blog [post](https://tobyhobson.com/posts/cats/typeclasses/)

#### Functors

You're probably familiar with the map method in the Scala standard library. Collections, Futures and Options all have a map 
method but unfortunately there's no base class for mappable types, making it hard to write generic code. 
Fortunately [Cats](https://typelevel.org/cats/) includes a type class and various implementations which gives us this "base type"

Examples of Functors including composition can be found [here](src/main/scala/uk/co/tobyhobson/cats/Functors.scala)

Check out the supporting blog [post](https://tobyhobson.com/posts/cats/functors/)

#### Monads and Monad transformers

Functors compose, so long as we only use `map()` we can compose any combination of Functors e.g. `List[Option[A]]`. However
Monads (types with `flatMap()`) don't necessarily compose without an additional data type known as a *monad transformer*

Examples of monad transformers (OptionT and EitherT) including workarounds for variance issues can be found 
[here](src/main/scala/uk/co/tobyhobson/cats/MonadTransformers.scala)

Check out the supporting blog posts [here](https://tobyhobson.com/posts/cats/monoids/) 
and [here](https://tobyhobson.com/posts/cats/monad-transformers/)

#### MonadError

We can use a MonadTransformer to wrap `Future[Either[L, R]]` then map the left and right projections. But what happens if
the underlying future fails? `MonadError` allows us to map the left projection if the underlying future holds a 
Left **or** the future fails

An example of MonadError usage can be found [here](src/main/scala/uk/co/tobyhobson/cats/MonadErrors.scala)

Check out the supporting blog post [here](https://tobyhobson.com/posts/cats/monad-error/)

#### Applicatives

`Monads` are prevalent in the scala world. Think of `Future's flatMap()`. New developers often use Monads along with
for comprehension to perform multiple independent operations but this is not what Monad's are designed for. `Applicatives`
are the go to tool for performing **multiple independent** operations.

See an example of calling multiple external services in parallel using Applicatives [here](src/main/scala/uk/co/tobyhobson/cats/ApplicativeFutures.scala)

An example of form style validation can be found [here](src/main/scala/uk/co/tobyhobson/cats/ApplicativeValidation.scala)

Check out the supporting blog post [here](https://tobyhobson.com/posts/cats/applicatives-vs-monads/)

#### Validated and ValidatedNel

`Either` doesn't really work when we want to accumulate errors. I explain why in my [blog post](https://tobyhobson.com/cats-validated/). 
Validated is a much better option - it's usage alongside `Applicative` is almost identical to `Either`

Find an example [here](src/main/scala/uk/co/tobyhobson/cats/ValidatedNels.scala)

The supporting blog post can be found [here](https://tobyhobson.com/posts/cats/validated/)
