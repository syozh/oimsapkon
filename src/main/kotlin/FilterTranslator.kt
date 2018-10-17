package test.oim.icf.sap.hr

import org.identityconnectors.framework.common.exceptions.ConnectorException
import org.identityconnectors.framework.common.objects.AttributeUtil
import org.identityconnectors.framework.common.objects.filter.*

class FilterTranslator : AbstractFilterTranslator<Map<String, Any>>() {
    companion object {
        val MAPPING = mapOf(
            "__UID__" to "EMPLOYEE_ID",
            "PERSONAL_DATA:LAST_NAME" to "LASTNAME_M",
            "PERSONAL_DATA:FIRSTNAME" to "FSTNAME_M",
            "ORG_ASSIGNMENT:ORGTXT" to "ORGTXT",
            "ORG_ASSIGNMENT:JOBTXT" to "JOBTXT",
            "ORG_ASSIGNMENT:POSTXT" to "POSTXT",
            "ORG_ASSIGNMENT:COSTCENTER" to "COSTCENTER"
        )
    }

    override fun createEqualsExpression(filter: EqualsFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createEqualsIgnoreCaseExpression(filter: EqualsIgnoreCaseFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createStartsWithExpression(filter: StartsWithFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createStartsWithIgnoreCaseExpression(filter: StartsWithIgnoreCaseFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createEndsWithExpression(filter: EndsWithFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createEndsWithIgnoreCaseExpression(filter: EndsWithIgnoreCaseFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createContainsExpression(filter: ContainsFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createContainsIgnoreCaseExpression(filter: ContainsIgnoreCaseFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createGreaterThanExpression(filter: GreaterThanFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createGreaterThanOrEqualExpression(filter: GreaterThanOrEqualFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createLessThanExpression(filter: LessThanFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createLessThanOrEqualExpression(filter: LessThanOrEqualFilter, not: Boolean) =
        createSimpleExpression(filter, not)

    override fun createAndExpression(left: Map<String, Any>, right: Map<String, Any>) =
        left + right

    override fun createOrExpression(left: Map<String, Any>, right: Map<String, Any>) =
        throw ConnectorException("Unsupported filter expr: OR(..,..)")

    @Suppress("UNCHECKED_CAST")
    private fun createSimpleExpression(filter: AttributeFilter, not: Boolean): Map<String, Any> {
        if (not) throw ConnectorException("Unsupported filter expr: NOT(..)")
        val n = filter.attribute.name
        val v = AttributeUtil.getAsStringValue(filter.attribute)
        val p = when (filter) {
            is EqualsFilter -> MAPPING[n] to v
            is StartsWithFilter -> MAPPING[n] to "$v*"
            is EndsWithFilter -> MAPPING[n] to "*$v"
            is ContainsFilter -> MAPPING[n] to "*$v*"
            else -> throw ConnectorException("Unsupported filter: ${filter::class.java}")
        }
        if (p.first == null) throw ConnectorException("Unsupported filter attribute: $n")
        return mapOf(p) as Map<String, Any>
    }
}
