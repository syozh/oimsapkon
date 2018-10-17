package test.oim.icf.sap.hr.bapi

import com.sap.conn.jco.JCoDestination
import java.util.*

class BapiEmployeeGetData(destination: JCoDestination)
    : Bapi<BapiEmployeeGetData>("BAPI_EMPLOYEE_GETDATA", destination) {

    fun find(filter: Map<String, Any>?) = execute(filter ?: mapOf("LASTNAME_M" to "*"))

    fun findByUid(uid: String, date: Date = Date()) = execute(mapOf("EMPLOYEE_ID" to uid, "DATE" to date))

    fun readUidList() = readTable("PERSONAL_DATA") { it["PERNO"] as String }

    fun readPersonalData() = readTable("PERSONAL_DATA") { BapiItem("PERSONAL_DATA", it) }

    fun readOrgAssignments() = readTable("ORG_ASSIGNMENT") { BapiItem("ORG_ASSIGNMENT", it) }
}
