package test.oim.icf.sap.hr.bapi

import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoRecord
import org.identityconnectors.common.logging.Log
import org.identityconnectors.framework.common.objects.AttributeBuilder
import java.util.*

class BapiException(message: String) : Exception(message)

open class Bapi<T : Bapi<T>>(name: String,
                             private val destination: JCoDestination,
                             private val exceptionOnError: Boolean = true) {

    companion object {
        private val LOG = Log.getLog(Bapi::class.java)

        private fun logLevelOf(type: String?) = when (type) {
            "E" -> Log.Level.ERROR
            "W" -> Log.Level.WARN
            "I" -> Log.Level.INFO
            else -> Log.Level.OK
        }
    }

    private val function = destination.repository.getFunction(name)
    private var success = false

    @Suppress("UNCHECKED_CAST")
    fun execute(args: Map<String, Any>): T {
        LOG.ok("""BAPI "{0}": execute: args={1}""", function.name, args)
        val imports = function.importParameterList
        imports.clear()
        for ((k, v) in args) imports.setValue(k, v)
        function.execute(destination)
        check()
        return this as T
    }

    fun <S> readTable(name: String, factory: (Map<String, Any?>) -> S): List<S> {
        if (!success) return emptyList()
        val t = function.tableParameterList.getTable(name)
        val l = mutableListOf<S>()
        for (i in 0 until t.numRows) {
            t.row = i
            l.add(factory.invoke(readRow(t)))
        }
        return l
    }

    private fun readRow(row: JCoRecord): Map<String, Any?> {
        val m = mutableMapOf<String, Any?>()
        row.forEach { m[it.name] = it.value }
        return m
    }

    private fun check() {
        success = true
        val r = function.exportParameterList?.getStructure("RETURN") ?: return
        val t = r.getString("TYPE")
        val m = r.getString("MESSAGE")
        val s = """BAPI "${function.name}": return: type="$t", message="$m""""
        LOG.log(logLevelOf(t), null, s)
        LOG.ok("""BAPI "${function.name}": debug: ${function.toXML()}""")
        if (t == "E") {
            success = false
            if (exceptionOnError) throw BapiException(m)
        }
    }
}

open class BapiItem(private val prefix: String, private val fields: Map<String, Any?>) {
    open fun makeAttributeName(fieldName: String) = "$prefix:$fieldName"

    open fun makeAttributeValue(fieldValue: Any?) = when (fieldValue) {
        is Date -> fieldValue.time
        else -> fieldValue
    }

    open fun makeAttribute(fieldName: String, fieldValue: Any?) =
        AttributeBuilder.build(makeAttributeName(fieldName), makeAttributeValue(fieldValue))

    open fun toAttributes() = fields.map { (name, value) -> makeAttribute(name, value) }
}

fun BapiItem?.toAttributes() = this?.toAttributes() ?: listOf()
