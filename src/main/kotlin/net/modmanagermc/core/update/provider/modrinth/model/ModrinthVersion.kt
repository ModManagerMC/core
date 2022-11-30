/*
 * Copyright (c) 2022 DeathsGun
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

package net.modmanagermc.core.update.provider.modrinth.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.StringJoiner

/*
{
  "name": "Version 1.0.0",
  "version_number": "1.0.0",
  "changelog": "List of changes in this version: ...",
  "changelog_url": null,
  "dependencies": [
    {
      "version_id": "IIJJKKLL",
      "project_id": "QQRRSSTT",
      "dependency_type": "required"
    }
  ],
  "game_versions": [
    "1.16.5",
    "1.17.1"
  ],
  "version_type": "release",
  "loaders": [
    "fabric",
    "forge"
  ],
  "featured": true,
  "id": "IIJJKKLL",
  "project_id": "AABBCCDD",
  "author_id": "EEFFGGHH",
  "date_published": "2019-08-24T14:15:22Z",
  "downloads": 0,
  "files": [
    {
      "hashes": {
        "sha512": "93ecf5fe02914fb53d94aa3d28c1fb562e23985f8e4d48b9038422798618761fe208a31ca9b723667a4e05de0d91a3f86bcd8d018f6a686c39550e21b198d96f",
        "sha1": "c84dd4b3580c02b79958a0590afd5783d80ef504"
      },
      "url": "https://cdn.modrinth.com/data/AABBCCDD/versions/1.0.0/my_file.jar",
      "filename": "my_file.jar",
      "primary": false
    }
  ]
}
 */
@Serializable
data class ModrinthVersion(
    val id: String,
    val name: String,
    @SerialName("version_number")
    val version: String,
    val changelog: String,
    val dependencies: List<Dependency>,
    @SerialName("game_versions")
    val gameVersions: List<String>,
    @SerialName("version_type")
    val versionType: String,
    @SerialName("project_id")
    val projectId: String,
    @SerialName("date_published")
    val datePublished: Instant,
    val loaders: List<String>,
    val files: List<Asset>
) {

    @Serializable
    data class Dependency(
        @SerialName("version_id")
        val versionId: String?,
        @SerialName("project_id")
        val projectId: String?,
        @SerialName("dependency_type")
        val dependencyType: String
    )

    @Serializable
    data class Asset(
        val url: String,
        val filename: String,
        val hashes: Map<String, String>
    )
}
