package lectures.part4implicits

import lectures.part4implicits.PimpMyLibrary.RichAltInt

object PimpMyLibraryLoop1 extends App {

  // 2.isPrime

  implicit class RichInt(val value:Int) extends AnyVal {
    def isEven:Boolean = value % 2 == 0
    def sqrt:Double = Math.sqrt(value)

    def times(function:()=>Unit):Unit = {
      def timesAux(n:Int):Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n-1)
        }
      timesAux(value)
    }

    def *[T](list:List[T]):List[T] = {
      def concatenate(n:Int):List[T] =
        if (n<=0) List()
        else concatenate(n-1) ++ list

      concatenate(value)
    }
  }

  implicit class RicherInt(richInt:RichInt) {
    def isOdd:Boolean = !richInt.isEven
  }
  new RichInt(52).sqrt
  println(42 isEven) // new RichInt(42).isEven

  // compiler doesn't do multiple implicit searches
  //  42.isOdd
  /*
  Enrich the String class
  - isInt
  - encrypt
    "John" -> Lnjp

  Keep enriching the Int class
    - times(function)
      3.times(()=>...)
    -*
      3*List(1,2) => List(1,2,1,2,1,2)
  * */
  implicit class RichString(string:String) {
    def asInt:Int = Integer.valueOf(string) // java.lang.Integer -> Int
    def encrypt(cypherDistance:Int):String =
      string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println("3".asInt + 4)
  println("John".encrypt(2))

  3.times(() => println("Scala Rocks!"))
  println(4*List(1,2))

  // "3" / 4
  implicit def stringToInt(string:String):Int = Integer.valueOf(string)
  println("6"/2) // stringToInt("6")/2

  // equivalent: implicit class RichAltInt(value:Int)
  class RichAlternative(value:Int)
  implicit def enrich(value:Int):RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i:Int):Boolean = i == 1

  /*
  if (n) do something
  else do something else
  * */

  val aConditionValue = if (3) "OK" else "Something Wrong"
  println(aConditionValue)
}
