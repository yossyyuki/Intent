package jp.techacademy.yoshiyuki.okumura.intent

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ProcessData : RealmObject {
    @PrimaryKey
            /*LongをIntに変更*/
    var id: Int = 0
    var ProcessName: Int = 0
}