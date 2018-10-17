package test.oim.icf.sap.hr.bapi

import com.sap.conn.jco.JCoDestination
import java.util.*

class BapiEmplCommGetDetailedList(destination: JCoDestination)
    : Bapi<BapiEmplCommGetDetailedList>("BAPI_EMPLCOMM_GETDETAILEDLIST", destination, false) {

    fun findByUid(uid: String, date: Date = Date()) = execute(mapOf("EMPLOYEENUMBER" to uid, "TIMEINTERVALLOW" to date))

    fun readCommunications() = readTable("COMMUNICATION") { BapiItem("COMMUNICATION:${it["COMMTYPE"]}", it) }
}
