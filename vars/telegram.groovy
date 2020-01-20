import hudson.tasks.test.AbstractTestResultAction
import hudson.model.Actionable


def notifyStart(token, chatId) {
    message = "<b>Build started</b>\n\n" +
      "Project: <a href=\"$JOB_URL\">$JOB_BASE_NAME</a> \n" +
      "Build number: <a href=\"$BUILD_URL\">$BUILD_DISPLAY_NAME</a> \n" +
      "Branch: $GIT_BRANCH"
    
    def encodedMessage = URLEncoder.encode(message, "UTF-8")
    
    response = httpRequest (consoleLogResponseBody: true,
        contentType: 'APPLICATION_JSON',
        httpMode: 'GET',
        url: "https://api.telegram.org/bot$token/sendMessage?parse_mode=HTML&text=$encodedMessage&chat_id=$chatId&disable_web_page_preview=false",
        validResponseCodes: '200')
    
    return response
}

@NonCPS
def notifyEnd(token, chatId) {
    def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    def testReport = ""

    if (testResultAction != null) {
        def total = testResultAction.getTotalCount()
        def failed = testResultAction.getFailCount()
        def skipped = testResultAction.getSkipCount()

        testReport = testReport + ("Passed: " + (total - failed - skipped))
        testReport = testReport + (", Failed: " + failed)
        testReport = testReport + (", Skipped: " + skipped)
    } else {
        testReport = "No tests found."
    }
    
    message = "<b>Build completed</b>\n\n" +
        "Project: <a href=\"$JOB_URL\">$JOB_BASE_NAME</a> \n" +
        "Status: $currentBuild.currentResult\n" +
        "Build number: <a href=\"$BUILD_URL\">$BUILD_DISPLAY_NAME</a> \n" +
        "Branch: $GIT_BRANCH\n\n" +
        "Test results\n" + 
        "  $testReport"
    
    def encodedMessage = URLEncoder.encode(message, "UTF-8")
    
    response = httpRequest (consoleLogResponseBody: true,
        contentType: 'APPLICATION_JSON',
        httpMode: 'GET',
        url: "https://api.telegram.org/bot$token/sendMessage?parse_mode=HTML&text=$encodedMessage&chat_id=$chatId&disable_web_page_preview=false",
        validResponseCodes: '200')
    
    return response
}
