package com.qaprosoft.jenkins.pipeline.carina

import com.qaprosoft.jenkins.pipeline.Configuration
import com.qaprosoft.jenkins.pipeline.carina.CarinaScanner
import com.qaprosoft.jenkins.pipeline.Runner
import groovy.transform.InheritConstructors

@InheritConstructors
class CarinaRunner extends Runner {

    def scanner

    public CarinaRunner(context) {
        super(context)
        scanner = new CarinaScanner(context)
    }

	@Override
	public void onPush() {
		context.println("CarinaRunner->onPush")
		scanner.scanRepository()
		// handle each push/merge operation
		// execute logic inside this method only if $REPO_HOME/Jenkinsfile was updated
		context.println("TODO: implement snapshot build generation and emailing build number...")
	}
	
	@Override
	public void onPullRequest() {
		context.println("CarinaRunner->onPullRequest")
	}
}