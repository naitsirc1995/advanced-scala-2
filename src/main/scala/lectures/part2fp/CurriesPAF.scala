package lectures.part2fp


object CurriesPAF extends App {
  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5))

  // METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4)
  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA-expansion

  // Partial function applications
  val add5 = curriedAdder(5) _ // Int => Int

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7 : Int => Int = y => y + 7
  // as many different implementations of add7 using the above
  // be creative !

  val add7_v1 = (z: Int) => simpleAddFunction(7, z)

  println("Here my first version of add 7")
  println(add7_v1(3))

  val add7_v3 = curriedAddMethod(7) _

  println("This is the new version of the function")
  println(add7_v3(4))


  println("Now the instructor's solution")
  val add7 = (x: Int) => simpleAddFunction(7, x) //
  val add7_2 = simpleAddFunction.curried(7)
  val add7_6 = simpleAddFunction(7,_:Int)

  val add7_3 = curriedAddMethod(7)_
  val add7_4 = curriedAddMethod(7)(_) // PAF = alternative syntax

  val add7_5 = simpleAddMethod(7,_:Int) // alternative syntax for turning methods into function values
        // y => simpleAddMethod(7,y)

  // underscores are powerful
  def concatenator(a:String,b:String,c:String) = a+b+c
  val insertName = concatenator("Hello, I'm ",_:String,", how are you?") // x:String => concatenator(hello,x,how are you ?)
  println(insertName("Cristian"))

  val fillInTheBlanks = concatenator("Hello, ",_:String,_:String) // (x,y) => concatenator("Hello, ",x,y)

  println(fillInTheBlanks("Cristian"," Scala is awesome!"))

  // EXERCISES

  /*
  1. Process a list of numbers and return their string representations with different formats
    Use the %4.2f, %8.6g, and %14.12f with a curried formatter function
  * */

  println("%4.2f".format(Math.PI))

  println("Here goes my solution for the problem the instructor assigned")
  def curriedFormaterFunction(x:String)(y:Double):String = x.format(y)
  val listFormats =  List("%4.2f", "%8.6g", "%14.12f")
  val listNumbers = List(Math.PI,Math.E, Math.cos(Math.PI/4))

  listFormats
    .flatMap(
      x => listNumbers.map(curriedFormaterFunction(x))
    )
    .foreach(println)

  println("Now the second solution using list comprehension")
  for {
    x <- listFormats
    y <- listNumbers
  } yield println(curriedFormaterFunction(x)(y))

  /*
  2. difference between
    - functions vs methods
    - parameters: by-name vs 0-lambda
  * */

  def byName(n: => Int) = n+1
  def byFunction(f:()=>Int) = f() + 1

  def method:Int = 42
  def parenMethod():Int = 42

  /*
  calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
  * */

  println("Here are my solutions for the second part")
  println(byName(2))
  println(byName(method))
  println(byName(parenMethod()))
  //println(byName(() => 100)) lambda did not work

// None of these methods really worked
//  println(byFunction(2))
//  println(byFunction(method))
//  println(byFunction(parenMethod()))
  println(byFunction(()=>40))
  println("For the PAF I could not really understand how to do that. Lets see now the instructor's solution")

  println("Instructor's solution for exercise 1")
  def curriedFormatter(s:String)(number:Double):String = s.format(number)
  val numbers = List(Math.PI,Math.E,1,9.8,1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f")_ // lift
  val seriousFormat = curriedFormatter("%8.5f")_
  val preciseFormat = curriedFormatter("14.12f")_

  println(numbers.map(simpleFormat))

  println("From the things that the instructor did this is the nicest to me")
  byFunction(parenMethod _ ) // also works , but warning - unnecessary
}