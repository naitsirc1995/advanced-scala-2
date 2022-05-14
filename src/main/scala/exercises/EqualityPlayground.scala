package exercises

import lectures.part4implicits.TypeClasses.{User, anotherJohn, john}

object EqualityPlayground extends App {
  /*
  * Equality
  * */
  val john =  User("John",32,"john@rockthejvm.com")
  val anotherJohn = User("John",45,"anotherJohn@rockthejvm.com")


  trait Equal[T] {
    def apply(a:T,b:T):Boolean
  }



  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }


  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object Equal {
    def apply[T](a:T,b:T)(implicit equalizer:Equal[T]):Boolean =
      equalizer.apply(a,b)
  }

  println(Equal(anotherJohn,john))
  // AD-HOG polymorphism

  /*
  EXERCISE - Improve Equal TC with an implicit conversion class
  ===(anotherValue:T)
  !==(anotherValue:T)
  * */

  implicit class TypeSafeEqual[T](value:T) {
    def ===(other:T)(implicit equalizer:Equal[T]):Boolean = equalizer.apply(value,other)
    def !==(other:T)(implicit equalizer:Equal[T]):Boolean = ! equalizer.apply(value,other)
  }


  println(john === anotherJohn)
  /*
  john.===(anotherJohn)
  new TypeSafeEqual[User](john).===(anotherJohn)(NameEquality)
  * */
  /*
  TYPE SAFE
  * */


}
