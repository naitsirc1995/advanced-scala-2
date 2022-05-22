package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlaygroundLoop1 {
  /*
  Equality
  * */
  trait Equal[T] {
    def apply(a:T,b:T):Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a:User,b:User):Boolean = a.name == b.name && a.email == b.email
  }

  /*
  Exercise: implement the TC patter for the Equality tc.
  * */
  object Equal {
    def apply[T](a:T,b:T)(implicit equalizer:Equal[T]):Boolean =
      equalizer.apply(a,b)
  }

  val john = User("John",32,"john@rockthejvm.com")
  val anotherJohn = User("John",45,"anotherJohn@rockthejvm.com")
  println(Equal[User](john,anotherJohn))
  // AD-HOC polymorphism
}
