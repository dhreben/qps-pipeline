package com.qaprosoft.jenkins.jobdsl.factory.pipeline.hook

import groovy.transform.InheritConstructors

@InheritConstructors
public class PushJobFactory extends PipelineFactory {
	def project

	public PushJobFactory(folder, pipelineScript, jobName, jobDesc, project) {
		this.folder = folder
		this.pipelineScript = pipelineScript
		this.name = jobName
		this.description = jobDesc
		this.project = project
	}

	def create() {

		def pipelineJob = super.create()

		pipelineJob.with {
			properties {
				//TODO: add SCM artifacts
				pipelineTriggers {
					triggers {
						githubPush()
					}
				}
			}

			//TODO: think about other parameters to support DevOps CI operations
			parameters {
				stringParam('project', project, 'Your GitHub repository for scanning')
				//TODO: analyze howto support several gc_GIT_BRACH basing on project
				configure addExtensibleChoice('branch', "gc_GIT_BRANCH", "Select a GitHub Testing Repository Branch to run against", "master")
				booleanParam('onlyUpdated', true, '	If chosen, scan will be performed only in case of any change in *.xml suites.')

				choiceParam('removedConfigFilesAction', ['IGNORE', 'DELETE'], '')
				choiceParam('removedJobAction', ['IGNORE', 'DELETE'], '')
				choiceParam('removedViewAction', ['IGNORE', 'DELETE'], '')
			}

		}
		return pipelineJob
	}

	protected def getOrganization() {
		return 'qaprosoft'
	}

	protected def getGitHubAuthId(project) {
		//TODO: get API GitHub URL from binding
		return "https://api.github.com : ${project}-token"
	}
}