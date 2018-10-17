package test.oim.icf.sap.hr

import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoDestinationManager
import com.sap.conn.jco.ext.DestinationDataEventListener
import com.sap.conn.jco.ext.DestinationDataProvider
import com.sap.conn.jco.ext.Environment
import java.util.*

internal class DestinationDataProviderImpl : DestinationDataProvider {
    companion object {
        val instance = DestinationDataProviderImpl()
    }

    private val map = mutableMapOf<String, Properties>()

    override fun setDestinationDataEventListener(el: DestinationDataEventListener) {}

    override fun supportsEvents() = false

    override fun getDestinationProperties(destination: String) = map[destination]

    fun addDestination(destinationName: String, properties: Properties): JCoDestination {
        map[destinationName] = properties
        if (!Environment.isDestinationDataProviderRegistered())
            Environment.registerDestinationDataProvider(instance)
        return JCoDestinationManager.getDestination(destinationName)
    }
}
