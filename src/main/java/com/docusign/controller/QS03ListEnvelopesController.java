package com.docusign.controller;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopesInformation;


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
//
// Java Quickstart example: List envelopes whose status has changed
//
// Copyright (c) 2018 by DocuSign, Inc.
// License: The MIT License -- https://opensource.org/licenses/MIT
//
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

@Controller
public class QS03ListEnvelopesController {
    // Data for this example
    // Fill in these constants
    //
    // Obtain an OAuth access token from https://developers.docusign.com/oauth-token-generator
    @Value("${docusign.api.dev_token}")
    String accessToken;
    // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
    // upper right corner of the screen by your picture or the default picture.
    @Value("${docusign.api.account_id}")
    String accountId;

    // The API base path
    @Value("${docusign.api.base_url}")
    String basePath;

    @RequestMapping(path = "/qs03", method = RequestMethod.POST)
    public Object create(ModelMap model) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

        // Step 1. Call the API
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        // prepare the request body
        ListStatusChangesOptions options = envelopesApi.new ListStatusChangesOptions();
        LocalDate date = LocalDate.now().minusDays(10);
        options.setFromDate(date.toString("yyyy/MM/dd"));
        // call the API
        EnvelopesInformation results = envelopesApi.listStatusChanges(accountId, options);

        // Show results
        String title = "List Updated Envelopes";
        model.addAttribute("title", title);
        model.addAttribute("h1", title);
        model.addAttribute("message", "Envelopes::listStatusChanges results");
        model.addAttribute("json", new JSONObject(results).toString(4));
        return "pages/example_done";
    }


    // Handle get request to show the form
    @RequestMapping(path = "/qs03", method = RequestMethod.GET)
    public String get(ModelMap model) {
        model.addAttribute("title","List Updated Envelopes");
        return "pages/qs03";
    }
}
