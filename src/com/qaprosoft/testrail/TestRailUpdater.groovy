package com.qaprosoft.testrail


import com.qaprosoft.Logger
import static com.qaprosoft.jenkins.pipeline.Executor.*
import com.qaprosoft.zafira.ZafiraClient

class TestRailUpdater {

    private def context
    private ZafiraClient zc
    private TestRailClient trc
    private Logger logger
    private integration


    public TestRailUpdater(context) {
        this.context = context
        zc = new ZafiraClient(context)
        trc = new TestRailClient(context)
        logger = new Logger(context)
    }

    public void updateTestRun(uuid, isRebuild) {
        integration = zc.getTestRailIntegrationInfo(uuid)
        if(!isParamEmpty(integration)){
            integration.milestoneId = getMilestoneId()
            integration.assignedToId = getAssignedToId()
            parseIntegrationInfo()
            logger.info("INTEGRATION_INFO" + integration)
//            if(!isRebuild){
//                def testRun = addTestRun(false)
//                integration.testRunId = testRun.id
//            } else {
//                integration.testRunId = getTestRunId()
//            }
//            addResultsForCases()
        }
    }

    public def getTestRunId(){
        def testRunId = null
        def testRuns
        if(integration.milestoneId){
            testRuns = trc.getRuns(Math.round(integration.createdAfter/1000), integration.assignedToId, integration.milestoneId, integration.projectId, integration.suiteId)
        } else {
            testRuns = trc.getRuns(Math.round(integration.createdAfter/1000), integration.assignedToId, integration.projectId, integration.suiteId)
        }
        testRuns.each { Map testRun ->
            logger.info("TEST_RUN: " + testRun)
            if(testRun.name == integration.testRunName){
                testRunId = testRun.id
            }
        }
        return testRunId
    }

    public def getMilestoneId(){
        Map customParams = integration.customParams
        if(customParams.milestone){
            def milestoneId = null
            def milestones = trc.getMilestones(integration.projectId)
            milestones.each { Map milestone ->
                if (milestone.name == integration.milestone) {
                    milestoneId = milestone.id
                }
            }
            if(!milestoneId ){
                def milestone = trc.addMilestone(integration.projectId, integration.milestone)
                milestoneId = milestone.id
            }
            return milestoneId
        }
    }

    public def getAssignedToId(){
        Map customParams = integration.customParams
        def assignedToId = trc.getUserIdByEmail(customParams.assignee)
        return assignedToId.id
    }

    public def addTestRun(boolean include_all){
        def testRun
        if(integration.milestoneId){
            testRun = trc.addTestRun(integration.suiteId, integration.testRunName, integration.milestoneId, integration.assignedToId, include_all, integration.testCaseIds, integration.projectId)
        } else {
            testRun = trc.addTestRun(integration.suiteId, integration.testRunName, integration.assignedToId, include_all, integration.testCaseIds, integration.projectId)
        }
        logger.info("ADDED TESTRUN:\n" + testRun)
        return testRun
    }

    public def addResultsForCases(){
        def response = trc.addResultsForCases(integration.testRunId, integration.results)
        logger.info("ADD_RESULTS_RESPONSE: " + response)
    }

    public def parseIntegrationInfo(){
        Map testCaseResultMap = new HashMap<>()
        integration.integrationInfo.each { integrationInfoItem ->
            String[] tagInfoArray = integrationInfoItem.tagValue.split("-")
            def testCaseResult = {}
            if (testCaseResultMap.tagInfoArray[2]) {
                if (!integration.projectId) {
                    integration.projectId = tagInfoArray[0]
                    integration.suiteId = tagInfoArray[1]
                }
                testCaseResult.case_id = tagInfoArray[2]
                testCaseResult.status_id = TestRailStatusMapper.getTestRailStatus(integrationInfoItem.status)
                testCaseResult.comment = integrationInfoItem.message
            } else {
                testCaseResult = testCaseResultMap.get(tagInfoArray[2])
            }
            testCaseResult.defects = getDefectsString(testCaseResult.defects, integrationInfoItem.defectId)
            testCaseResultMap.put(tagInfoArray[2], testCaseResult)
        }
        integration.testCaseIds = testCaseResultMap.keySet()
        integration.results = testCaseResultMap.values()
    }
}
