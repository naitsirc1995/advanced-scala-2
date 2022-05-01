package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random


object ThreadComunication extends App
{
  /*
  the producer consumer problem

  producer -> [ ? ] -> consumer
  * */

  class SimpleContainer {
    private var value:Int = 0

    def isEmpty:Boolean = value == 0

    def set(newVallue:Int) = value = newVallue
    def get = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons():Unit = {
    val container = new SimpleContainer

    val consumer = new Thread( () => {
      println("[consumer] waiting...")
      while(container.isEmpty){
        println("[consumer] actively waiting...")
      }

      println("[consumer] I have consumed " + container.get)
    } )

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced, after a long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //naiveProdCons()

  // wait and notify
  def smartProdCons():Unit = {
    val container = new SimpleContainer

    val consumer  = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized{
        container.wait()
      }

      // container must have some value
      println("[consumer] I have consumed " + container.get)
    })


    val producer = new Thread(() => {
      println("[producer] Hard at work...")
      Thread.sleep(2000)
      val value = 42

      container.synchronized{
        println("[producer] I'm producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //smartProdCons()


  /*
    producer -> [ ? ? ? ]   -> consumer

  * */

  def prodConsLargeBuffer():Unit = {
    val buffer:mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty){
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // hey producer, there's empty space available, are you lazy ?!
          buffer.notify()
        }

        Thread.sleep(random.nextInt(250))
      }
    })

    val producer = new Thread( () => {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized{
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[producer] producing "+i)
          buffer.enqueue(i)

          // hey consumer, new food for you!
          buffer.notify()

          i+=1
          Thread.sleep(random.nextInt(500))
        }
      }
    })

    consumer.start()
    producer.start()
  }

  //prodConsLargeBuffer()

  /*
  Prod-cons, level 3
    producer1 -> [? ? ?] -> consumer1
    producer2 ----^   ^-----consumer2
  * */

  def multipleProdConsBuffer():Unit = {
    val buffer:mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity:Int = 5

    val nConsumers:Int = 3
    val nProducers:Int = 4

    val consumers: Seq[Thread] =
      (1 to nConsumers)
        .map(
          _ => {
            new Thread(
              () => {
                val random = new Random()
                while (true) {
                  buffer.synchronized {
                    if (buffer.isEmpty) {
                      println("[consumer] buffer empty, waiting...")
                      buffer.wait()
                    }

                    val x = buffer.dequeue()
                    println("[consumer] consumed " + x)

                    buffer.notify()

                  }

                  Thread.sleep(random.nextInt(250))

                }
              }
            )
          }
        )

    var i:Int = 0
    val producers:Seq[Thread] = {
      (1 to nProducers)
        .map(
          _ => new Thread(
            () => {
              val random  = new Random()

              while (true) {
                buffer.synchronized{
                  if (buffer.length == capacity) {
                    println("[producer] buffer full, waiting...")
                    buffer.wait()
                  }

                  println("[producer] produced value " + i)
                  buffer.enqueue(i)
                  buffer.notify()
                  i += 1

                  Thread.sleep(random.nextInt(500))

                }
              }
            }
          )
        )




    }

    producers.map(_.start)
    consumers.map(_.start)
  }



  class Consumer(id:Int, buffer:mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random:Random = new Random()

      while (true) {

        buffer.synchronized{
          /*
          producer produces value, two Cons are waiting.
          notify ONE consumer, notifies on buffer
          notify the other consumer
          * */
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer is empty, waiting...")
            buffer.wait()
          }

          val x:Int = buffer.dequeue() // OOps.!
          println(s"[consumer $id] value consumed " + x)
          buffer.notify()
        }

        Thread.sleep(250)
      }

    }
  }

  class Producer(id:Int,buffer:mutable.Queue[Int],capacity:Int) extends Thread {
    override def run(): Unit = {
      val random:Random = new Random()
      var i:Int = 0

      while (true) {
        buffer.synchronized {
          while (buffer.length == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        }

        Thread.sleep(500)
      }
    }
  }

  def multiProdCons(nConsumers:Int,nProducers:Int):Unit = {
    val buffer:mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i,buffer).start())
    (1 to nProducers).foreach(i => new Producer(i,buffer,capacity).start())
  }

  //multiProdCons(3,6)

  /*
  Exercises.
  1) think of an example where notifyALL acts in a different way than notify?
  2) create a deadlock (one thread or multiple thread block each other and they can't continue
  3) create a livelock
  * */



  // 2 creating a deadlock

  // I think creating a deadlock is rather easy. Two guys are trying to access the same info
  // and they won't be able to do it.

  def creatingDeadLock():Unit = {
    val buffer:mutable.Queue[Int] = new mutable.Queue[Int]
    val thread1:Thread = new Thread(() => {
      while (true) {
        buffer.synchronized(buffer.enqueue(2))
      }
    })

    val thread2:Thread = new Thread(() => {
      while (true) {
        buffer.synchronized(buffer.enqueue(3))
      }
    })

    val thread3:Thread = new Thread(() => {
      while (true) {
        buffer.synchronized{
          val x = buffer.dequeue()
          println("The value I currently have is " + x)
        }
      }
    })

    thread1.start()
    thread2.start()
  }

//  creatingDeadLock() // this is what I expected, JVM just stays on hold
  // Now lets see the instructor's solution

  def testNotifyAll():Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread( () => {
        bell.synchronized{
          println(s"[thread $i] waiting")
          bell.wait()
          println(s"[thread $i] hooray!")
        }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock'n roll!")
      bell.synchronized{
        bell.notify()
      }
    }).start()
  }


  //testNotifyAll()

  // 2 - deadlock

  case class Friend1(name:String) {
    def bow(other:Friend1) = {
      this.synchronized{
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other:Friend1) = {
      this.synchronized{
        println(s"$this: I am rising to my friend $other")
      }
    }
  }


  val sam = Friend1("Sam")
  val pierre = Friend1("Pierre")

//  new Thread(() => sam.bow(pierre)).start() // sam's lock,    | then pierre's lock
//  new Thread(() => pierre.bow(sam)).start() // pierre's lock  | then sam's lock

  // 3  - livelock

}