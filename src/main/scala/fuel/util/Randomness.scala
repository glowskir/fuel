package fuel.util

import scala.collection.generic.CanBuildFrom

/**
  * Trait intended to enable elegant use of different random number generators
  */
trait TRandom extends Serializable {
  def nextBoolean(): Boolean

  def nextBytes(bytes: Array[Byte])

  def nextDouble(): Double

  def nextFloat(): Float

  def nextGaussian(): Double

  def nextInt(): Int

  def nextInt(n: Int): Int

  def nextLong(): Long

  def setSeed(seed: Long)

  def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T])(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T]
}

/** Default random number generator - a wrapper on java.util.Random. 
  *
  * (copied from scala source code)
  */
class Random(val self: java.util.Random) extends TRandom with Serializable {
  /** Creates a new random number generator using a single long seed. */
  def this(seed: Long) = this(new java.util.Random(seed))

  /** Creates a new random number generator using a single integer seed. */
  def this(seed: Int) = this(seed.toLong)

  /** Creates a new random number generator. */
  def this() = this(new java.util.Random())

  override def nextBoolean(): Boolean = self.nextBoolean

  override def setSeed(seed: Long): Unit = self.setSeed(seed)

  override def nextBytes(bytes: Array[Byte]): Unit = self.nextBytes(bytes)

  override def nextDouble(): Double = self.nextDouble

  override def nextLong(): Long = self.nextLong

  override def nextFloat(): Float = self.nextFloat

  override def nextGaussian(): Double = self.nextGaussian

  override def nextInt(): Int = self.nextInt

  override def nextInt(n: Int): Int = self.nextInt(n)

  override def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T])(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T] = {
    new scala.util.Random(self).shuffle(xs)
  }
}

object Rng {
  def apply(implicit conf: Options) = new Random(conf("seed", 1))
}

