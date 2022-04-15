package lectures.part1as

object AdvancedPatternMatching extends App
{
  val numbers = List(1)
  val description = numbers match {
    case head::Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
  - constants
  - wildcards
  - case classes
  - tuples
  - some special magic like above
  * */
  class Person(val name:String,val age:Int)

  //
  object Person {
    def unapply(person:Person):Option[(String,Int)] =
      if (person.age < 21) None
      else Some((person.name),person.age)

    def unapply(age:Int):Option[String] =
      Some(if (age < 21) "minor" else "major")

  }

  val bob = new Person(name = "Bob",age = 25)
  val greeting = bob match {
    case Person(n,a) => s"Hi, my name is $n and I am $a yo."
  }
  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
  Exercise.
  * */
  val n:Int = 45
  val mathProperty = n match {
    case x if x < 10 => "Single digit"
    case x if x%2 == 0 => "an even number"
    case _ => "no property"
  }

  // The idea is to use the pattern matching that we just leanred+

  class SingleNumberMySolution(val n:Int)

  object SingleNumberMySolution
  {
    def unapply(x:Int):Option[String] =
      {
        if (x < 10) Option("Single digit")
        else if (x%2 == 0) Option("an even number")
        else None
      }
  }

  val mySolutionClassInstance = new SingleNumberMySolution(13)

  val mathPropertyMySolution = mySolutionClassInstance.n match {
    case SingleNumberMySolution(element) => s"What you got is a $element"
    case _ => "No cases were matched"
  }

  println("Now I have my own solution for this problem")
  println(mathPropertyMySolution)
  println("And I am really proud of it !!")

  println("Now lets continue with the instructor's solution")

  object even {
    def unapply(arg:Int):Boolean = arg %2 == 0
  }

  object singleDigit{
    def unapply(arg:Int):Boolean = arg > -10 && arg < 10
  }

  val m:Int = 8
  val mathPropertyInstructorSolution = m match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }

  println(mathPropertyInstructorSolution)

}