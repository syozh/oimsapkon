package test.oim.icf.sap.hr

import org.identityconnectors.common.IOUtil
import org.identityconnectors.common.security.GuardedString
import org.identityconnectors.framework.api.ConnectorFacade
import org.identityconnectors.framework.api.ConnectorFacadeFactory
import org.identityconnectors.framework.api.ConnectorInfoManagerFactory
import org.identityconnectors.framework.api.ConnectorKey
import org.identityconnectors.framework.common.objects.*
import org.identityconnectors.framework.common.objects.filter.FilterBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ConnectorIT {
    private lateinit var connector: ConnectorFacade

    @Before
    fun setUp() {
        val bundleDirectory = System.getProperty("bundleDirectory")
        val bundleName = System.getProperty("bundleName")
        val bundleVersion = System.getProperty("bundleVersion")
        val connectorName = Connector::class.java.canonicalName

        val cfg = ConnectorInfoManagerFactory
            .getInstance()
            .getLocalManager(IOUtil.makeURL(File(bundleDirectory), "$bundleName-$bundleVersion.jar"))
            .findConnectorInfo(ConnectorKey(bundleName, bundleVersion, connectorName))
            .createDefaultAPIConfiguration()

        val props = cfg.configurationProperties
        props.setPropertyValue("jcoDestination", System.getProperty("jcoDestination"))
        props.setPropertyValue("jcoClientAshost", System.getProperty("jcoClientAshost"))
        props.setPropertyValue("jcoClientGwhost", System.getProperty("jcoClientGwhost"))
        props.setPropertyValue("jcoClientGwserv", System.getProperty("jcoClientGwserv"))
        props.setPropertyValue("jcoClientClient", System.getProperty("jcoClientClient"))
        props.setPropertyValue("jcoClientSysnr", System.getProperty("jcoClientSysnr"))
        props.setPropertyValue("jcoClientLang", System.getProperty("jcoClientLang"))
        props.setPropertyValue("jcoClientUser", System.getProperty("jcoClientUser"))
        props.setPropertyValue("jcoClientPasswd", GuardedString(System.getProperty("jcoClientPasswd").toCharArray()))
        props.setPropertyValue("jcoClientTrace", System.getProperty("jcoClientTrace"))
        props.setPropertyValue("jcoTraceLevel", System.getProperty("jcoTraceLevel"))
        props.setPropertyValue("jcoTracePath", System.getProperty("jcoTracePath"))
        props.setPropertyValue("jrfcTrace", System.getProperty("jrfcTrace"))

        connector = ConnectorFacadeFactory.getInstance().newInstance(cfg)
        connector.validate()
    }

    @Test
    fun test1_searchByUid() {
        val uid = "00003774"
        val options = OperationOptionsBuilder().build()
        val filter = FilterBuilder.equalTo(Uid(uid))
        connector.search(ObjectClass.ACCOUNT, filter, { obj ->
            obj.pprint()
            assertEquals(obj.uid.uidValue, uid)
            true
        }, options)
    }

    @Test
    fun test2_searchByLastname() {
        val lastname = "Успенська"
        val options = OperationOptionsBuilder().build()
        val filter = FilterBuilder.equalTo(AttributeBuilder.build("PERSONAL_DATA:LAST_NAME", lastname))
        connector.search(ObjectClass.ACCOUNT, filter, { obj ->
            obj.pprint()
            assertEquals(AttributeUtil.getStringValue(obj.getAttributeByName("PERSONAL_DATA:LAST_NAME")), lastname)
            true
        }, options)
    }

    @Test
    fun test3_searchByLastnameMask() {
        val lastnameStart = "Усов"
        val options = OperationOptionsBuilder().build()
        val filter = FilterBuilder.startsWith(AttributeBuilder.build("PERSONAL_DATA:LAST_NAME", lastnameStart))
        connector.search(ObjectClass.ACCOUNT, filter, { obj ->
            obj.pprint()
            assertTrue(AttributeUtil.getStringValue(obj.getAttributeByName("PERSONAL_DATA:LAST_NAME")).startsWith(lastnameStart))
            true
        }, options)
    }

    @Test
    fun test4_searchByLastnameAndFirtsname() {
        val lastname = "Усов"
        val firstname = "Сергей"
        val options = OperationOptionsBuilder().build()
        val filter = FilterBuilder.and(
            FilterBuilder.equalTo(AttributeBuilder.build("PERSONAL_DATA:LAST_NAME", lastname)),
            FilterBuilder.equalTo(AttributeBuilder.build("PERSONAL_DATA:FSTNAME", firstname))
        )
        connector.search(ObjectClass.ACCOUNT, filter, { obj ->
            obj.pprint()
            assertTrue(AttributeUtil.getStringValue(obj.getAttributeByName("PERSONAL_DATA:LAST_NAME")).equals(lastname))
            assertTrue(AttributeUtil.getStringValue(obj.getAttributeByName("PERSONAL_DATA:FSTNAME")).equals(firstname))
            true
        }, options)
    }
}

fun ConnectorObject.pprint() {
    this.attributes.sortedBy { it.name }.forEach { println("${it.name} -> ${it.value}") }
}