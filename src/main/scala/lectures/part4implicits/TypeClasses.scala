package lectures.part4implicits

object TypeClasses extends App {
  trait HTMLWritable {
    def toHtml:String
  }

  case class User(name:String,age:Int,email:String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/></div>"
  }

  User("John",32,"john@rockthejvm.com").toHtml
  /*
  1 - for the types WE write.
  2 - ONE implementation out of quite a number.
  * */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value:Any) = value match {
      case User(n,a,e) =>
      case _ =>
    }
  }

  /*
  1 - lost type safety
  2 - need to modify this code
  3 - still ONE implementation
  * */

  trait HTMLSerializer[T] {
    def serialize(value:T):String
  }

  object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String =
      s"<div>${user.name} (${user.age} yo) <a href=${user.email}/></div>"
  }

  val john:User = User("John",32,"john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  // 1 - we can define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}<div>"
  }

  // 2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String =
      s"<div>${user.name}</div>"
  }


  // TYPE CLASS
  trait MyTypeClassTemplate[T] {
    def action(value:T):String
  }


  /*
  * Equality
  * has a method called equal that compares two values
  * I want you to also implement two instances of this equal type class
  * that compares users by name and by both name and email
  * */

  trait Equal[T] {
    def apply(a:T,b:T):Boolean
  }

  object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }



}