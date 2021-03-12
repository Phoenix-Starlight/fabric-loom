/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import com.google.common.io.Files
import net.fabricmc.loom.util.ProjectTestTrait
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@IgnoreIf({ os.windows }) // Linux and mac create the same files, im unsure why windows is different. Let me know if you have any ideas?
class ReproducibleBuildTest extends Specification implements ProjectTestTrait {
	@Override
	String name() {
		"reproducible"
	}

	@Unroll
	def "build (gradle #gradle)"() {
		when:
			def result = create("build", gradle)
		then:
			result.task(":build").outcome == SUCCESS
			getOutputHash("fabric-example-mod-1.0.0.jar") == modHash
			getOutputHash("fabric-example-mod-1.0.0-sources.jar") == sourceHash
		where:
			gradle 				| modHash								| sourceHash
			'6.8.3' 			| "ccd6aaff1b06df01e4dd8c08625b82c9"	| "8bd590dc03b7dd0de3a4a7aeb431d4e8"
			'7.0-milestone-2'	| "ccd6aaff1b06df01e4dd8c08625b82c9"	| "8bd590dc03b7dd0de3a4a7aeb431d4e8"
	}

	String getOutputHash(String name) {
		generateMD5(getOutputFile(name))
	}

	String generateMD5(File file) {
		HashCode hash = Files.asByteSource(file).hash(Hashing.md5())
		return hash.asBytes().encodeHex() as String
	}
}