package unmeshjoshi

import java.util.concurrent.ForkJoinPool

import scala.concurrent.duration._

class Dispatcher(val executorService: ForkJoinPool) {
  final val throughputDeadlineTime: Duration = 10.millis

  final val isThroughputDeadlineTimeDefined = throughputDeadlineTime.toMillis > 0

  final val throughput: Int = 10

  /**
    * Queue the message and schedule the mailbox for execution
    */
  def dispatch(receiver: ActorCell, invocation: Envelope): Unit = {
    val mailbox = receiver.mailbox
    mailbox.enqueue(invocation)
    registerForExecution(mailbox)
  }

  /**
    * Suggest to register the provided mailbox for execution
    */
  def registerForExecution(mailbox: Mailbox): Unit = {
    if (mailbox.canBeScheduled) {
      if (mailbox.setAsScheduled()) {
        executorService.execute(mailbox)
      }
    }
  }
}
