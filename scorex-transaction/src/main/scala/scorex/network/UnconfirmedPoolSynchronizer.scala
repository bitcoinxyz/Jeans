package scorex.network

import scorex.app.Application
import scorex.network.NetworkController.DataFromPeer
import scorex.network.TransactionalMessagesRepo.TransactionMessageSpec
import scorex.network.redone.ViewSynchronizer
import scorex.transaction.Transaction
import scorex.utils.ScorexLogging


class UnconfirmedPoolSynchronizer(application: Application) extends ViewSynchronizer with ScorexLogging {

  override val messageSpecs = Seq(TransactionMessageSpec)

  override val networkControllerRef = application.networkController

  override def receive = {
    case DataFromPeer(msgId, tx: Transaction, remote) if msgId == TransactionMessageSpec.messageCode =>
      println(s"got tx: $tx")
      //todo: check / put to pool
  }
}
