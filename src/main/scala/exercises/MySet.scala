package exercises

import scala.annotation.tailrec



trait MySet[A] extends (A => Boolean) {
  /*
  * EXERCISE - implemente a functional set
  * */

  // I will implement this as a linked list
  override def apply(elem: A): Boolean =
    contains(elem)

  def contains(elem:A):Boolean
  def +(elem:A):MySet[A]
  def ++(anotherSet:MySet[A]):MySet[A]

  def map[B](f:A=>B):MySet[B]
  def flatMap[B](f:A => MySet[B]):MySet[B]
  def filter(predicate: A=>Boolean):MySet[A]
  def foreach(f:A=>Unit):Unit

  /*
  EXERCISE #2
  - Removing an element
  - Intersection with another set
  - difference with another set
  * */
  def -(element:A):MySet[A]
  def &(anotherSet:MySet[A]):MySet[A]
  def --(anotherSet:MySet[A]):MySet[A]

  /*
  EXERCISE #3 - implemente a unary_! = NEGATION of a set
  set[1,2,3]
  * */

  def unary_! : MySet[A]
}


class EmptySet[A] extends MySet[A] {

  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem,this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  override def -(element: A): MySet[A] = this
  override def &(anotherSet: MySet[A]): MySet[A] = this
  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A]( _ => true )
}




// all elements of type A which satisfy a property
// { x in A |  property(x)}
class PropertyBasedSet[A](property: A=>Boolean) extends MySet[A] {
  def contains(elem:A):Boolean = property(elem)

  def +(elem:A):MySet[A] = new PropertyBasedSet[A](
    (x:A) => (property(x) || (x == elem))
  )

  def ++(anotherSet:MySet[A]):MySet[A] = new PropertyBasedSet[A](
    (x:A) => (property(x) || anotherSet(x))
  )

  // all integers => (_ % 3) => [0 1 2]
  def map[B](f:A=>B):MySet[B] = politelyFail
  def flatMap[B](f:A => MySet[B]):MySet[B] = politelyFail
  def foreach(f:A=>Unit):Unit = politelyFail

  def filter(predicate: A=>Boolean):MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x) )
  def -(element:A):MySet[A] = filter( x => x != element)
  def --(anotherSet:MySet[A]):MySet[A] = filter(!anotherSet)
  def &(anotherSet:MySet[A]):MySet[A] = filter(anotherSet)
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))



  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole!")
}


class NonEmptySet[A](head:A,tail:MySet[A]) extends MySet[A] {

  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)


  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](elem,this)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head



  override def map[B](f: A => B): MySet[B] =
    (tail map f) + f(head)

  override def flatMap[B] (f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] =
    if (predicate(head)) (tail filter predicate)  + head
    else tail.filter(predicate)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }


  override def -(element: A): MySet[A] =
    if (head == element) tail
    else tail - element + head

  override def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet) // remember that another set is also a f:A-Boolean function

  override def --(anotherSet: MySet[A]): MySet[A] =
    this.filter(value => (!anotherSet(value)))


  override def unary_! : MySet[A] = new PropertyBasedSet[A]( x => !this(x) )
}


object MySet {
  def apply[A](values:A*):MySet[A] = {
    @tailrec
    def buildSet(valSeq:Seq[A],acc:MySet[A]):MySet[A] =
      if (valSeq isEmpty) acc
      else buildSet(valSeq.tail,acc + valSeq.head)

    buildSet(values.toSeq,new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1,2,3,4)
  s + 5 ++  MySet(-1,-2) + 3 map (_*10) flatMap ( x => MySet(x,10*x) ) filter (_%2 == 0) foreach println

  println("Proving the implementation of the first operation")
  s - 2 foreach println

  println("Proving the implementation of the second operation")
  s & MySet(1,3,5) foreach println

  println("Proving the implementation of the third operation")
  s -- MySet(1,3) foreach println

  val negative = !s // s.unary_! = all the naturals not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))
  val negativeEven = negative.filter(_%2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5  // all the even numbers > 4 + 5
  println(negativeEven5(5))
}