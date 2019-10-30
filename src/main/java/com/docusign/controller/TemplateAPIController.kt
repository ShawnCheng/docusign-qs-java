package com.docusign.controller

import com.docusign.esign.api.TemplatesApi
import com.docusign.esign.client.ApiClient
import com.docusign.esign.client.ApiException
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.io.IOException

@Controller
class TemplateAPIController {

    // Data for this example
    // Fill in these constants
    //
    // Obtain an OAuth access token from https://developers.docusign.com/oauth-token-generator
    @Value("\${docusign.api.dev_token}")
    lateinit var accessToken : String
    // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
    // upper right corner of the screen by your picture or the default picture.
    @Value("\${docusign.api.account_id}")
    lateinit var accountId : String
    //
    // The API base path
    @Value("\${docusign.api.base_url}")
    lateinit var basePath : String

    @RequestMapping(path = ["/template_api/list_templates"], method = [RequestMethod.GET])
    @Throws(ApiException::class, IOException::class)
    fun listTemplates(
        model: ModelMap
    ): Int {
        model.addAttribute("title", "Embedded Signing Ceremony")

        println(DateTime.now())
        // Step 1. Call the API
        val apiClient = ApiClient(basePath)
        apiClient.addDefaultHeader("Authorization", "Bearer $accessToken")

        val templatesApi = TemplatesApi(apiClient)
        val templateResults = templatesApi.listTemplates(accountId)
        val templates = templateResults.envelopeTemplates

        for (i in templates.indices) {
            println("[ID] :" + templates[i].templateId)
            println("[Name] :" + templates[i].name)
            println("[Email Subject] :" + templates[i].emailSubject)
        }
        println(DateTime.now())
        return templates.size
    }

    @RequestMapping(path = ["/template_api/template_roles"], method = [RequestMethod.GET])
    @Throws(ApiException::class)
    fun listTemplateRolesOf(
        model: ModelMap,
        @RequestParam(value = "template_id", required = false) templateId: String
    ) {
        model.addAttribute("title", "Embedded Signing Ceremony")

        println(DateTime.now())
        // Step 1. Call the API
        val apiClient = ApiClient(basePath)
        apiClient.addDefaultHeader("Authorization", "Bearer $accessToken")

        val templatesApi = TemplatesApi(apiClient)
        val template = templatesApi.get(accountId, templateId)

        val recipients = templatesApi.listRecipients(accountId, templateId)

        println("================ Template Role Start =================")
        for (signer in recipients.signers) {
            println("Template Role: " + signer.roleName)
        }
        println("================ Template Role End =================")
        println(DateTime.now())
    }

    // Handle get request to show the form
    @RequestMapping(path = ["/template_api"], method = [RequestMethod.GET])
    operator fun get(model: ModelMap): String {
        model.addAttribute("title", "Template API Experiments")
        return "pages/template_api"
    }
}
