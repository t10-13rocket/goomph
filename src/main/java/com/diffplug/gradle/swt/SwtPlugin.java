/*
 * Copyright 2016 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.swt;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.plugins.JavaPlugin;

public class SwtPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		// ensure we don't double-apply the plugin
		if (project.getPlugins().hasPlugin(SwtPlugin.class)) {
			return;
		}

		// make sure the java plugin has been applied
		if (!project.getPlugins().hasPlugin(JavaPlugin.class)) {
			project.getPlugins().apply(JavaPlugin.class);
		}

		// create the SwtExtension
		SwtExtension swtExtension = project.getExtensions().create(SwtExtension.NAME, SwtExtension.class);
		project.afterEvaluate(proj -> {
			// add the update site as an ivy artifact
			IvyArtifactRepository repo = proj.getExtensions().getByType(IvyArtifactRepository.class);
			repo.artifactPattern("plugins/[artifact]_[revision].[ext]");
			repo.setUrl(swtExtension.updateSite());
			project.getRepositories().add(repo);

			// add all of SWT's dependencies 
			for (String dep : SwtExtension.DEPS) {
				project.getDependencies().add("compile", swtExtension.fullDep(dep));
			}
		});
	}
}
