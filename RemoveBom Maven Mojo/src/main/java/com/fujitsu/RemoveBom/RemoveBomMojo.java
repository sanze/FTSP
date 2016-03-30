package com.fujitsu.RemoveBom;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.fujitsu.util.Utf8BomRemoveUtil;

/**
 * Remove java file's bom info
 * @requiresProject true
 * @goal removeBom
 * @phase process-sources
 */
public class RemoveBomMojo extends AbstractMojo {
	
	/**  
     * @parameter expression="${project.build.sourceDirectory}"  
     * @required  
     */
	private String sourceDirectory;
	
	/**  
     * @parameter expression="${project.build.testSourceDirectory}"  
     * @required  
     */
	private String testSourceDirectory;

	public void execute() throws MojoExecutionException {
		//去除java文件bom信息
		try {
			new Utf8BomRemoveUtil("java").start(new File(sourceDirectory));
			new Utf8BomRemoveUtil("java").start(new File(testSourceDirectory));
		} catch (IOException e) {
			getLog().error(e);
		} 
	}
}
