package jp.techacademy.yoshiyuki.okumura.intent

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class InputData : RealmObject {
    @PrimaryKey
            /*LongをIntに変更*/
    var id: Int = 0
    var orderNumber: Int = 0
    var processName: String? = ""
    var workerName: String = ""


}
