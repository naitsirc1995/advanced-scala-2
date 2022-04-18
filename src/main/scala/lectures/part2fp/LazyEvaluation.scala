package lectures.part2fp

object LazyEvaluation extends App
{
  // lazy DELAYS the evaluation of values
  lazy val x:Int = {
    println("hello")
    42
  }
  println(x)
  println(x)

  // examples of implications:
  // side effects
  def sideEffectCondition:Boolean = {
    println("Boo")
    true
  }

  def simpleCondition:Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  // in conjunction with call by name
  def byNameMethod(n: =>Int):Int = n + n + n + 1
  def retriveMagicValue = {
    // side effect or a long computation
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retriveMagicValue))
  // I think the side effect will happen only once. Maybe.
}