/*
 *
 *                         Copyright (c) 2016-2023
 *                SparklingComet @ http://shanerx.org
 *               KillerOfPie @ http://killerofpie.github.io
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *                http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  NOTICE: All modifications made by others to the source code belong
 *  to the respective contributor. No contributor should be held liable for
 *  any damages of any kind, whether be material or moral, which were
 *  caused by their contribution(s) to the project. See the full License for more information.
 *
 */

package org.shanerx.tradeshop.data.storage.Json;

import com.bergerkiller.bukkit.common.config.JsonSerializer;
import com.google.gson.JsonPrimitive;
import org.bukkit.World;
import org.shanerx.tradeshop.data.storage.LinkageConfiguration;
import org.shanerx.tradeshop.utils.gsonprocessing.GsonProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonLinkageData extends JsonConfiguration implements LinkageConfiguration {

    Map<String, String> linkageData;

    public JsonLinkageData(World world) {
        super(world.getName(), "chest_linkage");
        load();
    }

    @Override
    public void load() {
        try {
            linkageData = GsonProcessor.jsonToMap(jsonObj.get("linkage_data").toString()).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
        } catch (JsonSerializer.JsonSyntaxException ex) {
            linkageData = new HashMap<>();
        }
    }

    @Override
    public Map<String, String> getLinkageData() {
        return linkageData;
    }

    @Override
    public void save() {
        jsonObj.add("linkage_data", new JsonPrimitive(GsonProcessor.toJson(linkageData)));

        saveFile();
    }
}
