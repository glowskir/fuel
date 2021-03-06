package fuel.example

import scala.Range
import fuel.func.RunExperiment
import fuel.func.SimpleEA
import fuel.moves.PermutationMoves
import fuel.util.OptColl

/**
  * Traveling Salesperson problem.
  *
  * Minimized fitness function.
  */

object TSP extends App {
  new OptColl('numCities -> 30, 'maxGenerations -> 300) {

    // Generate random distance matrix
    val numCities = opt('numCities, (_: Int) > 0)
    val cities = Seq.fill(numCities)((rng.nextDouble, rng.nextDouble))
    val distances = for (i <- cities) yield for (j <- cities)
      yield math.hypot(i._1 - j._1, i._2 - j._2)

    // Fitness function
    def eval(s: Seq[Int]) =
      Range(0, s.size).map(i => distances(s(i))(s((i + 1) % s.size))).sum

    RunExperiment(SimpleEA(PermutationMoves(numCities), eval))
  }
}


