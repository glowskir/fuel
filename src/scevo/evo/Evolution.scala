package scevo.evo


class Evolution[S <: Solution, ES <: EvaluatedSolution](val initialState: State[S]) {

  var generation: Int = 0
  var bestOfRun = None: Option[ES]
  var ideal = None: Option[ES]
  
  var log = List[String]()
  def log(s : String){
    val sg = "Generation: " + generation + s
    println( sg )
    log = log :+ sg
  }

  /* Performs evolutionary run. 
   * Returns the final state of evolutionary process, the best of run solution, and the ideal solution (if found) */
  def apply(search: SearchAlgorithm[S, ES],
    stopConditions: Seq[(State[S], Evolution[S, ES]) => Boolean],
    idealSolutionFilter: Option[Seq[ES] => Option[ES]],
    postGenerationCallback: Option[(Evolution[S,ES], State[S], Seq[ES], ES)  => State[S]]): (State[S], ES, Option[ES]) = {

    assert(!stopConditions.isEmpty, "At least one stopping condition has to be defined")

    println("Search process started")
    var currentState = initialState
    var previousPopulation = Seq[ES]() // needed for some algorithms, e.g. NSGA-II
    var stop: Boolean = false
    do {
      generation += 1
      var nextStep = search(currentState, previousPopulation)
      while (nextStep.isEmpty && stopConditions.forall(sc => !sc(currentState, this))) {
        log( "None of candidate solutions passed the evaluation stage. Restarting. ")
        currentState = initialState
        nextStep = search(currentState, previousPopulation)
      }
      if( nextStep.isEmpty )
    	  return (currentState, bestOfRun.get, ideal)
      val (state, evaluatedSolutions) = nextStep.get
      val bestOfGen = BestSelector.apply(evaluatedSolutions)
      if (bestOfRun.isEmpty || bestOfGen.betterThan(bestOfRun.get).getOrElse(false)) bestOfRun = Some(bestOfGen)
      println(s"Generation: $generation  BestOfGen: ${bestOfGen.fitness}  BestSoFar: ${bestOfRun.get.fitness}")
      ideal = idealSolutionFilter.getOrElse((s: Seq[ES]) => None).apply(evaluatedSolutions)
      stop = !stopConditions.forall(sc => !sc(state, this)) || ideal.isDefined
      currentState = if( postGenerationCallback.isEmpty ) state 
      	else postGenerationCallback.get(this,state,evaluatedSolutions,bestOfGen)
      previousPopulation = evaluatedSolutions
    } while (!stop)

    println("Search process completed")
    (currentState, bestOfRun.get, ideal)
  }
}

class StopConditionMaxGenerations(maxGenerations: Int) extends ((State[_], Evolution[_, _]) => Boolean) {
  def apply(state: State[_], evolution: Evolution[_, _]) = evolution.generation >= maxGenerations
}

class StopConditionMaxTime(maxMillisec: Long) extends ((State[_], Evolution[_, _]) => Boolean) {
  val startTime = System.currentTimeMillis()
  def apply(state: State[_], evolution: Evolution[_, _]) =
    System.currentTimeMillis() - startTime > maxMillisec
  def timeElapsed = System.currentTimeMillis() - startTime
}

