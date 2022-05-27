package lectures.part5ts

object RockingInheritanceLoop1 extends App {

  // convenience
  trait Writer[T] {
    def write(value:T):Unit
  }
  trait Closable {
    def close(status:Int):Unit
  }
  trait GenericStream[T]{
    // some methods
    def foreach(f:T=>Unit):Unit
  }

  def processStream[T](stream:GenericStream[T] with Writer[T] with Closable):Unit = {
    stream.foreach(println)
    stream.close(1)
  }

  // diamond problem
  trait Animal { def name:String}
  trait Lion extends Animal { override def name: String = "tiger" }
  trait Tiger extends Animal { override def name: String = "tiger" }
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)

  /*
  Mutant extends Animal with {override def name:String = "lion"}
  with {override def name:String = "tiger"}

  LAST OVERRIDE GETS PICKED
  * */

  // the super problem + type linearization

  trait Cold {
    def print:Unit = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print:Unit =  println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

}
