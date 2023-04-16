package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.lang.String.format;

@Slf4j
public class MappingRulesUtil {
    private static final List<String> relatorTermFieldsWithSubfieldEtoUpdate = List.of("100", "110", "700", "710" );
    private static final List<String> relatorTermFieldsWithSubfieldJtoUpdate = List.of("111", "711" );
    private final String ENTITY = "entity";
    private final String TARGET = "target";
    private final String CONTRIBUTOR_TYPE_ID = "contributors.contributorTypeId";
    private final String CONTRIBUTOR_TYPE_TEXT = "contributors.contributorTypeText";
    private final String UPDATED_MAPPING_RULES_PATH = "mappingRules/";
    private final String RELATOR_TERM_E_SUBFIELD_TARGET = "relatorTerm/relatorTermEsubfieldTarget.json";
    private final String RELATOR_TERM_J_SUBFIELD_TARGET = "relatorTerm/relatorTermJsubfieldTarget.json";

    public void relatorTermUpdate(JsonNode mappingRules) {
        log.info(format("Updating relator term mapping rules, subfield \"e\" for fields: %s", StringUtils.collectionToCommaDelimitedString(relatorTermFieldsWithSubfieldEtoUpdate)));
        updateContributorTypeByFields(mappingRules, relatorTermFieldsWithSubfieldEtoUpdate,
                UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_E_SUBFIELD_TARGET);

        log.info(format("Updating relator term mapping rules, subfield \"j\" for fields: %s", StringUtils.collectionToCommaDelimitedString(relatorTermFieldsWithSubfieldJtoUpdate)));
        updateContributorTypeByFields(mappingRules, relatorTermFieldsWithSubfieldEtoUpdate,
                UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_J_SUBFIELD_TARGET);

        mappingRules.get(0);
    }

    private void updateContributorTypeByFields(JsonNode mappingRules, List<String> fieldsToUpdate, String updateTargetFilePath) {
        for (String field : fieldsToUpdate) {
            for (JsonNode entityRule : mappingRules.get(field)) {
                ArrayNode rules = (ArrayNode) entityRule.get(ENTITY);
                for (int i = 0; i < rules.size(); i++) {
                    JsonNode rule = rules.get(i);
                    if (rule.get(TARGET).asText().equals(CONTRIBUTOR_TYPE_ID) || rule.get(TARGET).asText().equals(CONTRIBUTOR_TYPE_TEXT)) {
                        rules.remove(i);
                    }
                }
                rules.add(FileWorker.getJsonObject(updateTargetFilePath));
            }
        }
    }
}
