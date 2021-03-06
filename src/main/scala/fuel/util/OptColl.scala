package fuel.util

/**
  * Convenience functions for simultaneous construction of most common
  *  environment.
  */

object GetOptColl {
  def apply(args: Array[String]) = {
    val opt = Options(args)
    (opt, new CollectorFile(opt))
  }
  def apply(args: String = "") = {
    val opt = Options(args)
    (opt, new CollectorFile(opt))
  }
  def apply(args: Map[Symbol,Any]) = {
    val opt = Options(args)
    (opt, new CollectorFile(opt))
  }
}

object GetOptCollRng {
  def apply(args: Array[String]) = {
    val optColl = GetOptColl(args)
    (optColl._1, optColl._2, Rng(optColl._1))
  }
  def apply(args: String = "") = {
    val optColl = GetOptColl(args)
    (optColl._1, optColl._2, Rng(optColl._1))
  }
  def apply(args: Map[Symbol,Any]) = {
    val optColl = GetOptColl(args)
    (optColl._1, optColl._2, Rng(optColl._1))
  }
}

