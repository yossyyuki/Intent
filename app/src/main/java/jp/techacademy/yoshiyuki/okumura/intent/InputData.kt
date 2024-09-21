package jp.techacademy.yoshiyuki.okumura.intent

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.*

class InputData : RealmObject {
    @PrimaryKey
            /*LongをIntに変更*/
    var id: Int = 0
    var orderNumber: Int = 0
    var processName: String? = ""
    var workerName: String = ""
    var startDate: Date = Date()
    var endDate: Date? = null // ストップを押すまでnull
//    TODO:カラムの追加　計測時間　秒数？　開始時間と終了時間の2つをつくる方法もあり
//    TODO:InputdataをCSVにして吐き出す？それをggcloudに送るなど。商用利用について確認は必要。


}
