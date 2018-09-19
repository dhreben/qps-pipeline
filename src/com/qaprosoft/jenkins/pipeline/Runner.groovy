package com.qaprosoft.jenkins.pipeline

class Runner {
	protected def context
	
	public Runner(context) {
		super(context)
	}
	
	//Events
	public void onPush() {
		context.println("Runner->onPush")
	}

	public void onPullRequest() {
		context.println("Runner->onPullRequest")
    }
}
