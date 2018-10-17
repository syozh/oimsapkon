package test.oim.icf.sap.hr

import org.identityconnectors.common.logging.Log
import org.identityconnectors.framework.common.exceptions.ConnectorException
import org.identityconnectors.framework.common.objects.ObjectClass
import org.identityconnectors.framework.common.objects.OperationOptions
import org.identityconnectors.framework.common.objects.ResultsHandler
import org.identityconnectors.framework.spi.ConnectorClass
import org.identityconnectors.framework.spi.PoolableConnector
import org.identityconnectors.framework.spi.operations.SearchOp
import org.identityconnectors.framework.spi.operations.TestOp

@ConnectorClass(configurationClass = Configuration::class, displayNameKey = "connector.display")
class Connector : PoolableConnector, SearchOp<Map<String, Any>>, TestOp {

    companion object {
        private val LOG = Log.getLog(Connector::class.java)
    }

    private lateinit var configuration: Configuration
    private lateinit var dao: Dao

    override fun getConfiguration() = configuration

    override fun init(configuration: org.identityconnectors.framework.spi.Configuration) {
        try {
            this.configuration = configuration as Configuration
            this.dao = Dao(configuration)
        } catch (e: Exception) {
            LOG.error(e.message)
            throw ConnectorException(e)
        }
    }

    override fun dispose() { }

    override fun checkAlive() = test()

    override fun test() = dao.test()

    override fun createFilterTranslator(clazz: ObjectClass, options: OperationOptions): FilterTranslator {
        LOG.ok("objectClass={0}, options={1}", clazz, options.options)
        return FilterTranslator()
    }

    override fun executeQuery(clazz: ObjectClass, filter: Map<String, Any>?, handler: ResultsHandler, options: OperationOptions) {
        LOG.ok("objectClass={0}, filter={1}, options={2}", clazz, filter, options.options)
        if (clazz != ObjectClass.ACCOUNT) throw ConnectorException("Unsupported object class: $clazz")
        try {
            reconUsers(filter, handler)
        } catch (e: Exception) {
            LOG.error(e.message)
            throw ConnectorException(e)
        }
    }

    private fun reconUsers(filter: Map<String, Any>?, handler: ResultsHandler) {
        dao.getUidList(filter).forEach { handler.handle(dao.getUser(it)) }
    }
}
