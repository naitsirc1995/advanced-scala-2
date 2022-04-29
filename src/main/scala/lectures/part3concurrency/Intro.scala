package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
    interface Runnable {
      public void run()
    }
  * */
  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val aThread = new Thread(runnable)

//  aThread.start() // gives the signal to the JVM to start a JVM thread
//  // create a JVM thread => OS thread
//  //runnable.run() // doesn't do anything in parallel
//  aThread.join() // blocks until aThread finishes running

//  val threadHello = new Thread(() => (1 to 5 ).foreach(_ => println("Hello")))
//  val threadGoodbye = new Thread(() => (1 to 5 ).foreach(_ => println("Goodbye")))

//  threadHello.start()
//  threadGoodbye.start()
  // different runs produce different results !

  // executors
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(()=> println("Something in the thread pool"))

//  pool.execute(()=> {
//    Thread.sleep(1000)
//    println("done after 1 second")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("Done after 2 seconds")
//  })

  pool.shutdown()


  println(pool.isShutdown)

  def runInParallel = {
    var x = 0

    val thread1 = new Thread(()=> {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)
  }

//  for (_ <- 1 to 10000) runInParallel
  // race condition

  class BankAccount(var amount: Int){
    override def toString: String = "" + amount
  }

  def buy(account:BankAccount,thing:String,price:Int) = {
    account.amount -= price
//    println("I've bought " + thing )
//    println("my account is now " + account)
  }

//  for ( _ <- 1 to 10000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account,"shoes",3000))
//    val thread2 = new Thread(()=> buy(account,"iPhone12",price = 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if (account.amount != 43000) println("AHA: " + account.amount)
//  }

  /*
  thread1 (shoes):50000
    - account = 50000 - 3000 = 47000
  thread2 (iPhone):50000
    - account = 50000 - 4000 = 46000 overwrites the memory of account.amount
  * */

  // option #1: use synchronized()

  def buySafe(account:BankAccount,thing:String,price:Int) =
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
//      println("I've bought " + thing)
//      println("my account is now " + account)
    }

  // option #2: use @volatile
//  for ( _ <- 1 to 10000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buySafe(account,"shoes",3000))
//    val thread2 = new Thread(()=> buySafe(account,"iPhone12",price = 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if (account.amount != 43000) println("AHA: " + account.amount)
//  }

  /*
  Exercises
    1) Construct 50 "inception" threads
      thread1 -> thread2 -> thread3 -> ....
      println("hello from thread #3")

    in REVERSE ORDER
  * */

  def inception(nMax:Int,accumulator:Int):Thread = new Thread( () => {

    if (accumulator < nMax) {
      val newThread = inception(nMax,accumulator+1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $accumulator")
  })

    println("This is the thing that I remember from the code")
    inception(50,0).start()

  /*
  2
  * */

  var x = 0
  val threads = (1 to 100).map(_=>new Thread(()=>x+=1))
  threads.foreach(_.start())

  /*
  1) what is the biggest value possible for x? 100 (which is when all the threads act in turn)
  2) what is the SMALLEST value possible for x?

  thread1: x= 0
  thread2: x= 0
    ...
  thread100: x= 0

  for all threads: x = 1 and write it back to x
  * */

  threads.foreach(_.join())
  println("here the code from the sync problem")
  println(x)

  /*
  3 sleep fallacy
  * */

  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"

  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join() // wait for the awesome thread to join
  println(message)
  /*
  what's the value of message ? almost always "Scala is awesome"
  is it guaranteed? NO!

  (main thread)
    message = "Scala sucks"
    awesomeThread.start()(
    sleep() - relieves execution
    (awesome thread)
      sleep() - relieves execution
    (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
    (OS gives the CPU back to the MAIN thread)
      println("Scala sucks")
    (OS gives the CPU to awesomethread)
      message = "Scala is awesome"
  * */

  // how do we fix this ?
  // syncronizing does not work

}