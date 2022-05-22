package lectures.part4implicits

// TYPE CLASS
trait MyTypeClassTemplateLoop1[T] {
  def action(value:T):String
}

object MyTypeClassTemplateLoop1 {
  def apply[T](implicit instance:MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance
}