package com.docusign.controller;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.sun.jersey.core.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
//
// Java Quickstart example: Create an envelope and sign it with an embedded
// Signing Ceremony
//
// Copyright (c) 2018 by DocuSign, Inc.
// License: The MIT License -- https://opensource.org/licenses/MIT
//
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////


@Controller
public class QS01EmbedSigningCeremonyController {

    // Data for this example
    // Fill in these constants
    //
    // Obtain an OAuth access token from https://developers.docusign.com/oauth-token-generator
    String accessToken = "{ACCESS_TOKEN}";
    // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
    // upper right corner of the screen by your picture or the default picture.
    String accountId = "{ACCOUNT_ID}";
    // Recipient Information
    String signerName = "Xiao CHENG";
    String signerEmail = "{USER_EMAIL}";

    // The url for this web application
    String baseUrl = "http://localhost:8080";
    String clientUserId = "123"; // Used to indicate that the signer will use an embedded
    // Signing Ceremony. Represents the signer's userId within
    // your application.
    String authenticationMethod = "None"; // How is this application authenticating
    // the signer? See the `authenticationMethod' definition
    //  https://developers.docusign.com/esign-rest-api/reference/Envelopes/EnvelopeViews/createRecipient
    //
    // The API base path
    String basePath = "https://demo.docusign.net/restapi";
    // The document to be signed. See /qs-java/src/main/resources/World_Wide_Corp_lorem.pdf
    String docPdf = "World_Wide_Corp_lorem.pdf";

    String templateId = "9ae83d07-ac57-496b-a02c-092b92e8aeb3";
    public static final String TEMPLATE_ROLE_NAME = "signer";
    EnvelopeSummary envelopeSummary = null;
    EnvelopeSummary envelopeSummaryFromTemplate = null;

    @RequestMapping(path = "/qs01", method = RequestMethod.POST)
    public Object create(ModelMap model) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

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
        // We set the clientUserId to enable embedded signing for the recipient
        List<Signer> signers = getSigners();

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
        getSigners().get(0).setTabs(signerTabs);

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setDocuments(Arrays.asList(document));
        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(signers);
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.

        // Step 2. Call DocuSign to create and send the envelope
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        envelopeSummary = envelopesApi.createEnvelope(accountId, envelopeDefinition);
        String envelopeId = envelopeSummary.getEnvelopeId();
        return envelopeId;
    }

    private List<Signer> getSigners() {
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(clientUserId);
        signer.recipientId("1");

        Signer signer2 = new Signer();
        signer2.setEmail("email2@example.com");
        signer2.setName("singer 2");
        signer2.clientUserId("123-2");
        signer2.recipientId("2");

        return Arrays.asList(signer, signer2);
    }

    @RequestMapping(path = "/qs01/ceremony_url", method = RequestMethod.GET)
    public Object getCeremonyUrl(ModelMap model) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);

        // Step 3. The envelope has been created.
        //         Request a Recipient View URL (the Signing Ceremony URL)
        RecipientViewRequest viewRequest = new RecipientViewRequest();
        // Set the url where you want the recipient to go once they are done signing
        // should typically be a callback route somewhere in your app.
        viewRequest.setReturnUrl(baseUrl + "/ds-return");
        viewRequest.setAuthenticationMethod(authenticationMethod);
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(clientUserId);
        // call the CreateRecipientView API
        ViewUrl results1 = envelopesApi.createRecipientView(accountId, envelopeSummary.getEnvelopeId(), viewRequest);

        // Step 4. The Recipient View URL (the Signing Ceremony URL) has been received.
        //         The user's browser will be redirected to it.
        String redirectUrl = results1.getUrl();
        RedirectView redirect = new RedirectView(redirectUrl);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @RequestMapping(path = "/qs01/envelope_by_template", method = RequestMethod.GET)
    public Object createEnvelopeByTemplate(ModelMap model) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setTemplateId(templateId);
        envelopeDefinition.setTemplateRoles(getTemplateRoles());
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.

        // Step 2. Call DocuSign to create and send the envelope
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);

        envelopeSummaryFromTemplate = envelopesApi.createEnvelope(accountId, envelopeDefinition);
        String envelopeId = envelopeSummaryFromTemplate.getEnvelopeId();
        return envelopeId;
    }

    @RequestMapping(path = "/qs01/ceremony_url_by_template", method = RequestMethod.GET)
    public Object getCeremonyUrlByTemplate(ModelMap model) throws ApiException, IOException {
        model.addAttribute("title","Embedded Signing Ceremony");

        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);

        // Step 3. The envelope has been created.
        //         Request a Recipient View URL (the Signing Ceremony URL)
        RecipientViewRequest viewRequest = new RecipientViewRequest();
        // Set the url where you want the recipient to go once they are done signing
        // should typically be a callback route somewhere in your app.
        viewRequest.setReturnUrl(baseUrl + "/ds-return");
        viewRequest.setAuthenticationMethod(authenticationMethod);
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(clientUserId);
        // call the CreateRecipientView API
        ViewUrl results1 = envelopesApi.createRecipientView(accountId, envelopeSummaryFromTemplate.getEnvelopeId(), viewRequest);

        // Step 4. The Recipient View URL (the Signing Ceremony URL) has been received.
        //         The user's browser will be redirected to it.
        String redirectUrl = results1.getUrl();
        RedirectView redirect = new RedirectView(redirectUrl);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    private List<TemplateRole> getTemplateRoles() {
        TemplateRole signer = new TemplateRole();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(clientUserId);
        signer.setRoleName(TEMPLATE_ROLE_NAME);

        TemplateRole signer2 = new TemplateRole();
        signer2.setEmail("email2@example.com");
        signer2.setName("singer 2");
        signer2.clientUserId("123-2");
        signer2.setRoleName(TEMPLATE_ROLE_NAME);

        return Arrays.asList(signer, signer2);
    }

    // Read a file
    private byte[] readFile(String path) throws IOException {
        InputStream is = QS01EmbedSigningCeremonyController.class.getResourceAsStream("/" + path);
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
    @RequestMapping(path = "/qs01", method = RequestMethod.GET)
    public String get(ModelMap model) {
        model.addAttribute("title","Embedded Signing Ceremony");
        return "pages/qs01";
    }
}
