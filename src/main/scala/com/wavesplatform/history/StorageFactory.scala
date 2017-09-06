package com.wavesplatform.history

import java.util.concurrent.locks.{ReentrantReadWriteLock => RWL}
import javax.sql.DataSource

import com.wavesplatform.database.SQLiteWriter
import com.wavesplatform.features.FeatureProvider
import com.wavesplatform.settings.WavesSettings
import com.wavesplatform.state2.reader.SnapshotStateReader
import com.wavesplatform.state2.{BlockchainUpdaterImpl, StateWriter}
import scorex.transaction._
import scorex.utils.Time

import scala.util.Try

object StorageFactory {

  def apply(settings: WavesSettings, ds: DataSource, time: Time): Try[(NgHistory with DebugNgHistory, FeatureProvider, StateWriter, SnapshotStateReader, BlockchainUpdater, BlockchainDebugInfo)] = {
    val lock = new RWL(true)
    for {
      historyWriter <- HistoryWriterImpl(settings.blockchainSettings.blockchainFile, lock, settings.blockchainSettings.functionalitySettings, settings.featuresSettings)
      stateWriter = new SQLiteWriter(ds)
    } yield {
      val bcu = BlockchainUpdaterImpl(stateWriter, historyWriter, settings, time, lock)
      val history: NgHistory with DebugNgHistory with FeatureProvider = bcu.historyReader
      (history, history, stateWriter, bcu.bestLiquidState, bcu, bcu)
    }
  }
}
