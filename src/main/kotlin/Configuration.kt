package test.oim.icf.sap.hr

import org.identityconnectors.common.security.GuardedString
import org.identityconnectors.framework.spi.AbstractConfiguration
import org.identityconnectors.framework.spi.ConfigurationProperty

class Configuration : AbstractConfiguration() {
    @get:ConfigurationProperty(order =  1, required = true)  var jcoDestination: String = ""
    @get:ConfigurationProperty(order =  2, required = true)  var jcoClientAshost: String = ""
    @get:ConfigurationProperty(order =  3, required = true)  var jcoClientGwhost: String = ""
    @get:ConfigurationProperty(order =  4, required = true)  var jcoClientGwserv: String = ""
    @get:ConfigurationProperty(order =  5, required = true)  var jcoClientClient: String = ""
    @get:ConfigurationProperty(order =  6, required = true)  var jcoClientSysnr: String = ""
    @get:ConfigurationProperty(order =  7, required = true)  var jcoClientLang: String = ""
    @get:ConfigurationProperty(order =  8, required = true)  var jcoClientUser: String = ""
    @get:ConfigurationProperty(order =  9, required = true)  var jcoClientPasswd: GuardedString = GuardedString("".toCharArray())
    @get:ConfigurationProperty(order = 10, required = true)  var jcoClientTrace: String = ""
    @get:ConfigurationProperty(order = 11, required = true)  var jcoTraceLevel: String = ""
    @get:ConfigurationProperty(order = 12, required = false) var jcoTracePath: String? = null
    @get:ConfigurationProperty(order = 13, required = true)  var jrfcTrace: String = ""
    
    override fun validate() {}
}
