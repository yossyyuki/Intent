package jp.techacademy.yoshiyuki.okumura.intent

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class InputData : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var content: String? = null
}