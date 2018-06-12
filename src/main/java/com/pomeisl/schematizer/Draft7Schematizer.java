package com.pomeisl.schematizer;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Draft7Schematizer implements Schematizer {

	private String json;

	public Schematizer load(String json) {
		this.json = JsonUtil.pretty(json);
		return this;
	}

	public String schematize() {
		Gson gson = new Gson();
		JsonElement jElement = gson.fromJson(this.json, JsonElement.class);
		JsonObject skematized = this._schematize(jElement, true);
		return new Gson().toJson(skematized);
	}

	private JsonObject _schematize(JsonElement jElement, boolean examples) {
		JsonObject skematized_ = new JsonObject();
		String dataType = JsonUtil.dataType(jElement);

		skematized_.addProperty("type", dataType);
		switch (dataType) {
		case "object":

			JsonObject o_ = jElement.getAsJsonObject();
			Set<String> keyset = o_.keySet();

			JsonObject jo = new JsonObject();
			keyset.parallelStream().forEach(k -> {
				JsonElement e_ = o_.get(k);
				JsonElement res_ = _schematize(e_, examples);
				jo.add(k, res_);
			});

			skematized_.add("properties", jo);
			break;
		case "array":
			JsonArray a_ = jElement.getAsJsonArray();
			JsonElement value = a_.get(0);
			JsonElement valueSchema = this._schematize(value, examples);
			skematized_.add("items", valueSchema);
			// if (examples) {
			// skematized_.add("examples", jElement);
			// }
			break;
		case "integer":
		case "number":
		case "string":
		case "boolean":
			if (examples) {
				JsonArray arr_ = new JsonArray();
				arr_.add(jElement);
				skematized_.add("examples", arr_);
			}
			break;
		default:
			break;
		}

		return skematized_;
	}

}