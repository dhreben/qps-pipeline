package com.qaprosoft.jenkins.pipeline.integration.zebrunner

import com.qaprosoft.jenkins.Logger
import com.qaprosoft.jenkins.pipeline.Configuration

class ZebrunnerUpdater {

    private def context
    private ZebrunnerClient zc
    private Logger logger

    public ZebrunnerUpdater(context) {
        this.context = context
        zc = new ZebrunnerClient(context)
        logger = new Logger(context)
    }

    public def sendInitResult(integrationParameters, initialized) {
        def tenancyName = Configuration.get("tenancyName")
        def authToken = Configuration.get("authToken")
        def callbackURL = Configuration.get("callbackURL")
        return zc.sendInitResult(integrationParameters, tenancyName, authToken, callbackURL, initialized)
    }
}
