package test.oim.icf.sap.hr.bapi

import com.sap.conn.jco.JCoDestination
import java.util.*

class BapiAddressEmpGetDetailedList(destination: JCoDestination)
    : Bapi<BapiAddressEmpGetDetailedList>("BAPI_ADDRESSEMPGETDETAILEDLIST", destination) {

    fun findByUid(uid: String, date: Date = Date()) = execute(mapOf("EMPLOYEENUMBER" to uid, "TIMEINTERVALLOW" to date))

    fun readAddresses() = readTable("ADDRESS") { BapiItem("ADDRESS:${it["SUBTYPE"]}", it) }
}
