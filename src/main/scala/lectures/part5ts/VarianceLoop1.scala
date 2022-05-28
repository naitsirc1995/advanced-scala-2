package lectures.part5ts

object VarianceLoop1 extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage:CCage[Animal] = new CCage[Cat]

  //no - invariance
  class ICage[T]
//  val icage:ICage[Animal] = new ICage[Cat]
//  val x:Int = "hello"

  // hell no - opposite = contravariance
  class XCage[-T]
  val xcaage:XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal:T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal:T) // COVARIANT POSITION

  //class ContravariantCage[-T](val animal:T)
  /*
  val catCage:XCage[Cat] = new XCage[Animal](new Crocodile)
  * */


  //class CovariantVariableCage[+T](var animal:T) // types of vars are in CONTRAVARIANT POSITION
  /*
  val ccage:CCage[Animal] = new CCage[Cat](new Cat)
  ccage.animal = new Crocodile
  * */
  /*
  val animalCage:CovariantVariableCage[Animal] = new CovariantVariableCage[Dog](new Dog)
  animalCage.animal = new Cat
  * */
  //class ContravariantVariableCage[-T](var animal:T) // also in COVARIANT POSITION
  /*
  val catCage:XCage[Cat] = new XCage[Animal](new Crocodile)
  * */
  class InvariantVariableCage[T](var animal:T) // ok

//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal:T) // CONTRAVARIANT POSITION
//  }
  /*
  val ccage:CCage[Animal] = new CCage[Dog]
  ccage.add(new Cat)
  * */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal:T) = true
  }

  val acc:AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >:A](element:B):MyList[B] = new MyList[B]
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION.

  // return types
  class PetShop[-T]{
    // def get(isItaPuppy:Boolean):T //METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
    val catShop = new PetShop[Animal]{
      def get(isItaPuppy:Boolean):Animal = new Cat
    }

    val dogShop:PetShop[Dog] = catShop
    dogShop.get(true)  // EVIL CAT!
    * */

    def get[S<:T](isItaPuppy:Boolean,defaultAnimal:S):S = defaultAnimal
  }

  val shop:PetShop[Dog] = new PetShop[Animal]
  //val evilCat = shop.get(true,new Cat)
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)


  /*
  Big rule
    - method arguments are in CONTRAVARIANT position
    - return types are in COVARIANT position
  * */



}
