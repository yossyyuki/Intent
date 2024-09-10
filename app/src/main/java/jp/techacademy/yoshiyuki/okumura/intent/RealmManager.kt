package jp.techacademy.yoshiyuki.okumura.intent

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RealmManager {
    var realm: Realm? = null

    // Realmの設定
    val config = RealmConfiguration.Builder(schema = setOf(InputData::class))
        .name("myrealm.realm") // データベースファイル名を指定
        .build()

    fun setUp() {
        realm = Realm.open(config)
    }

    fun release() {
        realm?.close()
        realm = null
    }
}