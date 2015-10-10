package scevo.func 

import java.util.Calendar
import scevo.evo.State
import scevo.tools.Options
import scevo.tools.Collector

object Experiment {
  def run[S <: State](alg: Unit => S)(implicit opt: Options, coll: Collector) = 
    apply(alg)(opt,coll)()
  def apply[S <: State](alg: Unit => S)(implicit opt: Options, coll: Collector) 
  : (Unit => Option[S]) = {
    _: Unit =>
      {
        val startTime = System.currentTimeMillis()
        try {
          val state = alg()
          coll.set("status", "completed")
          if (opt.paramString("saveLastState", "false") == "true")
            coll.write("lastState", state)
          Some(state)
        } catch {
          case e: Exception => {
            coll.set("status", "error: " + e.getLocalizedMessage + e.getStackTrace().mkString(" ")) // .toString.replace('\n', ' '))
            throw e
          }
          None
        } finally {
          coll.setResult("totalTimeSystem", System.currentTimeMillis() - startTime)
          coll.setResult("system.endTime", Calendar.getInstance().getTime().toString)
          if(opt.paramBool("printResults"))
            println(coll.rdb.toString)
          coll.close
          opt.warnNonRetrieved
          None
        }
      }
  }
}


