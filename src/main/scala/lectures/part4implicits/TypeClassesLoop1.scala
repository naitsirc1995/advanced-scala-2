package lectures.part4implicits

import java.util.Calendar

object TypeClassesLoop1 extends App {

  trait HTMLWritable {
    def toHTML:String
  }

  case class User(name:String,age:Int,email:String) extends HTMLWritable {
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email/>"
  }

  User("John",32,"john@rockthejvm.com").toHTML

  /*
  1 - for the types WE write.
  2 - ONE implementation out of quite a number
  * */

  // option2 - pattern matching

  object HTMLSerializerPM {
    def serializeToHtml(value:Any) = value match {
      case User(n,a,e) =>
      case _ =>
    }
  }

  /*
    1 - lost type safety
    2 - need to modify this code everytime
    3 - still ONE implementation
    * */

  trait HTMLSerializer[T] {
    def serialize(value:T):String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String =
      s"<div>${user.name} (${user.age} yo) <a href=${user.email}/>"
  }
  val john = User("John",32,"john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  // 1 - we can define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString()}</div>"
  }

  val myDate: Date = Calendar.getInstance().getTime()
  println(DateSerializer.serialize(myDate))

  // 2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}/>"
  }

  // part 2
  object HTMLSerializer {
    def serialize[T](value:T)(implicit serializer:HTMLSerializer[T]):String =
      serializer.serialize(value)

    def apply[T](implicit serializer:HTMLSerializer[T]):HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style:color=blue>$value</div>"
  }


  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  //part3
  implicit class HTMLEnrichment[T](value:T) {
    def toHTML(implicit serializer:HTMLSerializer[T]):String = serializer.serialize(value)
  }

  println(john.toHTML) // println(new HTMLEnrichment[User](user).toHTML(UserSerializer))
  // COOL!
  /*
    - extend to new types
  * */

  println(2.toHTML)



}
