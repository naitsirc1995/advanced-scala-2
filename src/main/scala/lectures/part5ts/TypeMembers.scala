package lectures.part5ts

object TypeMembers extends App {
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAninal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat
  }


  val ac = new AnimalCollection
  //val dog:ac.AnimalType = ???

  //val cat:ac.BoundedAninal = new Cat

  val pup:ac.SuperBoundedAnimal = new Dog
  val cat:ac.AnimalC = new Cat

  type CatAlias = Cat
  //val anotherCat:CatAlias = new Cat

  // alternative to generics
  trait MyList {
    type T
    def add(element:T):MyList
  }

//  class NonEmptyList(value:Int) extends MyList {
//    override type T = Int
//
//    override def add(element: Int): MyList = ???
//  }

  type CatsType = cat.type

  val newCat:CatsType = cat

  /*
    Exercise - enforce a type to be applicable to SOME TYPES only
  * */

  // LOCKED
  trait MList {
    type A
    def head:A
    def tail:MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }
  // NOT OK
//  class CustomList(hd:String,tl:CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//
//    def head: String = hd
//    def tail:CustomList = tl
//  }

  class IntList(hd:Int,tl:IntList) extends MList {
    type A = Int

    def head: Int = hd
    def tail:IntList = tl
  }

  // Number
  // type member and type member constraints (bounds)
}
