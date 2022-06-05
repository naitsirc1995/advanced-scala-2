package lectures.part5ts

object VarianceLoop2 extends App {
  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal



  // what is variance?
  //"inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage:CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]
  //val icage:ICage[Animal] = new ICage[Cat]

  // hell no - opposite - contravariant
  class XCage[-T]
  val xcage:XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal:T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal:T) // COVARIANT POSITION

  //class ContravariantCage[-T](val animal:T)

  /*
  val catCage:XCage[Cat] = new XCage[Animal](new Crocodile)
  * */

  //class CovariantVariableCage[+T](var animal:T) // types of vars are in CONTRAVARIANT POSITION

  /*
  cal ccage:CCage[Animal] = new CCage[Cats](new Cat)
  ccage.cat = new Crocodile
  * */

  //class ContravariantVariableCage[-T](var animal:T)


//  trait AnotherCovariantCage[+T]{
//    def addAnimal(animal:T) // CONTRAVARIANT POSITION
//  }

  /*
    val ccage:CCage[Animal] = new CCage[Dog]
    ccage.add(new Cat)
  * */

  class AnotherContravariantCage[-T]{
    def addAnimal(animal:T) = true
  }

  val acc:AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  //acc.addAnimal(new Dog)
  class Kitty extends Cat
  class RedKitty extends Kitty

  class MyList[+A] {
    def add[B >:A](element:B):MyList[B] = new MyList[B]
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val animals2 = emptyList.add(new RedKitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

    /*
    *
    * */

    /*
  * 1. Invariant,covariant, contravariant Parking[T](things List[T])
  *   Parking[T](things:List[T]{
  *     park(vehicle:T)
  *     impound(vehicles:List[T])
  *     checkVehicles(conditions:String):List[T]
  * }
  * 2. used someone else's API: IList[T]
  * 3. Parking = monad!
  *   - flatMap
  *
  * */

  /*
  Big Rule
    - method arguments are in CONTRAVARIANT position
    - return types are in COVARIANT position
  * */


  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  class CParking[+T](vehicles:List[T]){
    def park[S>:T](vehicle:S):CParking[S] = ???
    def impount[S>:T](vehicles:List[S]):CParking[S] = ???
    def checkVehicles(conditions:String):List[T] = ???

    def flatMap[S](f:T=>CParking[S]):CParking[S] = ???
  }

  class XParking[-T](vehicles:List[T]){
    def park(vehicles:T):XParking[T] = ???
    def impound(vehicles:List[T]):XParking[T] = ???
    def checkVehicles[S<:T](conditions:String):List[S] = ???

    def flatMap[R<:T,S](f:R=>XParking[S]):XParking[S] = ???
  }

  /*
  Rule of thumb
    - use covariance = COLLECTION OF THINGS
    - use contravariance = GROUP OF ACTIONS
  * */

  class CParking2[+T](vehicles:IList[T]){
    def park[S>:T](vehicle:S):CParking[S] = ???
    def impount[S>:T](vehicles:IList[S]):CParking2[S] = ???
    def checkVehicles[S>:T](conditions:String):IList[S] = ???
  }

  class XParking2[-T](vehicles:IList[T]){
    def park(vehicles:T):XParking[T] = ???
    def impound[S<:T](vehicles:IList[S]):XParking[T] = ???
    def checkVehicles[S<:T](conditions:String):IList[S] = ???
  }


  // flatMap
}
