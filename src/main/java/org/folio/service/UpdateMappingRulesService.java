package org.folio.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.folio.client.AuthClient;
import org.folio.client.SRMClient;
import org.folio.model.Configuration;
import org.folio.util.FileWorker;
import org.folio.util.HttpWorker;
import org.folio.util.MappingRulesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.folio.FolioMappingRulesUpdateOrchidApp.exitWithMessage;

@Slf4j
@Service
public class UpdateMappingRulesService {
    private MappingRulesUtil mappingRulesUtil;
    private Configuration configuration;
    private SRMClient srmClient;
    private String MARC_BIB = "marc-bib";

    public void start() {
        configuration = FileWorker.getConfiguration();
        var httpWorker = new HttpWorker(configuration);
        var authClient = new AuthClient(configuration, httpWorker);

        httpWorker.setOkapiToken(authClient.authorize());

        srmClient = new SRMClient(httpWorker);
        mappingRulesUtil = new MappingRulesUtil();

        updateMappingRules();

        exitWithMessage("Script execution completed");
    }

    private void updateMappingRules() {
        JsonNode mappingRules = srmClient.retrieveMappingRules(MARC_BIB);
        mappingRulesUtil.relatorTermUpdate(mappingRules);
        srmClient.updateMappingRules(mappingRules, MARC_BIB);
    }
}
