package scevo.func

import scevo.core.State
import scevo.util.Options
import scevo.core.StatePop
import scevo.util.Counter

object Termination {

  object MaxTime {
    def apply(opt: Options) = {
      val maxMillisec = opt.paramInt("maxTime", 86400000, _ > 0)
      val startTime = System.currentTimeMillis()
      def timeElapsed = System.currentTimeMillis() - startTime
      s: Any => timeElapsed > maxMillisec
    }
  }
  class Count {
    def apply(cnt: Counter, max: Long) = {
      s: Any => cnt.count >= max
    }
  }
  object MaxIter extends Count {
    def apply[S <: State](cnt: Counter)(implicit opt: Options) = {
      val maxGenerations = opt('maxGenerations, 50, (_:Int) > 0)
      super.apply(cnt, maxGenerations)
    }
  }  
  def apply[S, E](otherCond: (S, E) => Boolean = (_: S, _: E) => false)(implicit config: Options) = Seq(
    MaxTime(config),
    (s: StatePop[(S, E)]) => s.exists(es => otherCond(es._1, es._2)))
}