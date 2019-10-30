package com.docusign.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.sun.jersey.core.util.Base64;

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
//
// Java Quickstart example: Send a signing request by email
//
// Copyright (c) 2018 by DocuSign, Inc.
// License: The MIT License -- https://opensource.org/licenses/MIT
//
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////


@Controller
public class QS02SendEnvelopeController {

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
    // Recipient Information
    @Value("${testdata.signer1.name}")
    String signerName;
    @Value("${testdata.signer1.email}")
    String signerEmail;

    // The url for this web application
    String baseUrl = "http://localhost:8080";
    //
    // The API base path
    @Value("${docusign.api.base_url}")
    String basePath;
    // The document to be signed. See /qs-java/src/main/resources/World_Wide_Corp_lorem.pdf
    String docPdf = "World_Wide_Corp_lorem.pdf";

    @RequestMapping(path = "/qs02", method = RequestMethod.POST)
    public Object create(ModelMap model) throws ApiException, IOException {

        // Step 1. Create the envelope definition
        // One "sign here" tab will be added to the document.

        byte[] buffer = readFile(docPdf);
        String docBase64 = new String(Base64.encode(buffer));

        // Create the DocuSign document object
        Document document = new Document();
        document.setDocumentBase64(docBase64);
        document.setName("Example document"); // can be different from actual file name
        document.setFileExtension("pdf"); // many different document types are accepted
        document.setDocumentId("1"); // a label used to reference the doc

        // The signer object
        // Create a signer recipient to sign the document, identified by name and email
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.recipientId("1");

        // Create a signHere tabs (also known as a field) on the document,
        // We're using x/y positioning. Anchor string positioning can also be used
        SignHere signHere = new SignHere();
        signHere.setDocumentId("1");
        signHere.setPageNumber("1");
        signHere.setRecipientId("1");
        signHere.setTabLabel("SignHereTab");
        signHere.setXPosition("195");
        signHere.setYPosition("147");

        // Add the tabs to the signer object
        // The Tabs object wants arrays of the different field/tab types
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signHere));
        signer.setTabs(signerTabs);

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setDocuments(Arrays.asList(document));
        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.

        // Step 2. Call DocuSign to create and send the envelope
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary results = envelopesApi.createEnvelope(accountId, envelopeDefinition);
        String envelopeId = results.getEnvelopeId();

        // Show results
        String title = "Signing Request by Email";
        model.addAttribute("title", title);
        model.addAttribute("h1", title);
        model.addAttribute("message", "Envelopes::create results");
        model.addAttribute("json", new JSONObject(results).toString(4));
        return "pages/example_done";
    }


    // Read a file
    private byte[] readFile(String path) throws IOException {
        InputStream is = QS02SendEnvelopeController.class.getResourceAsStream("/" + path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }


    // Handle get request to show the form
    @RequestMapping(path = "/qs02", method = RequestMethod.GET)
    public String get(ModelMap model) {
        model.addAttribute("title","Signing Request by Email");
        return "pages/qs02";
    }
}
