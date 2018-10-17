package test.oim.icf.sap.hr

import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.ext.DestinationDataProvider
import org.identityconnectors.common.security.GuardedString
import org.identityconnectors.framework.common.objects.*
import test.oim.icf.sap.hr.bapi.*
import java.util.*

class Dao(configuration: Configuration) {
    private val destination: JCoDestination
    private val bapiEmployeeGetData: BapiEmployeeGetData
    private val bapiAddressEmpGetDetailedList: BapiAddressEmpGetDetailedList
    private val bapiEmplCommGetDetailedList: BapiEmplCommGetDetailedList

    init {
        val properties = Properties()
        properties.setProperty(DestinationDataProvider.JCO_ASHOST, configuration.jcoClientAshost)
        properties.setProperty(DestinationDataProvider.JCO_GWHOST, configuration.jcoClientGwhost)
        properties.setProperty(DestinationDataProvider.JCO_GWSERV, configuration.jcoClientGwserv)
        properties.setProperty(DestinationDataProvider.JCO_CLIENT, configuration.jcoClientClient)
        properties.setProperty(DestinationDataProvider.JCO_SYSNR,  configuration.jcoClientSysnr)
        properties.setProperty(DestinationDataProvider.JCO_LANG,   configuration.jcoClientLang)
        properties.setProperty(DestinationDataProvider.JCO_USER,   configuration.jcoClientUser)
        properties.setProperty(DestinationDataProvider.JCO_PASSWD, decrypt(configuration.jcoClientPasswd))
        properties.setProperty(DestinationDataProvider.JCO_TRACE,  configuration.jcoClientTrace)
        properties.setProperty("jco.trace_level",                  configuration.jcoTraceLevel)
        if (configuration.jcoTracePath != null) properties.setProperty("jco.trace_path", configuration.jcoTracePath)
        properties.setProperty("jrfc.trace", configuration.jrfcTrace)

        destination = DestinationDataProviderImpl.instance.addDestination(configuration.jcoDestination, properties)
        bapiEmployeeGetData = BapiEmployeeGetData(destination)
        bapiAddressEmpGetDetailedList = BapiAddressEmpGetDetailedList(destination)
        bapiEmplCommGetDetailedList = BapiEmplCommGetDetailedList(destination)
    }

    private fun decrypt(password: GuardedString): String {
        lateinit var s : String
        password.access { s = String(it) }
        return s
    }

    fun test() = destination.ping()

    fun getUidList(filter: Map<String, Any>?) = bapiEmployeeGetData.find(filter).readUidList()

    fun getUser(uid: String): ConnectorObject {
        val attrs = mutableSetOf(Uid(uid), Name(uid))
        val items = mutableListOf<BapiItem>()
        bapiEmployeeGetData.findByUid(uid)
        items.addAll(bapiEmployeeGetData.readPersonalData())
        items.addAll(bapiEmployeeGetData.readOrgAssignments())
        items.addAll(bapiAddressEmpGetDetailedList.findByUid(uid).readAddresses())
        items.addAll(bapiEmplCommGetDetailedList.findByUid(uid).readCommunications())
        items.forEach { attrs.addAll(it.toAttributes()) }
        return ConnectorObjectBuilder().setObjectClass(ObjectClass.ACCOUNT).addAttributes(attrs).build()
    }
}