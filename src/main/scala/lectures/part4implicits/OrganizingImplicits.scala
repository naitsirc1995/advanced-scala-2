package lectures.part4implicits

object OrganizingImplicits extends App
{
  implicit val reverseOrdering:Ordering[Int] = Ordering.fromLessThan(_ > _)
  //implicit val normalOrdering:Ordering[Int] = Ordering.fromLessThan(_ < _)
  println(List(1,4,5,3,2).sorted)


  // scala.Predef

  /*
  Implicits:
    - val/var
    - objects
    - accesor methods = defs with no parentheses
   */
  // Exercise
  case class Person(name:String,age:Int)



  val person:List[Person] = List(
    Person("Steve",30),
    Person("Amy",22),
    Person("Jhon",66)
  )

//  object Person {
//    implicit val alphabeticOrdering: Ordering[Person] =
//      Ordering.fromLessThan((a,b) => a.name.compareTo(b.name) < 0)
//  }



  /*
  Implicit scope
    - normal scope = LOCAL SCOPE
    - imported scope.
    - companions of all types involved in the method signature.
      - List
      - Ordering
      - all the types involved = A or any supertype.
  * */

  // def sorted[B >: A](implicit ord: Ordering[B]): Repr

  object AlphabeticNameOrdering {
    implicit val alphabeticNameOrdering : Ordering[Person] =
      Ordering.fromLessThan((a,b) => a.name.compare(b.name) < 0)
  }

  object AgeOrdering {
    implicit val ageOrdering:Ordering[Person] = Ordering.fromLessThan(_.age > _.age)
  }

  import AlphabeticNameOrdering._

  println(person.sorted)

  /*
  Exercise

  - totalPrice = most used (50%)
  - by unit count = 25%
  - by unit price = 25%
  * */

  case class Purchase(nUnits:Int, unitPrice:Double)
  object Purchase {
    implicit val totalPriceOrdering:Ordering[Purchase] = Ordering.fromLessThan(
      (a,b) => a.nUnits*a.unitPrice < b.nUnits*b.unitPrice
    )
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering:Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering:Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

  val myPurchases:List[Purchase] =
    List(
      Purchase(13,12),
      Purchase(2,24),
      Purchase(45,3)
    )

  println(myPurchases.sorted)

  /*object TotalPriceOrdering {
    implicit val totalPriceOrdering:Ordering[Purchase] =
      Ordering.fromLessThan(
        (person1,person2) => person1.unitPrice*person1.nUnits > person2.unitPrice*person2.nUnits
      )
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering:Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits > _.nUnits)
  }

  object unitPriceOrdering {
    implicit val unitPrice:Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice > _.unitPrice)
  }


  val myPurchases:List[Purchase] =
    List(
      Purchase(13,12),
      Purchase(2,24),
      Purchase(45,3)
    )
*/
  /*import unitPriceOrdering._

  println("This is my actual sorted")
  println(myPurchases.sorted)*/








}