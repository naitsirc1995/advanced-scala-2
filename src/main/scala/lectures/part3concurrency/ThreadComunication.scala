package lectures.part3concurrency


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

  naiveProdCons()

}