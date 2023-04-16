package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.lang.String.format;

@Slf4j
public class MappingRulesUtil {
    public static final List<String> contributorsFieldsWithSubfieldEtoUpdate = List.of("100", "110", "700", "710" );
    public static final List<String> contributorsFieldsWithSubfieldJtoUpdate = List.of("111", "711" );
    public static final List<String> alternativeTitlesFields = List.of("130", "240" );
    public static final List<String> contributorsFields = List.of("100", "110", "111", "700", "710", "711" );
    public static final List<String> seriesFields = List.of("800", "810", "811", "830" );
    public static final List<String> subjectsFields = List.of("600", "610", "611", "630", "650", "651", "655" );
    public static final String ENTITY = "entity";
    public static final String TARGET = "target";
    public static final String SERIES = "series";
    public static final String SUBJECTS = "subjects";
    public static final String DOT = ".";
    public static final String VALUE = "value";
    public static final String CONTRIBUTOR_TYPE_ID = "contributors.contributorTypeId";
    public static final String CONTRIBUTOR_TYPE_TEXT = "contributors.contributorTypeText";
    public static final String UPDATED_MAPPING_RULES_PATH = "mappingRules/";
    public static final String RELATOR_TERM_E_SUBFIELD_TARGET = "relatorTerm/contributorsFieldsWithSubfieldETarget.json";
    public static final String RELATOR_TERM_J_SUBFIELD_TARGET = "relatorTerm/contributorsFieldsWithSubfieldJTarget.json";
    public static final String ALTERNATIVE_TITLE_TARGET = "authorityControl/alternativeTitlesTarget.json";
    public static final String CONTRIBUTORS_TARGET = "authorityControl/contributorsTarget.json";
    public static final String SERIES_TARGET = "authorityControl/seriesTarget.json";
    public static final String SUBJECTS_TARGET = "authorityControl/subjectsTarget.json";

    public void relatorTermUpdate(JsonNode mappingRules) {
        log.info(format("Updating relator term mapping rules, subfield \"e\" for fields: %s", StringUtils.collectionToCommaDelimitedString(contributorsFieldsWithSubfieldEtoUpdate)));
        contributorsFieldsWithSubfieldEtoUpdate.forEach(field -> {
            try {
                JsonNode fieldRule = mappingRules.get(field);
                removeTarget(fieldRule, CONTRIBUTOR_TYPE_ID);
                removeTarget(fieldRule, CONTRIBUTOR_TYPE_TEXT);
                addTarget(fieldRule, UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_E_SUBFIELD_TARGET);
            } catch (Exception e) {
                log.warn(format("Cannot update relator term mapping rules, subfield \"e\", field: %s", field), e);
            }
        });

        log.info(format("Updating relator term mapping rules, subfield \"j\" for fields: %s", StringUtils.collectionToCommaDelimitedString(contributorsFieldsWithSubfieldJtoUpdate)));
        contributorsFieldsWithSubfieldJtoUpdate.forEach(field -> {
            try {
                JsonNode fieldRule = mappingRules.get(field);
                removeTarget(fieldRule, CONTRIBUTOR_TYPE_ID);
                removeTarget(fieldRule, CONTRIBUTOR_TYPE_TEXT);
                addTarget(fieldRule, UPDATED_MAPPING_RULES_PATH + RELATOR_TERM_J_SUBFIELD_TARGET);
            } catch (Exception e) {
                log.warn(format("Cannot update relator term mapping rules, subfield \"j\", field: %s", field), e);
            }
        });
    }

    public void authorityControlUpdate(JsonNode mappingRules) {
        log.info(format("Updating alternative titles mapping rules for fields: %s", StringUtils.collectionToCommaDelimitedString(alternativeTitlesFields)));
        alternativeTitlesFields.forEach(field -> addTarget(mappingRules.get(field), UPDATED_MAPPING_RULES_PATH + ALTERNATIVE_TITLE_TARGET));

        log.info(format("Updating contributors mapping rules for fields: %s", StringUtils.collectionToCommaDelimitedString(contributorsFields)));
        contributorsFields.forEach(field -> addTarget(mappingRules.get(field), UPDATED_MAPPING_RULES_PATH + CONTRIBUTORS_TARGET));

        log.info(format("Updating series mapping rules for fields: %s", StringUtils.collectionToCommaDelimitedString(seriesFields)));
        seriesFields.forEach(field -> {
            JsonNode fieldRules = mappingRules.get(field);
            surroundEntityRulesWithEntity(fieldRules);
            addValueToTarget(fieldRules, SERIES);
            addTarget(fieldRules, UPDATED_MAPPING_RULES_PATH + SERIES_TARGET);
        });

        log.info(format("Updating subjects mapping rules for fields: %s", StringUtils.collectionToCommaDelimitedString(subjectsFields)));
        subjectsFields.forEach(field -> {
            JsonNode fieldRules = mappingRules.get(field);
            surroundEntityRulesWithEntity(fieldRules);
            addValueToTarget(fieldRules, SUBJECTS);
            addTarget(fieldRules, UPDATED_MAPPING_RULES_PATH + SUBJECTS_TARGET);
        });
    }

    @SneakyThrows
    private void removeTarget(JsonNode fieldRules, String targetName) {
        for (JsonNode entityRule : fieldRules) {
            ArrayNode rules = (ArrayNode) entityRule.get(ENTITY);
            for (int i = 0; i < rules.size(); i++) {
                JsonNode rule = rules.get(i);
                String targetValue = rule.get(TARGET).asText();
                if (targetValue.equals(targetName)) {
                    rules.remove(i);
                }
            }
        }
    }

    @SneakyThrows
    private void addTarget(JsonNode fieldRules, String targetPath) {
        for (JsonNode entityRule : fieldRules) {
            ArrayNode rules = (ArrayNode) entityRule.get(ENTITY);
            rules.add(FileWorker.getJsonObject(targetPath));
        }
    }

    public void surroundEntityRulesWithEntity(JsonNode fieldRules) {
        if (fieldRules.findValue(ENTITY) == null) {
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (JsonNode fieldRule : fieldRules) {
                arrayNode.add(fieldRule);
            }
            ((ArrayNode) fieldRules).removeAll();
            ((ArrayNode) fieldRules).add(new ObjectNode(JsonNodeFactory.instance).putIfAbsent(ENTITY, arrayNode));
        }
    }

    public void addValueToTarget(JsonNode fieldRules, String targetName) {
        for (JsonNode fieldRule : fieldRules) {
            for (JsonNode rule : fieldRule.get(ENTITY)) {
                if (rule.get(TARGET).asText().equals(targetName)) {
                    ((ObjectNode) rule).put(TARGET, targetName + DOT + VALUE);
                }
            }
        }
    }
}
