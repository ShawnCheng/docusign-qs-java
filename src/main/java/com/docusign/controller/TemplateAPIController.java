package com.docusign.controller;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopeTemplate;
import com.docusign.esign.model.EnvelopeTemplateResult;
import com.docusign.esign.model.EnvelopeTemplateResults;
import com.docusign.esign.model.EnvelopesInformation;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.Signer;

@Controller
public class TemplateAPIController {

    // Data for this example
    // Fill in these constants
    //
    // Obtain an OAuth access token from https://developers.docusign.com/oauth-token-generator
    String accessToken = "{ACCESS_TOKEN}";
    // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
    // upper right corner of the screen by your picture or the default picture.
    String accountId = "{ACCOUNT_ID}";
    //
    // The API base path
    String basePath = "https://demo.docusign.net/restapi";

    @RequestMapping(path = "/template_api/list_templates", method = RequestMethod.GET)
    public int listTemplates(ModelMap model,
            @RequestParam(value="envelope_id", required=false) String evenlopeId) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

        System.out.println(DateTime.now());
        // Step 1. Call the API
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);

        TemplatesApi templatesApi = new TemplatesApi(apiClient);
        EnvelopeTemplateResults templateResults = templatesApi.listTemplates(accountId);
        List<EnvelopeTemplateResult> templates = templateResults.getEnvelopeTemplates();

        for (int i = 0; i<templates.size(); i++) {
            System.out.println("[ID] :" + templates.get(i).getTemplateId());
            System.out.println("[Name] :" + templates.get(i).getName());
            System.out.println("[Email Subject] :" + templates.get(i).getEmailSubject());
        }
        System.out.println(DateTime.now());
        return templates.size();
    }

    @RequestMapping(path = "/template_api/template_roles", method = RequestMethod.GET)
    public void listTemplateRolesOf(ModelMap model,
            @RequestParam(value="template_id", required=false) String templateId) throws ApiException{
        model.addAttribute("title","Embedded Signing Ceremony");

        System.out.println(DateTime.now());
        // Step 1. Call the API
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);

        TemplatesApi templatesApi = new TemplatesApi(apiClient);
        EnvelopeTemplate template = templatesApi.get(accountId, templateId);

        Recipients recipients = templatesApi.listRecipients(accountId, templateId);

        System.out.println("================ Template Role Start =================");
        for (Signer signer: recipients.getSigners()) {
            System.out.println("Template Role: " + signer.getRoleName());
        }
        System.out.println("================ Template Role End =================");
        System.out.println(DateTime.now());
    }
    // Handle get request to show the form
    @RequestMapping(path = "/template_api", method = RequestMethod.GET)
    public String get(ModelMap model) {
        model.addAttribute("title","Template API Experiments");
        return "pages/template_api";
    }
}
