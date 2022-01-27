package org.egov.asset.web.controller.adaptor;

import java.lang.reflect.Type;

import org.egov.asset.model.CustomeFields;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AssetJsonAdaptor implements JsonSerializer<CustomeFields> {

	 @Override
	 public JsonElement serialize(final CustomeFields customFields, final Type type, final JsonSerializationContext jsc) {
	        final JsonObject jsonObject = new JsonObject();
	        if (customFields != null) {
	            if (customFields.getName() != null)
	                jsonObject.addProperty("name", customFields.getName());
	            else
	                jsonObject.addProperty("name", "");
	            if (customFields.getDataType() != null)
	                jsonObject.addProperty("dataType", customFields.getDataType());
	            else
	                jsonObject.addProperty("dataType", "");
	            if (customFields.isActive())
	                jsonObject.addProperty("active", customFields.isActive());
	            else
	                jsonObject.addProperty("active", false);
	            if (customFields.getVlaues() != null)
	                jsonObject.addProperty("values", customFields.getVlaues());
	            else
	                jsonObject.addProperty("values", "");
	            if (customFields.isMandatory())
	                jsonObject.addProperty("mandatory", customFields.isMandatory());
	            else
	                jsonObject.addProperty("mandatory", false);
	            jsonObject.addProperty("id", customFields.getId());
	        }
	        return jsonObject;
	    }
}
