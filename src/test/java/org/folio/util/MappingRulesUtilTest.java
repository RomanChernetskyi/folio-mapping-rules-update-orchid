package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MappingRulesUtilTest {
    private MappingRulesUtil mappingRulesUtil = new MappingRulesUtil();
    private final String MAPPING_RULES_PATH = "mappingRules.json";
    private final String RELATOR_TERM_MAPPING_RULES_RESULT_PATH = "relatorTermRules.json";
    private final String AUTHORITY_CONTROL_MAPPING_RULES_RESULT_PATH = "authorityControlRules.json";
    private final String COMPLETE_MAPPING_RULES_RESULT_PATH = "completeMappingRules.json";
    private final String OVERLAP_RULES = "overlapRulesTest.json";
    private final String INCORRECT_RULES = "incorrectRules.json";
    private ObjectNode mappingRules;
    private ObjectNode relatorTermMappingRulesResult;
    private ObjectNode authorityControlMappingRulesResult;
    private ObjectNode completeMappingRulesResult;

    @Before
    public void setup() {
        mappingRules = FileWorker.getJsonObject(MAPPING_RULES_PATH);
        relatorTermMappingRulesResult = FileWorker.getJsonObject(RELATOR_TERM_MAPPING_RULES_RESULT_PATH);
        authorityControlMappingRulesResult = FileWorker.getJsonObject(AUTHORITY_CONTROL_MAPPING_RULES_RESULT_PATH);
        completeMappingRulesResult = FileWorker.getJsonObject(COMPLETE_MAPPING_RULES_RESULT_PATH);
    }

    @Test
    public void shouldUpdateRelatorTerm() {
        mappingRulesUtil.relatorTermUpdate(mappingRules);
        Assert.assertEquals(relatorTermMappingRulesResult, mappingRules);
    }

    @Test
    public void shouldUpdateRelatorTermIfIncomingMarcRulesDoNotContainField() {
        mappingRules.remove("100");
        mappingRules.remove("111");

        mappingRulesUtil.relatorTermUpdate(mappingRules);

        relatorTermMappingRulesResult.remove("100");
        relatorTermMappingRulesResult.remove("111");
        Assert.assertEquals(relatorTermMappingRulesResult, mappingRules);
    }

    @Test
    public void shouldUpdateAuthorityControl() {
        mappingRulesUtil.authorityControlUpdate(mappingRules);
        Assert.assertEquals(authorityControlMappingRulesResult, mappingRules);
    }

    @Test
    public void shouldUpdateAuthorityControlIfIncomingMarcRulesDoNotContainField() {
        mappingRules.remove("130");
        mappingRules.remove("100");
        mappingRules.remove("800");
        mappingRules.remove("600");

        mappingRulesUtil.authorityControlUpdate(mappingRules);

        authorityControlMappingRulesResult.remove("130");
        authorityControlMappingRulesResult.remove("100");
        authorityControlMappingRulesResult.remove("800");
        authorityControlMappingRulesResult.remove("600");
        Assert.assertEquals(authorityControlMappingRulesResult, mappingRules);
    }

    @Test
    public void shouldUpdateAuthorityControlAndRelatorTermMappingRules() {
        mappingRulesUtil.relatorTermUpdate(mappingRules);
        mappingRulesUtil.authorityControlUpdate(mappingRules);

        Assert.assertEquals(completeMappingRulesResult, mappingRules);
    }

    @Test
    public void shouldNotOverlapMappingRulesOnUpdate() {
        JsonNode overlapMappingRulesResult = FileWorker.getJsonObject(OVERLAP_RULES);
        JsonNode mappingRules = overlapMappingRulesResult.deepCopy();

        mappingRulesUtil.relatorTermUpdate(mappingRules);
        mappingRulesUtil.authorityControlUpdate(mappingRules);

        Assert.assertEquals(overlapMappingRulesResult, mappingRules);
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorsForIncorrectIncomingMappingRuleOnRelatorTermUpdate() {
        JsonNode incorrectRules = FileWorker.getJsonObject(INCORRECT_RULES);
        mappingRulesUtil.relatorTermUpdate(incorrectRules);
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorsForIncorrectIncomingMappingRuleOnAuthorityControl() {
        JsonNode incorrectRules = FileWorker.getJsonObject(INCORRECT_RULES);
        mappingRulesUtil.authorityControlUpdate(incorrectRules);
    }
}