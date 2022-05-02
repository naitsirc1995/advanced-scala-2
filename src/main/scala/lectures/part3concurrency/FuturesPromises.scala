package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._
// important for futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesPromises extends App
{
  def calculateMeaningOfLife:Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculates the meaning of life on ANOTHER thread
  } // (global) which is passed by the compiler

  println(aFuture.value) // Option[Try[Int]]

  println("Waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  } // SOME thread

  Thread.sleep(3000)


  // mini social network

  case class Profile(id:String,name:String) {
    def poke(anotherProfile:Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // "database"
    val names:Map[String,String] = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill"-> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id:String):Future[Profile] = Future {
      // fetching from the DB
      Thread.sleep(random.nextInt(300))
      Profile(id,names(id))
    }

    def fetchBestFriend(profile:Profile):Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId,names(bfId))
    }

  }

  // client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
//  mark.onComplete{
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriend(markProfile)
//      bill.onComplete{
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//
//    case Failure(ex) => ex.printStackTrace()
//  }



  // functional composition of futures
  // map,flatmap,filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallback
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy","Forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult =
    SocialNetwork
      .fetchProfile("unknown id")
      .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


  // online
  case class User(name:String)
  case class Transaction(sender:String,receiver:String,amount:Double,status:String)

  object BankingApp {
    val name = "Rock the JVM banking"

    def fetchUser(name:String):Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user:User,merchantName:String, amount:Double):Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name,merchantName,amount,"SUCCESS")
    }

    def purchase(username:String,item:String,merchantName:String,cost:Double):String = {
      //fetch the user from the DB
      // create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user,merchantName,cost)
      } yield transaction.status

      Await.result(transactionStatusFuture,2.seconds) // implicit conversions -> pimp my library
    }
  }

  println(BankingApp.purchase("Daniel","iPhone 12","rock the JVM store",3000))

  // promises

  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(1000)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

  /*
  1) fullfill a future IMMEDIATELY with a value
  2) inSequence(fa,fb) will run future "b" until is made sure that future "a" has completed
  3) first(fa,fb) => new future with the first value of the two futures
  4) last(fa,fb) => new future with the last value
  5) retryUntil[T](action: () => Future[T], condition: T => Boolean):Future[T]
  * */


  // 1)
  def futureImmediateValue():Unit = {
    val immediateFuture:Future[Int] = Future(42)

    immediateFuture.onComplete {
      case Success(number) => println(s"the number was $number")
    }
  }

  // 2)

  object InSequenceObject {

    def inSequence(fa:Future[Int],fb:Future[Int]):Unit = {
      fa.onComplete {
        case Success(_) => fb.onComplete {
          case Success(_) => println(s"The second value was executed after the first one")
          case Failure(e) => e.printStackTrace()
        }
        case Failure(e) => e.printStackTrace()
      }
    }

    def execute():Unit = {
      println("I just started to execute")
      val fa:Future[Int] = Future {
        Thread.sleep(250)
        1
      }

      val fb:Future[Int] = Future {
        Thread.sleep(100)
        2
      }

      inSequence(fa,fb)
      Thread.sleep(1000)
    }
  }

  //InSequenceObject.execute()

  // last would be very similar, I am just not exited about the thing going on.

//  object RetryUntil {
//    def retryUntil[T](action: () => Future[T], condition:T => Boolean):Future[T] = {
//      action().onComplete {
//        case Success(result) => {
//          if (condition(result)) Future(result)
//          else retryUntil(action, condition)
//        }
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//  }

  // 1 fulfill  immediately
  def fulfillImmediately[T](value:T):Future[T] = Future(value)
  println("Instructor's solution")
  for {
    number <- fulfillImmediately(42)
  } println("The number I got was " + number)


  // 2 - insequence

  def inSequence[A,B](first:Future[A],second:Future[B]):Future[B] = first.flatMap(_ => second)


  val fa:Future[Int] = Future {
    Thread.sleep(500)
    1
  }

  val fb:Future[Int] = Future {
    Thread.sleep(100)
    2
  }

  inSequence(fa,fb).foreach(println)
  Thread.sleep(1000)


  // 3 -
  def first[A](fa:Future[A],fb:Future[A]):Future[A] = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // 4 - last out of the two futures
  def last[A](fa:Future[A],fb:Future[A]):Future[A] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    val checkAndComplete = (result:Try[A]) =>
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future

  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    45
  }
  first(fast,slow).foreach(println)
  last(fast,slow).foreach(println)

  Thread.sleep(1000)

  def retryUntil[A](action: () => Future[A],condition:A=>Boolean):Future[A] =
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action,condition)
      }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action,(x:Int) => x < 10).foreach(result => println("settled at " + result))
  Thread.sleep(10000)
}