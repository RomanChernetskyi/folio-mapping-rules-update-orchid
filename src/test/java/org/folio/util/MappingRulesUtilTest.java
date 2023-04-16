package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.folio.util.MappingRulesUtil.*;


public class MappingRulesUtilTest {
    private MappingRulesUtil mappingRulesUtil = new MappingRulesUtil();
    private final String MAPPING_RULES_PATH = "mappingRules.json";
    private final String MAPPING_RULES_RESULT_PATH = "relatorTermRules.json";
    private ObjectNode mappingRules;
    private ObjectNode mappingRulesResult;


    private JsonNode relatorTermSubfieldE;
    private JsonNode relatorTermSubfieldJ;

    @Before
    public void setup() {
        relatorTermSubfieldE = FileWorker.getJsonObject(UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_E_SUBFIELD_TARGET);
        relatorTermSubfieldJ = FileWorker.getJsonObject(UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_J_SUBFIELD_TARGET);
        mappingRules = FileWorker.getJsonObject(MAPPING_RULES_PATH);
        mappingRulesResult = FileWorker.getJsonObject(MAPPING_RULES_RESULT_PATH);
    }

    @Test
    public void shouldUpdateRelatorTerm() {
        mappingRulesUtil.relatorTermUpdate(mappingRules);

        JsonNode mappingRulesResult = FileWorker.getJsonObject(MAPPING_RULES_RESULT_PATH);
        Assert.assertEquals(mappingRulesResult, mappingRules);
    }

    @Test
    public void shouldUpdateRelatorTermIfIncomingMarcRulesDoNotContainField() {
        mappingRules.remove("100");
        mappingRules.remove("111");

        mappingRulesUtil.relatorTermUpdate(mappingRules);

        mappingRulesResult.remove("100");
        mappingRulesResult.remove("111");
        Assert.assertEquals(mappingRulesResult, mappingRules);
    }
}